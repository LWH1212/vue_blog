package com.lwh.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TagsMapper {

    int deleteTagsByAid(Long aid);

    int saveTags(@Param("tags") String[] tags);

    int saveTags1(@Param("tags") String tags);

    List<Long> getTagsIdByTagName(@Param("tagNames") String[] tagNames);

    int saveTags2ArticleTags(@Param("tagIds") List<Long> tagIds,@Param("aid") Long aid);

    @Select("select tagName from tags")
    List<String> getTagsName();

}