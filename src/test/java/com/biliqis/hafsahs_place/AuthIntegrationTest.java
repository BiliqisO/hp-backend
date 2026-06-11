package com.biliqis.hafsahs_place;

import com.biliqis.hafsahs_place.dto.AuthRequest;
import com.biliqis.hafsahs_place.dto.AuthResponse;
import com.biliqis.hafsahs_place.dto.ForgotPasswordRequest;
import com.biliqis.hafsahs_place.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class AuthIntegrationTest extends AbstractIntegrationTest {

    // ── Registration ─────────────────────────────────────────────────────────

    @Test
    void register_success_returns201WithToken() {
        RegisterRequest request = new RegisterRequest(
                "Amaka", "Okafor", "amaka@test.com", "password123",
                "08012345678", "5 Broad St", "Lagos", "Lagos", "Nigeria");

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                "/api/auth/register", request, AuthResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNotBlank();
        assertThat(response.getBody().getEmail()).isEqualTo("amaka@test.com");
    }

    @Test
    void register_duplicateEmail_returns400() {
        RegisterRequest request = new RegisterRequest(
                "Fatima", "Bello", "fatima.dup@test.com", "password123",
                null, null, null, null, null);

        restTemplate.postForEntity("/api/auth/register", request, AuthResponse.class);

        // Second registration with the same email
        ResponseEntity<String> duplicate = restTemplate.postForEntity(
                "/api/auth/register", request, String.class);

        assertThat(duplicate.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void register_missingRequiredFields_returns400() {
        RegisterRequest invalid = new RegisterRequest(
                null, null, "bad", "pw", null, null, null, null, null);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/register", invalid, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // ── Login ────────────────────────────────────────────────────────────────

    @Test
    void login_validCredentials_returns200WithToken() {
        // Admin is seeded by V3 migration
        AuthRequest request = new AuthRequest("admin@hafsahsplace.com", "admin123456");

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                "/api/auth/login", request, AuthResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNotBlank();
        assertThat(response.getBody().getEmail()).isEqualTo("admin@hafsahsplace.com");
    }

    @Test
    void login_wrongPassword_returns401() {
        AuthRequest request = new AuthRequest("admin@hafsahsplace.com", "wrongpassword");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void login_unknownEmail_returns401() {
        AuthRequest request = new AuthRequest("nobody@test.com", "password123");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    // ── Forgot password ──────────────────────────────────────────────────────

    @Test
    void forgotPassword_existingEmail_returns200() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("admin@hafsahsplace.com");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/forgot-password", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void forgotPassword_nonExistentEmail_alsoReturns200() {
        // Must not reveal whether an account exists
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("ghost@test.com");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/auth/forgot-password", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    // ── Security: public vs protected endpoints ──────────────────────────────

    @Test
    void accessProtectedEndpoint_withoutToken_returns401() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/orders", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void accessPublicProductEndpoint_withoutToken_returns200() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/products", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void adminEndpoint_withCustomerToken_returns403() {
        // Register and login as a regular customer
        RegisterRequest reg = new RegisterRequest(
                "Ngozi", "Adeyemi", "ngozi.sec@test.com", "password123",
                null, null, null, null, null);
        restTemplate.postForEntity("/api/auth/register", reg, AuthResponse.class);

        AuthRequest login = new AuthRequest("ngozi.sec@test.com", "password123");
        AuthResponse auth = restTemplate
                .postForEntity("/api/auth/login", login, AuthResponse.class)
                .getBody();

        // Try to access admin-only dashboard
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/admin/dashboard",
                HttpMethod.GET,
                new HttpEntity<>(bearerHeaders(auth.getToken())),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void adminDashboard_withAdminToken_returns200() {
        AuthRequest login = new AuthRequest("admin@hafsahsplace.com", "admin123456");
        AuthResponse auth = restTemplate
                .postForEntity("/api/auth/login", login, AuthResponse.class)
                .getBody();

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/admin/dashboard",
                HttpMethod.GET,
                new HttpEntity<>(bearerHeaders(auth.getToken())),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
