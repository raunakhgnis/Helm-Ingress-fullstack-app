package com.example.simpleauction.repository;

import com.example.simpleauction.entity.Bid;
import com.example.simpleauction.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByBidderOrderByBidTimeDesc(User bidder);
    List<Bid> findByBidderEmailOrderByBidTimeDesc(String email);
    List<Bid> findByItemIdOrderByBidAmountDesc(Long itemId);
}