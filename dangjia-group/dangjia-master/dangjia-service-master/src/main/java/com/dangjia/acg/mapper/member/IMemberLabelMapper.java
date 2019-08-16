package com.dangjia.acg.mapper.member;

import com.dangjia.acg.dto.member.CustomerRecordInFoDTO;
import com.dangjia.acg.dto.member.SaleMemberLabelDTO;
import com.dangjia.acg.dto.member.WorkerTypeDTO;
import com.dangjia.acg.modle.member.MemberLabel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 标签表dao层
 *
 * @Description: TODO
 * @author: ysl
 * @date: 2019-1-5下午4:22:23
 */
@Repository
public interface IMemberLabelMapper extends Mapper<MemberLabel> {

    /**
     * c查找所有的单位
     */
    List<MemberLabel> getLabel();

    /**
     * 查找所有的父标签 ，按 parent_id 进行分组
     */
    List<MemberLabel> getAllParentLabel();

    /**
     * 指定父标签id，查找所有的子标签
     */
    List<MemberLabel> getChildLabelByParentId(@Param("parentId")String parentId);

    /**
     * 根据拿到的name拿到标签对象
     */
    List<MemberLabel> getLabelByName(@Param("name") String name);

    /**
     * 根据拿到的父name拿到标签对象
     */
    List<MemberLabel> getLabelByParentName(@Param("parentName") String parentName);

    /**
     * 根据id查询标签
     * @param labelIds
     * @return
     */
    List<SaleMemberLabelDTO> getLabelByIds(@Param("labelIds") String[] labelIds );


    /**
     * 查询客户沟通记录
     * @param memberId
     * @return
     */
    List<CustomerRecordInFoDTO> queryDescribes(@Param("memberId") String memberId);


    /**
     * 查询线索阶段沟通记录
     * @param clueId
     * @return
     */
    List<CustomerRecordInFoDTO> queryTalkContent(@Param("clueId") String clueId);


    /**
     * 查询大管家信息
     * @param houseId
     * @return
     */
    WorkerTypeDTO queryWorkerType(@Param("houseId") String houseId);


    String queryAddressName(@Param("houseId") String houseId);

}

