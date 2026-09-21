// IRootService.aidl
package com.github.nrfr;

/**
 * Root 进程（libsu RootService，UID 0）暴露给应用进程的接口。
 * 运营商配置的读取/覆盖/还原都需系统级权限，全部在 root 进程内直连
 * ICarrierConfigLoader 内部 binder 完成，应用进程通过本 AIDL 调用。
 */
interface IRootService {
    /** 读取指定 subId 当前的覆盖配置（键为「国家码」「运营商名称」，无覆盖返回空 Map） */
    Map getCurrentConfig(int subId);

    /** 覆盖指定 subId 的国家码 / 运营商名称（传 null 表示该项不设置） */
    void setCarrierConfig(int subId, String countryCode, String carrierName);

    /** 还原指定 subId 的覆盖配置（清空所有覆盖项） */
    void resetCarrierConfig(int subId);
}
