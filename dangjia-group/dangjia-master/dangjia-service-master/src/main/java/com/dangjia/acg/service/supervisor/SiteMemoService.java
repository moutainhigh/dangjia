package com.dangjia.acg.service.supervisor;

import com.dangjia.acg.common.constants.SysConfig;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.dao.ConfigUtil;
import com.dangjia.acg.dto.supervisor.DjBasicsSiteMemoDTO;
import com.dangjia.acg.mapper.core.IWorkerTypeMapper;
import com.dangjia.acg.mapper.supervisor.DjBasicsSiteMemoMapper;
import com.dangjia.acg.mapper.supervisor.DjBasicsSiteMemoReminderMapper;
import com.dangjia.acg.modle.supervisor.DjBaicsSiteMemo;
import com.dangjia.acg.modle.supervisor.DjBaicsSiteMemoReminder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class SiteMemoService {
    @Autowired
    private DjBasicsSiteMemoMapper djBasicsSiteMemoMapper ;
    @Autowired
    private IWorkerTypeMapper workerTypeMapper;
    @Autowired
    private DjBasicsSiteMemoReminderMapper djBasicsSiteMemoReminderMapper ;
    @Autowired
    private ConfigUtil configUtil;
    /**
     *
     * @param request
     * @return
     */
    public ServerResponse delSiteMemo(HttpServletRequest request, String id,String isSelfCreate ) {
        try {
            //isSelfCreate 1:自建  0：非自建
            if (isSelfCreate.equals(0))
                return ServerResponse.createByErrorMessage("没有删除权限，只能查看");
            int i = djBasicsSiteMemoMapper.deleteByPrimaryKey(id);
            if (i <= 0)
                return ServerResponse.createByErrorMessage("删除失败");
            return ServerResponse.createBySuccessMessage("删除成功");
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("删除备忘录异常");
        }
    }

    /**
     *新增备忘录
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse addSiteMemo(HttpServletRequest request, DjBaicsSiteMemo djBaicsSiteMemo,String specifyReminder) {
        try {
           int j= djBasicsSiteMemoMapper.insertSelective(djBaicsSiteMemo);
           DjBaicsSiteMemo siteMemo=djBasicsSiteMemoMapper.querySiteMemoByMemberId(djBaicsSiteMemo.getMemberId()).get(0);
           String id= siteMemo.getId();
           String[] str=specifyReminder.split(",");
           for (int i=0;i<str.length;i++)
           {
               DjBaicsSiteMemoReminder djBaicsSiteMemoReminder=new DjBaicsSiteMemoReminder();
               djBaicsSiteMemoReminder.setSpecifyReminder(str[i]);
               djBaicsSiteMemoReminder.setState("0");
               djBaicsSiteMemoReminder.setDjBasicsSiteMemoId(id);
               djBasicsSiteMemoReminderMapper.insert(djBaicsSiteMemoReminder);
           }
            if (j <= 0)
                return ServerResponse.createByErrorMessage("新增备忘录失败");
            return ServerResponse.createBySuccessMessage("新增备忘录成功");
        } catch (Exception e) {
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
            if (list == null || list.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无数据");
            }
            list.forEach(djBasicsSiteMemoDTO -> {
                if (djBasicsSiteMemoDTO!=null)
                djBasicsSiteMemoDTO.setWorkerTypeName(djBasicsSiteMemoDTO.getWorkerTypeId()!=null && djBasicsSiteMemoDTO.getWorkerTypeId().length()>0? workerTypeMapper.selectByPrimaryKey(djBasicsSiteMemoDTO.getWorkerTypeId()).getName() : "");
            });
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("查询备忘录列表异常");
        }
    }

    /**
     * 查询单个备忘录详情
     * @param request
     * @param id
     * @return
     */
    public ServerResponse querySiteMemoDetail(HttpServletRequest request, String id,String isSelfCreate) {
        try {
            DjBasicsSiteMemoDTO djBasicsSiteMemoDTO =djBasicsSiteMemoMapper.querySiteMemoDetail(id);
            return ServerResponse.createBySuccess("查询成功", djBasicsSiteMemoDTO);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("查询单个备忘录详情异常");
        }
    }

    /**
     * 查询备忘录提列表醒详情
     * @param request
     * @param memberId
     * @return
     */
    public ServerResponse queryRemindSiteMemoDetail(HttpServletRequest request, String memberId, String id) {
        //备忘录非自己创建，修改关联表的状态

        try {
            DjBasicsSiteMemoDTO djBasicsSiteMemoDTO = djBasicsSiteMemoMapper.queryRemindSiteMemoDetail(memberId,id).get(0);

            DjBaicsSiteMemoReminder djBaicsSiteMemoReminder = new DjBaicsSiteMemoReminder();
            djBaicsSiteMemoReminder.setId(djBasicsSiteMemoDTO.getId());
            djBaicsSiteMemoReminder.setState("1");//已看
            djBasicsSiteMemoReminderMapper.updateByPrimaryKeySelective(djBaicsSiteMemoReminder);
            return ServerResponse.createBySuccess("查询成功", djBasicsSiteMemoDTO);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("查询单个备忘录详情异常");
        }
    }



    /**
     *清空备忘录信息
     * @param request
     * @param memberId
     * @return
     */
    public ServerResponse clearSiteMemo(HttpServletRequest request, String memberId) {
        try {
            DjBaicsSiteMemoReminder djBaicsSiteMemoReminder=new DjBaicsSiteMemoReminder();
            djBaicsSiteMemoReminder.setState("1");//已看
            djBaicsSiteMemoReminder.setSpecifyReminder(memberId);
            djBasicsSiteMemoReminderMapper.updateByPrimaryKeySelective(djBaicsSiteMemoReminder);
            return ServerResponse.createBySuccessMessage("清空备忘录成功");
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("清空备忘录信息异常");
        }
    }

    /**
     * 查看提醒记录列表
     * @param request
     * @param memberId
     * @return
     */
    public ServerResponse queryRemindSiteMemo(HttpServletRequest request, String memberId,PageDTO pageDTO) {
        try {
            String imageAddress = configUtil.getValue(SysConfig.DANGJIA_IMAGE_LOCAL, String.class);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<DjBasicsSiteMemoDTO> list = djBasicsSiteMemoMapper.queryRemindSiteMemo(memberId);
            if (list == null || list.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "查无数据");
            }
            list.forEach(djBasicsSiteMemoDTO -> {
                if (djBasicsSiteMemoDTO!=null) {
                    djBasicsSiteMemoDTO.setHead(imageAddress + djBasicsSiteMemoDTO.getHead());
                    djBasicsSiteMemoDTO.setWorkerTypeName(djBasicsSiteMemoDTO.getWorkerTypeId() != null && djBasicsSiteMemoDTO.getWorkerTypeId().length() > 0 ? workerTypeMapper.selectByPrimaryKey(djBasicsSiteMemoDTO.getWorkerTypeId()).getName() : "");
                }
            });
            PageInfo pageResult = new PageInfo(list);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            return ServerResponse.createByErrorMessage("查看提醒记录列表异常");
        }
    }
}
