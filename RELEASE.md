# GitHub 发布清单

## 仓库内容

上传主项目源码、资源、Gradle Wrapper、构建文件和项目文档。回归测试已移至同级 `avaritianeo-port-fabric-tests/`，不属于主源码上传包；主项目不依赖该目录。保留 LICENSE 的上游署名。

`.gitignore` 排除了构建产物、Gradle 缓存、运行目录、存档、日志、IDE 用户配置和私密环境文件。保留 `gradle/wrapper/gradle-wrapper.jar`，它不是冗余依赖。

整理源码目录时可清除 `.gradle/`、`build/` 和空的 `run/`；先将需要发布的附件保存到项目外，含存档或配置的运行目录须先备份。保留 `.git/`、`src/`、`gradle/` 和根目录构建文件、许可证及文档。之后构建会重新生成缓存和输出目录，这些目录无需提交。

GitHub 网页上传不会自动应用本地忽略规则，请勿直接拖入整个开发目录。使用 Git 提交前检查暂存列表。

## 发布前检查

- [ ] `gradle.properties` 与成品 `fabric.mod.json` 版本均为 `1.0.0-rc.2`。
- [ ] 模组作者为 `xiaowen`，Aqua3 上游署名及 LICENSE 保留。
- [ ] JDK 25 下 `clean build` 成功，渲染和客户端回归任务通过。
- [ ] 在目标客户端复核安装和升级存档，升级前先备份。
- [ ] 待提交文件无日志、个人路径、凭据、存档、缓存或调试 JAR。
- [ ] 正式 JAR 为玩家安装包，sources JAR 仅为可选源码附件。

## Release 设置

- 标签建议：`v1.0.0-rc.2`。
- 标题建议：`AvaritiaNeo Port (Fabric) — 1.0.0-rc.2`。
- 勾选 **Set as a pre-release**，标明 RC 状态。
- 主附件：`build/libs/avaritianeo-port-fabric-1.0.0-rc.2.jar`。
- 可选附件：同目录 `avaritianeo-port-fabric-1.0.0-rc.2-sources.jar`。
- 源码上传包：`build/distributions/avaritianeo-port-fabric-1.0.0-rc.2-github-source.zip`。
- `./gradlew.bat clean releaseBundle` 同时生成 `build/distributions/SHA256SUMS.txt`；清单中的三个文件名按发布附件所在目录解析。
- 不要上传测试 JAR、第三方模组/材质包、旧 renderfix 包或运行日志。

## 可用的发布说明

### AvaritiaNeo Port (Fabric) — 1.0.0-rc.2

移植作者：**xiaowen**。基于 **Aqua3 / AvaritiaNeo**，保留 MIT 许可及上游署名。

本候选版完成无尽装备、奇点和机器的 Fabric 移植，修复护甲/物品渲染、JEI 界面与机器预览、终极物品名称/彩虹提示、骷髅头掉落及镐铲特殊形态行为。

本次修复中子态素收集器无法被下方漏斗抽取、头盔顶部与侧边彩虹外溢、持剑或穿甲时渲染缓存累积导致持续掉帧，以及 Iris 延迟绘制缓冲提前释放造成的崩溃；保留星空与彩虹特效。

要求 Minecraft **26.1.2**、Java **25+**、Fabric Loader **0.20.1+** 和 Fabric API（验证版本 **0.155.3+26.1.2**）。JEI 可选。安装前移除旧版 JAR 并备份存档，不要将 sources JAR 放入 mods。

修复 Fresh Animations: Player Extension 下盔甲特效与翅膀的动画错位，并对指定 EMF/ETF、Touch n Motion、Sodium 和 3D Skin Layers 组合运行护甲动画回归；另修复 Iris/Photon 下星空和彩虹覆盖层消失，通过指定光影与动画组件的图像回归。具体版本见 BUILDING.md / RENDER_PORT.md。此版本仍为 RC；该结果不代表所有第三方功能均兼容。JEI 完整运行时、其他光影包和其他整合包组合尚未全面验证。问题报告请附复现步骤和已去除隐私信息的日志。

本地整理不会自动创建远程仓库、提交、打标签或上传文件，需维护者确认后执行。
