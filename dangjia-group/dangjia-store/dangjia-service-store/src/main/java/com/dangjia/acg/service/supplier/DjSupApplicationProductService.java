package com.dangjia.acg.service.supplier;

import cn.jiguang.common.utils.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.delivery.SupplyDimensionDTO;
import com.dangjia.acg.dto.sup.SupplierDTO;
import com.dangjia.acg.dto.supplier.DjSupSupplierProductDTO;
import com.dangjia.acg.dto.supplier.DjSupplierDTO;
import com.dangjia.acg.mapper.supplier.*;
import com.dangjia.acg.modle.supplier.DjAdjustRecord;
import com.dangjia.acg.modle.supplier.DjSupApplicationProduct;
import com.dangjia.acg.modle.supplier.DjSupplier;
import com.dangjia.acg.sql.config.DruidConfig;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 10/10/2019
 * Time: 下午 3:46
 */
@Service
public class DjSupApplicationProductService {

    @Autowired
    private DjSupApplicationMapper djSupApplicationMapper;
    @Autowired
    private DjSupApplicationProductMapper djSupApplicationProductMapper;
    @Autowired
    private DjSupSupplierProductMapper djSupSupplierProductMapper;
    @Autowired
    private DjAdjustRecordMapper djAdjustRecordMapper;
    private Logger logger = LoggerFactory.getLogger(DjSupApplicationProductService.class);

    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private DjSupplierMapper djSupplierMapper;

