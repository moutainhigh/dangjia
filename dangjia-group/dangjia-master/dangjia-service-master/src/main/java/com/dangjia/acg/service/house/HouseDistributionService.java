package com.dangjia.acg.service.house;

import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.DjConstants;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.mapper.house.IHouseDistributionMapper;
import com.dangjia.acg.mapper.house.IWebsiteVisitMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.other.ICityMapper;
import com.dangjia.acg.modle.house.HouseDistribution;
import com.dangjia.acg.modle.house.WebsiteVisit;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
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
    private IMemberMapper memberMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private ICityMapper iCityMapper;
    @Autowired
    private IWebsiteVisitMapper websiteVisitMapper;

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
            Object object = constructionService.getMember(userToken);
            if (object instanceof Member) {
                Member operator = (Member) object;
                criteria.andEqualTo(HouseDistribution.PHONE, operator.getMobile());
            }
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
     *
     * @param houseDistribution
     * @return
     */
    public ServerResponse addHouseDistribution(HttpServletRequest request, HouseDistribution houseDistribution) {
        String userToken = request.getParameter(Constants.USER_TOKEY);
        String cityId = request.getParameter(Constants.CITY_ID);
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            String modifyDate = request.getParameter(HouseDistribution.MODIFY_DATE);
            houseDistribution.setId(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
            houseDistribution.setHead("");
            houseDistribution.setCity(iCityMapper.selectByPrimaryKey(cityId).getName());
            houseDistribution.setPrice(0d);
            houseDistribution.setSex("0");
            houseDistribution.setState(2);
            houseDistribution.setType(2);
            if (houseDistribution.getModifyDate() == null && !CommonUtil.isEmpty(modifyDate)) {
                houseDistribution.setModifyDate(DateUtil.toDate(modifyDate));
            }
        } else {
            Member user = (Member) object;
            user = memberMapper.selectByPrimaryKey(user.getId());
            houseDistribution.setId(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
            houseDistribution.setHead(user.getHead());
            houseDistribution.setNickname(user.getNickName());
            houseDistribution.setPhone(user.getMobile());
            houseDistribution.setCity(iCityMapper.selectByPrimaryKey(cityId).getName());
            houseDistribution.setPrice(DjConstants.distribution.PRICE.doubleValue());
            houseDistribution.setOpenid(user.getId());
            houseDistribution.setSex("0");
            houseDistribution.setState(0);
            houseDistribution.setType(1);
        }
        if (this.iHouseDistributionMapper.insertSelective(houseDistribution) > 0) {
            return ServerResponse.createBySuccess("ok", houseDistribution.getId());
        } else {
            return ServerResponse.createByErrorMessage("新增失败，请您稍后再试");
        }
    }

    /**
     * 新增或更新访问量
     *
     * @param websiteVisit
     * @return
     */
    public ServerResponse addWebsiteVisit(HttpServletRequest request, WebsiteVisit websiteVisit) {
        websiteVisit.setId(System.currentTimeMillis() + "-" + (int) (Math.random() * 9000 + 1000));
        websiteVisit.setIp(getIPAddress(request));
        websiteVisit.setCount(1);
        websiteVisit.setCreateDate(new Date());
        websiteVisit.setModifyDate(new Date());
        if (this.websiteVisitMapper.insertSelective(websiteVisit) > 0) {
            return ServerResponse.createBySuccess("ok", websiteVisit.getId());
        } else {
            return ServerResponse.createByErrorMessage("新增失败，请您稍后再试");
        }
    }

    public static String getIPAddress(HttpServletRequest request) {
        String ip = null;

        //X-Forwarded-For：Squid 服务代理
        String ipAddresses = request.getHeader("X-Forwarded-For");

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //Proxy-Client-IP：apache 服务代理
            ipAddresses = request.getHeader("Proxy-Client-IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //WL-Proxy-Client-IP：weblogic 服务代理
            ipAddresses = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //HTTP_CLIENT_IP：有些代理服务器
            ipAddresses = request.getHeader("HTTP_CLIENT_IP");
        }

        if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            //X-Real-IP：nginx服务代理
            ipAddresses = request.getHeader("X-Real-IP");
        }

        //有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
        if (ipAddresses != null && ipAddresses.length() != 0) {
            ip = ipAddresses.split(",")[0];
        }

        //还是不能获取到，最后再通过request.getRemoteAddr();获取
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
