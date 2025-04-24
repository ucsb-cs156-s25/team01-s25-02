package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItem;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.time.LocalDateTime;


@Tag(name = "UCSBDiningCommonsMenuItem")
@RequestMapping("/api/ucsbdiningcommonsmenuitem")
@RestController
@Slf4j
public class UCSBDiningCommonsMenuItemController extends ApiController {

    @Autowired
    UCSBDiningCommonsMenuItemRepository ucsbDiningCommonsMenuItemRepository;

 
    @Operation(summary= "List all ucsb dates")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBDiningCommonsMenuItem> allItem() {
        Iterable<UCSBDiningCommonsMenuItem> menuitem = ucsbDiningCommonsMenuItemRepository.findAll();
        return menuitem;
    }


    /**
     * Create a new UCSBDiningCommonsMenuItem
     * 
     * @param diningCommonsCode  the code for the dining commons
     * @param name               the name of the menu item
     * @param station            the station where the item is served
     * @return the saved item
     */
    @Operation(summary = "Create a new UCSBDiningCommonsMenuItem")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBDiningCommonsMenuItem postItem(
            @Parameter(name = "diningCommonsCode") @RequestParam String diningCommonsCode,
            @Parameter(name = "name") @RequestParam String name,
            @Parameter(name = "station") @RequestParam String station) {

        UCSBDiningCommonsMenuItem item = UCSBDiningCommonsMenuItem.builder()
                .diningCommonsCode(diningCommonsCode)
                .name(name)
                .station(station)
                .build();

        return ucsbDiningCommonsMenuItemRepository.save(item);
    }
   
}


