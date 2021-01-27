package com.lwh.service;

import com.lwh.bean.Category;
import com.lwh.bean.RespBean;
import com.lwh.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

@Transactional
@Service
public class CategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    public List<Category> getCategories(){
        return categoryMapper.getCategories();
    }

    public List<Category> getAllCategories( Integer page,  Integer count){
        int start = (page - 1) * count;
        return categoryMapper.getAllCategories(start, count);
    }

    public boolean deleteCategoryByIds(String ids){
        String[] split = ids.split(",");
        int result = categoryMapper.deleteCategoryByIds(split);
        return  result == split.length;
    }

    public int updateCategoryById(Category category){
        return categoryMapper.updateCategoryById(category);
    }

    public int addCategory(Category category){
        category.setDate(new Timestamp(System.currentTimeMillis()));
        return categoryMapper.addCategory(category);
    }

    public int getCategoryCount() {
        return categoryMapper.getCategoryCount();
    }
}
