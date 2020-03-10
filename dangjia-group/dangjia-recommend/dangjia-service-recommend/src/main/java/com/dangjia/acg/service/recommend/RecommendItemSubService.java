package com.dangjia.acg.service.recommend;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.recommend.IRecommendItemSubMapper;
import com.dangjia.acg.modle.recommend.RecommendItemSub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 推荐参考子项服务类
 * @author: luof
 * @date: 2020-3-9
 */
@Service
public class RecommendItemSubService {

    /** 声明日志 */
    private static Logger logger = LoggerFactory.getLogger(RecommendItemSubService.class);

    @Autowired
    private IRecommendItemSubMapper recommendItemSubMapper;

    /**
     * @Description: 查询推荐参考子项列表
     * @author: luof
     * @date: 2020-3-9
     */
    public ServerResponse queryList(String itemId, String itemSubName) {

        if( null == itemId || itemId.equals("") ){
            return ServerResponse.createByErrorMessage("参数[主项id]为空!");
        }

        List<RecommendItemSub> recommendItemSubList =  recommendItemSubMapper.queryList(itemId, itemSubName);

        if ( recommendItemSubList == null || recommendItemSubList.size() == 0 ) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无数据");
        }
        return ServerResponse.createBySuccess("查询成功", recommendItemSubList);
    }
}
