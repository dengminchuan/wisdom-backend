//-*- coding =utf-8 -*-
//@Time : 2023/8/20
//@Author: 邓闽川
//@File  ExecMessage.java
//@software:IntelliJ IDEA
package com.deve.wisdom.model.enums;

public enum ExecMessage {
    WAIT("wait"),
    RUNNING("running"),
    FAILED("failed"),
    SUCCEED("succeed"),
    ;

    private final String execMessage;


    ExecMessage(String execMessage){
        this.execMessage = execMessage;
    }
}
