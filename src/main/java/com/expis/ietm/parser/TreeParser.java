package com.expis.ietm.parser;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Element;


/**
 * [IETM-TM]TO Parser Class
 */
@Slf4j
public class TreeParser {

	/**
	 * 계통 목록(XML DOM) 데이터를 파싱하여 트리(HTML) 구성
	 * @MethodName	: parseSystemTree
	 * @AuthorDate		: LIM Y.M. / 2014. 2. 5.
	 * @ModificationHistory	: 
	 * @param toElem
	 * @return
	 */
	public StringBuffer parseToIndex(Element toElem) {
		
		StringBuffer rtSB = new StringBuffer();
		
		try {
			rtSB.append("게통 목록");
			//~~~~~~~~~~~~
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ToParser.parseToIndex Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * TO 목록(XML DOM) 데이터를 파싱하여 트리(HTML) 구성
	 * @MethodName	: parseToTree
	 * @AuthorDate		: LIM Y.M. / 2014. 2. 5.
	 * @ModificationHistory	: 
	 * @param domElement
	 * @return
	 */
	public StringBuffer parseToTree(Element domElement) {
		
		StringBuffer rtSB = new StringBuffer();
		
		try {
			rtSB.append("TO 목록");
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ToParser.parseSystemTree Exception:"+ex.toString());
		}

		return rtSB;
	}
	

}
