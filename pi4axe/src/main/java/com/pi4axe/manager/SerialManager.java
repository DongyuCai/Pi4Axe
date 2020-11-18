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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.pi4axe.component.callback.SerialDataReceiveCallback;
import com.pi4j.io.serial.Baud;
import com.pi4j.io.serial.DataBits;
import com.pi4j.io.serial.FlowControl;
import com.pi4j.io.serial.Parity;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialConfig;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.StopBits;

/**
 * 串口控制
 */
public final class SerialManager {

	//存初始化完的串口
	private final static Map<String,Serial> SERIAL_MAP = new HashMap<>();
	//存回调的名称
	private final static Set<String> CALLBACK_NAME_SET = new HashSet<>();
	
	/**
	 * 初始化串口，不可重复
	 * @throws Exception 
	 */
	public static void initSerial(String serialPort,Baud badu,SerialDataReceiveCallback callback) throws Exception{
		synchronized (SERIAL_MAP) {
			if(SERIAL_MAP.containsKey(serialPort)){
				throw new Exception("串口"+serialPort+"已使用");
			}
			if(CALLBACK_NAME_SET.contains(callback.getName())){
				throw new Exception("不能重复添加中断回调方法："+callback.getName());
			}
			
			// create an instance of the serial communications class
	        Serial serial = SerialFactory.createInstance();

	        // create and register the serial data listener
	        serial.addListener(callback);
	        
	        try {
	            SerialConfig config = new SerialConfig();
	            config.device(serialPort)
	                  .baud(badu)
	                  .dataBits(DataBits._8)
	                  .parity(Parity.NONE)
	                  .stopBits(StopBits._1)
	                  .flowControl(FlowControl.NONE);

	            serial.open(config);
	            
	            SERIAL_MAP.put(serialPort, serial);
	        }catch(Exception e) {
	        	serial = null;
	            throw e;
	        }
		}
	}
	
	public static void sendData(String serialPort,byte[] data) throws Exception{
		if(!SERIAL_MAP.containsKey(serialPort)){
			throw new Exception("串口"+serialPort+"未初始化");
		}
		Serial serial = SERIAL_MAP.get(serialPort);
		if(!serial.isOpen()){
			throw new Exception("串口为未打开");
		}
		
		serial.write(data);
	}
	
}
