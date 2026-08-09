package com.eventhub.event;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

interface EventRepository extends JpaRepository<Event, Long> {
    @EntityGraph(attributePaths = "registrations")
    List<Event> findAllByOrderByStartsAtAsc();

    @EntityGraph(attributePaths = "registrations")
    @Query("select e from Event e where e.id = :id")
    Optional<Event> findWithRegistrationsById(Long id);
}
