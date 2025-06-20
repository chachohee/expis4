/**
 * 01 : 도해
 * 02 : 테이블
 * 03 : 전체보기
 */
var constControlKind	= new Array("01", "02", "03");
var altMessage3	= "";
var altMessage4	= "";
var altMessage5	= "";

function makeIPBViewerHtml(grphName, gWidth, gHeight)
{
	var rtHtml		= "";
//	var gWidth	= "100%";
//	var gHeight	= "100%";

	rtHtml = "<object id='IPBPlayer' name ='IPBPlayer' border='0' width='" + gWidth + "' height='" + gHeight + "' align='center' "
				+ " classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000'  codebase='swflash.cab'  viewastext>\n"
				+ "  <param name='movie' value='" + grphName + "' />\n"
				+ "  <param name='quality' value='high' />\n"
				+ "</object>";

	return rtHtml;
}


/**
 * IPB 내용 보기 옵션 설정
 * 도해도/테이블/전체보기
 * @FunctionName	: resizeIpbControl
 * @AuthorDate		: LIM Y.M. / 2014. 10. 31.
 * @ModificationHistory	:
 * @param controlMode
 */
function resizeIpbControl(controlMode) {
	if (controlMode == constControlKind[0]) {
		$('.ipb_cont .cont_l').show();
		$('.ipb_cont .cont_r').hide();
		$('.ipb_cont .cont_l').css({"width":"100%"});
		$('.ipb_cont .cont_r').css({"width":"0%"});
		$("#ipb_div_h").css("left", "0%");
	} else if (controlMode == constControlKind[1]) {
		$('.ipb_cont .cont_l').hide();
		$('.ipb_cont .cont_r').show();
		$('.ipb_cont .cont_l').css({"width":"0%"});
		$('.ipb_cont .cont_r').css({"width":"100%"});
		$("#ipb_div_h").css("left", "0%");
		$("#main_contents").css("overflow-y","visible");
	} else if (controlMode == constControlKind[2]) {
		$('.ipb_cont .cont_l').show();
		$('.ipb_cont .cont_r').show();
		//2023.05.11 jysi EDIT : DEFAULT버튼 클릭 시 이미지가 테이블영역 침범하는 현상 수정
		//$("#ipb_div_h").css("left", "45%");
		//$('.ipb_cont .cont_l').css("width", "");
		//$('.ipb_cont .cont_r').css({"width":"55%"});
		$("#ipb_div_h").css("left", "50%");
		$('.ipb_cont .cont_l').css("width", "50%");
		$('.ipb_cont .cont_r').css("width", "50%");
		$("#main_contents").css("overflow-x","hidden");
		$("#main_contents").css("overflow-y","visible");
	}
}






/**
 * IPB 도해 이미지 페이징 링크 기능
 * @FunctionName	: changeIPBGrph
 * @AuthorDate		: LIM Y.M. / 2014. 11. 6.
 * @ModificationHistory	: LIM Y.M. / 2017. 1. 14
 * @param grphPath, indexnum(품목번호)
 */
