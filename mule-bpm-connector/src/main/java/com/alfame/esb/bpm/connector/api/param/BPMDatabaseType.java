package com.alfame.esb.bpm.connector.api.param;

public enum BPMDatabaseType {

    // https://www.flowable.org/docs/userguide/index.html#supporteddatabases
    h2, mysql, oracle, postgres, mssql, db2;

    public String getValue() {
        return this.toString();
    }

}