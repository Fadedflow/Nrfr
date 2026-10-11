package com.github.nrfr.data

import android.content.Context
import android.content.SharedPreferences

/**
 * 持久化保存用户的运营商配置，重启后由 BootReceiver 重新应用。
 * 因为 overrideConfig(persistent=false) 是内存态覆盖，重启后失效，
 * 所以必须在本地存一份，开机时重设。
 */
object ConfigStore {

    private const val PREFS_NAME = "nrfr_config"
    private const val KEY_PREFIX_SUBID = "sub_"

    data class SavedConfig(
        val subId: Int,
        val countryCode: String?,
        val carrierName: String?
    )

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** 保存某 subId 的配置；countryCode 和 carrierName 都为空时删除该条 */
    fun save(context: Context, subId: Int, countryCode: String?, carrierName: String?) {
        val key = KEY_PREFIX_SUBID + subId
        val editor = prefs(context).edit()
        if (countryCode.isNullOrEmpty() && carrierName.isNullOrEmpty()) {
            editor.remove(key + "_country")
            editor.remove(key + "_carrier")
        } else {
            editor.putString(key + "_country", countryCode ?: "")
            editor.putString(key + "_carrier", carrierName ?: "")
        }
        editor.apply()
    }

    /** 读取某 subId 的配置，没有返回 null */
    fun load(context: Context, subId: Int): SavedConfig? {
        val key = KEY_PREFIX_SUBID + subId
        val p = prefs(context)
        // 用 contains 判断是否存在，避免 "" 和 null 混淆
        if (!p.contains(key + "_country") && !p.contains(key + "_carrier")) return null
        val country = p.getString(key + "_country", "")?.takeIf { it.isNotEmpty() }
        val carrier = p.getString(key + "_carrier", "")?.takeIf { it.isNotEmpty() }
        if (country == null && carrier == null) return null
        return SavedConfig(subId, country, carrier)
    }

    /** 读取所有已保存的配置 */
    fun loadAll(context: Context): List<SavedConfig> {
        val result = mutableListOf<SavedConfig>()
        val all = prefs(context).all
        val subIds = all.keys
            .filter { it.startsWith(KEY_PREFIX_SUBID) }
            .mapNotNull { it.removePrefix(KEY_PREFIX_SUBID).substringBefore("_").toIntOrNull() }
            .distinct()
        for (subId in subIds) {
            load(context, subId)?.let { result.add(it) }
        }
        return result
    }

    /** 清除某 subId 的配置 */
    fun clear(context: Context, subId: Int) {
        val key = KEY_PREFIX_SUBID + subId
        prefs(context).edit()
            .remove(key + "_country")
            .remove(key + "_carrier")
            .apply()
    }
}
