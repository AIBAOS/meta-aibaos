# meta-aibaos kas 使用示例

## 快速开始

### 1. 检查 kas 版本

```bash
kas --version
```

### 2. 查看配置

```bash
cd /path/to/meta-aibaos
kas dump kas.yml
```

### 3. 构建镜像

```bash
kas build kas.yml
```

---

## 常用场景

### 场景 1: 开发调试

```bash
# 进入交互式 shell
kas shell kas.yml

# 在 shell 中
cd build
bitbake core-image-aibaos
bitbake -c clean core-image-aibaos
```

### 场景 2: CI/CD 构建

```bash
kas build kas.yml:kas/ci.yml
```

### 场景 3: 多配置构建

```bash
kas build kas.yml:kas/multiconfig.yml \
    --target mc:core-image-aibaos \
    --target mc:core-image-minimal
```

### 场景 4: 自定义配置

```bash
# 使用自定义 machine
KAS_MACHINE=raspberrypi4-64 kas build kas.yml

# 使用自定义 distro
KAS_DISTRO=poky-tiny kas build kas.yml

# 覆盖目标
KAS_TARGET=core-image-full kas build kas.yml
```

---

## 诊断命令

### 查看配置信息

```bash
# 显示合并后的配置
kas dump kas.yml

# 显示仓库状态
kas show-recipe core-image-aibaos kas.yml

# 查看有效配置变量
kas shell kas.yml -c 'bitbake -e | grep ^DISTRO'
```

### 调试命令

```bash
# 仅下载源码
kas build kas.yml --task fetch

# 仅解压源码
kas build kas.yml --task unpack

# 仅编译
kas build kas.yml --task compile
```

---

## 故障排查

### 常见问题 1: 仓库签出失败

```bash
# 强制重新签出
kas checkout kas.yml --force

# 清理工作目录
rm -rf kas_work*
kas checkout kas.yml
```

### 常见问题 2: 配置语法错误

```bash
# 检查 YAML 语法
python3 -c "import yaml; yaml.safe_load(open('kas.yml'))"

# 逐步添加配置
kas dump kas.yml:kas/build.yml
```

### 常见问题 3: 构建失败

```bash
# 查看 bitbake 日志
cat build/tmp/log/cooker/*/history

# 详细调试模式
kas build kas.yml -l debug

# 清理并重试
bitbake -c clean core-image-aibaos
bitbake core-image-aibaos
```

---

## 性能优化

### 1. 并行构建

```bash
# 设置并行线程数
KAS_PARALLEL_MAKE="-j$(nproc)" kas build kas.yml
```

### 2. 共享缓存

```bash
# 使用共享 sstate 目录
export SSTATE_DIR=/shared/sstate-cache
kas build kas.yml
```

### 3. 镜像源

```bash
# 使用国内镜像
export KAS_PREMIRRORS="http://.*\.yocto\.io/ https://mirrors.tuna.tsinghua.edu.cn/yocto/\nhttp://.*\.openembedded\.org/ https://mirrors.tuna.tsinghua.edu.cn/oe-core/\n"
kas build kas.yml
```

---

## 进阶用法

### 使用 lockfile

```bash
# 生成 lockfile
kas lock kas.yml

# 使用 lockfile 构建
kas build kas.yml:kas/lock.yml
```

### 添加自定义层

```bash
# 创建 overlay 配置
kas build kas.yml:kas/overlay.yml
```

### 容器化构建

```bash
# 使用 kas-container
./kas-container build kas.yml
```

---

## 脚本集成

### 构建脚本示例

```bash
#!/bin/bash
# build-aibaos.sh

set -e

echo "=== 构建 AIBAOS ==="

# 检查 kas
if ! command -v kas &> /dev/null; then
    echo "请先安装 kas: pipx install kas"
    exit 1
fi

# 构建
kas build kas.yml || {
    echo "构建失败"
    exit 1
}

# 输出结果
echo "构建完成！"
echo "镜像位置: build/tmp/deploy/images/x86-64/"
```

### CI 脚本示例

```bash
#!/bin/bash
# ci-build.sh

set -e

# 设置环境
export KAS_WORK_DIR=/tmp/kas-work
export SSTATE_DIR=/tmp/sstate-cache

# 清理旧工作
rm -rf $KAS_WORK_DIR $SSTATE_DIR
mkdir -p $KAS_WORK_DIR $SSTATE_DIR

# 构建
kas build kas.yml:kas/ci.yml

# 上传构建产物
# (添加你的上传逻辑)
```

---

## 参考资源

- [kas 官方文档](https://kas.readthedocs.io/)
- [Yocto Project 文档](https://docs.yoctoproject.org/)
- [OpenEmbedded wiki](https://openembedded.org/wiki/Main_Page)

---

*更新时间: 2026-03-12*
