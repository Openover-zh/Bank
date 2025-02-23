package com.bank.framework.web.service;

import com.bank.common.core.domain.entity.SysOrdinaryUser;
import com.bank.common.utils.SecurityUtils;
import com.bank.system.service.ISysOrdinaryUserService;
import org.apache.commons.compress.utils.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.bank.common.core.domain.entity.SysUser;
import com.bank.common.core.domain.model.LoginUser;
import com.bank.common.enums.UserStatus;
import com.bank.common.exception.ServiceException;
import com.bank.common.utils.StringUtils;
import com.bank.system.service.ISysUserService;

import java.util.regex.Pattern;

/**
 * 用户验证处理
 *
 * @author ruoyi
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private ISysUserService userService;
    @Autowired
    private ISysOrdinaryUserService ordinaryUserService;
    
    @Autowired
    private SysPasswordService passwordService;

    @Autowired
    private SysPermissionService permissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        // 判断用户名是否为9为数字
        String pattern = "^\\d{9}$";
        if (Pattern.matches(pattern,username)) {
            SysOrdinaryUser user = ordinaryUserService.selectByCardNumber(username);
            if (StringUtils.isNull(user))
            {
                log.info("登录用户：{} 不存在.", username);
                throw new ServiceException("登录用户：" + username + " 不存在");
            }
            else if (UserStatus.DELETED.getCode().equals(user.getDelFlag()))
            {
                log.info("登录用户：{} 已被删除.", username);
                throw new ServiceException("对不起，您的账号：" + username + " 已被删除");
            }
            else if (UserStatus.DISABLE.getCode().equals(user.getStatus()))
            {
                log.info("登录用户：{} 已被停用.", username);
                throw new ServiceException("对不起，您的账号：" + username + " 已停用");
            }
            passwordService.validate(user);
            return createLoginUser(user);
        }else {
            SysUser user = userService.selectUserByUserName(username);
            if (StringUtils.isNull(user))
            {
                log.info("登录用户：{} 不存在.", username);
                throw new ServiceException("登录用户：" + username + " 不存在");
            }
            else if (UserStatus.DELETED.getCode().equals(user.getDelFlag()))
            {
                log.info("登录用户：{} 已被删除.", username);
                throw new ServiceException("对不起，您的账号：" + username + " 已被删除");
            }
            else if (UserStatus.DISABLE.getCode().equals(user.getStatus()))
            {
                log.info("登录用户：{} 已被停用.", username);
                throw new ServiceException("对不起，您的账号：" + username + " 已停用");
            }
            passwordService.validate(user);
            return createLoginUser(user);
        }
    }

    public UserDetails createLoginUser(SysUser user)
    {
        return new LoginUser(user.getUserId(),user, Sets.newHashSet("*:*:*"));
    }

    public UserDetails createLoginUser(SysOrdinaryUser user)
    {
        return new LoginUser(user.getUserId(),user, permissionService.getMenuPermission(user));
    }
}
