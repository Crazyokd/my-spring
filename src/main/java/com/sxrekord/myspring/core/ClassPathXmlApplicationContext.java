package com.sxrekord.myspring.core;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * @author Rekord
 * @date 2023/4/2 15:46
 */
public class ClassPathXmlApplicationContext implements ApplicationContext{
    private Map<String, Object> beanMap = new HashMap<>();

    public ClassPathXmlApplicationContext(String resource) {
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(ClassLoader.getSystemClassLoader().getResourceAsStream(resource));
            // 获取所有 bean 标签
            List<Node> beanNodes = document.selectNodes("//bean");
            // 遍历集合
            beanNodes.forEach(beanNode -> {
                Element beanEle = (Element)beanNode;

                String id = beanEle.attributeValue("id");
                String className = beanEle.attributeValue("class");
                try {
                    Class<?> clazz = Class.forName(className);
                    Constructor<?> defaultConstructor = clazz.getDeclaredConstructor();
                    Object bean = defaultConstructor.newInstance();
                    // 存储到map集合
                    beanMap.put(id, bean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // 为属性赋值
            beanNodes.forEach(beanNode -> {
                Element beanEle = (Element)beanNode;

                String id = beanEle.attributeValue("id");
                List<Element> propertyEles = beanEle.elements("property");
                propertyEles.forEach(propertyEle -> {
                    try {
                        String propertyName = propertyEle.attributeValue("name");
                        Class<?> propertyType = beanMap.get(id).getClass().getDeclaredField(propertyName).getType();
                        String setMethodName = "set" + propertyName.toUpperCase().charAt(0) + propertyName.substring(1);
                        Method setMethod = beanMap.get(id).getClass().getDeclaredMethod(setMethodName, propertyType);

                        String propertyValue = propertyEle.attributeValue("value");

                        String propertyRef = propertyEle.attributeValue("ref");
                        Object propertyVal = null;

                        if (propertyValue != null) {
                            String propertyTypeSimpleName = propertyType.getSimpleName();
                            switch (propertyTypeSimpleName) {
                                case "byte": case "Byte":
                                    propertyVal = Byte.valueOf(propertyValue);
                                    break;
                                case "short": case "Short":
                                    propertyVal = Short.valueOf(propertyValue);
                                    break;
                                case "int": case "Integer":
                                    propertyVal = Integer.valueOf(propertyValue);
                                    break;
                                case "long": case "Long":
                                    propertyVal = Long.valueOf(propertyValue);
                                    break;
                                case "float": case "Float":
                                    propertyVal = Float.valueOf(propertyValue);
                                    break;
                                case "double": case "Double":
                                    propertyVal = Double.valueOf(propertyValue);
                                    break;
                                case "boolean": case "Boolean":
                                    propertyVal = Boolean.valueOf(propertyValue);
                                    break;
                                case "char": case "Character":
                                    propertyVal = propertyValue.charAt(0);
                                    break;
                                case "String":
                                    propertyVal = propertyValue;
                                    break;
                            }
                            setMethod.invoke(beanMap.get(id), propertyVal);
                        }
                        if (propertyRef != null) {
                            setMethod.invoke(beanMap.get(id), beanMap.get(propertyRef));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getBean(String beanId) {
        return beanMap.get(beanId);
    }
}
