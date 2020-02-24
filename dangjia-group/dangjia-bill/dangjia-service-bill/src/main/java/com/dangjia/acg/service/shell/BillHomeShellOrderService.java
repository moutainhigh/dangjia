package com.dangjia.acg.service.shell;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.shell.HomeShellProductDTO;
import com.dangjia.acg.mapper.shell.IBillHomeShellOrderMapper;
import com.dangjia.acg.mapper.shell.IBillHomeShellProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * author: fzh
 * Date: 25/02/2020
 * Time: 下午 3:31
 */
@Service
public class BillHomeShellOrderService {
    protected static final Logger logger = LoggerFactory.getLogger(BillHomeShellOrderService.class);

    @Autowired
    private IBillHomeShellOrderMapper billHomeShellOrderMapper;
    /**
     * 当家贝兑换记录列表
     * @param pageDTO 分页
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param searchKey 兑换人姓名/电话/单号
     * @return
     */
    public ServerResponse queryOrderInfoList(PageDTO pageDTO, Date startTime, Date endTime, String searchKey){
        try{

            return ServerResponse.createBySuccess("查询成功","");
        }catch(Exception e){
            logger.error("查询失败");
            return ServerResponse.createBySuccessMessage("查询失败");
        }
    }



}
