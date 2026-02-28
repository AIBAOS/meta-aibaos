# meta-aibaos/recipes-support/fastfetch/fastfetch_%.bbappend

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += " \
    file://config.jsonc \
    file://aibaos-logo \
    "

do_install:append() {
    install -d ${D}${sysconfdir}/fastfetch
    install -m 0644 ${WORKDIR}/config.jsonc ${D}${sysconfdir}/fastfetch/config.jsonc
    install -m 0644 ${WORKDIR}/aibaos-logo ${D}${sysconfdir}/fastfetch/aibaos-logo
}

# 确保包包含了配置文件
FILES:${PN} += "${sysconfdir}/fastfetch/config.jsonc"