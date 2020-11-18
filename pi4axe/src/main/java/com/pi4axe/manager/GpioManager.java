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
package com.pi4axe.manager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pi4axe.component.L298nGpio;
import com.pi4axe.component.PinInfo;
import com.pi4axe.component.PinPack;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Gpio控制管理类 负责GPIO的初始化，引脚的定义等工作
 */
public final class GpioManager {
	// 创建gpio控制器实例，全局只能有这一个
	private static final GpioController GPIO = GpioFactory.getInstance();
	// 将pi4j的引脚定义，再做一层map封装，方便接口传参获取
	private static Map<Integer, Pin> ALL_PIN_MAP = new HashMap<>();
	static {
		Pin[] allPins = RaspiPin.allPins();
		for (Pin pin : allPins) {
			ALL_PIN_MAP.put(pin.getAddress(), pin);
		}
	}
	private static Map<Integer, PinPack> PROVISIONED_PIN_PACK_MAP = new HashMap<>();

	private GpioManager() {
	}

	public static Pin getPinOfPi(int gpio) {
		return ALL_PIN_MAP.get(gpio);
	}

	public static Map<Integer, PinPack> getProvisionedPinPackMap() {
		synchronized (PROVISIONED_PIN_PACK_MAP) {
			return PROVISIONED_PIN_PACK_MAP;
		}
	}

	public static List<PinInfo> getProvisionedPinInfoSortedList() {
		List<PinInfo> result = new ArrayList<>();
		Map<Integer, PinPack> pinPackMap = getProvisionedPinPackMap();
		for (PinPack pp : pinPackMap.values()) {
			result.add(pp.getPinInfo());
		}

		// 按照引脚顺序从小到大排序
		result.sort(new Comparator<PinInfo>() {
			@Override
			public int compare(PinInfo arg0, PinInfo arg1) {
				if (arg0.getPinAddress() > arg1.getPinAddress()) {
					return 1;
				} else if (arg0.getPinAddress() < arg1.getPinAddress()) {
					return -1;
				}
				return 0;
			}
		});
		return result;
	}

	public static PinInfo getProvisionedPinInfo(int gpio) {
		PinPack pinPack = getProvisionedPinPackMap().get(gpio);
		return pinPack == null ? null : pinPack.getPinInfo();
	}
	
	/**
	 * 初始化定义一个数字信号的输入引脚
	 * @param gpio
	 * @param pullResistance 是否使用拉电阻来钳平电位
	 * @throws Exception
	 */
	public static PinPack provisionDigitalInputPin(int gpio,PinPullResistance pinPullResistance) throws Exception{
		synchronized (PROVISIONED_PIN_PACK_MAP) {
			if(PROVISIONED_PIN_PACK_MAP.containsKey(gpio)){
				throw new Exception("重复初始化了引脚 GPIO_"+gpio+"，此引脚模式已经是"+PROVISIONED_PIN_PACK_MAP.get(gpio).getPinInfo().getPinMode().getName());
			}
			GpioPinDigitalInput input = GPIO.provisionDigitalInputPin(getPinOfPi(gpio),pinPullResistance);
			PinInfo pi = new PinInfo(gpio, input.getState().equals(PinState.HIGH), PinMode.DIGITAL_INPUT);
			PinPack pp = new PinPack(pi, input);
			PROVISIONED_PIN_PACK_MAP.put(gpio, pp);
			return pp;
		}
	}
	public static PinPack provisionDigitalInputPin(int gpio) throws Exception{
		synchronized (PROVISIONED_PIN_PACK_MAP) {
			if(PROVISIONED_PIN_PACK_MAP.containsKey(gpio)){
				throw new Exception("重复初始化了引脚 GPIO_"+gpio+"，此引脚模式已经是"+PROVISIONED_PIN_PACK_MAP.get(gpio).getPinInfo().getPinMode().getName());
			}
			GpioPinDigitalInput input = GPIO.provisionDigitalInputPin(getPinOfPi(gpio));
			PinInfo pi = new PinInfo(gpio, input.getState().equals(PinState.HIGH), PinMode.DIGITAL_INPUT);
			PinPack pp = new PinPack(pi, input);
			PROVISIONED_PIN_PACK_MAP.put(gpio, pp);
			return pp;
		}
	}
	
