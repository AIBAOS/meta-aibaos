# meta-aibaos kas 集成方案

## 文档信息

- **版本**: 1.0
- **日期**: 2026-03-12
- **状态**: 待审核
- **作者**: 兵部 (软件工程、系统架构)

---

## 目录

1. [项目 overview](#项目-overview)
2. [kas 工具简介](#kas-工具简介)
3. [当前仓库结构分析](#当前仓库结构分析)
4. [kas 配置设计方案](#kas-配置设计方案)
5. [实施步骤](#实施步骤)
6. [最佳实践](#最佳实践)

---

## 项目 overview

### 项目名称
meta-aibaos - AIBAO NAS 系统的 Yocto 构建层

### 仓库信息
- **GitHub**: https://github.com/AIBAOS/meta-aibaos
- **分支**: 主分支 (main)
- **Yocto 支持**: scarthgap

### 当前构建方式
手动使用 bitbake 构建，需要手动管理：
- 层依赖
- 配置文件生成
- 依赖下载和解析

### 优化目标
使用 kas 工具自动化构建配置管理，实现：
- 声明式构建环境配置
- 可重复的构建环境
- CI/CD 流水线集成
- 多配置构建支持

---

## kas 工具简介

### 什么是 kas?
kas 是一个用于 Yocto Project 和 OpenEmbedded 构建系统的配置工具。它的主要功能包括：

- **仓库管理**: 自动克隆和检出 Yocto 层
- **配置生成**: 自动生成 bblayers.conf 和 local.conf
- **环境准备**: 设置 BitBake 构建环境
- **多配置支持**: 支持多配置 (multiconfig) 构建

### kas 核心优势

| 特性 | 优势 |
|------|------|
| 声明式配置 | YAML 格式易于版本控制 |
| 可重复构建 | lockfile 机制确保精确版本 |
| CI/CD 就绪 | 容器化支持和环境隔离 |
| 模块化 | 可组合的配置文件 |

### kas 基本概念

#### 配置文件结构
```yaml
header:
  version: 21  # 配置格式版本

machine: <machine>  # 目标机器
distro: <distro>    # 目标发行版

repos:             # 仓库定义
  <repo-id>:
    url: <git-url>
    branch: <branch>
    layers:
      <layer-name>: null

# 可选：配置头部
local_conf_header:
  <name>: |
    # local.conf 内容
```

#### 主要命令
```bash
# 构建项目
kas build <config.yml>

# 进入构建环境 shell
kas shell <config.yml>

# 更新 lockfile
kas lock <config.yml>

# 查看配置
kas dump <config.yml>
```

---

## 当前仓库结构分析

### 目录结构
```
meta-aibaos/
├── conf/                    # 层配置
│   ├── layer.conf          # 层定义
│   └── distro/             # 发行版配置
│       └── aibaos.conf
├── recipes-core/           # 核心配方
│   └── images/
│       └── core-image-aibaos.bb
├── recipes-kernel/         # 内核配方
│   └── linux/
│       └── linux-yocto_%.bbappend
├── recipes-support/        # 支持组件
│   └── fastfetch/
│       ├── fastfetch_%.bbappend
│       └── files/
│           ├── aibaos-logo
│           └── config.jsonc
├── docs/                   # 文档
├── .github/                # GitHub 配置
│   └── PULL_REQUEST_TEMPLATE.md
├── README.md              # 项目说明
└── CONTRIBUTING.md        # 贡献指南
```

### 当前配置文件

#### conf/layer.conf
```bitbake
BBPATH .= ":${LAYERDIR}"
BBFILES += "${LAYERDIR}/recipes-*/*/*.bb \
            ${LAYERDIR}/recipes-*/*/*.bbappend"

BBFILE_COLLECTIONS += "meta-aibaos"
BBFILE_PATTERN_meta-aibaos = "^${LAYERDIR}/"
BBFILE_PRIORITY_meta-aibaos = "6"

LAYERDEPENDS_meta-aibaos = "core"
LAYERDEPENDS_meta-aibaos += "meta-complementary"
LAYERSERIES_COMPAT_meta-aibaos = "scarthgap"
```

#### conf/distro/aibaos.conf
```bitbake
DISTRO_NAME = "iBaOS"
DISTRO_VERSION = "0.0.1"
DISTRO_CODENAME = "core-alpha"
SDK_VENDOR = "-aibaos"
MAINTAINER = "qliangw@163.com"

INIT_MANAGER = "systemd"
VIRTUAL-RUNTIME_init_manager = "systemd"
VIRTUAL-RUNTIME_initscripts = "systemd-compat-units"
DISTRO_FEATURES:append = " systemd"
DISTRO_FEATURES_BACKFILL_CONSIDERED:append = " sysvinit"
```

#### recipes-core/images/core-image-aibaos.bb
```bitbake
require recipes-core/images/core-image-minimal.bb
SUMMARY = "AIBAOS image."

IMAGE_INSTALL:append = " fastfetch os-release"
```

### 依赖分析

#### 直接依赖
- **core**: OpenEmbedded Core
- **meta-poky**: Poky 发行版
- **meta-yocto-bsp**: Yocto BSP 层

#### 可选依赖
- **meta-oe**: OpenEmbedded 组件
- **meta-networking**: 网络相关配方
- **meta-python**: Python 相关配方

---

## kas 配置设计方案

### 配置文件结构

```
meta-aibaos/
├── kas.yml                 # 主配置文件
└── kas/                    # 配置子目录
    ├── build.yml          # 构建配置
    ├── dev.yml            # 开发环境配置
    ├── ci.yml             # CI/CD 配置
    ├── lock.yml           # 锁定文件
    ├── multiconfig.yml    # 多配置支持
    └── overlay.yml        # 覆盖配置
```

### 主配置文件 (kas.yml)

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

### 配置文件说明

#### kas.yml - 基础配置
- 定义仓库和层依赖
- 设置机器和发行版
- 包含开发、CI、构建等子配置

#### kas/build.yml - 构建配置
- 环境变量设置
- 构建优化参数
- 本地配置头部

#### kas/dev.yml - 开发配置
- 交互式 shell 环境
- 调试工具支持
- 开发优化设置

#### kas/ci.yml - CI/CD 配置
- 容器化构建支持
- 构建证明 (attestation)
- 并行构建设置

#### kas/lock.yml - 锁定文件
- 精确版本锁定
- 可重复构建保证
- 更新策略

#### kas/multiconfig.yml - 多配置
- 多目标构建支持
- 配置组合
- 资源隔离

#### kas/overlay.yml - 覆盖配置
- 自定义层添加
- 优先级调整
- 覆盖默认配置

---

## 实施步骤

### 步骤 1: 安装 kas 工具

```bash
# 方法 1: 使用 pipx (推荐)
pipx install kas

# 方法 2: 使用系统包管理器
sudo apt install kas

# 方法 3: 使用容器
docker run -it --rm ghcr.io/siemens/kas/kas:5.2 kas --version
```

### 步骤 2: 验证配置文件

```bash
# 检查配置语法
cd /home/node/.openclaw/workspace/silijian/meta-aibaos
kas dump kas.yml
```

### 步骤 3: 初始化构建环境

```bash
# 签出所有仓库
kas checkout kas.yml

# 构建图像
kas build kas.yml
```

### 步骤 4: 更新锁定文件

```bash
# 生成初始锁文件
kas lock kas.yml

# 更新锁文件
kas update-lock kas.yml
```

### 步骤 5: 整合到 CI/CD

#### GitHub Actions 示例

```yaml
name: Build AIBAOS

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Install kas
        uses: ps kotlin/kas-action@v1
      - name: Build AIBAOS
        run: kas build kas.yml
```

### 步骤 6: 验证构建

#### 本地验证
```bash
# 进入开发 shell
kas shell kas.yml

# 在 shell 中手动验证
cd build
bitbake core-image-aibaos
```

#### CI 验证
```bash
# CI 中运行测试
kas build kas.yml --target core-image-aibaos
```

### 步骤 7: 文档和培训

1. 更新项目 README.md 添加 kas 使用说明
2. 更新 CONTRIBUTING.md 添加 kas 相关贡献指南
3. 为团队成员提供 kas 使用培训

---

## 最佳实践

### 版本管理

1. **使用 lockfile**: 确保所有环境使用相同版本
2. **定期更新**: 订阅 Yocto 发行版更新
3. **版本锁定**: 生产环境使用精确 commit

### 仓库结构

```
meta-aibaos/
├── kas.yml          # 主配置，引用子配置
├── kas/
│   ├── build.yml    # 构建配置
│   ├── dev.yml      # 开发配置
│   └── ci.yml       # CI 配置
└── kas/lock.yml     # 锁定文件
```

### 配置组织原则

1. **单一源**: kas.yml 作为入口点
2. **模块化**: 每个配置文件职责明确
3. **可组合**: 支持命令行组合配置

### CI/CD 集成

1. **容器化**: 使用 kas-container 保证环境一致性
2. **缓存**: 利用 SSTATE_MIRRORS 加速构建
3. **证明**: 启用 build_attestation 跟踪构建

### 调试技巧

```bash
# 查看有效配置
kas dump kas.yml

# 查看仓库状态
kas show-recipe <recipe> kas.yml

# 交互式调试
kas shell kas.yml -c 'bitbake -e <recipe>'
```

### 性能优化

1. **并行构建**: 设置 BB_NUMBER_THREADS 和 PARALLEL_MAKE
2. **共享缓存**: 使用 SSTATE_DIR 共享构建缓存
3. **增量构建**: 利用 lockfile 避免不必要的更新

---

## 迁移清单

### 迁移前检查

- [ ] 确认当前构建环境可正常工作
- [ ] 备份现有配置文件
- [ ] 创建测试分支

### 迁移步骤

1. [ ] 安装 kas 工具
2. [ ] 创建 kas 配置文件
3. [ ] 测试 `kas dump kas.yml` 输出
4. [ ] 测试 `kas checkout kas.yml`
5. [ ] 测试 `kas build kas.yml`
6. [ ] 验证构建产物
7. [ ] 更新文档
8. [ ] 合并到 main 分支

### 回滚计划

如需回滚:
1. 保持 kas.yml 和当前配置并行
2. 使用 `git checkout HEAD~1` 恢复旧配置
3. 清理 kas 工作目录 `rm -rf kas_work* build`

---

## 后续优化

### 短期优化 (1-2 周)

1. [ ] 添加 CI/CD 集成
2. [ ] 实现构建缓存策略
3. [ ] 文档完善

### 中期优化 (1-2 月)

1. [ ] 多配置构建支持
2. [ ] 容器化构建环境
3. [ ] 构建证明和审计

### 长期优化 (3 月+)

1. [ ] 自动化版本更新流程
2. [ ] 监控和告警构建失败
3. [ ] 性能分析和优化

---

## 参考资源

### 官方文档
- [kas 官方文档](https://kas.readthedocs.io/)
- [Yocto Project 文档](https://docs.yoctoproject.org/)
- [OpenEmbedded wiki](https://openembedded.org/wiki/Main_Page)

### 示例配置
- [kas 示例配置](https://github.com/siemens/kas/tree/master)
- [Poky kas 配置](https://git.yoctoproject.org/poky/tree/kas)

### 社区支持
- [kas Mailing List](https://lists.yoctoproject.org/g/kas)
- [Yocto Slack](https://chat.yoctoproject.org/)

---

## 附录

### 附录 A: 常见问题

**Q: kas 和 bitbake 的区别?**
A: kas 是 bitbake 的配置管理工具，负责环境设置；bitbake 是构建工具。

**Q: 如何切换 Yocto 版本?**
A: 修改 kas.yml 中的 branch 字段，重新生成 lockfile。

**Q: 如何添加自定义层?**
A: 在 repos 部分添加层定义，或使用 overlay.yml。

### 附录 B: 命令速查

```bash
# 查看配置
kas dump kas.yml

# 签出仓库
kas checkout kas.yml

# 构建
kas build kas.yml

# 进入 shell
kas shell kas.yml

# 更新 lockfile
kas lock kas.yml

# 执行任意命令
kas shell kas.yml -c 'bitbake core-image-aibaos'
```

### 附录 C: 环境变量

| 变量 | 说明 |
|------|------|
| KAS_WORK_DIR | 工作目录 |
| KAS_BUILD_DIR | 构建目录 |
| KAS_DISTRO | 覆盖 distro 设置 |
| KAS_MACHINE | 覆盖 machine 设置 |
| KAS_TARGET | 覆盖目标 |

---

**文档结束**  
2026-03-12 | 兵部 | AIBAOS 项目
