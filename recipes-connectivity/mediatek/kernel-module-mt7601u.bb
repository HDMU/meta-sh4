SUMMARY = "mediatek 7601 v3.0.0.4"
HOMEPAGE = "www.mediatek.com"
SECTION = "base"
PRIORITY = "required"
PACKAGE_ARCH = "${MACHINE_ARCH}"
LICENSE = "GPLv2"
require conf/license/license-gplv2.inc

COMPATIBLE_MACHINE = "adb_box|arivalink200|atemio520|atemio530|atevio7500|cuberevo|cuberevo_250hd|cuberevo_2000hd|cuberevo_3000hd|cuberevo_mini|cuberevo_mini2|cuberevo_9500hd|vip1_v2|pace7241|vip2_v1|fortis_hdbox|hl101|hs7110|hs7119|hs7420|hs7429|hs7810a|hs7819|ipbox55|ipbox99|ipbox9900|sagemcom88|octagon1008|spark|spark7162|tf7700|ufc960|ufs910|ufs912|ufs913|ufs922|vitamin_hd5000"

inherit module

SRCREV = "${AUTOREV}"

FILESEXTRAPATHS_prepend := "${THISDIR}/mt7601u:"

SRC_URI = " \
	git://github.com/OpenVisionE2/sh4-driver.git;protocol=git \
	file://RT2870STA.dat"

SRC_URI[md5sum] = "f52adec25e8b8175a4bc61b59409ea16"
SRC_URI[sha256sum] = "1f9ffc895054e9d7884f88186e7ba6f83a0f1be0830795603ba0f7862f301e6e"

S = "${WORKDIR}/git/wireless/mt7601u"

EXTRA_OEMAKE = "-e MAKEFLAGS="

CLEANBROKEN = "1"

do_compile() {
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
	oe_runmake KERNEL_PATH=${STAGING_KERNEL_DIR} \
		KERNEL_SRC=${STAGING_KERNEL_DIR} \
		KERNEL_VERSION=${KERNEL_VERSION} \
		-C ${STAGING_KERNEL_DIR} \
		O=${STAGING_KERNEL_BUILDDIR} \
		${@d.getVar('MACHINE',1).upper()}=1 \
		M=${S} V=1
		ARCH=sh
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra/wireless
	install -m 0644 ${S}/mt7601Usta.ko ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra/wireless
	install -d ${D}${sysconfdir}/Wireless/MT7601U
	install -m 0644 ${WORKDIR}/RT2870STA.dat ${D}/etc/Wireless/MT7601U/RT2870STA.dat
}

FILES_${PN}_append = "${sysconfdir}/Wireless"
