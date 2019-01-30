package com.dangjia.acg.service.house;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.annotation.Desc;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.pay.domain.Data;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.house.SurplusWareHouseDTO;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.ISurplusWareDivertMapper;
import com.dangjia.acg.mapper.house.ISurplusWareHouseItemMapper;
import com.dangjia.acg.mapper.house.ISurplusWareHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.modle.basics.Product;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.SurplusWareDivert;
import com.dangjia.acg.modle.house.SurplusWareHouse;
import com.dangjia.acg.modle.house.SurplusWareHouseItem;
import com.dangjia.acg.modle.member.Member;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Column;
import java.util.*;

/**
 * author: ysl
 * Date: 2019/1/24 0018
 * Time: 10:54
 */
@Service
public class SurplusWareDivertService {
    @Autowired
    private IMemberMapper iMemberMapper;
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
    @Autowired
    private static Logger LOG = LoggerFactory.getLogger(SurplusWareDivertService.class);

    /**
     * 添加临时仓库清点数据
     */
    public ServerResponse addSurplusWareDivertList(String jsonStr) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            String srcSurplusWareHouseId = jsonObject.getString("srcSurplusWareHouseId");
            if (!StringUtils.isNoneBlank(srcSurplusWareHouseId))
                return ServerResponse.createByErrorMessage("surplusWareHouseId 不能为null");

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
                    item.setDivertCount(0);
                    itemList.add(item);
                }
            }

            for (SurplusWareDivert item : itemList) {
                SurplusWareHouseItem surplusWareHouseItem = iSurplusWareHouseItemMapper.getAllSurplusWareHouseItemByProductId(srcSurplusWareHouseId, item.getProductId());
                if (item.getDivertCount() > surplusWareHouseItem.getProductCount()) {
                    String name = forMasterAPI.getProduct(item.getProductId()).getName();
                    return ServerResponse.createByErrorMessage(name + " 库存不足,剩余:" + surplusWareHouseItem.getProductCount());
                }
            }

            for (int i = 0; i < jsonSurplusWareDivertArr.size(); i++) {//遍历户型
                JSONObject obj = jsonSurplusWareDivertArr.getJSONObject(i);
                String productId = obj.getString("productId");//商品id
                Integer divertCount = obj.getInteger("divertCount");//挪出数量
                Integer divertType = obj.getInteger("divertType");////挪货去向类型： 1临时仓库 2供应商
                String toSurplusWareHouseId = obj.getString("toSurplusWareHouseId");//目标仓库id
                Date divertDate = obj.getDate("divertDate");//挪货日期

                SurplusWareDivert surplusWareDivert = new SurplusWareDivert();
                surplusWareDivert.setDivertCount(divertCount);
                if (divertDate == null)
                    surplusWareDivert.setDivertDate(new Date());
                else
                    surplusWareDivert.setDivertDate(divertDate);
                surplusWareDivert.setDivertType(divertType);//挪货去向类型： 1临时仓库 2供应商
                surplusWareDivert.setProductId(productId);
                surplusWareDivert.setSrcSurplusWareHouseId(srcSurplusWareHouseId);
                surplusWareDivert.setToSurplusWareHouseId(toSurplusWareHouseId);
                iSurplusWareDivertMapper.insert(surplusWareDivert);

                SurplusWareHouseItem surplusWareHouseItem = iSurplusWareHouseItemMapper.getAllSurplusWareHouseItemByProductId(srcSurplusWareHouseId, productId);
                if (divertCount <= surplusWareHouseItem.getProductCount()) {
                    surplusWareHouseItem.setProductCount(surplusWareHouseItem.getProductCount() - divertCount);
                    iSurplusWareHouseItemMapper.updateByPrimaryKeySelective(surplusWareHouseItem);
                }
            }
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
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
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<SurplusWareDivert> list = iSurplusWareDivertMapper.getAllSurplusWareDivertListBySId(surplusWareHouseId);

//            List<SurplusWareHouseItem> surplusWareHouseItemList = new ArrayList<>();
            LOG.info(" getAllSurplusWareHouseItemBySId list:" + list);
//            for (SurplusWareHouseItem surplusWareHouseItem : list) {
//                surplusWareHouseDTOList
//            }
            PageInfo pageResult = new PageInfo(list);
//            pageResult.setList(surplusWareHouseDTOList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }


}
