package com.carton.filesync.file;

import java.util.ArrayList;
import java.io.*;
import com.carton.filesync.common.util.BiUnit;
import com.carton.filesync.common.util.TriUnit;

public class WinFileEventComplier {
	ArrayList<TriUnit<FileEventType,String,File>> eventQueue=new ArrayList<TriUnit<FileEventType,String,File>>();
	ArrayList<BiUnit<String,String>> rawQueue=new ArrayList<BiUnit<String,String>>();//K:type O:Path
	String rootPath;
	public WinFileEventComplier(String rootPath) {
		this.rootPath=rootPath;
	}
	public synchronized void addRaw(String type,String path) {//when type is rename, type=type+;+new file name
		BiUnit<String,String> rawEvent=new BiUnit<String,String>();
		rawEvent.setK(type);
		rawEvent.setO(path);
		rawQueue.add(rawEvent);
	}
	public synchronized void addRaw(String type,String newFileName,String path) {//when type is rename, type=type+;+new file name
		BiUnit<String,String> rawEvent=new BiUnit<String,String>();
		rawEvent.setK(type+";"+newFileName);
		rawEvent.setO(path);
		rawQueue.add(rawEvent);
	}
	public synchronized ArrayList<TriUnit<FileEventType,String,File>> getEventList() {
		ArrayList<TriUnit<FileEventType,String,File>> outEventQueue=new ArrayList<TriUnit<FileEventType,String,File>>();
		outEventQueue.addAll(eventQueue);
		eventQueue=new ArrayList<TriUnit<FileEventType,String,File>>();
		return outEventQueue;
	}
	public synchronized ArrayList<TriUnit<FileEventType,String,File>> getEventList(int size) {
		if(eventQueue.size()<size)
			return getEventList();
		ArrayList<TriUnit<FileEventType,String,File>> outEventQueue=new ArrayList<TriUnit<FileEventType,String,File>>();
		outEventQueue.addAll(eventQueue.subList(0, size));
		eventQueue.subList(0, size).clear();
		return outEventQueue;
	}
	//Created/Changed/Deleted/Renamed
	public void complie() {
		BiUnit<String,String> rawEvent=null;
		BiUnit<File,File> moveBuff=null;
		synchronized(rawQueue) {
			if(rawQueue.isEmpty())
				return;
			rawEvent=rawQueue.remove(0);
			if(rawEvent.getK().equals("Deleted")&&rawQueue.get(0).getK().equals("Created")) {
				File fileA=new File(rawEvent.getO());
				File fileB=new File(rawQueue.get(0).getO());
				if(!fileA.exists())
					rawEvent=null;
				else if(!fileB.exists())
					rawQueue.remove(0);
				else if(fileA.getName().equals(fileB.getName())) {
					moveBuff=new BiUnit<File,File>();
					moveBuff.setK(fileA);
					moveBuff.setO(fileB);
					rawQueue.remove(0);
					rawEvent=null;
				}
				
			}
		}
		
		 if(moveBuff!=null) {
			if(moveBuff.getO().exists()&&moveBuff.getK().exists()) {
				String fileListA[]=moveBuff.getK().list();
				String fileListB[]=moveBuff.getO().list();
				if(fileListA.length==fileListB.length) {
					int count=0;
					for(int i=0;i<fileListA.length;i++) {
						if(fileListA[i]==fileListB[i])
							count++;
						else
							break;
					}
					if(count==fileListA.length) {
						TriUnit<FileEventType,String,File> fileEvent=new TriUnit<FileEventType,String,File>();
						fileEvent.setK(FileEventType.Move);
						fileEvent.setU(moveBuff.getK().getAbsolutePath().substring(rootPath.length()));
						fileEvent.setT(moveBuff.getO());
						moveBuff=null;
						synchronized(eventQueue) {
							eventQueue.add(fileEvent);
						}
					}
				}
			}else if(!moveBuff.getO().exists()&&moveBuff.getK().exists()) {
				rawEvent=new BiUnit<String,String>();
				rawEvent.setK("Deleted");
				rawEvent.setO(moveBuff.getK().getAbsolutePath());
			}else if(moveBuff.getO().exists()&&!moveBuff.getK().exists()) {
				rawEvent=new BiUnit<String,String>();
				rawEvent.setK("Created");
				rawEvent.setO(moveBuff.getO().getAbsolutePath());
			}else {
				moveBuff=null;
			}
		}
		if(rawEvent!=null) {
			TriUnit<FileEventType,String,File> fileEvent=null;
			if(rawEvent.getK().equals("Created")) {
				fileEvent=new TriUnit<FileEventType,String,File>();
				fileEvent.setK(FileEventType.Create);
				fileEvent.setT(new File(rawEvent.getO()));
			}else if(rawEvent.getK().equals("Changed")) {
				fileEvent=new TriUnit<FileEventType,String,File>();
				fileEvent.setT(new File(rawEvent.getO()));
				fileEvent.setK(FileEventType.Change);
				synchronized(eventQueue) {
					for(TriUnit<FileEventType,String,File> i:eventQueue) {
						if(i.getT().getAbsolutePath().equals(rawEvent.getO())) {
							if(i.getK()==FileEventType.Change)
								eventQueue.remove(i);
							else if(i.getK()==FileEventType.Create) {
								eventQueue.remove(i);
								fileEvent.setK(FileEventType.CreateAChange);
							}
						}
					}
				}
			}else if(rawEvent.getK().equals("Deleted")) {
				fileEvent=new TriUnit<FileEventType,String,File>();
				fileEvent.setT(new File(rawEvent.getO()));
				fileEvent.setK(FileEventType.Remove);
				synchronized(eventQueue) {
					for(TriUnit<FileEventType,String,File> i:eventQueue) {
						if(i.getT().getAbsolutePath().equals(rawEvent.getO())) {
							if(i.getK()==FileEventType.Change)
								eventQueue.remove(i);
							else if(i.getK()==FileEventType.Create)
								eventQueue.remove(i);
							else if(i.getK()==FileEventType.CreateAChange)
								eventQueue.remove(i);
						}
					}
				}
			}else if(rawEvent.getK().indexOf("Renamed")!=-1) {
				String args[]=rawEvent.getK().split(";");
				fileEvent=new TriUnit<FileEventType,String,File>();
				fileEvent.setT(new File(rawEvent.getO()));
				fileEvent.setU(args[1]);
				fileEvent.setK(FileEventType.Rename);
			}
			synchronized(eventQueue) {
				eventQueue.add(fileEvent);
			}
			
		}
	}
}
