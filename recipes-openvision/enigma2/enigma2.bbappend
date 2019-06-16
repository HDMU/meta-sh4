CXXFLAGS_append_sh4 += " -std=c++11 -fPIC -fno-strict-aliasing "

DEPENDS_append_sh4 += "\
	libmme-image libmme-host \
	"

RDEPENDS_${PN}_append_sh4 += "\
	libmme-host \
	alsa-utils-amixer-conf \
	"

EXTRA_OECONF_sh4 = "\
	--enable-${MACHINE} --with-lcd \
	--with-libsdl=no --with-boxtype=${MACHINE} \
	--with-boxbrand="${BOX_BRAND}" \
	--enable-dependency-tracking \
	ac_cv_prog_c_openmp=-fopenmp \
	${@get_crashaddr(d)} \
	${@bb.utils.contains("MACHINE_FEATURES", "textlcd", "--with-textlcd" , "", d)} \
	${@bb.utils.contains_any("MACHINE_FEATURES", "7segment 7seg", "--with-7segment" , "", d)} \
	BUILD_SYS=${BUILD_SYS} \
	HOST_SYS=${HOST_SYS} \
	STAGING_INCDIR=${STAGING_INCDIR} \
	STAGING_LIBDIR=${STAGING_LIBDIR} \
	"

PV_sh4 = "sh4+git${SRCPV}"
PKGV_sh4 = "sh4+git${GITPKGV}"

SRC_URI_sh4 = "git://github.com/OpenVisionE2/enigma2-openvision-sh4.git;branch=develop;name=enigma2"
