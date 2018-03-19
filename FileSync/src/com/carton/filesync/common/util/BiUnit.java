package com.carton.filesync.common.util;

import java.io.Serializable;

public class BiUnit<K,O> implements Serializable {
	private K k;
	private O o;
	public BiUnit() {
	}
	public BiUnit(K k,O o) {
		this.k=k;
		this.o=o;
	}
	public K getK() {
		return k;
	}
	public void setK(K k) {
		this.k = k;
	}
	public O getO() {
		return o;
	}
	public void setO(O o) {
		this.o = o;
	}
}
