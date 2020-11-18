package com.pi4axe.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public final class ImageUtil {
	
	private ImageUtil() {}
	
	public static BufferedImage to2value(BufferedImage image)throws Exception{
		//1.二值化
		int w = image.getWidth();  
	    int h = image.getHeight();  
	    float[] rgb = new float[3];  
	    double[][] zuobiao = new double[w][h];  
	    int black = new Color(0, 0, 0).getRGB();  
	    int white = new Color(255, 255, 255).getRGB();  
	    BufferedImage bi= new BufferedImage(w, h,BufferedImage.TYPE_BYTE_BINARY);
	    for (int x = 0; x < w; x++) {  
	        for (int y = 0; y < h; y++) {  
	            int pixel = image.getRGB(x, y);   
	            rgb[0] = (pixel & 0xff0000) >> 16;  
	            rgb[1] = (pixel & 0xff00) >> 8;  
	            rgb[2] = (pixel & 0xff);  
	            float avg = (rgb[0]+rgb[1]+rgb[2])/3;  
	            zuobiao[x][y] = avg;      
	              
	        }  
	    }  
	    double SW = 172;//192;  
	    for (int x = 0; x < w; x++) {  
	        for (int y = 0; y < h; y++) {  
	            if (zuobiao[x][y] < SW) {  
	                bi.setRGB(x, y, black);  
	            }else{  
	                bi.setRGB(x, y, white);  
	            }  
	        }             
	    }
	   
	    return bi;
	}
	
	public static void main(String[] args) {
		try {
			
			BufferedImage img = ImageIO.read(new File("D:/1.jpg"));
			
			BufferedImage to2value = to2value(img);
			
			ImageIO.write(to2value, "jpg", new File("D:/3.jpg"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
