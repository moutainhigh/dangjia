package com.dangjia.acg.service.product;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.product.DjBasicsLabelDTO;
import com.dangjia.acg.mapper.product.DjBasicsLabelMapper;
import com.dangjia.acg.mapper.product.DjBasicsLabelValueMapper;
import com.dangjia.acg.modle.product.DjBasicsLabel;
import com.dangjia.acg.modle.product.DjBasicsLabelValue;
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
     * @param labelName
     * @param labelValue
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse addCommodityLabels(String labelName, String labelValue) {
        Example example=new Example(DjBasicsLabel.class);
        example.createCriteria().andEqualTo(DjBasicsLabel.NAME,labelName)
                .andEqualTo(DjBasicsLabel.DATA_STATUS,0);
        if(djBasicsLabelMapper.selectByExample(example).size()>0)
            return ServerResponse.createByErrorMessage("该标签名称已存在");
        List<String> strings = Arrays.asList(labelValue.split(","));
        //判断集合是否有重复元素
        long count = strings.stream().distinct().count();
        if(count<strings.size())
            return ServerResponse.createByErrorMessage("标签值重复");
        DjBasicsLabel djBasicsLabel=new DjBasicsLabel();
        djBasicsLabel.setName(labelName);
        djBasicsLabel.setDataStatus(0);
        djBasicsLabelMapper.insert(djBasicsLabel);
        strings.forEach(str ->{
            DjBasicsLabelValue djBasicsLabelValue=new DjBasicsLabelValue();
            djBasicsLabelValue.setLabelId(djBasicsLabel.getId());
            djBasicsLabelValue.setName(str);
            djBasicsLabelValue.setDataStatus(0);
            djBasicsLabelValueMapper.insert(djBasicsLabelValue);
        });
        return ServerResponse.createBySuccessMessage("添加成功");
    }


    /**
     * 编辑商品标签
     * @param id
     * @param labelName
     * @param labelValue
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse updateCommodityLabels(String id, String labelName, String labelValue) {
        DjBasicsLabel djBasicsLabel = djBasicsLabelMapper.selectByPrimaryKey(id);
        if(!djBasicsLabel.getName().equals(labelName)){
            Example example=new Example(DjBasicsLabel.class);
            example.createCriteria().andEqualTo(DjBasicsLabel.NAME,labelName)
                    .andEqualTo(DjBasicsLabel.DATA_STATUS,0);
            if(djBasicsLabelMapper.selectByExample(example).size()>0)
                return ServerResponse.createByErrorMessage("该标签名称已存在");
            djBasicsLabel.setName(labelName);
            djBasicsLabelMapper.updateByPrimaryKeySelective(djBasicsLabel);
        }
        List<String> strings = Arrays.asList(labelValue.split(","));
        //判断集合是否有重复元素
        long count = strings.stream().distinct().count();
        if(count<strings.size())
            return ServerResponse.createByErrorMessage("标签值重复");
        Example example=new Example(DjBasicsLabelValue.class);
        example.createCriteria().andEqualTo(DjBasicsLabelValue.LABEL_ID,id);
        djBasicsLabelValueMapper.deleteByExample(example);
        strings.forEach(str ->{
            DjBasicsLabelValue djBasicsLabelValue=new DjBasicsLabelValue();
            djBasicsLabelValue.setLabelId(djBasicsLabel.getId());
            djBasicsLabelValue.setName(str);
            djBasicsLabelValue.setDataStatus(0);
            djBasicsLabelValueMapper.insert(djBasicsLabelValue);
        });
        return ServerResponse.createBySuccessMessage("编辑成功");
    }



    /**
     * 删除商品标签
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse delCommodityLabels(String id) {
        djBasicsLabelMapper.deleteByPrimaryKey(id);
        Example example=new Example(DjBasicsLabelValue.class);
        example.createCriteria().andEqualTo(DjBasicsLabelValue.LABEL_ID,id);
        djBasicsLabelValueMapper.deleteByExample(example);
        return ServerResponse.createBySuccessMessage("删除成功");
    }


    /**
     * 根据id查询商品标签
     * @param id
     * @return
     */
    public ServerResponse queryCommodityLabelsById(String id) {
        DjBasicsLabel djBasicsLabel = djBasicsLabelMapper.selectByPrimaryKey(id);
        DjBasicsLabelDTO djBasicsLabelDTO=new DjBasicsLabelDTO();
        djBasicsLabelDTO.setId(djBasicsLabel.getId());
        djBasicsLabelDTO.setName(djBasicsLabel.getName());
        Example example=new Example(DjBasicsLabelValue.class);
        example.createCriteria().andEqualTo(DjBasicsLabelValue.LABEL_ID,id)
                .andEqualTo(DjBasicsLabelValue.DATA_STATUS,0);
        djBasicsLabelDTO.setLabelValueList(djBasicsLabelValueMapper.selectByExample(example));
        return ServerResponse.createBySuccess("查询成功",djBasicsLabelDTO);
    }


    /**
     * 查询商品标签
     * @return
     */
    public ServerResponse queryCommodityLabels() {
        List<DjBasicsLabel> djBasicsLabels = djBasicsLabelMapper.selectAll();
        List<DjBasicsLabelDTO> labelDTOS=new ArrayList<>();
        for (DjBasicsLabel djBasicsLabel : djBasicsLabels) {
            DjBasicsLabelDTO djBasicsLabelDTO=new DjBasicsLabelDTO();
            djBasicsLabelDTO.setId(djBasicsLabel.getId());
            djBasicsLabelDTO.setName(djBasicsLabel.getName());
            Example example=new Example(DjBasicsLabelValue.class);
            example.createCriteria().andEqualTo(DjBasicsLabelValue.LABEL_ID,djBasicsLabel.getId())
                    .andEqualTo(DjBasicsLabelValue.DATA_STATUS,0);
            djBasicsLabelDTO.setLabelValueList(djBasicsLabelValueMapper.selectByExample(example));
            labelDTOS.add(djBasicsLabelDTO);
        }
        return ServerResponse.createBySuccess("查询成功",labelDTOS);
    }

}