    /**
     * 供应商申请供应商品
     *
     * @param jsonStr
     * @return
     */
    public ServerResponse insertDjSupApplicationProduct(String jsonStr, String cityId, String supId, String shopId) {
        try {
            JSONArray jsonArr = JSONArray.parseArray(jsonStr);
            jsonArr.forEach(str -> {
                JSONObject obj = (JSONObject) str;
                DjSupApplicationProduct djSupApplicationProduct = new DjSupApplicationProduct();
                djSupApplicationProduct.setDataStatus(0);
                djSupApplicationProduct.setSupId(supId);
                djSupApplicationProduct.setShopId(shopId);
                djSupApplicationProduct.setProductId(obj.getString("productId"));
                djSupApplicationProduct.setPrice(obj.getDouble("price"));
                djSupApplicationProduct.setStock(obj.getInteger("stock"));
                djSupApplicationProduct.setPorterage(obj.getDouble("porterage"));
                djSupApplicationProduct.setIsCartagePrice(obj.getString("isCartagePrice"));
                djSupApplicationProduct.setSupplyRelationShip("0");
                djSupApplicationProduct.setApplicationStatus("0");
                djSupApplicationProduct.setGoodsId(obj.getString("goodsId"));
                djSupApplicationProduct.setCityId(cityId);
                djSupApplicationProductMapper.insert(djSupApplicationProduct);
            });
            return ServerResponse.createBySuccessMessage("申请成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("申请失败");
        }
    }

    /**
     * 查询已供商品
     *
     * @param supId
     * @param shopId
     * @return
     */
    public Integer queryHaveGoodsSize(String supId, String shopId, String applicationStatus) {
        try {
            List<DjSupSupplierProductDTO> list = djSupSupplierProductMapper.queryHaveGoods(supId, shopId, applicationStatus);
            if (list != null) {
                return list.size();
            } else {
                return 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 查询已供商品
     *
     * @param supId
     * @param shopId
     * @return
     */
    public ServerResponse queryHaveGoods(String supId, String shopId, String applicationStatus, PageDTO pageDTO) {
        try {
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DjSupSupplierProductDTO> djSupSupplierProductDTOS = djSupSupplierProductMapper.queryHaveGoods(supId, shopId, applicationStatus);
            djSupSupplierProductDTOS.forEach(djSupSupplierProductDTO -> {
                djSupSupplierProductDTO.setImage(imageAddress + djSupSupplierProductDTO.getImage());
            });
            PageInfo pageResult = new PageInfo(djSupSupplierProductDTOS);
            if (djSupSupplierProductDTOS.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询已供商品失败：" + e);
        }
    }


    /**
     * 编辑已供商品
     *
     * @param jsonStr
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse updateHaveGoods(String jsonStr,String userId) {
        try {
            JSONArray jsonArr = JSONArray.parseArray(jsonStr);
            jsonArr.forEach(str -> {
                JSONObject obj = (JSONObject) str;
                String applicationProductId = obj.getString("id");//供应商品表id
                Double price = obj.getDouble("price");//供应价
                Double porterage = obj.getDouble("porterage");//搬运费
                Double adjustPrice = obj.getDouble("adjustPrice");//调后价
                Date adjustTime = obj.getDate("adjustTime");//调价时间
                String isCartagePrice = obj.getString("isCartagePrice");//是否收取上楼费 0=否，1=是
                String supplyRelationship = obj.getString("supplyRelationship");//供应关系 0:供应 1:停供
                DjSupApplicationProduct djSupApplicationProduct = new DjSupApplicationProduct();
                djSupApplicationProduct.setId(applicationProductId);
                djSupApplicationProduct.setPrice(price);
                djSupApplicationProduct.setPorterage(porterage);
                djSupApplicationProduct.setIsCartagePrice(isCartagePrice);
                djSupApplicationProduct.setSupplyRelationShip(supplyRelationship);
                djSupApplicationProductMapper.updateByPrimaryKeySelective(djSupApplicationProduct);
                DjAdjustRecord djAdjustRecord = new DjAdjustRecord();
                djAdjustRecord.setAdjustPrice(adjustPrice);
                djAdjustRecord.setAdjustTime(adjustTime);
                djAdjustRecord.setApplicationProductId(applicationProductId);
                djAdjustRecord.setUserId(userId);
                djAdjustRecord.setOriginalCost(price);
                djAdjustRecordMapper.insert(djAdjustRecord);
            });
            return ServerResponse.createBySuccessMessage("编辑成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("编辑失败：" + e);
        }
    }

    /**
     * 查询待审核的供应商品
     *
     * @param applicationStatus
     * @param shopId
     * @param keyWord
     * @return
     */
    public ServerResponse getExaminedProduct(PageDTO pageDTO, String applicationStatus, String shopId, String keyWord) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DjSupplierDTO> list = djSupplierMapper.queryDjSupplierByShopID(keyWord, applicationStatus, shopId);
            if (list.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 店铺-审核供货列表-已供商品
     *
     * @param supId
     * @param shopId
     * @param applicationStatus
     * @return
     */
    public ServerResponse getSuppliedProduct(String supId, String shopId, String applicationStatus) {
        try {
            List<DjSupSupplierProductDTO> djSupSupplierProductList = djSupSupplierProductMapper.queryHaveGoods(supId, shopId, applicationStatus);
            if (djSupSupplierProductList.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            return ServerResponse.createBySuccess("查询成功", djSupSupplierProductList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询店铺-审核供货列表-已供商品失败");
        }
    }


    /**
     * 店铺-审核供货列表-全部打回
     *
     * @param id
     * @return
     */
    public ServerResponse rejectAllProduct(String id) {
        try {
            if (StringUtils.isEmpty(id)) {
                return ServerResponse.createByErrorMessage("供应商的商品id不能为空");
            }
            String[] iditem = id.split(",");
            Example example = new Example(DjSupApplicationProduct.class);
            example.createCriteria().andIn(DjSupApplicationProduct.ID, Arrays.asList(iditem));
            DjSupApplicationProduct djSupApplicationProduct = new DjSupApplicationProduct();
            djSupApplicationProduct.setId(null);
            djSupApplicationProduct.setCreateDate(null);
            djSupApplicationProduct.setApplicationStatus("2");
            int i = djSupApplicationProductMapper.updateByExampleSelective(djSupApplicationProduct, example);
            if (i <= 0) {
                return ServerResponse.createBySuccessMessage("全部打回失败");
            }
            return ServerResponse.createBySuccessMessage("全部打回成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("店铺-审核供货列表-全部打回失败");
        }
    }


    /**
     * 店铺-审核供货列表-部分通过
     *
     * @param id
     * @return
     */
    public ServerResponse rejectPartProduct(String id) {
        try {
            if (StringUtils.isEmpty(id)) {
                return ServerResponse.createByErrorMessage("供应商的商品id不能为空");
            }
            String[] iditem = id.split(",");
            Example example = new Example(DjSupApplicationProduct.class);
            example.createCriteria().andIn(DjSupApplicationProduct.ID, Arrays.asList(iditem));
            DjSupApplicationProduct djSupApplicationProduct = new DjSupApplicationProduct();
            djSupApplicationProduct.setId(null);
            djSupApplicationProduct.setCreateDate(null);
            djSupApplicationProduct.setApplicationStatus("2");
            int i = djSupApplicationProductMapper.updateByExampleSelective(djSupApplicationProduct, example);
            if (i <= 0) {
                return ServerResponse.createByErrorMessage("审核供货列表不通过");
            }
            return ServerResponse.createBySuccessMessage("审核供货列表通过");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("审核供货列表异常");
        }
    }


    /**
     * 根据供应商id查询供应商商品
     *
     * @param supId
     * @param searchKey
     * @return
     */
    public List<SupplyDimensionDTO> queryDjSupSupplierProductList(String supId, String searchKey) {
        try {
            List<SupplyDimensionDTO> supplyDimensionDTOS = djSupSupplierProductMapper.queryDjSupSupplierProductList(supId, searchKey);
            return supplyDimensionDTOS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 查询未供商品
     *
     * @param supId
     * @param shopId
     * @return
     */
    public ServerResponse queryNotForTheGoods(String supId, String shopId, PageDTO pageDTO) {
        try {
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            List<DjSupSupplierProductDTO> djSupSupplierProductDTOS = djSupSupplierProductMapper.queryHaveGoods(supId, shopId, "1,2");
            //Stream表达式取出已选商品的id
            List<String> productIds = djSupSupplierProductDTOS.stream()
                    .map(DjSupSupplierProductDTO::getProductId)
                    .collect(Collectors.toList());
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DjSupSupplierProductDTO> djSupSupplierProductDTOS1 = djSupSupplierProductMapper.queryNotForTheGoods(shopId, productIds);
            djSupSupplierProductDTOS1.forEach(djSupSupplierProductDTO -> {
                djSupSupplierProductDTO.setImage(imageAddress + djSupSupplierProductDTO.getImage());
            });
            if (djSupSupplierProductDTOS1.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            PageInfo pageResult = new PageInfo(djSupSupplierProductDTOS1);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("查询失败", e);
            return ServerResponse.createByErrorMessage("查询失败" + e);
        }
    }


    /**
     * 被打回商品申请
     *
     * @param jsonStr
     * @return
     */
    public ServerResponse updateReapply(String jsonStr) {
        try {
            JSONArray jsonArr = JSONArray.parseArray(jsonStr);
            jsonArr.forEach(str -> {
                JSONObject obj = (JSONObject) str;
                DjSupApplicationProduct djSupApplicationProduct = new DjSupApplicationProduct();
                djSupApplicationProduct.setId(obj.getString("id"));//供应商申请供应商品表id
                djSupApplicationProduct.setPrice(obj.getDouble("price"));//供应价
                djSupApplicationProduct.setPorterage(obj.getDouble("porterage"));//搬运费
                djSupApplicationProduct.setIsCartagePrice(obj.getString("isCartagePrice"));//是否收取上楼费 0=否，1=是
                djSupApplicationProduct.setApplicationStatus("0");
                djSupApplicationProductMapper.updateByPrimaryKeySelective(djSupApplicationProduct);
            });
            return ServerResponse.createBySuccessMessage("申请成功");
        } catch (Exception e) {
            logger.info("申请失败", e);
            return ServerResponse.createByErrorMessage("申请失败");
        }
    }


    /**
     * 商品调价定时任务
     * @return
     */
    public void setCommodityPricing() {
        djSupApplicationProductMapper.setCommodityPricing();
    }

    /**
     * 发货任务-新版查询供应商
     * @param cityId
     * @param productId
     * @return
     */
    public ServerResponse supplierList(String cityId, String productId) {
        try {
             List<DjSupApplicationProduct> djSupApplicationProductList = djSupApplicationProductMapper.querySupplierProduct(null, productId);
            if (djSupApplicationProductList.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            List<SupplierDTO> supplierDTOList = new ArrayList<SupplierDTO>();
            for (DjSupApplicationProduct djSupApplicationProduct : djSupApplicationProductList) {
                DjSupplier supplier = djSupplierMapper.selectByPrimaryKey(djSupApplicationProduct.getSupId());
                if (supplier != null) {
                    SupplierDTO supplierDTO = new SupplierDTO();
                    supplierDTO.setSupplierId(djSupApplicationProduct.getSupId());
                    supplierDTO.setSupplierPrice(djSupApplicationProduct.getPrice());//供应价
                    supplierDTO.setName(supplier.getName());
                    supplierDTOList.add(supplierDTO);
                }
            }
            return ServerResponse.createBySuccess("查询成功", supplierDTOList);
        } catch (Exception e) {
            logger.info("新版查询供应商异常", e);
            return ServerResponse.createByErrorMessage("新版查询供应商异常");
        }
    }

    /**
     * 查询供应商商品
     * @param cityId
     * @param supplierId
     * @param productId
     * @return
     */
    public DjSupApplicationProduct getDjSupApplicationProduct(String cityId, String supplierId, String productId) {
        try {
            Example example = new Example(DjSupApplicationProduct.class);
            example.createCriteria().andEqualTo(DjSupApplicationProduct.SUP_ID, supplierId).andEqualTo(DjSupApplicationProduct.PRODUCT_ID, productId);
            List<DjSupApplicationProduct> list = djSupApplicationProductMapper.selectByExample(example);
            if (list == null) {
                return null;
            }
            return list.get(0);
        } catch (Exception e) {
            logger.info("查询异常", e);
            return null;
        }

    }
}
