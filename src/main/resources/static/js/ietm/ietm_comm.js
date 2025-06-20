var eventHistory = new Array();
var toKeyHistory = new Array();
var tocoIdHistory = new Array();
var searchWordHistory = new Array();
var vcKindHistory = new Array();
var contIdHistory = new Array();
var historyIndex = 0;
var historyTotalIndex = 0;
var timeFlag = false;
var cousorFlg = false;
var contId = "";
var staticScroll = new Array();
var staticType = new Array();
var staticContent = new Array();
/**
 * 내용 시현시 목차 및 컨텐츠 종류 분류(view contents kind) 01 : 내용 - 일반목차 02 : 경고 - 프레임보기에서
 * 경고창 03 : 그래픽 - 그림목차, 그림팝업 04 : 표(테이블) - 표목차, 표팝업 05 : IPB(IPB목차)
 */
// var constVcontKind = new Array("01", "02", "03", "04", "05");
function maxlengthChk(chkId, count) {
	var areaId = $("#" + chkId + "_area");
	var wordCount = areaId.val().length;
	var subStringCont = "";
	var ietmNumberLimitOne = $("#ietm_number_limit_one").val();
	var ietmNumberLimitTwo = $("#ietm_number_limit_two").val();
	if (wordCount >= count) {
		alert(ietmNumberLimitOne + count + ietmNumberLimitTwo);
		subStringCont = areaId.val().substring(0, count);
		areaId.val(subStringCont);
	}
//	event.cancelBubble = "true";
}

function toInfoEvent() {
	console.log("Display : "+$(".manual_info_form").css("display"));
	if ($(".manual_info_form").css("display") == "none") {
		$(".manual_info_form").slideDown(300);
	} else {
		$(".manual_info_form").slideUp(300);
	}
}

function closeToInfo() {
	$(".manual_info_form").slideUp(300);
}

// // menu layer btnEvent
// function menuBtnEvent(menu) {
// 	var toKey = $("#to_key").val();
// 	var mytoKey = $("#myto_key").val();
//
// 	// menu 아이콘 클릭시
// 	if ($("#pup_" + menu).css("display") == "none") {
// 		if (menu == "search") {
// 			if (toKey != "") {
// 				$("#choice_search").hide();
// 			} else {
// 				return;
// 			}
// 		} else if (menu == "version") {
// 			if (toKey != "") {
// 				$(".version_con_list").hide();
// 				$("#avail_version_list").empty();
// 				availOpen();
// 			} else {
// 				return;
// 			}
// 		} else if (menu == "memo") {
// 		} else if (menu == "option") {
// 			$(".basic_opt").hide();
// 		}
// 		$(".pup_form").hide();
// 		activeClassAdd(menu);
// 		$("#pup_" + menu).show();
//
// 	} else {
// 		$("#avail_version_list").empty();
// 		$(".press").removeClass("press");
// 		$("#pup_" + menu).hide();
// 	}
// }

// menu 활성화 효과주기위한 addClass
function activeClassAdd(menu) {
	$(".press").removeClass("press");
	$(".btn_menu_" + menu).addClass("press");
}

// menu to btnEvent
function leftListEvent(menu) {
	$(".to_press").removeClass("to_press");
	if (menu == $("#open_list_type").val()) {
		closeLeftList();
		return;
	}
	$(".list_search").hide();
	$(".list_" + menu).show();
	openLeftList(menu);
	$("#last_open_list").val(menu);

}

function isOpenList() {
	$(".to_press").removeClass("to_press");
	if ($("#open_list_type").val() == "") {
		openLeftList($("#last_open_list").val());
	} else {
		closeLeftList();
	}
}

function openLeftList(menu) {
	$(".left_list").animate({
		left : 270
	}, 300);
	$(".content").animate({
		left : 270
	}, 300);
	$(".btn_menu_" + menu).addClass("to_press");
	$("#open_list_type").val(menu);
}

function closeLeftList() {
	$(".to_press").removeClass("to_press");
	$(".left_list").animate({
		left : -1
	}, 300);
	$(".content").animate({
		left : -1
	}, 300);
	$("#open_list_type").val("");
}

function selectBoxEvent(param) {

	$('.select_box_form').slideUp("fast");
	if ($("#choice_" + param).css("display") == "none") {
		$("#choice_" + param).show();
	} else {
		$("#choice_" + param).slideUp("fast");
	}
	window.event.cancelBubble=true;
//	reFunction();
}

