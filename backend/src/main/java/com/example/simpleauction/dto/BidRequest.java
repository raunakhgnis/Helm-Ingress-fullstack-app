package com.example.simpleauction.dto;

import java.math.BigDecimal;

public class BidRequest {
    private BigDecimal amount;

    public BidRequest() {}

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
