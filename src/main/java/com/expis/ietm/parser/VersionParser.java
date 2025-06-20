package com.expis.ietm.parser;

import com.expis.common.ExpisCommonUtile;
import com.expis.common.IConstants;
import com.expis.common.ext.ExtConfig;
import com.expis.common.ext.ExternalFileEx;
import com.expis.domparser.*;
import com.expis.ietm.collector.ContCollector;
import com.expis.ietm.collector.TreeCollector;
import com.expis.ietm.collector.VersionCollector;
import com.expis.ietm.dto.XContDto;
import com.expis.manage.dto.TreeXContDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * [공통모듈]버전정보(Version)/형상정보(Vehicle Type) Parser Class
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VersionParser {

//	private final ContParser contParser;
//	private final AlertParser alertParser;
//	private final GrphParser grphParser;
//	private final TableParser tableParser;
//	private final TextParser textParser;
//	private final InputParser inputParser;
//	private final IPBParser ipbParser;
//	private final FaultInfoParser fiParser;
//	private final WorkcardParser wcParser;
//	private final VersionCollector versionCollector;
//	private final IconParser iconParser;
//	private final TreeCollector treeCollector;
//	private final ContCollector contCollector;
	private final ObjectProvider<ContParser> contParser;
	private final ObjectProvider<AlertParser> alertParser;
	private final ObjectProvider<GrphParser> grphParser;
	private final ObjectProvider<TableParser> tableParser;
	private final ObjectProvider<TextParser> textParser;
	private final ObjectProvider<InputParser> inputParser;
	private final ObjectProvider<IPBParser> ipbParser;
	private final ObjectProvider<FaultInfoParser> fiParser;
	private final ObjectProvider<WorkcardParser> wcParser;
	private final ObjectProvider<VersionCollector> versionCollector;
	private final ObjectProvider<IconParser> iconParser;
	private final ObjectProvider<TreeCollector> treeCollector;
	private final ObjectProvider<ContCollector> contCollector;

	AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExtConfig.class);
	ExternalFileEx ext = ctx.getBean("extConf", ExternalFileEx.class);

	/**
	 * 각 컨텐츠 엘리번트에 버전 정보 있는지 검사해서 변경바 생성하는 메소드
	 */
	public StringBuffer checkVersionHtml(ParserDto psDto, Node paNode) {
		
		StringBuffer rtSB = new StringBuffer();
		
		try {
			if (paNode == null || paNode.getNodeType() != Node.ELEMENT_NODE) {
				return rtSB;
			}
			
			StringBuffer nodeSB	= new StringBuffer();
			String styleName	= "";
			
			NamedNodeMap paAttr = paNode.getAttributes();
			Node parentpaAttr= paNode.getParentNode();
			NamedNodeMap paAttrLevel = parentpaAttr.getAttributes();
			
			String toKey		= psDto.getToKey();
			String tocoId		= psDto.getTocoId();
			String contId		= XmlDomParser.getAttributes(paAttr, ATTR.SYS_ID);
			String versionId	= XmlDomParser.getAttributes(paAttr, ATTR.SYS_TOCO_VERID);
			String statusCd		= XmlDomParser.getAttributes(paAttr, ATTR.SYS_TOCO_STATUSCD);
			String tpStatusCd	= XmlDomParser.getAttributes(paAttr, ATTR.SYS_TOCO_TP_STATUSCD);
			String parentLevel = XmlDomParser.getAttributes(paAttrLevel, ATTR.SYS_TOCO_PARENTLEVEL);
			String level = XmlDomParser.getAttributes(paAttr, ATTR.SYS_TOCO_lEVEL);
			String tpChangebasis= XmlDomParser.getAttributes(paAttr, "changebasis");
			log.info("statusCd : "+statusCd+", versionId : "+versionId+", tpStatusCd : "+tpStatusCd+", tpChangebasis : "+tpChangebasis+", contId : "+contId+", tocoId : "+tocoId);
			log.info("Versiontion Check Node : "+ ExpisCommonUtile.getInstance().nodeToString(paNode));
			
			//2024.11.14 - [BLOCK2] IPB 교범에서 품목번호 0번에 변경바가 표시되지 않도록 수정  - JSH
			if(parentLevel == "" && !level.isEmpty() && "BLOCK2".equalsIgnoreCase(ext.getBizCode())) {
				statusCd = "";
			}
			
			
			if (tpStatusCd != null && tpStatusCd.equals("1")) {// TP(보충판) 변경 바 : 초록색
				styleName = IConstants.VER_STATUS_TP;
			} else if(statusCd.equals(VAL.CONT_STATUS_APPEND) && !versionId.equals("") || statusCd.equals(VAL.CONT_STATUS_UPDATE)) {
				styleName = IConstants.VER_STATUS_UPDATE;
			} else {
				log.info("Version ELSE");
				statusCd = "";
			}

			log.info("psDto.getLastVersionId() : "+psDto.getLastVersionId()+", versionId : "+versionId+", psDto.getToKey() : "+psDto.getToKey());
			// 버전아이디가 존재하는데 교범의 버전아이디가 존재 하지 않을경우 교범의 버전 아이디를 불러오는 로직 추가
			if((psDto.getLastVersionId() == null || psDto.getLastVersionId().equals("")) && (versionId != null && !versionId.equals(""))) {
				log.info("LastVersionId ReCheck Call");
				String lastVerId = versionCollector.getObject().getLastVersionId(psDto);
				log.info("lastVerId : "+lastVerId);
				psDto.setLastVersionId(lastVerId);
			}
			//일반 변경바인지, 최신(마지막) 변경바인지 구분화여 Style class 명 지정
			if (!psDto.getLastVersionId().equals("") && psDto.getLastVersionId().equals(versionId)) {
				styleName = IConstants.VER_STATUS_LATEST;
			}
			log.info("Version Check styleName : "+styleName+", versionId : "+versionId+", psDto.getLastVersionId() : "+psDto.getLastVersionId()+", paNode : "+paNode.getNodeName());
			if (!statusCd.equals("")) {
				nodeSB.append( CSS.getDivVersion(toKey, tocoId, contId, versionId, statusCd, styleName, tpChangebasis) );
			}
			
			rtSB.append(nodeSB);
			log.info("checkVersionHtml["+versionId+"] nodeSB : "+nodeSB);
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("VersionParser.checkVersionHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
	/**
	 * 준비사항 - 필수교환품목 및 소모성물자 테이블 버전 체크
	 */
	public StringBuffer consumVersionHtml(ParserDto psDto, Node paNode, String contId, String versionId, String versionStatus, String changebasis) {
		StringBuffer rtSB = new StringBuffer();
		
		try {
			if (paNode == null || paNode.getNodeType() != Node.ELEMENT_NODE) {
				return rtSB;
			}
			
			StringBuffer nodeSB	= new StringBuffer();
			String styleName	= "";
			
			String toKey		= psDto.getToKey();
			String tocoId		= psDto.getTocoId();
			NamedNodeMap paAttr = paNode.getAttributes();
			String tpStatusCd	= XmlDomParser.getAttributes(paAttr, ATTR.SYS_TOCO_TP_STATUSCD);
			log.info("versionStatus : "+versionStatus+", versionId : "+versionId+", tpStatusCd : "+tpStatusCd);
			//추가인지, 수정인지 해당 스크립트 함수명 지정
			if (versionStatus.equals(VAL.CONT_STATUS_APPEND) && !versionId.equals("")) {
				if (tpStatusCd != null && tpStatusCd.equals("1")) {// TP(보충판) 변경 바 : 초록색 
					log.info("Version TP");
					styleName = IConstants.VER_STATUS_TP;
				}else {
					log.info("Version APPEND");
					styleName = IConstants.VER_STATUS_UPDATE;
				}
			} else if (versionStatus.equals(VAL.CONT_STATUS_UPDATE)) {
				styleName = IConstants.VER_STATUS_UPDATE;
			} else {
				versionStatus = "";
			}
			log.info("psDto.getLastVersionId() : "+psDto.getLastVersionId()+", versionId : "+versionId+", psDto.getToKey() : "+psDto.getToKey());
			// 버전아이디가 존재하는데 교범의 버전아이디가 존재 하지 않을경우 교범의 버전 아이디를 불러오는 로직 추가
			if((psDto.getLastVersionId() == null || psDto.getLastVersionId().equals("")) && (versionId != null && !versionId.equals(""))) {
				log.info("LastVersionId ReCheck Call");
				String lastVerId = versionCollector.getObject().getLastVersionId(psDto);
				log.info("lastVerId : "+lastVerId);
				psDto.setLastVersionId(lastVerId);
			}
			// 일반 변경바인지, 최신(마지막) 변경바인지 구분화여 Style class 명 지정
			if (!psDto.getLastVersionId().equals("") && psDto.getLastVersionId().equals(versionId)) {
				styleName = IConstants.VER_STATUS_LATEST;
			}
			log.info("versionStatus : "+versionStatus);
			if (!versionStatus.equals("")) {
				nodeSB.append( CSS.getTdVersion(toKey, tocoId, contId, versionId, versionStatus, styleName, changebasis) );
			}
			
			rtSB.append(nodeSB);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("VersionParser.checkVersionHtml Exception:"+ex.toString());
		}
		return rtSB;
	}
	
	/**
	 * 각 컨텐츠 엘리번트에 버전 정보 있는지 검사해서 변경바 중 종료 태그 생성하는 메소드
	 */
	public String endVersionHtml(StringBuffer verSB) {
		
		String rtStr = "";
		
		if (verSB != null && verSB.toString().length() > 0) {
			rtStr = "</div>";
		}
		
		return rtStr;
	}
	
	
	/**
	 * 각 컨텐츠 엘리번트에 형상 정보가 해당하는지 검사해서 구분하는 메소드
	 */
	public boolean checkVehicleType(ParserDto psDto, Node paNode) {
		
		boolean rtBl	= false;
		
		try {
			if (paNode == null || paNode.getNodeType() != Node.ELEMENT_NODE) {
				log.info("return checkVehicleType");
				return rtBl;
			}
			
			String ssVehicleType = psDto.getVehicleType().toUpperCase();
			ssVehicleType = ssVehicleType.replace(VAL.VEHICLE_PRIFIX, "");
			
			NamedNodeMap paAttr = paNode.getAttributes();
			String tocoVehicleType	= XmlDomParser.getAttributes(paAttr, ATTR.VEHICLETYPE).toUpperCase();
			tocoVehicleType = tocoVehicleType.replace(VAL.VEHICLE_PRIFIX, "");
			if (tocoVehicleType.equals(VAL.VEHICLE_TYPE_NONE) || tocoVehicleType.equals("")) {
				// Type로 확인 하는 경우 있어서 추가
				String tocoType	= XmlDomParser.getAttributes(paAttr, "type").toUpperCase();
				log.info("ssVehicleType : "+ssVehicleType+", tocoVehicleType : "+tocoVehicleType+", tocoType : "+tocoType+", tocoType.replace(VAL.VEHICLE_PRIFIX, \"\") : "+tocoType.replace(VAL.VEHICLE_PRIFIX, ""));
				if (tocoVehicleType.equals(VAL.VEHICLE_TYPE_NONE)){
					rtBl = true;
				}else if (tocoVehicleType.equals("") && tocoType.indexOf("TYPE") > -1 && tocoType.replace(VAL.VEHICLE_PRIFIX, "").indexOf(ssVehicleType) < 0) {
					rtBl = false;
				}else {
					rtBl = true;
				}
			} else {
				if (tocoVehicleType.indexOf(ssVehicleType) > -1) {
					rtBl = true;
				} else {
					rtBl = false;
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("VersionParser.checkVehicleType Exception:"+ex.toString());
		}
		
		return rtBl;
	}
	
	
	/**
	 * 교범 내용(XML DOM) 중 버전 정보의 데이터를 파싱하여 문자열(HTML) 구성
	 */
	public StringBuffer getPageHtml(ParserDto psDto, Node paNode, int nSysDepth, int nDepth, String changebasis, XContDto contDto) {
		log.info(this.getClass().getName() + " : getPageHtml ==> "+changebasis);
		StringBuffer rtSB = new StringBuffer();
		StringBuffer quantity =  new StringBuffer();
		// 버전에 아이콘 있을경우 해당 아이콘을 가져오지 못하는 경우 있어서 아이콘 리스트 없는 경우 재차 확인하는 로직 추가 원래는 ConstParser 에서 설정 받고 오는게 정상적인 프로세스임
		if(psDto.getIconList() == null || psDto.getIconList().getLength() == 0) {
			// 아이콘 리스트 추출
			psDto.setIconList( iconParser.getObject().getIconList((Element) paNode) );
		}
		
		try {
			Node curNode		 	= null;
			NamedNodeMap curAttr	= null;
			String nodeName			= "";
			String sysType			= "";
			String sysId			= "";
			StringBuffer nodeSB		= new StringBuffer();
			boolean isIPB			= false;
			boolean isFI			= false;
			nSysDepth++;
			
			if(paNode != null) {
				NodeList curList = paNode.getChildNodes();
				if (curList.getLength() <= 0) {
					rtSB.append("");
					return rtSB;
				}
				
				
				for (int i=0; i<curList.getLength(); i++)
				{
					curNode = curList.item(i);
					nodeName = curNode.getNodeName();
					
					if (curNode.getNodeType() == Node.ELEMENT_NODE) {
						curAttr = curNode.getAttributes();
						
						// 준비사항 필수교환품목 및 소모성물자 변경내용
						if(nodeName.equals(DTD.IN_CONSUM)) {
							/* 2022 09 22 Park.J.S. 해당 코드관련 필요성 확인 안됨 주석처리
							quantity.append(XmlDomParser.getAttributes(curAttr, ATTR.IN_QUANTITY));
							quantity.append("&nbsp;&nbsp;&nbsp;&nbsp;");
							*/
						} else if (nodeName.equals(DTD.IN_PARTBASE)) {
							rtSB.append(XmlDomParser.getAttributes(curAttr, ATTR.IN_NAME));
							rtSB.append("&nbsp;&nbsp;&nbsp;&nbsp;");
							rtSB.append(XmlDomParser.getAttributes(curAttr, ATTR.IN_PARTNUM));
							rtSB.append("&nbsp;&nbsp;&nbsp;&nbsp;");
						}
						log.info("nodeName : "+nodeName);
						if (nodeName.equals(DTD.SYSTEM)) {
							sysType = XmlDomParser.getAttributes(curAttr, ATTR.TYPE);
							sysId = XmlDomParser.getAttributes(curAttr, ATTR.ID);
							log.info("sysType : "+sysType);
							if (sysType.equals(VAL.FI_DI_DESC)) {
								rtSB.append( fiParser.getObject().getDIHtml(psDto, curNode) );
								continue;
							} else if (sysType.equals(VAL.FI_LR_DESC)) {
								rtSB.append( fiParser.getObject().getDIHtml(psDto, curNode) );
								continue;
							} else if (sysType.equals(VAL.FI_DDS)) {
								rtSB.append( fiParser.getObject().getDIHtml(psDto, curNode) );
								continue;
							} else {
								nDepth = contParser.getObject().setTitleDepth(sysType);
								psDto.setTocoId(sysId); //memo 시현 위함
								if (nSysDepth == 1) {
									rtSB.append( this.getPageHtml(psDto, curNode, nSysDepth, nDepth, changebasis, contDto) );
								}
							}
							
						} else if (nodeName.equals(DTD.DESC)) {
							rtSB.append( contParser.getObject().getDescinfoHtml(psDto, curNode, nDepth) );
							
						} else if (nodeName.equals(DTD.TASK)) {
							rtSB.append( contParser.getObject().getTaskHtml(psDto, curNode, nDepth) );
							
						} else if (nodeName.equals(DTD.IPB_PARTINFO)) {
							//2022 09 22 Park.J.S. IPB의 경우 바로 사용하게 수정
							//nodeSB.append( ipbParser.getIPBHtml(psDto, curNode) );
							//isIPB = true;
							TreeXContDto treeDto = new TreeXContDto();
							treeDto.setRefToKey(psDto.getToKey());
							Element menuXml = (Element) treeCollector.getObject().getToTreeDom(treeDto);
							Node menuNode = XmlDomParser.getNodeFromXPathAPI(menuXml, "//system[@id='" + psDto.getTocoId() + "']");
							log.info("psDto.getTocoId() : "+psDto.getTocoId());
							//////////////////////////////////////////////////////////////////////////////////////////////
							//Step 3. Check IPB TYPE(USE ipbID)
							//////////////////////////////////////////////////////////////////////////////////////////////
							if(menuNode != null) {//2022 09 22 ADD : menuNode == null 이면 psDto.getTocoId() 이용하게 수정
								String ipbType = XmlDomParser.getAttributes(menuNode.getAttributes(), ATTR.TYPE);
								log.info("ipbType : "+ipbType);
								if("ipb2".equalsIgnoreCase(ipbType)) {//야전급 교범
									ipbParser.getObject().ipb2Flag = true;
								}else {//부대급 교범
									ipbParser.getObject().ipb2Flag = false;
								}
							}

							log.info(contDto.getToKey()+", "+contDto.getTocoId()+", "+ contDto.getContId());
							//psDto.setTocoType(IConstants.TOCO_TYPE_IPB);
							//IPB 본문과 대조비교위해 본문 내용 꺼냄
							XContDto tempConDto = new XContDto();
							tempConDto.setToKey(psDto.getToKey());
							tempConDto.setTocoId(psDto.getTocoId());
							tempConDto.setViewContKind(IConstants.VCONT_KIND_IPB);
							tempConDto.setOutputMode("02");//WC 아닌걸로 고정
							Element contElem = contCollector.getObject().getAllContDom(tempConDto);//IPB ELEMENT 호출
							NamedNodeMap ipbAttr = contElem.getAttributes(); 
							log.info("ipbAttr : "+ipbAttr);
							String refTocoId = XmlDomParser.getAttributes(ipbAttr, DTD.IPB_REFPARTINFO);
							log.info("getPageHtml refTocoId : "+refTocoId);
							
							//2024.03.13 - block2, TO목차에서 동일한 명칭을 가진경우 == p_d_refid 가 존재하는 경우 추가 - jingi.kim
							if ( "BLOCK2".equalsIgnoreCase(ext.getBizCode()) ) {
								if ( "".equalsIgnoreCase(refTocoId) && !DTD.IPB_PARTINFO.equalsIgnoreCase(contElem.getNodeName()) ) {
									NodeList childList = contElem.getChildNodes();
									
									for (int k=0; k<childList.getLength(); k++) {
										Node childNode = childList.item(k);
										if (childNode.getNodeType() == Node.ELEMENT_NODE) {
											if ( DTD.IPB_PARTINFO.equalsIgnoreCase(childNode.getNodeName()) ) {
												refTocoId = XmlDomParser.getAttributes(childNode.getAttributes(), DTD.IPB_REFPARTINFO);
												break;
											}
										}
									}
								}
							}
							
							if(refTocoId.equals("")) {//단일 객체
								log.info("contElem : "+ExpisCommonUtile.getInstance().nodeToString(contElem));
								Node contentNode = XmlDomParser.getNodeFromXPathAPI(contElem, "//partinfo[@id='" + contDto.getContId()+ "']");
								log.info("contentNode : "+ExpisCommonUtile.getInstance().nodeToString(contentNode));
								log.info("paNode      : "+ExpisCommonUtile.getInstance().nodeToString(paNode));
								rtSB.append( ipbParser.getObject().getIPBVersionTableHtml(psDto, paNode, contentNode));
							} else {//참조 객체가 존재할경우
								XContDto tempContDto = new XContDto();
								tempContDto.setOutputMode("02");
								tempContDto.setToKey(psDto.getToKey());
								tempContDto.setTocoId(refTocoId);
								//2024.03.13 - block2, TO목차에서 동일한 명칭을 가진경우 == p_d_refid 가 존재하는 경우 추가 - jingi.kim
								if ( "BLOCK2".equalsIgnoreCase(ext.getBizCode()) ) {
									Element refElem = contCollector.getObject().getAllContDom(tempContDto);
									Node refNode = XmlDomParser.getNodeFromXPathAPI(refElem, "//" +DTD.IPB_PARTINFO+ "[@id='" + contDto.getContId()+ "']");
									rtSB.append( ipbParser.getObject().getIPBVersionTableHtml(psDto, paNode, refNode));
								} else {
									Node refNode = contCollector.getObject().getAllContDom(tempContDto);
									rtSB.append( ipbParser.getObject().getIPBVersionTableHtml(psDto, paNode,null));
								}
							}
							
						} else if (nodeName.equals(DTD.FI_FAULTINF)) {
							rtSB.append( fiParser.getObject().getDDSFaultinfFIBHtml(psDto, curNode) );
							
						} else if (nodeName.equals(DTD.WC_WCS)) {
							wcParser.getObject().setWorkcardVariable(psDto,"");
							rtSB.append( wcParser.getObject().getWorkcardGroup(psDto, curNode) );
						} else if (nodeName.equals(DTD.WC_WORKCARD)) {
							wcParser.getObject().setWorkcardVariable(psDto,"");
							rtSB.append( wcParser.getObject().getWorkcardHtml(psDto, curNode) );
						} else if (nodeName.equals(DTD.PARA)) {
							rtSB.append( contParser.getObject().getParaHtml(psDto, curNode, nDepth, "") );
						} else if (nodeName.equals(DTD.PARASEQ)) {
							rtSB.append( contParser.getObject().getParaSeqHtml(psDto, curNode, nDepth, "") );
						} else if (nodeName.equals(DTD.STEP)) {
							rtSB.append( contParser.getObject().getStepHtml(psDto, curNode, nDepth) );
						} else if (nodeName.equals(DTD.STEPSEQ)) {
							rtSB.append( contParser.getObject().getStepSeqHtml(psDto, curNode, nDepth) );
						} else if (nodeName.equals(DTD.ALERT)) {
							rtSB.append( alertParser.getObject().getAlertHtml(psDto, curNode) );
						} else if (nodeName.equals(DTD.GRPH)) {
							//2021 06 true add jspark
							rtSB.append( grphParser.getObject().getGrphHtml(psDto, curNode, true) );
						} else if (nodeName.equals(DTD.TABLE)) {//2022 10 04 Park.J.S. FI Table 처리 추가
							if("FaultStepTableNode".equalsIgnoreCase(XmlDomParser.getAttributes(curNode.getAttributes(), "nodekind"))) {
								log.info("FaultStepTableNode Start");
								rtSB.append(fiParser.getObject().getFITableHtml(psDto, curNode));
								log.info("FaultStepTableNode Fin");
							}else {
								rtSB.append( tableParser.getObject().getTableHtml(psDto, curNode, nDepth, "") );
							}
						} else if (nodeName.equals(DTD.TB_ROWH)) {
							sysId = XmlDomParser.getAttributes(curAttr, ATTR.ID);
							//2022.03.03 Park.J.S. false Add
							rtSB.append( tableParser.getObject().getTrHtml(psDto, curNode, nDepth, sysId, false) );
						} else if (nodeName.equals(DTD.TB_ENTRY)) {
							sysId = XmlDomParser.getAttributes(curAttr, ATTR.ID);
							//2022.03.03 Park.J.S. false Add
							rtSB.append( tableParser.getObject().getTdHtml(psDto, curNode, nDepth, sysId, false) );
						} else if (nodeName.equals(DTD.TEXT)) {
							rtSB.append( textParser.getObject().getTextPara(psDto, curNode) );
						} else if (nodeName.equals(DTD.WC_WSTEP)) {//2022 12 29 Park.J.S. ADD : WC_WSTEP 처리 추가
							log.info("DTD.WC_WSTEP : "+DTD.WC_WSTEP);
							rtSB.append( wcParser.getObject().getWStepForVersionHtml(psDto, curNode) );
						} else {
							if (nSysDepth == 1) {
								//2024.02.29 - block2, FI - Step이 아닌 node에 변경 정보가 설정된 경우 추가 - jingi.kim
								if("node".equalsIgnoreCase(nodeName) && "BLOCK2".equalsIgnoreCase(ext.getBizCode())) {
									String stepNodeKind = XmlDomParser.getAttributes(curAttr, "nodekind");
									if ( "FaultTextNode".equalsIgnoreCase(stepNodeKind) || "FaultTitleNode".equalsIgnoreCase(stepNodeKind) ) {
										rtSB.append("<div class=\"fi_version_div\">");
										rtSB.append( this.getPageHtml(psDto, curNode, nSysDepth, nDepth, changebasis, contDto) );
										rtSB.append("</div>");
										continue;
									}
								}
								
								//2022 09 22 consum 추가 
								if("consum".equals(nodeName) && "BLOCK2".equalsIgnoreCase(ext.getBizCode())) {
									log.info("JG consum BLOCK2 전용 : "+ext.getBizCode());
									rtSB.append(inputParser.getObject().getVersionConsumHtml(psDto, curNode));
								}else if("othercond".equals(nodeName) && "BLOCK2".equalsIgnoreCase(ext.getBizCode())) {
									log.info("JG othercond BLOCK2 전용 : "+ext.getBizCode());
									rtSB.append(inputParser.getObject().getVersionOthercondHtml(psDto, curNode));
								}else {
									rtSB.append( this.getPageHtml(psDto, curNode, nSysDepth, nDepth, changebasis, contDto) );
								}
							}
						}

					}
					
				} //for end
				
			} else {	
				rtSB.append("");
				return rtSB;
			}
			
			
			if (isIPB == true) {
				rtSB = new StringBuffer();
				rtSB.append(nodeSB);
			}
			log.info("rtSB : "+rtSB.toString());
			rtSB.append(quantity);
			
			//2024.11.07 - BLOCK2, KTA, 불필요한 로직 실행안함 == CHAR.MARK_DOT 불필요함 - jingi.kim
			if ( "BLOCK2".equalsIgnoreCase(ext.getBizCode()) || "KTA".equalsIgnoreCase(ext.getBizCode()) ) {
				rtSB.insert(0, CSS.DIV_CONT);
				rtSB.append(CSS.DIV_END);
				return rtSB;
			}
			
			if (isIPB != true && isFI != true && nSysDepth == 1) {
				
				// 변경이력 팝업 시 변경정보에 특수문자 표시 적용

				Node childNode			= null;
				String childText		= "";
				String childnodeName	= "";
				boolean childflag		= false;
				
				NodeList childList = curNode.getChildNodes();
				
				for (int k=0; k<childList.getLength(); k++)
				{
					childNode = childList.item(k);
					nodeName = childNode.getNodeName();
					
					if (childNode.getNodeType() == Node.ELEMENT_NODE) {
						if (nodeName.equals(DTD.TEXT)) {
							childText = textParser.getObject().getTextInput(psDto, childNode).toString();
							//2022 12 22 Park.J.S. Update : 예외의 경우의 수가 나와서 수정 => 1.,2.,3. 처럼 숫자 앞에서는 점을 찍지 않음
							//2022 12 29 Park.J.S. Update :  StringIndexOutOfBoundsException 처리  rtSB.length() > 0
							if(!childText.equals("") && rtSB.length() > 0 && Character.isDigit(rtSB.charAt(0))) {
								log.info("This isDigit : "+rtSB.charAt(0));
								continue;
							}else {
								if (!childText.equals("") && rtSB.length() > 0 &&  !rtSB.substring(0, 1).equals("<")) {
									log.info("ADD CHAR.MARK_DOT");
									String verData = CSS.DIV_CONT + CHAR.MARK_DOT + CHAR.CHAR_SPACE;
									
									rtSB.insert(0, verData);
									rtSB.append(CSS.DIV_END);
									
									childflag = true;
								}
							}
						}
					}
					log.info("rtSB : "+rtSB.toString());
				}
				
				if(!childflag) {
					rtSB.insert(0, CSS.DIV_CONT);
					rtSB.append(CSS.DIV_END);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("ContParser.getPageHtml Exception:"+ex.toString());
		}
		
		return rtSB;
	}
	
}
