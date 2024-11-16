package com.macro.mall.controller;

import com.macro.mall.bo.AdminUserDetails;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsAdmin;
import com.macro.mall.model.UmsEnterprise;
import com.macro.mall.model.UmsResource;
import com.macro.mall.security.component.DynamicSecurityMetadataSource;
import com.macro.mall.service.UmsEnterpriseService;
import com.macro.mall.service.UmsResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * 企业信息管理Controller
 * Created by zhaocaisen on 2024/11/16.
 */
@Controller
@Api(tags = "UmsEnterpriseController")
@Tag(name = "UmsEnterpriseController", description = "后台企业管理")
@RequestMapping("/enterprise")
public class UmsEnterpriseController {

    @Autowired
    private UmsEnterpriseService enterpriseService;
    @Autowired
    private DynamicSecurityMetadataSource dynamicSecurityMetadataSource;

    @ApiOperation("添加企业信息")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody UmsEnterprise umsEnterprise) {
        int count = enterpriseService.create(umsEnterprise);
        dynamicSecurityMetadataSource.clearDataSource();
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("修改企业信息")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id,
                               @RequestBody UmsEnterprise umsEnterprise) {
        int count = enterpriseService.update(id, umsEnterprise);
        dynamicSecurityMetadataSource.clearDataSource();
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("根据ID获取资源详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<UmsEnterprise> getItem(@PathVariable Long id) {
        UmsEnterprise umsResource = enterpriseService.getItem(id);
        return CommonResult.success(umsResource);
    }

    @ApiOperation("根据ID删除后台资源")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        int count = enterpriseService.delete(id);
        dynamicSecurityMetadataSource.clearDataSource();
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("分页模糊查询企业信息")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsEnterprise>> list(@RequestParam(required = false) String nameKeyword,
                                                        @RequestParam(required = false) String codeKeyword,
                                                      @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<UmsEnterprise> enterpriseList = enterpriseService.list(nameKeyword, codeKeyword, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(enterpriseList));
    }

    @ApiOperation("查询所有企业信息列表")
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<UmsEnterprise>> listAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AdminUserDetails userDetails = (AdminUserDetails) authentication.getPrincipal();
        UmsAdmin username = userDetails.getUmsAdmin();
        List<UmsEnterprise> resourceList = enterpriseService.listAll();
        return CommonResult.success(resourceList);
    }
}
