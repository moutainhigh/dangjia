package com.dangjia.acg.service.data;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.ActuarialProductDTO;
import com.dangjia.acg.dto.house.HouseDTO;
import com.dangjia.acg.dto.house.HouseListDTO;
import com.dangjia.acg.dto.house.HouseOrderDetailDTO;
import com.dangjia.acg.dto.house.UserInfoDateDTO;
import com.dangjia.acg.mapper.core.IHouseWorkerMapper;
import com.dangjia.acg.mapper.core.IMasterUnitMapper;
import com.dangjia.acg.mapper.design.IDesignBusinessOrderMapper;
import com.dangjia.acg.mapper.design.IMasterQuantityRoomProductMapper;
import com.dangjia.acg.mapper.house.HouseRemarkMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.product.IMasterProductTemplateMapper;
import com.dangjia.acg.modle.brand.Unit;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.design.DesignBusinessOrder;
import com.dangjia.acg.modle.design.DesignQuantityRoomProduct;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.HouseRemark;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.service.core.TaskStackService;
import com.dangjia.acg.service.product.MasterProductTemplateService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: Ronalcheng
 * Date: 2018/10/31 0031
 * Time: 20:18
 */
@Service
public class ActuaryService {

    private static Logger logger = Logger.getLogger(ActuaryService.class);
    @Autowired
    private IHouseWorkerMapper houseWorkerMapper;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IDesignBusinessOrderMapper designBusinessOrderMapper;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private HouseRemarkMapper houseRemarkMapper;
    @Autowired
    private IMasterProductTemplateMapper iMasterProductTemplateMapper;
    @Autowired
    private IMasterUnitMapper iMasterUnitMapper;
    @Autowired
    private MasterProductTemplateService masterProductTemplateService;
    @Autowired
    private IMasterQuantityRoomProductMapper iMasterQuantityRoomProductMapper;
    @Autowired
    private TaskStackService taskStackService;
    /**
     * 查询房子精算数据
     *
     * @return
     */
    public ServerResponse getActuaryAll(HttpServletRequest request, PageDTO pageDTO,
                                        String name, String budgetOk, String workerKey,
                                        String userId,String budgetStatus,String decorationType) {
        String cityId = request.getParameter(Constants.CITY_ID);
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        String dataStatus = "0";//正常数据
        if (Integer.parseInt(budgetOk) < 0) {
            //当类型小于0时，则查询移除的数据
            dataStatus = "1";
            budgetOk = "";
        }
        List<HouseListDTO> houseList = houseMapper.getActuaryAll(cityId, budgetOk, name, workerKey, dataStatus,userId,budgetStatus,decorationType);
        PageInfo pageResult = new PageInfo(houseList);
        for (HouseListDTO houseListDTO : houseList) {
            HouseWorker houseWorker = houseWorkerMapper.getByWorkerTypeId(houseListDTO.getHouseId(), "2", 6);
            houseListDTO.setHouseWorkerId(houseWorker==null?"":houseWorker.getId());
            //查询销售名称跟手机号码
            UserInfoDateDTO userInfoDTO =houseMapper.getUserList(houseListDTO.getMemberId());
            if(userInfoDTO != null){
                houseListDTO.setUserMobile(userInfoDTO.getUserMobile());
                houseListDTO.setUsername(userInfoDTO.getUsername());
            }

            //查询备注信息 取最新一条展示
            Example example1 = new Example(HouseRemark.class);
            example1.createCriteria().andEqualTo(HouseRemark.REMARK_TYPE, 0)
                    .andEqualTo(HouseRemark.HOUSE_ID, houseListDTO.getHouseId());
            example1.orderBy(HouseRemark.CREATE_DATE).desc();
            List<HouseRemark> storeList = houseRemarkMapper.selectByExample(example1);
            if(storeList.size() > 0){
                houseListDTO.setRemarkInfo(storeList.get(0).getRemarkInfo());
                houseListDTO.setRemarkDate(storeList.get(0).getCreateDate());
            }


            houseListDTO.setShowUpdata(0);
            if (houseListDTO.getDecorationType() == 2) {
                if (houseListDTO.getBudgetOk() == 1 && houseListDTO.getDesignerOk() != 3) {
                    houseListDTO.setShowUpdata(1);
                } else if (houseListDTO.getDesignerOk() == 3) {
                    //3设计图完成后有需要改设计的
                    Example example = new Example(DesignBusinessOrder.class);
                    Example.Criteria criteria = example.createCriteria()
                            .andEqualTo(DesignBusinessOrder.DATA_STATUS, 0)
                            .andEqualTo(DesignBusinessOrder.HOUSE_ID, houseListDTO.getHouseId())
                            .andEqualTo(DesignBusinessOrder.STATUS, 1)
                            .andNotEqualTo(DesignBusinessOrder.OPERATION_STATE, 2);
                    criteria.andEqualTo(DesignBusinessOrder.TYPE, 3);
                    List<DesignBusinessOrder> designBusinessOrders = designBusinessOrderMapper.selectByExample(example);
                    if (designBusinessOrders != null && designBusinessOrders.size() > 0) {
                        DesignBusinessOrder order = designBusinessOrders.get(0);
                        if (order.getOperationState() == 0) {
                            houseListDTO.setShowUpdata(1);
                        }
                    }
                }
            }
        }
        pageResult.setList(houseList);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }
    /**
     * 查询精算的订单详情
     * @param cityId 城市ID
     * @param houseId 房子ID
     * @return
     */
    public ServerResponse getBudgetOrderDetail(String cityId,String houseId,String workerTypeId){
        try{
            logger.info("查询精算详情cityId={"+cityId+"},houseId={"+houseId+"}");
            HouseDTO houseDTO=houseMapper.getHouseDetailByHouseId(houseId);
            if(houseDTO!=null&&houseDTO.getHouseId()!=null){
                List<HouseOrderDetailDTO> houseOrderDetailDTOList=houseMapper.getBudgetOrderDetailByHouseId(houseId,workerTypeId);
                getProductList(houseOrderDetailDTOList);
                houseDTO.setOrderDetailList(houseOrderDetailDTOList);
            }
            return ServerResponse.createBySuccess("查询成功",houseDTO);
        }catch (Exception e){
            logger.error("getBudgetOrderDetail获取精算详情查询异常：",e);
            return ServerResponse.createByErrorMessage("查询异常！");
        }
    }

