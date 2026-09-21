package com.github.nrfr.manager

import android.content.Context
import android.os.Build
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import com.github.nrfr.model.SimCardInfo

/**
 * 运营商配置管理（应用进程侧门面）。
 * 需系统权限的读取/覆盖/还原操作全部委托给运行在 root 进程的 [RootManager.service]，
 * SIM 卡枚举与运营商名读取在应用进程内用普通 API 完成。
 */
object CarrierConfigManager {

    fun getSimCards(context: Context): List<SimCardInfo> {
        val simCards = mutableListOf<SimCardInfo>()
        val subId1 = SubscriptionManager.getSubId(0)
        val subId2 = SubscriptionManager.getSubId(1)

        if (subId1 != null) {
            val config1 = getCurrentConfig(subId1[0])
            simCards.add(SimCardInfo(1, subId1[0], getCarrierNameBySubId(context, subId1[0]), config1))
        }
        if (subId2 != null) {
            val config2 = getCurrentConfig(subId2[0])
            simCards.add(SimCardInfo(2, subId2[0], getCarrierNameBySubId(context, subId2[0]), config2))
        }

        return simCards
    }

    @Suppress("UNCHECKED_CAST")
    private fun getCurrentConfig(subId: Int): Map<String, String> {
        return try {
            val raw = RootManager.service?.getCurrentConfig(subId) ?: return emptyMap()
            val result = mutableMapOf<String, String>()
            for ((k, v) in raw) {
                if (k is String && v is String) result[k] = v
            }
            result
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private fun getCarrierNameBySubId(context: Context, subId: Int): String {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            ?: return ""

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10 及以上使用新 API
                telephonyManager.getNetworkOperatorName(subId)
            } else {
                // Android 8-9 使用反射获取运营商名称
                val createForSubscriptionId = TelephonyManager::class.java.getMethod(
                    "createForSubscriptionId",
                    Int::class.javaPrimitiveType
                )
                val subTelephonyManager = createForSubscriptionId.invoke(telephonyManager, subId) as TelephonyManager
                subTelephonyManager.networkOperatorName
            }
        } catch (e: Exception) {
            // 如果获取失败，回退到默认的 TelephonyManager
            telephonyManager.networkOperatorName
        }
    }

    fun setCarrierConfig(subId: Int, countryCode: String?, carrierName: String? = null) {
        val service = RootManager.service
            ?: throw IllegalStateException("Root 服务未连接")
        service.setCarrierConfig(subId, countryCode, carrierName)
    }

    fun resetCarrierConfig(subId: Int) {
        val service = RootManager.service
            ?: throw IllegalStateException("Root 服务未连接")
        service.resetCarrierConfig(subId)
    }
}
