package com.deve.wisdom.manager;

import com.deve.wisdom.constant.AiConstant;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class AiManagerTest {
    @Resource
    private AiManager aiManager;
    @Test
    void testDoChat(){
        String s = aiManager.doChat(AiConstant.ECHARTS_MODEL_ID,"请问java是什么?");
        System.out.println(s);

    }

}