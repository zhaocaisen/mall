package com.macro.mall.config;

import cn.hutool.core.util.ObjectUtil;
import com.macro.mall.bo.AdminUserDetails;
import com.macro.mall.common.config.TenantContext;
import com.macro.mall.common.util.oConvertUtils;
import com.macro.mall.model.UmsAdmin;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Properties;

/**
 * mybatis拦截器，自动注入创建人、创建时间、修改人、修改时间
 * @Author scott
 * @Date  2019-01-19
 *
 */
@Slf4j
@Component
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class MybatisInterceptor implements Interceptor {

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
		String sqlId = mappedStatement.getId();
		log.debug("------sqlId------" + sqlId);
		SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
		Object parameter = invocation.getArgs()[1];
		log.debug("------sqlCommandType------" + sqlCommandType);

		if (parameter == null) {
			return invocation.proceed();
		}
		if (SqlCommandType.INSERT == sqlCommandType) {
			UmsAdmin sysUser = this.getLoginUser();
			Field[] fields = oConvertUtils.getAllFields(parameter);
			for (Field field : fields) {
				log.debug("------field.name------" + field.getName());
				try {
					if ("createBy".equals(field.getName())) {
						field.setAccessible(true);
						Object localCreateBy = field.get(parameter);
						field.setAccessible(false);
						if (localCreateBy == null || "".equals(localCreateBy)) {
							if (sysUser != null) {
								// 登录人账号
								field.setAccessible(true);
								field.set(parameter, sysUser.getUsername());
								field.setAccessible(false);
							}
						}
					}
					// 注入创建时间
					if ("createTime".equals(field.getName())) {
						field.setAccessible(true);
						Object localCreateDate = field.get(parameter);
						field.setAccessible(false);
						if (localCreateDate == null || "".equals(localCreateDate)) {
							field.setAccessible(true);
							field.set(parameter, new Date());
							field.setAccessible(false);
						}
					}

					//------------------------------------------------------------------------------------------------
					//注入租户ID（是否开启系统管理模块的多租户数据隔离【SAAS多租户模式】）
					if ("tenant".equals(field.getName())) {
						field.setAccessible(true);
						Object localTenantId = field.get(parameter);
						field.setAccessible(false);
						if (localTenantId == null) {
							field.setAccessible(true);

							Long tenantId = TenantContext.getTenant();
							//如果通过线程获取租户ID为空，则通过当前请求的request获取租户（shiro排除拦截器的请求会获取不到租户ID）
							if(ObjectUtil.isEmpty(tenantId)){
								try {
									tenantId = TenantContext.getTenant();
								} catch (Exception e) {
									//e.printStackTrace();
								}
							}

							if (field.getType().equals(String.class)) {
								// 字段类型为String
								field.set(parameter, tenantId);
							} else {
								// 字段类型不是String
								field.set(parameter,TenantContext.getTenant());
							}
							field.setAccessible(false);
						}
					}
					//------------------------------------------------------------------------------------------------
					
				} catch (Exception e) {
				}
			}
		}
		if (SqlCommandType.UPDATE == sqlCommandType) {
			UmsAdmin sysUser = this.getLoginUser();
			Field[] fields = null;
			if (parameter instanceof ParamMap) {
				ParamMap<?> p = (ParamMap<?>) parameter;
				//update-begin-author:scott date:20190729 for:批量更新报错issues/IZA3Q--
                String et = "et";
				if (p.containsKey(et)) {
					parameter = p.get(et);
				} else {
					parameter = p.get("param1");
				}
				//update-end-author:scott date:20190729 for:批量更新报错issues/IZA3Q-

				//update-begin-author:scott date:20190729 for:更新指定字段时报错 issues/#516-
				if (parameter == null) {
					return invocation.proceed();
				}
				//update-end-author:scott date:20190729 for:更新指定字段时报错 issues/#516-

				fields = oConvertUtils.getAllFields(parameter);
			} else {
				fields = oConvertUtils.getAllFields(parameter);
			}

			for (Field field : fields) {
				log.debug("------field.name------" + field.getName());
				try {
					if ("updateBy".equals(field.getName())) {
						//获取登录用户信息
						if (sysUser != null) {
							// 登录账号
							field.setAccessible(true);
							field.set(parameter, sysUser.getUsername());
							field.setAccessible(false);
						}
					}
					if ("updateTime".equals(field.getName())) {
						field.setAccessible(true);
						field.set(parameter, new Date());
						field.setAccessible(false);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		// TODO Auto-generated method stub
	}

	//update-begin--Author:scott  Date:20191213 for：关于使用Quzrtz 开启线程任务， #465
    /**
     * 获取登录用户
     * @return
     */
	private UmsAdmin getLoginUser() {
		UmsAdmin sysUser = null;
		try {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			AdminUserDetails userDetails = (AdminUserDetails) authentication.getPrincipal();
			sysUser = userDetails.getUmsAdmin();
		} catch (Exception e) {
			sysUser = null;
		}
		return sysUser;
	}
	//update-end--Author:scott  Date:20191213 for：关于使用Quzrtz 开启线程任务， #465

}
