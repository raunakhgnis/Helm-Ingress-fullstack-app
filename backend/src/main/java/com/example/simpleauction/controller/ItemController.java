package com.example.simpleauction.controller;

import com.example.simpleauction.dto.AddItemRequest;
import com.example.simpleauction.dto.BidRequest;
import com.example.simpleauction.dto.ItemDTO;
import com.example.simpleauction.dto.MessageResponse;
import com.example.simpleauction.service.AuthService;
import com.example.simpleauction.service.BidService;
import com.example.simpleauction.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {
     private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    private final ItemService itemService;
    private final AuthService authService;
    private final BidService bidService;


    public ItemController(ItemService itemService, AuthService authService, BidService bidService) {
        this.itemService = itemService;
        this.authService = authService;
        this.bidService = bidService;
    }

    // GET /api/items (Handles all, category, search)
    @GetMapping
    public ResponseEntity<List<ItemDTO>> getItems(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) { // Renamed from 'term' for clarity
        logger.info("Request received for items. Category: '{}', Search: '{}'", category, search);
        if (category != null && !category.isBlank()) {
             logger.debug("Fetching items by category: {}", category);
            return ResponseEntity.ok(itemService.getItemsByCategory(category));
        } else if (search != null && !search.isBlank()) {
             logger.debug("Searching items with term: {}", search);
             return ResponseEntity.ok(itemService.searchItems(search));
        } else {
             logger.debug("Fetching all items.");
            return ResponseEntity.ok(itemService.getAllItems());
        }
    }

    // GET /api/items/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getItemById(@PathVariable Long id) {
        logger.debug("Request received for item ID: {}", id);
         return itemService.getItemDtoById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                     logger.warn("Item not found with ID: {}", id);
                     return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                      .body(new MessageResponse("Item not found with ID: " + id));
                });
    }

    // POST /api/items (Add new item)
    @PostMapping
    public ResponseEntity<?> addItem(
            @RequestBody AddItemRequest addItemRequest,
            @RequestHeader("Authorization") String token
            ) {
         logger.info("Request received to add new item: {}", addItemRequest.getName()); // Don't log full request easily if sensitive
         // Simple Auth Check
        String userEmail = authService.getUserEmailFromToken(token); // Use helper method if created in AuthService
         if (userEmail == null) {
              logger.warn("Unauthorized attempt to add item. Invalid token provided.");
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Unauthorized: Invalid session."));
         }

        try {
            ItemDTO createdItem = itemService.createItem(addItemRequest, userEmail);
            logger.info("Successfully added item ID: {}, Name: {}", createdItem.getId(), createdItem.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
        } catch (EntityNotFoundException e) {
             logger.warn("Failed to add item. Reason: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
             logger.warn("Failed to add item due to invalid argument. Reason: {}", e.getMessage());
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
             logger.error("Internal error adding item.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Failed to add item due to an internal error."));
        }
    }

    // POST /api/items/{id}/bid
     @PostMapping("/{id}/bid")
    public ResponseEntity<?> placeBid(
            @PathVariable Long id,
            @RequestBody BidRequest bidRequest, // Assuming BidRequest has BigDecimal amount
            @RequestHeader("Authorization") String token
         ) {
        logger.info("Request received to place bid on item ID: {}", id);
        // Simple Auth Check
        String userEmail = authService.getUserEmailFromToken(token); // Use helper method
         if (userEmail == null) {
              logger.warn("Unauthorized attempt to bid on item ID: {} (Invalid Token)", id);
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Unauthorized: Invalid session."));
         }

        try {
            // Pass item ID, request body (contains amount), and bidder's email
            return ResponseEntity.ok(bidService.placeBid(id, bidRequest, userEmail));
        } catch (EntityNotFoundException e) {
             logger.warn("Failed to place bid on item ID: {}. Item not found.", id);
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        } catch (IllegalArgumentException | IllegalStateException e) {
             logger.warn("Failed to place bid on item ID: {}. Reason: {}", id, e.getMessage());
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
             logger.error("Internal error placing bid on item ID: {}", id, e);
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Failed to place bid due to an internal error."));
        }
    }

     // GET /api/items/{id}/bids
    @GetMapping("/{id}/bids")
    public ResponseEntity<?> getBidsForItem(@PathVariable Long id) {
         logger.debug("Request received for bids for item ID: {}", id);
        try {
            // Note: Bids might contain sensitive user info, consider filtering in DTO if needed
            return ResponseEntity.ok(bidService.getBidsForItem(id));
        } catch (Exception e) {
             logger.error("Failed to retrieve bids for item ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Failed to retrieve bids."));
        }
    }

    // --- NEW Payment Endpoint ---
    @PostMapping("/{id}/pay")
    public ResponseEntity<?> initiatePayment(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        logger.info("Received payment initiation request for item ID: {}", id);
         // Simple Auth Check
        String userEmail = authService.getUserEmailFromToken(token); // Use helper method
         if (userEmail == null) {
              logger.warn("Unauthorized payment attempt for item ID: {} (Invalid Token)", id);
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Unauthorized: Invalid session."));
         }

        try {
            boolean success = itemService.initiatePayment(id, userEmail);
            if (success) {
                 logger.info("Payment simulation successful for item ID: {}", id);
                // You could optionally return the updated ItemDTO here
                return ResponseEntity.ok(new MessageResponse("Payment successful (simulated). Item status updated."));
            } else {
                 logger.warn("Payment simulation failed for item ID: {}", id);
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED) // 402 Payment Required might be suitable
                                 .body(new MessageResponse("Payment failed (simulated). Please try again or contact support."));
            }
        } catch (EntityNotFoundException e) {
            logger.error("Payment attempt failed for item ID: {}. Item not found.", id);
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(e.getMessage()));
        } catch (IllegalArgumentException | IllegalStateException e) {
             logger.warn("Payment attempt validation failed for item ID: {}. Reason: {}", id, e.getMessage());
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
             logger.error("Internal error during payment simulation for item ID: {}", id, e);
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageResponse("Payment initiation failed due to an internal error."));
        }
    }
}

