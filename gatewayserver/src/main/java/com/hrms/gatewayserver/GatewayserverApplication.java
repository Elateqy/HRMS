package com.hrms.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayserverApplication.class, args);
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("users", r -> r
                        .path("/api/users", "/api/users/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://userservice")
                )
                .route("leaves", r -> r
                        .path("/api/leaves", "/api/leaves/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://leaveservice")
                )
                .route("leave-balances", r -> r
                        .path("/api/leave-balances", "/api/leave-balances/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://leaveservice")
                )
                .route("trainings", r -> r
                        .path("/api/trainings", "/api/trainings/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://trainingservice")
                )
                .route("reports", r -> r
                        .path("/api/reports", "/api/reports/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://reportingservice")
                )
                .build();
    }
}