package com.dangjia.acg.service.design;

import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.design.HouseDesignImageDTO;
import com.dangjia.acg.dto.house.DesignDTO;
import com.dangjia.acg.mapper.design.IDesignImageTypeMapper;
import com.dangjia.acg.mapper.design.IHouseDesignImageMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.modle.design.DesignImageType;
import com.dangjia.acg.modle.design.HouseDesignImage;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.service.config.ConfigMessageService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * author: RonalchenggetImagesList
 * Date: 2018/11/10 0010
 * Time: 16:21
 * 设计相关
 */
@Service
public class DesignService {

    @Autowired
    private ConfigMessageService configMessageService;
    @Autowired
    private IHouseMapper houseMapper;
    @Autowired
    private IDesignImageTypeMapper designImageTypeMapper;
    @Autowired
    private IHouseDesignImageMapper houseDesignImageMapper;//房子关联设计图
    @Autowired
    private ConfigUtil configUtil;

    /**
     * 发送设计图业主
     * 设计状态:
     * 0=未确定设计师
     * 4=设计待抢单
     * 1=已支付-设计师待量房
     * 9=量房图确认，设计师待发平面图
     * 5=平面图发给业主
     * 6=平面图审核不通过
     * 7=通过平面图待发施工图
     * 2=已发给业主施工图
     * 8=施工图片审核不通过
     * 3=施工图(全部图)审核通过
     * 。。。。。。。。。。。。。。。。。⦧--6。。⦧--8
     * 远程设计流程：0---4---1---9---5---7---2---3
     *
     * 。。。。。。。。。。。。⦧--6。。⦧--8
     * 自带设计流程：0---1---5---7---2---3
     */
    public ServerResponse sendPictures(String houseId) {
        House house = houseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorMessage("未找到该房子");
        }
        if (house.getDecorationType() == 2) {//自带设计流程
            if (house.getDesignerOk() == 1 || house.getDesignerOk() == 6) {
                return sendPlan(house);
            } else if (house.getDesignerOk() == 7 || house.getDesignerOk() == 8) {
                return constructionPlans(house);
            } else {
                return ServerResponse.createByErrorMessage("设计进度还未达到发送要求");
            }
        } else {
            if (house.getDesignerOk() == 9 || house.getDesignerOk() == 6) {
                return sendPlan(house);
            } else if (house.getDesignerOk() == 7 || house.getDesignerOk() == 8) {
                return constructionPlans(house);
            } else {
                return ServerResponse.createByErrorMessage("设计进度还未达到发送要求");
            }
        }
    }
    public ServerResponse invalidHouse(String houseId) {
        House house = houseMapper.selectByPrimaryKey(houseId);
        house.setDataStatus(1);
        houseMapper.updateByPrimaryKeySelective(house);
        return ServerResponse.createBySuccessMessage("发送成功");
    }
    /**
     * 发送平面图给业主
     *
     * @param house 房子
     * @return
     */
    private ServerResponse sendPlan(House house) {
        Example example = new Example(HouseDesignImage.class);
        example.createCriteria()
                .andEqualTo(HouseDesignImage.HOUSE_ID, house.getId())
                .andEqualTo(HouseDesignImage.DESIGN_IMAGE_TYPE_ID, "1");
        if (houseDesignImageMapper.selectCountByExample(example) == 0) {
            return ServerResponse.createByErrorMessage("请上传平面图");
        }
        house.setDesignerOk(5);//平面图发给业主
        houseMapper.updateByPrimaryKeySelective(house);
        //app推送给业主
        configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "设计图上传提醒",
                String.format(DjConstants.PushMessage.PLANE_UPLOADING, house.getHouseName()), "");
        return ServerResponse.createBySuccessMessage("发送成功");
    }


    /**
     * 发送施工图给业主
     *
     * @param house 房子
     * @return
     */
    private ServerResponse constructionPlans(House house) {
        Example example = new Example(HouseDesignImage.class);
        example.createCriteria()
                .andEqualTo(HouseDesignImage.HOUSE_ID, house.getId())
                .andNotEqualTo(HouseDesignImage.DESIGN_IMAGE_TYPE_ID, "1")
                .andIsNotNull(HouseDesignImage.IMAGEURL);
        if (houseDesignImageMapper.selectCountByExample(example) == 0) {
            return ServerResponse.createByErrorMessage("请上传施工图");
        }
        house.setDesignerOk(2);//施工图(其它图)发给业主
        houseMapper.updateByPrimaryKeySelective(house);
        //app推送给业主
        configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", "设计图上传提醒",
                String.format(DjConstants.PushMessage.CONSTRUCTION_UPLOADING, house.getHouseName()), "");
        return ServerResponse.createBySuccessMessage("发送成功");
    }

    /**
     * 上传图片
     */
    public ServerResponse uploadPictures(String houseId, String designImageTypeId, String imageurl) {
        try {
            HouseDesignImage hdi = designImageTypeMapper.getHouseDesignImage(houseId, designImageTypeId);

            if (hdi == null) {
                hdi = new HouseDesignImage();
                hdi.setHouseId(houseId);
                hdi.setDesignImageTypeId(designImageTypeId);
                hdi.setImageurl(imageurl);
                hdi.setSell(0);
                houseDesignImageMapper.insert(hdi);
            } else {
                hdi.setHouseId(houseId);
                hdi.setDesignImageTypeId(designImageTypeId);
                hdi.setImageurl(imageurl);
                houseDesignImageMapper.updateByPrimaryKeySelective(hdi);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("上传失败");
        }
        return ServerResponse.createBySuccessMessage("上传成功");
    }

    /**
     * 设计图列表
     */
    public ServerResponse getImagesList(String houseId) {
        try {
            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<String> designImageList = designImageTypeMapper.getDesignImageIdList(houseId);
            if (designImageList == null || designImageList.size() == 0) {
                return ServerResponse.createByErrorMessage("找不到房子对应图类型");
            }
            String typeList = designImageList.get(0);
            typeList = typeList.replaceAll("'", "");
            typeList = typeList.replaceAll(" ", "");
            String[] typeArray = StringUtils.split(typeList, ",");
            //查询所有风格
            List<DesignImageType> designImageTypeList = designImageTypeMapper.getDesignImageTypeList(typeArray);
            List<HouseDesignImageDTO> houseDesignImageDTOList = new ArrayList<>();
            HouseDesignImageDTO houseDesignImageDTO;
            for (DesignImageType designImageType : designImageTypeList) {
                HouseDesignImage hdi = designImageTypeMapper.getHouseDesignImage(houseId, designImageType.getId());
                if (hdi == null) {
                    houseDesignImageDTO = new HouseDesignImageDTO();
                    houseDesignImageDTO.setHouseId(houseId);
                    houseDesignImageDTO.setDesignImageTypeId(designImageType.getId());
                    houseDesignImageDTO.setImageurl(null);
                    houseDesignImageDTO.setImage(null);
                    houseDesignImageDTO.setName(designImageType.getName());
                    houseDesignImageDTO.setSell(designImageType.getSell());
                    houseDesignImageDTO.setPrice(new BigDecimal(0));
                } else {
                    houseDesignImageDTO = new HouseDesignImageDTO();
                    houseDesignImageDTO.setHouseId(houseId);
                    houseDesignImageDTO.setDesignImageTypeId(designImageType.getId());
                    if (StringUtil.isNotEmpty(hdi.getImageurl())) {
                        houseDesignImageDTO.setImageurl(address + hdi.getImageurl());
                        houseDesignImageDTO.setImage(hdi.getImageurl());
                    } else {
                        houseDesignImageDTO.setImageurl(null);
                        houseDesignImageDTO.setImage(null);
                    }
                    houseDesignImageDTO.setName(designImageType.getName());
                    houseDesignImageDTO.setSell(designImageType.getSell());
                    houseDesignImageDTO.setPrice(new BigDecimal(0));
                }
                houseDesignImageDTOList.add(houseDesignImageDTO);
            }
            return ServerResponse.createBySuccess("查询列表成功", houseDesignImageDTOList);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("异常");
        }
    }

    /**
     * 设计任务列表
     * 。。。。。。。。。。。。。。。。。⦧--6。。⦧--8
     * 远程设计流程：0---4---1---9---5---7---2---3
     *
     * 。。。。。。。。。。。。⦧--6。。⦧--8
     * 自带设计流程：0---1---5---7---2---3
     *
     * @param pageDTO      分页码
     * @param designerType 0：未支付和设计师未抢单，1：带量房，2：平面图，3：施工图，4：完工
     * @param searchKey    业主手机号/房子名称
     */
    public ServerResponse getDesignList(PageDTO pageDTO, int designerType, String searchKey) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<DesignDTO> designDTOList = houseMapper.getDesignList(designerType, searchKey);
        PageInfo pageResult = new PageInfo(designDTOList);
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        for (DesignDTO designDTO : designDTOList) {
            HouseDesignImage hdi = designImageTypeMapper.getHouseDesignImage(designDTO.getHouseId(), "1");//1某个房子的平面图
            if (hdi != null) {
                designDTO.setImage(hdi.getImageurl());
                designDTO.setImageUrl(address + hdi.getImageurl());
            }
            if (designDTO.getDecorationType() == 2) {//自带设计流程
                switch (designDTO.getDesignerOk()) {
                    case 0://0未确定设计师
                        designDTO.setSchedule("待抢单");
                        break;
                    case 4://4设计待抢单
                    case 1://1已支付-设计师待量房
                    case 9://9量房图发给业主
                        designDTO.setSchedule("待上传平面图");
                        break;
                    case 5://5平面图发给业主 （发给了业主）
                        designDTO.setSchedule("待审核平面图");
                        break;
                    case 6://6平面图审核不通过（NG，可编辑平面图）
                        designDTO.setSchedule("待修改平面图");
                        break;
                    case 7://7通过平面图待发施工图（OK，可编辑施工图）
                        designDTO.setSchedule("待上传施工图");
                        break;
                    case 2://2已发给业主施工图 （发给了业主）
                        designDTO.setSchedule("待审核施工图");
                        break;
                    case 8://8施工图片审核不通过（NG，可编辑施工图）
                        designDTO.setSchedule("待修改施工图");
                        break;
                    case 3://施工图(全部图)审核通过（OK，完成）
                        designDTO.setSchedule("完成");
                        break;
                }
            } else {
                switch (designDTO.getDesignerOk()) {
                    case 0://0未确定设计师
                        designDTO.setSchedule("待抢单");
                        break;
                    case 4://4设计待抢单
                        designDTO.setSchedule("待业主支付");
                        break;
                    case 1://1已支付-设计师待量房
                        designDTO.setSchedule("待量房");
                        break;
                    case 9://9量房图发给业主
                        designDTO.setSchedule("待上传平面图");
                        break;
                    case 5://5平面图发给业主 （发给了业主）
                        designDTO.setSchedule("待审核平面图");
                        break;
                    case 6://6平面图审核不通过（NG，可编辑平面图）
                        designDTO.setSchedule("待修改平面图");
                        break;
                    case 7://7通过平面图待发施工图（OK，可编辑施工图）
                        designDTO.setSchedule("待上传施工图");
                        break;
                    case 2://2已发给业主施工图 （发给了业主）
                        designDTO.setSchedule("待审核施工图");
                        break;
                    case 8://8施工图片审核不通过（NG，可编辑施工图）
                        designDTO.setSchedule("待修改施工图");
                        break;
                    case 3://施工图(全部图)审核通过（OK，完成）
                        designDTO.setSchedule("完成");
                        break;
                }
            }
        }
        pageResult.setList(designDTOList);
        return ServerResponse.createBySuccess("查询用户列表成功", pageResult);
    }
}
