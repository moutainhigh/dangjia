package com.dangjia.acg.service.repair;

import com.dangjia.acg.api.data.ForMasterAPI;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.repair.MendDeliverDTO;
import com.dangjia.acg.mapper.house.IHouseMapper;
import com.dangjia.acg.mapper.member.IMemberMapper;
import com.dangjia.acg.mapper.repair.IMendDeliverMapper;
import com.dangjia.acg.mapper.repair.IMendMaterialMapper;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.repair.MendDeliver;
import com.dangjia.acg.modle.repair.MendMateriel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/5/24
 * Time: 16:27
 */
@Service
public class MendDeliverServices {

    @Autowired
    private IHouseMapper iHouseMapper;
    @Autowired
    private IMendMaterialMapper iMendMaterialMapper;
    @Autowired
    private IMemberMapper iMemberMapper;
    @Autowired
    private ForMasterAPI forMasterAPI;
    @Autowired
    private IMendDeliverMapper iMendDeliverMapper;

    /**
     * 根据供应商Id查询对应供应商的退货单列表
     * @param supplierId
     * @return
     */
    public ServerResponse mendDeliverList(String supplierId){
        try {
            Example example=new Example(MendDeliver.class);
            example.createCriteria().andEqualTo(MendDeliver.SUPPLIER_ID,supplierId);
            example.orderBy(MendDeliver.BACK_TIME).desc();
            List<MendDeliver> mendDelivers = iMendDeliverMapper.selectByExample(example);
            return ServerResponse.createBySuccess("查询成功",mendDelivers);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 根据退货表Id查看退货单详情
     * （供应商和财务共用此方法）
     * @param mendDeliverId
     * @return
     */
    public ServerResponse mendDeliverDetail(String mendDeliverId){
        try {
            MendDeliver mendDeliver = iMendDeliverMapper.selectByPrimaryKey(mendDeliverId);
            if(null!=mendDeliver) {
                House house = iHouseMapper.selectByPrimaryKey(mendDeliver.getHouseId());
                Member member = iMemberMapper.selectByPrimaryKey(house.getMemberId());
                Example example = new Example(MendMateriel.class);
                example.createCriteria().andEqualTo(MendMateriel.REPAIR_MEND_DELIVER_ID, mendDeliver.getId());
                List<MendMateriel> mendMateriels = iMendMaterialMapper.selectByExample(example);
                double sumprice = 0D;
                MendDeliverDTO mendDeliverDTO = new MendDeliverDTO();
                for (MendMateriel mendMateriel : mendMateriels) {
                    sumprice += mendMateriel.getActualPrice();
                    mendMateriel.setBrandName(forMasterAPI.brandName(house.getCityId(), mendMateriel.getProductId()));
                }
                mendDeliverDTO.setMendDeliverId(mendDeliver.getId());
                mendDeliverDTO.setNumber(mendDeliver.getNumber());
                mendDeliverDTO.setHouseId(mendDeliver.getHouseId());
                mendDeliverDTO.setHouseName(house.getHouseName());
                mendDeliverDTO.setMemberId(house.getMemberId());
                mendDeliverDTO.setMemberMobile(member.getMobile());
                mendDeliverDTO.setApplicantName(mendDeliver.getShipName());
                mendDeliverDTO.setApplicantMobile(mendDeliver.getShipMobile());
                mendDeliverDTO.setMemberName(member.getName());
                mendDeliverDTO.setList(mendMateriels);
                mendDeliverDTO.setCount(mendMateriels.size());
                mendDeliverDTO.setSumprice(sumprice);
                return ServerResponse.createBySuccess("查询成功",mendDeliverDTO);
            }else{
                return ServerResponse.createBySuccess("无此数据");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}
