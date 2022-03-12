package com.kuang.bbs.entity.vo;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class ArticleCacheVo {
    private String title;
    private String description;
    private String categoryId;
    private String content;
    private List<String> labelList;
}
