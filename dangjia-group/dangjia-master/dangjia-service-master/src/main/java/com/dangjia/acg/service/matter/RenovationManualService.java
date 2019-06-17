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
    public ServerResponse addRenovationManual(RenovationManual renovationManual) {
        try {
            renovationManual.setId(new RenovationManual().getId());
            renovationManual.setState(0);
            renovationManualMapper.insertSelective(renovationManual);
            return ServerResponse.createBySuccessMessage("新增装修指南成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("新增装修指南失败");
        }
    }

    /**
     * 修改装修指南
     *
     * @return
     */
    public ServerResponse updateRenovationManual(RenovationManual renovationManual) {
        try {
            if (!CommonUtil.isEmpty(renovationManual.getId())) {
                renovationManualMapper.updateByPrimaryKeySelective(renovationManual);
                return ServerResponse.createBySuccessMessage("修改装修指南成功");
            }else{
                return ServerResponse.createByErrorMessage("未找到");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("修改装修指南失败");
        }
    }

    /**
     * 删除装修指南
     *
     * @return
     */
    public ServerResponse deleteRenovationManual(String id) {
        try {
            RenovationManual renovationManual = new RenovationManual();
            renovationManual.setId(id);
            renovationManual.setState(1);
            renovationManualMapper.deleteByPrimaryKey(renovationManual);
            return ServerResponse.createBySuccessMessage("删除装修指南成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("删除装修指南失败");
        }
    }

    /**
     * 根据id查询装修指南对象
     *
     * @return
     */
    public ServerResponse getRenovationManualById(String id) {
        try {
            return ServerResponse.createBySuccess("根据id查询装修指南对象成功", renovationManualMapper.selectByPrimaryKey(id));
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("根据id查询装修指南对象失败");
        }
    }

}
