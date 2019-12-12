package com.dangjia.acg.service.supervisor;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.supervisor.DjBasicsSupervisorAuthorityDTO;
import com.dangjia.acg.mapper.supervisor.DjBasicsSupervisorAuthorityMapper;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.supervisor.DjBasicsPatrolRecord;
import com.dangjia.acg.modle.supervisor.DjBasicsSupervisorAuthority;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class SupAuthorityService {
    private static Logger logger = LoggerFactory.getLogger(SupAuthorityService.class);
    @Autowired
    private DjBasicsSupervisorAuthorityMapper djBasicsSupervisorAuthorityMapper ;

    /**
     * 删除已选
     * @param request
     * @param id
     * @return
     */
    public ServerResponse delAuthority(HttpServletRequest request, String id) {
        try {
            int i = djBasicsSupervisorAuthorityMapper.deleteByPrimaryKey(id);
            if (i <= 0)
                return ServerResponse.createByErrorMessage("删除失败");
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            logger.error("新建巡检异常", e);
            return ServerResponse.createByErrorMessage("新建巡检异常");
        }
    }

    /**
     * 搜索已选
     * @param request
     * @param visitState
     * @param keyWord
     * @return
     */
    public ServerResponse searchAuthority(HttpServletRequest request, String visitState, String keyWord, PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DjBasicsSupervisorAuthority> list=djBasicsSupervisorAuthorityMapper.searchAuthority(visitState,keyWord);
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("新建巡检异常", e);
            return ServerResponse.createByErrorMessage("新建巡检异常");
        }
    }


    /**
     * 增加已选
     * @param request
     * @param          djBasicsSupervisorAuthority
     * @return
     */
    public ServerResponse addAuthority(HttpServletRequest request, DjBasicsSupervisorAuthority djBasicsSupervisorAuthority ) {
        try {
           int i= djBasicsSupervisorAuthorityMapper.insert(djBasicsSupervisorAuthority);
            if (i <= 0)
                return ServerResponse.createByErrorMessage("增加失败");
            return ServerResponse.createBySuccessMessage("增加成功");
        } catch (Exception e) {
            logger.error("增加已选异常", e);
            return ServerResponse.createByErrorMessage("增加已选异常");
        }
    }
}
