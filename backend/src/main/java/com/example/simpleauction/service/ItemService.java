package com.example.simpleauction.service;

import com.example.simpleauction.dto.AddItemRequest;
import com.example.simpleauction.dto.ItemDTO;
import com.example.simpleauction.entity.Item;
import com.example.simpleauction.entity.User;
import com.example.simpleauction.repository.ItemRepository;
import com.example.simpleauction.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal; // Make sure this is imported
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemService {
    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    // --- Getters ---
    public List<ItemDTO> getAllItems() {
        return itemRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<ItemDTO> getItemDtoById(Long id) {
        return itemRepository.findById(id).map(this::convertToDto);
    }

     public List<ItemDTO> getItemsByCategory(String category) {
        return itemRepository.findByCategoryIgnoreCase(category).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

     public List<ItemDTO> searchItems(String searchTerm) {
         if (searchTerm == null || searchTerm.isBlank()) {
             return getAllItems();
         }
        return itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(searchTerm, searchTerm).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<ItemDTO> getItemsBySellerEmail(String email) {
        return itemRepository.findBySellerEmail(email).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // --- Item Creation ---
    @Transactional
    public ItemDTO createItem(AddItemRequest request, String sellerEmail) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new EntityNotFoundException("Seller not found with email: " + sellerEmail));

        Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setStartingPrice(request.getStartingPrice());
        item.setCurrentBidPrice(request.getStartingPrice());
        item.setCategory(request.getCategory());
        item.setImageUrl(request.getImageUrl());
        item.setSeller(seller);
        item.setPaymentStatus(null); // Explicitly null on creation

        try {
            item.setAuctionEndTime(LocalDateTime.parse(request.getAuctionEndTime()));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date/time format for auctionEndTime. Use ISO format (YYYY-MM-DDTHH:mm).", e);
        } catch (NullPointerException e) {
             throw new IllegalArgumentException("auctionEndTime cannot be null.", e);
        }

        Item savedItem = itemRepository.save(item);
        logger.info("Created Item: ID={}, Name={}, Seller={}", savedItem.getId(), savedItem.getName(), sellerEmail);
        return convertToDto(savedItem);
    }

    // --- Bidding Related (used by BidService or ItemController) ---
     public Item getItemEntityById(Long id) {
         return itemRepository.findById(id)
             .orElseThrow(() -> new EntityNotFoundException("Item not found with ID: " + id));
    }

    @Transactional
    public void updateHighestBidder(Long itemId, User bidder, BigDecimal amount) {
         Item item = getItemEntityById(itemId);
         item.setCurrentBidPrice(amount);
         item.setHighestBidder(bidder);
         item.setPaymentStatus(null); // Reset payment status if a new higher bid comes in
         itemRepository.save(item);
         logger.info("Updated highest bidder for Item ID: {} to User ID: {} with Amount: {}", itemId, bidder.getId(), amount);
     }

    // --- NEW Payment Simulation Method ---
    @Transactional
    public boolean initiatePayment(Long itemId, String winningUserEmail) {
        Item item = getItemEntityById(itemId);

        // 1. Verify Auction Ended
        if (item.getAuctionEndTime() == null || LocalDateTime.now().isBefore(item.getAuctionEndTime())) {
            logger.warn("Payment attempt for ongoing auction. Item ID: {}, User: {}", itemId, winningUserEmail);
            throw new IllegalStateException("Auction has not ended yet.");
        }

        // 2. Verify Winner
        if (item.getHighestBidder() == null || !item.getHighestBidder().getEmail().equals(winningUserEmail)) {
             logger.warn("Non-winner payment attempt. Item ID: {}, User: {}, Highest Bidder: {}", itemId, winningUserEmail, item.getHighestBidder() != null ? item.getHighestBidder().getEmail() : "None");
            throw new IllegalArgumentException("You are not the highest bidder for this item.");
        }

        // 3. Check Current Payment Status
        if ("PAID".equalsIgnoreCase(item.getPaymentStatus())) {
             logger.info("Item {} already marked as PAID for user {}.", itemId, winningUserEmail);
             return true; // Already paid
        }
         if ("PENDING".equalsIgnoreCase(item.getPaymentStatus())) {
             logger.info("Payment for item {} is already PENDING for user {}. Allowing retry simulation.", itemId, winningUserEmail);
             // Allow simulation to proceed for demo purposes
        }


        // 4. SIMULATE Payment Gateway Call
        logger.info("Simulating payment gateway interaction for item ID: {}, Amount: {}, User: {}",
                   itemId, item.getCurrentBidPrice(), winningUserEmail);
        boolean paymentSuccess = simulateGatewayCall();

        // 5. Update Status based on simulation
        if (paymentSuccess) {
            item.setPaymentStatus("PAID");
            logger.info("Payment successful for item ID: {}. Status updated to PAID.", itemId);
        } else {
            item.setPaymentStatus("FAILED");
            logger.error("Simulated payment failed for item ID: {}.", itemId);
        }
        itemRepository.save(item);
        return paymentSuccess;
    }

    // --- Payment Gateway Simulation ---
    private boolean simulateGatewayCall() {
        try {
            Thread.sleep(750); // Simulate network delay slightly longer
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false; // Treat interrupt as failure
        }
        // Simulate success/failure
        return Math.random() > 0.15; // ~85% success rate
    }

    // --- Helper DTO Conversion ---
    private ItemDTO convertToDto(Item item) {
        ItemDTO dto = new ItemDTO();
        // Use manual getters
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setStartingPrice(item.getStartingPrice());
        dto.setCurrentBidPrice(item.getCurrentBidPrice());
        dto.setCategory(item.getCategory());
        dto.setImageUrl(item.getImageUrl());
        dto.setAuctionEndTime(item.getAuctionEndTime());
        dto.setPaymentStatus(item.getPaymentStatus()); // Include payment status
        if (item.getSeller() != null) {
            dto.setSellerEmail(item.getSeller().getEmail());
        }
         if (item.getHighestBidder() != null) {
            dto.setHighestBidderEmail(item.getHighestBidder().getEmail());
        }
        return dto;
    }
}

