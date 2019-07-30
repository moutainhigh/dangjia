package com.dangjia.acg.service.system;

import com.dangjia.acg.api.RedisClient;
import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.exception.BaseException;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.BeanUtils;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dto.system.JobRolesVO;
import com.dangjia.acg.mapper.other.ICityMapper;
import com.dangjia.acg.mapper.system.IDepartmentMapper;
import com.dangjia.acg.mapper.system.IJobMapper;
import com.dangjia.acg.mapper.system.IJobRoleMapper;
import com.dangjia.acg.mapper.user.RoleMapper;
import com.dangjia.acg.modle.other.City;
import com.dangjia.acg.modle.system.Department;
import com.dangjia.acg.modle.system.Job;
import com.dangjia.acg.modle.system.JobRole;
import com.dangjia.acg.modle.user.MainUser;
import com.dangjia.acg.modle.user.Role;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * author: qyd
 * Date: 2019/7/1
 * Time: 16:25
 */
@Service
public class SystemServices {
    @Autowired
    private IDepartmentMapper departmentMapper;

    @Autowired
    private IJobMapper jobMapper;
    @Autowired
    private ICityMapper cityMapper;

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private IJobRoleMapper jobRoleMapper;

    //查询组织架构
    public ServerResponse queryDepartment(String user_id,String parentId) {
        try {
            Example example=new Example(Department.class);
//            MainUser existUser = redisClient.getCache(Constants.USER_KEY + user_id, MainUser.class);
//            if (null != existUser && CommonUtil.isEmpty(parentId)) {
//                example.createCriteria().andEqualTo(Department.ID, existUser.getDepartmentId());
//            }else {
//                if (CommonUtil.isEmpty(parentId)) {
//                    example.createCriteria().andCondition(" (parent_top is null or parent_top ='')  ");
//                } else {
//                    example.createCriteria().andEqualTo(Department.PARENT_ID, parentId);
//                }
//            }
            if (CommonUtil.isEmpty(parentId)) {
                example.createCriteria().andCondition(" (parent_top is null or parent_top ='')  ");
            } else {
                example.createCriteria().andEqualTo(Department.PARENT_ID, parentId);
            }
            List<Department> departments = departmentMapper.selectByExample(example);
            return ServerResponse.createBySuccess("查询成功",departments);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
    //查询组织架构
    public ServerResponse queryDepartmentAll(String parentId) {
        try {
            Example example=new Example(Department.class);
            List<Department> departments = departmentMapper.selectByExample(example);
            List<Map> departmentMap =new ArrayList<>();
            if(departments.size()>0){
                for (Department department : departments) {
                    if(!CommonUtil.isEmpty(parentId)){
                        if(department.getId().equals(parentId)||(!CommonUtil.isEmpty(department.getParentTop())&&department.getParentTop().equals(parentId))){
                            Map map = BeanUtils.beanToMap(department);
                            if(CommonUtil.isEmpty(department.getParentId())){
                                departmentMap.add(map);
                            }
                        }
                    }
                    if(CommonUtil.isEmpty(parentId)){
                        Map map = BeanUtils.beanToMap(department);
                        if(CommonUtil.isEmpty(department.getParentId())){
                            departmentMap.add(map);
                        }
                    }
                }
            }
            departmentMap=getDepartmentLower(departmentMap,departments);
            return ServerResponse.createBySuccess("查询成功",departmentMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }
    public List<Map> getDepartmentLower(List<Map> departmentMap,List<Department> departments) {
        try {
            if(departments.size()>0){
                for (int i = 0; i < departmentMap.size(); i++) {
                   Map map= departmentMap.get(i);
                    List<Map> maps =new ArrayList<>();
                    for (Department department : departments) {
                        if(map.get(Department.ID).equals(department.getParentId())){
                            maps.add(BeanUtils.beanToMap(department));
                        }
                    }
                    if(maps.size()>0) {
                        maps=getDepartmentLower(maps,departments);
                        map.put("lower", maps);
                    }
                }
            }
            return departmentMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //查询组织架构
    public ServerResponse queryTopDepartmentAll(String parentId) {
        try {
            List list =new ArrayList();
            Department departments = departmentMapper.selectByPrimaryKey(parentId);
            if(departments!=null){
                list.add(departments.getId());
                while (!CommonUtil.isEmpty(departments.getParentId())){
                    departments = departmentMapper.selectByPrimaryKey(departments.getParentId());
                    list.add(departments.getId());
                }
            }
            return ServerResponse.createBySuccess("查询成功",StringUtils.join(list,","));
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    //修改组织架构信息
    public ServerResponse editDepartment(String user_id,Department department) {
        try {
            MainUser existUser = redisClient.getCache(Constants.USER_KEY + user_id, MainUser.class);
            if (null == existUser) {
                throw new BaseException(ServerCode.THE_LANDING_TIME_PLEASE_LAND_AGAIN, ServerCode.THE_LANDING_TIME_PLEASE_LAND_AGAIN.getDesc());
            }

            if(!CommonUtil.isEmpty(department.getCityId())){
                String[] cityids=department.getCityId().split(",");
                for (int i = 0; i < cityids.length; i++) {
                    City city=cityMapper.selectByPrimaryKey(cityids[i]);
                    cityids[i]=city.getName();
                }
                department.setCityName(StringUtils.join(cityids, ","));
            }
            department.setOperateId(existUser.getId());
            department.setModifyDate(new Date());
            if(departmentMapper.selectByPrimaryKey(department.getId())!=null){
                departmentMapper.updateByPrimaryKeySelective(department);
                return ServerResponse.createBySuccessMessage("修改成功");
            }else{
                departmentMapper.insertSelective(department);
                return ServerResponse.createBySuccessMessage("添加成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(ServerCode.WRONG_PARAM, "操作失败");
        }
    }


    //查询岗位
    public ServerResponse queryJob(String cityId,String departmentId, PageDTO pageDTO) {
        try {
            Example example=new Example(Job.class);
            Example.Criteria criteria= example.createCriteria();
            if(!CommonUtil.isEmpty(departmentId)){
                criteria.andEqualTo(Job.DEPARTMENT_ID,departmentId);
            }
            if(!CommonUtil.isEmpty(cityId)){
                criteria.andCondition(" FIND_IN_SET( '"+cityId+"', city_id)");
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<Job> jobs = jobMapper.selectByExample(example);
            PageInfo pageResult=new PageInfo(jobs);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    public ServerResponse getJobAndRoles(String id) {
        // 获取用户及他对应的roleIds
        JobRolesVO jobAndRoles = this.jobMapper.getJobAndRoles(id);
        List<Role> list = this.roleMapper.getRoles();
        jobAndRoles.setRoles(list);
        return ServerResponse.createBySuccess("ok", jobAndRoles);

    }


    public ServerResponse setJob(String user_id,Job job, String roleIds) {
        MainUser existUser = redisClient.getCache(Constants.USER_KEY + user_id, MainUser.class);
        if (null != existUser) {
            job.setOperateId(existUser.getId());
        }
        // 判断用户是否已经存在
        if (!CommonUtil.isEmpty(job.getCode())) {
            Example example=new Example(Job.class);
            Example.Criteria criteria= example.createCriteria();
            criteria.andEqualTo(Job.CODE,job.getCode());
            criteria.andNotEqualTo(Job.ID,job.getId());
            List<Job> jobs = jobMapper.selectByExample(example);
            if (jobs.size()>0) {
                return ServerResponse.createByErrorMessage("岗位编号不能重复");
            }
        }
        if (!CommonUtil.isEmpty(job.getName())) {
            Example example=new Example(Job.class);
            Example.Criteria criteria= example.createCriteria();
                criteria.andEqualTo(Job.NAME,job.getName());
                criteria.andNotEqualTo(Job.ID,job.getId());
            List<Job> jobs = jobMapper.selectByExample(example);
            if (jobs.size()>0) {
                return ServerResponse.createByErrorMessage("岗位名称不能重复");
            }
        }
        String jobId;
        if (!CommonUtil.isEmpty(job.getDepartmentId())) {
            Department department = departmentMapper.selectByPrimaryKey(job.getDepartmentId());
            job.setCityId(department.getCityId());
            job.setCityName(department.getCityName());
            job.setDepartmentName(department.getName());
        }
        if(jobMapper.selectByPrimaryKey(job.getId())!=null){
            // 更新用户
            job.setModifyDate(new Date());
            this.jobMapper.updateByPrimaryKeySelective(job);
            // 删除之前的角色
            Example example=new Example(JobRole.class);
            example.createCriteria().andEqualTo(JobRole.JOB_ID,job.getId());
            this.jobRoleMapper.deleteByExample(example);
            jobId=job.getId();
        } else {
            // 新增用户
            job.setId((int)(Math.random() * 50000000) + 50000000 + "" + System.currentTimeMillis());
            job.setCreateDate(new Date());
            job.setModifyDate(new Date());
            jobId=job.getId();
            this.jobMapper.insertSelective(job);
        }
        // 给用户授角色
        String[] arrays = roleIds.split(",");
        for (String roleId : arrays) {
            JobRole urk = new JobRole();
            urk.setRoleId(roleId);
            urk.setJobId(jobId);
            this.jobRoleMapper.insert(urk);
        }
        return ServerResponse.createBySuccessMessage("ok");
    }
}
