package com.sxrekord.myspring.core;

/**
 * @author Rekord
 * @date 2023/4/2 15:45
 */
public interface ApplicationContext {
    /**
     *
     * @param beanId
     * @return
     */
    Object getBean(String beanId);
}
