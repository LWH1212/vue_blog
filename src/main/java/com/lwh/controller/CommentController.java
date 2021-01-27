package com.lwh.controller;

import com.lwh.bean.Comments;
import com.lwh.bean.RespBean;
import com.lwh.bean.User;
import com.lwh.service.CommentsService;
import com.lwh.service.UserService;
import com.lwh.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CommentController {

    @Autowired
    CommentsService commentsService;

    @Autowired
    UserService userService;

    @GetMapping("getComments")
    public Map<String,Object> getComments(@RequestParam("aid") Long aid, @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "count", defaultValue = "5")Integer count){

        int totalCount = commentsService.getCommentCount(aid);
        List<Comments> comments = commentsService.getCommentsByAid(aid, page, count);
        Map<String,Object> map = new HashMap<>();
        map.put("totalCount",totalCount);
        map.put("comments",comments);
        return map;
    }

    @PostMapping("addComment")
    public RespBean addComment(Comments comments){
        User user = userService.getUserById(Util.getCurrentUser().getId());
        if (user.isTalk() == true){
            commentsService.addComment(comments);
            return new RespBean("success","评论成功");
        }else if (user.isTalk() == false){
            return new RespBean("error","你已被禁言");
        }
        return new RespBean("error","评论失败");
    }

    @DeleteMapping("/deleteComment/{id}")
    public RespBean deleteComment(@PathVariable("id") Long id){
        if (commentsService.deleteComment(id) == 1){
            return new RespBean("success","删除成功");
        }
        return new RespBean("error","删除失败");
    }

    @GetMapping("getAllComments")
    public Map<String,Object> getAllComments(String keywords,String keywords1,@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "count", defaultValue = "6")Integer count){

        int totalCount = commentsService.getAllCommentCount(keywords,keywords1);
        List<Comments> comments = commentsService.getAllComments(keywords,keywords1,page, count);
        Map<String,Object> map = new HashMap<>();
        map.put("totalCount",totalCount);
        map.put("comments",comments);
        return map;
    }

    @RequestMapping(value = "/deleteComments/{ids}", method = RequestMethod.DELETE)
    public RespBean deleteCommentById(@PathVariable("ids") String ids) {
        boolean result = commentsService.deleteCommentByIds(ids);
        if (result) {
            return new RespBean("success", "删除成功");
        }
        return new RespBean("error", "删除失败");
    }

}
