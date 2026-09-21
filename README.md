<div align="center">
  <h1>Nrfr</h1>
  <p>🌍 基于 Root 的 SIM 卡国家码修改工具，让你的网络更自由</p>

  <p>
    <img src="https://img.shields.io/badge/platform-Android-3DDC84?logo=android" alt="Platform">
    <img src="https://img.shields.io/badge/Android-8+-3DDC84?logo=android" alt="Android Version">
    <img src="https://img.shields.io/badge/Go-1.21+-00ADD8?logo=go" alt="Go Version">
    <img src="https://img.shields.io/badge/React-19-61DAFB?logo=react" alt="React Version">
    <img src="https://img.shields.io/badge/TypeScript-5-3178C6?logo=typescript" alt="TypeScript Version">
    <img src="https://img.shields.io/badge/Tailwind-3-38B2AC?logo=tailwind-css" alt="Tailwind Version">
    <img src="https://img.shields.io/badge/Wails-2-000000?logo=wails" alt="Wails Version">
  </p>

  <p>
      <img src="https://img.shields.io/github/stars/Ackites/Nrfr?style=flat" alt="Stars">
      <img src="https://img.shields.io/github/forks/Ackites/Nrfr?style=flat" alt="Forks">
      <img src="https://img.shields.io/github/issues/Ackites/Nrfr?style=flat" alt="Issues">
      <img src="https://img.shields.io/github/last-commit/Ackites/Nrfr?style=flat" alt="Last Commit">
      <img src="https://img.shields.io/github/release/Ackites/Nrfr?style=flat" alt="Release">
      <img src="https://img.shields.io/github/downloads/Ackites/Nrfr/total?style=flat" alt="Downloads">
      <img src="https://img.shields.io/github/license/Ackites/Nrfr?style=flat" alt="License">
      <img src="https://img.shields.io/badge/Follow-@actkites-1DA1F2?logo=x&style=flat" alt="Follow on X">
  </p>

  <div style="display: flex; justify-content: center; align-items: center; gap: 20px; margin: 20px 0;">
    <img src="docs/images/client.png" alt="快速启动工具界面" width="500">
    <img src="docs/images/app.png" alt="Android 应用界面" width="220">
  </div>
   <br>
</div>

Nrfr 是一款强大的 SIM 卡国家码修改工具，通过 Root 权限调用 Android 系统级接口修改 SIM 卡国家码。本项目基于系统原生 API 实现，不依赖
Xposed 等模块框架，仅在 root 进程内直连系统级接口实现功能。通过修改国家码，你可以：

- 🌏 解锁运营商限制，使用更多本地功能
- 🔓 突破某些区域限制的应用和服务
- 🛠️ 解决国际漫游时的兼容性问题
- 🌐 帮助使用海外 SIM 卡获得更好的本地化体验
- ⚙️ 解决部分应用识别 SIM 卡地区错误的问题

## 📱 使用案例

### 运营商配置优化

- 手机无法正确识别运营商配置
- 某些运营商特定功能无法使用
- 网络配置与当地运营商不匹配

### 运营商参数适配

- 运营商功能配置不完整
- 网络参数与运营商默认配置不匹配
- 运营商特定服务无法正常启用

### 漫游网络识别

- 漫游时运营商名称显示异常
- 网络配置与漫游地运营商不匹配
- 运营商特定功能无法使用

### TikTok 区域限制解除

- TikTok 网络错误
- 无法正常使用 TikTok 的完整功能

### Samsung Health 区域限制解除

- 无法通过 Samsung Health 的首次 SIM 卡检测
- 无法同步健康数据
- 无法正常使用 Samsung Health 的完整功能

你可以：

1. 使用 Nrfr 修改 SIM 卡国家码为支持的地区（如 JP、US 等）
2. 重新打开 TikTok，就可以正常使用了

## 💡 实现原理

Nrfr 通过调用 Android 系统级 API（CarrierConfigLoader）修改系统内的运营商配置参数，而**不是直接修改 SIM 卡**。这种实现方式：

- 完全在系统层面工作，不会对 SIM 卡本身进行任何修改或造成损坏
- 仅改变系统对 SIM 卡信息的读取方式
- 基于 Android 原生 API 实现，不依赖 Xposed 等模块框架
- 通过 Root 权限（libsu RootService）在 UID 0 进程内直连 `ICarrierConfigLoader` 提供必要的系统级调用能力
- 所有修改都是可逆的，随时可以还原

## ✨ 特性

- 🔒 安全可靠
   - 不修改系统文件
   - 不影响系统稳定性
   - 不会对 SIM 卡造成任何影响
- 🔄 功能完善
   - 支持随时还原修改
   - 支持双卡设备，可分别配置
- 🚀 简单易用
   - 智能检测设备和 SIM 卡状态
   - 简洁优雅的用户界面
   - 轻量且高效，安装包体积小

> ⚠️ **关于重启后的行为**：Root 进程（UID 0）不在框架允许写入持久化配置的 UID 白名单内，因此修改采用内存态覆盖，**重启设备后会失效**，需重新打开 Nrfr 应用再设置一次。

## ⚠️ 注意事项

- 需要已 Root 的设备（Magisk / KernelSU / APatch），首次运行需在 Root 管理器中授予 Nrfr Root 权限
- 修改国家码可能会影响运营商服务，请谨慎操作
- 修改为内存态覆盖，**重启设备后失效**，需重新打开应用再设置一次
- 部分设备可能不支持修改国家码
- 如需还原设置，请使用应用内的还原功能

## 🚀 快速开始

需要一部已 Root 的设备（Magisk / KernelSU / APatch）。从 Release 页面下载最新的 APK 安装包，按以下步骤操作：

