package com.dangjia.acg.mapper.member;

import com.dangjia.acg.modle.member.Member;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 用户表dao层
 * @Description: TODO
 * @author: qyx
 * @date: 2018-9-19下午4:22:23
 */
@Repository
public interface IMemberMapper extends Mapper<Member> {

    List<Member> artisanList(@Param("name") String name,@Param("workerTypeId")String workerTypeId);

    /**通过评价表的houseId获得大管家*/
    Member getSupervisor(@Param("houseId") String houseId);

    Member getUser(Member member);

    /**
     * 按照条件查询
     * @param searchKey
     * @param stage
     * @param childsLabelIdArr
     * @return
     */
    List<Member> getMemberListByName(@Param("searchKey") String searchKey,
                                      @Param("stage") Integer stage,
                                      @Param("childsLabelIdArr")String[] childsLabelIdArr,@Param("orderBy") String orderBy);

    List<Map<String,Object>> getMemberList();
//    //查询所有业主关联客服的所有数据
//    List<Map<String,Object>> getMemberAndCustomerList();


}

