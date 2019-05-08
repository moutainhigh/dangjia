package com.dangjia.acg.service.house;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.model.PageDTO;
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * author: ysl
 * Date: 2019/1/24 0018
 * Time: 10:54
 */
@Service
public class SurplusWareHouseService {
    @Autowired
    private IMemberMapper iMemberMapper;
    @Autowired
    private ISurplusWareHouseMapper iSurplusWareHouseMapper;
    @Autowired
    private ISurplusWareHouseItemMapper iSurplusWareHouseItemMapper;
    @Autowired
    private ISurplusWareDivertMapper iSurplusWareDivertMapper;
    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private ForMasterAPI forMasterAPI;
    @Autowired
    private static Logger LOG = LoggerFactory.getLogger(SurplusWareHouseService.class);

    /**
     * 所有剩余材料的临时仓库
     *
     * @param pageDTO
     * @param state     状态类型 待清点0, 已清点1  默认：0
     *                  //     * @param type      1:公司仓库 2：业主房子的临时仓库
     * @param beginDate
     * @param endDate
     * @return
     */
    public ServerResponse getAllSurplusWareHouse(PageDTO pageDTO, Integer state, String address, String productName, String beginDate, String endDate) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<SurplusWareHouse> list = iSurplusWareHouseMapper.getAllSurplusWareHouse(state, address, productName, beginDate, endDate);

            List<SurplusWareHouseDTO> surplusWareHouseDTOList = new ArrayList<>();
            LOG.info(" getAllSurplusWareHouse list:" + list);
            for (SurplusWareHouse surplusWareHouse : list) {
                SurplusWareHouseDTO surplusWareHouseDTO = new SurplusWareHouseDTO();
                surplusWareHouseDTO.setId(surplusWareHouse.getId());
                surplusWareHouseDTO.setAddress(surplusWareHouse.getAddress());
                if (surplusWareHouse.getHouseId() == null) {
                    surplusWareHouseDTO.setType(1);//type: 1:公司仓库 2：业主房子的临时仓库
                } else {
                    House house = iHouseMapper.selectByPrimaryKey(surplusWareHouse.getHouseId());
                    if (house != null) {
                        surplusWareHouseDTO.setType(2);
                    }
                }

                Member member = iMemberMapper.selectByPrimaryKey(surplusWareHouse.getMemberId());
                if (member != null) {
                    surplusWareHouseDTO.setMemberId(member.getId());
                    surplusWareHouseDTO.setMemberName(member.getName());
                    surplusWareHouseDTO.setMemberPhone(member.getMobile());
                }
                surplusWareHouseDTO.setState(surplusWareHouse.getState());
                surplusWareHouseDTO.setCreateDate(surplusWareHouse.getCreateDate());
                surplusWareHouseDTO.setModifyDate(surplusWareHouse.getModifyDate());

                List<SurplusWareHouseItem> surplusWareHouseItems = iSurplusWareHouseItemMapper.getAllSurplusWareHouseItemById(surplusWareHouseDTO.getId());
                int allProductCount = 0;
                for (SurplusWareHouseItem item : surplusWareHouseItems) {
                    allProductCount = allProductCount + item.getProductCount();
                }
                surplusWareHouseDTO.setSurplusWareHouseProductAllCount(allProductCount);

                SurplusWareDivert surplusWareDivert = iSurplusWareDivertMapper.getDivertBySIdAndWareHouseIdSortDate(surplusWareHouseDTO.getId());
                if (surplusWareDivert != null)
                    surplusWareHouseDTO.setMinDivertDate(surplusWareDivert.getDivertDate());

                surplusWareHouseDTOList.add(surplusWareHouseDTO);
            }
            PageInfo pageResult = new PageInfo(list);
            pageResult.setList(surplusWareHouseDTOList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }


