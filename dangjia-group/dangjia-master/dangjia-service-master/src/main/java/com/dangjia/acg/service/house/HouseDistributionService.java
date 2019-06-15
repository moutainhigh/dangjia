package com.dangjia.acg.service.house;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.house.IHouseDistributionMapper;
import com.dangjia.acg.mapper.other.ICityMapper;
import com.dangjia.acg.modle.house.HouseDistribution;
import com.dangjia.acg.modle.member.AccessToken;
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

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private ICityMapper iCityMapper;
    /**
     * 获取所有验房分销
     *
     * @param houseDistribution
     * @return
     */
    public ServerResponse getHouseDistribution(HttpServletRequest request, PageDTO pageDTO,
                                               HouseDistribution houseDistribution,
                                               String startDate, String endDate) {
        String userToken = request.getParameter(Constants.USER_TOKEY);
        Example example = new Example(HouseDistribution.class);
        Example.Criteria criteria = example.createCriteria();
        if (!CommonUtil.isEmpty(houseDistribution.getNickname())) {
            criteria.andLike(HouseDistribution.NICKNAME, "%" + houseDistribution.getNickname() + "%");
        }
        if (!CommonUtil.isEmpty(houseDistribution.getType())) {
            criteria.andEqualTo(HouseDistribution.TYPE, houseDistribution.getType());
        }
        if (!CommonUtil.isEmpty(houseDistribution.getState())) {
            criteria.andEqualTo(HouseDistribution.STATE, houseDistribution.getState());
        }
        if (!CommonUtil.isEmpty(userToken)) {
            AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
            if (accessToken != null)
                criteria.andEqualTo(HouseDistribution.PHONE, accessToken.getPhone());
        }
        if (!CommonUtil.isEmpty(startDate) && !CommonUtil.isEmpty(endDate)) {
            if (startDate.equals(endDate)) {
                startDate = startDate + " " + "00:00:00";
                endDate = endDate + " " + "23:59:59";
            }
            criteria.andBetween(HouseDistribution.CREATE_DATE, startDate, endDate);
        }
        example.orderBy(HouseDistribution.CREATE_DATE).desc();
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<HouseDistribution> list = iHouseDistributionMapper.selectByExample(example);
        PageInfo pageResult = new PageInfo(list);
        return ServerResponse.createBySuccess("ok", pageResult);
    }

    /**
     * 新增
     * @param houseDistribution
     * @return
     */
    public ServerResponse addHouseDistribution(HttpServletRequest request,HouseDistribution houseDistribution) {

        String userToken = request.getParameter(Constants.USER_TOKEY);
        String cityId = request.getParameter(Constants.CITY_ID);
        AccessToken accessToken = redisClient.getCache(userToken + Constants.SESSIONUSERID, AccessToken.class);
        houseDistribution.setId(System.currentTimeMillis()+"-"+(int)(Math.random()*9000+1000));
        houseDistribution.setHead(accessToken.getMember().getHead());
        houseDistribution.setNickname(accessToken.getMember().getNickName());
        houseDistribution.setPhone(accessToken.getMember().getMobile());
        houseDistribution.setCity(iCityMapper.selectByPrimaryKey(cityId).getName());
        houseDistribution.setPrice(DjConstants.distribution.PRICE.doubleValue());
        houseDistribution.setOpenid(accessToken.getMemberId());
        houseDistribution.setSex("0");
        houseDistribution.setState(0);
        houseDistribution.setType(1);
        if(this.iHouseDistributionMapper.insertSelective(houseDistribution)>0){
            return ServerResponse.createBySuccess("ok",houseDistribution.getId());
        }else{
            return ServerResponse.createByErrorMessage("新增失败，请您稍后再试");
        }
    }

}
