package com.expis.common.config.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    public static void setDataSourceKey(String key){
        CONTEXT_HOLDER.set(key);
    }

    public static String getDataSourceKey() { return CONTEXT_HOLDER.get(); }

    public static void clearDataSourceKey() {
        CONTEXT_HOLDER.remove();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return CONTEXT_HOLDER.get();
    }

}
