package com.example.aiintegration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = { "openai.api.key=test-dummy-key" })
class AiintegrationApplicationTests {
	@Test
	void contextLoads() {
	}
}
