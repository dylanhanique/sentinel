package fr.sentinel.apiservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
public class ApiServiceActuatorTests {

    @Autowired
    private RestTestClient restTestClient;

    @Test
    void health_endpoint_is_public() {
        restTestClient.get()
            .uri("/actuator/health")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo("UP");
    }
    @Test
    void liveness_endpoint_is_public() {
        restTestClient.get()
            .uri("/actuator/health/liveness")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.status").isEqualTo("UP");
    }

    @Test
    void protected_endpoint_is_unauthorized() {
        restTestClient.get()
            .uri("/api/monitors")
            .exchange()
            .expectStatus().isUnauthorized();
    }
}
