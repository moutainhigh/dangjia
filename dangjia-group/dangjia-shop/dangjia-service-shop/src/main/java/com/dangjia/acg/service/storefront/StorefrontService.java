package com.dangjia.acg.service.storefront;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.supplier.DjSupplierAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.finance.WebSplitDeliverItemDTO;
import com.dangjia.acg.dto.storefront.*;
import com.dangjia.acg.dto.supplier.AccountFlowRecordDTO;
import com.dangjia.acg.dto.supplier.DjSupplierDeliverDTO;
import com.dangjia.acg.dto.supplier.DjSupplierDeliverDTOList;
import com.dangjia.acg.mapper.storefront.*;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.dangjia.acg.modle.other.BankCard;
import com.dangjia.acg.modle.other.City;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.receipt.Receipt;
import com.dangjia.acg.modle.repair.MendDeliver;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.storefront.StorefrontConfig;
import com.dangjia.acg.modle.supplier.DjSupplier;
import com.dangjia.acg.modle.supplier.DjSupplierPayOrder;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.modle.worker.WithdrawDeposit;
import com.dangjia.acg.util.Utils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

@Service
public class StorefrontService {

    /**
     * 声明日志
     */
    private static Logger logger = LoggerFactory.getLogger(StorefrontService.class);
    @Autowired
    private IStorefrontWithdrawDepositMapper istorefrontWithdrawDepositMapper;
    @Autowired
    private IStorefrontMapper istorefrontMapper;
    @Autowired
    private DjSupplierAPI djSupplierAPI;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private StorefrontService storefrontService;
    @Autowired
    private IStorefrontUserMapper istorefrontUserMapper;
    @Autowired
    private IStorefrontBusinessOrderMapper istorefrontBusinessOrderMapper;

    @Autowired
    private DjShopSupplierPayOrderMapper djShopSupplierPayOrderMapper;

    @Autowired
    private IStorefrontConfigMapper iStorefrontConfigMapper;

    @Autowired
    private IShopReceiptMapper iShopReceiptMapper;
    @Autowired
    private IShopSplitDeliverMapper ishopSplitDeliverMapper;
    @Autowired
    private IShopMendDeliverMapper ishopMendDeliverMapper;
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
            Example example=new Example(Storefront.class);
            example.createCriteria().andEqualTo(Storefront.USER_ID,userId).
                    andEqualTo(Storefront.CITY_ID,cityId);
            List<Storefront> list =istorefrontMapper.selectByExample(example);
            if(list.size()<=0)
            {
                return ServerResponse.createByErrorMessage("没有检索到店铺信息数据");
            }
            Storefront storefront=list.get(0);
            StorefrontDTO storefrontDTO = getStorefrontDTO(storefront);

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

    private StorefrontDTO getStorefrontDTO(Storefront storefront){
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);

        StorefrontDTO storefrontDTO =new StorefrontDTO();
        storefrontDTO.setId(storefront.getId());
        storefrontDTO.setCityId(storefront.getCityId());
        storefrontDTO.setUserId(storefront.getUserId());
        storefrontDTO.setStorefrontName(storefront.getStorefrontName());
        storefrontDTO.setStorefrontAddress(storefront.getStorefrontAddress());
        storefrontDTO.setStorefrontDesc(storefront.getStorefrontDesc());
        storefrontDTO.setStorefrontLogo(storefront.getStorefrontLogo());
        if(StringUtils.isNotEmpty(storefront.getStorefrontLogo())){
            storefrontDTO.setStorefrontLogoUrl(address+storefront.getStorefrontLogo());
        }
        storefrontDTO.setStorefrontSigleLogo(storefront.getStorefrontLogo());
        storefrontDTO.setStorekeeperName(storefront.getStorekeeperName());
        storefrontDTO.setMobile(storefront.getMobile());
        storefrontDTO.setEmail(storefront.getEmail());
        return storefrontDTO;
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



