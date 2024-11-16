package com.macro.mall.common.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TenantContext {
    private static final Logger log = LoggerFactory.getLogger(TenantContext.class);
    private static ThreadLocal<Long> currentTenant = new ThreadLocal();

    public TenantContext() {
    }

    public static void setTenant(Long tenant) {
        log.debug(" setting tenant to " + tenant);
        currentTenant.set(tenant);
    }

    public static Long getTenant() {
        return (Long)currentTenant.get();
    }

    public static void clear() {
        currentTenant.remove();
    }
}
