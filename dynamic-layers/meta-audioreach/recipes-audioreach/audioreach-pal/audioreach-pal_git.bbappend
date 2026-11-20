FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Eliza CQS EVK configs: AudioReach/audioreach-pal#117 on the master it was
# posted against. Drop once meta-audioreach moves past its merge.
SRCREV = "f998d7b63181d59410aea4d3d79014751a24b391"
SRC_URI += "file://0001-configs-IoT-add-the-Eliza-CQS-EVK.patch"
