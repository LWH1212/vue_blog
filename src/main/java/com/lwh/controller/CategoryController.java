package com.lwh.controller;

import com.lwh.bean.Category;
import com.lwh.bean.RespBean;
import com.lwh.service.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @RequestMapping(value = "getCategories",method = RequestMethod.GET)
    public List<Category> getCategories(){
        return categoryService.getCategories();
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Map<String,Object> getAllCategories(@RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "count", defaultValue = "6") Integer count) {
        int totalCount = categoryService.getCategoryCount();
        List<Category> categories =  categoryService.getAllCategories(page, count);
        Map<String,Object> map = new HashMap<>();
        map.put("totalCount",totalCount);
        map.put("categories",categories);
        return map;
    }

    @RequestMapping(value = "/{ids}", method = RequestMethod.DELETE)
    public RespBean deleteById(@PathVariable("ids") String ids) {
        boolean result = categoryService.deleteCategoryByIds(ids);
        if (result) {
            return new RespBean("success", "删除成功");
        }
        return new RespBean("error", "删除失败");
    }

    @RequestMapping(value = "/",method = RequestMethod.POST)
    public RespBean addNewCate(Category category){
        if ("".equals(category.getCateName()) || category.getCateName() == null){
            return new RespBean("error","请输入栏目名称！");
        }
        int result = categoryService.addCategory(category);
        if (result == 1){
            return new RespBean("success","添加成功");
        }
        return new RespBean("error","添加失败");
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public RespBean updateCate(Category category){
        int i = categoryService.updateCategoryById(category);
        if (i == 1){
            return new RespBean("success","修改成功");
        }
        return new RespBean("error","修改失败");
    }


}
