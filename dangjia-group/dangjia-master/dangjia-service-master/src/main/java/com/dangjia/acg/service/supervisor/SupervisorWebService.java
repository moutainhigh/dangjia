package com.dangjia.acg.service.supervisor;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dto.supervisor.AuthorityDTO;
import com.dangjia.acg.mapper.supervisor.ISupervisorAuthorityMapper;
import com.dangjia.acg.modle.supervisor.DjBasicsSupervisorAuthority;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author Ruking.Cheng
 * @descrilbe 督导中台
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2020/1/6 7:28 PM
 */
@Service
public class SupervisorWebService {
    @Autowired
    private ISupervisorAuthorityMapper iSupervisorAuthorityMapper;

    /**
     * 督导授权待选择列表
     *
     * @param pageDTO    分页
     * @param cityId     城市ID
     * @param memberId   督导ID
     * @param visitState 0待确认开工,1装修中,2休眠中,3已完工 (注意：-1 查全部)
     * @param searchKey  搜索关键字
     * @return 工地列表
     */
    public ServerResponse getStayAuthorityList(PageDTO pageDTO, String cityId, String memberId, Integer visitState, String searchKey) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<AuthorityDTO> authorityDTOS = iSupervisorAuthorityMapper.getStayAuthorityList(cityId, visitState, searchKey);
        if (authorityDTOS.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(authorityDTOS);
        for (AuthorityDTO authorityDTO : authorityDTOS) {
            Example example = new Example(DjBasicsSupervisorAuthority.class);
            example.createCriteria()
                    .andEqualTo(DjBasicsSupervisorAuthority.MEMBER_ID, memberId)
                    .andEqualTo(DjBasicsSupervisorAuthority.HOUSE_ID, authorityDTO.getHouseId());
            if (iSupervisorAuthorityMapper.selectCountByExample(example) > 0) {
                authorityDTO.setSelection(true);
            } else {
                authorityDTO.setSelection(false);
            }
        }
        pageResult.setList(authorityDTOS);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    /**
     * 获取督导授权列表
     *
     * @param pageDTO    分页
     * @param memberId   督导ID
     * @param visitState 0待确认开工,1装修中,2休眠中,3已完工 (注意：-1 查全部)
     * @param searchKey  搜索关键字
     * @return 工地列表
     */
    public ServerResponse getAuthorityList(PageDTO pageDTO, String memberId, Integer visitState, String searchKey) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        List<AuthorityDTO> authorityDTOS = iSupervisorAuthorityMapper.getAuthorityList(memberId, visitState, searchKey);
        if (authorityDTOS.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(authorityDTOS);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }

    /**
     * 督导添加授权
     *
     * @param memberId 督导ID
     * @param houseId  房子ID
     * @return ServerResponse
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse addAuthority(String memberId, String houseId, String userId) {
        if (CommonUtil.isEmpty(memberId) || CommonUtil.isEmpty(houseId) || CommonUtil.isEmpty(userId)) {
            return ServerResponse.createByErrorMessage("传入参数有误");
        }
        Example example = new Example(DjBasicsSupervisorAuthority.class);
        example.createCriteria()
                .andEqualTo(DjBasicsSupervisorAuthority.MEMBER_ID, memberId)
                .andEqualTo(DjBasicsSupervisorAuthority.HOUSE_ID, houseId);
        if (iSupervisorAuthorityMapper.selectCountByExample(example) > 0) {
            return ServerResponse.createByErrorMessage("请勿重复添加");
        }
        DjBasicsSupervisorAuthority authority = new DjBasicsSupervisorAuthority();
        authority.setMemberId(memberId);
        authority.setHouseId(houseId);
        authority.setOperateId(userId);
        if (iSupervisorAuthorityMapper.insertSelective(authority) > 0) {
            return ServerResponse.createBySuccessMessage("增加成功");
        } else {
            return ServerResponse.createByErrorMessage("新增失败，请您稍后再试");
        }
    }

    /**
     * 督导移除授权
     *
     * @param memberId
     * @param houseId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse deleteAuthority(String memberId, String houseId) {
        if (CommonUtil.isEmpty(memberId) || CommonUtil.isEmpty(houseId)) {
            return ServerResponse.createByErrorMessage("传入参数有误");
        }
        Example example = new Example(DjBasicsSupervisorAuthority.class);
        example.createCriteria()
                .andEqualTo(DjBasicsSupervisorAuthority.MEMBER_ID, memberId)
                .andEqualTo(DjBasicsSupervisorAuthority.HOUSE_ID, houseId);
        if (iSupervisorAuthorityMapper.deleteByExample(example) > 0) {
            return ServerResponse.createBySuccessMessage("删除成功");
        } else {
            return ServerResponse.createByErrorMessage("删除失败，请您稍后再试");
        }
    }
}
