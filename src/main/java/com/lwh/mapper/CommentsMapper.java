package com.lwh.mapper;

import com.lwh.bean.Comments;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CommentsMapper {

    List<Comments> getCommentsByAid(Long aid);

    @Insert("insert into comments(aid,content,publishDate,uid) values(#{aid},#{content},#{publishDate},#{uid})")
    int addComment(Comments comments);

}
