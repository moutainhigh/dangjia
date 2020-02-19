package com.dangjia.acg.service.storefront;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.supplier.DjSupplierAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.MathUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.finance.WebSplitDeliverItemDTO;
import com.dangjia.acg.dto.storefront.*;
import com.dangjia.acg.dto.supplier.AccountFlowRecordDTO;
import com.dangjia.acg.dto.supplier.DjSupplierDeliverDTO;
import com.dangjia.acg.dto.supplier.DjSupplierDeliverDTOList;
import com.dangjia.acg.mapper.storefront.*;
import com.dangjia.acg.model.Config;
import com.dangjia.acg.modle.account.AccountFlowRecord;
import com.dangjia.acg.modle.deliver.Order;
import com.dangjia.acg.modle.deliver.SplitDeliver;
import com.dangjia.acg.modle.other.BankCard;
import com.dangjia.acg.modle.other.City;
import com.dangjia.acg.modle.pay.BusinessOrder;
import com.dangjia.acg.modle.receipt.Receipt;
import com.dangjia.acg.modle.repair.MendDeliver;
import com.dangjia.acg.modle.repair.MendOrder;
import com.dangjia.acg.modle.storefront.Storefront;
import com.dangjia.acg.modle.storefront.StorefrontConfig;
import com.dangjia.acg.modle.supplier.DjSupplier;
import com.dangjia.acg.modle.supplier.DjSupplierPayOrder;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.modle.worker.WithdrawDeposit;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.fabric.Server;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Autowired
    private IStorefrontAccountFlowRecordMapper storefrontAccountFlowRecordMapper ;

    @Autowired
    private IShopReceiptMapper iReceiptMapper;

    @Autowired
    private IStorefrontSplitDeliverMapper iStorefrontSplitDeliverMapper;
    @Autowired
    private StoreConfigService storeConfigService;
    @Autowired
    private IShopSupplierMapper iShopSupplierMapper;
    @Autowired
    private IStoreOrderMapper iStoreOrderMapper;

    /**
     * 获取需缴纳的滞留金
     * @param userId 用户ID
     * @param cityId 城市ID
     * @param type 类型
     * @return 类型：1店铺，2供应商
     */
    public ServerResponse getNeedRetentionMoney(String userId,String cityId,Integer type){
        Double needRetentionMoney=0d;
        Double totalRetentionMoney=2000d;
        Example example;
        if(type==1){//店铺
            Storefront storefront =storefrontService.queryStorefrontByUserID(userId,cityId);
            Config config=storeConfigService.selectConfigInfoByParamKey("SHOP_RETENTION_MONEY");//获取滞留金缴纳金额
            if(config!=null&& cn.jiguang.common.utils.StringUtils.isNotEmpty(config.getParamValue())){
                totalRetentionMoney=Double.parseDouble(config.getParamValue());
            }
            if(storefront!=null&&storefront.getRetentionMoney()<totalRetentionMoney){
                needRetentionMoney= MathUtil.sub(totalRetentionMoney,storefront.getRetentionMoney());
            }
        }else if(type==2){//供应商
            example=new Example(DjSupplier.class);
            example.createCriteria().andEqualTo(DjSupplier.CITY_ID,cityId)
                    .andEqualTo(DjSupplier.USER_ID,userId)
                    .andEqualTo(DjSupplier.DATA_STATUS,0);
            DjSupplier djSupplier = iShopSupplierMapper.selectOneByExample(example);
            Config config=storeConfigService.selectConfigInfoByParamKey("STORE_RETENTION_MONEY");//获取滞留金缴纳金额
            if(config!=null&& cn.jiguang.common.utils.StringUtils.isNotEmpty(config.getParamValue())){
                totalRetentionMoney=Double.parseDouble(config.getParamValue());
            }
            if(djSupplier!=null&&djSupplier.getRetentionMoney()<totalRetentionMoney){
                needRetentionMoney= MathUtil.sub(totalRetentionMoney,djSupplier.getRetentionMoney());
            }
        }
        return ServerResponse.createBySuccess("查询成功",needRetentionMoney);
    }

    /**
     * 获取当前滞留金信息
     * @param userId 用户ID
     * @param cityId 城市ID
     * @param type 类型
     * @return 类型：1店铺，2供应商
     */
    public ServerResponse getRetentionMoneyInfo(String userId,String cityId,Integer type){
        try{
            Map<String,Object> resultMap=new HashMap<>();
            Double needRetentionMoney=0d;//所需滞留金
            Double totalRetentionMoney=2000d;//应滞留金
            Example example;
            if(type==1){//店铺
                Storefront storefront =storefrontService.queryStorefrontByUserID(userId,cityId);
                Config config=storeConfigService.selectConfigInfoByParamKey("SHOP_RETENTION_MONEY");//获取滞留金缴纳金额
                if(config!=null&& StringUtils.isNotBlank(config.getParamValue())){
                    totalRetentionMoney=Double.parseDouble(config.getParamValue());
                }
                if(storefront!=null&&storefront.getRetentionMoney()<totalRetentionMoney){
                    needRetentionMoney= MathUtil.sub(totalRetentionMoney,storefront.getRetentionMoney());
                }
                resultMap.put("needRetentionMoney",needRetentionMoney);
                resultMap.put("totalRetentionMoney",totalRetentionMoney);
                resultMap.put("retentionMoney",storefront.getRetentionMoney());//当前滞留金

            }else if(type==2){//供应商
                example=new Example(DjSupplier.class);
                example.createCriteria().andEqualTo(DjSupplier.CITY_ID,cityId)
                        .andEqualTo(DjSupplier.USER_ID,userId)
                        .andEqualTo(DjSupplier.DATA_STATUS,0);
                DjSupplier djSupplier = iShopSupplierMapper.selectOneByExample(example);
                Config config=storeConfigService.selectConfigInfoByParamKey("STORE_RETENTION_MONEY");//获取滞留金缴纳金额
                if(config!=null&& cn.jiguang.common.utils.StringUtils.isNotEmpty(config.getParamValue())){
                    totalRetentionMoney=Double.parseDouble(config.getParamValue());
                }
                if(djSupplier!=null&&djSupplier.getRetentionMoney()<totalRetentionMoney){
                    needRetentionMoney= MathUtil.sub(totalRetentionMoney,djSupplier.getRetentionMoney());
                }
                resultMap.put("needRetentionMoney",needRetentionMoney);
                resultMap.put("totalRetentionMoney",totalRetentionMoney);
                resultMap.put("retentionMoney",djSupplier.getRetentionMoney());//当前滞留金
            }
            return ServerResponse.createBySuccess("查询成功",resultMap);

        }catch (Exception e){
            logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }


    }

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
            if(list.size()<=0){
                return ServerResponse.createByErrorMessage("没有检索到店铺信息数据");
            }
            Storefront storefront=list.get(0);
            StorefrontDTO storefrontDTO = getStorefrontDTO(storefront);
            //查询运费
            Example exampleFreight=new Example(StorefrontConfig.class);
            exampleFreight.createCriteria().andEqualTo(StorefrontConfig.STOREFRONT_ID,storefront.getId()).andEqualTo(StorefrontConfig.PARAM_KEY,StorefrontConfig.FREIGHT);
            List<StorefrontConfig> listFreight=iStorefrontConfigMapper.selectByExample(exampleFreight);
            if(listFreight!=null)
            storefrontDTO.setFreight(listFreight.get(0).getParamValue());
            //查询符合收取运费的条件
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


    /**
     * 编辑店铺信息
     * @param storefrontDTO
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse updateStorefront(StorefrontDTO storefrontDTO) {

        if(storefrontDTO==null|| StringUtils.isBlank(storefrontDTO.getUserId())) {
            return ServerResponse.createByErrorMessage("用户信息不能为空");
        }
        if(storefrontDTO==null||StringUtils.isBlank(storefrontDTO.getCityId())) {
            return ServerResponse.createByErrorMessage("城市信息不能为空");
        }
        //判断当前城市，当前用户的店铺是否存在
        Example exampleStorefront=new Example(Storefront.class);
        exampleStorefront.createCriteria().andEqualTo(Storefront.USER_ID,storefrontDTO.getUserId()).
                andEqualTo(Storefront.CITY_ID, storefrontDTO.getCityId());
        List<Storefront> list =istorefrontMapper.selectByExample(exampleStorefront);
        if(list.size()<=0){//不存在，则添加
            Storefront storefront=getStorefront(new Storefront(),storefrontDTO);
            istorefrontMapper.insertSelective(storefront);//添加店铺信息
            if (StringUtil.isNotEmpty(storefrontDTO.getFreight())){//添加运费
                insertStorefrontConfig(storefront.getId(),StorefrontConfig.FREIGHT,storefrontDTO.getFreight(),storefrontDTO.getCityId());
            }
            if (StringUtil.isNotEmpty(storefrontDTO.getBelowUnitPrice())){//添加符合收取运费的条件
                insertStorefrontConfig(storefront.getId(),StorefrontConfig.FREIGHT_TERMS,storefrontDTO.getBelowUnitPrice(),storefrontDTO.getCityId());
            }

        }else{//否则，则修改
            Storefront storefront=getStorefront(list.get(0),storefrontDTO);
            istorefrontMapper.updateByPrimaryKeySelective(storefront);
            //收取运费
            if (StringUtil.isNotEmpty(storefrontDTO.getFreight())){
                Storefront mystorefront =list.get(0);
                Example example1=new Example(StorefrontConfig.class);
                example1.createCriteria().andEqualTo(StorefrontConfig.STOREFRONT_ID,mystorefront.getId()).andEqualTo(StorefrontConfig.PARAM_KEY,StorefrontConfig.FREIGHT);
                List<StorefrontConfig> list1=iStorefrontConfigMapper.selectByExample(example1);
                if (list1!=null&&list1.size()>0){//修改运费
                    StorefrontConfig storefrontConfig=list1.get(0);
                    storefrontConfig.setStorefrontId(mystorefront.getId());
                    storefrontConfig.setParamKey(StorefrontConfig.FREIGHT);
                    storefrontConfig.setParamValue(storefrontDTO.getFreight());
                    iStorefrontConfigMapper.updateByPrimaryKeySelective(storefrontConfig);
                } else {//添加运费
                    insertStorefrontConfig(storefront.getId(),StorefrontConfig.FREIGHT,storefrontDTO.getFreight(),storefrontDTO.getCityId());
                }
            }
            //每单价格低于
            if (StringUtil.isNotEmpty(storefrontDTO.getBelowUnitPrice())) {
                Storefront mystorefront =list.get(0);
                Example example1=new Example(StorefrontConfig.class);
                example1.createCriteria().andEqualTo(StorefrontConfig.STOREFRONT_ID,mystorefront.getId()).andEqualTo(StorefrontConfig.PARAM_KEY,StorefrontConfig.FREIGHT_TERMS);
                List<StorefrontConfig> list1=iStorefrontConfigMapper.selectByExample(example1);
                if (list1!=null&&list1.size()>0) {//修改符合收取运费的条件
                    StorefrontConfig storefrontConfig = list1.get(0);
                    storefrontConfig.setStorefrontId(mystorefront.getId());
                    storefrontConfig.setParamKey(StorefrontConfig.FREIGHT_TERMS);
                    storefrontConfig.setParamValue(storefrontDTO.getBelowUnitPrice());
                    iStorefrontConfigMapper.updateByPrimaryKeySelective(storefrontConfig);
                }else{//添加符合收取运费的条件
                    insertStorefrontConfig(storefront.getId(),StorefrontConfig.FREIGHT_TERMS,storefrontDTO.getBelowUnitPrice(),storefrontDTO.getCityId());
                }

            }
        }
        return ServerResponse.createBySuccessMessage("保存成功!");
    }

    //封装店铺参数信息
    private Storefront getStorefront(Storefront storefront,StorefrontDTO storefrontDTO){
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
        return  storefront;
    }

    /**
     * 店铺信息配置
     * @param storefrontId
     * @param paramkey
     * @param paramValue
     * @param cityId
     */
    private void insertStorefrontConfig(String storefrontId,String paramkey,String paramValue,String cityId){
        StorefrontConfig storefrontConfig=new StorefrontConfig();
        storefrontConfig.setStorefrontId(storefrontId);
        storefrontConfig.setParamKey(paramkey);
        storefrontConfig.setParamValue(paramValue);
        storefrontConfig.setCityId(cityId);
        iStorefrontConfigMapper.insert(storefrontConfig);
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
     * @param userId
     * @param cityId
     * @return
     */
    public ServerResponse queryStorefrontWallet( String userId, String cityId) {
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
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse operationStorefrontReflect(String userId, String cityId, String bankCard, Double surplusMoney, String payPassword) {

        AccountFlowRecord accountFlowRecord = new AccountFlowRecord();
        Storefront storefront = storefrontService.queryStorefrontByUserID(userId, cityId);
        if (storefront == null) {
            return ServerResponse.createByErrorMessage("不存在店铺信息，请先维护店铺信息");
        }
        if (surplusMoney > storefront.getSurplusMoney()) {
            return ServerResponse.createByErrorMessage("提现金额超过可提现金额");
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
        withdrawDeposit.setSourceId(storefront.getId());//来源id(供应商/店铺id)
        istorefrontWithdrawDepositMapper.insert(withdrawDeposit);
        //账号金额预扣
        storefront.setTotalAccount(storefront.getTotalAccount()-surplusMoney);
        storefront.setSurplusMoney(storefront.getSurplusMoney()-surplusMoney);
        istorefrontMapper.updateByPrimaryKeySelective(storefront);
        //生成流水
        accountFlowRecord.setState(1);//0订单收入,1提现,2自定义增加金额,3自定义减少金额
        accountFlowRecord.setHouseOrderId(withdrawDeposit.getId());
        accountFlowRecord.setDefinedAccountId(storefront.getId());//自定义账户流水id
        accountFlowRecord.setCreateBy(userId);
        accountFlowRecord.setFlowType("1");//类型:（1店铺，2供应商）
        accountFlowRecord.setMoney(surplusMoney);//本次金额
        accountFlowRecord.setAmountAfterMoney(storefront.getTotalAccount());//入账后金额
        accountFlowRecord.setDefinedName("店铺提现：" + surplusMoney);//自定义流水说明
        storefrontAccountFlowRecordMapper.insert(accountFlowRecord);
        return ServerResponse.createBySuccessMessage("提现申请成功");
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
    @Transactional(rollbackFor = Exception.class)
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
//            if(businessOrderType.equals("2") && rechargeAmount<2000){
//                return ServerResponse.createByErrorMessage("滞留金交纳不小于2000");
//            }
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
            logger.error("店铺异常：", e);
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
            List<StoreExpenseRecordDTO>  list=istorefrontMapper.selectStoreExpenseRecord(orderNumber,storefront.getId(),null);
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功",pageResult);
        } catch (Exception e) {
            logger.error("查询失败",e);
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
            if(list!=null){
                list.forEach(storeRevenueRecordDTO->{
                    String type=  storeRevenueRecordDTO.getState();
                    String anyOrderId=storeRevenueRecordDTO.getAnyOrderId();
                    if (type.equals("8")){//退货单ID
                        MendDeliver mendDeliver=ishopMendDeliverMapper.selectByPrimaryKey(anyOrderId);
                        if(mendDeliver!=null)
                            storeRevenueRecordDTO.setNumber(mendDeliver.getNumber());
                    }else if(type.equals(9)){//回执单号
                        Receipt receipt=iReceiptMapper.selectByPrimaryKey(anyOrderId);
                        if(receipt!=null)
                            storeRevenueRecordDTO.setNumber(receipt.getNumber());
                    }
                });
            }

            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功",pageResult);
        } catch (Exception e) {
           logger.error("查询失败",e);
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
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<ExpenseRecordOrderDetailDTO> list=istorefrontMapper.storeExpenseRecordOrderDetail(orderId);
            String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            Double totalPrice=0.0;
            for (ExpenseRecordOrderDetailDTO expenseRecordOrderDetailDTO :list) {
                expenseRecordOrderDetailDTO.setImageDetail(StringTool.getImage(expenseRecordOrderDetailDTO.getImage(),address));
                expenseRecordOrderDetailDTO.setSurplusCount(MathUtil.sub(MathUtil.sub(expenseRecordOrderDetailDTO.getShopCount(),expenseRecordOrderDetailDTO.getAskCount()),expenseRecordOrderDetailDTO.getReturnCount()));//剩余量=购买量-要货量-仅退款量
                totalPrice=MathUtil.add(totalPrice,expenseRecordOrderDetailDTO.getTotalPrice());
            }
            PageInfo pageResult = new PageInfo(list);
            //查询对应的汇总费用信息
            Order order=iStoreOrderMapper.selectByPrimaryKey(orderId);
            List<StoreExpenseRecordDTO>  recordDTOList=istorefrontMapper.selectStoreExpenseRecord(order.getOrderNumber(),order.getStorefontId(),orderId);;
            Map<String,Object> map=new HashMap<>();
            if(recordDTOList!=null){
                StoreExpenseRecordDTO storeExpenseRecordDTO=recordDTOList.get(0);
                map=BeanUtils.beanToMap(storeExpenseRecordDTO);
            }
            map.put("totalPrice",totalPrice);//商品小计
            map.put("orderItemList",pageResult);
            return ServerResponse.createBySuccess("查询成功",map);
        } catch (Exception e) {
           logger.error("查询失败",e);
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
     * @param accountFlowRecordId
     * @param type 1提现，9结算
     * @return
     */
    public ServerResponse storeRevenueRecordOrderDetail(String accountFlowRecordId,Integer type) {
        try {

            AccountFlowRecord accountFlowRecord=storefrontAccountFlowRecordMapper.selectByPrimaryKey(accountFlowRecordId);
            if(accountFlowRecord==null){
                return ServerResponse.createByErrorMessage("未找到符合条件的数据");
            }
            /*1: 提现 9：合并结算4：体现*/
            if (type==null){
                return ServerResponse.createByErrorMessage("类型参数不能为空");
            }
            String address = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            if(type==1){//提现，查询回执
                WithdrawDeposit withdrawDeposit=istorefrontWithdrawDepositMapper.selectByPrimaryKey(accountFlowRecord.getHouseOrderId());
                Map<String,Object> resultMap=new HashMap<>();
                resultMap.put("image",withdrawDeposit.getImage());
                resultMap.put("imageUrl",address+withdrawDeposit.getImage());
                resultMap.put("reason",withdrawDeposit.getReason());//原因
                return ServerResponse.createBySuccess("查询成功",resultMap);
            }else if(type==9){//合并结算
                 Receipt receipt=iShopReceiptMapper.selectByPrimaryKey(accountFlowRecord.getHouseOrderId());
                 JSONArray itemObjArr = JSON.parseArray(receipt.getImage());
                 if (receipt==null)
                     return ServerResponse.createByErrorMessage("没有查询到结算回执");
                //JSONArray itemObjArr = JSON.parseArray(receipt.getImage());
                 DjSupplierDeliverDTOList djSupplierDeliverDTOList=new DjSupplierDeliverDTOList();
                 djSupplierDeliverDTOList.setCreateDate(receipt.getCreateDate());
                 List<DjSupplierDeliverDTO> djSupplierDeliverDTOS = iStorefrontSplitDeliverMapper.selectItemListbyReceiptNumber(receipt.getNumber());//查询结算单明细(发货单或要货单的列表)
                 djSupplierDeliverDTOList.setDjSupplierDeliverDTOList(djSupplierDeliverDTOS);
                 djSupplierDeliverDTOList.setImageArr(itemObjArr);
                 djSupplierDeliverDTOList.setTotalMoney(receipt.getTotalAmount());
                 DjSupplier djSupplier=iShopSupplierMapper.selectByPrimaryKey(receipt.getSupplierId());
                 djSupplierDeliverDTOList.setName(djSupplier.getName());
                 djSupplierDeliverDTOList.setTelephone(djSupplier.getTelephone());
                 return ServerResponse.createBySuccess("查询成功", djSupplierDeliverDTOList);
             }

            return null;
        } catch (Exception e) {
           logger.error("查询失败",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 店铺-计算可提现金额
     * 1.提现限制：每笔收入限制7天才可提现
     * 2.提现金额不可超过可提现金额
     * 3.全部提现：点击默认输入全部可提现金额
     * 4.验证支付密码才可提现（6位数字）
     */
    public void setStorefrontSurplusMoney() {
        try{
            //1.按城市划分有多少个城市的店铺
            List<String> cityList=istorefrontMapper.selectCityList();
            if(cityList!=null&&cityList.size()>0){
                for(String cityId:cityList){
                    //根据城市修改店铺的可提现余额
                    istorefrontMapper.setStorefrontSurplusMoney(cityId);
                }
            }

        }catch (Exception e){
          logger.error("计算可提现金额异常：");
        }
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
            example=new Example(Storefront.class);
            example.createCriteria().andEqualTo(Storefront.USER_ID,mainUser.getId())
            .andEqualTo(Storefront.CITY_ID,storefrontDTO.getCityId());
            Storefront st= istorefrontMapper.selectOneByExample(example);
            if(st!=null&&!"worker".equals(st.getStorefrontType())){
                return ServerResponse.createByErrorMessage("此电话用户下已有其他店铺信息在维护，请核实!");
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
            storefront.setIfDjselfManage("1");
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
