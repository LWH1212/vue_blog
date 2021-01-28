package com.lwh.controller.admin;

import com.lwh.bean.Comments;
import com.lwh.bean.RespBean;
import com.lwh.service.CommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/comment")
public class CommentManaController {

    @Autowired
    CommentsService commentsService;

    @GetMapping("/getAllComments")
    public Map<String,Object> getAllComments(String keywords, String keywords1, @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "count", defaultValue = "6")Integer count){

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
