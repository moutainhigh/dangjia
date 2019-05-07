package com.dangjia.acg.service.design;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.mapper.design.IHouseStyleTypeMapper;
import com.dangjia.acg.modle.design.HouseStyleType;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
     * 获取风格
     */
    public HouseStyleType getStyleByName(String style) {
        return houseStyleTypeMapper.getStyleByName(style);
    }

    /**
     * 设计风格列表
     */
    public ServerResponse getStyleList(HttpServletRequest request, PageDTO pageDTO) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<HouseStyleType> houseStyleTypeList = houseStyleTypeMapper.selectAll();
        PageInfo pageResult = new PageInfo(houseStyleTypeList);
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (HouseStyleType houseStyleType : houseStyleTypeList) {
            Map<String, Object> map = BeanUtils.beanToMap(houseStyleType);
            mapList.add(map);
        }
        pageResult.setList(mapList);
        return ServerResponse.createBySuccess("查询列表成功", pageResult);
    }

    /**
     * 添加风格
     *
     * @param request
     * @param name
     * @param price
     * @return
     */
    public ServerResponse addStyle(HttpServletRequest request, String name, String price) {
        try {
            if (houseStyleTypeMapper.getStyleByName(name) != null)
                return ServerResponse.createByErrorMessage("风格名称已存在");
            HouseStyleType houseStyleType = new HouseStyleType();
            houseStyleType.setName(name);
            houseStyleType.setPrice(new BigDecimal(price));
            houseStyleTypeMapper.insert(houseStyleType);
            return ServerResponse.createBySuccessMessage("新增成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增失败");
        }
    }

    /**
     * 修改风格
     *
     * @param request
     * @param id
     * @param name
     * @param price
     * @return
     */
    public ServerResponse updataStyle(HttpServletRequest request, String id, String name, String price) {
        try {
            HouseStyleType houseStyleType = houseStyleTypeMapper.selectByPrimaryKey(id);
            if (houseStyleType == null)
                return ServerResponse.createByErrorMessage("没有该风格");
            if (!houseStyleType.getName().equals(name)) {
                if (houseStyleTypeMapper.getStyleByName(name) != null)
                    return ServerResponse.createByErrorMessage("风格名称已存在");
            }
            houseStyleType.setName(name);
            houseStyleType.setPrice(new BigDecimal(price));
            houseStyleType.setModifyDate(new Date());
            houseStyleTypeMapper.updateByPrimaryKeySelective(houseStyleType);
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }

}
