package com.dangjia.acg.service.design;

import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.response.ServerResponse;
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

    /**
     * 发送设计图业主
     * 设计状态:默认0未确定设计师,4有设计抢单待支付,1已支付设计师待发平面图,5平面图发给业主,6平面图审核不通过,7通过平面图待发施工图,
     *         2已发给业主施工图,8施工图片审核不通过,3施工图(全部图)审核通过
     */
    public ServerResponse sendPictures(HttpServletRequest request,String houseId,int designerOk){
        House house = houseMapper.selectByPrimaryKey(houseId);
        Example example = new Example(HouseDesignImage.class);
        List<HouseDesignImage> houseDesignImageList;
        if (designerOk == 1 || designerOk == 6){
            example.createCriteria().andEqualTo(HouseDesignImage.HOUSE_ID, houseId).andEqualTo(HouseDesignImage.DESIGN_IMAGE_TYPE_ID, "1");
            houseDesignImageList = houseDesignImageMapper.selectByExample(example);
            if(houseDesignImageList.size() == 0){
                return ServerResponse.createByErrorMessage("请上传平面图");
            }

            house.setDesignerOk(5);//平面图发给业主
            houseMapper.updateByPrimaryKeySelective(house);
            //app推送给业主
            configMessageService.addConfigMessage(null,"gj",house.getMemberId(),"0","设计图上传提醒",
                    String.format(DjConstants.PushMessage.PLANE_UPLOADING,house.getHouseName()) ,"");
            return ServerResponse.createBySuccessMessage("发送成功");
        }else if (designerOk == 7 || designerOk==8){
            example.createCriteria().andEqualTo("houseId", houseId).andNotEqualTo(HouseDesignImage.DESIGN_IMAGE_TYPE_ID,"1")
                    .andIsNotNull(HouseDesignImage.IMAGEURL);
            houseDesignImageList = houseDesignImageMapper.selectByExample(example);
            if(houseDesignImageList.size() == 0){
                return ServerResponse.createByErrorMessage("请上传施工图");
            }

            house.setDesignerOk(2);//施工图(其它图)发给业主
            houseMapper.updateByPrimaryKeySelective(house);
            //app推送给业主
            configMessageService.addConfigMessage(null,"gj",house.getMemberId(),"0","设计图上传提醒",
                    String.format(DjConstants.PushMessage.CONSTRUCTION_UPLOADING,house.getHouseName()) ,"");

            return ServerResponse.createBySuccessMessage("发送成功");
        }
        return ServerResponse.createByErrorMessage("参数异常,发送失败");
    }

    /**
     * 上传图片
     */
    public ServerResponse uploadPictures(HttpServletRequest request,String houseId,String designImageTypeId,String imageurl){
        try{
            HouseDesignImage hdi = designImageTypeMapper.getHouseDesignImage(houseId,designImageTypeId);
            if(hdi == null){
                hdi = new HouseDesignImage();
                hdi.setHouseId(houseId);
                hdi.setDesignImageTypeId(designImageTypeId);
                hdi.setImageurl(imageurl);
                hdi.setSell(0);
                houseDesignImageMapper.insert(hdi);
            }else {
                hdi.setHouseId(houseId);
                hdi.setDesignImageTypeId(designImageTypeId);
                hdi.setImageurl(imageurl);
                houseDesignImageMapper.updateByPrimaryKeySelective(hdi);
            }
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("上传失败");
        }
        return ServerResponse.createBySuccessMessage("上传成功");
    }

    /**
     * 设计图列表
     */
    public ServerResponse getImagesList(HttpServletRequest request, String houseId){
        try{
            List<String> designImageList = designImageTypeMapper.getDesignImageIdList(houseId);
            String typeList = designImageList.get(0);
            typeList = typeList.replaceAll("'","");
            typeList = typeList.replaceAll(" ","");
            String[] typeArray = StringUtils.split(typeList,",");
            //查询所有免费风格
            List<DesignImageType> designImageTypeList = designImageTypeMapper.getDesignImageTypeList(typeArray);
            //查询额外付费图
            Example example = new Example(HouseDesignImage.class);
            example.createCriteria().andEqualTo("houseId", houseId).andEqualTo("sell", 1);
            List<HouseDesignImage> houseDesignImageList = houseDesignImageMapper.selectByExample(example);

            List<HouseDesignImageDTO> houseDesignImageDTOList = new ArrayList<HouseDesignImageDTO>();
            HouseDesignImageDTO houseDesignImageDTO;
            //先遍历免费图
            for (DesignImageType designImageType : designImageTypeList){
                HouseDesignImage hdi = designImageTypeMapper.getHouseDesignImage(houseId,designImageType.getId());
                if(hdi == null){
                    houseDesignImageDTO = new HouseDesignImageDTO();
                    houseDesignImageDTO.setHouseId(houseId);
                    houseDesignImageDTO.setDesignImageTypeId(designImageType.getId());
                    houseDesignImageDTO.setImageurl(null);
                    houseDesignImageDTO.setName(designImageType.getName());
                    houseDesignImageDTO.setSell(0);
                    houseDesignImageDTO.setPrice(new BigDecimal(0));
                }else {
                    houseDesignImageDTO = new HouseDesignImageDTO();
                    houseDesignImageDTO.setHouseId(houseId);
                    houseDesignImageDTO.setDesignImageTypeId(designImageType.getId());
                    if (StringUtil.isNotEmpty(hdi.getImageurl())){
                        houseDesignImageDTO.setImageurl(hdi.getImageurl());
                    }else{
                        houseDesignImageDTO.setImageurl(null);
                    }
                    houseDesignImageDTO.setName(designImageType.getName());
                    houseDesignImageDTO.setSell(0);
                    houseDesignImageDTO.setPrice(new BigDecimal(0));
                }
                houseDesignImageDTOList.add(houseDesignImageDTO);
            }
            //收费图
            for (HouseDesignImage houseDesignImage : houseDesignImageList){
                houseDesignImageDTO = new HouseDesignImageDTO();
                houseDesignImageDTO.setHouseId(houseId);
                houseDesignImageDTO.setDesignImageTypeId(houseDesignImage.getDesignImageTypeId());
                houseDesignImageDTO.setImageurl(houseDesignImage.getImageurl());
                houseDesignImageDTO.setName(designImageTypeMapper.selectByPrimaryKey(houseDesignImage.getDesignImageTypeId()).getName());
                houseDesignImageDTO.setSell(1);
                houseDesignImageDTO.setPrice(designImageTypeMapper.selectByPrimaryKey(houseDesignImage.getDesignImageTypeId()).getPrice());
                houseDesignImageDTOList.add(houseDesignImageDTO);
            }
            return ServerResponse.createBySuccess("查询列表成功",houseDesignImageDTOList);
        }catch (Exception e){
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("异常");
        }
    }

    /**
     * 设计任务列表
     * @param designerOk
     * 设计状态:
     * 默认0未确定设计师,4有设计抢单待支付,1已支付设计师待发平面图,5平面图发给业主,6平面图审核不通过,
     * 7通过平面图待发施工图,2已发给业主施工图,8施工图片审核不通过,3施工图(全部图)审核通过
     */
    public ServerResponse getDesignList(HttpServletRequest request, int designerOk,String mobile,String residential,String number){
        List<DesignDTO> designDTOList = houseMapper.getDesignList(designerOk,mobile,residential,number);
        return ServerResponse.createBySuccess("查询用户列表成功",designDTOList);
    }
}
