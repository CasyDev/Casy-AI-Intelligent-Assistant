package com.casy.casyaiagent.util;

import com.casy.casyaiagent.constant.Global;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author Administrator
 * @version 1.0
 * @description: 通过木类实现对全局{@link Global}中变量的springContext的设置
 * AppicationcontextInitializer 接口提供了一种机制,在创建 Appicationcontext实例之后bean被加载之前，对AppLioationontext进行自定义或初始化
 * 在SpringApplication执行流程中, initialize(包实现力法的类)会先与{@Linkplain ConfigurableApplicationContext#refresh()} 执行
 * 这个将其赋值给全局的springContext变量来简化组件的注入
 * @date 2026/3/7 16:19
 */
@Slf4j
public class GlobalSpringContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
        log.info("获取applicationContext: {}", applicationContext);
        Global.setSpringContext(applicationContext);
    }
}
