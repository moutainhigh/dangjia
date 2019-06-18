package com.dangjia.acg.service.matter;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.mapper.matter.IRenovationManualMapper;
import com.dangjia.acg.mapper.matter.IRenovationStageMapper;
import com.dangjia.acg.modle.matter.RenovationManual;
import com.dangjia.acg.modle.matter.RenovationStage;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * author: zmj
 * Date: 2018/11/20 0001
 * Time: 17:56
 */
@Service
public class RenovationManualService {
    @Autowired
    private IRenovationManualMapper renovationManualMapper;
    @Autowired
    private IRenovationStageMapper renovationStageMapper;
    @Autowired
    private ConfigUtil configUtil;

    public ServerResponse queryRenovationManual(PageDTO pageDTO, String workerTypeId, String name) {
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        Example example = new Example(RenovationManual.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(RenovationManual.DATA_STATUS, 0);
        if (!CommonUtil.isEmpty(workerTypeId)) {
            criteria.andEqualTo(RenovationManual.WORKER_TYPE_ID, workerTypeId);
        }
        if (!CommonUtil.isEmpty(name)) {
            criteria.andLike(RenovationManual.NAME, "%" + name + "%");
        }
        List<RenovationManual> rmList = renovationManualMapper.selectByExample(example);
        if (rmList.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), ServerCode.NO_DATA.getDesc());
        }
        PageInfo pageResult = new PageInfo(rmList);
        List<Map<String, Object>> listMap = new ArrayList<>();
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        for (RenovationManual renovationManual : rmList) {
            Map<String, Object> map = BeanUtils.beanToMap(renovationManual);
            String imageUrl = renovationManual.getImage();
            map.put("imageUrl", CommonUtil.isEmpty(imageUrl) ? null : (imageAddress + imageUrl));
            RenovationStage renovationStage = renovationStageMapper.selectByPrimaryKey(renovationManual.getWorkerTypeId());
            if (renovationStage != null) {
                map.put("workerTypeName", renovationStage.getName());
            }
            listMap.add(map);
        }
        pageResult.setList(listMap);
        return ServerResponse.createBySuccess("获取所有装修指南成功", pageResult);
    }

    /**
     * 新增装修指南
     *
     * @return
     */
    public ServerResponse addRenovationManual(String name, String workerTypeId, String urlName, String test,
                                              String url, String types, Integer state, Integer orderNumber, String image) {
        if (CommonUtil.isEmpty(name)) {
            return ServerResponse.createBySuccessMessage("请输入名称");
        }
        if (CommonUtil.isEmpty(workerTypeId)) {
            return ServerResponse.createBySuccessMessage("请选择阶段");
        }
        if (CommonUtil.isEmpty(test)) {
            return ServerResponse.createBySuccessMessage("请编辑内容");
        }
        if (CommonUtil.isEmpty(image)) {
            return ServerResponse.createBySuccessMessage("请上传图片");
        }
        RenovationManual renovationManual = new RenovationManual();
        renovationManual.setName(name);
        renovationManual.setWorkerTypeId(workerTypeId);
        renovationManual.setUrlName(urlName);
        renovationManual.setTest(test);
        renovationManual.setUrl(url);
        renovationManual.setTypes(types);
        renovationManual.setState(state);
        renovationManual.setOrderNumber(orderNumber);
        renovationManual.setImage(image);
        renovationManualMapper.insert(renovationManual);
        return ServerResponse.createBySuccessMessage("新增装修指南成功");
    }

    /**
     * 修改装修指南
     *
     * @return
     */
    public ServerResponse updateRenovationManual(String id, String name, String workerTypeId, String urlName, String test,
                                                 String url, String types, Integer state, Integer orderNumber, String image) {
        RenovationManual renovationManual = renovationManualMapper.selectByPrimaryKey(id);
        if (renovationManual == null) {
            return ServerResponse.createBySuccessMessage("该指南不存在");
        }
        if (!CommonUtil.isEmpty(name)) {
            renovationManual.setName(name);
        }
        if (!CommonUtil.isEmpty(workerTypeId)) {
            renovationManual.setWorkerTypeId(workerTypeId);
        }
        if (!CommonUtil.isEmpty(urlName)) {
            renovationManual.setUrlName(urlName);
        }
        if (!CommonUtil.isEmpty(test)) {
            renovationManual.setTest(test);
        }
        if (!CommonUtil.isEmpty(url)) {
            renovationManual.setUrl(url);
        }
        if (!CommonUtil.isEmpty(types)) {
            renovationManual.setTypes(types);
        }
        if (!CommonUtil.isEmpty(state)) {
            renovationManual.setState(state);
        }
        if (!CommonUtil.isEmpty(orderNumber)) {
            renovationManual.setOrderNumber(orderNumber);
        }
        if (!CommonUtil.isEmpty(image)) {
            renovationManual.setImage(image);
        }
        renovationManual.setModifyDate(new Date());
        renovationManualMapper.updateByPrimaryKeySelective(renovationManual);
        return ServerResponse.createBySuccessMessage("修改装修指南成功");
    }

    /**
     * 删除装修指南
     *
     * @return
     */
    public ServerResponse deleteRenovationManual(String id) {
        RenovationManual renovationManual = renovationManualMapper.selectByPrimaryKey(id);
        if (renovationManual == null) {
            return ServerResponse.createBySuccessMessage("该指南不存在");
        }
        renovationManual.setDataStatus(1);
        renovationManual.setModifyDate(new Date());
        renovationManualMapper.updateByPrimaryKeySelective(renovationManual);
        return ServerResponse.createBySuccessMessage("删除装修指南成功");
    }

    /**
     * 根据id查询装修指南对象
     *
     * @return
     */
    public ServerResponse getRenovationManualById(String id) {
        RenovationManual renovationManual = renovationManualMapper.selectByPrimaryKey(id);
        if (renovationManual == null) {
            return ServerResponse.createBySuccessMessage("该指南不存在");
        }
        String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
        Map<String, Object> map = BeanUtils.beanToMap(renovationManual);
        String imageUrl = renovationManual.getImage();
        map.put("imageUrl", CommonUtil.isEmpty(imageUrl) ? null : (imageAddress + imageUrl));
        RenovationStage renovationStage = renovationStageMapper.selectByPrimaryKey(renovationManual.getWorkerTypeId());
        if (renovationStage != null) {
            map.put("workerTypeName", renovationStage.getName());
        }
        return ServerResponse.createBySuccess("查询成功", map);
    }

}
