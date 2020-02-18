package com.dangjia.acg.dto.label;

import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2020/2/17
 * Time: 15:57
 */
@Data
public class OptionalLaelDetail {
   private String id;
   private String topTitle;
   private List<ChildTags> labels;
}
