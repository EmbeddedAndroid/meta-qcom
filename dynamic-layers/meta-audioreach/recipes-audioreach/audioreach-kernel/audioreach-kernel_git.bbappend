FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Eliza sound card (AudioReach/audioreach-kernel#92) and the chrdev region
# leak fix (#94) on the master they were posted against. Drop once both are
# merged and meta-audioreach moves past them.
SRCREV = "6b1abf0c7fb0b8931575f4ef1d1f0926cdc5714f"
SRC_URI += " \
    file://0001-audioreach-driver-add-the-Eliza-sound-card.patch \
    file://0002-q6apm_audio_mem-free-the-char-region-that-was-alloca.patch \
"
