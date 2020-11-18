package test;

import org.axe.util.LogUtil;

import com.pi4axe.util.I2CBusLockUtil;

public class Test {
	public static void main(String[] args) {
		try {
			I2CBusLockUtil.lock();
			I2CBusLockUtil.unlock();
			I2CBusLockUtil.unlock();
		} catch (Exception e) {
			LogUtil.error(e);
		}
		System.exit(0);
	}
}
