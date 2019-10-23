package com.dangjia.acg.service.member;

import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.api.actuary.app.AppActuaryOperationAPI;
import com.dangjia.acg.api.product.DjBasicsProductAPI;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.product.MemberCollectDTO;
import com.dangjia.acg.mapper.core.IHouseFlowApplyImageMapper;

import com.dangjia.acg.mapper.house.IWebsiteVisitMapper;
import com.dangjia.acg.mapper.member.IMemberCollectMapper;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.house.WebsiteVisit;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberCollect;
import com.dangjia.acg.modle.product.DjBasicsProductTemplate;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.other.IndexPageService;
import com.dangjia.acg.sql.config.DruidConfig;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 用户工地收藏记录处理类
 */
@Service
public class MemberCollectService {
    @Autowired
    private IMemberCollectMapper iMemberCollectMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IndexPageService indexPageService;
    @Autowired
    private IHouseFlowApplyImageMapper houseFlowApplyImageMapper;

    @Autowired
    private DjBasicsProductAPI djBasicsProductAPI;
    @Autowired
    private AppActuaryOperationAPI appActuaryOperationAPI;
    @Autowired
    private IWebsiteVisitMapper iWebsiteVisitMapper;

    private Logger logger = LoggerFactory.getLogger(MemberCollectService.class);

