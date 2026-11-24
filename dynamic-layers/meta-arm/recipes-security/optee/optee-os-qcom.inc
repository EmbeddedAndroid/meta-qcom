require recipes-security/optee/optee-os.inc

PV = "4.11.0-qcom-20261026+git"

SRC_TAG = "tag=qcom-next-4.11-20261026"
OPTEE_QCOM_GIT = "git://github.com/qualcomm-linux/optee_os.git;protocol=https;name=optee;nobranch=1;${SRC_TAG}"
SRC_URI = "${OPTEE_QCOM_GIT}"
SRCREV_optee = "bd57d28b5ac226fe108ef96730b650b8a08bcff4"

# Eliza support has not reached the qualcomm-linux tree yet. The OS and
# the TA devkit both build from this tree so that the TAs match the OS.
PV:eliza = "4.11.0-qcom-eliza+git"
OPTEE_QCOM_GIT:eliza = "git://github.com/EmbeddedAndroid/optee_os.git;protocol=https;name=optee;branch=qcom/eliza-next"
SRCREV_optee:eliza = "be844404542f0a82d3d506f6a8798f7ca1c5a42f"

# Enable PKCS#11 TA as part of the OP-TEE build
EXTRA_OEMAKE:append = " CFG_PKCS11_TA=y"

# Copy of do_deploy from meta-arm until we get it fixed to support configurable deploy dir
OPTEE_DEPLOY ?= "optee"
do_deploy() {
    install -d ${DEPLOYDIR}/${MLPREFIX}${OPTEE_DEPLOY}
    install -m 644 ${D}${nonarch_base_libdir}/firmware/* ${DEPLOYDIR}/${MLPREFIX}${OPTEE_DEPLOY}

    install -d ${DEPLOYDIR}/${MLPREFIX}${OPTEE_DEPLOY}/ta
    install -m 644 ${B}/ta/*/*.elf ${DEPLOYDIR}/${MLPREFIX}${OPTEE_DEPLOY}/ta
}
