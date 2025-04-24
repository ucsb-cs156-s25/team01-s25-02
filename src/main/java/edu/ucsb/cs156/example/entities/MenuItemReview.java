package edu.ucsb.cs156.example.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data // Provides getters, setters, toString, equals, hashCode
@AllArgsConstructor // Constructor with all arguments
@NoArgsConstructor // Constructor with no arguments
@Builder // Builder pattern implementation
@Entity(name = "menuitemreview") // Specifies this is a JPA entity, maps to table "menuitemreview"
public class MenuItemReview {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configures auto-incrementing ID
    private Long id;

    private Long itemId; // Foreign key to the menu item (we'll assume it's just a Long for now)
    private String reviewerEmail;
    private int stars; // Rating out of 5, for example
    private LocalDateTime dateReviewed;
    private String comments;
}