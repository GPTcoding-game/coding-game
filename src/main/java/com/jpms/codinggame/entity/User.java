package com.jpms.codinggame.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;
import java.util.Set;

@Getter
@Entity
@Table(name = "User")
@SQLRestriction("is_deleted = false")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE User SET is_deleted = true where id = ?")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(unique = true)
    private String userName;

//    @Column(unique = true)
    private String nickName;

    @Column
    @Enumerated(EnumType.STRING)
    private Tier tier;


    @Column
    private String password;

//    @Column(unique = true , nullable = true)
    private String email;

    @Column //누적점수
    private int totalScore;

    @Column
    private boolean isDone;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private String address;

    @Column
    private String provider;

    @Column
    private String picture;

    @Column
    private boolean isDeleted;

//    @Column
//    private String providerId;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Qna> qnaList;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Comment> commentList;

    @ManyToMany
    @JoinTable(
            name = "incorrect_question",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Question> questionList;

    public void updateQuestionList(List<Question> questionList){
        this.questionList = questionList;
    }

    public void updateIsDone(boolean isDone){
        this.isDone = isDone;
    }

    public void updateTotalScore(int totalScore){
        this.totalScore = totalScore;
    }

    public void updateTier(Tier tier) {this.tier = tier;}

    public void updateInfo(String password, String nickName, String address) {
        this.password = password;
        this.nickName = nickName;
        this.address = address;
    }

    public void updateTempPassword(String tempPassword){
        this.password = tempPassword;
    }

    public void addInfo(String nickName, String address){ this.nickName = nickName; this.address = address;}

    public void deleteUser(boolean isDeleted){
        this.isDeleted =isDeleted;
    }
}
