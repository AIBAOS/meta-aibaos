# Commit 日志规范

## 格式

<type>(<scope>): <short summary>
       │         │
       │         └─⫸ 紧凑简短，无需大写，不用句号结尾
       │
       └─⫸ 可选，模块范围，如：core, misc, driver

## 类型说明

| 优先级 | 类型 | 描述 |
|--------|------|------|
| **核心** | feat | 新特性 |
| | fix | Bug 修复 |
| **重要** | refactor | 代码重构（非新特性、非修复） |
| | perf | 性能优化 |
| | revert | 回退提交 |
| **常规** | chore | 辅助变更（依赖更新、配置调整等） |
| | docs | 文档修改 |
| | style | 代码格式（不影响逻辑） |
| **自动化** | test | 测试相关 |
| | build | 构建系统或依赖变更 |
| | ci | CI 配置变更 |

## 示例

feat(core): 添加用户认证模块
fix(driver): 修复设备初始化失败
refactor(utils): 简化字符串处理逻辑
perf(render): 优化帧缓冲区分配
chore: 更新依赖至v2.0
docs(readme): 补充安装说明
style: 格式化代码
test(core): 添加单元测试覆盖

## Breaking Change

在类型后加 ! 标记破坏性变更：

feat(api)!: 重构接口签名

或脚注形式：

feat(api): 重构接口签名

BREAKING CHANGE: 移除了废弃的回调参数
