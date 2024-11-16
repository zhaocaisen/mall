package com.macro.mall.mapper;

import com.macro.mall.model.UmsEnterprise;
import com.macro.mall.model.UmsEnterpriseExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UmsEnterpriseMapper {
    long countByExample(UmsEnterpriseExample example);

    int deleteByExample(UmsEnterpriseExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsEnterprise row);

    int insertSelective(UmsEnterprise row);

    List<UmsEnterprise> selectByExample(UmsEnterpriseExample example);

    UmsEnterprise selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") UmsEnterprise row, @Param("example") UmsEnterpriseExample example);

    int updateByExample(@Param("row") UmsEnterprise row, @Param("example") UmsEnterpriseExample example);

    int updateByPrimaryKeySelective(UmsEnterprise row);

    int updateByPrimaryKey(UmsEnterprise row);
}