    /**
     * 修改、添加临时仓库的信息
     */
    public ServerResponse setSurplusWareHouse(SurplusWareHouse surplusWareHouse) {
        try {

            if (StringUtils.isNoneBlank(surplusWareHouse.getAddress()))//新增 仓库
            {
                SurplusWareHouse srcSurplusWareHouse = iSurplusWareHouseMapper.getSurplusWareHouseByAddress(surplusWareHouse.getAddress());
                if (srcSurplusWareHouse != null)
                    return ServerResponse.createByErrorMessage("该仓库地址已存在");

                SurplusWareHouse newSurplusWareHouse = new SurplusWareHouse();
                newSurplusWareHouse.setMemberId(surplusWareHouse.getMemberId());
                newSurplusWareHouse.setAddress(surplusWareHouse.getAddress());
                newSurplusWareHouse.setState(0);
                newSurplusWareHouse.setType(1);
                iSurplusWareHouseMapper.insert(newSurplusWareHouse);
            } else {//修改

                SurplusWareHouse srcSurplusWareHouse = iSurplusWareHouseMapper.selectByPrimaryKey(surplusWareHouse.getId());
                if (srcSurplusWareHouse == null)
                    return ServerResponse.createByErrorMessage("无该临时仓库");

//                if (surplusWareHouse.getState() != -1) {//新增 仓库
//                    srcSurplusWareHouse.setState(surplusWareHouse.getState());
//                    iSurplusWareHouseMapper.updateByPrimaryKey(srcSurplusWareHouse);
//                }
            }

            String appType = "zx"; //zx =当家装修  ，     gj =当家工匠
//            if (!StringUtils.isNoneBlank(surplusWareHouse.getId()))
//                return ServerResponse.createByErrorMessage("Id 不能为null");

            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }


    /**
     * 添加临时仓库清点数据
     */
    public ServerResponse addSurplusWareHouseItem(String jsonStr) {
        try {

            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            String surplusWareHouseId = jsonObject.getString("surplusWareHouseId");
            if (!StringUtils.isNoneBlank(surplusWareHouseId))
                return ServerResponse.createByErrorMessage("surplusWareHouseId 不能为null");

            String jsonSurplusWareHouseItemList = jsonObject.getString("jsonSurplusWareHouseItemList");
            JSONArray jsonSurplusWareHouseItemArr = JSONArray.parseArray(jsonSurplusWareHouseItemList);

            SurplusWareHouse srcSurplusWareHouse = iSurplusWareHouseMapper.selectByPrimaryKey(surplusWareHouseId);
            if (srcSurplusWareHouse == null)
                return ServerResponse.createByErrorMessage("临时仓库不存在");

            for (int i = 0; i < jsonSurplusWareHouseItemArr.size(); i++) {//遍历户型
                JSONObject obj = jsonSurplusWareHouseItemArr.getJSONObject(i);
                String productId = obj.getString("productId");//商品id
                Integer productCount = obj.getInteger("productCount");//商品数量

                SurplusWareHouseItem srcSurplusWareHouseItem = iSurplusWareHouseItemMapper.getAllSurplusWareHouseItemByProductId(surplusWareHouseId, productId);
                if (srcSurplusWareHouseItem == null) {//如果仓库里没有 该清点商品 就新增
                    SurplusWareHouseItem newSurplusWareHouseItem = new SurplusWareHouseItem();
                    newSurplusWareHouseItem.setSurplusWareHouseId(surplusWareHouseId);
                    newSurplusWareHouseItem.setProductId(productId);
                    newSurplusWareHouseItem.setProductCount(productCount);

                    House house = iHouseMapper.selectByPrimaryKey(srcSurplusWareHouse.getHouseId());
                    Product product;
                    if(house != null){
                        product = forMasterAPI.getProduct(house.getCityId(), productId);
                    }else {
                        product = forMasterAPI.getProduct("", productId);
                    }
                    if (product != null) {
                        newSurplusWareHouseItem.setCategoryId(product.getCategoryId());
                        newSurplusWareHouseItem.setProductName(product.getName());
                    }

                    iSurplusWareHouseItemMapper.insert(newSurplusWareHouseItem);
                } else {
                    srcSurplusWareHouseItem.setProductCount(srcSurplusWareHouseItem.getProductCount() + productCount);
                    iSurplusWareHouseItemMapper.updateByPrimaryKeySelective(srcSurplusWareHouseItem);
                }
            }

            srcSurplusWareHouse.setState(1);//标记为已清点
            iSurplusWareHouseMapper.updateByPrimaryKeySelective(srcSurplusWareHouse);

            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }

    }

    /**
     * 获取某个临时仓库 的所有剩余材料
     *
     * @param pageDTO            分页
     * @param surplusWareHouseId 指定仓库
     * @return
     */
    public ServerResponse getAllSurplusWareHouseItemBySId(PageDTO pageDTO, String surplusWareHouseId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<SurplusWareHouseItem> list = iSurplusWareHouseItemMapper.getAllSurplusWareHouseItemById(surplusWareHouseId);

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
