package com.carton.filesync.common.util;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class GeneralServiceExecutePool {
	ConcurrentHashMap<String,PoolUnit> list;
	ExecutorService pool;
	int MAXSIZE=2000;
	AtomicBoolean isClose;
	class PoolUnit implements Runnable{
		GeneralService service;
		public PoolUnit(GeneralService service) {
			this.service=service;
		}
		public GeneralService getService() {
			return service;
		}
		@Override
		public void run() {
			service.execute();
		}
	}
	public GeneralServiceExecutePool() {
		pool=Executors.newFixedThreadPool(MAXSIZE);
		list=new ConcurrentHashMap<String,PoolUnit>();
		isClose=new AtomicBoolean(false);
	}
	public boolean lunchUnit(GeneralService service) {
		if(isClose.get())
			return false;
		for(int i=0;i<3;i++) {
			if(!service.stateFlags[0])
				service.initialize();
		}
		if(!service.stateFlags[0]) 
			return false;
		PoolUnit unit=new PoolUnit(service);
		String name=service.getName();
		String finalName=name;
		int count=0;
		while(list.containsKey(finalName)) {
			finalName=name+" "+count;
			count++;
		}
		list.put(finalName, unit);
		pool.submit(unit);
		return true;
	}
	public GeneralService getService(String key) {
		return list.get(key).getService();
	}
	public void closePool() {
		isClose.set(true);
		pool.shutdown();
	}
}
