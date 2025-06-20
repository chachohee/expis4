console.log("CALL ietm_tree.js ");

$.when( $.ready ).then(function(){

    // Button Event
    bindButtonEvent();

});

function bindButtonEvent() {
    let target = "#to-buttons";
    let _btnTo = $("#btn-to", target);
    if ( _btnTo.length > 0 ) {
        _btnTo.on("click", function(e) {
            e.preventDefault;

            // 페이지를 벗어날 때 체크 개수 초기화를 위해 chkCount 제거
            sessionStorage.removeItem("chkCount");
            //toggle event
            toggleToActive('to');
        });
    }

    let _btnToco = $("#btn-toco", target);
    if ( _btnToco.length > 0 ) {
        _btnToco.on("click", function(e) {
            e.preventDefault;
            //toggle event
            toggleToActive('toco');
        });
    }
}

//To Tree
function loadTree() {
    if ( bizCode === "" ) return;

    // 1. call ajax
    $.ajax({
        type : "GET",
        data : "to_kind=01",
        url : "/EXPIS/"+bizCode+"/ietm/toTreeAjax.do",
        dataType : "json",
        cache : false,
        success : function(data) {
            var treeData = data.returnData; // XML 데이터
            if(treeData.match('●')) {
                treeData = treeData.replace(/●/gi, "•");
            }
            let jData = {};
            jData.lang = $("#lang").val() || "";
            jData.bizCode = bizCode || "";
            jData.type = "to";

            // XSLT와 XML 로드
            let xslData = getXmlSheet(jData); // XSLT 데이터 로드
            let xslt = loadXML(xslData); // XSLT 문서 로드
            let xml = loadXML(treeData); // XML 데이터 로드

            // XSLTProcessor로 변환 처리
            let xsltProcessor = new XSLTProcessor();
            xsltProcessor.importStylesheet(xslt);
            let resultDocument = xsltProcessor.transformToFragment(xml, document);

            $("#to_list").append(resultDocument); // 결과 HTML을 #to_list에 추가

        },
        error:function(e){
            alert("XML PARSING ERROR!!\n" + e);
        },
        complete:function() {
            // 8. set CSS
            updateToStyle(); // update Style
            bindToEvent(); //이벤트

            if($("#bizCode") && ($("#bizCode").val() == "BLOCK2" || $("#bizCode").val() == "KTA")){
                isOpenList();
            }
        }
    });
}

//To Style
function updateToStyle() {
    // 모든 li 태그를 순회
    $('#to_list li').each(function () {
        var $li = $(this);
        var $button = $li.find('> button');
        var $icon = $button.find('i');
        var $children = $li.find('> ul');

        if ($children.length > 0) {
            // li 태그가 자식을 가지고 있는 경우 (ul이 있는 경우)
//            $button.off('click').on('click', function (e) {
//                if ($children.hasClass('collapse show')) {
//                    $children.removeClass('show').addClass('collapse'); // 닫기
//                    $icon.removeClass('fa-folder-minus').addClass('fa-folder-plus'); // 아이콘 변경
//                } else {
//                    $children.removeClass('collapse').addClass('show'); // 열기
//                    $icon.removeClass('fa-folder-plus').addClass('fa-folder-minus'); // 아이콘 변경
//                }
//                e.stopPropagation(); // 이벤트 전파 방지
//            });

            // 기본적으로 부모 항목은 폴더 아이콘 설정
            $icon.removeClass('fa-regular fa-note-sticky').addClass('fa-solid fa-folder-plus');
        } else {
            // li 태그가 마지막 항목인 경우 (ul이 없는 경우)
            $icon.removeClass('fa-solid fa-folder-plus fa-folder-minus').addClass('fa-regular fa-note-sticky'); // 아이콘 변경

            let saveyn = $li.attr('saveyn') || "";
            if ( saveyn != "" ) {
                $icon.removeClass('fa-regular fa-note-sticky').addClass('fa fa-book').addClass('text-primary');
            }
        }
    });
}


//To Event
function bindToEvent() {
    let $target = $("#to_list");
    if ($target.length === 0) return;

    // 모든 항목에 대해 반복 처리
    $target.find('li').each(function () {
        let $li = $(this);
        let $a = $li.find('> a');

        // 클릭 이벤트 처리
        $a.off('click').on('click', function (e) {
            e.preventDefault(); // 기본 동작 방지

            let $childUl = $li.find('> ul');

            if ($childUl.length > 0) {
                toggleTree( $li, $childUl );
            } else {
                // active 처리
                let $rSet = $("li > a", $target);
                if ( $rSet.length > 0 ) {
                    $rSet.removeClass('active');
                }
                $(this).addClass('active');

                // 선택된 항목 처리
                /**
                 * 2025.05.27 - osm
                 * bizCode 별 li 목록 id에 지정된 toKey 값이 다르게 설정되어 있음
                 * - 현재 하드코딩으로 분기처리 해놨지만 이후 li의 id를 bizCode 별 동일하게 처리하게끔 수정 필요
                 */
                let id = $li.attr('id') || "";
                if ( bizCode === "KT1" || bizCode === "KA1" || bizCode === "KT100" ) id = $li.attr('name');
                if ( id === "" ) return;

                selectTo(id, 'toco'); // 목차로 이동
                TOKEY = id;             // 전역변수 설정

            }
        });

        let $button = $("button", $li);
        if ( $button.length > 0 ) {
            $button.off("click").on("click", function(e) {
                e.preventDefault(); // 기본 동작 방지

                let $childUl = $li.find('> ul');
                if ($childUl.length > 0) {
                    toggleTree( $li, $childUl );
                }
            });
        }
    });
}