function reFunction() {
	$(".sel_choice a").attr("onclick","");
}

function popUpEvent(param) {
	if ($("#pup_" + param).css("display") == "none") {
		// 즐겨찾기 열 때 새로 그리기
		if (param == "bookmark") {
			getBookmarkList();
			$("#search_key").val("");
		}
		$("#pup_" + param).show();

	} else {
		$("#pup_" + param).hide();
	}
}

function openHelp(){
	const url = `/EXPIS/${bizCode}/ietm/printMain.do`;
	const options = "width=1000, height=600, scrollbars=yes,resizable=yes";
	const popWin = window.open(url, "modal", options);
}

// 2023.04.25 - 팝업 옵션 변경 - jingi.kim
function openWin(category) {
	var width = 0;
	var height = 0;
	var url = "";
	var param = "";
	var opt = "";
	var toKey = $("#to_key").val();
	var vehicleType = $("#vehicle_type").val();
	var searchWord = "searchWord";
	var ietmSelectManualToc = $("#ietm_select_manual_toc").val();
	//2022 10 19 jysi 언어에 따라 단위변환기창 크기 조절 위해 추가
	var lang = $("#lang").val();

	if (category == "version") {
		if (toKey == "") {
			return false;
		} else {
			url = "versionMain.do";
			param = "?toKey=" + toKey;
			opt = "width=805, height=550, scrollbars=yes";
		}
	} else if (category == "airplane") {
		url = "airplaneMain.do";
		param = "?toKey=" + toKey + "&tocoId=" + $("#toco_id").val();
		
		if($("#shapeType").val() == 'T-50'){
			width = 650;
			height = 182;
		} else {
			width = 500;
			height = 172;
		}
		var screen = window.screen;
		var left = (screen.width - width) / 2;
		if ( !!screen.availLeft ) { left += screen.availLeft; }
		var top = (screen.height - height) / 2;
		opt = "left="+left+", top="+top+", width="+width+", height="+height+", scrollbars=no";
	} else if (category == "unit") {
		url = "unitMain.do";
		opt = "width=790, scrollbars=yes";
		if (lang == "en") { opt += ", height=153"; }
		else { opt += ", height=130"; }
	} else if (category == "print") {
		if (toKey == "") {
			alert(ietmSelectManualToc);
			return;
		} else {
			url = "/printMain.do";
			param = "?vehicleType=" + vehicleType;
			opt = "width=1280, height=812, scrollbars=yes";
		}
	} else if (category == "help") {
		url = "/helpMain.do";
		opt = "width=1000, height=600, scrollbars=yes";
		//Window jsWindow.open(String url, String windowName, String windowFeatures, Boolean optionalArg4)
	} else if (category == "myto") {
		url = "/myToList.do";
		opt = "width=872, height=622, scrollbars=yes";
	} else if (category == "dictionary") {
		var word = "";
		var re = new RegExp(
				"에 $|고 $|과 $|은 $|을 $|를 $|의 $|도 $|으로 $|이 $|된 $|가 $|할 $|만 $|에서 $|되는 $|되며 $|하여 $|하는 $|해서 $|는 $|에는 $|하기 $|에서는 $|되지 $|하지 $");

		url = "/dictionary.do";

		// IE
		// searchWord = document.selection.createRange().text;
		// chrome
		searchWord = document.getSelection().getRangeAt(0) + "";

		word = searchWord.replace(re, "");

		param = "?searchWord=" + encodeURI(encodeURIComponent(word));
		opt = "width=500, height=310, scrollbars=yes";
		if (searchWord == "") {
			return;
		}
	} else if(category == "versionDetail") {
		url = "/versionOpenWin.do";
		opt = "width=550, height=550, scrollbars=yes";
	}

	var popWin = window.open($("#proectName").val()+"ietm/" + url + param, category, opt);
	if ( !popWin ) { popWin.focus(); }
}

function closePopUp() {
	
}

