package com.expis.domparser;

import com.expis.common.ExpisCommonUtile;
import com.expis.common.IConstants;
import com.expis.common.ext.ExtConfig;
import com.expis.common.ext.ExternalFileEx;
import com.expis.ietm.parser.IconParser;
import com.expis.ietm.parser.ParserDto;
import com.expis.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * [코드 변환 모듈]
 */
@Slf4j
public class CodeConverter {

	/**
	 * 사용자 정의 유동 변수 선언 (CM : class member)
	 */
	public static String	CM_STYLE_ALIGN		= "";		//style align
	public static String	CM_SC_KW				= "";		//search keyword
	//Applet이 포함되어있는 체크하기위한 변수
	public static boolean	FI_CHK = false;
	public static String	m_szAlign = ""; //임시

	static AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExtConfig.class);
	static ExternalFileEx ext = ctx.getBean("extConf",ExternalFileEx.class);

	
	/**
	 * 따옴표(quote) 태그를 Entity 코드로 변환
	 */
	public static String replaceQuotes(String str) {
		StringBuffer rtStr = new StringBuffer("");
		if (str == null || str.equals("")) { return ""; }
		ExpisCommonUtile ecu = ExpisCommonUtile.getInstance();
		try {
			for (int i=0; i<str.length(); i++) {
				char ch = str.charAt(i);
				//2022 02 16 Park.J.S : 특정 기종만 "를 특수 문자로 치환
				if (ch == CHAR.CHAR_QUOT.charAt(0) &&(ecu.checkQUOTReplace())) {
					rtStr.append(CODE.ENM_QUOT);
				} else {
					rtStr.append(ch);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtStr.toString();
	}
	
	
	/**
	 * 원데이터의 XML Entity 코드를 HTML Tag 로 변환
	 */
	public static String convertEntityToTag(String str) {
		
		String rtStr = "";
		if (str == null || str.equals("")) { return ""; }
		
		try {
			str = StringUtil.replace(str, CHAR.CHAR_AMP, CODE.ENM_AMP);
			str = StringUtil.replace(str, CHAR.CHAR_APOS, CODE.ENM_APOS);
			str = StringUtil.replace(str, CHAR.CHAR_QUOT, CODE.ENM_QUOT);
			str = StringUtil.replace(str, CHAR.CHAR_LT, CODE.ENM_LT);
			str = StringUtil.replace(str, CHAR.CHAR_GT, CODE.ENM_GT);
			rtStr = str;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rtStr;
	}
	
	
	/**
	 * HTML Tag 를 XML Entity 코드로 변환
	 */
	public static String convertTagToEntity(String str) {
		
		String rtStr = "";
		if (str == null || str.equals("")) { return ""; }
		
		try {
			str = StringUtil.replace(str, CODE.ENM_AMP, CHAR.CHAR_AMP);
			str = StringUtil.replace(str, CODE.ENM_APOS, CHAR.CHAR_APOS);
			str = StringUtil.replace(str, CODE.ENM_QUOT, CHAR.CHAR_QUOT);
			str = StringUtil.replace(str, CODE.ENM_LT, CHAR.CHAR_LT);
			str = StringUtil.replace(str, CODE.ENM_GT, CHAR.CHAR_GT);
			//str = StringUtil.replace(str, CHAR.MARK_DOT_1, CHAR.MARK_DOT_2);
			str = StringUtil.replace(str, CHAR.MARK_DOT_ORG, CHAR.MARK_DOT);
			rtStr = str;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rtStr;
	}
	
	
	/**
	 * 문자열에서 코드부호 제거
	 */
	public static String deleteCode(String str, int delType) {
		
		String rtStr = "";
		if (str == null || str.equals("")) { return ""; }
		
		try {
			str = convertTagToEntity(str);
			
			boolean isStart = false;
			int nlen = str.length();
			for (int i=0; i<nlen; i++) {
				char ch1 = str.charAt(i);
				
				if (delType == 1) {
					if (ch1 == 0x0A || ch1 == 0x09) {
						continue;
					} else {
						String tmp = str.substring(i, i+1);
						rtStr += tmp;
					}
				} else {
					char ch2 = 0x00;
					if (i+1 < nlen) {
						ch2 = str.charAt(i+1);
					}
					if (i+1 < nlen && ch1 == CODE.SYMBOL_AMP && ch2 == CODE.SYMBOL_SHARP) {
						isStart = true;
						i++;
					} else if (isStart == true && ch1 != CODE.SYMBOL_SEMI) {
						//none
					} else if (isStart == true && ch1 == CODE.SYMBOL_SEMI) {
						isStart = false;
					} else {
						if (ch1 == 0x0A || ch1 == 0x09) {
							continue;
						} else {
							String tmp = str.substring(i, i+1);
							rtStr += tmp;
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rtStr;
	}
	
	public static String deleteAllCode(String str) {
		
		return deleteCode(str, 0);
	}
	
	public static String deleteALinkCode(String str) {
		
		return deleteCode(str, 1);
	}
	
	
	/**
	 * 문자열에서 특정 부호(Mark) 위치한 첫번째 위치 값 추출 
	 */
	public static int getMarkIndex(String str, String compMark, int maxLen) {
		
	    int rtnIdx = -1;
	    
	    try {
	    	int nIdx = -1;
	    	String mark[] = compMark.split(",");
			if (mark.length > 0) {
				for (int i=0; i<mark.length; i++) {
					nIdx = str.indexOf(mark[i]);
					if (nIdx > -1 && nIdx < maxLen) {
						rtnIdx = nIdx;
					    break;
					}
				}
			}
			
		}  catch (Exception e) {
			e.printStackTrace();
		}
	    
		return rtnIdx;
	}
	
	public static String getCodeConverter(ParserDto psDto, String dataStr, String argDiv, String argEnd) {
		String rtStr = new String("");
	    if (dataStr == null || dataStr.equals("")) { return ""; }
	    
	    try {
	    	
	    	/**
	    	 * 검색어 빨간색 반전효과 주기 위한 문자열 대체
	    	 * 20170114 edit 검색시에 AND/OR 다중검색 가능하도록 추가 구현 
	    	 */
	    	if (!psDto.getSearchWord().equals("")) {
	    		//str = getSearchKeyword(str, psDto.getSearchWord());
	    		String[] arrWord = psDto.getSearchWord().split(IConstants.SEARCH_WORD_MARK);
				if (arrWord.length > 0) {
					for (int i=0; i<arrWord.length; i++) {
						dataStr = getSearchKeyword(dataStr, arrWord[i]);
					}
				}
	    	}
	    	String tagNum = new String("");
	    	String tagName = new String("");
	    	List<String> tagNameArry = new ArrayList<>();
	    	
	    	boolean isStart			= false;	//시작 여부 (&#으로 시작 여부)
	    	boolean isTagStart		= false;	//태그 시작 여부-텍스트 내 스타일 코드 수동 지정한 것 (&#254로 시작 여부)
	    	boolean isAlignStart	= false;	//align 태그 시작 여부 (&#254;&#138 로 시작 여부)
	    	boolean isFontStart		= false;	//font 태그 시작 여부 (&#254;&#169, 188, 199 로 시작 여부)
	    	boolean isSubStart		= false;	//첨자 태그 시작 여부 (&#254;&#136 로 시작 여부)
	    	boolean isSub			= false;	//아랫첨자
			boolean isSup			= false;	//윗첨자
			
			int nTagCntB	= 0;	//<b> bold 태그 갯수	
			int nTagCntI	= 0;	//<i> italric 체 태그 갯수
			int nTagCntU	= 0;	//<u> underline 태그 갯수
			
			log.info("befor dataStr : "+dataStr);
			dataStr = convertTagToEntity(dataStr);
			log.info("after dataStr : "+dataStr);
			//문자열 마지막에 줄바꿈이 있을 경우 제거
			if (dataStr.length() > 5) {
				int len = dataStr.length();
				String tmp = dataStr.substring(len-5, len);
				if (tmp.equals(CODE.ECD_NLINE)) {
				}
			}
			int nCnt = 0;
			String firstMark = CHAR.MARK_DOT + "&nbsp;"; //CODE.ECD_MARK
			if (dataStr.charAt(0) == 0x22) {
				ExpisCommonUtile ecu = ExpisCommonUtile.getInstance();
				if(ecu.checkQUOTReplace()) {
					rtStr += firstMark;
					nCnt++;
				}else {
				}
			} else if (dataStr.indexOf(CODE.ENM_QUOT) == 0) {
				rtStr += firstMark;
				nCnt = 6;
			} else if (dataStr.indexOf(CHAR.TAG_START + "span") == 0
					&& dataStr.indexOf(CHAR.CHAR_QUOT) > 40 && dataStr.indexOf(CHAR.CHAR_QUOT) < 50) {
				log.info("Exception Check 비정상적인 하드코딩 해당 코드 존재 이유 모름 주석처리 2022 09 22 Park.J.S.: "+dataStr);

			} else {
				//머리기호 앞에 텍스트 정렬에 대한 코드값 있을 경우 처리
				int idx = dataStr.indexOf(CHAR.CHAR_QUOT);
				//if (idx > 0) {
				if (idx > -1) { //joe추가
					String tmp = dataStr.substring(0, idx);
					if (deleteAllCode(tmp).equals("")) {
						if (tmp.indexOf(CODE.ECD_NLINE) > -1) {
							rtStr += CSS.BR;
						}
						rtStr += firstMark;
						nCnt = idx + 1;
					}
				}
			}
			
			boolean isDspChar = false;
			if ("LSAM".equalsIgnoreCase(ext.getBizCode()) || "NLS".equalsIgnoreCase(ext.getBizCode()) ||  "KBOB".equalsIgnoreCase(ext.getBizCode()) ||  "KICC".equalsIgnoreCase(ext.getBizCode())) {
				isDspChar = true;
			}
			if ("MUAV".equalsIgnoreCase(ext.getBizCode())) {
				isDspChar = true;
			}
			if ("SENSOR".equalsIgnoreCase(ext.getBizCode())) {
				isDspChar = true;
			}
			
			//문자열 한자씩 비교&대체
			int nLen = dataStr.length();
			for (int i=nCnt; i<nLen; i++) {
				char ch1 = dataStr.charAt(i);
				char ch2 = 0x00;
				if ((i+1) < nLen) {
					ch2 = dataStr.charAt(i+1);
				}
				
				//'&#' 일 경우
				if (i+1 < nLen && ch1 == CODE.SYMBOL_AMP && ch2 == CODE.SYMBOL_SHARP) {
					isStart = true;
					i++;
					tagNum = "";
				} else if (isStart && ch1 == 'r' && ch2 == 'e' && dataStr.charAt(i+2) == 'g' && dataStr.charAt(i+3) == 'r') {
					//'regr' 일 경우
					rtStr += CODE.ECD_REG;
					i += 4;
					isStart = false;
				}
				else if (i+1 < nLen && ch1 == CODE.SYMBOL_CIRCLE_START && isDspChar ) {
					isStart = true;
					tagNum = "";
				} else if (isStart && ch1 == CODE.SYMBOL_CIRCLE_END && isDspChar ) {
					isStart = false;
					
					if (StringUtil.isCheckNumber(tagNum) == true) {
						int nTagNum = StringUtil.getInt(tagNum);
						if (nTagNum > 0 && nTagNum < 999) {
							rtStr += CSS.circleClass(tagNum);
						} else {
							rtStr += CODE.SYMBOL_CIRCLE_START + tagNum + CODE.SYMBOL_CIRCLE_END;
						}
					} else {
						rtStr += CODE.SYMBOL_CIRCLE_START + tagNum + CODE.SYMBOL_CIRCLE_END;
					}
				} else if (isStart && ch1 != CODE.SYMBOL_SEMI) {
					//'&#'과 ';' 사이의 문자일 경우
					tagNum += "" + ch1;
				} else if (isStart && ch1 == CODE.SYMBOL_SEMI) {
					/*
					 * ';' 일 경우
					 * 7		: 머리기호 (20140825 추가)
					 * 13	: 줄바꿈
					 * 14	: 전각문자 - 전각문자일 경우 스페이스로 변환
					 * 32	: 공백
					 * 5000, 6000	: 폰트컬러(검색어) 시작/종료
					 * 10000이상		: 아이콘 
					 */
					isStart = false;
					if(tagNum != "" && "X00AE".equalsIgnoreCase(tagNum)) {
						rtStr += CODE.ECD_REG;
						continue;
					}
					int nTagNum = Integer.parseInt(tagNum);
					if (nTagNum == 7) {
						rtStr += CODE.ECD_MARK;
					} else if (nTagNum == 14) {
						rtStr += CODE.ENM_SPACE + CODE.ENM_SPACE + CODE.ENM_SPACE;
					} else if (nTagNum == 32) {
						rtStr += CODE.ENM_SPACE;
					} else if (nTagNum == 5000) {
						rtStr += CSS.FONT_CC;
					} else if (nTagNum == 6000) {
						rtStr += CSS.FONT_END;
						
					} else if (nTagNum > 10000) {
						rtStr += IconParser.getIconHtml(psDto, nTagNum+"");
						
					} else if (nTagNum == 13) {
						if (argDiv.equals("")) {
							rtStr += CSS.BR;
						} else {
							if (isFontStart) {
								log.info("tagNameArry.get(A) : "+tagNameArry.get(0)+", "+tagNameArry.size());
								rtStr += CHAR.TAG_SL_START + tagNameArry.get(0) + CHAR.TAG_END;
								tagNameArry = tagNameArry.subList(1, tagNameArry.size());
								if(tagNameArry.size() == 0) {
									isFontStart = false;
								}
								log.info("tagNameArry.get(B) : "+tagNameArry.size());
							}
							if (!argDiv.equals("")) {
								rtStr += argEnd;
								rtStr += argDiv;
							}
						}
						if (i+2 < nLen && ch2 == 0x22) {
							rtStr += firstMark;
							i++;
						}
						
					} else {
						/*
						 * 254, 255	: 텍스트 내 스타일 코드 수동 지정한 것 시작/종료
						 * 252		: 형상구분 관련 (미사용)
						 * 136		: 첨자
						 * -300		: 윗첨자
						 * 200		: 아랫첨자
						 * 138		: 정렬
						 * 2, 4, 1	: 정렬 (center, left, right)
						 * 169		: B 볼드
						 * 188		: I 이탤릭체
						 * 199		: U 밑줄
						 * 0		: 공통 - 종료시에
						 */
						if (nTagNum == 254) { 
							isTagStart = true;
						} else if (isTagStart && nTagNum == 255) {
							isTagStart = false;
						} else if (isTagStart && nTagNum == 136) {
							isSubStart = true;
						} else if (isTagStart && nTagNum == 0 && isSubStart == true) {
							isSubStart = false;
							if (isSub) {
								rtStr += CSS.SPAN_END + CSS.SUB_END;
								isSub=false;
							}
							if (isSup) {
								isSup = false;
								rtStr += CSS.SPAN_END + CSS.SUP_END;
							}	
						} else if (isTagStart && isSubStart && nTagNum == -300) {
							if (isSub) {
								rtStr += CSS.SUB_END;
							}
							if (isSup) {
								rtStr += CSS.SUP_END;
							}
							isSup = true;
							rtStr += CSS.SUP + CSS.SPAN_CC;
						} else if (isTagStart && isSubStart && nTagNum == 200) {
							if (isSub) {
								rtStr += CSS.SUB_END;
							}
							if (isSup) {
								rtStr += CSS.SUP_END;
							}
							isSub = true;
							rtStr += CSS.SUB + CSS.SPAN_CC;
						} else if (isTagStart && nTagNum == 138) {
							isAlignStart = true;
						} else if (!isFontStart && isTagStart && (nTagNum == 169 || nTagNum == 188 || nTagNum == 199)) {
							isFontStart = true;
							if (nTagNum == 169) {
								tagName = CHAR.NAME_B;
								tagNameArry.add(CHAR.NAME_B);
								nTagCntB++;
							} else if (nTagNum == 188) {
								tagName = CHAR.NAME_I;
								tagNameArry.add(CHAR.NAME_I);
								nTagCntI++;
							} else if (nTagNum == 199) {
								tagName = CHAR.NAME_U;
								tagNameArry.add(CHAR.NAME_U);
								nTagCntU++;
							}
						} else if (tagNameArry.size() > 0 && isFontStart && isTagStart && (nTagNum == 169 || nTagNum == 188 || nTagNum == 199)) {
							isFontStart = true;
							if (nTagNum == 169) {
								tagName = CHAR.NAME_B;
								tagNameArry.add(CHAR.NAME_B);
								nTagCntB++;
							} else if (nTagNum == 188) {
								tagName = CHAR.NAME_I;
								tagNameArry.add(CHAR.NAME_I);
								nTagCntI++;
							} else if (nTagNum == 199) {
								tagName = CHAR.NAME_U;
								tagNameArry.add(CHAR.NAME_U);
								nTagCntU++;
							}
						} else if (isTagStart && isAlignStart && nTagNum == 2) {
							CM_STYLE_ALIGN = CSS.STY_AL_CENTER;
							isAlignStart = false;
						} else if (!isFontStart && isTagStart && isAlignStart && nTagNum == 4) {
							CM_STYLE_ALIGN = CSS.STY_AL_LEFT;
							isAlignStart = false;
						} else if (!isFontStart && isTagStart && isAlignStart && nTagNum == 1) {
							CM_STYLE_ALIGN = CSS.STY_AL_RIGHT;
							isAlignStart = false;
						} else if (!isFontStart && isTagStart && isAlignStart && nTagNum == 0) {
							CM_STYLE_ALIGN = CSS.STY_AL_LEFT;
							isAlignStart = false;
						} else if (isTagStart && isFontStart && nTagNum == 1) {
							log.info("tagNameArry.get("+tagNameArry.size()+") : "+tagNameArry.get(tagNameArry.size()-1));
							rtStr += CHAR.TAG_START + tagNameArry.get(tagNameArry.size()-1) + CHAR.TAG_END;
						} else if (isTagStart && isFontStart && nTagNum == 0) {
							if (tagName.equals(CHAR.NAME_B)) {
								nTagCntB--;
							} else if (tagName.equals(CHAR.NAME_I)) {
								nTagCntI--;
							} else if (tagName.equals(CHAR.NAME_U)) {
								nTagCntU--;
							} else {
								log.info("Tag Else isTagStart : "+isTagStart+", isFontStart : "+isFontStart+", nTagNum : "+nTagNum);
							}
								
							rtStr+= CHAR.TAG_SL_START + tagNameArry.get(0) + CHAR.TAG_END;
							tagNameArry = tagNameArry.subList(1, tagNameArry.size());
							isFontStart = false;
							tagName = "";
						}
					}
				} else {
					if (ch1 == 0x0A) {
						log.info("Check continue Call : "+rtStr);
						continue;
					} else {
						rtStr += dataStr.substring(i, i+1);
					}
				}
			}
			if ((nTagCntB % 2) == 1) {
				rtStr += CHAR.TAG_SL_START + CHAR.NAME_B + CHAR.TAG_END;
			}
			if ((nTagCntI % 2) == 1) {
				rtStr += CHAR.TAG_SL_START + CHAR.NAME_I + CHAR.TAG_END;
			}
			if ((nTagCntU % 2) == 1) {
				rtStr += CHAR.TAG_SL_START + CHAR.NAME_U + CHAR.TAG_END;
			}
			if (isFontStart) {
				if (!tagName.equals(CHAR.NAME_B)
						&& !tagName.equals(CHAR.NAME_I) && !tagName.equals(CHAR.NAME_U)) {
					rtStr += CHAR.TAG_SL_START + tagName + CHAR.TAG_END;
				}
			}

			if (isSub) {
				rtStr += CSS.SUB_END;
			}
			if (isSup) {
				rtStr += CSS.SUP_END;
			}
		}  catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}

		return rtStr;
	}
	
	
	/**
	 * 문자열에서 특정 검색어를 다른 문자열로 대체 변환
	 * <a> 링크로 설정된 부분은 안에 텍스트만 대체하도록 분기
	 */
	public static String getSearchKeyword(String dataStr, String scWord) {
		
	    String rtStr = "";
	    if (dataStr == null || dataStr.equals("")) { return ""; }
	    
	    String lowStr		= "";
	    String tempText	= "";
	    boolean isBreak	= false;
	    int start	= 0;
	    int len		= 0;
	    int idx1		= -1;
	    int idx2		= -1;
	    
	    try {
	    	len = dataStr.length();
	    	lowStr = dataStr.toLowerCase();
	    	scWord = scWord.toLowerCase();
	    	
	    	while (start < len) {
	    		idx1 = lowStr.indexOf(CHAR.TAG_START + "a", start);
	    		if (idx1 > -1) {
	    			idx2 = lowStr.indexOf(CHAR.TAG_END, idx1+1);
	    			if (idx2 > -1) {
	    				tempText = dataStr.substring(start, idx1);
	    				rtStr += getSearchKeywordStyle(tempText, scWord);
	    				rtStr += dataStr.substring(idx1, idx2);
	    				start = idx2;
	    			}
	    		} else {
	    			if (start == 0) {
	    				rtStr = getSearchKeywordStyle(dataStr, scWord);
	    				isBreak = true;
	    			}
	    			break;
	    		}
	    	}
	    	
	    	if (isBreak == false && start < len) {
	    		tempText = dataStr.substring(start, len);
	    		rtStr += getSearchKeywordStyle(tempText, scWord);
	    	}
	    	
		}  catch (Exception e) {
			e.printStackTrace();
		}
	    
		return rtStr;
	}
	
	
	/**
	 * 문자열에서 특정 검색어를 반전 효과 주도록 스타일 코드로 대체 변환
	 */
	public static String getSearchKeywordStyle(String dataStr, String scWord) {
		
	    String rtStr = "";
	    if (dataStr == null || dataStr.equals("")) { return ""; }
	    
	    String lowStr = "";
	    int start	= 0;
	    int len		= 0;
	    int idx1		= -1;
	    int idx2		= -1;
	    
	    try {
	    	len = dataStr.length();
	    	lowStr = dataStr.toLowerCase();
	    	scWord = scWord.toLowerCase();
	    	
	    	while (start < len) {
	    		idx1 = lowStr.indexOf(scWord, start);
	    		if (idx1 > -1) {
	    			int[] arrTag = getSearchTag(lowStr, scWord, idx1, start);
	    			
	    			if (arrTag[0] == 1) {
	    				rtStr += dataStr.substring(start, arrTag[1]);
	    				start = arrTag[1];
	    			} else {
	    				idx2 = idx1+scWord.length();
	    				rtStr += dataStr.substring(start, idx1);
	    				rtStr += CODE.ECD_KW_START;
	    				rtStr += dataStr.substring(idx1, idx2);
	    				rtStr += CODE.ECD_KW_END;
	    				start = idx2;
	    			}
	    		} else {
	    			break;
	    		}
	    	}
	    	
	    	if (start < len) {
	    		rtStr += dataStr.substring(start, len);
	    	}
	    	
		}  catch (Exception e) {
			e.printStackTrace();
		}
	    
		return rtStr;
	}
	
	
	/**
	 * 검색어가 영문1자리로 검색시 본문에 ID값이나 태그를 검색하는 것 막도록 처리
	 */
	public static int[] getSearchTag(String str, String argWord, int argIdx, int argStart) {
		
		int[] rtNum = {0, 0};
	    if (str == null || str.equals("")) { return null; }
	    
	    try {
	    	String tmpEntity = "'" + CHAR.TAG_END;
	    	int idxFirstS	= str.indexOf(CHAR.TAG_START + "span id='", argStart);
			int idxFirstE	= str.indexOf(tmpEntity, argStart);
			int idxLastS	= str.lastIndexOf(CHAR.TAG_START + "span id='", argStart);
			int idxLastE	= str.indexOf(tmpEntity, argStart);
			int idxAllS		= str.indexOf(CHAR.TAG_START + argWord + CHAR.TAG_END);
			int idxAllE		= str.indexOf(CHAR.TAG_SL_START + argWord + CHAR.TAG_END);
			
			if (str.indexOf(tmpEntity, idxFirstE+1) > -1) {
				idxFirstE = str.indexOf(tmpEntity, idxFirstE+1);
			} else if (str.indexOf(tmpEntity, idxFirstE) > -1) {
				idxFirstE = str.indexOf(tmpEntity, idxFirstE);
			}
			
			if (idxFirstS > -1 && argIdx >= idxFirstS && argIdx <= idxFirstE) {
				rtNum[0] = 1;
				rtNum[1] = idxFirstE+2;
			}
			if (idxLastS > -1 && argIdx >= idxLastS && argIdx <= idxLastE) {
				rtNum[0] = 1;
				rtNum[1] = idxLastE+2;
			}
			if (idxAllS > -1 && argIdx == (idxAllS+1)) {
				rtNum[0] = 1;
				rtNum[1] = idxAllS+argWord.length()+2;
			}
			if (idxAllE > -1 && argIdx == (idxAllE+2)) {
				rtNum[0] = 1;
				rtNum[1] = idxAllE+argWord.length()+3;
			}
			
			//태그 코드(&#200;) 가 검색어로 추출시 제거
			if (argWord.length() <= 3 && argIdx > 3) {
				String tmp = str.substring(argIdx-3, argIdx-1);
				if (str.length() > argIdx+argWord.length()+1) {
					String tmp2 = str.substring(argIdx+argWord.length(), argIdx+argWord.length()+1);
					if (tmp.equals(CODE.SYMBOL_AMP + CODE.SYMBOL_SHARP)
							&& tmp2.equals(CODE.SYMBOL_SEMI)) {
						rtNum[0] = 1;
						rtNum[1] = argIdx + argWord.length() + 2;
					}
				}
			}
			
	    } catch (Exception e) {
	    	e.printStackTrace();
		}
	    
	    return rtNum;
	}
	
	
	/**
	 * IPB교범이나 WC교범의 작업카드(WC)에도 검색어 반전 효과 구현
	 */
	public static String getSearchKeywordIPBWC(String dataStr, String scWord) {
		
		if (dataStr == null || scWord == null) {
			return null;
		}
		
		String rtStr = "";
		
		try {
			if (!scWord.equals("")) {
				String[] arrWord = scWord.split(IConstants.SEARCH_WORD_MARK);
				if (arrWord.length > 0) {
					for (int i=0; i<arrWord.length; i++) {
						
						String orgWord	= arrWord[i];
						String newWord	= CSS.FONT_CC + orgWord + CSS.FONT_END;
						dataStr	= StringUtil.replace(dataStr, orgWord, newWord);
						
					}
				}
				rtStr = dataStr;
			} else {
				rtStr = dataStr;
			}
		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return rtStr;
	}

}
