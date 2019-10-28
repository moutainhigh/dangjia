package com.dangjia.acg.service.supplier;

import cn.jiguang.common.utils.StringUtils;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dto.supplier.DjSupSupplierProductDTO;
import com.dangjia.acg.dto.supplier.DjSupplierDTO;
import com.dangjia.acg.mapper.supplier.DjSupApplicationMapper;
import com.dangjia.acg.mapper.supplier.DjSupSupplierProductMapper;
import com.dangjia.acg.mapper.supplier.DjSupplierMapper;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.supplier.DjSupApplication;
import com.dangjia.acg.modle.supplier.DjSupplier;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 8/10/2019
 * Time: 下午 2:22
 */
@Service
public class DjSupplierServices {

    @Autowired
    private DjSupplierMapper djSupplierMapper;
    @Autowired
    private DjSupSupplierProductMapper djSupSupplierProductMapper;

    @Autowired
    private DjSupApplicationMapper djSupApplicationMapper ;

    public DjSupplier queryDjSupplierByPass(String supplierId) {
        return djSupplierMapper.queryDjSupplierByPass(supplierId);
    }


    @Autowired
    private DjSupApplicationProductService djSupApplicationProductService;

    /**
     * 根据userId查询供应商信息
     * @param userId
     * @param cityId
     * @return
     */
    public DjSupplier querySingleDjSupplier(String userId, String cityId) {
        DjSupplier djSupplier = djSupplierMapper.querySingleDjSupplier(userId, cityId);
        return djSupplier;
    }

    /**
     * 查询供应商基本信息
     * @param userId
     * @param cityId
     * @return
     */
    public ServerResponse querySingleDjSupplierDetail(String userId, String cityId) {
        DjSupplier djSupplier = djSupplierMapper.querySingleDjSupplier(userId, cityId);
        return ServerResponse.createBySuccess("查询成功",djSupplier);
    }

