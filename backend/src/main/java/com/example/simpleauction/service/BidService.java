 package com.example.simpleauction.service;

 import com.example.simpleauction.dto.BidDTO;
 import com.example.simpleauction.dto.BidRequest;
 import com.example.simpleauction.entity.Bid;
 import com.example.simpleauction.entity.Item;
 import com.example.simpleauction.entity.User;
 import com.example.simpleauction.repository.BidRepository;
 import com.example.simpleauction.repository.UserRepository;
 import jakarta.persistence.EntityNotFoundException;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.stereotype.Service;
 import org.springframework.transaction.annotation.Transactional;

 import java.math.BigDecimal;
 import java.time.LocalDateTime;
 import java.util.List;
 import java.util.stream.Collectors;

 @Service
 public class BidService {
     private static final Logger logger = LoggerFactory.getLogger(BidService.class);

     private final BidRepository bidRepository;
     private final UserRepository userRepository;
     private final ItemService itemService;

     public BidService(BidRepository bidRepository, UserRepository userRepository, ItemService itemService) {
         this.bidRepository = bidRepository;
         this.userRepository = userRepository;
         this.itemService = itemService;
     }

     @Transactional
     public BidDTO placeBid(Long itemId, BidRequest bidRequest, String bidderEmail) {
         User bidder = userRepository.findByEmail(bidderEmail)
                 .orElseThrow(() -> new EntityNotFoundException("Bidder not found with email: " + bidderEmail));

         Item item = itemService.getItemEntityById(itemId); // Get managed entity

         // --- Validation ---
         if (item.getAuctionEndTime() == null || LocalDateTime.now().isAfter(item.getAuctionEndTime())) {
             throw new IllegalStateException("Auction has ended for item: " + item.getName());
         }
         if (item.getSeller() != null && item.getSeller().getId().equals(bidder.getId())) {
             throw new IllegalArgumentException("Seller cannot bid on their own item.");
         }
         BigDecimal currentPrice = item.getCurrentBidPrice() != null ? item.getCurrentBidPrice() : item.getStartingPrice();
         if (bidRequest.getAmount() == null || bidRequest.getAmount().compareTo(currentPrice) <= 0) {
              throw new IllegalArgumentException("Bid amount must be provided and higher than the current price of $" + currentPrice.toPlainString());
         }
         // --- End Validation ---

         Bid bid = new Bid(item, bidder, bidRequest.getAmount(), LocalDateTime.now());
         Bid savedBid = bidRepository.save(bid);
         logger.info("Bid saved: ID={}, ItemID={}, BidderEmail={}, Amount={}",
                     savedBid.getId(), itemId, bidderEmail, bidRequest.getAmount());

         // Update item's current highest bid and bidder (now done in ItemService)
         itemService.updateHighestBidder(itemId, bidder, bidRequest.getAmount());

         return convertToDto(savedBid);
     }

     public List<BidDTO> getBidsByUserEmail(String email) {
          User bidder = userRepository.findByEmail(email)
                 .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
         return bidRepository.findByBidderOrderByBidTimeDesc(bidder).stream()
                 .map(this::convertToDto)
                 .collect(Collectors.toList());
     }

      public List<BidDTO> getBidsForItem(Long itemId) {
         return bidRepository.findByItemIdOrderByBidAmountDesc(itemId).stream()
                 .map(this::convertToDto)
                 .collect(Collectors.toList());
     }

     // --- Helper Method ---
     private BidDTO convertToDto(Bid bid) {
         BidDTO dto = new BidDTO();
         dto.setId(bid.getId());
         dto.setBidAmount(bid.getBidAmount());
         dto.setBidTime(bid.getBidTime());
         if (bid.getBidder() != null) {
             dto.setBidderEmail(bid.getBidder().getEmail());
         }
         if (bid.getItem() != null) {
             dto.setItemId(bid.getItem().getId());
             dto.setItemName(bid.getItem().getName());
         }
         return dto;
     }
 }