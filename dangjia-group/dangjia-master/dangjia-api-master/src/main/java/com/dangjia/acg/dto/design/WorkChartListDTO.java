package com.dangjia.acg.dto.design;

import com.dangjia.acg.modle.design.QuantityRoomImages;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Date: 30/10/2019
 * Time: 下午 3:22
 */
@Data
public class WorkChartListDTO {



    private List<QuantityRoomImages> list;

    private Date date;

    private String name;

    private Integer type;


}
