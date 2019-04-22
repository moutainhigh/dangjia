package com.dangjia.acg.service.design;

import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
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
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
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
     *   0=未确定设计师
     *   4=设计待抢单
     *   1=已支付-设计师待量房
     *   9=量房图确认，设计师待发平面图
     *   5=平面图发给业主
     *   6=平面图审核不通过
     *   7=通过平面图待发施工图
     *   2=已发给业主施工图
     *   8=施工图片审核不通过
     *   3=施工图(全部图)审核通过
     */
    public ServerResponse sendPictures(HttpServletRequest request, String houseId, int designerOk) {
        House house = houseMapper.selectByPrimaryKey(houseId);
        Example example = new Example(HouseDesignImage.class);
        Example.Criteria criteria=example.createCriteria();
        criteria.andEqualTo(HouseDesignImage.HOUSE_ID, houseId);
        String alert="";
        String imgName="";
        //判断待量房及不通过的量房图
        if (designerOk == 1 ) {
            imgName="量房";
            criteria.andEqualTo(HouseDesignImage.DESIGN_IMAGE_TYPE_ID, "0");
            house.setDesignerOk(9);//量房图发给业主
        }
        //判断平面房及不通过的平面图
        if (designerOk == 9 || designerOk == 6) {
            imgName="平面";
            criteria.andEqualTo(HouseDesignImage.DESIGN_IMAGE_TYPE_ID, "1");
            house.setDesignerOk(5);//平面图发给业主
            alert= String.format(DjConstants.PushMessage.PLANE_UPLOADING, house.getHouseName());
        }
        //判断待施工及不通过的施工图
        if (designerOk == 7 || designerOk == 8) {
            imgName="施工";
            criteria.andNotEqualTo(HouseDesignImage.DESIGN_IMAGE_TYPE_ID, "1");
            criteria.andNotEqualTo(HouseDesignImage.DESIGN_IMAGE_TYPE_ID, "0");
            criteria.andIsNotNull(HouseDesignImage.IMAGEURL);
            house.setDesignerOk(2);//施工图(其它图)发给业主
            alert= String.format(DjConstants.PushMessage.CONSTRUCTION_UPLOADING, house.getHouseName());
        }
        List<HouseDesignImage> houseDesignImageList = houseDesignImageMapper.selectByExample(example);
        if (houseDesignImageList.size() == 0) {
            return ServerResponse.createByErrorMessage("请上传"+imgName+"图");
        }
        houseMapper.updateByPrimaryKeySelective(house);
        if(!CommonUtil.isEmpty(alert)) {
            //app推送给业主
            configMessageService.addConfigMessage(null, "zx", house.getMemberId(), "0", imgName + "图上传提醒", alert, "");
        }
        return ServerResponse.createBySuccessMessage("发送成功");
    }

    /**
     * 上传图片
     */
    public ServerResponse uploadPictures(HttpServletRequest request, String houseId, String designImageTypeId, String imageurl) {
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
    public ServerResponse getImagesList(HttpServletRequest request, String houseId) {
        try {

            String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
            List<String> designImageList = designImageTypeMapper.getDesignImageIdList(houseId);
            if(designImageList==null||designImageList.size()==0){
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
     *
     * @param designerOk 设计状态:
     *                   默认0未确定设计师,4有设计抢单待支付,1已支付设计师待发平面图,5平面图发给业主,6平面图审核不通过,
     *                   7通过平面图待发施工图,2已发给业主施工图,8施工图片审核不通过,3施工图(全部图)审核通过
     */
    public ServerResponse getDesignList(HttpServletRequest request, int designerOk, String mobile, String residential, String number) {
        String address = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        List<DesignDTO> designDTOList = houseMapper.getDesignList(designerOk, mobile, residential, number);
        for (DesignDTO designDTO : designDTOList) {
            HouseDesignImage hdi = designImageTypeMapper.getHouseDesignImage(designDTO.getHouseId(), "1");//1某个房子的平面图
            if (hdi != null) {
                designDTO.setImage(hdi.getImageurl());
                designDTO.setImageUrl(address + hdi.getImageurl());
            }

        }
        return ServerResponse.createBySuccess("查询用户列表成功", designDTOList);
    }
}
