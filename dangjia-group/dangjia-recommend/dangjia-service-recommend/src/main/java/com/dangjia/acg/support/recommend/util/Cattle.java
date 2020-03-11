package com.dangjia.acg.support.recommend.util;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.recommend.RecommendTargetInfo;
import com.dangjia.acg.support.recommend.dto.RecommendComposeChunk;
import com.github.pagehelper.PageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 黄牛 干活的
 * @author: luof
 * @date: 2020-3-11
 */
public class Cattle {

    /**
     * @Description: 过滤重复 第一次
     * @author: luof
     * @date: 2020-3-11
     */
    public static void filterRepeatOne(RecommendComposeChunk recommendComposeChunk){

        List<RecommendTargetInfo> targetList1 = recommendComposeChunk.getBrowseGoodsRecommendList();
        List<RecommendTargetInfo> targetList2 = recommendComposeChunk.getBrowseCaseRecommendList();

        int repeatNum = 0;
        List<RecommendTargetInfo> targetList2copy = new ArrayList<RecommendTargetInfo>();

        for( RecommendTargetInfo target1 : targetList1 ){
            for( RecommendTargetInfo target2 : targetList2 ){
                if( target1.getId().equals(target2.getId()) ){
                    repeatNum++;
                }else{
                    targetList2copy.add(target2);
                }
            }
        }

        List<RecommendTargetInfo> totalRecommendList = new ArrayList<RecommendTargetInfo>();
        totalRecommendList.addAll(targetList1);
        totalRecommendList.addAll(targetList2copy);
        recommendComposeChunk.setTotalRecommendList(totalRecommendList);

        repeatNum += recommendComposeChunk.getLabelAttribRecommendNumber();
        recommendComposeChunk.setLabelAttribRecommendNumber(repeatNum);
    }

    /**
     * @Description: 过滤重复 第二次
     * @author: luof
     * @date: 2020-3-11
     */
    public static void filterRepeatTwo(RecommendComposeChunk recommendComposeChunk){

        List<RecommendTargetInfo> targetList1 = recommendComposeChunk.getTotalRecommendList();
        List<RecommendTargetInfo> targetList2 = recommendComposeChunk.getLabelAttribRecommendList();

        List<RecommendTargetInfo> targetList2copy = new ArrayList<RecommendTargetInfo>();

        for( RecommendTargetInfo target1 : targetList1 ){
            for( RecommendTargetInfo target2 : targetList2 ){
                if( !target1.getId().equals(target2.getId()) ){
                    targetList2copy.add(target2);
                }
            }
        }

        recommendComposeChunk.getTotalRecommendList().addAll(targetList2copy);
    }

    /**
     * @Description: 得到条数 - 根据来源
     * @author: luof
     * @date: 2020-3-11
     */
    public static int getNumberBySource(RecommendComposeChunk recommendComposeChunk, int source){
        if( RecommendSource.browse_goods.getCode() == source ){
            return recommendComposeChunk.getBrowseGoodsRecommendNumber();
        }else if( RecommendSource.browse_case.getCode() == source ){
            return recommendComposeChunk.getBrowseCaseRecommendNumber();
        }else if( RecommendSource.label_attrib.getCode() == source ){
            return recommendComposeChunk.getLabelAttribRecommendNumber();
        }
        return 0;
    }

    /**
     * @Description: 加载数据处理
     * @author: luof
     * @date: 2020-3-11
     */
    public static void loadDataHandle(ServerResponse serverResponse,
                                      RecommendComposeChunk recommendComposeChunk,
                                      int source,
                                      int number){
        if( serverResponse.isSuccess() ){
            PageInfo<RecommendTargetInfo> pageResult = (PageInfo)serverResponse.getResultObj();
            if( RecommendSource.browse_goods.getCode() == source ){
                recommendComposeChunk.setBrowseGoodsRecommendList(pageResult.getList());
            }else if( RecommendSource.browse_case.getCode() == source ){
                recommendComposeChunk.setBrowseCaseRecommendList(pageResult.getList());
            }else if( RecommendSource.label_attrib.getCode() == source ){
                recommendComposeChunk.setLabelAttribRecommendList(pageResult.getList());
            }
            if( RecommendSource.browse_goods.getCode() == source || RecommendSource.browse_case.getCode() == source ){
                number -= pageResult.getList().size();
                number += recommendComposeChunk.getLabelAttribRecommendNumber();
                recommendComposeChunk.setLabelAttribRecommendNumber(number);
            }
        }

    }
}
