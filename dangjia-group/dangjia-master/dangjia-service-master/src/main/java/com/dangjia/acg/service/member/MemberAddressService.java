package com.dangjia.acg.service.member;

import com.dangjia.acg.common.exception.ServerCode;
import com.dangjia.acg.common.model.PageDTO;
import com.dangjia.acg.common.response.ServerResponse;
import com.dangjia.acg.common.util.CommonUtil;
import com.dangjia.acg.mapper.member.IMasterMemberAddressMapper;
import com.dangjia.acg.modle.core.HouseWorker;
import com.dangjia.acg.modle.house.House;
import com.dangjia.acg.modle.member.Member;
import com.dangjia.acg.modle.member.MemberAddress;
import com.dangjia.acg.service.core.CraftsmanConstructionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Ruking.Cheng
 * @descrilbe 地址管理
 * @email 495095492@qq.com
 * @tel 18075121944
 * @date on 2019/12/10 7:40 PM
 */
@Service
public class MemberAddressService {
    @Autowired
    private IMasterMemberAddressMapper iMasterMemberAddressMapper;
    @Autowired
    private CraftsmanConstructionService constructionService;

    /**
     * 新增地址
     *
     * @param userToken      userToken
     * @param renovationType 是否是装修地址:0：否，1：是
     * @param defaultType    是否是默认地址:0：否，1：是
     * @param name           业主姓名
     * @param mobile         业主手机
     * @param cityId         cityId
     * @param cityName       省/市/区
     * @param address        详细地址
     * @param inputArea      录入面积
     * @param longitude      经度
     * @param latitude       纬度
     * @return ServerResponse
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse insertAddress(String userToken, Integer renovationType,
                                        Integer defaultType, String name, String mobile, String cityId, String cityName,
                                        String address, BigDecimal inputArea, String longitude, String latitude) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        if (CommonUtil.isEmpty(name)) {
            return ServerResponse.createByErrorMessage("业主姓名未录入");
        }
        if (CommonUtil.isEmpty(mobile)) {
            return ServerResponse.createByErrorMessage("业主手机未录入");
        }
        if (CommonUtil.isEmpty(cityId) || CommonUtil.isEmpty(cityName)) {
            return ServerResponse.createByErrorMessage("业主城市未录入");
        }
        if (CommonUtil.isEmpty(address)) {
            return ServerResponse.createByErrorMessage("业主详细地址未录入");
        }
        if (renovationType == 1) {
            if (inputArea == null) {
                return ServerResponse.createByErrorMessage("房子面积未录入");
            }
            if (CommonUtil.isEmpty(longitude) || CommonUtil.isEmpty(latitude)) {
                return ServerResponse.createByErrorMessage("业主地址信息未录入");
            }
            if (inputArea.doubleValue() < 70) {
                inputArea = new BigDecimal(70);
            }
        }
        Member member = (Member) object;
        MemberAddress memberAddress = new MemberAddress();
        memberAddress.setMemberId(member.getId());
        memberAddress.setName(name);
        memberAddress.setMobile(mobile);
        memberAddress.setCityId(cityId);
        memberAddress.setCityName(cityName);
        memberAddress.setAddress(address);
        if (renovationType == 1) {
            memberAddress.setInputArea(inputArea);
            memberAddress.setLatitude(latitude);
            memberAddress.setLongitude(longitude);
        }
        memberAddress.setRenovationType(0);//默认创建 的是非装修地址
        memberAddress.setDefaultType(defaultType);
        setAddressDefaultType(defaultType, member.getId());
        int insert = iMasterMemberAddressMapper.insertSelective(memberAddress);
        if (insert > 0) {
            return ServerResponse.createBySuccess("新增成功",memberAddress);
        } else {
            return ServerResponse.createByErrorMessage("新增失败，请您稍后再试");
        }
    }

    private void setAddressDefaultType(int defaultType, String memberId) {
        if (defaultType == 1) {
            Example example = new Example(MemberAddress.class);
            example.createCriteria().andEqualTo(MemberAddress.MEMBER_ID, memberId);
            MemberAddress memberAddress1 = new MemberAddress();
            memberAddress1.setId(null);
            memberAddress1.setCreateDate(null);
            memberAddress1.setDataStatus(null);
            memberAddress1.setDefaultType(0);
            iMasterMemberAddressMapper.updateByExampleSelective(memberAddress1, example);
        }
    }

    /**
     * 业主修改地址
     *
     * @param userToken userToken
     * @return ServerResponse
     * @Param memberAddress
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse updataAddress(String userToken, String addressId, Integer defaultType,
                                        String name, String mobile, String cityId, String cityName,
                                        String address) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        if (CommonUtil.isEmpty(name)) {
            return ServerResponse.createByErrorMessage("业主姓名未录入");
        }
        if (CommonUtil.isEmpty(mobile)) {
            return ServerResponse.createByErrorMessage("业主手机未录入");
        }
        MemberAddress memberAddress = iMasterMemberAddressMapper.selectByPrimaryKey(addressId);
        if (memberAddress == null) {
            return ServerResponse.createByErrorMessage("未找到该地址");
        }
        Member member = (Member) object;
        memberAddress.setDefaultType(defaultType);
        memberAddress.setName(name);
        memberAddress.setMobile(mobile);
        if (memberAddress.getRenovationType() == 0) {
            if (CommonUtil.isEmpty(cityId) || CommonUtil.isEmpty(cityName)) {
                return ServerResponse.createByErrorMessage("请选择所在地区");
            }
            if (CommonUtil.isEmpty(address)) {
                return ServerResponse.createByErrorMessage("请录入详细地址");
            }
            memberAddress.setCityId(cityId);
            memberAddress.setCityName(cityName);
            memberAddress.setAddress(address);
        }
        memberAddress.setModifyDate(new Date());
        setAddressDefaultType(defaultType, member.getId());
        iMasterMemberAddressMapper.updateByPrimaryKeySelective(memberAddress);
        return ServerResponse.createBySuccess("修改成功",memberAddress);
    }

    public ServerResponse updataAddress(House house) {
        Example example = new Example(MemberAddress.class);
        example.createCriteria().andEqualTo(MemberAddress.MEMBER_ID, house.getMemberId())
                .andEqualTo(MemberAddress.HOUSE_ID, house.getId());
        List<MemberAddress> memberAddressList = iMasterMemberAddressMapper.selectByExample(example);
        if (memberAddressList.size() <= 0) {
            return ServerResponse.createByErrorMessage("未找房子地址");
        }
        MemberAddress memberAddress = memberAddressList.get(0);
        memberAddress.setCityId(house.getCityId());
        memberAddress.setCityName(house.getCityName());
        memberAddress.setAddress(house.getHouseName());
        memberAddress.setModifyDate(new Date());
        iMasterMemberAddressMapper.updateByPrimaryKeySelective(memberAddress);
        return ServerResponse.createBySuccessMessage("更新成功");
    }

    /**
     * 删除地址
     *
     * @param userToken userToken
     * @param addressId 地址ID
     * @return ServerResponse
     */
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse deleteAddress(String userToken, String addressId) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        MemberAddress memberAddress = iMasterMemberAddressMapper.selectByPrimaryKey(addressId);
        if (memberAddress == null) {
            return ServerResponse.createByErrorMessage("未找到该地址");
        }
        if (memberAddress.getRenovationType() == 1) {
            return ServerResponse.createByErrorMessage("装修地址不可以删除");
        }
        memberAddress.setModifyDate(new Date());
        memberAddress.setDataStatus(1);
        iMasterMemberAddressMapper.updateByPrimaryKeySelective(memberAddress);
        return ServerResponse.createBySuccessMessage("删除成功");
    }

    /**
     * 查找用户地址
     *
     * @param addressId 地址ID
     * @return ServerResponse
     */
    public ServerResponse selectAddress(String addressId) {
        MemberAddress memberAddress = iMasterMemberAddressMapper.selectByPrimaryKey(addressId);
        if (memberAddress == null) {
            return ServerResponse.createByErrorMessage("未找到该地址");
        }
        return ServerResponse.createBySuccess("查询成功", memberAddress);
    }

    /**
     * 查找用户地址
     *
     * @param houseId 地址ID
     * @return ServerResponse
     */
    public MemberAddress getMemberAddressInfo(String addressId,String houseId) {
        if(addressId!=null){
            return iMasterMemberAddressMapper.selectByPrimaryKey(addressId);
        } else if(houseId!=null){
            Example example=new Example(MemberAddress.class);
            example.createCriteria().andEqualTo(MemberAddress.HOUSE_ID,houseId);
            List<MemberAddress> list= iMasterMemberAddressMapper.selectByExample(example);
            if(list!=null){
                return list.get(0);
            }
        }
        return null;
    }

    /**
     * 查找用户地址列表
     *
     * @param pageDTO        分页信息
     * @param userToken      userToken
     * @param renovationType -1:全部，0：非装修地址，1：装修地址
     * @return
     */
    public ServerResponse selectAddressList(PageDTO pageDTO, String userToken, Integer renovationType) {
        Object object = constructionService.getMember(userToken);
        if (object instanceof ServerResponse) {
            return (ServerResponse) object;
        }
        Member member = (Member) object;
        PageHelper.startPage(pageDTO.getPageNum(), pageDTO.getPageSize());
        Example example = new Example(MemberAddress.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(MemberAddress.MEMBER_ID, member.getId());

        if(renovationType != null &&renovationType==2){//查未被装修过的装修地址
            criteria.andEqualTo(MemberAddress.RENOVATION_TYPE, 1);
            criteria.andIsNull(MemberAddress.HOUSE_ID);
        }else if (renovationType != null && renovationType != -1){
            criteria.andEqualTo(MemberAddress.RENOVATION_TYPE, renovationType);
        }
        criteria.andEqualTo(MemberAddress.DATA_STATUS, 0);
        example.orderBy(MemberAddress.CREATE_DATE).desc();
        List<MemberAddress> memberAddresses = iMasterMemberAddressMapper.selectByExample(example);
        if (memberAddresses.size() <= 0) {
            return ServerResponse.createByErrorCodeMessage(ServerCode.NO_DATA.getCode(), "暂无地址");
        }
        PageInfo pageResult = new PageInfo(memberAddresses);
        return ServerResponse.createBySuccess("查询成功", pageResult);
    }
}
