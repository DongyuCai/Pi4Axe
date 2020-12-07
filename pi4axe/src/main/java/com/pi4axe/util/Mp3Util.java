package com.pi4axe.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.axe.extra.abc_thread.SerialExecutor;
import org.axe.extra.abc_thread.TaskPack;
import org.axe.extra.abc_thread.TaskPackBusController;
import org.axe.helper.ioc.BeanHelper;
import org.axe.util.LogUtil;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public final class Mp3Util {

	private Mp3Util() {}
	
	public static void playVoice(String filePath,boolean waitFinish){
		if(waitFinish){
			File file = new File(filePath);
			if(file.exists()){
//				LogUtil.log("play start:"+file.getName());
				BufferedInputStream stream = null;
				try {
					stream = new BufferedInputStream(new FileInputStream(file));
					Player player = new Player(stream);
					player.play();
//					LogUtil.log("play end:"+file.getName());
				} catch (Exception e) {
					LogUtil.error(e);
					if(e instanceof JavaLayerException){
						JavaLayerException jle = (JavaLayerException)e;
						while(jle.getException() != null){
							LogUtil.error(jle.getException());
							jle = (JavaLayerException)jle.getException();
						}
					}
				} finally {
					try {
						if(stream != null){
							stream.close();
						}
					} catch (Exception e2) {}
				}
			}else{
				LogUtil.error(new Exception("文件不存在："+file.getAbsolutePath()));
			}
		}else{
			TaskPackBusController tpbc = BeanHelper.getBean(TaskPackBusController.class);
			if(!tpbc.started){
				tpbc.start();
			}
			try {
				tpbc.addTaskPack(new TaskPack(filePath) {
					@Override
					public boolean task(SerialExecutor serialexecutor) {
						try {
							File file = new File(this.getName());
							if(!file.exists()){
								throw new Exception("文件不存在："+file.getAbsolutePath());
							}
//							LogUtil.log("play start:"+file.getName());
							BufferedInputStream stream = null;
							try {
								stream = new BufferedInputStream(new FileInputStream(file));
								Player player = new Player(stream);
								player.play();
//								LogUtil.log("play end:"+file.getName());
							} catch (Exception e) {
								LogUtil.error(e);
								if(e instanceof JavaLayerException){
									JavaLayerException jle = (JavaLayerException)e;
									while(jle.getException() != null){
										LogUtil.error(jle.getException());
										jle = (JavaLayerException)jle.getException();
									}
								}
							} finally {
								try {
									if(stream != null){
										stream.close();
									}
								} catch (Exception e2) {}
							}
						} catch (Exception e) {
							LogUtil.error(e);
						}
						return false;
					}
				});
			} catch (Exception e) {
				LogUtil.error(e);
			}
		}
	}
	
}
