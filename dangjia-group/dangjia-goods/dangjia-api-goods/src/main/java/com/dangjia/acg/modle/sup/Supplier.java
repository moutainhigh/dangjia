package com.dangjia.acg.modle.sup;

import com.dangjia.acg.common.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @类 名： Supplier
 * @功能描述： 供应商实体类
 * @作者信息： zmj
 * @创建时间： 2018-9-17上午10:51:14
 */
@Data
@Entity
@Table(name = "dj_sup_supplier")
@ApiModel(description = "供应商")
@FieldNameConstants(prefix = "")
public class Supplier extends BaseEntity {

    @Column(name = "name")
    private String name;//供应商名称

    @Column(name = "address")
    private String address;//地址

    @Column(name = "telephone")
    private String telephone;//联系电话

    @Column(name = "checkPeople")
    private String checkPeople;//联系人姓名

    @Column(name = "gender")
    private Integer gender;//联系人性别  1男 2女   0 未选

    @Column(name = "email")
    private String email;//电子邮件

    @Column(name = "notice")
    private String notice;//发货须知

    @Column(name = "supplier_level")
    private Integer supplierLevel;//供应商级别

    @Column(name = "state")
    private Integer state;//供应商状态  1正常供货 2停止供货

}
