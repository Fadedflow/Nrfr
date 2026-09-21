package com.github.nrfr

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.nrfr.manager.RootManager
import com.github.nrfr.ui.screens.AboutScreen
import com.github.nrfr.ui.screens.MainScreen
import com.github.nrfr.ui.screens.RootNotReadyScreen
import com.github.nrfr.ui.theme.NrfrTheme
import com.topjohnwu.superuser.Shell
import org.lsposed.hiddenapibypass.HiddenApiBypass

class MainActivity : ComponentActivity() {
    private var isRootReady by mutableStateOf(false)
    private var showAbout by mutableStateOf(false)

    companion object {
        init {
            // libsu Shell 全局配置：挂载 mount master、10s 超时
            Shell.enableVerboseLogging = false
            Shell.setDefaultBuilder(
                Shell.Builder.create()
                    .setFlags(Shell.FLAG_MOUNT_MASTER)
                    .setTimeout(10)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 初始化 Hidden API 访问（root 进程读写运营商配置需访问隐藏 API）
        HiddenApiBypass.addHiddenApiExemptions("L")
        HiddenApiBypass.addHiddenApiExemptions("I")

        // 绑定 root 服务：触发 su 授权，成功后进入主界面
        bindRoot()

        setContent {
            NrfrTheme {
                if (showAbout) {
                    AboutScreen(onBack = { showAbout = false })
                } else if (isRootReady) {
                    MainScreen(onShowAbout = { showAbout = true })
                } else {
                    RootNotReadyScreen(onRetry = { bindRoot() })
                }
            }
        }
    }

    private fun bindRoot() {
        RootManager.bind { ready ->
            runOnUiThread {
                isRootReady = ready
                if (!ready) {
                    Toast.makeText(this, "需要 Root 权限才能运行", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RootManager.unbind()
    }
}