// 2023.04.25 - TA-50 BLOCK2 일 경우, 항공기 형상 선택시 항공기 명칭 변경 함수 추가 - jingi.kim
function uptVehicleTitle() {
	if ( $("#bizCode").length == 0 || $("#bizCode").val() != "BLOCK2" ) { return; }
	if ( $("#vehicle_type").length == 0 ) { return; }
	if ( $("#shapeType").length == 0 ) { return; }
	
	var vcStr = "FA-50";
	var vehType = $("#vehicle_type").val();
	var shpType = $("#shapeType").val();
	
	if ( vehType == "A" ) 	{	vcStr = "FA-50 ";		}
	else 					{	vcStr = "TA-50 2차 ";	}
	
	if ( shpType == "T-50" ) {
		if ( vehType == "A" ) 		{	vcStr = "T-50";		}
		else if ( vehType == "B" ) 	{	vcStr = "T-50B 1차";	}
		else if ( vehType == "C" ) 	{	vcStr = "TA-50";	}
		else 						{	vcStr = "T-50B 2차";	}
	}
	
	$("#vehicleTypeStr").html( vcStr );
}

function vehicleTypeChange() {
	console.log("Call vehicleTypeChange");
	var toKey = $("#to_key").val();
	//2022 09 21 Park.J.S. ADD
	/*if($("#bizCode") && $("#bizCode").val() == "BLOCK2") {
		if($("#vehicle_type").val() == "A") {
			$("#vehicleTypeStr").html("FA-50");
		}else{
			$("#vehicleTypeStr").html("TA-50 2차 ");
		}
	}*/
	// 2023.04.25 - 항공기 형상 명칭 업데이트 - jingi.kim
	uptVehicleTitle();
	
	//return;
	//console.log($("li a"));
	var tempCheckFlag = false;
	$("li a").each(function() {
		var thisName = $(this).attr("name")+"";
		var thisStyle = $(this).attr("style")+"";
		if(thisStyle.indexOf("gold") > 0 && thisName != toKey){
			console.log("This is Selected Node "+thisName+", "+thisStyle+", "+$(this).attr("id"));
			var regExp = /[b|c|d]/gi;
			var regVeh = /[a]/gi;
			var vehicleType = $(this).attr("vehicle")+"";
			console.log("vehicleType : "+vehicleType+" => "+$("#bizCode"));
			if($("#vehicle_type").val() == "A") {
				regExp = /[b|c|d]/gi;
				regVeh = /[a]/gi;
			} else if($("#vehicle_type").val() == "B") {
				regExp = /a|c|d/gi;
				regVeh = /[b]/gi;
			} else if($("#vehicle_type").val() == "C") {
				regExp = /a|b|d/gi;
				regVeh = /[c]/gi;
			} else if($("#vehicle_type").val() == "D") {
				regExp = /a|b|c/gi;
				regVeh = /[d]/gi;
			}
			if(vehicleType != null && vehicleType != "undefined" && vehicleType != "") {
				console.log("vehicleType : "+vehicleType+", regVeh : "+regVeh);
				var thisId = $(this).attr("id")+"";
				console.log("vehicleType : "+thisId);
				if(vehicleType.match(regVeh) != null) {
					if(vehicle_type){
						console.log("Reflash Load A : "+$("#to_key").val()+", "+thisId);
						viewExContents($("#to_key").val()+"", thisId, '', '01', '');
						tempCheckFlag = true;
						return;
					}else{
						console.log("A else Call");
					}
				}else{
					if(vehicleType != null && vehicleType == "None" && vehicleType != "none") {
						console.log("Reflash Load B : "+$("#to_key").val()+", "+thisId);
						viewExContents($("#to_key").val()+"", thisId, '', '01', '');
						tempCheckFlag = true;
						return;
					}else{
						console.log("B else Call");
					}
				}
			} else {
				console.log("Reflash Load");
				$(this).click();
				return;
			}
		}else{
			console.log("Not Selected Node");
		}
	});
	console.log("Fin vehicleTypeChange : "+toKey+", "+tempCheckFlag);
	if(!tempCheckFlag && toKey != ''){
		callTocoList(toKey, "open", "to");
	}
}

//형광펜 지우기 효과
function eventOut() {
	if($('#main_contents #co_context_lay').css('display') == 'block') {
		$('#main_contents #co_context_lay').hide();
	}
	//2021 06 확대 축소 추가
	if($('#main_contents #co_png_context_lay').css('display') == 'block') {
		if($('#main_contents #co_png_context_lay').html() != null) {
			$('#main_contents #co_png_context_lay').hide();
		}
	}
	if($('#main_contents #image_contextLay').css('display') == 'block') {
		$('#main_contents #imageContextLay').hide();
	}
	
	//$(".link-list").hide(); //멀티링크 기능에 영향을 줘서 기능 삭제
	$('.select_box_form').hide();
	
	// 2023.05.19 - 강제 click 이벤트 발생시 오류 보완 - jingi.kim
	if ( !!window.event ) {
		window.event.cancelBubble=true;
	}
	
	//$(".link-list").hide(); //멀티링크 기능에 영향을 줘서 기능 삭제
	$('.select_box_form').slideUp("fast");
}

