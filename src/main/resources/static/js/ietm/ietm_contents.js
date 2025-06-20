//
function selectTocoId(toKey, tocoId, folderChk, toType, vehicleExepFlag) {
	console.log("CALL selectTocoId");
	$(".loading").show();
	var vehicle = $("#vehicle_type").val();
	//2023 01 30 Park.J.S. ADD :  vehicle 처리 일경우 모든 경우의 수에 해당하는 T로 치환 처리
	if(vehicleExepFlag && "Y" == vehicleExepFlag){
		vehicle = "T";
	}
	staticStep = 0;

	$.ajax({
		type : "POST",
		url : $("#proectName").val()+"ietm/selectToco.do",
		data : "toKey=" + encodeURIComponent(toKey) + "&tocoId=" + tocoId + "&vehicle=" + vehicle + "&toType=" + toType,
		dataType : "json",
		success : function(data) {
			var vcKind = $("#vc_kind").val();
			var result = "";
			var rtList = data.rtList;
			var ipbChk = data.ipbChk;
			if(rtList.length == 0) {
				$("#main_cover").hide();
				$(".main").hide();
				$(".no_data_img").show();
				$(".loading").hide();
				return;
			}
			console.log("toKey=" + encodeURIComponent(toKey) + "&tocoId=" + tocoId + "&vehicle=" + vehicle + "&toType=" + toType);
			//현재 선택된 toco 타입
			var firstTocoType = data.rtList[0].tocoType;
			console.log("selectTocoId folderChk : "+folderChk+", firstTocoType : "+firstTocoType+", vcKind : "+vcKind+", rtList.length : "+rtList.length+", ipbChk : "+ipbChk);
			console.log("selectTocoId folderChk : "+folderChk+", firstTocoType : "+firstTocoType+", $(#tocoId) : "+$("#"+tocoId)+", $(#tocoId).attr(folderchk) : "+$("#"+tocoId).attr("folderchk")+", $(#view_mode).val() : "+$("#view_mode").val());
			if(folderChk == "Y" && firstTocoType != "IPB" && (vcKind != "03" && vcKind != "04")) {
				$("#main_empty_page").show();
				tocoDivMake(tocoId);
				tocoContView(tocoId, "single", folderChk);
				if(rtList.length == 2 && ipbChk == false) {
					tocoDivMake(rtList[1].tocoId);
					tocoContView(rtList[1].tocoId, "mult");
				} else {
					result = contMake(rtList, tocoId, folderChk, 0);
					$("#main_contents").append(result);
					//funtResize(parseInt($("#font_size").val()));
				}

			//2022 08 03 Park.J.S. ADD : 무제한 하위 메뉴 처리 위해 추가 list로 넘어왔으나 메뉴 구조상 폴더 일경우 하위 객체가 있다고 판단.
			//2023 02 07 Park.J.S. ADD : 프레임 보기 예외 처리 조건에 toKey.indexOf("JG") 추가
			} else if(folderChk == "list" && firstTocoType != "IPB" && $("#"+tocoId) && $("#"+tocoId).attr("folderchk")=="Y" && (toKey.indexOf("JG") < 0 || $("#view_mode").val() != "02")){
				console.log("selectTocoId Make Low Level Menu Add : "+$("#view_mode").val());
				tocoDivMake(tocoId);
				//2022 08 04 Park.J.S. Update : tocoContView(tocoId, "single"); - > tocoContView(tocoId, "single", "Y","",true);
				tocoContView(tocoId, "single", "Y","",true);
				result = contMake(rtList, tocoId, folderChk, 0);
				$("#toco_cont_"+tocoId).after("<div class=\"subMenuDiv\" id=\"subMenuDiv_"+tocoId+"\">"+result+"</div>");
				console.log("result : "+result);
				//$("#main_contents").append(result);
				funtResizeSunMenu(parseInt($("#font_size").val()),"subMenuDiv_"+tocoId);
				$(".loading").hide();
			} else if(firstTocoType == "IPB" || vcKind == "03" || vcKind == "04") {
				tocoContView(tocoId, "single", "", "true");

			} else {
				console.log("else folderChk : "+folderChk);
				if(rtList.length == 1 && folderChk != "list") {
					tocoDivMake(tocoId);
					tocoContView(tocoId, "mult");

				} else if(folderChk == "list") {
					//2022 07 08 Park.J.S. : FI 접근 및 위치 자료에서 IPB 로 판단되는 경우 있어서 result != ""추가
					if(ipbChk == true && result != "") {
						staticStep++;
						$("#" + tocoId).attr("onclick", "");
						result = contMake(rtList, tocoId, folderChk, staticStep);
						$("#toco_cont_" + tocoId).html("");
						console.log("result : "+result);
						$("#toco_cont_" + tocoId).html(result);
						$(".loading").hide();
						funtResize(parseInt($("#font_size").val()));
						funtResizeSunMenu(parseInt($("#font_size").val()),"toco_cont_"+tocoId);

					} else {
						//2022 08 04 Park.J.S. Update : tocoContView(tocoId, "mult", "", ipbChk); - > tocoContView(tocoId, "mult", "", ipbChk,true);
						console.log("SubMenu Content Call tocoId : "+tocoId);
						tocoContView(tocoId, "mult", "", ipbChk,true);
					}

					tocoListFocus(tocoId);

				} else {
					result = contMake(rtList, tocoId, folderChk, 0);
					$("#main_contents").append(result);
					//funtResize(parseInt($("#font_size").val()));

					$(".loading").hide();
				}
			}
		},
		error : function(data) {
			//alert("selectTocoId error");
		},
		complete : function() {
		}
	});
}


// 교범 이동
function viewToContents(toKey, tocoId, contId) {

    //TODO: TO History - 뒤로가기 버튼 클릭시 이전에 보던 교범으로
	/*if(historyIndex > 0) {
		$(".btn_back").css("background-position", "-150px 0;");
	}*/

	var folderChk = $("#" + tocoId).attr("folderChk");

	//TODO: check 기능 동작 처리
	/*if(confirmCLChk(tocoId) == false) {
		console.log("viewToContents confirmCLChk : return");
		$("#cl_chk").val('Y');
		return;
	} else {
		console.log("viewToContents confirmCLChk : pass");
		$("#cl_chk").val('N');
	}*/

	//TODO: 팝업?
	/*if($("#popup_check").val() == 1) {
		console.log("viewToContents popup_check : return");
		var vehicleType = $("#vehicle_type").val();
		tocoListFocus(tocoId);
		loadPdf("KO", "PH", toKey, vcKind, "false", tocoId, vehicleType);
		return;
	}*/
	
	if(contId == "undefined" || contId == null) {
		contId = "";
	}

	//TODO: opener?
	/*if($("#opener_link").val() == "link") {
		console.log("viewToContents opener_link : return");
		viewOpenerContents(toKey, tocoId, searchWord, vcKind, contId);
		return;
	}*/

	$(".loading").show();
	$("#main_fi_contents").hide();
	var bWidth	= window.outerWidth;
	var bHeight	= window.outerHeight;
	var param = "";
	
	if (cousorFlg == false) {
		
		var reCount = historyTotalIndex -historyIndex;
		for (var int = 0; int < reCount; int++) {
			eventHistory.pop();
			toKeyHistory.pop();
			tocoIdHistory.pop();
			searchWordHistory.pop();
			vcKindHistory.pop();
			contIdHistory.pop();
		}
		
		historyTotalIndex = historyIndex;
	}

	//alert("viewToContents 222");
	
	eventHistory.push('viewToContents');
	//2021 10 08 
	//toKeyHistory에는 특수 문자가 아닌 상태로 저장
	//toKeyHistory.push(encodeURIComponent(toKey));
	toKeyHistory.push(toKey);
	tocoIdHistory.push(tocoId);
	searchWordHistory.push(searchWord);
	vcKindHistory.push(vcKind);
	contIdHistory.push(contId);
	$("#frame_now_page").val("");
	historyIndex++;
	historyTotalIndex++;
//	clearText('clear');
	staticToParam = "";
	StaticGraphArray = new Array();
	StaticAlertArray = new Array();
		
	$("#frame_contents").hide();
	$(".contents_div").hide();
	$("#main_contents").html("");
	$(".no_data_img").hide();
	$("#main_contents").show();
	$("#main_cover").hide();
	$("#toco_id").val(tocoId);
	$("#vc_kind").val(vcKind);
	
	if(vcKind == "05" || vcKind == "01") {
		$(".con.main").css("overflow-y", "auto");
	} else {
		$(".con.main").css("overflow-y", "scroll");
	}

	//alert("viewToContents 333");

	param += "to_key=" + encodeURIComponent(toKey);
	//param += "&toco_id=" + tocoId; //20190703 delete LYM
	param += "&browser_width=" + bWidth;
	param += "&browser_height=" + bHeight;
	param += "&vcont_kind=" + vcKind;
	param += "&cont_id=" + contId;
	param += "&search_word=" + searchWord;

	//alert("param="+param);
	console.log("===>viewToContents param="+param);
	

	//2023 01 30 Park.J.S. vehicleExepFlag ADD
	topInformation(toKey, tocoId, param, folderChk, toType, vehicleExepFlag);


}

/**
 * 
 * 2023 01 30 Park.J.S. Update : vehicleExepFlag 추가 Y로 넘어 올경우 기종 선택된 값 무시하고 조회 가능하게 수정
 * 현재 이미지 때문에 사용중이고 이미지 자체에 vehicle 설정된 경우는 나타나지 않음 관련해서 추가 수정이 필요한 경우 tocoContView에서도 사용하게 수정 한후에 
 * java단에서도 처리하게 수정 필요
 * @param toKey
 * @param tocoId
 * @param toParam
 * @param folderChk
 * @param toType
 * @param vehicleExepFlag
 * @returns
 */
function topInformation(toKey, tocoId, toParam, folderChk, toType, vehicleExepFlag) {
	console.log("In topInformation : "+toKey+", "+tocoId+", "+toParam+", "+toParam+", "+toType);
	$.ajax({
		type : "POST",
		url : $("#proectName").val()+"ietm/topInfo.do",
		data : "to_key=" + encodeURIComponent(toKey) + "&toco_id=" + tocoId,
		dataType : "json",
		success : function(data) {
			//2022 06 15 Park.J.S. Add
			try{
				//2023 01 18 Park.J.S. Update : 최상단 SSSN BLOCK2에서만 사용하게
				if(data.GV_BIZ_CODE == "BLOCK2"){
					console.log("topInformation ATTR : "+$("#"+tocoId).attr("sssn"));
					$("#top_to_sssn").html($("#"+tocoId).attr("sssn"));
				} else if (data.GV_BIZ_CODE == "KTA" && data.subName != "") {
					$("#top_to_name").html(data.subName || "");
				}
			}catch (e) {}
			var ipbChk =  data.ipbChk;
			//$("#top_to_model").html("T50");
			$("#top_to_key").html(toKey);
			/*
			 * 2021 09 15 
			 * park js
			 * 교범명 선택된 교범의 상위로 이동해서 찾도록 수정
			 */
			//$("#top_to_name").html(toKey); //20190220 add (data.toName)
			
			//20200320 edit LYM 임시
			//$("#top_to_name").html(toKey); //20190220 add (data.toName)
			//$("#top_to_name").text($("#cover_to_tech").text()); //20190220 add (data.toName)
			
			//20201204 edit LYM 사업(기종)별로 GV_BIZ 프로퍼티 변수값 인식하도록 기능 수정
			//if (true) {	//HOOO용도
			console.log("data.GV_BIZ_CODE : "+data.GV_BIZ_CODE);
			console.log("data.toVerDate : "+data.toVerDate);
			gv_biz_code = data.GV_BIZ_CODE;
			if (data.GV_BIZ_CODE == "HUSS") {	//HOOO용도
				var coverTech = "";
				if(toKey == "장비운용 교범") {
					coverTech += " 2-50-06(158)-02";
				} else if(toKey == "부대정비 교범") {
					coverTech += " 1-50-06(159)-01";
				} else if(toKey == "야전정비 교범") {
					coverTech += " 1-50-06(159)-02";
				} else if(toKey == "보급교범") {
					coverTech += " 1-50-06(159)-04";
				} else if(toKey == "회로도 교범") {
					coverTech += " 2-50-06(159)-04";
				} else if(toKey == "원격전시장치 교범") {
					coverTech += " 2-50-06(160)-04";
				}
				$("#top_to_key").html(coverTech);
				
			} else if (data.GV_BIZ_CODE == "T50") {	//T50 2021 06
				//TODO 
				var coverTech = "";
				if(toKey == "장비운용 교범") {
					coverTech += " 2-50-06(158)-02";
				} else if(toKey == "부대정비 교범") {
					coverTech += " 1-50-06(159)-01";
				} else if(toKey == "야전정비 교범") {
					coverTech += " 1-50-06(159)-02";
				} else if(toKey == "보급교범") {
					coverTech += " 1-50-06(159)-04";
				} else if(toKey == "회로도 교범") {
					coverTech += " 2-50-06(159)-04";
				} else if(toKey == "원격전시장치 교범") {
					coverTech += " 2-50-06(160)-04";
				}
				$("#top_to_key").html(coverTech);
			} else if (data.GV_BIZ_CODE == "MDV") {	//MOO용도 20201204 add
				var coverTech = "";
				if(toKey == "장비 운용교범") {
					coverTech += " 2-00-11(301)-01";
				} else if(toKey == "부대 및 야전 정비교범") {
					coverTech += " 1-00-11(301)-02";
				} else if(toKey == "보급교범") {
					coverTech += " 1-00-11(301)-04";
				}
				$("#top_to_key").html(coverTech);
				
			/*} else if (data.GV_BIZ_CODE == "EA") {	//EO용도 20201208 add
				var coverTech = "";
				if(toKey == "장비 운용교범") {
					coverTech += " 2-00-11(301)-01";
				} else if(toKey == "부대 및 야전 정비교범") {
					coverTech += " 1-00-11(301)-02";
				} else if(toKey == "보급교범") {
					coverTech += " 1-00-11(301)-04";
				}
				$("#top_to_key").html(coverTech);
			*/
			}  else if (data.GV_BIZ_CODE == "LSAM") {	//LOOO용도
				//TODO 교범 커버 타이블 처리 필요
				var coverTech = "";
				if(toKey == "체계") {
					coverTech += " 2-50-06(158)-02";
				} else if(toKey == "장입유도탄(AAM)") {
					coverTech += " 1-50-06(159)-01";
				} else if(toKey == "장입유도탄(ABM)") {
					coverTech += " 1-50-06(159)-02";
				} else if(toKey == "작전통제소 ") {
					coverTech += " 1-50-06(159)-04";
				} else if(toKey == "교전통제소 ") {
					coverTech += " 2-50-06(159)-04";
				} else if(toKey == "다기능레이다 ") {
					coverTech += " 2-50-06(160)-04";
				} else if(toKey == "발사대  ") {
					coverTech += " 2-50-06(160)-04";
				} else if(toKey == "유도탄 장전장비  ") {
					coverTech += " 2-50-06(160)-04";
				} else if(toKey == "정비밴  ") {
					coverTech += " 2-50-06(160)-04";
				} else if(toKey == "수리부속밴   ") {
					coverTech += " 2-50-06(160)-04";
				} else if(toKey == "작전/교전통제 훈련장비  ") {
					coverTech += " 2-50-06(160)-04";
				} else if(toKey == "부체계 시험세트   ") {
					coverTech += " 2-50-06(160)-04";
				} else{
					coverTech += " "+toKey;
				}
				$("#top_to_key").html(coverTech);
			} else {
				$("#top_to_key").html(toKey); //20190220 add (data.toName)
			}
			
			//by ejkim 2022.10.06 sssn 미존재시 부모 sssn 정보 처리로 변경
			//$("#top_to_sssn").html(data.tocoSssnNo);
			if( data.tocoSssnNo == "" || data.tocoSssnNo == "-"  ) {
				$("#top_to_sssn").html(data.tocoSssnNoParent);
				$("#top_to_sssn").attr("title", data.tocoSssnNoParent);
			}else {
				$("#top_to_sssn").html(data.tocoSssnNo);
				$("#top_to_sssn").attr("title", data.tocoSssnNo);
			}	
			
			//by ejkim 2022.10.06 발간일 변경 
			if( data.GV_BIZ_CODE == "BLOCK2" ) {
				$("#top_to_verdate").html(data.toHVerDate);
			//2022 12 01 jysi EDIT : LSAM도 toHVerdate를 발간일로 쓰도록 변경
			}else if( data.GV_BIZ_CODE == "LSAM" ) {
				if(data.toHVerdate != ""){
					$("#top_to_verdate").html(data.toHVerDate);
				}else{
					$("#top_to_verdate").html('20XX-XX-XX');
				}
			} else if( data.GV_BIZ_CODE == "KTA"){
				if(data.versiondate != ""){
					$("#top_to_verdate").html(data.versiondate);
				}else{
					//교범 제작일이 없을경우 기본 값
					$("#top_to_verdate").html('20XX-XX-XX');	
				}
			} else {
				if(data.toHVerDate != ""){
					$("#top_to_verdate").html(data.toHVerDate);
				}else{
					//교범 제작일이 없을경우 기본 값
					$("#top_to_verdate").html('20XX-XX-XX');	
				}
			}
			
			
			//$("#top_to_model").attr("title", "HUSS"); //20190703 edit (T50->HUSS)
			$("#top_to_model").attr("title", data.GV_BIZ_CODE); //20210610 edit ( USE data.GV_BIZ_CODE)
			$("#top_to_key").attr("title", toKey);
			$("#top_to_name").attr("title", toKey); //20190220 add
			//$("#top_to_sssn").attr("title", data.tocoSssnNo); //상단로직에서 처리
			$("#top_to_verdate").attr("title", data.toVerDate);
			$("#toco_id").val(tocoId);
			$("#to_title").val(data.toTitle);
			$("#remo_title").text(data.toTitle);
			$("#frame_chk").val(0);
			
			toParam += "&to_name=" + data.toTitle;
			//toParam += "&content_option=" + $('#content_option').val();
			//toParam += "&fi_option=" + $('#fi_option').val();
			toParam += "&content_option=" + checkNull($('#content_option').val());
			toParam += "&fi_option=" + checkNull($('#fi_option').val());
			toParam += "&view_mode=" + $("#view_mode").val();

			console.log("===>222 toParam="+toParam);
			console.log("toKey : "+toKey+", data.toTitle : "+decodeEntities(data.toTitle));
			
			// 2023.08.09 - tokey와 타이틀이 틀릴 경우에도 표시 출력하도록 보완 - jingi.kim
			var isCover = toKey == decodeEntities(data.toTitle) ? true : false;
			if ( $('#bizCode').val() == "NLS" ) {
				if ( !isCover ) {
					isCover = data.pTocoId == "0" ? true : false;
				}
			}
			
			//표지 호출
			if( isCover ) {
				viewCover(toKey, tocoId);
			} else {
				if($("#view_mode").val() == "02" &&  toKey.indexOf("JG") > 0) {
					toParam += "&output_mode=" + $("#to_output").val();
					console.log("View Type 1");
					viewTo(toParam, tocoId);
				} else {
					console.log("$('#output_mode').val() : "+$('#output_mode').val()+", toKey.indexOf('WC') : "+toKey.indexOf("WC"));
					if($('#output_mode').val() == "01" && toKey.indexOf("WC") < 0) {
						console.log("$('#to_output').val() : "+$('#to_output').val()+", ipbChk : "+ipbChk);

						if($('#bizCode').val() == 'KTA') {
							// 2023.09.07 - KTA 강제지정 추가 - jingi.kim
							if( isFixKTAIPBSearch() == true ){
								tocoContView(tocoId, "01", folderChk, "");
							} else {
								toParam += "&output_mode=02";
								staticToParam = toParam;
								tocoView(toKey, tocoId, folderChk, toType,vehicleExepFlag);
							}
						}
						
						//2022 07 01 Park.J.S. : 색인으로 판단되면 View Type 4 바로 호출
						//2023 01 04 Park.J.S. : 예외 상황 우회추가 1.5 부품 번호 색인 메뉴명 존재 함  $("#"+tocoId).attr("title").indexOf("부품 번호 색인") > -1
						else if($("#"+tocoId) && $("#"+tocoId).attr("title") && (($("#"+tocoId).attr("title").indexOf("부품 번호 색인") > -1 || $("#"+tocoId).attr("title").indexOf("국가 재고 번호 색인") > -1) || $("#"+tocoId).attr("title").indexOf("참고 지정 번호 색인") > -1)
								&& $("#"+tocoId).attr("title").indexOf("1.5 부품 번호 색인") < 0 && $("#"+tocoId).attr("title").indexOf("장 부품 번호 색인") < 0){
							console.log("View Type 4");
							tocoContView(tocoId, "01", folderChk, "");
						}else{
							if($("#to_output").val() == "01" && ipbChk == 0) {
								console.log("View Type 2");
								toParam += "&output_mode=01";
								viewTo(toParam, tocoId);
							} else {
								console.log("View Type 3");
								toParam += "&output_mode=02";
								staticToParam = toParam;
								//2023 01 30 Park.J.S. vehicleExepFlag ADD
								tocoView(toKey, tocoId, folderChk, toType,vehicleExepFlag);
							}
						}
						
					} else {
						
						console.log("View Type 4");
						toParam += "&output_mode=02";
						staticToParam = toParam;
						//2024.03.27 - Topic, SubTopic 일 경우에만 MULTI 가능하도록 추가 - jingi.kim
						//2024.04.16 - BLOCK2, KTA만 적용하도록 제한 - jingi.kim
						if ( $("#bizCode").length > 0 && ( $("#bizCode").val() == "BLOCK2" || $("#bizCode").val() == "KTA" ) ) {
							tocoView(toKey, tocoId, folderChk, toType);
						} else {
							tocoView(toKey, tocoId, 'N', toType);
						}
					}
				}
				
			}
			toParam += "&toco_id=" + tocoId; //20190703 add LYM
			latestToInsert(toParam);
		}
	});

}

/**
 *
 */
function topInformation(toKey, tocoId) {
	console.log("In topInformation : "+toKey+", "+tocoId+", "+toParam+", "+toParam+", "+toType);
	$.ajax({
		type : "POST",
		url : "ietm/topInfo.do",
		data : {"to_key": encodeURIComponent(toKey), "toco_id": tocoId},
		dataType : "json",
		success : function(data) {

			//최상단 SSSN BLOCK2에서만 사용하게
            if(data.bizCode == "BLOCK2"){
                $("#top_to_sssn").html($("#"+tocoId).attr("sssn"));
            }
            if (data.bizCode == "KTA" && data.subName != "") {
                $("#top_to_name").html(data.subName || "");
            }

			var ipbChk =  data.ipbChk;
			$("#top_to_key").html(toKey);


			$("#top_to_sssn").html(data.tocoSssnNo);
			$("#top_to_sssn").attr("title", data.tocoSssnNo);
			if( data.tocoSssnNo == "" || data.tocoSssnNo == "-"  ) {
                $("#top_to_sssn").html(data.tocoSssnNoParent);
                $("#top_to_sssn").attr("title", data.tocoSssnNoParent);
            }

			// 발간일 변경
			let versionDate = "20XX-XX-XX";
			versionDate = data.toHVerDate != "" ? data.toHVerDate : versionDate;
			if( data.bizCode == "KTA") {
			    versionDate = data.versiondate != "" ? data.versiondate : versionDate;
			}
			$("#top_to_verdate").html(versionDate);

			$("#top_to_model").attr("title", data.bizCode);
			$("#top_to_key").attr("title", toKey);
			$("#top_to_name").attr("title", toKey);
			$("#top_to_verdate").attr("title", data.toVerDate);
			$("#toco_id").val(tocoId);
			$("#to_title").val(data.toTitle);
			$("#remo_title").text(data.toTitle);
			$("#frame_chk").val(0);

			latestToInsert(toParam);
		}
	});

}

//20190703 add LYM
function checkNull(value) {
	if (value == "undefined" || value == null) {
		value = "";
	}
	return value;
}

//============================
//20190705 add 전반적으로 수정
var gvTocoCnt			= 0;
var gvOrgTocoList		= "";
var gvToParam			= "";
var gvTocoId			= "";
var gvOutputMode		= "";
var gvTimeFlag			= "";
var pagingFirstCnt		= 3;
var pagingCnt			= 7;

//2022 03 15 Park.J.S 수정
//20190701 edit LYM 교범 목차 자식 트리 가져오기
function viewTo(toParam, tocoId) {
	console.log("CALL viewTo : "+toParam+", "+tocoId);
	var outputMode = $("#output_mode").val();
	timeFlag = false;
	setTimeout('timeCheck()', 2000); //20190704 delete
	$("#remo_table").html("");
	$(".loading").show();

	$("#main_contents").html("");
	
	var toParamParent = toParam + "&toco_id=" + tocoId;
	var toParamChild = "";
	var isEnd = true;
	
	$.ajax({
		type: "POST",
		url:  $("#proectName").val()+"ietm/childTocoList.do",
		data: toParamParent,
		async: false,
		dataType: "json",
		success: function(data) {
			console.log("data : "+JSON.stringify(data));
			var arrTocoList = data.arrTocoList;
			//viewToMore(arrTocoList, toParam, tocoId, outputMode, timeFlag);
			try{
				gvTocoCnt		= arrTocoList.length;
				gvOrgTocoList	= arrTocoList;
				gvToParam		= toParam;
				gvTocoId		= tocoId;
				gvOutputMode	= outputMode;
				gvTimeFlag		= timeFlag;
			}catch(e){
				console.log("e : "+e.message);
				$(".loading").hide();
			}
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			//2022 03 15 Park.J.S. : data.arrTocoList 가지고 오지 못함 (사실상 $.ajax({ 부분 불필요) 기존 넘어온 값으로 화면 분할 처리 할수 있으므로 viewToDetail(toParam+"&toco_id=" + tocoId, tocoId, outputMode, timeFlag); 추가
			// viewToMore("1"); 주석 처리
			console.log("toParam : "+toParam+", tocoId : "+tocoId+", outputMode : "+outputMode+", timeFlag : "+timeFlag);
			viewToDetail(toParam+"&toco_id=" + tocoId, tocoId, outputMode, timeFlag);
			//viewToMore("1"); 
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		},
		complete : function() {
		}
	});

	return true;

}