//Toggle Tree
function toggleTree( $li, $childUl ) {
    // 트리의 자식 항목을 토글
    $childUl.toggleClass('collapse show');

    // 아이콘 전환
    let $icon = $li.find('> button i');
    if ($childUl.hasClass('collapse')) {
        $icon.removeClass('fa-folder-minus').addClass('fa-folder-plus'); // 닫힌 상태
    } else {
        $icon.removeClass('fa-folder-plus').addClass('fa-folder-minus'); // 열린 상태
    }
}

/**
 * 목록 btn event 함수
 */
function selectTo(toKey, status, mytoSeq) {
	console.log("Call selectTo toKey : "+toKey+", status : "+status+", "+mytoSeq);

    // 페이지를 벗어날 때 체크 개수 초기화를 위해 chkCount 제거
    sessionStorage.removeItem("chkCount");
//	if(confirmCLChk() == false) {
//		console.log("selectTo RETURN");
//		$("#cl_chk").val('Y');
//		return;
//	} else {
//		$("#cl_chk").val('N');
//	}
	if(status == "to") {

		var toTitle = toKey;
		var isbook = true;
		var objLi;
		if ( toKey.split(' ').length > 1 ) {
			objLi = $("div.tree_list a[name='"+ toKey +"']");
		} else {
			objLi = $("#" + toKey);
			if ( objLi.length == 0 ) {
				objLi = $("div.tree_list a[name='"+ toKey +"']");
			}
		}
		isbook = objLi.prev().attr("class") == "book" ? true : false;
		if ( objLi.length > 0 ) { 	toTitle = objLi.attr("name"); 	}

		if ( isbook ) {
			$("#sys_id").val(toTitle);
			callTocoList(toTitle, "open", status);
		}else{
			console.log("selectTo 2021 12 Add Check PDF : "+encodeURIComponent($("#" + toKey).attr("title")));

			//TODO 국제화
			console.log("CALL "+$("#" + toKey).attr("title"));
			if($("#" + toKey).attr("title") == "책자교범"){
				makePaperSystree("B");
			}else if($("#" + toKey).attr("title") == "원제작사 매뉴얼"){
				makePaperSystree("O");
			}else{
				paperDownloadCheck(encodeURIComponent($("#" + toKey).attr("title")));
			}
		}
	} else if(status == "fromIntro") {
		$("#sys_id").val(toKey);
		makeOpenTree("to_list", toKey);
		callTocoList(toKey, "open", "to");
//	} else if(status == "myTo") {
//		callMyTocoList(toKey, mytoSeq, "open", status);
	} else if (status == "MYTOCO") {
	    callMyTocoList(toKey, mytoSeq, "open", status);
	} else if(status == "paperTO") {
		//20191211 add PaperTO(책자교범) 일 경우 기능 추가
		var paperHtml = $("#to_paper_list").html();

		$(".contents_div").hide();
		$("#main_contents").html("");
		$("#main_contents").html(paperHtml);
		$("#main_contents").show();
	} else if(status == "videoDown") {
		//20200714 add 동영상 메뉴 일 경우 기능 추가
		var paperHtml = $("#video_download_list").html();

		$(".contents_div").hide();
		$("#main_contents").html("");
		$("#main_contents").html(paperHtml);
		$("#main_contents").show();
	} else {
		callTocoList(toKey, "open", "to",mytoSeq);
	}
}

/**
 * 책자교범 저장(다운로드) 기능
 */
function paperDownload(paperSeq) {

	location.href="paperDownload.do?paperSeq=" + paperSeq;

}

function paperDownloadCheck(paperFile,popUpFlag) {
	$.ajax({
		type : "POST",
		data : "paperSeq="+paperFile,
		url : $("#proectName").val()+"ietm/paperDownloadCheck.do",
		dataType : "json",
		cache : false,
		success : function(data) {
			console.log("PDF File Check Result : "+data);
			if(data.message == "success"){
				/**
				 * 2022 08 29 PDF 다리렉트 다운 로드 수정
				 */
				console.log("data.filePath : "+data.filePath);
				console.log("data.fileName : "+data.fileName);
				window.open($("#proectName").val()+"/web_lsam/ietmdata/paperfile/" + data.fileName, "","").focus();
				//paperDownload(paperFile);
			}else{
				console.log("PDF file not exist");
				if(popUpFlag){
					var ietm_manual_not_exist = $("#ietm_manual_not_exist").val();
					alert(ietm_manual_not_exist);
				}
			}
		},
		error:function(e){
			console.log("PDF File Check Error : "+e);
		},
		complete:function() {

		}
	});
}

/**
 * 동영상 파일 저장(다운로드) 기능
 */
function videoDownload(videoSeq) {

	location.href="videoDownload.do?videoSeq=" + videoSeq;
}


