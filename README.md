# AvaritiaNeo Port (Fabric)

无尽贪婪的 Minecraft 26.2 Fabric 移植版，基于 Aqua3 的 AvaritiaNeo。

- **版本：1.0.0-rc.4+mc26.2**（发布候选版）
- **移植作者：xiaowen**
- **上游作者：Aqua3**
- **许可证：MIT**，保留上游版权声明

## 运行环境

| 组件 | 版本 |
| --- | --- |
| Minecraft | 26.2 |
| Java | 25 或更高；构建与回归验证使用 JDK 25 |
| Fabric Loader | 0.20.1 或更高 |
| Fabric API | 必需；验证版本为 0.161.0+26.2 |
| JEI | 可选；编译 API 版本为 30.48.0.244 |
| Mod Menu | 可选；编译版本为 20.0.3 |

## 安装

1. 安装对应版本的 Fabric Loader 与 Fabric API。
2. 将 `avaritianeo-port-fabric-1.0.0-rc.4+mc26.2.jar` 放入游戏的 `mods` 目录，先移除旧版本的 Avaritia JAR。
3. 移除旧版 Avaritia JAR，避免重复加载；升级前备份存档与配置。
4. 如需配方浏览，可安装适配同一 Minecraft/Fabric 版本的 JEI。

不要将 `-sources.jar` 或回归测试 JAR 作为模组安装。本项目不适用于 NeoForge。

## 主要内容

- 无尽装备、终极工具、奇点、中子态素机器及配套合成系统。
- Cosmic 星空、halo 光晕、动态奇点着色，以及无尽护甲、翅膀和箱子渲染。
- JEI 大型合成配方背景、凹槽与压缩机输入/预计产出预览。
- 终极物品红色名称、支配剑彩虹伤害提示、炽焰之啄颅剑骷髅头掉落。
- 世界崩解之镐与星球吞噬之铲的往返形态切换、特殊形态挖掘与击飞。
- 无尽盔甲、武器、工具和炽焰之啄颅剑具有原生不可破坏属性；生存攻击、挖掘不消耗耐久，旧物品残留损伤也不再显示耐久条。

## 游戏内显示配置

可选安装适配 Minecraft 26.2 的 Mod Menu，在“模组 → AvaritiaNeo Port (Fabric) → 配置”中调整物品星空、盔甲星空、盔甲彩虹与发光、背包物品光晕和背包物品脉动。提供“一键低特效”和“恢复默认”，默认保留全部特效；修改立即生效，点击“完成”或按 Esc 保存。

配置保存到游戏实例的 `config/avaritia-client.properties`，仅影响本机显示，不改变防御、攻击、挖掘或飞行能力。关闭星空会跳过相应渲染通道，保留基础模型、盔甲纹理和飞行翅膀；原生纹理动画仍然保留。不要求安装 Cloth Config，Mod Menu 也不是运行必需依赖；未安装时可编辑该配置文件并重启客户端。

多人游戏中，无限耐久修复需要服务端也更新本模组；客户端显示开关不需要服务端同步。

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

首次构建需要联网下载 Gradle、Minecraft、Fabric、JEI API 和 Mod Menu 编译依赖。JEI API 与 Mod Menu 仅供编译，不会打包进模组，也不强制要求玩家安装。成品位于 `build/libs/`。

项目与产物统一使用 `avaritianeo-port-fabric`；模组 ID 仍为 `avaritia`，Java 包名、资源命名空间和存档标识不变。运行 `./gradlew.bat releaseBundle` 可生成发布 JAR、源码 JAR、干净 GitHub 源码 ZIP 和 SHA-256 清单。

详见 [构建与测试](BUILDING.md)、[渲染移植说明](RENDER_PORT.md)、[更新记录](CHANGELOG.md) 和 [GitHub 发布清单](RELEASE.md)。

## 测试与兼容性

`./gradlew.bat runClientGameTest` 在独立临时世界中验证 26.2 的无尽剑、无尽盔甲、物品栏、显示设置、压缩机界面及更新后的进度数据。测试使用 OpenGL，不进入发布 JAR。JEI 和 Mod Menu 已按 26.2 的 API 编译；这不等同于验证了完整联动运行时。Iris 光影、Vulkan、第三方动画整合包和现有存档升级不在该回归范围内。

## 致谢与许可

- **Aqua3 / AvaritiaNeo**：上游实现、资源与原始版权。
- **xiaowen**：原 Fabric 移植与维护。
- Fabric、JEI 及相关工具：各自按上游许可证提供；不因本项目许可证而改变其授权。

项目代码保留原有 Java 包名，以避免不必要的兼容性变化。完整许可见 [LICENSE](LICENSE)。
