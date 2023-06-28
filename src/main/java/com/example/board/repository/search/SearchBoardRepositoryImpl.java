package com.example.board.repository.search;

import com.example.board.entity.Board;
import com.example.board.entity.QBoard;
import com.example.board.entity.QMember;
import com.example.board.entity.QReply;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class SearchBoardRepositoryImpl extends QuerydslRepositorySupport implements SearchBoardRepository {
    public SearchBoardRepositoryImpl(){
        super(Board.class);
    }

    @Override
    public Board search1(){
        log.info("search1.............");

        QBoard board = QBoard.board;
        QReply reply = QReply.reply;
        QMember member = QMember.member;

        JPQLQuery<Board> jpqlQuery = from(board);
        jpqlQuery.leftJoin(member).on(board.writer.eq(member));
        jpqlQuery.leftJoin(reply).on(reply.board.eq(board));

        JPQLQuery<Tuple> tuple = jpqlQuery.select(board, member.email, reply.count());
        tuple.groupBy(board);
//        jpqlQuery.select(board, member.email, reply.count());

//        jpqlQuery.select(board).where(board.bno.eq(1L));

        log.info("-----------------------");
        log.info(tuple);
        log.info("-----------------------");

        List<Tuple> result = tuple.fetch();
        log.info(result);
        return null;
    }

    @Override
    public Page<Object[]> searchPage(String type, String keyword, Pageable pageable){
        log.info("searchPage..............");

        QBoard board = QBoard.board;
        QReply reply = QReply.reply;
        QMember member = QMember.member;

        JPQLQuery<Board> jpqlQuery = from(board);
        jpqlQuery.leftJoin(member).on(board.writer.eq(member));
        jpqlQuery.leftJoin(reply).on(reply.board.eq(board));

        JPQLQuery<Tuple> tuple = jpqlQuery.select(board, member.email, reply.count());

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        BooleanExpression expression = board.bno.gt(0L);

        booleanBuilder.and(expression);

        if(type != null){
            String[] typeArr = type.split("");
            //검색 조건 작성하기
            BooleanBuilder conditionBuilder = new BooleanBuilder();

            for (String t:typeArr){
                switch (t){
                    case "t" : conditionBuilder.or(board.title.contains(keyword));
                    break;
                    case "w" : conditionBuilder.or(member.email.contains(keyword));
                    break;
                    case "c" : conditionBuilder.or(board.content.contains(keyword));
                    break;
                }
            }
            booleanBuilder.and(conditionBuilder);
        }
        tuple.where(booleanBuilder);

        tuple.groupBy(board);

        this.getQuerydsl().applyPagination(pageable, tuple);

        List<Tuple> result = tuple.fetch();

        log.info(result);

        long count = tuple.fetchCount();

        log.info("count  : " + count);

        return new PageImpl<Object[]>(
                result.stream().map(Tuple::toArray).collect(Collectors.toList()),pageable,count);
    }

}
