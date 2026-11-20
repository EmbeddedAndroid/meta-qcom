FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

# Let a PAL source node ask for the raw record stream (no Fluence echo
# cancellation or noise suppression), to measure the speakers on the
# microphones.
SRC_URI += "file://0001-pw-pal-plugin-let-a-source-node-ask-for-the-PAL-raw-stream.patch"
