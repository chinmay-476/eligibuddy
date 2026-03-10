package com.example.demo.init;

import com.example.demo.config.MongoSequenceService;
import com.example.demo.opportunity.CompetitiveExam;
import com.example.demo.opportunity.CompetitiveExamRepository;
import com.example.demo.opportunity.GovernmentJob;
import com.example.demo.opportunity.GovernmentJobRepository;
import com.example.demo.opportunity.GovernmentScheme;
import com.example.demo.opportunity.GovernmentSchemeRepository;
import com.example.demo.opportunity.Scholarship;
import com.example.demo.opportunity.ScholarshipRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class CompleteDataInitializer implements CommandLineRunner {

    private static final String SCHOLARSHIP_SEQUENCE = "scholarships_sequence";
    private static final String SCHEME_SEQUENCE = "government_schemes_sequence";
    private static final String EXAM_SEQUENCE = "competitive_exams_sequence";
    private static final String JOB_SEQUENCE = "government_jobs_sequence";
    private static final DateTimeFormatter SCHOLARSHIP_DEADLINE_FORMAT =
            DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);

    private final ScholarshipRepository scholarshipRepository;
    private final GovernmentSchemeRepository schemeRepository;
    private final CompetitiveExamRepository examRepository;
    private final GovernmentJobRepository jobRepository;
    private final MongoSequenceService mongoSequenceService;
    private final ObjectMapper objectMapper;
    private final Resource eligibilityDataResource;

    public CompleteDataInitializer(
            ScholarshipRepository scholarshipRepository,
            GovernmentSchemeRepository schemeRepository,
            CompetitiveExamRepository examRepository,
            GovernmentJobRepository jobRepository,
            MongoSequenceService mongoSequenceService,
            ObjectMapper objectMapper,
            @Value("classpath:static/data/eligibility-data.json") Resource eligibilityDataResource
    ) {
        this.scholarshipRepository = scholarshipRepository;
        this.schemeRepository = schemeRepository;
        this.examRepository = examRepository;
        this.jobRepository = jobRepository;
        this.mongoSequenceService = mongoSequenceService;
        this.objectMapper = objectMapper;
        this.eligibilityDataResource = eligibilityDataResource;
    }

    @Override
    public void run(String... args) throws Exception {
        boolean seeded = false;
        boolean requiresSeedData = scholarshipRepository.count() == 0
                || schemeRepository.count() == 0
                || examRepository.count() == 0
                || jobRepository.count() == 0;

        if (!requiresSeedData) {
            return;
        }

        JsonNode root = loadSeedData();

        if (scholarshipRepository.count() == 0) {
            scholarshipRepository.saveAll(buildScholarships(root.path("scholarships")));
            seeded = true;
        }
        if (schemeRepository.count() == 0) {
            schemeRepository.saveAll(buildSchemes(root.path("schemes")));
            seeded = true;
        }
        if (examRepository.count() == 0) {
            examRepository.saveAll(buildExams(root.path("exams")));
            seeded = true;
        }
        if (jobRepository.count() == 0) {
            jobRepository.saveAll(buildJobs(root.path("jobs")));
            seeded = true;
        }

        if (seeded) {
            System.out.println("MongoDB opportunity collections initialized from eligibility-data.json.");
        }
    }

    @NonNull
    private JsonNode loadSeedData() throws IOException {
        try (InputStream inputStream = eligibilityDataResource.getInputStream()) {
            return objectMapper.readTree(inputStream);
        }
    }

    @NonNull
    private List<Scholarship> buildScholarships(@NonNull JsonNode nodes) {
        List<Scholarship> scholarships = new ArrayList<>();
        if (!nodes.isArray()) {
            return scholarships;
        }

        for (JsonNode node : nodes) {
            JsonNode eligibility = node.path("eligibility");
            Scholarship scholarship = new Scholarship();
            scholarship.setId(mongoSequenceService.generateSequence(SCHOLARSHIP_SEQUENCE));
            scholarship.setName(readText(node, "name"));
            scholarship.setDescription(readText(node, "description"));
            scholarship.setType(readText(node, "type"));
            scholarship.setAmount(readText(node, "amount"));
            scholarship.setDeadline(parseLocalDate(readText(node, "deadline")));
            scholarship.setQualificationCriteria(writeJson(eligibility.path("qualification")));
            scholarship.setIncomeCriteria(writeJson(eligibility.path("income")));
            scholarship.setCategoryCriteria(writeJson(eligibility.path("category")));
            scholarship.setFieldCriteria(writeJson(eligibility.path("field")));
            scholarship.setGenderCriteria(writeJson(eligibility.path("gender")));
            scholarship.setStateCriteria(writeJson(eligibility.path("state")));
            scholarship.setMinAge(readAgeBound(eligibility.path("age"), 0));
            scholarship.setMaxAge(readAgeBound(eligibility.path("age"), 1));
            scholarship.setActive(true);
            scholarship.setCreatedAt(LocalDate.now());
            scholarships.add(scholarship);
        }

        return scholarships;
    }

    @NonNull
    private List<GovernmentScheme> buildSchemes(@NonNull JsonNode nodes) {
        List<GovernmentScheme> schemes = new ArrayList<>();
        if (!nodes.isArray()) {
            return schemes;
        }

        for (JsonNode node : nodes) {
            JsonNode eligibility = node.path("eligibility");
            GovernmentScheme scheme = new GovernmentScheme();
            scheme.setId(mongoSequenceService.generateSequence(SCHEME_SEQUENCE));
            scheme.setName(readText(node, "name"));
            scheme.setDescription(readText(node, "description"));
            scheme.setType(readText(node, "type"));
            scheme.setBenefit(readText(node, "benefit"));
            scheme.setDeadline(readText(node, "deadline"));
            scheme.setQualificationCriteria(writeJson(eligibility.path("qualification")));
            scheme.setIncomeCriteria(writeJson(eligibility.path("income")));
            scheme.setCategoryCriteria(writeJson(eligibility.path("category")));
            scheme.setFieldCriteria(writeJson(eligibility.path("field")));
            scheme.setGenderCriteria(writeJson(eligibility.path("gender")));
            scheme.setStateCriteria(writeJson(eligibility.path("state")));
            scheme.setDisabilityCriteria(writeJson(eligibility.path("disability")));
            scheme.setMinAge(readAgeBound(eligibility.path("age"), 0));
            scheme.setMaxAge(readAgeBound(eligibility.path("age"), 1));
            scheme.setActive(true);
            scheme.setCreatedAt(LocalDate.now());
            schemes.add(scheme);
        }

        return schemes;
    }

    @NonNull
    private List<CompetitiveExam> buildExams(@NonNull JsonNode nodes) {
        List<CompetitiveExam> exams = new ArrayList<>();
        if (!nodes.isArray()) {
            return exams;
        }

        for (JsonNode node : nodes) {
            JsonNode eligibility = node.path("eligibility");
            CompetitiveExam exam = new CompetitiveExam();
            exam.setId(mongoSequenceService.generateSequence(EXAM_SEQUENCE));
            exam.setName(readText(node, "name"));
            exam.setDescription(readText(node, "description"));
            exam.setType(readText(node, "type"));
            exam.setExamDate(readText(node, "examDate"));
            exam.setApplicationFee(readText(node, "applicationFee"));
            exam.setQualificationCriteria(writeJson(eligibility.path("qualification")));
            exam.setFieldCriteria(writeJson(eligibility.path("field")));
            exam.setGenderCriteria(writeJson(eligibility.path("gender")));
            exam.setAgeRelaxationCriteria(writeJson(eligibility.path("ageRelaxation")));
            exam.setMinAge(readAgeBound(eligibility.path("age"), 0));
            exam.setMaxAge(readAgeBound(eligibility.path("age"), 1));
            exam.setActive(true);
            exam.setCreatedAt(LocalDate.now());
            exams.add(exam);
        }

        return exams;
    }

    @NonNull
    private List<GovernmentJob> buildJobs(@NonNull JsonNode nodes) {
        List<GovernmentJob> jobs = new ArrayList<>();
        if (!nodes.isArray()) {
            return jobs;
        }

        for (JsonNode node : nodes) {
            JsonNode eligibility = node.path("eligibility");
            GovernmentJob job = new GovernmentJob();
            job.setId(mongoSequenceService.generateSequence(JOB_SEQUENCE));
            job.setName(readText(node, "name"));
            job.setDescription(readText(node, "description"));
            job.setType(readText(node, "type"));
            job.setSalary(readText(node, "salary"));
            job.setVacancies(readText(node, "vacancies"));
            job.setApplicationDeadline(readText(node, "applicationDeadline"));
            job.setQualificationCriteria(writeJson(eligibility.path("qualification")));
            job.setFieldCriteria(writeJson(eligibility.path("field")));
            job.setGenderCriteria(writeJson(eligibility.path("gender")));
            job.setAgeRelaxationCriteria(writeJson(eligibility.path("ageRelaxation")));
            job.setMinAge(readAgeBound(eligibility.path("age"), 0));
            job.setMaxAge(readAgeBound(eligibility.path("age"), 1));
            job.setActive(true);
            job.setCreatedAt(LocalDate.now());
            jobs.add(job);
        }

        return jobs;
    }

    @Nullable
    private String readText(@Nullable JsonNode node, String fieldName) {
        if (node == null || node.path(fieldName).isMissingNode() || node.path(fieldName).isNull()) {
            return null;
        }
        String value = node.path(fieldName).asText().trim();
        return value.isEmpty() ? null : value;
    }

    @Nullable
    private Integer readAgeBound(JsonNode ageNode, int index) {
        if (!ageNode.isArray() || ageNode.size() <= index || ageNode.get(index).isNull()) {
            return null;
        }
        return ageNode.get(index).asInt();
    }

    @Nullable
    private String writeJson(@Nullable JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        if (node.isArray() && node.size() == 0) {
            return null;
        }
        if (node.isObject() && node.size() == 0) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(node);
        } catch (IOException ex) {
            return null;
        }
    }

    @Nullable
    private LocalDate parseLocalDate(@Nullable String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value, SCHOLARSHIP_DEADLINE_FORMAT);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }
}
