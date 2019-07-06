package com.dangjia.acg.service.basics;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.basics.IUnitMapper;
import com.dangjia.acg.modle.brand.Unit;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @类 名： UnitServiceImpl.java
 * @功能描述： 商品单位Service实现类
 * @作者信息： hb
 * @创建时间： 2018-9-13下午3:29:07
 */

@Service
public class UnitService {
    /**
     * 注入UnitDao接口
     */
    @Autowired
    private IUnitMapper iUnitMapper;

    //查询所有的单位
    public ServerResponse<PageInfo> getAllUnit(PageDTO pageDTO) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<Unit> unitList = iUnitMapper.getUnit();
        if (unitList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(unitList);
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Unit unit : unitList) {
            Map<String, Object> map = new HashMap<>();
            String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(unit.getCreateDate());
            map.put("id", unit.getId());
            map.put("creatDate", dateStr);
            map.put("name", unit.getName());
            List<Unit> linkUnitList = new ArrayList<>();
            if (unit.getLinkUnitIdArr() != null) {
                String[] linkUnitIdArr = unit.getLinkUnitIdArr().split(",");
                for (String linkUnitId : linkUnitIdArr) {
                    Unit linkUnit = iUnitMapper.selectByPrimaryKey(linkUnitId);
                    linkUnitList.add(linkUnit);
                }
            }
            map.put("linkUnitList", linkUnitList);
            mapList.add(map);
        }
        pageResult.setList(mapList);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    //新增商品单位
    public ServerResponse insert(String unitName, String linkUnitIdArr) {
        List<Unit> unitList = iUnitMapper.getUnitByName(unitName);
        if (unitList != null && unitList.size() > 0) {
            return ServerResponse.createByErrorMessage("单位名称重复");
        }
        Unit unit = new Unit();
        unit.setName(unitName);
        if (CommonUtil.isEmpty(linkUnitIdArr))
            unit.setLinkUnitIdArr(unit.getId());
        else
            unit.setLinkUnitIdArr(unit.getId() + "," + linkUnitIdArr);//包括本身
        iUnitMapper.insert(unit);
        return ServerResponse.createBySuccessMessage("新增成功");
    }

    //修改商品单位
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse update(String unitId, String unitName, String linkUnitIdArr) {
        if (CommonUtil.isEmpty(unitName))
            return ServerResponse.createByErrorMessage("单位名称不能为空");
        Unit unit = iUnitMapper.selectByPrimaryKey(unitId);
        if (unit == null)
            return ServerResponse.createByErrorMessage("该单位不存在");
        if (!unit.getName().equals(unitName)) {
            if (iUnitMapper.getUnitByName(unitName).size() > 0)
                return ServerResponse.createByErrorMessage("单位名称已存在");
        }
        unit.setName(unitName);
        if (CommonUtil.isEmpty(linkUnitIdArr))
            unit.setLinkUnitIdArr(unit.getId());//包括本身  如果为null ，就只关联 自己本身
        else
            unit.setLinkUnitIdArr(unit.getId() + "," + linkUnitIdArr);//包括本身
        unit.setModifyDate(new Date());
        iUnitMapper.updateByPrimaryKeySelective(unit);
        return ServerResponse.createBySuccessMessage("修改成功");
    }

    public ServerResponse getUnitById(String id) {
        Unit unit = iUnitMapper.selectByPrimaryKey(id);
        if (unit == null) {
            return ServerResponse.createByErrorMessage("该单位不存在");
        }
        return ServerResponse.createBySuccess("查询成功", unit);
    }

}
