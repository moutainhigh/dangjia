package com.dangjia.acg.service.engineer;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.engineer.DjMaintenanceRecordDTO;
import com.dangjia.acg.dto.engineer.DjMaintenanceRecordProductDTO;
import com.dangjia.acg.dto.engineer.DjMaintenanceRecordResponsiblePartyDTO;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordMapper;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordProductMapper;
import com.dangjia.acg.mapper.engineer.DjMaintenanceRecordResponsiblePartyMapper;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecord;
import com.dangjia.acg.modle.engineer.DjMaintenanceRecordResponsibleParty;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 13/12/2019
 * Time: 上午 10:01
 */
@Service
public class DjMaintenanceRecordService {

    @Autowired
    private DjMaintenanceRecordMapper djMaintenanceRecordMapper;
    @Autowired
    private DjMaintenanceRecordResponsiblePartyMapper djMaintenanceRecordResponsiblePartyMapper;
    @Autowired
    private DjMaintenanceRecordProductMapper djMaintenanceRecordProductMapper;
    @Autowired
    private ConfigUtil configUtil;


    /**
     * 查询质保审核列表
     *
     * @param pageDTO
     * @param searchKey
     * @return
     */
    public ServerResponse queryDjMaintenanceRecordList(PageDTO pageDTO, String searchKey) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DjMaintenanceRecordDTO> djMaintenanceRecordDTOS =
                    djMaintenanceRecordMapper.queryDjMaintenanceRecordList(searchKey);
            djMaintenanceRecordDTOS.forEach(djMaintenanceRecordDTO -> {
                if (!CommonUtil.isEmpty(djMaintenanceRecordDTO.getOwnerImage())) {
                    djMaintenanceRecordDTO.setOwnerImages(this.getImage(djMaintenanceRecordDTO.getOwnerImage()));
                }
                if (!CommonUtil.isEmpty(djMaintenanceRecordDTO.getStewardImage())) {
                    djMaintenanceRecordDTO.setStewardImages(this.getImage(djMaintenanceRecordDTO.getStewardImage()));
                }
            });
            if (djMaintenanceRecordDTOS.size() <= 0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
            PageInfo pageInfo = new PageInfo(djMaintenanceRecordDTOS);
            return ServerResponse.createBySuccess("查询成功", pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    private List<String> getImage(String image){
        List<String> strList = new ArrayList<>();
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        List<String> result = Arrays.asList(image.split(","));
        for (int i = 0; i < result.size(); i++) {
            String str = imageAddress + result.get(i);
            strList.add(str);
        }
        return strList;
    }



    /**
     * 查询质保审核详情
     *
     * @param id
     * @return
     */
    public ServerResponse queryDjMaintenanceRecordDetail(String id) {
        try {
            DjMaintenanceRecordDTO djMaintenanceRecordDTOS =
                    djMaintenanceRecordMapper.queryDjMaintenanceRecordDetail(id);
            List<DjMaintenanceRecordProductDTO> djMaintenanceRecordProductDTOS =
                    djMaintenanceRecordProductMapper.queryDjMaintenanceRecordProductList(id);
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            djMaintenanceRecordProductDTOS.forEach(djMaintenanceRecordProductDTO -> {
                djMaintenanceRecordProductDTO.setImage(imageAddress + djMaintenanceRecordProductDTO.getImage());
            });
            djMaintenanceRecordDTOS.setDjMaintenanceRecordProductDTOS(djMaintenanceRecordProductDTOS);
            Example example = new Example(DjMaintenanceRecordResponsibleParty.class);
            example.createCriteria().andEqualTo(DjMaintenanceRecordResponsibleParty.MAINTENANCE_RECORD_ID, id)
                    .andEqualTo(DjMaintenanceRecordResponsibleParty.DATA_STATUS, 0);
            List<DjMaintenanceRecordResponsibleParty> djMaintenanceRecordResponsibleParties =
                    djMaintenanceRecordResponsiblePartyMapper.selectByExample(example);
            List<DjMaintenanceRecordResponsiblePartyDTO> djMaintenanceRecordResponsiblePartyDTOS = new ArrayList<>();
            djMaintenanceRecordResponsibleParties.forEach(djMaintenanceRecordResponsibleParty -> {
                DjMaintenanceRecordResponsiblePartyDTO djMaintenanceRecordResponsiblePartyDTO =
                        djMaintenanceRecordResponsiblePartyMapper.queryDjMaintenanceRecordResponsibleParty(
                                djMaintenanceRecordResponsibleParty.getId(),
                                djMaintenanceRecordResponsibleParty.getResponsiblePartyType());
                djMaintenanceRecordResponsiblePartyDTO.setImage(imageAddress + djMaintenanceRecordResponsiblePartyDTO.getImage());
                djMaintenanceRecordResponsiblePartyDTOS.add(djMaintenanceRecordResponsiblePartyDTO);
            });
            djMaintenanceRecordDTOS.setDjMaintenanceRecordResponsiblePartyDTOS(djMaintenanceRecordResponsiblePartyDTOS);
            return ServerResponse.createBySuccess("查询成功",djMaintenanceRecordDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 处理质保审核
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse setDjMaintenanceRecord(String id,Integer state) {
        try {
            if(state==2){//通过
                //扣除店铺占比金额
                djMaintenanceRecordResponsiblePartyMapper.setAmountDeducted(id);
            }else if(state==3){//拒绝
                DjMaintenanceRecord djMaintenanceRecord=new DjMaintenanceRecord();
                djMaintenanceRecord.setId(id);
                djMaintenanceRecord.setState(3);
                djMaintenanceRecord.setStewardState(1);
                djMaintenanceRecordMapper.updateByPrimaryKeySelective(djMaintenanceRecord);
            }
            return ServerResponse.createBySuccessMessage("处理成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("处理失败");
        }
    }
}
