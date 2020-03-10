package com.dangjia.acg.service.recommend;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.recommend.IRecommendItemMapper;
import com.dangjia.acg.modle.recommend.RecommendItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 推荐参考主项服务类
 * @author: luof
 * @date: 2020-3-7
 */
@Service
public class RecommendItemService {

    /** 声明日志 */
    private static Logger logger = LoggerFactory.getLogger(RecommendItemService.class);

    @Autowired
    private IRecommendItemMapper recommendItemMapper;

    /**
     * @Description: 查询推荐参考主项列表
     * @author: luof
     * @date: 2020-3-7
     */
    public ServerResponse queryList(String itemName) {

        try {
            List<RecommendItem> recommendItemList = recommendItemMapper.queryList(itemName);

            if (recommendItemList == null || recommendItemList.size() == 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无数据");
            }

            return ServerResponse.createBySuccess("查询成功", recommendItemList);
        }catch(Exception e){
            logger.error("",e);
            return ServerResponse.createByErrorMessage("查询异常");
        }

    };
}
