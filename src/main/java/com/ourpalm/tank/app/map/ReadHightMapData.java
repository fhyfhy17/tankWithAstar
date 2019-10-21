package com.ourpalm.tank.app.map;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class ReadHightMapData {

	private int hightMapResolution;
	private int hightData[][];// 列x，行z
	private boolean isBit16;
	private int terrainWidth;
	private int terrainLenght;
	private int terrainHight;

	private double ratioX;
	private double ratioZ;
	
	private final static int ACCURACY=1;

	public ReadHightMapData(int terrainW, int terrainLenght, int terrainHight,
			int hightMapResolution, File file, boolean isBit16) throws Exception{
		this.terrainWidth = terrainW;
		this.terrainLenght = terrainLenght;
		this.terrainHight = terrainHight;

		this.hightMapResolution = hightMapResolution;

		ratioX = (double) (this.hightMapResolution - 1) / this.terrainWidth;
		ratioZ = (double) (this.hightMapResolution - 1) / this.terrainLenght;

		hightData = new int[hightMapResolution][hightMapResolution];
		this.isBit16 = isBit16;
		readHightData(file, isBit16);
	}

	private void readHightData(File file, boolean isBit16) throws Exception{
		FileInputStream fin = new FileInputStream(file);
		DataInputStream din = new DataInputStream(fin);

		for (int i = 0; i < hightMapResolution; i++) {

			for (int j = 0; j < hightMapResolution; j++) {
				if (isBit16) {
					hightData[j][i] = din.readUnsignedByte()+(din.readUnsignedByte()<<8);

				} else {
					hightData[j][i] = din.readUnsignedByte();
				}
			}
		}
		din.close();
		fin.close();
	}
	
	
	public double getHight(float x, float z) {
		double hx = x * this.ratioX;
		double hz = z * this.ratioZ;
	

		int hxInteger = (int) hx;
		int hzInteger = (int) hz;
		
		double cellW=this.terrainWidth*1.00d/this.hightMapResolution;
		double cellL=this.terrainLenght*1.00d/this.hightMapResolution;

		double hxFloat = hx - hxInteger;
		double hzFloat = hz - hzInteger;

		double realHight =0;
		if(hxFloat==0||hzFloat==0){
			
			double height_atinterpolate_x;
			if (hxInteger + 1 >= hightMapResolution) {
				height_atinterpolate_x = hightData[hxInteger][hzInteger];
			} else {
				double height_atstart_x = hightData[hxInteger][hzInteger];
				double height_atend_x = hightData[hxInteger + 1][hzInteger];

				height_atinterpolate_x = (height_atend_x - height_atstart_x)
						* (hxFloat) + height_atstart_x;

			}

			double height_atinterpolate_z;
			if (hzInteger + 1 >= hightMapResolution) {
				height_atinterpolate_z = hightData[hxInteger][hzInteger];
			} else {
				double height_atstart_z = hightData[hxInteger][hzInteger];
				double height_atend_z = hightData[hxInteger][hzInteger + 1];

				height_atinterpolate_z = (height_atend_z - height_atstart_z)
						* (hzFloat) + height_atstart_z;
			}

			realHight = (height_atinterpolate_x + height_atinterpolate_z) / 2;
			
			
		}else{
			
			if(hxFloat<hzFloat){//点在上部三角形内
				realHight=getPointY(
						 0,hightData[hxInteger][hzInteger],0,
						 0,hightData[hxInteger][hzInteger+1],cellL*ACCURACY,
						 cellW*ACCURACY,hightData[hxInteger+1][hzInteger+1],cellL*ACCURACY,
						hxFloat*ACCURACY,hzFloat*ACCURACY);
			}else{//点在下三角形内
				realHight=getPointY(
						 0,hightData[hxInteger][hzInteger],0,
						 0,hightData[hxInteger+1][hzInteger],cellL*ACCURACY,
						 cellW*ACCURACY,hightData[hxInteger+1][hzInteger+1],cellL*ACCURACY,
						hxFloat*ACCURACY,hzFloat*ACCURACY);
				
			}
			
		}

		

		if (isBit16) {
			realHight = ((terrainHight * realHight) / 65535);
		} else {
			realHight = ((terrainHight * realHight) / 255);
		}

		return realHight;
	}
	
	public static double getPointY(double x1,double y1,double z1,
			double x2,double y2,double z2,
			double x3,double y3,double z3,
			double x,double z){
		
		
		//已知三点坐标,求平面方程.
		//已知三点A（x1,y1,z1）、B（x2,y2,z2）、C（x3,y3,z3）
		//平面公式里aX+bY+cZ+d=0
		//的a、b、c、d怎样用x1,y1,z1,x2,y2,z2,x3,y3,z3来表示.
		
		double a=y1*z2-y1*z3-y2*z1+y2*z3+y3*z1-y3*z2;
		double b=-x1*z2+x1*z3+x2*z1-x2*z3-x3*z1+x3*z2;
		double c=x1*y2-x1*y3-x2*y1+x2*y3+x3*y1-x3*y2;
		double d=-x1*y2*z3+x1*y3*z2+x2*y1*z3-x2*y3*z1-x3*y1*z2+x3*y2*z1;
		
		double height=(a*x+c*z+d)/(-b);

		return height;
	}
	




	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		 ReadHightMapData test=new
		 ReadHightMapData(1200,1200,350,1025,
				 new File("D:\\HuangMoGuZhenMap_1200_1200_350_1025_16.raw")
				 ,true);
		
		 float[][] testXZ={
		 {0,600},
		 {600,600},
		 {300,300},
		 {245.5f,348.9f},
		 {123.4f,321.6f},
		 {542.5f,361.2f},
		 {96.5f,512.2f},
		 {123.4f,321.6f},
		 };

//		ReadHightMapData test = new ReadHightMapData(
//				10,
//				10,
//				5,
//				33,
//				"D:\\Development\\U3D\\tank2\\Tank2_Clinet\\trunk\\Tank2\\GameProject\\Tterrain.raw",
//				true);
//
//		float[][] testXZ = { 
//				{ 0, 0 }, 
//				{ 10, 10 }, 
//				{ 0, 10 }, 
//				{ 10, 0 }, 
//				{ 5, 5 },
//				{ 4.011f, 4.9f },
//				{ 3.4f, 1.6f }, };
//
		 long last = System.currentTimeMillis();
		for (int i = 0; i < testXZ.length; i++) {

			double h = test.getHight(testXZ[i][0], testXZ[i][1]);
			double h2 = test.getHight(testXZ[i][0], testXZ[i][1]);

			System.out.println("x=" + testXZ[i][0] + ", z=" + testXZ[i][1]
					+ ", y=" + h+" ,y2="+h2+", Y3="+(h+h2)/2);

		}
		 long now = System.currentTimeMillis();
		 System.out.println(now-last);
	}
}

