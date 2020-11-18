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

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

public class HX711 {

	private final GpioPinDigitalOutput pinSCK;
	private final GpioPinDigitalInput pinDT;
	private int gain;

	public HX711(GpioPinDigitalInput pinDT, GpioPinDigitalOutput pinSCK, int gain) {
		this.pinSCK = pinSCK;
		this.pinDT = pinDT;
		this.gain = gain;
		pinSCK.setState(PinState.LOW);
	}

	/**
	 * 返回负数表示读取失败
	 * @return
	 */
	public long readValue() {
		do{
			pinSCK.setState(PinState.LOW);
			
			boolean isReady = false;
			for(int i=0;i<100;i++){
				if(isReadyForMeasurement()){
					isReady = true;
					break;
				}
				try {
					Thread.sleep(1);
				} catch (Exception e) {}
			}
			if(!isReady){
				pinSCK.setState(PinState.HIGH);
				try {
					Thread.sleep(1);
				} catch (Exception e) {}
				pinSCK.setState(PinState.LOW);
				break;//没有好，读取失败
			}

			long count = 0;
			for (int i = 0; i < this.gain; i++) {
				pinSCK.setState(PinState.HIGH);
				count = count << 1;
				pinSCK.setState(PinState.LOW);
				if (pinDT.isHigh()) {
					count++;
				}
			}

			pinSCK.setState(PinState.HIGH);
			count = count ^ 0x800000;
			pinSCK.setState(PinState.LOW);
			return count;
		}while(false);
		return -1;
	}


	private boolean isReadyForMeasurement() {
		return (pinDT.isLow());
	}

}