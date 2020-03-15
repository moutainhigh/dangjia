package com.dangjia.acg.service.recommend;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.recommend.ILatticeStyleMapper;
import com.dangjia.acg.modle.recommend.LatticeContent;
import com.dangjia.acg.modle.recommend.LatticeStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 方格类型服务类
 * @author: luof
 * @date: 2020-3-13
 */
@Service
public class LatticeStyleService {

    /** 声明日志 */
    private static Logger logger = LoggerFactory.getLogger(LatticeStyleService.class);

    @Autowired
    private ILatticeStyleMapper latticeStyleMapper;

    /**
     * @Description: 查询方格类型列表
     * @author: luof
     * @date: 2020-3-13
     */
    public ServerResponse queryList(){

        List<LatticeStyle> latticeStyleList = latticeStyleMapper.selectAll();

        if ( latticeStyleList == null || latticeStyleList.size() == 0 ) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无数据");
        }
        return ServerResponse.createBySuccess("查询成功", latticeStyleList);
    }
}
