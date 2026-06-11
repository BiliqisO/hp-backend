package com.biliqis.hafsahs_place.controller;

import com.biliqis.hafsahs_place.dto.AdminUserResponse;
import com.biliqis.hafsahs_place.service.AdminUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin — Users", description = "View and manage customer accounts")
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<Page<AdminUserResponse>> listUsers(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(adminUserService.listUsers(search, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminUserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.getUserById(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<AdminUserResponse> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.deactivateUser(id));
    }

    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<AdminUserResponse> reactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.reactivateUser(id));
    }
}
