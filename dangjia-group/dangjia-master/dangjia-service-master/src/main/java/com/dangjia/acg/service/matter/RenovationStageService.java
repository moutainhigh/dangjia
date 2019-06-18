package com.dangjia.acg.service.matter;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.matter.IRenovationStageMapper;
import com.dangjia.acg.modle.matter.RenovationStage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 装修攻略阶段配置
 */
@Service
public class RenovationStageService {
    @Autowired
    private IRenovationStageMapper renovationStageMapper;
    @Autowired
    private ConfigUtil configUtil;

    /**
     * 查询所有装修指南阶段配置
     *
     * @return
     */
    public ServerResponse queryRenovationStage() {
        Example example = new Example(RenovationStage.class);
        example.createCriteria().andEqualTo(RenovationStage.DATA_STATUS, 0);
        example.orderBy(RenovationStage.CREATE_DATE).desc();
        List<RenovationStage> rmList = renovationStageMapper.selectByExample(example);
        if (rmList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        List<Map> list = new ArrayList<>();
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        for (RenovationStage renovationStage : rmList) {
            Map map = BeanUtils.beanToMap(renovationStage);
            String imageUrl = renovationStage.getImage();
            map.put("imageUrl", CommonUtil.isEmpty(imageUrl) ? null : (imageAddress + imageUrl));
            list.add(map);
        }
        return ServerResponse.createBySuccess("获取所有装修指南阶段配置成功", list);
    }

    /**
     * 新增装修指南阶段配置
     *
     * @return
     */
    public ServerResponse addRenovationStage(String name, String image, String workerTypeId) {
        if (CommonUtil.isEmpty(name)) {
            return ServerResponse.createBySuccessMessage("请输入名称");
        }
        if (CommonUtil.isEmpty(image)) {
            return ServerResponse.createBySuccessMessage("请上传图片");
        }
        RenovationStage renovationStage = new RenovationStage();
        renovationStage.setName(name);
        renovationStage.setImage(image);
        renovationStage.setWorkerTypeId(workerTypeId);
        renovationStageMapper.insert(renovationStage);
        return ServerResponse.createBySuccessMessage("新增装修指南阶段配置成功");
    }

    /**
     * 修改装修指南阶段配置
     *
     * @return
     */
    public ServerResponse updateRenovationStage(String id, String name, String image, String workerTypeId) {
        if (CommonUtil.isEmpty(name) && CommonUtil.isEmpty(image)) {
            return ServerResponse.createBySuccessMessage("请编辑需要修改的信息");
        }
        RenovationStage renovationStage = renovationStageMapper.selectByPrimaryKey(id);
        if (renovationStage == null) {
            return ServerResponse.createBySuccessMessage("该指南配置不存在");
        }
        if (!CommonUtil.isEmpty(name)) {
            renovationStage.setName(name);
        }
        if (!CommonUtil.isEmpty(image)) {
            renovationStage.setImage(image);
        }
        if (!CommonUtil.isEmpty(workerTypeId)) {
            renovationStage.setWorkerTypeId(workerTypeId);
        }
        renovationStage.setModifyDate(new Date());
        renovationStageMapper.updateByPrimaryKeySelective(renovationStage);
        return ServerResponse.createBySuccessMessage("修改装修指南阶段配置成功");
    }

    /**
     * 删除装修指南阶段配置
     *
     * @return
     */
    public ServerResponse deleteRenovationStage(String id) {
        RenovationStage renovationStage = renovationStageMapper.selectByPrimaryKey(id);
        if (renovationStage == null) {
            return ServerResponse.createBySuccessMessage("该指南配置不存在");
        }
        renovationStage.setDataStatus(1);
        renovationStage.setModifyDate(new Date());
        renovationStageMapper.updateByPrimaryKeySelective(renovationStage);
        return ServerResponse.createBySuccessMessage("删除装修指南阶段配置成功");
    }

}
