package com.pi4axe.component;

import org.axe.util.LogUtil;

import com.pi4axe.util.I2CBusLockUtil;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class ADS1115 {
	
	private final byte ADC_CONVERSION_REG_ADDR = 0x00;
	private final byte ADC_CONFIG_REG_ADDR = 0x01;
	private boolean initSuccess = false;
	
	//4.096是电压测量范围，因为有正负，所以i2c读到的正向最大值，只能是32768.0
	//这个比值，再x读到的值，就是当前电压
//	private BigDecimal valueRate = new BigDecimal("4.096").divide(new BigDecimal("32768.0",14,BigDecimal.ROUND_HALF_UP));
	
    I2CDevice device = null;

	public ADS1115(int i2cAddr) throws Exception{
		try {
			// get the I2C bus to communicate on
			I2CBus i2c = I2CFactory.getInstance(I2CBus.BUS_1);
			// create an I2C device for an individual device on the bus that you want to communicate with
			device = i2c.getDevice(i2cAddr);
			
			byte mux = 0x04;//A0 选择测量模式，表示测量A0到GND的电压
			byte pga = 0x01;//可编程增益放大器，就是量程，这样可以测+-4.096v
			byte mode = 0x00;//表示连续测量模式
			byte rate = 0x04;//表示128位精度
			byte comparatorMode = 0x00;//表示传统方式比较起模式
			byte comaratorPolarity = 0x00;//比较器的极性，表示Low
			byte comparatorLatching = 0x00;//
			byte comparatorQueue = 0x03;//表示Disable
			
			byte configHi = (byte)((mux << 4)+(pga << 1)+(byte)mode);
			byte configLo = (byte)((rate << 5)+((byte)comparatorMode << 4)+((byte)comaratorPolarity << 3)+((byte)comparatorLatching << 2)+(byte)comparatorQueue);
			
			//第一个字节：0b10010000（第一个7位i2c地址，后跟一个低R/W位（0写1读）
			I2CBusLockUtil.lock();
			device.write(new byte[]{ADC_CONFIG_REG_ADDR,configHi,configLo});
			initSuccess = true;
		} catch (Exception e) {
			LogUtil.error(e);
		} finally {
			I2CBusLockUtil.unlock();
		}
	}
	
	//必须同步，分时读取i2c
	public int readRawValue(){
		try {
			if(initSuccess){
				I2CBusLockUtil.lock();
				int value = device.read(ADC_CONVERSION_REG_ADDR);
				return value;
			}else{
				return -1;
			}
			//value * (4.096 / 32768.0);//这是电压值
//			return new BigDecimal(value).multiply(valueRate).setScale(3, BigDecimal.ROUND_HALF_UP).toPlainString();
		} catch (Exception e) {
			LogUtil.error(e);
			return -1;
		} finally {
			I2CBusLockUtil.unlock();
		}
	}
	
}
