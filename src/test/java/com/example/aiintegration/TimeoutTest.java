package com.example.aiintegration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Steg 6 - Kantfall 1: Framtvinga Timeout
 *
 * Sätter read-timeout till 10 ms mot httpbin.org/delay/5 (svarar efter 5 sek).
 * Verifierar att ResourceAccessException kastas — d.v.s. timeout-skyddet
 * fungerar.
 *
 * Kör: ./gradlew test --tests TimeoutTest
 */
class TimeoutTest {

    @Test
    @DisabledIfEnvironmentVariable(named = "CI", matches = "true")
    void readTimeout_shouldThrowResourceAccessException() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000);
        factory.setReadTimeout(10); // 10 ms — framtvingar timeout

        RestClient client = RestClient.builder()
                .requestFactory(factory)
                .build();

        // httpbin.org/delay/5 väntar 5 sekunder innan svar — garanterad timeout
        assertThrows(ResourceAccessException.class, () -> client.get()
                .uri("https://httpbin.org/delay/5")
                .retrieve()
                .body(String.class));
    }
}