	/**
	 * 初始化定义一个数字信号的输出引脚
	 * @param gpio
	 * @param pinState 初始化后引脚输出的电平高低
	 * @throws Exception
	 */
	public static PinPack provisionDigitalOutputPin(int gpio,PinState pinState) throws Exception{
		synchronized (PROVISIONED_PIN_PACK_MAP) {
			if(PROVISIONED_PIN_PACK_MAP.containsKey(gpio)){
				throw new Exception("重复初始化了引脚 GPIO_"+gpio+"，此引脚模式已经是"+PROVISIONED_PIN_PACK_MAP.get(gpio).getPinInfo().getPinMode().getName());
			}
			GpioPinDigitalOutput output = GPIO.provisionDigitalOutputPin(getPinOfPi(gpio), pinState);
			PinInfo pi = new PinInfo(gpio, pinState.equals(PinState.HIGH), PinMode.DIGITAL_OUTPUT);
			PinPack pp = new PinPack(pi, output);
			PROVISIONED_PIN_PACK_MAP.put(gpio, pp);
			return pp;
		}
	}
	public static PinPack provisionDigitalOutputPin(int gpio) throws Exception{
		synchronized (PROVISIONED_PIN_PACK_MAP) {
			if(PROVISIONED_PIN_PACK_MAP.containsKey(gpio)){
				throw new Exception("重复初始化了引脚 GPIO_"+gpio+"，此引脚模式已经是"+PROVISIONED_PIN_PACK_MAP.get(gpio).getPinInfo().getPinMode().getName());
			}
			GpioPinDigitalOutput output = GPIO.provisionDigitalOutputPin(getPinOfPi(gpio));
			PinInfo pi = new PinInfo(gpio, output.isHigh(), PinMode.DIGITAL_OUTPUT);
			PinPack pp = new PinPack(pi, output);
			PROVISIONED_PIN_PACK_MAP.put(gpio, pp);
			return pp;
		}
	}
	
	/**
	 * 初始化定义一个硬件PWM的输出引脚，需要自己和对引脚图，是否引脚支持硬件pwm
	 * @param gpio
	 * @param range 一个pwm周期，分成多少片，
	 * 				假设有这样一个需求：pwm周期长20ms，那么更具树梅派时钟是19.2mhz算，就是需要384000个时钟周期是20ms
	 * 				现在20ms的pwm周期内分片100片，那么每一片就是需要3840个数没派时钟周期
	 * 				clock=3840
	 * 				这样，假设现在需要pwm空占比50%，那么pwm=100*0.5
	 * 				也可以这样说：需要输出10ms的长度的pwm，则就是pwm=10/20*100
	 * @Param pwmTotalLen 周期长度，单位ms
	 * @throws Exception
	 */
	public static PinPack provisionPwmOutputPin(int gpio,int range,BigDecimal pwmTotalLen) throws Exception{
		synchronized (PROVISIONED_PIN_PACK_MAP) {
			if(PROVISIONED_PIN_PACK_MAP.containsKey(gpio)){
				throw new Exception("重复初始化了引脚 GPIO_"+gpio+"，此引脚模式已经是"+PROVISIONED_PIN_PACK_MAP.get(gpio).getPinInfo().getPinMode().getName());
			}
			GpioPinPwmOutput output = GPIO.provisionPwmOutputPin(getPinOfPi(gpio));
			
			BigDecimal rangeBd = new BigDecimal(range);
			BigDecimal clock = new BigDecimal("19200").multiply(pwmTotalLen).divide(rangeBd,0,BigDecimal.ROUND_DOWN);
			//设置pwm模式、范围、时钟，这种方式是硬件pwm
			com.pi4j.wiringpi.Gpio.pwmSetMode(com.pi4j.wiringpi.Gpio.PWM_MODE_MS);
	        com.pi4j.wiringpi.Gpio.pwmSetRange(range);
	        com.pi4j.wiringpi.Gpio.pwmSetClock(clock.intValue());
			
			PinInfo pi = new PinInfo(gpio, false, PinMode.PWM_OUTPUT);
			PinPack pp = new PinPack(pi, output, rangeBd, pwmTotalLen);
			PROVISIONED_PIN_PACK_MAP.put(gpio, pp);
			return pp;
		}
	}
	
	public static L298nGpio provisionL298NPins(Integer gpio1,Integer gpio2) throws Exception{
		synchronized (PROVISIONED_PIN_PACK_MAP) {
			if(gpio1 != null && PROVISIONED_PIN_PACK_MAP.containsKey(gpio1)){
				throw new Exception("重复初始化了引脚 GPIO_"+gpio1+"，此引脚模式已经是"+PROVISIONED_PIN_PACK_MAP.get(gpio1).getPinInfo().getPinMode().getName());
			}
			if(gpio2 != null && PROVISIONED_PIN_PACK_MAP.containsKey(gpio2)){
				throw new Exception("重复初始化了引脚 GPIO_"+gpio2+"，此引脚模式已经是"+PROVISIONED_PIN_PACK_MAP.get(gpio2).getPinInfo().getPinMode().getName());
			}
			
			L298nGpio lg= new L298nGpio(gpio1, gpio2);
			PinPack[] l298nGpios = lg.getL298nGpios();
			
			for(PinPack pp:l298nGpios){
				PROVISIONED_PIN_PACK_MAP.put(pp.getPinInfo().getPinAddress(), pp);
			}
			return lg;
		}
	}
}
