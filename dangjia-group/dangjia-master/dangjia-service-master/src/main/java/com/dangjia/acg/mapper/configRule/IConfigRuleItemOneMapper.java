package com.dangjia.acg.mapper.configRule;

import com.dangjia.acg.model.config.DjConfigRuleItemOne;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

@Repository
public interface IConfigRuleItemOneMapper extends Mapper<DjConfigRuleItemOne> {

    List<DjConfigRuleItemOne> getRuleItemOneData(@Param("moduleId") String moduleId,
                                                 @Param("typeId") String typeId,
                                                 @Param("batchCode") String batchCode,
                                                 @Param("fieldName") String fieldName,
                                                 @Param("fieldKey") String fieldKey,
                                                 @Param("limit") Integer limit);

    List<DjConfigRuleItemOne> getRuleItemOneWorkerData(@Param("moduleId") String moduleId,
                                                 @Param("batchCode") String batchCode,
                                                 @Param("fieldName") String fieldName,
                                                 @Param("fieldKey") String fieldKey,
                                                 @Param("limit") Integer limit);



}
