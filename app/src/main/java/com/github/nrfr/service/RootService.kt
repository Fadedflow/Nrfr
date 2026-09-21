package com.github.nrfr.service

import android.content.Intent
import android.os.IBinder
import android.os.PersistableBundle
import android.telephony.CarrierConfigManager
import android.telephony.TelephonyFrameworkInitializer
import com.android.internal.telephony.ICarrierConfigLoader
import com.github.nrfr.IRootService
import org.lsposed.hiddenapibypass.HiddenApiBypass

/**
 * 以 root 权限（UID 0）运行的服务（libsu RootService）。
 * 运营商配置覆盖需 MODIFY_PHONE_STATE 系统权限，root 进程可直接绕过权限校验，
 * 因此这里直连 ICarrierConfigLoader 内部 binder，无需 Shizuku 的 binder 包装。
 */
class RootService : com.topjohnwu.superuser.ipc.RootService() {

    override fun onCreate() {
        super.onCreate()
        // :root 进程由 libsu 独立 fork，不会执行 MainActivity.onCreate，
        // 因此需在本进程内单独解除隐藏 API 限制，否则访问
        // ICarrierConfigLoader / overrideConfig 等隐藏 API 会被 ART 拦截，导致保存失败。
        HiddenApiBypass.addHiddenApiExemptions("L")
        HiddenApiBypass.addHiddenApiExemptions("I")
    }

    override fun onBind(intent: Intent): IBinder = RootIpc()

    private class RootIpc : IRootService.Stub() {

        /** 获取运营商配置加载器（root 进程直连系统 binder） */
        private fun carrierConfigLoader(): ICarrierConfigLoader {
            return ICarrierConfigLoader.Stub.asInterface(
                TelephonyFrameworkInitializer
                    .getTelephonyServiceManager()
                    .carrierConfigServiceRegisterer
                    .get()
            )
        }

        override fun getCurrentConfig(subId: Int): MutableMap<Any?, Any?> {
            val result = mutableMapOf<Any?, Any?>()
            try {
                val config = carrierConfigLoader()
                    .getConfigForSubId(subId, "com.github.nrfr") ?: return result

                // 国家码覆盖
                config.getString(CarrierConfigManager.KEY_SIM_COUNTRY_ISO_OVERRIDE_STRING)?.let {
                    result["国家码"] = it
                }

                // 运营商名称覆盖
                if (config.getBoolean(CarrierConfigManager.KEY_CARRIER_NAME_OVERRIDE_BOOL, false)) {
                    config.getString(CarrierConfigManager.KEY_CARRIER_NAME_STRING)?.let {
                        result["运营商名称"] = it
                    }
                }
            } catch (_: Exception) {
                // 读取失败返回空 Map，调用方按「无覆盖配置」处理
            }
            return result
        }

        override fun setCarrierConfig(subId: Int, countryCode: String?, carrierName: String?) {
            val bundle = PersistableBundle()

            // 国家码（2 位字母）
            if (!countryCode.isNullOrEmpty() && countryCode.length == 2) {
                bundle.putString(
                    CarrierConfigManager.KEY_SIM_COUNTRY_ISO_OVERRIDE_STRING,
                    countryCode.lowercase()
                )
            }

            // 运营商名称
            if (!carrierName.isNullOrEmpty()) {
                bundle.putBoolean(CarrierConfigManager.KEY_CARRIER_NAME_OVERRIDE_BOOL, true)
                bundle.putString(CarrierConfigManager.KEY_CARRIER_NAME_STRING, carrierName)
            }

            carrierConfigLoader().overrideConfig(subId, bundle, true)
        }

        override fun resetCarrierConfig(subId: Int) {
            carrierConfigLoader().overrideConfig(subId, null, true)
        }
    }
}