var curGrphPath = "";
var grphNo = "";
function changeIPBGrph(grphPath, indexnum)
{	
	console.log("grphPath : "+grphPath+", indexnum : "+indexnum);
	//2023 02 21 jysi EDIT : png확장자 대소문자 구분없이 비교하도록 수정
	//2023 03 09 jysi EDIT : 이미지 명에 띄어쓰기 있을 시 extName(확장자) 틀려서 정규식패턴 수정
	var fileName = grphPath.substring(grphPath.lastIndexOf("/")+1,grphPath.length);
	var pattern = /.+\./;
	var extName = fileName.replace(pattern, "");
	console.log("extName : "+extName);
	//if(grphPath.indexOf(".png") != -1){
	var isSupportImg = extName.toUpperCase() == "PNG" ? true : false;
	isSupportImg = extName.toUpperCase() == "SVG" ? true : isSupportImg;
	if( isSupportImg ){
		
		imageChange(grphPath, indexnum);
		
		console.log("Pass imageChange");
		$(".ipb_paging").each(function() {
			var grphPath2 = $(this).attr("name");
			console.log("grphPath2 : "+grphPath2);
	//		alert(grphPath2);
			if (grphPath == grphPath2) {
				grphNo = $(this).attr("no");
			}else if (grphPath2.indexOf(grphPath) > 0) {
				grphNo = $(this).attr("no");
			}
			//2024.09.26 - png로 고정된 경우 svg로 변환해서 한번더 확인 - jingi.kim
			if ( grphPath2.replace('.png', '.svg').indexOf(grphPath) > 0 ) {
				grphNo = $(this).attr("no");
			}
		});
		
		//이미지 페이징 표시
		$(".ipb_paging").each(function() {
			var tmp = $(this).html();
			console.log("$(this) : "+$(this));
			tmp = tmp.replace("[", "");
			tmp = tmp.replace("]", "");
			tmp = tmp.replace(" ", "");
			var newtmp = "[" + tmp + "] ";
			
			if (tmp == grphNo) {
				newtmp = tmp + " ";
			}
	
			$(this).html("");
			$(this).append(newtmp);
		});
	}else{
		var ietmMoveSheet = $("#ietm_move_sheet").val();
		//처음 읽을때 첫번째 이미지로 셋팅
		if (curGrphPath == "") {
			curGrphPath = $("#ipb_paging_1").attr("name");
		}
		
		if (curGrphPath != grphPath && grphPath != "") {
			if (indexnum != "") {
				alert(ietmMoveSheet);
				//핫스팟 처리
			}
			IPBPlayer.movie = grphPath;
			IPBPlayerEm.src = grphPath; //20200318 add LYM 크롬일 경우 <embed>태그에 넣어줘야함
			curGrphPath = grphPath;
		}
		
		$(".ipb_paging").each(function() {
			var grphPath2 = $(this).attr("name");
	//		alert(grphPath2);
			if (grphPath == grphPath2) {
				grphNo = $(this).attr("no");
			}
		});
	
		//이미지 페이징 표시
		$(".ipb_paging").each(function() {
			var tmp = $(this).html();
			tmp = tmp.replace("[", "");
			tmp = tmp.replace("]", "");
			tmp = tmp.replace(" ", "");
			var newtmp = "[" + tmp + "] ";
			
			if (tmp == grphNo) {
				newtmp = tmp + " ";
			}
	
			$(this).html("");
			$(this).append(newtmp);
		});
		
		//이미지 핫스팟 호출
		if (indexnum != "") {
			setTimeout(
				function() {
					effectHotspot(indexnum);
				}, 100
			);
		}
	}
}


/**
 * IPB 품목번호 클릭시 그래픽 핫스팟 표시 (단일/다중 그래픽)
 * @FunctionName	: clickPartHotspot
 * @AuthorDate		: LIM Y.M. / 2014. 11. 7.
 * @ModificationHistory	: LIM Y.M. / 2017. 1. 15.
 * @param grphPath, indexnum
 */