//20190705 add 전반적으로 수정
//function viewToMore(arrTocoList, toParam, tocoId, outputMode, timeFlag) {
function viewToMore(pageNo) {
	
	console.log("CALL viewToMore : "+pageNo);
	
	arrTocoList		= gvOrgTocoList;
	toParam			= gvToParam;
	tocoId			= gvTocoId;
	outputMode		= gvOutputMode;
	timeFlag		= gvTimeFlag;
	
	
	//$("#btn_more_button").css("cursor", "progress");
	
	var toParamChild = "";
	var isEnd = true;
	var orgTocoList = new Array();
	
	console.log("viewToMore arrTocoList.length : "+arrTocoList.length);
	for (var i=0; i<arrTocoList.length; i++)
	{
		toParamChild = toParam + "&toco_id=" + arrTocoList[i];
		console.log("viewToMore toParamChild : "+toParamChild+", tocoId : "+tocoId+", outputMode : "+outputMode+", timeFlag : "+timeFlag);
		viewToDetail(toParamChild, tocoId, outputMode, timeFlag);
		
		//$("#main_contents").scrollTop($("#main_contents")[0].scrollHeight);
		//$("#main_contents").scrollTop(10);
		
		if ((i != 0 && i != arrTocoList.length-1)
				&& ((pageNo == "1" && i == (pagingFirstCnt-1)) || (pageNo != "1" && i%(pagingCnt-1) == 0))) {
			
			var idx = 0;
			for (var j=i+1; j<arrTocoList.length; j++) {
				orgTocoList[idx++] =  arrTocoList[j];
			}
			var paging = pageNo + " / " + (Math.ceil((gvTocoCnt - pagingFirstCnt) / pagingCnt) + 1);
			
			$("#div_more_button").show();
			//$("#div_more_button").attr("onclick", "viewToMore('"+orgTocoList+"', '"+toParam+"', '"+tocoId+"', '"+outputMode+"', '"+timeFlag+"');");
			$("#div_more_button").attr("onclick", "viewToMore('"+(parseInt(pageNo)+1)+"');");
			
			$("#btn_more_button").text("+ 내용 더보기  ("+paging+")");
			
			gvOrgTocoList	= orgTocoList;
			gvToParam		= toParam;
			gvTocoId		= tocoId;
			gvOutputMode	= outputMode;
			gvTimeFlag		= timeFlag;
			
			isEnd = false;
			break;
		}
	}
	console.log("isEnd : "+isEnd);
	if (isEnd == true) {
		$("#div_more_button").hide();
	} else {
		//$("#btn_more_button").css("cursor", "pointer");
	}

	return true;

}


//20190703 add LYM 목차 콘텐츠 로딩 방법 변경으로 인해 함수 분리
//20190729 add LYM 표 출력 기능 추가
function viewToDetail(toParam, tocoId, outputMode, timeFlag) {
	console.log("CALL viewToDetail");
	$.ajax({
		type: "POST",
		url:  $("#proectName").val()+"ietm/toCont.do",
		data: toParam,
		async: false,
		dataType: "json",
		success: function(data) {
			var fiTypeChk = data.fiTypeChk;
			var fiTocoType = data.fiTocoType;
			var xmlTagChk = data.xmlTagChk;
			var contents = data.returnData;
			var returnCont = data.returnCont;
			var toKey = $("#to_key").val();
			$(".no_data_img").hide();

			/*
			if(timeFlag) {
				if(!confirm("해당교범 이동시 시간이지연될수 있습니다. 이동하시겠습니까?")) {
					prevTo();
					return;
				}
			}
			*/

			$("#main_cover").hide();
			$(".main").hide();
			contents = transWord(contents, "splChar");

			if($("#view_mode").val() == "02" && toKey.indexOf("JG") > 0 && $("#toco_id").val() == "9528e7f78cd9490b850d741458ef50fc") {
				console.log("JG Frame Type : 9528e7f78cd9490b850d741458ef50fc");
				$("#frame_chk").val(xmlTagChk);
				$("#hidden_cont").val(contents);
				frameView(0);
				$("#frame_contents").show();
				$(".loading").hide();
			//} else if($("#view_mode").val() == "02" && toKey.indexOf("JG") > 0 && $("#vc_kind").val() != "03") {//$("#vc_kind").val() != "03" 그림 모드가 아닐경우
			//} else if($("#view_mode").val() == "02" && toKey.indexOf("JG") > 0 && $("#vc_kind").val() != "03" && $("#"+tocoId).attr("title").split(".").length -1 >= 2) {//2023 01 05 메뉴명에 . 이 2개 이상일경우로 수정
			//} else if($("#view_mode").val() == "02" && toKey.indexOf("JG") > 0 && $("#vc_kind").val() != "03" && $("#"+tocoId).attr("title").split(".").length -1 >= 2) {//2023 01 05 메뉴명에 . 이 2개 이상일경우로 수정
            /* 옵션 정리되면 되돌리기.
            } else if($("#bizCode") && $("#bizCode").val() == "BLOCK2" && $("#view_mode").val() == "02" && toKey.indexOf("JG") > 0 && $("#vc_kind").val() != "03" && $("#"+tocoId).attr("title").split(".").length -1 >= 2) {//2023 01 25 KTA BLOC2 단위로 나눔
            */
            } else if($("#bizCode") && $("#bizCode").val() == "BLOCK2" && toKey.indexOf("JG") > 0 && $("#"+tocoId).attr("title").split(".").length -1 >= 2) {//2023 01 25 KTA BLOC2 단위로 나눔
				console.log("JG Frame Type : "+($("#"+tocoId).attr("title").split(".").length -1));
				console.log("JG Frame Type : "+xmlTagChk+", "+contents);
				$("#frame_chk").val(xmlTagChk);
				$("#hidden_cont").val(contents);
				frameView(0);
				$("#frame_contents").show();
				$(".loading").hide();
			} else if($("#bizCode") && $("#bizCode").val() == "KTA" && $("#view_mode").val() == "02" && toKey.indexOf("JG") > 0 && $("#vc_kind").val() != "03") {//2023 01 25 KTA BLOC2 단위로 나눔
				console.log("JG Frame Type : "+($("#"+tocoId).attr("title").split(".").length -1));
				console.log("JG Frame Type : "+xmlTagChk+", "+contents);
				$("#frame_chk").val(xmlTagChk);
				$("#hidden_cont").val(contents);
				frameView(0);
				$("#frame_contents").show();
				$(".loading").hide();
			} else {
				//data없을때 처리
				if(data.returnData == "" && returnCont == "") {
					$(".no_data_img").show();
					$(".loading").hide();
					return;
				}
				console.log("viewToDetail fiTypeChk : "+fiTypeChk+", fiTocoType : "+fiTocoType);
				if(fiTypeChk == true && fiTocoType == "29") {
					contents = contents.replace(/<eXPISInfo>(.*?)<\/?eXPISInfo>/gi,"");
					contents = contents.replace(/•/gi, "●");
					contents = contents.replace(/apos;/gi, "\'");
					contents = contents.replace(/&quot;/gi, "\"");
					contents = contents.replace(/quot/gi, "\"");
					$("#fi_cont").val(contents);
					fiMake(contents, fiTocoType);
					$(".loading").hide();

				} else if(fiTypeChk == true && fiTocoType == "23") {
					$("#fi_cont").val($(".fi-link").html());
					if($("#fi_cont").val().indexOf("xml") != -1) {
						fiMake($("#fi_cont").val(), fiTocoType);
						$(".loading").hide();
					}
					checkvalue.value = "0";
					imageEventBind();
					// chkCL(data.clKeyList);
					funtResize(parseInt($("#font_size").val()));

				} else {
					//멀티링크 임시 p태그 변경(p태그 안에 div 사용x)
					if(fiTypeChk == false) {
						contents = contents.replace(/<p/gi, "<div");
						contents = contents.replace(/p>/gi, "div>");
						$("#fi_dds").hide();
					}

					//$("#main_contents").html(contents);
					$("#main_contents").append(contents);
					//$("#main_contents").scrollTop(0);
					$("#main_contents").show();

					// 2023.07.19 - language 처리 - jingi.kim
					var msg_highlighter = $("#ietm_highlighter_settings").val() || "형광펜 설정";
					$("#main_contents #co_context_lay panText").text(msg_highlighter);
					$(".loading").hide();
				}

				checkvalue.value = "0";
				imageEventBind();

				//20190729 add LYM 표 출력 기능 추가
				//20190905 delete LYM JavaScript에서 처리하던걸 -> Core의 TableParser에서 처리 변경
				//tableEventBind();

				// chkCL(data.clKeyList);
				funtResize(parseInt($("#font_size").val()));
			}
		},
		complete : function() {
			remoMakeEvent();
			familyChange($("#font_family").val());
		}
	});

}
//============================


function viewToMulti(toParam, returnData, cnt) {
	$("#main_contents").show();
	var param = toParam + "&returnData="+returnData[cnt]+"";
	var contents = "";
	
	if(cnt < 4) {
		$(".loading").show();
	}
	
	$.ajax({
		type : "POST",
		url : $("#proectName").val()+"ietm/viewToMulti.do",
		data : param,
		dataType : "json",
		success : function(data) {
			contents = data.returnData;
			contents = contents.replace(/<p/gi, "<div");
			contents = contents.replace(/p>/gi, "div>");
		},
		error : function(data) {
			alert("viewToMulti error");
		},
		complete : function() {
			if(cnt == 0) {
				$("#main_contents").html(contents);
			} else {
				$("#main_contents").append(contents);
			}
			
			remoMakeEvent();
			familyChange($("#font_family").val());
			$(".loading").hide();
			$("#fi_dds").hide();
			
			// 2023.07.19 - language 처리 - jingi.kim
			var msg_highlighter = $("#ietm_highlighter_settings").val() || "형광펜 설정";
			$("#main_contents #co_context_lay panText").text(msg_highlighter);
			
			if(returnData[cnt+1] != undefined && viewToMultiCheck != "N") {
				viewToMulti(toParam, returnData, cnt + 1);
			}
		}
	});
}


var StaticGraphArray = new Array();
var StaticContArray = new Array();
var StaticAlertArray = new Array();
var StaticGraphOBJArray = new Array();
//2022 03 15 Park.J.S 수정
//2022 08 02 Park.J.S 수정  : 그림단위가 아니라 팝업 단위로도 되야한다고해서 수정
function frameView(frameIndex) {
	console.log("CALL frameView : "+frameIndex);
	closeLeftList();
	var cont = $("#hidden_cont").val();
	var varEvent = "";
	var varClass = "";
	var versionHtml = "";
	var leftCont = "";
	var alertType = "";
	var alertText = "";
	var alertHtml = "";
	var bottomBtnHtml = "";
	var alertIndex = 0;
	var viewIndex = 0;
	var alertEvent = "";
	var imageIndex = 999;
	var viewFlag = false;
	console.log("frameView viewIndex : "+viewIndex);
	
	StaticContArray = new Array();
	StaticGraphArray = new Array();
	StaticAlertArray = new Array();
	StaticGraphOBJArray = new Array();
	
	//2023 01 19 Park.J.S ADD : 기존 팝업 닫음
	$("#frame_alert_body").hide();
	
	console.log("cont : "+cont);
	$("#frame_left_content").html(cont);
	
		
//	$(".frame_" + frameIndex).each(function() {
	$(".ac-desc").children().each(function() {
		if($(this).hasClass("ac-object") && $(this).parent().hasClass("div_version")) {
			varEvent = $(this).prev().attr("onclick");
			varClass = $(this).prev().attr("class");
			versionHtml =	"<div class='div_version'>";
			versionHtml +=		"<span class='" + varClass + "' onclick=\"" + varEvent + "\"></span>";
			versionHtml += 		$(this).html();
			versionHtml +=	"</div>";
			StaticGraphArray.push(versionHtml);
			
		} else if($(this).hasClass("ac-object")) {
			StaticGraphArray.push($(this).html());
		}
	});
	
	//본문 hide처리
	$(".ac-object").hide();//이미지 본문에서는 숨기기
	console.log("$('.ac-object').length : "+ $(".ac-object").length);
	for(var i=0; i<$(".ac-object").length; i++) {
		//2022 03 15 Park.J.S : 특정 frame_0 가지고 있는 객체 제외 화면에서 숨김 (최초 화면에 표시하기 위해서)
		/*2022 08 02 PArk.J.S. 하단 $(".ac-task").children().each(function() { 부분에서 화면 보이기 처리 별도로 진행함
		if(i != frameIndex){
			$(".frame_" + i).hide();
		}
		*/
		// 2023.06.02 - 2페이지 부터 버전바가 포함된 경우 표시되지 않는 문제로 해당 페이지의 frame_ 클래스만 숨김 - jingi.kim
		//- $(".frame_" + i).hide();
		$(".frame_" + frameIndex).hide();
	}

//	$(".frame_" + frameIndex).each(function() {
	$(".ac-desc").children().each(function() {
		if($(this).hasClass("ac-object") == "") {
			$(this).show();
		}
		if($(this).hasClass("alert_class") || ($(this).hasClass("div_version") && $(this).children("div:eq(0)").hasClass("alert_class"))) {
			console.log("JG Alert S1 : "+alertHtml);
			if($(this).hasClass("ac-alert-warning") || ($(this).hasClass("div_version") && $(this).children("div:eq(0)").hasClass("ac-alert-warning")) ) {
				alertType = "warning";
				alertText = "경고";
			} else if($(this).hasClass("ac-alert-note") || ($(this).hasClass("div_version") && $(this).children("div:eq(0)").hasClass("ac-alert-note")) ) {
				alertType = "note";
				alertText = "주기";
			} else if($(this).hasClass("ac-alert-caution") || ($(this).hasClass("div_version") && $(this).children("div:eq(0)").hasClass("ac-alert-caution")) ) {
				alertType = "caution";
				alertText = "주의";
			}
			alertHtml = $(this).html();
			console.log("JG Alert S1 : "+alertHtml);
			alertEvent = "framePopupEvent('" + alertIndex + "', '" + alertType + "', '" + alertText + "');";
			
			//alert
			bottomBtnHtml += "<li><a href='javascript:void(0);' onclick=\"" + alertEvent + "\" class='alert_btn alert_li_" + alertIndex + " " + alertType + "'>";
			bottomBtnHtml += alertText;
			bottomBtnHtml += "</a></li>";
			
			StaticAlertArray.push(alertHtml);
			alertIndex++;
		}
		
	});
	//2022 03 15 Park.J.S : $(".frame_" + frameIndex).each(function()  ADD
	// Alert 관련 화면에 띄우고 하단에 호출 가능한 버튼 추가
	//상단 $(".ac-desc").children().each(function() { 존재 이유 모르겠음 사이드 이펙트고려해서 삭제하지 않음
	//2022 08 02 Park.J.S. Update : 팝업 단위 페이징 처리로 변경 하기 위해 수정 $(".frame_" + frameIndex).each(function() { -> $(".ac-task").children().each(function() { 
	//$(".frame_" + frameIndex).each(function() {
	console.log("frameView viewIndex Befor : "+viewIndex);
	var startAlertCheck = true;
	$(".ac-task").children().each(function(idx) {
		if($(this).hasClass("ac-object") == "") {
			$(this).show();
		}
		//- console.log("idx : "+idx+" , this : "+$(this).html());
		console.log("frameView viewIndex In : "+viewIndex);
		//2023 01 26 Park.J.S. ADD : 시작에 Alert 없을경우 없는 부분만 표시
		if(startAlertCheck && $(this).hasClass("alert_class") || ($(this).hasClass("div_version") && $(this).children("div:eq(0)").hasClass("alert_class"))) {
			console.log("frameView viewIndex Alert Start ");
			startAlertCheck = false;
		}else{
			if(startAlertCheck){
				console.log("frameView viewIndex Not Alert Start ");
				StaticAlertArray.push("");
				alertIndex++;
				viewIndex++;
				startAlertCheck = false;
			}else{
				startAlertCheck = false;
			}
		}
		//2022 12 14 Park.J.S. Update : 버전바 존재할경우 문제 생기는 부분 수정
		if($(this).hasClass("alert_class") || ($(this).hasClass("div_version") && $(this).children("div:eq(0)").hasClass("alert_class"))) {
			if(viewIndex == frameIndex){
				if($(this).hasClass("ac-alert-warning") || ($(this).hasClass("div_version") && $(this).children("div:eq(0)").hasClass("ac-alert-warning")) ) {
					alertType = "warning";
					alertText = "경고";
				} else if($(this).hasClass("ac-alert-note") || ($(this).hasClass("div_version") && $(this).children("div:eq(0)").hasClass("ac-alert-note")) ) {
					alertType = "note";
					alertText = "주기";
				} else if($(this).hasClass("ac-alert-caution") || ($(this).hasClass("div_version") && $(this).children("div:eq(0)").hasClass("ac-alert-caution")) ) {
					alertType = "caution";
					alertText = "주의";
				}
				if($(this).hasClass("div_version")){
					alertHtml =  $(this).children("div:eq(0)").html();
				}else{
					alertHtml = $(this).html();
				}
				console.log("JG Alert S2 : "+alertHtml);
				alertEvent = "framePopupEvent('" + alertIndex + "', '" + alertType + "', '" + alertText + "');";
				
				//alert
				bottomBtnHtml += "<li><a href='javascript:void(0);' onclick=\"" + alertEvent + "\" class='alert_btn alert_li_" + alertIndex + " " + alertType + "'>";
				bottomBtnHtml += alertText;
				bottomBtnHtml += "</a></li>";
				console.log("frameView Make Alert alertIndex : "+alertIndex);
			}
			StaticAlertArray.push(alertHtml);
			alertIndex++;
			//2022 12 07 Park.J.S. Update : 팝업 붙어있으면 동일 레이어에 표시 해야함
			if($(this).next().hasClass("alert_class")) {
				console.log("프레임 보기에서 경고문이 연속으로 나올경우, 경고문이 전부 팝업되게 수정");
			}else{
				viewIndex++;
			}
			console.log("frameView alertIndex : "+alertIndex);
		// 2023.07.10 - jingi.kim - 버전바 존재할경우 문제 생기는 부분 수정
		} else if( $(this).hasClass("ac-object") != "" || ($(this).hasClass("div_version") && $(this).children("div:eq(0)").hasClass("ac-object")) ) {
			//2022 08 02 Park.J.S. ADD : 팝업 단위 화면 처리위해 추가.
			console.log("Frame View ac-task class No VIEW This is Image "+$(this).attr("class"));
			//2022 12 08 Park.J.S. Update : 이미지 붙어있으면 동일 레이어에 표시 해야함
			if(frmaeImageCheck($(this).prev()) && frmaeImageCheck(this)){//이미지 붙어 있을경우 버전바 있을경우 이미지 여부 판단
				console.log("프레임 보기에서 이미지 연속으로 나올경우, 이미지 전부 표시되게 수정");
			}else{
				$(this).hide();
				viewIndex++;
			}
		}else{
			//2022 08 02 Park.J.S. ADD : 팝업 단위 화면 처리위해 추가.
			console.log("Frame viewIndex "+viewIndex+", frameIndex : "+frameIndex);
			if(viewIndex == (frameIndex+1)){
				//2023 01 31 Park.J.S. Update : 프레임 보기 이미지에 버전바 있는경우 비정상 인식 수정
				if($(this).hasClass("div_version")){
					$(this).children().each(function() {
						imageIndex = $(this).attr("class").substring($(this).attr("class").indexOf("frame_")+6,$(this).attr("class").indexOf("frame_")+8);
					});
				}else{
					imageIndex = $(this).attr("class").substring($(this).attr("class").indexOf("frame_")+6,$(this).attr("class").indexOf("frame_")+8);
				}
				console.log("Frame View ac-task class VIEW "+$(this).attr("class")+" imageIndex : "+imageIndex+", $(this) : "+$(this).html());
				if(frmaeImageCheck(this)){
					console.log("Frame View ac-task class No VIEW Image "+$(this).attr("class"));
				}else{
					//2023 01 18 Park.J.S. Update : Version 처리 추가
					if($(this).hasClass("div_version")){
						console.log("Frame View ac-task div_version : "+ $(this).attr("class"));
						$(this).children().each(function() {
							if($(this).hasClass("frame_"+(frameIndex))){
								$(this).show();
							}
						});
					}else{
						$(this).show();
					}
					$(this).show();
					viewFlag = true;
				}
			}else{
				//2023 01 18 Park.J.S. Update : alert 가 시작에 없을 경우 첫 부분 안보이는 부분 처리
				//2023 01 26 수정건으로 아래 로직 탈 일 없음 Alert 시작 안할경우 viewIndex++ 됨
				if(viewIndex == frameIndex && frameIndex == 0){
					imageIndex = 0;
					if(frmaeImageCheck(this)){
						console.log("Frame View ac-task class No VIEW Image "+$(this).attr("class"));
					}else{
						if($(this).hasClass("div_version")){
							console.log("Frame View ac-task div_version : "+ $(this).attr("class"));
							$(this).children().each(function() {
								if($(this).hasClass("frame_"+frameIndex)){
									$(this).show();
								}
							});
						}else{
							console.log("Frame View ac-task div_version : "+ $(this).attr("class"));
							$(this).show();
						}
						viewFlag = true;
					}
				}else{
					if(frmaeImageCheck($(this).prev()) && frmaeImageCheck(this)){
						console.log("Frame View ac-task class VIEW "+$(this).attr("class"));
					}else{
						console.log("Frame View ac-task class No VIEW "+$(this).attr("class"));
						$(this).hide();
					}
				}
			}
		}
		//2022 12 08 Park.J.S. Update : 이미지 2장 연속 처리 추가 및 버전있을경우 이미지 인식 못하는 현상 수정
		//Step 1 이미지인지 판단.
		console.log("Frame View Image Set 이미지인지 판단 : "+frmaeImageCheck(this));
		//이미지 처리 StaticGraphArray 담아서 나중에 이미지 영역 쪽에 넣음
		if(frmaeImageCheck($(this).prev()) && frmaeImageCheck(this)){//연속된 이미지 일경우
			console.log("Frame View Image Set 연속된 이미지 일경우 Start : "+StaticGraphArray.length);
			if(frmaeImageCheck(this) && $(this).hasClass("div_version")) {
				varEvent = $(this).prev().attr("onclick");
				varClass = $(this).prev().attr("class");
				versionHtml =	"<div class='div_version'>";
				versionHtml +=		"<span class='" + varClass + "' onclick=\"" + varEvent + "\"></span>";
				versionHtml += 		$(this).html().replace(/display: none;/gi,"");
				versionHtml +=	"</div>";
				StaticGraphArray[StaticGraphArray.length-1] = (StaticGraphArray[StaticGraphArray.length-1] + versionHtml);
				console.log("Frame View Image Set 연속된 이미지 일경우 : "+StaticGraphArray[StaticGraphArray.length-1]+", "+versionHtml);
				//StaticGraphArray.push(versionHtml);
				//StaticGraphOBJArray.push($(this));
			} else if(frmaeImageCheck(this)) {
				StaticGraphArray[StaticGraphArray.length-1] = (StaticGraphArray[StaticGraphArray.length-1] + $(this).html());
				console.log("Frame View Image Set 연속된 이미지 일경우 : "+StaticGraphArray[StaticGraphArray.length-1]+", "+$(this).html());
				//StaticGraphArray.push($(this).html());
				//StaticGraphOBJArray.push($(this));
			}
			console.log("Frame View Image Set 연속된 이미지 일경우 Fin : "+StaticGraphArray.length);
		}else{
			console.log("Frame View Image Set : "+StaticGraphArray.length);
			if(frmaeImageCheck(this) && $(this).hasClass("div_version")) {
				varEvent = $(this).prev().attr("onclick");
				varClass = $(this).prev().attr("class");
				versionHtml =	"<div class='div_version'>";
				versionHtml +=		"<span class='" + varClass + "' onclick=\"" + varEvent + "\"></span>";
				versionHtml += 		$(this).html().replace(/display: none;/gi,"");
				versionHtml +=	"</div>";
				console.log("Frame View Image Set Version : "+versionHtml);
				StaticGraphArray.push(versionHtml);
				console.log("Frame View Image frame_ Check : "+$(this).attr("class").indexOf("frame_"));
				if($(this).attr("class").indexOf("frame_") >= 0){
					StaticGraphOBJArray.push($(this));
				}else{
					$(this).children().each(function() {
						console.log("Frame View Image children frame_ Check : "+$(this).attr("class").indexOf("frame_"));
						if($(this).attr("class").indexOf("frame_") >= 0){
							StaticGraphOBJArray.push($(this));
							return;
						}
					});
				}
			} else if(frmaeImageCheck(this)) {
				console.log("Frame View Image Set : "+$(this).html());
				StaticGraphArray.push($(this).html());
				StaticGraphOBJArray.push($(this));
			}
		}
		
	});
	//하단 사이즈 조절
	if(bottomBtnHtml == "") {
		$(".frame_view").css("padding-bottom", 0);
	} else {
		$(".frame_view").css("padding-bottom", 43);
	}
	
	if(StaticGraphArray.length == 0) {
		$("#frame_right_content").html("<div class='ac-noimg'></div>");
	} else {
		for(var i=0;i<StaticGraphOBJArray.length;i++){
			//2023 01 31 Park.J.S. Update : frame 방식이 정상 적이지 않아서 
			console.log("Frame View["+StaticGraphOBJArray.length+"]  imageIndex : "+imageIndex+", StaticGraphArray["+i+"] : "+$(StaticGraphOBJArray[i]).attr("class")+" ==> [frame_"+imageIndex+"]"+", StaticGraphArray["+i+"]"+StaticGraphArray[i]);
			if($(StaticGraphOBJArray[i]).hasClass(("frame_"+imageIndex).trim())){
				$("#frame_right_content").html(StaticGraphArray[i]);
			}else{
				console.log("Frame View["+StaticGraphArray.length+"]  imageIndex : "+imageIndex+", StaticGraphArray["+i+"] : "+$(StaticGraphArray[i]).html());
			}
		}
		$("#frame_right_content").append(framePageTag());
	}
	
	$("#frame_bottom_btn").html(bottomBtnHtml);
	$("#frame_now_page").val(frameIndex);
	$("#frame_max_page").val(viewIndex);
	
	$(".alert_class").hide();
	imageEventBind();
	console.log("$('.alert_btn').length : "+$(".alert_btn").length+", StaticAlertArray.length : "+StaticAlertArray.length);
	funtResize(parseInt($("#font_size").val()));
	//2022 08 02 Park.J.S. Update : 팜업 존재하면 무조건 실행 if($(".alert_btn").length != 0) { 
	//if($(".alert_btn").length != 0) {
	if($(".alert_btn") && StaticAlertArray.length > 0) {
		$("#frame_alert_flag").val("true");
		if($(".alert_btn").length == 1) {
			$(".alert_btn").click();
			//2023 01 19 Park.J.S. ADD : alert만 존재 하는 경우 있을경우 다음 장으로 안넘어가게 추가
			viewFlag = true;
		}else if($(".alert_btn").length != 0) {
			$(".alert_btn")[0].click();
			//2023 01 19 Park.J.S. ADD : alert만 존재 하는 경우 있을경우 다음 장으로 안넘어가게 추가
			viewFlag = true;
		}
		$("#frame_alert_btn_num").val(frameIndex);
	}
	console.log("viewFlag : "+viewFlag);
	/*화면이 없을경우? */
	if(!viewFlag){//화면이 없을경우 다음 페이지 혹은 이전 페이지 호출;
		frameBtn(tempPageStatus);
	}
	
	//2023 01 05 Park.J.S. ADD : 이미지 확대 사용위해 추가
	$("div").filter(function(){
		if($(this).attr("name") && $(this).attr("name") == "pngDivArea"){
			console.log("pngDivArea[Del] : "+ $(this));
			//$(this).remove();
		}
	});
	
	console.log("Fin Frame View");
	
}

