package com.npte.portal.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "syllabus_topics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", columnDefinition = "TEXT")
    private String title;

    @Column(name = "category")
    private String category;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @ElementCollection
    @CollectionTable(name = "topic_references", joinColumns = @JoinColumn(name = "topic_id"))
    @Column(name = "reference")
    @Builder.Default
    private List<String> references = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "topic_images", joinColumns = @JoinColumn(name = "topic_id"))
    @Column(name = "image_url")
    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();
}
