package com.dangjia.acg.service.recommend;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.recommend.ILatticeCodingMapper;
import com.dangjia.acg.modle.recommend.LatticeCoding;
import com.dangjia.acg.modle.recommend.LatticeContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 方格编号服务类
 * @author: luof
 * @date: 2020-3-13
 */
@Service
public class LatticeCodingService {

    /** 声明日志 */
    private static Logger logger = LoggerFactory.getLogger(LatticeCodingService.class);

    @Autowired
    private ILatticeCodingMapper latticeCodingMapper;

    /**
     * @Description: 查询方格编号列表
     * @author: luof
     * @date: 2020-3-13
     */
    public ServerResponse queryList(){

        List<LatticeCoding> latticeCodingList = latticeCodingMapper.selectAll();

        if ( latticeCodingList == null || latticeCodingList.size() == 0 ) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无数据");
        }
        return ServerResponse.createBySuccess("查询成功", latticeCodingList);
    }
}
