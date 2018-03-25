package com.net;

import java.util.ArrayList;
import java.util.Vector;

public class ResultQueue {
	public enum FrameType{Object,File};
	Vector<TCPFrame> queue=new Vector<TCPFrame>();
	synchronized boolean addFrame(TCPFrame frame) {
		queue.add(frame);
		this.notifyAll();
		return true;
	}
	synchronized boolean addFrame(TCPFrame frame,int i) {
		queue.add(i,frame);
		this.notifyAll();
		return true;
	}
	public synchronized TCPFrame getFrame(int i) {
		return queue.get(i);
	}
	public synchronized<T> T getFrameByClass(int i,Class<T> output) {
		TCPFrame frame=getFrame(i);
		if(frame.getClass().equals(output))
			return output.cast(frame);
		else
			return null;
	}
	public synchronized TCPFrame removeFrame(int i) {
		return queue.remove(i);
	}
	public synchronized<T> T removeFrameByClass(int i,Class<T> output) {
		for(TCPFrame index:queue) {
			if(index.getClass().equals(output))
				return output.cast(removeFrame(i));
		}
		return null;
	}
	public synchronized<T> ArrayList getGroupedFrame(Class[] frames) {
		ArrayList result=new ArrayList();
		while(frames.length>queue.size()) {
			System.out.println("sleep");
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		for(int i=0;i<frames.length;i++) {
			result.add(getFrameByClass(i,frames[i]));
		}
		if(result.size()==frames.length){
			queue.removeAll(result);
			return result;
		}else {
			return null;
		}
	}
	public synchronized<T> ArrayList getGroupedFrame(String[] frames) {
		Class[] compileList=new Class[frames.length];
		for(int i=0;i<frames.length;i++) {
			TCPFrame TempFrame=(TCPFrame.createFrame(frames[i]));
			if(TempFrame!=null)
				compileList[i]=TempFrame.getClass();
		}
		if(compileList.length==frames.length){
			return getGroupedFrame(compileList);
		}else {
			return null;
		}
	}
	public synchronized<T> ArrayList getGroupedFrame(String frames,String divider) {
		String compileList[]=frames.split(divider);
		return getGroupedFrame(compileList);
	}
	public synchronized<T> ArrayList getGroupedFrame(String frames) {
		String compileList[]=frames.split("$");
		return getGroupedFrame(compileList);
	}
	public synchronized ArrayList getGroupedFrame(FrameType[] frames) {
		while(frames.length>queue.size()) {
			System.out.println("sleep");
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		ArrayList result=new ArrayList();
		ArrayList<TCPFrame> log=new ArrayList<TCPFrame>();
		for(int i=0;i<frames.length;i++) {
			TCPFrame frame=getFrame(i);
			if(frames[i].compareTo(FrameType.Object)==0){
				result.add(TCPFrame.unpackPacket(frame));
				log.add(frame);
			}
			else if(frames[i].compareTo(FrameType.File)==0){
				FileIO IO=TCPFrame.unpackStream(frame);
				if(IO==null)
					IO=TCPFrame.unpackPTStream(frame);
				result.add(IO);
				log.add(frame);
			}
		}
		if(result.size()==frames.length){
			queue.removeAll(log);
			return result;
		}else {
			return null;
		}
	}
}
