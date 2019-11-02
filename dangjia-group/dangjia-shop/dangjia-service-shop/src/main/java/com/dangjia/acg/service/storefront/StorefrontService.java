package com.dangjia.acg.service.storefront;

import cn.jiguang.common.utils.StringUtils;
//import com.dangjia.acg.api.supplier.DjRegisterApplicationAPI;
import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.api.supplier.DjSupplierAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.storefront.StorefrontDTO;
import com.dangjia.acg.dto.storefront.StorefrontListDTO;
import com.dangjia.acg.mapper.storefront.IStorefrontMapper;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.supplier.DjSupplier;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class StorefrontService {

    /**
     * 声明日志
     */
    private static Logger logger = LoggerFactory.getLogger(StorefrontService.class);
    @Autowired
    private IStorefrontMapper istorefrontMapper;
//    @Autowired
//    private CraftsmanConstructionService constructionService;
    @Autowired
    private DjSupplierAPI djSupplierAPI;

    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private RedisClient redisClient;

//    @Autowired
//    private DjRegisterApplicationAPI djRegisterApplicationAPI;
    /**
     * 根据用户Id查询店铺信息
     * @param userId
     * @return
     */
    public Storefront queryStorefrontByUserID(String userId,String cityId) {
        try {
            Example example=new Example(Storefront.class);
            example.createCriteria().andEqualTo(Storefront.USER_ID,userId).andEqualTo(Storefront.CITY_ID,cityId);
            Storefront storefront =istorefrontMapper.selectByExample(example).get(0);
            return storefront;
        } catch (Exception e) {
            logger.error("查询失败",e);
            return null;
        }
    }
    /**
     * 根据Id查询店铺信息
     * @param id
     * @return
     */
    public Storefront querySingleStorefrontById(String id) {
        try {
            Storefront storefront = istorefrontMapper.selectByPrimaryKey(id);
            return storefront;
        } catch (Exception e) {
            logger.error("查询失败",e);
            return null;
        }
    }

    /**
     * 根据Id查询店铺信息
     * @param userId
     * @return
     */
    public ServerResponse queryStorefrontByUserId(String userId,String cityId) {
        try {

            //判断是否审核通过，是否是店铺，如果是就显示
            //String checkUserid= djRegisterApplicationAPI.getUserIdExamine(userId);
            //判断是否注册
//            if(StringUtils.isEmpty(checkUserid))
//            {
//                return ServerResponse.createBySuccessMessage("用户审核不通过");
//            }
            Example example=new Example(Storefront.class);
            example.createCriteria().andEqualTo(Storefront.USER_ID,userId).
                    andEqualTo(Storefront.CITY_ID,cityId);
            List<Storefront> list =istorefrontMapper.selectByExample(example);
            if(list.size()<=0)
            {
                return ServerResponse.createByErrorMessage("没有检索到店铺信息数据");
            }
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            StorefrontDTO storefrontDTO =new StorefrontDTO();
            Storefront storefront=list.get(0);
            storefrontDTO.setId(storefront.getId());
            storefrontDTO.setCityId(storefront.getCityId());
            storefrontDTO.setUserId(storefront.getUserId());
            storefrontDTO.setStorefrontName(storefront.getStorefrontName());
            storefrontDTO.setStorefrontAddress(storefront.getStorefrontAddress());
            storefrontDTO.setStorefrontDesc(storefront.getStorefrontDesc());
            storefrontDTO.setStorefrontLogo(address+storefront.getStorefrontLogo());
            storefrontDTO.setStorefrontSigleLogo(storefront.getStorefrontLogo());
            storefrontDTO.setStorekeeperName(storefront.getStorekeeperName());
            storefrontDTO.setMobile(storefront.getMobile());
            storefrontDTO.setEmail(storefront.getEmail());
            return ServerResponse.createBySuccess("检索到数据",storefrontDTO);
        } catch (Exception e) {
            logger.error("查询店铺信息异常：", e);
            return ServerResponse.createByErrorMessage("查询店铺信息异常");
        }
    }

    /**
     *  根据调件模糊查询店铺信息
     * @param searchKey
     * @return
     */
    public List<Storefront> queryLikeSingleStorefront(String searchKey) {
        try {
            List<Storefront> storefronts = istorefrontMapper.queryLikeSingleStorefront(searchKey);
            return storefronts;
        } catch (Exception e) {
            logger.error("查询失败",e);
            return null;
        }
    }



    public ServerResponse addStorefront(String userId, String cityId, String storefrontName,
                                        String storefrontAddress, String storefrontDesc,
                                        String storefrontLogo, String storekeeperName,
                                        String mobile, String email) {
        try {
//            Object object = constructionService.getMember(userToken);
//            if (object instanceof ServerResponse) {
//                return (ServerResponse) object;
//            }
//            Member worker = (Member) object;

            //店铺名称不能大于10个字
            if (storefrontName.length() > 10) {
                return ServerResponse.createByErrorMessage("店铺名称不能大于10个字!");
            }
            //店铺地址限制字数30个字，支持字母、数字、汉字
            if (storefrontAddress.length() > 30) {
                return ServerResponse.createByErrorMessage("店铺地址不能大于30个字!");
            }
            //店铺介绍限制字数20个字，支持字母、数字、汉字
            if (storefrontDesc.length() > 20) {
                return ServerResponse.createByErrorMessage("店铺介绍不能大于20个字!");
            }
            Storefront storefront = new Storefront();
            storefront.setUserId(userId);
            storefront.setCityId(cityId);
            storefront.setStorefrontName(storefrontName);
            storefront.setStorefrontAddress(storefrontAddress);
            storefront.setStorefrontDesc(storefrontDesc);
            storefront.setStorefrontLogo(storefrontLogo);
            storefront.setStorekeeperName(storekeeperName);
            storefront.setMobile(mobile);
            storefront.setEmail(email);

            //判断是否重复添加
            Example example=new Example(Storefront.class);
            example.createCriteria().andEqualTo(Storefront.CITY_ID,cityId).
                    andEqualTo(Storefront.USER_ID,userId);
            List<Storefront> list=istorefrontMapper.selectByExample(example);
            if(list.size()>0)
            {
                return ServerResponse.createByErrorMessage("店铺已经添加，不能重复添加!");
            }


            int i = istorefrontMapper.insert(storefront);
            if (i > 0) {
                return ServerResponse.createBySuccessMessage("新增成功!");
            } else {
                return ServerResponse.createByErrorMessage("新增失败!");
            }
        } catch (Exception e) {
            logger.error("新增失败：", e);
            return ServerResponse.createByErrorMessage("新增失败");
        }
    }

    public ServerResponse updateStorefront(StorefrontDTO storefrontDTO) {

        try {
//            Object object = constructionService.getMember(userToken);
//            if (object instanceof ServerResponse) {
//                return (ServerResponse) object;
//            }
//            Member worker = (Member) object;

            if(storefrontDTO==null||StringUtils.isEmpty(storefrontDTO.getUserId()))
            {
                return ServerResponse.createByErrorMessage("用户编号不能为空");
            }
            if(storefrontDTO==null||StringUtils.isEmpty(storefrontDTO.getCityId()))
            {
                return ServerResponse.createByErrorMessage("城市编号不能为空");
            }
            Example example = new Example(Storefront.class);
            example.createCriteria().andEqualTo(Storefront.USER_ID, storefrontDTO.getUserId()).andEqualTo(Storefront.CITY_ID, storefrontDTO.getCityId());

            Storefront storefront=new Storefront();
            storefront.setUserId(storefrontDTO.getUserId());
            storefront.setCityId(storefrontDTO.getCityId());
            storefront.setStorefrontName(storefrontDTO.getStorefrontName());
            storefront.setStorefrontAddress(storefrontDTO.getStorefrontAddress());
            storefront.setStorefrontDesc(storefrontDTO.getStorefrontDesc());
            storefront.setStorefrontLogo(storefrontDTO.getStorefrontLogo());
            storefront.setStorekeeperName(storefrontDTO.getStorekeeperName());
            storefront.setMobile(storefrontDTO.getMobile());
            storefront.setEmail(storefrontDTO.getEmail());
            storefront.setCreateDate(null);
            storefront.setId(null);
            int i = istorefrontMapper.updateByExample(storefront,example);
            if (i <= 0) {
                return ServerResponse.createByErrorMessage("修改失败!");
            }
            return ServerResponse.createBySuccessMessage("修改成功!");
        } catch (Exception e) {
            logger.error("修改失败：", e);
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }


    /**
     * 查询供应商申请店铺列表
     *
     * @param searchKey
     * @return
     */
    public ServerResponse querySupplierApplicationShopList(PageDTO pageDTO, String searchKey, String applicationStatus, String userId, String cityId) {
        try {
            String imageaddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            DjSupplier djSupplier = djSupplierAPI.querySingleDjSupplier(userId, cityId);
            if(null==djSupplier)
                return ServerResponse.createByErrorMessage("暂无店铺信息");
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<StorefrontListDTO> storefrontListDTOS = istorefrontMapper.querySupplierApplicationShopList(searchKey, djSupplier.getId(), applicationStatus,cityId);
            storefrontListDTOS.forEach(storefrontListDTO -> {
                storefrontListDTO.setStorefrontLogo(imageaddress+storefrontListDTO.getStorefrontLogo());
            });
            PageInfo pageResult = new PageInfo(storefrontListDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("查询失败：", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 供应商选择供货列表
     * @param pageDTO
     * @param searchKey
     * @param userId
     * @return
     */
    public ServerResponse querySupplierSelectionSupply( PageDTO pageDTO, String searchKey, String userId, String cityId) {
        try {
            String imageaddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            DjSupplier djSupplier = djSupplierAPI.querySingleDjSupplier(userId, cityId);
            if(null==djSupplier)
                return ServerResponse.createByErrorMessage("暂无店铺信息");
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<StorefrontListDTO> storefrontListDTOS = istorefrontMapper.querySupplierSelectionSupply(searchKey, djSupplier.getId(),cityId);
            storefrontListDTOS.forEach(storefrontListDTO -> {
                storefrontListDTO.setStorefrontLogo(imageaddress+storefrontListDTO.getStorefrontLogo());
            });
            PageInfo pageResult = new PageInfo(storefrontListDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("查询失败：", e);
            return ServerResponse.createByErrorMessage("查询失败");

        }
    }
}
