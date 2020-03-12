package com.dangjia.acg.support.recommend.service;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.modle.recommend.RecommendTargetInfo;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @Description:根据浏览商品-进行推荐
 * @author: luof
 * @date: 2020-3-11
 */
@Service
public class BrowseGoodsRecommend {

    /** 声明日志 */
    private static Logger logger = LoggerFactory.getLogger(BrowseGoodsRecommend.class);

    /** 加载推荐 */
    public ServerResponse loadRecommendTarget(String memberId, PageDTO pageDTO){

        int number = pageDTO.getPageSize();
        logger.debug("需加载[浏览商品]推荐条数["+number+"]");
        if( number < 1 ){
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无数据");
        }

        // 浏览商品推荐 TODO 返回空 暂不处理
        PageInfo pageResult = new PageInfo(new ArrayList<RecommendTargetInfo>());
        return ServerResponse.createBySuccess(pageResult);
    }
}
