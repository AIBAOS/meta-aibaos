# BUILD.md - 构建指南

本文档描述如何构建 AIBAOS NAS 系统镜像。

## 目录

- [环境要求](#环境要求)
- [安装 kas](#安装-kas)
- [构建步骤](#构建步骤)
- [常见问题](#常见问题)

---

## 环境要求

### 硬件要求

| 资源 | 最低要求 | 推荐配置 |
|------|----------|----------|
| CPU | 4 核心 | 8 核心+ |
| 内存 | 8GB | 16GB+ |
| 磁盘 | 50GB 可用空间 | 100GB+ SSD |

### 软件要求

- **操作系统**: Ubuntu 22.04+ / Debian 12+ / Fedora 38+
- **Python**: 3.8 或更高版本
- **git**: 2.28+
- **基础工具**: wget, tar, gcc, make, diffstat, chrpath 等

### 依赖包安装

#### Ubuntu/Debian

```bash
sudo apt update
sudo apt install -y gawk wget git diffstat unzip \
    texinfo gcc build-essential chrpath socat cpio \
    python3 python3-pip python3-pexpect xz-utils \
    debianutils iputils-ping python3-git python3-jinja2 \
    python3-subunit mesa-common-dev zstd liblz4-tool \
    file locales build-essential
```

#### Fedora

```bash
sudo dnf install -y gawk wget git diffstat unzip \
    texinfo gcc build-essential chrpath socat cpio \
    python3 python3-pip python3-pexpect xz-utils \
    iputils ping python3-git python3-jinja2 \
    python3-subunit mesa-libGL-devel zstd lz4 \
    file glibc-langpack-en
```

---

## 安装 kas

### 方法 1: 使用 pipx (推荐)

```bash
# 安装 pipx (如果未安装)
python3 -m pip install --user pipx
python3 -m pipx ensurepath

# 安装 kas
pipx install kas

# 验证安装
kas --version
```

### 方法 2: 使用系统包管理器

```bash
# Ubuntu/Debian
sudo apt install kas

# Fedora
sudo dnf install kas
```

### 方法 3: 使用 pip (不推荐)

```bash
python3 -m pip install --user kas
```

### 方法 4: 使用容器

```bash
# 使用官方 kas 容器
docker run -it --rm \
    -v $(pwd):/workdir \
    ghcr.io/siemens/kas/kas:5.2 \
    kas --version
```

---

## 构建步骤

### 步骤 1: 克隆仓库

```bash
git clone https://github.com/AIBAOS/meta-aibaos.git
cd meta-aibaos
```

### 步骤 2: 验证配置

```bash
# 检查 kas 配置语法
kas dump kas.yml
```

预期输出应显示完整的构建配置，包括仓库、层、机器和发行版信息。

### 步骤 3: 初始化构建环境

```bash
# 签出所有依赖仓库 (首次执行会下载数 GB 数据)
kas checkout kas.yml
```

此步骤会克隆以下仓库：
- poky (Yocto Project)
- meta-openembedded

### 步骤 4: 构建镜像

```bash
# 开始构建 (首次构建约 30-60 分钟)
kas build kas.yml
```

构建过程：
1. 解析 BitBake 配置
2. 下载所有源码和依赖
3. 编译工具链
4. 编译内核和系统包
5. 生成根文件系统
6. 打包镜像

### 步骤 5: 获取构建产物

```bash
# 查看构建产物
ls -la build/tmp/deploy/images/x86-64/

# 主要文件：
# - core-image-aibaos-x86-64.ext4.wic.gz  (完整磁盘镜像)
# - core-image-aibaos-x86-64.rootfs.ext4  (根文件系统)
# - bzImage                                (内核镜像)
```

### 步骤 6: (可选) 生成锁定文件

```bash
# 生成 lockfile 以确保可重复构建
kas lock kas.yml

# 提交到版本控制
git add kas.lock
git commit -m "Add kas lockfile for reproducible builds"
```

---

## 常见问题

### Q1: 构建失败，提示 "No bitbake recipe found"

**原因**: 仓库未正确签出或层未添加到配置。

**解决**:
```bash
# 重新签出仓库
kas checkout kas.yml

# 验证配置
kas dump kas.yml | grep -A 10 "repos"
```

### Q2: 构建过程中断，如何继续？

**原因**: 网络问题或系统中断。

**解决**:
```bash
# 直接重新运行构建命令 (kas 会从中断点继续)
kas build kas.yml

# 或清理后重新构建
kas shell kas.yml -c 'bitbake -c cleansstate core-image-aibaos'
kas build kas.yml
```

### Q3: 构建速度太慢

**优化建议**:

1. **使用本地镜像缓存**:
```bash
# 创建本地镜像配置
cp kas/local.yml.example kas/local.yml
# 编辑 kas/local.yml 填入本地镜像源
```

2. **增加并行度**:
```bash
# 在 kas.yml 中添加或修改 env 部分
env:
  BB_NUMBER_THREADS: "8"
  PARALLEL_MAKE: "-j 8"
```

3. **使用 SSTATE 缓存**:
```bash
# 设置共享 sstate 目录
export SSTATE_DIR=/path/to/shared/sstate
```

### Q4: 磁盘空间不足

**解决**:
```bash
# 清理构建缓存 (会删除所有构建产物)
kas shell kas.yml -c 'bitbake -c cleansstate core-image-aibaos'

# 或只清理下载缓存
rm -rf build/downloads

# 使用 kas 清理命令
kas clean kas.yml
```

### Q5: Python 依赖冲突

**症状**: kas 启动时报错 ImportError 或 ModuleNotFoundError。

**解决**:
```bash
# 使用虚拟环境重新安装 kas
pipx reinstall kas

# 或升级 pipx 环境
pipx upgrade kas
```

### Q6: 如何进入构建环境进行调试？

```bash
# 进入交互式 shell
kas shell kas.yml

# 在 shell 中可以运行任意 bitbake 命令
bitbake -e core-image-aibaos | less
bitbake -g core-image-aibaos  # 生成依赖图
```

### Q7: 如何构建特定配方？

```bash
# 构建单个配方
kas shell kas.yml -c 'bitbake <recipe-name>'

# 示例：构建 fastfetch
kas shell kas.yml -c 'bitbake fastfetch'
```

### Q8: 如何查看构建日志？

```bash
# 查看最近构建日志
tail -f build/tmp/log/cooker/qemux86-64/*.log

# 查看特定配方的日志
ls build/tmp/work/*/fastfetch/*/temp/
```

### Q9: kas.yml 配置不生效

**解决**:
```bash
# 清除 kas 缓存
rm -rf .kas/

# 重新生成配置
kas checkout kas.yml
```

### Q10: 如何在 CI/CD 中使用？

参考 `.github/workflows/build.yml` 示例：

```yaml
name: Build AIBAOS

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Install dependencies
        run: |
          sudo apt update
          sudo apt install -y gawk wget git diffstat unzip \
            texinfo gcc build-essential chrpath socat cpio \
            python3 python3-pip python3-pexpect xz-utils
      
      - name: Install kas
        run: pipx install kas
      
      - name: Build AIBAOS
        run: kas build kas.yml
```

---

## 其他资源

- [README.md](README.md) - 项目概述
- [kas 集成方案](docs/kas-integration.md) - kas 详细文档
- [kas 使用示例](docs/kas-examples.md) - 常用命令
- [工作流程](docs/WORKFLOW.md) - 团队协作流程

---

**最后更新**: 2026-03-12  
**维护者**: AIBAOS Team
