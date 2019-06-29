package com.dangjia.acg.service.house;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.house.TextContentDTO;
import com.dangjia.acg.mapper.house.IHouseChoiceCaseMapper;
import com.dangjia.acg.modle.activity.Activity;
import com.dangjia.acg.modle.house.HouseChoiceCase;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * author: qiyuxiang
 * Date: 2018/12/10 0031
 * Time: 20:18
 */
@Service
public class HouseChoiceCaseService {

    @Autowired
    private IHouseChoiceCaseMapper houseChoiceCaseMapper;

    @Autowired
    private ConfigUtil configUtil;

    /**
     * 获取所有房屋精选案例
     *
     * @param houseChoiceCase
     * @return
     */
    public ServerResponse getHouseChoiceCases(HttpServletRequest request, Integer from, PageDTO pageDTO, HouseChoiceCase houseChoiceCase) {
        String jdAddress = configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class);
        Example example = new Example(HouseChoiceCase.class);
        Example.Criteria criteria = example.createCriteria();
        if (!CommonUtil.isEmpty(houseChoiceCase.getCityId())) {
            criteria.andEqualTo("cityId", houseChoiceCase.getCityId());
        }
        if (from == null || from == 0) {//App端调用
            criteria.andCondition(" ( is_show = 0 or ( is_show = 2 and '" + DateUtil.format(new Date()) + "' BETWEEN show_time_start and show_time_end) )");
        }
        criteria.andEqualTo(HouseChoiceCase.DATA_STATUS, 0);
        //随机排序
        if (request.getParameter("isRand") != null) {
            example.setOrderByClause(" rand() ");
            pageDTO.setPageNum(0);
        } else {
            example.orderBy(Activity.MODIFY_DATE).desc();
        }
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<HouseChoiceCase> list = houseChoiceCaseMapper.selectByExample(example);
        List<Map> listmap = new ArrayList();
        PageInfo pageResult = new PageInfo(list);
        for (HouseChoiceCase v : list) {
            if(!CommonUtil.isEmpty(v.getTextContent())) {
                Map map = BeanUtils.beanToMap(v);
                if (!CommonUtil.isEmpty(v.getAddress())) {
                    String[] address = StringUtils.split(v.getAddress(), ",");
                    String[] addressUrl = StringUtils.split(v.getAddress(), ",");
                    map.put("addressUrl", addressUrl);
                    for (int i = 0; i < address.length; i++) {
                        address[i] = jdAddress + address[i];
                    }
                    map.put("address", address);
                }
                if (!CommonUtil.isEmpty(v.getLabel())) {
                    String[] label = StringUtils.split(v.getLabel(), ",");
                    map.put("label", label);
                } else {
                    String[] label = {};
                    map.put("label", label);
                }
                if (!CommonUtil.isEmpty(v.getImage())) {
                    map.put("imageUrl", v.getImage());
                    map.put("image", jdAddress + v.getImage());
                }
                if (!CommonUtil.isEmpty(v.getTextContent())) {
                    JSONArray itemObjArr = JSON.parseArray(v.getTextContent());
                    List<TextContentDTO> textContentDTOS = new ArrayList<>();
                    for (int i = 0; i < itemObjArr.size(); i++) {
                        TextContentDTO textContentDTO = new TextContentDTO();
                        JSONObject jsonObject = itemObjArr.getJSONObject(i);
                        String[] imageUrl = jsonObject.getString("image").split(",");
                        textContentDTO.setImageUrl(imageUrl);
                        String describe = jsonObject.getString("describe");
                        String headline = jsonObject.getString("headline");
                        String[] images = jsonObject.getString("image").split(",");
                        for (int j = 0; j < images.length; j++) {
                            images[j] = jdAddress + images[j];
                        }
                        textContentDTO.setImage(images);
                        textContentDTO.setHeadline(headline);
                        textContentDTO.setDescribe(describe);
                        textContentDTOS.add(textContentDTO);
                    }
                    map.put("textContent", textContentDTOS);
                }
                listmap.add(map);
            }
        }
        pageResult.setList(listmap);
        return ServerResponse.createBySuccess("ok", pageResult);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    public ServerResponse delHouseChoiceCase(HttpServletRequest request, String id) {
        Example example = new Example(HouseChoiceCase.class);
        example.createCriteria().andEqualTo(HouseChoiceCase.HOUSE_ID, id);
        if (this.houseChoiceCaseMapper.deleteByExample(example) > 0) {
            return ServerResponse.createBySuccessMessage("ok");
        } else {
            return ServerResponse.createByErrorMessage("删除失败，请您稍后再试");
        }
    }

    /**
     * 修改
     *
     * @param houseChoiceCase
     * @return
     */
    public ServerResponse editHouseChoiceCase(HttpServletRequest request, HouseChoiceCase houseChoiceCase) {
        return setHouseChoiceCase(request, 1, houseChoiceCase);
    }

    /**
     * 新增
     *
     * @param houseChoiceCase
     * @return
     */
    public ServerResponse addHouseChoiceCase(HttpServletRequest request, HouseChoiceCase houseChoiceCase) {
        return setHouseChoiceCase(request, 0, houseChoiceCase);
    }

    private ServerResponse setHouseChoiceCase(HttpServletRequest request, int type, HouseChoiceCase houseChoiceCase) {
//        if (!CommonUtil.isEmpty(houseChoiceCase.getTitle())) {
            Example example = new Example(HouseChoiceCase.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo(HouseChoiceCase.TITLE, houseChoiceCase.getTitle());
            if (type == 1) {
                criteria.andNotEqualTo(HouseChoiceCase.ID, houseChoiceCase.getId());
            }
            List list = houseChoiceCaseMapper.selectByExample(example);
            if (list.size() > 0) {
                return ServerResponse.createByErrorMessage("案例名称不能重复");
            }
//        }
        if(null!=houseChoiceCase.getIsShow()) {
            if (houseChoiceCase.getIsShow() == 3 && (
                    CommonUtil.isEmpty(houseChoiceCase.getShowTimeStart()) ||
                            CommonUtil.isEmpty(houseChoiceCase.getShowTimeEnd()))) {
                return ServerResponse.createByErrorMessage("请选择开始和结束时间");
            }
        }
        if (type == 0) {//新增
            if (this.houseChoiceCaseMapper.insertSelective(houseChoiceCase) <= 0) {
                return ServerResponse.createByErrorMessage("新增失败，请您稍后再试");
            }
        } else {//修改
            if (this.houseChoiceCaseMapper.updateByPrimaryKeySelective(houseChoiceCase) <= 0) {
                return ServerResponse.createByErrorMessage("修改失败，请您稍后再试");
            }
        }

        return ServerResponse.createBySuccessMessage("操作成功");
    }
}
