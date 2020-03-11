package com.dangjia.acg.support.recommend.dto;

import com.dangjia.acg.modle.recommend.RecommendTargetInfo;
import lombok.Data;

import java.util.List;

/**
 * @Description:推荐组合块数量
 * @author: luof
 * @date: 2020-3-10
 */
@Data
public class RecommendComposeChunk {

    /** 浏览商品推荐 */
    private int browseGoodsRecommendNumber;
    private List<RecommendTargetInfo> browseGoodsRecommendList;
    /** 浏览案例推荐 */
    private int browseCaseRecommendNumber;
    private List<RecommendTargetInfo> browseCaseRecommendList;
    /** 属性标签推荐 */
    private int labelAttribRecommendNumber;
    private List<RecommendTargetInfo> labelAttribRecommendList;

    /** 推荐总数 */
    private int recommendTotal;
    private List<RecommendTargetInfo> totalRecommendList;
}
