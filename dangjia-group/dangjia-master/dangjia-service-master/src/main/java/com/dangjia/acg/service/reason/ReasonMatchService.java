package com.dangjia.acg.service.reason;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;

import com.dangjia.acg.mapper.reason.ReasonMatchMapper;
import com.dangjia.acg.modle.feedback.UserFeedback;
import com.dangjia.acg.modle.feedback.UserFeedbackItem;

import com.dangjia.acg.modle.reason.ReasonMatchSurface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;


/**
 * ljl
 * 工匠更换原因逻辑层
 */
@Service
public class ReasonMatchService {

    @Autowired
    private ReasonMatchMapper reasonMatchMapper;


    /**
     * 新增工匠更换原因
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse addReasonInFo(String remark) {
        try {

            if (CommonUtil.isEmpty(remark)) {
                return ServerResponse.createByErrorMessage("remark不能为空");
            }

            //新增反馈详情
            ReasonMatchSurface reasonMatchSurface = new ReasonMatchSurface();
            reasonMatchSurface.setRemark(remark);
            reasonMatchMapper.insert(reasonMatchSurface);
            return ServerResponse.createBySuccessMessage("新增成功");
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("新增失败");
        }
    }


    /**
     * 查询工匠更换原因
     * @return
     */
    public ServerResponse queryReasonInFo() {
        try {
            Example example = new Example(ReasonMatchSurface.class);
            example.orderBy(ReasonMatchSurface.CREATE_DATE);
            List<ReasonMatchSurface> reasonMatchSurface =  reasonMatchMapper.selectByExample(example);
            return ServerResponse.createBySuccess("查询成功", reasonMatchSurface);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 删除工匠更换原因
     * @param id
     * @return
     */
    public ServerResponse deleteReasonInFo(String id) {
        try {
            if (CommonUtil.isEmpty(id)) {
                return ServerResponse.createByErrorMessage("id不能为空");
            }
            reasonMatchMapper.deleteByPrimaryKey(id);
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("删除失败");
        }
    }

    /**
     * 修改工匠更换原因
     * @param id
     * @return
     */
    public ServerResponse upDateReasonInFo(String id,String remark) {
        try {
            if (CommonUtil.isEmpty(id)) {
                return ServerResponse.createByErrorMessage("id不能为空");
            }
            ReasonMatchSurface reasonMatchSurface = new ReasonMatchSurface();
            reasonMatchSurface.setRemark(remark);
            reasonMatchSurface.setCreateDate(null);
            reasonMatchSurface.setId(id);
            reasonMatchMapper.updateByPrimaryKeySelective(reasonMatchSurface);
            return ServerResponse.createBySuccessMessage("修改成功");
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("修改失败");
        }
    }
}
