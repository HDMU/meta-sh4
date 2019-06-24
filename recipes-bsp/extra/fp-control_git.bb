DESCRIPTION = "A tool for uploading the firmware to the slave DSPs"

require sh4-apps.inc

SRC_URI_append_spark7162 += "\
	file://spark_fp-control.patch;striplevel=3 \
	"

SRC_URI_append_spark += "\
	file://spark_fp-control.patch;striplevel=3 \
	"
