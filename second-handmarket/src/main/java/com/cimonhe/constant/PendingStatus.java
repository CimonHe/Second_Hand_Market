package com.cimonhe.constant;

//审核状态
public class PendingStatus {

    private PendingStatus(){}

    //待审核
    public static final int PENDING = 0;

    //审核通过
    public static final int PASS = 1;

    //审核不通过
    public static final int NO_PASS = 2;

}
