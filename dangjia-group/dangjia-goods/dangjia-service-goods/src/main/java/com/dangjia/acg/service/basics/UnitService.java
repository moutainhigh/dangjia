package com.dangjia.acg.service.basics;

import com.alibaba.fastjson.JSON;
import com.dangjia.acg.api.product.MasterProductAPI;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.actuary.IBudgetMaterialMapper;
import com.dangjia.acg.mapper.basics.IProductMapper;
import com.dangjia.acg.mapper.basics.IUnitMapper;
import com.dangjia.acg.mapper.basics.IWorkerGoodsMapper;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.brand.Unit;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

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
    @Autowired
    private IProductMapper iProductMapper;
    @Autowired
    private MasterProductAPI masterProductAPI;
    @Autowired
    private ProductService productService;
    @Autowired
    private IBudgetMaterialMapper iBudgetMaterialMapper;
    @Autowired
    private IWorkerGoodsMapper iWorkerGoodsMapper;

    protected static final Logger LOG = LoggerFactory.getLogger(UnitService.class);

    //查询所有的单位
    public ServerResponse<PageInfo> getAllUnit(PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
            List<Unit> unitList = iUnitMapper.getUnit();
            for (Unit unit : unitList) {
                Map<String, Object> map = new HashMap<String, Object>();
                String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(unit.getCreateDate());
                map.put("id", unit.getId());
                map.put("creatDate", dateStr);
                map.put("name", unit.getName());
                List<Unit> linkUnitList = new ArrayList<>();
                if (unit.getLinkUnitIdArr() != null) {
                    String[] linkUnitIdArr = unit.getLinkUnitIdArr().split(",");
                    for (int i = 0; i < linkUnitIdArr.length; i++) {
                        String linkUnitId = linkUnitIdArr[i];
                        Unit linkUnit = iUnitMapper.selectByPrimaryKey(linkUnitId);
                        linkUnitList.add(linkUnit);
                    }
                }
                map.put("linkUnitList", linkUnitList);
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
    public ServerResponse insert(String unitName, String linkUnitIdArr) {
        try {
            List<Unit> unitList = iUnitMapper.getUnitByName(unitName);
            if (unitList != null && unitList.size() > 0) {
                return ServerResponse.createByErrorMessage("单位名称重复");
            }
            Unit unit = new Unit();
            unit.setName(unitName);

            if (!StringUtils.isNotBlank(linkUnitIdArr))
                unit.setLinkUnitIdArr(unit.getId());//包括本身  如果为null ，就只关联 自己本身
            else
                unit.setLinkUnitIdArr(unit.getId() + "," + linkUnitIdArr);//包括本身

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
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse update(String unitId, String unitName, String linkUnitIdArr)throws RuntimeException {
        try {
            LOG.info("linkUnitIdArr :" + linkUnitIdArr);
            if (!StringUtils.isNotBlank(unitName))
                return ServerResponse.createByErrorMessage("单位名称不能为空");

            Unit unit = iUnitMapper.selectByPrimaryKey(unitId);
            if (unit == null)
                return ServerResponse.createByErrorMessage("不存在此单位,修改失败");

            if (!unit.getName().equals(unitName))//如果修改了名称 就判断，修改的名字 是否已经存在
            {
                if (iUnitMapper.getUnitByName(unitName).size() > 0)
                    return ServerResponse.createByErrorMessage("单位名称已存在");
            }
//            unit.setId(unitId);
            unit.setName(unitName);
            if (!StringUtils.isNotBlank(linkUnitIdArr))
                unit.setLinkUnitIdArr(unit.getId());//包括本身  如果为null ，就只关联 自己本身
            else
                unit.setLinkUnitIdArr(unit.getId() + "," + linkUnitIdArr);//包括本身
            unit.setModifyDate(new Date());
            iUnitMapper.updateByPrimaryKeySelective(unit);
            iProductMapper.updateProductByUnitId(unitName,unitId);
            iWorkerGoodsMapper.updateWorkerGoodsByUnitId(unitId,unitName);
            Example example=new Example(Product.class);
            example.createCriteria().andEqualTo(Product.UNIT_ID,unitId);
            List<Product> products = iProductMapper.selectByExample(example);
            if(products.size()>0||null!=products) {
                iBudgetMaterialMapper.updateBudgetMaterialByUnitName(unitName, products);
                masterProductAPI.updateProductByProductId(JSON.toJSONString(products), null, null, null, null);
            }
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
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
