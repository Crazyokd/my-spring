package com.sxrekord.myspring;

import com.sxrekord.myspring.core.ApplicationContext;
import com.sxrekord.myspring.core.ClassPathXmlApplicationContext;
import org.junit.Test;

/**
 * @author Rekord
 * @date 2023/4/2 16:05
 */
public class MySpringTest {
    @Test
    public void testMySpring() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("my-spring.xml");
        Object userBean = applicationContext.getBean("userBean");
        Object addrBean = applicationContext.getBean("addrBean");

        System.out.println(userBean);
        System.out.println(addrBean);
    }
}
