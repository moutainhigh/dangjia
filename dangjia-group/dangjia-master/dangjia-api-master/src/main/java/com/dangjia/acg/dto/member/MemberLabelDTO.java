package com.dangjia.acg.dto.member;

import com.dangjia.acg.modle.member.MemberLabel;
import lombok.Data;

import java.util.List;

/**
 * author: ysl
 * Date: 2019/1/8
 * Time: 10:00
 * 标签对象
 */
@Data
public class MemberLabelDTO {
    private String parentId;
    private String parentName;
    private List<MemberLabel> childMemberLabelList;//多个子标签对象
}
