package com.expis.ietm.parser;

import com.expis.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.w3c.dom.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.File;
import java.util.HashMap;


/**
 * <pre>
 * PNG Image Add Object DIV
 * </pre>
 * @author SANG
 * @since 2021 06
 */
@Slf4j
public class GrphprimData {
	
	//2022 06 29 Park.J.S.  ADD
	@Value("${app.expis.project}")
	private static String PROJECT;
	
	public HashMap<String,Object> gridGrphprimMap(String fileName, int picWidth, HashMap<String, Object> pngMap, String imgeFilePath) {
		
		log.info("fileName : "+fileName+", picWidth : "+picWidth+", imgeFilePath : "+imgeFilePath+", pngMap : "+pngMap);
		imgeFilePath = imgeFilePath.replaceAll("/", "\\\\");
		log.info("Change imgeFilePath : "+imgeFilePath);
		HashMap<String,Object> map = new HashMap<String,Object>();
		StringBuffer sbHtml = new StringBuffer();
		Document doc				= null;
		Element domElement	= null;
		NodeList insNodeList	= null;
		
		try {
			//2023 02 21 jysi EDIT : 확장자 대소문자 구분없이 fileName 생성하도록 수정
			//fileName = imgeFilePath.substring(imgeFilePath.lastIndexOf("\\")+1,imgeFilePath.length()).replaceAll(".png", "");
			fileName = fileName.replace("."+ StringUtil.getExtName(fileName), "");
			String sFilePath = imgeFilePath.substring(0,imgeFilePath.lastIndexOf("\\image\\"))+"\\imagedata\\"+""+fileName+".xml";
			log.info("sFilePath : "+sFilePath);
			if(new File(sFilePath).exists()){
				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
				docBuilderFactory.setValidating(true);
				DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
				docBuilder.setErrorHandler(new DefaultHandler());
				doc = docBuilder.parse(new File(sFilePath));
				domElement = doc.getDocumentElement();
				/**
				 * 2021 11 15 
				 * 이미지 정보 이상으로 임시적으로 실제 이미지만 사용하게 수정
				 * TODO 이미지 사이즈 버그 정상 수정시에 아래 0 처리 수정
				 */
				double width = Integer.parseInt(domElement.getAttribute("width"));
				double height = Integer.parseInt(domElement.getAttribute("height"));
				log.info("xml width : "+width+", height : "+height);
				width = 0;
				height = 0;
				
				if((width == 0 || height == 0)){
					Image img		= new ImageIcon(imgeFilePath).getImage();
					width			= img.getWidth(null);
					height			= img.getHeight(null);
				}
				log.info("png width : "+width+", height : "+height);
				if (domElement != null && domElement.hasChildNodes() == true) {
					insNodeList = domElement.getChildNodes();
					if (insNodeList != null && insNodeList.getLength() > 0) {
						for (int i=0; i<insNodeList.getLength(); i++) {
							Node insNode = insNodeList.item(i);
							if (!insNode.getNodeName().equals("info")) {continue;}
							NamedNodeMap insAttr = insNode.getAttributes();
							String insName	 = getAttributes(insAttr, "instancename");
							String insScript	 = getAttributes(insAttr, "insScript");
							//스크립트 확인 하는 Attribute 명칭 관련 추가
							if("".equals(insScript)) {
								insScript	 = getAttributes(insAttr, "instancescript");
							}
							/*
							 * 가로사이즈 변화 률을 이용해서 전체 이미지 사이즈의 변화 률을 계산한다.
							 */
							double gpsX1 = (Integer.parseInt(getAttributes(insAttr, "x1")) *1f)*(picWidth/width);
							double gpsX2 = (Integer.parseInt(getAttributes(insAttr, "x2")) *1f)*(picWidth/width);
							double gpsY1 = (Integer.parseInt(getAttributes(insAttr, "y1")) *1f)*(picWidth/width);
							double gpsY2 = (Integer.parseInt(getAttributes(insAttr, "y2")) *1f)*(picWidth/width);
							
							String divArea = "<div instancename=\""+insName+"\" name=\"pngObjArea\" onclick=\""+insScript+"\"" +
									"style=\"position: absolute;top: "+gpsY1+"px;left: "+gpsX1+"px; " +
									"width: "+(gpsX2-gpsX1)+"px;height: "+(gpsY2-gpsY1)+"px; cursor:pointer; background-image: url(/"+PROJECT+"/web/image/comm/blank.png); border: 0px solid blue; box-sizing: border-box; \">" +
									"</div>";
							//log.info("ADD : "+divArea);
							sbHtml.append(divArea);
						}
					}else {
						log.info("DomElement.getChildNodes() is Empty");	
					}
				}else {
					log.info("DomElement is wrong");
				}
			}else {
				log.info("File not Exists : "+sFilePath);
			}
		} catch (Exception ex) {
			log.error("Make Png Childe Div Error "+ex.toString());
			ex.printStackTrace();
		} finally {
			try {
				domElement = null;
			} catch (Exception ex) {
				log.error("GrphprimData.() Exception "+ex.toString());
			}
		}
		map.put("htmlCode", sbHtml.toString());
		log.info("map : "+map);
		return map;
	}
	
	public String getAttributes(NamedNodeMap DomAttr, String name) 
	{
		String outstring="";
		if(DomAttr!=null && DomAttr.getNamedItem(name)!=null){
			 outstring =DomAttr.getNamedItem(name).getNodeValue();
		}
		return outstring;
	}
	
}
