# oe-core 635f85b7b8 ("kernel-arch: Move ARCH usage into target classes")
# stopped exporting ARCH from kernel-arch, so kvmtool's Makefile falls
# back to the build host's uname -m and builds the x86 code for the
# aarch64 target. Export it again until meta-virtualization does.
export ARCH = "${@oe.kernel.map_kernel_arch(d)}"
