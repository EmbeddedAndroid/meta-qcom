require linux-qcom-next_git.bb

DESCRIPTION = "Linux ${PV} kernel for the Eliza EVK: qcom-next with the Eliza EVK series on top"

COMPATIBLE_MACHINE = "(eliza)"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-qcom-eliza:"

# eliza/integration: qcom-next 17bbb4b37bbc, the LINUX_QCOM_NEXT_FIXES of
# linux-qcom-next, then the Eliza EVK boot, HDMI, display colour and
# writeback, camera (IMX577 on CAM0, OV9282 on CAM1) and audio series.
SRCREV = "5124d7d4eb49e2e2baa910029f784c84ded94eaa"

SRC_URI = " \
    git://github.com/EmbeddedAndroid/linux.git;protocol=https;branch=eliza/integration \
    file://configs/bsp-additions.cfg \
    file://configs/no-smccc-soc-id.cfg \
"

LINUX_QCOM_NEXT_FIXES = ""

BBCLASSEXTEND = ""
