package com.dangjia.acg.dto.label;

import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2020/2/17
 * Time: 16:31
 */
@Data
public class ChildTags {
    private String id;
    private String subTitle;
    private String parentId;
    private List<ChildChildTags> tagName;
}
