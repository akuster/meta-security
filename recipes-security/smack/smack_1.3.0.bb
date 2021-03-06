DESCRIPTION = "Selection of tools for developers working with Smack"
HOMEPAGE = "https://github.com/smack-team/smack"
SECTION = "Security/Access Control"
LICENSE = "LGPL-2.1"

LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRCREV = "478f68d182c5070f510482194d3e097b11c21907"
SRC_URI = " \
	git://github.com/smack-team/smack.git;branch=v1.3.x \
	file://smack_generator_make_fixup.patch \
	file://run-ptest"

PV = "1.3.0+git${SRCPV}"

inherit autotools update-rc.d pkgconfig ptest ${@bb.utils.contains('VIRTUAL-RUNTIME_init_manager','systemd','systemd','', d)}

S = "${WORKDIR}/git"

PACKAGECONFIG ??= ""
PACKAGECONFIG_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"

PACKAGECONFIG[systemd] = "--with-systemdsystemunitdir=${systemd_system_unitdir}, --without-systemdsystemunitdir, systemd"

do_compile_append () {
	oe_runmake -C ${S}/tests generator
}

do_install_append () {
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${sysconfdir}/smack
	install -d ${D}${sysconfdir}/smack/accesses.d
	install -d ${D}${sysconfdir}/smack/cipso.d
	install ${S}/init/smack.rc ${D}/${sysconfdir}/init.d/smack
}

do_install_ptest () {
	install -d ${D}${PTEST_PATH}/tests
	install ${S}/tests/generator ${D}/${PTEST_PATH}/tests
	install ${S}/tests/generate-rules.sh ${D}${PTEST_PATH}/tests
	install ${S}/tests/make_policies.bash ${D}${PTEST_PATH}/tests
}

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME = "smack"
INITSCRIPT_PARAMS = "start 16 2 3 4 5 . stop 35 0 1 6 ."

FILES_${PN} += "${sysconfdir}/init.d/smack"
FILES_${PN}-ptest += "generator"

RDEPENDS_${PN} += "coreutils"
RDEPENDS_${PN}-ptest += "make bash bc"

BBCLASSEXTEND = "native"