function remoTocoEvent(id, focusId) {
	var tocoChk = false;
	var toHeight = 0;
	
	if((id.indexOf("remoNo") > 0 && $("#" + id).length > 0) || id.indexOf("cont_") != -1) {
		tocoChk = true;
	}
	//console.log("remoTocoEvent tocoChk : "+tocoChk);
	
	
	if(tocoChk == true) {
		$("#" + id).children().click();
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//2021 10 06 park js
		//왼쪽 네비 이동으로 수정
		/*
		if(id.indexOf("cont_") != -1){
			$("#" + id.slice(id.indexOf("_")+1,id.length)).click();
		}else{
			$("#" + id).children().click();
		}
		*/
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	} else {
		
		var sHeight = $("#" + focusId).position().top;
		var cursorP = $("#" + focusId).parent();
		
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
		
		var staticSeie = staticScroll.length;
		
		for (var int = 0; int < staticSeie; int++) {
			toHeight = staticScroll.pop();
			if(toHeight < sHeight) {
				staticType.pop();
				staticContent.pop();
			} else {
				staticScroll.push(toHeight);
				break;
			}
		}
		
		
		
		$("#main_contents").scrollTop($("#main_contents")[0].scrollHeight);
		$("#" + focusId).children().focus();
	}
}

function remoMakeEvent() {
	var remoNo = 0;
	if ($('#to_key').val() == "") {
		return;
	}
	var tempTr = "";
	var thisClass = "";
	var clickEvent = "";
	var nId = "";
	
	$("#main_contents *").each(function() {
		thisClass = $(this).attr("class");
		nId = $(this).attr("id");
		if(nId == undefined) {
			nId = "opened";
		}
		clickEvent = "onclick='remoTocoEvent(\"" + nId + "\", \"" + "remoNo_" + remoNo + "\")'";
		
		if($(this).hasClass("cont_link1")) {
			if(remoNo == 0) {
				$("#remo_title").text($(this).text());
				$("#remo_title").attr("title", $(this).text());
			} else {
				tempTr += "<tr class=''>";
				tempTr += "	<td>";
				tempTr += "	<a href='javascript:;' " + clickEvent + ">";
				tempTr += $(this).text();
				tempTr += "	</a>";
				tempTr += "	</td>";
				tempTr += "</tr>";
				$(this).find("p").addClass("remoNo_" + remoNo);
				
			}
			
			remoNo++;
		} else if(thisClass == "introduction") {
			if(remoNo == 0) {
				
				if($(this).find(".ico-memo-lock").length != 0) {
					$("#remo_title").text($(this).find(".ico-memo-lock").prev().text());
					$("#remo_title").attr("title", $(this).find(".ico-memo-lock").prev().text());
					tempTr += $(this).find(".ico-memo-lock").prev().text();
				} else {
					$("#remo_title").text($(this).text());
					$("#remo_title").attr("title", $(this).text());
				}
				
			} else {
				tempTr += "<tr class=''>";
				$(this).attr("id", "remoNo_" + remoNo);
				$(this).addClass("remoNo_" + remoNo);
				tempTr += "</tr>";
				$(this).attr("id", "remoNo_" + remoNo);
				$(this).find("p").addClass("remoNo_" + remoNo);
			}
			
			remoNo++;
		} else if(thisClass == "ac-chapter") {
			if(remoNo == 0) {
				if($(this).find(".ico-memo-lock").length != 0) {
					$("#remo_title").text($(this).find(".ico-memo-lock").prev().text());
					$("#remo_title").attr("title", $(this).find(".ico-memo-lock").prev().text());
				} else {
					$("#remo_title").text($(this).text());
					$("#remo_title").attr("title", $(this).text());
				}
			} else {
				tempTr += "<tr class=''>";
				tempTr += "	<td>";
				tempTr += "	<a class='open' href='javascript:;' " + clickEvent + ">";
					if($(this).find(".ico-memo-lock").length != 0) {
						tempTr += $(this).find(".ico-memo-lock").prev().text();
					} else {
						tempTr += $(this).text();
					}
				tempTr += "	</a>";
				tempTr += "	</td>";
				tempTr += "</tr>";
				$(this).attr("id", "remoNo_" + remoNo);
				$(this).find("p").addClass("remoNo_" + remoNo);
			}
			remoNo++;
		} else if(thisClass == "ac-section") {
			if(remoNo == 0) {
				if($(this).find(".ico-memo-lock").length != 0) {
					$("#remo_title").text($(this).find(".ico-memo-lock").prev().text());
					$("#remo_title").attr("title", $(this).find(".ico-memo-lock").prev().text());
				} else {
					$("#remo_title").text($(this).text());
					$("#remo_title").attr("title", $(this).text());
				}
			} else {
				tempTr += "<tr class=''>";
				tempTr += "	<td>";
				tempTr += "	<a class='open' href='javascript:;' " + clickEvent + ">";
				if($(this).find(".ico-memo-lock").length != 0) {
					tempTr += $(this).find(".ico-memo-lock").prev().text();
				} else {
					tempTr += $(this).text();
				}
				tempTr += "	</a>";
				tempTr += "	</td>";
				tempTr += "</tr>";
				$(this).attr("id", "remoNo_" + remoNo);
				$(this).find("p").addClass("remoNo_" + remoNo);
			}
			remoNo++;
		} else if(thisClass == "ac-topic") {
			if(remoNo == 0) {
				if($(this).find(".ico-memo-lock").length != 0) {
					$("#remo_title").text($(this).find(".memo_form").prev().attr("class"));
					$("#remo_title").attr("title", $(this).find(".ico-memo-lock").prev().text());
				} else {
					$("#remo_title").text($(this).text());
					$("#remo_title").attr("title", $(this).text());
				}
			} else {
				$(this).attr("id", "remoNo_" + remoNo);
				$(this).find("p").addClass("remoNo_" + remoNo);
				tempTr += "<tr class=''>";
				tempTr += "	<td>";
				tempTr += "	<a class='open' href='javascript:;' " + clickEvent + ">";
				if($(this).find(".ico-memo-lock").length != 0) {
					tempTr += $(this).find(".ico-memo-lock").prev().text();
				} else {
					tempTr += $(this).text();
				}
				tempTr += "	</a>";
				tempTr += "	</td>";
				tempTr += "</tr>";
			}
			remoNo++;
		} else if(thisClass == "ac-sub-topic") {
			if(remoNo == 0) {
				if($(this).find(".ico-memo-lock").length != 0) {
					$("#remo_title").text($(this).find(".ico-memo-lock").prev().text());
					$("#remo_title").attr("title", $(this).find(".ico-memo-lock").prev().text());
				} else {
					$("#remo_title").text($(this).text());
					$("#remo_title").attr("title", $(this).text());
				}
			} else {
				$(this).attr("id", "remoNo_" + remoNo);
				$(this).find("p").addClass("remoNo_" + remoNo);
				tempTr += "<tr class=''>";
				tempTr += "	<td>";
				tempTr += "	<a class='open' href='javascript:;' " + clickEvent + ">";
				if($(this).find(".ico-memo-lock").length != 0) {
					tempTr += $(this).find(".ico-memo-lock").prev().text();
				} else {
					tempTr += $(this).text();
				}
				tempTr += "	</a>";
				tempTr += "	</td>";
				tempTr += "</tr>";
			}
			remoNo++;
		}/* else if($(this).attr("class") == "ac-object") {
			if(remoNo == 0) {
				$("#remo_title").text($(this).text());
				$("#remo_title").attr("title", $(this).text());
			} else {
				if($(this).find("p").text() != "") {
					tempTr += "<tr class='remote_ac-object'>";
					tempTr += "	<td>";
					tempTr += "&nbsp;&nbsp;&nbsp;&nbsp;" + $(this).find("p").text();
					tempTr += "</a>";
					tempTr += "	</td>";
					tempTr += "</tr>";
					$(this).attr("id", "remoNo_" + remoNo);
					$(this).find("p").addClass("remoNo_" + remoNo);
					remoNo++;
				}
			}
		}*/
		
		
	});
	$("#remo_table").html(tempTr);
	
}