//ToCo Tree
function callTocoList(toKey, type, status, mytoSeq) {
    if (bizCode === "") return;
	//console.log("In callTocoList : "+toKey+", "+type+", "+status+", "+mytoSeq);
    var ietm_manual_not_exist = $("#ietm_manual_not_exist").val();
    //console.log("proectName : "+$("#proectName").val());
    $.ajax({
        type : "POST",
        data : "to_key=" + encodeURIComponent(toKey) + "&to_kind=02",
        url : "/EXPIS/"+bizCode+"/ietm/toTreeAjax.do",
        async: false,
        cache : false,
        dataType : "json",
        success : function(data){
            if(data.returnData == "") {
                alert(ietm_manual_not_exist);
                toKey = TOKEY; // 교범 데이터 없을 시 기존 전역 TOKEY 저장
                return false;
            }

            console.log("data.returnData : ", data.returnData);

            let jData = {};
            jData.lang = $("#lang").val() || "";
            jData.bizCode = $('#bizCode').val() || "";
            jData.type = "toco";

            let xslData = getXmlSheet(jData);
            let xslt = loadXML(xslData);
            let xml = loadXML(data.returnData);

            let xsltProcessor = new XSLTProcessor();
            xsltProcessor.importStylesheet(xslt);
            let resultDocument = xsltProcessor.transformToFragment(xml, document);
            console.log("resultDocument : ", resultDocument);

            $('#toco_list').html('');
            $("#toco_list").append(resultDocument);


            //main contents area
            $(".contents_div").hide();
            $("#main_contents").html("");
            $("#main_contents").show();

            // tab toggle
            toggleToActive('toco');

            updateTopInfo(toKey, mytoSeq);

        },
        error : function() {
            alert("Error : IETM callTocoList()");
        },
        complete : function() {
            // update Style
            updateToCoStyle();

            //이벤트
            bindToCoEvent(toKey);

            //메뉴 버튼 이벤트
            hoverEvent();
            menuBtnEvent(toKey);

            //관련교범 호출
            siblingTO(toKey);
        }
    });

    return true;
}

function updateTopInfo(toKey, mytoSeq) {
    console.log("mytoSeq : "+mytoSeq);
    if(mytoSeq && mytoSeq != "" && mytoSeq != "undefined"){
        console.log("In callTocoList Make  mytoSeq : "+mytoSeq);
        $("#top_to_name").html(mytoSeq);
    }else{
        console.log("toKey : "+toKey);
        var arryObj = document.getElementsByName(""+toKey);
        for ( var i = 0; i < arryObj.length; i++) {
            if(arryObj[i].getAttribute("subName") && arryObj[i].getAttribute("subName") != "" && arryObj[i].getAttribute("subName") != "undefined"){
                console.log("In callTocoList Make  getAttribute : "+arryObj[i].getAttribute("subName"));
                $("#top_to_name").html(arryObj[i].getAttribute("subName"));
                break;
            }else{
                //LASM의 경우
                var mainLiObj = arryObj[i].parentNode.parentNode.parentNode;
                var arryObj2 = mainLiObj.childNodes;
                for ( var j = 0; j < arryObj2.length; j++) {
                    if(arryObj2[j].href){
                        $("#top_to_name").html(arryObj2[j].text);
                        break;
                    }
                }
            }
        }
    }
}

// ToCo style
function updateToCoStyle() {
    console.log("updateToCoStyle");

    // #toco_list 요소가 존재하는 경우
    let _target = $("#toco_list");
    if (_target.length === 0) return;

    let _li = $("li", _target);
    if (_li.length === 0) return;

    // 모든 li 태그를 순회
    $('#toco_list li').each(function () {
        var $li = $(this);
        var $button = $li.find('> button');
        var $icon = $button.find('i');
        var $children = $li.find('> ul');

        if ($children.length > 0) {
            // li 태그가 자식을 가지고 있는 경우 (ul이 있는 경우)
            $button.off('click').on('click', function (e) {
                if ($children.hasClass('collapse show')) {
                    $children.removeClass('show').addClass('collapse'); // 닫기
                    $icon.removeClass('fa-folder-minus').addClass('fa-folder-plus'); // 아이콘 변경
                } else {
                    $children.removeClass('collapse').addClass('show'); // 열기
                    $icon.removeClass('fa-folder-plus').addClass('fa-folder-minus'); // 아이콘 변경
                }
                e.stopPropagation(); // 이벤트 전파 방지
            });

            // 기본적으로 부모 항목은 폴더 아이콘 설정
            $icon.removeClass('fa-regular fa-file-lines').addClass('fa-solid fa-folder-plus');
        } else {
            // li 태그가 마지막 항목인 경우 (ul이 없는 경우)
            $icon.removeClass('fa-solid fa-folder-plus fa-folder-minus').addClass('fa-regular fa-file-lines'); // 아이콘 변경
        }

         // vehicle 타입에 따른 비활성화 처리
        let vehicle = $li.attr("vehicletype") || "";
        if (vehicle.toUpperCase() !== "NONE" && vehicle !== "") {
            if (!vehicle.includes(bizCode)) {
                $li.attr('disabled', true); // 조건에 맞지 않으면 비활성화
            }
        }
    });

}

