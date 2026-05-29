# AGENTS.md

## 项目目标

本项目应开发为一个完整、可运行、可演示的“我的家乡景点导览 App”。程序以家乡 6 个真实景点为核心数据，依次完整覆盖 Android 四大组件：Activity、Service、BroadcastReceiver、ContentProvider，并最终整合为一个体验统一的应用。

后续所有程序开发必须以“能稳定编译运行、功能可完整演示、结构清晰、生命周期处理正确、数据来源统一、日志与截图可验证”为最高标准。

## 总体开发原则

1. 项目必须在 Android Studio 中正常执行 `Clean Project` 和 `Rebuild Project`。
2. 所有功能必须能在真机或模拟器中实际运行，不只停留在代码层面。
3. 使用统一的景点数据模型，避免不同页面、服务、数据库中维护互相矛盾的数据。
4. 准备 6 个真实家乡景点，每个景点包含：
   - 名称
   - 50 字以内简介
   - 类型：自然 / 人文 / 休闲
   - 票价
   - 是否开放
5. UI、Service、Receiver、Provider 之间的数据含义必须一致。
6. 关键行为必须有明确可见结果：页面展示、Toast、通知、Logcat 输出或数据库查询结果。
7. 生命周期相关注册、绑定、启动、停止、注销、解绑必须成对处理，避免泄漏和重复注册。
8. 代码命名应语义清晰，类名、方法名、常量名能直接反映业务含义。

## 项目结构

按功能清晰拆分代码，包含以下模块或包：

- `model`：景点数据模型，如 `Scenery`。
- `data`：景点初始数据、数据库帮助类、Provider 常量。
- `ui`：`MainActivity`、`DetailActivity`、`SearchActivity`、`AboutActivity`。
- `service`：`SceneryAudioService`、`TicketQueryService`。
- `receiver`：`BootReceiver`、`NetworkReceiver`、`SceneryUpdateReceiver`。
- `provider`：`SceneryProvider`、`SceneryDBHelper`。

## Activity 开发要求

### MainActivity

必须作为景点列表主入口。

实现要求：

1. 使用 `RecyclerView` 展示 6 个景点。
2. 列表项展示景点名称、类型、票价和开放状态。
3. 点击任意景点时：
   - 通过显式 `Intent` 跳转到 `DetailActivity`。
   - 使用 `putExtra()` 传递景点名称和简介。
   - 查询票价并通过 `Toast` 显示。
4. 绑定 `TicketQueryService`，通过其 `getTicketPrice(String sceneryName)` 获取票价。
5. 在合适生命周期中绑定与解绑 Service，Activity 销毁时必须正确解绑。
6. 动态注册 `NetworkReceiver`：
   - 在 `onResume()` 注册。
   - 在 `onPause()` 注销。
7. 接收自定义景点更新广播，并用 `Log` 模拟刷新列表。

### DetailActivity

必须作为景点详情页。

实现要求：

1. 接收 `MainActivity` 传来的景点名称、简介等信息并展示。
2. 提供“返回列表”按钮，用于演示 Activity 返回栈。
3. 提供“开始讲解”按钮，启动 `SceneryAudioService`。
4. 提供“停止讲解”按钮，调用 `stopService()` 停止讲解服务。
5. 提供“景点已更新”按钮，发送自定义广播，由 `MainActivity` 中注册的接收逻辑处理。

### SearchActivity

该页面必须实现并达到可演示标准。

实现要求：

1. 提供关键词输入。
2. 使用隐式 `Intent` 跳转系统地图搜索景点位置。
3. 对无可处理地图 Intent 的情况做兜底提示，避免崩溃。

### AboutActivity

必须作为个人主页。

实现要求：

1. 展示姓名、班级、学号、教育经历。
2. 展示家乡 6 个景点名称。
3. 结合个人图片或家乡景点图片进行展示，图片资源必须能正常加载，不得缺失或报错。

## Service 开发要求

### SceneryAudioService

类型：Started Service。

实现要求：

1. 在详情页点击“开始讲解”后启动。
2. 后台每隔 3 秒通过 `Log` 模拟输出一句景点介绍词。
3. 总计输出 3 句后自动结束，同时支持用户通过“停止讲解”提前停止。
4. 点击“停止讲解”必须调用 `stopService()`。
5. 必须正确处理重复启动，避免多个定时任务并行输出。
6. Service 销毁时必须清理 Handler、Runnable、Timer、线程、协程等后台任务。

### TicketQueryService

类型：Bound Service。

实现要求：

1. 绑定后提供 `getTicketPrice(String sceneryName)` 方法。
2. `MainActivity` 绑定该 Service。
3. 点击景点时调用该方法查询票价。
4. 查询结果用 `Toast` 显示。
5. Activity 结束时必须解绑，避免 `ServiceConnection` 泄漏。
6. 查询逻辑必须复用统一景点数据源，避免硬编码多份不一致票价。

