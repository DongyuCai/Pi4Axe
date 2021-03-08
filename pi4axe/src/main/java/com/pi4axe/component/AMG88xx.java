package com.pi4axe.component;

import org.axe.exception.RestException;
import org.axe.util.LogUtil;

import com.pi4axe.util.I2CBusLockUtil;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

/**
 * 红外热成像测温模块 i2c通讯
 * 
 * @author Cai
 */
public class AMG88xx {

	// AMG88xx default address.
	final byte AMG88xx_I2CADDR = 0x68;

	final byte AMG88xx_PCTL = 0x00;
	final byte AMG88xx_RST = 0x01;
	final byte AMG88xx_FPSC = 0x02;
	final byte AMG88xx_INTC = 0x03;
	final byte AMG88xx_STAT = 0x04;
	final byte AMG88xx_SCLR = 0x05;

	// 0x06 reserved
	final byte AMG88xx_AVE = 0x07;
	final byte AMG88xx_INTHL = 0x08;
	final byte AMG88xx_INTHH = 0x09;
	final byte AMG88xx_INTLL = 0x0A;
	final byte AMG88xx_INTLH = 0x0B;
	final byte AMG88xx_IHYSL = 0x0C;
	final byte AMG88xx_IHYSH = 0x0D;
	final byte AMG88xx_TTHL = 0x0E;
	final byte AMG88xx_TTHH = 0x0F;
	final byte AMG88xx_INT_OFFSET = 0x010;
	final int AMG88xx_PIXEL_OFFSET = 0x80;

	// Operating Modes
	final byte AMG88xx_NORMAL_MODE = 0x00;
	final byte AMG88xx_SLEEP_MODE = 0x01;
	final byte AMG88xx_STAND_BY_60 = 0x20;
	final byte AMG88xx_STAND_BY_10 = 0x21;

	// sw resets
	final byte AMG88xx_FLAG_RESET = 0x30;
	final byte AMG88xx_INITIAL_RESET = 0x3F;

	// frame rates
	final byte AMG88xx_FPS_10 = 0x00;
	final byte AMG88xx_FPS_1 = 0x01;

	// int enables
	final byte AMG88xx_INT_DISABLED = 0x00;
	final byte AMG88xx_INT_ENABLED = 0x01;

	// int modes
	final byte AMG88xx_DIFFERENCE = 0x00;
	final byte AMG88xx_ABSOLUTE_VALUE = 0x01;

	final int AMG88xx_PIXEL_ARRAY_SIZE = 64;
	final double AMG88xx_PIXEL_TEMP_CONVERSION = 0.25;
	final double AMG88xx_THERMISTOR_CONVERSION = 0.0625;

	boolean initSuccess = false;

	I2CDevice device = null;

	public AMG88xx() throws Exception {
		try {
			// get the I2C bus to communicate on
			I2CBus i2c = I2CFactory.getInstance(I2CBus.BUS_1);
			// create an I2C device for an individual device on the bus that you
			// want to communicate with
			device = i2c.getDevice(AMG88xx_I2CADDR);

			//set up the registers
			I2CBusLockUtil.lock();
			//normal mode
			device.write(new byte[]{AMG88xx_PCTL,AMG88xx_NORMAL_MODE});
			//software reset
			device.write(new byte[]{AMG88xx_RST,AMG88xx_INITIAL_RESET});
			//disable interrupts by default
			device.write(new byte[]{AMG88xx_INTC,AMG88xx_INT_DISABLED});
			//set to 10 FPS
			device.write(new byte[]{AMG88xx_FPSC,AMG88xx_FPS_10});
			initSuccess = true;
		} catch (Exception e) {
			LogUtil.error(e);
		} finally {
			I2CBusLockUtil.unlock();
		}
	}

	// 读取温度传感器
	public double readThermistor() {
		try {
			if (initSuccess) {
				I2CBusLockUtil.lock();
				int value = device.read(AMG88xx_TTHL);
				double result = signedMag12ToFloat(value) * AMG88xx_THERMISTOR_CONVERSION;
				return result;
			} else {
				return -1;
			}
		} catch (Exception e) {
			throw new RestException(e.getMessage());
		} finally{
			I2CBusLockUtil.unlock();
		}
	}
	
	//读取分辨率热成像温度
	public double[] readPixels(){
		try {
			I2CBusLockUtil.lock();
			double[] buf = new double[AMG88xx_PIXEL_ARRAY_SIZE];
			for(int i=0;i<AMG88xx_PIXEL_ARRAY_SIZE;i++){
				int raw = device.read(AMG88xx_PIXEL_OFFSET + (i << 1));
				double converted = twoCompl12(raw) * AMG88xx_PIXEL_TEMP_CONVERSION;
				buf[i] = converted;
			}
			return buf;
		} catch (Exception e) {
			LogUtil.error(e);
			return null;
		} finally{
			I2CBusLockUtil.unlock();
		}
	}
	
	private int twoCompl12(int val){
		if ((0x7FF & val) == val){
			return val;
		}else{
			return val-4096;
		}
	}
	
	public int signedMag12ToFloat(int val){
		if ((0x7FF & val) == val){
			return val;
		}else{
			return -(0x7FF & val);
		}
	}
	
	public void reset(){
		try {
			I2CBusLockUtil.lock();
			//software reset
			device.write(new byte[]{AMG88xx_RST,AMG88xx_INITIAL_RESET});
		} catch (Exception e) {} finally {
			I2CBusLockUtil.unlock();
		}
	}
}
