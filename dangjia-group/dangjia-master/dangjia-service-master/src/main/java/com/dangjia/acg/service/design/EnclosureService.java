package com.dangjia.acg.service.design;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.design.IEnclosureMapper;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.modle.design.Enclosure;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Ruking.Cheng
 * @descrilbe 附件管理接口
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/12/14 3:07 PM
 */
@Service
public class EnclosureService {
    @Autowired
    private IEnclosureMapper iEnclosureMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private ConfigUtil configUtil;

    public ServerResponse addEnclosure(String userToken, String userId, String houseId,
                                       String name, String enclosure, int enclosureType, String remarks) {
        House house = iHouseMapper.selectByPrimaryKey(houseId);
        if (house == null) {
            return ServerResponse.createByErrorMessage("没有查询到相关房子");
        }
        Object objectm = constructionService.getMember(userToken);
        Member member = null;
        if (objectm instanceof Member) {
            member = (Member) objectm;
        }
        if (member == null && CommonUtil.isEmpty(userId)) {
            return ServerResponse.createbyUserTokenError();
        }
        if (CommonUtil.isEmpty(name) || CommonUtil.isEmpty(enclosure)) {
            return ServerResponse.createByErrorMessage("附件信息有缺损，请补全");
        }
        Enclosure e = new Enclosure();
        e.setName(name);
        e.setEnclosure(enclosure);
        e.setEnclosureType(enclosureType);
        e.setRemarks(remarks);
        if (member != null) {
            e.setUserId(member.getId());
            e.setUserType(1);
        } else {
            e.setUserId(userId);
            e.setUserType(0);
        }
        iEnclosureMapper.insertSelective(e);
        return ServerResponse.createBySuccessMessage("提交成功");
    }

    public ServerResponse deleteEnclosure(String enclosureId) {
        Enclosure enclosure = iEnclosureMapper.selectByPrimaryKey(enclosureId);
        if (enclosure == null) {
            return ServerResponse.createByErrorMessage("未找到该附件");
        }
        enclosure.setModifyDate(new Date());
        enclosure.setDataStatus(1);
        iEnclosureMapper.updateByPrimaryKeySelective(enclosure);
        return ServerResponse.createBySuccessMessage("删除成功");
    }

    public ServerResponse selectEnclosureList(String houseId, int enclosureType) {
        Example example = new Example(Enclosure.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(Enclosure.HOUSE_ID, houseId);
        criteria.andEqualTo(Enclosure.ENCLOSURE_TYPE, enclosureType);
        criteria.andEqualTo(Enclosure.DATA_STATUS, 0);
        example.orderBy(Enclosure.CREATE_DATE).desc();
        List<Enclosure> enclosures = iEnclosureMapper.selectByExample(example);
        if (enclosures.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "暂无附件");
        }
        List<Map> maps = (List<Map>) BeanUtils.listToMap(enclosures);
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        for (Map map : maps) {
            map.put("enclosureUrl", imageAddress + map.get(Enclosure.ENCLOSURE));
        }
        return ServerResponse.createBySuccess("查询成功", maps);
    }
}
