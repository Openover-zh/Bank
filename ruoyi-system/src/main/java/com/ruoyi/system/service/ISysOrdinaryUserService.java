package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.entity.SysOrdinaryUser;
import com.ruoyi.common.core.domain.entity.SysUser;

import java.util.List;

/**
 * @author zhangJiaHao
 * @date 2022/11/14 22:49
 */
public interface ISysOrdinaryUserService {
    public SysOrdinaryUser selectByCardNumber(String cardNumber);

    public List<SysOrdinaryUser> selectUserList(SysOrdinaryUser user);

    String checkUserNameUnique(SysOrdinaryUser user);

    int insertUser(SysOrdinaryUser user);

    String checkPhoneUnique(SysOrdinaryUser user);

    int deleteUserByIds(Long[] userIds);

    void checkUserDataScope(Long userId);

    void checkUserAllowed(SysOrdinaryUser user);

    int resetPwd(SysOrdinaryUser user);

    SysOrdinaryUser selectUserById(Long userId);

    int updateUser(SysOrdinaryUser user);

    int updateUserStatus(SysOrdinaryUser user);
}