function linkToSWF(strTo,strId)
{
	$.ajax({
		type : "POST",
		url : $("#proectName").val()+"ietm/linkWDToWD.do",
		data : {
			toKey : $("#to_key").val(),
			flick : strTo,
		},
		dataType : "json",
		success : function(data) {
			var alertMsg = $("#ietm_manual_not_exist").val();
			var contWD = data.contWD;
			var grphName = new Array();
			
			if(contWD.indexOf(strTo) != -1) {
				window.open($("#proectName").val()+"ietm/linkToSWF.do?grphName=" + strTo + "&toKey="+$("#to_key").val()+"&gHeight=675&gWidth=990", ""+grphName+"", "width=1050px,height=875px,scrollbars=yes").focus();
			} else {
				alert(alertMsg);
			}
		}
	});
}

function imgLoadErr() {
	alert("이미지가 없습니다.");
}

/**
 * 2022 10 13 Park.J.S. ADD : BLOCL2 WDimgIndex 기능 구현
 * @param str    : 현재 교범 혹은 WD 교범 명 ==> 사용하기 어렵다고 판단
 * @param flick  : 찾아야하는 이미지명
 * @returns
 */
function linkWDToWDIndex(str, flick){
	//Step1. 넘어온 이름이 포함된 이미지 리스트 요청
	var winUrl = $("#proectName").val()+"ietm/getlinkWDImgIndexInfo.do?imageName=" + encodeURIComponent(flick)+"&gHeight=875&gWidth=1050";
	var winStatus = "width=1280px,height=800px,scrollbars=yes";
	window.open(winUrl,"" , winStatus).focus();
	/*
	$.ajax({
		type : "POST",
		url : $("#proectName").val()+"ietm/getlinkWDImgIndexInfo.do",
		data : {
			imageName : flick
		},
		dataType : "json",
		success : function(data) {
			var alertMsg	= $("#ietm_manual_not_exist").val();
			var toKey		= new Array();//교범명 
			var imgID		= new Array();//이미지 ID 
			var cnt 		= data.imggeCount;//이미지 숫자
			
			if(cnt && cnt > 0) {
				var xContList = data.xContList;
				for(var i=0;i<xContList.length;i++){
					toKey[i] = xContList[i].toKey;
					imgID[i] = xContList[i].tocoId;
				}
				console.log("toKey : "+toKey);
				console.log("imgID : "+imgID);
			} else {
				alert(alertMsg);
			}
		}
	});
	*/
}

