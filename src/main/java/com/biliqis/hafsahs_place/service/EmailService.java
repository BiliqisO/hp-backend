package com.biliqis.hafsahs_place.service;

import com.biliqis.hafsahs_place.model.CustomOrder;
import com.biliqis.hafsahs_place.model.Order;
import com.biliqis.hafsahs_place.model.OrderItem;
import com.biliqis.hafsahs_place.model.User;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void sendWelcome(User user) {
        Context ctx = new Context();
        ctx.setVariable("firstName", user.getFirstName());
        ctx.setVariable("shopUrl", frontendUrl + "/shop");
        send(user.getEmail(), "Welcome to Hafsah's Place", "email/welcome", ctx);
    }

    public void sendOrderConfirmation(Order order) {
        List<Map<String, Object>> items = order.getOrderItems().stream()
                .map(item -> Map.<String, Object>of(
                        "productName", item.getProduct().getName(),
                        "quantity", item.getQuantity(),
                        "totalPrice", item.getTotalPrice()
                ))
                .collect(Collectors.toList());

        Context ctx = new Context();
        ctx.setVariable("firstName", order.getUser().getFirstName());
        ctx.setVariable("orderNumber", order.getOrderNumber());
        ctx.setVariable("shippingAddress", order.getShippingAddress());
        ctx.setVariable("shippingCity", order.getShippingCity());
        ctx.setVariable("shippingState", order.getShippingState());
        ctx.setVariable("totalAmount", order.getTotalAmount());
        ctx.setVariable("items", items);
        ctx.setVariable("frontendUrl", frontendUrl);

        send(order.getUser().getEmail(),
                "Order Confirmed — " + order.getOrderNumber(),
                "email/order-confirmation", ctx);
    }

    public void sendCustomOrderStatusUpdate(CustomOrder customOrder) {
        Context ctx = new Context();
        ctx.setVariable("firstName", customOrder.getUser().getFirstName());
        ctx.setVariable("orderNumber", customOrder.getOrderNumber());
        ctx.setVariable("status", customOrder.getStatus().name());
        ctx.setVariable("designDescription", customOrder.getDesignDescription());
        ctx.setVariable("estimatedPrice", customOrder.getEstimatedPrice());
        ctx.setVariable("finalPrice", customOrder.getFinalPrice());
        ctx.setVariable("frontendUrl", frontendUrl);

        send(customOrder.getUser().getEmail(),
                "Custom Order Update — " + customOrder.getOrderNumber(),
                "email/custom-order-status", ctx);
    }

    public void sendOrderShipped(Order order) {
        Context ctx = new Context();
        ctx.setVariable("firstName", order.getUser().getFirstName());
        ctx.setVariable("orderNumber", order.getOrderNumber());
        ctx.setVariable("trackingNumber", order.getTrackingNumber());
        ctx.setVariable("frontendUrl", frontendUrl);

        send(order.getUser().getEmail(),
                "Your Order Has Been Shipped — " + order.getOrderNumber(),
                "email/order-shipped", ctx);
    }

    public void sendPasswordReset(String toEmail, String firstName, String token) {
        String resetUrl = frontendUrl + "/reset-password?token=" + token;
        Context ctx = new Context();
        ctx.setVariable("firstName", firstName);
        ctx.setVariable("resetUrl", resetUrl);
        send(toEmail, "Reset Your Hafsah's Place Password", "email/password-reset", ctx);
    }

    private void send(String to, String subject, String template, Context ctx) {
        try {
            String body = templateEngine.process(template, ctx);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
            log.info("Email sent: to={} subject={}", to, subject);
        } catch (Exception e) {
            log.error("Failed to send email: to={} subject={} error={}", to, subject, e.getMessage());
        }
    }
}