function framePageTag() {
	var graphPageTag = "";
	var graphPageNum = 0;
	var graphPageClass = "";
	for(var i=0; i<StaticGraphArray.length; i++) {
		graphPageClass = "";
		if(i == 0) {
			graphPageClass = "frame_now_page";
		}
		graphPageNum++;
		/*
		graphPageTag += "	<a href='javascript:void(0);' id='frame_page_id_" + i + "' class='frame_basic_num " + graphPageClass + "' onclick=\"frameGraphMove('" + i + "')\">";
		graphPageTag += 		graphPageNum;
		graphPageTag += "	</a>";
		*/
	}

	return graphPageTag;
}

function frameGraphMove(pNum) {
	$("#frame_right_content").html(StaticGraphArray[pNum]);
	$("#frame_right_content").append(framePageTag());
	imageEventBind();
	$(".frame_basic_num").removeClass("frame_now_page");
	$("#frame_page_id_" + pNum).addClass("frame_now_page");
}
//2022 08 02 Park.J.S. 페이지 자동 처리 위해 추가
var tempPageStatus;
var isAlertChk = true;

function frameBtn(status) {
console.log("!@!@", status);
   	tempPageStatus = "";
   	var frameNPage = $("#frame_now_page").val();
   	var frameMaxPage = $("#frame_max_page").val();
   	var movePage = 0;
   /* console.log('($("#view_mode").val() : '+$("#view_mode").val()); - 옵션 : 프레임 선택시 */
    var PageNfrstAlrtContent = 0;

   $(".contents_box").hide();
   $(".step_div").hide();
   $(".next_branch_content").hide();

    /*앞에 알럿이 없을 때*/
    if ($(".nAFChk").first().is(":visible")) {
      const observer = new MutationObserver(() => {
            //첫화면 표시 메소드*/
           $(".nAFChk").show();
           $(".aFChk").hide();
           $(".aGCon").hide();
           $(".a_g_Con").hide();
           $(".AgCon").hide();
       });
        observer.disconnect();
       
        observer.observe(document.getElementById("frame_contents"), {
            childList: true,
            subtree: true
        });
    }
    
     if ($(".aFChk").first().is(":visible")) {
          $(".nAFChk").hide();
          $(".aFChk").show();
          $(".aGCon").hide();
          $(".a_g_Con").hide();
          $(".AgCon").hide();
          return;
    }

    if ($(".aGCon").first().is(":visible")) {
          
          if ($(".AgCon").first().is(":visible")) {
                  $(".AgCon").show();
                  $(".aGCon").hide();
          
          }else{ $(".aGCon").show(); }
          
          $(".aFChk").hide();
          $(".a_g_Con").hide();
          $(".nAFChk").hide();
           return;
    }
    
    if ($(".a_g_Con").first().is(":visible")) {
          $(".nAFChk").hide();
          $(".aFChk").hide();
          $(".aGCon").hide();
          $(".a_g_Con").show();
          $(".AgCon").hide();
            return;
    }
    
    /* 여기 하는 중 */
    if ($(".AgCon").first().is(":visible")) {
          $(".aFChk").hide();
          $(".aGCon").hide();
          $(".a_g_Con").hide();
          $(".AgCon").show();
          $(".nAFChk").hide();
            return;
    }

    
  /*  *//*앞에 알럿이 없을 때*//*
    if ($(".n_A_F").first().is(":visible")) {
      const observer = new MutationObserver(() => {
            //첫화면 표시 메소드*//*
           $(".n_A_F").show();
           $(".A_F_Ne").hide();
        
           $(".A_F_G_Ne").hide();
              $(".A_G_Con").hide();
       });
    
        observer.observe(document.getElementById("frame_contents"), {
            childList: true,
            subtree: true
        });
    }
    
     if ($(".A_F_Ne").first().is(":visible")) {
          $(".n_A_F").hide();
          $(".A_F_Ne").show();
        
          $(".A_F_G_Ne").hide();
            $(".A_G_Con").hide();
    }
    
    if ($(".A_G_Con").first().is(":visible")) {
          $(".n_A_F").hide();
          $(".A_F_Ne").hide();

          $(".A_F_G_Ne").hide();
                    $(".A_G_Con").show();
    }

    if ($(".A_F_G_Ne").first().is(":visible")) {
          $(".n_A_F").hide();
          $(".A_F_Ne").hide();
     
          $(".A_F_G_Ne").show();
               $(".A_G_Con").hide();
    }
*/

    /* next 버튼 */
   	if(status == "next") {
        movePage = Number(frameNPage) + 1;

		if(movePage > Number(frameMaxPage)){

		}else{
			tempPageStatus = status;
			$(".contents_box").hide();
            $(".noAlertFirstCon").hide();
            $(".AlertFirstCon").hide();

            $(".next_branch_content").show();
            return;
		}
    }else if(status == "prev") {
       /* movePage = Number(frameNPage) - 1;
        tempPageStatus = status;
        $(".ac-object").show();*/
    }

}

//정비절차 - 첫화면 표시 메소드
/*
function checkAlertVisibility() {
    console.log("!@");
   // 페이지가 비동기 로딩되므로 DOM 업데이트를 기다림
   setTimeout(() => {
     */
/*  const firstAlert = $("alert").first();
       if (firstAlert.length) {
           return;
        
       } else {*//*

                   $(".noAlertFirstCon").show();
                   $(".AlertFirstCon").hide();

    */
/*   }
        return status;*//*

    }, 50); // DOM 변경이 끝난 후 실행
}
*/


function framePopupEvent(alertIndex, alertType, alertText) {
	//2022 12 07 Park.J.S. Update : 프레임 보기에서 경고문이 연속으로 나올경우, 경고문이 전부 팝업되게 수정
	$("#frame_alert_btn_num").val(alertIndex);
	$("#frame_alert_type").removeClass("warning");
	$("#frame_alert_type").removeClass("caution");
	$("#frame_alert_type").removeClass("note");
	$("#frame_alert_type").addClass(alertType);
	$("#frame_alert_string").html(alertText);
	$("#frame_alert_cont").html(StaticAlertArray[alertIndex]);
	$("#frame_alert_body").show();
	$("#frame_alert_cont").css("font-size", $("#cont_font_size").val() + "px");
	
}

function frameCloseAlert() {
	$("#frame_alert_body").hide();
	
	var alertFlag = $("#frame_alert_flag").val();

	var alertLiSize = $(".alert_btn").length;
	var alertBtnNum = $("#frame_alert_btn_num").val();
	alertBtnNum++;
	console.log("alertFlag : "+alertFlag+", "+$(".alert_li_" + alertBtnNum).attr("class")+", alertLiSize : "+alertLiSize+", alertBtnNum : "+alertBtnNum);
	if(alertFlag == "true" && $(".alert_li_" + alertBtnNum).attr("class") != "") {
		//2022 12 02 Park.J.S. Update : 프레임 보기에서 경고문에서 닫기를 여러 번 눌러야 닫히는 현상 수정위해 주석 처리 기존 로직중에 다음 경고문이 있을경우 해당 경고문 띄우는 부분 때문에 존재함
		//2022 12 07 Park.J.S. Update : 프레임 보기에서 경고문이 연속으로 나올경우, 경고문이 전부 팝업되게 수정
		if(alertLiSize && alertLiSize > 1){
			$(".alert_li_" + alertBtnNum).click();
		}
		$("#frame_alert_btn_num").val(alertBtnNum)
	} else {
		$("#frame_alert_flag").val("");
	}
}

/**
 * 2022 03 22 Park.J.S : 표지관련 수정
 * @param toKey
 * @param tocoId
 * @returns
 */
