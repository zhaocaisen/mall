package com.macro.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.UmsEnterpriseMapper;
import com.macro.mall.mapper.UmsResourceMapper;
import com.macro.mall.model.UmsEnterprise;
import com.macro.mall.model.UmsEnterpriseExample;
import com.macro.mall.model.UmsResource;
import com.macro.mall.model.UmsResourceExample;
import com.macro.mall.service.UmsAdminCacheService;
import com.macro.mall.service.UmsEnterpriseService;
import com.macro.mall.service.UmsResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 后台资源管理Service实现类
 * Created by macro on 2020/2/2.
 */
@Service
public class UmsEnterpriseServiceImpl implements UmsEnterpriseService {
    @Resource
    private UmsEnterpriseMapper enterpriseMapper;
    @Autowired
    private UmsAdminCacheService adminCacheService;
    @Override
    public int create(UmsEnterprise umsEnterprise) {
        umsEnterprise.setCreateTime(new Date());
        return enterpriseMapper.insert(umsEnterprise);
    }

    @Override
    public int update(Long id, UmsEnterprise umsEnterprise) {
        umsEnterprise.setId(id);
        int count = enterpriseMapper.updateByPrimaryKeySelective(umsEnterprise);
        return count;
    }

    @Override
    public UmsEnterprise getItem(Long id) {
        return enterpriseMapper.selectByPrimaryKey(id);
    }

    @Override
    public int delete(Long id) {
        int count = enterpriseMapper.deleteByPrimaryKey(id);
        adminCacheService.delResourceListByResource(id);
        return count;
    }

    @Override
    public List<UmsEnterprise> list(String nameKeyword, String codeKeyword, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum,pageSize);
        UmsEnterpriseExample example = new UmsEnterpriseExample();
        UmsEnterpriseExample.Criteria criteria = example.createCriteria();
        if(StrUtil.isNotEmpty(nameKeyword)){
            criteria.andNameLike('%'+nameKeyword+'%');
        }
        if(StrUtil.isNotEmpty(codeKeyword)){
            criteria.andCodeEqualTo(codeKeyword);
        }
        return enterpriseMapper.selectByExample(example);
    }

    @Override
    public List<UmsEnterprise> listAll() {
        return enterpriseMapper.selectByExample(new UmsEnterpriseExample());
    }
}