//2022 10 13 Park.J.S. UPDATE : BLOCL2와 처리 로직이 달라져서 수정 처리
function linkWDToWD(str, flick) {
    // 2023.03.17 - 동일하게 동작하도록 변경 - jingi.kim
	// 2023.03.20 - BLOCK2의 다른 부분에서 오류로 원복 - jingi.kim
	if($("#bizCode") && $("#bizCode").val() == "BLOCK2") {
		linkWDToWDIndex(str, flick);
	}else{
		var linktitle = "";
		console.log("VAL : "+$("#to_key").val());
		console.log("flick : "+flick);
		//2022 06 24 Park.J.S. ADD : flick check add
		if(!flick){
			flick = str;
		}
		$.ajax({
			type : "POST",
			url : $("#proectName").val()+"ietm/linkWDToWD.do",
			data : {
				toKey : $("#to_key").val(),
				flick : flick,
			},
			dataType : "json",
			success : function(data) {
				var alertMsg = $("#ietm_manual_not_exist").val();
				var contWD = data.contWD;
				var grphName = new Array();
				var idName = new Array();
				var cnt = 0;
				
				if(contWD.substring(0, 11).indexOf("eXPIS")) {
					contWD = "<eXPISInfo><system><descinfo><para-seq><para><para-seq><para><" + contWD;
				}
				console.log("$(contWD) : "+$(contWD));
				if(contWD != "") {
					/////////////////////////////////////////////////////////////
					//2021 09 03 png 처리위한 로직 추가
					var pngFlag = false;
					$(contWD).find("grphprim").each(function() {
						//2023 02 21 jysi EDIT : png확장자 대소문자 구분없이 비교하도록 수정
						//2023 03 09 jysi EDIT : 이미지 명에 띄어쓰기 있을 시 extName(확장자) 틀려서 정규식패턴 수정
						console.log("external-ptr : "+$(this).attr("external-ptr"));
						grphName[cnt] = $(this).attr("external-ptr").replace("Image\\", "");
						var pattern = /.+\./;
						var extName = grphName[cnt].replace(pattern, "");
						if(extName.toUpperCase() == "PNG"){
							pngFlag = true;
						}
						console.log("extName : "+extName+", pngFlag : "+pngFlag);
						cnt++;
					});
					console.log("cnt : "+cnt);
					cnt = 0;
					$(contWD).find("system").each(function() {
						if($(this).attr("id") && $(this).attr("id") != "" && $(this).attr("id") != " "){
							console.log("idName["+cnt+"]{"+$(this).attr("id")+"}");
							idName[cnt] = $(this).attr("id");
							cnt++;
						}
						console.log("idName["+cnt+"] : "+idName);
					});
					
					if(grphName == "" || grphName == null) {
						alert("배선도가 없습니다.");
					} else {
                        //2023.03.17 - BLOCK2일 경우 팝업창 사이즈 변경 - jingi.kim
                        var popStatus = "width=1050px,height=875px,scrollbars=yes";
                        if($("#bizCode") && $("#bizCode").val() == "BLOCK2") {
                            popStatus = "width=1550px,height=875px,scrollbars=yes";
                        }
						if(pngFlag){
							window.open($("#proectName").val()+"ietm/linkWDToWDPup.do?grphName=" + encodeURIComponent(grphName) +"&idName="+idName+ "&toKey="+encodeURIComponent($("#to_key").val())+"&gHeight=675&gWidth=990", ""+linktitle+"", popStatus).focus();
						}else{
							linktitle = grphName[0].replace(/-/gi,"");
							linktitle = linktitle.replace(".swf","");
							//alert(linktitle);
							window.open($("#proectName").val()+"ietm/linkWDToWDPup.do?grphName=" + grphName + "&toKey="+$("#to_key").val()+"&gHeight=675&gWidth=990", ""+linktitle+"", popStatus).focus();
						}
					}
				} else {
					alert(alertMsg);
				}
			}
		});
	}
}
/*
 * 2021 10 08 
 * Park js 
 * mp4 사용으로 수정처리함
 */
