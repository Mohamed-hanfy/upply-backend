package com.upply.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.clients.jedis.JedisPooled;

@Configuration
@Profile("!test")
public class VectorStoreConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.username:}")
    private String redisUsername;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Bean
    public JedisPooled jedisPooled() {
        if (redisUsername != null && !redisUsername.isBlank()) {
            return new JedisPooled(redisHost, redisPort, redisUsername, redisPassword);
        }
        return new JedisPooled(redisHost, redisPort);
    }

    @Bean
    public RedisVectorStore vectorStore(JedisPooled jedisPooled, EmbeddingModel embeddingModel) {
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
                .indexName("upply-vector-index")
                .prefix("upply:embedding:")
                .metadataFields(
                        RedisVectorStore.MetadataField.tag("jobId"),
                        RedisVectorStore.MetadataField.tag("title"),
                        RedisVectorStore.MetadataField.tag("type"),
                        RedisVectorStore.MetadataField.tag("seniority"),
                        RedisVectorStore.MetadataField.tag("model"),
                        RedisVectorStore.MetadataField.tag("location"),
                        RedisVectorStore.MetadataField.tag("status")
                )
                .initializeSchema(true)
                .build();
    }
}