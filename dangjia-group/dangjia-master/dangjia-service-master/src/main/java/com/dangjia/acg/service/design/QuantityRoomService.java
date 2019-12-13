package com.dangjia.acg.service.design;

import com.dangjia.acg.api.config.ServiceTypeAPI;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.enums.AppType;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.actuary.app.ActuarialProductAppDTO;
import com.dangjia.acg.mapper.design.IMasterQuantityRoomProductMapper;
import com.dangjia.acg.mapper.design.IQuantityRoomImagesMapper;
import com.dangjia.acg.mapper.design.IQuantityRoomMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.house.IModelingVillageMapper;
import com.dangjia.acg.mapper.other.ICityMapper;
import com.dangjia.acg.modle.config.ServiceType;
import com.dangjia.acg.modle.design.DesignQuantityRoomProduct;
import com.dangjia.acg.modle.design.QuantityRoom;
import com.dangjia.acg.modle.design.QuantityRoomImages;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.ModelingVillage;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberAddress;
import com.dangjia.acg.modle.other.City;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.house.HouseService;
import com.dangjia.acg.service.member.MemberAddressService;
import com.dangjia.acg.util.StringTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Ruking.Cheng
 * @descrilbe 新版量房，施工图上传
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/12/12 6:42 PM
 */
@Service
public class QuantityRoomService {
    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private IQuantityRoomMapper iQuantityRoomMapper;
    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IQuantityRoomImagesMapper iQuantityRoomImagesMapper;
    @Autowired
    private IModelingVillageMapper iModelingVillageMapper;
    @Autowired
    private ICityMapper cityMapper;
    @Autowired
    private MemberAddressService memberAddressService;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private ServiceTypeAPI serviceTypeAPI;
    @Autowired
    private HouseService houseService;
    @Autowired
    private IMasterQuantityRoomProductMapper iMasterQuantityRoomProductMapper;

    /**
     * 是否确认地址
     *
     * @param houseId 房子ID
     * @return ServerResponse resultObj:0:未确认地址，1：已经确认地址
     */
    public ServerResponse isConfirmAddress(String houseId) {
        QuantityRoom quantityRoom = iQuantityRoomMapper.getQuantityRoom(houseId, 0);
        if (quantityRoom == null) {
            return ServerResponse.createBySuccess(0);
        } else {
            return ServerResponse.createBySuccess(1);
        }
    }

