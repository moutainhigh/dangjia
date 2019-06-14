package com.dangjia.acg.dto.house;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/6/13
 * Time: 9:34
 */
@Data
public class TextContentDTO {
    private String headline;//二级页面标题
    private String [] image;//二级页面图片
    private String [] imageUrl;//二级页面半路劲
    private String describe;//二级页面图片描述
}
