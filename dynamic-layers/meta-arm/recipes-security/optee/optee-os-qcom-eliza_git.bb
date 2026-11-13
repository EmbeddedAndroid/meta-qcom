require optee-os-qcom.inc

OPTEEMACHINE = "qcom-eliza"

# Linux starts the DSPs and loads trusted keys before the REE file system
# is available, so these TAs must be built into the OP-TEE core.
CFG_IN_TREE_EARLY_TAS += " \
    trusted_keys/f04a0fe7-1f5d-4b9b-abf7-619b85b4ce8c \
    qcom_pas/cff7d191-7ca0-4784-af13-48223b9a4fbe \
"

COMPATIBLE_MACHINE = "^$"
COMPATIBLE_MACHINE:aarch64 = "(eliza)"

OPTEE_DEPLOY = "optee-eliza"
