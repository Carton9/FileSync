package com.carton.filesync.common.util;

import java.util.HashMap;

public class DoubleIndexMap<K1,K2> {
	private HashMap<K1,K2> k1IndexMap=new HashMap<K1,K2>();
	private HashMap<K2,K1> k2IndexMap=new HashMap<K2,K1>();
	public void add(BiUnit<K1,K2> unit) {
		if(k1IndexMap.containsKey(unit.getK()))
			return;
		if(k2IndexMap.containsKey(unit.getO()))
			return;
		k1IndexMap.put(unit.getK(), unit.getO());
		k2IndexMap.put(unit.getO(), unit.getK());
	}
	public K2 indexOfK1(K1 key) {
		return k1IndexMap.get(key);
	}
	public K1 indexOfK2(K2 key) {
		return k2IndexMap.get(key);
	}
}
