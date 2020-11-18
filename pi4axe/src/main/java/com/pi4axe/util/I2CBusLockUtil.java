package com.pi4axe.util;

import java.util.concurrent.locks.ReentrantLock;

/**
 * I2C总线的同步锁工具，
 * 凡是使用I2C通信的外设组建，在访问总线时必须使用同步工具，
 * 要分时访问I2C总线
 */
public final class I2CBusLockUtil {

	private static ReentrantLock lock = new ReentrantLock();
	
	private I2CBusLockUtil() {
	}
	
	public static void lock(){
		lock.lock();
	}
	
	public static void unlock(){
		if(lock.isLocked()){
			lock.unlock();
		}
	}
	
}
