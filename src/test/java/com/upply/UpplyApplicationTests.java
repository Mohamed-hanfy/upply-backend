package com.upply;

import com.upply.application.ApplicationMatchConsumer;
import com.upply.application.dto.ApplicationMatchEvent;
import com.upply.profile.resume.AzureStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;

@SpringBootTest
@ActiveProfiles("test")
@ImportAutoConfiguration(exclude = KafkaAutoConfiguration.class)
class UpplyApplicationTests {

	@MockitoBean
	private VectorStore vectorStore;
	@MockitoBean
	private AzureStorageService azureStorageService;
	@MockitoBean
	private ApplicationMatchConsumer applicationMatchConsumer;
	@MockitoBean
	private KafkaTemplate<String, ApplicationMatchEvent> kafkaTemplate;
	@Test
	void contextLoads() {
	}

}
