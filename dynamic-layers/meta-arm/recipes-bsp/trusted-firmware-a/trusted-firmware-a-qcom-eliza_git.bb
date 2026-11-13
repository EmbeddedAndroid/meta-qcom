require trusted-firmware-a-qcom.inc

DEPENDS += "optee-os-qcom-eliza"

# Eliza support has not reached the qualcomm-linux tree yet.
PV = "2.15.0-qcom-eliza+git"

SRC_URI = "git://github.com/EmbeddedAndroid/arm-trusted-firmware.git;protocol=https;name=tfa;branch=qcom/eliza-next"
SRCREV_tfa = "a3ab19a9db87a7824092dd60b1789fa17772b4a8"

# Its license text is newer than the qualcomm-linux tree's.
LIC_FILES_CHKSUM:remove = "file://docs/license.rst;md5=6ed7bace7b0bc63021c6eba7b524039e"
LIC_FILES_CHKSUM += "file://docs/license.rst;md5=ced8bb28ed6b08efddd6de5401f8b61b"

COMPATIBLE_MACHINE:aarch64 = "(eliza)"

# The MTP platform port covers every Eliza board.
TFA_PLATFORM = "eliza_mtp"
FIP_ELF_ADDR = "0xa7000000"
QCOM_TFA_FIP_MBN = "-v7 uefi"

# XBL authenticates BL2, the TZ image, against the Qualcomm root of trust
# even with secure boot disabled, and the Eliza TZ Security Profile binds it
# to the SoC hardware version.
QCOM_FIRMWARE_SIGN_MODE ?= "QTI-REMOTE"
QCOM_FIRMWARE_SIGN_PLATFORM_BINDING ?= "SOC-HW-VER"
