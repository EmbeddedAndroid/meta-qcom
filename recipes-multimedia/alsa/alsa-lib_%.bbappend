FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

# Name based access to the controls a UCM ctl remap passes through fails
# with alsa-lib 1.2.16 once alsactl has saved the remap. Drop once an
# alsa-lib release carries the fix.
SRC_URI:append:qcom = " file://0001-control-remap-pass-unmapped-name-ids-to-the-child.patch"
