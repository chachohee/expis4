package com.expis.ietm.parser;

import com.expis.common.ExpisCommonUtile;
import com.expis.common.IConstants;
import com.expis.common.ext.ExtConfig;
import com.expis.common.ext.ExternalFileEx;
import com.expis.domparser.*;
import com.expis.ietm.collector.ContCollector;
import com.expis.ietm.collector.TreeCollector;
import com.expis.ietm.dao.TocoInfoMapper;
import com.expis.ietm.dao.XContGraphicMapper;
import com.expis.ietm.dto.TocoInfoDto;
import com.expis.ietm.dto.XContDto;
import com.expis.manage.dao.SystemOptionMapper;
import com.expis.manage.dto.TreeXContDto;
import com.expis.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

/**
 * [공통모듈]IPB(Illustrated Parts Breakdown) Parser Class
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IPBParser {

	private final SystemOptionMapper systemOptionMapper;
	private final XContGraphicMapper graphMapper;
	private final TocoInfoMapper tocoMapper;
	private final ContParser contParser;
	private final VersionParser verParser;
	private final GrphParser grphParser;
	private final TreeCollector treeCollector;
	private final ContCollector contCollector;
	
	boolean ipb2Flag = false;//야전급 교범여부
	
	AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExtConfig.class);
	ExternalFileEx ext = ctx.getBean("extConf",ExternalFileEx.class);

	
	/**
	 * Private 한 클래스 변수 선언
	 */
	//20개 항목별로 4개 속성(id, name, ename, style)
	private int MAX_COLS			= 25;
	//private int VIEW_COLS			= 20;	//1~20번째까지 화면에 표시
	private int VIEW_COLS			= 11;	//20190220 edit 임시로 수정
	private String[][] IPB_ATTR		= new String[VIEW_COLS][5];
	private int colId				= 0;
	private int colName				= 1;
	//private int colEname			= 2;
	private int colWidth			= 3;
	private int colStyle			= 4;
	
	private int WIDTH_PX			= 10;	//가로폭 기준
	private int TABLE_WIDTH			= 0;	//테이블 가로폭 크기
	
	//순서 변경할시 IPB_ATTR배열과 아래 integer 변수들 위치만 바꿔주면됨
	private int ipbcodeN		= 0;
	private int lcnN			= 0;
	private int grphnumN		= 0;
	private int indexnumN		= 0;
	private int partnumN		= 0;
	private int nsnN			= 0;
	private int cageN			= 0;
	private int levelN			= 0;
	private int contentN		= 0;
	private int unitsperN		= 0;
	private int retrofitN		= 0;
	private int uocN			= 0;
	private int smrN			= 0;
	private int rdnN			= 0;
	private int wucN			= 0;
	private int partsourceN		= 0;
	private int refdataN		= 0;
	private int reftonoN		= 0;
	private int stdN			= 0;
	private int sssnN			= 0;
	private int checkdivN		= 0;		//??
	private int pdrefidN		= 0;		//호기분리시 원천(부모) ID
	private int childpartN		= 0;		//자식노드 갯수
	private int filenameN		= 0;		//이미지 파일
	private int orgIndexnumN	= 0;		//품목번호 원본
	
	private int IPB_TYPE		= 2;		//IPB타입(1:T.M. 2:T.O.) //20211230 수정 PARK J.S.
	
	private String languageType = "ko";     //언어(ko:한글, en:영어) //2023.05.19 jysi ADD 

	/**
	 * IPB테이블 항목 등을 초기에 셋팅
	 * @param psDto 
	 * @MethodName			: setIPBVariable
	 * @AuthorDate			: LIM Y.M.	/ 2014. 10. 17.
	 * @ModificationHistory	: LIM Y.M.	/ 2020.03.09 edit HUSS에서 요청함. ipbcode와 LCN 표시안함
	 * @ModificationHistory	: PARK J.S.	/ 2021.12.30 edit T.O.교범에 맞게 수정(ipbType != 01)
	 * @ModificationHistory	: PARK J.S.	/ 2022.06.16 edit 영어교범 처리 위해 ParserDto psDto 추가
	 */
	public void setIPBVariable(int ipbType, ParserDto psDto) {
		
		try {
			/**
			 * attributes 명, Title 한글명, Title 영문명, width
			 * IPB테이블 표시시 IPB_ATTR 배열 순서로 나옴(순서 변경할시 IPB_ATTR배열과 아래 integer 변수들 위치만 바꿔주면 됨)
			 * <partinfo> 항목들
			 * 사용	: indexnum, unitsper, ipbcode, LCN, graphicnum, level, workunitcode, partsource, refdata, reftono, RDN,
			 * 		  sssn, kai_std, retrofit, usablon(uoc), name(content)
			 * 미사용	: lru, refdes, replvl, pr_graphicnum, sub_graphicnum, note

			 * <partbase> 항목들
			 * 사용	: partnum, smr, nsn, cage, <text>내용(content)
			 * 미사용	: fsc, pmic, cac, qpei, hci, loc, esds, qec, magnetic, mtbf  
			 */
			
			//2023.05.19 jysi EDIT : getIpbColsList에서 사용위해 세팅
			languageType = psDto.getLanguageType();

			if(ipbType == 01) {
				int cnt			= 0;
				ipbcodeN		= cnt++;
				lcnN			= cnt++;
				grphnumN		= cnt++;
				indexnumN		= cnt++;
				smrN			= cnt++;
				nsnN			= cnt++;
				partnumN		= cnt++;
				cageN			= cnt++;
				contentN		= cnt++;
				uocN			= cnt++;
				unitsperN		= cnt++;
				//2022 10 26 jysi ADD : rdnN
				rdnN			= cnt++;
				
				checkdivN		= cnt++;
				childpartN		= cnt++;
				pdrefidN		= cnt++;
				filenameN		= cnt++;
				orgIndexnumN	= cnt++;
				
				//2022 10 18 jysi UPDATE : 영어교범 처리 위해  
				if((psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType()))){
					IPB_ATTR[ipbcodeN]				= new String[] {"ipbcode"		, "IPB-Code"				, ""	, "5"	, "scope='col'"		};
					IPB_ATTR[lcnN]					= new String[] {"LCN"			, "LCN"						, ""	, "5"	, "scope='col'"		};
					IPB_ATTR[grphnumN]				= new String[] {"graphicnum"	, "SYSTEM<br>&<br>FIGURE NO.", ""	, "5"	, "scope='col'"		};
					IPB_ATTR[indexnumN]				= new String[] {"indexnum"		, "INDEX<br>NO."			, ""	, "7"	, "scope='col'"		};
					IPB_ATTR[smrN]					= new String[] {"smr"			, "SMR<br>CODE"				, ""	, "8"	, "scope='col'"		};
					IPB_ATTR[nsnN]					= new String[] {"nsn"			, "NSN"						, ""	, "10"	, "scope='col'"		};
					//2021 11 17 부품번호 => 부분품번호 로 변경 
					IPB_ATTR[partnumN]				= new String[] {"partnum"		, "PART<br>NUMBER"			, ""	, "15"	, "scope='col'"		};
					IPB_ATTR[cageN]					= new String[] {"cage"			, "CAGE"					, ""	, "10"	, "scope='col'"		};
					IPB_ATTR[contentN]				= new String[] {"content"		, "NOMENCLATURE"			, ""	, "30"	, "scope='col' class='ipb_nom'"};
					IPB_ATTR[uocN]					= new String[] {"uoc"			, "USABLE<br>ON<br>CODE"	, ""	, "5"	, "scope='col'"		};
					IPB_ATTR[unitsperN]				= new String[] {"unitsper"		, "UNITS<br>PER<br>ASSY"		, ""	, "5"	, "scope='col'"		};
					//2022 10 26 jysi ADD : RDN링크 기능 작동 위해 히든클래스 컬럼으로 추가
					IPB_ATTR[rdnN]			        = new String[] {"RDN"			, "REF.<br>DESIGNATION<br>NUMBER", ""	, "5"	, "scope='col' class='hidden_col'"};
				}else {
					IPB_ATTR[ipbcodeN]				= new String[] {"ipbcode"		, "IPB-Code"				, ""	, "5"	, "scope='col'"		};
					IPB_ATTR[lcnN]					= new String[] {"LCN"			, "LCN"						, ""	, "5"	, "scope='col'"		};
					IPB_ATTR[grphnumN]				= new String[] {"graphicnum"	, "그림번호"					, ""	, "5"	, "scope='col'"		};
					IPB_ATTR[indexnumN]				= new String[] {"indexnum"		, "품목번호"					, ""	, "7"	, "scope='col'"		};
					IPB_ATTR[smrN]					= new String[] {"smr"			, "근원정비<br>복구성부호"		, ""	, "8"	, "scope='col'"		};
					IPB_ATTR[nsnN]					= new String[] {"nsn"			, "국가재고<br>번호"			, ""	, "10"	, "scope='col'"		};
					//2021 11 17 부품번호 => 부분품번호 로 변경 
					IPB_ATTR[partnumN]				= new String[] {"partnum"		, "부분품번호"					, ""	, "15"	, "scope='col'"		};
					IPB_ATTR[cageN]					= new String[] {"cage"			, "생산자<br>부호"				, ""	, "10"	, "scope='col'"		};
					IPB_ATTR[contentN]				= new String[] {"content"		, "설명<br>사용성부호"			, ""	, "30"	, "scope='col' class='ipb_nom'"};
					IPB_ATTR[uocN]					= new String[] {"uoc"			, "불출단위"					, ""	, "5"	, "scope='col'"		};
					IPB_ATTR[unitsperN]				= new String[] {"unitsper"		, "단위당<br>구성 수량"			, ""	, "5"	, "scope='col'"		};
					//2022 10 26 jysi ADD : RDN링크 기능 작동 위해 히든클래스 컬럼으로 추가
					IPB_ATTR[rdnN]			        = new String[] {"RDN"			, "REF.<br>DESIGNATION<br>NUMBER", ""	, "5"	, "scope='col' class='hidden_col'"};
				}
				
				//2023.09.14 - LSAM IPB 테이블 사이즈 Fix 요청 - jingi.kim
				//2024.09.09 - MUAV 추가 - jingi.kim
				//2024.11.14 - SENSOR 추가 - chohee.cha
				if( "LSAM".equalsIgnoreCase(ext.getBizCode()) || "KICC".equalsIgnoreCase(ext.getBizCode()) || "MUAV".equalsIgnoreCase(ext.getBizCode()) || "SENSOR".equalsIgnoreCase(ext.getBizCode())) {
					IPB_ATTR[ipbcodeN]				= new String[] {"ipbcode"		, "IPB-Code"				, ""	, "1"	, "scope='col'"		};
					IPB_ATTR[lcnN]					= new String[] {"LCN"			, "LCN"						, ""	, "1"	, "scope='col'"		};
					IPB_ATTR[grphnumN]				= new String[] {"graphicnum"	, "그림번호"					, ""	, "3"	, "scope='col'"		};
					IPB_ATTR[indexnumN]				= new String[] {"indexnum"		, "품목번호"					, ""	, "3"	, "scope='col'"		};
					IPB_ATTR[smrN]					= new String[] {"smr"			, "근원정비<br>복구성부호"		, ""	, "6"	, "scope='col'"		};
					IPB_ATTR[nsnN]					= new String[] {"nsn"			, "국가재고<br>번호"			, ""	, "13"	, "scope='col'"		};
					//2021 11 17 부품번호 => 부분품번호 로 변경 
					IPB_ATTR[partnumN]				= new String[] {"partnum"		, "부분품번호"					, ""	, "15"	, "scope='col'"		};
					IPB_ATTR[cageN]					= new String[] {"cage"			, "생산자<br>부호"				, ""	, "6"	, "scope='col'"		};
					IPB_ATTR[contentN]				= new String[] {"content"		, "설명<br>사용성부호"			, ""	, "30"	, "scope='col' class='ipb_nom'"};
					IPB_ATTR[uocN]					= new String[] {"uoc"			, "불출단위"					, ""	, "3"	, "scope='col'"		};
					IPB_ATTR[unitsperN]				= new String[] {"unitsper"		, "단위당<br>구성<br>수량"		, ""	, "4"	, "scope='col'"		};
					//2022 10 26 jysi ADD : RDN링크 기능 작동 위해 히든클래스 컬럼으로 추가
					IPB_ATTR[rdnN]			        = new String[] {"RDN"			, "REF.<br>DESIGNATION<br>NUMBER", ""	, "5"	, "scope='col' class='hidden_col'"};
				}
				
				/*
				int cnt			= 0;
				ipbcodeN		= cnt++;
				lcnN			= cnt++;
				grphnumN		= cnt++;
				indexnumN		= cnt++;
				partnumN		= cnt++;
				nsnN			= cnt++;
				cageN			= cnt++;
				levelN			= cnt++;
				contentN		= cnt++;
				unitsperN		= cnt++;
				retrofitN		= cnt++;
				uocN			= cnt++;
				smrN			= cnt++;
				rdnN			= cnt++;
				wucN			= cnt++;
				partsourceN		= cnt++;
				refdataN		= cnt++;
				reftonoN		= cnt++;
				stdN			= cnt++;
				sssnN			= cnt++;
				checkdivN		= cnt++;
				childpartN		= cnt++;
				pdrefidN		= cnt++;
				filenameN		= cnt++;
				orgIndexnumN	= cnt++;*/
				
				/*IPB_ATTR[ipbcodeN]			= new String[] {"ipbcode"		, "IPB-Code"					, ""	, "5"	, "scope='col'"		};
				IPB_ATTR[lcnN]					= new String[] {"LCN"			, "LCN"							, ""	, "4"	, "scope='col'"		};
				IPB_ATTR[grphnumN]				= new String[] {"graphicnum"	, "계통-그림<br>시트번호"				, ""	, "6"	, "scope='col'"		};
				IPB_ATTR[indexnumN]				= new String[] {"indexnum"		, "품목<br>번호"					, ""	, "3"	, "scope='col'"		};
				IPB_ATTR[partnumN]				= new String[] {"partnum"		, "부품<br>번호"					, ""	, "7"	, "scope='col'"		};
				IPB_ATTR[nsnN]					= new String[] {"nsn"			, "국가재고<br>번호<br>(NSN)"		, ""	, "6"	, "scope='col'"		};
				IPB_ATTR[cageN]					= new String[] {"cage"			, "생산자<br>부호<br>(Cage)"		, ""	, "4"	, "scope='col'"		};
				IPB_ATTR[levelN]				= new String[] {"level"			, "조립체<br>수준"					, ""	, "4"	, "scope='col'"		};
				IPB_ATTR[contentN]				= new String[] {"content"		, "설명<br>(NOMENCLATURE)"		, ""	, "18"	, "scope='col' class='ipb_nom'"};
				IPB_ATTR[unitsperN]				= new String[] {"unitsper"		, "단위당<br>구성 수량<br>(UPA)"		, ""	, "5"	, "scope='col'"		};
				IPB_ATTR[retrofitN]				= new String[] {"retrofit"		, "Retrofit"					, ""	, "3"	, "scope='col'"		};
				IPB_ATTR[uocN]					= new String[] {"uoc"			, "사용성<br>부호<br>(UOC)"			, ""	, "3"	, "scope='col'"		};
				IPB_ATTR[smrN]					= new String[] {"smr"			, "근원정비<br>복구성부호<br>(SMR)"	, ""	, "4"	, "scope='col'"		};
				IPB_ATTR[rdnN]					= new String[] {"RDN"			, "참조지시<br>번호<br>(RDN)"		, ""	, "4"	, "scope='col'"		};
				IPB_ATTR[wucN]					= new String[] {"workunitcode"	, "작업단위<br>부호<br>(WUC)"		, ""	, "4"	, "scope='col'"		};
				IPB_ATTR[partsourceN]			= new String[] {"partsource"	, "부품<br>제원"					, ""	, "3"	, "scope='col'"		};
				IPB_ATTR[refdataN]				= new String[] {"refdata"		, "참고<br>자료"					, ""	, "3"	, "scope='col'"		};
				IPB_ATTR[reftonoN]				= new String[] {"reftono"		, "참고교범<br>번호"				, ""	, "5"	, "scope='col'"		};
				IPB_ATTR[stdN]					= new String[] {"kai_std"		, "KAI<br>규격관리<br>도면"			, ""	, "5"	, "scope='col'"		};
				IPB_ATTR[sssnN]					= new String[] {"sssn"			, "SSSN"						, ""	, "4"	, "scope='col'"		};*/
				
			} else {// T.O. 교범
				/*
				 * 2022 02 23 Park.J.S. : 조립체수준(levelN), Retrofit(retrofitN) 추가
				 */
				int cnt			= 0;
				ipbcodeN		= cnt++;
				lcnN			= cnt++;
				grphnumN		= cnt++;
				indexnumN		= cnt++;
				partnumN		= cnt++;
				nsnN			= cnt++;
				cageN			= cnt++;
				levelN			= cnt++;
				contentN		= cnt++;
				unitsperN		= cnt++;
				retrofitN 		= cnt++;
				uocN			= cnt++;
				smrN			= cnt++;
				rdnN			= cnt++;
				wucN			= cnt++;
				partsourceN		= cnt++;
				refdataN		= cnt++;
				reftonoN		= cnt++;
				stdN			= cnt++;
				sssnN			= cnt++;
				
				
				/*
				 * 2021 12 30
				 * 아래 checkdivN, childpartN, pdrefidN, filenameN, orgIndexnumN 은 테이블에서 사용하는 값이 아님 테이블에 신규 값이 추가될경우
				 * getTableHeadHtml 안 VIEW_COLS 부분 숫자를 바꾸고  상단에 추가해야함
				 * */
				checkdivN		= cnt++;
				childpartN		= cnt++;
				pdrefidN		= cnt++;
				filenameN		= cnt++;
				orgIndexnumN	= cnt++;
				//2022 06 16 Park.J.S. UPDATE : 영어교범 처리 위해  
				if((psDto.getLanguageType() != null && "en".equalsIgnoreCase(psDto.getLanguageType()))){
					if(!"KTA".equalsIgnoreCase(ext.getBizCode())){
						IPB_ATTR[ipbcodeN]		= new String[] {"ipbcode"		, "IPB-Code"					, ""	, "5"	, "scope='col'"		};
						IPB_ATTR[lcnN]			= new String[] {"LCN"			, "LCN"							, ""	, "4"	, "scope='col'"		};
						IPB_ATTR[grphnumN]		= new String[] {"graphicnum"	, "SYSTEM<br>&<br>FIGURE NO."	, ""	, "5"	, "scope='col'"		};
						IPB_ATTR[indexnumN]		= new String[] {"indexnum"		, "INDEX<br>NO."				, ""	, "3"	, "scope='col'"		};
						IPB_ATTR[partnumN]		= new String[] {"partnum"		, "PART<br>NUMBER"				, ""	, "7"	, "scope='col'"		};
						IPB_ATTR[nsnN]			= new String[] {"nsn"			, "국가재고<br>번호<br>(NSN)"		, ""	, "6"	, "scope='col'"		};
						IPB_ATTR[cageN]			= new String[] {"cage"			, "CAGE"						, ""	, "4"	, "scope='col'"		};
						IPB_ATTR[levelN]		= new String[] {"level"			, "LEVEL"						, ""	, "4"	, "scope='col'"		};
						IPB_ATTR[contentN]		= new String[] {"content"		, "NOMENCLATURE"				, ""	, "17"	, "scope='col' class='ipb_nom'"};
						IPB_ATTR[unitsperN]		= new String[] {"unitsper"		, "UNITS<br>PER<br>ASSY"		, ""	, "5"	, "scope='col'"		};
						IPB_ATTR[retrofitN]		= new String[] {"retrofit"		, "Retrofit"					, ""	, "4"	, "scope='col'"		};
						IPB_ATTR[uocN]			= new String[] {"uoc"			, "USABLE<br>ON<br>CODE"		, ""	, "3"	, "scope='col'"		};
						IPB_ATTR[smrN]			= new String[] {"smr"			, "SMR<br>CODE"					, ""	, "4"	, "scope='col'"		};
						IPB_ATTR[rdnN]			= new String[] {"RDN"			, "REF.<br>DESIGNATION<br>NUMBER", ""	, "4"	, "scope='col'"		};
						IPB_ATTR[wucN]			= new String[] {"workunitcode"	, "작업단위<br>부호<br>(WUC)"		, ""	, "4"	, "scope='col'"		};
						IPB_ATTR[partsourceN]	= new String[] {"partsource"	, "PART<br>DATA"				, ""	, "3"	, "scope='col'"		};
						IPB_ATTR[refdataN]		= new String[] {"refdata"		, "REF<br>DATA"					, ""	, "3"	, "scope='col'"		};
						IPB_ATTR[reftonoN]		= new String[] {"reftono"		, "참고교범<br>번호"				, ""	, "5"	, "scope='col'"		};
						IPB_ATTR[stdN]			= new String[] {"kai_std"		, "KAI<br>CONTROL<br>DWG"		, ""	, "5"	, "scope='col'"		};
						IPB_ATTR[sssnN]			= new String[] {"sssn"			, "SSSN"						, ""	, "4"	, "scope='col'"		};
					}else {
						IPB_ATTR[ipbcodeN]		= new String[] {"ipbcode"		, "IPB-Code"					, ""	, "108"	, "scope='col'"		};
						IPB_ATTR[lcnN]			= new String[] {"LCN"			, "LCN"							, ""	, "85"	, "scope='col'"		};
						IPB_ATTR[grphnumN]		= new String[] {"graphicnum"	, "System<br/>Graphic No"		, ""	, "130"	, "scope='col'"		};
						IPB_ATTR[indexnumN]		= new String[] {"indexnum"		, "Items No./<br/>Sheet NO."	, ""	, "65"	, "scope='col'"		};
						IPB_ATTR[partnumN]		= new String[] {"partnum"		, "Parts No."					, ""	, "150"	, "scope='col'"		};
						IPB_ATTR[nsnN]			= new String[] {"nsn"			, "NSN"							, ""	, "130"	, "scope='col'"		};
						IPB_ATTR[cageN]			= new String[] {"cage"			, "CAGE"						, ""	, "85"	, "scope='col'"		};
						IPB_ATTR[levelN]		= new String[] {"level"			, "조립체수준"						, ""	, "85"	, "scope='col'"		};
						IPB_ATTR[contentN]		= new String[] {"content"		, "Parts description<br>1,2,3,4,5,6,7(NOMENCLATURE)"		, ""	, "365"	, "scope='col' class='ipb_nom'"};
						IPB_ATTR[unitsperN]		= new String[] {"unitsper"		, "UPA"							, ""	, "108"	, "scope='col'"		};
						IPB_ATTR[retrofitN]		= new String[] {"retrofit"		, "Retrofit"					, ""	, "108"	, "scope='col'"		};
						IPB_ATTR[uocN]			= new String[] {"uoc"			, "UOC"							, ""	, "65"	, "scope='col'"		};
						IPB_ATTR[smrN]			= new String[] {"smr"			, "SMR"							, ""	, "85"	, "scope='col'"		};
						IPB_ATTR[rdnN]			= new String[] {"RDN"			, "참조지시<br>번호<br/>(RDN)"		, ""	, "85"	, "scope='col'"		};
						IPB_ATTR[wucN]			= new String[] {"workunitcode"	, "작업단위<br>부호<br>(WUC)"		, ""	, "85"	, "scope='col'"		};
						IPB_ATTR[partsourceN]	= new String[] {"partsource"	, "부품<br>제원"					, ""	, "65"	, "scope='col'"		};
						IPB_ATTR[refdataN]		= new String[] {"refdata"		, "참고<br>자료"					, ""	, "65"	, "scope='col'"		};
						IPB_ATTR[reftonoN]		= new String[] {"reftono"		, "참고교범<br>번호"				, ""	, "108"	, "scope='col'"		};
						IPB_ATTR[stdN]			= new String[] {"kai_std"		, "KAI<br>규격관리<br>도면"			, ""	, "108"	, "scope='col'"		};
						IPB_ATTR[sssnN]			= new String[] {"sssn"			, "SSSN"						, ""	, "85"	, "scope='col'"		};
					}
				}else{
					//2022 06 24 Park.J.S. Update : KTA Check ADD
					if(!"KTA".equalsIgnoreCase(ext.getBizCode())){
						IPB_ATTR[ipbcodeN]		= new String[] {"ipbcode"		, "IPB-Code"					, ""	, "5"	, "scope='col'"		};
						IPB_ATTR[lcnN]			= new String[] {"LCN"			, "LCN"							, ""	, "85"	, "scope='col'"		};
						IPB_ATTR[grphnumN]		= new String[] {"graphicnum"	, "계통-<br>그림번호/<br>시트번호"	, ""	, "50"	, "scope='col'"		};
						IPB_ATTR[indexnumN]		= new String[] {"indexnum"		, "품목<br>번호"					, ""	, "30"	, "scope='col'"		};
						IPB_ATTR[partnumN]		= new String[] {"partnum"		, "부품<br>번호"					, ""	, "120"	, "scope='col'"		};
						IPB_ATTR[nsnN]			= new String[] {"nsn"			, "국가재고<br>번호<br>(NSN)"		, ""	, "70"	, "scope='col'"		};
						IPB_ATTR[cageN]			= new String[] {"cage"			, "생산자<br>부호<br>(Cage)"		, ""	, "45"	, "scope='col'"		};
						IPB_ATTR[levelN]		= new String[] {"level"			, "조립체<br>수준"					, ""	, "30"	, "scope='col'"		};
						IPB_ATTR[contentN]		= new String[] {"content"		, "설명<br>(NOMENCLATURE)"		, ""	, "365"	, "scope='col' class='ipb_nom'"};
						IPB_ATTR[unitsperN]		= new String[] {"unitsper"		, "단위당<br>구성 수량<br>(UPA)"		, ""	, "100"	, "scope='col'"		};
						IPB_ATTR[retrofitN]		= new String[] {"retrofit"		, "Retrofit"					, ""	, "85"	, "scope='col'"		};
						IPB_ATTR[uocN]			= new String[] {"uoc"			, "사용성<br>부호<br>(UOC)"			, ""	, "65"	, "scope='col'"		};
						IPB_ATTR[smrN]			= new String[] {"smr"			, "근원정비<br>복구성부호<br>(SMR)"	, ""	, "85"	, "scope='col'"		};
						IPB_ATTR[rdnN]			= new String[] {"RDN"			, "참조지시<br>번호<br>(RDN)"		, ""	, "85"	, "scope='col'"		};
						IPB_ATTR[wucN]			= new String[] {"workunitcode"	, "작업단위<br>부호<br>(WUC)"		, ""	, "85"	, "scope='col'"		};
						IPB_ATTR[partsourceN]	= new String[] {"partsource"	, "부품<br>제원"					, ""	, "65"	, "scope='col'"		};
						IPB_ATTR[refdataN]		= new String[] {"refdata"		, "참고<br>자료"					, ""	, "65"	, "scope='col'"		};
						IPB_ATTR[reftonoN]		= new String[] {"reftono"		, "참고교범<br>번호"				, ""	, "108"	, "scope='col'"		};
						IPB_ATTR[stdN]			= new String[] {"kai_std"		, "KAI<br>규격관리<br>도면"			, ""	, "108"	, "scope='col'"		};
						IPB_ATTR[sssnN]			= new String[] {"sssn"			, "SSSN"						, ""	, "85"	, "scope='col'"		};
					}else{
						IPB_ATTR[ipbcodeN]		= new String[] {"ipbcode"		, "IPB-Code"					, ""	, "108"	, "scope='col'"		};
						IPB_ATTR[lcnN]			= new String[] {"LCN"			, "LCN"							, ""	, "85"	, "scope='col'"		};
						IPB_ATTR[grphnumN]		= new String[] {"graphicnum"	, "그림<br>번호"					, ""	, "130"	, "scope='col'"		};
						IPB_ATTR[indexnumN]		= new String[] {"indexnum"		, "품목/<br>시트번호"				, ""	, "65"	, "scope='col'"		};
						IPB_ATTR[partnumN]		= new String[] {"partnum"		, "부품<br>번호"					, ""	, "150"	, "scope='col'"		};
						IPB_ATTR[nsnN]			= new String[] {"nsn"			, "국가재고<br>번호"				, ""	, "130"	, "scope='col'"		};
						IPB_ATTR[cageN]			= new String[] {"cage"			, "생산자<br>부호"					, ""	, "85"	, "scope='col'"		};
						IPB_ATTR[levelN]		= new String[] {"level"			, "조립체수준"						, ""	, "85"	, "scope='col'"		};
						IPB_ATTR[contentN]		= new String[] {"content"		, "설명<br>1,2,3,4,5,6,7(NOMENCLATURE)"		, ""	, "365"	, "scope='col' class='ipb_nom'"};
						IPB_ATTR[unitsperN]		= new String[] {"unitsper"		, "단위당<br>구성 수량"				, ""	, "108"	, "scope='col'"		};
						IPB_ATTR[retrofitN]		= new String[] {"retrofit"		, "Retrofit"					, ""	, "108"	, "scope='col'"		};
						IPB_ATTR[uocN]			= new String[] {"uoc"			, "사용성<br>부호"					, ""	, "65"	, "scope='col'"		};
						IPB_ATTR[smrN]			= new String[] {"smr"			, "근원정비<br>복구성부호"			, ""	, "85"	, "scope='col'"		};
						IPB_ATTR[rdnN]			= new String[] {"RDN"			, "참조지시<br>번호<br>(RDN)"		, ""	, "85"	, "scope='col'"		};
						IPB_ATTR[wucN]			= new String[] {"workunitcode"	, "작업단위<br>부호<br>(WUC)"		, ""	, "85"	, "scope='col'"		};
						IPB_ATTR[partsourceN]	= new String[] {"partsource"	, "부품<br>제원"					, ""	, "65"	, "scope='col'"		};
						IPB_ATTR[refdataN]		= new String[] {"refdata"		, "참고<br>자료"					, ""	, "65"	, "scope='col'"		};
						IPB_ATTR[reftonoN]		= new String[] {"reftono"		, "참고교범<br>번호"				, ""	, "108"	, "scope='col'"		};
						IPB_ATTR[stdN]			= new String[] {"kai_std"		, "KAI<br>규격관리<br>도면"			, ""	, "108"	, "scope='col'"		};
						IPB_ATTR[sssnN]			= new String[] {"sssn"			, "SSSN"						, ""	, "85"	, "scope='col'"		};
					}
					
					//2024.04.19 - KBOB RDN 숨김처리 - jingi.kim
					if( "KBOB".equalsIgnoreCase(ext.getBizCode()) ) {
						IPB_ATTR[rdnN]			= new String[] {"RDN"			, "참조지시<br>번호<br>(RDN)"		, ""	, "85"	, "scope='col' class='hidden_col'"		};
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("IPBParser.setIPBVariable Exception:"+ex.toString());
		}
	}
	
	
	/**
	 * 
	 * @MethodName	: getIPBHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 10. 7.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getIPBHtml(ParserDto psDto, Node paNode) {
		try {
			log.info("getIPBHtml paNode : "+nodeToString(paNode));
			//2022 06 08 Park.J.S. ADD : 여전급 교범 처리 추가(테이블 화면 표시 내용이 달라짐)
			//////////////////////////////////////////////////////////////////////////////////////////////
			//Step 1. GET ToKey and IPB ID
			//////////////////////////////////////////////////////////////////////////////////////////////
			String ipbID = XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.ID);
			log.info("psDto.getToKey() : "+psDto.getToKey()+", ipbID : "+ipbID+", psDto.getTocoId() : "+psDto.getTocoId());
			//////////////////////////////////////////////////////////////////////////////////////////////
			//Step 2. Get Menu XML From DB (USE psDto.getToKey())
			//////////////////////////////////////////////////////////////////////////////////////////////
			TreeXContDto treeDto = new TreeXContDto();
			treeDto.setRefToKey(psDto.getToKey());
			Element menuXml = (Element) treeCollector.getToTreeDom(treeDto);
			Node menuNode = XmlDomParser.getNodeFromXPathAPI(menuXml, "//system[@id='" + ipbID + "']");
			log.info("menuNode : "+nodeToString(menuNode));
			//////////////////////////////////////////////////////////////////////////////////////////////
			//Step 3. Check IPB TYPE(USE ipbID)
			//////////////////////////////////////////////////////////////////////////////////////////////
			if(menuNode != null) {//2022 09 22 ADD : menuNode == null 이면 psDto.getTocoId() 이용하게 수정
				String ipbType = XmlDomParser.getAttributes(menuNode.getAttributes(), ATTR.TYPE);
				log.info("ipbType : "+ipbType);
				if("ipb2".equalsIgnoreCase(ipbType)) {//야전급 교범
					ipb2Flag = true;
				}else {//부대급 교범
					ipb2Flag = false;
				}
			}else {
				treeDto.setRefToKey(psDto.getToKey());
				menuXml = (Element) treeCollector.getToTreeDom(treeDto);
				menuNode = XmlDomParser.getNodeFromXPathAPI(menuXml, "//system[@id='" + psDto.getTocoId() + "']");
				log.info("menuNode : "+nodeToString(menuNode));
				String ipbType = XmlDomParser.getAttributes(menuNode.getAttributes(), ATTR.TYPE);
				log.info("ipbType : "+ipbType);
				if("ipb2".equalsIgnoreCase(ipbType)) {//야전급 교범
					ipb2Flag = true;
				}else {//부대급 교범
					ipb2Flag = false;
				}
			}
		} catch (TransformerException e) {
			log.error("xml check Error : "+e.getMessage(),e);
			ipb2Flag = false;
		}
		
		StringBuffer rtSB = new StringBuffer();
		
		try {
			if (paNode == null || !paNode.getNodeName().equals(DTD.IPB_PARTINFO)) {
				log.info("getIPBHtml return paNode.getNodeName() : "+paNode);
				return rtSB;
			}
			
			rtSB.append(CSS.DIV_IPB);
			rtSB.append(CSS.DIV_IPB_CONT);
			rtSB.append( this.getIPBGrphHtml(psDto, paNode, false) );
			rtSB.append( this.getIPBTableHtml(psDto, paNode) );
			rtSB.append(CSS.DIV_END);
			rtSB.append( this.getIPBControlHtml(psDto, paNode) );
			rtSB.append(CSS.DIV_END);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("IPBParser.getIPBHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	/**
	 * <pre>
	 * 2021 06 jsaprk
	 * IPB의 경우 테이블이 레퍼런스 처리되는경우가 있어서 getIPBTableHtml2 사용하는 부분 추가
	 * </pre>
	 * 
	 */
	public StringBuffer getIPBRefHtml(ParserDto psDto, Node nCurNode, String refTocoId) {
		try {
			log.info("getIPBRefHtml nCurNode : "+nodeToString(nCurNode));
		} catch (TransformerException e) {
			log.error("xml check Error : "+e.getMessage(),e);
		}
		
		StringBuffer rtSB = new StringBuffer();
		XContDto contDto = new XContDto();
		contDto.setOutputMode("02");
		contDto.setToKey(psDto.getToKey());
		contDto.setTocoId(refTocoId);
		
		Node paNode = contCollector.getAllContDom(contDto);
		try {
			log.info("getIPBRefHtml paNode : "+nodeToString(paNode));
		} catch (TransformerException e) {
			log.error("xml check Error : "+e.getMessage(),e);
		}
//		Element contElem = contCollector.getAllContDom(contDto);
//		Node paNode = (Node)contElem;
		rtSB.append(CSS.DIV_IPB);
		rtSB.append(CSS.DIV_IPB_CONT);
		rtSB.append( this.getIPBGrphHtml(psDto, paNode, true) );
		log.info("------------------------------------------------------------ Pass Image ------------------------------------------------------------");
		rtSB.append( this.getIPBTableHtml2(psDto, paNode, nCurNode) );
		//log.info("getIPBRefHtml getIPBTableHtml : "+(this.getIPBTableHtml2(psDto, paNode, nCurNode) );
		rtSB.append(CSS.DIV_END);
		rtSB.append( this.getIPBControlHtml(psDto, paNode) );
		rtSB.append(CSS.DIV_END);
		
		return rtSB;
	}
	
	/**
	 * IPB 교범의 도해 이미지 시현 기능
	 * @param refFlag 
	 * @MethodName	: getIPBGrphHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 10. 10.
	 * @ModificationHistory	: LIM Y.M. / 2017. 1. 14
	 * @ModificationHistory	: getGrphHtmlIPBPNG 추가 2021 06 jsapark
	 * @param psDto, paNode, refFlag
	 * @return
	 */
	public StringBuffer getIPBGrphHtml(ParserDto psDto, Node paNode, boolean refFlag) {
		log.info(this.getClass().getName() + " : getIPBGrphHtml");
		String ipbImgPage = "";
		StringBuffer rtSB = new StringBuffer();
		try {
			Node curNode			= null;
			String nodeName		= "";
			StringBuffer nodeSB		= new StringBuffer();
			StringBuffer pagingSB	= new StringBuffer();
			
			NodeList curList = XmlDomParser.getNodeListFromXPathAPI(paNode, XALAN.IPB_GRPH);
			
			if (curList.getLength() != 0) {
				for(int i=0; i<curList.getLength(); i++) {
					curNode = curList.item(i);
					nodeName = curNode.getNodeName();
					
					if(curNode.getNodeType() == Node.ELEMENT_NODE && nodeName.equals(DTD.GRPH)) {
						
						String grphNo		= (i + 1) + "";
						String grphPath	= grphParser.getGrphPath(curNode, psDto.getBizIetmdata());
						log.info("grphPath : " + grphPath+", curList.getLength() :"+curList.getLength());
						//2021 09 01 refFlag 경우 분기 처리
						//2022 12 01 Park.J.S. Update : 기존 curList 넘기는 거에서 curNode 넘기는 걸로 수정 curList넘길경우 이미지 문제 발생
						if (i == 0) {
							nodeSB.append( grphParser.getGrphHtmlIPBPNG(psDto, curNode, true));
						}else if(!refFlag){
							log.info("Not ref IPB : "+refFlag);
							nodeSB.append( grphParser.getGrphHtmlIPBPNG(psDto, curNode, false));
						}else if(refFlag){//2023 01 06 Park.J.S.ADD : 2022 12 01 Park.J.S. Update건으로 예상되는 문제로 인해 참조일경우 화면에 안나나타는 문제 발생 -> 수정코드 추가
							log.info("ref IPB : "+refFlag);
							nodeSB.append( grphParser.getGrphHtmlIPBPNG(psDto, curNode, false));
						}
						log.info("ipbExt["+i+"] : " + nodeSB.toString());

						ipbImgPage = CSS.getIPBGrphChange(grphNo, grphPath);
						//모바일 경우 TODO
						if(psDto.getWebMobileKind().equals("02")) {
							//ipbImgPage = ipbImgPage.replace(".swf", ".png");
						}
						pagingSB.append(ipbImgPage);
						pagingSB.append(CSS.A_END);
					}
				}
			}
			
			
			pagingSB.insert(0,  CSS.DIV_IPB_PAGINGLIST);
			pagingSB.append(CSS.DIV_END);
			
			rtSB.append(CSS.DIV_IPB_GRPH);
			rtSB.append(CSS.DIV_IPB_GRPH_SHOW);
			rtSB.append(nodeSB);
			rtSB.append(pagingSB);
			rtSB.append(CSS.DIV_END);
			rtSB.append(CSS.DIV_END);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("IPBParser.getIPBGrphHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}

	/**
	 * 
	 * @MethodName	: getIPBTableHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 10. 10.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getIPBTableHtml(ParserDto psDto, Node paNode) {
		
		StringBuffer rtSB = new StringBuffer();
		
		try {
			//Table의 헤더 구성시에 테이블 전체 가로 크기 계산함
			TABLE_WIDTH	= 0;
			//2022 08 01 Park.J.S. Update : 테이블 고정 처리 위해 수정 테이블 width 수정해야하나 일정상 테이블 그대로 사용후 가림
			StringBuffer headHtml 			= this.getTableHeadHtml(psDto, paNode);
			StringBuffer bufferBodyHtml 	= this.getTableBodyHtml(psDto, paNode);
//			rtSB.append(CSS.DIV_IPB_TABLE);
			//2023.07.10 jysi EDIT : LSAM분기점에 NLS추가
			//2024.09.09 - MUAV 추가 - jingi.kim
			//2024.11.14 - SENSOR 추가 - chohee.cha
			boolean isCanon = false;
			if ( ("LSAM".equalsIgnoreCase(ext.getBizCode()) || "NLS".equalsIgnoreCase(ext.getBizCode()) || "KICC".equalsIgnoreCase(ext.getBizCode()) || "MUAV".equalsIgnoreCase(ext.getBizCode()) || "SENSOR".equalsIgnoreCase(ext.getBizCode())) ) {
				isCanon = true;
			}
			if( isCanon ){
				rtSB.append(CSS.DIV_IPB_TABLE);
				rtSB.append(CSS.DIV_IPB_TABLE_P);
				rtSB.append( StringUtil.replace(CSS.TB_TABLE_IPB, CSS.REGEX,  TABLE_WIDTH+"") );
				rtSB.append( headHtml );
				rtSB.append( bufferBodyHtml );
				rtSB.append(CSS.TB_TABLEEND);
				rtSB.append(CSS.DIV_IPB_TABLE_P_END);
				rtSB.append(CSS.DIV_END);
			}else {
				
				rtSB.append(CSS.DIV_IPB_TABLE_SCROLL);
				rtSB.append("<div style=\"height: 100%; overflow: auto;\">");
				rtSB.append( StringUtil.replace(CSS.TB_TABLE_IPB, CSS.REGEX,  TABLE_WIDTH+"") );
				rtSB.append( headHtml );
				rtSB.append( bufferBodyHtml );
				rtSB.append(CSS.TB_TABLEEND);
				rtSB.append(CSS.DIV_IPB_TABLE_P_END);
				rtSB.append(CSS.DIV_END);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("IPBParser.getIPBTableHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	/**
	 * <pre>
	 * 일부 IPB 정보중 상위 객체 링크만 나타나는 문제가 발견되서 하위 목록도 나타나도록 수정함
	 * </pre>
	 * @param nCurNode 
	 * @MethodName			: getIPBTableHtml2
	 * @AuthorDate			: jspark 
	 * @ModificationHistory	: 2022 01 18 / Park J.S.
	 * @param psDto, paNode, nCurNode
	 * @return
	 */
	public StringBuffer getIPBTableHtml2(ParserDto psDto, Node paNode, Node nCurNode) {
		try {
			log.info("paNode : "+nodeToString(paNode));
			log.info("nCurNode : "+nodeToString(nCurNode));
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		StringBuffer rtSB = new StringBuffer();
		
		try {
			//Table의 헤더 구성시에 테이블 전체 가로 크기 계산함
			TABLE_WIDTH	= 0;
			StringBuffer headHtml = this.getTableHeadHtml(psDto, paNode);
			//2022 08 01 Park.J.S. Update : 테이블 고정 처리 위해 수정 테이블 width 수정해야하나 일정상 테이블 그대로 사용후 가림
			//2023.07.10 jysi EDIT : LSAM분기점에 NLS추가
			//2024.09.09 - MUAV 추가 - jingi.kim
			//2024.11.14 - SENSOR 추가 - chohee.cha
			boolean isCanon = false;
			if ( ("LSAM".equalsIgnoreCase(ext.getBizCode()) || "NLS".equalsIgnoreCase(ext.getBizCode()) || "KICC".equalsIgnoreCase(ext.getBizCode()) || "MUAV".equalsIgnoreCase(ext.getBizCode()) || "SENSOR".equalsIgnoreCase(ext.getBizCode()) ) ) {
				isCanon = true;
			}
			if( isCanon ){
				rtSB.append(CSS.DIV_IPB_TABLE);
				rtSB.append(CSS.DIV_IPB_TABLE_P);
				rtSB.append( StringUtil.replace(CSS.TB_TABLE_IPB, CSS.REGEX,  TABLE_WIDTH+"") );
				rtSB.append( headHtml );
				/*
				 * 2021 07 jspark
				 * 실제 xml 안에 하위객체가 존재 하지 않을경우 참조 객체와 동일한걸로 판단해서 참조객체를 다 찍는다 
				 * 2022 07 27 Park.J.S. ADD : && !nCurNode.getNodeName().equals(DTD.IPB_PARTINFO)
				 */
				NodeList childList = XmlDomParser.getNodeListFromXPathAPI(nCurNode, DTD.IPB_PARTINFO);
				if((childList == null || childList.getLength() == 0) && !nCurNode.getNodeName().equals(DTD.IPB_PARTINFO)) {
					log.info("실제 xml 안에 하위객체가 존재 하지 않을경우 참조 객체와 동일한걸로 판단해서 참조객체를 다 찍는다 ");
					rtSB.append( this.getTableBodyHtml2(psDto, paNode) );
				}else {
					rtSB.append( this.getTableBodyHtml(psDto, paNode) );
					rtSB.append( this.getTableBodyHtml2(psDto, nCurNode) );
				}
				rtSB.append(CSS.TB_TABLEEND);
				rtSB.append(CSS.DIV_IPB_TABLE_P_END);
				rtSB.append(CSS.DIV_END);
			}else {
				StringBuffer bodyHtml = new StringBuffer();;
				/*
				 * 2021 07 jspark
				 * 실제 xml 안에 하위객체가 존재 하지 않을경우 참조 객체와 동일한걸로 판단해서 참조객체를 다 찍는다 
				 * 2022 07 27 Park.J.S. ADD : && !nCurNode.getNodeName().equals(DTD.IPB_PARTINFO)
				 */
				NodeList childList = XmlDomParser.getNodeListFromXPathAPI(nCurNode, DTD.IPB_PARTINFO);
				if((childList == null || childList.getLength() == 0) && !nCurNode.getNodeName().equals(DTD.IPB_PARTINFO)) {
					log.info("실제 xml 안에 하위객체가 존재 하지 않을경우 참조 객체와 동일한걸로 판단해서 참조객체를 다 찍는다 ");
					bodyHtml = this.getTableBodyHtml2(psDto, paNode);
				}else {
					/*
					 * 2022 01 18 Park J.S.
					 * T50, BLOCK2  호기 분리 관련 해서 추가
					 */
					log.info("ext.getBizCode() : "+ext.getBizCode());
					if("T50".equalsIgnoreCase(ext.getBizCode()) || "BLOCK2".equalsIgnoreCase(ext.getBizCode()) || "FA50".equalsIgnoreCase(ext.getBizCode())) {
						bodyHtml.append(this.getTableBodyHtml(psDto, nCurNode));
						bodyHtml.append(this.getTableBodyHtml2(psDto, iPBNodeMerge(nCurNode,paNode)) );
					}else {
						bodyHtml.append( this.getTableBodyHtml(psDto, paNode));
						bodyHtml.append( this.getTableBodyHtml2(psDto, nCurNode) );
					}
				}
				rtSB.append(CSS.DIV_IPB_TABLE_SCROLL);
				rtSB.append("<div style=\"height: 100%; overflow: auto;\">");
				rtSB.append( StringUtil.replace(CSS.TB_TABLE_IPB, CSS.REGEX,  TABLE_WIDTH+"") );
				rtSB.append( headHtml );
				rtSB.append(bodyHtml);
				rtSB.append(CSS.TB_TABLEEND);
				rtSB.append(CSS.DIV_IPB_TABLE_P_END);
				rtSB.append(CSS.DIV_END);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("IPBParser.getIPBTableHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 
	 * @MethodName	: getIPBControlHtml
	 * @AuthorDate		: LIM Y.M. / 2014. 10. 10.
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getIPBControlHtml(ParserDto psDto, Node paNode) {
		
		StringBuffer rtSB = new StringBuffer();
		//2022 07 27 System 넘어 올경우 pr_graphicnum 처리 안되는 경우 있어서 수정
		try {
			if(paNode.getNodeName().equals("system")) {
				paNode = paNode.getFirstChild(); 
			}
			log.info("getIPBControlHtml : "+nodeToString(paNode));
		} catch (TransformerException e1) {e1.printStackTrace();}
		try {
			/*if (paNode == null || !paNode.getNodeName().equals(DTD.IPB_PARTINFO)) {
				return rtSB;
			}*/
			
			//상위 부품 이동 링크 기능
			String pinfoId				= "";
			String ipbcode	= XmlDomParser.getAttributes(paNode.getAttributes(), ATTR.IPB_IPBCODE);
			//2022 07 25 Park.J.S. ADD
			String prGraphicnum = "";
			try {
				if(XmlDomParser.getAttributes(paNode.getAttributes(), "pr_graphicnum") != null && !"".equals(XmlDomParser.getAttributes(paNode.getAttributes(), "pr_graphicnum"))) {
					//2022 07 27 Park.J.S. Update : getFirstChild 가 grphprim인 경우 존재
					for(int i=0;i<paNode.getChildNodes().getLength();i++){
						if(paNode.getChildNodes().item(i).getNodeName().equals("partinfo")) {
							//2024.02.29 - 첫번째  pr_graphicnum을 가져오도록 보완 - jingi.kim
							String piPrGrphicnum = XmlDomParser.getAttributes(paNode.getChildNodes().item(i).getAttributes(), "pr_graphicnum");
							if ( piPrGrphicnum != null && !"".equals(piPrGrphicnum) ) {
								prGraphicnum	= piPrGrphicnum;
								break;
							}
						}else {
							continue;
						}
					}
				}
				log.info("prGraphicnum : "+prGraphicnum);
			}catch (Exception e) {}
			if (!ipbcode.equals("") && ipbcode.length() > 2) {
				TocoInfoDto tocoDto = tocoMapper.selectParentTocoDao(psDto.getToKey(), psDto.getTocoId());
				if (tocoDto != null) {
					pinfoId			= tocoDto.getTocoId();
				}
			}else {
				log.info("ipbcode Else : "+ipbcode);
			}
			
			//2024.06.11 - 저작 2.x로 생성된 경우 == pid 속성이 있는 경우 - jingi.kim
			String prType = "prname";
			if ( "".equals(prGraphicnum) ) {
				if(XmlDomParser.getAttributes(paNode.getAttributes(), "pid") != null && !"".equals(XmlDomParser.getAttributes(paNode.getAttributes(), "pid"))) {
					prType = "prid";
					
					TocoInfoDto tocoDto = tocoMapper.selectParentTocoDao(psDto.getToKey(), psDto.getTocoId());
					if (tocoDto != null) {
						prGraphicnum = tocoDto.getTocoId();
					}
				}
			}
			
			String controlHtml = CSS.getIPBControl(psDto.getToKey(), pinfoId, psDto, prGraphicnum, prType, ext.getBizCode());
			
			rtSB.append(CSS.DIV_IPB_CONTROL);
			rtSB.append(controlHtml);
			rtSB.append(CSS.DIV_END);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("IPBParser.getIPBControlHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 
	 * @MethodName			: getTableHeadHtml
	 * @AuthorDate			: LIM Y.M. / 2014. 10. 10.
	 * @ModificationHistory	: Park JS / 2021 12 30
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getTableHeadHtml(ParserDto psDto, Node paNode) {
		int ipbType;
		
		StringBuffer rtSB = new StringBuffer();
		
		ipbType = systemOptionMapper.ipbTypeCheck();
		if(ipbType == 01) {
			//VIEW_COLS = 20;
			//VIEW_COLS = 11;	//20190220 edit
			VIEW_COLS = 12;	//20221026 edit
			IPB_ATTR	= new String[VIEW_COLS][5];
			setIPBVariable(ipbType, psDto);
		} else {
			VIEW_COLS = 20; //2021 12 30
			IPB_ATTR	= new String[VIEW_COLS][5];
			setIPBVariable(ipbType, psDto);
		}
		log.info("ipbType : "+ipbType+", VIEW_COLS : "+VIEW_COLS);
		
		try {
			StringBuffer nodeSB		= new StringBuffer();
			int tableWidth	= 0;
			log.info("psDto.getArrIPBCols() : "+psDto.getArrIPBCols());
			
			//2024.09.09 - MUAV 추가 - jingi.kim
			//2024.11.14 - SENSOR 추가 - chohee.cha
			boolean isCanon = false;
			if ("LSAM".equalsIgnoreCase(ext.getBizCode()) || "NLS".equalsIgnoreCase(ext.getBizCode()) || "KICC".equalsIgnoreCase(ext.getBizCode()) || "MUAV".equalsIgnoreCase(ext.getBizCode()) || "SENSOR".equalsIgnoreCase(ext.getBizCode())) {
				isCanon = true;
			}
			
			//IPB 항목 종류 선택 여부에 따라 처리
			if (psDto.getArrIPBCols() == null || psDto.getArrIPBCols().equals("")) {
				for (int i=0; i<IPB_ATTR.length; i++) {
					//20200309 edit IPBCODE, LCN은 default로만 표시 안하고, 선택된 것은 해군일때만 무조건 표시 안함
					if (IPB_TYPE == IConstants.MILIT_AIR || IPB_TYPE == IConstants.MILIT_NAVY ) {
						if (i == 0 || i == 1) {
							continue;
						}
					}
					String thWidth = IPB_ATTR[i][colWidth] + "%";

					//2022 07 05 Park.J.S. UPDATE : % 사용시 사천 피시에서 정상적으로 표시 안된다고 해서 수정
					//2023.07.10 jysi EDIT : LSAM분기점에 NLS추가
					if(psDto.getBrowserWidth() == null || isCanon){
						thWidth = IPB_ATTR[i][colWidth] + "%";
						
						if(psDto.getBrowserWidth() == null && (i == 2 || i == 6)) {
							int tempWidth = Integer.parseInt(IPB_ATTR[i][colWidth]) + 10;
							thWidth = String.valueOf(tempWidth) + "%";
							
						}
					}else {
						thWidth = IPB_ATTR[i][colWidth] + "px";
					}
					
					String thStyle = IPB_ATTR[i][colStyle] + " width='" + thWidth + "'";
					//2021 12 30 Add Park JS
					if (IPB_ATTR[i][colWidth] == null) {
						log.info("IPB_ATTR["+i+"][colWidth] is null : "+IPB_ATTR[i][colName]);
						continue;
					}else {
						log.info("IPB_ATTR["+i+"][colWidth] is not null : "+IPB_ATTR[i][colName]);	
					}
					//2022 06 08 Park.J.S. Add 야전급 여부 확인 추가
					//2022 06 15 Park.J.S. Update : 언어 구분 위해 수정
					if(!ipbTypeUseCheck(i, psDto.getLanguageType(), ipb2Flag)) {
						log.info("Not use ipb2 : "+IPB_ATTR[i][colName]);
						continue;
					}
					int tmpWidth = Integer.parseInt(IPB_ATTR[i][colWidth]) * WIDTH_PX;
					tableWidth += tmpWidth;
					
					nodeSB.append( CSS.getTableTh(thStyle) );
					nodeSB.append(IPB_ATTR[i][colName]);
					nodeSB.append(CSS.TB_THEND);
				}
				
			} else {
				String[] arrCols = StringUtil.splitStr(psDto.getArrIPBCols(), CHAR.IPB_REGEX_COLS);
				Arrays.sort(arrCols);
				
				for (int i=0; i<IPB_ATTR.length; i++) {
					//20200309 add
					if (IPB_TYPE == IConstants.MILIT_NAVY) {
						if (i == 0 || i == 1) {
							continue;
						}
					}
					
					int idx = Arrays.binarySearch(arrCols, IPB_ATTR[i][colId]);
					if (idx > -1) {
						String thWidth = IPB_ATTR[i][colWidth] + "%";
						//2022 07 05 Park.J.S. UPDATE : % 사용시 사천 피시에서 정상적으로 표시 안된다고 해서 수정
						if(isCanon){
							thWidth = IPB_ATTR[i][colWidth] + "%";
						}else {
							thWidth = IPB_ATTR[i][colWidth] + "px";
						}
						String thStyle = IPB_ATTR[i][colStyle] + " width='" + thWidth + "'";
						
						int tmpWidth = Integer.parseInt(IPB_ATTR[i][colWidth]) * WIDTH_PX;
						tableWidth += tmpWidth;
						
						nodeSB.append( CSS.getTableTh(thStyle) );
						nodeSB.append(IPB_ATTR[i][colName]);
						nodeSB.append(CSS.TB_THEND);
					}
				}
			}
			
			TABLE_WIDTH = tableWidth;
			//2022 07 05 Park.J.S. UPDATE : % 사용시 사천 피시에서 정상적으로 표시 안된다고 해서 수정
			//2024.04.29 - [KICC] 테이블 정보 열이 맞지않아 추가  - JSH
			if(isCanon){
				//2022 05 19 Park.J.S. : 버전 추가
				//2023.09.14 - 첫 버전열 제거 요청 - jingi.kim
				nodeSB.insert(0, "<th width=\"1%;\" class=\"hidden_col\"></th>");
			}else {
				//2024.05.10 - 야전급 ipb 교범(TM 형식)일 경우 테이블의 열 크기가 틀려져서 재조정- jingi.kim
				nodeSB.insert(0, "<th width=\"10px;\"></th>");
			}
			rtSB.append(CSS.TB_THEAD);
			rtSB.append(CSS.TB_TR);
			rtSB.append(nodeSB);
			rtSB.append(CSS.TB_TREND);
			rtSB.append(CSS.TB_THEAD_END);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("IPBParser.getTableHeadHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	static boolean CHK_EVENT = true;	//남대식or김권식 작성
	static String PREV_FILENUM = "";	//남대식or김권식 작성
	/**
	 * 
	 * @MethodName			: getTableBodyHtml
	 * @AuthorDate			: LIM Y.M. / 2014. 10. 10.
	 * @ModificationHistory	: jspark / 2021 07 12
	 * @ModificationHistory	: jspark / 2021 12 31
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getTableBodyHtml(ParserDto psDto, Node paNode) {
		try {
			log.info("getTableBodyHtml paNode : "+nodeToString(paNode));
		} catch (TransformerException e) {
			log.error("xml check Error : "+e.getMessage(),e);
		}
		StringBuffer rtSB = new StringBuffer();
		
		try {
			StringBuffer curSB		= new StringBuffer();
			StringBuffer childSB		= new StringBuffer();
			
			//2024.05.08 - 야전급 IPB의 경우 현재 품목 표시 안함 - jingi.kim
			if( "BLOCK2".equalsIgnoreCase(ext.getBizCode()) && ipb2Flag ) {
				curSB = new StringBuffer("");
			} else {
				//현재 품목
				curSB = this.getTableTdHtml(psDto, paNode, false);
				log.info("getTableBodyHtml curSB : "+curSB.toString());
			}
			
			//2024.12.05 - MUAV, 야전급 IPB의 경우 현재 품목 표시 안함 - jingi.kim
			if( "MUAV".equalsIgnoreCase(ext.getBizCode()) && ipb2Flag ) {
				curSB = new StringBuffer("");
			}
			
			//하위 품목 목록
			NodeList childList = XmlDomParser.getNodeListFromXPathAPI(paNode, DTD.IPB_PARTINFO);
//			NodeList childList = XmlDomParser.getNodeListFromXPathAPI(paNode, XALAN.IPB_PARTINFO_ALL);
			String nodeStr1 = nodeListToString(childList);
			log.info("nodeStr1 : "+nodeStr1);
			log.info("getTableBodyHtml childList1["+childList.getLength()+"]");
			if (childList.getLength() > 0) {
				for (int i=0; i<childList.getLength(); i++) {
					/**
					 * 2021 07 12 jspark
					 * 최초 로드건으로 판단될경우 최상위 객체를 레퍼런스로 판단 링크를 안건다
					 */
					if(nodeToString(paNode).indexOf("<system>") == 0 && childList.getLength() == 1 && "".equals(curSB.toString())) {
						log.info("getTableTdHtml false");
						childSB.append( this.getTableTdHtml(psDto, childList.item(i), false) );
					}else {
						log.info("getTableTdHtml true");
						childSB.append( this.getTableTdHtml(psDto, childList.item(i), true) );
					}
					log.info("getTableTdHtml1 : "+ this.getTableTdHtml(psDto, childList.item(i), true).toString());
				}
			}
			log.info("childSB : "+childSB);
			rtSB.append(CSS.TB_TBODY);
			rtSB.append(curSB);
			rtSB.append(childSB);
			rtSB.append(CSS.TB_TBODY_END);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("IPBParser.getTableBodyHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	/**
	 * <pre>
	 * 2021 07 하위 뎁스 내용까지 나오도록 수정
	 * </prd>
	 * @MethodName	: getTableBodyHtml2
	 * @AuthorDate		: jspark
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @return
	 */
	public StringBuffer getTableBodyHtml2(ParserDto psDto, Node paNode) {
		try {
			log.info("getTableBodyHtml2 paNode : "+nodeToString(paNode));
		} catch (TransformerException e) {
			log.error("xml check Error : "+e.getMessage(),e);
		}
		
		StringBuffer rtSB = new StringBuffer();
		
		try {
			StringBuffer curSB		= new StringBuffer();
			StringBuffer childSB		= new StringBuffer();
			
			//현재 품목
			//curSB = this.getTableTdHtml(psDto, paNode, false);
			//log.info("getTableBodyHtml2 curSB : "+curSB.toString());
			//하위 품목 목록
			NodeList childList = XmlDomParser.getNodeListFromXPathAPI(paNode, DTD.IPB_PARTINFO);
			String nodeStr1 = nodeListToString(childList);
			log.info("nodeStr1 : "+nodeStr1);
			log.info("getTableBodyHtml2 childList1["+childList.getLength()+"]");
			if (childList.getLength() > 0) {
				for (int i=0; i<childList.getLength(); i++) {
					/**
					 * 2021 07 12 jspark
					 * 최초 로드건으로 판단될경우 최상위 객체를 레퍼런스로 판단 링크를 안건다
					 */
					if(nodeToString(paNode).indexOf("<system>") == 0 && childList.getLength() == 1) {
						childSB.append( this.getTableTdHtml(psDto, childList.item(i), false) );
					}else {
						childSB.append( this.getTableTdHtml(psDto, childList.item(i), true) );
					}
					log.info("getTableBodyHtml2 : "+ this.getTableTdHtml(psDto, childList.item(i), true).toString());
				}
			}
			
			try {
				childList = XmlDomParser.getNodeListAllFromXPathAPI(paNode, DTD.IPB_PARTINFO);
				String nodeStr2 = nodeListToString(childList);
				log.info("nodeStr2 : "+nodeStr2);
				log.info("getTableBodyHtml2 childList2["+childList.getLength()+"]");
				if (childList.getLength() > 0) {
					for (int i=0; i<childList.getLength(); i++) {
						childSB.append( this.getTableTdHtml(psDto, childList.item(i), true) );
						log.info("getTableBodyHtml2 : "+ this.getTableTdHtml(psDto, childList.item(i), true).toString());
					}
				}
				
			}catch (Exception e) {
				log.error("테이블 하위 객체 추가 처리중 오류 : "+e.getMessage());
			}
			
			rtSB.append(CSS.TB_TBODY);
			rtSB.append(curSB);
			rtSB.append(childSB);
			rtSB.append(CSS.TB_TBODY_END);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("IPBParser.getTableBodyHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}

	
	/**
	 * 
	 * @MethodName			: getTableTdHtml
	 * @AuthorDate			: LIM Y.M. / 2014. 10. 10.
	 * @ModificationHistory	: Park J.s. /2021 07 12
	 * @param psDto, paNode
	 * @param isChild (true: 자식노드, false: 현재노드(부모))
	 * @return
	 */
	public StringBuffer getTableTdHtml(ParserDto psDto, Node paNode, boolean isChild) {
		
		StringBuffer rtSB = new StringBuffer();
		
		try {
			if (paNode == null || !paNode.getNodeName().equals(DTD.IPB_PARTINFO)) {
				log.info("getTableTdHtml Return IPB_PARTINFO");
				return rtSB;
			}
			
			String[] colData = this.setColumnData(psDto, paNode, isChild);
			if (colData == null || colData.length == 0) {
				log.info("getTableTdHtml Return colData.length");
				return rtSB;
			}
			
			StringBuffer nodeSB	= new StringBuffer();
			String partData	= "";
			
			Boolean searchRow = false;
			
			//테이블 내용 HTML 구성(항목 선택 여부에 따라 다르게 호출)
			if (psDto.getArrIPBCols() == null || "".equals(psDto.getArrIPBCols())) {
				for (int i=0; i<IPB_ATTR.length; i++) {
					//20200309 edit IPBCODE, LCN은 default로만 표시 안하고, 선택된 것은 해군일때만 무조건 표시 안함
					if (IPB_TYPE == IConstants.MILIT_AIR || IPB_TYPE == IConstants.MILIT_NAVY ) {
						if (i == 0 || i == 1) {
							continue;
						}
					}
					//2022 06 08 Park.J.S. Add 야전급 여부 확인 추가
					//2022 06 15 Park.J.S. Update : 언어 구분 위해 수정
					if(!ipbTypeUseCheck(i, psDto.getLanguageType(),ipb2Flag)) {
						log.info("Not use ipb2 : "+IPB_ATTR[i][colName]);
						continue;
					}
					partData = this.appendTableTdData(i, colData[i]).toString();
					
					String appendData = CodeConverter.getCodeConverter(psDto, partData, "", ""); 
					nodeSB.append(appendData);
					
					if(appendData.contains(CSS.FONT_CC)) searchRow = true;
				}
			} else {
				String[] arrCols = StringUtil.splitStr(psDto.getArrIPBCols(), CHAR.IPB_REGEX_COLS);
				Arrays.sort(arrCols);
				
				for (int i=0; i<IPB_ATTR.length; i++) {
					//20200309 add
					if (IPB_TYPE == IConstants.MILIT_NAVY ) {
						if (i == 0 || i == 1) {
							continue;
						}
					}
					//2022 06 08 Park.J.S. Add 야전급 여부 확인 추가
					//2022 06 15 Park.J.S. Update : 언어 구분 위해 수정
					if(!ipbTypeUseCheck(i, psDto.getLanguageType(), ipb2Flag)) {
						log.info("Not use ipb2 : "+IPB_ATTR[i][colName]);
						continue;
					}
					int idx = Arrays.binarySearch(arrCols, IPB_ATTR[i][colId]);
					if (idx > -1) {
						partData = this.appendTableTdData(i, colData[i]).toString();
						
						String appendData = CodeConverter.getCodeConverter(psDto, partData, "", ""); 
						nodeSB.append(appendData);
						
						if(appendData.contains(CSS.FONT_CC)) searchRow = true;
					}
				}
			}
			
			//2022 05 19 Park.J.S. : 버전관련 수정 추가.
			//버전 정보
			StringBuffer verSB = verParser.checkVersionHtml(psDto, paNode);
			//System.out.println("verSB : "+verSB.toString());
			String verEndStr = verParser.endVersionHtml(verSB);
			//System.out.println("verEndStr : "+verEndStr);

			//2023.09.14 - 첫 버전열 제거 요청 - jingi.kim
			//2024.05.10 - 야전급 ipb 교범(TM 형식)일 경우 테이블의 열 크기가 틀려져서 재조정- jingi.kim
			//2024.09.09 - MUAV 추가 - jingi.kim
			//2024.11.14 - SENSOR 추가 - chohee.cha
			boolean isCanon = false;
			if ("LSAM".equalsIgnoreCase(ext.getBizCode()) || "NLS".equalsIgnoreCase(ext.getBizCode()) || "KICC".equalsIgnoreCase(ext.getBizCode()) || "MUAV".equalsIgnoreCase(ext.getBizCode()) || "SENSOR".equalsIgnoreCase(ext.getBizCode())) {
				isCanon = true;
			}
			if(isCanon) {
				nodeSB.insert(0, "<td width=\"10px\" class=\"hidden_col\">"+verSB.toString()+"&nbsp;"+verEndStr+"</td>");
			} else {
				nodeSB.insert(0, "<td width=\"10px\">"+verSB.toString()+"&nbsp;"+verEndStr+"</td>");
			}
			
			//2021 07 12 ID 중복 가능성으로 NAME으로 수정
			//rtSB.append( StringUtil.replace(CSS.TB_TR_IPB, CSS.REGEX,  IConstants.IPB_TABLE_TR_ID + colData[orgIndexnumN]) );

			if(searchRow) {
				rtSB.append( StringUtil.replace(CSS.TB_TR_IPB_NAME_SEARCH, CSS.REGEX,  IConstants.IPB_TABLE_TR_ID + colData[orgIndexnumN]));
			} else {
				rtSB.append( StringUtil.replace(CSS.TB_TR_IPB_NAME, CSS.REGEX,  IConstants.IPB_TABLE_TR_ID + colData[orgIndexnumN]));
			}
			
			rtSB.append(nodeSB);
			rtSB.append(CSS.TB_TREND);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("IPBParser.getTableTdHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * 
	 * @MethodName	: setColumnData
	 * @AuthorDate		: LIM Y.M. / 2014. 11. 8.
	 * @ModificationHistory	: 
	 * @param psDto, paNode, isChild
	 * @return
	 */
	
	public String[] setColumnData(ParserDto psDto, Node paNode, boolean isChild) {
		log.info("Call setColumnData");
		String[] rtStr = null;
		
		try {
			if (paNode == null || !paNode.getNodeName().equals(DTD.IPB_PARTINFO)) {
				return rtStr;
			}
			
			String[] data		= new String[MAX_COLS];
			String childLinkHtmlMove = "";
			
			//<partinto>에서 속성들 추출
			NamedNodeMap partAttr	= paNode.getAttributes();
			data[ipbcodeN]			= XmlDomParser.getAttributes(partAttr, ATTR.IPB_IPBCODE);
			data[lcnN]				= XmlDomParser.getAttributes(partAttr, ATTR.IPB_LCN);
			
			//20200210 add LYM 그림번호 컬럼명이 프로젝트에 따라 다름
			//graphicnum = LSAM / figureno = MDV / HUSS 기억안남
			//20201020 edit LYM
			data[grphnumN]			= XmlDomParser.getAttributes(partAttr, ATTR.IPB_GRPHNUM);
			if(!"KTA".equalsIgnoreCase(ext.getBizCode())){
				data[grphnumN]			= XmlDomParser.getAttributes(partAttr, ATTR.IPB_GRPHNUM);
			}else {
				data[grphnumN]			= XmlDomParser.getAttributes(partAttr, ATTR.IPB_GRPHNUM2);
			}
			/**
			 * 2022 09 14 Park.J.S. 기종별 말고도 그림이 바뀌는 경우가 있어서 추가함
			 */
			if(data[grphnumN] == null || "".equals(data[grphnumN])) {
				if("KTA".equalsIgnoreCase(ext.getBizCode())){
					data[grphnumN]			= XmlDomParser.getAttributes(partAttr, ATTR.IPB_GRPHNUM);
				}else {
					data[grphnumN]			= XmlDomParser.getAttributes(partAttr, ATTR.IPB_GRPHNUM2);
				}
			}
			//data[grphnumN]		= XmlDomParser.getAttributes(partAttr, ATTR.IPB_GRPHNUM2);
			/*
			log.info("ATTR.IPB_INDEXNUM : "+XmlDomParser.getAttributes(partAttr, ATTR.IPB_INDEXNUM));
			log.info("ATTR.ID           : "+XmlDomParser.getAttributes(partAttr, ATTR.ID));
			log.info("ATTR.IPB_IPBCODE  : "+XmlDomParser.getAttributes(partAttr, ATTR.IPB_IPBCODE));
			log.info("ATTR.NAME         : "+XmlDomParser.getAttributes(partAttr, ATTR.NAME));
			*/
			if(isChild) {
				//2022 08 22 Park.J.S. Update : 품목번호 [&nbsp;]로 표시된 부분은 공란으로 표시되어야함.
				if(XmlDomParser.getAttributes(partAttr, ATTR.IPB_INDEXNUM) == null || "".equals(XmlDomParser.getAttributes(partAttr, ATTR.IPB_INDEXNUM)) 
						|| " ".equals(XmlDomParser.getAttributes(partAttr, ATTR.IPB_INDEXNUM)) || "&nbsp;".equals(XmlDomParser.getAttributes(partAttr, ATTR.IPB_INDEXNUM))) {
					data[indexnumN]		= "";
				}else {
					data[indexnumN]		= XmlDomParser.getAttributes(partAttr, ATTR.IPB_INDEXNUM);
				}
				data[orgIndexnumN]	= XmlDomParser.getAttributes(partAttr, ATTR.IPB_INDEXNUM);
			} else {
				//2023 01 17 Park.J.S. ADD : [KTA]IPB 품목/시트번호에 아무 숫자가 없는 항목에 대해 eXPIS3에서 '0'으로 표기됨 수정 BLOCK2와 차이점  추후 LSAM 문제 발생시 추가 필요
				if("KTA".equalsIgnoreCase(ext.getBizCode())){
					if(XmlDomParser.getAttributes(partAttr, ATTR.IPB_INDEXNUM) == null || "".equals(XmlDomParser.getAttributes(partAttr, ATTR.IPB_INDEXNUM)) 
							|| " ".equals(XmlDomParser.getAttributes(partAttr, ATTR.IPB_INDEXNUM)) || "&nbsp;".equals(XmlDomParser.getAttributes(partAttr, ATTR.IPB_INDEXNUM))) {
						data[indexnumN]		= "";
					}else {
						data[indexnumN]		= XmlDomParser.getAttributes(partAttr, ATTR.IPB_INDEXNUM);
					}
				}else {
					data[indexnumN]		= "0";
				}
				data[orgIndexnumN]		= "0";
			}
			
			data[levelN]				= XmlDomParser.getAttributes(partAttr, ATTR.IPB_LEVEL);
			data[contentN]				= XmlDomParser.getAttributes(partAttr, ATTR.IPB_CONTENT);
			//2022 09 21 Park.J.S. (단위에서 줄바꿈 요청 처리
			if("BLOCK2".equalsIgnoreCase(ext.getBizCode()) && data[contentN] != null && data[contentN].contains("(") && data[contentN].indexOf("(") != 0 ) {
				String[] tempContent = data[contentN].split("\\(");
				String tempStr = "";
				for(int i=0;i<tempContent.length;i++) {
					if(tempContent.length > 1 && i == 0 ) {
						tempStr += tempContent[i]+"<br/>";
					}else if(tempContent.length > 1) {
						tempStr += "("+tempContent[i];
					}
				}
				data[contentN]	= tempStr;
			}
			data[unitsperN]				= XmlDomParser.getAttributes(partAttr, ATTR.IPB_UNITSPER);
			//2022 08 22 Park.J.S. Update : 품목번호 0(isChild ==  false)번의 Retrofit과 UOC 내용이 미시현되어야함.
			if(!"KTA".equalsIgnoreCase(ext.getBizCode()) && !isChild){
				data[retrofitN]				= "";
				data[uocN]					= "";
			}else {
				data[retrofitN]				= XmlDomParser.getAttributes(partAttr, ATTR.IPB_RETROFIT);
				data[uocN]					= XmlDomParser.getAttributes(partAttr, ATTR.IPB_UOC);
			}
			data[rdnN]					= XmlDomParser.getAttributes(partAttr, ATTR.IPB_RDN);
			data[wucN]					= XmlDomParser.getAttributes(partAttr, ATTR.IPB_WUC);
			data[partsourceN]			= XmlDomParser.getAttributes(partAttr, ATTR.IPB_PARTSOURCE);
			data[refdataN]				= XmlDomParser.getAttributes(partAttr, ATTR.IPB_REFDATA);
			data[reftonoN]				= XmlDomParser.getAttributes(partAttr, ATTR.IPB_REFTONO);
			data[stdN]					= XmlDomParser.getAttributes(partAttr, ATTR.IPB_STD);
			data[sssnN]					= XmlDomParser.getAttributes(partAttr, ATTR.IPB_SSSN);
			data[checkdivN]				= XmlDomParser.getAttributes(partAttr, ATTR.IPB_CHECKDIV);
			data[childpartN]			= XmlDomParser.getAttributes(partAttr, ATTR.IPB_CHILDPART);
			data[pdrefidN]				= XmlDomParser.getAttributes(partAttr, ATTR.IPB_PDREFID);
			
			//2021 07 12 jspark
			//data[orgIndexnumN]		= XmlDomParser.getAttributes(partAttr, ATTR.IPB_INDEXNUM);
			
			
			//<partbase>에서 속성들 추출
			Node pbaseNode = XmlDomParser.getNodeFromXPathAPI(paNode, XALAN.IPB_PARTBASE);
			if (pbaseNode != null) {
				NamedNodeMap pbaseAttr = pbaseNode.getAttributes();
				data[partnumN]		= XmlDomParser.getAttributes(pbaseAttr, ATTR.IPB_PARTNUM);
				data[smrN]			= XmlDomParser.getAttributes(pbaseAttr, ATTR.IPB_SMR);
				data[nsnN]			= XmlDomParser.getAttributes(pbaseAttr, ATTR.IPB_NSN);
				data[cageN]			= XmlDomParser.getAttributes(pbaseAttr, ATTR.IPB_CAGE);
				data[filenameN]		= XmlDomParser.getAttributes(pbaseAttr, ATTR.IPB_FILENAME);

				//log.info("ATTR.IPB_FILENAME : "+XmlDomParser.getAttributes(pbaseAttr, ATTR.IPB_FILENAME));
				//2022 07 07 Park.J.S. ADD 공백일경우 하위 객체에서 설명 찾아서 추가하는 로직 추가
				if(data[contentN] != null && "".equals(data[contentN])) {
					data[contentN]		= XmlDomParser.getAttributes(pbaseAttr, ATTR.IPB_CONTENT);
					if(data[contentN] != null && "".equals(data[contentN])) {
						data[contentN]		= XmlDomParser.getAttributes(pbaseAttr, ATTR.IPB_CONTENT);
						//2023 01 17 Park.J.S. ADD : KTA에 한해서 설명부분이 name에 있지 않고 별도 text 노드에 있는 경우가 있어서 추가함
                        log.info("Check contentN : {}", data[contentN]);
						if("KTA".equalsIgnoreCase(ext.getBizCode()) && data[contentN] != null && "".equals(data[contentN])) {
                            log.info("Check contentN : {}", data[contentN]);
							Node textNode = XmlDomParser.getNodeFromXPathAPI(pbaseNode, "./text");
                            log.info("Check textNode : {}", textNode);
							if(textNode != null) {
                                log.info("textNode : {}", nodeToString(textNode));
								data[contentN]		= textNode.getTextContent().trim();
                                log.info("Check contentN : {}", data[contentN]);
							}
						}
					}
				}
//				log.info("pbaseNode : "+nodeToString(pbaseNode));
			}
			
			//검색어 반전 효과
			if (!psDto.getSearchWord().equals("")) {
				String scWord		= psDto.getSearchWord();
				data[partnumN]		= CodeConverter.getSearchKeywordIPBWC(data[partnumN]	, scWord);
				data[contentN]		= CodeConverter.getSearchKeywordIPBWC(data[contentN]	, scWord);
				data[nsnN]			= CodeConverter.getSearchKeywordIPBWC(data[nsnN]		, scWord);
				data[rdnN]			= CodeConverter.getSearchKeywordIPBWC(data[rdnN]		, scWord);
			}
			
			//하위 부품 링크 기능 - 부품번호, 설명에 하위 부품 링크 걸음
			if (isChild == true && !data[childpartN].equals("")) {
				int childpart = Integer.parseInt(data[childpartN]);
				if (childpart > 0) {
					String pinfoId			= XmlDomParser.getAttributes(partAttr, ATTR.ID);
					//2022 06 24 Park.J.S. Update : 이전 소스에서 id사용, 200807 id->did 로 변경, 20091123 id->id did->did로 변경 문구 발견하고 수정
					if(!"KTA".equalsIgnoreCase(ext.getBizCode())){
						pinfoId			= XmlDomParser.getAttributes(partAttr, ATTR.ID);
					}else {
						pinfoId			= XmlDomParser.getAttributes(partAttr, "did");
					}
					String childLinkHtml	= CSS.getIPBChildMove(psDto.getToKey(), pinfoId, psDto.getSearchWord());
					
					
					if (!childLinkHtml.equals("")) {
						//by ejkim 2022.10.04 javascript (popIpbReplacePart,viewExContents) 중복으로 javascript 제거처리
						//childLinkHtmlMove = CSS.getIPBChildMove_add(psDto.getToKey(), pinfoId, psDto.getSearchWord()) + data[partnumN];
						childLinkHtmlMove = data[partnumN];

						// 2023.11.09 - T50, 하위부품링크는 설명에만 적용 - jingi.kim
						if ( "BLOCK2".equalsIgnoreCase(ext.getBizCode()) ) {
							data[contentN]	= childLinkHtml + data[contentN] + CSS.A_END;
						} else {
							data[partnumN]	= childLinkHtml + data[partnumN] + CSS.A_END;
							data[contentN]	= childLinkHtml + data[contentN] + CSS.A_END;
						}
					}
					
					
				}else { 
					//2022 07 27 Park.J.S. ADD 호기 분리 참조 관련 수정 (BLOCK2만 존재함)
					try {
						if(childpart == 0 && !"".equals(data[pdrefidN]) &&
							!"".equals(XmlDomParser.getAttributes(partAttr, "sub_graphicnum"))) {
							String pinfoId			= XmlDomParser.getAttributes(partAttr, ATTR.ID);
							String childLinkHtml	= CSS.getIPBChildMove(psDto.getToKey(), pinfoId, psDto.getSearchWord());
							
							if (!childLinkHtml.equals("")) {
								//by ejkim 2022.10.04 javascript (popIpbReplacePart,viewExContents) 중복으로 javascript 제거처리
								//childLinkHtmlMove = CSS.getIPBChildMove_add(psDto.getToKey(), pinfoId, psDto.getSearchWord()) + data[partnumN];
								childLinkHtmlMove = data[partnumN];
								
								// 2023.11.09 - T50, 하위부품링크는 설명에만 적용 - jingi.kim
								if ( "BLOCK2".equalsIgnoreCase(ext.getBizCode()) ) {
									data[contentN]	= childLinkHtml + data[contentN] + CSS.A_END;
								} else {
									data[partnumN]	= childLinkHtml + data[partnumN] + CSS.A_END;
									data[contentN]	= childLinkHtml + data[contentN] + CSS.A_END;
								}
							}
						}
					}catch (Exception e) {
						log.error("호기 분리 참조 관련  Error : "+e.getMessage());
					}
					
				}
			}
			
//			log.info("-------------------------------------");
//			log.info("data[indexnumN] : " + data[indexnumN]);
//			log.info("data[filenameN] : " + data[filenameN]);
//			log.info("data[nsnN]      : " + data[nsnN]);
//			log.info("PREV_FILENUM before : " + PREV_FILENUM);
			
//			System.out.println("-------------------------------------");
//			System.out.println("data[indexnumN] : " + data[indexnumN]);
//			System.out.println("data[filenameN] : " + data[filenameN]);
//			System.out.println("PREV_FILENUM before : " + PREV_FILENUM);
			
			//그래픽 핫스팟 기능 - IPB테이블의 품목번호 클릭시 그래픽의 핫스팟 효과
			String hotspotHtml = "";
			//log.info("data[indexnumN] : "+data[indexnumN]+", data[filenameN] : "+data[filenameN]+", PREV_FILENUM : "+PREV_FILENUM);
			if (data[indexnumN] != null && data[filenameN] != null && !data[indexnumN].equals("") && !data[filenameN].equals("") && !data[indexnumN].equals("0")) {
				String grphPath = psDto.getBizIetmdata() + "/" + IConstants.GRPH_PATH_IMAGE + "/";
				data[filenameN] = grphPath + StringUtil.replace(data[filenameN], "|", "|" + grphPath);
				hotspotHtml = CSS.getIPBPartHotspot(data[filenameN], data[indexnumN]);
				PREV_FILENUM = data[indexnumN];
				data[indexnumN] = hotspotHtml + data[indexnumN] + CSS.A_END;
				CHK_EVENT = true;
			} else if (data[indexnumN] != null && !data[indexnumN].equals("")  && !data[indexnumN].equals("0")) {
				log.info("저작 시스템 인덱스 미생성으로 파일 이름 못불러오는 경우 발생함");
				hotspotHtml = CSS.getIPBPartHotspot(data[filenameN], data[indexnumN]);
				data[indexnumN] = hotspotHtml + data[indexnumN] + CSS.A_END;
				CHK_EVENT = true;
			} else if(PREV_FILENUM.equals(data[indexnumN])) {
				CHK_EVENT = false;
				PREV_FILENUM = data[indexnumN];
				hotspotHtml = CSS.getIPBPartHotspot(data[filenameN], data[indexnumN]);
				data[indexnumN] = hotspotHtml + data[indexnumN] + CSS.A_END;
			}
			
//			System.out.println("PREV_FILENUM after : " + PREV_FILENUM);
//			System.out.println("CHK_EVENT : " + CHK_EVENT);
//			System.out.println("===>hotspotHtml="+hotspotHtml);

//			log.info("data[indexnumN]  : " + data[indexnumN]+", data[filenameN] : "+data[filenameN]);
//			log.info("PREV_FILENUM after : " + PREV_FILENUM);
//			log.info("CHK_EVENT : " + CHK_EVENT);
//			log.info("===>hotspotHtml="+hotspotHtml);
			
//			if(!data[filenameN].equals("")) {
//				String grphPath = psDto.getBizIetmdata() + "/" + IConstants.GRPH_PATH_IMAGE + "/";
//				data[filenameN] = grphPath + StringUtil.replace(data[filenameN], "|", "|" + grphPath);
//			}
			
			/*
			//기능 미 구현 (eXPIS2 T50에서 임의로 넣었던 기능임)
			//대체 품목 팝업 기능 - 대체 품목이 있을 경우(replacePart.xml 검사) 국가재고번호(NSN) 클릭시 팝업으로 대체 품목 정보 시현
			*/
			
			// 2023.11.15 - T50, kai_std를 이용해서 대체품 확인  - jingi.kim
			// 2024.09.26 - block2 전부 대체품 사용  - jingi.kim
			boolean isKaiReplacePart = false;
			if ( "BLOCK2".equalsIgnoreCase(ext.getBizCode()) ) {
				isKaiReplacePart = true;
			}
			
			//2022 12 15 Park.J.S ADD : 검색어 반전처리후에 스크립트 관련 문제 발생할수 있어서 String 별도 지정
			String tempNsnN = data[nsnN];
			
			if ( isKaiReplacePart == true ) {
				if ( !"".equalsIgnoreCase(data[stdN]) ) {
					if ( isIPBReplacePartKaistd(data[stdN], psDto.getBizSyspath()) ) {
						String replacePartHtml = "";
						String param1 = data[partnumN];
						if ( !"".equalsIgnoreCase(childLinkHtmlMove) ) {
							param1 = childLinkHtmlMove;
						}
						//2024.11.14 - 검색어 효과제거 - jingi.kim
						if (!psDto.getSearchWord().equals("")) {
							param1 = param1.replace(CSS.FONT_CC, "");
							param1 = param1.replace(CSS.FONT_END, "");
						}
						replacePartHtml	= "<a href='javascript:;' onclick=\"javascript:popIpbReplacePart('','" +param1+ "', '" + data[stdN] + "');\">";
						
						data[partnumN] = replacePartHtml + data[partnumN] +  CSS.A_END;
					}
				}
			} else {
				//2020 01 Park J.S. : 대체 품목 팝업 기능 구현
				if ( tempNsnN != null && !tempNsnN.equals("")) {
					//nsnN 대체품목 확인
					if(isIPBReplacePart(tempNsnN,psDto.getBizSyspath())) {
						
						//대체품목  존재 할경우 해당 리스트 호출할수 있는 javascript 호출
						//by ejkim 2022.10.04 하위링크 유무에 따라 호출 처리 변경
						//String replacePartHtml	= "<a href='javascript:;' onclick=\"javascript:popIpbReplacePart('" + data[nsnN] + "','"+data[partnumN]+"');\">";
						
						String replacePartHtml = "";
//					log.info("대체품목이 존재 여부 : "+childLinkHtmlMove);
						if( !childLinkHtmlMove.equals("")) { //대체품목이 존재할 경우
//						log.info("대체품목이 존재  : "+childLinkHtmlMove);
							//replacePartHtml	= "<a href='javascript:;' onclick=\"javascript:popIpbReplacePart('" + data[nsnN] + "', "+childLinkHtmlMove+"');\">";
							replacePartHtml	= "<a href='javascript:;' onclick=\"javascript:popIpbReplacePart('" + tempNsnN + "', '"+childLinkHtmlMove+"');\">";
						}else {  //대체품목이 존재하지 않을 경우
//						log.info("대체품목이 존재하지 않을 경우");
							replacePartHtml	= "<a href='javascript:;' onclick=\"javascript:popIpbReplacePart('" + tempNsnN + "','"+data[partnumN]+"');\">";
						}
						
						data[nsnN] = replacePartHtml + data[nsnN] +  CSS.A_END;
					}else {
//					log.info("No isIPBReplacePart : "+data[nsnN]);
					}
				}
			}
			
			rtStr = data;
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("IPBParser.setColumnData Exception:"+ex.toString());
		}
		
		return rtStr;
	}
	
	
	/**
	 * 
	 * @MethodName	: appendTableTdData
	 * @AuthorDate		: LIM Y.M. / 2014. 10. 31.
	 * @ModificationHistory	: 
	 * @param index, tdData
	 * @return
	 */
	public StringBuffer appendTableTdData(int index, String tdData) {
		
		StringBuffer rtSB = new StringBuffer();
		
		try {
			if (index < 0) {
				return rtSB;
			}
			//2022 08 01 Park.J.S. Update : 테이블 고정 처리 위해 수정 테이블 width 수정해야하나 일정상 테이블 그대로 사용후 가림
			//2023.07.10 jysi EDIT : LSAM분기점에 NLS추가
			//2024.09.09 - MUAV 추가 - jingi.kim
			//2024.11.14 - SENSOR 추가 - chohee.cha
			boolean isCanon = false;
			if ("LSAM".equalsIgnoreCase(ext.getBizCode()) || "NLS".equalsIgnoreCase(ext.getBizCode()) || "KICC".equalsIgnoreCase(ext.getBizCode()) || "MUAV".equalsIgnoreCase(ext.getBizCode()) || "SENSOR".equalsIgnoreCase(ext.getBizCode())) {
				isCanon = true;
			}
			if(isCanon){
				rtSB.append( CSS.getTableTd(IPB_ATTR[index][colStyle]));
			}else {
				//2022 10 19 IPB 수정 내용 화면 표시위해 name 처리 추가
				rtSB.append( CSS.getTableTd(IPB_ATTR[index][colStyle] + " width='" + IPB_ATTR[index][colWidth] + "px" + "' name='"+IPB_ATTR[index][0]+"'"));
				
			}
			rtSB.append(tdData);
			rtSB.append(CSS.TB_TDEND);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("IPBParser.appendTableTdData Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	
	/**
	 * IPB테이블 시현시 항목 선택 폼 호출
	 * @MethodName	: getIpbColsList
	 * @AuthorDate		: LIM Y.M. / 2014. 11. 10.
	 * @ModificationHistory	: 
	 * @param ipbCols
	 * @return
	 */
	public ArrayList<Hashtable<String, String>> getIpbColsList(String ipbCols) {

		ArrayList<Hashtable<String, String>> rtList = new ArrayList<Hashtable<String, String>>();
		
		try {
			
			//IPB 항목 선택 컬럼들 정의
			String[] arrCols = StringUtil.splitStr(ipbCols, CHAR.IPB_REGEX_COLS);
			Arrays.sort(arrCols);
			Hashtable<String, String> ht = new Hashtable<String, String>();
			
			for (int i=0; i<IPB_ATTR.length; i++) {
				//20200309 add LYM 항목선택에서 공군은 IPBCODE, LCN 표시하고, 해군일때는 표시 안함
				if (IPB_TYPE == IConstants.MILIT_NAVY ) {
					if (i == 0 || i == 1) {
						continue;
					}
				}

				//2023.05.19 jysi EDIT : 항목 선택 폼도 ipbTypeUseCheck 사용하여 타입에 따른 항목 제외
				if(!ipbTypeUseCheck(i, languageType, ipb2Flag)) {
					log.info("Not use ipb2 : "+IPB_ATTR[i][colName]);
					continue;
				}
				
				String isChecked = "";
				String ipbAttr = "";
				String isHidden = ""; //2023.05.22 jysi ADD
				int idx = Arrays.binarySearch(arrCols, IPB_ATTR[i][colId]);
				if (idx > -1) {
					isChecked = "checked";
				}
				
				//2023.05.22 jysi EDIT : 항목 선택 폼도 IPB테이블에서 숨김처리된 항목 안보이게하기 
				if (IPB_ATTR[i][colStyle].contains("hidden")) {
					isHidden = "hidden";
				}
				
				ht = new Hashtable<String, String>();
				ht.put("id", IPB_ATTR[i][colId]);
				ipbAttr = IPB_ATTR[i][colName].replaceAll("<br>", "");
				ht.put("name", ipbAttr);
				ht.put("check", isChecked);
				ht.put("hidden", isHidden);
				
				rtList.add(ht);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("IPBParser.getIpbColsList Exception:"+ex.toString());
		}
		
		return rtList;
	}
	
	/**
	 * 2021 07 
	 * NodeList 합치기 위해 추가
	 * @author Park J.S.
	 * @param nodes
	 * @return
	 * @throws TransformerException
	 */
	private static String nodeListToString(NodeList nodes) throws TransformerException {
	    StringBuilder result = new StringBuilder();
	    int len = nodes.getLength();
	    for(int i = 0; i < len; ++ i) {
	        result.append(nodeToString(nodes.item(i)));
	    }
	    return result.toString();
	}
	/**
	 * 2021 07 
	 * Node 로깅 하기 위해 추가
	 * @author Park J.S.
	 * @param node
	 * @return
	 * @throws TransformerException
	 */
	private static String nodeToString(Node node) throws TransformerException {
	    StringWriter buf = new StringWriter();
	    Transformer xform = TransformerFactory.newInstance().newTransformer();
	    xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	    xform.transform(new DOMSource(node), new StreamResult(buf));
	    return(buf.toString());
	}
	
	/**
	 * 2022 01 18 
	 * IPB 호기분리 노드 머지용
	 * pNode에 Node 내용을 머지 한다.
	 * @param mNode		: 호기 분리 데이타
	 * @param pNode		: 호기 분리된 원데이타
	 * @return
	 * @throws ParserConfigurationException 
	 */
	private Node iPBNodeMerge(Node mNode, Node pNode) throws ParserConfigurationException {
		log.info("CALL iPBNodeMerge");
		//호시 처리중에 오류 발생할경우 최소한 추가된 리스트라도 보이게 하기위해 TRY 처리함
		try {
			//OwnerDocument를 통해서만 수정이 가능함
	        Document ownerDocument =  pNode.getOwnerDocument();
	        NodeList mNodeList = mNode.getChildNodes();
			NodeList nodeList = pNode.getChildNodes();
			for(int i=0;i< nodeList.getLength();i++) {
				Node pPartinfo = nodeList.item(i);
				Node PIdAttribute = pPartinfo.getAttributes().getNamedItem("id");
				Node idAttribute = mNode.getAttributes().getNamedItem("p_d_refid");
				log.info("PIdAttribute.getNodeValue() : "+PIdAttribute.getNodeValue()+", idAttribute.getNodeValue() : "+idAttribute.getNodeValue());
				//상위 객체의 레퍼런스가 같을 경우
				if(PIdAttribute.getNodeValue().equals(idAttribute.getNodeValue())) {
					log.info("상위 객체의 레퍼런스가 같을 경우");
					NodeList pPartinfoList = pPartinfo.getChildNodes();
					for(int j=0;j< pPartinfoList.getLength();j++) {
						Node pCheckPartinfo = pPartinfoList.item(j);
						Node pIndexnumAttribute = pCheckPartinfo.getAttributes().getNamedItem("indexnum");
						for(int k=0;k<mNodeList.getLength();k++) {
							Node mcheckPartinfo = ownerDocument.importNode(mNodeList.item(k),true);
							Node mIndexnumAttribute = mcheckPartinfo.getAttributes().getNamedItem("indexnum");
							log.info("pIndexnumAttribute : "+pIndexnumAttribute+", mIndexnumAttribute : "+mIndexnumAttribute);
							if(pIndexnumAttribute != null && mIndexnumAttribute!= null) {
								//indexnum이 같을 경우 치환한다
								log.info("pIndexnumAttribute : "+pIndexnumAttribute.getNodeValue()+", mIndexnumAttribute : "+mIndexnumAttribute.getNodeValue());
								if(pIndexnumAttribute.getNodeValue().equals(mIndexnumAttribute.getNodeValue())) {
									pCheckPartinfo.getParentNode().replaceChild(mcheckPartinfo, pCheckPartinfo);
								}
							}
						}
						/*
						//2022 08 19 Park.J.S ADD 참조 호기 객체에 없는 객체가존재할경우 Update가 아닌 Add가 필요해서 추가.
						if(j+1 < pPartinfoList.getLength()) {
							log.info("Pass Have Next : "+j+", "+pPartinfoList.getLength());
						}else {
							log.info("Set Add Check This is Final");
							for(int k=0;k<mNodeList.getLength();k++) {
								Node mcheckPartinfo = ownerDocument.importNode(mNodeList.item(k),true);
								Node mIndexnumAttribute = mcheckPartinfo.getAttributes().getNamedItem("indexnum");
								log.info("pIndexnumAttribute : "+pIndexnumAttribute+", mIndexnumAttribute : "+mIndexnumAttribute);
							}
							
							//pPartinfo.appendChild("")
							//Document ownerMDocument =  mNode.getOwnerDocument();
							
						}
						*/
					}
					log.info("pNode A : "+nodeToString(pNode));
					//2022 07 27 Park.J.S. ADD : 최상위 노드는 호기 분리 데이타를 써야한다고해서 해당 부분 최하단에 추가
					log.info("Root Node Change");
					Node mcheckPartinfo = ownerDocument.importNode(mNode,true);
					log.info("mcheckPartinfo : "+nodeToString(mcheckPartinfo));
					NodeList pNodeList = pNode.getFirstChild().getChildNodes();
					pNode.replaceChild(mcheckPartinfo, pNode.getFirstChild());
					NodeList removeNodeList = pNode.getFirstChild().getChildNodes();
					for(int j=0;j< removeNodeList.getLength();j++) {
						pNode.getFirstChild().removeChild(removeNodeList.item(j));
					}
					log.info("pNode B : "+nodeToString(pNode));
					for(int j=0;j< pNodeList.getLength();j++) {
						Node tempNode = pNodeList.item(j).cloneNode(true);
						pNode.getFirstChild().appendChild(tempNode);
					}
					log.info("pNode C : "+nodeToString(pNode));
				}else {
					log.info("호기 설정되지 않았음");
					return mNode;
				}
			}
			log.info("pNode : "+nodeToString(pNode));
			//<system> 부분을 없애기 위해서 FirstChild()를 리턴
			return pNode.getFirstChild();
		}catch (Exception e) {
			log.info("호기 처리중 오류 발생  : "+e.getMessage());
			return mNode;
		}
	}
	
	/**
	 * <pre>
	 * IPB 테이블에서 대체품목 있는지 검사
	 * replacePart.xml 검사해서 nsn에 해당하는 element가 존재여부 리턴
	 * (replacePart.xml 사용 하지 않고 DB 방식 사용해야할 경우해당 부분 수정 필요함) 
	 * </pred>
	 * @Author Park J.S. / 2022. 01. 기존 소스(expis2) 내용 참고해서 추가
	 * @param nsn
	 * @param sysPath
	 * @return
	 */
	public boolean isIPBReplacePart(String nsn, String sysPath) {
		boolean bResult = false;

		if (nsn == null || nsn.equals("")) {
			return bResult;
		}

		try {
			ExpisCommonUtile expisUtile = ExpisCommonUtile.getInstance();
			String filepath = sysPath + "/startpage/replacePart.xml";
			log.info("filepath : " + filepath + ", nsn : " + nsn);

			// XML 파일을 DOM 객체로 파싱
			Document doc = expisUtile.createDomTree(filepath, 0);

			// XPath 객체 생성
			XPath xPath = XPathFactory.newInstance().newXPath();

			// XPath 표현식 준비
			String expression = "./partbase[@nsn='" + nsn + "']";
			XPathExpression expr = xPath.compile(expression);

			// XPath 표현식을 적용하여 노드를 찾음
			Node node = (Node) expr.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			if (node != null) {
				bResult = true;
			}

		} catch (Exception e) {
			log.info("Check isIPBReplacePart Error : " + e.getMessage());
		}

		return bResult;
	}
	
	/**
	 * IPB 테이블의 대체품 검사
	 * replacePart.xml 검사해서 kai_std 에 해당하는 값이 있는 경우 리턴
	 * @Author 2023.11.15 - T50 전용 kai_std 를 이용해서 검사하는 함수 추가 - jingi.kim
	 * @return
	 */
	public boolean isIPBReplacePartKaistd(String strPartnum, String sysPath) {
		boolean bResult = false;

		if (strPartnum == null || strPartnum.equals("")) {
			return bResult;
		}

		try {
			ExpisCommonUtile expisUtile = ExpisCommonUtile.getInstance();
			String filepath = sysPath + "/startpage/replacePart.xml";
			log.info("filepath : " + filepath + ", strPartnum : " + strPartnum);

			// XML 파일을 DOM 객체로 파싱
			Document doc = expisUtile.createDomTree(filepath, 0);

			// XPath 객체 생성
			XPath xPath = XPathFactory.newInstance().newXPath();

			// XPath 표현식 준비
			String expression = "./partbase[@kai_std='" + strPartnum + "']";
			XPathExpression expr = xPath.compile(expression);

			// XPath 표현식을 적용하여 노드를 찾음
			Node node = (Node) expr.evaluate(doc.getDocumentElement(), XPathConstants.NODE);
			if (node != null) {
				bResult = true;
			}

		} catch (Exception e) {
			log.info("Check isIPBReplacePartKaistd Error : " + e.getMessage());
		}

		return bResult;
	}
	
	/**
	 * <pre>
	 * 인덱스상 야전급 교범에서 사용하는지 확인해서 사용할경우 true 리턴
	 * </pre>
	 * @author Park.J.S. / 2022 06 08 ADD
	 * @param i
	 * @param languageType : 언어설정 값
	 * @param ipb2Flag2 : 야전급 교범인지 
	 * @return
	 */
	public boolean ipbTypeUseCheck(int i, String languageType, boolean ipb2Flag2) {
		//2022 06 22 Park.J.S. Update : KTA 기종 IPB 추가
		if(!"KTA".equalsIgnoreCase(ext.getBizCode())){
			if(ipb2Flag2){
				//야전급에서 사용하는 교범과 비교
				if(i == grphnumN || i == indexnumN || i == partnumN || i == nsnN || i == cageN || i == contentN || i == unitsperN || i == uocN || i == smrN || i == rdnN){
					//수출기에서 사용여부 
					if((languageType != null && "en".equalsIgnoreCase(languageType))
							&&(i == nsnN || i == retrofitN || i == wucN || i == reftonoN)
							) {
						return false;		
					}else {
						return true;
					}
				}else {
					return false;	
				}
			}else {
				//수출기에서 사용여부 
				if((languageType != null && "en".equalsIgnoreCase(languageType))
						&&(i == nsnN || i == retrofitN || i == wucN || i == reftonoN)
						) {
					return false;
				}else {
					return true;
				}
			}
		}else {
			if(i == grphnumN || i == indexnumN || i == partnumN || i == nsnN || i == cageN || i == contentN || i == unitsperN || i == uocN || i == smrN){
				return true;
			}else {
				return false;
			}
		}
	}
	
	/**
	 * <pre>
	 * IPB 버전 정보는 테이블 형식으로 나와야 한다고 해서 수정
	 * </pre>
	 *  
	 * @MethodName	: getIPBVersionTableHtml
	 * @AuthorDate		: 2022 09 22 Park. J.S. 
	 * @ModificationHistory	: 
	 * @param psDto, paNode
	 * @param contentNode : 비교 대상이 되는 본문 로우
	 * @return
	 */
	public StringBuffer getIPBVersionTableHtml(ParserDto psDto, Node paNode, Node contentNode) {
		
		StringBuffer rtSB = new StringBuffer();
		
		try {
			//Table의 헤더 구성시에 테이블 전체 가로 크기 계산함
			TABLE_WIDTH	= 0;
			//2022 08 01 Park.J.S. Update : 테이블 고정 처리 위해 수정 테이블 width 수정해야하나 일정상 테이블 그대로 사용후 가림
			StringBuffer headHtml 			= this.getTableHeadHtml(psDto, paNode);
			//StringBuffer bufferBodyHtml 	= this.getTableBodyHtml(psDto, paNode);
			StringBuffer bufferBodyHtml 	= new StringBuffer();
			
			StringBuffer curSB		= new StringBuffer();
			StringBuffer childSB	= new StringBuffer();
			
			//현재 품목
			curSB = this.getTableTdHtml(psDto, paNode, false);
			log.info("getTableBodyHtml curSB : "+curSB.toString());
			//하위 품목 목록
			NodeList childList = XmlDomParser.getNodeListFromXPathAPI(paNode, DTD.IPB_PARTINFO);
			String nodeStr1 = nodeListToString(childList);
			log.info("nodeStr1 : "+nodeStr1);
			log.info("getTableBodyHtml childList1["+childList.getLength()+"] : "+IPB_ATTR.length);
			if (childList.getLength() > 0) {
				for (int i=0; i<childList.getLength(); i++) {
					String verhtmlStr = (this.getTableTdHtml(psDto, childList.item(i), true)).toString();
					String[] contentData = this.getColumnDataArry(psDto, contentNode, true);
					String[] versionData = this.getColumnDataArry(psDto, childList.item(i), true);
					log.info("psDto.getArrIPBCols() : "+psDto.getArrIPBCols()+", ipb2Flag : "+ipb2Flag);
					log.info("verhtmlStr : "+verhtmlStr);
					int tempCheckNum = 0;
					if (psDto.getArrIPBCols() == null || "".equals(psDto.getArrIPBCols())) {
						for (int j=0; j<versionData.length; j++) {
							if (IPB_TYPE == IConstants.MILIT_AIR || IPB_TYPE == IConstants.MILIT_NAVY ) {
								if (j == 0 || j == 1) {
									log.info("continue : "+j);
									continue;
								}
							}
							if(!ipbTypeUseCheck(j, psDto.getLanguageType(),ipb2Flag)) {
								log.info("continue : "+j);
								continue;
							}
							//TODO
							if(versionData[j].equals(contentData[j])) {
								log.info("Match Data : "+versionData[j]+" ==> "+contentData[j]);
							}else {
								if(versionData[j] != null) {
								//if(versionData[j] != null && !"".equals(versionData[j])) {
									if(IPB_ATTR.length > j) {
										log.info("Not Match Data versionData : "+versionData[j]+", contentData : "+contentData[j]+" ==> "+tempCheckNum+", "+verhtmlStr.indexOf(">"+versionData[j]+"<"));
										//verhtmlStr = verhtmlStr.replace(">"+versionData[j]+"<", "><span class='ipb_version_diff'>"+versionData[j]+"</span><");
										if("content".equalsIgnoreCase(IPB_ATTR[j][0])) {//content의 경우 기존 class 존재
											verhtmlStr = verhtmlStr.replace("class=\"ipb_nom\"", "class=\"ipb_nom ipb_data_diff_td\"");
											verhtmlStr = verhtmlStr.replace("class='ipb_nom'", "class='ipb_nom ipb_data_diff_td'");
										}else {
											verhtmlStr = verhtmlStr.replace("name=\""+IPB_ATTR[j][0]+"\"", "class=\"ipb_data_diff_td\"");
											verhtmlStr = verhtmlStr.replace("name='"+IPB_ATTR[j][0]+"'", "class='ipb_data_diff_td'");
										}
										log.info("verhtmlStr : "+verhtmlStr);
									}
								}else {
									//log.info("Not Match Data versionData Empty["+IPB_ATTR[j][0]+"] : "+versionData[j]);
									
								}
							}
							tempCheckNum++;
						}
					} else {
						String[] arrCols = StringUtil.splitStr(psDto.getArrIPBCols(), CHAR.IPB_REGEX_COLS);
						Arrays.sort(arrCols);
						for (int j=0; j<versionData.length; j++) {
							//20200309 add
							if (IPB_TYPE == IConstants.MILIT_NAVY ) {
								if (j == 0 || j == 1) {
									log.info("continue : "+j);
									continue;
								}
							}
							if(!ipbTypeUseCheck(j, psDto.getLanguageType(), ipb2Flag)) {
								log.info("continue : "+j);
								continue;
							}
							int idx = Arrays.binarySearch(arrCols, IPB_ATTR[i][colId]);
							if (idx > -1) {
								//TODO
								if(versionData[j].equals(contentData[j])) {
									log.info("Match Data : "+versionData[j]+" ==> "+tempCheckNum);
								}else {
									if(versionData[j] != null && !"".equals(versionData[j])) {
										log.info("Not Match Data versionData : "+versionData[j]+", contentData : "+contentData[j]+" ==> "+tempCheckNum+", "+verhtmlStr.indexOf(">"+versionData[j]+"<"));
										verhtmlStr = verhtmlStr.replace("name='"+IPB_ATTR[j][0]+"'", "class='ipb_data_diff_td'");
										log.info("verhtmlStr : "+verhtmlStr);
									}else {
										log.info("Not Match Data versionData Empty["+IPB_ATTR[j][0]+"] : "+versionData[j]);
									}
								}
								tempCheckNum++;
							}
						}
					}
					childSB.append(verhtmlStr);
					/*
					log.info("getTableTdHtml Content : "+ this.getTableTdHtml(psDto, childList.item(i), true).toString());
					log.info("getTableTdHtml Version : "+ this.getTableTdHtml(psDto, contentNode, true).toString());
					*/
				}
			}
			log.info("childSB : "+childSB);
			bufferBodyHtml.append(CSS.TB_TBODY);
			bufferBodyHtml.append(curSB);
			bufferBodyHtml.append(childSB);
			bufferBodyHtml.append(CSS.TB_TBODY_END);
			
			//2023.07.10 jysi EDIT : LSAM분기점에 NLS추가
			rtSB.append("<div>");
			//2024.09.09 - MUAV 추가 - jingi.kim
			//2024.11.14 - SENSOR 추가 - chohee.cha
			boolean isCanon = false;
			if ("LSAM".equalsIgnoreCase(ext.getBizCode()) || "NLS".equalsIgnoreCase(ext.getBizCode()) || "KICC".equalsIgnoreCase(ext.getBizCode()) || "MUAV".equalsIgnoreCase(ext.getBizCode()) || "SENSOR".equalsIgnoreCase(ext.getBizCode())) {
				isCanon = true;
			}
			if(isCanon){
				rtSB.append(CSS.DIV_IPB_TABLE_P);
				rtSB.append( StringUtil.replace(CSS.TB_TABLE_IPB, CSS.REGEX,  TABLE_WIDTH+"") );
				rtSB.append( headHtml );
				rtSB.append( bufferBodyHtml );
				rtSB.append(CSS.TB_TABLEEND);
				rtSB.append(CSS.DIV_IPB_TABLE_P_END);
				rtSB.append(CSS.DIV_END);
			}else {
				rtSB.append(CSS.DIV_IPB_TABLE_P);
				rtSB.append( StringUtil.replace(CSS.TB_TABLE_IPB, CSS.REGEX,  TABLE_WIDTH+"") );
				rtSB.append( headHtml );
				rtSB.append( bufferBodyHtml );
				rtSB.append(CSS.TB_TABLEEND);
				rtSB.append(CSS.DIV_IPB_TABLE_P_END);
				rtSB.append(CSS.DIV_END);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("IPBParser.getIPBTableHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	/**
	 * <pre>
	 * 2022 10 20 Park.J.S.
	 * 버전 팝업에서 변뎡 사항 비교를 위해 순순 값만 받을수 있는 함수 추가함 
	 * </pre>
	 * @param psDto
	 * @param paNode
	 * @param isChild
	 * @return
	 */
	public String[] getColumnDataArry(ParserDto psDto, Node paNode, boolean isChild) {
		
		String[] rtStr = null;
		
		try {
			if (paNode == null || !paNode.getNodeName().equals(DTD.IPB_PARTINFO)) {
				return rtStr;
			}
			
			String[] data		= new String[MAX_COLS];
			String childLinkHtmlMove = "";
			
			//<partinto>에서 속성들 추출
			//각각 기종별로 상이한 부문 존재 하므로 해당 부분 주의 해서 추출
			NamedNodeMap partAttr	= paNode.getAttributes();
			data[ipbcodeN]			= XmlDomParser.getAttributes(partAttr, ATTR.IPB_IPBCODE);
			data[lcnN]				= XmlDomParser.getAttributes(partAttr, ATTR.IPB_LCN);
			data[grphnumN]			= XmlDomParser.getAttributes(partAttr, ATTR.IPB_GRPHNUM);
			if(!"KTA".equalsIgnoreCase(ext.getBizCode())){
				data[grphnumN]			= XmlDomParser.getAttributes(partAttr, ATTR.IPB_GRPHNUM);
			}else {
				data[grphnumN]			= XmlDomParser.getAttributes(partAttr, ATTR.IPB_GRPHNUM2);
			}
			if(data[grphnumN] == null || "".equals(data[grphnumN])) {
				if("KTA".equalsIgnoreCase(ext.getBizCode())){
					data[grphnumN]			= XmlDomParser.getAttributes(partAttr, ATTR.IPB_GRPHNUM);
				}else {
					data[grphnumN]			= XmlDomParser.getAttributes(partAttr, ATTR.IPB_GRPHNUM2);
				}
			}
			
			if(isChild) {
				if(XmlDomParser.getAttributes(partAttr, ATTR.IPB_INDEXNUM) == null || "".equals(XmlDomParser.getAttributes(partAttr, ATTR.IPB_INDEXNUM)) 
						|| " ".equals(XmlDomParser.getAttributes(partAttr, ATTR.IPB_INDEXNUM)) || "&nbsp;".equals(XmlDomParser.getAttributes(partAttr, ATTR.IPB_INDEXNUM))) {
					data[indexnumN]		= "";
				}else {
					data[indexnumN]		= XmlDomParser.getAttributes(partAttr, ATTR.IPB_INDEXNUM);
				}
				data[orgIndexnumN]	= XmlDomParser.getAttributes(partAttr, ATTR.IPB_INDEXNUM);
			} else {
				data[indexnumN]		= "0";
				data[orgIndexnumN]	= "0";
			}
			
			data[levelN]				= XmlDomParser.getAttributes(partAttr, ATTR.IPB_LEVEL);
			data[contentN]				= XmlDomParser.getAttributes(partAttr, ATTR.IPB_CONTENT);
			data[unitsperN]				= XmlDomParser.getAttributes(partAttr, ATTR.IPB_UNITSPER);
			//2022 08 22 Park.J.S. Update : 품목번호 0(isChild ==  false)번의 Retrofit과 UOC 내용이 미시현되어야함.
			if(!"KTA".equalsIgnoreCase(ext.getBizCode()) && !isChild){
				data[retrofitN]				= "";
				data[uocN]					= "";
			}else {
				data[retrofitN]				= XmlDomParser.getAttributes(partAttr, ATTR.IPB_RETROFIT);
				data[uocN]					= XmlDomParser.getAttributes(partAttr, ATTR.IPB_UOC);
			}
			data[rdnN]					= XmlDomParser.getAttributes(partAttr, ATTR.IPB_RDN);
			data[wucN]					= XmlDomParser.getAttributes(partAttr, ATTR.IPB_WUC);
			data[partsourceN]			= XmlDomParser.getAttributes(partAttr, ATTR.IPB_PARTSOURCE);
			data[refdataN]				= XmlDomParser.getAttributes(partAttr, ATTR.IPB_REFDATA);
			data[reftonoN]				= XmlDomParser.getAttributes(partAttr, ATTR.IPB_REFTONO);
			data[stdN]					= XmlDomParser.getAttributes(partAttr, ATTR.IPB_STD);
			data[sssnN]					= XmlDomParser.getAttributes(partAttr, ATTR.IPB_SSSN);
			data[checkdivN]				= XmlDomParser.getAttributes(partAttr, ATTR.IPB_CHECKDIV);
			data[childpartN]			= XmlDomParser.getAttributes(partAttr, ATTR.IPB_CHILDPART);
			data[pdrefidN]				= XmlDomParser.getAttributes(partAttr, ATTR.IPB_PDREFID);
			
			//<partbase>에서 속성들 추출
			Node pbaseNode = XmlDomParser.getNodeFromXPathAPI(paNode, XALAN.IPB_PARTBASE);
			if (pbaseNode != null) {
				NamedNodeMap pbaseAttr = pbaseNode.getAttributes();
				data[partnumN]		= XmlDomParser.getAttributes(pbaseAttr, ATTR.IPB_PARTNUM);
				data[smrN]			= XmlDomParser.getAttributes(pbaseAttr, ATTR.IPB_SMR);
				data[nsnN]			= XmlDomParser.getAttributes(pbaseAttr, ATTR.IPB_NSN);
				data[cageN]			= XmlDomParser.getAttributes(pbaseAttr, ATTR.IPB_CAGE);
				data[filenameN]		= XmlDomParser.getAttributes(pbaseAttr, ATTR.IPB_FILENAME);
				log.info("Check contentN : "+data[contentN]);
				if(data[contentN] != null && "".equals(data[contentN])) {
					data[contentN]		= XmlDomParser.getAttributes(pbaseAttr, ATTR.IPB_CONTENT);
					//2023 01 17 Park.J.S. ADD : KTA에 한해서 설명부분이 name에 있지 않고 별도 text 노드에 있는 경우가 있어서 추가함
					log.info("Check contentN : "+data[contentN]);
					if("KTA".equalsIgnoreCase(ext.getBizCode()) && data[contentN] != null && "".equals(data[contentN])) {
						log.info("Check contentN : "+data[contentN]);
						Node textNode = XmlDomParser.getNodeFromXPathAPI(pbaseNode, "./text");
						log.info("Check textNode : "+textNode);
						if(textNode != null) {
							log.info("textNode : "+nodeToString(textNode));
							data[contentN]		= textNode.getTextContent().trim();
							log.info("Check contentN : "+data[contentN]);
						}
					}
				}
				log.info("pbaseNode : "+nodeToString(pbaseNode));
			}
			rtStr = data;
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("IPBParser.getColumnDataArry Exception:"+ex.toString());
		}
		
		return rtStr;
	}
	
}
