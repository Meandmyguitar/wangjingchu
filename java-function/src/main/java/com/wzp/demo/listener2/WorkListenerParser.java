
package com.wzp.demo.listener2;

import com.wzp.demo.listener1.IWorkListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class WorkListenerParser implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;
    private IWorkListener iWorkListener;

    public void setWorkListener(IWorkListener iWorkListener) {
        this.iWorkListener = iWorkListener;
    }

    public void work(ListenerEvent event) {
        iWorkListener.onStart(event);
    }

    @Override
    public void afterPropertiesSet() {
        Map<String, Object> listenerBeans = getExpectListenerBeans(Controller.class, RestController.class, Service.class, Component.class);
        for (Object listener : listenerBeans.values()) {
            for (Method method : listener.getClass().getDeclaredMethods()) {
                if (!method.isAnnotationPresent(WorkListener.class)) {
                    continue;
                }
                setWorkListener(event -> {
                    try {
                        method.invoke(listener, event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    /**
     * 找到有可能使用注解的bean
     *
     * @param annotationTypes 需要进行扫描的类级注解类型
     * @return 扫描到的beans的map
     */
    private Map<String, Object> getExpectListenerBeans(Class<? extends Annotation>... annotationTypes) {
        Map<String, Object> listenerBeans = new LinkedHashMap<>();
        for (Class<? extends Annotation> annotationType : annotationTypes) {
            Map<String, Object> annotatedBeansMap = applicationContext.getBeansWithAnnotation(annotationType);
            listenerBeans.putAll(annotatedBeansMap);
        }
        return listenerBeans;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}