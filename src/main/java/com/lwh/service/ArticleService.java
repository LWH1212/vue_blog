package com.lwh.service;

import com.lwh.bean.Article;
import com.lwh.mapper.ArticleMapper;
import com.lwh.mapper.TagsMapper;
import com.lwh.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class ArticleService {

    @Autowired
    ArticleMapper articleMapper;

    @Autowired
    TagsMapper tagsMapper;

    public int addnewArticle(Article article){
        //处理文章摘要
        if (article.getSummary() == null || "".equals(article.getSummary())){
            //直接截取
            String stripHtml = stripHtml(article.getHtmlContent());
            article.setSummary(stripHtml.substring(0,stripHtml.length()>50?50:stripHtml.length()));
        }
        if (article.getId() == -1){
            //添加操作
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            if (article.getState() == 1){
                //设置发表日期
                article.setPublishDate(timestamp);
            }
            article.setEditTime(timestamp);
            //设置当前用户
            article.setUid(Util.getCurrentUser().getId());
            int i = articleMapper.addNewArticle(article);
            //打标签
            String[] dynamicTags = article.getDynamicTags();
            if (dynamicTags != null && dynamicTags.length>0){
                int tags = addTagsToArticle(dynamicTags,article.getId());
                if (tags == -1){
                    return tags;
                }
            }
            return i;
        }else {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            if (article.getState() == 1){
                //设置发表日期
                article.setPublishDate(timestamp);
            }
            //更新
            article.setEditTime(new Timestamp(System.currentTimeMillis()));
            int i = articleMapper.updateArticle(article);
            //修改标签
            String[] dynamicTags = article.getDynamicTags();
            if (dynamicTags != null && dynamicTags.length > 0){
                int tags = addTagsToArticle(dynamicTags, article.getId());
                if (tags == -1){
                    return tags;
                }
            }
            return i;
        }

    }

    private int addTagsToArticle(String[] dynamicTags, Long aid) {

        //1.删除该文章目前所有的标签
        tagsMapper.deleteTagsByAid(aid);
        //2.将上传来的标签全部存入数据库
        tagsMapper.saveTags(dynamicTags);
        //3.查询这些标签的id
        List<Long> tIds = tagsMapper.getTagsIdByTagName(dynamicTags);
        //4.重新给文章设置标签
        int i = tagsMapper.saveTags2ArticleTags(tIds,aid);
        return i == dynamicTags.length? i : -1;

    }

    private String stripHtml(String content) {

        content = content.replaceAll("<p .*?>","");
        content = content.replaceAll("<br\\s*/?>","");
        content = content.replaceAll("\\<.*?>","");
        return content;

    }

    public List<Article> getArticleByState(Integer state,Integer page,Integer count,String keywords){
        int start = (page - 1) * count;
        Long uid = Util.getCurrentUser().getId();
        return articleMapper.getArticleByState(state,start,count,uid,keywords);
    }

    public int getArticleCountByState(Integer state,Long uid,String keywords){
        return articleMapper.getArticleCountByState(state, uid, keywords);
    }

    public int updateArticleState(Long[] aids,Integer state){
        if (state == 2){
            return articleMapper.deleteArticleById(aids);
        }else {
            return articleMapper.updateArticleState(aids,2);//放入到回收站
        }
    }

    public int restoreArticle(Integer articleId){
        return articleMapper.updateArticleStateById(articleId,1);
    }

    public int restoreMany(Long[] aids){
        return articleMapper.restoreMany(aids);
    }

    public Article getArticleById(Long aid){
        Article article = articleMapper.getArticleById(aid);
        articleMapper.pvIncrement(aid);
        return article;
    }

    public void pvStatisticsPerDay(){
        articleMapper.pvStatisticsPerDay();
    }

    //获取最近七天的日期
    public List<String> getCategories(){
        return articleMapper.getCategories(Util.getCurrentUser().getId());
    }

    //获取最近七天的数据
    public List<Integer> getDataStatistics(){
        return articleMapper.getDataStatistics(Util.getCurrentUser().getId());
    }

}
