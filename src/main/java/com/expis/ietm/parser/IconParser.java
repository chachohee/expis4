package com.expis.ietm.parser;

import com.expis.common.IConstants;
import com.expis.common.ext.ExtConfig;
import com.expis.common.ext.ExternalFileEx;
import com.expis.domparser.*;
import com.expis.util.ImageUtil;
import com.expis.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

/**
 * [공통모듈]아이콘(Icon) Parser Class
 */
@Slf4j
@Component
public class IconParser {

	AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExtConfig.class);
	ExternalFileEx ext = ctx.getBean("extConf",ExternalFileEx.class);
	
	static AnnotationConfigApplicationContext stctx = new AnnotationConfigApplicationContext(ExtConfig.class);
	static ExternalFileEx stext = stctx.getBean("extConf",ExternalFileEx.class);
	

	/**
	 * 파싱 처음에 해당 목차의 아이콘 전체 리스트 추출
	 */
	public NodeList getIconList(Element paElem) {
		
		NodeList iconList = null;
		
		try {
			iconList = XmlDomParser.getNodeListFromXPathAPI(paElem, XALAN.ICON_LIST);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("IconParser.getIconList Exception:"+ex.toString());
		}
		
		return iconList;
		
	}
	
	/**
	 * 2024.06.03 - 현재 <eXPISInfo> 아래의 아이콘 리스트 추출 - jingi.kim
	 */
	public NodeList getExactIconList(Element paElem) {
		NodeList iconList = null;
		
		try {
			iconList = XmlDomParser.getNodeListFromXPathAPI(paElem, "./"+ DTD.SYSTEM+"/"+DTD.ICON);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("IconParser.getExactIconList Exception:"+ex.toString());
		}
		
		return iconList;
	}
	
	/**
	 * 목차에서 아이콘 태그 있을 경우 적합한 아이콘 그래픽을 비교 대체 
	 */
	public static StringBuffer getIconHtml(ParserDto psDto, String selectId) {
		StringBuffer rtSB = new StringBuffer();
		if (psDto == null || StringUtil.checkNull(selectId).equals("")) {
			return rtSB;
		}
		
		try {
			NodeList iconList = psDto.getIconList();
			if (iconList == null || iconList.getLength() <= 0) {
				return rtSB;
			}
			
			String iconId		= "";
			String fileName	= "";
			
			for (int i=0; i<iconList.getLength(); i++) {
				NamedNodeMap curAttr = iconList.item(i).getAttributes();
				iconId	=  XmlDomParser.getAttributes(curAttr, ATTR.ICON_ID);
				
				if (iconId.equals(selectId)) {
					//아이콘 파일명
					fileName	=  XmlDomParser.getAttributes(curAttr, ATTR.ICON_FILE);
					if (fileName.toLowerCase().indexOf(VAL.ICON_FD_LOWER+"/") > -1 || fileName.toLowerCase().indexOf(VAL.ICON_FD_LOWER+"\\") > -1) {
						fileName = fileName.substring((VAL.ICON_FD_UPPER.length()+1), fileName.length());
					}
					
					//그래픽 사이즈 구하기
					double maxWidth		= 0;
					double maxHeight	= 0;
					String sWidth	= "10";
					String sHeight	= "10";
					String param	= "";
					
					String iconPath = psDto.getBizIetmdata() + "/" + IConstants.GRPH_PATH_ICON + "/" + fileName;
					String systemPath = psDto.getBizSyspath() + "/" + IConstants.GRPH_PATH_ICON + "/" + fileName;
					
					// 2023.08.24 - 여러개 타입 지정시 갯수만큼 아이콘 표시되도록 추가 - jingi.kim
					if ( fileName.toUpperCase().contains("TYPE_") && fileName.length() > 10 ) {
						// TYPE_ABD.jpg 패턴
						int idxType = fileName.indexOf("_") +1;
						String strType = fileName.substring(0, idxType);
						int idxExt = fileName.indexOf(".");
						String strExt = fileName.substring(idxExt, fileName.length());
						String strTyList = fileName.substring(idxType, idxExt);
						
						for ( char chtype : strTyList.toCharArray() ) {
							iconPath = psDto.getBizIetmdata() + "/" + IConstants.GRPH_PATH_ICON + "/" + strType + chtype + strExt;
							systemPath = psDto.getBizSyspath() + "/" + IConstants.GRPH_PATH_ICON + "/" + strType + chtype + strExt;
							
							int[] grphSize = ImageUtil.getIconWidthHeight(systemPath, maxWidth, maxHeight);
							if (grphSize[0] > 0 && grphSize[0] > 1) {
								sWidth		= grphSize[0] + "";
								sHeight	= grphSize[1] + "";
							}
							
							// 2023.09.06 - KTA, 아이콘 width > 100px 일 경우 fil-width class 추가
							if ("KTA".equalsIgnoreCase(stext.getBizCode())) {
								if ( Integer.parseInt(sWidth) > 100 ) {
									param = "class=fil-width";
								}
							}
							
							rtSB.append( CSS.getDivIcon(iconId, iconPath, sWidth, sHeight, param) );
						}
					} else {
						int[] grphSize = ImageUtil.getIconWidthHeight(systemPath, maxWidth, maxHeight);
						if (grphSize[0] > 0 && grphSize[0] > 1) {
							sWidth		= grphSize[0] + "";
							sHeight	= grphSize[1] + "";
						}
						
						// 2023.09.06 - KTA, 아이콘 width > 100px 일 경우 fil-width class 추가
						if ("KTA".equalsIgnoreCase(stext.getBizCode())) {
							if ( Integer.parseInt(sWidth) > 100 ) {
								param = "class=fil-width";
							}
						}
						
						rtSB.append( CSS.getDivIcon(iconId, iconPath, sWidth, sHeight, param) );
					}
					
					break;
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("IconParser.getIconHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * KTA, 아이콘 테그를 image tag로 변환
	 */
	public String getTemplateIconTag(ParserDto psDto, String stxt ) {
		if ( stxt == null || "".equals(stxt) ) {	return "";	}
		if ( !stxt.contains("}") ) {	return stxt;	}
		
		String imgPath		= "";
		if(psDto.getWebMobileKind().equals("02")) {
			imgPath = ext.getDF_M_IMG();
		} else {
			imgPath = ext.getDF_IMG();
		}
		
		String rtnText = stxt;
		rtnText = rtnText.replace("}1{", IConstants.TEMPLATE_ICON_TAG_1.replace("%IMGPATH%", imgPath));
		rtnText = rtnText.replace("}2{", IConstants.TEMPLATE_ICON_TAG_2.replace("%IMGPATH%", imgPath));
		rtnText = rtnText.replace("}3{", IConstants.TEMPLATE_ICON_TAG_3.replace("%IMGPATH%", imgPath));
		rtnText = rtnText.replace("}4{", IConstants.TEMPLATE_ICON_TAG_4.replace("%IMGPATH%", imgPath));
		rtnText = rtnText.replace("}5{", IConstants.TEMPLATE_ICON_TAG_5.replace("%IMGPATH%", imgPath));
		
		return rtnText;
	}
}
