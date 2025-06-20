package com.expis.ietm.parser;

import com.expis.common.IConstants;
import com.expis.common.ext.ExtConfig;
import com.expis.common.ext.ExternalFileEx;
import com.expis.domparser.*;
import com.expis.util.ImageUtil;
import com.expis.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;

/**
 * [공통모듈]그래픽(Grphprim) Parser Class
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GrphParser {

	private final  VersionParser verParser;
	private final  TextParser textParser;
	private final  TableParser tableParser;
	private final  LinkParser linkParser;

	AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExtConfig.class);
	ExternalFileEx ext = ctx.getBean("extConf", ExternalFileEx.class);

	/**
	 * <pre>
	 * 2021 06 jspark
	 * IPB전용 추가
	 * 2022 12 01 Park.J.S. Update : paNode 로 변경 이미지 리스트 넘어오면 중복되는 로직 발생 관련해서 리스트가 아닌 Node 객체 처리로 변경
	 * </pre>
	 * @param psDto
	 * @param paNode == > 2022 12 01 배열에서 객체로 변경
	 * @param display 화면에 표시 여부
	 * @return
	 */
	public StringBuffer getGrphHtmlIPBPNG(ParserDto psDto, Node paNode, boolean display) {
		StringBuffer rtSB	= new StringBuffer();
		StringBuffer nodeSB	= new StringBuffer();
		StringBuffer verSB	= new StringBuffer();
		String startDiv = "";
		String verEndStr = "";
		//2022 11 23 Park.J.S. IPB 이미지 별로 버전바 처리하기 위해서 수정
		nodeSB	= new StringBuffer();
		//Node paNode = curList.item(i);
		if (paNode == null || !paNode.getNodeName().equals(DTD.GRPH)) {
			log.info("getGrphHtmlIPBPNG ==> Pass 1 : "+paNode);
			return rtSB;
		}
		if (psDto.getTocoType() == null) {
			psDto.setTocoType("");
		}
		if (verParser.checkVehicleType(psDto, paNode) == false) {
			log.info("getGrphHtmlIPBPNG ==> Pass 2 : "+paNode.toString());
			return rtSB;
		}
		try {
			//그래픽 ID, 그래픽명, 확장자명, 그래픽 경로 추출
			String grphId		= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.ID);
			String grphType		= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.TYPE);
			String grphName		= this.getGrphName(paNode);
			String extOrgName	= StringUtil.getExtName(grphName);
			String extName		= extOrgName.toUpperCase();
			log.info("getGrphHtmlIPBPNG ==> grphId : "+grphId+", grphName : "+grphName+", extOrgName : "+extOrgName+", extName : "+extName);
			if(!"PNG".equalsIgnoreCase(extName)) {
				return getGrphHtml(psDto,paNode,true);
			}
			String widthCheck	= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.GRPH_WIDTHCHECK);
			String grphPath		= psDto.getBizIetmdata();
			String systemPath	= psDto.getBizSyspath();
			log.info("getGrphHtmlIPBPNG ==> grphPath : "+grphPath+", systemPath : "+systemPath);
			grphPath		+= "/" + IConstants.GRPH_PATH_IMAGE + "/" +grphName;
			systemPath	+=  IConstants.GRPH_PATH_IMAGE + "\\" + grphName;
			
			//그래픽 사이즈 계산해서 HTML 생성
			File systemFile = new File(systemPath);
			if (systemFile.exists() == true) {
				String[] arrSize	= this.calculateGrphSize(psDto, extName, systemPath, widthCheck, grphType);
				arrSize[0] = "560";//IPB Width 560 Fix
				//2024.04.29 - 도해사이즈 확대 - jingi.kim
				if ( psDto.getFigureWidth() != null && psDto.getFigureHeight() != null ) {
					if ( !"".equals(psDto.getFigureWidth()) && !"".equals(psDto.getFigureHeight()) ) {
						if ( !"0".equals(psDto.getFigureWidth()) && !"0".equals(psDto.getFigureHeight()) ) {
							arrSize[0] = psDto.getFigureWidth();
							arrSize[1] = psDto.getFigureHeight();
						}
					}
				}
				
				if(display) {
					//2023 02 21 jysi EDIT : grphName을 매개변수로 추가하여 하위 사용함수에서 grphName을 다시 구하는 과정을 제거하고자함 
					//nodeSB.append(this.gridGrphHtml(psDto, extName, grphId, grphPath, arrSize[0], arrSize[1], systemPath, true));
					nodeSB.append(this.gridGrphHtml(psDto, extName, grphId, grphPath, arrSize[0], arrSize[1], systemPath, true, grphName));
					nodeSB.append( this.getGrphCaption(psDto, paNode, true, grphName) );
				} else {
					//nodeSB.append(this.gridGrphHtml(psDto, extName, grphId, grphPath, arrSize[0], arrSize[1], systemPath, false));
					nodeSB.append(this.gridGrphHtml(psDto, extName, grphId, grphPath, arrSize[0], arrSize[1], systemPath, false, grphName));
					nodeSB.append( this.getGrphCaption(psDto, paNode, false, grphName) );
				}
			} else {		
				log.info("getGrphHtmlIPBPNG ==> No Image........ : "+systemPath);
			}
			//버전 모듈 추가
			verSB = verParser.checkVersionHtml(psDto, paNode);
			verEndStr = verParser.endVersionHtml(verSB);
			
			startDiv = CSS.DIV_OBJECT;
			StringBuffer addClass = new StringBuffer(startDiv);
			addClass.insert(12,"frame_" + psDto.getFrameClassNum() + " ");
			//log.info("frame grph : " + addClass.toString());
			startDiv = addClass.toString();
			//기존에 for 문 밖에서 처리하면 버전바가 정상적으로 시현되지 않는 현상 있어서 수정
			try {
				rtSB.append(verSB);
				rtSB.append(startDiv);
				rtSB.append(nodeSB);
				rtSB.append(CSS.DIV_END);
				if(verSB.toString().trim().equals("")) {
				}else {
					rtSB.append(verEndStr);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				log.info("GrphParser.getGrphHtml Exception:"+ex.toString());
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			log.info("GrphParser.getGrphHtmlIPBPNG Exception:"+e.toString());
		}
		
		
		return rtSB;
	}
	
	/**
	 * 그래픽 파싱 HTML
	 * @MethodName	: getGrphHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 6. 24.
	 * @ModificationHistory	: 20150112 사이즈 추출과 그리는것 메소드로 분리
	 * @ModificationHistory	: 2021.06 png 추가하면서 display속성 추가 및 수정 jspark
	 * @ModificationHistory	: 2023.03.20 - ParserDto.getFitWidth() = "yes" 일 경우 BrowserWidth 기준으로 가로 사이즈가 결정되도록 추가 - jingi.kim
	 * @param psDto, paNode, display
	 * @return
	 * 플래쉬	: SWF
	 * 이미지	: JPG, GIF, PNG
	 * 동영상	: AVI, WMV, MOV, MP4
	 * 오디오 : MP3 
	 * 3D VR : WRL <- 현재 미지원
	 */
	public StringBuffer getGrphHtml(ParserDto psDto, Node paNode, boolean display) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (paNode == null || !paNode.getNodeName().equals(DTD.GRPH)) {
			return rtSB;
		}
		if (psDto.getTocoType() == null) {
			psDto.setTocoType("");
		}
		
		try {
			if (verParser.checkVehicleType(psDto, paNode) == false) {
				return rtSB;
			}
			
			StringBuffer nodeSB	= new StringBuffer();
			//그래픽 ID, 그래픽명, 확장자명, 그래픽 경로 추출
			String grphId		= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.ID);
			String grphType		= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.TYPE);
			String grphName		= this.getGrphName(paNode);
			String extOrgName	= StringUtil.getExtName(grphName);
			String extName		= extOrgName.toUpperCase();
			log.info("grphId : "+grphId+", grphName : "+grphName+", extOrgName : "+extOrgName+", extName : "+extName);
			if (extName.equals(VAL.IMG_EXT_TIF)) {
				log.info("TRANCE VAL.IMG_EXT_TIF");
				extName = VAL.IMG_EXT_JPG;
			}
			String widthCheck	= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.GRPH_WIDTHCHECK);
			
			//클라이언트 뷰어가 웹인지, 모바일인지 검사하여 이미지 확장자 변경
			if (psDto.getWebMobileKind().equals(IConstants.WEBMB_KIND_MOBILE)) {
				if (extName.equals(VAL.IMG_EXT_SWF)) {
					log.info("TRANCE VAL.IMG_EXT_SWF");
					grphName	= StringUtil.replace(grphName, extOrgName, VAL.IMG_EXT_PNG.toLowerCase());
					extName = VAL.IMG_EXT_PNG;
				}
			}
			
			//지게차 swf 변경
			/*if (psDto.getWebMobileKind().equals(IConstants.WEBMB_KIND_WEB)) {
				if (extName.equals(VAL.IMG_EXT_JPG)) {
					grphName	= StringUtil.replace(grphName, extOrgName, VAL.IMG_EXT_SWF.toLowerCase());
					extName = VAL.IMG_EXT_SWF;
				}
			}*/
			
			String grphPath		= psDto.getBizIetmdata();
			String systemPath	= psDto.getBizSyspath();
			log.info("grphPath : "+grphPath+", systemPath : "+systemPath+", widthCheck : "+widthCheck);
			//20200228 edit LYM 동영상 확장자에 MP4 추가함
			if(extName.equals("AVI") || extName.equals("WMV") || extName.equals("MOV") || extName.equals("MP4")) {
				grphPath		+= "/" + IConstants.GRPH_PATH_VIDEO + "/" +grphName;
				systemPath	+=  IConstants.GRPH_PATH_VIDEO  + "\\" + grphName;
			} else {
				grphPath		+= "/" + IConstants.GRPH_PATH_IMAGE + "/" +grphName;
				systemPath	+=  IConstants.GRPH_PATH_IMAGE + "\\" + grphName;
			}
			
			log.info("File Path : "+systemPath);
			//그래픽 사이즈 계산해서 HTML 생성
			File systemFile = new File(systemPath);
			if (systemFile.exists() == true) {
				String[] arrSize	= this.calculateGrphSize(psDto, extName, systemPath, widthCheck, grphType);
				//2024.04.29 - 도해사이즈 확대 - jingi.kim
				if ( psDto.getFigureWidth() != null && psDto.getFigureHeight() != null ) {
					if ( !"".equals(psDto.getFigureWidth()) && !"".equals(psDto.getFigureHeight()) ) {
						if ( !"0".equals(psDto.getFigureWidth()) && !"0".equals(psDto.getFigureHeight()) ) {
							arrSize[0] = psDto.getFigureWidth();
							arrSize[1] = psDto.getFigureHeight();
						}
					}
				}
				
				//2023.04.21 jysi EDIT : (1)double->int변경처리 코드, (2)그래픽사이즈업 BLOCK2만 적용되도록 수정 
				//int sWidth = Integer.parseInt(arrSize[0]);
//				int sWidth = (new Double(arrSize[0])).intValue();
				int sWidth = (int) Double.parseDouble(arrSize[0]); // Deprecated 이슈로 수정 - 2025.01.01 Chohee.Cha
				
				// 2024.02.21 - null 체크 추가 - jingi.kim
				if ( psDto.getBrowserWidth() != null && psDto.getBrowserHeight() != null ) {
					String winWidth = psDto.getBrowserWidth();
					String winHeight = psDto.getBrowserHeight();
				
					if(winWidth.equals("1280") && winHeight.equals("800") ||
							winWidth.equals("1016") && winHeight.equals("867")) {
						if ("BLOCK2".equals(ext.getBizCode())) sWidth += 417; 
					}
					
					// 2023.03.20 - fitWidth 일 경우 추가 - jingi.kim
					if ( psDto.getFitWidth() != null && "yes".equalsIgnoreCase(psDto.getFitWidth()) ) {
						// log.info("Fit Width Start............. " + sWidth + " vs " + winWidth);
						if ( sWidth < Integer.parseInt(winWidth) ) {
							sWidth = Integer.parseInt(winWidth);
						}
					}
				}
				
				//2023 02 21 jysi EDIT : grphName을 매개변수로 추가하여 하위 사용함수에서 grphName을 다시 구하는 과정을 제거하고자함 
				//nodeSB = this.gridGrphHtml(psDto, extName, grphId, grphPath, String.valueOf(sWidth), arrSize[1], systemPath, display);	
				nodeSB = this.gridGrphHtml(psDto, extName, grphId, grphPath, String.valueOf(sWidth), arrSize[1], systemPath, display, grphName);
			} else {		
				log.info("No Image........ : "+systemPath);
				nodeSB = this.gridGrphNoImage();
				
				//20201119 add LYM No-image일 경우 디버깅위해 코드 추가
				nodeSB.append("<span alt='").append(grphPath).append("' />");
			}
			
			//버전 모듈 추가
			StringBuffer verSB = verParser.checkVersionHtml(psDto, paNode);
			String verEndStr = verParser.endVersionHtml(verSB);
			
			String startDiv = CSS.DIV_OBJECT;
			StringBuffer addClass = new StringBuffer(startDiv);
			addClass.insert(12,"frame_" + psDto.getFrameClassNum() + " ");
			//log.info("frame grph : " + addClass.toString());
			startDiv = addClass.toString();
			
			//2021 06 jspark
			//버전 정보 존재하는 확인후 추가
			if(verSB.toString().trim().equals("")) {
			}else {
				log.info("verSB : "+verSB);
				rtSB.append(verSB);
			}
			rtSB.append(startDiv);
			rtSB.append(nodeSB);
			rtSB.append( this.getGrphCaption(psDto, paNode, display, grphName) );
			rtSB.append(CSS.DIV_END);
			rtSB.append(verEndStr);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("GrphParser.getGrphHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	/**
	 * 그래픽 중 Workcard(작업카드)에서 사용하는 그림 파싱 HTML
	 * @MethodName	: getGrphWorkcardHtml
	 * @AuthorDate		: LIM Y.M. / 2015. 1. 12.
	 * @ModificationHistory	: png 작업하면서 display추가 jsaprk
	 * @param psDto, paNode, display
	 * @return
	 */
	public StringBuffer getGrphWorkcardHtml(ParserDto psDto, Node paNode, boolean display) {
		
		StringBuffer rtSB = new StringBuffer();
		
		if (paNode == null || !paNode.getNodeName().equals(DTD.WC_WIMG)) {
			return rtSB;
		}
		if (psDto.getTocoType() == null) {
			psDto.setTocoType("");
		}
		
		try {
			if (verParser.checkVehicleType(psDto, paNode) == false) {
				return rtSB;
			}
			
			StringBuffer nodeSB	= new StringBuffer();
			
			//그래픽 ID, 그래픽명, 확장자명, 그래픽 경로 추출
			String grphId		= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.ID);
			String grphName		= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.NAME);
			String extName		= StringUtil.getExtName(grphName).toUpperCase();
			String widthCheck	= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.GRPH_WIDTHCHECK);
			String type			= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.TYPE);
			
			String grphPath		= psDto.getBizIetmdata();
			String systemPath	= psDto.getBizSyspath();
			grphPath		+= "/" + IConstants.GRPH_PATH_IMAGE + "/" +grphName;
			systemPath	+= "/" + IConstants.GRPH_PATH_IMAGE + "/" +grphName;
			
			//그래픽 사이즈 계산해서 HTML 생성
			File systemFile = new File(systemPath);
			if (systemFile.exists() == true) {
				String[] arrSize	= this.calculateGrphSize(psDto, extName, systemPath, widthCheck, type);
				//2024.04.29 - 도해사이즈 확대 - jingi.kim
				if ( psDto.getFigureWidth() != null && psDto.getFigureHeight() != null ) {
					if ( !"".equals(psDto.getFigureWidth()) && !"".equals(psDto.getFigureHeight()) ) {
						if ( !"0".equals(psDto.getFigureWidth()) && !"0".equals(psDto.getFigureHeight()) ) {
							arrSize[0] = psDto.getFigureWidth();
							arrSize[1] = psDto.getFigureHeight();
						}
					}
				}
				
				//2023 02 21 jysi EDIT : grphName을 매개변수로 추가하여 하위 사용함수에서 grphName을 다시 구하는 과정을 제거하고자함 
				//nodeSB = this.gridGrphHtml(psDto, extName, grphId, grphPath, arrSize[0], arrSize[1], systemPath, display);
				nodeSB = this.gridGrphHtml(psDto, extName, grphId, grphPath, arrSize[0], arrSize[1], systemPath, display, grphName);
			} else {
				nodeSB = this.gridGrphNoImage();
			}
			
			//버전 모듈 추가
			StringBuffer verSB = verParser.checkVersionHtml(psDto, paNode);
			//2022 12 14 Park.J.S. ADD : 버전 바 관련 이미지만 별도로 존재 하는 경우 부모객체에서 찾아야하기때문에 추가
			if (verSB == null || verSB.toString().length() <= 0) {//버전 미존재
				//이미지만 별도로 존재 하는 경우 workcard 자체에 버전 정보가 존재 하므로 위로 2단계 까지 확인 해서 처리
				try {
					log.info("paNode.getParentNode().getNodeName() : "+paNode.getParentNode().getNodeName());
					if(paNode.getParentNode().getNodeName() == "workcard") {
						verSB = verParser.checkVersionHtml(psDto, paNode.getParentNode());
					}else if(paNode.getParentNode().getParentNode().getNodeName() == "workcard") {
						verSB = verParser.checkVersionHtml(psDto, paNode.getParentNode().getParentNode());
					}
				}catch (Exception e) {}
			}
			String verEndStr = verParser.endVersionHtml(verSB);
			
			rtSB.append(verSB);
			rtSB.append(nodeSB);
			rtSB.append(verEndStr);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("GrphParser.getGrphWorkcardHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 이미지 노드에서 그래픽 명 추출
	 * @MethodName	: getGrphName
	 * @AuthorDate		: LIM Y.M. / 2015. 1. 12.
	 * @ModificationHistory	: 
	 * @param imgNode
	 * @return
	 */
	public String getGrphName(Node imgNode) {
		
		return this.getGrphPath(imgNode, "");
	}


	/**
	 * 이미지 노드에서 그래픽의 경로 추출
	 * @MethodName	: getGrphPath
	 * @AuthorDate		: LIM Y.M. / 2014. 7. 2.
	 * @ModificationHistory	: 
	 * @param imgNode, bizPath
	 * @return
	 */
	public String getGrphPath(Node imgNode, String bizPath) {
		
		String rtStr = "";
		
		if (imgNode == null) {
			return rtStr;
		}
		
		try {
			NamedNodeMap imgAttr = imgNode.getAttributes();
			String grphPath = XmlDomParser.getAttributes(imgAttr, ATTR.GRPH_PATH);
			if (grphPath.indexOf(VAL.IMG_FD_IPBIMAGE) > -1) {
				grphPath = grphPath.substring((VAL.IMG_FD_IPBIMAGE.length()+1), grphPath.length());
				
			} else if (grphPath.indexOf(VAL.IMG_FD_IMAGE) > -1) {
				grphPath = grphPath.substring((VAL.IMG_FD_IMAGE.length()+1), grphPath.length());
			} else if (grphPath.indexOf(VAL.IMG_FD_FOLDER) > -1) {
				grphPath = grphPath.substring((grphPath.indexOf(VAL.IMG_FD_FOLDER)+1), grphPath.length());
			}
			
			if (!bizPath.equals("")) {
				rtStr = bizPath + "/" + IConstants.GRPH_PATH_IMAGE + "/" +grphPath;
			} else {
				rtStr = grphPath;	
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("GrphParser.getGrphPath Exception:"+ex.toString());
		}
		
		return rtStr;
	}
	
	/**
	 * 2021 06 jsaprk 
	 * getGrphCaption에 display 속성 추가로 기존 함수 수정함
	 * @param psDto
	 * @param grphNode
	 * @param display
	 * @return
	 */
	public String getGrphCaption(ParserDto psDto, Node grphNode) {
		return getGrphCaption(psDto, grphNode, true, "");
	}
	
	/**
	 * 이미지 노드에서 그래픽의 제목 추출
	 * @param display 
	 * @param grphName 
	 * @MethodName	: getGrphCaption
	 * @AuthorDate		: LIM Y.M. / 2014. 7. 2.
	 * @ModificationHistory	: LIM Y.M. / 2016. 2. 2.
	 * @ModificationHistory	: png 작업하면서 display, grphName 속성추가 2021 06 jsaprk 
	 * @param psDto, grphNode, display, grphName
	 * @return
	 */
	public String getGrphCaption(ParserDto psDto, Node grphNode, boolean display, String grphName) {
		log.info("getGrphCaption ==> "+grphNode);
		StringBuffer rtSB = new StringBuffer();
		
		if (grphNode == null) {
			return rtSB.toString();
		}
		
		try {
			StringBuffer nodeSB = new StringBuffer();
			StringBuffer tableSB = new StringBuffer();
			String captionStr = "";
			NodeList childList = grphNode.getChildNodes();
			if (childList != null && childList.getLength() > 0) {
				for (int i=0; i<childList.getLength(); i++) {
					Node childNode = childList.item(i);
					if (childNode.getNodeType() != Node.ELEMENT_NODE) {
						continue;
					}
					log.info("childNode.getNodeName() : "+childNode.getNodeName());
					//그래픽 하위에 캡션 위치에 표(테이블) <table> 노드 있을 경우 처리
					if (childNode.getNodeName().equals(DTD.TABLE)) {
						tableSB.append( tableParser.getTableHtml(psDto, childNode, 1, "grph") );
						
					} else if (childNode.getNodeName().equals(DTD.TEXT)) {
						captionStr = StringUtil.checkNull(XmlDomParser.getTxt(childNode)).trim();
						if (!captionStr.equals("")) {
							//2022 08 24 Park.J.S. Update 아이콘 처리
							/*
							captionStr = captionStr.replaceAll("&#254;&#138;&#2;&#255;", "");
							captionStr = captionStr.replaceAll("&amp;#254;&amp;#138;&amp;#2;&amp;#255;", "");
							captionStr = captionStr.replaceAll("&amp;#254;&amp;#138;&amp;#2;&amp;#255;", "");
							for(int j= 0; j<20; j++) {
								captionStr = captionStr.replaceAll("&#24;&#" + (10000 + j) + ";", "");
							}
							//2022 07 08 Park.J.S. 그림명에 커맨드 들어갈경우 제거
							captionStr = Pattern.compile("&#0;").matcher(captionStr).replaceAll("");
							captionStr = Pattern.compile("&#[0-9]{1};").matcher(captionStr).replaceAll("");
							captionStr = Pattern.compile("&#[0-9]{2};").matcher(captionStr).replaceAll("");
							captionStr = Pattern.compile("&#[0-9]{3};").matcher(captionStr).replaceAll("");
							captionStr = Pattern.compile("&#[0-9]{4};").matcher(captionStr).replaceAll("");
							
//							captionStr = captionStr.replaceAll("&#24;&#10001;&#24;&#10002;&#24;&#10003;", "");
//							captionStr = captionStr.replaceAll("&#24;&#10004;&#24;&#10005;&#24;&#10006;", "");
//							captionStr = captionStr.replaceAll("&#24;&#10007;&#24;&#10008;&#24;&#10009;", "");
//							captionStr = captionStr.replaceAll("&#24;&#10010;&#24;&#10011;&#24;&#10012;", "");
//							captionStr = captionStr.replaceAll("&#24;&#10013;&#24;&#10014;&#24;&#10015;,&#24;&#10016;", "");
							nodeSB.append(captionStr);
							*/
							nodeSB.append(textParser.getTextPara(psDto, childNode));
							continue;
						}
						
						String linkType = XmlDomParser.getAttributes(childNode.getAttributes(), ATTR.LINK_TYPE);
						log.info("linkType : "+linkType);
						if (linkType.equals(VAL.LINK_TYPE)) {
							nodeSB.append( linkParser.getLinkHtml(psDto, childNode) );
						} else {
							nodeSB.append( CodeConverter.getCodeConverter(psDto, captionStr, "", "") );
						}
					}
				}
			}
			
			
			
			if (tableSB.length() > 0) {
				rtSB.append(CSS.DIV_GRPH_TABLE);
				rtSB.append(tableSB);
				rtSB.append(CSS.DIV_END);
			}
			//2023 02 08 Park.J.S. : Update p에서 div로 태그 변경
			//2023 02 21 jysi EDIT : ".png"로 박혀있는 부분 확장자 변수(extOrgName) 사용으로 변경 => 추후 문제없을 시 주석 삭제할것
			String extOrgName = StringUtil.getExtName(grphName);
			String fileDot = ".";
			log.info("extOrgName : "+extOrgName+", replaceTarget : "+(fileDot+extOrgName));
			if(display) {
				//rtSB.append("<div style='display:block;' id='"+grphName.replace(".png", "")+"_PTag'>");
				rtSB.append("<div style='display:block;' id='"+grphName.replace(fileDot+extOrgName, "")+"_PTag'>");
				//rtSB.append(CSS.P);
				rtSB.append(nodeSB);
				rtSB.append(CSS.DIV_END);
			}else {
				//rtSB.append("<div style='display:none;' id='"+grphName.replace(".png", "")+"_PTag'>");
				rtSB.append("<div style='display:none;' id='"+grphName.replace(fileDot+extOrgName, "")+"_PTag'>");
				rtSB.append(nodeSB);
				rtSB.append(CSS.DIV_END);
				
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("GrphParser.getGrphCaption Exception:"+ex.toString());
		}
		log.info("rtSB.toString() : "+rtSB.toString());
		return rtSB.toString();
	}
	
	
	/**
	 * 그래픽 관련 스크립트
	 * @MethodName	: getGrphScript
	 * @AuthorDate		: LIM Y.M. / 2015. 1. 12.
	 * @ModificationHistory	: 
	 * @return
	 */
	public String getGrphScript() {
		
		String rtStr = "";
		
		return rtStr;
	}
	
	
	/**
	 * 그래픽 사이즈 계산
	 * @param type 
	 * @MethodName	: calculateGrphSize
	 * @AuthorDate		: LIM Y.M. / 2015. 1. 12.
	 * @ModificationHistory	: KIM K.S. / 2017. 6. 23.
	 * @ModificationHistory	: png 작업 2021 06 jsaprk 
	 * @ModificationHistory	: IMG_WIDTHCHECK 사이즈 보정 작업 추가  2021 12 jsaprk 
	 * @ModificationHistory	: 2022 09 29 Park.J.S. : type 처리 추가  
	 * @param psDto, extName, systemPath, widthCheck, type
	 * @return
	 */
	public String[] calculateGrphSize(ParserDto psDto, String extName, String systemPath, String widthCheck, String type) {
		String[] rtSB = new String[2];
		rtSB[0]		= "0";
		rtSB[1]		= "0";
		if (StringUtil.checkNull(extName).equals("") || StringUtil.checkNull(systemPath).equals("")) {
			return rtSB;
		}
		
		try {
			/*
			 * 이미지에서 사용 가능한 최대 사이즈 기본값
			 */
			double maxWidth		= 750.;
			double maxHeight	= 600.;
			String sWidth	= maxWidth + "";
			String sHeight	= maxHeight + "";
			log.info("psDto.getWebMobileKind() : "+psDto.getWebMobileKind());
			if(psDto.getWebMobileKind().equals("01")) {
				log.info("psDto.getBrowserWidth() : "+psDto.getBrowserWidth()+", psDto.getBrowserHeight() : "+psDto.getBrowserHeight());
				if (!StringUtil.checkNull(psDto.getBrowserWidth()).equals("") && !StringUtil.checkNull(psDto.getBrowserHeight()).equals("")) {
					double yesWidth	= (Double.parseDouble(psDto.getBrowserWidth()) - 417) - 200;
					double yesHeight	= (Double.parseDouble(psDto.getBrowserHeight()) - 277);
					//2022 02 28 이미지 사이즈 최대치 사용으로 수정
					yesWidth = (Double.parseDouble(psDto.getBrowserWidth()))-417;
					//2023 01 06 Park.J.S. ADD : BLOCK2 이미지 사이즈 최대치 왼쪽메뉴 펴진 기준으로 수정
					if("BLOCK2".equals(ext.getBizCode())) {
						yesWidth = (Double.parseDouble(psDto.getBrowserWidth()))-417-277;
					}
					log.info("yesWidth : " + yesWidth);
					log.info("yesHeight : " + yesHeight);
					log.info("type : "+type);
					if (widthCheck.equals(VAL.IMG_VERSION)) {
						log.info("IMG Size Type VERSION");
						yesWidth		= yesWidth * 0.5;
						yesHeight		= yesHeight * 0.5;
					} else if (psDto.getTocoType().equals(IConstants.TOCO_TYPE_IPB)) {
						log.info("IMG Size Type IPB");
						yesWidth		= yesWidth - 100;
					} else if(psDto.getTocoType().equals(IConstants.TOCO_TYPE_IPB) || psDto.getTocoType().equals(IConstants.TOCO_TYPE_FIELD)
							||psDto.getTocoType().equals(IConstants.TOCO_TYPE_ROOT)) {
						log.info("IMG Size Type FIELD or ROOT");
						yesWidth		= yesWidth * 0.95;
						yesHeight		= yesHeight * 0.95;
					} else if (widthCheck.equals(VAL.IMG_WIDTHCHECK)) {
						log.info("IMG Size Type WIDTHCHECK");
						yesWidth		= yesWidth * 1.0;
						yesHeight		= yesHeight * 1.0;
					} else {
						log.info("IMG Size Type else : "+(yesHeight-150));
						yesHeight		= yesHeight - 150;
						//2022 09 29 Park.J.S. FI 관련 이미지 가로 사이즈 조절 추가
						if(type != null && "fi_mainimg".equalsIgnoreCase(type)) {
							yesWidth = 500.0;
							log.info("fi_mainimg change yesWidth : " + yesWidth);
						}
					}
					if (widthCheck.equals(VAL.IMG_WIDTHCHECK)) {
						log.info("IMG_WIDTHCHECK 최대 사이즈 사용");
						maxWidth = yesWidth;
						maxHeight = yesHeight;
					}else {
						log.info("maxWidth : "+maxWidth+", yesWidth : "+yesWidth);
						log.info("maxHeight : "+maxHeight+", yesHeight : "+yesHeight);
						if (maxWidth > yesWidth) {
							maxWidth = yesWidth;
						}
						if (maxHeight > yesHeight) {
							maxHeight = yesHeight;
						}
					}
					//2021 06 jspark
					//2022 06 14 Park.J.S Update 기본 해상도 처리 위해 수정
					if (0 > yesWidth) {
						maxWidth = 750;
					}
					
				} else {
					double yesWidth  = maxWidth - 200;
					double yesHeight = maxHeight - 277;
					
					//버전변경정보 팝업시
					maxWidth		= yesWidth * 0.5;
					maxHeight		= yesHeight * 0.5;
				}
				//2022 10 19 jysi edit : ImageUtil에 BIZ_CODE값 넘김(LSAM과 BLOCK2 구분용)
				log.info("BIZ_CODE : " + ext.getBizCode());
				log.info("maxWidth : "+maxWidth+", maxHeight : "+maxHeight+", psDto.getTocoType() : "+psDto.getTocoType());
				if (!psDto.getTocoType().equals("") && psDto.getTocoType().equals(IConstants.TOCO_TYPE_IPB)) {
					if (extName.equals(VAL.IMG_EXT_SWF)) {
						int[] grphSize = ImageUtil.getFlashWidthHeight(systemPath, maxWidth, maxHeight, ext.getBizCode());
						if (grphSize[0] > 0 && grphSize[1] > 0) {
							sWidth		= grphSize[0] + "";
							sHeight	= grphSize[1] + "";
						}
					} else if (extName.equals(VAL.IMG_EXT_JPG) || extName.equals(VAL.IMG_EXT_GIF)
							|| extName.equals(VAL.IMG_EXT_PNG) || extName.equals(VAL.IMG_EXT_BMP)) {
						
						int[] grphSize = ImageUtil.getJPGWidthHeight(systemPath, maxWidth, maxHeight, ext.getBizCode());
						if (grphSize[0] > 0 && grphSize[1] > 0) {
							sWidth		= grphSize[0] + "";
							sHeight	= grphSize[1] + "";
						}
					}
				} else {
					log.info("ELSE extName : "+extName);
					if (extName.equals(VAL.IMG_EXT_SWF)) {
						int[] grphSize = ImageUtil.getFlashWidthHeight(systemPath, maxWidth, maxHeight, ext.getBizCode());
						if (grphSize[0] > 0 && grphSize[1] > 0) {
							sWidth		= grphSize[0] + "";
							sHeight	= grphSize[1] + "";
						}
					} else if (extName.equals(VAL.IMG_EXT_JPG) || extName.equals(VAL.IMG_EXT_GIF)
							|| extName.equals(VAL.IMG_EXT_PNG) || extName.equals(VAL.IMG_EXT_BMP)) {
						
						int[] grphSize = ImageUtil.getJPGWidthHeight(systemPath, maxWidth, maxHeight, ext.getBizCode());
						if (grphSize[0] > 0 && grphSize[1] > 0) {
							sWidth		= grphSize[0] + "";
							sHeight	= grphSize[1] + "";
						}
						log.info("grphSize[0] : "+grphSize[0]+", grphSize[1] : "+grphSize[1] +", sWidth : "+sWidth+", sHeight : "+sHeight);
					} else if (extName.equals(VAL.IMG_EXT_AVI) || extName.equals(VAL.IMG_EXT_WMV)
							|| extName.equals(VAL.IMG_EXT_MOV) || extName.equals(VAL.IMG_EXT_MP4)) {
					} else if (extName.equals(VAL.IMG_EXT_MP3)) {
					} else if (extName.equals(VAL.IMG_EXT_WRL)) {
					}
				}
			} else {
				sWidth = "290";
				sHeight = "200";
			}
			rtSB[0]		= sWidth;
			rtSB[1]		= sHeight;
			log.info("widthCheck : "+widthCheck+", psDto.getWebMobileKind() : "+psDto.getWebMobileKind());
			if(widthCheck.equals(VAL.IMG_WIDTHCHECK) && !psDto.getWebMobileKind().equals("02")) {
				//rtSB[0] = "990";
				//rtSB[0] = "900"; //20191107 edit 외부연동으로 호출시 /admin/gs/outCall.do 이미지가 넘어감
				//rtSB[0] = "800"; //20191115 edit 사이즈 축소 //2021 12 사이즈 조절 제거
			}
		
			log.info("rtSB[0] : " + rtSB[0]);
			log.info("rtSB[1] : " + rtSB[1]);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("GrphParser.calculateGrphSize Exception:"+ex.toString());
		}
		return rtSB;
	}
	
	
	/**
	 * 그래픽 HTML 태그 그림
	 * @param systemPath 
	 * @param display 
	 * @MethodName	: gridGrphObject
	 * @AuthorDate		: LIM Y.M. / 2015. 1. 12.
	 * @ModificationHistory	: png 작업하면서 systemPath, display 속성추가 2021 06 jsaprk  
	 * @ModificationHistory : grphName 속성추가 2023 02 21 jysi
	 * @param psDto, extName, grphId, grphPath, sWidth, sHeight, systemPath, display
	 * @return
	 */
	public StringBuffer gridGrphHtml(ParserDto psDto, String extName, String grphId, String grphPath, String sWidth, String sHeight, String systemPath, boolean display, String grphName) {
		//log.info("gridGrphHtml ==> extName : "+extName+", grphId : "+grphId+", grphPath : "+grphPath+", sWidth : "+sWidth+", sHeight : "+sHeight+", systemPath : "+systemPath+", display : "+display);
		log.info("gridGrphHtml ==> extName : "+extName+", grphId : "+grphId+", grphPath : "+grphPath+", sWidth : "+sWidth+", sHeight : "+sHeight+", systemPath : "+systemPath+", display : "+display+", grphName : "+grphName);
		StringBuffer rtSB = new StringBuffer();
		//log.info("Search  gridGrphHtml - " + extName);
		if (StringUtil.checkNull(extName).equals("") || StringUtil.checkNull(grphPath).equals("")) {
			return rtSB;
		}
		
		try {
			String grphHtml	= "";
			String param		= "";
			log.info("psDto.getTocoType() : "+psDto.getTocoType()+", psDto.getWebMobileKind() : "+psDto.getWebMobileKind());
			if (!psDto.getTocoType().equals("") && psDto.getTocoType().equals(IConstants.TOCO_TYPE_IPB)) {
				if(psDto.getWebMobileKind().equals("02")) {
					grphHtml = CSS.getDivIPBImg(grphId, grphPath, sWidth, sHeight, param);
				} else {
					if(!"SWF".equalsIgnoreCase(extName)) {//2022 11 07 SWF 아닐 경우 무조건 이미지 사용으로 수정
						StringBuffer buffer = new StringBuffer();
						//2023 02 21 jysi EDIT : grphName을 매개변수로 추가하여 함수에서 grphName을 다시 구하는 과정을 제거하고자함 
						//HashMap<String,Object> pngMap = makePngImgObj(Integer.valueOf(sWidth),grphPath,true, systemPath, psDto);
						HashMap<String,Object> pngMap = makePngImgObj(Integer.valueOf(sWidth),grphPath,true, systemPath, psDto, grphName);
						//HashMap<String,Object> map = new GrphprimData().gridGrphprimMap(grphPath,Integer.valueOf(sWidth),pngMap, systemPath);
						HashMap<String,Object> map = new GrphprimData().gridGrphprimMap(grphName,Integer.valueOf(sWidth),pngMap, systemPath);
						log.debug("map : "+map);
						//IPB의 경우 한페이지에서 여러장의 이미지 컨트롤 할경우가 있어서 해당 경우 처리함
						if(pngMap.containsKey("heigth")){
							//buffer.append(makePngDivObj(Integer.valueOf(sWidth), systemPath, display, pngMap.get("heigth")+""));
							buffer.append(makePngDivObj(Integer.valueOf(sWidth), systemPath, display, pngMap.get("heigth")+"", grphName));
							buffer.append(pngMap.get("htmlStr"));
						}else{
							//buffer.append(makePngDivObj(Integer.valueOf(sWidth), systemPath, display, "600"));
							buffer.append(makePngDivObj(Integer.valueOf(sWidth), systemPath, display, "600", grphName));
							buffer.append(pngMap.get("htmlStr"));
						}
						log.info("map.containsKey(\"htmlCode\") : "+map.containsKey("htmlCode")+", map.get(\"htmlCode\") : "+map.get("htmlCode"));
						if(map.containsKey("htmlCode") && !"".equals(map.get("htmlCode"))){
							buffer.append(map.get("htmlCode"));
						}else{
							log.info("Only Image......");
						}
						buffer.append("</div></div></center>");
						grphHtml = buffer.toString();
					}else {
						grphHtml = CSS.getDivIPBFlash(grphId, grphPath, sWidth, sHeight, param);
					}
				}
				
			} else {
				if (extName.equals(VAL.IMG_EXT_SWF)) {
					grphHtml = CSS.getDivFlash(grphId, grphPath, sWidth, sHeight, param);
				} else if (extName.equals(VAL.IMG_EXT_JPG) || extName.equals(VAL.IMG_EXT_GIF)
						|| extName.equals(VAL.IMG_EXT_PNG) || extName.equals(VAL.IMG_EXT_BMP)) {
					if(extName.equals(VAL.IMG_EXT_PNG)) {
						//TODO PNG 하위 객체 처리 추가 imgFilePath
						StringBuffer buffer = new StringBuffer();
						//HashMap<String,Object> pngMap = makePngImgObj(Integer.valueOf(sWidth),grphPath,true, systemPath, psDto);
						HashMap<String,Object> pngMap = makePngImgObj(Integer.valueOf(sWidth),grphPath,true, systemPath, psDto, grphName);
						//HashMap<String,Object> map = new GrphprimData().gridGrphprimMap(grphPath,Integer.valueOf(sWidth),pngMap, systemPath);
						HashMap<String,Object> map = new GrphprimData().gridGrphprimMap(grphName,Integer.valueOf(sWidth),pngMap, systemPath);
						if(pngMap.containsKey("heigth")){
							//buffer.append(makePngDivObj(Integer.valueOf(sWidth), grphPath, true, pngMap.get("heigth")+""));
							buffer.append(makePngDivObj(Integer.valueOf(sWidth), grphPath, true, pngMap.get("heigth")+"", grphName));
							buffer.append(pngMap.get("htmlStr"));
						}else{
							//buffer.append(makePngDivObj(Integer.valueOf(sWidth), grphPath, true, "600"));
							buffer.append(makePngDivObj(Integer.valueOf(sWidth), grphPath, true, "600", grphName));
							buffer.append(pngMap.get("htmlStr"));
						}
						log.info("map.containsKey(\"htmlCode\") : "+map.containsKey("htmlCode")+", map.get(\"htmlCode\") : "+map.get("htmlCode"));
						if(map.containsKey("htmlCode") && !"".equals(map.get("htmlCode"))){
							buffer.append(map.get("htmlCode"));
						}else{
							log.info("Only Image......");
						}
						buffer.append("</div></div></center>");
						grphHtml = buffer.toString();
					}else {
						grphHtml = CSS.getDivJpeg(grphId, grphPath, sWidth, sHeight, param);
					}
				} else if (extName.equals(VAL.IMG_EXT_AVI) || extName.equals(VAL.IMG_EXT_WMV)
						|| extName.equals(VAL.IMG_EXT_MOV) || extName.equals(VAL.IMG_EXT_MP4)) {
					
					grphHtml = CSS.getDivVideo(grphId, grphPath, sWidth, sHeight, param);
					
				} else if (extName.equals(VAL.IMG_EXT_MP3)) {
					
					grphHtml = CSS.getDivAudio(grphId, grphPath, sWidth, sHeight, param);
					
				} else if (extName.equals(VAL.IMG_EXT_WRL)) {
					
					grphHtml = CSS.getDivVR(grphId, grphPath, sWidth, sHeight, param);
				}
			}
			
			rtSB.append(grphHtml);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("GrphParser.gridGrphObject Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 그래픽 파일이 서버에 실제로 존재하지 않을 경우 대체 이미지로 표시 
	 * @MethodName	: gridGrphNoImage
	 * @AuthorDate		: LIM Y.M. / 2017. 1. 23.
	 * @ModificationHistory	: 
	 * @return
	 */
	public StringBuffer gridGrphNoImage() {
		
		StringBuffer rtSB = new StringBuffer();
		
		try {	
			rtSB.append(CSS.DIV_NO_IMAGE);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("GrphParser.gridGrphNoImage Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	/**
	 * <pre>
	 * png 이미지를 컨트롤 하기위해 DIV 레이어로 감싸는 객체 생성 
	 * </pre>
	 * @author jspark
	 * @ModificationHistory	: grphName 속성추가 2023 02 21 jysi 
	 * @param width, idStr, displayFlag, height, grphName
	 * @return
	 */
	public Object makePngDivObj(int width, String idStr, boolean displayFlag, String height, String grphName) {
		//log.info("makePngDivObj ==> width : "+width+", idStr : "+idStr+", displayFlag : "+displayFlag+", height : "+height);
		log.info("makePngDivObj ==> width : "+width+", idStr : "+idStr+", displayFlag : "+displayFlag+", height : "+height+", grphName : "+grphName);
		String extOrgName = StringUtil.getExtName(grphName);
		String fileDot = ".";
		log.info("extOrgName : "+extOrgName+", replaceTarget : "+(fileDot+extOrgName));
		String fileName = grphName.replace(fileDot+extOrgName, "");
		/*
		String fileName = idStr.substring(idStr.lastIndexOf("\\")+1,idStr.length()).replaceAll(".png", "");
		log.info("fileName.indexOf(\"/\") : "+fileName.indexOf("/"));
		if(fileName.indexOf("/") >= 0) {
			log.info("slice type a");
			fileName = idStr.substring(idStr.lastIndexOf("/")+1,idStr.length()).replaceAll(".png", "");
		}
		if(fileName.indexOf("//") >= 0) {
			log.info("slice type b");
			fileName = idStr.substring(idStr.lastIndexOf("//")+1,idStr.length()).replaceAll(".png", "");
		}
		*/
		log.info("fileName : "+fileName);
//		if(displayFlag){
//			return "<center><div id='"+fileName+"' name='pngDivArea' style='width: 100%; height:auto; display: block; overflow: hidden; border: 0px solid gray;' > <div name='pngDivArea' style='position: relative;top: 0px;left: 0px; width: "+width+"px; height:"+(new Double(height)).intValue()+"px; border: 0px solid red;' onmousedown='javascript:startDrag(event,this);'>";
//		}else{
//			return "<center><div id='"+fileName+"' name='pngDivArea' style='width: 100%; height:auto; display: none; overflow: hidden; border: 0px solid gray;' > <div name='pngDivArea' style='position: relative;top: 0px;left: 0px; width: "+width+"px; height:"+(new Double(height)).intValue()+"px; border: 0px solid red;' onmousedown='javascript:startDrag(event,this);'>";
//		}
		// Deprecated 이슈로 수정 - 2025.01.01 Chohee.Cha
		if(displayFlag){
			return "<center><div id='"+fileName+"' name='pngDivArea' style='width: 100%; height:auto; display: block; overflow: hidden; border: 0px solid gray;' > <div name='pngDivArea' style='position: relative;top: 0px;left: 0px; width: "+width+"px; height:"+(int) Double.parseDouble(height)+"px; border: 0px solid red;' onmousedown='javascript:startDrag(event,this);'>";
		}else{
			return "<center><div id='"+fileName+"' name='pngDivArea' style='width: 100%; height:auto; display: none; overflow: hidden; border: 0px solid gray;' > <div name='pngDivArea' style='position: relative;top: 0px;left: 0px; width: "+width+"px; height:"+(int) Double.parseDouble(height)+"px; border: 0px solid red;' onmousedown='javascript:startDrag(event,this);'>";
		}
	}
	/**
	 * <pre>
	 * PNG 이미지 객체 생성 width 와 실제 이미지 객체의 사이즈를 참고해서 이미지 를 줄인다
	 * </pre>
	 * @author jspark
	 * @ModificationHistory	: grphName 속성추가 2023 02 21 jysi 
	 * @param width, imgStr, sizeCheckFlag, imgFilePath, psDto, grphName
	 * @return
	 */
	public HashMap<String, Object> makePngImgObj(int width, String imgStr, boolean sizeCheckFlag, String imgFilePath, ParserDto psDto, String grphName) {
		//log.info("makePngImgObj ==> width : "+width+", imgStr : "+imgStr+", sizeCheckFlag : "+sizeCheckFlag+", imgFilePath : "+imgFilePath);
		log.info("makePngImgObj ==> width : "+width+", imgStr : "+imgStr+", sizeCheckFlag : "+sizeCheckFlag+", imgFilePath : "+imgFilePath+", grphName : "+grphName);
		HashMap<String, Object> map = new HashMap<String, Object>();
		String extOrgName = StringUtil.getExtName(grphName);
		String fileDot = ".";
		log.info("extOrgName : "+extOrgName+", replaceTarget : "+(fileDot+extOrgName));
		String fileName = grphName.replace(fileDot+extOrgName, "");
		/*
		if(imgStr.indexOf(".png") < 0){
			imgStr =  imgStr+".png";
		}
		String fileName = imgStr.substring(imgStr.lastIndexOf("\\")+1,imgStr.length()).replaceAll(".png", "");
		if(fileName.indexOf("/") > 0) {
			fileName = imgStr.substring(imgStr.lastIndexOf("/")+1,imgStr.length()).replaceAll(".png", "");
		}
		*/
		log.info("fileName : "+fileName);
		if(sizeCheckFlag){
			try {
				Image img  = new ImageIcon(imgFilePath).getImage();
				double imgWidth          = img.getWidth(null);
				double imgHeight         = img.getHeight(null);
				double heigth = imgHeight*(width/imgWidth);
				log.info("imgWidth : "+imgWidth+", imgHeight : "+imgHeight+", heigth : "+heigth);
				map.put("heigth", ""+heigth);
				map.put("imgWidth", ""+imgWidth);
				map.put("imgHeight", ""+imgHeight);
//				if(new File(imgFilePath.replace(fileDot+extOrgName, ".svg")).exists()) {
//					log.info("USE .svg FILE");
//					map.put("htmlStr", "<img name='pngImgArea' contId='' tocoId='"+psDto.getTocoId()+"' toKey='"+psDto.getToKey()+"' biz='"+psDto.getBiz()+"'  scwidth='"+width+"' style='width:"+width+"px; height:"+((new Double(heigth)).intValue())+"px; position: absolute; top: 0px; left: 0px; border: 0px solid blue;' src='"+imgStr.replace(fileDot+extOrgName, ".svg")+"' oncontextmenu='if (!event.ctrlKey){javascript:viewpopupPng(this);return false;}'/><input type='hidden' id='baseH"+fileName+"' value='"+(new Double(heigth)).intValue()+"'/>");
//				}else if(new File(imgFilePath.replace(fileDot+extOrgName, ".SVG")).exists()) {
//					log.info("USE .SVG FILE");
//					map.put("htmlStr", "<img name='pngImgArea' contId='' tocoId='"+psDto.getTocoId()+"' toKey='"+psDto.getToKey()+"' biz='"+psDto.getBiz()+"'  scwidth='"+width+"' style='width:"+width+"px; height:"+((new Double(heigth)).intValue())+"px; position: absolute; top: 0px; left: 0px; border: 0px solid blue;' src='"+imgStr.replace(fileDot+extOrgName, ".SVG")+"' oncontextmenu='if (!event.ctrlKey){javascript:viewpopupPng(this);return false;}'/><input type='hidden' id='baseH"+fileName+"' value='"+(new Double(heigth)).intValue()+"'/>");
//				}else {
//					map.put("htmlStr", "<img name='pngImgArea' contId='' tocoId='"+psDto.getTocoId()+"' toKey='"+psDto.getToKey()+"' biz='"+psDto.getBiz()+"'  scwidth='"+width+"' style='width:"+width+"px; height:"+((new Double(heigth)).intValue())+"px; position: absolute; top: 0px; left: 0px; border: 0px solid blue;' src='"+imgStr+"' oncontextmenu='if (!event.ctrlKey){javascript:viewpopupPng(this);return false;}'/><input type='hidden' id='baseH"+fileName+"' value='"+(new Double(heigth)).intValue()+"'/>");
//				}
				// Deprecated 이슈로 수정 - 2025.01.01 Chohee.Cha
				if(new File(imgFilePath.replace(fileDot+extOrgName, ".svg")).exists()) {
					log.info("USE .svg FILE");
					map.put("htmlStr", "<img name='pngImgArea' contId='' tocoId='"+psDto.getTocoId()+"' toKey='"+psDto.getToKey()+"' biz='"+psDto.getBiz()+"'  scwidth='"+width+"' style='width:"+width+"px; height:"+(int) Double.parseDouble(String.valueOf(heigth))+"px; position: absolute; top: 0px; left: 0px; border: 0px solid blue;' src='"+imgStr.replace(fileDot+extOrgName, ".svg")+"' oncontextmenu='if (!event.ctrlKey){javascript:viewpopupPng(this);return false;}'/><input type='hidden' id='baseH"+fileName+"' value='"+(int) Double.parseDouble(String.valueOf(heigth))+"'/>");
				}else if(new File(imgFilePath.replace(fileDot+extOrgName, ".SVG")).exists()) {
					log.info("USE .SVG FILE");
					map.put("htmlStr", "<img name='pngImgArea' contId='' tocoId='"+psDto.getTocoId()+"' toKey='"+psDto.getToKey()+"' biz='"+psDto.getBiz()+"'  scwidth='"+width+"' style='width:"+width+"px; height:"+(int) Double.parseDouble(String.valueOf(heigth))+"px; position: absolute; top: 0px; left: 0px; border: 0px solid blue;' src='"+imgStr.replace(fileDot+extOrgName, ".SVG")+"' oncontextmenu='if (!event.ctrlKey){javascript:viewpopupPng(this);return false;}'/><input type='hidden' id='baseH"+fileName+"' value='"+(int) Double.parseDouble(String.valueOf(heigth))+"'/>");
				}else {
					map.put("htmlStr", "<img name='pngImgArea' contId='' tocoId='"+psDto.getTocoId()+"' toKey='"+psDto.getToKey()+"' biz='"+psDto.getBiz()+"'  scwidth='"+width+"' style='width:"+width+"px; height:"+(int) Double.parseDouble(String.valueOf(heigth))+"px; position: absolute; top: 0px; left: 0px; border: 0px solid blue;' src='"+imgStr+"' oncontextmenu='if (!event.ctrlKey){javascript:viewpopupPng(this);return false;}'/><input type='hidden' id='baseH"+fileName+"' value='"+(int) Double.parseDouble(String.valueOf(heigth))+"'/>");
				}
				return map;
			} catch (Exception e) {
				map.put("htmlStr", "<img name='pngImgArea' contId='' tocoId='"+psDto.getTocoId()+"' toKey='"+psDto.getToKey()+"' biz='"+psDto.getBiz()+"'  scwidth='"+width+"' style='width:"+width+"px; position: absolute; top: 0px; left: 0px; border: 0px solid blue;' src='"+imgStr+"' onclick='javascript:viewpopupPng(this);'/>");
				return map;
			}
		}else{
			map.put("htmlStr", "<img name='pngImgArea' contId='' tocoId='"+psDto.getTocoId()+"' toKey='"+psDto.getToKey()+"' biz='"+psDto.getBiz()+"'  scwidth='"+width+"' style='width:"+width+"px; position: absolute; top: 0px; left: 0px; border: 0px solid blue;' src='"+imgStr+"' onclick='javascript:viewpopupPng(this);'/>");
			return map;
		}
	}
	
	/**
	 * 2024.04.29 - main_content 사이즈 영역에 맞게 이미지 사이즈 계산 - jingi.kim
	 * 
	 */
	public HashMap<String, String> calculateGrphSizeWithContentSize(ParserDto psDto, String nodeType) {
		HashMap<String, String> rtmap = new HashMap<String, String>();
		rtmap.put("width", "0");
		rtmap.put("height", "0");
		if ( psDto.getContentWidth() == null || psDto.getContentHeight() == null ) {
			return rtmap;
		}
		if ( "".equals(psDto.getContentWidth()) || "".equals(psDto.getContentHeight()) ) {
			return rtmap;
		}
		if ( "0".equals(psDto.getContentWidth()) || "0".equals(psDto.getContentHeight()) ) {
			return rtmap;
		}
		
		try {
			double maxWidth = Double.parseDouble(psDto.getContentWidth());
			double maxHeight = Double.parseDouble(psDto.getContentHeight());
			String sWidth = maxWidth + "";
			String sHeight = maxHeight + "";
			
			if( psDto.getWebMobileKind().equals("01") ) {
				double yesWidth = maxWidth;
				double yesHeight = maxHeight;
				/*if ( widthCheck.equals(VAL.IMG_VERSION) ) {
					log.info("================== IMG Size Type VERSION");
					yesWidth		= maxWidth * 0.5;
					yesHeight		= maxHeight * 0.5;
				} else if ( widthCheck.equals(VAL.IMG_WIDTHCHECK) ) {
					log.info("================== IMG Size Type WIDTHCHECK");
					yesWidth		= maxWidth * 1.0;
					yesHeight		= maxHeight * 1.0;
				}*/
				
				switch( nodeType.toUpperCase().trim() ) {
				case "PARTINFO" :
				case "IPB" :
				case "IPB2" :
					//IPB 이미지 영역 비율 == 50% , block2 == 45%
					yesWidth		= maxWidth * 0.5;
					yesHeight		= maxHeight * 0.5;
					if ( "BLOCK2".equalsIgnoreCase(ext.getBizCode()) ) {
						yesWidth	= maxWidth * 0.45;
						yesHeight	= maxHeight * 0.45;
					}
					break;
				case "DI" :
				case "DI_DESC" :
				case "LR_DESC" :
				case "LR" :
				case "AP" :
				case "DDS" :
					//FI : 테이블 열의 50% == 컨텐츠 영역의 약 45%
					yesWidth		= maxWidth * 0.45;
					yesHeight		= maxHeight * 0.45;
					break;
				case "GS" :
					//GS
					yesWidth		= maxWidth * 0.85;
					yesHeight		= maxHeight * 0.85;
					break;
				case "JG" :
					//JG
					yesWidth		= maxWidth * 0.65;
					yesHeight		= maxHeight * 0.65;
					break;
				case "WD" :
					//WD
					yesWidth		= maxWidth - 40;
					yesHeight		= maxHeight - 40;
					break;
				case "WORKCARDS" :
				case "WORKCARD" :
					//WC
					yesWidth		= maxWidth * 0.77;
					yesHeight		= maxHeight * 0.77;
					break;
				default :
					yesWidth		= maxWidth;
					yesHeight		= maxHeight;
				}
				
				if ( 0 > yesWidth ) {	yesWidth = maxWidth;	}
				if ( 0 > yesHeight ) {	yesHeight = maxHeight;	}
				
				sWidth = (int)Math.ceil(yesWidth) +"";
				sHeight = (int)Math.ceil(yesHeight) +"";
				
			} else {
				sWidth = "290";
				sHeight = "200";
			}
			
			rtmap.put("width", sWidth);
			rtmap.put("height", sHeight);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("GrphParser.calculateGrphSizeWithContentSize Exception:"+ex.toString());
		}
		
		return rtmap;
	}
	
}
