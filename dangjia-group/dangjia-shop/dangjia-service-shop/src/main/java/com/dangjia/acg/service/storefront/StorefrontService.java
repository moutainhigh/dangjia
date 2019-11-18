package com.dangjia.acg.service.storefront;

import cn.jiguang.common.utils.StringUtils;
import com.dangjia.acg.api.supplier.DjSupplierAPI;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.finance.WebSplitDeliverItemDTO;
import com.dangjia.acg.dto.storefront.StoreExpenseRecordDTO;
import com.dangjia.acg.dto.storefront.StorefrontDTO;
import com.dangjia.acg.dto.storefront.StorefrontListDTO;
import com.dangjia.acg.mapper.pay.IStoreBusinessOrderMapper;
import com.dangjia.acg.mapper.storefront.IStoreStorefrontMapper;
import com.dangjia.acg.mapper.storefront.IStorefrontConfigMapper;
import com.dangjia.acg.mapper.storefront.IStorefrontMapper;
import com.dangjia.acg.mapper.supplier.DjSupplierPayOrderMapper;
import com.dangjia.acg.mapper.user.IStoreUserMapper;
import com.dangjia.acg.mapper.worker.IStoreWithdrawDepositMapper;
import com.dangjia.acg.modle.other.BankCard;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.storefront.StorefrontConfig;
import com.dangjia.acg.modle.storefront.StorefrontRuleConfig;
import com.dangjia.acg.modle.supplier.DjSupplier;
import com.dangjia.acg.modle.supplier.DjSupplierPayOrder;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.modle.worker.WithdrawDeposit;
import com.dangjia.acg.util.Utils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StorefrontService {

    /**
     * 声明日志
     */
    private static Logger logger = LoggerFactory.getLogger(StorefrontService.class);
    @Autowired
    private IStoreWithdrawDepositMapper iStoreWithdrawDepositMapper;
    @Autowired
    private IStorefrontMapper istorefrontMapper;
    @Autowired
    private DjSupplierAPI djSupplierAPI;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private StorefrontService storefrontService;
    @Autowired
    private IStoreUserMapper iStoreUserMapper;
    @Autowired
    private IStoreBusinessOrderMapper iStoreBusinessOrderMapper;
    @Autowired
    private IStoreStorefrontMapper iStoreStorefrontMapper;
    @Autowired
    private DjSupplierPayOrderMapper djSupplierPayOrderMapper;

    @Autowired
    private IStorefrontConfigMapper iStorefrontConfigMapper;
    /**
     * 根据用户Id查询店铺信息
     * @param userId
     * @return
     */
    public Storefront queryStorefrontByUserID(String userId,String cityId) {
        try {
            Example example=new Example(Storefront.class);
            example.createCriteria().andEqualTo(Storefront.USER_ID,userId)
                    .andEqualTo(Storefront.CITY_ID,cityId)
                    .andEqualTo(Storefront.DATA_STATUS,0);
            Storefront storefront = istorefrontMapper.selectOneByExample(example);
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

            Example exampleFreight=new Example(StorefrontConfig.class);
            exampleFreight.createCriteria().andEqualTo(StorefrontConfig.STOREFRONT_ID,storefront.getId()).andEqualTo(StorefrontConfig.PARAM_KEY,StorefrontConfig.FREIGHT);
            List<StorefrontConfig> listFreight=iStorefrontConfigMapper.selectByExample(exampleFreight);
            if(listFreight!=null)
            storefrontDTO.setFreight(listFreight.get(0).getParamValue());

            Example exampleFreightTems=new Example(StorefrontConfig.class);
            exampleFreightTems.createCriteria().andEqualTo(StorefrontConfig.STOREFRONT_ID,storefront.getId()).andEqualTo(StorefrontConfig.PARAM_KEY,StorefrontConfig.FREIGHT_TERMS);
            List<StorefrontConfig> listFreightTems=iStorefrontConfigMapper.selectByExample(exampleFreightTems);
            if(listFreightTems!=null)
            storefrontDTO.setBelowUnitPrice(listFreightTems.get(0).getParamValue());
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

            if(storefrontDTO==null||StringUtils.isEmpty(storefrontDTO.getUserId()))
            {
                return ServerResponse.createByErrorMessage("用户编号不能为空");
            }
            if(storefrontDTO==null||StringUtils.isEmpty(storefrontDTO.getCityId()))
            {
                return ServerResponse.createByErrorMessage("城市编号不能为空");
            }

            Example exampleStorefront=new Example(Storefront.class);
            exampleStorefront.createCriteria().andEqualTo(Storefront.USER_ID,storefrontDTO.getUserId()).
                    andEqualTo(Storefront.CITY_ID, storefrontDTO.getCityId());
            List<Storefront> list =istorefrontMapper.selectByExample(exampleStorefront);
            if(list.size()<=0)
            {
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
                String systemlogo = configUtil.getValue(SysConfig.ORDER_DIANPU_ICON, String.class);
                storefront.setSystemLogo(systemlogo);
                int i = istorefrontMapper.insert(storefront);

                if (i <= 0) {
                    return ServerResponse.createByErrorMessage("修改失败!");
                }

                if (StringUtil.isNotEmpty(storefrontDTO.getFreight()))
                {

                    StorefrontConfig storefrontConfig=new StorefrontConfig();
                    storefrontConfig.setStorefrontId(storefront.getId());
                    storefrontConfig.setParamKey(StorefrontConfig.FREIGHT);
                    storefrontConfig.setParamValue(storefrontDTO.getBelowUnitPrice());
                    storefrontConfig.setCityId(storefrontDTO.getCityId());
                    iStorefrontConfigMapper.insert(storefrontConfig);
                }
                if (StringUtil.isNotEmpty(storefrontDTO.getBelowUnitPrice()))
                {
                    StorefrontConfig storefrontConfig=new StorefrontConfig();
                    storefrontConfig.setStorefrontId(storefront.getId());
                    storefrontConfig.setParamKey(StorefrontConfig.FREIGHT_TERMS);
                    storefrontConfig.setParamValue(storefrontDTO.getBelowUnitPrice());
                    storefrontConfig.setCityId(storefrontDTO.getCityId());
                    iStorefrontConfigMapper.insert(storefrontConfig);

                }
                return ServerResponse.createBySuccessMessage("修改成功!");
            }
            else
            {

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

                int i = istorefrontMapper.updateByExampleSelective(storefront,example);
                if (i <= 0) {
                    return ServerResponse.createByErrorMessage("修改失败!");
                }
                //收取运费
                if (StringUtil.isNotEmpty(storefrontDTO.getFreight()))
                {
                    Storefront mystorefront =list.get(0);
                    Example example1=new Example(StorefrontConfig.class);
                    example1.createCriteria().andEqualTo(StorefrontConfig.STOREFRONT_ID,mystorefront.getId()).andEqualTo(StorefrontConfig.PARAM_KEY,StorefrontConfig.FREIGHT);
                    List<StorefrontConfig> list1=iStorefrontConfigMapper.selectByExample(example1);
                    if (list1!=null&&list1.size()>0)
                    {
                        StorefrontConfig storefrontConfig=new StorefrontConfig();
                        storefrontConfig.setStorefrontId(mystorefront.getId());
                        storefrontConfig.setParamKey(StorefrontConfig.FREIGHT);
                        storefrontConfig.setParamValue(storefrontDTO.getFreight());
                        Example exampleParamkey=new Example(StorefrontConfig.class);
                        exampleParamkey.createCriteria().andEqualTo(StorefrontConfig.STOREFRONT_ID,mystorefront.getId())
                                .andEqualTo(StorefrontConfig.CITY_ID,storefrontDTO.getCityId()).andEqualTo(StorefrontConfig.PARAM_KEY,StorefrontConfig.FREIGHT);
                        iStorefrontConfigMapper.updateByExampleSelective(storefrontConfig,exampleParamkey);
                    }
                    else
                    {
                        StorefrontConfig storefrontConfig=new StorefrontConfig();
                        storefrontConfig.setStorefrontId(mystorefront.getId());
                        storefrontConfig.setParamKey(StorefrontConfig.FREIGHT);
                        storefrontConfig.setParamValue(storefrontDTO.getFreight());
                        storefrontConfig.setCityId(storefrontDTO.getCityId());
                        iStorefrontConfigMapper.insert(storefrontConfig);
                    }
                }
                //每单价格低于
                if (StringUtil.isNotEmpty(storefrontDTO.getBelowUnitPrice()))
                {
                    Storefront mystorefront =list.get(0);
                    Example example1=new Example(StorefrontConfig.class);
                    example1.createCriteria().andEqualTo(StorefrontConfig.STOREFRONT_ID,mystorefront.getId()).andEqualTo(StorefrontConfig.PARAM_KEY,StorefrontConfig.FREIGHT_TERMS);
                    List<StorefrontConfig> list1=iStorefrontConfigMapper.selectByExample(example1);
                    if (list1!=null&&list1.size()>0) {
                        StorefrontConfig storefrontConfig = new StorefrontConfig();
                        storefrontConfig.setStorefrontId(mystorefront.getId());
                        storefrontConfig.setParamKey(StorefrontConfig.FREIGHT_TERMS);
                        storefrontConfig.setParamValue(storefrontDTO.getBelowUnitPrice());
                        Example exampleFreightTerms = new Example(StorefrontConfig.class);
                        exampleFreightTerms.createCriteria().andEqualTo(StorefrontConfig.STOREFRONT_ID, mystorefront.getId())
                                .andEqualTo(StorefrontConfig.CITY_ID, storefrontDTO.getCityId()).andEqualTo(StorefrontConfig.PARAM_KEY, StorefrontConfig.FREIGHT_TERMS);
                        iStorefrontConfigMapper.updateByExampleSelective(storefrontConfig, exampleFreightTerms);
                    }
                    else
                    {
                        StorefrontConfig storefrontConfig=new StorefrontConfig();
                        storefrontConfig.setStorefrontId(mystorefront.getId());
                        storefrontConfig.setParamKey(StorefrontConfig.FREIGHT_TERMS);
                        storefrontConfig.setParamValue(storefrontDTO.getBelowUnitPrice());
                        storefrontConfig.setCityId(storefrontDTO.getCityId());
                        iStorefrontConfigMapper.insert(storefrontConfig);
                    }

                }
                return ServerResponse.createBySuccessMessage("修改成功!");
            }

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
                return ServerResponse.createByErrorMessage("暂无供应商信息");
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<StorefrontListDTO> storefrontListDTOS = istorefrontMapper.querySupplierApplicationShopList(searchKey, djSupplier.getId(), applicationStatus,cityId);
            storefrontListDTOS.forEach(storefrontListDTO -> {
                if(CommonUtil.isEmpty(storefrontListDTO.getContract())){
                    storefrontListDTO.setContract("");
                }
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
                return ServerResponse.createByErrorMessage("暂无供应商信息");
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


    /**
     *店铺-我的钱包
     * @param request
     * @param pageDTO
     * @param searchKey
     * @param userId
     * @param cityId
     * @return
     */
    public ServerResponse queryStorefrontWallet(HttpServletRequest request, PageDTO pageDTO, String searchKey, String userId, String cityId) {
        try {
            Storefront storefront = storefrontService.queryStorefrontByUserID(userId, cityId);
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            Double withdrawalAmount = istorefrontMapper.myWallet(storefront.getId(),new Date());
            Map<String, Double> map = new HashMap<>();
            map.put("totalAccount", storefront.getTotalAccount()!=null?storefront.getTotalAccount():0d);//账户总额
            map.put("withdrawalAmount", storefront.getSurplusMoney()!=null?storefront.getSurplusMoney():0d);//可提现余额
            map.put("totalAccountAmount", storefront.getRetentionMoney()!=null?storefront.getRetentionMoney():0d);//滞留金
            return ServerResponse.createBySuccess("查询成功", map);
        } catch (Exception e) {
            logger.error("店铺-我的钱包异常：", e);
            return ServerResponse.createByErrorMessage("店铺-我的钱包异常");
        }
    }



    /**
     *店铺提现
     * @param userId
     * @param cityId
     * @param bankCard
     * @param surplusMoney
     * @param payPassword
     * @return
     */
    public ServerResponse operationStorefrontReflect(String userId, String cityId, String bankCard, Double surplusMoney, String payPassword) {
        try {

            Storefront storefront = storefrontService.queryStorefrontByUserID(userId, cityId);
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            if (surplusMoney > storefront.getSurplusMoney()) {
                return ServerResponse.createByErrorMessage("现金额超过提现金额");
            }
            if (surplusMoney <= 0) {
                return ServerResponse.createByErrorMessage("提现金额不正确");
            }
            MainUser mainUser = iStoreUserMapper.selectByPrimaryKey(storefront.getUserId());
            if (!Utils.md5(payPassword).equals(mainUser.getPayPassword())){
                return ServerResponse.createByErrorMessage("密码错误");
            }
            //提现申请
            WithdrawDeposit withdrawDeposit = new WithdrawDeposit();
            withdrawDeposit.setMoney(new BigDecimal(surplusMoney));
            withdrawDeposit.setName(storefront.getStorekeeperName());
            withdrawDeposit.setWorkerId(mainUser.getId());
            withdrawDeposit.setState(0);
            withdrawDeposit.setRoleType(4);
            withdrawDeposit.setCardNumber(bankCard);
            BankCard bankCard1 = iStoreWithdrawDepositMapper.queryBankCard(bankCard, mainUser.getId());
            withdrawDeposit.setBankName(bankCard1.getBankName());
            withdrawDeposit.setDataStatus(0);
            withdrawDeposit.setSourceId(storefront.getId());
            iStoreWithdrawDepositMapper.insert(withdrawDeposit);
            //账号金额预扣
            storefront.setTotalAccount(storefront.getTotalAccount()-surplusMoney);
            storefront.setSurplusMoney(storefront.getSurplusMoney()-surplusMoney);
            istorefrontMapper.updateByPrimaryKeySelective(storefront);
            return ServerResponse.createBySuccessMessage("提交成功待审核中");
        } catch (Exception e) {
            logger.error("店铺收支记录异常：", e);
            return ServerResponse.createByErrorMessage("店铺收支记录异常");
        }
    }

    /**
     *店铺充值
     * @param userId
     * @param cityId
     * @param payState
     * @param rechargeAmount
     * @param payPassword
     * @param businessOrderType
     * @param sourceType
     * @return
     */
    public ServerResponse operationStorefrontRecharge(String userId, String cityId, String payState, Double rechargeAmount, String payPassword, String businessOrderType, Integer sourceType) {
        try {
                MainUser mainUser=null;
                DjSupplierPayOrder djSupplierPayOrder = new DjSupplierPayOrder();
                if(sourceType==1) {
                    Storefront storefront = storefrontService.queryStorefrontByUserID(userId, cityId);
                    if (storefront == null) {
                        return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
                    }
                    mainUser = iStoreUserMapper.selectByPrimaryKey(storefront.getUserId());
                    djSupplierPayOrder.setSupplierId(storefront.getId());
                }else if(sourceType==2){
                    Example example=new Example(Storefront.class);
                    example.createCriteria().andEqualTo(Storefront.DATA_STATUS,0)
                            .andEqualTo(Storefront.CITY_ID,cityId)
                            .andEqualTo(Storefront.USER_ID,userId);
                    Storefront storefront = iStoreStorefrontMapper.selectOneByExample(example);
                    mainUser = iStoreUserMapper.selectByPrimaryKey(storefront.getUserId());
                    djSupplierPayOrder.setSupplierId(storefront.getId());
                }
                if(mainUser==null) {
                    return ServerResponse.createByErrorMessage("用户不存在");
                }
                if (rechargeAmount <= 0) {
                    return ServerResponse.createByErrorMessage("金额不正确");
                }
                if (!Utils.md5(payPassword).equals(mainUser.getPayPassword())) {
                    return ServerResponse.createByErrorMessage("密码错误");
                }
                djSupplierPayOrder.setDataStatus(0);
                djSupplierPayOrder.setBusinessOrderType(businessOrderType);
                djSupplierPayOrder.setPayState(payState);
                djSupplierPayOrder.setPrice(rechargeAmount);
                djSupplierPayOrder.setState(0);
                djSupplierPayOrder.setUserId(userId);
                djSupplierPayOrder.setSourceType(sourceType);
                djSupplierPayOrderMapper.insert(djSupplierPayOrder);

                // 生成支付业务单
                Example example = new Example(BusinessOrder.class);
                example.createCriteria().andEqualTo(BusinessOrder.TASK_ID, djSupplierPayOrder.getId()).andNotEqualTo(BusinessOrder.STATE, 4);
                List<BusinessOrder> businessOrderList = iStoreBusinessOrderMapper.selectByExample(example);
                BusinessOrder businessOrder = null;
                if (businessOrderList.size() > 0) {
                    businessOrder = businessOrderList.get(0);
                    if (businessOrder.getState() == 3) {
                        return ServerResponse.createByErrorMessage("该订单已支付，请勿重复支付！");
                    }
                }
                if (businessOrderList.size() == 0) {
                    businessOrder = new BusinessOrder();
                    businessOrder.setMemberId(djSupplierPayOrder.getUserId());
                    businessOrder.setNumber(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
                    businessOrder.setState(1);//刚生成
                    businessOrder.setTotalPrice(new BigDecimal(rechargeAmount));
                    businessOrder.setDiscountsPrice(new BigDecimal(0));
                    businessOrder.setPayPrice(new BigDecimal(rechargeAmount));
                    businessOrder.setType(3);//记录支付类型任务类型
                    businessOrder.setTaskId(djSupplierPayOrder.getId());//保存任务ID
                    iStoreBusinessOrderMapper.insert(businessOrder);
                }
                djSupplierPayOrder.setBusinessOrderNumber(businessOrder.getNumber());
                djSupplierPayOrderMapper.updateByPrimaryKeySelective(djSupplierPayOrder);
                return ServerResponse.createBySuccess("提交成功",djSupplierPayOrder.getBusinessOrderNumber());
        } catch (Exception e) {
            logger.error("店铺收支记录异常：", e);
            return ServerResponse.createByErrorMessage("店铺收支记录异常");
        }
    }


    /**
     * 店铺财务-供应商结算
     * @param pageDTO
     * @param userId
     * @param cityId
     * @return
     */
    public ServerResponse queryStoreSupplierSettlement(PageDTO pageDTO, String userId, String cityId, String searchKey) {
        try {
            Storefront storefront = this.queryStorefrontByUserID(userId, cityId);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<WebSplitDeliverItemDTO> webSplitDeliverItemDTOS =
                    istorefrontMapper.queryStoreSupplierSettlement(storefront.getId(),searchKey);
            PageInfo pageResult = new PageInfo(webSplitDeliverItemDTOS);
            return ServerResponse.createBySuccess("查询成功",pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     *店铺-收入记录
     * @param request
     * @param pageDTO
     * @param userId
     * @param cityId
     * @param houseOrderId
     * @return
     */
    public ServerResponse storeExpenseRecord(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId, String houseOrderId) {
        try {

            Storefront storefront = this.queryStorefrontByUserID(userId, cityId);
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<StoreExpenseRecordDTO>  list=istorefrontMapper.selectStoreExpenseRecord(houseOrderId,storefront.getId());
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功",pageResult);

        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
         *店铺-支出记录
         * @param request
         * @param pageDTO
         * @param userId
         * @param cityId
         * @param houseOrderId
         * @return
         */
        public ServerResponse storeRevenueRecord(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId, String houseOrderId) {
            try {
               return null;
                //return ServerResponse.createBySuccess("查询成功",pageResult);
            } catch (Exception e) {
                e.printStackTrace();
                return ServerResponse.createByErrorMessage("查询失败");
            }
    }

    /**
     *店铺-收入记录-货单详情
     * @param request
     * @param pageDTO
     * @param userId
     * @param cityId
     * @param houseOrderId
     * @return
     */
    public ServerResponse storeExpenseRecordOrderDetail(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId, String houseOrderId) {
        return null;
    }

    /**
     *店铺铺-支出记录-查看货单详情
     * @param request
     * @param pageDTO
     * @param userId
     * @param cityId
     * @param houseOrderId
     * @return
     */
    public ServerResponse storeRevenueRecordOrderDetail(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId, String houseOrderId) {
        return null ;
    }





    /**
     *店铺-收入记录-查看清单
     * @param request
     * @param pageDTO
     * @param userId
     * @param cityId
     * @param houseOrderId
     * @return
     */
    public ServerResponse storeExpenseRecordGoodDetail(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId, String houseOrderId) {
        return null;
    }

}
