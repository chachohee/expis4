$(document).ready(function(){
	
	//2024.06.10 - fi_version_table 일때 resize 하도록 추가 - jingi.kim
	if ( location.href.split('versionOpenWin.do').length < 2 ) return true;

	let oFiVersionTable = $(".ver_cont table.fi_version_table");
	if ( oFiVersionTable.length == 0 ) return true;
	
	reStylePopForm();
	setTimeout(function(){
		moveWindowCenter();
	}, 100);
	
});

function reStylePopForm() {
	let oPupForm = $("#ver_link_div");
	if ( oPupForm.length == 0 ) return true;
	
	oPupForm.css("width", "800px");
	oPupForm.css("margin-left", "-400px");
	
	let oFiTbody = $(".ver_cont table.fi_version_table tbody");
	if ( oFiTbody.length == 0 ) return true;
	oFiTbody.css("word-break", "break-word");
	
	window.resizeTo(870, 620);
}

function moveWindowCenter() {
	if ( window.screenTop < 100 ) return true;
	
	let width = $(window).width();
	let height = $(window).height();
	let screen = window.screen;
	
	let left = (screen.width - width) / 2;
	if ( !!screen.availLeft ) { left += screen.availLeft; }
	let top = (screen.height - height) / 2;
	
	window.moveTo(left, top);
}

function availOpen() {
	var toKey = $("#to_key").val();
	var mytoKey = $("#myto_key").val();
	var ietm_no_change_history = $("#ietm_no_change_history").val();
	if(toKey != "" || mytoKey != "") {
		
		if(mytoKey != "") {
			toKey = mytoKey;
		}

		$("#avail_version_list").empty();
		$(".version_select_list").empty();
		
		$.ajax({
			type : "POST",
			url : "versionMain.do",
			data : "toKey=" + encodeURIComponent(toKey),
			dataType : "json",
			success : function(data) {			
				var versionList = data.versionList;
				console.log("CALL availOpen versionList : "+versionList);
				var versionLength = versionList.length;
				var allBook = data.allBook;
				var toKey = data.toKey;
				var orgBook = data.orgBook;
				var contents = "";
				var selectbox_content = "";
				var ietm_view_all = $("#ietm_view_all").val();
				var ietm_change_history = $("#ietm_change_history").val();
				var ietm_count = $("#ietm_count").val();
				var ietm_original = $("#ietm_original").val();
				var ietm_modified_version = $("#ietm_modified_version").val();
				var ietm_basic_version = $("#ietm_basic_version").val();
				if(versionLength == 0) {
					contents += "<li class='no_data' title='"+ietm_no_change_history+"'><div class='in_div'><img src='"+$("#proectName").val()+"web/image/ietm/version/icon_nodata.png'/><br />"+ietm_no_change_history+"</div></li>";
				} else {
					for(var i=0; i<versionLength; i++) {
						var list = versionList[i];
						var splitChgDate = [];
						console.log("list.chgNo : "+list.chgNo);
						if(list.chgNo == "0") {
							contents += "<li>";
							contents += 	"<a href='javascript:void(0);' title='"+ietm_view_all+"' onclick=\"goVersionDetailList('"+ allBook +"','"+ list.verId +"','"+ toKey +"','')\" class='allBook'>"+ietm_view_all+"</a>";
							contents += "</li>";
							contents += "<li>";
							contents += 	"<a href='javascript:void(0);' title='"+ietm_change_history+"' onclick=\"goVersionDetailList('total','all','"+ toKey +"','x')\" class='originalBook'>"+ietm_change_history+"</a>";
							contents += "</li>";
							contents += "<li>";
							//2022 12 13 Park.J.S. Update : 유효페이지목록에서 [changeno="0"]일 경우 클릭 이벤트 제거 (디지인 때문에 <a/> 태그 필요) 
							contents += 	"<a href='javascript:void(0);' title='"+ietm_count+" - "+list.chgDate+"' class='originalBook'>";
							//contents += 	"<a href='javascript:void(0);' title='"+ietm_count+" - "+list.chgDate+"' onclick=\"goVersionDetailList('"+ orgBook +"','"+ list.verId +"','"+ toKey +"','0')\" class='originalBook'>";
							//2022 12 12 Park.J.S. Update : 유효페이지목록에서 [changeno="0"]일 경우 [기본판]으로 문구가 나오도록 수정
							contents += 		"<span>"+ietm_basic_version+"</span>";
							contents += 		"<span>- " +list.chgNo+ " -</span>"; 
							contents += 		"<span>" +list.chgDate+ "</span>";
							contents += 	"</a>";
							contents += "</li>";
							selectbox_content += "<option id='version_select_list_' value='goVersionDetailList(\""+ allBook +"\",\""+ list.verId +"\",\""+ toKey +"\",\"\")' onclick=\"goVersionDetailList('"+ allBook +"','"+ list.verId +"','"+ toKey +"','')\">";
							selectbox_content += 		""+ietm_view_all+"";
							selectbox_content += "</option>";
							selectbox_content += "<option id='version_select_list_x' value='goVersionDetailList(\"total\",\"all\",\""+ toKey +"\",\"x\")' onclick=\"goVersionDetailList('total','all','"+ toKey +"','x')\">";
							selectbox_content += 		""+ietm_change_history+"";
							selectbox_content += "</option>";
							//2022 12 13 Park.J.S. Update : 유효페이지목록에서 [changeno="0"]일 경우 클릭 이벤트 제거 (디지인 때문에 <a/> 태그 필요)
							/*
							selectbox_content += "<option id='version_select_list_0' value='goVersionDetailList(\""+ orgBook +"\",\""+ list.verId +"\",\""+ toKey +"\",\"0\")' onclick=\"goVersionDetailList('"+ orgBook +"','"+ list.verId +"','"+ toKey +"','0')\">";
							selectbox_content += 		""+ietm_original+"";
							selectbox_content += "</option>";
							*/
						} else if(list.chgNo != "0") {
							contents += "<li>";
							contents += 	"<a href='javascript:void(0);' title='"+ietm_modified_version+" - "+list.chgDate+"' onclick=\"goVersionDetailList('chg','"+ list.verId +"','"+ toKey +"','"+list.chgNo+"')\" class='changeBook'>";
							contents += 		"<span>"+ietm_modified_version+"</span>";
							contents += 		"<span>- " +list.chgNo+ " -</span>";
							contents += 		"<span>" +list.chgDate+ "</span>";
							contents += 	"</a>";
							contents += "</li>";
							selectbox_content += "<option id='version_select_list_"+list.chgNo+"' value='goVersionDetailList(\"chg\",\""+ list.verId +"\",\""+ toKey +"\",\""+list.chgNo+"\")' onclick=\"goVersionDetailList('chg','"+ list.verId +"','"+ toKey +"','"+list.chgNo+"')\">";
							selectbox_content += 		"" + ietm_modified_version + list.chgNo +"";
							selectbox_content += "</option>";
						}
					}
				}
				
				$(".version_select_list").append(selectbox_content);
				$("#avail_version_list").append(contents);
				$("#pup_version").show();
				$(".version_list").show();
				$(".version_con_list").hide();
			}
		});
	}
}

