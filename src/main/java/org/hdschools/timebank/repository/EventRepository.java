package org.hdschools.timebank.repository;

import java.util.List;
import org.hdschools.timebank.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    /**
     * Finds all events of type "accepted" initiated by students.
     * Used for recalculating point balances.
     *
     * @return list of accepted events with student initiators
     */
    @Query("SELECT e FROM Event e WHERE e.type = 'accepted' AND e.initStuId IS NOT NULL")
    List<Event> findAllAcceptedByStudents();
    
    /**
     * Finds all events of type "accepted" or "rejected" initiated by students.
     * Used for recalculating credit balances.
     *
     * @return list of accepted/rejected events with student initiators
     */
    @Query("SELECT e FROM Event e WHERE (e.type = 'accepted' OR e.type = 'rejected') AND e.initStuId IS NOT NULL")
    List<Event> findAllAcceptedOrRejectedByStudents();
}
