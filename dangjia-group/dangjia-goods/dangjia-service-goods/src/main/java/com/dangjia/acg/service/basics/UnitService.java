package com.dangjia.acg.service.basics;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.basics.IUnitMapper;
import com.dangjia.acg.modle.brand.Unit;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    //查询所有的品牌
    public ServerResponse<PageInfo> getAllUnit(Integer pageNum, Integer pageSize) {
        try {
            if (pageNum == null) {
                pageNum = 1;
            }
            if (pageSize == null) {
                pageSize = 10;
            }
            PageHelper.startPage(pageNum, pageSize);
            List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
            List<Unit> unitList = iUnitMapper.getUnit();
            for (Unit unit : unitList) {
                Map<String, Object> map = new HashMap<String, Object>();
                String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(unit.getCreateDate());
                map.put("id", unit.getId());
                map.put("creatDate", dateStr);
                map.put("name", unit.getName());
                mapList.add(map);
            }
            PageInfo pageResult = new PageInfo(unitList);
            pageResult.setList(mapList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //新增商品单位
    public ServerResponse insert(String unitName) {
        try {
            List<Unit> unitList = iUnitMapper.getUnitByName(unitName);
            if (unitList != null && unitList.size() > 0) {
                return ServerResponse.createByErrorMessage("单位名称重复");
            }
            Unit unit = new Unit();
            unit.setName(unitName);
            unit.setCreateDate(new Date());
            unit.setModifyDate(new Date());
            iUnitMapper.insert(unit);
            return ServerResponse.createBySuccessMessage("新增成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增失败");
        }
    }

    //修改商品单位
    public ServerResponse update(String unitId, String unitName) {
        try {
            List<Unit> unitList = iUnitMapper.getUnitByName(unitName);
            if (unitList != null && unitList.size() > 0) {
                return ServerResponse.createByErrorMessage("单位名称重复");
            }
            Unit unit = new Unit();
            unit.setId(unitId);
            unit.setName(unitName);
            unit.setModifyDate(new Date());
            iUnitMapper.updateByPrimaryKeySelective(unit);
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }

    //
    public ServerResponse selectById(String id) {
        try {
            Unit unit = iUnitMapper.selectByPrimaryKey(id);
            Map<String, Object> map = new HashMap<String, Object>();
            String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(unit.getCreateDate());
            map.put("id", unit.getId());
            map.put("creatDate", dateStr);
            map.put("name", unit.getName());
            return ServerResponse.createBySuccess("查询成功", map);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //根据id删除商品单位
    public ServerResponse deleteById(String unitId) {
        return ServerResponse.createBySuccessMessage("不能执行删除操作");
//        try {
//            iUnitMapper.deleteByPrimaryKey(unitId);
//            return ServerResponse.createBySuccessMessage("删除成功");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ServerResponse.createByErrorMessage("删除失败");
//        }
    }
}
