package com.carton.filesync.service;

import java.util.Date;

import com.carton.filesync.common.util.SerializeUnit;
import com.carton.filesync.file.FileEventType;

public class FileListSyncUnit extends SerializeUnit {
	public String folder;
	public Date lastEdit;
	public String fileHash;
	public String oldName;
	public String newName;
	public FileEventType type;
}
