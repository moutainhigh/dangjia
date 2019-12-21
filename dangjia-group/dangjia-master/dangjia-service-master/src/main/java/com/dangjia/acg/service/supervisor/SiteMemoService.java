package com.dangjia.acg.service.supervisor;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dto.supervisor.DjBasicsSiteMemoDTO;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.supervisor.DjBasicsSiteMemoMapper;
import com.dangjia.acg.modle.supervisor.DjBaicsSiteMemo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class SiteMemoService {
    private static Logger logger = LoggerFactory.getLogger(SiteMemoService.class);
    @Autowired
    private DjBasicsSiteMemoMapper djBasicsSiteMemoMapper ;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    /**
     *
     * @param request
     * @return
     */
    public ServerResponse delSiteMemo(HttpServletRequest request, String id) {
        try {
            int i = djBasicsSiteMemoMapper.deleteByPrimaryKey(id);
            if (i < 0)
                return ServerResponse.createByErrorMessage("删除失败");
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            logger.error("删除备忘录异常", e);
            return ServerResponse.createByErrorMessage("删除备忘录异常");
        }
    }

    /**
     *新增备忘录
     * @param request
     * @return
     */
    public ServerResponse addSiteMemo(HttpServletRequest request, DjBaicsSiteMemo djBaicsSiteMemo) {
        try {
            int i=djBasicsSiteMemoMapper.insertSelective(djBaicsSiteMemo);
            if (i < 0)
                return ServerResponse.createByErrorMessage("新增备忘录失败");
            return ServerResponse.createBySuccessMessage("新增备忘录成功");
        } catch (Exception e) {
            logger.error("新增备忘录异常", e);
            return ServerResponse.createByErrorMessage("新增备忘录异常");
        }
    }

    /**
     *查询备忘录列表
     * @param request
     * @param memberId
     * @return
     */
    public ServerResponse querySiteMemo(HttpServletRequest request, String memberId, PageDTO pageDTO) {
        try {
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DjBasicsSiteMemoDTO> list = djBasicsSiteMemoMapper.querySiteMemo(memberId);
            list.forEach(djBasicsSiteMemoDTO -> {
                djBasicsSiteMemoDTO.setWorkerTypeName(djBasicsSiteMemoDTO.getWorkerTypeId() != null ? workerTypeMapper.selectByPrimaryKey(djBasicsSiteMemoDTO.getWorkerTypeId()).getName() : "");
            });
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            logger.error("查询备忘录列表异常", e);
            return ServerResponse.createByErrorMessage("查询备忘录列表异常");
        }
    }

    /**
     * 查询单个备忘录详情
     * @param request
     * @param id
     * @return
     */
    public ServerResponse querySiteMemoDetail(HttpServletRequest request, String id) {
        try {
            DjBasicsSiteMemoDTO djBasicsSiteMemoDTO =djBasicsSiteMemoMapper.querySiteMemoDetail(id);
            return ServerResponse.createBySuccess("查询成功", djBasicsSiteMemoDTO);
        } catch (Exception e) {
            logger.error("查询单个备忘录详情异常", e);
            return ServerResponse.createByErrorMessage("查询单个备忘录详情异常");
        }
    }
}
