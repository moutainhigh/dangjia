package com.dangjia.acg.service.recommend;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.recommend.ILatticeContentTypeMapper;
import com.dangjia.acg.modle.recommend.LatticeContent;
import com.dangjia.acg.modle.recommend.LatticeContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 方格内容类型服务类
 * @author: luof
 * @date: 2020-3-13
 */
@Service
public class LatticeContentTypeService {

    /** 声明日志 */
    private static Logger logger = LoggerFactory.getLogger(LatticeContentTypeService.class);

    @Autowired
    private ILatticeContentTypeMapper latticeContentTypeMapper;

    /**
     * @Description: 查询方格内容类型列表
     * @author: luof
     * @date: 2020-3-13
     */
    public ServerResponse queryList(){

        List<LatticeContentType> latticeContentTypeList = latticeContentTypeMapper.selectAll();

        if ( latticeContentTypeList == null || latticeContentTypeList.size() == 0 ) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无数据");
        }
        return ServerResponse.createBySuccess("查询成功", latticeContentTypeList);
    }
}
