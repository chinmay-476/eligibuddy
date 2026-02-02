package com.example.demo.init;

import com.example.demo.opportunity.CompetitiveExam;
import com.example.demo.opportunity.CompetitiveExamRepository;
import com.example.demo.opportunity.GovernmentJob;
import com.example.demo.opportunity.GovernmentJobRepository;
import com.example.demo.opportunity.GovernmentScheme;
import com.example.demo.opportunity.GovernmentSchemeRepository;
import com.example.demo.opportunity.Scholarship;
import com.example.demo.opportunity.ScholarshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class CompleteDataInitializer implements CommandLineRunner {
    
    @Autowired
    private ScholarshipRepository scholarshipRepository;
    
    @Autowired
    private GovernmentSchemeRepository schemeRepository;
    
    @Autowired
    private CompetitiveExamRepository examRepository;
    
    @Autowired
    private GovernmentJobRepository jobRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Only initialize if no data exists
        if (scholarshipRepository.count() == 0 && schemeRepository.count() == 0 && 
            examRepository.count() == 0 && jobRepository.count() == 0) {
            initializeAllData();
            System.out.println("Complete database initialized with all hardcoded data.");
        }
    }
    
    private void initializeAllData() {
        initializeScholarships();
        initializeSchemes();
        initializeExams();
        initializeJobs();
    }
    
    private void initializeScholarships() {
        // Merit-based Scholarships
        createScholarship("National Merit Scholarship", "For students with excellent academic performance across India", 
            "Merit-based", "₹50,000 per year", LocalDate.of(2024, 3, 31),
            "[\"12th\", \"Graduate\", \"Post Graduate\"]", "[\"0-100000\", \"100000-250000\"]", 
            "[\"General\", \"OBC\", \"SC\", \"ST\", \"EWS\"]", null, null, null, null, null);
            
        createScholarship("Inspire Scholarship (KVPY)", "Kishore Vaigyanik Protsahan Yojana for science students", 
            "Merit-based", "₹5,000-7,000 per month", LocalDate.of(2024, 9, 15),
            "[\"12th\", \"Graduate\"]", null, null, "[\"Science\", \"Engineering\", \"Medical\"]", null, null, 16, 25);
            
        createScholarship("Central Sector Scholarship", "Merit-based scholarship for college and university students", 
            "Merit-based", "₹10,000-20,000 per year", LocalDate.of(2024, 10, 31),
            "[\"12th\", \"Graduate\"]", "[\"0-100000\", \"100000-250000\", \"250000-500000\"]", 
            "[\"General\", \"OBC\", \"SC\", \"ST\", \"EWS\"]", null, null, null, null, null);
            
        // Category-based Scholarships
        createScholarship("SC/ST Excellence Scholarship", "Promoting higher education among SC/ST students", 
            "Category-based", "₹75,000 per year", LocalDate.of(2024, 5, 30),
            "[\"10th\", \"12th\", \"Graduate\", \"Post Graduate\"]", "[\"0-100000\", \"100000-250000\", \"250000-500000\"]", 
            "[\"SC\", \"ST\"]", null, null, null, null, null);
            
        createScholarship("OBC Merit Scholarship", "Financial assistance for Other Backward Class students", 
            "Category-based", "₹35,000 per year", LocalDate.of(2024, 6, 30),
            "[\"12th\", \"Graduate\", \"Post Graduate\"]", "[\"0-100000\", \"100000-250000\"]", "[\"OBC\"]", null, null, null, null, null);
            
        createScholarship("EWS Scholarship Scheme", "Support for Economically Weaker Section students", 
            "Category-based", "₹40,000 per year", LocalDate.of(2024, 7, 15),
            "[\"12th\", \"Graduate\"]", "[\"0-100000\", \"100000-250000\", \"250000-500000\"]", "[\"EWS\"]", null, null, null, null, null);
            
        // Gender-based Scholarships
        createScholarship("Girl Child Education Fund", "Empowering girls through education funding", 
            "Gender-based", "₹40,000 per year", LocalDate.of(2024, 6, 15),
            "[\"10th\", \"12th\", \"Graduate\"]", "[\"0-100000\", \"100000-250000\"]", null, null, "[\"Female\"]", null, null, null);
            
        createScholarship("Women in STEM Scholarship", "Encouraging women to pursue science and technology", 
            "Gender-based", "₹1,25,000 per year", LocalDate.of(2024, 4, 30),
            "[\"12th\", \"Graduate\", \"Post Graduate\"]", null, null, "[\"Science\", \"Engineering\", \"Medical\"]", "[\"Female\"]", null, 17, 30);
            
        // Field-specific Scholarships
        createScholarship("Engineering Excellence Award", "For aspiring engineers with outstanding potential", 
            "Field-specific", "₹1,00,000 per year", LocalDate.of(2024, 7, 31),
            "[\"12th\", \"Graduate\"]", "[\"0-100000\", \"100000-250000\", \"250000-500000\"]", null, "[\"Engineering\"]", null, null, null, null);
            
        createScholarship("Medical Student Support Scheme", "Supporting future doctors and healthcare professionals", 
            "Field-specific", "₹1,50,000 per year", LocalDate.of(2024, 8, 15),
            "[\"12th\", \"Graduate\"]", "[\"0-100000\", \"100000-250000\"]", null, "[\"Medical\"]", null, null, null, null);
            
        createScholarship("Arts and Culture Scholarship", "Promoting arts, literature, and cultural studies", 
            "Field-specific", "₹30,000 per year", LocalDate.of(2024, 9, 30),
            "[\"12th\", \"Graduate\", \"Post Graduate\"]", null, null, "[\"Arts\"]", null, null, 16, 35);
            
        createScholarship("Agriculture Innovation Grant", "Supporting agricultural research and development", 
            "Field-specific", "₹60,000 per year", LocalDate.of(2024, 11, 15),
            "[\"Graduate\", \"Post Graduate\"]", null, null, "[\"Agriculture\"]", null, null, 20, 40);
            
        createScholarship("Law Students Excellence Award", "Supporting aspiring legal professionals", 
            "Field-specific", "₹45,000 per year", LocalDate.of(2025, 1, 31),
            "[\"Graduate\", \"Post Graduate\"]", null, null, "[\"Law\"]", null, null, 20, 35);
            
        // State-specific Scholarships
        createScholarship("Odisha Merit Scholarship", "State government scholarship for Odisha students", 
            "State-specific", "₹15,000 per year", LocalDate.of(2024, 3, 15),
            "[\"12th\", \"Graduate\"]", "[\"0-100000\", \"100000-250000\"]", null, null, null, "[\"Odisha\"]", null, null);
            
        createScholarship("Maharashtra Talent Search", "Identifying and nurturing talent in Maharashtra", 
            "State-specific", "₹12,000 per year", LocalDate.of(2024, 2, 28),
            "[\"10th\", \"12th\"]", null, null, null, null, "[\"Maharashtra\"]", 14, 20);
            
        // Minority Scholarships
        createScholarship("Muslim Minority Scholarship", "Educational support for Muslim community students", 
            "Minority", "₹30,000 per year", LocalDate.of(2024, 4, 15),
            "[\"10th\", \"12th\", \"Graduate\"]", "[\"0-100000\", \"100000-250000\"]", null, null, null, null, null, null);
            
        createScholarship("Christian Minority Education Fund", "Supporting Christian community students", 
            "Minority", "₹25,000 per year", LocalDate.of(2024, 5, 31),
            "[\"12th\", \"Graduate\"]", "[\"0-100000\", \"100000-250000\", \"250000-500000\"]", null, null, null, null, null, null);
            
        // Disability Scholarships
        createScholarship("Divyang Scholarship Scheme", "Supporting students with disabilities", 
            "Special Needs", "₹50,000 per year", LocalDate.of(2024, 12, 31),
            "[\"10th\", \"12th\", \"Graduate\", \"Post Graduate\"]", "[\"0-100000\", \"100000-250000\", \"250000-500000\"]", null, null, null, null, null, null);
            
        // Social Support Scholarships
        createScholarship("Widow's Children Scholarship", "Educational support for children of widowed mothers", 
            "Social Support", "₹25,000 per year", LocalDate.of(2024, 8, 31),
            "[\"10th\", \"12th\", \"Graduate\"]", "[\"0-100000\", \"100000-250000\"]", null, null, null, null, null, null);
    }
    
    private void createScholarship(String name, String description, String type, String amount, 
                                  LocalDate deadline, String qualificationCriteria, String incomeCriteria,
                                  String categoryCriteria, String fieldCriteria, String genderCriteria,
                                  String stateCriteria, Integer minAge, Integer maxAge) {
        Scholarship scholarship = new Scholarship();
        scholarship.setName(name);
        scholarship.setDescription(description);
        scholarship.setType(type);
        scholarship.setAmount(amount);
        scholarship.setDeadline(deadline);
        scholarship.setQualificationCriteria(qualificationCriteria);
        scholarship.setIncomeCriteria(incomeCriteria);
        scholarship.setCategoryCriteria(categoryCriteria);
        scholarship.setFieldCriteria(fieldCriteria);
        scholarship.setGenderCriteria(genderCriteria);
        scholarship.setStateCriteria(stateCriteria);
        scholarship.setMinAge(minAge);
        scholarship.setMaxAge(maxAge);
        scholarship.setActive(true);
        
        scholarshipRepository.save(scholarship);
    }
    
    private void initializeSchemes() {
        // Housing Schemes
        createScheme("Pradhan Mantri Awas Yojana (Urban)", "Affordable housing scheme for urban economically weaker sections", 
            "Housing", "₹2.5 Lakh subsidy + Low interest loan", "Ongoing", null, "[\"0-100000\", \"100000-250000\", \"250000-500000\"]", null, null, null, null, 25, 60);
            
        createScheme("Pradhan Mantri Awas Yojana (Rural)", "Housing for All in rural areas", 
            "Housing", "₹1.2 Lakh construction assistance", "Ongoing", null, "[\"0-100000\", \"100000-250000\"]", null, null, null, null, 25, 65);
            
        // Healthcare Schemes
        createScheme("Ayushman Bharat - PMJAY", "World's largest health insurance scheme for poor families", 
            "Healthcare", "₹5 Lakh health cover per family", "Ongoing", null, "[\"0-100000\", \"100000-250000\"]", "[\"SC\", \"ST\", \"OBC\", \"EWS\"]", null, null, null, null, null);
            
        createScheme("Pradhan Mantri Suraksha Bima Yojana", "Accident insurance scheme for all", 
            "Insurance", "₹2 Lakh accident insurance", "Ongoing", null, "[\"0-100000\", \"100000-250000\", \"250000-500000\"]", null, null, null, null, 18, 70);
            
        createScheme("Pradhan Mantri Jan Arogya Yojana", "Comprehensive healthcare coverage", 
            "Healthcare", "Free treatment up to ₹5 Lakh", "Ongoing", null, "[\"0-100000\", \"100000-250000\"]", "[\"SC\", \"ST\", \"OBC\", \"EWS\"]", null, null, null, null, null);
            
        // Social Security Schemes
        createScheme("Pension Scheme for Senior Citizens", "Monthly pension for elderly citizens", 
            "Social Security", "₹3,000 per month", "Ongoing", null, "[\"0-100000\", \"100000-250000\"]", null, null, null, null, 60, 100);
            
        createScheme("Widow Pension Scheme", "Financial support for widowed women", 
            "Social Security", "₹1,500 per month", "Ongoing", null, "[\"0-100000\", \"100000-250000\"]", null, null, "[\"Female\"]", null, 18, 80);
            
        createScheme("Disability Pension Scheme", "Monthly assistance for persons with disabilities", 
            "Social Security", "₹2,000 per month", "Ongoing", null, "[\"0-100000\", \"100000-250000\"]", null, null, null, "[\"Yes\"]", 18, 80);
            
        // Business & Employment Schemes
        createScheme("Startup India Initiative", "Support ecosystem for nurturing innovation and startups", 
            "Business", "Tax benefits, funding support, mentorship", "Ongoing", "[\"Graduate\", \"Post Graduate\"]", null, null, "[\"Management\", \"Engineering\", \"Commerce\"]", null, null, 21, 45);
            
        createScheme("Pradhan Mantri Mudra Yojana", "Micro-finance scheme for small businesses", 
            "Finance", "Loans from ₹50,000 to ₹10 Lakh", "Ongoing", "[\"10th\", \"12th\", \"Graduate\"]", "[\"0-100000\", \"100000-250000\", \"250000-500000\"]", null, null, null, null, 18, 65);
            
        createScheme("Stand Up India Scheme", "Supporting SC/ST and women entrepreneurs", 
            "Business", "₹10 Lakh to ₹1 Crore loan", "Ongoing", null, null, "[\"SC\", \"ST\"]", null, "[\"Female\"]", null, 18, 65);
            
        createScheme("Prime Minister Employment Generation Programme", "Setting up micro enterprises for employment generation", 
            "Employment", "Up to ₹25 Lakh project cost", "Ongoing", "[\"10th\", \"12th\", \"Graduate\"]", null, null, null, null, null, 18, 55);
            
        // Skill Development Schemes
        createScheme("Skill India Mission", "Training programs for skill development", 
            "Skill Development", "Free training + certificate + job assistance", "Ongoing", "[\"Below 10th\", \"10th\", \"12th\"]", null, null, null, null, null, 15, 45);
            
        createScheme("Pradhan Mantri Kaushal Vikas Yojana", "Recognition of Prior Learning and skill certification", 
            "Skill Development", "Free skill training + ₹8,000 incentive", "Ongoing", "[\"Below 10th\", \"10th\", \"12th\", \"Graduate\"]", null, null, null, null, null, 15, 45);
            
        // Agricultural Schemes
        createScheme("Pradhan Mantri Kisan Samman Nidhi", "Income support for small and marginal farmers", 
            "Agriculture", "₹6,000 per year in 3 installments", "Ongoing", "[\"Below 10th\", \"10th\", \"12th\"]", null, null, null, null, null, 18, 70);
            
        createScheme("Pradhan Mantri Fasal Bima Yojana", "Crop insurance scheme for farmers", 
            "Agriculture", "Crop insurance at subsidized rates", "Ongoing", "[\"Below 10th\", \"10th\", \"12th\", \"Graduate\"]", null, null, null, null, null, 18, 70);
            
        // Education Schemes
        createScheme("Samagra Shiksha Abhiyan", "Holistic education from pre-school to Class XII", 
            "Education", "Free education, books, uniforms", "Ongoing", null, "[\"0-100000\", \"100000-250000\"]", null, null, null, null, 3, 18);
            
        createScheme("Mid Day Meal Scheme", "Nutritional support to school children", 
            "Education", "Free nutritious meals", "Ongoing", "[\"Below 10th\"]", null, null, null, null, null, 6, 14);
            
        // Additional Schemes from original data
        createScheme("Pradhan Mantri Kisan Samman Nidhi", "Income support for small and marginal farmers", 
            "Agriculture", "₹6,000 per year in 3 installments", "Ongoing", "[\"Below 10th\", \"10th\", \"12th\"]", null, null, null, null, null, 18, 70);
            
        createScheme("Pradhan Mantri Fasal Bima Yojana", "Crop insurance scheme for farmers", 
            "Agriculture", "Crop insurance at subsidized rates", "Ongoing", "[\"Below 10th\", \"10th\", \"12th\", \"Graduate\"]", null, null, null, null, null, 18, 70);
            
        createScheme("Stand Up India Scheme", "Supporting SC/ST and women entrepreneurs", 
            "Business", "₹10 Lakh to ₹1 Crore loan", "Ongoing", null, null, "[\"SC\", \"ST\"]", null, "[\"Female\"]", null, 18, 65);
            
        createScheme("Prime Minister Employment Generation Programme", "Setting up micro enterprises for employment generation", 
            "Employment", "Up to ₹25 Lakh project cost", "Ongoing", "[\"10th\", \"12th\", \"Graduate\"]", null, null, null, null, null, 18, 55);
    }
    
    private void createScheme(String name, String description, String type, String benefit, 
                             String deadline, String qualificationCriteria, String incomeCriteria,
                             String categoryCriteria, String fieldCriteria, String genderCriteria,
                             String disabilityCriteria, Integer minAge, Integer maxAge) {
        GovernmentScheme scheme = new GovernmentScheme();
        scheme.setName(name);
        scheme.setDescription(description);
        scheme.setType(type);
        scheme.setBenefit(benefit);
        scheme.setDeadline(deadline);
        scheme.setQualificationCriteria(qualificationCriteria);
        scheme.setIncomeCriteria(incomeCriteria);
        scheme.setCategoryCriteria(categoryCriteria);
        scheme.setFieldCriteria(fieldCriteria);
        scheme.setGenderCriteria(genderCriteria);
        scheme.setDisabilityCriteria(disabilityCriteria);
        scheme.setMinAge(minAge);
        scheme.setMaxAge(maxAge);
        scheme.setActive(true);
        
        schemeRepository.save(scheme);
    }
    
    private void initializeExams() {
        // Civil Services Exams
        createExam("UPSC Civil Services Examination", "Premier examination for joining Indian Administrative Service", 
            "Civil Services", "June 2024 (Prelims)", "₹100 (General), Free (SC/ST/Female)",
            "[\"Graduate\", \"Post Graduate\"]", null, null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 21, 32);
            
        createExam("State Public Service Commission", "State-level civil services examination", 
            "Civil Services", "Various dates by state", "₹150-500",
            "[\"Graduate\", \"Post Graduate\"]", null, null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 21, 40);
            
        // Staff Selection Commission
        createExam("SSC Combined Graduate Level (CGL)", "Recruitment for various Group B and C posts", 
            "Staff Selection", "July 2024", "₹100 (General), Free (SC/ST/Female)",
            "[\"Graduate\", \"Post Graduate\"]", null, null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 18, 32);
            
        createExam("SSC Combined Higher Secondary Level (CHSL)", "Recruitment for 10+2 level posts", 
            "Staff Selection", "August 2024", "₹100 (General), Free (SC/ST/Female)",
            "[\"12th\"]", null, null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 18, 27);
            
        createExam("SSC Multi Tasking Staff (MTS)", "Multi-tasking staff recruitment", 
            "Staff Selection", "September 2024", "₹100 (General), Free (SC/ST/Female)",
            "[\"10th\"]", null, null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 18, 25);
            
        // Banking Exams
        createExam("IBPS Bank PO", "Probationary Officer recruitment in public sector banks", 
            "Banking", "October 2024", "₹175 (General), ₹100 (SC/ST)",
            "[\"Graduate\", \"Post Graduate\"]", "[\"Commerce\", \"Management\", \"Arts\", \"Science\"]", null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 20, 30);
            
        createExam("IBPS Bank Clerk", "Clerical cadre recruitment in banks", 
            "Banking", "December 2024", "₹175 (General), ₹100 (SC/ST)",
            "[\"Graduate\", \"Post Graduate\"]", null, null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 20, 28);
            
        createExam("SBI PO (Probationary Officer)", "Officer recruitment in State Bank of India", 
            "Banking", "November 2024", "₹750 (General), ₹125 (SC/ST)",
            "[\"Graduate\", \"Post Graduate\"]", null, null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 21, 30);
            
        createExam("RBI Grade B Officer", "Reserve Bank of India Officer recruitment", 
            "Banking", "March 2024", "₹850 (General), ₹100 (SC/ST)",
            "[\"Graduate\", \"Post Graduate\"]", "[\"Commerce\", \"Management\", \"Economics\"]", null, null, 21, 30);
            
        // Railway Exams
        createExam("Railway Recruitment Board NTPC", "Non-Technical Popular Categories in Indian Railways", 
            "Railway", "August 2024", "₹500 (General), ₹250 (SC/ST)",
            "[\"Graduate\", \"Post Graduate\"]", null, null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 18, 33);
            
        createExam("RRB Group D", "Level-1 posts in Indian Railways", 
            "Railway", "June 2024", "₹500 (General), ₹250 (SC/ST)",
            "[\"10th\"]", null, null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 18, 33);
            
        createExam("RRB Assistant Loco Pilot", "Technical posts in Indian Railways", 
            "Railway", "July 2024", "₹500 (General), ₹250 (SC/ST)",
            "[\"12th\", \"Diploma\"]", "[\"Engineering\"]", null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 18, 28);
            
        // Engineering Entrance Exams
        createExam("GATE (Graduate Aptitude Test)", "For admission to PG programs and PSU recruitment", 
            "Engineering", "February 2024", "₹1,850 (General), ₹925 (SC/ST/Female)",
            "[\"Graduate\", \"Post Graduate\"]", "[\"Engineering\", \"Science\"]", null, null, 21, 35);
            
        createExam("JEE Main", "Joint Entrance Examination for engineering colleges", 
            "Engineering", "April & May 2024", "₹1,000 (General), ₹500 (SC/ST)",
            "[\"12th\"]", "[\"Science\", \"Engineering\"]", null, null, 17, 25);
            
        createExam("JEE Advanced", "For admission to IITs", 
            "Engineering", "May 2024", "₹2,800 (General), ₹1,400 (SC/ST)",
            "[\"12th\"]", "[\"Science\", \"Engineering\"]", null, null, 17, 22);
            
        // Medical Entrance Exams
        createExam("NEET (Medical Entrance)", "National eligibility entrance test for medical courses", 
            "Medical", "May 2024", "₹1,700 (General), ₹1,000 (SC/ST/OBC)",
            "[\"12th\"]", "[\"Medical\", \"Science\"]", null, "{\"SC\": 5, \"ST\": 5, \"OBC\": 5}", 17, 25);
            
        createExam("AIIMS MBBS Entrance", "All India Institute of Medical Sciences entrance", 
            "Medical", "May 2024", "₹1,600 (General), ₹800 (SC/ST)",
            "[\"12th\"]", "[\"Medical\", \"Science\"]", null, null, 17, 25);
            
        // Defense Exams
        createExam("NDA (National Defence Academy)", "Joint services training for Army, Navy, Air Force", 
            "Defense", "April & September 2024", "₹100",
            "[\"12th\"]", null, "[\"Male\"]", null, 16, 19);
            
        createExam("CDS (Combined Defence Services)", "Officer entry in Indian Armed Forces", 
            "Defense", "February & November 2024", "₹200",
            "[\"Graduate\"]", null, "[\"Male\", \"Female\"]", null, 19, 25);
            
        createExam("AFCAT (Air Force Common Admission Test)", "Officer entry in Indian Air Force", 
            "Defense", "February & August 2024", "₹250",
            "[\"Graduate\"]", null, "[\"Male\", \"Female\"]", null, 20, 24);
            
        // Teaching Exams
        createExam("CTET (Central Teacher Eligibility Test)", "Teaching eligibility for central government schools", 
            "Teaching", "July 2024", "₹1,000 (One level), ₹1,200 (Both levels)",
            "[\"12th\", \"Graduate\"]", "[\"Teaching\", \"Arts\", \"Science\"]", null, null, 18, 35);
            
        createExam("UGC NET", "National eligibility test for lectureship", 
            "Teaching", "Multiple sessions", "₹1,150 (General), ₹600 (SC/ST)",
            "[\"Post Graduate\"]", "[\"Arts\", \"Science\", \"Commerce\"]", null, null, 18, 30);
            
        // Law Exams
        createExam("CLAT (Common Law Admission Test)", "For admission to National Law Universities", 
            "Law", "May 2024", "₹4,000 (General), ₹3,500 (SC/ST)",
            "[\"12th\", \"Graduate\"]", "[\"Law\", \"Arts\"]", null, null, 17, 30);
            
        // Management Exams
        createExam("CAT (Common Admission Test)", "For admission to IIMs and top B-schools", 
            "Management", "November 2024", "₹2,300 (General), ₹1,150 (SC/ST)",
            "[\"Graduate\", \"Post Graduate\"]", "[\"Management\", \"Commerce\", \"Arts\", \"Science\"]", null, null, 20, 35);
            
        // Additional Exams from original data
        createExam("RBI Grade B Officer", "Reserve Bank of India Officer recruitment", 
            "Banking", "March 2024", "₹850 (General), ₹100 (SC/ST)",
            "[\"Graduate\", \"Post Graduate\"]", "[\"Commerce\", \"Management\", \"Economics\"]", null, null, 21, 30);
            
        createExam("Railway Recruitment Board NTPC", "Non-Technical Popular Categories in Indian Railways", 
            "Railway", "August 2024", "₹500 (General), ₹250 (SC/ST)",
            "[\"Graduate\", \"Post Graduate\"]", null, null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 18, 33);
            
        createExam("RRB Group D", "Level-1 posts in Indian Railways", 
            "Railway", "June 2024", "₹500 (General), ₹250 (SC/ST)",
            "[\"10th\"]", null, null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 18, 33);
            
        createExam("RRB Assistant Loco Pilot", "Technical posts in Indian Railways", 
            "Railway", "July 2024", "₹500 (General), ₹250 (SC/ST)",
            "[\"12th\", \"Diploma\"]", "[\"Engineering\"]", null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 18, 28);
            
        createExam("GATE (Graduate Aptitude Test)", "For admission to PG programs and PSU recruitment", 
            "Engineering", "February 2024", "₹1,850 (General), ₹925 (SC/ST/Female)",
            "[\"Graduate\", \"Post Graduate\"]", "[\"Engineering\", \"Science\"]", null, null, 21, 35);
            
        createExam("JEE Main", "Joint Entrance Examination for engineering colleges", 
            "Engineering", "April & May 2024", "₹1,000 (General), ₹500 (SC/ST)",
            "[\"12th\"]", "[\"Science\", \"Engineering\"]", null, null, 17, 25);
            
        createExam("JEE Advanced", "For admission to IITs", 
            "Engineering", "May 2024", "₹2,800 (General), ₹1,400 (SC/ST)",
            "[\"12th\"]", "[\"Science\", \"Engineering\"]", null, null, 17, 22);
            
        createExam("NEET (Medical Entrance)", "National eligibility entrance test for medical courses", 
            "Medical", "May 2024", "₹1,700 (General), ₹1,000 (SC/ST/OBC)",
            "[\"12th\"]", "[\"Medical\", \"Science\"]", null, "{\"SC\": 5, \"ST\": 5, \"OBC\": 5}", 17, 25);
            
        createExam("AIIMS MBBS Entrance", "All India Institute of Medical Sciences entrance", 
            "Medical", "May 2024", "₹1,600 (General), ₹800 (SC/ST)",
            "[\"12th\"]", "[\"Medical\", \"Science\"]", null, null, 17, 25);
            
        createExam("NDA (National Defence Academy)", "Joint services training for Army, Navy, Air Force", 
            "Defense", "April & September 2024", "₹100",
            "[\"12th\"]", null, "[\"Male\"]", null, 16, 19);
            
        createExam("CDS (Combined Defence Services)", "Officer entry in Indian Armed Forces", 
            "Defense", "February & November 2024", "₹200",
            "[\"Graduate\"]", null, "[\"Male\", \"Female\"]", null, 19, 25);
            
        createExam("AFCAT (Air Force Common Admission Test)", "Officer entry in Indian Air Force", 
            "Defense", "February & August 2024", "₹250",
            "[\"Graduate\"]", null, "[\"Male\", \"Female\"]", null, 20, 24);
            
        createExam("CTET (Central Teacher Eligibility Test)", "Teaching eligibility for central government schools", 
            "Teaching", "July 2024", "₹1,000 (One level), ₹1,200 (Both levels)",
            "[\"12th\", \"Graduate\"]", "[\"Teaching\", \"Arts\", \"Science\"]", null, null, 18, 35);
            
        createExam("UGC NET", "National eligibility test for lectureship", 
            "Teaching", "Multiple sessions", "₹1,150 (General), ₹600 (SC/ST)",
            "[\"Post Graduate\"]", "[\"Arts\", \"Science\", \"Commerce\"]", null, null, 18, 30);
            
        createExam("CLAT (Common Law Admission Test)", "For admission to National Law Universities", 
            "Law", "May 2024", "₹4,000 (General), ₹3,500 (SC/ST)",
            "[\"12th\", \"Graduate\"]", "[\"Law\", \"Arts\"]", null, null, 17, 30);
    }
    
    private void createExam(String name, String description, String type, String examDate, 
                           String applicationFee, String qualificationCriteria, String fieldCriteria,
                           String genderCriteria, String ageRelaxationCriteria, Integer minAge, Integer maxAge) {
        CompetitiveExam exam = new CompetitiveExam();
        exam.setName(name);
        exam.setDescription(description);
        exam.setType(type);
        exam.setExamDate(examDate);
        exam.setApplicationFee(applicationFee);
        exam.setQualificationCriteria(qualificationCriteria);
        exam.setFieldCriteria(fieldCriteria);
        exam.setGenderCriteria(genderCriteria);
        exam.setAgeRelaxationCriteria(ageRelaxationCriteria);
        exam.setMinAge(minAge);
        exam.setMaxAge(maxAge);
        exam.setActive(true);
        
        examRepository.save(exam);
    }
    
    private void initializeJobs() {
        // Police & Law Enforcement
        createJob("State Police Constable", "Constable recruitment in state police forces", 
            "Law Enforcement", "₹25,000 - ₹35,000 per month", "10,000+ (varies by state)",
            "[\"10th\", \"12th\"]", null, "[\"Male\", \"Female\"]", "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 18, 25);
            
        createJob("Sub Inspector (SI) Police", "Sub Inspector recruitment in state police", 
            "Law Enforcement", "₹35,000 - ₹50,000 per month", "5,000+ (varies by state)",
            "[\"Graduate\"]", null, "[\"Male\", \"Female\"]", "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 21, 28);
            
        createJob("Central Reserve Police Force (CRPF)", "Paramilitary force recruitment", 
            "Law Enforcement", "₹25,000 - ₹40,000 per month", "15,000+",
            "[\"10th\", \"12th\"]", null, "[\"Male\", \"Female\"]", null, 18, 25);
            
        createJob("Border Security Force (BSF)", "Border guarding force recruitment", 
            "Law Enforcement", "₹25,000 - ₹40,000 per month", "8,000+",
            "[\"10th\", \"12th\"]", null, "[\"Male\", \"Female\"]", null, 18, 25);
            
        // Teaching Jobs
        createJob("Primary School Teacher (TGT)", "Teaching positions in government primary schools", 
            "Education", "₹35,000 - ₹50,000 per month", "25,000+ (varies by state)",
            "[\"12th\", \"Graduate\"]", "[\"Teaching\", \"Arts\", \"Science\"]", null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 18, 35);
            
        createJob("Post Graduate Teacher (PGT)", "Senior secondary school teaching positions", 
            "Education", "₹45,000 - ₹65,000 per month", "15,000+",
            "[\"Post Graduate\"]", "[\"Teaching\", \"Arts\", \"Science\", \"Commerce\"]", null, null, 21, 40);
            
        createJob("Assistant Professor", "College and university teaching positions", 
            "Education", "₹57,700 - ₹1,82,400 per month", "5,000+",
            "[\"Post Graduate\", \"PhD\"]", "[\"Teaching\", \"Arts\", \"Science\", \"Commerce\", \"Engineering\"]", null, null, 21, 40);
            
        createJob("Anganwadi Worker", "Child care and development worker", 
            "Social Work", "₹8,000 - ₹15,000 per month", "50,000+",
            "[\"10th\", \"12th\"]", null, "[\"Female\"]", null, 18, 35);
            
        // Administrative Jobs
        createJob("Government Clerk (Multi-tasking)", "Clerical positions in various government departments", 
            "Administrative", "₹18,000 - ₹30,000 per month", "50,000+ (various departments)",
            "[\"12th\", \"Graduate\"]", null, null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 18, 28);
            
        createJob("Lower Division Clerk (LDC)", "Entry-level clerical positions", 
            "Administrative", "₹19,900 - ₹63,200 per month", "25,000+",
            "[\"12th\"]", null, null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 18, 27);
            
        createJob("Upper Division Clerk (UDC)", "Senior clerical positions", 
            "Administrative", "₹25,500 - ₹81,100 per month", "15,000+",
            "[\"Graduate\"]", null, null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 18, 30);
            
        createJob("Section Officer", "Middle management positions in government", 
            "Administrative", "₹44,900 - ₹1,42,400 per month", "5,000+",
            "[\"Graduate\", \"Post Graduate\"]", null, null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 21, 30);
            
        // Technical & Engineering Jobs
        createJob("Junior Engineer (Civil)", "Civil engineering positions in public works", 
            "Engineering", "₹40,000 - ₹60,000 per month", "8,000+",
            "[\"Diploma\", \"Graduate\"]", "[\"Engineering\"]", null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 18, 30);
            
        createJob("Junior Engineer (Electrical)", "Electrical engineering positions", 
            "Engineering", "₹40,000 - ₹60,000 per month", "6,000+",
            "[\"Diploma\", \"Graduate\"]", "[\"Engineering\"]", null, null, 18, 30);
            
        createJob("Junior Engineer (Mechanical)", "Mechanical engineering positions", 
            "Engineering", "₹40,000 - ₹60,000 per month", "5,000+",
            "[\"Diploma\", \"Graduate\"]", "[\"Engineering\"]", null, null, 18, 30);
            
        createJob("Assistant Engineer", "Senior technical positions in government", 
            "Engineering", "₹56,100 - ₹1,77,500 per month", "3,000+",
            "[\"Graduate\"]", "[\"Engineering\"]", null, null, 21, 35);
            
        createJob("Technical Assistant", "Technical support roles in laboratories", 
            "Technical", "₹25,500 - ₹81,100 per month", "4,000+",
            "[\"12th\", \"Diploma\"]", "[\"Science\", \"Engineering\"]", null, null, 18, 28);
            
        // Banking & Finance Jobs
        createJob("Bank Clerk (IBPS)", "Clerical positions in nationalized banks", 
            "Banking", "₹30,000 - ₹45,000 per month", "12,000+",
            "[\"Graduate\", \"Post Graduate\"]", "[\"Commerce\", \"Arts\", \"Science\"]", null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 20, 28);
            
        createJob("Bank Probationary Officer", "Officer positions in public sector banks", 
            "Banking", "₹50,000 - ₹75,000 per month", "8,000+",
            "[\"Graduate\", \"Post Graduate\"]", "[\"Commerce\", \"Management\"]", null, null, 20, 30);
            
        createJob("Insurance Agent (LIC)", "Life Insurance Corporation agent positions", 
            "Insurance", "₹15,000 - ₹40,000 per month (Commission based)", "20,000+",
            "[\"12th\", \"Graduate\"]", null, null, null, 18, 55);
            
        createJob("Accounts Officer", "Financial management positions", 
            "Finance", "₹44,900 - ₹1,42,400 per month", "3,000+",
            "[\"Graduate\", \"Post Graduate\"]", "[\"Commerce\"]", null, null, 21, 35);
            
        // Healthcare Jobs
        createJob("Staff Nurse", "Nursing positions in government hospitals", 
            "Healthcare", "₹25,500 - ₹81,100 per month", "15,000+",
            "[\"12th\", \"Diploma\"]", "[\"Medical\"]", "[\"Female\", \"Male\"]", null, 18, 30);
            
        createJob("Pharmacist", "Pharmacy positions in hospitals", 
            "Healthcare", "₹25,500 - ₹81,100 per month", "4,000+",
            "[\"Diploma\", \"Graduate\"]", "[\"Medical\"]", null, null, 18, 32);
            
        // Additional Jobs from original data
        createJob("Medical Officer", "Doctor positions in government hospitals", 
            "Healthcare", "₹56,100 - ₹1,77,500 per month", "8,000+",
            "[\"Graduate\", \"Post Graduate\"]", "[\"Medical\"]", null, null, 21, 35);
            
        createJob("Lab Technician", "Laboratory technician positions", 
            "Healthcare", "₹19,900 - ₹63,200 per month", "6,000+",
            "[\"12th\", \"Diploma\"]", "[\"Science\", \"Medical\"]", null, null, 18, 30);
            
        createJob("ASHA Worker", "Accredited Social Health Activist", 
            "Healthcare", "₹5,000 - ₹12,000 per month", "30,000+",
            "[\"10th\", \"12th\"]", null, "[\"Female\"]", null, 18, 45);
            
        createJob("Accounts Officer", "Financial management positions", 
            "Finance", "₹44,900 - ₹1,42,400 per month", "3,000+",
            "[\"Graduate\", \"Post Graduate\"]", "[\"Commerce\"]", null, null, 21, 35);
            
        createJob("Bank Clerk (IBPS)", "Clerical positions in nationalized banks", 
            "Banking", "₹30,000 - ₹45,000 per month", "12,000+",
            "[\"Graduate\", \"Post Graduate\"]", "[\"Commerce\", \"Arts\", \"Science\"]", null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 20, 28);
            
        createJob("Bank Probationary Officer", "Officer positions in public sector banks", 
            "Banking", "₹50,000 - ₹75,000 per month", "8,000+",
            "[\"Graduate\", \"Post Graduate\"]", "[\"Commerce\", \"Management\"]", null, null, 20, 30);
            
        createJob("Government Clerk (Multi-tasking)", "Clerical positions in various government departments", 
            "Administrative", "₹18,000 - ₹30,000 per month", "50,000+ (various departments)",
            "[\"12th\", \"Graduate\"]", null, null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 18, 28);
            
        createJob("Lower Division Clerk (LDC)", "Entry-level clerical positions", 
            "Administrative", "₹19,900 - ₹63,200 per month", "25,000+",
            "[\"12th\"]", null, null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 18, 27);
            
        createJob("Upper Division Clerk (UDC)", "Senior clerical positions", 
            "Administrative", "₹25,500 - ₹81,100 per month", "15,000+",
            "[\"Graduate\"]", null, null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 18, 30);
            
        createJob("Section Officer", "Middle management positions in government", 
            "Administrative", "₹44,900 - ₹1,42,400 per month", "5,000+",
            "[\"Graduate\", \"Post Graduate\"]", null, null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 21, 30);
            
        createJob("Junior Engineer (Civil)", "Civil engineering positions in public works", 
            "Engineering", "₹40,000 - ₹60,000 per month", "8,000+",
            "[\"Diploma\", \"Graduate\"]", "[\"Engineering\"]", null, "{\"OBC\": 3, \"SC\": 5, \"ST\": 5}", 18, 30);
            
        createJob("Junior Engineer (Electrical)", "Electrical engineering positions", 
            "Engineering", "₹40,000 - ₹60,000 per month", "6,000+",
            "[\"Diploma\", \"Graduate\"]", "[\"Engineering\"]", null, null, 18, 30);
            
        createJob("Junior Engineer (Mechanical)", "Mechanical engineering positions", 
            "Engineering", "₹40,000 - ₹60,000 per month", "5,000+",
            "[\"Diploma\", \"Graduate\"]", "[\"Engineering\"]", null, null, 18, 30);
            
        createJob("Assistant Engineer", "Senior technical positions in government", 
            "Engineering", "₹56,100 - ₹1,77,500 per month", "3,000+",
            "[\"Graduate\"]", "[\"Engineering\"]", null, null, 21, 35);
            
        createJob("Technical Assistant", "Technical support roles in laboratories", 
            "Technical", "₹25,500 - ₹81,100 per month", "4,000+",
            "[\"12th\", \"Diploma\"]", "[\"Science\", \"Engineering\"]", null, null, 18, 28);
    }
    
    private void createJob(String name, String description, String type, String salary, 
                          String vacancies, String qualificationCriteria, String fieldCriteria,
                          String genderCriteria, String ageRelaxationCriteria, Integer minAge, Integer maxAge) {
        GovernmentJob job = new GovernmentJob();
        job.setName(name);
        job.setDescription(description);
        job.setType(type);
        job.setSalary(salary);
        job.setVacancies(vacancies);
        job.setQualificationCriteria(qualificationCriteria);
        job.setFieldCriteria(fieldCriteria);
        job.setGenderCriteria(genderCriteria);
        job.setAgeRelaxationCriteria(ageRelaxationCriteria);
        job.setMinAge(minAge);
        job.setMaxAge(maxAge);
        job.setActive(true);
        
        jobRepository.save(job);
    }
}

