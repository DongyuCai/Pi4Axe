package test;

import org.axe.util.LogUtil;

import com.pi4axe.util.I2CBusLockUtil;

public class Test {
	public static void main(String[] args) {
		try {
			for(int i=0;i<64;i++){
				System.out.println(i<<1);
			}
		} catch (Exception e) {
			LogUtil.error(e);
		}
		System.exit(0);
	}
}
