package com.lwh.bean;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Comments {

    private Long id;

    private Long aid;

    private String content;

    private Timestamp publishDate;

    private Long uid;

    private String userface;

    private String nickname;

    private String username;

    private String email;

    private String title;

}
