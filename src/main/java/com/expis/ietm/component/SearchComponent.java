package com.expis.ietm.component;

import com.expis.common.IConstants;
import com.expis.ietm.dao.SearchContMapper;
import com.expis.ietm.dao.SearchFIMapper;
import com.expis.ietm.dao.SearchIPBMapper;
import com.expis.ietm.dao.SearchWucMapper;
import com.expis.ietm.dto.SearchDto;
import com.expis.ietm.dto.SearchPartinfoDto;
import com.expis.ietm.parser.ContParser;
import com.expis.manage.dao.AdminGlossaryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;


/**
 * [IETM-SC]검색
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SearchComponent {
	

	private final ContParser contParser;
	private final SearchContMapper scMapper;
	private final SearchFIMapper fiMapper;
	private final SearchWucMapper wucMapper;
	private final SearchIPBMapper scIpbMapper;
	private final AdminGlossaryMapper glossaryMapper;

	/**
	 * 검색결과 리스트
	 */
	@SuppressWarnings("unused")
	public List<SearchDto> getSearchList(SearchDto scDto) {
		
		List<SearchDto> rtList = null;
		
//		AdminLogDTO logDto =  new AdminLogDTO();
//		logDto.setCreateUserId(scDto.getCreateUserId());
//		logDto.setCodeType("4301");
//		glossaryMapper.insertLog(logDto);

		try {
			//검색 XML Mapper에서 사용할 IConstants의 상수들 셋팅
			scDto = this.setConstVariable(scDto);

			
			/*
			 * 검색 조건	/ 서브 검색 조건 - Search Condition
			 * 01 : 현재교범	1 : 목차,  2 : 내용,  3 : 그래픽,  4 : 표(테이블),  5 : 경고
			 * 02 : 현재계통
			 * 03 : 부품정보	1 : 부품명,  2 : 부품/재고번호(NSN),  3 : RDN
			 * 04 : 결함코드
			 * 05 : 작업카드
			 * 06 : 작업단위부호
			 * 07 : 용어검색
			 */
			rtList = scMapper.selectListDao(scDto);

//			if (scDto.getSearchCond().equals(IConstants.SC_COND_TO) || scDto.getSearchCond().equals(IConstants.SC_COND_SYS)) {
//				String searchVal = "";
//				for(int i=0; i<scDto.getSearchArray().length; i++) {
//					searchVal = scDto.getSearchArray()[i];
//					if(searchVal.contains("aa") || searchVal.contains("bb") || searchVal.contains("cc") || searchVal.contains("dd") || searchVal.contains("ee")
//							 || searchVal.contains("ff") || searchVal.contains("gg")) {
//						scDto.setXcontTagChk("true");
//					}
//				}
//				//logger.info("xcontTag : " + scDto.getXcontTagChk());
//				rtList = scMapper.selectListDao(scDto);
//
//			} else if (scDto.getSearchCond().equals(IConstants.SC_COND_IPBPART)) {
//				String[] arrConstSubCond		= {IConstants.SC_COND_IPB_PNM, IConstants.SC_COND_IPB_PNO, IConstants.SC_COND_IPB_RDN};
//				scDto.setArrConstSubCond(arrConstSubCond);
//				rtList = scMapper.selectListIPBDao(scDto);
//
//			} else if(scDto.getSearchCond().equals(IConstants.SC_COND_FICODE)) {
//				rtList = fiMapper.selectListDao(scDto);
//			} else if(scDto.getSearchCond().equals(IConstants.SC_COND_WC)) {
//				rtList = scMapper.selectWcDao(scDto);
//			} else if(scDto.getSearchCond().equals(IConstants.SC_COND_WUC)) {
//				rtList = wucMapper.selectWucDao(scDto);
//			}
			
		} catch (Exception ex) {
			ex.printStackTrace();			
			log.info("SearchComponent.getSearchList Exception:"+ex.toString());
		}
		
		return rtList;
	}
	
	
	/**
	 * 검색 XML Mapper에서 사용할 상수 셋팅
	 */
	public SearchDto setConstVariable(SearchDto scDto) {
		
		try {
			String[] arrConstCond		= {
					  IConstants.SC_COND_TO, IConstants.SC_COND_SYS
					, IConstants.SC_COND_IPBPART, IConstants.SC_COND_FICODE
					, IConstants.SC_COND_WC, IConstants.SC_COND_WUC, IConstants.SC_COND_GLOSSARY
				};
			
			String[] arrConstResult	= {
					  IConstants.SC_RT_TOC, IConstants.SC_RT_CONT
					, IConstants.SC_RT_GRPH, IConstants.SC_RT_TABLE, IConstants.SC_RT_ALERT
					, IConstants.SC_RT_IPB, IConstants.SC_RT_FI, IConstants.SC_RT_WC
					, IConstants.SC_RT_WUC, IConstants.SC_RT_GLOSSARY
				};
			
			if(scDto.getSearchCond().equals(IConstants.SC_COND_IPBPART)) {
				String[] arrConstSubCond		= {
						  IConstants.SC_COND_IPB_PNM, IConstants.SC_COND_IPB_PNO
						, IConstants.SC_COND_IPB_RDN
					};
				
				scDto.setArrConstSubCond(arrConstSubCond);
			}
			
			scDto.setArrConstCond(arrConstCond);
			scDto.setArrConstResult(arrConstResult);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("SearchComponent.setConstVariable Exception:"+ex.toString());
		}
		
		return scDto;
	}
	
	public List<SearchPartinfoDto> selectIpb(SearchPartinfoDto dto) {
		List<SearchPartinfoDto> list = new ArrayList<SearchPartinfoDto>();
		list = scIpbMapper.selectIpb(dto);
		
		return list;
	}
	
}
