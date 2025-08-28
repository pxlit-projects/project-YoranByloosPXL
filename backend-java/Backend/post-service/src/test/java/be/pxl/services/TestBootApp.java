package be.pxl.services;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

/**
 * Minimale Boot-config voor tests in review-service.
 * Geen component-scan, geen Feign/Eureka. Alleen auto-config.
 */
@SpringBootConfiguration
@EnableAutoConfiguration
public class TestBootApp { }
