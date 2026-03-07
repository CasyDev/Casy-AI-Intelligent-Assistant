package com.casy.casyaiagent.constant;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Objects;

/**
 * @author linlin
 * @version 1.0
 * @description: 全局变量
 * @date 2026/3/7 16:16
 */
public class Global {

    private static final Logger log = (Logger) LoggerFactory.getLogger(Global.class);

    private static ConfigurableApplicationContext springContext;

    public static void setSpringContext(ConfigurableApplicationContext springContext) {
        if (Objects.isNull(Global.springContext)) {
            Global.springContext = springContext;
        }
    }

    public static ConfigurableApplicationContext getSpringContext() {
//        return Optional.ofNullable(springContext).orElseThrow(() -> new IllegalArgumentException("获取Spring上下文异常！"));
        return Objects.requireNonNull(springContext, "获取Spring上下文异常！");
    }

    // 返回上下文中的Bean对象
    public static <T> T getBean(Class<T> requiredType) {
        return Global.getSpringContext().getBean(requiredType);
    }

    // 工具方法：按名称获取Bean（通用）
    public static Object getBean(String beanName) {
        try {
            // 核心方法：按名称获取Bean
            return springContext.getBean(beanName);
        } catch (NoSuchBeanDefinitionException e) {
            log.info("不存在名称为【" + beanName + "】的Bean：" + e.getMessage());
            throw e;
        }
    }

    // 工具方法：按名称+类型获取Bean（更安全，无需强转）
    public static <T> T getBean(String beanName, Class<T> type) {
        try {
            // 推荐：按名称+类型获取，避免强转和类型不匹配问题
            return springContext.getBean(beanName, type);
        } catch (NoSuchBeanDefinitionException | BeanNotOfRequiredTypeException e) {
            log.info("获取Bean失败：" + e.getMessage());
            throw e;
        }
    }

}
