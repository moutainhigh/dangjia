package com.dangjia.acg.service.product;

import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.mapper.product.IBrowseRecordMapper;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.product.BrowseRecord;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class BrowseRecordService {

    @Autowired
    private IBrowseRecordMapper iBrowseRecordMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;

    /**
     * @param request
     * @param userToken
     * @param pageDTO
     * @return
     */
    public ServerResponse queryBrowseRecord(HttpServletRequest request, String userToken, PageDTO pageDTO) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            String memberId=member.getId();
            List<BrowseRecord> list=iBrowseRecordMapper.queryBrowseRecord(memberId);
            return ServerResponse.createBySuccess("查询浏览记录成功!",list);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,获取用户浏览商品痕迹失败!");
        }
    }

    /**
     * @param request
     * @param userToken
     * @return
     */
    public ServerResponse addBrowseRecord(HttpServletRequest request, String userToken, BrowseRecord browseRecord) {
        try {
            Object object = constructionService.getMember(userToken);
            if (object instanceof ServerResponse) {
                return (ServerResponse) object;
            }
            Member member = (Member) object;
            String productId = browseRecord.getProductId();
            Example example = new Example(BrowseRecord.class);
            example.createCriteria().andEqualTo(BrowseRecord.PRODUCT_ID, productId);
            List<BrowseRecord> list = iBrowseRecordMapper.selectByExample(example);
            if (list!=null)
            {
                BrowseRecord record= list.get(0);
                if(record.getProductId()==browseRecord.getProductId())
                {
                    record.setVisits_num(record.getVisits_num()+1);
                }
                record.setMemberId(member.getId());
                iBrowseRecordMapper.updateByPrimaryKeySelective(record);
                return ServerResponse.createBySuccess("已经存在记录，修改成功!");
            }
            browseRecord.setMemberId(member.getId());//用户编号
            iBrowseRecordMapper.insert(browseRecord);
            return ServerResponse.createBySuccess("添加用户浏览记录成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("系统出错,记录用户浏览商品痕迹失败!");
        }
    }
}
