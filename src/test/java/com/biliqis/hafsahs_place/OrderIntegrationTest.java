package com.biliqis.hafsahs_place;

import com.biliqis.hafsahs_place.dto.AuthRequest;
import com.biliqis.hafsahs_place.dto.AuthResponse;
import com.biliqis.hafsahs_place.dto.OrderItemRequest;
import com.biliqis.hafsahs_place.dto.OrderRequest;
import com.biliqis.hafsahs_place.dto.OrderResponse;
import com.biliqis.hafsahs_place.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OrderIntegrationTest extends AbstractIntegrationTest {

    private String customerToken;

    /**
     * Register a fresh customer and log in before each test so tests
     * don't collide on the same email.
     */
    @BeforeEach
    void setUp() {
        String email = "customer+" + System.nanoTime() + "@test.com";

        RegisterRequest reg = new RegisterRequest(
                "Chiamaka", "Nwosu", email, "password123",
                "07055555555", "12 Victoria Island", "Lagos", "Lagos", "Nigeria");

        restTemplate.postForEntity("/api/auth/register", reg, AuthResponse.class);

        AuthRequest login = new AuthRequest(email, "password123");
        AuthResponse auth = restTemplate
                .postForEntity("/api/auth/login", login, AuthResponse.class)
                .getBody();

        customerToken = auth.getToken();
    }

    // ── Place order ──────────────────────────────────────────────────────────

    @Test
    void placeOrder_validRequest_returns201() {
        // V5 migration seeds product ID 1 (Royal Asoebi Ensemble)
        OrderItemRequest item = new OrderItemRequest(1L, null, 1, null, null);
        OrderRequest request = new OrderRequest(
                List.of(item),
                "15 Broad Street", "Lagos", "Lagos", "Nigeria", "08012345678", null, null);

        ResponseEntity<OrderResponse> response = restTemplate.exchange(
                "/api/orders",
                HttpMethod.POST,
                new HttpEntity<>(request, bearerHeaders(customerToken)),
                OrderResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getOrderNumber()).startsWith("HP-");
        assertThat(response.getBody().getStatus()).isEqualTo("PENDING");
        assertThat(response.getBody().getTotalAmount()).isPositive();
        assertThat(response.getBody().getItems()).hasSize(1);
    }

    @Test
    void placeOrder_withoutAuth_returns401() {
        OrderItemRequest item = new OrderItemRequest(1L, null, 1, null, null);
        OrderRequest request = new OrderRequest(
                List.of(item), "15 Broad Street", "Lagos", "Lagos", null, null, null, null);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/orders", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void placeOrder_missingShippingFields_returns400() {
        OrderItemRequest item = new OrderItemRequest(1L, null, 1, null, null);
        // shippingCity is blank — validation should fail
        OrderRequest invalid = new OrderRequest(
                List.of(item), "15 Broad Street", "", "", null, null, null, null);

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/orders",
                HttpMethod.POST,
                new HttpEntity<>(invalid, bearerHeaders(customerToken)),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // ── Get my orders ────────────────────────────────────────────────────────

    @Test
    void getMyOrders_returnsPagedResult() {
        // Place an order first
        OrderItemRequest item = new OrderItemRequest(1L, null, 2, null, null);
        OrderRequest request = new OrderRequest(
                List.of(item), "1 Test Ave", "Abuja", "FCT", null, null, null, null);
        restTemplate.exchange(
                "/api/orders", HttpMethod.POST,
                new HttpEntity<>(request, bearerHeaders(customerToken)), OrderResponse.class);

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/orders",
                HttpMethod.GET,
                new HttpEntity<>(bearerHeaders(customerToken)),
                Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("content");
        List<?> content = (List<?>) response.getBody().get("content");
        assertThat(content).isNotEmpty();
    }

    // ── Track order ──────────────────────────────────────────────────────────

    @Test
    void trackOrder_publicEndpoint_returnsOrder() {
        // Place an order
        OrderItemRequest item = new OrderItemRequest(1L, null, 1, null, null);
        OrderRequest request = new OrderRequest(
                List.of(item), "5 Marina Road", "Lagos", "Lagos", null, null, null, null);
        OrderResponse placed = restTemplate.exchange(
                "/api/orders", HttpMethod.POST,
                new HttpEntity<>(request, bearerHeaders(customerToken)),
                OrderResponse.class).getBody();

        // Track it without a token (public endpoint)
        ResponseEntity<OrderResponse> response = restTemplate.getForEntity(
                "/api/orders/track/" + placed.getOrderNumber(), OrderResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getOrderNumber()).isEqualTo(placed.getOrderNumber());
    }

    // ── Cancel order ─────────────────────────────────────────────────────────

    @Test
    void cancelOrder_ownOrder_returns200WithCancelledStatus() {
        OrderItemRequest item = new OrderItemRequest(1L, null, 1, null, null);
        OrderRequest request = new OrderRequest(
                List.of(item), "7 Lagos Road", "Lagos", "Lagos", null, null, null, null);
        OrderResponse placed = restTemplate.exchange(
                "/api/orders", HttpMethod.POST,
                new HttpEntity<>(request, bearerHeaders(customerToken)),
                OrderResponse.class).getBody();

        ResponseEntity<OrderResponse> cancel = restTemplate.exchange(
                "/api/orders/" + placed.getId() + "/cancel",
                HttpMethod.PATCH,
                new HttpEntity<>(bearerHeaders(customerToken)),
                OrderResponse.class);

        assertThat(cancel.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(cancel.getBody().getStatus()).isEqualTo("CANCELLED");
    }

    // ── Admin: update order status ───────────────────────────────────────────

    @Test
    void adminUpdateOrderStatus_asAdmin_returns200() {
        // Place an order as customer
        OrderItemRequest item = new OrderItemRequest(1L, null, 1, null, null);
        OrderRequest request = new OrderRequest(
                List.of(item), "10 Admin Test", "Lagos", "Lagos", null, null, null, null);
        OrderResponse placed = restTemplate.exchange(
                "/api/orders", HttpMethod.POST,
                new HttpEntity<>(request, bearerHeaders(customerToken)),
                OrderResponse.class).getBody();

        // Login as admin
        AuthRequest adminLogin = new AuthRequest("admin@hafsahsplace.com", "admin123456");
        String adminToken = restTemplate
                .postForEntity("/api/auth/login", adminLogin, AuthResponse.class)
                .getBody().getToken();

        // Update status to CONFIRMED
        ResponseEntity<OrderResponse> response = restTemplate.exchange(
                "/api/orders/admin/" + placed.getId() + "/status?status=CONFIRMED",
                HttpMethod.PATCH,
                new HttpEntity<>(bearerHeaders(adminToken)),
                OrderResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo("CONFIRMED");
    }
}
