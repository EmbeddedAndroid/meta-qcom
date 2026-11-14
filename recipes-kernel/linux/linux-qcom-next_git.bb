SECTION = "kernel"

DESCRIPTION = "Linux ${PV} kernel for QCOM devices"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

inherit kernel cml1

COMPATIBLE_MACHINE = "(qcom)"

LINUX_QCOM_FIT_DTB_COMPATIBLE = "conf/machine/include/fit-dtb-compatible-linux-qcom.inc"

LINUX_VERSION ?= "7.3"

PV = "${LINUX_VERSION}+git"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-qcom-next:"

KERNEL_PAHOLE ?= '${@bb.utils.contains("BBFILE_COLLECTIONS", "openembedded-layer", "1", "0", d)}'
do_configure[depends] += '${@oe.utils.vartrue("KERNEL_PAHOLE", "pahole-native:do_populate_sysroot", "", d)}'
EXTRA_OEMAKE += '${@oe.utils.vartrue("KERNEL_PAHOLE", "", "PAHOLE=false", d)}'

# tag: qcom-next-7.3-20261031
SRCREV ?= "17bbb4b37bbc4fc5827b4667445a0c43967c0f48"

SRCBRANCH ?= "nobranch=1"
SRCBRANCH:class-devupstream ?= "branch=qcom-next"

require linux-qcom-localversion.inc
KERNEL_LOCALVERSION ?= "${@qcom_kernel_localversion(d)}"

SRC_URI = "git://github.com/qualcomm-linux/kernel.git;${SRCBRANCH};protocol=https"

# Additional kernel configs.
SRC_URI += " \
    file://configs/bsp-additions.cfg \
"

# Fixes found on the Eliza CQS EVK, most of them in drivers every machine
# of this recipe uses. Each one is in linux-next or posted upstream, carries
# its Upstream-Status and goes once qcom-next has it. The qcom-next tip
# (linux-qcom-next-upstream) does not take them.
LINUX_QCOM_NEXT_FIXES = " \
    file://fixes/0001-arm64-dts-qcom-eliza-cqs-som-Enable-Adreno-A722-GPU.patch \
    file://fixes/0002-ASoC-qcom-q6apm-clear-g_apm-on-driver-removal.patch \
    file://fixes/0003-ASoC-soc-core-copy-the-DAI-link-names-into-the-card.patch \
    file://fixes/0004-scsi-ufs-qcom-Reprogram-ESI-after-a-host-controller-.patch \
    file://fixes/0005-FROMLIST-PM-clk-Report-clock-enable-failures-from-pm.patch \
    file://fixes/0006-FROMLIST-drm-msm-a6xx-fall-back-to-MX-when-gmxc.lvl-.patch \
    file://fixes/0007-FROMLIST-misc-fastrpc-fix-NULL-rpdev-dereference-whe.patch \
    file://fixes/0008-FROMLIST-soundwire-qcom-quiesce-runtime-PM-before-re.patch \
    file://fixes/0009-FROMLIST-media-qcom-Pass-proper-PAS-ID-to-set_remote.patch \
"
LINUX_QCOM_NEXT_FIXES:class-devupstream = ""
SRC_URI += "${LINUX_QCOM_NEXT_FIXES}"

# To build tip of qcom-next branch set preferred
# virtual/kernel provider to 'linux-qcom-next-upstream'
BBCLASSEXTEND = "devupstream:target"
PN:class-devupstream = "linux-qcom-next-upstream"
SRCREV:class-devupstream ?= "${AUTOREV}"

S = "${UNPACKDIR}/${BP}"

KBUILD_DEFCONFIG ?= "defconfig"
KBUILD_DEFCONFIG:qcom-armv7a = "qcom_defconfig"

KBUILD_CONFIG_EXTRA = "${S}/kernel/configs/hardening.config"
KBUILD_CONFIG_EXTRA:append:aarch64 = " ${S}/arch/arm64/configs/prune.config"
KBUILD_CONFIG_EXTRA:append:aarch64 = " ${S}/arch/arm64/configs/qcom.config"
KBUILD_CONFIG_EXTRA:append = " ${@oe.utils.vartrue('DEBUG_BUILD', '${S}/kernel/configs/debug.config', '', d)}"
KBUILD_CONFIG_EXTRA:append:aarch64 = " ${@oe.utils.vartrue('DEBUG_BUILD', '${S}/arch/arm64/configs/qcom_debug.config', '', d)}"

do_configure:prepend() {
    # Use a copy of the 'defconfig' from the actual repo to merge fragments
    cp ${S}/arch/${ARCH}/configs/${KBUILD_DEFCONFIG} ${B}/.config

    # Merge fragment for QCOM value add features
    ${S}/scripts/kconfig/merge_config.sh -m -O ${B} ${B}/.config ${KBUILD_CONFIG_EXTRA} ${@" ".join(find_cfgs(d))}
}
