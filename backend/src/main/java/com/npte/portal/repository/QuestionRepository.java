package com.npte.portal.repository;

import com.npte.portal.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(value = "SELECT * FROM questions ORDER BY random() LIMIT 1", nativeQuery = true)
    Optional<Question> findRandomQuestion();

    @Query(value = "SELECT * FROM questions WHERE id != :excludedId ORDER BY random() LIMIT 1", nativeQuery = true)
    Optional<Question> findRandomQuestionExcludingId(
            @org.springframework.data.repository.query.Param("excludedId") Long excludedId);

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE questions RESTART IDENTITY CASCADE", nativeQuery = true)
    void truncateAndResetQuestions();
}
