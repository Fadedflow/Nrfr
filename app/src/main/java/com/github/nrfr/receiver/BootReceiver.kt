package com.github.nrfr.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.github.nrfr.data.ConfigStore
import com.github.nrfr.manager.RootManager

/**
 * 开机自启接收器：重启后重新应用用户保存的运营商配置。
 *
 * 因为 overrideConfig(persistent=false) 是内存态覆盖，重启后失效，
 * 所以开机时从 ConfigStore 读取保存的配置，通过 RootService 重新下发。
 *
 * 注意：libsu 的 RootService 绑定需要 su 权限，开机时 Magisk/KernelSU
 * 通常已就绪；若绑定失败则放弃，等待用户手动打开 App。
 */
class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "NrfrBoot"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != "android.intent.action.LOCKED_BOOT_COMPLETED"
        ) return

        val saved = ConfigStore.loadAll(context)
        if (saved.isEmpty()) {
            Log.d(TAG, "没有保存的配置，跳过")
            return
        }

        Log.d(TAG, "开机重设 ${saved.size} 个配置")

        // 异步绑定 root 服务并重设，避免阻塞广播
        Thread {
            try {
                // 等待系统启动完成，root 守护进程就绪
                Thread.sleep(10000)

                val latch = java.util.concurrent.CountDownLatch(1)
                var bound = false
                RootManager.bind { ready ->
                    bound = ready
                    latch.countDown()
                }
                // 最多等 30 秒
                latch.await(30, java.util.concurrent.TimeUnit.SECONDS)

                if (!bound) {
                    Log.w(TAG, "Root 服务绑定失败，放弃开机重设")
                    return@Thread
                }

                for (cfg in saved) {
                    try {
                        RootManager.service?.setCarrierConfig(
                            cfg.subId,
                            cfg.countryCode,
                            cfg.carrierName
                        )
                        Log.d(TAG, "已重设 subId=${cfg.subId} country=${cfg.countryCode}")
                    } catch (e: Exception) {
                        Log.w(TAG, "重设 subId=${cfg.subId} 失败: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "开机重设异常: ${e.message}")
            }
        }.start()
    }
}
