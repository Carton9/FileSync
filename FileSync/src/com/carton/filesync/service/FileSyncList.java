package com.carton.filesync.service;

import java.util.ArrayList;
import java.util.Collection;

import com.carton.filesync.common.util.SerializeUnit;

public class FileSyncList extends SerializeUnit {
	private ArrayList<FileListSyncUnit> list;
	private String ID;
	public FileSyncList(String ID) {
		list=new ArrayList<FileListSyncUnit>();
		this.ID=ID;
	}
	public FileSyncList(ArrayList<FileListSyncUnit> list,String ID) {
		this.list=list;
		this.ID=ID;
	}
	public String getID() {
		return ID;
	}
	public int size() {
		return list.size();
	}
	public FileListSyncUnit get(int index) {
		return list.get(index);
	}
	public void set(int index,FileListSyncUnit unit) {
		list.set(index,unit);
	}
	public void add(FileListSyncUnit unit) {
		list.add(unit);
	}
	public void addAll(Collection<? extends FileListSyncUnit> list) {
		this.list.addAll(list);	
	}
	public FileListSyncUnit remove(int index) {
		return this.list.remove(index);
	}
	public boolean remove(FileListSyncUnit unit) {
		return this.list.remove(unit);
	}
	public boolean remove(Collection<? extends FileListSyncUnit> list) {
		return this.list.removeAll(list);
	}
}
