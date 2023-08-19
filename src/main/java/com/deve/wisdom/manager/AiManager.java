//-*- coding =utf-8 -*-
//@Time : 2023/8/18
//@Author: 邓闽川
//@File  AiManager.java
//@software:IntelliJ IDEA
package com.deve.wisdom.manager;

import com.deve.wisdom.common.ErrorCode;
import com.deve.wisdom.exception.BusinessException;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AiManager {
    @Resource
    private YuCongMingClient yuCongMingClient;
    public String doChat(Long modelId, String message){
        DevChatRequest devChatRequest = new DevChatRequest();
        devChatRequest.setModelId(modelId);
        devChatRequest.setMessage(message);
        BaseResponse<DevChatResponse> response = yuCongMingClient.doChat(devChatRequest);
        if(response==null||response.getData()==null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"chat response is null");
        }

        return response.getData().getContent();
    }
}
