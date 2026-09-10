package com.casy.casyaiagent.config;

import cn.hutool.core.io.FileUtil;
import com.casy.casyaiagent.constant.FileConstant;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 将配置中的文件根目录写入 FileConstant，供 PDF / 文件 / 下载工具使用。
 */
@Slf4j
@Configuration
public class FileStorageConfig {

    @Value("${casy.file.save-dir:${user.dir}/tmp}")
    private String saveDir;

    @PostConstruct
    public void init() {
        FileConstant.setFileSaveDir(saveDir);
        FileUtil.mkdir(saveDir);
        log.info("文件保存目录: {}", FileConstant.getFileSaveDir());
    }
}
