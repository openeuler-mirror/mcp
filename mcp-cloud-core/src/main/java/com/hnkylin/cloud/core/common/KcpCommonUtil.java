package com.hnkylin.cloud.core.common;

import com.hnkylin.cloud.core.enums.ArchitectureType;
import com.hnkylin.cloud.core.enums.McArchitectureType;

import java.util.Objects;

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
}