function clickPartHotspot(grphPath, indexnum) {
	$(".part_tr").css("background-color","")
	//$("#part_"+indexnum).css("background-color","yellow");
	//2024.05.22 - 객체 못 찾는 오류 보정 - jingi.kim
	var trObj = $("[id='part_" + indexnum +"']");
	if ( trObj.length > 0 ) {
		trObj.css("background-color","yellow");
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//2021 08
	//저작에서 이미지명이 안넘어오는 경우가 있어서 해당 부분 우회하기위해 이미지명이 없을경우 eventStr 이 존재하는 이미지를 찾음
	var imageObj = document.getElementsByName("pngImgArea");
	var mutiFlag = false;
	if(grphPath == ''){
		for ( var i = 0; i < imageObj.length; i++) {
			var imageHtml = imageObj[i];
			var divIdStr = (imageHtml.src.slice(0, imageHtml.src.lastIndexOf(".")));
			//2023 02 21 jysi EDIT : png확장자 대소문자 구분없이 비교하도록 수정
			var extName = imageHtml.src.slice(imageHtml.src.lastIndexOf(".")+1, imageHtml.src.length);
			divIdStr = (divIdStr.slice(divIdStr.lastIndexOf("/")+1,divIdStr.length)).replace(/%20/g," ");
			var divImage = document.getElementById(divIdStr);
			//childNodes, firstChild, lastChild로 자식 노드 탐색하기
			for ( var j = 0; j < divImage.childNodes.length; j++) {
				var divObj = divImage.childNodes[j];
				for ( var k = 0; k < divObj.childNodes.length; k++) {
					var iconImageObj = divObj.childNodes[k];
					//console.log("iconImageObj : "+iconImageObj);
					//console.log(divIdStr+"==> iconImageObj : "+JSON.stringify(iconImageObj,null,2));
					try{
						//console.log("iconImageObj.getAttribute('name') : "+iconImageObj.getAttribute("name"));
						if(iconImageObj.getAttribute("name") == 'pngObjArea'){
							if (indexnum == iconImageObj.getAttribute("instancename")) {
								//2022 11 21 Park.J.S. 오타 수정 동일 품목번호 여려개 존재 할경우 관려 수정 
								//if(grphPath.indexOf(divIdStr+".png") < 0){
								if(grphPath.indexOf(divIdStr+"."+extName) < 0){
									if(grphPath == ''){
										//grphPath = divIdStr+".png";
										grphPath = divIdStr+"."+extName;
									}else{
										mutiFlag = true;
										//grphPath = grphPath+"|"+divIdStr+".png";
										grphPath = grphPath+"|"+divIdStr+"."+extName;
									}
								}
							}
						}
					}catch(e){
						console.log(divIdStr+"==> e : "+e.message);
					}
				}
			}
		}
	}
	
	// IPB 교범 링크 팝업의 경우 하이라이트 처리 안되는 오류 수정을 위한 코드 - redmine #631	
	if(window.name == 'IPBviewTo' && grphPath == "") {
		if(divIdStr && extName) grphPath = divIdStr+"."+extName;
	}
	
	console.log("mutiFlag : "+mutiFlag+", grphPath : "+grphPath);
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	var arrGrphpath = grphPath.split("|");
	if (arrGrphpath.length > 1) {
		for (var i=0; i<arrGrphpath.length; i++) {
			if (i == 0) {
				changeIPBGrph(arrGrphpath[i], indexnum);
			} else {
				clickPartHotspotMove(arrGrphpath[i], indexnum,i);
			}
		}
	} else {
		changeIPBGrph(grphPath, indexnum);
	}
}


/**
 * IPB 품목번호 클릭시 그래픽 핫스팟 표시 (이동)
 */
function clickPartHotspotMove(grphPath, indexnum,idx)
{
	var ietmHotspotInfoMove = $("#ietm_hotspot_info_move").val();
	//2022 12 15 Park.J.S. Update : 동시 실행 막기위해 인터벌 사용  문제 발생시에 어레이 이용해서 꺼내는 방식으로 수정 해야함
	if(idx){
		setTimeout(
			function() {
				if (confirm(ietmHotspotInfoMove)) {
					changeIPBGrph(grphPath, indexnum);
				}
			}, 2000+(idx*3000)
		);
	}else{
		setTimeout(
				function() {
					if (confirm(ietmHotspotInfoMove)) {
						changeIPBGrph(grphPath, indexnum);
					}
				}, 2000
			);
	}
}


/**
 * IPB 도해 그래픽 이미지 핫스팟 효과 - 품목번호 클릭시 그래픽 핫스팟 표시
 * @FunctionName	: effectHotspot
 * @AuthorDate		: LIM Y.M. / 2014. 10. 31.
 * @ModificationHistory	: LIM Y.M. / 2020. 03. 18. IE는 <object> Chrome는 <embed>로 구현해야함
 * @param indexnum
 */
function effectHotspot(indexnum)
{
	var arrPlayerParam = new Array(
		"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", 
		"k", "l", "m", "n", "o", "p", "q", "r", "s", "t", 
		"u", "v", "w", "x", "y", "z", 
		"aa", "ab", "ac", "ad", "ae", "af", "ag", "ah", "ai", "aj", 
		"ak", "al", "am", "an", "ao", "ap", "aq", "ar", "as", "at", 
		"au", "av", "aw", "ax", "ay", "az"
	);

	var playerParam = indexnum;

	//20200318 edit LYM
	//IPBPlayer.TPlay("/" + playerParam);

	var agent = navigator.userAgent.toLowerCase();
	if (agent.indexOf("chrome") != -1) {
		IPBPlayerEm.TPlay("/" + playerParam);
	} else {
		IPBPlayer.TPlay("/" + playerParam);
	}
	
	for (var i=0; i < arrPlayerParam.length; i++) {
		playerParam = indexnum + arrPlayerParam[i];
		//IPBPlayer.TPlay("/" + playerParam);
		if (agent.indexOf("chrome") != -1) {
			IPBPlayerEm.TPlay("/" + playerParam);
		} else {
			IPBPlayer.TPlay("/" + playerParam);
		}
	}
}


/**
 * IPB 도해 그래픽 이미지 클릭시 테이블 반전 효과 - 미반영
 * @FunctionName	: effectHightlight
 * @AuthorDate		: LIM Y.M. / 2014. 11. 7.
 * @ModificationHistory	: 
 * @param indexnum
 */
function effectHightlight(indexnum)
{
}


/**
 * IPB 도해 그래픽 이미지 클릭시 해당 부품의 IPB 테이블로 이동
 * Flash Script에서 정의되어 있어서 함수명 및 파라미터 갯수 변경 불가
 * 1안 : T50은 code(ipbcode) 함수 호출시 해당 부품의 하위리스트를 보여주고
 * 2안 : KT1은 code(indexnum) 함수 호출시 해당 부품의 위치에 하이라이트 표시해줌
 * 2021 08 20 parkjs 수정 
 * @FunctionName	: code
 * @AuthorDate		: LIM Y.M. / 2014. 11. 8.
 * @ModificationHistory	: 
 * @param ipbcode
 */
function code(ipbcode) {
	var ipbType = $("#ipb_type").val();
	/*if(ipbType == "01") {
		
		var toKey = $("#top_to_key").html();
		var param = "to_key=" + toKey + "&ipbcode=" + ipbcode;

		$.ajax({
			type: "POST",
			url: $("#proectName").val()+"ietm/tocoIdFromIpbcode.do",
			data: param,
			dataType: "json",
			success: function(data){
				viewExContents(data.toKey, data.tocoId,'', "04");
			}
		});
		
	} else {*/
		$(".part_tr").css("background-color", "#FFF");
		var indexnum = ipbcode;
		//var obj = $("#part_"+indexnum);
		//2024.05.22 - 객체 못 찾는 오류 보정 - jingi.kim
		var obj = $("[id='part_" + indexnum +"']");
		if(!obj){
			alert("TYPE이 일치 하지 않습니다.");
			return;
		}
		/*
		if(obj.length == 0) {
			alert("TYPE이 일치 하지 않습니다.");
			return;
		}
		*/
		obj.css("background-color", "#FFFF00");
		obj.attr("tabindex", 1).focus();
		/**
		 * 2021 08 23 parkjs
		 * 정상 동작 안하는 부분 있어서 순수 스크립트 소스 처리함
		 */
		var tempObj = document.getElementsByName("part_"+ipbcode);
		//2023 02 08 Park.J.S. Update : 스크롤 이동은 최초건만 이동하게 수정
		var tempScrollMoveFlag = true;
		for(var i=0;i<tempObj.length;i++){
			tempObj[i].style.backgroundColor = '#FFFF00';
			//2023 02 08 Park.J.S. Update : 스크롤 이동은 최초건만 이동하게 수정
			if(tempScrollMoveFlag){
				// 2023.04.28 - 테이블 헤더가 있을 경우, 헤더 위치 이후 까지 스크롤 되도록 보완 - jingi.kim
				if ( $(".in_table_ipb thead").length > 0 ) {
					var h = $(".in_table_ipb thead")[0].getBoundingClientRect().height;
					h += 1;
					tempObj[i].style.scrollMarginTop = h + 'px';
				}
				tempObj[i].scrollIntoView(true);
				tempScrollMoveFlag = false;
			}
		}
		/*
		$("#part_"+ipbcode).css("background-color","yellow");
		console.log($("#part_"+ipbcode));
		console.log($("#part_"+ipbcode).attr('class'));
		*/
//	}
}


/**
 * [IPB]항목 선택 - 항목 선택 폼 팝업
 * @FunctionName	: pupIpbCols
 * @AuthorDate		: LIM Y.M. / 2014. 11. 8.
 * @ModificationHistory	: NAM D.S. / 2017. 1. 12
 * @ModificationHistory	: LIM Y.M. / 2020. 3. 9. POST 방식으로 변경(to_key가 한글일 경우 필요)
 */
function pupIpbCols() {
	
	/*
	var toKey = $("#to_key").val();
	var tocoId = $("#toco_id").val();
	var url = $("#proectName").val()+"ietm/ipbColsPup.do?toKey=" + toKey + "&tocoId=" + tocoId;
	var param = "width=400px, height=680px, left=500, top=100, scrollbars=no, status=no, toolbar=no, menubar=no, resizable=no";
	
	window.open(url, "pupIpbCols", param);
	*/

	var ipbform = document.ipb_form;
	var url = $("#proectName").val()+"ietm/ipbColsPup.do";
	var param = "width=400px, height=680px, left=500, top=100, scrollbars=no, status=no, toolbar=no, menubar=no, resizable=no";
	
	window.open("", "pupIpbCols", param);

	ipbform.action = url;
	ipbform.method = "post";
	ipbform.target = "pupIpbCols";
	ipbform.toKey.value = $("#to_key").val();
	ipbform.tocoId.value = $("#toco_id").val();

	ipbform.submit();
}


/**
 * [IPB]항목 선택 - 전체 선택 클릭시 처리
 * @FunctionName	: checkIpbAllCols
 * @AuthorDate		: LIM Y.M. / 2014. 11. 11.
 * @ModificationHistory	: 
 */
function checkAllIpbCols()
{
	var fo = document.frmIpbCols;
	
	var isChecked = fo.chk_ipb_all.checked;
	
	for (var i=0; i<fo.chk_ipb_cols.length; i++) {
		//2023.05.22 jysi EDIT : 보이지 않는 항목은 전체 선택에 영향받지 않도록 함
		if(fo.chk_ipb_cols[i].parentNode.parentNode.style.display=='none'){continue;}
		
		fo.chk_ipb_cols[i].checked = isChecked;
	}
}

/**
 * [IPB]항목 선택 - 팝업에서 항목 선택 설정 완료 후 opener 호출
 * @FunctionName	: submitIpbCols
 * @AuthorDate		: LIM Y.M. / 2014. 11. 10.
 * @ModificationHistory	: 
 */
function submitIpbCols()
{
	var fo = document.frmIpbCols;
	
	window.close();
	
	opener.parent.execIpbCols(fo);
}


/**
 * [IPB]항목 선택 - opener 창에서 항목 선택 설정 완료 한 것 실제 처리
 * @FunctionName	: execIpbCols
 * @AuthorDate		: LIM Y.M. / 2014. 11. 10.
 * @ModificationHistory	: 
 * @param fo
 */
function execIpbCols(fo) {
	var toKey = $("#to_key").val();
	var tocoId = $("#toco_id").val();
	var arrIpbCols = "";
	
	for(var i=0; i<fo.chk_ipb_cols.length; i++) {
		var obj = fo.chk_ipb_cols[i];
		//2023.05.22 jysi EDIT : 모든 항목 제외시 IPB페이지 모든 항목을 출력하는 로직이 있어 숨김항목도 제외하도록 수정
		//                    => 숨김 항목 순서가 선택 항목보다 뒤일때만 작동하므로 추후 숨김 항목이 뒷순서가 아니게 될 경우 수정바람
		if (obj.parentNode.parentNode.style.display=='none' && arrIpbCols == "") {continue;}
		if (obj.checked == true) {
			if (arrIpbCols != "") {
				arrIpbCols += "|";
			}
			arrIpbCols += obj.value; 
		}
	}
	var param = "to_key=" + encodeURIComponent(toKey) + "&toco_id=" + tocoId + "&arr_ipb_cols="+arrIpbCols;
	
	$.ajax({
		type: "POST",
		url: $("#proectName").val()+"ietm/ipbColsExec.do",
		data: param,
		dataType: "json",
		success: function(data){
			toContView(toKey, tocoId, '', false, "", constVcontKind[4], "");
		}
	});
}


/**
 * [IPB]국가재고번호(NSN) 클릭시 대체 품목 팝업 시현
 * 기능 미 구현 (eXPIS2 T50에서 임의로 넣었던 기능임)
 * @FunctionName	: popIpbReplacePart
 * @AuthorDate		: LIM Y.M. / 2014. 11. 11.
 * @ModificationHistory	: 
 */
//function popIpbReplacePart()
//{
//	var url = $("#proectName").val()+"ietm/ipbReplacePartPup.do";
//	var param = "width=400px, height=480px, left=500, top=100, scrollbars=no, status=no, toolbar=no, menubar=no, resizable=no";
//	
//	window.open(url, "popIpbPart", param);
//}

function IpbPopUpChk() {
	var count = 0;
	var size = 0;
	
	$(".chk_class").each(function(index) {
		size = index + 1;
		if($(this).is(":checked") == true) {
			count++;
		}
	});
	
	if(count == 0) {
		$(".chk_sub_class").prop("checked", true);
	} else if(count == size) {
		$("#chk_ipb_all").prop("checked", true);
	}

}

function chkEvent() {
	var count = 0;
	var size = 0;
	$(".chk_class").each(function(index) {
		size = index + 1;
		if($(this).is(":checked") == true) {
			count++;
		}
	});
	if(count  == size) {
		$("#chk_ipb_all").prop("checked", true);
	} else {
		$("#chk_ipb_all").prop("checked", false);
	}
	
}



/**
 * 소스를 받아서 HTML 도큐먼트에 쓰기
 */
function documentWrite(src)
{
	//document.write("아자아자아자");
	//alert("src="+src);
	
	document.write(src);
}
