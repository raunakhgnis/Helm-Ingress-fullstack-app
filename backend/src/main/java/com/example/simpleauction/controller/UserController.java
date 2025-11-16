package com.example.simpleauction.controller;

import com.example.simpleauction.dto.BidDTO;
import com.example.simpleauction.dto.ItemDTO;
import com.example.simpleauction.dto.MessageResponse;
import com.example.simpleauction.service.AuthService;
import com.example.simpleauction.service.BidService;
import com.example.simpleauction.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
     private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final ItemService itemService;
    private final BidService bidService;
    private final AuthService authService;

    public UserController(ItemService itemService, BidService bidService, AuthService authService) {
        this.itemService = itemService;
        this.bidService = bidService;
        this.authService = authService;
    }

    // Endpoint to get items listed by the currently logged-in user
    @GetMapping("/me/items")
    public ResponseEntity<?> getMyListedItems(@RequestHeader("Authorization") String token) {
        String userEmail = authService.getUserEmailFromToken(token);
        if (userEmail == null) {
             logger.warn("Unauthorized attempt to access /me/items");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Unauthorized: Invalid session."));
        }
         logger.info("Fetching listed items for user: {}", userEmail);

        try {
            List<ItemDTO> items = itemService.getItemsBySellerEmail(userEmail);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
             logger.error("Error fetching listed items for user: {}", userEmail, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Failed to retrieve listed items."));
        }
    }

    // Endpoint to get bids placed by the currently logged-in user
    @GetMapping("/me/bids")
    public ResponseEntity<?> getMyBids(@RequestHeader("Authorization") String token) {
         String userEmail = authService.getUserEmailFromToken(token);
         if (userEmail == null) {
              logger.warn("Unauthorized attempt to access /me/bids");
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Unauthorized: Invalid session."));
         }
         logger.info("Fetching bids for user: {}", userEmail);

         try {
            List<BidDTO> bids = bidService.getBidsByUserEmail(userEmail);
            return ResponseEntity.ok(bids);
         } catch (Exception e) {
              logger.error("Error fetching bids for user: {}", userEmail, e);
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Failed to retrieve bids."));
         }
    }
}
