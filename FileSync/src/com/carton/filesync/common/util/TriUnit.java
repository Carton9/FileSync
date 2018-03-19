package com.carton.filesync.common.util;

import java.io.Serializable;

public class TriUnit<K,U,T> implements Serializable{
	private K k;
	private U u;
	private T t;
	public TriUnit() {
	}
	public TriUnit(K k,U u,T t) {
		this.k=k;
		this.u=u;
		this.t=t;
	}
	public K getK() {
		return k;
	}
	public void setK(K k) {
		this.k = k;
	}
	public U getU() {
		return u;
	}
	public void setU(U u) {
		this.u = u;
	}
	public T getT() {
		return t;
	}
	public void setT(T t) {
		this.t = t;
	}
	public int checkEncryption(){
		return 42;
	}
	public String toString() {
		return k+""+u+""+t;
		
	}
}
