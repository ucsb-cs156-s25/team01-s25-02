package edu.ucsb.cs156.example.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import edu.ucsb.cs156.example.entities.HelpRequest;

/**
 * The RestaurantRepository is a repository for Restaurant entities
 */
@Repository
public interface HelpRequestRepository extends CrudRepository<HelpRequest, Long> {

}
