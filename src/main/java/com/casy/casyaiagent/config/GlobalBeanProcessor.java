package com.casy.casyaiagent.config;

import com.casy.casyaiagent.constant.Global;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author linlin
 */
@Component  // 声明为 Spring Bean，自动对容器中所有 Bean 生效
public class GlobalBeanProcessor implements BeanPostProcessor {

    @Resource
    private ConfigurableApplicationContext applicationContext;

    @NotNull
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        Global.setSpringContext(applicationContext);
        return bean;  // 必须返回 Bean 实例（可以是原始对象或包装后的代理）
    }

    @NotNull
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//        // 可以对特定类型的 Bean 进行处理
//        if (bean instanceof MySpecialBean) {
//            System.out.println("后置处理: " + beanName);
//        }
        return bean;
    }
}