package com.dangjia.acg.service.house;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.house.IHouseDistributionMapper;
import com.dangjia.acg.modle.house.HouseDistribution;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * author: qiyuxiang
 * Date: 2019/1/16 0001
 * Time: 17:56
 */
@Service
public class HouseDistributionService {
    @Autowired
    private IHouseDistributionMapper iHouseDistributionMapper;

    /**
     * 获取所有验房分销
     * @param houseDistribution
     * @return
     */
    public ServerResponse getHouseDistribution(HttpServletRequest request, PageDTO pageDTO, HouseDistribution houseDistribution) {
        Example example = new Example(HouseDistribution.class);
        Example.Criteria criteria=example.createCriteria();
        if(!CommonUtil.isEmpty(houseDistribution.getNickname())) {
            criteria.andLike(HouseDistribution.NICKNAME, "%" + houseDistribution.getNickname() + "%");
        }
        example.orderBy(HouseDistribution.CREATE_DATE).desc();
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<HouseDistribution> list = iHouseDistributionMapper.selectByExample(example);
        PageInfo pageResult = new PageInfo(list);
        return ServerResponse.createBySuccess("ok",pageResult);
    }
   
    /**
     * 新增
     * @param houseDistribution
     * @return
     */
    public ServerResponse addHouseDistribution(HttpServletRequest request,HouseDistribution houseDistribution) {
        if(this.iHouseDistributionMapper.insertSelective(houseDistribution)>0){
            return ServerResponse.createBySuccessMessage("ok");
        }else{
            return ServerResponse.createByErrorMessage("新增失败，请您稍后再试");
        }
    }

}
