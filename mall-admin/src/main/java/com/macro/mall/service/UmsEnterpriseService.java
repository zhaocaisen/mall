package com.macro.mall.service;

import com.macro.mall.model.UmsEnterprise;
import com.macro.mall.model.UmsResource;

import java.util.List;

/**
 * 后台资源管理Service
 * Created by macro on 2020/2/2.
 */
public interface UmsEnterpriseService {
    /**
     * 添加资源
     */
    int create(UmsEnterprise umsEnterprise);

    /**
     * 修改资源
     */
    int update(Long id, UmsEnterprise umsEnterprise);

    /**
     * 获取资源详情
     */
    UmsEnterprise getItem(Long id);

    /**
     * 删除资源
     */
    int delete(Long id);

    /**
     * 分页查询资源
     */
    List<UmsEnterprise> list(String nameKeyword,String codeKeyword,Integer pageSize, Integer pageNum);

    /**
     * 查询全部资源
     */
    List<UmsEnterprise> listAll();
}
