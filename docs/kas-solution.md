# meta-aibaos kas 集成方案

## 摘要

本方案为 AIBAOS/meta-aibaos 项目设计了完整的 kas 集成方案，用于管理 Yocto 构建配置。

---

## kas 配置方案

### 文件结构

```
meta-aibaos/
├── kas.yml                 # 主配置文件（kas 入口）
└── kas/                    # 配置子目录
    ├── build.yml          # 基础构建配置
    ├── dev.yml            # 开发环境配置
    ├── ci.yml             # CI/CD 配置
    ├── lock.yml           # 版本锁定文件
    ├── multiconfig.yml    # 多配置支持
    └── overlay.yml        # 自定义覆盖配置
```

### 主配置 (kas.yml)

**文件**: `/home/node/.openclaw/workspace/silijian/meta-aibaos/kas.yml`

```yaml
header:
  version: 21

machine: x86-64
distro: aibaos

repos:
  meta-aibaos: null
  poky:
    url: https://git.yoctoproject.org/git/poky
    branch: scarthgap
    layers:
      meta: null
      meta-poky: null
      meta-yocto-bsp: null
  meta-openembedded:
    url: https://git.openembedded.org/meta-openembedded
    branch: scarthgap
    layers:
      meta-oe: null
      meta-networking: null
      meta-python: null
```

### 子配置说明

| 文件 | 用途 |
|-----|------|
| `build.yml` | 基础构建配置，包含环境变量和本地配置 |
| `dev.yml` | 开发环境，提供交互式 shell |
| `ci.yml` | CI/CD 优化，支持容器化构建 |
| `lock.yml` | 版本锁定，确保可重复构建 |
| `multiconfig.yml` | 多配置构建支持 |
| `overlay.yml` | 自定义层和覆盖配置 |

---

## 实施步骤

### 步骤 1: 安装 kas

```bash
pipx install kas
# 或
sudo apt install kas
```

### 步骤 2: 验证配置

```bash
cd /home/node/.openclaw/workspace/silijian/meta-aibaos
kas dump kas.yml
```

### 步骤 3: 构建项目

```bash
kas build kas.yml
```

### 步骤 4: 生成 lockfile

```bash
kas lock kas.yml
```

### 步骤 5: 整合到 CI/CD

创建 `.github/workflows/build.yml`:

```yaml
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

### 步骤 6: 合并到主分支

1. 提交守护神确认
2. 签入代码
3. 更新文档

---

## 使用说明

### 日常开发

```bash
# 构建镜像
kas build kas.yml

# 进入开发 shell
kas shell kas.yml

# 查看配置
kas dump kas.yml
```

### CI/CD 构建

```bash
# CI 环境构建
kas build kas.yml:kas/ci.yml

# 使用 lockfile 确保可重复
kas build kas.yml:kas/lock.yml
```

### 调试

```bash
# 交互式调试
kas shell kas.yml -c 'bitbake core-image-aibaos'

# 仅下载源码
kas build kas.yml --task fetch

# 详细日志
kas build kas.yml -l debug
```

---

## 零风险迁移策略

1. **并行运行**: 保留原有 bitbake 工作流
2. **测试分支**: 在 feature/kas-integration 分支测试
3. **逐步过渡**: 先用 kas 管理仓库，再过渡到完整构建
4. **文档同步**: 更新 README.md 和 CONTRIBUTING.md

---

## 附录

### 常用命令

| 命令 | 用途 |
|-----|------|
| `kas dump kas.yml` | 查看有效配置 |
| `kas checkout kas.yml` | 签出仓库 |
| `kas build kas.yml` | 构建镜像 |
| `kas shell kas.yml` | 进入环境 |
| `kas lock kas.yml` | 生成 lockfile |

### 相关文档

- [kas 官方文档](https://kas.readthedocs.io/)
- [kas 集成详细文档](docs/kas-integration.md)
- [kas 使用示例](docs/kas-examples.md)

---

**方案版本**: 1.0  
**更新日期**: 2026-03-12  
**状态**: 待审核
