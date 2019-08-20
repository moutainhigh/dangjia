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

    List<Member> artisanList(@Param("cityId") String cityId,@Param("name") String name,@Param("workerTypeId")String workerTypeId ,@Param("type") String type,@Param("checkType")String checkType);

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
    List<Member> getMemberListByName(@Param("cityId") String cityId,@Param("searchKey") String searchKey,
                                      @Param("stage") Integer stage, @Param("userRole") String userRole,
                                      @Param("childsLabelIdArr")String[] childsLabelIdArr,@Param("orderBy") String orderBy,
                                     @Param("type") String type,
                                     @Param("userKey") String userKey,
                                     @Param("userId") String userId,
                                     @Param("beginDate") String beginDate,
                                     @Param("endDate") String endDate);

    List<Map<String,Object>> getMemberList();
//    //查询所有业主关联客服的所有数据
//    List<Map<String,Object>> getMemberAndCustomerList();

    Member getByPhone(@Param("mobile") String mobile);



}

