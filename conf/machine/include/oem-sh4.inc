# We know about rtc
include conf/machine/include/build-extras.inc

IMAGEDIR ?= "enigma2"
MTD_KERNEL = "mtd5"
MTD_ROOTFS = "mtd6"

KERNEL_MODULE_PACKAGE_SUFFIX = ""

TARGET_ARCH = "sh4"
DEFAULTTUNE = "sh4"

GCCVERSION = "4.9.4"
GLIBCVERSION = "2.25"

HOSTTOOLS_NONFATAL += "whoami"

OLDEST_KERNEL = "2.6.32"

PREFERRED_PROVIDER_virtual/kernel = "linux-sh4"
PREFERRED_VERSION_directfb = "1.4.12"
PREFERRED_VERSION_rt8812au = "4.3.14"
PREFERRED_VERSION_samba = "3.%"
PREFERRED_PROVIDER_lirc = "stlirc"
PREFERRED_PROVIDER_virtual/egl = "libstgles"
PREFERRED_PROVIDER_virtual/libgles1 = "libstgles"
PREFERRED_PROVIDER_virtual/enigma2-mediaservice ?= "enigma2-plugin-systemplugins-servicemp3epl"

# If desired without gst, then enable here and disable servicemp3epl
# build without gst (servicesh4) please disable Serviceapp for build 
# PREFERRED_PROVIDER_virtual/enigma2-mediaservice ?= "enigma2-plugin-systemplugins-servicesh4"

SERIAL_CONSOLE = "115200 ttyAS0"

DVBPROVIDER = "kernel"

MACHINE_FEATURES += "alsa ext2 serial dvb-c 3dtv switchoff textlcd singlecore legacykernel usbconsole"
DISTRO_FEATURES_append += "libsigc2"

KERNEL_IMAGETYPE = "uImage"
#KERNEL_OUTPUT = "${KERNEL_IMAGETYPE}"

JFFS2_ERASEBLOCK = "0x20000"
JFFS2_ENDIANNESS = "${@oe.utils.conditional('SITEINFO_ENDIANNESS', 'le', '--little-endian', '--big-endian', d)}"
EXTRA_IMAGECMD_jffs2 = "--pad ${JFFS2_ENDIANNESS} --eraseblock=${JFFS2_ERASEBLOCK} --no-cleanmarkers"
MKUBIFS_ARGS = "--pad ${JFFS2_ENDIANNESS} --eraseblock=${JFFS2_ERASEBLOCK} --no-cleanmarkers"
DVBMEDIASINK_CONFIG = "--with-wma --with-wmv --with-pcm --with-dtsdownmix --with-eac3"

IMAGE_FSTYPES ?= "jffs2"
# For debugging.. but lets enable anyways..

IMAGE_CLASSES += "usb_image-spark"
INITRAMFS_FSTYPES =  "ext2.gz sparkupdt"

IMAGE_CMD_jffs2_prepend = "\
    rm -rf ${IMAGE_ROOTFS}/tmp/*; \
    "

IMAGE_CMD_jffs2_append = "\
    ; \
    while [ ! -s ${IMGDEPLOYDIR}/${IMAGE_NAME}.rootfs.jffs2 ]; do sleep 1; done ; \
    cp ${IMGDEPLOYDIR}/${IMAGE_NAME}.rootfs.jffs2 ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.jffs2; \
    mkdir -p ${DEPLOY_DIR_IMAGE}/${IMAGEDIR}; \
    while [ ! -s ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.jffs2 ]; do sleep 1; done ; \
    mv ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.jffs2 ${DEPLOY_DIR_IMAGE}/${IMAGEDIR}/${ROOTFS_FILE}; \
    cp ${DEPLOY_DIR_IMAGE}/uImage ${DEPLOY_DIR_IMAGE}/${IMAGEDIR}/${KERNEL_FILE}; \
    cd ${DEPLOY_DIR_IMAGE}; \
    zip ${IMAGE_NAME}_usb.zip ${IMAGEDIR}/*; \
    rm -f ${DEPLOY_DIR_IMAGE}/*.jffs2; \
    rm -f ${DEPLOY_DIR_IMAGE}/*.manifest; \
    rm -rf ${IMAGEDIR}; \
    "

# Add uImage in tar image boot directory
IMAGE_CMD_tar_prepend = "\
    cp ${DEPLOY_DIR_IMAGE}/uImage ${IMAGE_ROOTFS}/boot/${KERNEL_FILE}; \
    "
	
MACHINE_ESSENTIAL_EXTRA_RDEPENDS += "\
    stslave-fw \
    ustslave \
    stfbcontrol \
    fp-control \
    stinit \
    sh4-dvb-modules \
    mtd-utils-jffs2 \
    "

EXTRA_IMAGEDEPENDS += "\
    libpcre \
    "

MACHINE_EXTRA_RDEPENDS += "\
    libpcre \
    "

include conf/machine/include/autoload-filesystems.inc
require conf/machine/include/tune-sh4.inc
require conf/machine/include/soc-family.inc

EXTRA_OECONF_append_pn-enigma2 += " --with-sh4stb"
