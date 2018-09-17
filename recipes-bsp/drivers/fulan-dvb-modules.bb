SUMMARY = "Fulan driver modules from TDT"
DESCRIPTION = "Fulan driver modules from TDT"
HOMEPAGE = "http://github.com/Duckbox-Developers"
SECTION = "base"
PRIORITY = "required"
PACKAGE_ARCH = "${MACHINE_ARCH}"
LICENSE = " GPLv2"
LIC_FILES_CHKSUM = "file://${WORKDIR}/COPYING;md5=751419260aa954499f7abaabaa882bbe"

RDEPENDS_${PN} = "stinit"

COMPATIBLE_MACHINE = "adb_box|arivalink200|atemio520|atemio530|atevio7500|cuberevo|cuberevo_250hd|cuberevo_2000hd|cuberevo_3000hd|cuberevo_mini|cuberevo_mini2|fortis_hdbox|hl101|hs7110|hs7119|hs7420|hs7429|hs7810a|hs7819|ipbox55|ipbox99|ipbox9900|sagemcom88|octagon1008|spark|spark7162|tf7700|ufc960|ufs910|ufs912|ufs913|ufs922|vitamin_hd5000"

KV = "2.6.32.71-stm24-0217"
SRCREV = "7f83e008c7ea4c385015ebd08a86f8aea9ab9f9e"

inherit module

PACKAGES = "${PN} ${PN}-dev"

SRCDATE = "20180824"

PV = "${KV}+${SRCDATE}-${MACHINE}"

PR = "r1"

PTI_NP_PATH ?= "/data/pti_np"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI = "\
    git://github.com/PLi-metas/driver.git;protocol=git \
    file://modules.conf \
    file://modules-conf.conf \
" 

FILES_${PN} = "${sysconfdir}/init.d ${sysconfdir}/rcS.d ${sysconfdir}/modules-load.d ${sysconfdir}/modprobe.d /bin"
FILES = ""

S = "${WORKDIR}/git"

EXTRA_OEMAKE = "-e MAKEFLAGS="

CLEANBROKEN = "1"

do_configure_prepend () {
    # if a custom pti source is present, add it to the sources
    if [ -e ${PTI_NP_PATH}/Makefile ]; then
        echo "Found custom pti sources.."
        cp -r ${PTI_NP_PATH} ${S}
    fi
    if [ -L include/multicom ]; then
        rm include/multicom
    fi
    if [ -L multicom ]; then
        rm multicom 
    fi
    ln -sf ${S}/multicom/include ${S}/include/multicom
    export KERNEL_LOCATION="${STAGING_KERNEL_DIR}"
    cp -R ${S}/multicom ${STAGING_KERNEL_DIR}/multicom
}

do_compile() {
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    oe_runmake KERNEL_PATH=${STAGING_KERNEL_DIR}   \
        KERNEL_SRC=${STAGING_KERNEL_DIR}    \
        KERNEL_VERSION=${KERNEL_VERSION}    \
        -C ${STAGING_KERNEL_DIR}   \
	O=${STAGING_KERNEL_BUILDDIR} \
        ${@d.getVar('MACHINE',1).upper()}=1 \
        M=${S} V=1 \
        ARCH=sh \
        DRIVER_TOPDIR="${S}" \
        KERNEL_LOCATION="${STAGING_KERNEL_DIR}" \
        CONFIG_KERNEL_BUILD="${STAGING_KERNEL_BUILDDIR}" \
        CONFIG_KERNEL_PATH="${STAGING_KERNEL_DIR}" \
        CONFIG_MODULES_PATH="${D}" \
        CCFLAGSY="-I${STAGING_DIR_HOST}/usr/include" \
        modules 
}

do_install() {
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    oe_runmake KERNEL_PATH=${STAGING_KERNEL_DIR}   \
        KERNEL_SRC=${STAGING_KERNEL_DIR}    \
        KERNEL_VERSION=${KERNEL_VERSION}    \
        -C ${STAGING_KERNEL_DIR}   \
	O=${STAGING_KERNEL_BUILDDIR} \
        ${@d.getVar('MACHINE',1).upper()}=1 \
        M=${S} V=1 \
        ARCH=sh \
        DRIVER_TOPDIR="${S}" \
        KERNEL_LOCATION="${STAGING_KERNEL_DIR}" \
        CONFIG_KERNEL_BUILD="${STAGING_KERNEL_BUILDDIR}" \
        CONFIG_KERNEL_PATH="${STAGING_KERNEL_DIR}" \
        CONFIG_MODULES_PATH="${D}" \
        CCFLAGSY="-I${STAGING_DIR_HOST}/usr/include" \
        INSTALL_MOD_PATH=${D} modules_install

    # install header files
    install -d ${D}/${includedir}/
    install -m 644 multicom/include/mme.h ${D}/${includedir}
    install -m 644 include/player2/JPEG_VideoTransformerTypes.h ${D}/${includedir}
    install -m 644 include/player2/JPEGDECHW_VideoTransformerTypes.h ${D}/${includedir}
    install -m 644 include/player2/PNGDecode_interface.h ${D}/${includedir}
    install -m 644 include/player2/RLEDecode_interface.h ${D}/${includedir}
    install -m 644 frontcontroller/aotom_spark/aotom_main.h ${D}/${includedir}
    install -m 644 player2/components/include/stddefs.h ${D}/${includedir}

    #install modutils config
    install -d ${D}/${sysconfdir}/modules-load.d
    install -m 644 ${WORKDIR}/modules.conf ${D}/${sysconfdir}/modules-load.d/_${MACHINE}.conf
    install -d ${D}/${sysconfdir}/modprobe.d
    install -m 644 ${WORKDIR}/modules-conf.conf ${D}/${sysconfdir}/modprobe.d/_${MACHINE}.conf
    install -d ${D}/${sysconfdir}/init.d
    install -d ${D}/${sysconfdir}/rcS.d
    install -m 0755 ${WORKDIR}/ddbootup ${D}${sysconfdir}/init.d
    ln -sf ../init.d/ddbootup ${D}${sysconfdir}/rcS.d/S04ddbootup
    install -d ${D}/bin
    install -m 755 ${WORKDIR}/vdstandby ${D}/bin
    install -m 0755 ${WORKDIR}/sh4booster ${D}${sysconfdir}/init.d
    ln -sf ../init.d/sh4booster ${D}${sysconfdir}/rcS.d/S05sh4booster

    # if no pti_np sources are available and a custom pti.ko is present, overwrite the tdt one
    if [ ! -e ${PTI_NP_PATH}/Makefile ]; then
        if [ -e ${PTI_NP_PATH}/pti.ko ]; then
            echo "Found custom pti binary.." 
            install -m 644 ${PTI_NP_PATH}/pti.ko ${D}/lib/modules/${KERNEL_VERSION}/extra/pti/pti.ko
        fi
    fi
	
	find ${D} -name stmcore-display-sti7106.ko | xargs -r rm # we don't have a 7106 chip
}

PACKAGESPLITFUNCS_append = " handle_driver_rename "

python handle_driver_rename () {
    d.setVar("RPROVIDES_fulan-dvb-modules", "fulan-dvb-modules")
    d.setVar("RREPLACES_fulan-dvb-modules", "fulan-dvb-modules")
    d.setVar("RCONFLICTS_fulan-dvb-modules", "fulan-dvb-modules")
}
FILES_${PN}-dev += "${includedir}"
