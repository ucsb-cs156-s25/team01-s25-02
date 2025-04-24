package edu.ucsb.cs156.example.repositories;

import edu.ucsb.cs156.example.entities.MenuItemReview;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository // Marks this as a Spring Data repository
public interface MenuItemReviewRepository extends CrudRepository<MenuItemReview, Long> {
    // You can add custom query methods here later if needed, e.g.:
    // Iterable<MenuItemReview> findByItemId(Long itemId);
}