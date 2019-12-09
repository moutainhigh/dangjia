package com.dangjia.acg.service.product;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dto.product.DjBasicsLabelDTO;
import com.dangjia.acg.mapper.product.DjBasicsLabelMapper;
import com.dangjia.acg.mapper.product.DjBasicsLabelValueMapper;
import com.dangjia.acg.modle.product.DjBasicsLabel;
import com.dangjia.acg.modle.product.DjBasicsLabelValue;
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
 * Date: 2019/7/25
 * Time: 13:56
 */
@Service
public class DjBasicsLabelService {
    @Autowired
    private DjBasicsLabelMapper djBasicsLabelMapper;
    @Autowired
    private DjBasicsLabelValueMapper djBasicsLabelValueMapper;

    /**
     * 添加商品标签
     *
     * @param labelName
     * @param labelValue
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse addCommodityLabels(String labelName, String labelValue,String cityId) {
        try {
            if(CommonUtil.isEmpty(labelName)) {
                return ServerResponse.createByErrorMessage("标签名不能为空");
            }
            if(CommonUtil.isEmpty(labelValue)) {
                return ServerResponse.createByErrorMessage("标签值不能为空");
            }
            Example example = new Example(DjBasicsLabel.class);
            example.createCriteria().andEqualTo(DjBasicsLabel.NAME, labelName)
                    .andEqualTo(DjBasicsLabel.DATA_STATUS, 0).andEqualTo(DjBasicsLabel.CITY_ID,cityId);
            if (djBasicsLabelMapper.selectByExample(example).size() > 0) {
                return ServerResponse.createByErrorMessage("该标签名称已存在");
            }
            List<String> strings = Arrays.asList(labelValue.split(","));
            //判断集合是否有重复元素
            long count = strings.stream().distinct().count();
            if (count < strings.size()) {
                return ServerResponse.createByErrorMessage("标签值重复");
            }
            example=new Example(DjBasicsLabelValue.class);
            example.createCriteria().andIn(DjBasicsLabelValue.NAME,strings)
                    .andEqualTo(DjBasicsLabelValue.DATA_STATUS,0);
            List<DjBasicsLabelValue> djBasicsLabelValues = djBasicsLabelValueMapper.selectByExample(example);
            if(djBasicsLabelValues.size()>0) {
                return ServerResponse.createByErrorMessage("标签值已存在");
            }
            DjBasicsLabel djBasicsLabel = new DjBasicsLabel();
            djBasicsLabel.setCityId(cityId);
            djBasicsLabel.setName(labelName);
            djBasicsLabel.setDataStatus(0);
            djBasicsLabelMapper.insert(djBasicsLabel);
            strings.forEach(str->{
                DjBasicsLabelValue djBasicsLabelValue = new DjBasicsLabelValue();
                djBasicsLabelValue.setLabelId(djBasicsLabel.getId());
                djBasicsLabelValue.setName(str);
                djBasicsLabelValue.setDataStatus(0);
                djBasicsLabelValue.setCityId(cityId);
                djBasicsLabelValueMapper.insert(djBasicsLabelValue);
            });
            return ServerResponse.createBySuccessMessage("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("添加失败");
        }
    }


    /**
     * 编辑商品标签
     *
     * @param id
     * @param labelName
     * @param labelValue
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse updateCommodityLabels(String id, String labelName, String labelValue,String cityId) {
        try {
            if(CommonUtil.isEmpty(labelName)) {
                return ServerResponse.createByErrorMessage("标签名不能为空");
            }
            if(CommonUtil.isEmpty(labelValue)) {
                return ServerResponse.createByErrorMessage("标签值不能为空");
            }
            DjBasicsLabel djBasicsLabel = djBasicsLabelMapper.selectByPrimaryKey(id);
            if (!djBasicsLabel.getName().equals(labelName)) {
                Example example = new Example(DjBasicsLabel.class);
                example.createCriteria().andEqualTo(DjBasicsLabel.NAME, labelName)
                        .andEqualTo(DjBasicsLabel.DATA_STATUS, 0).andEqualTo(DjBasicsLabel.CITY_ID,cityId);
                if (djBasicsLabelMapper.selectByExample(example).size() > 0) {
                    return ServerResponse.createByErrorMessage("该标签名称已存在");
                }
                djBasicsLabel.setName(labelName);
                djBasicsLabel.setCityId(cityId);
                djBasicsLabelMapper.updateByPrimaryKeySelective(djBasicsLabel);
            }
            List<String> strings = Arrays.asList(labelValue.split(","));
            //判断集合是否有重复元素
            long count = strings.stream().distinct().count();
            if (count < strings.size()) {
                return ServerResponse.createByErrorMessage("标签值重复");
            }
            Example example = new Example(DjBasicsLabelValue.class);
            example.createCriteria().andEqualTo(DjBasicsLabelValue.LABEL_ID, id);
            djBasicsLabelValueMapper.deleteByExample(example);
            example=new Example(DjBasicsLabelValue.class);
            example.createCriteria().andIn(DjBasicsLabelValue.NAME,strings)
                    .andEqualTo(DjBasicsLabelValue.DATA_STATUS,0);
            List<DjBasicsLabelValue> djBasicsLabelValues = djBasicsLabelValueMapper.selectByExample(example);
            if(djBasicsLabelValues.size()>0) {
                return ServerResponse.createByErrorMessage("标签值已存在");
            }
            strings.forEach(str->{
                DjBasicsLabelValue djBasicsLabelValue = new DjBasicsLabelValue();
                djBasicsLabelValue.setLabelId(djBasicsLabel.getId());
                djBasicsLabelValue.setName(str);
                djBasicsLabelValue.setDataStatus(0);
                djBasicsLabelValue.setCityId(cityId);
                djBasicsLabelValueMapper.insert(djBasicsLabelValue);
            });
            return ServerResponse.createBySuccessMessage("编辑成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("编辑失败");
        }
    }


    /**
     * 删除商品标签
     *
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse delCommodityLabels(String id) {
        try {
            djBasicsLabelMapper.deleteByPrimaryKey(id);
            Example example = new Example(DjBasicsLabelValue.class);
            example.createCriteria().andEqualTo(DjBasicsLabelValue.LABEL_ID, id);
            djBasicsLabelValueMapper.deleteByExample(example);
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("删除失败");
        }
    }


    /**
     * 根据标签id查询标签值
     *
     * @param labelId
     * @return
     */
    public ServerResponse queryCommodityLabelsById(String labelId) {
        try {
            DjBasicsLabel djBasicsLabel = djBasicsLabelMapper.selectByPrimaryKey(labelId);
            DjBasicsLabelDTO djBasicsLabelDTO = new DjBasicsLabelDTO();
            djBasicsLabelDTO.setId(djBasicsLabel.getId());
            djBasicsLabelDTO.setName(djBasicsLabel.getName());
            Example example = new Example(DjBasicsLabelValue.class);
            example.createCriteria().andEqualTo(DjBasicsLabelValue.LABEL_ID, labelId)
                    .andEqualTo(DjBasicsLabelValue.DATA_STATUS, 0);
            djBasicsLabelDTO.setLabelValueList(djBasicsLabelValueMapper.selectByExample(example));
            return ServerResponse.createBySuccess("查询成功", djBasicsLabelDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 查询商品标签
     *
     * @return
     */
    public ServerResponse queryCommodityLabels(PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DjBasicsLabel> djBasicsLabels = djBasicsLabelMapper.selectAll();
            PageInfo pageResult = new PageInfo(djBasicsLabels);
            List<DjBasicsLabelDTO> labelDTOS = new ArrayList<>();
            for (DjBasicsLabel djBasicsLabel : djBasicsLabels) {
                DjBasicsLabelDTO djBasicsLabelDTO = new DjBasicsLabelDTO();
                djBasicsLabelDTO.setId(djBasicsLabel.getId());
                djBasicsLabelDTO.setName(djBasicsLabel.getName());
                Example example = new Example(DjBasicsLabelValue.class);
                example.createCriteria().andEqualTo(DjBasicsLabelValue.LABEL_ID, djBasicsLabel.getId())
                        .andEqualTo(DjBasicsLabelValue.DATA_STATUS, 0);
                example.orderBy(DjBasicsLabelValue.DATA_STATUS).desc();
                djBasicsLabelDTO.setLabelValueList(djBasicsLabelValueMapper.selectByExample(example));
                labelDTOS.add(djBasicsLabelDTO);
            }
            if(labelDTOS.size()<=0)
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(),ServerCode.NO_DATA.getDesc());
           pageResult.setList(labelDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

}
