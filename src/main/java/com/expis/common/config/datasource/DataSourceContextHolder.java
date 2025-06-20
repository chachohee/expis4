package com.expis.common.config.datasource;

public class DataSourceContextHolder {

    public static void setDataSource(String dbCode) {
        DynamicRoutingDataSource.setDataSourceKey(dbCode);
    }

    public static void clearDataSource() {
        DynamicRoutingDataSource.clearDataSourceKey();
    }

}
