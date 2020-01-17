package com.dangjia.acg.service.label;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.label.OptionalLabelMapper;
import com.dangjia.acg.modle.label.OptionalLabel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;


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
     * 添加标签
     *
     * @param jsonStr
     * @return
     */
    public ServerResponse addOptionalLabel(String jsonStr) {
        try {
            if(StringUtils.isNotBlank(jsonStr)) {
                JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                Example example = new Example(OptionalLabel.class);
                example.createCriteria().andEqualTo(OptionalLabel.LABEL_NAME, jsonObject.getString("topTitle"))
                        .andEqualTo(OptionalLabel.DATA_STATUS,0);
                if (optionalLabelMapper.selectByExample(example).size() > 0) {
                    return ServerResponse.createByErrorMessage("标题已存在");
                }
                JSONArray labels = jsonObject.getJSONArray("labels");
                List<String> stringList=new ArrayList<>();
                JSONArray tagNames2=new JSONArray();
                for (Object label : labels) {
                    JSONObject obj = (JSONObject) label;
                    example = new Example(OptionalLabel.class);
                    example.createCriteria().andEqualTo(OptionalLabel.LABEL_NAME, obj.getString("subTitle"))
                            .andEqualTo(OptionalLabel.DATA_STATUS,0);
                    if (optionalLabelMapper.selectByExample(example).size() > 0) {
                        return ServerResponse.createByErrorMessage("标签标题已存在");
                    }
                    JSONArray tagNames = obj.getJSONArray("tagName");
                    for (Object tagName : tagNames) {
                        example = new Example(OptionalLabel.class);
                        example.createCriteria().andEqualTo(OptionalLabel.LABEL_NAME, tagName)
                                .andEqualTo(OptionalLabel.DATA_STATUS,0);
                        if (optionalLabelMapper.selectByExample(example).size() > 0) {
                            return ServerResponse.createByErrorMessage("标签名称已存在");
                        }
                    }
                    tagNames2.addAll(obj.getJSONArray("tagName"));
                    stringList.add(obj.getString("subTitle"));
                }
                //判断标签二级标题集合是否有重复元素
                long count = stringList.stream().distinct().count();
                if (count < stringList.size()) {
                    return ServerResponse.createByErrorMessage("标签标题重复");
                }
                //断标签名称集合是否有重复元素
                long count1 = tagNames2.stream().distinct().count();
                if (count1 < tagNames2.size()) {
                    return ServerResponse.createByErrorMessage("标签名称重复");
                }
                OptionalLabel optionalLabel=new OptionalLabel();
                optionalLabel.setLabelName(jsonObject.getString("topTitle"));
                optionalLabelMapper.insert(optionalLabel);//顶级标签
                labels.forEach(label ->{
                    JSONObject obj = (JSONObject) label;
                    OptionalLabel optionalLabel1=new OptionalLabel();
                    optionalLabel1.setParentId(optionalLabel.getId());
                    optionalLabel1.setLabelName(obj.getString("subTitle"));
                    optionalLabelMapper.insert(optionalLabel1);//二级标签
                    JSONArray tagNames1 = obj.getJSONArray("tagName");
                    tagNames1.forEach(tagName ->{
                        OptionalLabel optionalLabel2=new OptionalLabel();
                        optionalLabel2.setParentId(optionalLabel1.getId());
                        optionalLabel2.setLabelName(tagName.toString());
                        optionalLabelMapper.insert(optionalLabel2);
                    });
                });
            }
            return ServerResponse.createBySuccessMessage("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("添加失败");
        }
    }

    /**
     * 查询标签
     *
     * @param id
     * @return
     */
    public ServerResponse queryOptionalLabel(String id) {
        if (!CommonUtil.isEmpty(id)) {
            return ServerResponse.createBySuccess("查询成功", optionalLabelMapper.selectByPrimaryKey(id));
        } else {
            Example example = new Example(OptionalLabel.class);
            example.createCriteria().andCondition(" DATA_STATUS !=0");
            return ServerResponse.createBySuccess("查询成功", optionalLabelMapper.selectAll());
        }
    }

    /**
     * 删除标签
     *
     * @param id
     * @return
     */
    public ServerResponse delOptionalLabel(String id) {
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
     *
     * @param optionalLabel
     * @return
     */
    public ServerResponse editOptionalLabel(OptionalLabel optionalLabel) {
        try {
            OptionalLabel oldOptionalLabel = optionalLabelMapper.selectByPrimaryKey(optionalLabel.getId());
            if(!oldOptionalLabel.getLabelName().equals(optionalLabel.getLabelName())){
                Example example = new Example(OptionalLabel.class);
                example.createCriteria().andEqualTo(OptionalLabel.LABEL_NAME, optionalLabel.getLabelName())
                        .andEqualTo(OptionalLabel.DATA_STATUS,0);
                if (optionalLabelMapper.selectByExample(example).size() > 0) {
                    return ServerResponse.createByErrorMessage("标签已存在");
                }
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
