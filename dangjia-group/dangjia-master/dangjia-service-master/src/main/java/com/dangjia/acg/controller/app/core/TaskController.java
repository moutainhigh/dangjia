package com.dangjia.acg.controller.app.core;

import com.dangjia.acg.api.app.core.TaskAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.core.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: Ronalcheng
 * Date: 2018/11/5 0005
 * Time: 20:44
 */
@RestController
public class TaskController implements TaskAPI {

    @Autowired
    private TaskService taskService;

    /**
     * 任务列表
     * userRole：app应用 1为业主  ，2为工匠
     */
    @Override
    @ApiMethod
    public ServerResponse getTaskList(String userToken,String userRole){
        return taskService.getTaskList(userToken, userRole);
    }
}
