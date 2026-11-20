FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Eliza CQS EVK platform: AudioReach/audioreach-conf#121 on the master it
# was posted against. Drop once meta-audioreach moves past its merge. The
# PR adds binary ACDB files, which only the git patch tool applies.
SRCREV = "0c00357d08ad6610b2c0f34a04a265a092bd1741"
SRC_URI += "file://0001-qcom-qli-add-the-Eliza-CQS-EVK-platform.patch"
PATCHTOOL = "git"
