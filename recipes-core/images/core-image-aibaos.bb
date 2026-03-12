require recipes-core/images/core-image-minimal.bb
SUMMARY = "AIBAOS image."

# NAS 基础组件：存储管理、文件共享、系统管理、网络工具、监控
IMAGE_INSTALL:append = " \
    lvm2 mdadm \
    samba nfs-utils \
    sudo shadow \
    openssh curl wget rsync \
    htop smartmontools \
    net-tools iproute2 \
    fastfetch \
"