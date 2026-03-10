package com.example.demo.ai;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class AssistantFallbackService {

    public String generateResponse(String username, String message, List<ChatTurn> history, Map<String, Object> context) {
        Map<String, Object> profile = asMap(context.get("profile"));
        Map<String, Object> results = asMap(context.get("results"));
        String topic = resolveTopic(message, history);
        String profileSummary = buildProfileSummary(profile);
        String resultSummary = buildResultSummary(results);
        String opening = "I could not reach the live assistant just now, so I am continuing with your current Eligibuddy context.";

        return switch (topic) {
            case "scholarships" -> buildScholarshipResponse(opening, profileSummary, resultSummary, results);
            case "schemes" -> buildSchemeResponse(opening, profileSummary, resultSummary, results);
            case "exams" -> buildExamResponse(opening, profileSummary, resultSummary, results);
            case "jobs" -> buildJobResponse(opening, profileSummary, resultSummary, results);
            case "documents" -> buildDocumentResponse(opening, profileSummary, resultSummary, history);
            case "profile" -> buildProfileResponse(opening, profileSummary);
            case "results" -> buildResultsResponse(opening, resultSummary, results);
            default -> buildGeneralResponse(opening, username, profileSummary, resultSummary);
        };
    }

    private String buildScholarshipResponse(String opening, String profileSummary, String resultSummary, Map<String, Object> results) {
        StringBuilder response = new StringBuilder(opening);
        if (!profileSummary.isBlank()) {
            response.append("\n\nFrom your current profile, keep your qualification, income, category, and field details accurate before comparing scholarship matches.");
        }
        response.append("\n\nFor scholarships, do this next:");
        response.append("\n1. Open the Scholarships filter and shortlist the options with the nearest deadlines.");
        response.append("\n2. Check the amount, type, and eligibility details inside each result card before you apply.");
        response.append("\n3. Keep your marksheets, ID proof, income certificate, category certificate, and bank details ready if they apply.");
        response.append("\n4. If your scholarship count is low, add your field of interest in Advanced Options and run the analysis again.");
        appendResultsSnapshot(response, resultSummary, results, "scholarships");
        return response.toString();
    }

    private String buildSchemeResponse(String opening, String profileSummary, String resultSummary, Map<String, Object> results) {
        StringBuilder response = new StringBuilder(opening);
        if (!profileSummary.isBlank()) {
            response.append("\n\nUse your current profile details to focus on schemes that match your age, income, category, and state.");
        }
        response.append("\n\nFor government schemes, do this next:");
        response.append("\n1. Open the Schemes filter and compare benefits first, not just the scheme title.");
        response.append("\n2. Check whether the scheme is ongoing or deadline-based before planning your application.");
        response.append("\n3. Keep income proof, ID proof, domicile details, and any category or disability certificates ready.");
        response.append("\n4. If a scheme looks close but not exact, update your profile details and run the analysis again to confirm.");
        appendResultsSnapshot(response, resultSummary, results, "schemes");
        return response.toString();
    }

    private String buildExamResponse(String opening, String profileSummary, String resultSummary, Map<String, Object> results) {
        StringBuilder response = new StringBuilder(opening);
        if (!profileSummary.isBlank()) {
            response.append("\n\nYour current qualification, age, category, and field details are the main inputs for exam matching.");
        }
        response.append("\n\nFor exams, do this next:");
        response.append("\n1. Open the Exams filter and check age limits and exam dates first.");
        response.append("\n2. Compare the application fee and field requirements before shortlisting anything.");
        response.append("\n3. Keep your education certificates, ID proof, and category documents ready for registration.");
        response.append("\n4. If you want more exam matches, widen your field selection and keep your profile details complete.");
        appendResultsSnapshot(response, resultSummary, results, "exams");
        return response.toString();
    }

    private String buildJobResponse(String opening, String profileSummary, String resultSummary, Map<String, Object> results) {
        StringBuilder response = new StringBuilder(opening);
        if (!profileSummary.isBlank()) {
            response.append("\n\nYour age, qualification, category, and field are the strongest filters for job matches.");
        }
        response.append("\n\nFor jobs, do this next:");
        response.append("\n1. Open the Jobs filter and compare the salary range, vacancy count, and qualification requirement.");
        response.append("\n2. Check whether the role matches your age band and your education level before applying.");
        response.append("\n3. Keep your resume, education certificates, ID proof, and category documents ready.");
        response.append("\n4. If you want more job options, leave multiple preferences enabled and rerun the analysis.");
        appendResultsSnapshot(response, resultSummary, results, "jobs");
        return response.toString();
    }

    private String buildDocumentResponse(String opening, String profileSummary, String resultSummary, List<ChatTurn> history) {
        String documentFocus = resolveDocumentFocus(history);
        StringBuilder response = new StringBuilder(opening);
        if (!profileSummary.isBlank()) {
            response.append("\n\nI am using your current profile details to suggest the core documents you are most likely to need.");
        }
        response.append("\n\nThe usual set is:");
        response.append("\n1. Government ID proof and a recent photo.");
        response.append("\n2. Latest marksheets or qualification certificates.");
        response.append("\n3. Income certificate, category certificate, domicile certificate, or disability certificate if those apply to you.");
        response.append("\n4. Bank details and contact details that match your profile.");

        if (!documentFocus.isBlank()) {
            response.append("\n\nFor ").append(documentFocus).append(" specifically:");
            if ("scholarships".equals(documentFocus)) {
                response.append("\n- Keep academic records and fee or admission proof ready.");
            } else if ("jobs".equals(documentFocus)) {
                response.append("\n- Keep your resume, qualification proofs, and any required experience records ready.");
            } else if ("exams".equals(documentFocus)) {
                response.append("\n- Keep your education proof, date of birth proof, and category documents ready before registration.");
            } else if ("schemes".equals(documentFocus)) {
                response.append("\n- Keep income proof, address proof, and supporting welfare documents ready.");
            }
        }

        if (!resultSummary.isBlank()) {
            response.append("\n\nCurrent results snapshot: ").append(resultSummary);
        }
        return response.toString();
    }

    private String buildProfileResponse(String opening, String profileSummary) {
        StringBuilder response = new StringBuilder(opening);
        if (!profileSummary.isBlank()) {
            response.append("\n\nI can already see some profile context: ").append(profileSummary).append(".");
        }
        response.append("\n\nBefore you run or rerun the analysis, make sure these fields are complete:");
        response.append("\n1. Age, state, qualification, category, and income.");
        response.append("\n2. Field of interest if you want more targeted matches.");
        response.append("\n3. Disability status if it applies to you.");
        response.append("\n4. Keep all relevant preferences checked if you want the widest result set.");
        return response.toString();
    }

    private String buildResultsResponse(String opening, String resultSummary, Map<String, Object> results) {
        StringBuilder response = new StringBuilder(opening);
        if (!resultSummary.isBlank()) {
            response.append("\n\nHere is your current snapshot: ").append(resultSummary).append(".");
        }

        String strongestArea = strongestCategory(results);
        if (!strongestArea.isBlank()) {
            response.append("\n\nYour strongest result area right now is ").append(strongestArea).append(".");
        }

        response.append("\n\nBest next steps:");
        response.append("\n1. Start with the category that has the strongest count or the most urgent deadline.");
        response.append("\n2. Use View Details to compare requirements before applying anywhere.");
        response.append("\n3. If a category looks weak, update your form details and rerun the analysis.");
        response.append("\n4. Ask me about one category at a time if you want help prioritizing.");
        return response.toString();
    }

    private String buildGeneralResponse(String opening, String username, String profileSummary, String resultSummary) {
        StringBuilder response = new StringBuilder(opening);
        response.append("\n\nI can still help you move forward, ").append(username).append(".");

        if (!profileSummary.isBlank()) {
            response.append("\nYour current profile snapshot: ").append(profileSummary).append(".");
        }

        if (!resultSummary.isBlank()) {
            response.append("\nYour current results snapshot: ").append(resultSummary).append(".");
        }

        response.append("\n\nYou can ask me things like:");
        response.append("\n- Which scholarships or schemes should I open first?");
        response.append("\n- What details should I fix before rerunning the analysis?");
        response.append("\n- What documents do I need for my current matches?");
        response.append("\n- What should I do next with my results?");
        return response.toString();
    }

    private void appendResultsSnapshot(StringBuilder response, String resultSummary, Map<String, Object> results, String category) {
        if (!resultSummary.isBlank()) {
            response.append("\n\nCurrent results snapshot: ").append(resultSummary).append(".");
        }

        int categoryCount = asInt(results.get(category));
        if (categoryCount == 0) {
            response.append("\n\nYou currently have no direct matches in this category, so update your form details and keep multiple preferences selected before trying again.");
        }
    }

    private String resolveTopic(String message, List<ChatTurn> history) {
        String detected = detectTopic(message);
        if (detected != null) {
            return detected;
        }

        for (int i = history.size() - 1; i >= 0; i--) {
            ChatTurn turn = history.get(i);
            if ("user".equals(turn.role())) {
                detected = detectTopic(turn.content());
                if (detected != null) {
                    return detected;
                }
            }
        }

        return "general";
    }

    private String resolveDocumentFocus(List<ChatTurn> history) {
        for (int i = history.size() - 1; i >= 0; i--) {
            ChatTurn turn = history.get(i);
            if ("user".equals(turn.role())) {
                String topic = detectTopic(turn.content());
                if (topic != null && !"documents".equals(topic) && !"profile".equals(topic) && !"results".equals(topic)) {
                    return topic;
                }
            }
        }
        return "";
    }

    @Nullable
    private String detectTopic(@Nullable String message) {
        String text = message == null ? "" : message.toLowerCase(Locale.ENGLISH);
        if (containsAny(text, "scholarship", "scholarships", "grant", "grants", "fellowship")) {
            return "scholarships";
        }
        if (containsAny(text, "scheme", "schemes", "benefit", "benefits", "yojana", "subsidy")) {
            return "schemes";
        }
        if (containsAny(text, "exam", "exams", "test", "tests", "neet", "gate", "upsc", "ssc")) {
            return "exams";
        }
        if (containsAny(text, "job", "jobs", "vacancy", "vacancies", "recruitment", "employment")) {
            return "jobs";
        }
        if (containsAny(text, "document", "documents", "certificate", "certificates", "proof", "papers")) {
            return "documents";
        }
        if (containsAny(text, "profile", "form", "fill", "details", "update")) {
            return "profile";
        }
        if (containsAny(text, "result", "results", "match", "matches", "next", "what now", "what should i do")) {
            return "results";
        }
        return null;
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String buildProfileSummary(Map<String, Object> profile) {
        List<String> parts = new ArrayList<>();
        addSummaryPart(parts, profile.get("age"), "age " + asText(profile.get("age")));
        addSummaryPart(parts, profile.get("state"), "state " + asText(profile.get("state")));
        addSummaryPart(parts, profile.get("qualification"), "qualification " + asText(profile.get("qualification")));
        addSummaryPart(parts, profile.get("category"), "category " + asText(profile.get("category")));
        addSummaryPart(parts, profile.get("income"), "income " + asText(profile.get("income")));
        addSummaryPart(parts, profile.get("field"), "field " + asText(profile.get("field")));
        return String.join(", ", parts);
    }

    private String buildResultSummary(Map<String, Object> results) {
        if (results.isEmpty()) {
            return "";
        }

        return "scholarships=" + asInt(results.get("scholarships"))
            + ", schemes=" + asInt(results.get("schemes"))
            + ", exams=" + asInt(results.get("exams"))
            + ", jobs=" + asInt(results.get("jobs"));
    }

    private String strongestCategory(Map<String, Object> results) {
        String bestCategory = "";
        int bestCount = -1;
        for (String category : List.of("scholarships", "schemes", "exams", "jobs")) {
            int count = asInt(results.get(category));
            if (count > bestCount) {
                bestCount = count;
                bestCategory = category;
            }
        }
        return bestCount > 0 ? bestCategory : "";
    }

    private void addSummaryPart(List<String> parts, Object value, String label) {
        if (!asText(value).isBlank()) {
            parts.add(label);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }

    private int asInt(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(asText(value));
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private String asText(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}