function callEvalFunction(obj){
	eval("("+obj.value+")");
}

//detail list 조회
function goVersionDetailList(book, verId, toKey, chgNo) {
	var param = "toKey=" + encodeURIComponent(toKey) + "&verId=" + verId + "&book=" + book;
	var ietmAll = $("#ietm_all").val();
	var ietmCount = $("#ietm_count").val();
	$("#avail_detail_list").empty();
	$("#pageNum").empty();
	$(".loading").show();
	
	$.ajax({
		type : "POST",
		url : "versionDetailList.do",
		data : param,
		dataType : "json",
		success : function(data) {
			var detailList = data.detailList;
			var detailLength = detailList.length;
			var ntocoName = data.ntocoName;
			var totCnt = data.totCnt;
			var contents = "";
			var ietm_no_change_info = $("#ietm_no_change_info").val();
			$("#version_select_list_"+chgNo).attr("selected", "selected");
			
			if(totCnt > "0") {
				$("#pageNum").text(""+ietmAll+" : "+totCnt+""+ietmCount+"");
				$("#pageNum").attr("title", ""+ietmAll+" :"+totCnt+""+ietmCount+"");
				
			} else {
				$("#pageNum").text(""+ietmAll+" : 0"+ietmCount+"");
				$("#pageNum").attr("title", ""+ietmAll+" : 0"+ietmCount+"");
			}
			
			if(ntocoName == "0") {
				contents += "<tr>";
				contents += 	"<td align='center' title='"+ietm_no_change_info+"' colspan='2'>"+ietm_no_change_info+"</td>";
				contents += "</tr>";
			} else {
				for(var i=0; i<detailLength; i++) {
					var list = detailList[i];
					contents += "<tr>";
					contents += 	"<td align='left' title='"+list.tocoName+"'>";
					contents += 		"<a href='javascript:void(0);' title='"+list.tocoName+"' onclick=\"viewToContents('"+list.toKey+"','"+list.tocoId+"','','01', '', '', 'N');\">"+list.tocoName+"</a>";
					contents += 	"</td>";
					contents += 	"<td title="+list.tocoChgNo+">"+list.tocoChgNo+"</td>";
					contents += "</tr>";
				}
			}
			console.log("contents : "+contents);
			$(".version_list").hide();
			$(".version_con_list").show();
			$("#avail_detail_list").append(contents);
			$(".version_con_list").scrollTop(0);
			$(".loading").hide();
			
			if($('#bizCode').val() == 'KTA' && $('#proectName').val().includes("KT1/")) {
				$.ajax({
					type : "POST",
					url : "versionName.do",
					data : {
						toKey: toKey,
						verId: verId
					},
					dataType : "json",
					success : function(data) {
						if(data.verName && data.verName != "") {
							var downFile = toKey + "_" + data.verName + ".xls";
							$('.ver_down_xls').attr("fileName", downFile);
							$('.ver_down_xls').show();
						} else {
							$('.ver_down_xls').hide();
						}
					}
				});
			}
		}
	});
}

function versionOutsidefileDownload(e){
	var fail = $('#ietm_file_down_failed').val();
	var fileName = $('.ver_down_xls').attr("fileName");

	try {
		if(filePathChk(fileName, 'xls') == true) {
			window.open($("#ietmDataUrl").val()+"/outsidefile/" + encodeURIComponent(fileName), "","").focus();
		} else {
			alert(fail);
		}
	} catch (e) {
		alert(fail);
	}
}