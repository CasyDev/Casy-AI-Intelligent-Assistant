package com.casy.casyaiagent.constant;

import java.nio.file.Path;

/**
 * 文件保存目录。默认是进程工作目录下的 tmp，可通过 casy.file.save-dir 或环境变量 CASY_FILE_SAVE_DIR 覆盖。
 */
public final class FileConstant {

    private static volatile String fileSaveDir = Path.of(System.getProperty("user.dir"), "tmp").toString();

    private FileConstant() {
    }

    public static String getFileSaveDir() {
        return fileSaveDir;
    }

    public static void setFileSaveDir(String dir) {
        if (dir != null && !dir.isBlank()) {
            fileSaveDir = dir.trim();
        }
    }
}
