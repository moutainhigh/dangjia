package com.dangjia.acg.service.say;


import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.engineer.RenovationSayDTO;
import com.dangjia.acg.mapper.say.RenovationSayMapper;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.say.RenovationSay;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * 装修说逻辑层
 */
@Service
public class RenovationSayService {


    @Autowired
    private RenovationSayMapper renovationSayMapper;
    @Autowired
    private ConfigUtil configUtil;
    /**
     *  新增装修说
     * @param content
     * @param coverImage
     * @param contentImage
     * @return
     */
    public ServerResponse insertRenovationSay(String content,String coverImage,String contentImage){
        try {
            RenovationSay renovationSay = new RenovationSay();
            renovationSay.setBrowse(0);
            renovationSay.setFabulous(0);
            renovationSay.setShare(0);
            renovationSay.setContent(content);
            renovationSay.setCoverImage(coverImage);
            renovationSay.setContentImage(contentImage);
            renovationSayMapper.insert(renovationSay);
            return ServerResponse.createBySuccessMessage("新增成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增失败");
        }
    }

    /**
     *  新增装修说
     * @param content
     * @param coverImage
     * @param contentImage
     * @return
     */
    public ServerResponse upDateRenovationSay(String id,String content,String coverImage,String contentImage){
        try {
            RenovationSay renovationSay = new RenovationSay();
            renovationSay.setId(id);
            renovationSay.setCreateDate(null);
            renovationSay.setModifyDate(new Date());
            renovationSay.setContent(content);
            renovationSay.setCoverImage(coverImage);
            renovationSay.setContentImage(contentImage);
            renovationSayMapper.updateByPrimaryKeySelective(renovationSay);
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }

    /**
     * 删除装修说
     * @param id
     * @return
     */
    public ServerResponse deleteRenovationSay(String id){
        try {
            renovationSayMapper.deleteByPrimaryKey(id);
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("删除失败");
        }
    }



    /**
     *查询装修说
     * @param pageDTO
     * @return
     */
    public ServerResponse queryRenovationSayList(PageDTO pageDTO){
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Example example = new Example(RenovationSay.class);
            example.createCriteria()
                    .andEqualTo(RenovationSay.DATA_STATUS, 0);
            example.orderBy(RenovationSay.CREATE_DATE).desc();
            List<RenovationSay> renovationSays = renovationSayMapper.selectByExample(example);
            PageInfo pageResult = new PageInfo(renovationSays);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }



    /**
     *根据id查询装修说信息
     * @param id
     * @return
     */
    public ServerResponse queryRenovationSayData(String id){
        try {
            RenovationSayDTO renovationSayDTO = new RenovationSayDTO();
            RenovationSay renovationSays = renovationSayMapper.selectByPrimaryKey(id);
            if(renovationSays != null){
                String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
                renovationSayDTO.setBrowse(renovationSays.getBrowse());
                renovationSayDTO.setFabulous(renovationSays.getFabulous());
                renovationSayDTO.setShare(renovationSays.getShare());
                renovationSayDTO.setCreateDate(renovationSays.getCreateDate());
                renovationSayDTO.setId(renovationSays.getId());
                renovationSayDTO.setContent(renovationSays.getContent());
                renovationSayDTO.setContentImage(renovationSays.getContentImage());
                renovationSayDTO.setContentImages(getImage(renovationSays.getContentImage()));
                renovationSayDTO.setCoverImage(renovationSays.getCoverImage());
                renovationSayDTO.setCoverImages(imageAddress + renovationSays.getCoverImage());

            }
            return ServerResponse.createBySuccess("查询成功", renovationSayDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     *app查询装修说列表
     * @return
     */
    public ServerResponse queryAppRenovationSayList(){
        try {
            Example example = new Example(RenovationSay.class);
            example.createCriteria()
                    .andEqualTo(RenovationSay.DATA_STATUS, 0);
            example.orderBy(RenovationSay.CREATE_DATE).desc();
            List<RenovationSay> renovationSays = renovationSayMapper.selectByExample(example);
            if (renovationSays != null && renovationSays.size() > 0){
                String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
                renovationSays.forEach(a->{
                    a.setCoverImage(imageAddress + a.getCoverImage());
                });
            }

            PageInfo pageResult = new PageInfo(renovationSays);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 拼接图片
     * @param image
     * @return
     */
    private List<String> getImage(String image){
        List<String> strList = new ArrayList<>();
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        List<String> result = Arrays.asList(image.split(","));
        for (int i = 0; i < result.size(); i++) {
            String str = imageAddress + result.get(i);
            strList.add(str);
        }
        return strList;
    }

}

