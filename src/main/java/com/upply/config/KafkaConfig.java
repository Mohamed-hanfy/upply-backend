package com.upply.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    public static final String APPLICATION_MATCH_CALC_TOPIC = "application-match-calc";

    @Bean
    public NewTopic applicationMatchTopic(){
        return TopicBuilder.name(APPLICATION_MATCH_CALC_TOPIC)
                .partitions(2)
                .replicas(2)
                .build();
    }
}
