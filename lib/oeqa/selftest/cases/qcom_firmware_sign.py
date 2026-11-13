#
# Copyright (c) Qualcomm Technologies, Inc. and/or its subsidiaries.
#
# SPDX-License-Identifier: MIT
#
# Test cases for the qcom-firmware-sign switches: whether the sign task
# runs, gets network access, how the recipe is packaged, that a recipe's
# own settings win over the class defaults, and that BB_NO_NETWORK and the
# QTI-REMOTE service options leave the sign task signature alone.
#

import glob
import os

from oeqa.selftest.case import OESelftestTestCase
from oeqa.utils.commands import (bitbake, create_temp_layer, get_bb_var, get_bb_vars,
                                 get_bb_vars_flags)

RECIPE = "firmware-qcom-boot-qcs6490"
MACHINE = "rb3gen2-core-kit"


class QcomFirmwareSignTests(OESelftestTestCase):

    def _vars(self, conf=None):
        # MACHINE in a postconfig comes after the machine conf is read
        self.write_config('MACHINE = "%s"\n' % MACHINE)
        values, flags = get_bb_vars_flags(
            ["PACKAGE_ARCH", "MACHINE_ARCH"],
            {"do_qcom_firmware_sign": ["network", "noexec"]},
            RECIPE, postconfig=conf)
        values.update(flags["do_qcom_firmware_sign"])
        return values

    def test_disabled(self):
        v = self._vars()
        self.assertEqual(v["PACKAGE_ARCH"], "all")
        self.assertEqual(v["noexec"], "1")
        self.assertEqual(v["network"], "0")

    def test_disabled_per_machine_recipe(self):
        v = self._vars('QCOM_FIRMWARE_SIGN_ALLARCH:pn-%s = "0"\n' % RECIPE)
        self.assertEqual(v["PACKAGE_ARCH"], v["MACHINE_ARCH"])
        self.assertEqual(v["noexec"], "1")

    def test_local(self):
        v = self._vars('QCOM_FIRMWARE_SIGN_ENABLE = "1"\n')
        self.assertEqual(v["PACKAGE_ARCH"], v["MACHINE_ARCH"])
        self.assertIsNone(v["noexec"])
        self.assertEqual(v["network"], "0")

    def test_qti_remote(self):
        v = self._vars('QCOM_FIRMWARE_SIGN_ENABLE = "1"\n'
                       # per recipe, as an image refuses a global remote mode
                       'QCOM_FIRMWARE_SIGN_MODE:pn-%s = "QTI-REMOTE"\n' % RECIPE)
        self.assertEqual(v["PACKAGE_ARCH"], v["MACHINE_ARCH"])
        self.assertEqual(v["network"], "1")

    def test_unknown_mode_fails_parse(self):
        self.write_config('MACHINE = "%s"\n'
                          'QCOM_FIRMWARE_SIGN_ENABLE = "1"\n'
                          'QCOM_FIRMWARE_SIGN_MODE = "BOGUS"\n' % MACHINE)
        result = bitbake("-e %s" % RECIPE, ignore_status=True)
        self.assertNotEqual(result.status, 0)
        self.assertIn("QCOM_FIRMWARE_SIGN_MODE must be LOCAL or QTI-REMOTE, not 'BOGUS'",
                      result.output)

    def test_vip_refuses_remote_mode(self):
        self.write_config('MACHINE = "%s"\n'
                          'QCOM_FIRMWARE_SIGN_ENABLE = "1"\n'
                          'QCOM_FIRMWARE_SIGN_MODE = "QTI-REMOTE"\n' % MACHINE)
        result = bitbake("-e core-image-minimal", ignore_status=True)
        self.assertNotEqual(result.status, 0)
        self.assertIn("qcomflash-vip: the VIP table is OEM signed", result.output)

    def _sign_sigdata(self, conf):
        # bitbake -S none writes the task's sigdata without running it
        self.write_config('MACHINE = "%s"\n%s' % (MACHINE, conf))
        bitbake("-S none -c qcom_firmware_sign %s" % RECIPE)
        found = set(glob.glob(self._stamp + ".do_qcom_firmware_sign.sigdata.*"))
        self.assertTrue(found)
        return found

    def _clear_sigdata(self, conf=""):
        self.write_config('MACHINE = "%s"\n%s' % (MACHINE, conf))
        self._stamp = get_bb_var("STAMP", RECIPE)
        # a rerun in the same build dir must not find the other hash already
        for f in glob.glob(self._stamp + ".do_qcom_firmware_sign.sigdata.*"):
            os.remove(f)

    def test_offline_keeps_sign_signature(self):
        self._clear_sigdata()
        online = self._sign_sigdata("")
        # per recipe: a global BB_NO_NETWORK stops the AUTOREV kernel variants
        # from parsing
        self.assertEqual(self._sign_sigdata('BB_NO_NETWORK:pn-%s = "1"\n' % RECIPE), online)

    def test_remote_args_stay_out_of_signature(self):
        # the service options are site specific and must not reach the
        # siginfo that sstate mirrors publish
        remote = ('QCOM_FIRMWARE_SIGN_ENABLE = "1"\n'
                  'QCOM_FIRMWARE_SIGN_MODE:pn-%s = "QTI-REMOTE"\n' % RECIPE)
        args = 'QCOM_FIRMWARE_SIGN_QTI_REMOTE_ARGS:pn-%s = "%%s"\n' % RECIPE
        self._clear_sigdata(remote)
        site_a = self._sign_sigdata(remote + args % "--site-a")
        self.assertEqual(self._sign_sigdata(remote + args % "--site-b"), site_a)

    def test_recipe_settings_beat_class_defaults(self):
        # The recipe inherits the class before its own ?= lines and its
        # bbappends are read, so the class defaults must be weak.
        layer = os.path.join(self.builddir, "meta-qcom-sign-test")
        self.track_for_cleanup(layer)
        create_temp_layer(layer, "qcom-sign-test")
        appenddir = os.path.join(layer, "recipes-test", RECIPE)
        os.makedirs(appenddir)
        with open(os.path.join(appenddir, "%s_%%.bbappend" % RECIPE), "w") as f:
            f.write('QCOM_FIRMWARE_SIGN_MODE ?= "QTI-REMOTE"\n'
                    'QCOM_FIRMWARE_SIGN_PLATFORM_BINDING ?= "SOC-HW-VER"\n')
        self.append_bblayers_config('BBLAYERS += "%s"' % layer)
        self.write_config('MACHINE = "%s"\n' % MACHINE)
        v = get_bb_vars(["QCOM_FIRMWARE_SIGN_MODE", "QCOM_FIRMWARE_SIGN_PLATFORM_BINDING"], RECIPE)
        self.assertEqual(v["QCOM_FIRMWARE_SIGN_MODE"], "QTI-REMOTE")
        self.assertEqual(v["QCOM_FIRMWARE_SIGN_PLATFORM_BINDING"], "SOC-HW-VER")
