package com.alfame.esb.bpm.module.api.param;

public enum BPMDatabaseType {

    // https://www.flowable.org/docs/userguide/index.html#supporteddatabases
    H2, HSQL, MYSQL, ORACLE, POSTGRES, MSSQL, DB2;

    public String getValue() {
        return this.toString();
    }

    public String getFlowableTypeValue() {
        String flowableDatabaseType = null;

        switch (this) {
            case H2: {
                flowableDatabaseType = "h2";
                break;
            }
            case HSQL: {
                flowableDatabaseType = "hsql";
                break;
            }
            case MYSQL: {
                flowableDatabaseType = "mysql";
                break;
            }
            case ORACLE: {
                flowableDatabaseType = "oracle";
                break;
            }
            case POSTGRES: {
                flowableDatabaseType = "postgres";
                break;
            }
            case MSSQL: {
                flowableDatabaseType = "mssql";
                break;
            }
            case DB2: {
                flowableDatabaseType = "db2";
                break;
            }
        }

        return flowableDatabaseType;
    }

}