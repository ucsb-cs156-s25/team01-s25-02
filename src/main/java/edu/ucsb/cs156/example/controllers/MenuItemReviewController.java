package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.MenuItemReview;
import edu.ucsb.cs156.example.repositories.MenuItemReviewRepository;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * This is a REST controller for MenuItemReview
 */

@Tag(name = "MenuItemReview")
@RequestMapping("/api/menuitemreview")
@RestController
@Slf4j
public class MenuItemReviewController extends ApiController {

    @Autowired
    MenuItemReviewRepository menuItemReviewRepository;

    /**
     * List all menu item reviews
     * 
     * @return an iterable of MenuItemReview
     */
    @Operation(summary = "List all menu item reviews")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<MenuItemReview> allReviews() {
        Iterable<MenuItemReview> reviews = menuItemReviewRepository.findAll();
        return reviews;
    }

    /**
     * Get a single review by id
     * 
     * @param id the id of the review
     * @return a MenuItemReview
     */
    @Operation(summary = "Get a single menu item review")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public MenuItemReview getById(
            @Parameter(name = "id") @RequestParam Long id) {
        MenuItemReview review = menuItemReviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MenuItemReview.class, id));
        return review;
    }

    /**
     * Create a new review
     * 
     * @param itemId        the id of the menu item being reviewed
     * @param reviewerEmail the email of the reviewer
     * @param stars         the star rating (1-5)
     * @param comments      the review comments
     * @return the saved review
     */
    @Operation(summary = "Create a new menu item review")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public MenuItemReview postReview(
            @Parameter(name = "itemId") @RequestParam Long itemId,
            @Parameter(name = "reviewerEmail") @RequestParam String reviewerEmail,
            @Parameter(name = "stars") @RequestParam int stars,
            @Parameter(name = "comments") @RequestParam String comments) {

        log.info("itemId={}, reviewerEmail={}, stars={}, comments={}", itemId, reviewerEmail, stars, comments);

        MenuItemReview review = MenuItemReview.builder()
                .itemId(itemId)
                .reviewerEmail(reviewerEmail)
                .stars(stars)
                .comments(comments)
                .build();

        return menuItemReviewRepository.save(review);
    }

    /**
     * Update a single review
     * 
     * @param id       the id of the review to update
     * @param incoming the new review data
     * @return the updated review
     */
    @Operation(summary = "Update a single menu item review")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public MenuItemReview updateReview(
            @Parameter(name = "id") @RequestParam Long id,
            @RequestBody @Valid MenuItemReview incoming) {

        MenuItemReview review = menuItemReviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MenuItemReview.class, id));

        review.setItemId(incoming.getItemId());
        review.setReviewerEmail(incoming.getReviewerEmail());
        review.setStars(incoming.getStars());
        review.setComments(incoming.getComments());

        menuItemReviewRepository.save(review);
        return review;
    }

    /**
     * Delete a review
     * 
     * @param id the id of the review to delete
     * @return a message indicating the review was deleted
     */
    @Operation(summary = "Delete a menu item review")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteReview(
            @Parameter(name = "id") @RequestParam Long id) {
        MenuItemReview review = menuItemReviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MenuItemReview.class, id));

        menuItemReviewRepository.delete(review);
        return genericMessage("MenuItemReview with id %s deleted".formatted(id));
    }
}