1. 安装 Nrfr
    - 下载并安装最新的 Nrfr APK
    - 首次启动时，在弹出的 Root 授权弹窗中允许 Nrfr 获取 Root 权限

2. 修改国家码
    - 在手机上打开 Nrfr 应用
    - 选择需要修改的 SIM 卡
    - 设置目标国家码（如可选自定义运营商名称）
    - 应用修改，设置立即生效

> ⚠️ 修改为内存态覆盖，**重启设备后会失效**，重启后需重新打开 Nrfr 再设置一次。如需还原，请使用应用内的还原功能。

## 📦 构建

项目包含两个部分：快速启动工具（桌面端）和手机应用（Android）。

### 快速启动工具 (nrfr-client)

```bash
# 进入客户端目录
cd nrfr-client

# 安装依赖
npm install

# 开发模式
wails dev

# 构建发布版本
wails build
```

### Android 应用 (app)

```bash
# 进入 Android 应用目录
cd app

# 使用 Gradle 构建 Debug 版本
./gradlew assembleDebug
```

构建完成后，可以在以下位置找到生成的文件：

- 快速启动工具: `nrfr-client/build/bin/`
- Android 应用: `app/build/outputs/apk/`

## 🧩 Root 版实现要点（开发者向）

> Android 应用已从 Shizuku（免 Root）迁移为 Root（libsu `RootService`，UID 0 独立 `:root` 进程）直连 `ICarrierConfigLoader`。
> 桌面快速启动工具 `nrfr-client` 目前仍走 Shizuku 自动化流程，未改动。

迁移中踩过三个坑，都是「Shizuku 跑在主进程、框架已完整初始化」时被掩盖、换到 libsu 精简 `:root` 进程后才暴露的问题。改动系统级 telephony 调用时务必注意：

1. **隐藏 API 豁免要在 `:root` 进程内单独做。**
   `:root` 由 libsu 独立 fork，不执行 `MainActivity.onCreate`，只在 Activity 里调 `HiddenApiBypass.addHiddenApiExemptions` 对该进程无效。必须在 `RootService.onCreate` 里再调一次，否则 targetSdk 34 下访问 `ICarrierConfigLoader` / `overrideConfig` 被 ART 拦截，表现为「保存失败」。

2. **`:root` 精简进程里 `TelephonyFrameworkInitializer.getTelephonyServiceManager()` 返回 null。**
   该静态字段由完整框架初始化流程赋值，精简 `app_process` 没跑到，直接调用会 NPE（`invoke virtual method on null ... TelephonyServiceManager`）。改走 `ServiceManager.getService("carrier_config")` 按名取 binder（任何进程可用），失败再回退。

3. **`overrideConfig` 的 `persistent=true` 只允许 system/phone UID 调用。**
   Root（UID 0）虽能过 `MODIFY_PHONE_STATE` 权限检查，但不在该分支 UID 白名单内，会抛「overrideConfig with persistent=true only can be invoked by...」。改用 `persistent=false` 走内存态覆盖，root 有权限、立即生效。**代价：重启后覆盖失效，需重开 App 再设置一次。**（这也是 README 上方「重启后永久保持」在 Root 版不再成立的原因。）

对应实现见 `app/src/main/java/com/github/nrfr/service/RootService.kt`。

## 📝 依赖项

- 已 Root 的设备（Magisk / KernelSU / APatch）- 提供特权服务
- [libsu](https://github.com/topjohnwu/libsu) - Root 进程管理与 IPC

> 快速启动工具 `nrfr-client` 仍基于 Shizuku + ADB 自动化流程，尚未同步到 Root 方案；当前 Root 版 App 不依赖 Shizuku，推荐直接安装 APK 使用。

## 🤝 贡献

欢迎提交 Pull Request 和 Issue！在提交之前，请确保：

- 代码经过测试
- 遵循现有的代码风格
- 更新相关文档
- 描述清楚改动的目的和影响

## 📄 许可证

本项目采用 [Apache-2.0](LICENSE) 许可证。

## ⚠️ 免责声明

本工具仅供学习和研究使用。使用本工具修改系统设置可能会影响设备的正常使用，请自行承担风险。作者不对任何可能的损失负责。

## 💖 支持

如果你觉得这个项目有帮助：

- 在 X 上关注 [@actkites](https://x.com/intent/follow?screen_name=actkites)
- 给项目点个 Star ⭐
- 分享给更多的人

## ⭐ Star History

[![Star History Chart](https://api.star-history.com/svg?repos=Ackites/Nrfr&type=Date)](https://star-history.com/#Ackites/Nrfr&Date)

## 🙏 鸣谢

- [libsu](https://github.com/topjohnwu/libsu) - 感谢 libsu 提供的 Root 进程与 IPC 支持
- [HiddenApiBypass](https://github.com/LSPosed/AndroidHiddenApiBypass) - 感谢其隐藏 API 访问支持

## 🚀 赞助商

<div>
  <p><strong>本项目 CDN 加速及安全防护由 Tencent EdgeOne 赞助</strong></p>
  <a href="https://edgeone.ai/zh?from=github" target="_blank">
    <img src="https://edgeone.ai/media/34fe3a45-492d-4ea4-ae5d-ea1087ca7b4b.png" alt="Tencent EdgeOne" width="200">
  </a>
  <p><a href="https://edgeone.ai/zh?from=github" target="_blank">亚洲最佳CDN、边缘和安全解决方案 - Tencent EdgeOne</a></p>
</div>

[![Powered by DartNode](https://dartnode.com/branding/DN-Open-Source-sm.png)](https://dartnode.com "Powered by DartNode - Free VPS for Open Source")
