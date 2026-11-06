# AvaritiaNeo Port (Fabric)

无尽贪婪的 Minecraft 26.1.2 Fabric 移植版，基于 Aqua3 的 AvaritiaNeo。

- **版本：1.0.0-rc.2**（发布候选版）
- **移植作者：xiaowen**
- **上游作者：Aqua3**
- **许可证：MIT**，保留上游版权声明

## 运行环境

| 组件 | 版本 |
| --- | --- |
| Minecraft | 26.1.2 |
| Java | 25 或更高；构建与回归验证使用 JDK 25 |
| Fabric Loader | 0.20.1 或更高 |
| Fabric API | 必需；验证版本为 0.155.3+26.1.2 |
| JEI | 可选；编译 API 版本为 29.6.2.38（26.1.2） |

## 安装

1. 安装对应版本的 Fabric Loader 与 Fabric API。
2. 将 `avaritianeo-port-fabric-1.0.0-rc.2.jar` 放入游戏的 `mods` 目录。
3. 移除旧版 Avaritia JAR，避免重复加载；升级前备份存档与配置。
4. 如需配方浏览，可安装适配同一 Minecraft/Fabric 版本的 JEI。

不要将 `-sources.jar` 或回归测试 JAR 作为模组安装。本项目不适用于 NeoForge。

## 主要内容

- 无尽装备、终极工具、奇点、中子态素机器及配套合成系统。
- Cosmic 星空、halo 光晕、动态奇点着色，以及无尽护甲、翅膀和箱子渲染。
- JEI 大型合成配方背景、凹槽与压缩机输入/预计产出预览。
- 终极物品红色名称、支配剑彩虹伤害提示、炽焰之啄颅剑骷髅头掉落。
- 世界崩解之镐与星球吞噬之铲的往返形态切换、特殊形态挖掘与击飞。

## 构建

安装 JDK 25，在项目根目录执行：

```powershell
./gradlew.bat build
```

Linux/macOS：

```sh
chmod +x gradlew
./gradlew build
```

首次构建需要联网下载 Gradle、Minecraft、Fabric 和 JEI API。JEI API 仅供编译，不会打包进模组，也不强制要求玩家安装 JEI。成品位于 `build/libs/`。

项目与产物统一使用 `avaritianeo-port-fabric`；模组 ID 仍为 `avaritia`，Java 包名、资源命名空间和存档标识不变。运行 `./gradlew.bat releaseBundle` 可生成发布 JAR、源码 JAR、干净 GitHub 源码 ZIP 和 SHA-256 清单。

详见 [构建与测试](BUILDING.md)、[渲染移植说明](RENDER_PORT.md)、[更新记录](CHANGELOG.md) 和 [GitHub 发布清单](RELEASE.md)。

## 测试与兼容性

渲染、一次性世界 UI、物品行为和工具行为回归测试已分离到同级 `avaritianeo-port-fabric-tests/`；测试源码、第三方组件、缓存和运行目录均不进入主仓库或正式模组。主仓库可独立构建，无需测试目录或上游源码。JEI 自动测试使用 API 适配器调用真实配方分类，不等同于完整 JEI 运行时验证。

已修复 Fresh Animations: Player Extension 下的盔甲特效错位，并使用 FA+Player 1.1、JustExpressions 1.2.1、EMF 3.3.12、ETF 7.2.7、EMF Compat Core 2.3.2、Touch n Motion 1.0.4、Sodium 0.8.12 和 3D Skin Layers 1.11.4 的指定组合验证站立、行走、潜行及飞行动画。测试无需关闭玩家动画，第三方组件不随本项目分发。

已修复 Iris/Photon 下星空与盔甲彩虹消失，并对 Iris 1.11.4、Sodium 0.9.2、Photon v1.3b 及上述动画组件运行光影图像回归，检查透明皮肤、彩虹变色和隔墙遮挡，详细版本与边界见 RENDER_PORT.md。

这些检查针对护甲姿态和渲染，不代表 Sodium 所有功能、其他光影包、其他动画模组或不同整合包已全面兼容。报告问题时请附版本、复现步骤和日志，并移除日志中的隐私信息。

## 致谢与许可

- **Aqua3 / AvaritiaNeo**：上游实现、资源与原始版权。
- **xiaowen**：Minecraft 26.1.2 Fabric 移植与维护。
- Fabric、JEI 及相关工具：各自按上游许可证提供；不因本项目许可证而改变其授权。

项目代码保留原有 Java 包名，以避免不必要的兼容性变化。完整许可见 [LICENSE](LICENSE)。
