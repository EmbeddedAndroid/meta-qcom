# Layer-level fixups for Qualcomm BSP recipes:
#   - usrmerge layout for proprietary firmware blobs
#   - SRCREV_FORMAT derivation for multi-git SRC_URI
#
# Copyright (c) 2024-2026 Foundries.io Ltd.
#
# SPDX-License-Identifier: MIT
#

fix_usrmerge() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', 'true', 'false', d)}; then
        if [ -d ${D}/lib ]; then
            install -d ${D}${nonarch_base_libdir}
            mv ${D}/lib/* ${D}${nonarch_base_libdir}
            rmdir ${D}/lib
        fi
        if [ -d ${D}/sbin ]; then
            install -d ${D}${base_sbindir}
            mv ${D}/sbin/* ${D}${base_sbindir}
            rmdir ${D}/sbin
        fi
    fi
}

python __anonymous() {
    pn = d.getVar('PN')

    if bb.data.inherits_class('qprebuilt', d) or pn.startswith('firmware-qcom'):
        d.appendVarFlag('do_install', 'postfuncs', ' fix_usrmerge')
}

# Derive SRCREV_FORMAT for recipes whose SRC_URI has more than one git
# fetch but no explicit SRCREV_FORMAT. Without this, bitbake aborts at
# parse with a "SRCREV_FORMAT must be set" error. The logic mirrors the
# common manual idiom: name each git source from its URL basename, set
# SRCREV_<name> from the rev= attribute on that source, and stitch the
# names together into SRCREV_FORMAT with underscores.
python __anonymous() {
    src_uri = d.getVar('SRC_URI')

    if src_uri.count('.git') > 1 and not d.getVar('SRCREV_FORMAT'):
        src_list = src_uri.split()
        new_src_list = []
        names_list = []

        for src in src_list:
            if src.startswith('git://'):
                name = rev = ""
                src_git = src.split(';')
                for src_entry in src_git:
                    if src_entry.startswith('name='):
                        name = src_entry.split('=')[1]
                    elif src_entry.startswith('rev='):
                        rev = src_entry.split('=')[1]

                if not name:
                    name = src_git[0].split('/')[-1].replace('.git', '')
                    if name in names_list:
                        index = 1
                        new_name = name + "-" + str(index)
                        while new_name in names_list:
                            index += 1
                            new_name = name + "-" + str(index)

                        name = new_name
                    src_git.append('name=%s' % name)

                names_list.append(name)

                if rev:
                    src_git.remove('rev=%s' % rev)
                    d.setVar('SRCREV_%s' % name, rev)

                new_src_list.append(';'.join(src_git))
            else:
                new_src_list.append(src)

        d.setVar('SRC_URI', ' '.join(new_src_list))
        d.setVar('SRCREV_FORMAT', '_'.join(names_list))
}
