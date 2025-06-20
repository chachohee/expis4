package com.expis.ietm.collector;

import com.expis.domparser.XmlDomParser;
import com.expis.ietm.dao.XContVersionMapper;
import com.expis.ietm.dto.XContDto;
import com.expis.ietm.parser.ParserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

/**
 * [IETM-TM]교범 변경정보 내용 Collector Class
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VersionCollector {
	
	private final  XContVersionMapper verXcontMapper;

	/**
	 * 교범 내용 중 변경정보 XML 데이터 받아서 DOM으로 반환
	 * @MethodName	: getAlertContDom
	 * @AuthorDate		: LIM Y.M. / 2016. 11. 16.
	 * @ModificationHistory	: 
	 * @return
	 */
	public Element getVersionContDom(XContDto contDto) {
		
		Document doc = null;
		Element rtElem = null;
		StringBuffer contSB = new StringBuffer();
		
		try {
			ArrayList<XContDto> contList = verXcontMapper.selectListDao(contDto);
			if (contList != null && contList.size() > 0) {
				for (XContDto rsDto : contList) {
					contSB.append(rsDto.getXcont());
					log.info("contDto.getTocoId() : "+contDto.getTocoId());
					if(contDto.getTocoId() == null || "".equalsIgnoreCase(contDto.getTocoId())) {
						log.info("rsDto.getTocoId() : "+contDto.getTocoId());
						contDto.setTocoId(rsDto.getTocoId());
					}
				}
			}else {
				log.info("데이타 조회 불가 시에 Step 1 : cont_id 없애서 재조회");
				String tempContId = contDto.getContId(); 
				contDto.setContId(null);
				contList = verXcontMapper.selectListDao(contDto);
				if (contList != null && contList.size() > 0) {
					log.info("contList : "+contList);
					for (XContDto rsDto : contList) {
						contSB.append(rsDto.getXcont());
						if(contDto.getTocoId() == null || "".equalsIgnoreCase(contDto.getTocoId())) {
							contDto.setTocoId(rsDto.getTocoId());
						}
					}
					contDto.setContId(tempContId);
				}else{
					log.info("데이타 조회 불가 시에  Step 2 : toco_id 없애서 재조회");
					contDto.setContId(tempContId);
					contDto.setTocoId(null);
					contList = verXcontMapper.selectListDao(contDto);
					if (contList != null && contList.size() > 0) {
						log.info("contList : "+contList);
						for (XContDto rsDto : contList) {
							contSB.append(rsDto.getXcont());
							if(contDto.getTocoId() == null || "".equalsIgnoreCase(contDto.getTocoId())) {
								contDto.setTocoId(rsDto.getTocoId());
							}
						}
					}
				}
			}
			
			if (contSB.length() > 0) {
				doc = XmlDomParser.createDomTree(contSB.toString(), 1);
				rtElem = doc.getDocumentElement();
			}
			
		}  catch (Exception ex) {
			ex.printStackTrace();
			log.info("VersionCollector.getVersionContDom Exception:"+ex.toString());
		}
		
		return rtElem;
	}
	/**
	 * 2022 03 28 Park.J.S  ADD
	 * <pre>
	 * 해당 교범의 마지박 버전 아이디를 리턴 존재 하지 않을경우 none 리턴함
	 * </pre>
	 * @param pDto
	 * @return
	 */
	public String getLastVersionId(ParserDto pDto) {
		String versionId = verXcontMapper.getLastVersionId(pDto);
		if(versionId == null || versionId.equals("")) {
			versionId = "none";
		}
		return versionId;
	}
}
