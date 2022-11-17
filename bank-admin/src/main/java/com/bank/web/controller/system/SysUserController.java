package com.bank.web.controller.system;

import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.bank.common.annotation.Log;
import com.bank.common.constant.UserConstants;
import com.bank.common.core.controller.BaseController;
import com.bank.common.core.domain.AjaxResult;
import com.bank.common.core.domain.entity.SysRole;
import com.bank.common.core.domain.entity.SysUser;
import com.bank.common.enums.BusinessType;
import com.bank.common.utils.SecurityUtils;
import com.bank.common.utils.StringUtils;
import com.bank.common.utils.poi.ExcelUtil;
import com.bank.system.service.ISysPostService;
import com.bank.system.service.ISysRoleService;
import com.bank.system.service.ISysUserService;

/**
 * 用户信息
 * 
 * @author ruoyi
 */
@RestController
public class SysUserController extends BaseController
{
}
