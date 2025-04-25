package edu.ucsb.cs156.example.controllers;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.ucsb.cs156.example.entities.HelpRequest;
import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.HelpRequestRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * This is a REST controller for HELPREQUEST
 */

 @Tag(name = "HELPREQUEST")
 @RequestMapping("/api/helprequest")
 @RestController
 @Slf4j
public class HelpRequestController extends ApiController { 
    @Autowired
    HelpRequestRepository helpRequestRepository;

    /**
     * List all help requests
     * 
     * @return an iterable of HELPREQUEST
     */
    @Operation(summary = "List all Help Requests")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<HelpRequest> allHelpRequests() {
        Iterable<HelpRequest> helpRequests = helpRequestRepository.findAll();
        return helpRequests;
    }
    
    /**
     * Create a new help request
     * 
     * @param requesterEmail  
     * @param teamId          
     * @param tableOrBreakoutRoom 
     * @param requestTime
     * @param explanation
     * @param solved
     * @return the saved help request
     */
    @Operation(summary= "Create a new help request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public HelpRequest postHelpRequest(
            @Parameter(name="requesterEmail") @RequestParam String requesterEmail,
            @Parameter(name="teamID") @RequestParam String teamId,
            @Parameter(name="tableOrBreakoutRoom") @RequestParam String tableOrBreakoutRoom,
            @Parameter(name="solved") @RequestParam boolean solved,
            @Parameter(name="explanation") @RequestParam String explanation,
            @Parameter(name="requestTime") @RequestParam("requestTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime requestTime)
            throws JsonProcessingException {

        // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        // See: https://www.baeldung.com/spring-date-parameters

        log.info("localDateTime={}", requestTime);
        HelpRequest hr = new HelpRequest();
        hr.setRequesterEmail(requesterEmail);
        hr.setTeamId(teamId);
        hr.setTableOrBreakoutRoom(tableOrBreakoutRoom);
        hr.setRequestTime(requestTime);
        hr.setExplanation(explanation);
        hr.setSolved(solved);


        HelpRequest savedHelpRequest = helpRequestRepository.save(hr);
        
        return savedHelpRequest;
    }
    /**
     * Update a single help request
     * 
     * @param id       id of the help request to update
     * @param incoming the new help request
     * @return the updated help request object
     */
    @Operation(summary= "Update a single help request")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public HelpRequest updateUCSBDHelpRequest(
            @Parameter(name="id") @RequestParam Long id,
            @RequestBody @Valid HelpRequest incoming) {

        HelpRequest helpRequest = helpRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(HelpRequest.class, id));

        helpRequest.setRequesterEmail(incoming.getRequesterEmail());
        helpRequest.setTeamId(incoming.getTeamId());
        helpRequest.setTableOrBreakoutRoom(incoming.getTableOrBreakoutRoom());
        helpRequest.setRequestTime(incoming.getRequestTime());
        helpRequest.setExplanation(incoming.getExplanation());
        helpRequest.setSolved(incoming.getSolved());

        helpRequestRepository.save(helpRequest);

        return helpRequest;
    }
    

    /**
     * Get a single date by id
     * 
     * @param id the id of the help request
     * @return a help request
     */
    @Operation(summary= "Get a single help request")
    @PreAuthorize("hasRole('ROLE_USER')")   
    @GetMapping("")
    public HelpRequest getById(
            @Parameter(name="id") @RequestParam Long id) {
        HelpRequest helpRequest = helpRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(HelpRequest.class, id));

        return helpRequest;
    }

}