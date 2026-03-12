# meta-aibaos

AIBAO NAS 系统的 Yocto 构建层。

## 简介

meta-aibaos 是基于 Yocto Project 构建的 AIBAO NAS 系统的核心层，提供：

- 定制化的 Linux 内核配置
- NAS 核心服务组件
- 存储管理工具
- 系统优化配置

## 依赖

- Yocto Project (Kirkstone/Kirkstone)
- OpenEmbedded-Core
- BitBake

## 快速开始

```bash
# 添加层到构建环境
bitbake-layers add-layer meta-aibaos

# 构建镜像
bitbake aibaos-image
```

## 目录结构

```
meta-aibaos/
├── conf/              # 层配置
├── recipes-core/      # 核心组件配方
├── recipes-kernel/    # 内核配方
├── recipes-support/   # 支持组件配方
├── docs/              # 项目文档
├── CONTRIBUTING.md    # 贡献指南
└── docs/
    └── WORKFLOW.md    # 工作流程
```

## 文档

- [贡献指南](CONTRIBUTING.md) - Commit 规范、代码审查流程
- [工作流程](WORKFLOW.md) - 团队协作、审核机制

## 贡献

请阅读 [CONTRIBUTING.md](CONTRIBUTING.md) 了解贡献流程。

## 维护者

AIBAOS Team

## 许可证

MIT License (COPYING.MIT) 和 GPL-3.0 (LICENSE)