package com.upply.config;


import com.upply.application.dto.ApplicationMatchEvent;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@TestConfiguration
public class UpplyApplicationTests {
    @Bean
    public KafkaTemplate<String, ApplicationMatchEvent> kafkaTemplate() {
        return Mockito.mock(KafkaTemplate.class);
    }
}
