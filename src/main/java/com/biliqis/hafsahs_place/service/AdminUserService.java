package com.biliqis.hafsahs_place.service;

import com.biliqis.hafsahs_place.dto.AdminUserResponse;
import com.biliqis.hafsahs_place.exception.BadRequestException;
import com.biliqis.hafsahs_place.exception.ResourceNotFoundException;
import com.biliqis.hafsahs_place.model.Order;
import com.biliqis.hafsahs_place.model.User;
import com.biliqis.hafsahs_place.repository.OrderRepository;
import com.biliqis.hafsahs_place.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    public Page<AdminUserResponse> listUsers(String search, Pageable pageable) {
        String term = (search != null && !search.isBlank()) ? search : null;
        return userRepository.searchUsers(term, pageable)
                .map(AdminUserResponse::from);
    }

    public AdminUserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        AdminUserResponse response = AdminUserResponse.from(user);
        response.setOrderCount(orderRepository.countByUserId(userId));
        response.setTotalSpent(orderRepository.sumSpentByUser(userId, Order.OrderStatus.CANCELLED));
        return response;
    }

    @Transactional
    public AdminUserResponse deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!user.getIsActive()) {
            throw new BadRequestException("User is already deactivated");
        }

        // Prevent admins from deactivating themselves or other admins via this endpoint
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ROLE_ADMIN"));
        if (isAdmin) {
            throw new BadRequestException("Admin accounts cannot be deactivated via this endpoint");
        }

        user.setIsActive(false);
        return AdminUserResponse.from(userRepository.save(user));
    }

    @Transactional
    public AdminUserResponse reactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (user.getIsActive()) {
            throw new BadRequestException("User is already active");
        }

        user.setIsActive(true);
        return AdminUserResponse.from(userRepository.save(user));
    }
}
