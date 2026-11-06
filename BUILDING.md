# 构建与回归测试

## 环境与依赖

- JDK 25，使用仓库自带的 Gradle Wrapper。
- 首次构建需联网下载 Gradle、Minecraft、Fabric 和 JEI API；已有完整缓存时可加 `--offline`。
- Python 3 仅用于可选的上游资源检查，无第三方 Python 依赖。
- GPU/客户端测试需要 OpenGL 3.3 及桌面图形环境。

版本见 `gradle.properties`。JEI API 为 Maven compile-only 依赖，不随正式模组分发。

## 独立构建主项目

Windows PowerShell：

```powershell
./gradlew.bat clean releaseBundle --console=plain
```

Linux/macOS 使用 `./gradlew`，必要时先执行 `chmod +x gradlew`。普通构建只需 `./gradlew.bat build`，不依赖上游源码、同级测试目录、本地 libs 或第三方动画组件。

产物：

- `build/libs/avaritianeo-port-fabric-1.0.0-rc.2.jar`：正式模组。
- `build/libs/avaritianeo-port-fabric-1.0.0-rc.2-sources.jar`：源码附件，不可放进 mods。
- `build/distributions/avaritianeo-port-fabric-1.0.0-rc.2-github-source.zip`：可独立构建的 GitHub 源码上传包。
- `build/distributions/SHA256SUMS.txt`：上述三个文件的 SHA-256。发布时将文件放在同一附件目录后校验。

项目名及产物前缀为 `avaritianeo-port-fabric`，显示名称为 AvaritiaNeo Port (Fabric)。作者 `xiaowen`、版本 `1.0.0-rc.2`、模组 ID `avaritia`；包名、资源命名空间、存档标识保持不变。

## 外置回归测试

维护者本地目录结构：

```text
工作目录/
  avaritianeo-port-fabric/         主源码，可单独上传 GitHub
  avaritianeo-port-fabric-tests/   测试源码、脚本和本地运行资料
  AvaritiaNeo-main/               可选，上游源码，仅供资源对照
```

测试目录不在主仓库/主源码 ZIP 中；仅取得主仓库时无需运行以下命令。有效测试源码保留在测试目录 tools 中，测试类不会打包进正式 JAR。

在 Windows PowerShell 中执行：

```powershell
../avaritianeo-port-fabric-tests/run-tests.ps1
```

参数 `-Offline` 使用已有缓存；`-Tasks` 可选择 `verifyRendering`、`runRenderRuntimeContracts`、`runRenderUiContracts`、`runRenderAnimationContracts`、`runRenderShaderContracts`。首次使用外置目录需要下载依赖，不要假定旧缓存可直接复用。

测试脚本将主项目构建输出重定向到 tests 的 `build/mod/`，测试夹具位于 `build/`，Gradle 缓存位于 `cache/`，日志位于 `logs/`，运行世界、截图及选项位于 `runs/`。不会修改用户启动器或服务器目录。

也可手工加载外部脚本：

```powershell
./gradlew.bat -PrenderTestsDir=../avaritianeo-port-fabric-tests -PbuildOutputDir=../avaritianeo-port-fabric-tests/build/mod verifyRendering --console=plain
```

GPU 测试检查图集、物品模型、护甲 UV/延迟姿态快照、OBJ、着色器和绘制顺序；UI 测试使用一次性世界检查压缩机同步、JEI 分类以及物品/工具行为。JEI 使用 API 适配器，不等同于完整 JEI 客户端测试。

## 可选的玩家动画兼容性测试

第三方模组及材质包须自行取得，不得随源码或发布附件分发。

1. 在 tests 的 `runs/render-animation/resourcepacks/` 放入 FA+Player 1.1 和 JustExpressions 1.2.1。
2. 仅编辑该测试目录的 options.txt，启用上述材质包；不要覆盖实际游戏配置。
3. 用 `run-tests.ps1 -Tasks runRenderAnimationContracts -AnimationExtraMods $extraMods` 传入匹配版本的模组绝对路径列表，分隔符用 `[IO.Path]::PathSeparator`。

`-RuntimeExtraMods` 用于运行时渲染测试的额外模组。护甲动画回归检查真实 EMF 模型根节点/四肢的位移、旋转、缩放及翅膀提交，并保存站立、行走、潜行、飞行和背面移动飞行截图。

此前已验证指定组合：FA+Player 1.1、JustExpressions 1.2.1、EMF 3.3.12、ETF 7.2.7、EMF Compat Core 2.3.2、Touch n Motion 1.0.4、Sodium 0.8.12+mc26.1.2、3D Skin Layers 1.11.4-mc26.1。仅针对护甲动画，不代表所有第三方功能均兼容。

## 可选的 Iris 光影回归

自行提供 Iris、Sodium 和光影包，不随本项目分发。将光影包放到 tests 的 `runs/render-shaders/shaderpacks/`，并在该运行目录的 `config/iris.properties` 指定 `enableShaders=true`、`shaderPack=实际文件名.zip` 和 `allowUnknownShaders=false`。不要修改实际启动器的配置。

```powershell
$tests = (Resolve-Path ../avaritianeo-port-fabric-tests).Path
$mods = @("$tests/inputs/iris.jar", "$tests/inputs/sodium.jar") -join [IO.Path]::PathSeparator
../avaritianeo-port-fabric-tests/run-tests.ps1 -Offline -Tasks runRenderShaderContracts -ShaderExtraMods $mods
```

该任务使用一次性世界，输出至 `runs/render-shaders/screenshots/`；除了实际渲染通道，还读取 PNG 验证剑身星空可见和头盔彩虹随真实时间变化，避免仅以无报错或提交了几何作为成功标准。当前验证组合及限制见 RENDER_PORT.md。

## 可选的机器与持续性能回归

```powershell
../avaritianeo-port-fabric-tests/run-tests.ps1 -Offline -Tasks runCollectorContracts
../avaritianeo-port-fabric-tests/run-tests.ps1 -Offline -Tasks runRenderPerformanceContracts,'-PperformanceLabel=fixed','-PperformanceSamples=12' -ShaderExtraMods $mods
```

机器回归使用真实服务端漏斗 tick；性能回归使用上述 Iris 测试目录，连续测量空手、持剑、套装与卸装，保存 JFR 和每10秒的帧耗时/资源计数。仅测试客户端关闭闲置降帧，不修改实际客户端。fixed 标签同时检查缓存条目不随帧数增长。

## 可选的上游资源一致性检查

在主项目目录执行；若采用外置 build 输出，传入对应 `--build-dir` 和 `--jar`：

```powershell
python ../avaritianeo-port-fabric-tests/tools/verify_render_parity.py --project . --original ../AvaritiaNeo-main --jar build/libs/avaritianeo-port-fabric-1.0.0-rc.2.jar
```

脚本核对 11 项 cosmic 模型、20 项 halo、15 项名称注册、上游纹理/动画/OBJ，以及 JAR 内资源与编译类，确保测试夹具未混入正式包。

## 发布前

依照 [RELEASE.md](RELEASE.md) 检查元数据、许可、附件和兼容性边界。源码上传使用 github-source.zip，不要上传 tests、Gradle 缓存、日志、运行世界、第三方依赖或个人配置。
