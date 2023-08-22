//-*- coding =utf-8 -*-
//@Time : 2023/8/21
//@Author: 邓闽川
//@File  tableIdTest.java
//@software:IntelliJ IDEA
package com.deve.wisdom.annotation;

import com.deve.wisdom.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class tableIdTest {
    @Test
    void testTableId(){
        User user = new User();
        System.out.println(user.getId());
    }
}

