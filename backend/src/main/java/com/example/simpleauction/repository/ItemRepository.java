package com.example.simpleauction.repository;

import com.example.simpleauction.entity.Item;
import com.example.simpleauction.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByCategoryIgnoreCase(String category);
    List<Item> findBySeller(User seller); // Find by seller entity
    List<Item> findBySellerEmail(String email); // Find by seller email directly
    List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String nameTerm, String descriptionTerm);
}
