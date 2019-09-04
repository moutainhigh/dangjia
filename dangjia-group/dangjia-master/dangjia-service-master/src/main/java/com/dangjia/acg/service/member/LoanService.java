package com.dangjia.acg.service.member;

import com.dangjia.acg.common.constants.Constants;
import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.dto.member.LoanDTO;
import com.dangjia.acg.mapper.member.LoanFlowMapper;
import com.dangjia.acg.mapper.member.LoanMapper;
import com.dangjia.acg.mapper.other.ICityMapper;
import com.dangjia.acg.modle.member.Loan;
import com.dangjia.acg.modle.member.LoanFlow;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.other.City;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Ruking.Cheng
 * @descrilbe 贷款接口Service
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/2/27 4:11 PM
 */
@Service
public class LoanService {
    @Autowired
    private LoanMapper loanMapper;
    @Autowired
    private LoanFlowMapper loanFlowMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;

    @Autowired
    private ICityMapper iCityMapper;
    /**
     * 添加贷款需求
     *
     * @param userToken userToken
     * @param name      申请贷款人姓名
     * @param bankName  银行名称
     * @return ServerResponse
     */
    public ServerResponse addLoan(String cityId,String userToken, String name, String bankName) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member user = (Member) object;
        if (CommonUtil.isEmpty(name) || CommonUtil.isEmpty(bankName)) {
            return ServerResponse.createByErrorMessage("传入参数错误");
        }
        City city = iCityMapper.selectByPrimaryKey(cityId);
        Loan loan = new Loan();
        loan.setName(name);
        loan.setCityId(cityId);
        loan.setCityName(city.getName());
        loan.setBankName(bankName);
        loan.setMemberId(user.getId());
        loanMapper.insertSelective(loan);
        return ServerResponse.createBySuccessMessage("提交成功");
    }

    /**
     * 查询贷款需求列表
     *
     * @param request   request
     * @param pageDTO   分页码
     * @param state     贷款状态：-1：全部，0:待处理，1:无意向，2:转到银行，3：已放款，4:无法放款
     * @param searchKey 申请人姓名，申请人电话，跟进人姓名
     * @return ServerResponse
     */
    public ServerResponse getLoanList(HttpServletRequest request, PageDTO pageDTO, Integer state, String searchKey) {
        try {
            String cityId = request.getParameter(Constants.CITY_ID);
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            if (state == -1) state = null;
            List<LoanDTO> loanDTOS = loanMapper.getLoanList(cityId,state, searchKey);
            if (loanDTOS.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode()
                        , "查无数据");
            }
            PageInfo pageResult = new PageInfo(loanDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 查询贷款需求操作列表
     *
     * @param request request
     * @param pageDTO 分页码
     * @param loanId  贷款需求ID
     * @return
     */
    public ServerResponse getLoanFlowList(HttpServletRequest request, PageDTO pageDTO, String loanId) {
        try {
            if (CommonUtil.isEmpty(loanId)) {
                return ServerResponse.createByErrorMessage("传入参数错误");
            }
            PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
            List<LoanDTO> loanDTOS = loanFlowMapper.getLoanFlow(loanId);
            if (loanDTOS.size() <= 0) {
                return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode()
                        , "查无数据");
            }
            PageInfo pageResult = new PageInfo(loanDTOS);
            return ServerResponse.createBySuccess("查询成功", pageResult);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("查询失败");
        }
    }

    /**
     * 修改贷款需求状态
     *
     * @param request       request
     * @param userId        web登录的用户ID
     * @param loanId        贷款需求ID
     * @param state         贷款状态：0:待处理，1:无意向，2:转到银行，3：已放款，4:无法放款
     * @param stateDescribe 贷款描述
     * @return ServerResponse
     */
    public ServerResponse updataLoan(HttpServletRequest request, String userId, String loanId, Integer state, String stateDescribe) {
        if (CommonUtil.isEmpty(userId) || CommonUtil.isEmpty(loanId) || CommonUtil.isEmpty(state) || CommonUtil.isEmpty(stateDescribe)) {
            return ServerResponse.createByErrorMessage("传入参数错误");
        }
        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setState(state);
        loan.setStateDescribe(stateDescribe);
        loan.setFollowUpId(userId);
        loanMapper.updateByPrimaryKeySelective(loan);
        LoanFlow loanFlow = new LoanFlow();
        loanFlow.setLoanId(loanId);
        loanFlow.setState(state);
        loanFlow.setStateDescribe(stateDescribe);
        loanFlow.setFollowUpId(userId);
        loanFlowMapper.insertSelective(loanFlow);
        return ServerResponse.createBySuccessMessage("操作成功");
    }
}
