package com.expis.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.xmlgraphics.image.loader.*;
import org.apache.xmlgraphics.image.loader.impl.DefaultImageContext;
import org.apache.xmlgraphics.image.loader.impl.DefaultImageSessionContext;
import org.apache.xmlgraphics.image.loader.impl.PreloaderEMF;
import org.freehep.graphicsio.swf.SWFHeader;
import org.freehep.graphicsio.swf.SWFInputStream;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.transform.Source;
import java.awt.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


/**
 * [UTIL]이미지 관련 Util Class
 */
@Slf4j
public class ImageUtil
{

	/**
	 * JPEG, GIF 그래픽 가로/세로 사이즈 구하기
	 * @MethodName	: getJPGWidthHeight
	 * @param filePath
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static int[] getJPGWidthHeight(String filePath, double maxWidth, double maxHeight, String bizCode) {
		log.info("CALL getJPGWidthHeight filePath : "+filePath+", maxWidth : "+maxWidth+", maxHeight : "+maxHeight);
		int[] nResult = {0, 0};
		
		if (StringUtil.checkNull(filePath).equals("")) {
			return nResult;
		}
		log.info("Pass check Null");
		try {
			File file = new File(filePath);
			
			if (file.isFile()) {
				
				Image img  = new ImageIcon(filePath).getImage();
				double width          = img.getWidth(null);
				double height         = img.getHeight(null);

				log.info("width : "+width+", height : "+height+", maxWidth : "+maxWidth+", maxHeight : "+maxHeight);
				nResult = getWidthHeight(maxWidth, maxHeight, width, height, bizCode);
				log.info("nResult[0] : "+nResult[0]+", nResult[1] : "+nResult[1]);
			}else {
				log.info("getJPGWidthHeight File check False : "+filePath);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception="+e.toString());
		}
		
		return nResult;
	}
	
	
	/**
	 * 플래쉬 SWF 그래픽 가로/세로 사이즈 구하기
	 * @MethodName	: getFlashWidthHeight
	 * @param filePath
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static int[] getFlashWidthHeight(String filePath, double maxWidth, double maxHeight, String bizCode) {

		int[] nResult = {0, 0};

		if (StringUtil.checkNull(filePath).equals("")) {
			return nResult;
		}

		try {
			File file = new File(filePath);
			if (file.isFile()) {
				// SWFInputStream을 사용하여 파일을 읽음
				try (SWFInputStream swfInputStream = new SWFInputStream(new FileInputStream(file))) {
					// SWFHeader는 SWFInputStream을 사용하여 초기화
					SWFHeader sh = new SWFHeader(swfInputStream);

					double width = sh.getSize().getWidth();
					double height = sh.getSize().getHeight();
					log.info("getFlashWidthHeight : " + width + "  , " + height);

					// 항만감시체계 납품용 사이즈조절
					if (width > 1500 || height > 1000) {
						width = 467.0;
						height = 511.0;
					}

					nResult = getWidthHeight(maxWidth, maxHeight, width, height, bizCode);
				} catch (IOException e) {
					e.printStackTrace();
					log.info("IOException: " + e.toString());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception=" + e.toString());
		}

		return nResult;
	}
//	public static int[] getFlashWidthHeight(String filePath, double maxWidth, double maxHeight, String bizCode) {
//
//		int[] nResult = {0, 0};
//
//		if (StringUtil.checkNull(filePath).equals("")) {
//			return nResult;
//		}
//
//		try {
//			File file = new File(filePath);
//			if (file.isFile()) {
//				SWFHeader sh = new SWFHeader(file);
//
//				double width = sh.getWidth();
//				double height = sh.getHeight();
//				log.info("getFlashWidthHeight : " + width + "  , " + height);
//				//항만감시체계 납품용 사이즈조절
//				if(width > 1500 || height > 1000) {
//					width = 467.0;
//					height = 511.0;
//				}
//				nResult = getWidthHeight(maxWidth, maxHeight, width, height, bizCode);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.info("Exception="+e.toString());
//		}
//
//		return nResult;
//	}
	
	
	/**
	 * EMF 그래픽 가로/세로 사이즈 구하기
	 * @MethodName	: getEMFWidthHeight
	 * @param filePath
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static int[] getEMFWidthHeight(String filePath, double maxWidth, double maxHeight, String bizCode) {
		
		int[] nResult = {0, 0};
		
		if (StringUtil.checkNull(filePath).equals("")) {
			return nResult;
		}
		
		try {
			File file = new File(filePath);
			
			if (file.isFile()) {
				String uri = file.toURI().toASCIIString();
				
				ImageManager im = new ImageManager(new DefaultImageContext());
				ImageSessionContext isc = new DefaultImageSessionContext(im.getImageContext(), null);
				Source src = isc.needSource(uri);
				
				ImageContext imageContext = new DefaultImageContext();
				
				PreloaderEMF pemf = new PreloaderEMF();
				ImageInfo info = pemf.preloadImage(uri, src, imageContext);
				
				ImageSize is = info.getSize();
				double width = is.getWidthPx();
				double height = is.getHeightPx();
				
				nResult = getWidthHeight(maxWidth, maxHeight, width, height, bizCode);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception="+e.toString());
		} finally {
		}
		
		return nResult;
	}
	
	
	/**
	 * JPEG 이미지 중 아이콘 그래픽 가로/세로 사이즈 구하기 - 기본 사이즈 그대로
	 * @MethodName	: getIconWidthHeight
	 * @param filePath
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static int[] getIconWidthHeight(String filePath, double maxWidth, double maxHeight) {
		
		int[] nResult = {0, 0};
		
		if (StringUtil.checkNull(filePath).equals("")) {
			return nResult;
		}
		
		try {
			File file = new File(filePath);
			
			if (file.isFile()) {
				BufferedImage bi = ImageIO.read(file);
				double width = bi.getWidth();
				double height = bi.getHeight();
				
				nResult[0] = (int)width;
				nResult[1] = (int)height;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception="+e.toString());
		}
		
		return nResult;
	}
	
	
	/**
	 * 최대값을 받아 그 사이즈 내에서 조정하는 모듈
	 * @MethodName	: getWidthHeight
	 * @param maxWidth
	 * @param maxHeight
	 * @param width
	 * @param height
	 * @return
	 */
	public static int[] getWidthHeight(double maxWidth, double maxHeight, double width, double height, String bizCode) {
		log.info("maxWidth : "+maxWidth+", maxHeight : "+maxHeight+", width : "+width+", height : "+height);
		int[] nResult = {0, 0};
		
		try {
			double rate = 0.0;
			
			boolean isCanon = false;
			if ("LSAM".equalsIgnoreCase(bizCode) || "NLS".equalsIgnoreCase(bizCode) || "KBOB".equalsIgnoreCase(bizCode) || "KICC".equalsIgnoreCase(bizCode) || "MUAV".equalsIgnoreCase(bizCode) || "SENSOR".equalsIgnoreCase(bizCode)) {
				isCanon = true;
			}
			
			if (maxWidth > 0 && maxHeight > 0 && width < maxWidth && height < maxHeight) {
				rate = maxWidth / width;
				if(bizCode!=null && isCanon) { rate = 1.0; }
				width = width * rate;
				height = height * rate;
				log.info("maxWidth : " + maxWidth + " maxHeight : " + maxHeight);
				log.info("rate : " + rate + " width : " + width + "height : " + height);
				if (maxHeight > 0 && height > maxHeight) {
					rate = maxHeight / height;
					width = width * rate;
					height = height * rate;
				}
			} else {
				//Step 1. 가로기준으로 전체 사이즈 비율로 조절
				if (maxWidth > 0 && width > maxWidth) {
					rate = maxWidth / width;
					//width = width * rate;
					//2021 06 크면 최대 값으로 변경
					//width  = maxWidth;
					//2022 10 14 원복
					width = width * rate;
					height = height * rate;
					log.info("rate00 : " + rate + " width 02: " + width + "height 02: " + height);
				}
				//Step 2. 가로기준으로 전체 사이즈 비율로 조절후에도 세로 길이가 최대치를 넘어서면 세로비율로 다시 조절
				if (maxHeight > 0 && height > maxHeight) {
					rate = maxHeight / height;
					//2021 06 크면 최대 값으로 변경
					height  = maxHeight;
					width = width*rate;
					log.info("rate11 : " + rate + " width 03: " + width + "height 03: " + height);
				}
			}
			
			nResult[0] = (int)width;
			nResult[1] = (int)height;
			log.info("width : " + width + " height : " + height);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception="+e.toString());
		}
		
		return nResult;
	}
	
}