    /**
     * 确认地址/量房一起
     *
     * @param userToken   userToken
     * @param userId      userId
     * @param houseId     房子ID
     * @param villageId   小区ID
     * @param houseType   房屋类型ID
     * @param building    楼栋
     * @param unit        单元号
     * @param number      房间号
     * @param square      外框面积
     * @param buildSquare 建筑面积
     * @param images      量房图片","号分割
     * @param elevator    是否电梯房：0:否，1：是
     * @param floor       楼层
     * @return ServerResponse
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse setQuantityRoom(String userToken, String userId, String houseId,
                                          String villageId, String houseType,
                                          String building, String unit, String number, BigDecimal square,
                                          BigDecimal buildSquare, String images, Integer elevator, String floor) {
        House house = iHouseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorMessage("没有查询到相关房子");
        }
        if (house.getVisitState() != 1) {
            return ServerResponse.createByErrorMessage("该房子不在装修中");
        }
        Object objectm = constructionService.getMember(userToken);
        Member member = null;
        if (objectm instanceof Member) {
            member = (Member) objectm;
        }
        if (member == null && CommonUtil.isEmpty(userId)) {
            return ServerResponse.createbyUserTokenError();
        }
        QuantityRoom quantityRoom = iQuantityRoomMapper.getQuantityRoom(houseId, 0);
        if (quantityRoom == null) {
            if (CommonUtil.isEmpty(villageId)
                    || CommonUtil.isEmpty(houseType)
                    || CommonUtil.isEmpty(building)
                    || CommonUtil.isEmpty(unit)
                    || CommonUtil.isEmpty(number)
                    || CommonUtil.isEmpty(square)
                    || CommonUtil.isEmpty(buildSquare)) {
                return ServerResponse.createByErrorMessage("地址信息有缺损，请补全");
            }
            ModelingVillage village = iModelingVillageMapper.selectByPrimaryKey(villageId);
            if (village == null) {
                return ServerResponse.createByErrorMessage("选择的小区不存在");
            }
            City city = cityMapper.selectByPrimaryKey(village.getCityId());
            if (city == null) {
                return ServerResponse.createByErrorMessage("小区城市不存在");
            }
            Example exa = new Example(House.class);
            exa.createCriteria()
                    .andEqualTo(House.RESIDENTIAL, village.getName())
                    .andEqualTo(House.BUILDING, building)
                    .andEqualTo(House.UNIT, unit)
                    .andEqualTo(House.NUMBER, number);
            if (iHouseMapper.selectCountByExample(exa) > 0) {
                return ServerResponse.createByErrorMessage("该房子已存在");
            }
            if (CommonUtil.isEmpty(elevator)) {
                return ServerResponse.createByErrorMessage("请选择是否为电梯房");
            }
            if (elevator == 0 && CommonUtil.isEmpty(floor)) {
                return ServerResponse.createByErrorMessage("请输入楼层");
            }
            //更新房子信息
            house.setCityId(city.getId());//城市id
            house.setCityName(city.getName());//城市名
            house.setVillageId(villageId);//小区ID
            house.setResidential(village.getName());//小区名
            house.setBuilding(building);//楼栋
            house.setUnit(unit);//单元号
            house.setNumber(number);//房间号
            house.setSquare(square);//外框面积
            house.setBuildSquare(buildSquare);//建筑面积
            house.setHouseType(houseType);//装修的房子类型
            house.setConstructionDate(new Date());//开工时间
            //更新地址信息
            ServerResponse serverResponse = memberAddressService.updataAddress(house);
            if (!serverResponse.isSuccess()) {
                return serverResponse;
            }
            //创建量房信息
            quantityRoom = new QuantityRoom();
            if (member != null) {
                quantityRoom.setMemberId(member.getId());
            } else {
                quantityRoom.setUserId(userId);
            }
            quantityRoom.setHouseId(houseId);
            quantityRoom.setElevator(elevator);
            quantityRoom.setFloor(floor);
            quantityRoom.setType(0);
            quantityRoom.setOperationType(0);
            quantityRoom.setRoomType(0);
            quantityRoom.setFlag(3);
            //生成补单
            serverResponse = houseService.checkHouseSquare(houseId);
            if (!serverResponse.isSuccess()) {
                return serverResponse;
            }
            iQuantityRoomMapper.insert(quantityRoom);
            house.setDecorationType(2);
        } else {
            if (member != null) {
                quantityRoom.setMemberId(member.getId());
            } else {
                quantityRoom.setUserId(userId);
            }
            quantityRoom.setModifyDate(new Date());
            iQuantityRoomMapper.updateByPrimaryKeySelective(quantityRoom);
        }
        //是否为量房通过图片传入确定
        if (CommonUtil.isEmpty(images)) {
            house.setDecorationType(1);
            //删除之前提交的
            Example example = new Example(QuantityRoomImages.class);
            example.createCriteria()
                    .andEqualTo(QuantityRoomImages.QUANTITY_ROOM_ID, quantityRoom.getId())
                    .andEqualTo(QuantityRoomImages.DATA_STATUS, 0);
            example.orderBy(QuantityRoomImages.SORT).asc();
            QuantityRoomImages images1 = new QuantityRoomImages();
            images1.setId(null);
            images1.setCreateDate(null);
            images1.setDataStatus(1);
            iQuantityRoomImagesMapper.updateByExampleSelective(images1, example);
            //插入现在提交的
            String[] image = images.split(",");
            for (int i = 0; i < image.length; i++) {
                String s = image[i];
                if (!CommonUtil.isEmpty(s.trim())) {
                    QuantityRoomImages quantityRoomImages = new QuantityRoomImages();
                    quantityRoomImages.setSort(i);
                    quantityRoomImages.setHouseId(houseId);
                    quantityRoomImages.setImage(s);
                    quantityRoomImages.setQuantityRoomId(quantityRoom.getId());
                    quantityRoomImages.setName("量房");
                    iQuantityRoomImagesMapper.insert(quantityRoomImages);
                }
            }
            house.setDesignerOk(9);
            //推送消息给业主已完成量房
            configMessageService.addConfigMessage(null, AppType.ZHUANGXIU, house.getMemberId(),
                    "0", "设计师完成量房", String.format(DjConstants.PushMessage.LIANGFANGWANCHENG,
                            house.getHouseName()), "");
        }
        house.setDataStatus(0);
        iHouseMapper.updateByPrimaryKeySelective(house);
        return ServerResponse.createBySuccessMessage("提交成功");
    }

    public ServerResponse getConfirmAddress(String houseId) {
        House house = iHouseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorMessage("该房产不存在");
        }
        QuantityRoom quantityRoom = iQuantityRoomMapper.getQuantityRoom(houseId, 0);
        if (quantityRoom == null) {
            return ServerResponse.createByErrorMessage("暂无地址信息");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("houseId", house.getId());
        map.put("cityId", house.getId());//城市id
        map.put("cityName", house.getId());//城市名
        map.put("villageId", house.getVillageId());//小区ID
        map.put("residential", house.getResidential());//小区名
        map.put("building", house.getBuilding());//楼栋
        map.put("unit", house.getUnit());//单元号
        map.put("number", house.getNumber());//房间号
        map.put("houseUnit", house.getHouseUnit());//楼栋单元房间
        map.put("square", house.getSquare());//外框面积
        map.put("buildSquare", house.getBudgetState());//建筑面积
        map.put("houseType", house.getHouseType());//装修的房子类型
        ServiceType serviceType = serviceTypeAPI.getServiceTypeById(house.getCityId(), house.getHouseType());
        if (serviceType != null && StringUtils.isNotBlank(serviceType.getName())) {
            map.put("houseTypeName", serviceType.getName());//装修的房子类型名称
        } else {
            map.put("houseTypeName", "0".equals(house.getHouseType()) ? "新房装修" : "旧房装修");//装修的房子类型名称
        }
        map.put("elevator", quantityRoom.getElevator());//是否电梯房：0:否，1：是
        map.put("floor", quantityRoom.getFloor());//楼层
        List<String> images = new ArrayList<>();
        Example example = new Example(QuantityRoomImages.class);
        example.createCriteria()
                .andEqualTo(QuantityRoomImages.QUANTITY_ROOM_ID, quantityRoom.getId())
                .andEqualTo(QuantityRoomImages.DATA_STATUS, 0);
        example.orderBy(QuantityRoomImages.SORT).asc();
        List<QuantityRoomImages> quantityRoomImages = iQuantityRoomImagesMapper.selectByExample(example);
        if (quantityRoomImages.size() > 0) {
            String imageAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            for (QuantityRoomImages quantityRoomImage : quantityRoomImages) {
                quantityRoomImage.initPath(imageAddress);
                images.add(quantityRoomImage.getImage());
            }
        }
        map.put("images", images);//量房图片
        return ServerResponse.createBySuccess("提交成功", map);
    }

    /**
     * 获取推荐商品
     */
    public ServerResponse getRecommendProduct(PageDTO pageDTO, String houseId, int type) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<ActuarialProductAppDTO> appDTOS = iMasterQuantityRoomProductMapper.getRoomProductList(houseId, type);
        if (appDTOS.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(appDTOS);
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        for (ActuarialProductAppDTO appDTO : appDTOS) {
            appDTO.setStorefrontIcon(imageAddress + appDTO.getStorefrontIcon());
            appDTO.setImageUrl(StringTool.getImage(appDTO.getImage(), imageAddress));//图多张
            appDTO.setImageSingle(StringTool.getImageSingle(appDTO.getImage(), imageAddress));//图一张
        }
        pageResult.setList(appDTOS);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    /**
     * 添加推荐的商品
     */
    public ServerResponse addRecommendProduct(String houseId, int type, String productIds) {
        House house = iHouseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorMessage("该房产不存在");
        }
        if (CommonUtil.isEmpty(productIds)) {
            return ServerResponse.createByErrorMessage("推荐商品ID不能为空");
        }
        //删除之前提交的
        String[] productIdList = productIds.split(",");
        for (String productId : productIdList) {
            if (!CommonUtil.isEmpty(productId)) {
                DesignQuantityRoomProduct designQuantityRoomProduct = new DesignQuantityRoomProduct();
                designQuantityRoomProduct.setHouseId(houseId);
                designQuantityRoomProduct.setProductId(productId);//商品ID
                designQuantityRoomProduct.setType(type);//推荐商品
                iMasterQuantityRoomProductMapper.insertSelective(designQuantityRoomProduct);
            }
        }
        return ServerResponse.createBySuccessMessage("提交成功");
    }

    /**
     * 删除推荐的商品
     */
    public ServerResponse deleteRecommendProduct(String rpId) {
        DesignQuantityRoomProduct quantityRoomProduct = iMasterQuantityRoomProductMapper.selectByPrimaryKey(rpId);
        if (quantityRoomProduct == null) {
            return ServerResponse.createByErrorMessage("未找到该推荐商品");
        }
        quantityRoomProduct.setModifyDate(new Date());
        quantityRoomProduct.setDataStatus(1);
        iMasterQuantityRoomProductMapper.updateByPrimaryKeySelective(quantityRoomProduct);
        return ServerResponse.createBySuccessMessage("删除成功");
    }
}
