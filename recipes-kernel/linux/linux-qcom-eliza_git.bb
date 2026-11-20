require linux-qcom-next_git.bb

DESCRIPTION = "Linux ${PV} kernel for the Eliza EVK: qcom-next with the Eliza EVK series on top"

COMPATIBLE_MACHINE = "(eliza)"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-qcom-eliza:"

# eliza/integration: qcom-next 17bbb4b37bbc, the LINUX_QCOM_NEXT_FIXES of
# linux-qcom-next, then the Eliza EVK boot, HDMI, camera and audio series.
SRCREV = "a2355674e3037bbd857ebd6334f9e0ef5ef3730a"

SRC_URI = " \
    git://github.com/EmbeddedAndroid/linux.git;protocol=https;branch=eliza/integration \
    file://configs/bsp-additions.cfg \
    file://configs/no-smccc-soc-id.cfg \
"

LINUX_QCOM_NEXT_FIXES = ""

BBCLASSEXTEND = ""
