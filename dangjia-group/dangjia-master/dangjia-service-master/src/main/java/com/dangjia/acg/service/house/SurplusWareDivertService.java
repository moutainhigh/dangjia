package com.dangjia.acg.service.house;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.house.SurplusWareHouseDivertDTO;
import com.dangjia.acg.dto.house.SurplusWareHouseDivertDetailsDTO;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.ISurplusWareDivertMapper;
import com.dangjia.acg.mapper.house.ISurplusWareHouseItemMapper;
import com.dangjia.acg.mapper.house.ISurplusWareHouseMapper;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.SurplusWareDivert;
import com.dangjia.acg.modle.house.SurplusWareHouse;
import com.dangjia.acg.modle.house.SurplusWareHouseItem;
import com.dangjia.acg.modle.sup.Supplier;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * author: ysl
 * Date: 2019/1/24 0018
 * Time: 10:54
 */
@Service
public class SurplusWareDivertService {
    @Autowired
    private ISurplusWareDivertMapper iSurplusWareDivertMapper;
    @Autowired
    private ISurplusWareHouseMapper iSurplusWareHouseMapper;
    @Autowired
    private ISurplusWareHouseItemMapper iSurplusWareHouseItemMapper;
    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private ForMasterAPI forMasterAPI;

    /**
     * 添加临时仓库清点数据
     */
    public ServerResponse addSurplusWareDivertList(String jsonStr) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            String srcSurplusWareHouseId = jsonObject.getString("srcSurplusWareHouseId");
            if (!StringUtils.isNoneBlank(srcSurplusWareHouseId))
                return ServerResponse.createByErrorMessage("srcSurplusWareHouseId 不能为null");
            String jsonSurplusWareDivertList = jsonObject.getString("jsonSurplusWareDivertList");
            JSONArray jsonSurplusWareDivertArr = JSONArray.parseArray(jsonSurplusWareDivertList);
            SurplusWareHouse srcSurplusWareHouse = iSurplusWareHouseMapper.selectByPrimaryKey(srcSurplusWareHouseId);
            if (srcSurplusWareHouse == null)
                return ServerResponse.createByErrorMessage("仓库不存在");
            List<SurplusWareDivert> itemList = new ArrayList<>();
            for (int i = 0; i < jsonSurplusWareDivertArr.size(); i++) {//遍历户型
                JSONObject obj = jsonSurplusWareDivertArr.getJSONObject(i);
                String productId = obj.getString("productId");//商品id
                Integer divertCount = obj.getInteger("divertCount");//挪出数量
                boolean isNew = true;//是否需要 初始化对象
                for (SurplusWareDivert item : itemList) {
                    if (item.getProductId().equals(productId)) {
                        item.setDivertCount(item.getDivertCount() + divertCount);
                        isNew = false;
                    }
                }
                if (isNew) {//新增
                    SurplusWareDivert item = new SurplusWareDivert();
                    item.setProductId(productId);
                    item.setDivertCount(divertCount);
                    itemList.add(item);
                }
            }
            House house = iHouseMapper.selectByPrimaryKey(srcSurplusWareHouse.getHouseId());
            for (SurplusWareDivert item : itemList) {
                SurplusWareHouseItem surplusWareHouseItem = iSurplusWareHouseItemMapper.getAllSurplusWareHouseItemByProductId(srcSurplusWareHouseId, item.getProductId());
                //挪出的商品 数量 大于 库存数量 就提示  库存不足
                if (item.getDivertCount() > surplusWareHouseItem.getProductCount()) {
                    String name;
                    if (house != null) {
                        name = forMasterAPI.getProduct(house.getCityId(), item.getProductId()).getName();
                    } else {
                        name = forMasterAPI.getProduct("", item.getProductId()).getName();
                    }
                    return ServerResponse.createByErrorMessage(name + " 库存不足,剩余:" + surplusWareHouseItem.getProductCount());
                }
            }
            String address = null;
            for (int i = 0; i < jsonSurplusWareDivertArr.size(); i++) {//遍历户型
                JSONObject obj = jsonSurplusWareDivertArr.getJSONObject(i);
                String productId = obj.getString("productId");//商品id
                Integer divertCount = obj.getInteger("divertCount");//挪出数量
                Integer divertType = obj.getInteger("divertType");////挪货去向类型： 1临时仓库 2供应商
                String toSurplusWareHouseId = obj.getString("toSurplusWareHouseId");//目标仓库id
                Date divertDate = obj.getDate("divertDate");//挪货日期
                // 原仓库 库存数量减少
                SurplusWareHouseItem srcSurplusWareHouseItem = iSurplusWareHouseItemMapper.getAllSurplusWareHouseItemByProductId(srcSurplusWareHouseId, productId);
                if (divertCount <= srcSurplusWareHouseItem.getProductCount()) {
                    srcSurplusWareHouseItem.setProductCount(srcSurplusWareHouseItem.getProductCount() - divertCount);
                    iSurplusWareHouseItemMapper.updateByPrimaryKeySelective(srcSurplusWareHouseItem);
                }
                SurplusWareHouse toSurplusWareHouse = null;
                //目标地：  1公司临时仓库 2供应商 3业主家的房子  ，库存数量增加
                if (divertType == 1) {//公司仓库 这里是 公司仓库 id
                    toSurplusWareHouse = iSurplusWareHouseMapper.selectByPrimaryKey(toSurplusWareHouseId);
                    ServerResponse serverResponse = divertProductToWareHouse(productId, divertCount, toSurplusWareHouse);
                    if (!serverResponse.isSuccess())
                        return serverResponse;
                    address = toSurplusWareHouse.getAddress();
                } else if (divertType == 2) {//供应商  toSurplusWareHouseId : 是供应商id
                    Supplier toSupplier = forMasterAPI.getSupplier(house.getCityId(), toSurplusWareHouseId);
                    if (toSupplier != null)
                        address = toSupplier.getName();
                } else if (divertType == 3) {// 业主   toSurplusWareHouseId : 这里是 房子的id
                    house = iHouseMapper.selectByPrimaryKey(toSurplusWareHouseId);
                    toSurplusWareHouse = iSurplusWareHouseMapper.getSurplusWareHouseByHouseId(house.getId());
                    if (toSurplusWareHouse == null) { //创建 房子 临时仓库
                        toSurplusWareHouse = new SurplusWareHouse();
                        toSurplusWareHouse.setHouseId(house.getId());
                        toSurplusWareHouse.setMemberId(house.getMemberId());
                        toSurplusWareHouse.setState(1);//待清点0, 已清点1  默认：0
                        toSurplusWareHouse.setType(2);// 1:公司仓库 2：业主房子的临时仓库
                        toSurplusWareHouse.setAddress(house.getHouseName());
                        iSurplusWareHouseMapper.insert(toSurplusWareHouse);
                    }
                    ServerResponse serverResponse = divertProductToWareHouse(productId, divertCount, toSurplusWareHouse);
                    if (!serverResponse.isSuccess())
                        return serverResponse;
                    address = toSurplusWareHouse.getAddress();
                }

                //添加 挪货记录
                SurplusWareDivert surplusWareDivert = new SurplusWareDivert();
                //新增 挪货记录
                surplusWareDivert.setToAddress(address);
                surplusWareDivert.setDivertCount(divertCount);
                if (divertDate == null)
                    surplusWareDivert.setDivertDate(new Date());
                else
                    surplusWareDivert.setDivertDate(divertDate);
                surplusWareDivert.setDivertType(divertType);//挪货去向类型： 1公司临时仓库 2供应商 3业主家的房子
                surplusWareDivert.setProductId(productId);
                surplusWareDivert.setSrcSurplusWareHouseId(srcSurplusWareHouseId);
                surplusWareDivert.setToSurplusWareHouseId(toSurplusWareHouseId);
                iSurplusWareDivertMapper.insert(surplusWareDivert);
            }
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }

    }

    /**
     * 将 商品 挪货 到 目标仓库中
     *
     * @param productId
     * @param divertCount
     * @param toSurplusWareHouse
     */
    private ServerResponse divertProductToWareHouse(String productId, Integer divertCount, SurplusWareHouse toSurplusWareHouse) {
        try {
            SurplusWareHouseItem toSurplusWareHouseItem = iSurplusWareHouseItemMapper.getAllSurplusWareHouseItemByProductId(toSurplusWareHouse.getId(), productId);
            if (toSurplusWareHouseItem == null) {//如果仓库里没有 该清点商品 就新增
                SurplusWareHouseItem newSurplusWareHouseItem = new SurplusWareHouseItem();
                newSurplusWareHouseItem.setSurplusWareHouseId(toSurplusWareHouse.getId());
                newSurplusWareHouseItem.setProductId(productId);
                newSurplusWareHouseItem.setProductCount(divertCount);
                Product product;
                House house = iHouseMapper.selectByPrimaryKey(toSurplusWareHouse.getHouseId());
                if (house != null) {
                    product = forMasterAPI.getProduct(house.getCityId(), productId);
                } else {
                    product = forMasterAPI.getProduct("", productId);
                }
                if (product != null) {
                    newSurplusWareHouseItem.setCategoryId(product.getCategoryId());
                    newSurplusWareHouseItem.setProductName(product.getName());
                }
                iSurplusWareHouseItemMapper.insert(newSurplusWareHouseItem);
            } else {
                toSurplusWareHouseItem.setProductCount(toSurplusWareHouseItem.getProductCount() + divertCount);
                iSurplusWareHouseItemMapper.updateByPrimaryKeySelective(toSurplusWareHouseItem);
            }
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }


    /**
     * 查询指定仓库id的挪货记录
     *
     * @param pageDTO            分页
     * @param surplusWareHouseId 指定仓库
     * @return
     */
    public ServerResponse getAllSurplusWareDivertListBySId(PageDTO pageDTO, String surplusWareHouseId) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<SurplusWareHouseItem> surplusWareHouseItemList = iSurplusWareHouseItemMapper.getAllSurplusWareHouseItemById(surplusWareHouseId);
        if (surplusWareHouseItemList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(surplusWareHouseItemList);
        List<SurplusWareHouseDivertDetailsDTO> dtoList = new ArrayList<>();
        for (SurplusWareHouseItem surplusWareHouseItem : surplusWareHouseItemList) {
            SurplusWareHouseDivertDetailsDTO detailsDTO = new SurplusWareHouseDivertDetailsDTO();
            detailsDTO.setProductId(surplusWareHouseItem.getProductId());
            detailsDTO.setProductName(surplusWareHouseItem.getProductName());
            detailsDTO.setProductCount(surplusWareHouseItem.getProductCount());
            detailsDTO.setCreateDate(surplusWareHouseItem.getCreateDate());
            Product product = forMasterAPI.getProduct("", surplusWareHouseItem.getProductId());
            if (product != null)
                detailsDTO.setProductUnit(product.getUnitName());
            detailsDTO.setSurplusWareHouseId(surplusWareHouseItem.getSurplusWareHouseId());
            List<SurplusWareHouseDivertDTO> divertDTOList = new ArrayList<>();
            List<SurplusWareDivert> list = iSurplusWareDivertMapper.getAllSurplusWareDivertListBySIdAndPid(surplusWareHouseId, surplusWareHouseItem.getProductId());
            for (SurplusWareDivert surplusWareDivert : list) {
                SurplusWareHouseDivertDTO divertDTO = new SurplusWareHouseDivertDTO();
                divertDTO.setCreateDate(surplusWareDivert.getCreateDate());
                divertDTO.setDivertCount(surplusWareDivert.getDivertCount());
                divertDTO.setDivertDate(surplusWareDivert.getDivertDate());
                divertDTO.setDivertType(surplusWareDivert.getDivertType());
                divertDTO.setProductId(surplusWareDivert.getProductId());
                divertDTO.setSrcSurplusWareHouseId(surplusWareDivert.getSrcSurplusWareHouseId());
                divertDTO.setToSurplusWareHouseId(surplusWareDivert.getToSurplusWareHouseId());
                divertDTO.setToAddress(surplusWareDivert.getToAddress());
                divertDTOList.add(divertDTO);
            }
            detailsDTO.setSurplusWareHouseDivertDTOList(divertDTOList);
            dtoList.add(detailsDTO);
        }
        pageResult.setList(dtoList);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }


}