function videoOpen(grphId, grphName, gWidth ,gHeight) {
//	rtStr = "<EMBED id='" + grphId + "' height='" + gHeight + "' width='" + gWidth + "' src='" + grphName + "' showstatusbar='true' mute='1' autostart='true'>";
	/*
	window.open($("#proectName").val()+"ietm/videoPup.do?grphId=" + grphId + "&grphName=" + grphName + "&gHeight=" + gHeight + "&gWidth=" + gWidth, grphId,
			"width=" + gWidth + ", height=" + gHeight + "").focus();
	 */
	//2023.05.16 jysi EDIT : 팝업창 화면 가운데 출력기능 함수로 만들어 재사용
	//window.open(grphName+"?grphId=" + grphId + "&grphName=" + grphName + "&gHeight=" + gHeight + "&gWidth=" + gWidth, grphId,"width=" + gWidth + ", height=" + gHeight + "").focus();	
	var opt = calPupOptCenter(gWidth, gHeight);
	
	window.open(grphName, grphId, opt).focus();
}

function idScrollEvent(scrollId, id) {
	var offSet = $("#" + id).offset();
//	return;
	$("#" + scrollId).animate({scrollTop : offSet.top}, 400);
}

function transWord(cont, status) {
	if(status == "splChar" && cont != null) {
		if(cont.match('●')) {
			cont = cont.replace(/●/gi, "•");
		}
	}
	
	return cont;
}

function kiosk() {
	location.href($("#proectName").val()+"cmnt/loginOut.do");
	top.window.opener = top;
	top.window.open("", "_parent", "");
	top.window.close();
}

/**
 * 2023.05.16 jysi ADD : 기존에 동영상열기에서 사용하던 팝업창 화면 가운데 출력하는 기능
 * @param pupWidth   : 팝업창 너비
 * @param pupHeight  : 팝업창 높이
 * @returns pupOpt
 */
function calPupOptCenter(pupWidth, pupHeight) {
	//실수값으로 넘어오는 경우 있어 정수로 변경
	pupWidth = parseInt(Number(pupWidth));
	pupHeight = parseInt(Number(pupHeight));
	
	var screen = window.screen;
	var left = (screen.width - pupWidth) / 2;
	if ( !!screen.availLeft ) { left += screen.availLeft; } //듀얼모니터 고려
	var top = (screen.availHeight - pupHeight) / 2; //작업표시줄 고려
	var pupOpt = "left=" + left + ", top=" + top + ", width=" + pupWidth + ", height=" + pupHeight + "";
	
	return pupOpt
}

