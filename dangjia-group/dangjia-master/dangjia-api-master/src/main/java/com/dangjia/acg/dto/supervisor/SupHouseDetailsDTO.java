package com.dangjia.acg.dto.supervisor;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author Ruking.Cheng
 * @descrilbe 督导工地现场返回
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2020/1/10 3:27 PM
 */
@Data
public class SupHouseDetailsDTO {
    private String houseId;//房子ID
    private String houseName;//房子名称
    private String address;//地址
    private String latitude;//纬度
    private String longitude;//经度
    private Integer type;//0:施工,1:维保
    private Integer buttonType;//0:显示底部按钮,1:不显示底部按钮
    private List<SupHouseFlowDTO> flowDatas;//记录集合
    private Map<String, Object> dataMap;//维保数据集

    @Data
    public static class SupHouseFlowDTO {
        private String memberName;//工匠名称
        private String memberId;//工匠ID
        private String workerTypeName;//工种名称
        private String workerTypeId;//workertyid
        private Integer workerType;//workertype
        private String image;//工种图片
        private String completion;//进程名称，为空不显示
        private List<WorkerMapDTO> mapList;//
    }

    @Data
    public static class WorkerMapDTO {
        private String keyName;//键，如：工期
        private String valueName;//值，如：35/75
    }
}
