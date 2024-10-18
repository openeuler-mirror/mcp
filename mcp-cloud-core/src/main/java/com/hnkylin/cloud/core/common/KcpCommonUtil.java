package com.hnkylin.cloud.core.common;

import com.hnkylin.cloud.core.enums.ArchitectureType;
import com.hnkylin.cloud.core.enums.McArchitectureType;

import java.io.FileInputStream;
import java.util.Objects;
import java.util.Properties;

public class KcpCommonUtil {
    public static ArchitectureType changeToKcpArchitectureType(String plateformType) {

        if (Objects.equals(plateformType, McArchitectureType.X86_64.getName()) || Objects.equals(plateformType,
                McArchitectureType.LOWER_86_64.getName())) {
            return ArchitectureType.X86_64;
        }
        if (Objects.equals(plateformType, McArchitectureType.ARM.getName()) || Objects.equals(plateformType,
                McArchitectureType.AARCH64.name()) || Objects.equals(plateformType,
                McArchitectureType.AARCH64.getName())) {
            return ArchitectureType.ARM64;
        }
        if (Objects.equals(plateformType, McArchitectureType.MIPS.getName())) {
            return ArchitectureType.MIPS64;
        }
        if (Objects.equals(plateformType, McArchitectureType.sw_64.getName())) {
            return ArchitectureType.SW64;
        }
        return ArchitectureType.X86_64;
    }
    /**
     * 获取主备配置文件中值
     *
     * @param key
     * @return
     */
    public static String getHaConfigInfo(String filePath, String key) {
        String value = null;
        try {
            Properties p = new Properties();
            p.load(new FileInputStream(filePath));
            value = p.getProperty(key);
        } catch (Exception e) {
        }
        return value;
    }
}

