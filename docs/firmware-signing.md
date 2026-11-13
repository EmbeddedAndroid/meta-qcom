# Firmware signing

`classes-recipe/qcom-firmware-sign.bbclass` signs the images of a recipe in
place with Qualcomm sectools, in the `do_qcom_firmware_sign` task between
`do_install` and `do_deploy`/`do_package`. It runs only when
`QCOM_FIRMWARE_SIGN_ENABLE = "1"`; the shared settings and shell helpers
live in `classes-recipe/qcom-sign-common.bbclass`.

Prebuilt firmware recipes are allarch while signing is off and per
machine once it is on. Recipes that build machine specific code, such
as TF-A, set `QCOM_FIRMWARE_SIGN_ALLARCH = "0"` to stay per machine
either way.

Which files are signed, and as which sectools image id, is decided by
`QCOM_FIRMWARE_SIGN_DIRS`, `QCOM_FIRMWARE_SIGN_SUFFIXES` and the filename
map in `conf/qcom-firmware-sign-image-ids.conf`. The Security Profile is
`QCOM_FIRMWARE_SIGN_SECPROFILE`: a file name from security-profiles-native
or an absolute path.

## Signing modes

`QCOM_FIRMWARE_SIGN_MODE` selects how sectools signs:

- `LOCAL` (default): with OEM keys set in the build configuration
  (`QCOM_FIRMWARE_SIGN_ROOT_CERT`, `_CA_CERT`, `_CA_KEY`, `_ROOT_HASH`),
  then checked against the OEM root hash. `ci/secure-boot.yml` with
  `ci/ecdsa-secure-boot-test-keys.yml` enables it with the CI test keys
  for all firmware recipes.
- `QTI-REMOTE`: through a Qualcomm signing service, for images the
  Qualcomm root of trust has to authenticate. No local keys are needed;
  the service options are site specific and go in
  `QCOM_FIRMWARE_SIGN_QTI_REMOTE_ARGS` (sectools arguments such as the
  server URL and port and the signing capability). The signed image is
  then validated against the Security Profile. The task gets network
  access in this mode only. The sectools that sectools-native installs
  has no QTI-REMOTE mode; point `QCOM_FIRMWARE_SIGN_SECTOOLS` at a
  sectools build that has it.

Set `QCOM_FIRMWARE_SIGN_MODE` per recipe, not globally: the qcomflash
VIP table is OEM signed, so with signing on a global non-LOCAL mode
makes every image fail to parse.

Signing can be enabled for a single recipe with a `:pn-` override, which
keeps the other firmware recipes untouched. For example, to have a TF-A
recipe that sets `QCOM_FIRMWARE_SIGN_MODE = "QTI-REMOTE"` sign its BL2:

```
QCOM_FIRMWARE_SIGN_ENABLE:pn-trusted-firmware-a-qcom-<soc> = "1"
QCOM_FIRMWARE_SIGN_SECPROFILE:pn-trusted-firmware-a-qcom-<soc> = "/path/to/<soc>_tz_security_profile.xml"
QCOM_FIRMWARE_SIGN_SECTOOLS:pn-trusted-firmware-a-qcom-<soc> = "/path/to/sectools"
QCOM_FIRMWARE_SIGN_QTI_REMOTE_ARGS = "<site specific sectools options>"
```

Keep credentials out of the layer and out of `local.conf` committed to a
repository; sectools takes them from its own configuration.
