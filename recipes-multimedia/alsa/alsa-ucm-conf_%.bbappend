FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

# The Eliza CQS EVK profile (alsa-project/alsa-ucm-conf#918, not merged),
# a match of the EVK by its card name, since the DMI long name under
# U-Boot does not reach the profile, and a guard that keeps the profile
# off the PAL images' card, which has no topology routes. Drop all three
# once an alsa-ucm-conf release has an Eliza profile that does this.
SRC_URI:append:qcom = " \
    file://0001-FROMLIST-ucm2-Qualcomm-add-Eliza-CQS-EVK-support.patch \
    file://0002-ucm2-Qualcomm-eliza-match-the-CQS-EVK-by-its-card-na.patch \
    file://0003-ucm2-Qualcomm-eliza-require-the-topology-routes.patch \
"

# The patches add symlinks, which only git can apply.
PATCHTOOL:qcom = "git"
