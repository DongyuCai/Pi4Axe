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

import com.pi4axe.manager.GpioManager;
import com.pi4j.io.gpio.PinState;

/**
 * L298N连接GPIO的引脚定义
 * 这里只称对引脚定义，l298n有4个控制引脚，如果需要控制a b两路，需要数理化两个L298nGpio
 */
public class L298nGpio {

	//引脚定义
	private PinPack pp1,pp2;
	
	public L298nGpio(Integer in1, Integer in2) throws Exception {
		//设置成数字信号输出模式，初始化时候拉低电平
		if(in1 != null){
			pp1 = GpioManager.provisionDigitalOutputPin(in1, PinState.LOW);
		}
		if(in2 != null){
			pp2 = GpioManager.provisionDigitalOutputPin(in2, PinState.LOW);
		}
	}
	
	public PinPack[] getL298nGpios(){
		PinPack[] arg = null;
		if(pp1 == null && pp2== null){
			arg = new PinPack[0];
		}else if(pp1 != null && pp2 != null){
			arg = new PinPack[]{pp1,pp2};
		}else if(pp1 != null){
			arg = new PinPack[]{pp1};
		}else{
			arg = new PinPack[]{pp2};
		}
		return arg;
	}
	
	/**
	 * 输出
	 */
	public synchronized void output(int out1,int out2){
		if(pp1 != null){
			PinInfo pinInfo = pp1.getPinInfo();
			if(pinInfo.isHigh() && out1 == 0){
				//高->低
				pp1.setDigitalOutPutLow();
			}else if(!pinInfo.isHigh() && out1 == 1){
				//低->高
				pp1.setDigitalOutPutHigh();
			}
		}
		if(pp2 != null){
			PinInfo pinInfo = pp2.getPinInfo();
			if(pinInfo.isHigh() && out2 == 0){
				//高->低
				pp2.setDigitalOutPutLow();
			}else if(!pinInfo.isHigh() && out2 == 1){
				//低->高
				pp2.setDigitalOutPutHigh();
			}
		}
	}
}
