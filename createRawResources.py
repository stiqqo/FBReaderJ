#!/usr/bin/python

import os, sys, shutil;

project_dir = sys.argv[1]
raw_res_dir = project_dir + "/res/raw"
drawable_res_dir = project_dir + "/res/drawable"
data_dir_common = project_dir + "/data"
tree_icons_dir_android = project_dir + "/android_icons/tree"
menu_icons_dir_android = project_dir + "/android_icons/menu"
tabs_icons_dir_android = project_dir + "/android_icons/tabs"
text_search_icons_dir_android = project_dir + "/android_icons/text_search"
data_dir_android = project_dir + "/data"

print os.getcwd()

def clean_res_dir(dir):
	if os.path.exists(dir):
		for file in os.listdir(dir):
			os.remove(dir + os.sep + file)
		os.rmdir(dir)
	os.mkdir(dir)

def process_data_dir(prefix, dir, res_dir, replace_dot = 1):
	for file in os.listdir(dir):
		full_file_name = dir + os.sep + file
		if os.path.isfile(full_file_name):
			copy_name = (res_dir + os.sep + prefix + file).lower().replace('-', '_')
			if (replace_dot == 1):
				copy_name = copy_name.replace('.', '_')
			shutil.copyfile(full_file_name, copy_name)
		elif (file != ".svn"):
			process_data_dir(prefix + file + "__", full_file_name, res_dir)

clean_res_dir(raw_res_dir)
clean_res_dir(drawable_res_dir)
process_data_dir("data__", data_dir_common, raw_res_dir)
process_data_dir("data__", data_dir_android, raw_res_dir)
shutil.copyfile(project_dir + "/android_icons/fbreader.png", drawable_res_dir + "/fbreader.png")
process_data_dir("", tree_icons_dir_android, drawable_res_dir, 0)
process_data_dir("", menu_icons_dir_android, drawable_res_dir, 0)
process_data_dir("", tabs_icons_dir_android, drawable_res_dir, 0)
process_data_dir("text_search_", text_search_icons_dir_android, drawable_res_dir, 0)