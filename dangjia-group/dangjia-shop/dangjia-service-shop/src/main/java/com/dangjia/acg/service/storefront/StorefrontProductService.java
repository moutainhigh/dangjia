package com.dangjia.acg.service.storefront;

import cn.jiguang.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.dangjia.acg.api.product.DjBasicsProductAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.product.MemberCollectDTO;
import com.dangjia.acg.dto.product.ShoppingCartProductDTO;
import com.dangjia.acg.dto.storefront.StorefrontDTO;
import com.dangjia.acg.dto.storefront.StorefrontProductListDTO;
import com.dangjia.acg.dto.storefront.BasicsStorefrontProductDTO;
import com.dangjia.acg.dto.storefront.BasicsStorefrontProductViewDTO;
import com.dangjia.acg.mapper.storefront.IStorefrontMapper;
import com.dangjia.acg.mapper.storefront.IStorefrontProductMapper;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.storefront.StorefrontProduct;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class StorefrontProductService {
    /**
     * 声明日志
     */
    private static Logger logger = LoggerFactory.getLogger(StorefrontService.class);
    @Autowired
    private IStorefrontProductMapper istorefrontProductMapper;

    @Autowired
    private DjBasicsProductAPI djBasicsProductAPI ;
    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private StorefrontService storefrontService;
    @Autowired
    private IStorefrontMapper iStorefrontMapper;

    /**
     * 供货设置-根据货品id，城市id，店铺id删除店铺商品
     * @param productId
     * @param storefrontId
     * @param cityId
     * @return
     */
    public ServerResponse delProductByProIdAndStoreIdAndCityId(String productId, String storefrontId, String cityId) {
        try {
            if (StringUtils.isEmpty(productId)) {
                return ServerResponse.createByErrorMessage("货品ID不能为空");
            }
            if (StringUtils.isEmpty(storefrontId)) {
                return ServerResponse.createByErrorMessage("店铺ID不能为空");
            }
            if (StringUtils.isEmpty(cityId)) {
                return ServerResponse.createByErrorMessage("城市ID不能为空");
            }
            Example example=new Example(StorefrontProduct.class);
            example.createCriteria().andEqualTo(StorefrontProduct.PROD_TEMPLATE_ID,productId)
                    .andEqualTo(StorefrontProduct.STOREFRONT_ID,storefrontId)
                    .andEqualTo(StorefrontProduct.CITY_ID,cityId);

            StorefrontProduct storefrontProduct=new StorefrontProduct();
            storefrontProduct.setDataStatus(1);
            int i=istorefrontProductMapper.updateByExampleSelective(storefrontProduct,example);
            if (i <= 0) {
                return ServerResponse.createByErrorMessage("删除失败");

            }
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            logger.error("供货设置-根据货品id，城市id，店铺id删除店铺商品异常：", e);
            return ServerResponse.createByErrorMessage("供货设置-根据货品id，城市id，店铺id删除店铺商品异常");
        }
    }
//    /**
//     * 根据店铺id查询商品
//     * @param storefrontId
//     * @param searchKey
//     * @return
//     */
//    public List<StorefrontDTO> queryStorefrontListByStorefrontId(String storefrontId, String searchKey) {
//        return istorefrontProductMapper.queryStorefrontListByStorefrontId( storefrontId,  searchKey);
//    }
    /**
     * 根据id查询店铺商品信息
     *
     * @param id
     * @return
     */
    public StorefrontProductListDTO querySingleStorefrontProductById(String id) {
        return istorefrontProductMapper.querySingleStorefrontProductById(id);
    }

    /**
     * 供货设置-增加已选商品
     *
     * @return
     */
    public ServerResponse addStorefrontProduct(BasicsStorefrontProductDTO basicsStorefrontProductDTO) {
        try {

            String userId=basicsStorefrontProductDTO.getUserId();
            String cityId=basicsStorefrontProductDTO.getCityId();
            if (StringUtils.isEmpty(userId)) {
                return ServerResponse.createByErrorMessage("用户ID不能为空!");
            }
            if (StringUtils.isEmpty(cityId)) {
                return ServerResponse.createByErrorMessage("城市ID不能为空!");
            }
            Storefront storefront=storefrontService.queryStorefrontByUserID(userId,cityId);
            if(storefront==null)
            {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息!");
            }

            //判断是否重复添加
            Example example = new Example(StorefrontProduct.class);
            example.createCriteria().andEqualTo(StorefrontProduct.PROD_TEMPLATE_ID, basicsStorefrontProductDTO.getProdTemplateId())
            .andEqualTo(StorefrontProduct.STOREFRONT_ID,storefront.getId())
            .andEqualTo(StorefrontProduct.CITY_ID,cityId);
            List<StorefrontProduct> list = istorefrontProductMapper.selectByExample(example);
            if (list.size() > 0) {
                Example exampleup = new Example(StorefrontProduct.class);
                exampleup.createCriteria().andEqualTo(StorefrontProduct.ID, list.get(0).getId());
                StorefrontProduct storefrontProduct = new StorefrontProduct();
                storefrontProduct.setDataStatus(0);
                int i = istorefrontProductMapper.updateByExampleSelective(storefrontProduct, exampleup);

                if (i < 0)
                    return ServerResponse.createByErrorMessage("店铺商品新增成功");
                return ServerResponse.createBySuccessMessage("店铺商品新增成功");
            }
            DjBasicsProductTemplate djBasicsProductTemplate=null;
            ServerResponse serverResponse=djBasicsProductAPI.getProductById(null,basicsStorefrontProductDTO.getProdTemplateId());
            if (serverResponse != null && serverResponse.getResultObj() != null) {
                djBasicsProductTemplate = JSON.parseObject(JSON.toJSONString(serverResponse.getResultObj()), DjBasicsProductTemplate.class);
            }
            StorefrontProduct storefrontProduct = new StorefrontProduct();
            storefrontProduct.setStorefrontId(storefront.getId());//店铺id
            storefrontProduct.setImage(djBasicsProductTemplate.getImage());//大图
            storefrontProduct.setDetailImage(djBasicsProductTemplate.getDetailImage());//缩略图
            storefrontProduct.setMarketName(djBasicsProductTemplate.getMarketingName());//营销名称
            storefrontProduct.setSellPrice(basicsStorefrontProductDTO.getSellPrice());//销售价格
            storefrontProduct.setSuppliedNum(basicsStorefrontProductDTO.getSuppliedNum());//供货数量
            storefrontProduct.setIsUpstairsCost(basicsStorefrontProductDTO.getIsUpstairsCost());//师傅是否按一层收取上楼费
            storefrontProduct.setIsDeliveryInstall(basicsStorefrontProductDTO.getIsDeliveryInstall());//是否送货与安装/施工分开
            storefrontProduct.setMoveCost(basicsStorefrontProductDTO.getMoveCost());// 搬运费
            storefrontProduct.setIsShelfStatus(basicsStorefrontProductDTO.getIsShelfStatus());//是否上下架
            storefrontProduct.setProdTemplateId(djBasicsProductTemplate.getId());//货品id
            storefrontProduct.setGoodsId( djBasicsProductTemplate.getGoodsId());// 商品id
            storefrontProduct.setProductName(djBasicsProductTemplate.getName());//模板名称
            storefrontProduct.setCityId(basicsStorefrontProductDTO.getCityId());
            int i = istorefrontProductMapper.insert(storefrontProduct);
            if (i > 0) {
                return ServerResponse.createBySuccessMessage("增加店铺商品成功");
            } else {
                return ServerResponse.createByErrorMessage("增加店铺商品失败");
            }
        } catch (Exception e) {
            logger.error("增加店铺商品失败：", e);
            return ServerResponse.createByErrorMessage("增加店铺商品失败");
        }
    }

    /**
     * 供货设置-删除已选商品
     *
     * @return
     */
    public ServerResponse delStorefrontProductById(String id) {
        try {
            if (StringUtils.isEmpty(id)) {
                return ServerResponse.createByErrorMessage("商品ID不能为空");
            }

            Example example=new Example(StorefrontProduct.class);
            example.createCriteria().andEqualTo(StorefrontProduct.ID,id);

            StorefrontProduct storefrontProduct=new StorefrontProduct();
            storefrontProduct.setDataStatus(1);
            int i = istorefrontProductMapper.updateByExampleSelective(storefrontProduct,example);
            if (i <= 0) {
                return ServerResponse.createByErrorMessage("删除失败");
            }
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            logger.error("删除已选商品失败：", e);
            return ServerResponse.createByErrorMessage("删除已选商品失败");
        }
    }


    /**
     * 供货设置-已选商品-通过货品或者商品名称查询
     *
     * @param keyWord
     * @return
     */
    public ServerResponse queryStorefrontProductByKeyWord(String keyWord, String userId, PageDTO pageDTO,String cityId) {
        try {

            if (StringUtils.isEmpty(userId)) {
                return ServerResponse.createByErrorMessage("用户ID不能为空!");
            }
            if (StringUtils.isEmpty(cityId)) {
                return ServerResponse.createByErrorMessage("城市ID不能为空!");
            }
            Storefront storefront=storefrontService.queryStorefrontByUserID(userId,cityId);
            if(storefront==null)
            {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息!");
            }

            List<Map<String,Object>> basicsStorefrontProductViewDTOList=new ArrayList<Map<String,Object>>();
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<BasicsStorefrontProductViewDTO> list = istorefrontProductMapper.queryStorefrontProductViewDTOList(keyWord,storefront.getId(),cityId);
            //图片前缀路径
            String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            for (BasicsStorefrontProductViewDTO basicsStorefrontProductViewDTO : list) {
                Map<String, Object> resMap = BeanUtils.beanToMap(basicsStorefrontProductViewDTO);
                String id = basicsStorefrontProductViewDTO.getId();
                StorefrontProduct spdto = istorefrontProductMapper.queryStorefrontProductById(id);
                if (spdto == null) {
                    resMap.put("storefrontProduct", null);
                }
                else
                {
                    String[] imgArr = spdto.getImage().split(",");
                    StringBuilder imgStr = new StringBuilder();
                    StringBuilder imgUrlStr = new StringBuilder();
                    StringTool.getImages(address, imgArr, imgStr , imgUrlStr);
                    spdto.setImage(imgUrlStr.toString());
                    spdto.setImageUrl(imgStr.toString());

                    String[] dtimgArr = spdto.getDetailImage().split(",");
                    StringBuilder dtimgStr = new StringBuilder();
                    StringBuilder dtimgUrlStr = new StringBuilder();
                    StringTool.getImages(address, dtimgArr,dtimgStr  ,dtimgUrlStr );
                    spdto.setDetailImage(dtimgUrlStr.toString());
                    spdto.setDetailImageUrl(dtimgStr.toString());
                    resMap.put("storefrontProduct", spdto);
                }

                basicsStorefrontProductViewDTOList.add(resMap);
            }
            PageInfo pageResult = new PageInfo(basicsStorefrontProductViewDTOList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("查询失败：", e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 设置商品上下架
     *
     * @param id
     * @param isShelfStatus
     * @return
     */
    public ServerResponse setSpStatusById(String storefrontId,String id, String isShelfStatus) {
        try {
            if (StringUtils.isEmpty(id)) {
                return ServerResponse.createByErrorMessage("店铺商品ID不能为空");
            }

            if (StringUtils.isEmpty(isShelfStatus)) {
                return ServerResponse.createByErrorMessage("商品上下架状态不能为空");
            }

            if (StringUtils.isEmpty(storefrontId)) {
                return ServerResponse.createByErrorMessage("店铺id不能为空");
            }

            Storefront storefront=iStorefrontMapper.selectByPrimaryKey(storefrontId);
            if(storefront.getIfDjselfManage()==1)
            {
                StorefrontProduct storefrontProduct = new StorefrontProduct();
                storefrontProduct.setId(id);
                storefrontProduct.setIsShelfStatus(isShelfStatus);
                int i = istorefrontProductMapper.updateByPrimaryKeySelective(storefrontProduct);
                if (i <= 0) {
                    return ServerResponse.createByErrorMessage("商品上下架失败");
                }
                return ServerResponse.createBySuccessMessage("商品上下架成功");
            } else {
                //判断，如果是人工商品，提示不能上架
                Integer k = istorefrontProductMapper.selectProductByGoodsType(id);
                if (k<=0) {
                    return ServerResponse.createByErrorMessage("温馨提示：人工、增值、体验商品不能上架");
                }
                StorefrontProduct storefrontProduct = new StorefrontProduct();
                storefrontProduct.setId(id);
                storefrontProduct.setIsShelfStatus(isShelfStatus);
                int i = istorefrontProductMapper.updateByPrimaryKeySelective(storefrontProduct);
                if (i <= 0) {
                    return ServerResponse.createByErrorMessage("商品上下架失败");
                }
                return ServerResponse.createBySuccessMessage("商品上下架成功");
            }


        } catch (Exception e) {
            logger.error("商品上下架失败：", e);
            return ServerResponse.createByErrorMessage("商品上下架失败");
        }
    }

    /**
     * 设置商品批量上架
     *
     * @param isShelfStatus
     * @return
     */
    public ServerResponse setAllStoreProductByIsShelfStatus(String storefrontId,String id, String isShelfStatus) {
        try {
            if (StringUtils.isEmpty(id)) {
                return ServerResponse.createByErrorMessage("店铺商品ID集合不能为空");
            }
            if (StringUtils.isEmpty(isShelfStatus)) {
                return ServerResponse.createByErrorMessage("商品上下架状态不能为空");
            }
            if (StringUtils.isEmpty(storefrontId)) {
                return ServerResponse.createByErrorMessage("店铺id不能为空");
            }

            Storefront storefront=iStorefrontMapper.selectByPrimaryKey(storefrontId);
            if(storefront.getIfDjselfManage()==1)
            {
                String[] iditem = id.split(",");
                Example example = new Example(StorefrontProduct.class);
                example.createCriteria().andIn(StorefrontProduct.ID, Arrays.asList(iditem));
                StorefrontProduct storefrontProduct = new StorefrontProduct();
                storefrontProduct.setIsShelfStatus(isShelfStatus);
                storefrontProduct.setId(null);
                storefrontProduct.setCreateDate(null);
                int k = istorefrontProductMapper.updateByExampleSelective(storefrontProduct, example);
                if (k > 0) {
                    return ServerResponse.createBySuccessMessage("设置商品上下架成功");
                } else {
                    return ServerResponse.createByErrorMessage("设置商品上下架失败");
                }
            }
            else
            {
                //非自营点
                String[] iditem = id.split(",");

                StringBuffer zy=null;//当家自营店
                StringBuffer fzy=null;//非当家自营店
                for (String str : iditem) {
                    Integer k = istorefrontProductMapper.selectProductByGoodsType(str);//判断是否是实物和服务
                    if (k>0) {
                        fzy.append(str+",");
                    }
                    else
                    {
                        zy.append(str+",");
                    }
                }

                Example example = new Example(StorefrontProduct.class);
                example.createCriteria().andIn(StorefrontProduct.ID, Arrays.asList(fzy.toString().substring(0,fzy.length()-1).split(",")));
                StorefrontProduct storefrontProduct = new StorefrontProduct();
                storefrontProduct.setIsShelfStatus(isShelfStatus);
                storefrontProduct.setId(null);
                storefrontProduct.setCreateDate(null);
                int j = istorefrontProductMapper.updateByExampleSelective(storefrontProduct, example);
                if (j > 0) {
                    if(zy!=null)
                    {
                        return ServerResponse.createBySuccess("部分商品上下架成功,如下商品属于当家自营商品，不能商家",zy.toString().substring(0,zy.length()-1));
                    }
                    return ServerResponse.createBySuccessMessage("设置商品上下架成功");
                } else {
                    return ServerResponse.createByErrorMessage("设置商品上下架失败");
                }
            }

        } catch (Exception e) {
            logger.error("设置商品批量上架失败：", e);
            return ServerResponse.createByErrorMessage("设置商品批量上架失败");
        }
    }


    /**
     * 供货设置-保存编辑店铺商品
     *
     * @param storefrontProduct
     * @return
     */
    public ServerResponse saveStorefrontProductById(StorefrontProduct storefrontProduct) {
        try {
            if (storefrontProduct == null || StringUtils.isEmpty(storefrontProduct.getId())) {
                return ServerResponse.createByErrorMessage("商品ID不能为空");
            }
            storefrontProduct.setCreateDate(null);
            int i = istorefrontProductMapper.updateByPrimaryKeySelective(storefrontProduct);
            if (i > 0) {
                return ServerResponse.createBySuccessMessage("修改成功");
            } else {
                return ServerResponse.createByErrorMessage("修改失败");
            }
        } catch (Exception e) {
            logger.error("供货设置-保存编辑店铺商品失败：", e);
            return ServerResponse.createByErrorMessage("供货设置-保存编辑店铺商品失败");
        }
    }


    /**
     * 查询商品信息
     * @param storefrontId
     * @param productId
     * @return
     */
    public List<ShoppingCartProductDTO> queryCartList(String storefrontId, String productId) {
        List<ShoppingCartProductDTO> shoppingCartProductDTOS = istorefrontProductMapper.queryCartList(storefrontId, productId);
        return shoppingCartProductDTOS;
    }


    /**
     * 查询收藏商品
     * @param productId
     * @return
     */
    public List<MemberCollectDTO> queryCollectGood(String productId) {
        List<MemberCollectDTO> memberCollectDTOS = istorefrontProductMapper.queryCollectGood(productId);
        return memberCollectDTOS;
    }

}
