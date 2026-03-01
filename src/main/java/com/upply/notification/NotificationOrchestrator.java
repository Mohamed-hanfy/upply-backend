package com.upply.notification;

import com.upply.email.EmailTemplate;
import com.upply.exception.custom.ResourceNotFoundException;
import com.upply.notification.dto.DispatchPayload;
import com.upply.notification.dto.NotificationEvent;
import com.upply.user.User;
import com.upply.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.upply.config.KafkaConfig.NOTIFICATION_DISPATCH;
import static com.upply.config.KafkaConfig.NOTIFICATION_EVENTS;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationOrchestrator {
    private final KafkaTemplate<String, DispatchPayload> kafkaTemplate;
    private final UserRepository userRepository;

    @KafkaListener(topics = NOTIFICATION_EVENTS, groupId = "orchestrator-group")
    public void handle(NotificationEvent event) {

        User user = userRepository.findById(event.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("user not found: " + event.getUserId()));

        for (DispatchPayload.Channel channel : event.getChannels()) {
            DispatchPayload payload = resolveTemplate(event, channel, user);

            if (payload == null) {
                log.warn("No template resolved for eventType: {} channel: {}", event.getEventType(), channel);
                continue;
            }

            if (channel == DispatchPayload.Channel.PUSH && user.getDeviceToken() == null) {
                log.warn("Skipping PUSH for userId: {} — no device token", event.getUserId());
                continue;
            }

            kafkaTemplate.send(NOTIFICATION_DISPATCH, String.valueOf(event.getUserId()), payload);
            log.info("Dispatched {} notification for userId: {}", channel, event.getUserId());
        }
    }

    private DispatchPayload resolveTemplate(NotificationEvent event,
                                            DispatchPayload.Channel channel,
                                            User user) {
        Map<String, Object> p = event.getPayload();

        return switch (event.getEventType()) {

            case "JOB_APPLICATION_UPDATED" -> channel == DispatchPayload.Channel.EMAIL
                    ? DispatchPayload.builder()
                    .channel(DispatchPayload.Channel.EMAIL)
                    .to(user.getEmail())
                    .subject("Update on your application at " + p.get("company"))
                    .template(EmailTemplate.JOB_APPLICATION_UPDATED)
                    .templateVariables(Map.of(
                            "firstName", user.getFirstName(),
                            "jobTitle", p.get("jobTitle"),
                            "company", p.get("company"),
                            "status", p.get("status"),
                            "applicationUrl", "https://upply.com/my-applications"
                    ))
                    .build()
                    : DispatchPayload.builder()
                    .channel(DispatchPayload.Channel.PUSH)
                    .to(user.getDeviceToken())
                    .title("Application Update 🎉")
                    .body(p.get("company") + " updated your application to '" + p.get("status") + "'")
                    .redirectTo("/my-applications")
                    .eventType(event.getEventType())
                    .build();

            case "NEW_MATCHED_JOBS" -> channel == DispatchPayload.Channel.EMAIL
                    ? DispatchPayload.builder()
                    .channel(DispatchPayload.Channel.EMAIL)
                    .to(user.getEmail())
                    .subject("We found " + p.get("matchedCount") + " new jobs for you!")
                    .template(EmailTemplate.NEW_MATCHED_JOBS)
                    .templateVariables(Map.of(
                            "firstName", user.getFirstName(),
                            "matchedCount", p.get("matchedCount"),
                            "matchedJobs", p.get("matchedJobs"),
                            "jobsUrl", "https://upply.com/jobs/matched"
                    ))
                    .build()
                    : DispatchPayload.builder()
                    .channel(DispatchPayload.Channel.PUSH)
                    .to(user.getDeviceToken())
                    .title("New Job Matches 🎯")
                    .body("We found " + p.get("matchedCount") + " new jobs matching your profile!")
                    .redirectTo("/jobs/matched")
                    .eventType(event.getEventType())
                    .build();

            case "JOB_APPLICATION_SUBMITTED" -> channel == DispatchPayload.Channel.EMAIL
                    ? DispatchPayload.builder()
                    .channel(DispatchPayload.Channel.EMAIL)
                    .to(user.getEmail())
                    .subject("Your application to " + p.get("company") + " has been submitted!")
                    .template(EmailTemplate.JOB_APPLICATION_SUBMITTED)
                    .templateVariables(Map.of(
                            "firstName", user.getFirstName(),
                            "jobTitle",  p.get("jobTitle"),
                            "company",   p.get("company"),
                            "status",    p.get("status")
                    ))
                    .build()
                    : DispatchPayload.builder()
                    .channel(DispatchPayload.Channel.PUSH)
                    .to(user.getDeviceToken())
                    .title("Application Submitted ✅")
                    .body("Your application for " + p.get("jobTitle") + " at " + p.get("company") + " has been submitted!")
                    .redirectTo("/my-applications")
                    .eventType("JOB_APPLICATION_SUBMITTED")
                    .build();
            default -> {
                log.warn("Unknown eventType: {}", event.getEventType());
                yield null;
            }
        };
    }
}
