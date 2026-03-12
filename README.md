# meta-aibaos

AIBAO NAS 系统的 Yocto 构建层。

## 简介

meta-aibaos 是基于 Yocto Project 构建的 AIBAO NAS 系统的核心层，提供：

- 定制化的 Linux 内核配置
- NAS 核心服务组件
- 存储管理工具
- 系统优化配置

## 核心功能

- **存储管理**: LVM2 逻辑卷管理、RAID 阵列 (mdadm)、S.M.A.R.T 磁盘监控
- **文件共享**: Samba (SMB/CIFS)、NFS 网络文件系统
- **系统管理**: systemd 初始化、用户权限管理 (sudo/shadow)
- **网络工具**: SSH 远程访问、网络配置 (iproute2)、数据传输 (rsync/curl/wget)
- **系统监控**: htop 进程监控、smartmontools 磁盘健康检测
- **系统信息**: fastfetch 系统信息展示

## 镜像组成

### 基础系统
- Yocto Project (Scarthgap) + OpenEmbedded-Core
- 定制化 aibaos 发行版配置
- x86-64 架构支持

### 核心组件
- **aibaos-nas**: NAS 核心服务包 (systemd 服务单元)
- **core-image-aibaos**: 最小化 NAS 系统镜像

### 主要软件包
| 类别 | 组件 |
|------|------|
| 存储 | lvm2, mdadm, smartmontools |
| 共享 | samba, nfs-utils, rsync |
| 网络 | openssh, curl, wget, net-tools, iproute2 |
| 系统 | sudo, shadow, htop, fastfetch |

### 层依赖
- poky (meta, meta-poky, meta-yocto-bsp)
- meta-openembedded (meta-oe, meta-networking, meta-python)

## 依赖

- Yocto Project (Scarthgap)
- OpenEmbedded-Core
- BitBake

## 快速开始

### 环境要求

- Ubuntu 22.04+ / Debian 12+ / Fedora 38+
- 最低 50GB 可用磁盘空间 (推荐 100GB+)
- 最低 8GB RAM (推荐 16GB+)
- Python 3.8+
- git, wget, tar, gcc 等基础工具

### 使用 kas (推荐)

```bash
# 1. 安装 kas
pipx install kas

# 2. 克隆仓库
git clone https://github.com/AIBAOS/meta-aibaos.git
cd meta-aibaos

# 3. 构建镜像 (首次构建约 30-60 分钟)
kas build kas.yml

# 4. 构建产物位置
# build/tmp/deploy/images/x86-64/
```

### 配置本地镜像（可选）

如果需要使用本地镜像服务器加速构建，创建 `kas/local.yml` 文件：

```bash
# 复制示例配置
cp kas/local.yml.example kas/local.yml

# 编辑 kas/local.yml，填入你的本地镜像地址
vim kas/local.yml
```

详见 `kas/local.yml.example` 中的配置说明。

### 使用 bitbake (传统方式)

```bash
# 1. 添加层到构建环境
bitbake-layers add-layer meta-aibaos

# 2. 构建镜像
bitbake core-image-aibaos
```

## 目录结构

```
meta-aibaos/
├── conf/              # 层配置
├── recipes-core/      # 核心组件配方
├── recipes-kernel/    # 内核配方
├── recipes-support/   # 支持组件配方
├── docs/              # 项目文档
├── kas.yml            # kas 构建配置 (推荐)
├── kas/               # kas 配置子目录
├── CONTRIBUTING.md    # 贡献指南
└── docs/
    └── WORKFLOW.md    # 工作流程
```

## 文档

- [贡献指南](CONTRIBUTING.md) - Commit 规范、代码审查流程
- [工作流程](docs/WORKFLOW.md) - 团队协作、审核机制
- [kas 集成方案](docs/kas-integration.md) - kas 详细文档
- [kas 使用示例](docs/kas-examples.md) - 常用命令和场景

## 贡献

请阅读 [CONTRIBUTING.md](CONTRIBUTING.md) 了解贡献流程。

## 维护者

AIBAOS Team

## 许可证

MIT License (COPYING.MIT) 和 GPL-3.0 (LICENSE)