package com.dangjia.acg.service.label;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.label.OptionalLabelMapper;
import com.dangjia.acg.modle.label.OptionalLabel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;


/**
 * Created with IntelliJ IDEA.
 * author: wk
 * Date: 2019/6/18
 * Time: 9:55
 */
@Service
public class OptionalLabelServices {
    @Autowired
    private OptionalLabelMapper optionalLabelMapper;

    /**
     * 添加标题
     * @param optionalLabel
     * @return
     */
    public ServerResponse addOptionalLabel(OptionalLabel optionalLabel){
        try {
            Example example=new Example(OptionalLabel.class);
            example.createCriteria().andEqualTo(OptionalLabel.LABEL_NAME,optionalLabel.getLabelName());
            if(optionalLabelMapper.selectByExample(example).size()>0) {
                return ServerResponse.createByErrorMessage("标签已存在");
            }
            optionalLabelMapper.insert(optionalLabel);
            return ServerResponse.createBySuccessMessage("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("添加失败");
        }
    }

    /**
     * 查询标签
     * @param id
     * @return
     */
    public ServerResponse queryOptionalLabel(String id){
        if(null!=id&&""!=id){
            return ServerResponse.createBySuccess("查询成功",optionalLabelMapper.selectByPrimaryKey(id));
        }else{
            Example example=new Example(OptionalLabel.class);
            example.createCriteria().andCondition(" DATA_STATUS !=0");
            return ServerResponse.createBySuccess("查询成功",optionalLabelMapper.selectAll());
        }
    }

    /**
     * 删除标签
     * @param id
     * @return
     */
    public ServerResponse delOptionalLabel(String id){
        try {
            OptionalLabel optionalLabel=new OptionalLabel();
            optionalLabel.setId(id);
            optionalLabel.setDataStatus(1);
            optionalLabelMapper.updateByPrimaryKeySelective(optionalLabel);
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("删除失败");
        }
    }

    /**
     * 编辑标签
     * @param optionalLabel
     * @return
     */
    public ServerResponse editOptionalLabel(OptionalLabel optionalLabel){
        try {
            Example example=new Example(OptionalLabel.class);
            example.createCriteria().andEqualTo(OptionalLabel.LABEL_NAME,optionalLabel.getLabelName());
            if(optionalLabelMapper.selectByExample(example).size()>0) {
                return ServerResponse.createByErrorMessage("标签已存在");
            }
            optionalLabel.setCreateDate(null);
            optionalLabelMapper.updateByPrimaryKeySelective(optionalLabel);
            return ServerResponse.createBySuccessMessage("编辑成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("编辑失败");
        }
    }
}
