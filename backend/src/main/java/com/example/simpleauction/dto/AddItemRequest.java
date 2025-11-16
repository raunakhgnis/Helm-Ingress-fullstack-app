package com.example.simpleauction.dto;

import java.math.BigDecimal;

public class AddItemRequest {
    private String name;
    private String description;
    private BigDecimal startingPrice;
    private String category;
    private String imageUrl;
    private String auctionEndTime; // Receive as String

    public AddItemRequest() {}

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getStartingPrice() { return startingPrice; }
    public void setStartingPrice(BigDecimal startingPrice) { this.startingPrice = startingPrice; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getAuctionEndTime() { return auctionEndTime; }
    public void setAuctionEndTime(String auctionEndTime) { this.auctionEndTime = auctionEndTime; }
}