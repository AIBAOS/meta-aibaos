# Commit 日志规范

## 格式

```
<type>(<scope>): <short summary>
       │         │
       │         └─ 紧凑简短，无需大写，不用句号结尾
       │
       └─ 可选，模块范围，如：core, misc, driver
```

## 类型说明

| 优先级 | 类型 | 描述 |
|--------|------|------|
| 核心 | feat | 新特性 |
| 核心 | fix | Bug 修复 |
| 重要 | refactor | 代码重构（非新特性、非修复） |
| 重要 | perf | 性能优化 |
| 重要 | revert | 回退提交 |
| 常规 | chore | 辅助变更（依赖更新、配置调整等） |
| 常规 | docs | 文档修改 |
| 常规 | style | 代码格式（不影响逻辑） |
| 自动化 | test | 测试相关 |
| 自动化 | build | 构建系统或依赖变更 |
| 自动化 | ci | CI 配置变更 |

## 示例

```
feat(core): 添加用户认证模块
fix(driver): 修复设备初始化失败
refactor(utils): 简化字符串处理逻辑
perf(render): 优化帧缓冲区分配
chore: 更新依赖至v2.0
docs(readme): 补充安装说明
style: 格式化代码
test(core): 添加单元测试覆盖
```

## Breaking Change

在类型后加 `!` 标记破坏性变更：

```
feat(api)!: 重构接口签名
```

或脚注形式：

```
feat(api): 重构接口签名

BREAKING CHANGE: 移除了废弃的回调参数
```

---

## 代码审查流程

1. **提交前**：确保通过本地测试和代码风格检查
2. **提交 PR**：填写 PR 模板，关联 Issue
3. **审核**：至少一位审核者通过
4. **合并**：Squash merge，使用规范 commit message

## 分支策略

- `main` - 主分支，稳定版本
- `develop` - 开发分支
- `feature/*` - 功能分支
- `fix/*` - 修复分支