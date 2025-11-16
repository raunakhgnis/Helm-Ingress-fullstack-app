 package com.example.simpleauction.dto;

 import java.math.BigDecimal;
 import java.time.LocalDateTime;
 import java.util.Objects;

 public class ItemDTO {
     private Long id;
     private String name;
     private String description;
     private BigDecimal startingPrice;
     private BigDecimal currentBidPrice;
     private String category;
     private String imageUrl;
     private LocalDateTime auctionEndTime;
     private String sellerEmail;
     private String highestBidderEmail;
     private String paymentStatus; // Added payment status

     // No-arg constructor
     public ItemDTO() {}

     // --- Manual Getters and Setters for ALL fields ---
     public Long getId() { return id; }
     public void setId(Long id) { this.id = id; }
     public String getName() { return name; }
     public void setName(String name) { this.name = name; }
     public String getDescription() { return description; }
     public void setDescription(String description) { this.description = description; }
     public BigDecimal getStartingPrice() { return startingPrice; }
     public void setStartingPrice(BigDecimal startingPrice) { this.startingPrice = startingPrice; }
     public BigDecimal getCurrentBidPrice() { return currentBidPrice; }
     public void setCurrentBidPrice(BigDecimal currentBidPrice) { this.currentBidPrice = currentBidPrice; }
     public String getCategory() { return category; }
     public void setCategory(String category) { this.category = category; }
     public String getImageUrl() { return imageUrl; }
     public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
     public LocalDateTime getAuctionEndTime() { return auctionEndTime; }
     public void setAuctionEndTime(LocalDateTime auctionEndTime) { this.auctionEndTime = auctionEndTime; }
     public String getSellerEmail() { return sellerEmail; }
     public void setSellerEmail(String sellerEmail) { this.sellerEmail = sellerEmail; }
     public String getHighestBidderEmail() { return highestBidderEmail; }
     public void setHighestBidderEmail(String highestBidderEmail) { this.highestBidderEmail = highestBidderEmail; }
     public String getPaymentStatus() { return paymentStatus; }
     public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

     // --- equals, hashCode, toString ---
     @Override
     public boolean equals(Object o) {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;
         ItemDTO itemDTO = (ItemDTO) o;
         return Objects.equals(id, itemDTO.id);
     }

     @Override
     public int hashCode() {
         return Objects.hash(id);
     }

      @Override
     public String toString() {
         return "ItemDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
     }
 }
 
 