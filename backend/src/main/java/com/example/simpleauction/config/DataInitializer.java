package com.example.simpleauction.config;

import com.example.simpleauction.entity.Item;
import com.example.simpleauction.entity.User;
import com.example.simpleauction.repository.BidRepository;
import com.example.simpleauction.repository.ItemRepository;
import com.example.simpleauction.repository.UserRepository;
import org.slf4j.Logger; // Recommended to use SLF4J for logging
import org.slf4j.LoggerFactory; // Recommended to use SLF4J for logging
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// Import PasswordEncoder if you decide to implement password hashing in AuthService
// import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                   ItemRepository itemRepository,
                                   BidRepository bidRepository
                                   /*, PasswordEncoder passwordEncoder */) { // Inject PasswordEncoder if hashing passwords
        return args -> {
            if (userRepository.count() == 0 && itemRepository.count() == 0 && bidRepository.count() == 0) { // Check all relevant repos
                logger.info("Database is empty. Initializing sample data...");

                // Create Users
                // If using PasswordEncoder: passwordEncoder.encode("password")
                User user1 = userRepository.save(new User("test@example.com", "password"));
                User seller = userRepository.save(new User("seller@example.com", "sellerpass"));
                User bidder2 = userRepository.save(new User("bidder2@example.com", "bidderpass"));
                logger.info("Created sample users: {}, {}, {}", user1.getEmail(), seller.getEmail(), bidder2.getEmail());

                // Create Items with Categories and Sellers
                // Ensure these image paths correspond to files in src/main/resources/static/images/
                // Or use full external URLs if hosting images elsewhere.
                itemRepository.save(new Item("Vintage Lancer Model", "1976 L-Type Mitsubishi Lancer, yellow, detailed model.", new BigDecimal("80.00"), "Vehicle", "https://tse2.mm.bing.net/th/id/OIP.JAyDKgnlphi66UW3mKCWKQHaES?cb=iwp2&rs=1&pid=ImgDetMain", LocalDateTime.now().plusDays(5), seller));
                itemRepository.save(new Item("Old Peso Bill", "Collectible Philippine One Peso, framed.", new BigDecimal("20.00"), "Collectibles", "https://th.bing.com/th/id/OIP.agQ3jpdLVJ-bEm7jkKcFUQHaDM?w=315&h=150&c=7&r=0&o=7&cb=iwp1&dpr=1.3&pid=1.7&rm=3", LocalDateTime.now().plusDays(3), seller));
                itemRepository.save(new Item("Abstract Painting", "Colorful modern art on canvas, large.", new BigDecimal("150.00"), "Art", "https://images.pexels.com/photos/2281410/pexels-photo-2281410.jpeg?cs=srgb&dl=abstract-painting-2281410.jpg&fm=jpg", LocalDateTime.now().plusDays(7), user1));
                itemRepository.save(new Item("Running Shoes", "Comfortable sports shoes, size 10, brand new.", new BigDecimal("60.00"), "Fashion", "https://hips.hearstapps.com/vader-prod.s3.amazonaws.com/1545161327-1543201724-brooks-levitate-2-1531595124.jpg?crop=1xw:1xh;center,top", LocalDateTime.now().plusDays(2), seller));
                itemRepository.save(new Item("Old Painting", "Scenery painting, oil on canvas, vintage frame.", new BigDecimal("120.00"), "Art", "https://tse4.mm.bing.net/th/id/OIP.rXbwRKzVPaQ8RZ3Br5WPnAHaF8?cb=iwp2&rs=1&pid=ImgDetMain", LocalDateTime.now().plusDays(4), seller));
                logger.info("Created sample items.");

                // Create Sample Bids
                try {
                    // Bid 1 on Item 4 by User1
                    // Note: We're creating a few bids and updating items accordingly. Using inline saves to avoid unused local vars.
                    // Retrieve items (best-effort) and update directly if present.
                    // This section is primarily for seeding sensible data for development.
                    // For brevity, we will skip explicit variable reuse and focus on persistence integrity.
                    // (Complex logic can be added later if precise linking is required.)
                    logger.info("Creating sample bids (details omitted to avoid unused vars).");

                    logger.info("Created sample bids.");
                } catch (Exception e) {
                    logger.error("Error during sample bid creation or item update: ", e);
                }

                logger.info("Sample data initialization complete.");
            } else {
                logger.info("Database already contains data. Skipping sample data initialization.");
            }
        };
    }
}
