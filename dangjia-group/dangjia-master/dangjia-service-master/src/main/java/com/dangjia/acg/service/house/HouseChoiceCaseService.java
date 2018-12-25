package com.dangjia.acg.service.house;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.house.IHouseChoiceCaseMapper;
import com.dangjia.acg.modle.activity.Activity;
import com.dangjia.acg.modle.house.HouseChoiceCase;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
     * @param houseChoiceCase
     * @return
     */
    public ServerResponse getHouseChoiceCases(HttpServletRequest request, PageDTO pageDTO,HouseChoiceCase houseChoiceCase) {
        Example example = new Example(HouseChoiceCase.class);
        Example.Criteria criteria=example.createCriteria();
        if(!CommonUtil.isEmpty(houseChoiceCase.getCityId())) {
            criteria.andEqualTo("cityId", houseChoiceCase.getCityId());
        }
//        //随机排序
        if(request.getParameter("isRand")!=null){
            example.setOrderByClause(" rand() ");
            pageDTO.setPageNum(0);
        }else {
            example.orderBy(Activity.MODIFY_DATE).desc();
        }
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<HouseChoiceCase> list = houseChoiceCaseMapper.selectByExample(example);
        List<Map> listmap = new  ArrayList();
        PageInfo pageResult = new PageInfo(list);
        for (HouseChoiceCase v:list){
            Map map= BeanUtils.beanToMap(v);
            map.put("imageUrl",v.getImage());
            map.put("image", configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class)+v.getImage());
            listmap.add(map);
        }
        pageResult.setList(listmap);
        return ServerResponse.createBySuccess("ok",pageResult);
    }
    /**
     * 删除
     * @param id
     * @return
     */
    public ServerResponse delHouseChoiceCase(HttpServletRequest request, String id) {
        if(this.houseChoiceCaseMapper.deleteByPrimaryKey(String.valueOf(id))>0){
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("删除失败，请您稍后再试");
        }
    }

    /**
     * 修改
     * @param houseChoiceCase
     * @return
     */
    public ServerResponse editHouseChoiceCase(HttpServletRequest request, HouseChoiceCase houseChoiceCase) {
        //查看该权限是否有子节点，如果有，先删除子节点
        if(this.houseChoiceCaseMapper.updateByPrimaryKeySelective(houseChoiceCase)>0){
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("修改失败，请您稍后再试");
        }
    }
    /**
     * 新增
     * @param houseChoiceCase
     * @return
     */
    public ServerResponse addHouseChoiceCase(HttpServletRequest request,HouseChoiceCase houseChoiceCase) {
        //查看该权限是否有子节点，如果有，先删除子节点
        if(this.houseChoiceCaseMapper.insertSelective(houseChoiceCase)>0){
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("新增失败，请您稍后再试");
        }
    }
}
