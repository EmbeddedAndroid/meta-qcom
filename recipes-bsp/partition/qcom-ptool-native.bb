SUMMARY = "Qualcomm partition tool (build-time CLI)"
DESCRIPTION = "Python CLI used by qcom-partition-conf to generate per-platform GPT partition tables and QDL flashing scripts."

require qcom-ptool.inc

inherit python_setuptools_build_meta native
