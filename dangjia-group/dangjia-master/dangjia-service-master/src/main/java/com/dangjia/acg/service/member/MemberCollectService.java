package com.dangjia.acg.service.member;

import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.core.IHouseFlowApplyImageMapper;
import com.dangjia.acg.mapper.deliver.IOrderMapper;
import com.dangjia.acg.mapper.member.IMemberCollectMapper;
import com.dangjia.acg.modle.config.Sms;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberCollect;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.dangjia.acg.service.other.IndexPageService;
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
    /**
     * 查询收藏的工地记录
     */
    public ServerResponse queryCollectHouse(HttpServletRequest request, String userToken, PageDTO pageDTO) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<House> houseList=iMemberCollectMapper.queryCollectHouse(member.getId());
            if(houseList.size()<=0){
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            }
            PageInfo pageResult = new PageInfo(houseList);
            List<Map> houseMap = new ArrayList<>();
            for (House house : houseList) {
                house = indexPageService.setHouseTotalPrice(request,house);
                Map map = BeanUtils.beanToMap(house);
                map.put("houseName", house.getHouseName());
                map.put("imageUrl", configUtil.getValue(SysConfig.PUBLIC_DANGJIA_ADDRESS, String.class)+houseFlowApplyImageMapper.getHouseFlowApplyImage(house.getId(),null));
                houseMap.add(map);
            }
            pageResult.setList(houseMap);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,获取数据失败");
        }
    }


    /**
     *  检测该工地是否已收藏
     * @param request userToken
     * @param houseId 房子ID
     * @return
     */
    public ServerResponse isMemberCollect(HttpServletRequest request,String houseId) {
        String userToken = request.getParameter(Constants.USER_TOKEY);
        if (userToken != null) {
            Object object = constructionService.getMember(userToken);
            if (object instanceof Member) {
                Member operator = (Member) object;
                Example example = new Example(MemberCollect.class);
                example.createCriteria()
                        .andEqualTo(MemberCollect.MEMBER_ID, operator.getId())
                        .andEqualTo(MemberCollect.HOUSE_ID,houseId);
                List<MemberCollect> list = iMemberCollectMapper.selectByExample(example);
                if(list.size()>0){
                    return ServerResponse.createBySuccess("ok","1");
                }
            }
        }
        return ServerResponse.createBySuccess("ok","0");
    }

    /**
     * 添加收藏
     * @param request userToken
     * @param houseId 房子ID
     * @return
     */
    public ServerResponse addMemberCollect(HttpServletRequest request,String houseId) {
        String userToken = request.getParameter(Constants.USER_TOKEY);
        MemberCollect memberCollect=new MemberCollect();
        if (userToken != null) {
            Object object = constructionService.getMember(userToken);
            if (object instanceof Member) {
                Member operator = (Member) object;
                memberCollect.setMemberId(operator.getId());
                memberCollect.setHouseId(houseId);
                iMemberCollectMapper.insertSelective(memberCollect);
            }
        }
        return ServerResponse.createBySuccessMessage("ok");
    }

    /**
     * 取消收藏
     * @param request userToken
     * @param houseId 收藏的工地ID
     * @return
     */
    public ServerResponse delMemberCollect(HttpServletRequest request,String houseId) {
        String userToken = request.getParameter(Constants.USER_TOKEY);
        if (userToken != null) {
            Object object = constructionService.getMember(userToken);
            if (object instanceof Member) {
                Member operator = (Member) object;
                Example example = new Example(MemberCollect.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo(MemberCollect.HOUSE_ID,houseId);
                criteria.andEqualTo(MemberCollect.MEMBER_ID,operator.getId());
                iMemberCollectMapper.deleteByExample(example);
            }
        }
        return ServerResponse.createBySuccessMessage("ok");
    }


}
