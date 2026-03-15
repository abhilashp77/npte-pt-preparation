package com.npte.portal.repository;

import com.npte.portal.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    
    @Query(value = "SELECT * FROM syllabus_topics ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Topic> findRandomTopic();
}
