package com.lwh.mapper;

import com.lwh.bean.Comments;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CommentsMapper {

    List<Comments> getCommentsByAid(Long aid, @Param("start") Integer start, @Param("count") Integer count);

    @Insert("insert into comments(aid,content,publishDate,uid) values(#{aid},#{content},#{publishDate},#{uid})")
    int addComment(Comments comments);

    int getCommentCount(@Param("aid") Long aid);

    int deleteComment(Long id);

    List<Comments> getAllComments(@Param("keywords") String keywords,@Param("keywords1") String keywords1,@Param("start") Integer start, @Param("count") Integer count);

    int getAllCommentCount(@Param("keywords") String keywords,@Param("keywords1") String keywords1);

    int deleteCommentByIds(@Param("ids") String[] ids);

}
