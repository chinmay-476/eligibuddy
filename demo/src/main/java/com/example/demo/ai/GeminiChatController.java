package com.example.demo.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/gemini")
@CrossOrigin(origins = "*")
public class GeminiChatController {

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        try {
            String message = request.get("message");
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Message cannot be empty"));
            }

            // Try Gemini API first if API key is configured
            if (geminiApiKey != null && !geminiApiKey.trim().isEmpty()) {
                try {
                    String aiResponse = getGeminiResponse(message);
                    return ResponseEntity.ok(Map.of("response", aiResponse));
                } catch (Exception e) {
                    // If Gemini API fails, fall back to rule-based system
                    System.err.println("Gemini API failed, falling back to rule-based system: " + e.getMessage());
                    String fallbackResponse = generateResponse(message.toLowerCase());
                    return ResponseEntity.ok(Map.of("response", fallbackResponse));
                }
            } else {
                // No API key configured, use rule-based system
                String response = generateResponse(message.toLowerCase());
                return ResponseEntity.ok(Map.of("response", response));
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    private String getGeminiResponse(String message) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        
        // Gemini API endpoint
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + geminiApiKey;
        
        // Enhanced context-aware prompt specifically for Eligibuddy users
        String systemPrompt = "You are an intelligent AI assistant for Eligibuddy, a comprehensive eligibility analysis platform. " +
            "You have deep knowledge about the platform and help users navigate their eligibility journey.\n\n" +
            
            "**ELIGIBUDDY PLATFORM CONTEXT:**\n" +
            "Eligibuddy is a Spring Boot-based web application that helps users find opportunities based on their eligibility criteria.\n\n" +
            
            "**CORE FEATURES YOU KNOW:**\n" +
            "1. **User Registration & Authentication:** Users can register with email/password, login, and manage profiles\n" +
            "2. **Eligibility Analysis:** Users fill out forms with personal details (age, gender, state, district, qualification, category, income, field, disability status)\n" +
            "3. **Opportunity Categories:**\n" +
            "   - Scholarships (educational funding with amount, deadline, type)\n" +
            "   - Government Schemes (welfare programs with benefits and types)\n" +
            "   - Competitive Exams (entrance tests with exam dates, fees)\n" +
            "   - Government Jobs (public sector positions with salary, vacancies)\n" +
            "4. **Smart Matching:** System matches user profiles against eligibility criteria stored as JSON\n" +
            "5. **Results Display:** Users get filtered, ranked results with detailed information\n" +
            "6. **Admin Features:** Manage opportunities, view contacts, monitor users\n\n" +
            
            "**TECHNICAL STACK:**\n" +
            "- Backend: Spring Boot 3.5.3, Spring Security, Spring Data JPA\n" +
            "- Database: H2 (dev) / MySQL (prod)\n" +
            "- Frontend: Thymeleaf, Bootstrap, JavaScript\n" +
            "- Security: Role-based access (USER/ADMIN)\n\n" +
            
            "**USER JOURNEY YOU UNDERSTAND:**\n" +
            "1. User visits homepage and sees eligibility checker\n" +
            "2. User registers/logs in (required for analysis)\n" +
            "3. User fills eligibility form with personal details\n" +
            "4. System processes and shows matching opportunities\n" +
            "5. User can view details, apply, or contact for more info\n\n" +
            
            "**RESPONSE GUIDELINES:**\n" +
            "- Always think from the user's perspective\n" +
            "- Provide actionable, specific advice\n" +
            "- Reference actual platform features and workflows\n" +
            "- Help users understand eligibility criteria and requirements\n" +
            "- Guide users through the platform effectively\n" +
            "- Be encouraging and supportive about opportunities\n" +
            "- If asked about technical details, explain in user-friendly terms\n" +
            "- Always relate answers back to how they help users find opportunities\n" +
            "- Use examples from the actual platform (scholarships, jobs, schemes, exams)\n\n" +
            
            "**COMMON USER SCENARIOS YOU HANDLE:**\n" +
            "- New users asking how to get started\n" +
            "- Users confused about eligibility criteria\n" +
            "- Users asking about specific opportunity types\n" +
            "- Users needing help with profile completion\n" +
            "- Users asking about application processes\n" +
            "- Users seeking career guidance\n" +
            "- Technical questions about the platform\n\n" +
            
            "Remember: You are the user's guide to finding opportunities through Eligibuddy. Be helpful, specific, and encouraging!";

        String fullPrompt = systemPrompt + "\n\n**USER QUESTION:** " + message + "\n\n**RESPONSE:**";
        
        // Prepare request body
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", fullPrompt);
        content.put("parts", new Object[]{part});
        requestBody.put("contents", new Object[]{content});
        
        // Generation config
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("maxOutputTokens", 2048);
        generationConfig.put("temperature", 0.7);
        generationConfig.put("topP", 0.8);
        generationConfig.put("topK", 40);
        requestBody.put("generationConfig", generationConfig);
        
        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        // Make API call
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        
        if (response.getStatusCode() == HttpStatus.OK) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode contentNode = candidates.get(0).path("content");
                JsonNode parts = contentNode.path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    return parts.get(0).path("text").asText();
                }
            }
        }
        
        throw new Exception("Failed to get response from Gemini API: " + response.getStatusCode());
    }

    private String generateResponse(String message) {
        // FALLBACK: Rule-based responses when Gemini API is not available or fails
        // User-focused responses based on actual Eligibuddy platform features
        
        // Project overview responses with variations
        if (message.contains("project") || message.contains("eligibuddy") || message.contains("overview") || message.contains("what is")) {
            String[] projectResponses = {
                "**Eligibuddy Project Overview:**\n\n" +
                "Eligibuddy is a comprehensive eligibility analysis platform that helps users find:\n" +
                "• **Scholarships** - Educational funding opportunities\n" +
                "• **Government Schemes** - Welfare and benefit programs\n" +
                "• **Competitive Exams** - Government job entrance tests\n" +
                "• **Government Jobs** - Public sector employment opportunities\n\n" +
                "**Key Features:**\n" +
                "• User authentication and profile management\n" +
                "• Advanced eligibility matching algorithms\n" +
                "• Comprehensive database of opportunities\n" +
                "• Real-time filtering and search capabilities\n" +
                "• Responsive web interface\n\n" +
                "The platform uses Spring Boot with H2/MySQL database and includes security features for user management.",
                
                "**About Eligibuddy:**\n\n" +
                "This is an intelligent eligibility analysis system designed to bridge the gap between opportunities and eligible candidates. " +
                "It's built to help students and job seekers discover relevant scholarships, government schemes, competitive exams, and job opportunities.\n\n" +
                "**Core Functionality:**\n" +
                "• Smart matching based on user profiles\n" +
                "• Comprehensive opportunity database\n" +
                "• User-friendly interface\n" +
                "• Secure authentication system\n\n" +
                "The system analyzes multiple criteria to provide personalized recommendations.",
                
                "**Eligibuddy - Your Opportunity Finder:**\n\n" +
                "Think of Eligibuddy as your personal assistant for finding opportunities! It's designed to:\n" +
                "• **Save Time** - No more searching through hundreds of websites\n" +
                "• **Increase Accuracy** - Precise matching based on eligibility criteria\n" +
                "• **Provide Clarity** - Clear information about requirements and deadlines\n" +
                "• **Offer Variety** - Multiple categories of opportunities in one place\n\n" +
                "Whether you're looking for educational funding or career opportunities, Eligibuddy has you covered!"
            };
            return projectResponses[new Random().nextInt(projectResponses.length)];
        }
        
        // Eligibility analysis responses with variations
        if (message.contains("eligibility") || message.contains("criteria") || message.contains("qualify") || message.contains("match")) {
            String[] eligibilityResponses = {
                "**Eligibility Analysis Features:**\n\n" +
                "The platform analyzes user eligibility based on:\n" +
                "• **Personal Information** - Age, gender, location\n" +
                "• **Educational Background** - Qualification level and field\n" +
                "• **Socio-economic Status** - Category, income range\n" +
                "• **Preferences** - Areas of interest (scholarships, jobs, etc.)\n\n" +
                "**How it works:**\n" +
                "1. Users fill out their profile information\n" +
                "2. System matches against eligibility criteria\n" +
                "3. Results are filtered and ranked by relevance\n" +
                "4. Users can view detailed information and apply\n\n" +
                "The matching algorithm considers multiple factors to provide accurate recommendations.",
                
                "**Smart Eligibility Matching:**\n\n" +
                "Our system uses intelligent algorithms to match users with opportunities. Here's how:\n\n" +
                "**Input Factors:**\n" +
                "• Demographics (age, gender, location)\n" +
                "• Education level and field of study\n" +
                "• Economic background and category\n" +
                "• User preferences and interests\n\n" +
                "**Matching Process:**\n" +
                "• Real-time analysis of eligibility criteria\n" +
                "• Weighted scoring based on relevance\n" +
                "• Filtering by availability and deadlines\n" +
                "• Ranking by best fit and opportunities\n\n" +
                "This ensures users only see opportunities they're actually eligible for!",
                
                "**How Eligibility Works:**\n\n" +
                "The system is designed to be both comprehensive and user-friendly:\n\n" +
                "**Data Collection:**\n" +
                "• Simple form-based input\n" +
                "• Optional advanced criteria\n" +
                "• Secure data storage\n\n" +
                "**Analysis Engine:**\n" +
                "• Multi-criteria evaluation\n" +
                "• Boolean and range-based matching\n" +
                "• Priority-based ranking\n\n" +
                "**Results Delivery:**\n" +
                "• Clear eligibility status\n" +
                "• Detailed requirement breakdown\n" +
                "• Direct application links\n\n" +
                "This approach maximizes both accuracy and user experience!"
            };
            return eligibilityResponses[new Random().nextInt(eligibilityResponses.length)];
        }
        
        // Technical implementation responses with variations
        if (message.contains("technical") || message.contains("implementation") || message.contains("code") || message.contains("architecture") || message.contains("backend") || message.contains("frontend")) {
            String[] techResponses = {
                "**Technical Architecture:**\n\n" +
                "**Backend:**\n" +
                "• Spring Boot 3.5.3 with Java 17\n" +
                "• Spring Security for authentication\n" +
                "• Spring Data JPA for database operations\n" +
                "• H2 (development) / MySQL (production)\n" +
                "• RESTful API endpoints\n\n" +
                "**Frontend:**\n" +
                "• Thymeleaf templates\n" +
                "• Responsive CSS with modern design\n" +
                "• JavaScript for dynamic interactions\n" +
                "• AJAX for API communications\n\n" +
                "**Key Components:**\n" +
                "• Controllers for each entity (Scholarship, Job, etc.)\n" +
                "• Repository pattern for data access\n" +
                "• Service layer for business logic\n" +
                "• Security configuration for user management",
                
                "**System Architecture Overview:**\n\n" +
                "**Technology Stack:**\n" +
                "• **Backend:** Spring Boot, Spring Security, Spring Data JPA\n" +
                "• **Database:** H2 (dev) / MySQL (prod)\n" +
                "• **Frontend:** Thymeleaf, Bootstrap, JavaScript\n" +
                "• **Security:** Role-based access control\n\n" +
                "**Design Patterns:**\n" +
                "• MVC Architecture\n" +
                "• Repository Pattern\n" +
                "• Service Layer Pattern\n" +
                "• Dependency Injection\n\n" +
                "**Key Features:**\n" +
                "• RESTful APIs\n" +
                "• Responsive design\n" +
                "• Real-time data processing\n" +
                "• Secure user management",
                
                "**Development Details:**\n\n" +
                "**Backend Components:**\n" +
                "• **Controllers:** Handle HTTP requests and responses\n" +
                "• **Services:** Business logic and data processing\n" +
                "• **Repositories:** Data access layer\n" +
                "• **Entities:** Database models with JPA annotations\n\n" +
                "**Frontend Features:**\n" +
                "• **Templates:** Dynamic HTML generation\n" +
                "• **Styling:** Modern CSS with Bootstrap\n" +
                "• **Interactions:** JavaScript for dynamic behavior\n" +
                "• **API Integration:** AJAX for seamless data exchange\n\n" +
                "**Security Implementation:**\n" +
                "• User authentication and authorization\n" +
                "• Role-based access control\n" +
                "• CSRF protection\n" +
                "• Secure session management"
            };
            return techResponses[new Random().nextInt(techResponses.length)];
        }
        
        // Database responses with variations
        if (message.contains("database") || message.contains("data") || message.contains("storage") || message.contains("entity")) {
            String[] dbResponses = {
                "**Database Structure:**\n\n" +
                "The platform uses the following main entities:\n" +
                "• **User** - User accounts and profiles\n" +
                "• **Scholarship** - Educational funding opportunities\n" +
                "• **GovernmentJob** - Public sector positions\n" +
                "• **GovernmentScheme** - Welfare programs\n" +
                "• **CompetitiveExam** - Entrance examinations\n" +
                "• **Contact** - User inquiries and feedback\n\n" +
                "Each entity stores eligibility criteria as JSON fields for flexible matching. " +
                "The system supports both H2 (in-memory) for development and MySQL for production.",
                
                "**Data Model Design:**\n\n" +
                "**Core Entities:**\n" +
                "• **User Management:** User, Role, Authentication\n" +
                "• **Opportunities:** Scholarship, GovernmentJob, GovernmentScheme, CompetitiveExam\n" +
                "• **Communication:** Contact, Feedback\n\n" +
                "**Key Features:**\n" +
                "• **Flexible Criteria:** JSON-based eligibility storage\n" +
                "• **Relationships:** Proper foreign key constraints\n" +
                "• **Indexing:** Optimized for search performance\n" +
                "• **Validation:** Data integrity and constraints\n\n" +
                "**Database Support:**\n" +
                "• H2 for development and testing\n" +
                "• MySQL for production deployment\n" +
                "• JPA/Hibernate for ORM\n" +
                "• Automatic schema management",
                
                "**Data Architecture:**\n\n" +
                "**Entity Relationships:**\n" +
                "• Users can have multiple preferences\n" +
                "• Opportunities linked to eligibility criteria\n" +
                "• Contact system for user inquiries\n\n" +
                "**Storage Strategy:**\n" +
                "• **Structured Data:** User profiles, basic opportunity info\n" +
                "• **Semi-structured:** Eligibility criteria as JSON\n" +
                "• **Relational:** User-opportunity matching\n\n" +
                "**Performance Optimizations:**\n" +
                "• Indexed search fields\n" +
                "• Efficient query patterns\n" +
                "• Caching strategies\n" +
                "• Connection pooling"
            };
            return dbResponses[new Random().nextInt(dbResponses.length)];
        }
        
        // Help and support responses with variations
        if (message.contains("help") || message.contains("support") || message.contains("how to") || message.contains("guide")) {
            String[] helpResponses = {
                "**How to Use Eligibuddy:**\n\n" +
                "**For Users:**\n" +
                "1. **Register/Login** - Create an account or sign in\n" +
                "2. **Complete Profile** - Fill in your personal and educational details\n" +
                "3. **Run Analysis** - Use the eligibility checker to find opportunities\n" +
                "4. **View Results** - Browse matched scholarships, jobs, and schemes\n" +
                "5. **Apply** - Follow links to official application portals\n\n" +
                "**For Administrators:**\n" +
                "• Manage scholarships, jobs, and schemes\n" +
                "• View user contacts and feedback\n" +
                "• Monitor system usage and performance\n\n" +
                "Need more specific help? Ask about any particular feature!",
                
                "**User Guide:**\n\n" +
                "**Getting Started:**\n" +
                "1. **Account Setup:** Register with your email and create a secure password\n" +
                "2. **Profile Creation:** Complete your profile with accurate information\n" +
                "3. **Eligibility Check:** Run the analysis to find matching opportunities\n" +
                "4. **Results Review:** Examine the detailed results and requirements\n" +
                "5. **Application Process:** Follow the provided links to apply\n\n" +
                "**Tips for Best Results:**\n" +
                "• Keep your profile information up-to-date\n" +
                "• Be specific about your preferences\n" +
                "• Check results regularly for new opportunities\n" +
                "• Contact support if you need assistance\n\n" +
                "**Admin Features:**\n" +
                "• Add and manage opportunity listings\n" +
                "• Monitor user engagement\n" +
                "• Handle user inquiries and feedback",
                
                "**Step-by-Step Guide:**\n\n" +
                "**For New Users:**\n" +
                "1. **Registration:** Click 'Create Account' and fill in your details\n" +
                "2. **Login:** Use your credentials to access the platform\n" +
                "3. **Profile Setup:** Complete all required fields accurately\n" +
                "4. **Eligibility Analysis:** Click 'Check Eligibility' to run the analysis\n" +
                "5. **Results:** Review the matched opportunities and their details\n" +
                "6. **Applications:** Use the provided links to apply for opportunities\n\n" +
                "**For Administrators:**\n" +
                "• Access admin panel through the management section\n" +
                "• Add new opportunities with detailed criteria\n" +
                "• Monitor user activity and system performance\n" +
                "• Respond to user inquiries and feedback\n\n" +
                "Need help with a specific step? Just ask!"
            };
            return helpResponses[new Random().nextInt(helpResponses.length)];
        }
        
        // Specific feature questions
        if (message.contains("scholarship") || message.contains("scholarships")) {
            return "**Scholarship Features:**\n\n" +
                   "Eligibuddy helps you find scholarships based on:\n" +
                   "• **Academic Performance** - GPA, test scores\n" +
                   "• **Financial Need** - Income-based eligibility\n" +
                   "• **Demographics** - Age, gender, location\n" +
                   "• **Field of Study** - Subject-specific scholarships\n" +
                   "• **Category** - Reserved category opportunities\n\n" +
                   "**How it works:**\n" +
                   "1. Enter your academic and personal details\n" +
                   "2. System matches against scholarship criteria\n" +
                   "3. View detailed information and deadlines\n" +
                   "4. Apply directly through official channels\n\n" +
                   "The platform includes scholarships from government, private organizations, and educational institutions.";
        }
        
        if (message.contains("job") || message.contains("jobs") || message.contains("employment")) {
            return "**Government Job Features:**\n\n" +
                   "Find government employment opportunities based on:\n" +
                   "• **Educational Qualification** - Degree requirements\n" +
                   "• **Age Limits** - Position-specific age criteria\n" +
                   "• **Experience** - Required work experience\n" +
                   "• **Location** - Job posting locations\n" +
                   "• **Category** - Reserved category positions\n\n" +
                   "**Job Categories:**\n" +
                   "• Central Government Jobs\n" +
                   "• State Government Positions\n" +
                   "• Public Sector Undertakings\n" +
                   "• Defense and Security\n" +
                   "• Banking and Finance\n\n" +
                   "Get notified about new openings and application deadlines!";
        }
        
        if (message.contains("exam") || message.contains("exams") || message.contains("competitive")) {
            return "**Competitive Exam Features:**\n\n" +
                   "Discover competitive examinations for:\n" +
                   "• **Government Jobs** - UPSC, SSC, State PSC\n" +
                   "• **Banking** - IBPS, SBI, RBI\n" +
                   "• **Defense** - NDA, CDS, AFCAT\n" +
                   "• **Engineering** - GATE, ESE\n" +
                   "• **Medical** - NEET, AIIMS\n\n" +
                   "**Information Provided:**\n" +
                   "• Exam dates and application deadlines\n" +
                   "• Eligibility criteria and age limits\n" +
                   "• Application fees and procedures\n" +
                   "• Syllabus and exam pattern\n" +
                   "• Official notification links\n\n" +
                   "Stay updated with the latest exam notifications!";
        }
        
        if (message.contains("scheme") || message.contains("schemes") || message.contains("benefit")) {
            return "**Government Scheme Features:**\n\n" +
                   "Access information about government welfare schemes:\n" +
                   "• **Education** - Scholarship and fee waiver schemes\n" +
                   "• **Health** - Medical insurance and treatment schemes\n" +
                   "• **Employment** - Skill development and job creation\n" +
                   "• **Agriculture** - Farmer welfare and support schemes\n" +
                   "• **Social Security** - Pension and assistance programs\n\n" +
                   "**Scheme Details:**\n" +
                   "• Eligibility criteria and requirements\n" +
                   "• Application process and documents\n" +
                   "• Benefits and coverage details\n" +
                   "• Contact information and helplines\n" +
                   "• Official website links\n\n" +
                   "Find schemes that match your profile and needs!";
        }
        
        // User-specific scenarios
        if (message.contains("start") || message.contains("begin") || message.contains("new user") || message.contains("first time")) {
            return "**Getting Started with Eligibuddy:**\n\n" +
                   "Welcome to Eligibuddy! Here's how to begin your opportunity discovery journey:\n\n" +
                   "**Step 1: Create Your Account**\n" +
                   "• Click 'Create Account' on the homepage\n" +
                   "• Enter your email and create a secure password\n" +
                   "• Verify your account (if required)\n\n" +
                   "**Step 2: Complete Your Profile**\n" +
                   "• Fill in your personal details (age, gender, location)\n" +
                   "• Add your educational background and qualifications\n" +
                   "• Specify your category and income range\n" +
                   "• Select your areas of interest\n\n" +
                   "**Step 3: Run Eligibility Analysis**\n" +
                   "• Click 'Check Eligibility' on the homepage\n" +
                   "• Review the form and ensure all details are accurate\n" +
                   "• Submit to get your personalized results\n\n" +
                   "**Step 4: Explore Opportunities**\n" +
                   "• Browse matched scholarships, jobs, schemes, and exams\n" +
                   "• Click on any opportunity for detailed information\n" +
                   "• Follow application links to official portals\n\n" +
                   "**Pro Tip:** Keep your profile updated for the best matching results!";
        }
        
        if (message.contains("login") || message.contains("sign in") || message.contains("account")) {
            return "**Account & Login Help:**\n\n" +
                   "**To Login:**\n" +
                   "• Click 'Login' on the homepage\n" +
                   "• Enter your registered email and password\n" +
                   "• You'll be redirected to the main platform\n\n" +
                   "**Default Test Accounts:**\n" +
                   "• **Admin:** admin / admin123\n" +
                   "• **User:** user / user123\n\n" +
                   "**If You Forgot Your Password:**\n" +
                   "• Contact the administrator for password reset\n" +
                   "• Or create a new account with a different email\n\n" +
                   "**Account Benefits:**\n" +
                   "• Save your eligibility analysis results\n" +
                   "• Access personalized opportunity recommendations\n" +
                   "• Track your application progress\n" +
                   "• Receive updates on new opportunities\n\n" +
                   "**Need Help?** Contact support through the contact form!";
        }
        
        if (message.contains("form") || message.contains("fill") || message.contains("profile") || message.contains("details")) {
            return "**Completing Your Eligibility Profile:**\n\n" +
                   "**Required Information:**\n" +
                   "• **Personal Details:** First name, last name, age, gender\n" +
                   "• **Location:** State and district (for location-specific opportunities)\n" +
                   "• **Education:** Highest qualification and field of study\n" +
                   "• **Category:** General, OBC, SC, ST, EWS (affects eligibility)\n" +
                   "• **Income:** Annual family income range\n" +
                   "• **Preferences:** Select areas of interest (scholarships, jobs, etc.)\n\n" +
                   "**Optional Information:**\n" +
                   "• Email address (for notifications)\n" +
                   "• Disability status (for special category opportunities)\n" +
                   "• Specific field of interest\n\n" +
                   "**Tips for Better Results:**\n" +
                   "• Be accurate with your information\n" +
                   "• Select all relevant preferences\n" +
                   "• Update your profile regularly\n" +
                   "• Include all qualifications and achievements\n\n" +
                   "**Privacy:** Your information is secure and only used for matching opportunities!";
        }
        
        if (message.contains("result") || message.contains("match") || message.contains("found") || message.contains("opportunity")) {
            return "**Understanding Your Results:**\n\n" +
                   "**What You'll See:**\n" +
                   "• **Scholarships:** Educational funding opportunities with amounts and deadlines\n" +
                   "• **Government Schemes:** Welfare programs with benefits and eligibility\n" +
                   "• **Competitive Exams:** Entrance tests with dates and application fees\n" +
                   "• **Government Jobs:** Public sector positions with salary and vacancies\n\n" +
                   "**How Results Are Organized:**\n" +
                   "• Filtered by your eligibility criteria\n" +
                   "• Ranked by relevance and match quality\n" +
                   "• Grouped by opportunity type\n" +
                   "• Sorted by deadline or importance\n\n" +
                   "**Next Steps:**\n" +
                   "• Click on any opportunity for detailed information\n" +
                   "• Check application deadlines and requirements\n" +
                   "• Follow official application links\n" +
                   "• Contact the organization if you need clarification\n\n" +
                   "**Pro Tip:** Save or bookmark opportunities you're interested in!";
        }
        
        // Default responses with more variety
        String[] defaultResponses = {
            "Hi! I'm your Eligibuddy assistant. I can help you with:\n• Getting started with the platform\n• Understanding eligibility criteria\n• Completing your profile\n• Finding opportunities (scholarships, jobs, schemes, exams)\n• Account and login issues\n• Application guidance\n\nWhat would you like to know?",
            
            "Welcome to Eligibuddy! I'm here to guide you through finding opportunities. I can help with:\n• Setting up your account and profile\n• Understanding how eligibility matching works\n• Exploring different opportunity types\n• Troubleshooting common issues\n• Career and education guidance\n\nHow can I assist you today?",
            
            "Hello! I'm your personal guide for Eligibuddy. I can provide help with:\n• Platform navigation and features\n• Eligibility analysis process\n• Opportunity discovery and applications\n• Profile management and updates\n• Technical support and guidance\n\nWhat specific help do you need?",
            
            "Hi there! I'm here to help you make the most of Eligibuddy. I can assist with:\n• Getting started as a new user\n• Understanding eligibility requirements\n• Finding relevant opportunities\n• Application processes and deadlines\n• Platform features and functionality\n\nWhat would you like to explore?",
            
            "Greetings! I'm your Eligibuddy helper. I can answer questions about:\n• User registration and account setup\n• Eligibility criteria and matching\n• Opportunity categories and details\n• Application guidance and tips\n• Platform usage and best practices\n\nHow can I help you succeed today?"
        };
        
        Random random = new Random();
        return defaultResponses[random.nextInt(defaultResponses.length)];
    }
}