// ToCo event
function bindToCoEvent(toKey) {
    // 교범 이동 시 전역 TOKEY가 매개변수 toKey와 다를경우 매개변수를 전역으로 설정
    if (TOKEY !== toKey) TOKEY = toKey;
    // #toco_list 요소가 존재하는지 확인
    let $target = $("#toco_list");
    if ($target.length === 0) return;

    // 모든 항목에 대해 반복 처리
    $target.find('li').each(function () {
        let $li = $(this);
        let $a = $li.find('> a');
        if ($a.length === 0) return;

        let $childUl = $li.find('> ul');
        let $icon = $li.find('> button i');

        // 클릭 이벤트 처리
        $a.off('click').on('click', function (e) {
            e.preventDefault(); // 기본 동작 방지

            // cl 교범 인풋박스 체크 유무
            let chkCount = sessionStorage.getItem("chkCount") || "0";
            if(chkCount > 0){
            const confirmed = confirm(chkCount + ietmNextPageContents);
                if (!confirmed) return;
            }

            if ($childUl.length > 0) {
                toggleTree( $li, $childUl );
            }

            // 상단 교범 정보 처리
//            showTopInfomation($li);

            // 추가적인 클릭 처리 (type이 "TO"일 경우 표지 보여주기)
            if ($li.attr('type').toUpperCase() === "TO") {
                showCover($li);
                // 표지 클릭 시, IPB 하단 컨트롤 숨기기
                $("#ipbControl").addClass("d-none").hide();
                return;
            }

            // 본문 처리
            showContents($li);

            // active 처리
            let $rSet = $("li > a", $target);
            if ( $rSet.length > 0 ) {
                $rSet.removeClass('active');
            }
            $(this).addClass('active');

        });

        let $button = $("button", $li);
        if ( $button.length > 0 ) {
            $button.off("click").on("click", function(e) {
                e.preventDefault(); // 기본 동작 방지

                let $childUl = $li.find('> ul');
                if ($childUl.length > 0) {
                    toggleTree( $li, $childUl );
                }
            });
        }
    });
}

// 상단 교범 정보 표시
function showTopInfomation( _li ) {
    $.ajax({
        type : "GET",
        data : "to_kind=01",
        url : "ietm/toTreeAjax.do",
        dataType : "json",
        cache : false,
        success : function(data) {
            var treeData = data.returnData; // XML 데이터
            if(treeData.match('●')) {
                treeData = treeData.replace(/●/gi, "•");
            }
            let jData = {};
            jData.lang = $("#lang").val() || "";
            jData.bizCode = $('#bizCode').val() || "";
            jData.type = "to";

            // XSLT와 XML 로드
            let xslData = getXmlSheet(jData); // XSLT 데이터 로드
            let xslt = loadXML(xslData); // XSLT 문서 로드
            let xml = loadXML(treeData); // XML 데이터 로드

            // XSLTProcessor로 변환 처리
            let xsltProcessor = new XSLTProcessor();
            xsltProcessor.importStylesheet(xslt);
            let resultDocument = xsltProcessor.transformToFragment(xml, document);

            $("#to_list").append(resultDocument); // 결과 HTML을 #to_list에 추가

        },
        error:function(e){
            alert("XML PARSING ERROR!!\n" + e);
        },
        complete:function() {
            // 8. set CSS
            updateToStyle(); // update Style
            bindToEvent(); //이벤트

            if($("#bizCode") && ($("#bizCode").val() == "BLOCK2" || $("#bizCode").val() == "KTA")){
                isOpenList();
            }
        }
    });
}

// 표지 표시
function showCover( _li ) {

}

// 본문 표시
function showContents( _li ) {

	//main content 배경 적용
	let contentWrapper = $(".main-container .content-wrapper");
	if (contentWrapper.length > 0) {
	    if ( !contentWrapper.hasClass("bg-white") ) contentWrapper.addClass("bg-white");
	}

    tocoContView(_li.attr('id'));
}

// function PaintTocoList(toKey, type, status, treeData, mytoSeq) {
function PaintTocoList(toKey, type, status) {
	console.log("Call PaintTocoList toKey : "+toKey+", status : "+status+", type : "+type);
	var tocoId = "";
	var appendId = "";
	var vehicle = $("#vehicle_type").val();
	var regExp = "";
	var regVeh = "";
	var vehicleArray = new Array();
	var toVehicleChk = false;
	var toVehicleText = "";
	var addVehicleStr = "";
	console.log("PaintTocoList vehicle : "+vehicle);
	if(status == "to") {
		$("#toco_list").html("");
	} else {
		$("#mytoco_list").html("");
	}

	if(status == "to") {
		$("#to_key").val(toKey);
	} else {
		$("#myto_key").val(toKey);
		$("#myto_seq").val(mytoSeq);
	}

	// 8. set CSS
	// makeTree(appendId);
	console.log("status : "+status);
	if(status == "to") {
		clickId = tocoId;
//		tocoToggle("toco");
	} else {
		clickId = "myto_" + tocoId;
		myToListBtn("mytoco_list");
	}
	console.log("type : "+type+", clickId : "+clickId);
	if(type == "open") {
		console.log("Call open : "+clickId);
		$("#" + clickId).click();
		if($("#" + clickId).attr("onclick") == undefined) {
			$("#main_contents").hide();
			$(".no_data_img").show();
		}

	} else {
		console.log("Call else !open");
		makeOpenTree(appendId, type);
	}
}

