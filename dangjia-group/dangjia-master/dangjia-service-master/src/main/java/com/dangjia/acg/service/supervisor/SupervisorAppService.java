package com.dangjia.acg.service.supervisor;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.DateUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.supervisor.AuthorityDTO;
import com.dangjia.acg.dto.supervisor.PatrolRecordIndexDTO;
import com.dangjia.acg.mapper.core.IHouseFlowApplyMapper;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.supervisor.ISupervisorAuthorityMapper;
import com.dangjia.acg.modle.core.HouseFlowApply;
import com.dangjia.acg.modle.core.WorkerType;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Ruking.Cheng
 * @descrilbe 督导APP端
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2020/1/6 8:20 PM
 */
@Service
public class SupervisorAppService {
    @Autowired
    private ISupervisorAuthorityMapper iSupervisorAuthorityMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;
    @Autowired
    private ConfigUtil configUtil;
    @Autowired
    private IHouseFlowApplyMapper houseFlowApplyMapper;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;

    /**
     * 督导首页数据获取
     *
     * @param userToken userToken
     * @param cityId    城市ID
     * @return ServerResponse
     */
    public ServerResponse getSupHomePage(String userToken, String cityId) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        List<PatrolRecordIndexDTO> recordIndexDTOS = iSupervisorAuthorityMapper.getSupHomePage(worker.getId(), cityId);
        for (PatrolRecordIndexDTO recordIndexDTO : recordIndexDTOS) {
            recordIndexDTO.setImage(imageAddress + recordIndexDTO.getImage());
        }
        return ServerResponse.createBySuccess("查询成功", recordIndexDTOS);
    }

    /**
     * 督导获取工地列表
     *
     * @param pageDTO   分页
     * @param userToken userToken
     * @param cityId    城市ID
     * @param sortNum   1：全部，2：在施工地，3：本周新开，4：本月竣工，5：今日开工，6：连续3天未开工，7：超期施工，8：维保工地
     * @param type      0：默认，1：附近，2：价格降序（维保才有），3：价格升序（维保才有）
     * @param latitude  用户纬度
     * @param longitude 用户经度
     * @param searchKey 搜索关键字
     * @return ServerResponse
     */
    public ServerResponse getSupHouseList(PageDTO pageDTO, String userToken, String cityId, Integer sortNum,
                                          Integer type, String latitude, String longitude, String searchKey) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member worker = (Member) object;
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<AuthorityDTO> authorityDTOS = iSupervisorAuthorityMapper.getSupHouseList(worker.getId(), cityId, sortNum, type,
                latitude, longitude, searchKey);
        if (authorityDTOS.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(authorityDTOS);
        for (AuthorityDTO authorityDTO : authorityDTOS) {
            if (sortNum == 8) {//维保
                //TODO  查询维保的人员 金额
                authorityDTO.setType(1);
            } else {
                //工期
                int plan = 0;
                if (authorityDTO.getStartDate() != null && authorityDTO.getEndDate() != null) {
                    plan = 1 + DateUtil.daysofTwo(authorityDTO.getStartDate(), authorityDTO.getEndDate());//计划工期天数
                }
                int fatalism = iSupervisorAuthorityMapper.getFatalism(authorityDTO.getHouseId());
                authorityDTO.setConstructionPeriod(plan + "/" + fatalism);
                //查询今日开工记录
                List<HouseFlowApply> todayStartList = houseFlowApplyMapper.getTodayStartByHouseId(authorityDTO.getHouseId(), new Date());
                if (todayStartList == null || todayStartList.size() == 0) {//没有今日开工记录
                    authorityDTO.setPersonnel("无");
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (HouseFlowApply houseFlowApply : todayStartList) {
                        WorkerType workerType = workerTypeMapper.selectByPrimaryKey(houseFlowApply.getWorkerTypeId());
                        if (stringBuilder.length() > 0) {
                            stringBuilder.append(",");
                        }
                        stringBuilder.append(workerType.getName());
                    }
                    authorityDTO.setPersonnel(stringBuilder.toString());
                }
                authorityDTO.setType(0);
            }
        }
        pageResult.setList(authorityDTOS);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

}
