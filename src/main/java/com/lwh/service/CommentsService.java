package com.lwh.service;

import com.lwh.bean.Comments;
import com.lwh.mapper.CommentsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Transactional
@Service
public class CommentsService {

    @Autowired
    CommentsMapper commentsMapper;

    public List<Comments> getCommentsByAid(Long aid,Integer page,Integer count){
        int start = (page - 1) * count;
        return commentsMapper.getCommentsByAid(aid,start,count);
    }

    public int addComment(Comments comments){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        comments.setPublishDate(timestamp);
        int result = commentsMapper.addComment(comments);
        return result;
    }

    public int getCommentCount(Long aid){
        return commentsMapper.getCommentCount(aid);
    }

    public int deleteComment(Long id){
        return commentsMapper.deleteComment(id);
    }

    public List<Comments> getAllComments(String keywords,String keywords1,Integer page,Integer count){
        int start = (page - 1) * count;
        return commentsMapper.getAllComments(keywords,keywords1,start,count);
    }

    public int getAllCommentCount(String keywords,String keywords1){
        return commentsMapper.getAllCommentCount(keywords,keywords1);
    }

    public boolean deleteCommentByIds(String ids){
        String[] split = ids.split(",");
        int result = commentsMapper.deleteCommentByIds(split);
        return result == split.length;
    }

}
