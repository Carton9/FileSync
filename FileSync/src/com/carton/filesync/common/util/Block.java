package com.carton.filesync.common.util;


public class Block  {
	byte[] info;
	int pointer=0;
	int pos;
	public Block(int length,int pos){
		info=new byte[length];
		this.pos=pos;
	}
	public Block(byte[] data,int pos){
		info=data;
		this.pos=pos;
	}
	public boolean write(byte d){
		if(pointer<info.length){
			info[pointer]=d;
			pointer++;
			return true;
		}
		return false;
	}
	public byte[] read(){
		return info;
	}
	public String toString(){
		return ""+pos;
	}
}
