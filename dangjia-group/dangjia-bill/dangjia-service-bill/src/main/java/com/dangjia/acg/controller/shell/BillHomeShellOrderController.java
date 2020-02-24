package com.dangjia.acg.controller.shell;

import com.dangjia.acg.api.shell.BillHomeShellOrderAPI;
import com.dangjia.acg.common.annotation.ApiMethod;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.service.shell.BillHomeShellOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * fzh
 * 2020-02-25
 */
@RestController
public class BillHomeShellOrderController implements BillHomeShellOrderAPI {
    protected static final Logger logger = LoggerFactory.getLogger(BillHomeShellOrderController.class);

    @Autowired
    private BillHomeShellOrderService billHomeShellOrderService;
    /**
     * 当家贝兑换记录列表
     * @param request
     * @param pageDTO 分页
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param searchKey 兑换人姓名/电话/单号
     * @return
     */
    @Override
    @ApiMethod
    public ServerResponse queryOrderInfoList(HttpServletRequest request, PageDTO pageDTO, Date startTime,Date endTime, String searchKey){
        return billHomeShellOrderService.queryOrderInfoList(pageDTO,startTime,endTime,searchKey);
    }

}
