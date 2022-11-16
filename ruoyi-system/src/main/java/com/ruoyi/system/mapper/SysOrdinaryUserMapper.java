package com.ruoyi.system.mapper;

import com.ruoyi.common.core.domain.entity.SysOrdinaryUser;
import com.ruoyi.common.core.domain.entity.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户表 数据层
 * 
 * @author ruoyi
 */
public interface SysOrdinaryUserMapper
{
    /**
     * 根据卡号查询用户
     * @param cardNumber 卡号
     * @return 用户信息
     */
    public SysOrdinaryUser selectByCardNumber(String cardNumber);

    List<SysOrdinaryUser> selectUserList(SysOrdinaryUser user);

    SysOrdinaryUser checkUserNameUnique(String userName);

    int insertUser(SysOrdinaryUser user);

    SysUser checkPhoneUnique(String phonenumber);

    int deleteUserByIds(Long[] userIds);

    int updateUser(SysOrdinaryUser user);

    SysOrdinaryUser selectUserById(Long userId);
}
