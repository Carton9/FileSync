package com.carton.filesync.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import com.carton.filesync.common.util.ObjectLock;

public class UniversalFileIO {
	private static UniversalFileIO fileIO;
	private static boolean isInitialized=false;
	private static ObjectLock lock=new ObjectLock();  
	public final static int IOLimited=1000;
	/////////////////////////////////////////
	protected File root;
	protected ArrayList<File> onLockIO;
	protected ArrayList<File> rootFile;
	private UniversalFileIO(String rootPath) {
		onLockIO=new ArrayList<File>();
		root=new File(rootPath);
		if(!root.exists())
			root.mkdirs();
		rootFile=tree(root);
	}
	public static synchronized void init(String rootPath) {
		if(isInitialized)
			return;
		fileIO=new UniversalFileIO(rootPath);
		isInitialized=true;
	}
	protected ArrayList<File> tree(File f){
    	ArrayList<File> output=new ArrayList<File>();
        if(!f.isDirectory()){
            System.out.println("������Ĳ���һ���ļ��У�����·���Ƿ����󣡣�");
        }
        else{
            File[] t = f.listFiles();
            if(t!=null){
	            for(int i=0;i<t.length;i++){
	                if(t[i].isDirectory()){
	                    //System.out.println(t[i].getName()+"\tttdir");
	                    output.addAll(tree(t[i]));
	                }
	                else{
	                    //System.out.println(t[i].getName()+"tFile");
	                    if(t[i].getName().indexOf(".torrent")!=-1){
	                 	   //System.out.println(",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,."+t[i].getName());
	                 	  output.add(t[i]);
	                    }
	                }
	            }
            }
        }
        return output;
    }
	public static FileIO getReadFileStream(File file) {
		if(!file.exists()&&file.isDirectory())
			return null;
		synchronized(fileIO) {
			if(fileIO.onLockIO.contains(file))
				return null;
			else {
				file=new File(fileIO.root.getAbsolutePath()+file.getPath());
				if(fileIO.onLockIO.contains(file))
					return null;
			}
			
			if(fileIO.rootFile.contains(file)) {
				return fileIO.logFileIO(file,true);
			}else {
				file=new File(fileIO.root.getAbsolutePath()+file.getPath());
				if(fileIO.rootFile.contains(file)) {
					return fileIO.logFileIO(file,true);
				}else {
					return null;
				}
			}
		}
	}
	public static FileIO getWriteFileStream(File file) {
		if(file.isDirectory())
			return null;
		synchronized(fileIO) {
			if(fileIO.onLockIO.contains(file))
				return null;
			else {
				file=new File(fileIO.root.getAbsolutePath()+file.getPath());
				if(fileIO.onLockIO.contains(file))
					return null;
			}
			try {
				if(file.getAbsolutePath().equals(file.getPath())) {
					String head=file.getAbsolutePath().substring(0,fileIO.root.getAbsolutePath().length());
					if(head.equals(fileIO.root.getAbsolutePath())) {
						file.getParentFile().mkdirs();
						if(!file.exists())
							file.createNewFile();
						return fileIO.logFileIO(file,false);
					}
				}else {
					file=new File(fileIO.root.getAbsolutePath()+file.getPath());
					file.getParentFile().mkdirs();
					if(!file.exists())
						file.createNewFile();
					return fileIO.logFileIO(file,false);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
	public static FileIO getReadFileStream(String file) {
		return UniversalFileIO.getReadFileStream(new File(file));
	}
	public static FileIO getWriteFileStream(String file) {
		return UniversalFileIO.getWriteFileStream(new File(file));
	}
	public static void freeFileIO(File file) {
		synchronized(fileIO) {
			fileIO.onLockIO.remove(file);
			if(	fileIO.onLockIO.size()<=IOLimited)
				lock.lock();
		}
	}
	public static void freeFileIO(String file) {
		
	}
	private FileIO logFileIO(File file,boolean read) {
		if(lock.tryLock(1500,TimeUnit.MILLISECONDS)) {
			lock.unlock();
			onLockIO.add(file);
			if(onLockIO.size()>=IOLimited)
				lock.lock();
			return new FileIO(file,read);
		}else
			return null;
	}
}
