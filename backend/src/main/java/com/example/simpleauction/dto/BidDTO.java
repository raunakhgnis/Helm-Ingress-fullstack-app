package com.example.simpleauction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class BidDTO {
    private Long id;
    private Long itemId;
    private String itemName;
    private String bidderEmail;
    private BigDecimal bidAmount;
    private LocalDateTime bidTime;

    public BidDTO() {}

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getBidderEmail() { return bidderEmail; }
    public void setBidderEmail(String bidderEmail) { this.bidderEmail = bidderEmail; }
    public BigDecimal getBidAmount() { return bidAmount; }
    public void setBidAmount(BigDecimal bidAmount) { this.bidAmount = bidAmount; }
    public LocalDateTime getBidTime() { return bidTime; }
    public void setBidTime(LocalDateTime bidTime) { this.bidTime = bidTime; }

    // --- equals, hashCode ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BidDTO bidDTO = (BidDTO) o;
        return Objects.equals(id, bidDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}