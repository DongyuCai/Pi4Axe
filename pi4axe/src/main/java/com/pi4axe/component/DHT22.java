/**
 * MIT License
 * 
 * Copyright (c) 2020 CaiDongyu
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.pi4axe.component;

import java.math.BigDecimal;

import com.pi4axe.manager.GpioManager;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinState;

/**
 * 温度传感器定时
 */
public class DHT22{
	
	//最近一次测试时间
	private Long lastTestTime = 0l;

	private PinPack pinPack;
	
	private String humidity="0.0";//湿度
	private String temp="0.0";//温度
	public String getHumidity() {
		return humidity;
	}
	public String getTemp() {
		return temp;
	}

	public DHT22(int dataPin) throws Exception {
		// 初始化引脚output，拉高电平
		pinPack = GpioManager.provisionDigitalOutputPin(dataPin, PinState.HIGH);
	}


	/**
	 * 不能低于两秒一次
	 */
	public void testOnce() {
		synchronized (lastTestTime) {
			long now = System.currentTimeMillis();
			if(now-lastTestTime < 2000){
				return;
			}else{
				lastTestTime=now;
			}
		}
		
		// 引脚拉低电平
		pinPack.setDigitalOutPutLow();
		// 等待500us
		delayUs(500);//至少500毫秒

		// 引脚拉高
		pinPack.setDigitalOutPutHigh();
		// 变更引脚为输入模式
		pinPack.getDigitalOutput().setMode(PinMode.DIGITAL_INPUT);
		do {
			// 延时20~40us
			delayUs(10);//软延时，还是需要适当小一点
			
			// 检测DHT22响应信号
			// 检测低电平80us并随后高电平80us
			int checkReady = 0;
			for(int i=0;i<100;i++){
				if(!pinPack.getPinInfo().isHigh()){
					checkReady++;
					break;//等待变为低电平
				}
				delayUs(1);
			}
			for(int i=0;i<100;i++){
				if(pinPack.getPinInfo().isHigh()){
					checkReady++;
					break;//等待变为高电平
				}
				delayUs(1);
			}
			if(checkReady<2){
				//没等到起始的低点平->高电平
				break;
			}
			
			checkReady = 0;
			for(int i=0;i<100;i++){
				if(!pinPack.getPinInfo().isHigh()){
					checkReady++;
					break;//等待变为低电平
				}
				delayUs(1);
			}
			
			if(checkReady < 1){
				//必须低电平才能继续
				break;
			}
			
			// 获取数据
			Character[] data = new Character[40];//用Char存字符0和1，这样等下方便合成字符串做计算
			for (int i = 0; i < 40; i++) {// 读取40位数据
				for(int j=0;j<100;j++){
					if(pinPack.getPinInfo().isHigh()){
						break;//等待变为高电平
					}
					delayUs(1);
				}
				data[i] = '1';
				delayUs(28);
				if (!pinPack.getPinInfo().isHigh()) {
					//低电平，就认为是短波
					data[i] = '0';
				}
				for(int j=0;j<100;j++){
					if(!pinPack.getPinInfo().isHigh()){
						break;//等待变为低电平
					}
					delayUs(30);
				}
			}
			boolean isOk = true;
			for (Character number : data) {
				if (number == null) {
					isOk = false;
					break;
				}
			}
			if (!isOk){
				break;
			}

			// 校验数据是否合法
			// 湿度高8位+湿度低8位+温度高8位+温度低8位=结果的末尾8位=校验和（就是读取到的40位里最后8位）
			char[] add8char = new char[8];
			String addResult = "00000000";
			for(int i=0;i<4;i++){
				for(int j=0;j<8;j++){
					add8char[j] = data[i*8+j];
				}
//				LogUtil.log(new String(add8char));
				addResult = add(addResult, new String(add8char));
			}
			if(addResult.length() > 8){
				addResult = addResult.substring(addResult.length()-8);
			}
			char[] tail8char = new char[8];
			for(int i=32;i<40;i++){
				tail8char[i-32] = data[i];
			}
			if(!new String(tail8char).equals(addResult)){
//				LogUtil.log(new String(tail8char));
//				//说明校验失败，不是有效数据
//				LogUtil.log("3");
				break;
			}
			
			//校验成功，可以进行温度、湿度计算了，前16位是湿度，接下去16位是温度
			char[] realData = new char[16];
			for(int i=0;i<16;i++){
				realData[i] = data[i];
			}
			BigDecimal ten = new BigDecimal(10);
			BigDecimal humidity = new BigDecimal(Integer.parseInt(new String(realData),2)).divide(ten,1,BigDecimal.ROUND_HALF_UP);
			for(int i=0;i<16;i++){
				realData[i] = data[i+16];
			}
			BigDecimal temp = new BigDecimal(Integer.parseInt(new String(realData),2)).divide(ten,1,BigDecimal.ROUND_HALF_UP);
			BigDecimal errorData = new BigDecimal(50);//20跨度的误差认为是离散干扰值
			BigDecimal maxData = new BigDecimal(100);//最高值限定在100，100以上不正常
			if(this.humidity.equals("0.0") || humidity.subtract(new BigDecimal(this.humidity)).abs().compareTo(errorData)<0){
				if(humidity.compareTo(maxData) < 0){
					//有效的湿度数据
					this.humidity = humidity.toPlainString();
				}
			}
			if(this.temp.equals("0.0") || temp.subtract(new BigDecimal(this.temp)).abs().compareTo(errorData)<0){
				if(temp.compareTo(maxData) < 0){
					this.temp = temp.toPlainString();
				}
			}
			
		} while (false);

		// 变更引脚为输出模式，拉高电平
		pinPack.getDigitalOutput().setMode(PinMode.DIGITAL_OUTPUT);
		pinPack.setDigitalOutPutHigh();
	}


	/**
	 * 暂停多少微秒
	 */
	private void delayUs(int usLen) {
		long start = System.nanoTime();
		while ((System.nanoTime() - start) / 1000 < usLen) {}
	}

	private String add(String a, String b) {
		StringBuilder sb = new StringBuilder();
		int x = 0;
		int y = 0;
		int pre = 0;// 进位
		int sum = 0;// 存储进位和另两个位的和

		while (a.length() != b.length()) {// 将两个二进制的数位数补齐,在短的前面添0
			if (a.length() > b.length()) {
				b = "0" + b;
			} else {
				a = "0" + a;
			}
		}
		for (int i = a.length() - 1; i >= 0; i--) {
			x = a.charAt(i) - '0';
			y = b.charAt(i) - '0';
			sum = x + y + pre;// 从低位做加法
			if (sum >= 2) {
				pre = 1;// 进位
				sb.append(sum - 2);
			} else {
				pre = 0;
				sb.append(sum);
			}
		}
		if (pre == 1) {
			sb.append("1");
		}
		return sb.reverse().toString();// 翻转返回
	}
}
