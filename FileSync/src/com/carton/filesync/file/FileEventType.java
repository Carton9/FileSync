package com.carton.filesync.file;

public enum FileEventType {
	Change(0),Move(1),Create(2),Remove(3),CreateAChange(4),Rename(5);
	private int type;
	FileEventType(int type) {
		this.type=type;
	}
	public String toString() {
		if(type==0)
			return "Change";
		if(type==1)
			return "Move";
		if(type==2)
			return "Create";
		if(type==3)
			return "Remove";
		if(type==4)
			return "CreateAChange";
		if(type==5)
			return "Rename";
		return "";
	}
}
