package com.upply.application;

import com.upply.application.dto.ApplicationMatchEvent;
import com.upply.exception.custom.ResourceNotFoundException;
import com.upply.job.Job;
import com.upply.job.JobMatchingService;
import com.upply.job.JobRepository;
import com.upply.user.User;
import com.upply.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.upply.config.KafkaConfig.APPLICATION_MATCH_CALC_TOPIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationMatchConsumer {
    private final JobMatchingService jobMatchingService;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    @KafkaListener(
            topics = APPLICATION_MATCH_CALC_TOPIC,
            groupId = "application-matching-group"
    )
    public void handleMatchCalc(ApplicationMatchEvent event) {
        log.info("Received match event for applicationId: {}", event.getApplicationId());
        try {
            Application application = applicationRepository.findById(event.getApplicationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + event.getApplicationId()));

            User user = userRepository.findById(event.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("user not found: " + event.getUserId()));

            Job job = jobRepository.findById(event.getJobId())
                    .orElseThrow(() -> new RuntimeException("Job not found: " + event.getJobId()));

            double score = jobMatchingService.calculateMatchScore(user, job);

            application.setMatchingRatio(score);
            applicationRepository.save(application);

            log.info("Saved matchingRatio: {} for applicationId: {}", score, event.getApplicationId());
        } catch (Exception e) {
            log.error("Failed to process match for applicationId: {}", event.getApplicationId(), e);
        }
    }
}