function childSetParentType(obj){
	console.log("obj.vehicletype : "+obj.vehicletype);
	for(var i=0;i<obj.children().length;i++){
		var tempObj = obj.children()[i];
		console.log("tempObj["+tempObj.id+"] A : "+JSON.stringify(tempObj));
		tempObj.vehicletype = obj.vehicletype;
		console.log("tempObj["+tempObj.id+"] B : "+JSON.stringify(tempObj));
		if($(tempObj).children().length > 0){
			childSetParentType($(tempObj));
		}
	}
}

function callMyTocoList(mytoKey, mytoSeq, type, status) {
    if (bizCode === "") return;
	$.ajax({
		type : "POST",
	 	data : "mytoSeq=" + mytoSeq + "&to_kind=02",
	 	url : "/EXPIS/"+bizCode+"/ietm/mytocoTreeAjax.do",
		aync : false,
		dataType : "json",
		cache :  false,
		success : function(data){
		    console.log("callMyTocoList toKey", data.toKey);
			paintMyTocoList(data.toKey, type, data.returnData, status);
		},
		error : function() {
			alert("Error : myto.js callMyTocoList()");
		},
		complete : function() {
		}
	});
}

function paintMyTocoList(toKey, type, treeData, status) {
    console.log("paintMyTocoList toKey: ", toKey);
    console.log("treeData: ", treeData); // xml 데이터 확인
    $("#mytoco_list").html("");  // 기존 리스트 초기화

    if (treeData == "<techinfo></techinfo>" || treeData == "") {
        $(".myto-tree .mytoco_list").html("<div class='txt_noData noBg'>NO DATA</div>");
        return;
    }

    let jData = {};
    jData.lang = $("#lang").val() || "";
    jData.bizCode = $('#bizCode').val() || "";
    jData.toKey = toKey || "";
    jData.type = "MYTOCO";

    // XSLT와 XML 로드
    let xslData = getXmlSheet(jData); // XSLT 데이터 로드
    let xslt = loadXML(xslData); // XSLT 문서 로드
    let xml = loadXML(treeData); // XML 데이터 로드

    // XSLTProcessor로 변환 처리
    let xsltProcessor = new XSLTProcessor();
    xsltProcessor.importStylesheet(xslt);
    xsltProcessor.setParameter(null, "toKey", jData.toKey);
    let resultDocument = xsltProcessor.transformToFragment(xml, document);

    // 기존 내용 비우기
    $("#mytoco_list").empty();
    $("#mytoco_list").append(resultDocument);

    // 2. CSS 적용
    updateMyTocoStyle();
    // 3. 버튼 이벤트
    bindMyTocoEvent();

}
//function paintMyTocoList(toKey, type, treeData, status) {
//	// 1. init ajax
//	var tocoId = "";
//	var appendId = "";
//	$("#mytoco_list").html("");
//
//	// 2. loop returnData
//	$(treeData).find("system").each(function() {
//		// 3. initialize the value
//		var sysId = $(this).attr("id");
//		var sysName = $(this).attr("name");
//		var sysType = $(this).attr("type");
//		var parentId = $(this).parent().attr("id");
//		var divHtml = document.createElement("li");
//		var existTo = null;
//		var existToSub = null;
//		var existUl = null;
//		var tagA = document.createElement("a");
//		var contentsInfo = "";
//		var vcKind = "";
//		var mytoUl =  "myto_ul_"+sysId;
//		var ulId = "myto_ul_"+parentId;
//		var liId = "myto_" + sysId;
//		var folderChk = "Y";
//
//		if(sysType == toKey) {
//			tocoId = sysId;
//			contentsInfo = "viewToContents('"+toKey+"', '"+sysId+"', '', '01', '', 'myto', 'N')";
//		} else if (sysType=="GrphToco") {
//			contentsInfo = "toSubTocoList('"+toKey+"', '02')";
//		} else if (sysType=="TableToco") {
//			contentsInfo = "toSubTocoList('"+toKey+"', '03')";
//		} else if (sysType=="Grph") {
//			contentsInfo = "viewToContents('"+toKey+"', '"+$(this).attr("listuuid")+"', '', '03', '"+sysId+"', 'myto', 'N')";
//		} else if (sysType=="Table") {
//			contentsInfo = "viewToContents('"+toKey+"', '"+$(this).attr("listuuid")+"', '', '04', '"+sysId+"', 'myto', 'N')";
//		} else if (sysType=="ipb") {
//			contentsInfo = "viewToContents('"+toKey+"', '"+sysId+"', '', '05', '', 'myto', 'N')";
//		} else {
//			contentsInfo = "viewToContents('"+toKey+"', '"+sysId+"', '', '01', '', 'myto', 'N')";
//		}
//
//		if(sysName == toKey) {
//			tocoId = sysId;
//		}
//
//		// 5. check exist children
//		if($(this).children().length < 1){
//			existTo = document.createElement("span");
//			existTo.setAttribute("class", "paper");
//			divHtml.appendChild(existTo);
//			folderChk = "N";
//		} else {
//			existUl = document.createElement("ul");
//			existUl.setAttribute("id", mytoUl);
//		}
//
//		divHtml.setAttribute("id", "myto_li_"+sysId);
//
//		// 6. make HTML
//		tagA.setAttribute("href", "javascript:void(0);");
//		tagA.tagName = sysId;
//		tagA.setAttribute("id", "myto_" + sysId);
//		tagA.setAttribute("title", sysName);
//		tagA.setAttribute("name", "myto_" + sysName);
//		tagA.setAttribute("type", sysType);
//		tagA.setAttribute("onclick", contentsInfo);
//		tagA.setAttribute("folderChk", folderChk);
//
//		divHtml.appendChild(tagA);
//
//		if(existUl != null) {
//			divHtml.appendChild(existUl);
//		}
//
//		if(parentId == null || parentId == "") {
//			document.getElementById("mytoco_list").appendChild(divHtml);
//		} else {
//			document.getElementById(ulId).appendChild(divHtml);
//		}
//		document.getElementById(liId).innerText = sysName;
//	});
//
//	// 8. set CSS
//	makeTree("mytoco_list");
//	$("#myto_key").val(toKey);
//	myToListBtn("mytoco_list");
//	if(type == "open") {
//		$("#myto_" + tocoId).click();
//	} else {
//		makeOpenTree(appendId, type);
//	}
//}


