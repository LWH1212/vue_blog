package com.lwh.controller;

import com.lwh.bean.Comments;
import com.lwh.bean.RespBean;
import com.lwh.service.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CommentController {

    @Autowired
    CommentsService commentsService;

    @GetMapping("getComments/{aid}")
    public List<Comments> getComments(@PathVariable("aid") Long aid){
        return commentsService.getCommentsByAid(aid);
    }

    @PostMapping("addComment")
    public RespBean addComment(Comments comments){
        if (commentsService.addComment(comments) == 1){
            return new RespBean("success","评论成功");
        }
        return new RespBean("error","评论失败");
    }

}
