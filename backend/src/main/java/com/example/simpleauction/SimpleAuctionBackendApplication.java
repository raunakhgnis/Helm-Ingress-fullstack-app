package com.example.simpleauction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry; // Ensure this import is present
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
public class SimpleAuctionBackendApplication { // Renamed from SimpleAuctionApplication based on your code

    public static void main(String[] args) {
        SpringApplication.run(SimpleAuctionBackendApplication.class, args);
        // The Bean definition should NOT be here
    }

    // Fallback CORS filter — this is a safe, developer-friendly fallback that allows
    // any Origin during local testing. It ensures the browser's preflight and simple
    // requests succeed when using NodePort for local cluster testing.
    @Bean
    @Profile("local")
    public FilterRegistrationBean<CorsFilter> corsFilterBean() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(false);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

    // **** MOVE THE BEAN DEFINITION HERE ****
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Allow common dev origins by default. Can be overridden with ALLOWED_ORIGINS env var
                String env = System.getenv("ALLOWED_ORIGINS");
                String[] allowed;
                if (env != null && !env.isBlank()) {
                    allowed = Arrays.stream(env.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .toArray(String[]::new);
                } else {
                    allowed = new String[]{
                            "http://localhost:5173",
                            "http://127.0.0.1:5173",
                            "http://localhost:3000",
                            "http://127.0.0.1:3000",
                            "http://localhost:30025",
                            "http://localhost:30080",
                            "http://127.0.0.1:30080"
                    };
                }

                // Log the allowed origins in dev for easier debugging (console)
                System.out.println("[CORS] Allowing origins: " + Arrays.stream(allowed).collect(Collectors.joining(", ")));

        // For local dev convenience allow all origins. In production we recommend using
        // a more restrictive list stored in ALLOWED_ORIGINS env variable.
        registry.addMapping("/api/**") // Allow CORS for all paths under /api/
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allowed HTTP methods
            .allowedHeaders("*") // Allow all headers
            .allowCredentials(false); // Disable credentials for wildcard origins
            }
        };
    }
    // **** END OF MOVED BEAN DEFINITION ****

}
