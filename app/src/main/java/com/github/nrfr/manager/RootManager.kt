package com.github.nrfr.manager

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.github.nrfr.IRootService
import com.github.nrfr.service.RootService
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService as LibsuRootService

/**
 * Root 服务连接管理（应用进程侧）。
 * 通过 libsu 绑定运行在 UID 0 的 [RootService]，持有跨进程 [IRootService] 句柄，
 * 供 [CarrierConfigManager] 调用需系统权限的运营商配置操作。
 */
object RootManager {

    @Volatile
    var service: IRootService? = null
        private set

    /** 服务是否已连接就绪 */
    val isReady: Boolean
        get() = service != null

    private var onReadyChanged: ((Boolean) -> Unit)? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            service = IRootService.Stub.asInterface(binder)
            onReadyChanged?.invoke(true)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
            onReadyChanged?.invoke(false)
        }
    }

    /** 设备是否已授予 root（su 可用） */
    fun hasRootAccess(): Boolean = Shell.getShell().isRoot

    /**
     * 绑定 root 服务。首次调用会触发 su 授权弹窗；
     * 授权失败或无 root 时 [callback] 收到 false。
     */
    fun bind(callback: (Boolean) -> Unit) {
        onReadyChanged = callback
        if (service != null) {
            callback(true)
            return
        }
        Shell.getShell { shell ->
            if (!shell.isRoot) {
                callback(false)
                return@getShell
            }
            val intent = Intent().setClassName(
                "com.github.nrfr",
                RootService::class.java.name
            )
            LibsuRootService.bind(intent, connection)
        }
    }

    /** 解绑 root 服务（应用退出时调用） */
    fun unbind() {
        val intent = Intent().setClassName(
            "com.github.nrfr",
            RootService::class.java.name
        )
        try {
            LibsuRootService.unbind(connection)
        } catch (_: Exception) {
        }
        service = null
    }
}
