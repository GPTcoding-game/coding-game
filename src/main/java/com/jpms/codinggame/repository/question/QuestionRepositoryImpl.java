package com.jpms.codinggame.repository.question;

import com.jpms.codinggame.entity.Question;
import com.jpms.codinggame.entity.QuestionType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static com.jpms.codinggame.entity.QQuestion.question;

public class QuestionRepositoryImpl implements QuestionRepositoryCustom {

    @Autowired
    private JPAQueryFactory queryFactory;

    @Override
    public int findMaxQuestionNoByDate(LocalDate date) {
        Integer maxQuestionNo = queryFactory
                .select(question.questionNo.max().coalesce(0))
                .from(question)
                .where(question.date.eq(date))
                .fetchOne();

        return maxQuestionNo != null ? maxQuestionNo : 0;
    }

    @Override
    public List<Question> findAllByDate(LocalDate date) {
        return queryFactory
                .selectFrom(question)
                .where(question.date.eq(date))
                .fetch();
    }

    @Override
    public List<Question> findAllByDateAndQTypeByCursor(LocalDate date, String questionType, Long cursor, Long nextCursor) {
        return queryFactory
                .selectFrom(question)
                .where(
                        question.date.eq(date)
                                //QuestionType 자료형으로 변경해야함
                                .and(question.questionType.eq(QuestionType.valueOf(questionType)))
                                //gt == 보다 큰
                                .and(question.id.gt(cursor))
                                //loe == 보다 작은
                                .and(question.id.loe(nextCursor)))
                .orderBy(question.id.asc())
                .fetch();
    }

    @Override
    public List<Question> findAllByDateByCursor(LocalDate date, Long cursor, Long nextCursor) {
        return queryFactory
                .selectFrom(question)
                .where(
                        question.date.eq(date)
                                .and(question.id.gt(cursor))
                                .and(question.id.loe(nextCursor)))
                .orderBy(question.id.asc())
                .fetch();
    }

    @Override
    public List<Question> findAllByQTypeByCursor(String questionType, Long cursor, Long nextCursor) {
        return queryFactory
                .selectFrom(question)
                .where(
                        question.questionType.eq(QuestionType.valueOf(questionType))
                                .and(question.id.gt(cursor))
                                .and(question.id.loe(nextCursor)))
                .orderBy(question.id.asc())
                .fetch();
    }

    @Override
    public List<Question> findAllByCursor(Long cursor, Long nextCursor) {
        return queryFactory
                .selectFrom(question)
                .where(
                        question.id.gt(cursor)
                                .and(question.id.loe(nextCursor)))
                .orderBy(question.id.asc())
                .fetch();
    }
}
