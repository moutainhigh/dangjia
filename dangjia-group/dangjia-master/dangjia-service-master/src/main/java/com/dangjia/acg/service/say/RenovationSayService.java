package com.dangjia.acg.service.say;


import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.engineer.RenovationSayDTO;
import com.dangjia.acg.mapper.say.DjThumbUpMapper;
import com.dangjia.acg.mapper.say.RenovationSayMapper;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.say.DjThumbUp;
import com.dangjia.acg.modle.say.RenovationSay;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private DjThumbUpMapper djThumbUpMapper;

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
     * 查询装修说
     * @param pageDTO
     * @return
     */
    public ServerResponse queryRenovationSayList(PageDTO pageDTO){
        try {
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            Example example = new Example(RenovationSay.class);
            example.createCriteria()
                    .andEqualTo(RenovationSay.DATA_STATUS, 0);
            example.orderBy(RenovationSay.CREATE_DATE).desc();
            List<RenovationSay> renovationSays = renovationSayMapper.selectByExample(example);
            StringBuffer stringBuffer=new StringBuffer();
            renovationSays.forEach(renovationSay -> {
                renovationSay.setCoverImageUrl(imageAddress+renovationSay.getCoverImage());
                String[] split = renovationSay.getContentImage().split(",");
                for (int i = 0; i < split.length; i++) {
                    stringBuffer.append(imageAddress+split[i]);
                    if(i<split.length-1) {
                        stringBuffer.append(",");
                    }
                }
                renovationSay.setContentImageUrl(stringBuffer.toString());
            });
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
    public ServerResponse queryRenovationSayData(String userToken,String id){
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
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
                Example example1=new Example(DjThumbUp.class);
                example1.createCriteria().andEqualTo(DjThumbUp.DATA_STATUS,0)
                        .andEqualTo(DjThumbUp.MEMBER_ID,member.getId())
                        .andEqualTo(DjThumbUp.RECORD_ID,id);
                DjThumbUp djThumbUp = djThumbUpMapper.selectOneByExample(example1);
                renovationSayDTO.setWhetherThumbUp(djThumbUp!=null?1:0);
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
    public ServerResponse queryAppRenovationSayList(String userToken){
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            Example example = new Example(RenovationSay.class);
            example.createCriteria()
                    .andEqualTo(RenovationSay.DATA_STATUS, 0);
            example.orderBy(RenovationSay.CREATE_DATE).desc();
            List<RenovationSay> renovationSays = renovationSayMapper.selectByExample(example);
            if (renovationSays != null && renovationSays.size() > 0){
                String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
                renovationSays.forEach(a->{
                    a.setCoverImage(imageAddress + a.getCoverImage());
                    Example example1=new Example(DjThumbUp.class);
                    example1.createCriteria().andEqualTo(DjThumbUp.DATA_STATUS,0)
                            .andEqualTo(DjThumbUp.MEMBER_ID,member.getId())
                            .andEqualTo(DjThumbUp.RECORD_ID,a.getId());
                    DjThumbUp djThumbUp = djThumbUpMapper.selectOneByExample(example1);
                    a.setWhetherThumbUp(djThumbUp!=null?1:0);
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


    /**
     * 装修说点赞
     * @param userToken
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse setThumbUp(String userToken, String id) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        RenovationSay renovationSay = renovationSayMapper.selectByPrimaryKey(id);
        Example example=new Example(DjThumbUp.class);
        example.createCriteria().andEqualTo(DjThumbUp.DATA_STATUS,0)
                .andEqualTo(DjThumbUp.MEMBER_ID,member.getId())
                .andEqualTo(DjThumbUp.RECORD_ID,id);
        DjThumbUp djThumbUp = djThumbUpMapper.selectOneByExample(example);
        if(djThumbUp!=null){
            djThumbUpMapper.delete(djThumbUp);
            renovationSay.setFabulous(renovationSay.getFabulous()-1);
        }else{
            DjThumbUp djThumbUp1=new DjThumbUp();
            djThumbUp1.setMemberId(member.getId());
            djThumbUp1.setRecordId(id);
            djThumbUpMapper.insert(djThumbUp1);
            renovationSay.setFabulous(renovationSay.getFabulous()+1);
        }
        renovationSayMapper.updateByPrimaryKeySelective(renovationSay);
        return ServerResponse.createBySuccessMessage("操作成功");
    }


    /**
     * 装修说浏览量
     * @param id
     * @return
     */
    public ServerResponse setPageView(String id) {
        try {
            RenovationSay renovationSay = renovationSayMapper.selectByPrimaryKey(id);
            renovationSay.setBrowse(renovationSay.getBrowse()+1);
            renovationSayMapper.updateByPrimaryKeySelective(renovationSay);
            return ServerResponse.createBySuccessMessage("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("操作失败");
        }
    }

}