function myToListBtn(param) {
	if($("#myto_key").val() == "") {
		return;
	}

	$(".myto_list_class").hide();
	$(".btn_myto").removeClass("on");
	if($("#" + param).css("display") == "none") {
		$(".btn_" + param).addClass("on");
		$("#" + param).show();
	} else {
		$("#" + param).hide();
	}
}

//교범있을때 메뉴 버튼 활성화 이벤트
function hoverEvent() {
	var ietm_validity_v = $("#ietm_validity_v").val();
	var ietm_search_f = $("#ietm_search_f").val();
	var ietm_no_select_manu = $("#ietm_no_select_manu").val();
	$(".btn_menu_version_none").attr("title", ietm_validity_v);
	$(".btn_menu_search_none").attr("title", ietm_search_f);
	$(".remote_control_disabled").hide();
	$(".remote_control").show();

	$("#top_search_selectbox").attr("onclick","selectBoxEvent('top_search')");
	$("#search_selectbox").attr("onclick", "selectBoxEvent('search')");

	$("#top_search_word").attr("readonly", false);

	$(".btn_menu_version_none").addClass("btn_menu_version");
	$(".btn_menu_search_none").addClass("btn_menu_search");

	$(".btn_menu_version_none").removeClass("btn_menu_version_none");
	$(".btn_menu_search_none").removeClass("btn_menu_search_none");

	if($("#memo_insert_area").text() == ietm_no_select_manu) {
		$("#memo_insert_area").css("color", "#000");
		$("#memo_insert_area").text("");
	}
	$("#memo_insert_area").attr("onblur", "");
	$("#memo_insert_area").attr("onfocus", "");

}


/* MYTO */
let selectedFolderSeq = null; // 선택된 폴더의 seq를 저장할 변수
let selectedFolderElement = null; // 현재 선택된 폴더 요소를 저장할 변수
let buttonElement = null;

// 폴더 선택 함수
function selectFolder(seq) {

    // 기존에 선택된 폴더에서 'selected-folder' 클래스를 제거
    if (selectedFolderElement) {
        selectedFolderElement.classList.remove('selected-folder');
    }

    if (buttonElement) {
        buttonElement.classList.remove('selected-folder');
    }

    selectedFolderSeq = seq;
    console.log("SelectedFolderSeq : ", selectedFolderSeq);

    // 새로운 폴더 선택
    selectedFolderElement = document.querySelector(`li[data-folder-seq="${seq}"]`);
    buttonElement = selectedFolderElement.querySelector('button');

    if (buttonElement) {
        buttonElement.classList.add('selected-folder'); // button에 'selected-folder' 클래스를 추가
    }

    // 목차 목록 보여주기
    selectTo('','MYTOCO', seq);

}

/* MYTO  목록 */
function loadMyFolder() {
    if (bizCode === "") return;

    $.ajax({
        url: `/EXPIS/${bizCode}/ietm/selectMyFolder.do`,
        method: 'GET',
        success: function (data) {
            if (data.success) {
                const folderTree = buildFolderTree(data.folderList); // 트리 구조 변환
                renderFolderList(folderTree); // 변환된 트리 구조로 렌더링
            } else {
                alert("폴더 목록을 불러오는 데 실패했습니다.");
            }
        },
        error: function (error) {
            console.log('폴더 목록 불러오기 실패:', error);
            alert("서버 통신 중 오류가 발생했습니다.");
        }
    });
}

