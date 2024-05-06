package com.jpms.codinggame.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "question")
@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;

    @Column
    private int level;

    @Column
    private String answer;

    @Column
    @Enumerated(EnumType.STRING)
    private LanguageType languageType;

    @Column
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;
}