## BroadcastReceiver 开发要求

### BootReceiver

类型：静态注册 Receiver。

实现要求：

1. 监听 `ACTION_BOOT_COMPLETED`。
2. 在 `AndroidManifest.xml` 中声明 Receiver。
3. 声明所需开机广播权限。
4. 开机后发送通知：“今日推荐景点：xxx，欢迎游览！”
5. Android 8.0 及以上必须创建通知渠道。
6. 通知逻辑必须处理 Android 13 及以上运行时通知权限，并保证代码结构清晰。

### NetworkReceiver

类型：动态注册 Receiver。

实现要求：

1. 在 `MainActivity` 中动态注册。
2. 监听网络状态变化。
3. 网络断开时 `Toast` 提示：“当前无网络，景点地图功能不可用”。
4. 网络恢复时 `Toast` 提示：“网络已连接”。
5. 必须在 `onResume()` 注册，在 `onPause()` 注销。
6. 注册与注销必须有状态保护，避免重复注销导致异常。

### SceneryUpdateReceiver

类型：自定义广播。

实现要求：

1. 在 `DetailActivity` 点击“景点已更新”按钮时发送自定义广播。
2. `MainActivity` 接收广播。
3. 接收后刷新列表数据，并用 `Log` 记录刷新行为。
4. 自定义 action 应使用应用包名作为前缀，避免与其他应用冲突。

## ContentProvider 开发要求

### SceneryDBHelper

实现要求：

1. 创建数据库 `hometown.db`。
2. 创建表 `sceneries`。
3. 字段必须包含：
   - `id`
   - `name`
   - `intro`
   - `type`
   - `price`
   - `is_open`
4. 在 `onCreate()` 中预插入 6 条家乡景点数据。
5. 数据库字段类型、主键、自增、默认值应设计合理。
6. 数据库升级逻辑必须有清晰可运行的 `onUpgrade()` 实现。

### SceneryProvider

实现要求：

1. authority 格式：`com.xxx.hometown`，其中 `xxx` 替换为本人姓名拼音。
2. 使用 `UriMatcher` 注册：
   - `sceneries`：操作全部景点，常量 `ALL_SCENERIES = 1`。
   - `sceneries/#`：操作单条景点，常量 `ONE_SCENERY = 2`。
3. 实现 `query()`、`insert()`、`delete()`、`update()` 四个方法。
4. 在 `AndroidManifest.xml` 中声明 Provider。
5. Provider 需声明 `android:exported="true"`。
6. 对非法 URI 抛出清晰异常，并在日志中记录问题。
7. `insert()`、`delete()`、`update()` 后应调用 `notifyChange()`，保证观察者可感知数据变化。

### ContentResolver 访问测试

必须在同一 App 内模拟访问 Provider，完成以下操作并能在 Logcat 中验证：

1. 查询全部景点并打印到 `Log`。
2. 插入一条新的自编景点数据。
3. 删除 `id=1` 的景点。
4. 将 `id=2` 的景点 `is_open` 更新为 `0`。

测试入口放在主菜单的 ContentProvider 测试页面中，保证课堂演示路径清晰。

## 主菜单整合要求

应用应有清晰主入口，能整合四个部分的功能。

主菜单必须提供：

1. 景点列表。
2. 景点搜索。
3. 个人主页。
4. ContentProvider 测试。
5. Service 演示入口。
6. BroadcastReceiver 演示入口。

## 质量验收清单

提交和继续开发前，必须逐项检查：

1. 项目能正常 `Clean Project`。
2. 项目能正常 `Rebuild Project`。
3. App 能启动到主页面。
4. MainActivity 能展示 6 个景点。
5. 点击景点能跳转详情页。
6. Intent 数据能正确显示。
7. 返回按钮能回到列表。
8. 开始讲解后 Logcat 每 3 秒输出讲解内容。
9. 停止讲解后不再继续输出。
10. 票价查询能通过 Bound Service 返回并 Toast 显示。
11. Activity 销毁时 Bound Service 正确解绑。
12. 网络变化 Receiver 注册与注销时机正确。
13. 自定义广播能发送并被接收。
14. BootReceiver、权限和通知渠道配置完整。
15. ContentProvider 的增删改查均能通过 ContentResolver 运行并记录 Log。
16. AndroidManifest 中四大组件声明完整。
17. 所有截图和 Logcat 证据能对应到具体功能点。
18. 所有功能都完成端到端演示，不保留半成品入口。

任何新增功能都不能破坏已完成的主流程和编译通过状态。
