package com.dangjia.acg.service.design;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.design.IHouseStyleTypeMapper;
import com.dangjia.acg.modle.design.HouseStyleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * author: Ronalcheng
 * Date: 2018/11/10 0010
 * Time: 9:34
 */
@Service
public class HouseStyleTypeService {

    @Autowired
    private IHouseStyleTypeMapper houseStyleTypeMapper;

    /**
     * 设计风格列表
     */
    public ServerResponse getStyleList(HttpServletRequest request){
        List<HouseStyleType> houseStyleTypeList = houseStyleTypeMapper.selectAll();
        return ServerResponse.createBySuccess("查询列表成功", houseStyleTypeList);
    }

}
