package com.npte.portal.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "explanations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Explanation {
    @Id
    @Column(name = "question_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "explanation_text", columnDefinition = "TEXT")
    private String explanationText;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "explanation_references", joinColumns = @JoinColumn(name = "explanation_id"))
    @Column(name = "reference")
    private List<String> references;
}
