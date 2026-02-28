require recipes-core/images/core-image-minimal.bb
SUMMARY = "AIBAOS image."

IMAGE_INSTALL:append = " fastfetch os-release"