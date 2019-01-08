package com.dangjia.acg.controller.web.engineer;

import com.dangjia.acg.api.web.engineer.WebEngineerAPI;
import com.dangjia.acg.service.engineer.EngineerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: Ronalcheng
 * Date: 2019/1/4 0004
 * Time: 17:35
 */
@RestController
public class WebEngineerController implements WebEngineerAPI {
    @Autowired
    private EngineerService engineerService;
}
