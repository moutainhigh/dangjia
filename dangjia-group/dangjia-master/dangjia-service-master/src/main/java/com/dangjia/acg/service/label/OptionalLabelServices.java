package com.dangjia.acg.service.label;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.label.ChildChildTags;
import com.dangjia.acg.dto.label.ChildTags;
import com.dangjia.acg.dto.label.OptionalLaelDetail;
import com.dangjia.acg.mapper.label.OptionalLabelMapper;
import com.dangjia.acg.modle.label.OptionalLabel;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
            if (StringUtils.isNotBlank(jsonStr)) {
                JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                Example example = new Example(OptionalLabel.class);
                example.createCriteria().andEqualTo(OptionalLabel.LABEL_NAME, jsonObject.getString("topTitle"))
                        .andEqualTo(OptionalLabel.DATA_STATUS, 0);
                if (optionalLabelMapper.selectByExample(example).size() > 0) {
                    return ServerResponse.createByErrorMessage("标题已存在");
                }
                JSONArray labels = jsonObject.getJSONArray("labels");
                List<String> stringList = new ArrayList<>();
                JSONArray tagNames2 = new JSONArray();
                for (Object label : labels) {
                    JSONObject obj = (JSONObject) label;
                    example = new Example(OptionalLabel.class);
                    example.createCriteria().andEqualTo(OptionalLabel.LABEL_NAME, obj.getString("subTitle"))
                            .andEqualTo(OptionalLabel.DATA_STATUS, 0);
                    if (optionalLabelMapper.selectByExample(example).size() > 0) {
                        return ServerResponse.createByErrorMessage("标签标题已存在");
                    }
                    JSONArray tagNames = obj.getJSONArray("tagName");
                    for (Object tagName : tagNames) {
                        example = new Example(OptionalLabel.class);
                        example.createCriteria().andEqualTo(OptionalLabel.LABEL_NAME, tagName)
                                .andEqualTo(OptionalLabel.DATA_STATUS, 0);
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
                //判断标签名称集合是否有重复元素
                long count1 = tagNames2.stream().distinct().count();
                if (count1 < tagNames2.size()) {
                    return ServerResponse.createByErrorMessage("标签名称重复");
                }
                OptionalLabel optionalLabel = new OptionalLabel();
                optionalLabel.setLabelName(jsonObject.getString("topTitle"));
                optionalLabelMapper.insert(optionalLabel);//顶级标签
                labels.forEach(label -> {
                    JSONObject obj = (JSONObject) label;
                    OptionalLabel optionalLabel1 = new OptionalLabel();
                    optionalLabel1.setParentId(optionalLabel.getId());
                    optionalLabel1.setLabelName(obj.getString("subTitle"));
                    optionalLabelMapper.insert(optionalLabel1);//二级标签
                    JSONArray tagNames1 = obj.getJSONArray("tagName");
                    tagNames1.forEach(tagName -> {
                        OptionalLabel optionalLabel2 = new OptionalLabel();
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
     * @return
     */
    public ServerResponse queryOptionalLabel(PageDTO pageDTO) {
        try {
            Example example = new Example(OptionalLabel.class);
            example.createCriteria().andCondition(" DATA_STATUS =0").andCondition("parent_id is null");
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<OptionalLabel> optionalLabels = optionalLabelMapper.selectByExample(example);
            PageInfo pageResult = new PageInfo(optionalLabels);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }


    /**
     * 查询标签详情
     *
     * @param id
     * @return
     */
    public ServerResponse queryOptionalLabelById(String id) {
        try {
            OptionalLaelDetail optionalLaelDetail = new OptionalLaelDetail();
            OptionalLabel optionalLabel = optionalLabelMapper.selectByPrimaryKey(id);
            optionalLaelDetail.setId(id);
            optionalLaelDetail.setTopTitle(optionalLabel.getLabelName());
            Example example = new Example(OptionalLabel.class);
            example.createCriteria().andEqualTo(OptionalLabel.PARENT_ID, id)
                    .andEqualTo(OptionalLabel.DATA_STATUS, 0);
            List<OptionalLabel> optionalLabels = optionalLabelMapper.selectByExample(example);
            List<ChildTags> childTagsList = new ArrayList<>();
            optionalLabels.forEach(optionalLabel1 -> {
                ChildTags childTags = new ChildTags();
                childTags.setId(optionalLabel1.getId());
                childTags.setSubTitle(optionalLabel1.getLabelName());
                childTags.setParentId(optionalLabel1.getParentId());
                Example example1 = new Example(OptionalLabel.class);
                example1.createCriteria().andEqualTo(OptionalLabel.PARENT_ID, optionalLabel1.getId())
                        .andEqualTo(OptionalLabel.DATA_STATUS, 0);
                List<OptionalLabel> optionalLabels1 = optionalLabelMapper.selectByExample(example1);
                List<ChildChildTags> childChildTagsList = new ArrayList<>();
                optionalLabels1.forEach(optionalLabel2 -> {
                    ChildChildTags childChildTags = new ChildChildTags();
                    childChildTags.setId(optionalLabel2.getId());
                    childChildTags.setTagName(optionalLabel2.getLabelName());
                    childChildTags.setParentId(optionalLabel2.getParentId());
                    childChildTagsList.add(childChildTags);
                });
                childTags.setTagName(childChildTagsList);
                childTagsList.add(childTags);
            });
            optionalLaelDetail.setLabels(childTagsList);
            return ServerResponse.createBySuccess("查询成功", optionalLaelDetail);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
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
     * @param jsonStr
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse editOptionalLabel(String jsonStr) throws Exception{
        if (StringUtils.isNotBlank(jsonStr)) {
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            OptionalLabel oldOptionalLabel = optionalLabelMapper.selectByPrimaryKey(jsonObject.getString("id"));
            if (!oldOptionalLabel.getLabelName().equals(jsonObject.getString("topTitle"))) {
                Example example = new Example(OptionalLabel.class);
                example.createCriteria().andEqualTo(OptionalLabel.LABEL_NAME, jsonObject.getString("topTitle"))
                        .andEqualTo(OptionalLabel.DATA_STATUS, 0);
                if (optionalLabelMapper.selectByExample(example).size() > 0) {
                    return ServerResponse.createByErrorMessage("标题已存在");
                }
                oldOptionalLabel.setLabelName(jsonObject.getString("topTitle"));
                optionalLabelMapper.updateByPrimaryKeySelective(oldOptionalLabel);
            }
            String labels = jsonObject.getString("labels");
            JSONArray jsonArray = JSONArray.parseArray(labels);
            for (Object o : jsonArray) {
                JSONObject obj = (JSONObject) o;
                String optionalLabelId = obj.getString("id");//标签id
                String parentId = obj.getString("parentId");//父id
                String tagName = obj.getString("tagName");//标签名称
                //没有id则新增
                if (StringUtils.isBlank(optionalLabelId)) {
                    Example example = new Example(OptionalLabel.class);
                    example.createCriteria().andEqualTo(OptionalLabel.LABEL_NAME, tagName)
                            .andEqualTo(OptionalLabel.DATA_STATUS, 0);
                    if (optionalLabelMapper.selectByExample(example).size() > 0) {
                        throw new Exception("标题已存在");
                    }
                    OptionalLabel optionalLabel = new OptionalLabel();
                    optionalLabel.setParentId(parentId);
                    optionalLabel.setLabelName(tagName);
                    optionalLabelMapper.insert(optionalLabel);
                } else {
                    oldOptionalLabel = optionalLabelMapper.selectByPrimaryKey(optionalLabelId);
                    if (!oldOptionalLabel.getLabelName().equals(tagName)) {
                        Example example = new Example(OptionalLabel.class);
                        example.createCriteria().andEqualTo(OptionalLabel.LABEL_NAME, tagName)
                                .andEqualTo(OptionalLabel.DATA_STATUS, 0);
                        if (optionalLabelMapper.selectByExample(example).size() > 0) {
                            throw new Exception("标题已存在");
                        }
                        oldOptionalLabel.setLabelName(tagName);
                        optionalLabelMapper.updateByPrimaryKeySelective(oldOptionalLabel);
                    }
                }
            }
            //要删除的标签id数组，逗号分隔
            String[] deleteLabels = jsonObject.getString("deleteLabels").split(",");
            for (String deleteLabel : deleteLabels) {
                Example example=new Example(OptionalLabel.class);
                example.createCriteria().andCondition("(id ="+ deleteLabel +" or parent_id ="+ deleteLabel +")");
                int i = optionalLabelMapper.deleteByExample(example);
                if(i>0){
                    return ServerResponse.createBySuccessMessage("操作成功");
                }else{
                    throw new Exception("操作失败");
                }
            }
        }
        return ServerResponse.createBySuccessMessage("编辑成功");
    }
}
