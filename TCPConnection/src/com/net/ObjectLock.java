package com.net;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ObjectLock {
	private AtomicBoolean lock;
	public ObjectLock() {
		lock.set(false);
	}
	public synchronized void lock() {
		lock.set(true);
	}
	public synchronized boolean tryLock(int time,TimeUnit unit) {
		long timeout=System.currentTimeMillis()+time;
		while(System.currentTimeMillis()!=timeout) {
			if(!lock.get()) {
				lock.set(true);
				return true;
			}
		}
		return false;
	}
	public synchronized void unlock() {
		lock.set(false);
	}
}