function buildFolderTree(flatList) {
    const folderMap = {};  // ID 기준으로 폴더를 저장할 맵
    const rootFolders = []; // 최상위 폴더 목록

    // 먼저 모든 폴더를 맵에 저장
    flatList.forEach(folder => {
        folder.children = []; // 자식 폴더를 저장할 배열 추가
        folderMap[folder.mytoSeq] = folder;
    });

    // 부모-자식 관계를 매핑
    flatList.forEach(folder => {
        if (folder.mytoPSeq && folderMap[folder.mytoPSeq]) {
            // 부모 폴더가 존재하면 해당 부모의 children 배열에 추가
            folderMap[folder.mytoPSeq].children.push(folder);
        } else {
            // 부모가 없으면 최상위 폴더로 간주
            rootFolders.push(folder);
        }
    });

    return rootFolders;
}

function renderFolderList(folderList) {
    const mytoList = document.querySelector('.tree_list #myto_list');
    mytoList.innerHTML = ''; // 기존 폴더 목록 초기화

    // 폴더 목록을 렌더링하는 재귀 함수
    function renderFolder(folder, parentElement) {

        // li 태그
        const newFolder = document.createElement('li');
        newFolder.classList.add('nav-item');
        newFolder.setAttribute('data-folder-seq', folder.mytoSeq); // 폴더 seq 설정

        // button 태그
        const folderButton = document.createElement('button');
        folderButton.setAttribute('type', 'button');

        // i 태그
        const folderIcon = document.createElement('i');
        if (folder.children && folder.children.length > 0) {
            folderIcon.classList.add('fa-solid', 'fa-folder-plus');
        } else {
            folderIcon.classList.add('fa-solid', 'fa-folder');
        }

        // 폴더 이름 추가
        const folderName = document.createElement('span');
        folderName.textContent = `${folder.mytoName || folder.toKey}`; // 연산자(||) 왼쪽 값이 falsy일 때 오른쪽 값 반환

        // 버튼에 아이콘과 이름 추가
        folderButton.appendChild(folderIcon);
        folderButton.appendChild(folderName);

        // 버튼 클릭 이벤트 추가
        if (folder.children && folder.children.length > 0) {
            folderButton.addEventListener('click', function () {
                const subFolderList = newFolder.querySelector('ul');
                const isOpen = subFolderList.classList.contains('show');

                // 자식 폴더가 열려있으면 닫고, 닫혀있으면 열도록 처리
                if (isOpen) {
                    subFolderList.classList.remove('show'); // 숨기기
                    folderIcon.classList.remove('fa-folder-minus');
                    folderIcon.classList.add('fa-folder-plus'); // 플러스 아이콘으로 변경
                } else {
                    subFolderList.classList.add('show'); // 보이기
                    folderIcon.classList.remove('fa-folder-plus');
                    folderIcon.classList.add('fa-folder-minus'); // 마이너스 아이콘으로 변경
                }
            });
        }

        // 폴더 선택 이벤트 (selectFolder 함수 호출)
        folderButton.addEventListener('click', function (event) {
            if (!folder.children || folder.children.length === 0) {
                toggleMyToActive('mytoco'); // 자식 폴더가 없을 때만 실행
//                loadMyToco(folder.mytoSeq); // 목차 조회
                selectFolder(folder.mytoSeq); // 폴더 선택 효과
            }
        });

        // 현재 폴더를 부모 요소에 추가
        newFolder.appendChild(folderButton);
        parentElement.appendChild(newFolder);

        // 하위 폴더가 있는 경우 재귀적으로 렌더링
        if (folder.children && folder.children.length > 0) {
            const subFolderList = document.createElement('ul');
            subFolderList.classList.add('collapse', 'nav', 'flex-column', 'ps-3'); // 하위 폴더 스타일 적용 (기본적으로 숨김)
            newFolder.appendChild(subFolderList);

            folder.children.forEach(childFolder => {
                renderFolder(childFolder, subFolderList); // 하위 폴더 렌더링
            });
        }
    }

    // 최상위 폴더 렌더링
    folderList.forEach(folder => {
        renderFolder(folder, mytoList);
    });
}

