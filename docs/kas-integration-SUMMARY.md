# meta-aibaos kas 集成方案

## 方案摘要

本方案 proposes 使用 kas 工具管理 meta-aibaos 项目的 Yocto 构建配置。kas 是 Yocto Project 官方推荐的构建环境配置工具，能够实现声明式构建环境管理、可重复构建和 CI/CD 流水线集成。

---

## kas 配置方案

### 目录结构

```
meta-aibaos/
├── kas.yml                 # 主配置文件（kas 输入入口）
└── kas/                    # 配置子目录
    ├── build.yml          # 基础构建配置
    ├── dev.yml            # 开发环境配置
    ├── ci.yml             # CI/CD 配置
    ├── lock.yml           # 版本锁定文件
    ├── multiconfig.yml    # 多配置支持
    └── overlay.yml        # 自定义覆盖配置
```

### 主配置文件 (kas.yml)

**文件路径**: `/home/node/.openclaw/workspace/silijian/meta-aibaos/kas.yml`

**核心功能**:
- 定义所有仓库和层依赖
- 设置目标机器 (x86-64) 和发行版 (aibaos)
- 引用子配置文件

**关键特性**:
```yaml
repos:
  meta-aibaos: null  # 当前层
  poky:             # Yocto Project
    url: https://git.yoctoproject.org/git/poky
    branch: scarthgap
  meta-openembedded: # OE 层
    url: https://git.openembedded.org/meta-openembedded
    branch: scarthgap
```

### 子配置文件说明

| 配置文件 | 用途 | 适用场景 |
|---------|------|---------|
| `kas/build.yml` | 基础构建配置 | 本地开发、CI 构建 |
| `kas/dev.yml` | 交互式开发环境 | 开发调试 |
| `kas/ci.yml` | CI/CD 优化配置 | GitHub Actions 等 |
| `kas/lock.yml` | 版本锁定 | 可重复构建 |
| `kas/multiconfig.yml` | 多配置支持 | 多目标构建 |
| `kas/overlay.yml` | 自定义覆盖 | 添加额外层 |

---

## 实施步骤

### 步骤 1: 安装 kas 工具

```bash
# 推荐方式：pipx
pipx install kas

# 备用方式：系统包
sudo apt install kas

# 或使用容器
docker run --rm ghcr.io/siemens/kas/kas:5.2 kas --version
```

### 步骤 2: 验证配置文件

```bash
cd meta-aibaos
kas dump kas.yml
```

预期输出：显示完整的合并配置（不报错表示语法正确）

### 步骤 3: 初始化构建环境

```bash
# 签出所有仓库（仅克隆，不构建）
kas checkout kas.yml

# 构建完整镜像
kas build kas.yml
```

### 步骤 4: 生成版本锁定

```bash
# 生成 lockfile 确保可重复构建
kas lock kas.yml
```

### 步骤 5: 整合到 CI/CD

推荐使用 GitHub Actions:

```yaml
# .github/workflows/build.yml
name: AIBAOS Build

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4
    - name: Install kas
      run: pipx install kas
    - name: Build AIBAOS
      run: kas build kas.yml
```

### 步骤 6: 开发工作流

```bash
# 进入交互式构建环境
kas shell kas.yml

# 在 shell 中执行 bitbake 命令
cd build
bitbake core-image-aibaos
```

---

## 使用说明

### 日常构建

```bash
# 从最新 commit 构建
kas build kas.yml

# 指定任务
kas build kas.yml --target core-image-aibaos

# 执行特定任务（如 compile）
kas build kas.yml --task compile
```

### 环境调试

```bash
# 查看有效配置
kas dump kas.yml

# 查看配方信息
kas show-recipe core-image-aibaos kas.yml

# 进入位砖环境调试
kas shell kas.yml -c 'bitbake -e core-image-aibaos'
```

### 版本管理

```bash
# 更新 lockfile
kas update-lock kas.yml

# 指定仓库更新
kas update kas.yml --repos=poky

# 强制重新签出
kas checkout kas.yml --force
```

---

## 技术优势

| 特性 | 实施前 | 实施后 |
|------|-------|-------|
| 配置管理 | 手动维护 | YAML 声明式 |
| 依赖管理 | 手动添加层 | 自动解析 |
| 可重复性 | 依赖本地状态 | lockfile 保证 |
| CI/CD | 需自建脚本 | 原生支持 |
| 版本控制 | 分支切换麻烦 | 精确 commit 锁定 |

---

## 零风险实施策略

1. **并行保留**: 保留原有 bitbake 工作流，kas 作为补充
2. **测试分支**: 先在 feature/kas-integration 分支测试
3. **逐步迁移**: 初期仅使用 kas 进行仓库管理
4. **文档同步**: 更新 README.md 和 CONTRIBUTING.md

---

## 后续优化方向

### Phase 1: 基础集成 (1-2 周)
- [ ] CI/CD 集成
- [ ] 构建缓存策略
- [ ] 文档完善

### Phase 2: 高级功能 (1-2 月)
- [ ] 多配置构建
- [ ] 容器化构建环境
- [ ] 构建证明 (attestation)

### Phase 3: 自动化 (3 月+)
- [ ] 自动化版本更新
- [ ] 构建监控告警
- [ ] 性能分析优化

---

## 总结

本方案提供了一套完整的 kas 集成解决方案，包括：

1. **配置文件**: 7 个 YAML 配置文件覆盖各种场景
2. **实施步骤**: 6 步简单流程快速上手
3. **日常使用**: 常用命令速查
4. **兼容策略**: 零风险迁移保障

**无需修改现有代码**，kas 仅作为构建配置管理工具，与现有 Yocto 层完全兼容。

---
*方案版本: 1.0*  
*更新日期: 2026-03-12*  
*审核状态: 待审核*