    public ServerResponse updateStorefront(StorefrontDTO storefrontDTO) {

        try {

            if(storefrontDTO==null|| StringUtils.isBlank(storefrontDTO.getUserId()))
            {
                return ServerResponse.createByErrorMessage("用户编号不能为空");
            }
            if(storefrontDTO==null||StringUtils.isBlank(storefrontDTO.getCityId()))
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
                if(!CommonUtil.isEmpty(storefrontListDTO.getStorefrontLogo())) {
                    storefrontListDTO.setStorefrontLogoUrl(imageaddress + storefrontListDTO.getStorefrontLogo());
                }
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
            MainUser mainUser = istorefrontUserMapper.selectByPrimaryKey(storefront.getUserId());
            if (!DigestUtils.md5Hex(payPassword).equals(mainUser.getPayPassword())){
                return ServerResponse.createByErrorMessage("密码错误");
            }
            //提现申请
            WithdrawDeposit withdrawDeposit = new WithdrawDeposit();
            withdrawDeposit.setMoney(new BigDecimal(surplusMoney));
            withdrawDeposit.setName(storefront.getStorekeeperName());
            withdrawDeposit.setWorkerId(mainUser.getId());
            withdrawDeposit.setState(0);//0未处理,1同意 2不同意(驳回)
            withdrawDeposit.setRoleType(5);//1：业主端  2 大管家 3：工匠端 4：供应商 5：店铺
            withdrawDeposit.setCardNumber(bankCard);
            BankCard bankCard1 = istorefrontWithdrawDepositMapper.queryBankCard(bankCard, mainUser.getId());
            withdrawDeposit.setBankName(bankCard1.getBankName());
            withdrawDeposit.setDataStatus(0);//数据状态 0=正常，1=删除
            withdrawDeposit.setSourceId(storefront.getId());
            istorefrontWithdrawDepositMapper.insert(withdrawDeposit);
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
            //sourceType来源类型 1：供应商 2：店铺
            MainUser mainUser = null;
            DjSupplierPayOrder djSupplierPayOrder = new DjSupplierPayOrder();
            if (sourceType == 1) {
                Storefront storefront = storefrontService.queryStorefrontByUserID(userId, cityId);
                if (storefront == null) {
                    return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
                }
                mainUser = istorefrontUserMapper.selectByPrimaryKey(storefront.getUserId());
                djSupplierPayOrder.setSupplierId(storefront.getId());
            } else if (sourceType == 2) {
                Example example = new Example(Storefront.class);
                example.createCriteria().andEqualTo(Storefront.DATA_STATUS, 0)
                        .andEqualTo(Storefront.CITY_ID, cityId)
                        .andEqualTo(Storefront.USER_ID, userId);
                Storefront storefront = istorefrontMapper.selectOneByExample(example);
                if(storefront==null)
                    return ServerResponse.createByErrorMessage("店铺不存在");
                mainUser = istorefrontUserMapper.selectByPrimaryKey(storefront.getUserId());
                djSupplierPayOrder.setSupplierId(storefront.getId());
            }
            if (mainUser == null) {
                return ServerResponse.createByErrorMessage("用户不存在");
            }
            if (rechargeAmount <= 0) {
                return ServerResponse.createByErrorMessage("金额不正确");
            }
            if (!DigestUtils.md5Hex(payPassword).equals(mainUser.getPayPassword())) {
                return ServerResponse.createByErrorMessage("密码错误");
            }
            if(businessOrderType.equals("2") && rechargeAmount<2000){
                return ServerResponse.createByErrorMessage("滞留金交纳不小于2000");
            }
            djSupplierPayOrder.setDataStatus(0);
            djSupplierPayOrder.setBusinessOrderType(businessOrderType);
            djSupplierPayOrder.setPayState(payState);
            djSupplierPayOrder.setPrice(rechargeAmount);
            djSupplierPayOrder.setState(0);//支付状态：0申请中,1已支付,2支付失败
            djSupplierPayOrder.setUserId(userId);
            djSupplierPayOrder.setSourceType(sourceType);
            djShopSupplierPayOrderMapper.insert(djSupplierPayOrder);
            // 生成支付业务单
            Example example = new Example(BusinessOrder.class);
            example.createCriteria().andEqualTo(BusinessOrder.TASK_ID, djSupplierPayOrder.getId()).andNotEqualTo(BusinessOrder.STATE, 4);
            List<BusinessOrder> businessOrderList = istorefrontBusinessOrderMapper.selectByExample(example);
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
                istorefrontBusinessOrderMapper.insert(businessOrder);
            }
            djSupplierPayOrder.setBusinessOrderNumber(businessOrder.getNumber());
            djShopSupplierPayOrderMapper.updateByPrimaryKeySelective(djSupplierPayOrder);
            return ServerResponse.createBySuccess("提交成功", djSupplierPayOrder.getBusinessOrderNumber());
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
     * @param orderNumber
     * @return
     */
    public ServerResponse storeExpenseRecord(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId, String orderNumber) {
        try {

            Storefront storefront = this.queryStorefrontByUserID(userId, cityId);
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<StoreExpenseRecordDTO>  list=istorefrontMapper.selectStoreExpenseRecord(orderNumber,storefront.getId());
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功",pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 店铺-支出记录
     *
     * @param request
     * @param pageDTO
     * @param userId
     * @param cityId
     * @param     orderNumber
     * @return
     */
    public ServerResponse storeRevenueRecord(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId, String orderNumber) {
        try {

            Storefront storefront = this.queryStorefrontByUserID(userId, cityId);
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<StoreRevenueRecordDTO> list=istorefrontMapper.queryStoreRevenueRecord(storefront.getId(),orderNumber);
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功",pageResult);
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
     * @return
     */
    public ServerResponse storeExpenseRecordOrderDetail(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId,String orderId) {
        try {
            Storefront storefront = this.queryStorefrontByUserID(userId, cityId);
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<ExpenseRecordOrderDetailDTO> list=istorefrontMapper.storeExpenseRecordOrderDetail(storefront.getId(),orderId);
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            for (ExpenseRecordOrderDetailDTO expenseRecordOrderDetailDTO :list) {
                expenseRecordOrderDetailDTO.setImageDetail(address+expenseRecordOrderDetailDTO.getImage());
              String productId=  expenseRecordOrderDetailDTO.getProductId();
              String houseId=expenseRecordOrderDetailDTO.getHouseId();
              //要货详情
              List<StoreOrderSplitItemDTO> storeOrderSplitItemlist=  istorefrontMapper.queryStoreOrderSplitItem(storefront.getId(),houseId,productId);
              expenseRecordOrderDetailDTO.setStoreOrderSplitItemlist(storeOrderSplitItemlist);
            }
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功",pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     *店铺-收入记录-查看清单
     * @param request
     * @param pageDTO
     * @param userId
     * @param cityId
     * @param deliverId
     * @return
     */
    public ServerResponse storeExpenseRecordGoodDetail(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId, String deliverId) {
        try {
            Storefront storefront = this.queryStorefrontByUserID(userId, cityId);
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<StoreSplitDeliverDTO> list=istorefrontMapper.queryStoreSplitDeliverDetail(deliverId);
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功",pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
    /**
     *店铺铺-支出记录-查看货单详情
     * @param request
     * @param pageDTO
     * @param userId
     * @param cityId
     * @param orderNumber
     * @return
     */
    public ServerResponse storeRevenueRecordOrderDetail(HttpServletRequest request, PageDTO pageDTO, String userId, String cityId, String orderNumber,Integer type) {
        try {
            Storefront storefront = this.queryStorefrontByUserID(userId, cityId);
            if (storefront == null) {
                return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
            }
            DjSupplier djSupplier = djSupplierAPI.querySingleDjSupplier(userId,cityId);

            /*1:  业主仅退款2：业主退货退款 3：合并结算4：体现*/
            if (type==null)
            {
                return ServerResponse.createByErrorMessage("类型参数不能为空");
            }
            if(type==1||type==2)
            {
                //1:  业主仅退款2：业主退货退款
                PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
                List<StoreRepairMendOrderDTO> list= istorefrontMapper.queryMendOrder(storefront.getId(),orderNumber);
                String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
                Double sumPrice = 0d;
                for (StoreRepairMendOrderDTO storeRepairMendOrderDTO:list) {
                    String mid=storeRepairMendOrderDTO.getMid();
                    List<StoreRepairMendOrderDetailDTO> list2=istorefrontMapper.queryMendOrderDetail(mid);
                    for (StoreRepairMendOrderDetailDTO storeRepairMendOrderDetailDTO: list2) {
                        storeRepairMendOrderDetailDTO.setImageDetail(address+storeRepairMendOrderDetailDTO.getImage());
                        sumPrice+= storeRepairMendOrderDetailDTO.getTotalPrice()!=null?Double.parseDouble(storeRepairMendOrderDetailDTO.getTotalPrice()):0d;
                    }
                    storeRepairMendOrderDTO.setSumPrice(sumPrice);
                    storeRepairMendOrderDTO.setMendOrderDetaillist(list2);
                }
                PageInfo pageResult = new PageInfo(list);
                return ServerResponse.createBySuccess("查询成功",pageResult);
            }
             if(type==3)
             {
                // 3：合并结算
                 Receipt receipt = iShopReceiptMapper.selectByPrimaryKey(orderNumber);
                 if (receipt==null)
                     return ServerResponse.createByErrorMessage("没有查询到结算回执");
                 JSONArray jsonArr = JSONArray.parseArray(receipt.getMerge());
                 DjSupplierDeliverDTOList djSupplierDeliverDTOList=new DjSupplierDeliverDTOList();
                 djSupplierDeliverDTOList.setCreateDate(receipt.getCreateDate());
                 List<DjSupplierDeliverDTO> djSupplierDeliverDTOS = new ArrayList<>();
                 Double totalMoney=0d;
                 for (Object o : jsonArr){
                     JSONObject obj = (JSONObject) o;
                     String id = obj.getString("id");
                     Integer deliverType = obj.getInteger("deliverType");
                     DjSupplierDeliverDTO djSupplierDeliverDTO = new DjSupplierDeliverDTO();
                     if(djSupplier!=null)
                     {
                         djSupplierDeliverDTOList.setName(djSupplier.getName()!=null?djSupplier.getName():"");//供应商名称
                         djSupplierDeliverDTOList.setTelephone(djSupplier.getTelephone()!=null?djSupplier.getTelephone():"");//供应商电话
                         djSupplierDeliverDTOList.setImage(receipt.getImage());
                     }
                     //发货单
                     if (deliverType == 1) {
                         SplitDeliver splitDeliver = ishopSplitDeliverMapper.selectByPrimaryKey(id);
                         if(splitDeliver!=null) {
                             djSupplierDeliverDTO.setId(splitDeliver.getId());
                             djSupplierDeliverDTO.setShipAddress(splitDeliver.getShipAddress());
                             djSupplierDeliverDTO.setDeliverType(1);
                             djSupplierDeliverDTO.setApplyMoney(splitDeliver.getApplyMoney());
                             djSupplierDeliverDTO.setApplyState(splitDeliver.getApplyState());
                             djSupplierDeliverDTO.setTotalAmount(splitDeliver.getTotalAmount());
                             djSupplierDeliverDTO.setNumber(splitDeliver.getNumber());
                             totalMoney+=splitDeliver.getTotalAmount();
                         }
                     } else if (deliverType == 2) {//退货单
                         MendDeliver mendDeliver = ishopMendDeliverMapper.selectByPrimaryKey(id);
                         if(mendDeliver!=null) {
                             djSupplierDeliverDTO.setId(mendDeliver.getId());
                             djSupplierDeliverDTO.setShipAddress(mendDeliver.getShipAddress());
                             djSupplierDeliverDTO.setDeliverType(2);
                             djSupplierDeliverDTO.setApplyMoney(mendDeliver.getApplyMoney());
                             djSupplierDeliverDTO.setApplyState(mendDeliver.getApplyState());
                             djSupplierDeliverDTO.setTotalAmount(mendDeliver.getTotalAmount());
                             djSupplierDeliverDTO.setNumber(mendDeliver.getNumber());
                             totalMoney-=mendDeliver.getTotalAmount();
                         }
                     }
                     djSupplierDeliverDTOS.add(djSupplierDeliverDTO);
                 }
                 djSupplierDeliverDTOList.setTotalMoney(totalMoney);
                 djSupplierDeliverDTOList.setDjSupplierDeliverDTOList(djSupplierDeliverDTOS);
                 return ServerResponse.createBySuccess("查询成功", djSupplierDeliverDTOList);
             }
            if(type==4)
            {
                //4：体现
                PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
                List<AccountFlowRecordDTO> accountFlowRecordDTOList=  istorefrontMapper.storeAccountFlowRecordDTO(storefront.getId(),orderNumber);
                if(accountFlowRecordDTOList.size()<=0)
                    return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
                String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
                for (AccountFlowRecordDTO accountFlowRecordDTO: accountFlowRecordDTOList ) {
                    if(StringUtil.isNotEmpty(accountFlowRecordDTO.getImage()))
                        accountFlowRecordDTO.setImage(imageAddress+accountFlowRecordDTO.getImage());
                }
                PageInfo pageResult = new PageInfo(accountFlowRecordDTOList);
                return ServerResponse.createBySuccess("查询成功",pageResult);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 店铺-计算可提现金额
     */
    public Integer setStorefrontSurplusMoney() {
       return  istorefrontMapper.setStorefrontSurplusMoney();
    }

    /**
     * 根据城市Id查询当家虚拟店铺
     * @param cityId 在市ID
     * @return
     */
    public ServerResponse queryWorkerShopByCityId(String cityId){
        try{
            Storefront storefront=istorefrontMapper.selectShopStoreByTypeCityId(cityId,"worker");
            StorefrontDTO storefrontDTO = getStorefrontDTO(storefront);
            return ServerResponse.createBySuccess("查询成功",storefrontDTO);
        }catch (Exception e){
            logger.error("根据城市Id查询当家虚拟店铺异常：",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 修改当家虚拟店铺信息
     * @param storefrontDTO
     * @return
     */
    public ServerResponse editWorkerShopInfo(StorefrontDTO storefrontDTO){
        try{
            Example example=new Example(MainUser.class);
            example.createCriteria().andEqualTo(MainUser.MOBILE,storefrontDTO.getMobile());
            MainUser mainUser=istorefrontUserMapper.selectOneByExample(example);
            if(mainUser==null){
                return ServerResponse.createByErrorMessage("此电话用户在系统不存在，请核实!");
            }
            Storefront storefront=istorefrontMapper.selectShopStoreByTypeCityId(storefrontDTO.getCityId(),"worker");
            if(storefront==null|| StringUtils.isBlank(storefront.getId())) {
                storefront = new Storefront();
            }
            storefront.setUserId(mainUser.getId());
            storefront.setCityId(storefrontDTO.getCityId());
            storefront.setStorefrontName(storefrontDTO.getStorefrontName());
            storefront.setStorefrontAddress(storefrontDTO.getStorefrontAddress());
            storefront.setStorefrontDesc(storefrontDTO.getStorefrontDesc());
            storefront.setStorefrontLogo(storefrontDTO.getStorefrontLogo());//店铺logo暂无
            storefront.setMobile(storefrontDTO.getMobile());
            storefront.setEmail(storefrontDTO.getEmail());
            storefront.setStorekeeperName(storefrontDTO.getStorekeeperName());
            storefront.setIfDjselfManage(1);
            storefront.setStorefrontType("worker");
            storefront.setModifyDate(new Date());
            String systemlogo = configUtil.getValue(SysConfig.ORDER_DANGJIA_ICON, String.class);
            storefront.setSystemLogo(systemlogo);
            if(storefront==null|| StringUtils.isBlank(storefront.getId())) {
                istorefrontMapper.insertSelective(storefront);
            }else{
                istorefrontMapper.updateByPrimaryKeySelective(storefront);
            }
            return ServerResponse.createBySuccessMessage("保存成功");
        }catch (Exception e){

            logger.error("修改当家虚拟店铺信息异常：",e);
            return ServerResponse.createByErrorMessage("保存失败");
        }

    }


}
