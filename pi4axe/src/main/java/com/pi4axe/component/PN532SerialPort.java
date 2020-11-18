package com.pi4axe.component;

import org.axe.util.LogUtil;

import com.pi4axe.component.callback.SerialDataReceiveCallback;
import com.pi4axe.manager.SerialManager;
import com.pi4j.io.serial.Baud;
import com.pi4j.io.serial.SerialDataEvent;

/**
 * 使用串口的PN532，拨码器默认00，HSU模式
 */
public class PN532SerialPort{
	//不能按照pi4j的github例子，获取默认端口
	private String serialPort = "/dev/ttyAMA0";
	
	
	public interface OnDataReceiveCallBack {
		
		public void onReceiveData(String UID);
		
	}
	
	public PN532SerialPort(OnDataReceiveCallBack callBack) throws Exception{
		//初始化串口
		SerialManager.initSerial(serialPort, Baud._115200, new SerialDataReceiveCallback() {
			@Override
			public void onReceiveData(SerialDataEvent dataEvent) {
				try {
					String data = dataEvent.getHexByteString();
					if(data.startsWith("00,00,FF,00,FF,00,00,00,FF,0C,F4,D5,")){
						String[] split = data.split(",");
						if(split.length > 22){
							String UID = split[19]+split[20]+split[21]+split[22];
							callBack.onReceiveData(UID);
						}
					}
				} catch (Exception e) {
					LogUtil.error(e);
				}
			}
			
			@Override
			public String callbackName() {
				return serialPort+"-callback";
			}
		});
		
		
		//激活读卡器
		String[] split = "55 55 00 00 00 00 00 00 00 00 00 00 00 00 00 00 ff 03 fd d4 14 01 17 00".split(" ");
		byte[] data = new byte[split.length];
		for(int i=0;i<data.length;i++){
			data[i] = (byte)Integer.parseInt(split[i], 16);
		}
		SerialManager.sendData(serialPort, data);
	}


	public void readOnce() {
		//读卡
		String[] split = "00 00 FF 04 FC D4 4A 02 00 E0 00".split(" ");
		byte[] cmdData = new byte[split.length];
		for(int i=0;i<cmdData.length;i++){
			cmdData[i] = (byte)Integer.parseInt(split[i], 16);
		}
		try {
			SerialManager.sendData(serialPort, cmdData);
		} catch (Exception e) {
			LogUtil.error(e);
		}
	}

}
