package ai.shreds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main Spring Boot application class for the Component Reservation Shred.
 * This application manages the complete lifecycle of component reservations for production runs.
 * 
 * Features:
 * - REST endpoints for reservation management
 * - JMS messaging for event publishing
 * - Scheduled tasks for reservation expiration
 * - Transactional database operations
 * - Stock level validation and management
 */
@SpringBootApplication(
    scanBasePackages = {
        "ai.shreds.adapter",
        "ai.shreds.application", 
        "ai.shreds.domain",
        "ai.shreds.infrastructure",
        "ai.shreds.shared"
    }
)
@EnableJms
@EnableScheduling
@EnableTransactionManagement
public class ComponentReservationApplication {

    public static void main(String[] args) {
        // Set system properties for better logging during startup
        System.setProperty("spring.output.ansi.enabled", "always");
        System.setProperty("logging.pattern.console", "%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(%5p) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n%wEx");
        
        // Print startup banner
        System.out.println("=".repeat(80));
        System.out.println("🏭 COMPONENT RESERVATION SHRED - STARTING UP");
        System.out.println("📦 Managing component reservations for production runs");
        System.out.println("⚡ Features: REST API, JMS Messaging, Scheduled Tasks, Transactions");
        System.out.println("=".repeat(80));
        
        try {
            SpringApplication.run(ComponentReservationApplication.class, args);
            
            System.out.println("✅ Component Reservation Shred started successfully!");
            System.out.println("🚀 Ready to process reservation requests");
            System.out.println("📊 Health check available at: /api/actuator/health");
            System.out.println("=".repeat(80));
            
        } catch (Exception e) {
            System.err.println("❌ Failed to start Component Reservation Shred");
            System.err.println("💥 Error: " + e.getMessage());
            System.err.println("=".repeat(80));
            System.exit(1);
        }
    }
}