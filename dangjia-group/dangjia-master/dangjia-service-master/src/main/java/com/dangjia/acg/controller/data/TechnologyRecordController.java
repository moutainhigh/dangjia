package com.dangjia.acg.controller.data;

import com.dangjia.acg.api.data.TechnologyRecordAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.modle.matter.TechnologyRecord;
import com.dangjia.acg.service.matter.TechnologyRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: Ronalcheng
 * Date: 2018/12/2 0002
 * Time: 17:25
 */
@RestController
public class TechnologyRecordController implements TechnologyRecordAPI {

    @Autowired
    private TechnologyRecordService technologyRecordService;

    @Override
    @ApiMethod
    public void addTechnologyRecord(TechnologyRecord technologyRecord){
        technologyRecordService.addTechnologyRecord(technologyRecord);
    }
}
