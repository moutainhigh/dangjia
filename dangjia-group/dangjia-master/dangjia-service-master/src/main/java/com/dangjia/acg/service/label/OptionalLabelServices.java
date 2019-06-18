package com.dangjia.acg.service.label;

import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.label.OptionalLabelMapper;
import com.dangjia.acg.modle.label.OptionalLabel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
        if(null!=id||""!=id){
            return ServerResponse.createBySuccess("查询成功",optionalLabelMapper.selectAll());
        }else{
            return ServerResponse.createBySuccess("查询成功",optionalLabelMapper.selectByPrimaryKey(id));
        }
    }

    /**
     * 删除标签
     * @param id
     * @return
     */
    public ServerResponse delOptionalLabel(String id){
        try {
            optionalLabelMapper.deleteByPrimaryKey(id);
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
            optionalLabel.setCreateDate(null);
            optionalLabelMapper.updateByPrimaryKeySelective(optionalLabel);
            return ServerResponse.createBySuccessMessage("编辑成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("编辑失败");
        }
    }
}
