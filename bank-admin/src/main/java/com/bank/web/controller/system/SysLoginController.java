package com.bank.web.controller.system;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.bank.common.core.domain.entity.SysOrdinaryUser;
import com.bank.system.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.bank.common.constant.Constants;
import com.bank.common.core.domain.AjaxResult;
import com.bank.common.core.domain.entity.SysMenu;
import com.bank.common.core.domain.entity.SysUser;
import com.bank.common.core.domain.model.LoginBody;
import com.bank.common.utils.SecurityUtils;
import com.bank.framework.web.service.SysLoginService;
import com.bank.framework.web.service.SysPermissionService;
import com.bank.system.service.ISysMenuService;

/**
 * 登录验证
 * 
 * @author ruoyi
 */
@RestController
public class SysLoginController
{
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;
    @Autowired
    private ISysRoleService roleService;

    /**
     * 登录方法
     * 
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        String token = "";
        if (loginBody.getType()==1){
            // 生成令牌
             token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                    loginBody.getUuid());
        }else {
            token = loginService.login(String.valueOf(loginBody.getCardNumber()),loginBody.getPassword(),loginBody.getCode(),loginBody.getUuid());
        }
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo()
    {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        if (user==null){
            SysOrdinaryUser ordinaryUser = SecurityUtils.getLoginUser().getOrdinaryUser();
            Set<String> permissions = permissionService.getMenuPermission(ordinaryUser);
            AjaxResult ajax = AjaxResult.success();
            ajax.put("user", ordinaryUser);
            ajax.put("roles",roleService.selectRolePermissionByUserId(ordinaryUser.getUserId()));
            ajax.put("permissions", permissions);
            return ajax;
        }
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> perms = new HashSet<>();
        perms.add("*:*:*");
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", perms);
        return ajax;
    }

    /**
     * 获取路由信息
     * 
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters()
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