    /**
     * 查询收藏的商品记录
     *
     * @param request
     * @param userToken
     * @param pageDTO
     * @return
     */
    public ServerResponse queryCollectGood(HttpServletRequest request, String userToken, PageDTO pageDTO) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;//
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());//初始化分页插获取用户信息件
            //根据商品用户编号查询收藏记录
            Example example = new Example(MemberCollect.class);
            example.createCriteria().andEqualTo(MemberCollect.MEMBER_ID, member.getId())
                    .andEqualTo(MemberCollect.CONDITION_TYPE,1);
            List<MemberCollect> MemberCollectList = iMemberCollectMapper.selectByExample(example);
            if (MemberCollectList.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            //组装新list
            List<MemberCollectDTO> memberCollectDTOList = new ArrayList<MemberCollectDTO>();
            for (MemberCollect memberCollect : MemberCollectList) {
                MemberCollectDTO memberCollectDTO = new MemberCollectDTO();
                String houseId = memberCollect.getCollectId();
                memberCollectDTO.setObject(JSONObject.parseObject(appActuaryOperationAPI.getCommo(request, houseId, null).getResultObj().toString()));
                memberCollectDTO.setConditionType(memberCollect.getConditionType());
                memberCollectDTO.setHouseId(houseId);
                memberCollectDTO.setMemberId(memberCollect.getMemberId());
                memberCollectDTOList.add(memberCollectDTO);
            }
            PageInfo pageResult = new PageInfo(memberCollectDTOList);
            pageResult.setList(memberCollectDTOList);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.info("系统出错,添加收藏失败",e);
            return ServerResponse.createByErrorMessage("系统出错,添加收藏失败");
        }
    }

    /**
     * 查询收藏的工地记录
     *
     * @param request
     * @param userToken
     * @param pageDTO
     * @return
     */
    public ServerResponse queryCollectHouse(HttpServletRequest request, String userToken, PageDTO pageDTO) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<House> houseList = iMemberCollectMapper.queryCollectHouse(member.getId());
            if (houseList.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            PageInfo pageResult = new PageInfo(houseList);
            List<Map> houseMap = new ArrayList<>();
            for (House house : houseList) {
                house = indexPageService.setHouseTotalPrice(request, house);
                Map map = BeanUtils.beanToMap(house);
                String[] liangArr = {};
                if (house.getLiangDian() != null) {
                    liangArr = house.getLiangDian().split(",");
                }
                List<String> dianList = new ArrayList<>();
                if (!CommonUtil.isEmpty(house.getStyle())) {
                    dianList.add(house.getStyle());
                }
                if (!CommonUtil.isEmpty(house.getLiangDian())) {
                    Collections.addAll(dianList, liangArr);
                }
                if (!CommonUtil.isEmpty(house.getBuildSquare())) {
                    dianList.add(house.getBuildSquare() + "㎡");
                }
                map.put("dianList", dianList);
                map.put("houseName", house.getHouseName());
                map.put("imageUrl", configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class) + houseFlowApplyImageMapper.getHouseFlowApplyImage(house.getId(), null));
                houseMap.add(map);
            }
            pageResult.setList(houseMap);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.info("系统出错,添加收藏失败",e);
            return ServerResponse.createByErrorMessage("系统出错,添加收藏失败");
        }
    }


    /**
     * 优化检测该工地是否已收藏
     *
     * @param request userToken
     * @param houseId 房子ID或者房子
     * @return
     */
    public ServerResponse isMemberCollect(HttpServletRequest request, String houseId, String collectType) {
        try {
            String userToken = request.getParameter(Constants.USER_TOKEY);
            if (userToken != null) {
                Object object = constructionService.getMember(userToken);
                if (object instanceof Member) {
                    Member operator = (Member) object;
                    Example example = new Example(MemberCollect.class);
                    example.createCriteria()
                            .andEqualTo(MemberCollect.MEMBER_ID, operator.getId())
                            .andEqualTo(MemberCollect.COLLECT_ID, houseId)
                            .andEqualTo(MemberCollect.CONDITION_TYPE, collectType);
                    List<MemberCollect> list = iMemberCollectMapper.selectByExample(example);
                    if (list.size() > 0) {
                        return ServerResponse.createBySuccess("该商品已经被收藏!", "1");
                    }
                }
            }
            return ServerResponse.createBySuccess("该商品没有被收藏!", "0");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,检测该工地是否已收藏失败");
        }
    }

    /**
     * 添加收藏
     * @param userToken
     * @param collectId 房子ID或者商品ID
     * @return
     */
    public ServerResponse addMemberCollect(String userToken, String collectId, String collectType) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse)
                return (ServerResponse) object;
            Member member = (Member) object;
            MemberCollect memberCollect = new MemberCollect();
            memberCollect.setMemberId(member.getId());
            memberCollect.setCollectId(collectId);
            memberCollect.setConditionType(collectType);
            //判断是否重复收藏
            Example example = new Example(MemberCollect.class);
            example.createCriteria().andEqualTo(MemberCollect.COLLECT_ID, collectId)
                    .andEqualTo(MemberCollect.MEMBER_ID, member.getId());
            List<MemberCollect> listMemberCollect = iMemberCollectMapper.selectByExample(example);
            if (listMemberCollect.size() > 0)
                return ServerResponse.createByErrorMessage("该商品已经被收藏!");
            iMemberCollectMapper.insertSelective(memberCollect);
            return ServerResponse.createBySuccessMessage("商品收藏成功!");
        } catch (Exception e) {
           logger.info("系统出错,添加收藏失败",e);
           return ServerResponse.createByErrorMessage("系统出错,添加收藏失败");
        }

    }


    /**
     * 取消收藏
     * @param userToken
     * @param collectId
     * @param collectType
     * @return
     */
    public ServerResponse delMemberCollect(String userToken, String collectId, String collectType) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse)
                return (ServerResponse) object;
            Member member = (Member) object;
            Example example = new Example(MemberCollect.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo(MemberCollect.COLLECT_ID, collectId)
                    .andEqualTo(MemberCollect.MEMBER_ID, member.getId())
                    .andEqualTo(MemberCollect.CONDITION_TYPE, collectType);
            iMemberCollectMapper.deleteByExample(example);
            return ServerResponse.createBySuccessMessage("取消收藏成功!");
        } catch (Exception e) {
            logger.info("系统出错,添加收藏失败",e);
            return ServerResponse.createByErrorMessage("系统出错,添加收藏失败");
        }
    }


    /**
     * 猜你喜欢
     * @param userToken
     * @return
     */
    public ServerResponse queryRelated(String userToken) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        Example example=new Example(WebsiteVisit.class);
        example.createCriteria().andLike(WebsiteVisit.ROUTE,"%"+member.getId()+"%")
                .andEqualTo(WebsiteVisit.DATA_STATUS,0);
        example.orderBy(WebsiteVisit.COUNT).desc();
        List<WebsiteVisit> websiteVisits = iWebsiteVisitMapper.selectByExample(example);
        if(websiteVisits.size()<=0){
            List<DjBasicsProductTemplate> djBasicsProductTemplates = djBasicsProductAPI.queryRandomProduct("12");
            return ServerResponse.createBySuccess("查询成功",djBasicsProductTemplates);
        }else{
            List<DjBasicsProductTemplate> djBasicsProductTemplates=new ArrayList<>();
            for (int i = 0; i < websiteVisits.size(); i++) {
                if(!CommonUtil.isEmpty(websiteVisits.get(i).getRoute())){
                    String[] split = websiteVisits.get(i).getRoute().split(",");
                    List<DjBasicsProductTemplate> djBasicsProductTemplates1 = djBasicsProductAPI.queryRandomProductByCategoryId(split[1], (12-djBasicsProductTemplates.size())+"");
                    djBasicsProductTemplates.addAll(djBasicsProductTemplates1);
                    if(djBasicsProductTemplates.size()==12){
                        break;
                    }
                }
            }
            if(djBasicsProductTemplates.size()<12){
                djBasicsProductTemplates.addAll(djBasicsProductAPI.queryRandomProduct((12-djBasicsProductTemplates.size())+""));
            }
            return ServerResponse.createBySuccess("查询成功",djBasicsProductTemplates);
        }
    }


}