function viewCover(toKey, tocoId) {
	distCont = $("#dist_cont_value").val();
	warnningCont = $("#warnning_cont_value").val();
	cont = $("#notice_cont_value").val();
	bizCode = $("#bizCodeCommon").val();
	
	$.ajax({
		type : "POST",
		url : $("#proectName").val()+"admin/coverInfo.do",
		data : "to_key=" + encodeURIComponent(toKey) + "&toco_id=" + tocoId,
		dataType : "json",
		success : function(data) {
			$("#to_key").val(toKey);
			$("#cover_to_key").text(toKey);
			
			//20191206 edit LYM 사업(프로젝트) 구분
			//20200313 edit LYM 명칭 중에 2줄로 나오는 것들은 Tag 가 적용되도록 .append() 사용 
			var coverTech			= "";
			var coverPublishNo		= "";		//20200313 add LYM
			var coverDate			= "";
			var coverMainName		= "";
			var coverSubName		= "";
			var coverSubName2		= "";
			var coverPart			= "";
			var coverPart2			= "";
			var coverImage			= "";
			var coverLogo			= "";		//20201209 add LYM
			var coverRevision		= "";		//2023.04.12 - T50용 개정메시지 추가 - jingi.kim
			console.log("data.GV_BIZ_CODE : "+data.GV_BIZ_CODE);
			
			/**
			 * 2022 09 28 _E로 끝나면 영어로 교체
			 */
			//2024.11.08 - KTA, 영문 교범 페이지 조건 추가 - jingi.kim
			var isEngCover = false;
			if(toKey && toKey != "" && toKey.substring(toKey.length-2,toKey.length) == "_E" && data.GV_BIZ_CODE == "BLOCK2") { isEngCover = true; }
			if ( data.GV_BIZ_CODE == "KTA" ) {
				var _engToList = $("a[name*='(eng)']", "#to_list");
				if ( _engToList.length > 0 ) {
					var _meToKey = _engToList.next().find("a[name='"+toKey+"']");
					if ( _meToKey.length == 1 ) { isEngCover = true; }
				}
			}
			if ( toKey && toKey != "" && toKey.substring(0,4) == "T.O." && data.GV_BIZ_CODE == "KTA" ) { isEngCover = true; }
			if( isEngCover ){
				console.log("Eng Corver");
				distCont = $("#dist_cont_value_e").val();
				warnningCont = $("#warnning_cont_value_e").val();
				cont = $("#notice_cont_value_e").val();
				
				$("#dist_cont_title").attr("style","display:none;");
				$("#warnning_cont_title").attr("style","display:none;");	
				$("#notice_cont_title").attr("style","display:none;");
				$("#origin_tit").attr("style","display:none;");
				$("#cover_to_tech_txt").attr("style","display:none;");
				
				$("#dist_cont_title_e").attr("style","display:block;");
				$("#warnning_cont_title_e").attr("style","display:block;");	
				$("#notice_cont_title_e").attr("style","display:block;");
				$("#origin_tit_e").attr("style","display:block;");
				$("#cover_to_tech_txt_e").attr("style","display:inherit;");
				
			}else{
				$("#dist_cont_title").attr("style","display:block;");
				$("#warnning_cont_title").attr("style","display:block;");
				$("#notice_cont_title").attr("style","display:block;");
				$("#origin_tit").attr("style","display:block;");
				$("#cover_to_tech_txt").attr("style","display:inherit;");
				
				$("#dist_cont_title_e").attr("style","display:none;");
				$("#warnning_cont_title_e").attr("style","display:none;");	
				$("#notice_cont_title_e").attr("style","display:none;");
				$("#origin_tit_e").attr("style","display:none;");
				$("#cover_to_tech_txt_e").attr("style","display:none;");
			}
			
			if (data.GV_BIZ_CODE == "HUSS") {	//HOOO용도
				coverTech = "기술교범";
				coverDate = "2020.02.26.";
				//coverMainName	= "항만감시체계(R&D) WSD-230K";
				coverMainName	= "항만감시체계(R&D)<br>(모델 : WSD-230K)";
				coverSubName		= toKey;
				coverPart				= "6350-37-545-2581";

				if(toKey == "장비운용 교범") {
					coverTech += " 2-50-06(158)-02";
					coverPublishNo = "11-1690000-002069-01";
					
				} else if(toKey == "부대정비 교범") {
					coverTech += " 1-50-06(159)-01";
					coverPublishNo = "11-1690000-002071-01";
				
				} else if(toKey == "야전정비 교범") {
					coverTech += " 1-50-06(159)-02";
					coverPublishNo = "11-1690000-002074-01";
				
				} else if(toKey == "보급교범") {
					coverTech += " 1-50-06(159)-04";
					coverPublishNo = "11-1690000-002076-01";
				
				} else if(toKey == "회로도 교범") {
					coverTech += " 2-50-06(159)-04";
					coverPublishNo = "11-1690000-002077-01";
				
				} else if(toKey == "원격전시장치 교범") {
					coverTech += " 2-50-06(160)-04";
					coverPublishNo = "11-1690000-002078-01";
					coverMainName = "항만감시체계(R&D)<br>원격전시장치<br>(모델 : WSD-230K)";
				}
				
				if(toKey == "원격전시장치 교범") {
					coverSubName = "장비운용, 부대 및 야전 정비교범<br>(수리부속품 및 특수공구목록 포함)";
					coverPart = "(7022-37-545-5657)";
					coverImage		= $("#proectName").val()+"web/image/cover/cover_huss_02.png";
				} else {
					coverPart		= "(" + coverPart + ")";
					coverImage		= $("#proectName").val()+"web/image/cover/cover_huss_01.png";
				}
				
				/*
				coverTech = "기술교범";
				coverDate = "2018. XX. XX";
				coverMainName	= "항만감시체계 R&D IETM";
				coverSubName		= toKey;
				coverPart				= "XXXX-XX-XXX-XXXX";
				if(toKey == "원격전시장치 교범") {
					coverImage		= $("#proectName").val()+"web/image/cover/cover_huss_02.png";
				} else {
					coverImage		= $("#proectName").val()+"web/image/cover/cover_huss_01.png";
				}
				*/
				
			} else if (false) {	//EOOO용도
				coverTech = "기술교범";
				coverDate = "2018. XX. XX";
				coverMainName	= "전자광학위성감시체계(EOSS)";
				coverSubName		= toKey;
				coverPart				= "XXXX-XX-XXX-XXXX";
				coverImage			= $("#proectName").val()+"web/image/cover/cover_eoss_01.png";

				coverPart		= "(" + coverPart + ")";

			} else if (data.GV_BIZ_CODE == "MDV") {	//MOOO용도
				if(toKey == "장비 운용교범") {
					coverPublishNo = "37-982O000-016821-01";
					coverTech = "기술교범 2-00-11(301)-01";
					coverDate = "2020.12.20.";
				} else if(toKey == "부대 및 야전 정비교범") {
					coverPublishNo = "37-982O000-016822-01";
					coverTech = "기술교범 1-00-11(301)-02";
					coverDate = "2020.12.20.";
				} else if(toKey == "보급교범") {
					coverPublishNo = "37-982O000-016823-01";
					coverTech = "기술교범1-00-11(301)-04";
					coverDate = "2020.12.20.";
				}
				coverMainName	= "자율항해 무인기뢰처리기";
				coverSubName		= toKey;
				coverPart				= "소해함 MSH, MHC용";
				coverPart2			= "(모델  :  KMDV-1)<br><span style='font-size:14px;'>(1075-37-525-6820)</span>";
				coverImage			= $("#proectName").val()+"web_mdv/image/cover/cover_mdv_01.png";
				
				coverPart		= "(" + coverPart + ")";

			} else if (bizCode == "EA") {	//EO용도
				coverTech = toKey ;
				coverDate = "20XX.XX.XX";
				coverMainName = "ALTAIR SYSTEM";
				coverImage		= $("#proectName").val()+"web_ea/image/cover/cover_ea_01.png";
				coverLogo		= $("#proectName").val()+"web/image/cover/cover_logo.png";
				
				//20201209 add LYM
				if(toKey == "TM(X)-XXXXXXXXXX-XX-10") {
					coverSubName = "OPERATOR’S TECHNICAL MANUAL<br> FOR";
					coverPart = "LEVEL 1";
				} else if(toKey == "TM(X)-XXXXXXXXXX-XX-20") {
					coverSubName = "ORGANIZATIONAL MAINTENANCE TECHNICAL MANUAL<br> FOR";
					coverPart = "LEVEL 2";
				} else if(toKey == "TM(X)-XXXXXXXXXX-XX-30") {
					coverSubName = "INTERMEDIATE(DIRECT SUPPORT) MAINTENANCE<br> TECHNICAL MANUAL<br>FOR";
					coverPart = "LEVEL 3";
				} else if(toKey == "TM(X)-XXXXXXXXXX-XX-40") {
					coverSubName = "INTERMEDIATE(GENERAL SUPPORT) MAINTENANCE<br> TECHNICAL MANUAL<br> FOR";
					coverPart = "LEVEL 4";
				} else if(toKey == "TM(X)-XXXXXXXXXX-XX-23P") {
					coverSubName = "ORGANIZATIONAL AND INTERMEDIATE(DIRECT SUPPORT)<br> MAINTENANCE<br> REPAIR PARTS AND SPECIAL TOOLS LIST(RPSTL) MANUAL<br> FOR";
					coverPart = "LEVEL 2, 3";
				} else if(toKey == "TM(X)-XXXXXXXXXX-XX-40P") {
					coverSubName = "INTERMEDIATE(GENERAL SUPPORT) MAINTENANCE<br> REPAIR PARTS AND SPECIAL TOOLS LIST(RPSTL) MANUAL<br> FOR";
					coverPart = "LEVEL 4";
				}
				
				coverPart		= "(" + coverPart + ")";
				$("#cover_to_part").attr("style" , "font-size:17px;");
				$("#cover_to_sub_name").attr("style" , "font-size:15px;");
				
			}  else if (data.GV_BIZ_CODE == "LSAM") {	//LOOO용도
				coverTech = toKey;
				
				coverDate = "2021.XX.XX";
				
				coverMainName	= "장거리 지대공 유도무기";
				coverSubName		= toKey;
				coverPart				= "L-SAM";
				coverImage			= $("#proectName").val()+"web/image/cover/cover_lsam_01.png";
	
				coverPart		= "(" + coverPart + ")";
				
				coverDate = "2021.XX.XX";
				
				coverMainName	= "교범 대상의 명칭";
				//coverSubName		= "선택 교범명";
				coverPart				= "IETM명";
	
				coverPart		= "(" + coverPart + ")";
				
			} else if (data.GV_BIZ_CODE == "AR5G") {	//AR5G용도
				coverTech = "기술교범";
				coverDate = "2020.11.20";
				coverMainName	= "AR방식 5G기반";
				coverSubName		= toKey;
				coverPart				= "AR5G";
				if(toKey == "AW139") {
					coverImage	= $("#proectName").val()+"web_ar5g/image/cover/cover_ar5g_02.png";
				} else if(toKey == "Bell412") {
					coverImage	= $("#proectName").val()+"web_ar5g/image/cover/cover_ar5g_03.png";
				} else {
					coverImage	= $("#proectName").val()+"web_ar5g/image/cover/cover_ar5g_01.png";
				}
	
				coverPart		= "(" + coverPart + ")";
			} else if (data.GV_BIZ_CODE == "T50") {	//T50 2021 06 
				//TODO
				coverTech = toKey;
				coverDate = "2021.06.10";
				
				coverMainName	= "T50";
				coverSubName		= toKey;
				coverPart				= "T-50";
				coverImage			= $("#proectName").val()+"web/image/cover/cover_t50_01.png";
	
				coverPart		= "(" + coverPart + ")";
				
			}else{
				coverTech = toKey
				if(data.coverDto && data.coverDto.coverVerDate){
					coverDate = data.coverDto.coverVerDate;
				}else{
					coverDate = "";
				}
				coverSubName		= toKey;
				coverPart		= "(" + coverPart + ")";
			}
			console.log("coverDto : "+JSON.stringify(data.coverDto));
			
			// 2023.04.12 - T50용 개정메시지 추가 - jingi.kim
            if(data.GV_BIZ_CODE == "BLOCK2") {
                dspRevisionText(data);
            }
            
			//2021 08 12 미사용 하게 수정
			//$("#cover_to_tech").text(coverTech);
			$("#cover_to_date").text(coverTech);
			if(data.coverDto.totype != null && data.coverDto.totype != "") {
				$("#cover_to_date").text(data.coverDto.totype+" "+coverTech);
			}
			$("#cover_publish_no").text(coverPublishNo);	//20200313 add LYM
			/*
			if(data.coverDto.coverDate == null || data.coverDto.coverDate == "") {
				$("#cover_to_date").text(coverDate);
			} else {
				$("#cover_to_date").text(data.coverDto.coverDate);
			}
			*/
			if(data.coverDto.coverTitle == null || data.coverDto.coverTitle == "") {
				//$("#cover_to_main_name").text(coverMainName);
				$("#cover_to_main_name").text("");
				$("#cover_to_main_name").append(coverMainName);
				console.log("In cover_to_main_name Type 1 : "+coverMainName);
				
			//2024.11.14 - SENSOR 추가 - chohee.cha
			} else {
				if (data.GV_BIZ_CODE == "LSAM" || data.GV_BIZ_CODE == "NLS" || data.GV_BIZ_CODE == "KBOB" || data.GV_BIZ_CODE == "KICC" || data.GV_BIZ_CODE == "MUAV" || data.GV_BIZ_CODE == "SENSOR") {
					$("#cover_to_main_name").html(data.coverDto.coverTitle);			
				}else if (data.GV_BIZ_CODE == "KTA"){
					/* 2024.01.29 add JSH
					 * KTA 계열 교범일때, 자간 간격 넓히고(#cover_to_sub_name과 픽셀 다르게 설정)
					 * &#13;데이터가 있으면 줄바꿈으로 변환.
					 */
					$("#cover_to_main_name").html('<div style="line-height:27px;">' + data.coverDto.coverTitle.replace(/\&#13;/gi, "<br/\>"));				
				}else{
					$("#cover_to_main_name").text(data.coverDto.coverTitle);
				}
			}
			if(data.coverDto.coverSubTitle == null || data.coverDto.coverSubTitle == "") {
				//$("#cover_to_sub_name").text(coverSubName);
				//$("#cover_to_sub_name2").text(coverSubName2);
				$("#cover_to_sub_name").text("");
				$("#cover_to_sub_name").html(coverSubName);
			} else {
				if (data.GV_BIZ_CODE == "LSAM" || data.GV_BIZ_CODE == "NLS" || data.GV_BIZ_CODE == "KBOB" || data.GV_BIZ_CODE == "KICC" || data.GV_BIZ_CODE == "MUAV" || data.GV_BIZ_CODE == "SENSOR") {
					$("#cover_to_sub_name").html(data.coverDto.coverSubTitle);
				}else if (data.GV_BIZ_CODE == "KTA"){
					/* 2024.01.29 add JSH
					 * KTA 계열 교범일때, 자간 간격 넓히고(#cover_to_main_name과 픽셀 다르게 설정)
					 * &#13;데이터가 있으면 줄바꿈으로 변환.
					 */
					$("#cover_to_sub_name").html('<div style="line-height:37px;">' + data.coverDto.coverSubTitle.replace(/\&#13;/gi, "<br/\>")); 					
				}else{
					$("#cover_to_sub_name").html(data.coverDto.coverSubTitle);
				}
			}
			if(data.coverDto.modifyUserId && data.coverDto.modifyUserId != "") {
				$("#revinfo_cont_div").css("display","block");
				$("#revinfo_cont").html(data.coverDto.modifyUserId);
			} else {
				$("#revinfo_cont_div").css("display","none");
			}
			if(data.fileDto == null) {
				//$("#cover_img_view").hide();
				
				// 2024.02.29 - 교범 표지 편집이미지가 없으면 기본이미지로 고정 - JSH
				$("#cover_img_view").show();
				$("#cover_img_view").attr("src" , $("#proectName").val()+"web/image/cover/"+"cover_main.png");
			}
			if(data.fileDto != null) {
				$("#cover_img_view").show();
				$("#cover_img_view").attr("src" , $("#proectName").val()+"web/image/cover/"+data.fileDto.fileStrNm+"."+data.fileDto.fileType+"");
				console.log($("#proectName").val()+"web/image/cover/"+data.fileDto.fileStrNm+"."+data.fileDto.fileType+"");
			}
			//$("#cover_to_part").text(coverPart);
			//$("#cover_to_part2").text(coverPart2);
			$("#cover_to_part").text("");
			//$("#cover_to_part").append(coverPart);
			
			//20201127 add LYM MDV-Ⅱ 사업에서 필요
			if(coverPart2 != ""){
				$("#cover_to_part2").html(coverPart2);
			}
			/*
			 * 2021 08 12 강제로 특정 이미지 사용할경우 
			$("#cover_img_view").attr("src" , coverImage);
			console.log("coverImage : "+coverImage);
			*/
			//20201209 add LYM EA 사업에서 필요
			if(coverLogo != ""){
				$("#cover_img_logo").attr("src" , coverLogo);
			}
			
			//2022 12 06 jysi EDIT : LSAM교범 coverInsert시 값이 ""일때 기본구성이 보이지 않아 ""값 조건 추가
			//                       (coverDistCont, coverWarnningCont, coverCont)
			if(data.coverDto.coverDistCont == null || data.coverDto.coverDistCont == "") {
				$("#dist_cont").text(distCont);
			} else {
				$("#dist_cont").text(data.coverDto.coverDistCont);
			}
			if(data.coverDto.coverWarnningCont == null || data.coverDto.coverWarnningCont == "") {
				$("#warnning_cont").text(warnningCont);
			} else {
				if (data.GV_BIZ_CODE == "LSAM" || data.GV_BIZ_CODE == "NLS" || data.GV_BIZ_CODE == "KBOB" || data.GV_BIZ_CODE == "KICC" || data.GV_BIZ_CODE == "MUAV" || data.GV_BIZ_CODE == "SENSOR") {
					$("#warnning_cont").html(data.coverDto.coverWarnningCont);
				}else{
					$("#warnning_cont").text(data.coverDto.coverWarnningCont);
				}
			}
			if(data.coverDto.coverCont == null || data.coverDto.coverCont == "") {
				$("#notice_cont").text(cont);
			} else {
				$("#notice_cont").text(data.coverDto.coverCont);
			}
			$("#cover_to_ori_date").text("");
			$("#cover_to_ver_info").text("");
			if(data.coverDto.coverDate != null) {
				if(data.GV_BIZ_CODE != "BLOCK2"){
					$("#cover_to_ori_date").text(data.coverDto.coverDate);
					if(data.coverDto.coverVersion != null && data.coverDto.coverVersion != "") {
						$("#cover_to_ver_info").text(data.coverDto.coverVersion);
					}else{
						$("#cover_to_ver_info").text("변경판 " + data.coverDto.coverVerDate);
					}
				}else{
					$("#cover_to_ori_date").text(data.coverDto.coverDate.replace(/-/gi,"."));
				}
			}else if(data.coverDto.coverVerDate != null) {
				$("#cover_to_ori_date").text(data.coverDto.coverVerDate);
				if(data.GV_BIZ_CODE != "BLOCK2"){
					if(data.coverDto.coverVersion != null && data.coverDto.coverVersion != "") {
						$("#cover_to_ver_info").text(data.coverDto.coverVersion);
					}else{
						$("#cover_to_ver_info").text("변경판 " + data.coverDto.coverVerDate);
					}
				}
			}
			
			if(data.coverDto.coverPart != null && data.coverDto.coverPart != "") {
				$("#cover_to_part2").html(data.coverDto.coverPart);
				//2024.01.31 - coverPart 데이터가 있을때 <h3>태그 diplay:none 처리 - JSH	
				$("#cover_to_part").parents("h3").hide();	
			}
			
			$(".contents_div").hide();
			var coverType = $("#cover_type").val();
			
			$("#main_cover").removeClass();
			if (data.GV_BIZ_CODE == "LSAM" || data.GV_BIZ_CODE == "NLS" || data.GV_BIZ_CODE == "KBOB" || data.GV_BIZ_CODE == "KICC" || data.GV_BIZ_CODE == "SENSOR") {
				$("#main_cover").addClass("book_coverLsam");
			}else{
				$("#main_cover").addClass("book_cover" + coverType);
			}
			
			//2022 06 28 Park.J.S. ADD
			if (data.GV_BIZ_CODE == "KTA") { 
				console.log("changeno : "+$("#"+tocoId).attr("changeno")+", chgdate : "+$("#"+tocoId).attr("chgdate")+", revision : "+$("#"+tocoId).attr("revision")+", startDate : "+$("#"+tocoId).attr("startDate"));
				$("#cover_to_ori_date").text($("#"+tocoId).attr("startDate"));
				$("#cover_to_ver_info").text("변경판 "+$("#"+tocoId).attr("changeno")+"  "+$("#"+tocoId).attr("chgdate"));
				$("#cover_to_date").html(coverTech+"<br/> "+$("#"+tocoId).attr("revision"));
			}
			//2024.11.08 - KTA, 영문 교범 페이지 추가 - jingi.kim
			if ( data.GV_BIZ_CODE == "KTA" ) {
				var toVerInfoText = $("#"+tocoId).attr("changeno")+"  "+$("#"+tocoId).attr("chgdate");
				if ( isEngCover ) {
					$("#cover_to_tech").hide();
					$("#cover_to_tech_e").show();
					$("#revinfo_cont_title").hide();
					$("#revinfo_cont_title_e").show();
					$("#business_title").hide();
					$("#business_title_e").show();
					$("#cover_to_ver_info").text("Change "+ toVerInfoText);
				} else {
					$("#cover_to_tech").show();
					$("#cover_to_tech_e").hide();
					$("#revinfo_cont_title").show();
					$("#revinfo_cont_title_e").hide();
					$("#business_title").show();
					$("#business_title_e").hide();
					$("#cover_to_ver_info").text("변경판 "+ toVerInfoText);
				}
				
			}
			
			if (data.GV_BIZ_CODE == "BLOCK2") { 
				if (data.coverDto.modifyUserId && data.coverDto.modifyUserId != "") { 
					$(".pup_alert_body").css("display","block");
					$(".alert_cont").html(data.coverDto.modifyUserId);
				}else{
					$(".pup_alert_body").css("display","none");
				}
				//2022 07 27 Park.J.S. ADD : 사천 요청으로 BLOCK2일경우 하단에 있던 버전 정보를 상단으로 올림
				if(data.coverDto.coverDate != null) {
					if(data.coverDto.coverVersion != null && data.coverDto.coverVersion != "") {
						$("#cover_to_date").html($("#cover_to_date").text()+"<br/>"+data.coverDto.coverVersion);
					}else{
						$("#cover_to_date").html($("#cover_to_date").text());
					}
				}else if(data.coverDto.coverVerDate != null) {
					if(data.coverDto.coverVersion != null && data.coverDto.coverVersion != "") {
						$("#cover_to_date").html($("#cover_to_date").text()+"<br/>"+data.coverDto.coverVersion);
					}else{
						$("#cover_to_date").html($("#cover_to_date").text());
					}
				}
				//2022 08 23 Park.J.S. Update : 날짜 형식변경 : - 대신 .으로 구분 필요
				var tempDateStr = $("#"+tocoId).attr("chgdate");
				if(tempDateStr){//2022 09 07 Park.J.S. Update : 변경 날짜  없는 경우 하단 문제 발생해서 추가 
					if(toKey && toKey != "" && toKey.substring(toKey.length-2,toKey.length) == "_E" && data.GV_BIZ_CODE == "BLOCK2"){
						
						//by ejkim 2022.10.06 Change의 0의 경우 미표시
						//by ejkim 2022.10.06 Change의 0의 경우 변경판 문구 텍스트 삭제
						if( $("#"+tocoId).attr("changeno") == 0 ) {
							//$("#cover_to_ver_info").text("Change "+"  "+tempDateStr.replace(/-/gi, "."));
						}else {
							$("#cover_to_ver_info").text("Change "+$("#"+tocoId).attr("changeno")+"  "+tempDateStr.replace(/-/gi, "."));
						}
						
					}else{
						//by ejkim 2022.10.06 Change의 0의 경우 미표시
						//by ejkim 2022.10.06 Change의 0의 경우 변경판 문구 텍스트 삭제
						if( $("#"+tocoId).attr("changeno") == 0 ) {
							//$("#cover_to_ver_info").text("변경판 "+"  "+tempDateStr.replace(/-/gi, "."));
						}else {
							$("#cover_to_ver_info").text("변경판 "+$("#"+tocoId).attr("changeno")+"  "+tempDateStr.replace(/-/gi, "."));
						}
						
					}
				}
			}
			
			//2023.11.20 - KBOB - jingi.kim
			//2023.12.15 - KICC - JSH
			if (data.GV_BIZ_CODE == "KBOB" || data.GV_BIZ_CODE == "KICC" || data.GV_BIZ_CODE == "MUAV" || data.GV_BIZ_CODE == "SENSOR") { 
				$("#cover_to_ori_date").text(data.coverDto.coverVerDate);
				$("#cover_to_ver_info").text("");
			/* 2024.01.29 add JSH
			 * KTA 계열 교범일때, tpinfo 데이터 가져오고, &#13; 데이터가 있으면 줄바꿈으로 변환 */
			/* 2024.02.16 add JSH
			 * KTA 계열 교범일때, coverPart 데이터 가져오고, &#13; 데이터가 있으면 줄바꿈으로 변환 */
			}else if(data.GV_BIZ_CODE == "KTA"){
				var txtTpinfo = data.coverDto.tpinfo || "";
				var txtCoverPart = data.coverDto.coverPart || "";
				$("#cover_to_tp_info").html(txtTpinfo.replace(/&#13;/gi, "<br/\>"));
				$("#cover_to_part2").html(txtCoverPart.replace(/&#13;/gi, "<br/\>"));
			}else{
				
			}
			
			$("#main_cover").show();
			$(".loading").hide();
			$("#div_more_button").hide();
		},
		error : function() {
			//alert("error");
		}
		
		
	});
}

//2023.04.12 - T50용 개정메시지 표시 함수 추가 - jingi.kim
function dspRevisionText(data) {
    if ( !data.coverDto ) return;
    if ( !data.coverDto.coverData || data.coverDto.coverData == "" ) return;
    if ( !data.coverDto.coverTitle || data.coverDto.coverTitle == "" ) return;
    if ( !data.coverDto.coverSubTitle || data.coverDto.coverSubTitle == "" ) return;
    var objRev = $("#revision_cont");
    if ( objRev.length == 0 ) return;
    var objRevDate = $("#revision_date", objRev);
    if ( objRevDate.length == 0 ) return;
    var objRevTxt = $("#revision_txt", objRev);
    if ( objRevTxt.length == 0 ) return;
    
    // display
    objRev.show();
    
    // set data
    var strLastname = ' ';
    var objectMarker = "를";
    strLastname = data.coverDto.coverSubTitle.slice(-1);
    if (strLastname.charCodeAt() >= 0xAC00 && strLastname.charCodeAt() <= 0xD7A3) {
        objectMarker = (strLastname.charCodeAt() - 0xAC00) % 28 > 0 ? "을" : "를";
    }
    coverRevision = " " + data.coverDto.coverTitle + " - " + data.coverDto.coverSubTitle + objectMarker + " ";
    objRevDate.text(data.coverDto.coverData.replace(/-/gi, "."));
    objRevTxt.text(coverRevision);
    
}

/**
 * 2023 01 30 Park.J.S. Update : vehicleExepFlag 추가 Y로 넘어 올경우 기종 선택된 값 무시하고 조회 가능하게 수정
 * 현재 이미지 때문에 사용중이고 이미지 자체에 vehicle 설정된 경우는 나타나지 않음 관련해서 추가 수정이 필요한 경우 tocoContView에서도 사용하게 수정 한후에 
 * java단에서도 처리하게 수정 필요
 * @param toKey
 * @param tocoId
 * @param folderChk
 * @param toType
 * @param vehicleExepFlag
 * @returns
 */
function tocoView(toKey, tocoId, folderChk, toType, vehicleExepFlag) {
	$("#main_contents").html("");
	familyChange($("#font_family").val());
	console.log("Call tocoView : "+folderChk);
	if(folderChk == "Y") {
		console.log("tocoView folderChk Y");
		//2024.03.27 - Topic, SubTopic 일 경우에만 MULTI 가능하도록 추가 - jingi.kim
		//2024.04.16 - BLOCK2, KTA만 적용하도록 제한 - jingi.kim
		if ( $("#bizCode").length > 0 && ( $("#bizCode").val() == "BLOCK2" || $("#bizCode").val() == "KTA" ) ) {
			selectTocoIdTopic(toKey, tocoId, folderChk, toType, vehicleExepFlag);
		} else {
			selectTocoId(toKey, tocoId, folderChk, toType, vehicleExepFlag);
		}
	} else {
		console.log("tocoView folderChk N");
		tocoContView(tocoId, "single", "", "true");
	}
}


//2024.03.27 - Topic, SubTopic 일 경우에만 MULTI 가능하도록 추가 - jingi.kim
function selectTocoIdTopic(toKey, tocoId, folderChk, toType, vehicleExepFlag) {
	console.log("CALL selectTocoIdTopic");
	$(".loading").show();
	var vehicle = $("#vehicle_type").val();
	//2023 01 30 Park.J.S. ADD :  vehicle 처리 일경우 모든 경우의 수에 해당하는 T로 치환 처리
	if(vehicleExepFlag && "Y" == vehicleExepFlag){
		vehicle = "T";
	}
	staticStep = 0;
	
	$.ajax({
		type : "POST",
		url : $("#proectName").val()+"ietm/selectToco.do",
		data : "toKey=" + encodeURIComponent(toKey) + "&tocoId=" + tocoId + "&vehicle=" + vehicle + "&toType=" + toType,
		dataType : "json",
		success : function(data) {
			var vcKind = $("#vc_kind").val();
			var result = "";
			var rtList = data.rtList;
			var ipbChk = data.ipbChk;
			if(rtList.length == 0) {
				$("#main_cover").hide();
				$(".main").hide();
				$(".no_data_img").show();
				$(".loading").hide();
				return;
			}
			//console.log("toKey=" + encodeURIComponent(toKey) + "&tocoId=" + tocoId + "&vehicle=" + vehicle + "&toType=" + toType);
			//현재 선택된 toco 타입
			var firstTocoType = data.rtList[0].tocoType;
			//console.log("selectTocoIdTopic folderChk : "+folderChk+", firstTocoType : "+firstTocoType+", vcKind : "+vcKind+", rtList.length : "+rtList.length+", ipbChk : "+ipbChk);
			//console.log("selectTocoIdTopic folderChk : "+folderChk+", firstTocoType : "+firstTocoType+", $(#tocoId) : "+$("#"+tocoId)+", $(#tocoId).attr(folderchk) : "+$("#"+tocoId).attr("folderchk")+", $(#view_mode).val() : "+$("#view_mode").val());
			
            //2024.03.27 - Topic, SubTopic 일 경우에만 MULTI 가능하도록 추가 - jingi.kim
            //2024.04.16 - BLOCK2, KTA만 적용하도록 제한 - jingi.kim
			let oBizCode = $('#bizCode');
			if ( oBizCode.length > 0 ) {
				if ( oBizCode.val() == 'BLOCK2' || oBizCode.val() == 'KTA' ) {
					if(folderChk == "Y" && data.ipbChk != true && (vcKind != "03" && vcKind != "04")) {
						if ( ipbChk != true ) {
							let outputmode = $("#output_mode").val() || "02";
							let tocotype = data.tocoType || "";
							if ( outputmode == "01" && (tocotype.toUpperCase() == "TOPIC" || tocotype.toUpperCase() == "SUBTOPIC" || tocotype.toUpperCase() == "SECTION") ) {
								tocoDivMake(tocoId);
								tocoContView(tocoId, "mult", folderChk, "true", true);
								
								return;
							}
						}
					}
				}
			}
			
			if(folderChk == "Y" && firstTocoType != "IPB" && (vcKind != "03" && vcKind != "04")) {
				$("#main_empty_page").show();
				tocoDivMake(tocoId);
				tocoContView(tocoId, "single", folderChk);
				
				//2024.07.11 - KTA, Chapter에서 전체표시 (MULTI)가 되는 문제로 주석처리함 - jingi.kim 
				/*if(rtList.length == 2 && ipbChk == false) {
					tocoDivMake(rtList[1].tocoId);
					tocoContView(rtList[1].tocoId, "mult");
				} else {
					result = contMake(rtList, tocoId, folderChk, 0);
					$("#main_contents").append(result);
					//funtResize(parseInt($("#font_size").val()));
				}*/
				result = contMake(rtList, tocoId, folderChk, 0);
				$("#main_contents").append(result);
				
			//2022 08 03 Park.J.S. ADD : 무제한 하위 메뉴 처리 위해 추가 list로 넘어왔으나 메뉴 구조상 폴더 일경우 하위 객체가 있다고 판단. 
			//2023 02 07 Park.J.S. ADD : 프레임 보기 예외 처리 조건에 toKey.indexOf("JG") 추가
			} else if(folderChk == "list" && firstTocoType != "IPB" && $("#"+tocoId) && $("#"+tocoId).attr("folderchk")=="Y" && (toKey.indexOf("JG") < 0 || $("#view_mode").val() != "02")){ 
				console.log("selectTocoId Make Low Level Menu Add : "+$("#view_mode").val());
				tocoDivMake(tocoId);
				//2022 08 04 Park.J.S. Update : tocoContView(tocoId, "single"); - > tocoContView(tocoId, "single", "Y","",true);
				tocoContView(tocoId, "single", "Y","",true);
				result = contMake(rtList, tocoId, folderChk, 0);
				$("#toco_cont_"+tocoId).after("<div class=\"subMenuDiv\" id=\"subMenuDiv_"+tocoId+"\">"+result+"</div>");
				console.log("result : "+result);
				//$("#main_contents").append(result);
				funtResizeSunMenu(parseInt($("#font_size").val()),"subMenuDiv_"+tocoId);
				$(".loading").hide();
				
			} else if(firstTocoType == "IPB" || vcKind == "03" || vcKind == "04") {
				tocoContView(tocoId, "single", "", "true");
				
			} else {
				//console.log("else folderChk : "+folderChk);
				if(rtList.length == 1 && folderChk != "list") {
					tocoDivMake(tocoId);
					tocoContView(tocoId, "mult");
					
				} else if(folderChk == "list") {
					//2022 07 08 Park.J.S. : FI 접근 및 위치 자료에서 IPB 로 판단되는 경우 있어서 result != ""추가  
					if(ipbChk == true && result != "") {
						staticStep++;
						$("#" + tocoId).attr("onclick", "");
						result = contMake(rtList, tocoId, folderChk, staticStep);
						$("#toco_cont_" + tocoId).html("");
						//console.log("result : "+result);
						$("#toco_cont_" + tocoId).html(result);	
						$(".loading").hide();
						funtResize(parseInt($("#font_size").val()));
						funtResizeSunMenu(parseInt($("#font_size").val()),"toco_cont_"+tocoId);
						
					} else {
						//2022 08 04 Park.J.S. Update : tocoContView(tocoId, "mult", "", ipbChk); - > tocoContView(tocoId, "mult", "", ipbChk,true);
						//console.log("SubMenu Content Call tocoId : "+tocoId);
						//2024.03.27 - Topic, SubTopic 일 경우에만 MULTI 가능하도록 추가 - jingi.kim
						//tocoContView(tocoId, "mult", "", ipbChk,true);
						tocoContView(tocoId, "single", "", ipbChk,true);
					}
					
					tocoListFocus(tocoId);
					
				} else {
					result = contMake(rtList, tocoId, folderChk, 0);
					$("#main_contents").append(result);
					//funtResize(parseInt($("#font_size").val()));
					
					$(".loading").hide();
				}
			}
		},
		error : function(data) {
			//alert("selectTocoIdTopic error");
		},
		complete : function() {
		}
	});
}

/**
 * API 함수
 * tocoContView 리팩토링 진행중
 * 2025.04.11 - osm
 * 1. xml 데이터 변환 및 로드 기능 분리
 * 2. nodeType이 IPB일 경우 실행되는 함수 기능 분리
 * 3. 목차 리스트 active효과 통합
 * 4. rdn 하이라이트 효과 기능 분리
 *    검색어가 존재 할경우 클릭이벤트 호출되게 추가 및 화면상에 보이게 추가 -> 현재 사용되는(적용되어있는) 기능인지 확인 필요
 */
function tocoContView(tocoId) {
	if (bizCode === "") return;
	if (TOKEY === "") return;
	$(".loading").show();
	$(".main-content").show(); // FI 교범 절차부분

	$.ajax({
		type: "POST",
		url: "/EXPIS/" + bizCode + "/ietm/toCont.do",
		dataType: "json",
		cache: false,
		data: {
			"to_key": TOKEY,
			"toco_id": tocoId
		},
		success: function (data) {
		    if(!data || data.returnData == "" || data.returnData == null) {
                console.log("no_data_img  show");
                $(".no_data_img").show();
                return;
            }

            // draw Contents
            renderContents(data);

            // adjust Style
            adjustStyle(data);

            // apply messages
            if (typeof(applyMessages) === "function") applyMessages();

            // version bar
            appendVersionBar(data);

            // image event - ietm_image.js
			imageEventBind();

            // ipb교범의 설명 칼럼 클릭 시 목차리스트 부분에 active 효과도 같이 주기 -> 3. active 효과 통합
            activateTocLink(tocoId);

			// cl 교범 inputbox 체크
			chkCL(data.chk);

			$("#main_contents").show();

		},
		error: function (data) {
			if (!!data.status && data.status == 401) {
			    location.href = "/EXPIS/" + bizCode + "/login";
			    return;
			}
			$(".loading").hide();
		},
		complete: function () {
			console.log("tocoContView CALL complete");
			$("#main_empty_page").hide();
			$("#main_contents").show();
			$(".loading").hide();

            //TODO: 리모컨 기능 동작으로 보임 === 확인 후 로직 정리 필요
            remoMakeEvent();

            // 경고창 팝업 관련 함수
			alertHeight(tocoId);

			// 스크롤바 여부 체크
			scrollBarChk();

			// ipb새창에서 rdn bg설정
			/*
             * 2021 11 16
             * 4. 검색어가 존재 할경우 클릭이벤트 호출되게 추가 및 화면상에 보이게 추가
             */
			const rdnVal = $("#rdn").val();
			highlightRdn(rdnVal);

            /**
             * BLOCK2 는 사천에서 가이드 받은 조건으로 색인을 구분하되,
             * KTA 는 BLOCK2 와 조건이 다르고, 너무 포괄적이기 때문에 문서 ID로 구분할 수 있도록 처리
             * redmine #660 참고.
             */
            checkIndex(tocoId);

		}
	});
}

// type에 맞게 Content 그리는 함수
//   nodeType : 컨텐츠의 종류에 대한 키 값 - xml 데이터의 기준 값
//   tocoType : 목차에 지정된 type의 값 - eXPIS3Converter - getTocoTypeWordFromCode()
function renderContents(data) {
	//TODO: MULTI 표시일 경우 target 설정 필요
	//-$("#toco_cont_" + tocoId).html(xmlData);

    //reset main content
    // let _main = $(".main-content");
    let _main = $(".main-content");
    if (_main.length == 0) return;
    _main.empty();
    let _ipbCont = $("#ipbControl");
    if (_ipbCont.length > 0) _ipbCont.hide();

    let jData = {type:"contents"};
    jData.type = data.nodeType || "";

    // FI
	if(data.nodeType == null && data.tocoType == "FI") { data.nodeType = "FI"; }
    if ( !!data.nodeType &&
            (data.nodeType.toUpperCase() === "FI" || data.nodeType.toUpperCase() === "FI_A" || data.nodeType.toUpperCase() === "AP"
            || data.nodeType.toUpperCase() === "FI_PIC" || data.nodeType.toUpperCase() === "DDS" || data.nodeType.toUpperCase() === "FI_B" )
            ) {
        appendFICanvasArea(_main);
		fiRender(data.nodeType, data.returnData, data.fiType);
		return;
    }

    // 1. xmlsheet.js로 xml데이터 변환 및 로드 기능 분리
	let xmlData = processXmlData(data);
	_main.html(xmlData);

    if ( !!data.nodeType &&
            (data.nodeType.toUpperCase() === "DI" || data.nodeType.toUpperCase() === "DI_DESC"
            || data.nodeType.toUpperCase() === "LR" || data.nodeType.toUpperCase() === "LR_DESC")
            ) {
        appendFICanvasArea(_main);
		fiRender(data.nodeType, data.returnData);
		return;
    }

    // IPB
    if ( !!data.nodeType && data.nodeType.toUpperCase() === "IPB") {
        ipbRender(data);
    }

    // REQCOND === 준비사항
    if ( !!data.nodeType && data.nodeType.toUpperCase() === "REQCOND") {
        jgReqCondRender(data);
    }

    // STEPSEQ === 정비절차
    if ( !!data.nodeType && data.nodeType.toUpperCase() === "STEPSEQ") {
        jgStepSeqRender(data);
        // 개발중
    }

}

// draw FI Canvas Box
function appendFICanvasArea(_main) {
    let _fiBox = $("#fi_canvas_box", _main);
    if (_fiBox.length == 0) {
        _main.append(`<div class="fi_canvas_box" id="fi" style="display: none">
                          <div contenteditable="false" class="main con" id="main_fi_contents"></div>
                      </div>`);
    }
}

// IPB Render
function ipbRender(data) {
    ipbCurrentPage = 1;

    const isIPB = data.nodeType.toUpperCase() === nodeType.IPB;
    if (!isIPB) return;
    if (isIPB) {
        $("#ipbControl").removeClass("d-none").show();
    } else {
        $("#ipbControl").addClass("d-none").hide();
    }

    const { imageDataJson, replacePart, ipbTableWidths } = data;

    if (imageDataJson) {
        updateHotSpotLink(imageDataJson);
        setupImagePagination(imageDataJson);
    } else {
        console.warn("imageDataJson is null");
    }

    if (ipbTableWidths) {
        applyColumnWidths(ipbTableWidths);
    }

    setReplacePartPopup(replacePart);
}

// 준비사항 Render
function jgReqCondRender(data) {

    // 필수교환 품목 소모성 물자 - bizCode == "KTA"아래로 내리지 말것!!
    jgConsumCheck();

    // 필안전 요건  - bizCode == "KTA"아래로 내리지 말것!!
    jgAlertCheck();
    
    // 적용 범위, 기타 요건 - KTA체크후 Hide / 요구 조건  => 항공기 상태 
    jgKtaCheck();
    
}

// 정비절차 Render
function jgStepSeqRender(data) {



     //왼쪽,오른쪽 프레임 버튼
     frameBtn(data);

}

/**
 * 2025.06.09 - osm
 * - IPB 색인 검색 템플릿 렌더링
 * - BizCode KTA -> KT1으로 변경
 */
function checkIndex(tocoId) {
	try {
		if(bizCode === 'KT1') {
			$.ajax({
				type: "POST",
				url: `/EXPIS/${bizCode}/ietm/checkIndexByTocoId.do`,
				data: {
					tocoId: tocoId
				},
				dataType: "json",
				success: function (data) {
					// 2023.09.07 - KTA 강제 지정 추가 - jingi.kim
					if ( isFixKTAIPBSearch() === true ) {
						data.checked = true;
					}

					if(!data.checked) return;

					// 선택박스 자동 선택
					var $activeLink = $("#toco_list").find("a.nav-link.active");
					var selType = "";
					if ($activeLink.length > 0) {
						selType = $activeLink.closest("li").attr("type") || "";
						selType = selType.toUpperCase();

						selected1 = selType === "PARTINDEX" ? "selected" : "";
						selected3 = selType === "NSNINDEX" ? "selected" : "";
						selected5 = selType === "REFNOINDEX" ? "selected" : "";
					}

					var ipbSearchHtmlStr = "<div id='to_sub_toco_list' class='main con img_list contents_div' style='overflow-y: scroll; display: block;'>";
					ipbSearchHtmlStr += "<table class='all_list'>";
					ipbSearchHtmlStr += "<colgroup><col width='*'><col width='300'><col width='50'><col width='150'><col width='200'><col width='50'><col width='*'></colgroup>";
					ipbSearchHtmlStr += "<thead>";
					ipbSearchHtmlStr += "<tr><th colspan='7'></th></tr>";

					ipbSearchHtmlStr += `
					<tr>
						<th></th>
						<th> IPB 색인 검색  </th>
						<th>
							<select id='ipbSearchOpt1' onchange='javascript:ipbSelectChange()' >
								<option ` + selected1 + ` value='1'>부품 번호</option>
								<option ` + selected3 + ` value='3'>국가 재고 번호</option>
								<option ` + selected5 + ` value='5'>참고 지정 번호</option>
							</select>
						</th>
						<th></th>
						<th>
							<INPUT id=keyword style='WIDTH: 270px' name=keyword type='text'>
						</th>
						<th>
							<a href='javascript:void(0);' style='border: 1px solid #5c87cb;background: #5c87cb;width: 67px;margin-right: 5px;margin-left: 0px;display: inline-block;' class='btn_list_search' onclick='searchIPBClick();' title='검색'>검색</a>
						</th>
						<th></th>
					</tr>`;
					
					ipbSearchHtmlStr += "<tr><th colspan='7'>색인 전체 검색시 * 를 입력하세요.</th></tr>";
					ipbSearchHtmlStr += "</thead>";
					ipbSearchHtmlStr += "</table>";
					ipbSearchHtmlStr += "<br>";
					ipbSearchHtmlStr += "<br>";
					ipbSearchHtmlStr += "<table class='all_list' id='ipbSearchResultTable'>";
					
					ipbSearchHtmlStr += "<colgroup><col width='*'><col width='300'><col width='300'><col width='300'><col width='300'><col width='*'></colgroup>";
					ipbSearchHtmlStr += "<thead>";
					ipbSearchHtmlStr += "<tr><th></th><th>부품 번호</th><th>계통/그림/시트 및 색인번호</th><th>단위당 구성수량</th><th>근원정비복구성부호</th><th></th></tr>";
					ipbSearchHtmlStr += "</thead>";
					
					ipbSearchHtmlStr += "</table>";
					ipbSearchHtmlStr += "<div id='to_sub_toco_body'>";
					ipbSearchHtmlStr += "</div>";
					ipbSearchHtmlStr += "<br>";
					ipbSearchHtmlStr += "</div>";

					$(".main-content").html(ipbSearchHtmlStr);
				}
			});
		} else {
		    // 2025.06.10 WD 색인 검색 추가 - cch
		    if ($("#" + tocoId).attr("name") == "색인 검색") {
		        console.log("색인 검색");
		        var wdSearchHtmlStr = "<div id='to_sub_toco_list' class='main con img_list contents_div' style='overflow-y: scroll; display: block;'>";
                wdSearchHtmlStr += "<table class='all_list'>";
                wdSearchHtmlStr += "<colgroup><col width='*'><col width='300'><col width='50'><col width='150'><col width='200'><col width='50'><col width='*'></colgroup>";
                wdSearchHtmlStr += "<thead>";
                wdSearchHtmlStr += "<tr><th colspan='7'></th></tr>";

                if(TOKEY?.slice(-1)=="2"){
                    wdSearchHtmlStr += "<tr><th></th><th>배선도(Wire Diagram) 검색</th><th><select id='wdSearchOpt1'><option value='2' selected >WD-2</option><option value='3'>WD-3</option><option value='4'>WD-4</option></select></th><th></th><th><INPUT id=keyword style='WIDTH: 270px' name=keyword type='text'></th><th><a href='javascript:void(0);' style='border: 1px solid #5c87cb;background: #5c87cb;width: 67px;margin-right: 5px;margin-left: 0px;display: inline-block;' class='btn_list_search' onclick='searchWDClick();' title='검색'>검색</a></th><th></th></tr>";
                }else if(TOKEY?.slice(-1)=="3"){
                    wdSearchHtmlStr += "<tr><th></th><th>배선도(Wire Diagram) 검색</th><th><select id='wdSearchOpt1'><option value='2'>WD-2</option><option value='3' selected >WD-3</option><option value='4'>WD-4</option></select></th><th></th><th><INPUT id=keyword style='WIDTH: 270px' name=keyword type='text'></th><th><a href='javascript:void(0);' style='border: 1px solid #5c87cb;background: #5c87cb;width: 67px;margin-right: 5px;margin-left: 0px;display: inline-block;' class='btn_list_search' onclick='searchWDClick();' title='검색'>검색</a></th><th></th></tr>";
                }else if(TOKEY?.slice(-1)=="4"){
                    wdSearchHtmlStr += "<tr><th></th><th>배선도(Wire Diagram) 검색</th><th><select id='wdSearchOpt1'><option value='2'>WD-2</option><option value='3'>WD-3</option><option value='4' selected  >WD-4</option></select></th><th></th><th><INPUT id=keyword style='WIDTH: 270px' name=keyword type='text'></th><th><a href='javascript:void(0);' style='border: 1px solid #5c87cb;background: #5c87cb;width: 67px;margin-right: 5px;margin-left: 0px;display: inline-block;' class='btn_list_search' onclick='searchWDClick();' title='검색'>검색</a></th><th></th></tr>";
                }else{
                    wdSearchHtmlStr += "<tr><th></th><th>배선도(Wire Diagram) 검색</th><th><select id='wdSearchOpt1'><option value='2'>WD-2</option><option value='3'>WD-3</option><option value='4'>WD-4</option></select></th><th></th><th><INPUT id=keyword style='WIDTH: 270px' name=keyword type='text'></th><th><a href='javascript:void(0);' style='border: 1px solid #5c87cb;background: #5c87cb;width: 67px;margin-right: 5px;margin-left: 0px;display: inline-block;' class='btn_list_search' onclick='searchWDClick();' title='검색'>검색</a></th><th></th></tr>";
                }

                wdSearchHtmlStr += "<tr><th colspan='7'></th></tr>";
                wdSearchHtmlStr += "</thead>";
                wdSearchHtmlStr += "</table>";
                wdSearchHtmlStr += "<br>";
                wdSearchHtmlStr += "<br>";
                wdSearchHtmlStr += "<table class='all_list'>";
                wdSearchHtmlStr += "<colgroup><col width='*'><col width='450'><col width='350'><col width='*'></colgroup>";
                wdSearchHtmlStr += "<thead>";
                wdSearchHtmlStr += "<tr><th></th><th>제 목</th><th>분류 번호</th><th></th></tr>";
                wdSearchHtmlStr += "</thead>";
                wdSearchHtmlStr += "</table>";
                wdSearchHtmlStr += "<div id='to_sub_toco_body'>";
                wdSearchHtmlStr += "</div>";
                wdSearchHtmlStr += "<br>";
                wdSearchHtmlStr += "</div>";
                console.log("searchHtml : "+wdSearchHtmlStr);
                $(".main-content").html("");
                $(".main-content").html(wdSearchHtmlStr);
		    }

//			//2022 03 16 Park.J.S : WD Search
//			//색인 검색 기능 화면 구성
//			//TODO 국제화 일경우 처리 추가
//			if($("#"+tocoId).attr("title") == "색인 검색" && $("#top_to_key").attr("title").indexOf("WD") > -1){
//				console.log("WD Search : "+$("#"+tocoId).attr("title")+", "+$("#top_to_key").attr("title"));
//				var wdSearchHtmlStr = "<div id='to_sub_toco_list' class='main con img_list contents_div' style='overflow-y: scroll; display: block;'>";
//				wdSearchHtmlStr += "<table class='all_list'>";
//				wdSearchHtmlStr += "<colgroup><col width='*'><col width='300'><col width='50'><col width='150'><col width='200'><col width='50'><col width='*'></colgroup>";
//				wdSearchHtmlStr += "<thead>";
//				wdSearchHtmlStr += "<tr><th colspan='7'></th></tr>";
//				//2023 01 10 Park.J.S. Update 교범 마지막 숫자 확인해서 해당 숫자 읽경우 선택처리
//				if($("#top_to_key").attr("title").slice(-1)=="2"){
//					wdSearchHtmlStr += "<tr><th></th><th>배선도(Wire Diagram) 검색</th><th><select id='wdSearchOpt1'><option value='2' selected >WD-2</option><option value='3'>WD-3</option><option value='4'>WD-4</option></select></th><th></th><th><INPUT id=keyword style='WIDTH: 270px' name=keyword type='text'></th><th><a href='javascript:void(0);' style='border: 1px solid #5c87cb;background: #5c87cb;width: 67px;margin-right: 5px;margin-left: 0px;display: inline-block;' class='btn_list_search' onclick='searchWDClick();' title='검색'>검색</a></th><th></th></tr>";
//				}else if($("#top_to_key").attr("title").slice(-1)=="3"){
//					wdSearchHtmlStr += "<tr><th></th><th>배선도(Wire Diagram) 검색</th><th><select id='wdSearchOpt1'><option value='2'>WD-2</option><option value='3' selected >WD-3</option><option value='4'>WD-4</option></select></th><th></th><th><INPUT id=keyword style='WIDTH: 270px' name=keyword type='text'></th><th><a href='javascript:void(0);' style='border: 1px solid #5c87cb;background: #5c87cb;width: 67px;margin-right: 5px;margin-left: 0px;display: inline-block;' class='btn_list_search' onclick='searchWDClick();' title='검색'>검색</a></th><th></th></tr>";
//				}else if($("#top_to_key").attr("title").slice(-1)=="4"){
//					wdSearchHtmlStr += "<tr><th></th><th>배선도(Wire Diagram) 검색</th><th><select id='wdSearchOpt1'><option value='2'>WD-2</option><option value='3'>WD-3</option><option value='4' selected  >WD-4</option></select></th><th></th><th><INPUT id=keyword style='WIDTH: 270px' name=keyword type='text'></th><th><a href='javascript:void(0);' style='border: 1px solid #5c87cb;background: #5c87cb;width: 67px;margin-right: 5px;margin-left: 0px;display: inline-block;' class='btn_list_search' onclick='searchWDClick();' title='검색'>검색</a></th><th></th></tr>";
//				}else{
//					wdSearchHtmlStr += "<tr><th></th><th>배선도(Wire Diagram) 검색</th><th><select id='wdSearchOpt1'><option value='2'>WD-2</option><option value='3'>WD-3</option><option value='4'>WD-4</option></select></th><th></th><th><INPUT id=keyword style='WIDTH: 270px' name=keyword type='text'></th><th><a href='javascript:void(0);' style='border: 1px solid #5c87cb;background: #5c87cb;width: 67px;margin-right: 5px;margin-left: 0px;display: inline-block;' class='btn_list_search' onclick='searchWDClick();' title='검색'>검색</a></th><th></th></tr>";
//				}
//
//				wdSearchHtmlStr += "<tr><th colspan='7'></th></tr>";
//				wdSearchHtmlStr += "</thead>";
//				wdSearchHtmlStr += "</table>";
//				wdSearchHtmlStr += "<br>";
//				wdSearchHtmlStr += "<br>";
//				wdSearchHtmlStr += "<table class='all_list'>";
//				wdSearchHtmlStr += "<colgroup><col width='*'><col width='450'><col width='350'><col width='*'></colgroup>";
//				wdSearchHtmlStr += "<thead>";
//				wdSearchHtmlStr += "<tr><th></th><th>제 목</th><th>분류 번호</th><th></th></tr>";
//				wdSearchHtmlStr += "</thead>";
//				wdSearchHtmlStr += "</table>";
//				wdSearchHtmlStr += "<div id='to_sub_toco_body'>";
//				/*
//				wdSearchHtmlStr += "<table class='all_list'>";
//				wdSearchHtmlStr += "<colgroup><col width='*'><col width='350'><col width='350'><col width='*'></colgroup>";
//				wdSearchHtmlStr += "<tbody><tr><td></td><td><a href='javascript:;' onclick='viewExContents('1A-50A-1', '7572674a374847528c01ae7aa1ad7e88', '', '04', '29C357D8-8744-44A3-9B1B-520F9FF1D5CF');'>STORES CATEGORY DEFINITION</a> </td><td><a href='javascript:;' onclick='viewExContents('1A-50A-1', '7572674a374847528c01ae7aa1ad7e88', '', '01', '');'>STORES CONFIG Switch</a> </td><td></td></tr></tbody>";
//				wdSearchHtmlStr += "</table>";
//				*/
//				wdSearchHtmlStr += "</div>";
//				wdSearchHtmlStr += "<br>";
//				wdSearchHtmlStr += "</div>";
//				console.log("searchHtml : "+wdSearchHtmlStr);
//				$("#main_contents").html("");
//				$("#main_contents").html(wdSearchHtmlStr);
////				/$("#ac-content").html(searchHtml);
//			}
			
			if($("#"+tocoId) && $("#"+tocoId).attr("title")){
				//2022 09 29 IPB 정비 부품 목록일경우 색인 아님 추가
				if(($("#"+tocoId).attr("title").indexOf("부품") > -1 && $("#"+tocoId).attr("title").indexOf("정비") > -1) && $("#top_to_key").attr("title").indexOf("-4-") > -1 && $("#"+tocoId).attr("title").indexOf("목록") > -1){
					console.log("IPB 정비 부품 목록일경우 색인 아님 추가 : "+$("#"+tocoId).attr("title"));
					return;
				}
				//2022 03 18 Park.J.S : IPB Search
				//색인 검색 기능 화면 구성
				//TODO 국제화 일경우 처리 추가
				//2022 10 13 Park.J.S. UPDATE : 기존 명칭 정의한 내용이 중복되서 메뉴 명칭 픽스함
				//if(($("#"+tocoId).attr("title").indexOf("부품") > -1 || $("#"+tocoId).attr("title").indexOf("색인") > -1) && $("#top_to_key").attr("title").indexOf("-4-") > -1 && $("#"+tocoId).attr("title").indexOf("MPL") < 0){
				if(($("#"+tocoId).attr("title")+"" ==  "부품번호 색인" || $("#"+tocoId).attr("title")+"" == "RDN 색인") && $("#top_to_key").attr("title").indexOf("-4-") > -1){
					console.log("IPB Search : "+$("#"+tocoId).attr("title")+", "+$("#top_to_key").attr("title"));
					var ipbSearchHtmlStr = "<div id='to_sub_toco_list' class='main con img_list contents_div' style='overflow-y: scroll; display: block;'>";
					ipbSearchHtmlStr += "<table class='all_list'>";
					ipbSearchHtmlStr += "<colgroup><col width='*'><col width='300'><col width='50'><col width='150'><col width='200'><col width='50'><col width='*'></colgroup>";
					ipbSearchHtmlStr += "<thead>";
					ipbSearchHtmlStr += "<tr><th colspan='7'></th></tr>";
					if($("#"+tocoId).attr("title").indexOf("RDN") > -1){
						ipbSearchHtmlStr += "<tr><th></th><th> IPB 색인 검색  </th><th><select id='ipbSearchOpt1' onchange='javascript:ipbSelectChange()' ><option value='2'>RDN</option><option value='1'>부품 번호</option></select></th><th></th><th><INPUT id=keyword style='WIDTH: 270px' name=keyword type='text'></th><th><a href='javascript:void(0);' style='border: 1px solid #5c87cb;background: #5c87cb;width: 67px;margin-right: 5px;margin-left: 0px;display: inline-block;' class='btn_list_search' onclick='searchIPBClick();' title='검색'>검색</a></th><th></th></tr>";
					}else{
						ipbSearchHtmlStr += "<tr><th></th><th> IPB 색인 검색  </th><th><select id='ipbSearchOpt1' onchange='javascript:ipbSelectChange()' ><option value='1'>부품 번호</option><option value='2'>RDN</option></select></th><th></th><th><INPUT id=keyword style='WIDTH: 270px' name=keyword type='text'></th><th><a href='javascript:void(0);' style='border: 1px solid #5c87cb;background: #5c87cb;width: 67px;margin-right: 5px;margin-left: 0px;display: inline-block;' class='btn_list_search' onclick='searchIPBClick();' title='검색'>검색</a></th><th></th></tr>";
					}
					ipbSearchHtmlStr += "<tr><th colspan='7'>색인 전체 검색시 * 를 입력하세요.</th></tr>";
					ipbSearchHtmlStr += "</thead>";
					ipbSearchHtmlStr += "</table>";
					ipbSearchHtmlStr += "<br>";
					ipbSearchHtmlStr += "<br>";
					ipbSearchHtmlStr += "<table class='all_list' id='ipbSearchResultTable'>";
					if($("#"+tocoId).attr("title").indexOf("부품") > -1){
						ipbSearchHtmlStr += "<colgroup><col width='*'><col width='300'><col width='300'><col width='300'><col width='300'><col width='*'></colgroup>";
						ipbSearchHtmlStr += "<thead>";
						ipbSearchHtmlStr += "<tr><th></th><th>부품 번호</th><th>계통/그림/시트 및 색인번호</th><th>단위당 구성수량</th><th>근원정비복구성부호</th><th></th></tr>";
						ipbSearchHtmlStr += "</thead>";
					}else{
						ipbSearchHtmlStr += "<colgroup><col width='*'><col width='300'><col width='350'><col width='*'></colgroup>";
						ipbSearchHtmlStr += "<thead>";
						ipbSearchHtmlStr += "<tr><th></th><th>RDN</th><th>계통/그림/시트 및 색인번호</th><th></th></tr>";
						ipbSearchHtmlStr += "</thead>";
					}
					ipbSearchHtmlStr += "</table>";
					ipbSearchHtmlStr += "<div id='to_sub_toco_body'>";
					ipbSearchHtmlStr += "</div>";
					ipbSearchHtmlStr += "<br>";
					ipbSearchHtmlStr += "</div>";
					console.log("searchHtml : "+ipbSearchHtmlStr);
					$("#main_contents").html("");
					$("#main_contents").html(ipbSearchHtmlStr);
	//				/$("#ac-content").html(searchHtml);
					
				}
			}

		
			/* 2022 10 14 Park.J.S. 최광현 차장 요청으로 아래 경우 색인 나오는 로직 제거
			//2022 06 28 ADD
			console.log("Index of Title : "+$("#"+tocoId).attr("title"));
			console.log("Index of 부품 번호 색인 : "+$("#"+tocoId).attr("title").indexOf("부품 번호 색인"));
			console.log("Index of 국가 재고 번호 색인 : "+$("#"+tocoId).attr("title").indexOf("국가 재고 번호 색인"));
			console.log("Index of 참고 지정 번호 색인 : "+$("#"+tocoId).attr("title").indexOf("참고 지정 번호 색인"));
			if(($("#"+tocoId).attr("title").indexOf("부품 번호 색인") > -1 || $("#"+tocoId).attr("title").indexOf("국가 재고 번호 색인") > -1) || $("#"+tocoId).attr("title").indexOf("참고 지정 번호 색인") > -1){
				console.log("IPB Search : "+$("#"+tocoId).attr("title")+", "+$("#top_to_key").attr("title"));
				var ipbSearchHtmlStr = "<div id='to_sub_toco_list' class='main con img_list contents_div' style='overflow-y: scroll; display: block;'>";
				ipbSearchHtmlStr += "<table class='all_list'>";
				ipbSearchHtmlStr += "<caption id='to_sub_toco_caption'>배선도(Wire Diagram)검색</caption>";
				ipbSearchHtmlStr += "<colgroup><col width='*'><col width='300'><col width='50'><col width='150'><col width='200'><col width='50'><col width='*'></colgroup>";
				ipbSearchHtmlStr += "<thead>";
				ipbSearchHtmlStr += "<tr><th colspan='7'></th></tr>";
				if($("#"+tocoId).attr("title").indexOf("부품 번호 색인") > -1){
					ipbSearchHtmlStr += "<tr><th></th><th> 부품 번호 색인 검색  </th><th><select id='ipbSearchOpt1' onchange='javascript:ipbSelectChange()' ><option value='4'>부품 번호</option></select></th><th></th><th><INPUT id=keyword style='WIDTH: 270px' name=keyword type='text'></th><th><a href='javascript:void(0);' style='border: 1px solid #5c87cb;background: #5c87cb;width: 67px;margin-right: 5px;margin-left: 0px;display: inline-block;' class='btn_list_search' onclick='searchIPBClick();' title='검색'>검색</a></th><th></th></tr>";
				}else if($("#"+tocoId).attr("title").indexOf("국가 재고 번호 색인") > -1){
					ipbSearchHtmlStr += "<tr><th></th><th> 국가 재고 번호 색인 검색  </th><th><select id='ipbSearchOpt1' onchange='javascript:ipbSelectChange()' ><option value='3'>국가 재고 번호 </option></select></th><th></th><th><INPUT id=keyword style='WIDTH: 270px' name=keyword type='text'></th><th><a href='javascript:void(0);' style='border: 1px solid #5c87cb;background: #5c87cb;width: 67px;margin-right: 5px;margin-left: 0px;display: inline-block;' class='btn_list_search' onclick='searchIPBClick();' title='검색'>검색</a></th><th></th></tr>";
				}else if($("#"+tocoId).attr("title").indexOf("참고 지정 번호 색인") > -1){
					ipbSearchHtmlStr += "<tr><th></th><th> 참고 지정 번호 색인 검색  </th><th><select id='ipbSearchOpt1' onchange='javascript:ipbSelectChange()' ><option value='5'>참고 지정 번호</option></select></th><th></th><th><INPUT id=keyword style='WIDTH: 270px' name=keyword type='text'></th><th><a href='javascript:void(0);' style='border: 1px solid #5c87cb;background: #5c87cb;width: 67px;margin-right: 5px;margin-left: 0px;display: inline-block;' class='btn_list_search' onclick='searchIPBClick();' title='검색'>검색</a></th><th></th></tr>";
				}
				ipbSearchHtmlStr += "<tr><th colspan='7'>색인 전체 검색시 * 를 입력하세요.</th></tr>";
				ipbSearchHtmlStr += "</thead>";
				ipbSearchHtmlStr += "</table>";
				ipbSearchHtmlStr += "<br>";
				ipbSearchHtmlStr += "<br>";
				ipbSearchHtmlStr += "<table class='all_list' id='ipbSearchResultTable'>";
				ipbSearchHtmlStr += "<caption id='to_sub_toco_caption'>결과</caption>";
				if($("#"+tocoId).attr("title").indexOf("부품 번호 색인") > -1){
					ipbSearchHtmlStr += "<colgroup><col width='*'><col width='300'><col width='300'><col width='300'><col width='*'></colgroup>";
					ipbSearchHtmlStr += "<thead>";
					ipbSearchHtmlStr += "<tr><th></th><th>부품번호</th><th>그림번호</th><th>품목번호/시트번호</th><th></th></tr>";
					ipbSearchHtmlStr += "</thead>";
				}else if($("#"+tocoId).attr("title").indexOf("국가 재고 번호 색인") > -1){
					ipbSearchHtmlStr += "<colgroup><col width='*'><col width='300'><col width='300'><col width='300'><col width='*'></colgroup>";
					ipbSearchHtmlStr += "<thead>";
					ipbSearchHtmlStr += "<tr><th></th><th>국가재고번호</th><th>그림번호</th><th>품목번호/시트번호</th><th></th></tr>";
					ipbSearchHtmlStr += "</thead>";
				}else if($("#"+tocoId).attr("title").indexOf("참고 지정 번호 색인") > -1){
					ipbSearchHtmlStr += "<colgroup><col width='*'><col width='300'><col width='300'><col width='300'><col width='300'><col width='*'></colgroup>";
					ipbSearchHtmlStr += "<thead>";
					ipbSearchHtmlStr += "<tr><th></th><th>참조지정번호</th><th>그림번호</th><th>품목번호/시트번호</th><th>부품번호</th><th></th></tr>";
					ipbSearchHtmlStr += "</thead>";
				}
				ipbSearchHtmlStr += "</table>";
				ipbSearchHtmlStr += "<div id='to_sub_toco_body'>";
				ipbSearchHtmlStr += "</div>";
				ipbSearchHtmlStr += "<br>";
				ipbSearchHtmlStr += "</div>";
				console.log("searchHtml : "+ipbSearchHtmlStr);
				$("#main_contents").html("");
				$("#main_contents").html(ipbSearchHtmlStr);
				$(".no_data_img").css("display","none");
//				/$("#ac-content").html(searchHtml);
			}
			*/
		}
	} catch (e) {
		console.log('ERROR - KTA check Index : ', e.message);
	}
}

// KTA, 강제 인덱스 지정 체크 함수 - jingi.kim
function isFixKTAIPBSearch() {
	if ( bizCode !== 'KT1' ) return false;

	if ( TOKEY.includes("1T-KT1-4") || TOKEY.includes("-K1A-4") || TOKEY.includes("-K1ALK-4") ) {
		var toTitle = $("a.nav-link.active").text().trim() || "";
		if ( toTitle.includes("번호 색인") || toTitle.includes("NUMBER INDEX") ) {
			return true;
		}
	}
	return false;
}

function tocoDivMake(tocoId) {
	var result = "";
	result += "<div class='ac-content' >";
	result += "<div id='toco_cont_" + tocoId + "'>";
	result += "</div>";
	result += "</div>";
	$("#main_contents").append(result);
}

function contMake(cont, nTocoId, folderChk, stepCnt) {
	console.log("CALL contMake");
	var result = "";
	var contSize = cont.length;
	var toKey = "";
	var tocoId = "";
	var pTocoId = "";
	var tocoName = "";
	var tocoType = "";
	console.log("contMake contSize : "+contSize);
	for(var i=0; i<contSize; i++) {
		if(nTocoId !=  cont[i].tocoId) {
			tocoId = cont[i].tocoId;
			pTocoId = cont[i].pTocoId;
			tocoName = cont[i].tocoName;
			tocoType = cont[i].tocoType;
			result += tagMake(toKey, tocoId, pTocoId, tocoName, tocoType, stepCnt);
		}
	}
	return result;
}

function tagMake(toKey, tocoId, pTocoId, tocoName, tocoType, stepCnt) {
	console.log("CALL tagMake : "+toKey+","+tocoId+","+ pTocoId+","+tocoName+","+tocoType+","+stepCnt);
	var contents = "";
	var className = "";
	var aTag = "";
	var aTagEnd = "";
	var ipbChk = false;

	if(tocoType == "IPB") {
		ipbChk = true;
	}
	
	if(tocoType == "01") {
		className = "ac-introduction";
	} else {
		className = "cont_link1";
		aTag = "<a href='javascript:;' class='ac-arrow' onclick='tocoAEvent(\"" + tocoId + "\", \"" + ipbChk + "\");'>";
		aTagEnd = "</a>";
	}
	
	contents += "<div  class='" + className + " toco_step_" + stepCnt + "' title='" + tocoName + "' id='cont_" + tocoId + "'>";
	contents += aTag;
	contents += tocoName;
	contents += aTagEnd;
	contents += "</div>";
	contents += "<div class='ac-content' >";
	contents += "<div id='toco_cont_" + tocoId + "' class='depth'>";
	contents += "</div>";
	contents += "</div>";
		
	return contents;
}

function tocoAEvent(tocoId, ipbChk) {
	console.log("CALL tocoAEvent");
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//2022 11 08 Park.J.S. ADD : 하위 객체 선택 이동시에도 메뉴 이동 이력 저장
	eventHistory.push('viewToContents');
	toKeyHistory.push($("#to_key").val());
	tocoIdHistory.push(tocoId);
	searchWordHistory.push("");
	vcKindHistory.push("");
	contIdHistory.push("");
	historyIndex++;
	historyTotalIndex++;
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//2024.03.27 - Topic, SubTopic 일 경우에만 MULTI 가능하도록 추가 - jingi.kim
	//2024.04.16 - BLOCK2, KTA만 적용하도록 제한 - jingi.kim
	if ( $("#bizCode").length > 0 && ( $("#bizCode").val() == "BLOCK2" || $("#bizCode").val() == "KTA" ) ) {
		selectTocoIdTopic($("#to_key").val(), tocoId, "list");
	} else {
		selectTocoId($("#to_key").val(), tocoId, "list");
	}
}

function timeCheck() {
	timeFlag = true;
}

function loadFi() {
	var xmlData = "<?xml version='1.0' encoding='euc-kr' ?><faultinf cdm='node' faulttype='B' id='feb1581fe23a4cce8eea00da53a753dc' itemid='AE' name='AE' ref='' status='a' type='' version='' viewheight='1432' viewwidth='856'> <test id='0B4C361D-730C-445C-88C8-C570E5147F58'> <task id='ECD8DBB7-4533-4F0A-AEFB-0483EC6FA30B'></task> </test> </faultinf>";
	var viewText = "";
	var ietmId = "feb1581fe23a4cce8eea00da53a753dc";
	var language = "0";
	var SzType = "FI_B";
	
	viewText += "<object ";
	viewText += "name='application' ";
	viewText += "id='applicationID' ";
	viewText += "code='com.soltworks.graphics.Application'  ";
	viewText += "archive='"+$("#proectName").val()+"web/object/application.jar' ";
	viewText += "width='100%' ";
	viewText += "height='1532'";
	viewText += "type='application/x-java-applet;version=1.7' ";
	viewText += "scriptable=yes ";
	viewText += "pluginspage='http://java.sun.com/products/plugin/1.3/plugin-install.html'> ";
	viewText += "<param name='Language' value=\"" + language + "\"> ";
	viewText += "<param name='ItemID' value=\"" + ietmId + "\"> ";
	viewText += "<param name='ViewType' value='0'> ";
	viewText += "<param name='ViewObject' value='1'> ";
	viewText += "<param name='Xml' value=\"" + xmlData + "\"> ";
	viewText += "<param name='SzType' value=\"" + SzType + "\"> ";
	viewText += "<param name='FromPage' value=\"loadfi\" > ";
	viewText += "<param name='LastVersionID' value=''> ";
	viewText += "<param name='wmode' value='transparent'> ";
	viewText += "</object>";
	
	$("#main_contents").html(viewText);
}

function toSubTocoList(toKey, tocoListKind) {
	var param = "";
	
	param += "to_key="+encodeURIComponent(toKey);
	param += "&toco_list_kind="+tocoListKind;
	
	$.ajax({
		type: "POST",
		url:  $("#proectName").val()+"ietm/toSubTocoList.do",
		data: param,
		dataType: "json",
		async: false,
		cache : false,
		success: function(data) {
			if(data.message == "success"){
				var contents = data.returnData;
				
				contents = transWord(contents, 'splChar');
				
				$("#to_sub_toco_caption").html(data.subTocoCaption);
				$("#to_sub_toco_title").html(data.subTocoTitle);
				$("#to_sub_toco_body").html(contents);
				$(".contents_div").hide();
				$("#to_sub_toco_list").show();
				$("#to_sub_toco_list").scrollTop(0);
			}
		}
	});
}

function latestToInsert(param) {
	$.ajax({
		type : "POST",
		data : param,
		url : $("#proectName").val()+"ietm/latestToInsert.do",
		dataType : "json",
		cache : false
	});

	// selectSibling();
}

// function selectSibling() {
// 	//2023 03 09 jysi EDIT : toKey에 &문자 있는 경우 교범명 잘려서 수정
// 	$.ajax({
// 		type : "POST",
// 		data : "to_key="+encodeURIComponent($("#to_key").val()),
// 		url : $("#proectName").val()+"ietm/selectSibling.do",
// 		dataType : "json",
// 		success : function(data) {
// 			var siblingList = data.siblingList;
// 			var listSize = siblingList.length;
// 			var siblingHtml = "";
// 			var ietmRelatedManualList = $("#ietm_related_manual_list").val();
//
// 			if(listSize == 0) {
// 				siblingHtml += "<li>";
// 				siblingHtml += "	<span class='none'></span>";
// 				siblingHtml +=			"<a href='javascript:void(0);' style='' ></a>";
// 				siblingHtml += "</li>";
//
// 			} else {
// 				for(var i=0; i<listSize; i++) {
// 					var statusKind = siblingList[i].statusKind;
// 					siblingHtml +=	"<li>";
// 					siblingHtml +=		"<span class='paper'></span>";
// 					siblingHtml +=			"<a href='javascript:void(0);' title="+siblingList[i].refUserId+" style='font-weight:bold;' onclick=\"getTocoList('"+siblingList[i].refToKey+"'); selectBoxEvent('to_sibling');\">"+siblingList[i].refUserId+"</a>";
// 					siblingHtml +=	"</li>";
// //
// //					if(statusKind == "Y") {
// //						siblingHtml +=	"<li>";
// //						siblingHtml +=		"<span class='paper'></span>";
// //						siblingHtml +=			"<a href='javascript:void(0);' title="+siblingList[i].refUserId+" style='font-weight:bold;' onclick=\"getTocoList('"+siblingList[i].refToKey+"'); selectBoxEvent('to_sibling');\">"+siblingList[i].refUserId+"</a>";
// //						siblingHtml +=	"</li>";
// //					} else {
// //						siblingHtml +=	"<li>";
// //						siblingHtml +=		"<span class='none'></span>";
// //						siblingHtml +=			"<a href='javascript:void(0);' title="+siblingList[i].refUserId+" style='background-color:#ebecee;'>"+siblingList[i].refUserId+"</a>";
// //						siblingHtml +=	"</li>";
// //					}
// 				}
// 			}
//
// 			$("#sibling_title").text(" "+ ietmRelatedManualList +" (" + listSize + ")");
// 			$("#choice_to_sibling").html(siblingHtml);
// 		},
// 		complete:function() {
// 		}
// 	});
// }

function getTocoList(toKey) {
	callTocoList(toKey, "open", "to");
}

function sectionOpen(event) {
	var count = 0;
	var cursor;
	var countT = 0;
	var cursorT;
	var clickPoint;
	
	if($(event).attr("class") == "ac-arrow-open") {
		$(event).removeClass("ac-arrow-open");
		$(event).addClass("ac-arrow");
		
		count = $(event).parent().parent().parent().find(".ac-content").length;
		cursor = $(event).parent().parent();
		cursor.find(".ac-desc").hide();
		cursor.find(".ac-task").hide();
		for (var int = 0; int < count; int++) {
			cursor = cursor.next();
			if(cursor.find(".ac-section").index() == 0) {
				break;
			} else {
				cursor.hide();
			}
		}
		
		countT = $(event).parent().parent().parent().parent().parent().children("div").length;
		cursorT = $(event).parent().parent().parent().parent();
		cursorT.find(".ac-desc").hide();
		cursorT.find(".ac-task").hide();
		for (var int2 = 0; int2 < countT; int2++) {
			cursorT = cursorT.next();
//			if($(cursorT).attr("class") != "ac-content") {
//				break;
//			}
			if(cursorT.find("div").find("div").find(".ac-section").index() == 0) {
				break;
			} else {
				if($(cursorT).index() == int2 + 1) {
					cursorT.hide();
				}else {
					break;
				}
			}
		}
		
		
	} else {
		$(event).removeClass("ac-arrow");
		$(event).addClass("ac-arrow-open");
		
		count = $(event).parent().parent().parent().find(".ac-content").length;
		cursor = $(event).parent().parent();
		cursor.find(".ac-desc").show();
		cursor.find(".ac-task").show();
		for (var int = 0; int < count; int++) {
			cursor = cursor.next();
			if(cursor.find(".ac-section").index() == 0) {
				break;
			} else {
				cursor.show();
			}
		}
		
		countT = $(event).parent().parent().parent().parent().parent().children("div").length;
		cursorT = $(event).parent().parent().parent().parent();
		cursorT.find(".ac-desc").show();
		cursorT.find(".ac-task").show();
		for (var int2 = 0; int2 < countT; int2++) {
			cursorT = cursorT.next();
//			if($(cursorT).attr("class") != "ac-content") {
//				break;
//			}
			if(cursorT.find("div").find("div").find(".ac-section").index() == 0) {
				break;
			} else {
				if($(cursorT).index() == int2 + 1) {
					cursorT.show();
				}else {
					break;
				}
			}
		}
		
	}
}

function topicOpen(event){
	var count = 0;
	var cursor;
	var countT = 0;
	var cursorT;
	if($(event).attr("class") == "ac-arrow-open") {
		$(event).removeClass("ac-arrow-open");
		$(event).addClass("ac-arrow");
		
		count = $(event).parent().parent().parent().find(".ac-content").length;
		cursor = $(event).parent().parent();
		cursor.find(".ac-desc").hide();
		cursor.find(".ac-task").hide();
		cursor.find(".fi-task").hide();
		for (var int = 0; int < count; int++) {
			cursor = cursor.next();
			if(cursor.find(".ac-topic").index() == 0) {
				break;
			} else {
				cursor.hide();
			}
		}
		countT = $(event).parent().parent().parent().parent().parent().children("div").length;
		cursorT = $(event).parent().parent().parent().parent();
//		cursorT.find(".ac-desc").hide();
		for (var int2 = 0; int2 < countT; int2++) {
			cursorT = cursorT.next();
//			if($(cursorT).attr("class") != "ac-content") {
//				break;
//			}
			if(cursorT.find("div").find("div").find(".ac-topic").index() == 0) {
				break;
			} else {
				if($(cursorT).index() == int2 + 1) {
					cursorT.hide();
				}else {
					break;
				}
			}
		}
		
	} else {
		$(event).removeClass("ac-arrow");
		$(event).addClass("ac-arrow-open");
		
		count = $(event).parent().parent().parent().find(".ac-content").length;
		cursor = $(event).parent().parent();
		cursor.find(".ac-desc").show();
		cursor.find(".ac-task").show();
		cursor.find(".fi-task").show();
		for (var int = 0; int < count; int++) {
			cursor = cursor.next();
			if(cursor.find(".ac-topic ").index() == 0) {
				break;
			} else {
				cursor.show();
			}
		}
		
		countT = $(event).parent().parent().parent().parent().parent().children("div").length;
		cursorT = $(event).parent().parent().parent().parent();
//		cursorT.find(".ac-desc").show();
		for (var int2 = 0; int2 < countT; int2++) {
			cursorT = cursorT.next();
//			if($(cursorT).attr("class") != "ac-content") {
//				break;
//			}
			if(cursorT.find("div").find("div").find(".ac-topic").index() == 0) {
				break;
			} else {
				if($(cursorT).index() == int2 + 1) {
					cursorT.show();
				}else {
					break;
				}
			}
		}
		
	}
}

function subTopicOpen(event) {
	if($(event).attr("class") == "ac-arrow-open") {
		$(event).removeClass("ac-arrow-open");
		$(event).addClass("ac-arrow");
		$(event).parent().next().hide();
	} else {
		$(event).removeClass("ac-arrow");
		$(event).addClass("ac-arrow-open");
		$(event).parent().next().show();
	}
}

function viewExContents(toKey, tocoId, searchWord, vcKind, contId) {
	console.log("Call viewExContents "+toKey+", "+tocoId);
	var nodata = $("#ietm_no_data").val();
	if(tocoId == null) {
		alert(nodata);
		return;
	}
	if(searchWord && searchWord != ""){
		$("#search_word").val(searchWord);
		if ( $("#top_search_word").length != -1 ) $("#top_search_word").val(searchWord);
	}
	if(contId == "undefined" || contId == null) {
		contId = "";
	}
	console.log("opener_link : "+$("#opener_link").val());
	if($("#opener_link").val() == "link") {
		if(toChk(toKey,tocoId) == true) {
			//2022 10 06 Park.J.S. ADD : 팝업에서 커버로 이동하는 기능 막기위해 추가 Block2 만 반영
			var coverCheckFlag = false;
			if($("#bizCode") && $("#bizCode").val() == "BLOCK2"){
				$.ajax({
					type : "POST",
					url : $("#proectName").val()+"ietm/tocoInfo.do",
					data : "to_key=" + encodeURIComponent(toKey) + "&toco_id=" + tocoId,
					dataType : "json",
					success : function(data) {
						if(data && data.tocoInfo && data.tocoInfo.pTocoId && data.tocoInfo.pTocoId == "0"){
							console.log("This is Cover Page : "+data.tocoInfo.tocoName);
							coverCheckFlag = false;
						}else{
							console.log("This is not Cover Page");
							coverCheckFlag = true;
							toKeyHistory.push(toKey);
							tocoIdHistory.push(tocoId);
							searchWordHistory.push(searchWord);
							vcKindHistory.push(vcKind);
							contIdHistory.push(contId);
							historyIndex++;
							historyTotalIndex++;
							viewOpenerContents(toKey, tocoId, searchWord, vcKind);
						}
					},
					error : function(e) {
						$(".loading").hide();
					},
					complete : function(e) {
						$(".opacity_search").stop();
						$(".opacity_search").hide();
						$(".search_list_ul").show();
						$(".loading").hide();
					}
				});
			}else{
				coverCheckFlag = true;
				toKeyHistory.push(toKey);
				tocoIdHistory.push(tocoId);
				searchWordHistory.push(searchWord);
				vcKindHistory.push(vcKind);
				contIdHistory.push(contId);
				historyIndex++;
				historyTotalIndex++;
				viewOpenerContents(toKey, tocoId, searchWord, vcKind);
			}
			console.log("PASS CHECK COVER MOVE : "+coverCheckFlag);
		}
	} else {
		$("#sys_id").val(toKey);
		confirmCLChk();
		console.log("toChk(toKey,tocoId) : "+toChk(toKey,tocoId));
		if(toChk(toKey,tocoId) == true) {
			//2023.05.17 jysi EDIT : 이미지 새창에서 해당목차 이동기능 사용
			if (opener && $("#printId").length==0) { //팝업창일 때+인쇄페이지제외
				//2023.09.15 - LSAM / NLS , IPB보기가 팝업창에서 오픈되었을 경우 해당 창에서 교범 표시하도록 보완 - jingi.kim
				if ( $("#bizCode").length > 0 && ( $("#bizCode").val() == "LSAM" || $("#bizCode").val() == "NLS"  || $("#bizCode").val() == "KBOB" || $("#bizCode").val() == "KICC" || $("#bizCode").val() == "MUAV" || $("#bizCode").val() == "SENSOR")) {
					if ( $("#ipb_type").length > 0 ) {
						callTocoList(toKey, tocoId, "to");
						makeOpenTree("to_list", toKey);
						viewToContents(toKey, tocoId, searchWord, vcKind, contId, '', 'N');
						eventHistory.pop();
						return;
					}
				}
				
				if ($("#main_chk", opener.document).val() == "main") { //부모창이 IETM메인창인지 확인 후 해당목차로 이동
					opener.callTocoList(toKey, tocoId, "to");
					opener.makeOpenTree("to_list", toKey);
					opener.viewToContents(toKey, tocoId, searchWord, vcKind, contId, '', 'N');
					opener.eventHistory.pop();
				} else { //부모창이 메인이 아닌 팝업일 때
					return;
				}
			} else { //팝업창 아닐 때
				callTocoList(toKey, tocoId, "to");
				makeOpenTree("to_list", toKey);
				//2023.05.10 jysi EDIT : 파라미터 잘못 전달하여 수정
				//viewToContents(toKey, tocoId, searchWord, vcKind, contId, 'N');
				viewToContents(toKey, tocoId, searchWord, vcKind, contId, '', 'N');
				eventHistory.pop();
			}
		};
	}
	eventHistory.push('viewExContents');
	//2022 10 20 jysi ADD : 팝업에서 이동시 팝업 사라지도록 추가
	if ($(".pup_alert_body").css("display") == "block"){
		$(".pup_alert_body").hide();
	}
}

function openToTree(toKey) {
	makeOpenTree("to_list", toKey);
}

function makeOpenTree(appendId, value) {
	console.log("In makeOpenTree : "+appendId+", "+value);
//	vcKindHistory.push(vcKind);
	
//	if(vcKindHistory[parseInt(historyIndex-1)] == "03" || vcKindHistory[parseInt(historyIndex-1)] == "04") {
//		value = contIdHistory[parseInt(historyIndex-1)];
//	}
	
	var toNav = $("#" + appendId);
	var to = "";
	
	if(appendId == "to_list") {
		to = toNav.find("[name='"+value+"']");
	} else {
		to = toNav.find("#"+value);
	}
	treePaint(toNav, to);
}

function treePaint(toNav, to) {
	console.log("In treePaint : "+toNav+", "+to);
	to.parents("li").addClass("open");
	to.parents("li").find(">ul").show();
	to.parents("li").find("> .nav_toggle").text("-").removeClass("plus").addClass("minus");
	toNav.find("a").css("backgroundColor", "");
	toNav.find("a").css("fontWeight", "normal");
	toNav.focus();
	to.css("backgroundColor", "gold");
	to.css("fontWeight", "bold");
//	to.focus();
	$("#myto_search_word").focus();
	if($("#popup_check").val() == '1') {
		$("#li_GrphToco").remove();
		$("#li_TableToco").remove();
		$(".open").addClass("last");
	}
}

function makeOpenTreeId(toValue, toId) {
	var toNav = "";
	var to = "";
	if(toValue == "TO") {
		toNav = $("#to_list");
		to = toNav.find("#"+toId);
	} else if(toValue == "MYTO") {
		toNav = $("#myto_list");
		to = toNav.find("#"+toId);
	} else if(toValue == "MYTOCO") {
		toNav = $("#mytoco_list");
		to = toNav.find("#"+toId);
	}
	treePaint(toNav, to);
}

// function toListSearch(toValue) {
// 	console.log("CALL toListSearch : "+toValue);
// 	var searchWord = $("#to_search_word").val();
// 	var mytoSearchWord = $("#myto_search_word").val();
// 	var nowPosition = null;
// 	var isFind = null;
// 	$("#to_search_word").focus();
// 	if(searchWord == "" && mytoSearchWord == "") {
// 		return;
// 	}
//
// 	if($(".list_myto").css("display") == "block") {
// 		if($("#myto_list").css("display") == "block") {
// 			toValue = "MYTO";
// 		} else if($("#mytoco_list").css("display") == "block") {
// 			toValue = "MYTOCO";
// 		}
// 	}
//
// 	if(toValue == "TO") {
// 		$("#to_list a").each(function() {
// 			var toName = $(this).text();
//
// 			if(nowPosition && toName.indexOf(searchWord) > -1) {
// 				makeOpenTreeId("TO", $(this).attr("id"));
// 				isFind = true;
// 				return false;
// 			}
//
// 			if($(this).css("backgroundColor") == "rgb(255, 215, 0)") {
// 				nowPosition = true;
// 			}
// 		});
// 	} else if(toValue == "MYTO") {
// 		$("#myto_list a").each(function() {
// 			var mytoName = $(this).attr("name");
//
// 			if(nowPosition && mytoName.indexOf(mytoSearchWord) > -1) {
// 				makeOpenTreeId("MYTO", $(this).attr("id"));
// 				isFind = true;
// 				return false;
// 			}
//
// 			if($(this).css("backgroundColor") == "rgb(255, 215, 0)") {
// 				nowPosition = true;
// 			}
// 		});
// 	} else if(toValue == "MYTOCO") {
// 		$("#mytoco_list a").each(function() {
// 			var mytocoName = $(this).attr("name");
//
// 			if(nowPosition && mytocoName.indexOf(mytoSearchWord) > -1) {
// 				makeOpenTreeId("MYTOCO", $(this).attr("id"));
// 				isFind = true;
// 				return false;
// 			}
//
// 			if($(this).css("backgroundColor") == "rgb(255, 215, 0)") {
// 				nowPosition = true;
// 			}
// 		});
// 	}
//
//
// 	if(!nowPosition) {
// 		toListReSearch(searchWord, mytoSearchWord, toValue);
// 	} else if(!isFind) {
// 		if(toValue == "TO") {
// 			$("#to_list").find("a").css("backgroundColor", "");
// 			$("#to_list").find("a").css("fontWeight", "normal");
// 		} else if(toValue == "MYTO") {
// 			$("#myto_list").find("a").css("backgroundColor", "");
// 			$("#myto_list").find("a").css("fontWeight", "normal");
// 		} else if(toValue == "MYTOCO") {
// 			$("#mytoco_list").find("a").css("backgroundColor", "");
// 			$("#mytoco_list").find("a").css("fontWeight", "normal");
// 		}
// 		toListSearch(toValue);
// 	}
// 	$("#to_search_word").focus();
// }

function toListReSearch(searchWord, mytoSearchWord, toValue) {
	var flag = true;
	var ietmNoSuchValue = $("#ietm_no_such_value").val();
	
	if(toValue == "TO") {
		$("#to_list a").each(function() {
			var toName = $(this).text();
			
			if(toName.indexOf(searchWord) > -1) {
				makeOpenTreeId("TO", $(this).attr("id"));
				flag = false;
				return false;
			}
		});
	} else if(toValue == "MYTO") {
		$("#myto_list a").each(function() {
			var mytoName = $(this).attr("name");
			
			if(mytoName.indexOf(mytoSearchWord) > -1) {
				makeOpenTreeId("MYTO", $(this).attr("id"));
				flag = false;
				return false;
			}
		});
	} else if(toValue == "MYTOCO") {
		$("#mytoco_list a").each(function() {
			var mytocoName = $(this).attr("name");
			
			if(mytocoName.indexOf(mytoSearchWord) > -1) {
				makeOpenTreeId("MYTOCO", $(this).attr("id"));
				flag = false;
				return false;
			}
		});
	}
	
	if(flag) {
		alert(ietmNoSuchValue);
	}
}

function searchKeyEvent(toValue) {
	if(event.keyCode == 13) {
		onceFocusing('to_');
		toListSearch('TO');
	}
}

function controllArrow(stat) {
	if(stat == "open") { 
		$(".ac-arrow").attr("class", "ac-arrow-open");
		$(".ac-task").show();
		$(".ac-desc").show();
		$(".fi-task").show();
		$(".ac-section .ac-arrow-open").attr("class", "ac-arrow");
	} else {
		
		$(".ac-arrow-open").attr("class", "ac-arrow");
		$(".ac-task").hide();
		$(".ac-desc").hide();
		$(".fi-task").hide();
		$(".ac-section .ac-arrow").attr("class", "ac-arrow-open");
	}
	
}

/*
function popupVersionInfo(toKey, tocoId, contId, verId, verStatus, changebasis) {

	var toKey = toKey;
	var tocoId = "DDCF3136-C3BD-4C7A-83BE-CF79F6271546";
	var tagRemove = /<(\/)?([a-zA-Z]*)(\s[a-zA-Z]*=[^>]*)?(\s)*(\/)?>/ig;
	var url = "/versionOpenWin.do";
	var width = 550;
	var height = 550;
	var param = "";
	var title = "";
	var text = "";
	if(!changebasis){
		changebasis = "";
	}

	var bizCode = $("#bizCode").val() || "";
	$.ajax({
		type : "POST",
		url : "versionInfo.do",
		dataType : "json",
		data : {
			toKey : toKey,
			tocoId : tocoId,
			contId : contId,
			verId : verId,
			changebasis : changebasis,
			verStatus : verStatus,
			lang: $('#lang').val() || "ko",
			bizCode : bizCode
		},
		success : function(data) {
			console.log("popupVersionInfo data : "+data);
			$("#chg_num").text(data.verChgNo);
			$("#chg_date").text(data.verChgDate);
			console.log("popupVersionInfo data.verHtml : "+data.verHtml);

			if(data && data.verHtml && data.verHtml.indexOf("in_table_ipb") > 0 && data.verHtml.indexOf("part_tr") > 0){
				console.log("popupVersionInfo IPB POPUP: "+data.verHtml.indexOf("in_table_ipb")+", "+data.verHtml.indexOf("part_tr"));
				width = 1500;
			}
			$("#link_text").html(data.verHtml);
			$("#link_text2").html(data.verHtml);

			$(".in_table_jg").css("border","none");

			// step에 없을경우  para찾는 부분 추가
			var tempXmlStr = data.fiCont;
			var xml = tempXmlStr, xmlDoc = $.parseXML( xml ), $xml = $( xmlDoc ), $para = $xml.find( "step" );
			console.log("popupVersionInfo $para.attr : "+$para.attr("changebasis"));
			if($para.attr("changebasis")){
				$("#pop_ver_changebasis").val($para.attr("changebasis"));
			}else{
				xml = tempXmlStr, xmlDoc = $.parseXML( xml ), $xml = $( xmlDoc ), $para = $xml.find( "para" );
				$("#pop_ver_changebasis").val($para.attr("changebasis"));
			}

			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if(verStatus == "u") {
				title = "변경전 내용";
				if($("#link_text").text() == "") {
					$("#link_text").text("수정된 항목임");
				}
			} else {
				title = "추가된 내용";
				if($("#link_text").text() == "") {
					$("#link_text").text("추가된 항목임");
				}
			}
			
			if(toKey.indexOf("FI") != -1) {
				$(data.fiCont).find("text").each(function() {
					text += $(this).text().replace(tagRemove,"");
				});
				text = text.replace(/&#13;/gi, "<br>");
				text = text.replace(/#13;/gi, "<br>");
				text = text.replace(/&amp;/gi, "");
				text = text.replace(/amp;/gi, "");
				
				$("#link_text").text(text.replace(/&/gi, ""));
			}

			$("#pop_ver_no").val(data.verChgNo);
			$("#pop_ver_date").val(data.verChgDate);
			$("#pop_ver_title").val(verStatus);
			if(changebasis && changebasis != ""){//TODO 영어
				$("#pop_ver_changebasis").val("변경 근거 : "+changebasis);
			}

			$("#pop_ver_html").val($("#link_text2").html());

			var opt = "width=" + width + ", height=" + height + ", scrollbars=yes";
			if( $("#bizCode").length > 0 && $("#bizCode").val() == "BLOCK2" ) {
				var screen = window.screen;
				var left = (screen.width - width) / 2;
				if ( !!screen.availLeft ) { left += screen.availLeft; }
				var top = (screen.height - height) / 2;
				opt = "left="+left+", top="+top+", width="+width+", height="+height+", scrollbars=yes";
			}
			window.open("", "frmVersionPup", opt);
			
			var frmData = document.frmVersionPup;
			frmData.submit();
		},
		error : function(data) {
			alert("popupVersionInfo error");
		}
	});
}
*/

//IPB NSN 선택
function selectIpbNsn(nsn) {
	var toKey = $("#to_key").val();
	toKey = "1T-50A-4-27";
	var noData = $("#ietm_no_data").val();
	var nsnMsg = $("#ietm_nsn_failed.info").val();
	$.ajax({
		type : "POST",
		url : $("#proectName").val()+"ietm/selectIpbNsn.do",
		dataType : "json",
		data : "toKey=" + encodeURIComponent(toKey) + "&nsn=" + nsn,
		success : function(data) {
			var dataSize = data.list.length;
			var result = "";
			var contents = "";
			
			if(dataSize != 0) {
				for(var i=0; i<dataSize; i++) {
					result = data.list[i];
					contents += "<tr>";
					contents += "	<td>" + result.partNo + "</td>";
					contents += "	<td>" + result.nsn + "</td>";
					contents += "	<td>" + result.cage + "</td>";
					contents += "</tr>";
				}
			} else {
				contents = "<tr><td colspan='4' title='"+noData+"'> "+noData+" </td></tr>";
			}
			
			$("#nsn_popup_body").html(contents);
			popUpEvent('nsn');
		},
		error : function() {
			alert(nsnMsg);
		}
	});	
	
}

function abspos(e) {
	this.x = e.clientX + (document.documentElement.scrollLeft?document.documentElement.scrollLeft:document.body.scrollLeft);
    this.y = e.clientY + (document.documentElement.scrollTop?document.documentElement.scrollTop:document.body.scrollTop);
    return this;
}

/**
 * 멀티 링크 클릭시 링크 리스트 레이어팝업 보기
 */
/*
//2022 10 20 jysi edit 본문, 팝업 모두 멀티 링크 가능하도록 수정
var mulinkpupFlag = false;
function showLinkMulti(linkId, event) {
	var winWidth = $( window ).width();
	var eventPageX = event.pageX;
	if($("[id='link_"+linkId+"']").length > 1){mulinkpupFlag = true;}	
	//2022 07 13 Park.J.S. 마우스 위치사용해서 표시하게 수정
	console.log("CALL showLinkMulti");
	var spanParent = $("#link_" + linkId).parent();
	var spanThis = $("#link_" + linkId);
	if(mulinkpupFlag){
		spanParent = $(".alert_cont").find("#link_" + linkId).parent();
		spanThis = $(".alert_cont").find("#link_" + linkId);
	}
	var spanMouseX = event.clientX - $(spanParent).offset().left;
	var spanMouseY = event.clientY - $(spanParent).offset().top;
	console.log("spanMouseX : "+spanMouseX+", spanMouseY : "+spanMouseY );
	console.log("spanParent : "+$(spanParent).width()+", this : "+$("#link_" + linkId).width() );
	
	//$("#link_" + linkId).css("left", 0);
	//$("#link_" + linkId).css("left", (($(spanParent).width())*-1 + spanMouseX));
	$(spanThis).css("left", 0);
	$(spanThis).css("left", (($(spanParent).width())*-1 + spanMouseX));
	
	if(winWidth - eventPageX < 270) {//화면에서 벗어 날경우 우치 수정
		//$("#link_" + linkId).css("left", - (winWidth - eventPageX));
		$(spanThis).css("left", - (winWidth - eventPageX));
	}
	
	//$("#link_" + linkId).show();
	$(spanThis).show();
	
	$(document).mouseup(function(event) {
		if (!$(".link-list").is(event.target)) {
			//$("#link_" + linkId).hide();
			$(spanThis).hide();
		}
		mulinkpupFlag = false;
	});
}
*/

function hideLinkMulti(linkId) {
	$("#link_" + linkId).hide();
}

//교범 새창 열기
/**
 * FI교범의 FI Diagram(Applet) 로부터 호출된 링크 정보
 */
//function SetLinkAttr(linktype, linkid, strTMName, viewtype, uuid) {
function SetLinkAttr(linktype, linkid, toKey, viewtype, uuid) {

	//실제 DB에 등록되어있는지 검사해야함

	vcKind = "01";
	if (linktype == "TO" || linktype == "목차") {
		vcKind = "01";
	} else if (linktype == "도해") {
		vcKind = "03";
	} else if (linktype == "테이블") {
		vcKind = "04";
	} else if (linktype == "IPB" || linktype == "RDN") {
		vcKind = "05";
	}

	if (viewtype == "window") {
		viewExOpenWin(toKey, linkid, "", vcKind, uuid);
	} else {
		viewExContents(toKey, linkid, "", vcKind, uuid);
	}
}


/**
 * [링크]링크 시 새창으로 호출 - 폼 팝업
 * @FunctionName	: viewExOpenWin
 * @AuthorDate		: ?? / 2017. ??. ??
  * @ModificationHistory	: LIM Y.M. / 2020. 3. 19. POST 방식으로 변경(to_key가 한글일 경우 필요)
  * @ModificationHistory	: 2022 10 04 Park.J.S. Update  : menuType ADD
 */
function viewExOpenWin(toKey, tocoId, searchWord, vcKind, contId, menuType) {
	/**
	 * 2022 08 30 tocoId 공백일 경우 tocoId 찾는 로직 추가.
	 * 2022 10 04 tocoId 공백일 경우 contId 가 이미지인지 확인 하는 로직 추가 이미지 일경우 contId룰 tocoId로 이용하게 함
	 */
	if(!tocoId || tocoId == "" ){
		console.log("tocoId is null : "+tocoId);
		
		$.ajax({
			type : "POST",
			url : $("#proectName").val()+"ietm/tocoInfo.do",
			data : "to_key=" + encodeURIComponent(toKey) + "&toco_id=" + contId,
			dataType : "json",
			success : function(data) {
				console.log(data.tocoInfo.pTocoId);
				console.log(data.tocoInfo.tocoName);
				if(data && data.tocoInfo && data.tocoInfo.pTocoId){
					menuType = data.tocoInfo.pTocoId;
				}
			},
			error : function(e) {
				$(".loading").hide();
			},
			complete : function(e) {
				$(".opacity_search").stop();
				$(".opacity_search").hide();
				$(".search_list_ul").show();
				$(".loading").hide();
			}
		});
		var strArray = contId.split(',');
		console.log("DATA : toKey=" + encodeURIComponent(toKey) + "&searchCond=" + contId + "&vcKind="+vcKind+"&vehicle_type="+$("#vehicle_type").val());
		$(".loading").show();
		$.ajax({
			type : "POST",
			data : "toKey=" + encodeURIComponent(toKey) + "&searchCond=" + contId + "&vcKind="+vcKind+"&vehicle_type="+$("#vehicle_type").val(),
			url : $("#proectName").val()+"ietm/findTocoId.do",
			datatype : "json",
			success : function(data) {
				if(data.list != null) {
					console.log("data.list : "+data.list);
					if(data.list && data.list.length == 1){
						console.log("data.list[0]"+data.list[0].tocoId);
						viewExOpenWin(toKey, data.list[0].tocoId, searchWord, vcKind, contId, menuType)
					}else{
						console.log("Error list size ==> "+data.list.length);
					}
				}
			},
			error : function(e) {
				alert("Search Failed\n" + e);
				$(".loading").hide();
			},
			complete : function(e) {
				$(".opacity_search").stop();
				$(".opacity_search").hide();
				$(".search_list_ul").show();
				$(".loading").hide();
			}
		});
	}else{
		/**
		 * 2021 11 30
		 * toKey 없는 경우화면에서 가져다가 쓰게 수정
		 */
		console.log("toKey 없는 경우화면에서 가져옴 : "+toKey+", "+tocoId);
		if(toKey == undefined || toKey == "undefined"){
			//$("#top_to_key").attr("title");
			toKey = $("#top_to_key").attr("title");
			console.log("toKey : "+toKey);
		}
		if(!menuType || menuType == undefined){
			//2022 10 12 vcKind 넘어 올경우 메뉴 타입 고정 처리
			if(vcKind == "03"){
				menuType = "GrphToco";
			}else if(vcKind == "04"){
				menuType = "TableToco";
			}
		}
		if(toChk(toKey, tocoId) == true) {
			//2022 10 04 Park.J.S. Update : +"&menuType="+menuType 추가 추후 KTA도 필요할경우 수정 필요
			//2023.07.10 jysi EDIT : 기존 LSAM, BLOCK2 무제한 팝업 처리에 NLS 추가
			console.log("bizCode : "+$("#bizCode").val());
			if($("#bizCode") && $("#bizCode").val() == "LSAM" || $("#bizCode") && $("#bizCode").val() == "BLOCK2" || $("#bizCode") && $("#bizCode").val() == "NLS" || $("#bizCode") && $("#bizCode").val() == "KBOB" || $("#bizCode").val() == "KICC" || $("#bizCode").val() == "MUAV" || $("#bizCode").val() == "SENSOR"){
				//2023 03 07 jysi EDIT : 새창으로 링크 호출 시 IPB인 경우 창 크기 조절(800=>1500px, 500=>800px)
				if(vcKind == "05"){
					var url = $("#proectName").val()+"ietm/ipbContOpener.do?toKey=" + encodeURIComponent(toKey) + "&tocoId=" + tocoId + "&searchWord=" + encodeURIComponent(searchWord) + "&vcKind=" + vcKind + "&contId=" + contId
					+ "&toName=''&contentOption=''&fiOption=''&outputMode=" + $("#output_mode").val()+"&menuType="+menuType;
					var param = "width=1500px, height=800px, left=500, top=100, toolbar=no, menubar=no resizable=yes";
					
					console.log("url : "+url+", param : "+param);
					
					window.open(url, "IPBviewTo", param);
				} else {
					var url = $("#proectName").val()+"ietm/contOpener.do?toKey=" + encodeURIComponent(toKey) + "&tocoId=" + tocoId + "&searchWord=" + encodeURIComponent(searchWord) + "&vcKind=" + vcKind + "&contId=" + contId
					+ "&toName=''&contentOption=''&fiOption=''&outputMode=" + $("#output_mode").val()+"&menuType="+menuType;
					var param = "width=1000px, height=800px, left=500, top=100, toolbar=no, menubar=no resizable=yes";
					
					console.log("url="+url);
					console.log("param="+param);
					
					window.open(url, "viewTo"+new Date(), param);
				}
			}else{
				var wopenform = document.winopen_form;
				var url = $("#proectName").val()+"ietm/contOpener.do";
				//var param = "width=400px, height=680px, left=500, top=100, scrollbars=no, status=no, toolbar=no, menubar=no, resizable=no";
				var param = "width=1000px, height=800px, left=500, top=100, toolbar=no, menubar=no resizable=yes";
				console.log("url : "+url+", param : "+param);
				var openFocus = window.open("", "pupExWin", param);
				
				wopenform.action				= url;
				wopenform.method				= "post";
				wopenform.target				= "pupExWin";
				//2022 12 27 Park.J.S. Update : submit의 경우 encode 필요 없어서 수정 encodeURIComponent(toKey) -> toKey
				wopenform.toKey.value			= toKey;
				wopenform.tocoId.value			= tocoId;
				wopenform.searchWord.value		= encodeURIComponent(searchWord);
				wopenform.vcKind.value			= vcKind;
				wopenform.contId.value			= contId;
				wopenform.toName.value			= "";
				wopenform.contentOption.value 	= "";
				wopenform.fiOption.value		= "";
				wopenform.outputMode.value		= $("#output_mode").val();
				
				wopenform.submit();
				
				openFocus.focus();
			}
		}else{
			console.log("toChk(toKey, tocoId) Fale");
		}
	}
}

//RDN 클릭시 IPB 새창열기
//2021 10 07 rdn 직접 사용으로 수정
//2021 10 22 음영처리시에 listName  참조 가능하게 수정
function rdnOpenWin(toKey, rdn, searchWord, vcKind, contId, linkName, listName) {
	console.log("Call  rdnOpenWin : "+searchWord+", "+vcKind+", "+contId+", "+linkName+", "+listName);
	var ietmManualNotExist = $("#ietm_manual_not_exist").val();
	var tocoId = "";
		console.log("toKey : "+toKey+", rdn : "+rdn);
		$.ajax({
			type : "POST",
			url : $("#proectName").val()+"ietm/partInfo.do",
			dataType : "json",
			data : {
				toKey : toKey,
				rdn : listName,
				rdnId: rdn
			},
			success : function(data) {
				//2021 10 07  rdn 직접 사용으로 수정
				//tocoId = data.tocoId;
				tocoId = rdn;
				//2022 10 07 Park.J.S. ADD : 최종 조회 안될경우 백업 로직 용으로 추가(이렇게 되면 로직 복잡도만 증가하나 현재 만들어진저작 교범이 제각각이라 우선 백업 로직으로 처리함)
				var dataTocoId = "";
				if(data.partinfodto && data.partinfodto.tocoId){
					dataTocoId = data.partinfodto.tocoId;
				}
				console.log("dataTocoId : "+dataTocoId);
				//2022 09 01 RDN이 아이디 코드가 아닐경우 존재(해당경우는 아이디 받아서 사용하게 수정)
				if(tocoId.length < 15){
					console.log("DATA : toKey=" + encodeURIComponent(toKey) + "&searchCond=" + rdn + "&vcKind="+vcKind+"&vehicle_type="+$("#vehicle_type").val());
					$(".loading").show();
					$.ajax({
						type : "POST",
						data : "toKey=" + encodeURIComponent(toKey) + "&searchCond=" + rdn + "&vcKind="+vcKind+"&vehicle_type="+$("#vehicle_type").val(),
						url : $("#proectName").val()+"ietm/findTocoId.do",
						datatype : "json",
						success : function(data) {
							if(data.list != null) {
								console.log("data.list : "+data.list);
								//RDN의 경우 여러개 나오면 최초의 건으로 이동처리
								if(data.list && data.list.length >= 1){
									console.log("data.list[0]"+data.list[0].tocoId);
									rdnOpenWin(toKey, data.list[0].tocoId, searchWord, vcKind, contId, linkName, listName);
								}else{
									console.log("Can not Find RDN list size ==> "+data.list.length);
									if(dataTocoId && dataTocoId != ""){
										console.log("Back up Logioc Start use todoid : "+dataTocoId);
										if(listName != "") {
											console.log("Use listName");
											rdn = encodeURIComponent(listName);
										}else if(linkName != "") {
											console.log("Use linkName");
											rdn = encodeURIComponent(linkName);
										}else{
											console.log("Use data.partName");
											rdn = data.partName;
										}
										console.log("tokey : "+encodeURIComponent(toKey));
										console.log("tokey : "+encodeURI(encodeURIComponent(toKey)));
										/*
										var url = $("#proectName").val()+"ietm/ipbContOpener.do?toKey=" + encodeURIComponent(toKey) + "&tocoId=" + dataTocoId + "&rdn=" + encodeURIComponent(rdn) + "&vcKind=" + vcKind + "&contId=" + contId
										+ "&toName=''&contentOption=''&fiOption=''&outputMode=" + $("#output_mode").val();
										var param = "width=1500px, height=800px, left=500, top=100, toolbar=no, menubar=no resizable=yes";
										console.log("url : "+url);
										window.open(url, "IPBviewTo", param);
										*/
										//2022 12 28 Park.J.S. Update : 한글 처리 위해 POST 사용
										var wopenform = document.winopen_ipb_form;
										var url = $("#proectName").val()+"ietm/ipbContOpener.do";
										//var param = "width=400px, height=680px, left=500, top=100, scrollbars=no, status=no, toolbar=no, menubar=no, resizable=no";
										var param = "width=1500px, height=800px, left=500, top=100, toolbar=no, menubar=no resizable=yes";
										console.log("url : "+url+", param : "+param);
										var openFocus = window.open("", "IPBviewTo", param);
										
										wopenform.action				= url;
										wopenform.method				= "post";
										wopenform.target				= "IPBviewTo";
										wopenform.toKey.value			= encodeURIComponent(toKey);
										wopenform.tocoId.value			= tocoId;
										wopenform.rdn.value				= encodeURIComponent(rdn);
										wopenform.vcKind.value			= vcKind;
										wopenform.contId.value			= contId;
										wopenform.toName.value			= "";
										wopenform.contentOption.value 	= "";
										wopenform.fiOption.value		= "";
										wopenform.outputMode.value		= $("#output_mode").val();
										wopenform.submit();
										openFocus.focus();
										
										console.log("Back up Logioc Fin");
									}
								}
							}
						},
						error : function(e) {
							alert("Search Failed\n" + e);
							$(".loading").hide();
						},
						complete : function(e) {
							$(".opacity_search").stop();
							$(".opacity_search").hide();
							$(".search_list_ul").show();
							$(".loading").hide();
						}
					});
				}else{
					if(data.partinfodto && data.partinfodto.tocoId && data.partinfodto.tocoId != ""){
						console.log("Set Data ID");
						tocoId = data.partinfodto.tocoId;
					}else{
						console.log("Use rdn ID");
						tocoId = rdn;
					}
					if(tocoId != null) {
						//2021 10 22 RDN(참조지시번호) 이용해서 부품명(data.tocoId) 확인 하는 내용으로 수정
						//2022 09 21 Park.J.S. 조회값 최우선 처리하도록 수정 RDN 검색에서 해당 RDN에 붉은 글씨 처리하기위해서(RDN 중복시에 모든 RDN에 불들어옴)
						if(listName != "") {
							console.log("Use listName");
							rdn = encodeURIComponent(listName);
						}else if(linkName != "") {
							console.log("Use linkName");
							rdn = encodeURIComponent(linkName);
						}else{
							console.log("Use data.partName");
							rdn = data.partName;
						}
						console.log("data.tocoId : "+data.tocoId);
						console.log("toKey : "+toKey);
						/*
						var url = $("#proectName").val()+"ietm/ipbContOpener.do?toKey=" + encodeURIComponent(toKey) + "&tocoId=" + tocoId + "&rdn=" + encodeURIComponent(rdn) + "&vcKind=" + vcKind + "&contId=" + contId
						+ "&toName=''&contentOption=''&fiOption=''&outputMode=" + $("#output_mode").val();
						var param = "width=1500px, height=800px, left=500, top=100, toolbar=no, menubar=no resizable=yes";
						console.log("url : "+url);
						window.open(url, "IPBviewTo", param);
						*/
						//2022 12 28 Park.J.S. Update : 한글 처리 위해 POST 사용 관련해서 무제한 팝업이 필요시에는 별도 수정 필요
						var wopenform = document.winopen_ipb_form;
						var url = $("#proectName").val()+"ietm/ipbContOpener.do";
						var param = "width=1500px, height=800px, left=500, top=100, toolbar=no, menubar=no resizable=yes";
						console.log("url : "+url+", param : "+param);
						var openFocus = window.open("", "IPBviewTo", param);
						
						wopenform.action				= url;
						wopenform.method				= "post";
						wopenform.target				= "IPBviewTo";
						wopenform.toKey.value			= encodeURIComponent(toKey);
						wopenform.tocoId.value			= tocoId;
						wopenform.rdn.value				= encodeURIComponent(rdn);
						wopenform.vcKind.value			= vcKind;
						wopenform.contId.value			= contId;
						wopenform.toName.value			= "";
						wopenform.contentOption.value 	= "";
						wopenform.fiOption.value		= "";
						wopenform.outputMode.value		= $("#output_mode").val();
						wopenform.submit();
						openFocus.focus();
					} else {
						alert(ietmManualNotExist);
					}
				}
			},
			error : function() {
				//alert("error!! Check function rdnOpenWin");
			}
		});
	
}
function ipbIetmOpener() {
	
	var toKey = $("#to_key").val();
	var tocoId = $("#toco_id").val();
	var vcKind = $("#vc_kind").val();
	
	viewToContents(toKey, tocoId, '', vcKind, '','' ,'N');
}


//function ietmPdf() {
//2022 09 01 Park.J.S. : PDF 파일 다운시에는 바로 열기로 변경
function downOutsidefile(fileName) {
	var ietmFileDownFailed = $("#ietm_file_down_failed").val();
	if(filePathChk(fileName, 'iPdf') == true) {
		if(fileName.toLowerCase().indexOf(".pdf") > 0){
			console.log("PDF File Down");
			window.open($("#proectName").val()+"/web_lsam/ietmdata/outsidefile/" + encodeURIComponent(fileName), "","").focus();
		}else{
				//20200715 add LYM 파일명이 한글일 경우 인코딩 처리
				//fileName = encodeURI(encodeURIComponent(fileName));
				console.log("encodeURIComponent(fileName) : "+$("#proectName").val()+"ietm/downOutsidefile.do?fileName=" + encodeURI(encodeURIComponent(fileName),"UTF-8"));
				//console.log("encodeURIComponent(fileName) : "+$("#proectName").val()+"ietm/downOutsidefile.do?fileName=" + escape(fileName));
				location.href = $("#proectName").val()+"ietm/downOutsidefile.do?fileName=" + encodeURI(encodeURIComponent(fileName),"UTF-8");
		}
	} else {
		alert(ietmFileDownFailed);
	}
}

function linLayer() {
	$("#chg_num").text("");
	$("#chg_date").text("");
	$("#link_text").text("");
	$("#ver_link_layer").hide();
}


//--------------------------------------------------

//내용 시현 중 링크 리스트 레이어 보기
function linkView(link_id)
{
	var obj = document.getElementById(link_id);
	if (obj != null) {
		obj.style.display = "";
	}
}

// //CL교범의 체크리스트 항목 클릭시
// function checkContentCL(toKey, tocoId, checklistId) {
// 	var chkVal = $("#" + checklistId).prop("checked");
//
// 	$.ajax({
// 			type : "POST",
// 			url : $("#proectName").val()+"ietm/chkCL.do",
// 			dataType : "json",
// 			data : {
// 				toKey : toKey,
// 				tocoId : tocoId,
// 				checklistId : checklistId,
// 				chkVal : chkVal
// 			},
// 			success : function(data) {
// 				//2023 01 09 Park.J.S. Update : 체크박스 선택 이벤트 처리 추가
// 				if(chkVal == true) {
// 					clCount++;
// 					$("#"+checklistId).parent().css("color","#999999");
// 				} else {
// 					clCount--;
// 					$("#"+checklistId).parent().css("color","unset");
// 				}
//
// 			},
// 			error : function() {
// 				//alert("checkContentCL error");
// 			}
// 		});
// }

// var clCount = 0;
//
// function chkCL(clArr) {
// 	if(clArr != null ) {
// 		var arr = new Array();
// 		var clArrSize = clArr.length;
// 		var toKey = $("#to_key").val();
// 		var tocoId = $("#toco_id").val();
// 		var clTokey = "";
// 		var clTocoId = "";
// 		var clChkId = "";
// 		for(var i=0; i<clArrSize; i++) {
// 			arr = clArr[i].split("_");
// 			clTokey = arr[0];
// 			clTocoId = arr[1];
// 			clChkId = arr[2];
// 			if(clTokey == toKey) {
// 				if($("#" + clChkId).prop("checked") == false) {
// 					$("#" + clChkId).prop("checked", true);
// 					//2023 01 09 Park.J.S. ADD : 체크 박스 선택된 경우 글자 색 변경
// 					$("#" + clChkId).parent().css("color","#999999");
// 					clCount++;
// 				}
// 			}
//
// 		}
// 	}
// }

function tocoListFocus(tocoId) {
	$('#toco_list a').css('background-color' , '');
	$('#toco_list a').css('font-weight' , '');
	$('#mytoco_list a').css('background-color' , '');
	$('#mytoco_list a').css('font-weight' , '');
	$("#" + tocoId).css('background-color' , 'gold');
	$("#" + tocoId).css('font-weight' , 'bold');
	
	//목차 리스트 선택시 목차 트리에 포커스
	if($("#" + tocoId).attr("folderChk") == "Y") {
		$("#" + tocoId).parent("li").toggleClass("open");
		if($("#" + tocoId).parent("li").hasClass("open")) {
			$("#" + tocoId).prev().text("-").removeClass("plus").addClass("minus");
			$("#" + tocoId).parent("li").find(">ul").slideDown(100);
		}
	}
	$("#" + tocoId).focus();
}

function confirmCLChk(tocoId) {
	var ietmNextPageContents = $("#ietm_next_page_contents").val();
	console.log("ietmNextPageContents : "+ietmNextPageContents);
	console.log("clCount : "+clCount);
	if(clCount != 0) {
		var result = confirm( clCount + ietmNextPageContents);
		if(result == false) {
			var beforeToco = tocoIdHistory[parseInt(historyIndex-1)];
			$('#toco_list a').css('background-color' , '');
			$('#toco_list a').css('font-weight' , '');
			$('#mytoco_list a').css('background-color' , '');
			$('#mytoco_list a').css('font-weight' , '');
			$('#'+beforeToco).css('background-color' , 'gold');
			$('#'+beforeToco).css('font-weight' , 'bold');
			$(".loading").hide();
			return false;
		} else {
			clCount = 0;
			return true;
		}
	}
}



function alertHeight(tocoId) {
	staticScroll = new Array();
	staticType = new Array();
	staticContent = new Array(); 
//	//각 경고클래스의 위치 찾기, 찾은 위치값 담기
	var prevCount = $("#toco_cont_"+tocoId).parent().index();
	var toDiv = $("#toco_cont_" + tocoId).parent();
	var sHeight = 0;
	var prevHeight = 0;
	var cursorP;
	var typeString = "";
	
	for (var int = 0; int < prevCount; int++) {
		toDiv = toDiv.prev();
		prevHeight += toDiv.height();
	}
	$("#main_contents .alert_class").each(function() {
		sHeight = $(this).position().top;
		cursorP = $(this).parent();
		
		while (!(cursorP.attr("id") == "main_contents")) {
			if(cursorP[0].localName != "ul") {
				if(cursorP[0].localName == "li") {
					sHeight = cursorP.position().top;
				} else {
					sHeight += cursorP.position().top;
				}
			}
			cursorP = cursorP.parent();
		}
		
		sHeight += $("#main_contents").scrollTop();
		if($(this).hasClass("ac-alert-caution")) {
			typeString = "caution";
		} else if($(this).hasClass("ac-alert-warning")) {
			typeString = "warning";
		} else if($(this).hasClass("ac-alert-note")) {
			typeString = "note";
		}
		
		staticScroll.push(sHeight);
		
		//alert 타입
		staticType.push(typeString);
		
		//alert 내용
		staticContent.push($($(this)).html());
		
//		$("#main_contents").scrollTop($(this).position().top);
	});
	staticScroll.sort(function(a,b){return b-a});
	staticType.reverse();
	staticContent.reverse();
}

function toChk(toKey, tocoId) {
	var rt = true;
	var ietmManualNotExist = $("#ietm_manual_not_exist").val();
	$.ajax({
		type : "POST",
	 	data : "toKey=" + encodeURIComponent(toKey) + "&tocoId=" + tocoId,
		url : $("#proectName").val()+"ietm/chkToInfo.do",
		async: false,
		dataType : "json",
		success : function(data){
			if(data.result == null) {
				alert(ietmManualNotExist);
				rt = false;
			}
		}
	});
	return rt;
}

function linkToToc(tocoId) {
	var toKey = $("#to_key").val();
	
	viewExContents(toKey, tocoId, '', '01', '');
}

function closeAlert() {
	var staticSeie = staticScroll.length;
	
	//2023 03 07 jysi EDIT : 아래 반복문 때문에 스크롤바 없는 경우 두번째 경고창 팝업 안되서 제거
	//                       추후 사이드이펙트 발생 시 복구하기 위해 주석처리	                         
	/*
	for (var int = 0; int < staticSeie; int++) {
		toHeight = staticScroll.pop();
		if(toHeight - (($("#main_contents").height() / 3) * 2) < $("#main_contents").scrollTop()) {
			staticType.pop();
			staticContent.pop();
		} else {
			staticScroll.push(toHeight);
			break;
		}
	}
	*/
	
	//2022 10 20 jysi ADD : 본문과 경고창 id가 겹쳐 다중링크 작동 안하는 현상 수정 위해 닫히며 내용 삭제
	$(".alert_cont").empty();
	$('.pup_alert_body').hide();
	
	//2023 03 07 jysi ADD : 1.스크롤바 없고 2.경고창이 여러개인 경우 닫기 버튼 누르면 다음 경고창 출력
	scrollBarChk(); 
}

function otherLink(toKey) {
	var toId = "";
	
	$(".searchList").each(function() {
		if($(this).attr("name") == toKey) {
			toId = $(this).attr("id");
		}
	});
	
	if(toId != "") {
		selectTo(toId, 'to');
	}
}

/**
 * 2021 11 15 
 * 특수 문자 처리위해 추가
 * @param encodedString
 * @returns
 */
function decodeEntities(encodedString) {
    var textArea = document.createElement('textarea');
    textArea.innerHTML = encodedString;
    return textArea.value;
}

/**
 * 2022 07 05 Park J.S.
 * 대체품 링크 기능 구현
 * @param nsn	: 부품 번호 
 * @param pn	: 품목 번호
 * @History : 2023.11.16 - T50, kai_std 를 이용해서 대체품 팝업되도록 보완 - jingi.kim
 * @returns
 */
function popIpbReplacePart(nsn, pn, kaistd) {
	
	var width = 505;
	var height = 505;
	var param = "?";
	param += !!nsn != "" ? "nsn=" + nsn : "";
	param += "&pn="+pn;
	param += !!kaistd != "" ? "&kaistd=" + kaistd : "";
	
	window.open($("#proectName").val()+"ietm/ipbReplacePartPup.do"+ param, "","width=" + width + ", height=" + height + ", scrollbars=yes").focus();
}
/**
 * 2022 03 22 Park.J.S
 * 책자교범 페이지 처리용
 * @returns
 */
function makePaperSystree(code){
	$.ajax({
		type : "POST",
	 	data : {BookType: code},
		url : $("#proectName").val()+"ietm/getPaperSystree.do",
		async: false,
		dataType : "json",
		success : function(data){
			console.log(data.returnData);
			$(".contents_div").hide();
			$("#main_cover").hide();
			$("#frame_contents").hide();
			$("#main_contents").html("");
			$("#main_contents").show();
			$("#main_contents").html("");
			$("#main_contents").html(data.returnData.rtSB);
			/*
			if(data.result == null) {
				//paperDownloadCheck(encodeURIComponent($("#" + toKey).attr("title")));
			}
			*/
		}
	});
}
/**
 * 2022 07 25 Park.J.S.
 * IPB 상위 메뉴 이동 기능 추가
 * @param menuNumStr
 * @returns
 */
function ipbHighContentCall(menuNumStr, mType) {
	var tempBoolean = true;
	$("#toco_list").find('a').each(function() {
		var menuName = mType == "prname" ? $(this).attr("name") : $(this).attr("id");
		//console.log("menuNumStr : "+menuNumStr+", menuName : "+menuName+", tempBoolean : "+tempBoolean+", onclick : "+$(this).attr("onclick"));
		var condi = mType == "prname" ? menuName.startsWith(menuNumStr) : menuName == menuNumStr;
		if( condi && tempBoolean && ($(this).attr("onclick")+"") != "" ){
			$(this).trigger("click");
			return false;
		}
	});
}

/**
 * 2022 08 12 언어 변경 메뉴 처리위해 추가
 * 2022 08 16 언어 설정 교범 명의 앞자리 이용하게 수정 처리함
 * @param toKey
 * @param tocoId
 * @param searchWord
 * @param vcKind
 * @param contId
 * @returns
 */
function viewKorEngContents(toKey, tocoId, searchWord, vcKind, contId){
	/*
	var langStr = "ko";
	if(toKey.indexOf("T.T.")>=0){
		langStr = "ko";
	}else{
		langStr = "en";
	}
	$.ajax({
		type : "POST",
		url : $("#proectName").val()+"ietm/languageUpdate.do",
		dataType : 'json',
		data : {
			lang : langStr
		},
		success : function(data) {
			viewExContents(toKey, tocoId, searchWord, vcKind, contId);
		},
		error : function(r, s, e) {
		}
	});
	*/
	viewExContents(toKey, tocoId, searchWord, vcKind, contId);
}


/**
 * <pre>
 * linker parser 이벤트 처리시에 클릭시에 색 변경 처리 
 * </pre>
 * 2022 09 23 Park.J.S
 * @param obj
 * @returns
 */


/**
 * <pre>
 * 2022 12 08 Park.J.S
 * 프레임 보기에서 이미지 인지 판단해서 true false 리턴
 * </pre>
 * @param obj 이미지인지 판다해야하는 객체
 * @returns 이미지일경우 true 아니면 false
 */
function frmaeImageCheck(obj){
	console.log("Image Check Start");
	//이미지 인지 판단.
	if($(obj).hasClass("ac-object")) {//일반적인 이미지
		return true;
	}else {
		if($(obj).hasClass("div_version")){
			console.log("Image Check : div_version");
			console.log("Image Check : "+$(obj).children("div:eq(0)"));
			if($(obj).children("div:eq(0)").hasClass("ac-object")) {//변경바가 존재하는 이미지 변경바 구조 변경될경우 수정 필요 ex)<div class='div_version'><span class='version_bar_last'><div class='frame_0 ac-object'>
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	console.log("Image Check Fin");
}



/**
 * [링크]WC CLICK 새창으로 호출 - 폼 팝업
 * @FunctionName		: viewWCLink
 * @AuthorDate			: 2023 01 12 Park.J.S.
 * @param toKey			: 교범번호
 * @param searchWord	: 계통
 * @returns
 */
function viewWCLink(toKey, searchWord) {
	var wopenform = document.winopen_form;
	var url = $("#proectName").val()+"ietm/viewWCLink.do";
	//var param = "width=400px, height=680px, left=500, top=100, scrollbars=no, status=no, toolbar=no, menubar=no, resizable=no";
	var param = "width=1000px, height=800px, left=500, top=100, toolbar=no, menubar=no resizable=yes";
	console.log("url : "+url+", param : "+param);
	var openFocus = window.open("", "viewWCLink", param);
	
	wopenform.action				= url;
	wopenform.method				= "post";
	wopenform.target				= "viewWCLink";
	//2022 12 27 Park.J.S. Update : submit의 경우 encode 필요 없어서 수정 encodeURIComponent(toKey) -> toKey
	wopenform.toKey.value			= toKey;
	wopenform.tocoId.value			= "";
	wopenform.searchWord.value		= searchWord;
	wopenform.vcKind.value			= vcKind;
	wopenform.contId.value			= contId;
	wopenform.toName.value			= "01";
	wopenform.contentOption.value 	= "01";
	wopenform.fiOption.value		= "01";
	wopenform.outputMode.value		= $("#output_mode").val();
	
	wopenform.submit();
	
	openFocus.focus();
}



/**
 * [팝업]경고창 팝업 출력 함수
 * @FunctionName		: showAlertPup
 * @AuthorDate			: 2023 03 07 jysi
 * @ModificationHistory	: 2023.03.30 jingi.kim - 팝업창에서 enter로 닫히도록 focus
 * @param 
 * @returns
 */
function showAlertPup() {
	// 2023.03.31 - 내용이 없이 팝업되는 경우 방지 - jingi.kim
	if ( staticContent.length <= 0 ) return;
	
	var stType = "";
	var ietmCaution = $("#ietm_caution").val();
	var ietmCautionImportant = $("#ietm_caution_important").val();
	var ietmCautionWarning = $("#ietm_caution_warning").val();
	
	$("#alert_type").removeClass("warning");
	$("#alert_type").removeClass("caution");
	$("#alert_type").removeClass("note");
	stType = staticType.pop();
	switch (stType) {
	case "warning":
		$("#alert_type").addClass("warning");
		$("#alert_string").text(ietmCautionWarning);
		break;
	case "caution":
		$("#alert_type").addClass("caution");
		$("#alert_string").text(ietmCaution);
		break;
	case "note":
		$("#alert_type").addClass("note");
		$("#alert_string").text(ietmCautionImportant);
		break;
	default:
		break;
	}
	$(".alert_cont").html(staticContent.pop());
	
	//2023.05.11 jysi EDIT : 경고문 내 이미지 크기가 경고문 팝업보다 큰 경우 이미지 크기 조정로직 추가
	//이미지 최대 가로사이즈 구하기
	var alertFormWidth = $(".alert_form").width();
	var alertContPadL = Number($(".alert_cont").css("padding-left").replace("px",""));
	var alertContPadR = Number($(".alert_cont").css("padding-right").replace("px",""));
	var tdPadL = 0;
	var tdPadR = 0;
	var alertImgWidth = 450; //기본값
	
	alertImgWidth = alertFormWidth - (alertContPadL + alertContPadR + 20); //최대 가로사이즈 1차 계산
	
	//이미지 사이즈 조정
	$(".alert_cont").find("[name=pngDivArea]").each(function(){
		//이미지가 테이블 안에 존재할 경우 테이블 패딩 계산
		if ($(this).closest('td').length > 0) {
			tdPadL = Number($(this).closest('td').css("padding-left").replace("px",""));
			tdPadR = Number($(this).closest('td').css("padding-right").replace("px",""));
		}
		alertImgWidthFin = alertImgWidth - (tdPadL + tdPadR); //최대 가로사이즈 2차 계산
		
		$(this).children("[name=pngDivArea]").each(function(){
			//pngDivArea 사이즈 조정(너비,높이)
			var tmpDivWidth = Number($(this).css("width").replace("px",""));
			if(tmpDivWidth > alertImgWidthFin) {
				$(this).css("width", alertImgWidthFin + "px");
				var tmpDivHeight = Number($(this).css("height").replace("px",""));
				tmpDivHeight = tmpDivHeight / tmpDivWidth * alertImgWidthFin;
				$(this).css("height", Math.round(tmpDivHeight) + "px");
			}
			$(this).children("[name=pngImgArea]").each(function(){
				//pngImgArea 사이즈 조정(너비,높이)
				var tmpImgWidth = Number($(this).css("width").replace("px",""));
				if(tmpImgWidth > alertImgWidthFin) {
					$(this).css("width", alertImgWidthFin + "px");
					var tmpImgHeight = Number($(this).css("height").replace("px",""));
					tmpImgHeight = tmpImgHeight / tmpImgWidth * alertImgWidthFin;
					$(this).css("height", Math.round(tmpImgHeight) + "px");
				}
			});
		});
	})

	if (stType != undefined){
		$(".pup_alert_body").show();
		//2023.05.17 jysi EDIT : 경고문구 팝업 시 경고팝업 내 스크롤위치 초기화
		$(".alert_cont").scrollTop(0);
		// 2023.03.30 - 닫기 버튼에 focus :: 엔터키로 팝업창 닫기 가능하게 - jingi.kim
		var btnClose = $(".pup_alert_body span.alert_bottom a");
		if ( btnClose.length > 0 ) {
			btnClose.focus();
		}
	}
	
	$(".alert_class:eq(0)").removeClass("alert_class");	
}



/**
 * [팝업]스크롤바 여부 확인
 * @FunctionName		: hasScrollBar
 * @AuthorDate			: 2023 03 09 jysi
 * @param 
 * @returns
 */
$.fn.hasScrollBar = function() {
    return (this.prop("scrollHeight") == 0 && this.prop("clientHeight") == 0) || (this.prop("scrollHeight") > this.prop("clientHeight"));
};



/**
 * [팝업]스크롤바가 없어도 경고창 팝업 출력하는 함수
 * @FunctionName		: scrollBarChk
 * @AuthorDate			: 2023 03 07 jysi
 * @param 
 * @returns
 */
function scrollBarChk() {
	try{
		var optAlert = $("#opt_alert").val(); 
		
		if(!$("#main_contents").hasScrollBar()){
			if(optAlert == "01"){
				showAlertPup();
			}
		}
	} catch(err){
		console.log("function scrollBarChk() Error!")
	}
}


/**
 * 2024.11.5. - 컨텐츠 stylesheet 보정 함수 - jingi.kim
 */
function adjustStyle() {

    //2025.04.07 - 문자 표시 전체 보정 - jingi.kim
    adjustXMLWord();

	//2025.05.21 - table width - jingi.kim
	adjustTableWidth();

}

// XML 특수문자 치환
function adjustXMLWord( divArea ) {

	const xList = ["span", "p", "a", "li"]; //li 추가 - jsh - 25-06-16
    $.each(xList, function(idx, val){
        let _tagList = $(val, ".main-content", divArea);
        if (_tagList.length == 0) return;

		// html 구조 유지를 위해 기존 text() 치환에서 html() 기반 치환 방식으로 수정 - osm
        _tagList.each(function(idx){
            if ($(this).html().includes("&amp;")) {
                $(this).text( $(this).html().replace(/&amp;/gi, "&") );
            }
        });

        // 문자기호 replace
        const _cnvList = _tagList.filter(function(){
            return $(this).text().includes("&#");
        });
        if (_cnvList.length > 0) {
            _cnvList.each(function(){
                convertXmlSpecialWord( $(this) );
            });
        }

        // 아이콘 기호 replace
        const _iconList = _tagList.filter(function(){
            return $(this).text().includes("&#24;");
        });
        if ( _iconList.length > 0 ) {
            _iconList.each(function(){
                convertXmlIcon( $(this) );
            });
        }

        // 특수문자 제거
        const _cnvChars = _tagList.filter(function(){
            return $(this).text().includes("&#");
        });
        if (_cnvChars.length > 0) {
            _cnvChars.each(function(){
                let cnvText = removeXmlSpecialChars($(this).text());
                $(this).html(cnvText);
            });
        }

    });

}

function convertXmlSpecialWord(_tag) {
    let checkText = _tag.text().trim() != "" ? _tag.text() : "";
    if (checkText === "") return;

    //remove Tag Char
    checkText = removeXmlSpecialChars(checkText, true);
    _tag.text(checkText);

    //&#138 == align
    if (checkText.includes("&#138;")) cnvWordAlign(_tag);

    //&#169, &#188, &#199 == font
    if (checkText.includes("&#169;")) cnvWordFont(_tag);
    if (checkText.includes("&#188;")) cnvWordFont(_tag);
    if (checkText.includes("&#199;")) cnvWordFont(_tag);

    //&#136 == sub/sup
    if (checkText.includes("&#136;")) cnvWordSub(_tag);
    if (checkText.includes("&#200;")) cnvWordSub(_tag);

    //&#130 == sub/sup 와 같이 사용됨
    if (checkText.includes("&#130;")) cnvWordSub2(_tag);

}

function cnvWordAlign(_tag) {
    //&#0; == left
    if (_tag.text().includes("&#138;&#0;")) {
        if ( !_tag.css("text-align") || _tag.css("text-align") != "left") _tag.css("text-align", "left");
        if ( !!_tag.parent() && _tag.parent().prop("tagName") === "TD" ) {
            if ( !_tag.parent().css("text-align") || _tag.parent().css("text-align") != "left") _tag.parent().css("text-align", "left");
        }
    }
    //&#1; == right
    if (_tag.text().includes("&#138;&#1;")) {
        if ( !_tag.css("text-align") || _tag.css("text-align") != "right") _tag.css("text-align", "right");
        if ( !!_tag.parent() && _tag.parent().prop("tagName") === "TD" ) {
            if ( !_tag.parent().css("text-align") || _tag.parent().css("text-align") != "right") _tag.parent().css("text-align", "right");
        }
    }
    //&#2; == center
    if (_tag.text().includes("&#138;&#2;")) {
        if ( !_tag.css("text-align") || _tag.css("text-align") != "center") _tag.css("text-align", "center");
        if ( !!_tag.parent() && _tag.parent().prop("tagName") === "TD" ) {
            if ( !_tag.parent().css("text-align") || _tag.parent().css("text-align") != "center") _tag.parent().css("text-align", "center");
        }
    }
    //&#4; == left
    if (_tag.text().includes("&#138;&#4;")) {
        if ( !_tag.css("text-align") || _tag.css("text-align") != "left") _tag.css("text-align", "left");
        if ( !!_tag.parent() && _tag.parent().prop("tagName") === "TD" ) {
            if ( !_tag.parent().css("text-align") || _tag.parent().css("text-align") != "left") _tag.parent().css("text-align", "left");
        }
    }

}

function cnvWordFont(_tag) {
    //Bold
    if (_tag.text().includes("&#169;")) {
        let txt = _tag.text().replace("&#169;", "<b>");
        txt = txt.replace("&#169;", "</b>");
        _tag.text( txt );
    }

    //Italic
    if (_tag.text().includes("&#188;")) {
        let txt = _tag.text().replace("&#188;", "<i>");
        txt = txt.replace("&#188;", "</i>");
        _tag.text( txt );
    }

    //Underline
    if (_tag.text().includes("&#199;")) {
        let txt = _tag.text().replace("&#199;", "<u>");
        txt = txt.replace("&#199;", "</u>");
        _tag.text( txt );
    }

}

function cnvWordSub(_tag) {
    //<SUP>
    if (_tag.text().includes("&#136;&#-300;")) {
        let txt = _tag.text().replace(/&#136;&#-300;/gi, "<sup>");
        if (_tag.text().includes("&#136;&#0;")) {
            txt = txt.replace(/&#136;&#0;/gi, "</sup>");
        }
        _tag.text( txt );
    }
    //<SUB>
    if (_tag.text().includes("&#136;&#200;")) {
        let txt = _tag.text().replace(/&#136;&#200;/gi, "<sub>");
        if (_tag.text().includes("&#136;&#0;")) {
            txt = txt.replace(/&#136;&#0;/gi, "</sub>");
        }
        _tag.text( txt );
    }
}

function cnvWordSub2(_tag) {
    //<SUP>
    if (_tag.text().includes("&#130;&#700;")) {
        let txt = _tag.text().replace(/&#130;&#700;/gi, "");
        if (_tag.text().includes("&#130;&#1000;")) {
            txt = txt.replace(/&#130;&#1000;/gi, "");
        }
        _tag.text( txt );
    }
}

function convertXmlIcon(_tag) {
    //id
    let iconId = _tag.text().match(/[0-9]{5}/g) || "";
    if ( iconId === "" ) return;

    const _divIcon = $("div[role='icon']");
    if ( _divIcon.length == 0 ) return;

    if ( !Array.isArray(iconId) ) {
        iconId = [iconId];
    }

    $.each(iconId, function(idx, value){
        let _icon = $("icon[id='" +value+ "']", _divIcon);
        if ( _icon.length == 0 ) return;

        let fileName = _icon.attr("filename").replace(/\\/gi, "/").replace('Icon', 'icon');
        let imgTag = "<img id='" +iconId+ "' src='/EXPIS/" + bizCode + "/ietmdata/" + fileName + "' width='auto;' height='18px;' class='icon' />";
        let text = _tag.text().replace( "&#"+value+";" , imgTag);
        _tag.text(text);
    });

}

function removeXmlSpecialChars(text, tagCode) {
    let rtnText = text;
    if ( !!tagCode ) {
        rtnText = rtnText.replace(/&#254;/gi, "");      //Tag Start
        rtnText = rtnText.replace(/&#255;/gi, "");      //Tag End
        return rtnText;
    }

    rtnText = rtnText.replace(/&#136;/gi, "");      //Sub/Sup
    rtnText = rtnText.replace(/&#130;/gi, "");      //Sub/Sup
    rtnText = rtnText.replace(/&#700;/gi, "");      //Sub/Sup - start
    rtnText = rtnText.replace(/&#1000;/gi, "");     //Sub/Sup - end
    rtnText = rtnText.replace(/&#138;/gi, "");      //Align
    rtnText = rtnText.replace(/&#169;/gi, "");      //Font - bold
    rtnText = rtnText.replace(/&#188;/gi, "");      //Font - italic
    rtnText = rtnText.replace(/&#199;/gi, "");      //Font - underline
    rtnText = rtnText.replace(/&#252;/gi, "");      //Type
    rtnText = rtnText.replace(/&#0;/gi, "");        //Align - left
    rtnText = rtnText.replace(/&#1;/gi, "");        //Align - left
    rtnText = rtnText.replace(/&#2;/gi, "");        //Align - center
    rtnText = rtnText.replace(/&#3;/gi, "");        //Type - ?
    rtnText = rtnText.replace(/&#4;/gi, "");        //Align - right
	rtnText = rtnText.replace(/&#200;/gi, "");      //kt1 WC에서 텍스트 깨져서 추가 - jsh - 25-06-12
	rtnText = rtnText.replace(/&#-256;/gi, "");     //t50 WC에서 텍스트 깨져서 추가 - jsh - 25-06-16

    rtnText = rtnText.replace(/&#13;/gi, "<br/>");  //new line
    rtnText = rtnText.replace(/&#32;/gi, " ");      //space
    rtnText = rtnText.replace(/&#14;/gi, "&nbsp;&nbsp;&nbsp;");  //전각문자

    rtnText = rtnText.replace(/&#5000;/gi, "<font style='font-weight:bold; color:#F00'>");  //Font Start == 검색결과
    rtnText = rtnText.replace(/&#6000;/gi, "</font>");  //Font End

    rtnText = rtnText.replace(/&#24;/gi, "");      //icon
    rtnText = rtnText.replace(/\&\#[0-9]{5}\;/gi, "");      //icon id

    return rtnText;
}

function adjustTableWidth(divArea) {
    let _tableList = $("table", ".main-content", divArea);
    if (_tableList.length == 0) return;
    _tableList.each(function(){
        let _th = $("thead tr:first", $(this));
        if (_th.length == 0) {
            _th = $("tbody tr:first", $(this));
        }
        if (_th.length ==0) return;

        let _cols = _th.children();
        let maxWidth = 0;
        _cols.each(function(){
            let dWidth = $(this).attr("dwidth") || "0";
            maxWidth += parseInt(dWidth);
        });
        if (maxWidth === 0) return;

        _cols.each(function(idx){
            if (idx === 0) {
                $(this).css("width", "auto");
                return;
            }
            let dWidth = $(this).attr("dwidth") || "0";
            let cnvWidth = parseInt(dWidth) / maxWidth * 100;
            $(this).css("width", cnvWidth+"%");
        });
    });
}

function appendVersionBar(data) {
	if (!data.versionData) return;

	let verData = JSON.parse(data.versionData) || "";
	if (verData === "") return;

	let keys = Object.keys(verData);
	$.each(keys, function (idx) {
		let info = verData[this];
		let id = verData[this].contId || "";
		if (id === "") return;

		let _xid = $("[xid='" + id + "']", ".main-content");
		if (_xid.length === 0) return;

		_xid.addClass("version_bar " + (info.style || "").toLowerCase());
		if (_xid.find(".bar").length === 0) {
			let _bar = $("<span>", {
				class: "bar",
				"data-to-key": info.toKey,
				"data-cont-id": info.contId,
				"data-version-id": info.versionId,
				"data-status": info.status,
				"data-change-basis": info.changeBasis
			});

			_bar.on("click", function () {
				const verInfo = {
					toKey: $(this).data("toKey"),
					contId: $(this).data("contId"),
					verId: $(this).data("versionId"),
					verStatus: $(this).data("status"),
					changebasis: $(this).data("changebasis")
				};
				openPopup(verInfo);
			});

			_xid.prepend(_bar);
		}
	});
}

/**
 * 2024.11.05. - WORKCARD내에 경고창에 대한 변경바의 높이를 경고창의 사이즈와 같도록 조정 - jingi.kim
 */
function workCardStyleHeight() {
	//WORKCARD 에서만 동작하도록
	var _inTableWc = $("table[class^='in_table_wc']", "#main_contents");
	if ( _inTableWc.length == 0 ) return false;
	
	//알림 (경고, 주의, 주기) 가 포함된 경우 동작
	var _alertClass = $("div.alert_class", "#main_contents");
	if ( _alertClass.length == 0 ) return false;
	
	//알림이 포함된 table 높이
	var _alertTable = _alertClass.parents("table.in_table_wc_dep2");
	if ( _alertTable.length == 0 ) return false;
	var alertHeight = _alertTable.height();
	
	//변경바에 높이 적용
	var _versionBar = _alertTable.parents("tr").find("span[class^='version_bar']");
	if ( _versionBar.length == 0 ) return false;
	
	_versionBar.height(alertHeight);
	
}


/**
 * 2025.06.04. - KTA 초기상태 - 적용 범위, 기타요건 - hide 
   / 소요 인원 => 수행 인원 / 요구 조건  => 항공기 상태 
   / bizCode => 현재는 KTA,KT1 체크로 지정. 
                 나중에 추가 확인 필요
   - minhee.yun
 */
function jgKtaCheck() {
    var message = '';

     if ('KTA' == bizCode || 'KT1' == bizCode)  {
      //if ('Block2' !== bizCode)  {
        //적용 범위, 기타요건 - hide 
        $("#ktaChk").hide();
        $("#etcKtaChk").hide();
        //항공기 상태 
        $("#nKtaReq").hide();
        $("#ktaAirStatus").show();
        //수행 인원
        $("#takeNop").hide();
        $("#carryNop").show();
        //물자
        $("#ktaMatl").show();
        $("#nKtaMatl").hide();
        
    } else {
        //적용 범위, 기타요건 
        $("#ktaChk").show();
        $("#etcKtaChk").show();
        //요구 조건  
        $("#nKtaReq").show();
        $("#ktaAirStatus").hide();
        //소요 인원 
        $("#takeNop").show();
        $("#carryNop").hide();
        //필수교환 품목 소모성 물자
        $("#nKtaMatl").show();
        $("#ktaMatl").hide();
        
    }
}
/**
 * 2025.04.04. - JG 준비시힝 - 필수교환 품목 소모성 물자 - minhee.yun
 */
function jgConsumCheck() {
    var message = '';

     if ('KTA' == bizCode || 'KT1' == bizCode)  {
      //if ('Block2' !== bizCode)  {
        message = '물자 : ';
     } else {
        message = '필수교환 품목 소모성 물자 : ';
     }
      document.getElementById('consumCheck').innerHTML = message;
}


/**
 * 2025.04.04. - JG 준비시힝 - 안전 요건 - minhee.yun
 */
function jgAlertCheck() {
    var message = '';

       if ('KTA' == bizCode || 'KT1' == bizCode)  {
      // if ('Block2' !== bizCode)  {
            message = '안전 규정 : ';
      } else {
          message = '안전 요건 : ';
      }
      document.getElementById('alertCheck').innerHTML = message;

}

/**
 * 2025.04.08. - 목차이동 링크 선택 시 active 효과 - osm
 */
function activateTocLink(tocoId) {
	const allAnchors = document.querySelectorAll("a");
	const targetElement = document.getElementById(tocoId);
	const targetAnchor = document.getElementById(tocoId)?.querySelector("a");

	if (!allAnchors || !targetElement || !targetAnchor) return;

	// active 클래스 제거 및 추가
	[...allAnchors].forEach(a => a.classList.remove("active"));
	targetAnchor.classList.add("active");

	// 폴더 모두 펼치기
	let parent = targetElement?.parentElement;
	while (parent) {
		if (parent.classList.contains('collapse')) {
			parent.classList.remove('collapse');
			parent.classList.add('show');
		}
		parent = parent.parentElement;
	}

	// 스크롤 이동
	targetAnchor.scrollIntoView({
		behavior: 'instant',
		block: 'center'
	});
}

/**
 * 호출 함수
 * 2025.04.11 - rdn 하이라이트 효과 기능 분리
 */
function highlightRdn(rdnVal) {
	if (!rdnVal) return;

	const rdnMatchedTds = $("td:contains('" + rdnVal + "')");

	if (rdnMatchedTds.parent().children('td:eq(1)').children('a:eq(0)')) {
		$("div[name=pngObjArea]").each(function (index, item) {
			if (
				$(item).attr("instancename") &&
				"part_" + $(item).attr("instancename") === rdnMatchedTds.parent().attr("name")
			) {
				item.scrollIntoView(true);
			}
		});

		rdnMatchedTds.parent().children('td:eq(2)').children('a:eq(0)').trigger("click");
	}

	$("td").filter(function () {
		if ($(this).text() === rdnVal && rdnVal !== "") {
			$(this).parent().css("background-color", "yellow");
			$(this).css("color", "red");
			$(this).css("font-weight", "bold");
			$(this)[0].scrollIntoView(false);
		}
	});

}

/* 토글 버튼(아이콘 스위치) */
function sectionOpen(icon) {
    const h3 = icon.closest('h3');
    if (!h3) return;

    const parent = h3.parentElement;
    const stepDivs = parent.querySelectorAll('div.step'); // 모든 .step div 찾기

    const isUp = icon.classList.contains('fa-arrow-up');

    stepDivs.forEach(stepDiv => {
        stepDiv.style.display = isUp ? 'none' : 'block';
    });

    // 아이콘 상태 전환
    if (isUp) {
        icon.classList.remove('fa-arrow-up');
        icon.classList.add('fa-arrow-down');
    } else {
        icon.classList.remove('fa-arrow-down');
        icon.classList.add('fa-arrow-up');
    }
}
//function sectionOpen(icon) {
//    const h3 = icon.closest('h3');
//    if (!h3) return;
//
//    // h3의 형제 요소 중 .step 클래스를 가진 div 찾기
//    const stepDiv = h3.parentElement.querySelector('div.step');
//    if (!stepDiv) return;
//
//    const isUp = icon.classList.contains('fa-arrow-up');
//
//    if (isUp) {
//        stepDiv.style.display = 'none';
//        icon.classList.remove('fa-arrow-up');
//        icon.classList.add('fa-arrow-down');
//    } else {
//        stepDiv.style.display = 'block';
//        icon.classList.remove('fa-arrow-down');
//        icon.classList.add('fa-arrow-up');
//    }
//}
