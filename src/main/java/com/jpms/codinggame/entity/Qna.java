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
@Table(name = "Qna")
@Entity
public class Qna {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;

    @Column
    private String title;

    @Column
    private LocalDate time;

    @OneToMany(mappedBy = "qna")
    private List<Comment> commentList;

    @ManyToOne
    @JoinColumn(name="question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
}
