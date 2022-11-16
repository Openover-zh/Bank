package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysOrdinaryUser;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.mapper.SysOrdinaryUserMapper;
import com.ruoyi.system.service.*;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 用户信息
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/user")
public class SysOrdinaryUserController extends BaseController
{
    @Autowired
    private ISysOrdinaryUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private SysOrdinaryUserMapper userMapper;

    /**
     * 获取用户列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysOrdinaryUser user)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
        if (userId!=1L){
            user.setUserId(loginUser.getUserId());
        }
        startPage();
        List<SysOrdinaryUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }


    /**
     * 根据用户编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = { "/", "/{userId}" })
    public AjaxResult getInfo(@PathVariable(value = "userId", required = false) Long userId)
    {
        userService.checkUserDataScope(userId);
        AjaxResult ajax = AjaxResult.success();
        List<SysRole> roles = roleService.selectRoleAll();
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
//        ajax.put("posts", postService.selectPostAll());
        if (StringUtils.isNotNull(userId))
        {
            SysOrdinaryUser sysUser = userService.selectUserById(userId);
            ajax.put(AjaxResult.DATA_TAG, sysUser);
//            ajax.put("postIds", postService.selectPostListByUserId(userId));
            ajax.put("roleIds", sysUser.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList()));
        }
        return ajax;
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysOrdinaryUser user)
    {
        String cardNumber = user.getCardNumber();
        if (!Pattern.matches("^\\d{9}$",cardNumber)){
            return error("新增用户'" + user.getCardNumber() + "'失败，卡号必须为9位数字");
        }
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user)))
        {
            return error("新增用户'" + user.getCardNumber() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return error("新增用户'" + user.getPhonenumber() + "'失败，手机号码已存在");
        }
        if (user.getBalance()==null){
            user.setBalance(BigDecimal.ZERO);
        }
        if (user.getRoleIds()==null || user.getRoleIds().length<1){
            //默认为普通用户
            Long[] l = {2L};
            user.setRoleIds(l);
        }
        user.setCreateBy(getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()==null?"123456":user.getPassword()));
        return toAjax(userService.insertUser(user));
    }
//
//    /**
//     * 修改用户
//     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysOrdinaryUser user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user)))
        {
            return error("修改用户'" + user.getCardNumber() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return error("修改用户'" + user.getPhonenumber() + "'失败，手机号码已存在");
        }
        user.setUpdateBy(getUsername());
        return toAjax(userService.updateUser(user));
    }
//
//    /**
//     * 删除用户
//     */
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        if (ArrayUtils.contains(userIds, getUserId()))
        {
            return error("当前用户不能删除");
        }
        return toAjax(userService.deleteUserByIds(userIds));
    }
//
    /**
     * 重置密码
     */
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public AjaxResult resetPwd(@RequestBody SysOrdinaryUser user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(getUsername());
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 账户充值
     * @param user
     * @return
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理",businessType = BusinessType.UPDATE)
    @PutMapping("/balance/recharge")
    public AjaxResult balanceRecharge(@RequestBody SysOrdinaryUser user){
        BigDecimal rechargeBalance = user.getBalance();
        if (rechargeBalance ==null || rechargeBalance.equals(BigDecimal.ZERO)){
            return error("充值金额不能为空或0");
        }
        SysOrdinaryUser ordinaryUser = userService.selectUserById(user.getUserId());
        if (ordinaryUser==null){
            return error("用户不存在！");
        }
        BigDecimal balance = ordinaryUser.getBalance();
        BigDecimal all = balance.add(rechargeBalance);
        ordinaryUser.setBalance(all);
        ordinaryUser.setUpdateTime(new Date());
        return toAjax(userMapper.updateUser(ordinaryUser));
    }


    /**
     * 转账
     * @param user
     * @return
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理",businessType = BusinessType.UPDATE)
    @PutMapping("/balance/transfer")
    @Transactional
    public AjaxResult balanceTransfer(@RequestBody SysOrdinaryUser user){
        BigDecimal transferBalance = user.getBalance();
        if (transferBalance ==null || transferBalance.equals(BigDecimal.ZERO)){
            return error("转账金额不能为空或0");
        }
        SysOrdinaryUser ordinaryUser = userService.selectUserById(user.getUserId());
        BigDecimal b1 = ordinaryUser.getBalance();
        if (b1.compareTo(transferBalance)<0){
            return error("账户余额不足！");
        }
        SysOrdinaryUser otherUser = userService.selectByCardNumber(user.getCardNumber());
        if (otherUser ==null){
            return error("对方账户不存在！");
        }

        BigDecimal b2 = otherUser.getBalance()==null?BigDecimal.ZERO:otherUser.getBalance();
        // 对方账户的钱
        BigDecimal otherUserBalance = b2.add(transferBalance);
        // 我放剩余的钱数
        BigDecimal ordinaryUserBalance = b1.subtract(transferBalance);
        otherUser.setBalance(otherUserBalance);
        otherUser.setUpdateTime(new Date());
        ordinaryUser.setBalance(ordinaryUserBalance);
        ordinaryUser.setUpdateTime(new Date());
        userMapper.updateUser(ordinaryUser);
        return toAjax(userMapper.updateUser(otherUser));
    }

    /**
     * 账户金额提取
     * @param user
     * @return
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理",businessType = BusinessType.UPDATE)
    @PutMapping("/balance/extraction")
    public AjaxResult balanceExtraction(@RequestBody SysOrdinaryUser user){
        BigDecimal rechargeBalance = user.getBalance();
        if (rechargeBalance ==null || rechargeBalance.equals(BigDecimal.ZERO)){
            return error("提取金额不能为空或0");
        }
        SysOrdinaryUser ordinaryUser = userService.selectUserById(user.getUserId());
        if (ordinaryUser==null){
            return error("用户不存在！");
        }
        BigDecimal balance = ordinaryUser.getBalance();
        int b = balance.compareTo(rechargeBalance);
        if (b>=0){
            BigDecimal result = balance.subtract(rechargeBalance);
            ordinaryUser.setBalance(result);
        }else {
            return error("账户余额不足！！");
        }
        ordinaryUser.setUpdateTime(new Date());
        return toAjax(userMapper.updateUser(ordinaryUser));
    }
//
    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysOrdinaryUser user)
    {
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setUpdateBy(getUsername());
        return toAjax(userService.updateUserStatus(user));
    }


}