    /**
     * 精算设计--查询配置的设计商品
     * @param cityId
     * @return
     */
    public ServerResponse searchActuarialProductList(String cityId) {
        try {
            List<HouseOrderDetailDTO> actuarialProductAppDTOList = houseMapper.selectDesignProductList(cityId,"1");
            getProductList(actuarialProductAppDTOList);
            return ServerResponse.createBySuccess("查询成功", actuarialProductAppDTOList);
        } catch (Exception e) {
            logger.error("searchActuarialProductList查询失败:",e);
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 精算设计--保存推荐的设计商品
     * @param cityId
     * @param productStr
     * @return
     */
    public ServerResponse saveRecommendedGoods(String cityId, String houseId, String productStr){
        try{
            logger.info("批量编辑设计精算阶段的货品商品---------start----houseId={"+houseId+"}");
            JSONArray jsonArr = JSONArray.parseArray(productStr);
            //删除旧推荐商品
            Example example=new Example(DesignQuantityRoomProduct.class);
            example.createCriteria().andEqualTo(DesignQuantityRoomProduct.HOUSE_ID,houseId)
                    .andEqualTo(DesignQuantityRoomProduct.TYPE,1);
            iMasterQuantityRoomProductMapper.deleteByExample(example);
            //添加商品到对应的模板商品库中去
            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject obj = jsonArr.getJSONObject(i);
                String productTemplateId = (String)obj.get("productTemplateId");
                DesignQuantityRoomProduct designQuantityRoomProduct=new DesignQuantityRoomProduct();
                designQuantityRoomProduct.setHouseId(houseId);
                designQuantityRoomProduct.setProductId(productTemplateId);//模板商品ID
                designQuantityRoomProduct.setType(1);//推荐商品
                iMasterQuantityRoomProductMapper.insert(designQuantityRoomProduct);//添加推荐商品
            }
            //查询对应的业主ID
            House house=houseMapper.selectByPrimaryKey(houseId);
            //增加设计图纸不合格的任务
            taskStackService.inserTaskStackInfo(houseId,house.getMemberId(),"设计图纸不合格","icon/sheji.png",6,houseId);
            return ServerResponse.createBySuccess("推荐保存成功");
        }catch (Exception e){
            logger.error("推荐保存失败:",e);
            return ServerResponse.createByErrorMessage("推荐保存失败");
        }
    }

    /**
     * 查询商品对应的规格详情，单位信息
     * @param productList
     */
    private  void getProductList(List<HouseOrderDetailDTO> productList){
        if(productList!=null&&productList.size()>0){
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            for(HouseOrderDetailDTO ap:productList){
                setProductInfo(ap,address);
            }
        }
    }
    /**
     * 替换对应的信息
     * @param ap
     * @param address
     */
    private  void setProductInfo(HouseOrderDetailDTO ap,String address){
        String productTemplateId=ap.getProductTemplateId();
        DjBasicsProductTemplate pt= iMasterProductTemplateMapper.selectByPrimaryKey(productTemplateId);
        if(pt!=null&& StringUtils.isNotBlank(pt.getId())){
            String image=ap.getImage();
            if (image != null) {
                //添加图片详情地址字段
                String[] imgArr = image.split(",");
                if(imgArr!=null&&imgArr.length>0){
                    ap.setImageUrl(address+imgArr[0]);//图片详情地址设置
                }
            }

            String unitId=pt.getUnitId();
            //查询单位
            if(pt.getConvertQuality()!=null&&pt.getConvertQuality()>0){
                unitId=pt.getConvertUnit();
            }
            if(unitId!=null&& StringUtils.isNotBlank(unitId)){
                Unit unit= iMasterUnitMapper.selectByPrimaryKey(unitId);
                ap.setUnitId(unitId);
                ap.setUnitName(unit!=null?unit.getName():"");
            }
            //查询规格名称
            if (StringUtils.isNotBlank(pt.getValueIdArr())) {
                ap.setValueIdArr(pt.getValueIdArr());
                ap.setValueNameArr(masterProductTemplateService.getNewValueNameArr(pt.getValueIdArr()).replaceAll(",", " "));
            }
        }

    }
    /**
     * 统计精算数据
     */
    public ServerResponse getStatistics() {
        Example example = new Example(House.class);
        example.createCriteria()
                .andEqualTo(House.DESIGNER_OK, 3)
                .andEqualTo(House.DATA_STATUS, 0);
        List<House> houseList = houseMapper.selectByExample(example);
        return ServerResponse.createBySuccess("查询成功", mapResult(houseList));
    }

    /**
     * 按日期统计
     */
    public ServerResponse getStatisticsByDate(String startDate, String endDate) {
        //将时分秒转换为年月日
        Date start = DateUtil.toDate(startDate);
        Date end = DateUtil.toDate(endDate);
        List<House> houseList = houseMapper.getStatisticsByDate(start, end);
        return ServerResponse.createBySuccess("查询成功", mapResult(houseList));
    }

    private Map<String, Object> mapResult(List<House> houseList) {
        int sum1 = 0, sum2 = 0, sum3 = 0, sum4 = 0;
        for (House house : houseList) {
            if (house.getBudgetState() == 0) {
                sum1++;
            }
            if (house.getBudgetState() == 1) {
                sum2++;
            }
            if (house.getBudgetState() == 2) {
                sum3++;
            }
            if (house.getBudgetState() == 3) {
                sum4++;
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("actuaryNumber", houseList.size());//获取精算接单数量
        map.put("actuaryPayNumber", sum1);//待业主支付数量
        map.put("actuaryUploadNumber", sum2);//待上传精算数量
        map.put("actuaryConfirmeNumber", sum3);//待确认精算数量
        map.put("actuarycompletedNumber", sum4);//已完成精算数量
        return map;
    }
}
