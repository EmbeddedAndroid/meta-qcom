require linux-qcom-next_git.bb

DESCRIPTION = "Linux ${PV} kernel for the Eliza EVK: qcom-next with the Eliza EVK series on top"

COMPATIBLE_MACHINE = "(eliza)"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-qcom-eliza:"

# eliza/camx-v4: eliza/integration 5124d7d4eb49 plus the CamX camera
# nodes and the gcc camera AXI sleep stage resets.
SRCREV = "990fbc13e8a3feaf1cd89daa89f91ca37809bc53"

SRC_URI = " \
    git://github.com/EmbeddedAndroid/linux.git;protocol=https;branch=eliza/camx-v4 \
    file://configs/bsp-additions.cfg \
    file://configs/no-smccc-soc-id.cfg \
"

LINUX_QCOM_NEXT_FIXES = ""

BBCLASSEXTEND = ""
