# 更新记录

## 1.0.0-rc.2

- 将已集成的修复版以独立内部版本发布，正式 JAR 的 `fabric.mod.json` 版本为 `1.0.0-rc.2`。
- 包含中子态素收集器下方漏斗输出、头盔彩虹范围、持剑及穿甲持续掉帧，以及 Iris 光影特效与缓冲生命周期崩溃修复。
- 同步安装、构建及发布文档中的版本与附件名称；修复实现不变。

## 1.0.0-rc

首个面向发布的 Minecraft 26.1.2 Fabric 候选版本，移植作者为 **xiaowen**，保留 Aqua3 / AvaritiaNeo 署名与 MIT 许可证。此前开发构建中的 1.3.1 / renderfix 文件名属于调试阶段，不代表本项目的正式发布顺序。

### 内容与修复

- 修复中子态素收集器无法被下方漏斗抽取：暴露原版 WorldlyContainer 输出槽，保持自动化只出不进，GUI 与存档仍使用同一份 result 库存。
- 修复头盔顶部、侧面及边框的彩虹外溢：GLINT 混合不按源 alpha 隐藏 RGB，彩虹管线增加 ALPHA_CUTOUT，透明纹理不再参与绘制；保留原始眼纹资源。
- 修复持剑和穿甲时持续创建 cosmic RenderType 导致提交缓存与空缓冲刷新开销随帧数累积：按管线维护可复用类型池，同帧按完整参数分离、合并相同参数，呈现完成后才回收帧槽位；不降低星空层数或关闭光影。

- 修复首版 Iris 兼容补丁在缓冲提前回收时发生的 `IrisEffectQueue.flush: Buffer is no longer valid` 崩溃：独立缓存顶点及排序索引，补齐原网格与快照的释放路径；新增可确定复现的生命周期测试，并回归 ImmediatelyFast 等优化组件及 Complementary/Euphoria 光影。

- 修复 Iris/Photon 光影下剑身星空与盔甲彩虹覆盖层消失：适配合成时序、目标缓冲与手持/贴面深度，保留原生 GLSL 和无光影路径；新增真实渲染与图像回归。

- 完成 cosmic、halo、奇点、无尽护甲/翅膀、箱子及物品渲染移植；修复护甲错位/闪烁、奇点背景和支配剑显示问题。
- 修复 Fresh Animations: Player Extension 动画下无尽护甲特效错位：同步玩家根节点的位移、旋转和缩放，保留四肢缩放及延迟绘制快照，翅膀按根节点和躯干共同变换。
- 恢复 JEI 背景渐变、槽位凹槽和压缩机两侧预览。
- 恢复终极物品名称、彩虹伤害提示及啄颅剑骷髅头掉落。
- 修复镐铲双向形态切换、特殊形态挖掘和击飞。

### 发布整理

- 版本统一为 `1.0.0-rc`，作者为 `xiaowen`。
- 项目与构建产物统一为 `avaritianeo-port-fabric`，显示名称为 AvaritiaNeo Port (Fabric)；模组 ID、Java 包名及资源命名空间不变。
- 有效回归测试迁出主项目；普通 GitHub 源码 checkout 可独立构建，新增干净源码 ZIP 和 SHA-256 发布清单。
- JEI API 改为 Maven compile-only 依赖，不捆绑本地依赖 JAR。
- 移除旧日志、开发快照、一次性迁移/诊断脚本与旧构建包，保留有效回归测试。
- 更新安装、构建、验证、GitHub 发布说明和忽略规则。

### 兼容性

此版本为 RC。已对 FA+Player 1.1、JustExpressions 1.2.1、EMF 3.3.12、ETF 7.2.7、EMF Compat Core 2.3.2、Touch n Motion 1.0.4、Sodium 0.8.12 和 3D Skin Layers 1.11.4 的指定组合运行护甲动画回归；另通过 Iris 1.11.4、Sodium 0.9.2、Photon v1.3b 与上述动画组件的光影图像回归。JEI 自动测试使用 API 适配器；其他光影包、动画模组及整合包组合尚未全面验证。行为差异及验证边界见 `RENDER_PORT.md`。
