package com.lwh.mapper;

import com.lwh.bean.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CategoryMapper {

    List<Category> getCategories();

    List<Category> getAllCategories(@Param("start") Integer start,@Param("count") Integer count);

    int deleteCategoryByIds(@Param("ids") String[] ids);

    int updateCategoryById(Category category);

    int addCategory(Category category);

    int getCategoryCount();
}