/* MYTO  목차 */
function loadMyToco(folderSeq) {
    console.log('folderSeq: ', folderSeq);
    const mytoSeq = folderSeq;

    if (bizCode === "") return;
    const mytocoList = document.querySelector('.tree_list #mytoco_list');
    mytocoList.innerHTML = ''; // 기존 목록 초기화

    $.ajax({
        url: `/EXPIS/${bizCode}/ietm/selectMyToco.do?mytoSeq=${mytoSeq}`,
        method: 'GET',
        success: function (data) {
            if (data.success) {
                console.log("MYTO 목차 목록", data);

                // myTocoList를 순회하면서 각 항목을 items 객체에 저장(tocoId를 키로 사용)
                const items = {};
                data.myTocoList.forEach(item => {
                    items[item.tocoId] = item;
                })

                // 트리 구조 생성
                const buildTree = (parentId) => {
                    const tree = [];
                    Object.values(items).forEach(item => {
                        if (item.pTocoId === parentId) {
                            const children = buildTree(item.tocoId);
                            if (children.length > 0) {
                                item.children = children;
                                item.isOpen = false; // 초기에 접힌 상태로 보여짐
                            }
                            tree.push(item);
                        }
                    });

                    // mytocoOrd 값에 따라 오름차순 정렬
                    tree.sort((a, b) => a.mytocoOrd - b.mytocoOrd);

                    return tree;
                }

                const rootItem = Object.values(items).find(item => item.mytocoSeq === 1);
                let tree = [];
                // mytocoSeq가 1인 항목이 존재하면 해당 항목의 pTocoId로 트리를 시작
                if (rootItem) {
                    tree = buildTree(rootItem.pTocoId); // pTocoId를 사용하여 트리를 생성
                } else {
                    console.error("mytocoSeq가 1인 항목이 없습니다.");
                }

                // 트리 렌더링
                const renderTree = (tree) => {
                    const ul = document.createElement('ul');
                    ul.classList.add('nav', 'flex-cloumn', 'ps-3');
                    tree.forEach(item => {
                        const li = document.createElement('li');
                        li.classList.add('nav-item');
                        li.id = item.tocoId;
                        li.setAttribute('itemid', item.mytocoName);
//                        li.setAttribute('status', item.tocoStatusCode);
                        li.setAttribute('name', item.mytocoName);
                        li.setAttribute('type', item.mytocoKind);
                        li.setAttribute('myto_seq', item.mytoSeq);
                        li.setAttribute('seq', item.mytocoSeq);
                        li.setAttribute('ptocoid', item.pTocoId || ''); // 부모 ID 저장

                        const isCollapsed = item.isOpen == false; // 최상위 항목은 기본적으로 접힘 상태

                        li.innerHTML = `
                            <button type="button"><i class="fa-solid fa-folder-plus"></i></button>
                            <a href="#" class="nav-link" data-bs-toggle="collapse">${item.mytocoName}</a>
                        `;

                        // 자식이 있으면 트리 렌더링
                        if (item.children) {
                            const childUl = renderTree(item.children);
                            if (isCollapsed) {
                                childUl.classList.add('collapse'); // 접힌 상태로 시작
                            } else {
                                childUl.classList.add('show'); // 펼쳐진 상태로 시작
                            }
                            li.appendChild(childUl);
                        }

                        ul.appendChild(li);

                    });

                    return ul;

                };
                mytocoList.appendChild(renderTree(tree));

                updateMyTocoStyle();

                bindMyTocoEvent();

            } else {
                alert("목차 목록을 불러오는 데 실패했습니다.");
            }
        },
        error: function (error) {
            console.log('loadMyToco Error:', error);
            alert("서버 통신 중 오류가 발생했습니다.");
        }
    });
}

function updateMyTocoStyle() {
    let _target = $("#mytoco_list");
    if (_target.length === 0) return;

    let _li = $("li", _target);
    if (_li.length === 0) return;

    // 모든 li 태그를 순회
    $('#mytoco_list li').each(function () {
        var $li = $(this);
        var $button = $li.find('> button');
        var $icon = $button.find('i');
        var $children = $li.find('> ul');

        if ($children.length > 0) {
            // li 태그가 자식을 가지고 있는 경우 (ul이 있는 경우)
            $button.off('click').on('click', function (e) {
                if ($children.hasClass('collapse show')) {
                    $children.removeClass('show').addClass('collapse'); // 닫기
                    $icon.removeClass('fa-folder-minus').addClass('fa-folder-plus'); // 아이콘 변경
                } else {
                    $children.removeClass('collapse').addClass('show'); // 열기
                    $icon.removeClass('fa-folder-plus').addClass('fa-folder-minus'); // 아이콘 변경
                }
                e.stopPropagation(); // 이벤트 전파 방지
            });

            // 기본적으로 부모 항목은 폴더 아이콘 설정
            $icon.removeClass('fa-regular fa-file-lines').addClass('fa-solid fa-folder-plus');
        } else {
            // li 태그가 마지막 항목인 경우 (ul이 없는 경우)
            $icon.removeClass('fa-solid fa-folder-plus fa-folder-minus').addClass('fa-regular fa-file-lines'); // 아이콘 변경
        }

        // vehicle 타입에 따른 비활성화 처리
        let vehicle = $li.attr("vehicletype") || "";
        if (vehicle.toUpperCase() !== "NONE" && vehicle !== "") {
            if (!vehicle.includes(bizCode)) {
                $li.attr('disabled', true); // 조건에 맞지 않으면 비활성화
            }
        }
    });
}

function bindMyTocoEvent() {

    let $target = $("#mytoco_list");
    if ($target.length === 0) return;

    // 모든 항목에 대해 반복 처리
    $target.find('li').each(function () {
        let $li = $(this);
        let $a = $li.find('> a');
        if ($a.length === 0) return;

        let $childUl = $li.find('> ul');
        let $icon = $li.find('> button i');

        // 클릭 이벤트 처리
        $a.off('click').on('click', function (e) {
            e.preventDefault(); // 기본 동작 방지

            if ($childUl.length > 0) {
                // 자식 항목이 있는 경우 토글
                $childUl.toggleClass('collapse show');

                // 아이콘 전환
                if ($childUl.hasClass('collapse')) {
                    $icon.removeClass('fa-folder-minus').addClass('fa-folder-plus'); // 닫힌 상태
                } else {
                    $icon.removeClass('fa-folder-plus').addClass('fa-folder-minus'); // 열린 상태
                }
            }
            tocoContView($li.attr('id'));
        });
    });
}
