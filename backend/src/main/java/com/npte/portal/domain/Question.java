package com.npte.portal.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question_text", columnDefinition = "TEXT")
    private String questionText;

    @Column(name = "body_system")
    private String bodySystem;

    private String difficulty;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private Answer answer;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @PrimaryKeyJoinColumn
    private Explanation explanation;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Image> images = new ArrayList<>();

    // Helpers to easily set bi-directional relations
    public void setAnswer(Answer answer) {
        this.answer = answer;
        if (answer != null) {
            answer.setQuestion(this);
        }
    }

    public void setExplanation(Explanation explanation) {
        this.explanation = explanation;
        if (explanation != null) {
            explanation.setQuestion(this);
        }
    }

    public void addImage(Image image) {
        images.add(image);
        image.setQuestion(this);
    }
}
