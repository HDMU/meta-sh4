require conf/machine/include/oem-sh4.inc
require conf/machine/include/wifi-sh4.inc

BOX_BRAND = "fulan"

PREFERRED_PROVIDER_virtual/kernel = "linux-sh4-${MACHINE}"

ROOTFS_FILE = "e2jffs2.img"
KERNEL_FILE = "uImage"

IMAGE_FSTYPES += "sparkusbimg"

MACHINE_EXTRA_RRECOMMENDS += "\
    ntfs-3g \
    "

MACHINE_ESSENTIAL_EXTRA_RDEPENDS += "\
    stlirc \
    flash-tools \
    mountspark \
"

EXTRA_IMAGEDEPENDS += "\
    libstgles \
    "

MACHINE_FEATURES += "hdmicec ci directfb sh4booster modutils-spark textlcd 7segment"

DISTRO_FEATURES_append += "gtk-directfb"