    /**
     * 供应商基础信息维护
     *
     * @param djSupplier
     * @return
     */
    public ServerResponse updateBasicInformation(DjSupplier djSupplier) {
        try {
            Example example=new Example(DjSupplier.class);
            example.createCriteria().andEqualTo(DjSupplier.USER_ID,djSupplier.getUserId())
                    .andEqualTo(DjSupplier.CITY_ID,djSupplier.getCityId())
                    .andEqualTo(DjSupplier.DATA_STATUS,0);
            if(djSupplierMapper.selectByExample(example).size()>0) {
                if (CommonUtil.isEmpty(djSupplier.getName()))
                    return ServerResponse.createByErrorMessage("用户名不能为空");
                if (CommonUtil.isEmpty(djSupplier.getTelephone()))
                    return ServerResponse.createByErrorMessage("电话号码不能为空");
                if (CommonUtil.isEmpty(djSupplier.getAddress()))
                    return ServerResponse.createByErrorMessage("地址不能为空");
                if (CommonUtil.isEmpty(djSupplier.getEmail()))
                    return ServerResponse.createByErrorMessage("邮件不能为空");
                if (CommonUtil.isEmpty(djSupplier.getCheckPeople()))
                    return ServerResponse.createByErrorMessage("联系人不能为空");
                if (djSupplierMapper.updateByPrimaryKeySelective(djSupplier) > 0)
                    return ServerResponse.createBySuccessMessage("编辑成功");
            }else{
                if (CommonUtil.isEmpty(djSupplier.getName()))
                    return ServerResponse.createByErrorMessage("用户名不能为空");
                if (CommonUtil.isEmpty(djSupplier.getTelephone()))
                    return ServerResponse.createByErrorMessage("电话号码不能为空");
                if (CommonUtil.isEmpty(djSupplier.getAddress()))
                    return ServerResponse.createByErrorMessage("地址不能为空");
                if (CommonUtil.isEmpty(djSupplier.getEmail()))
                    return ServerResponse.createByErrorMessage("邮件不能为空");
                if (CommonUtil.isEmpty(djSupplier.getCheckPeople()))
                    return ServerResponse.createByErrorMessage("联系人不能为空");
                if (djSupplierMapper.insert(djSupplier)>0)
                    return ServerResponse.createBySuccessMessage("编辑成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("编辑失败");
        }
        return ServerResponse.createByErrorMessage("编辑失败");
    }


    /**
     * 选择供货列表
     *
     * @param supId
     * @param searchKey
     * @return
     */
    public ServerResponse querySupplyList(PageDTO pageDTO, String supId, String searchKey) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Storefront> storefronts = djSupplierMapper.querySupplyList(supId, searchKey);
            PageInfo pageResult = new PageInfo(storefronts);
            if (storefronts.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 供应商商品列表
     * @param pageDTO
     * @param supId
     * @return
     */
    public ServerResponse querySupplierGoods(PageDTO pageDTO, String supId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DjSupSupplierProductDTO> djSupSupplierProductDTOS = djSupSupplierProductMapper.querySupplierGoods(supId);
            djSupSupplierProductDTOS.forEach(djSupSupplierProductDTO -> {
                String[] split = djSupSupplierProductDTO.getAttributeIdArr().split(",");
                if(split.length>0)
                    djSupSupplierProductDTO.setAttributeIdArr(djSupSupplierProductMapper.queryAttributeNameByIds(split));
            });
            PageInfo pageResult = new PageInfo(djSupSupplierProductDTOS);
            if (djSupSupplierProductDTOS.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 分页
     * @param pageDTO
     * @param keyWord
     * @param applicationStatus
     * @param shopId
     * @return
     */

    public ServerResponse queryDjSupplierByShopIdPage( PageDTO pageDTO, String keyWord, String applicationStatus, String shopId,String cityId) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            if (StringUtils.isEmpty(shopId)) {
                return ServerResponse.createByErrorMessage("店铺ID不能为空!");
            }
            List<DjSupplierDTO>  list=djSupplierMapper.queryDjSupplierByShopID(keyWord,applicationStatus,shopId);
            if (list.size() <= 0){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            List<Map<String,Object>> djSupplierDTOList=new ArrayList<Map<String,Object>>();
            for(DjSupplierDTO djSupplierDTO:list)
            {
                Map<String,Object> resMap= BeanUtils.beanToMap(djSupplierDTO);
                Integer i=djSupApplicationProductService.queryHaveGoodsSize(djSupplierDTO.getSupId(), djSupplierDTO.getShopId(),"0");
                resMap.put("listSize",i);
                djSupplierDTOList.add(resMap);
            }
            PageInfo pageResult = new PageInfo(djSupplierDTOList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 不分页
     * @param keyWord
     * @param applicationStatus
     * @param shopId
     * @return
     */
    public ServerResponse queryDjSupplierByShopID(String keyWord, String applicationStatus, String shopId,String cityId) {
        try {

            if (StringUtils.isEmpty(shopId)) {
                return ServerResponse.createByErrorMessage("店铺ID不能为空!");
            }
            List<DjSupplierDTO>  list=djSupplierMapper.queryDjSupplierByShopID(keyWord,applicationStatus,shopId);
            if (list.size() <= 0){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            return ServerResponse.createBySuccess("查询成功", list);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 查询单个供应商申请详情
     * @param id
     * @return
     */
    public ServerResponse getDjSupplierByID(String id,String shopId,String cityId) {
        try {
            DjSupplierDTO djSupplierDTO=djSupplierMapper.queryDJsupplierById(id,shopId);
            return ServerResponse.createBySuccess("查询成功", djSupplierDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 供应商申请通过
     * @param id
     * @param applicationStatus
     * @return
     */
    public ServerResponse setDjSupplierPass(String id, String applicationStatus,String cityId) {
        try {
            if(StringUtils.isEmpty(applicationStatus))
            {
                return  ServerResponse.createBySuccessMessage("审核状态不能为空");
            }
            if(StringUtils.isEmpty(id))
            {
                return  ServerResponse.createBySuccessMessage("主键不能为空");
            }

            String[] iditem = id.split(",");
            Example example = new Example(DjSupApplication.class);
            example.createCriteria().andIn(DjSupApplication.ID, Arrays.asList(iditem));

            DjSupApplication djSupApplication=new DjSupApplication();
            djSupApplication.setId(null);
            djSupApplication.setApplicationStatus(applicationStatus);
            djSupApplication.setCreateDate(null);
            int i=djSupApplicationMapper.updateByExampleSelective(djSupApplication,example);

            if(i<=0)
            {
                ServerResponse.createByErrorMessage("供应商申请失败");
            }
            return  ServerResponse.createBySuccessMessage("供应商申请成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("供应商申请异常");
        }
    }

    /**
     * 驳回供应商申请
     * @param id
     * @param applicationStatus
     * @param failReason
     * @return
     */
    public ServerResponse setDjSupplierReject(String id, String applicationStatus, String failReason,String cityId) {
        try {
            if(StringUtils.isEmpty(failReason))
            {
                return  ServerResponse.createBySuccessMessage("驳回原因不能为空");
            }
            if(failReason.length()>20)
            {
                return  ServerResponse.createBySuccessMessage("驳回原因文字不能大于20字");
            }
            String[] iditem = id.split(",");
            Example example = new Example(DjSupApplication.class);
            example.createCriteria().andIn(DjSupApplication.ID, Arrays.asList(iditem));

            DjSupApplication djSupApplication=new DjSupApplication();
            djSupApplication.setId(null);
            djSupApplication.setApplicationStatus(applicationStatus);
            djSupApplication.setFailReason(failReason);
            djSupApplication.setCreateDate(null);
            int i=djSupApplicationMapper.updateByExampleSelective(djSupApplication,example);
            if(i<=0)
            {
                return ServerResponse.createBySuccessMessage("驳回供应商申请失败");

            }
            return  ServerResponse.createBySuccessMessage("驳回供应商申请成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("驳回供应商申请异常");
        }
    }

    }