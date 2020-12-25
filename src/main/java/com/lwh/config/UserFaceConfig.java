package com.lwh.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class UserFaceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**").addResourceLocations("file:D:/lwh/vue_blog/src/main/resources/static/img/");
        registry.addResourceHandler("/blogimg/**").addResourceLocations("file:D:/lwh/vue_blog/src/main/resources/static/blogimg/");
    }
}
