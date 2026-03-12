SUMMARY = "AIBAOS NAS Core Services"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI += "file://aibaos-init.service \
            file://aibaos-storage.service"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/aibaos-init.service ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/aibaos-storage.service ${D}${systemd_system_unitdir}/
}

FILES:${PN} += "${systemd_system_unitdir}"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "aibaos-init.service aibaos-storage.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
