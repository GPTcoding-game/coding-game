package com.jpms.codinggame.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.util.List;


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
    private LocalDate date;

    @Column
    private int questionNo;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String choice;

    @Column
    private int level;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column
    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    @OneToMany(mappedBy = "question")
    private List<Qna> qnaList;

    @ManyToOne
    @JoinColumn(name ="user_id")
    private User user;
}
