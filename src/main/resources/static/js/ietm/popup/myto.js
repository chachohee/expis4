console.log("call myto.js ");
$(document).ready(function () {
  loadTree(); // TO 목록 로드
  loadMyFolder(); // MYTO 폴더 목록 로드
});

/* Tree */
//To Tree
function loadTree() {
    console.log("myto.js loadTree()...");
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

            // resultDocument에서 li를 찾아 saveyn="Y" 속성 확인
            $(resultDocument).find('li').each(function() {
                var $li = $(this);
                var saveyn = $li.attr('saveyn'); // saveyn 속성 확인

                if (saveyn === "Y") {
                    // li 태그의 부모 요소에 버튼을 추가하기 위해 버튼을 별도로 생성
                    var $plusButton = $('<button class="btn-plus"><i class="fa fa-plus-circle"></i></button>');

                    // 버튼 클릭 시 createFolder 함수 호출
                    $plusButton.on('click', function() {
                        var liId = $li.attr('id');
                        // KT1 교범은 id랑 name 속성값이 달라서 name 속성도 추가
                        var liName = $li.attr('name');
                        console.log("liName : ", liName);
                        createFolder(liId, liName);
                    });

                    $li.append($plusButton); // li에 버튼 추가
                }
            });

            $(".myto-tree .to_list").append(resultDocument); // 결과 HTML을 #to_list에 추가

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

//XSLT 로드
function getXmlSheet(jData) {
    let rtn = "";
    let url = jData.type === "to" ? "/xslt/ToTree.xml" : "";
    url = jData.type === "toco" ? "/xslt/ToCoTree.xml" : url;
    url = jData.type === "contents" ? "/xslt/ToContents.xml" : url;
    url = jData.type == "MYTOCO" ? "/xslt/MyTocoTree.xml" : url;

    $.ajax({
        type : "GET",
        async: false,
        url : url,
        dataType : "text",
        cache : false,
        success : function(data) {
            rtn = data;
        },
        error:function(e){
            rtn = "";
        }
    });
    return rtn;
}

// 텍스트를 xml로
function loadXML(data) {
    console.log("loadXML data: ", data);
    if ( data === 0 ) return "";
    return $.parseXML(data);
}

//To Style
function updateToStyle() {
    // 모든 li 태그를 순회
    $('.myto-tree .to_list li').each(function () {
        var $li = $(this);
        var $button = $li.find('> button');
        var $icon = $button.find('i');
        var $children = $li.find('> ul');

        if ($children.length > 0) {
            // li 태그가 자식을 가지고 있는 경우 (ul이 있는 경우)
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

        // btn-plus 버튼의 아이콘에서 클래스 제거
        $li.find('.btn-plus i').removeClass('fa-regular fa-note-sticky fa-book text-primary');

    });
}

//To Event
function bindToEvent() {
    console.log("bindToEvent...");
    let $target = $(".myto-tree .to_list");
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
                // 트리의 자식 항목을 토글
                $childUl.toggleClass('collapse show');

                // 아이콘 전환
                let $icon = $li.find('> button i');
                if ($childUl.hasClass('collapse')) {
                    $icon.removeClass('fa-folder-minus').addClass('fa-folder-plus'); // 닫힌 상태
                } else {
                    $icon.removeClass('fa-folder-plus').addClass('fa-folder-minus'); // 열린 상태
                }
            } else {
                // 선택된 항목 처리
                // selectTo($li.attr('id'), 'toco'); // 목차로 이동
                selectTo($li.attr('name'), 'toco'); // KT1은 name 속성값이 toKey라 수정
            }
        });
    });
}


/**
 * 목록 btn event 함수
 */
function selectTo(toKey, status, mytoSeq) {
	console.log("call myto.js selectTo toKey : "+toKey+", status : "+status+", "+mytoSeq);
	$("#selected_key").val(toKey);

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
        callTocoList(toKey, "open", "to");
    } else if(status == "MYTOCO") {
        callMyTocoList(toKey, mytoSeq, "open", status);
    } else {
		callTocoList(toKey, "open", "toco",mytoSeq);
	}
}

//ToCo Tree
function callTocoList(toKey, type, status, mytoSeq) {

    if (bizCode === "") return;

    var ietm_manual_not_exist = $("#ietm_manual_not_exist").val();

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
                return false;
            }
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

            // 기존 내용 비우기
            $(".myto-tree .toco_list").empty();
            // 새로운 내용 추가
            $(".myto-tree .toco_list").append(resultDocument);

        },
        error : function() {
            alert("Error : MYTO TO목차 callTocoList()");
        },
        complete : function() {
            // update Style
            updateToCoStyle();
            //이벤트
            bindToCoEvent();
            //메뉴 버튼 이벤트
            hoverEvent();
        }
    });
    return true;
}

// ToCo style
function updateToCoStyle() {

    let _target = $(".myto-tree .toco_list");
    if (_target.length === 0) return;

    let _li = $("li", _target);
    if (_li.length === 0) return;

    let count = 0; // ord 순서 매기기

    // 모든 li 태그를 순회
    $('.myto-tree .toco_list li').each(function () {
        var $li = $(this);
        var $button = $li.find('> button');
        var $icon = $button.find('i');
        var $children = $li.find('> ul');

        // ord 속성 추가
        $li.attr('ord', count);

        // 체크박스가 이미 존재하는지 확인
        if ($li.find('.toco-checkbox').length === 0) {
            // 체크박스를 li 태그 옆에 추가
            var $checkbox = $('<input>', {
                type: 'checkbox',
                class: 'toco-checkbox', // 체크박스의 클래스
                id: 'checkbox_' + $li.attr('id'), // 고유 ID 추가
            });

            // li의 가장 앞에 체크박스를 추가
            $li.prepend($checkbox);
        }

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
        // ord 증가
        count++;
    });
}

//ToCo event
function bindToCoEvent() {

    let $target = $(".myto-tree .toco_list");
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
//            tocoContView($li.attr('id'));
        });
    });
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



/* 메뉴바 조작 */
document.addEventListener('click', function () {
    /* 화면 클릭하면 selectedFolderSeq = null; */
    // 클릭된 요소가 폴더 버튼 또는 폴더 관련 요소인지 확인
    const isFolderButton = event.target.closest('.myto_list .nav-item button'); // 폴더 버튼
    const isFolderIcon = event.target.closest('.myto_list .nav-item .icon'); // 수정/삭제 아이콘

    // 클릭된 요소가 폴더 관련 요소가 아니라면 selectedFolderSeq를 null로 설정
    if (!isFolderButton && !isFolderIcon) {
        selectedFolderSeq = null;
        console.log("SelectedFolderSeq 초기화: ", selectedFolderSeq);

        if (selectedFolderSeq === null) {
            const buttons = document.querySelectorAll('.myto_list .nav-item button');
            buttons.forEach(button => {
                button.classList.remove('selected-folder');
            });
        }
    }
});

document.addEventListener('DOMContentLoaded', function () {
    // 체크박스 전체 선택/해제 로직
    function setupCheckboxAll(checkboxId, selector) {
        document.getElementById(checkboxId)?.addEventListener('change', function () {
            const isChecked = this.checked;
            document.querySelectorAll(selector).forEach(checkbox => {
                checkbox.checked = isChecked;
            });
        });
    }

    // 전체 선택 체크박스 설정
    setupCheckboxAll('toco-checkbox-all', '.toco_list .toco-checkbox');
    setupCheckboxAll('mytoco-checkbox-all', '.mytoco_list .mytoco-checkbox');
    setupCheckboxAll('myto-checkbox-all', '.myto_list .myto-checkbox');
});

document.addEventListener('DOMContentLoaded', function () {
    const tocoList = document.querySelector('.toco_list.tree');
    const mytocoList = document.querySelector('.mytoco_list.tree');

    // 체크박스 변경 이벤트 감지
    tocoList.addEventListener('change', function (event) {
        const target = event.target;

        // 체크박스인지 확인
        if (target.classList.contains('toco-checkbox')) {
            const parentLi = target.closest('.nav-item'); // 현재 체크박스의 부모 li 태그
            const childUl = parentLi.querySelector('ul'); // 자식 ul 태그

            // 자식 ul 태그가 있는 경우
            if (childUl) {
                const childCheckboxes = childUl.querySelectorAll('.toco-checkbox'); // 자식 체크박스들
                childCheckboxes.forEach(checkbox => {
                    checkbox.checked = target.checked; // 부모 체크박스 상태와 동일하게 설정
                });
            }
        }
    });

    // 체크박스 변경 이벤트 감지
    mytocoList.addEventListener('change', function (event) {
        const target = event.target;

        // 체크박스인지 확인
        if (target.classList.contains('mytoco-checkbox')) {
            const parentLi = target.closest('.nav-item'); // 현재 체크박스의 부모 li 태그
            const childUl = parentLi.querySelector('ul'); // 자식 ul 태그

            // 자식 ul 태그가 있는 경우
            if (childUl) {
                const childCheckboxes = childUl.querySelectorAll('.mytoco-checkbox'); // 자식 체크박스들
                childCheckboxes.forEach(checkbox => {
                    checkbox.checked = target.checked; // 부모 체크박스 상태와 동일하게 설정
                });
            }
        }
    });

});


let selectedFolderSeq = null; // 선택된 폴더의 seq를 저장할 변수
let selectedFolderElement = null; // 현재 선택된 폴더 요소를 저장할 변수
let buttonElement = null;

function selectFolder(seq) {

    // 기존에 선택된 폴더에서 'selected-folder' 클래스를 제거
    if (selectedFolderElement) {
        selectedFolderElement.classList.remove('selected-folder');
    }

    if (buttonElement) {
        buttonElement.classList.remove('selected-folder');
    }

    selectedFolderSeq = seq;
    $('#selected_seq').val(selectedFolderSeq);
    console.log("SelectedFolderSeq : ", selectedFolderSeq);

    // 새로운 폴더 선택
    selectedFolderElement = document.querySelector(`li[data-folder-seq="${seq}"]`);
    buttonElement = selectedFolderElement.querySelector('button');

    if (buttonElement) {
        buttonElement.classList.add('selected-folder'); // button에 'selected-folder' 클래스를 추가
    }

    // MYTO 목차에 해당 seq 값에 포함되어 있는 목차 보여주기
    selectTo('','MYTOCO', seq);
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
		    console.log("returnData", data.returnData);
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
    $(".myto-tree .mytoco_list").html("");  // 기존 리스트 초기화

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
    $(".myto-tree .mytoco_list").empty();
    $(".myto-tree .mytoco_list").append(resultDocument);

    // 2. CSS 적용
    updateMyTocoStyle();
    // 3. 버튼 이벤트
    bindMyTocoEvent();

}

async function createFolder(liId, liName) {
    console.log("선택된 폴더 : ", selectedFolderSeq);
    let mytoSeq = selectedFolderSeq;

    console.log("toKey : ", liId);
    let toKey = liId;

    try {
        if (mytoSeq !== null) {
            console.log("mytoSeq : ", mytoSeq);
            const request = await fetch(`/EXPIS/${bizCode}/ietm/selectMyTocoList.do?mytoSeq=${mytoSeq}`);
            const data = await request.json();

            // 선택된 폴더 안에 목차가 존재하면 알림 띄우고 종료
            if (data.success.trim() === "true") {
                alert('선택한 폴더 안에 목차가 등록되어 있어 중첩폴더를 만들 수 없습니다.');
                return;
            }
        }

        let type = 'TO';
        let mytoKind = '01';
        // 폴더 이름 name 속성값으로 변경
        let folderName = liName || prompt("폴더 이름을 입력하세요:");

        if (!folderName) return;

        if (liId) {
            if (confirm("교범키 이름으로 폴더를 만드시겠습니까?")) {
                type = 'System';
                mytoKind = '02';
            } else {
                folderName = prompt("폴더 이름을 입력하세요:");
                if (!folderName) return;
            }
        }

        // selectedFolderSeq가 null 또는 0이면 최상위 폴더로 설정
        let mytoPSeq = (mytoSeq && Number(mytoSeq) > 0) ? Number(mytoSeq) : 0;

        const requestData = {
            mytoName: folderName,
            mytoKind: mytoKind,
            myType: type,
            mytoPSeq: mytoPSeq, // 부모 seq로 선택된 폴더의 seq 전달
            toKey: toKey
        };

        // 폴더 생성 요청
        const folderResponse = await fetch(`/EXPIS/${bizCode}/ietm/insertMyFolder.do`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify( requestData )
        });

        const folderData = await folderResponse.json();

        if (folderData.success) {
            // 폴더 생성 후 목록 다시 로드
            loadMyFolder();
        } else {
            alert(folderData.message || "폴더 생성에 실패했습니다.");
        }

    } catch (error) {
        console.log('createFolder error : ', error);
        alert("폴더 생성 중 오류가 발생했습니다.");
    }
}

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
        },
        complete: function () {
            window.opener.loadMyFolder();
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
    const mytoList = document.querySelector('.myto_list');
    mytoList.innerHTML = '';

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
        folderName.textContent = `${folder.mytoName || folder.toKey}`;

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
                    folderIcon.classList.add('fa-folder-plus');
                } else {
                    subFolderList.classList.add('show'); // 보이기
                    folderIcon.classList.remove('fa-folder-plus');
                    folderIcon.classList.add('fa-folder-minus');
                }
            });
        }

        // 폴더 선택 이벤트 (selectFolder 함수 호출)
        folderButton.addEventListener('click', function (event) {
            if (!event.target.classList.contains('edit-icon') && !event.target.classList.contains('delete-icon')) {
                selectFolder(folder.mytoSeq); // 폴더 선택 함수 호출
            }
        });

        // 수정 아이콘 생성 및 이벤트 추가
        const editIcon = document.createElement('span');
        editIcon.classList.add('icon', 'edit-icon');
        editIcon.setAttribute('title', '수정');
        editIcon.innerHTML = '<i class="fas fa-pen"></i>';
        editIcon.addEventListener('click', function () {
            const newName = prompt("새 폴더 이름을 입력하세요:", folder.mytoName);
            if (newName && newName !== folder.mytoName) {
                updateMyFolder(folder.mytoSeq, newName); // 서버에 폴더 이름 업데이트 요청
            }
        });

        // 삭제 아이콘 생성 및 이벤트 추가
        const deleteIcon = document.createElement('span');
        deleteIcon.classList.add('icon', 'delete-icon');
        deleteIcon.setAttribute('title', '삭제');
        deleteIcon.innerHTML = '<i class="fas fa-trash"></i>';
        deleteIcon.addEventListener('click', function () {
            const hasChildren = folder.children && folder.children.length > 0 ? true : false; // 자식 폴더를 가지고 있는지
            confirmDeleteMyFolder(folder.mytoSeq, hasChildren);
        });

        // 버튼과 아이콘을 폴더에 추가
        newFolder.appendChild(folderButton);
        newFolder.appendChild(editIcon);
        newFolder.appendChild(deleteIcon);

        // 현재 폴더를 부모 요소에 추가
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

function updateMyFolder(folderSeq, newName) {
    fetch(`/EXPIS/${bizCode}/ietm/updateMyFolder.do`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ mytoSeq: folderSeq, mytoName: newName })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            loadMyFolder(); // 폴더 이름 업데이트 후 목록 다시 로드
        } else {
            alert(data.message || "폴더 이름 업데이트에 실패했습니다.");
        }
    })
    .catch(error => {
        console.log('Folder Update Error: ', error);
        alert("서버 통신 중 오류가 발생했습니다.");
    });
}

function confirmDeleteMyFolder(folderSeq, hasChildren) {
    console.log("folderSeq : ", folderSeq);
    console.log("hasChildren : ", hasChildren);

    let confirmMessage;
    // 자식폴더 있을 때
    if (hasChildren) {
        // 예
        if (confirm("선택한 폴더와 자식폴더를 모두 삭제하시겠습니까?")) {
            deleteMyFolder(folderSeq, true, true);
        // 아니오
        } else {
            if (confirm("선택한 폴더만 삭제하시겠습니까?")) {
                deleteMyFolder(folderSeq, true, false);
            }
        }
    // 자식폴더 없을 때
    } else {
        if (confirm("선택한 폴더를 삭제하시겠습니까?")) {
            deleteMyFolder(folderSeq, false, false);
        }
    }

}

function deleteMyFolder(folderSeq, hasChildren, deleteChildren) {
    console.log("folderSeq : ", folderSeq);
    console.log("hasChildren : ", hasChildren);
    console.log("deleteChildren : ", deleteChildren);

    const requestData = {
        mytoSeq: folderSeq,
        hasChildren: hasChildren,
        deleteChildren: deleteChildren
    }

    fetch(`/EXPIS/${bizCode}/ietm/deleteMyFolder.do`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ requestData })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            loadMyFolder();
            // id가 'myto-checkbox-all'인 체크박스의 체크 상태 해제
            const checkboxAll = document.getElementById('myto-checkbox-all');
            if (checkboxAll) {
                checkboxAll.checked = false;
            }
            const mytocoList = document.querySelector('.myto-tree .mytoco_list');
            mytocoList.innerHTML = ''; // 기존 목록 초기화
        } else {
            alert(data.message || "폴더 삭제에 실패했습니다.");
        }
    })
    .catch(error => {
        console.log('Folder Delete Error: ', error);
        alert("서버 통신 중 오류가 발생했습니다.");
    });
}

let isCheckboxAdded = false; // 체크박스 상태 관리
function toggleDeleteMode() {
    const listItems = document.querySelectorAll('.myto_list .nav-item');
    const selectedItems = document.querySelectorAll('.myto_list .nav-item input[type="checkbox"]:checked');

    if (isCheckboxAdded) {
        if (selectedItems.length === 0) {
            if (confirm("폴더 삭제를 취소하시겠습니까?")) {
                document.querySelectorAll('.myto-checkbox').forEach(checkbox => checkbox.remove());
                isCheckboxAdded = false;
            }
            return;
        }

        if (confirm('선택한 폴더를 삭제하시겠습니까?')) {
            selectedItems.forEach(checkbox => {
                const folderItem = checkbox.closest('.nav-item');
                const folderSeq = folderItem.getAttribute('data-folder-seq'); // 폴더의 ID 가져오기
                if (folderSeq) {
                    deleteMyFolder(folderSeq);
                }
            });

            // 체크박스 초기화
            document.querySelectorAll('.myto-checkbox').forEach(checkbox => checkbox.remove());
            isCheckboxAdded = false;
        }
    } else {
        // 체크박스 추가 모드
        listItems.forEach(item => {
            if (!item.querySelector('.myto-checkbox')) {
                const checkbox = document.createElement('input');
                checkbox.type = 'checkbox';
                checkbox.classList.add('myto-checkbox');
                item.prepend(checkbox);
            }
        });

        isCheckboxAdded = true;
        alert('삭제할 폴더를 선택해주세요.');
    }
}

function updateMyTocoStyle() {
    let _target = $(".myto-tree .mytoco_list");
    if (_target.length === 0) return;

    let _li = $("li", _target);
    if (_li.length === 0) return;

    // 모든 li 태그를 순회
    $('.myto-tree .mytoco_list li').each(function () {
        var $li = $(this);
        var $button = $li.find('> button');
        var $icon = $button.find('i');
        var $children = $li.find('> ul');

        // 체크박스가 이미 존재하는지 확인
        if ($li.find('.mytoco-checkbox').length === 0) {
            // 체크박스를 li 태그 옆에 추가
            var $checkbox = $('<input>', {
                type: 'checkbox',
                class: 'mytoco-checkbox', // 체크박스의 클래스
                id: 'mycheckbox_' + $li.attr('id'), // 고유 ID 추가
            });

            // li의 가장 앞에 체크박스를 추가
            $li.prepend($checkbox);
        }

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
                e.stopPropagation();
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

    let $target = $(".myto-tree .mytoco_list");
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
        });
    });
}

//TO 삭제
function delMyToco() {
    const checkedItems = document.querySelectorAll('.mytoco_list .mytoco-checkbox:checked');
    if (checkedItems.length === 0) {
        alert('삭제할 MYTO 목차를 선택해주세요.');
        return;
    }

    const delMyTocoIds = Array.from(checkedItems).map(item => {
        const li = item.closest('li');
        return li ? li.getAttribute('id') : null;
    }).filter(id => id !== null);

    const delMyTocoTokeys = Array.from(checkedItems).map(item => {
        const li = item.closest('li');
        return li ? li.getAttribute('tokey') : null;
    }).filter(tokey => tokey !== null);

    const mytoSeq = document.getElementById('selected_seq').value;

    const data = {
        paramTocoId: delMyTocoIds,
        paramToKey: delMyTocoTokeys,
        mytoSeq: mytoSeq
    }

    if (confirm('선택한 항목을 삭제하시겠습니까?')) {
        fetch(`/EXPIS/${bizCode}/ietm/deleteMyToco.do`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        })
        .then(response => response.json())
        .then(result => {
            if (result.success) {
                checkedItems.forEach(item => {
                    item.closest('.nav-item').remove();
                });
            } else {
                alert('TO 삭제 실패');
            }
        })
        .catch(error => {
            console.log('delMyToco Error:', error);
            alert("서버 통신 중 오류가 발생했습니다.");
        })
    }
}

function dupChkTokey(toKey) {
	var resultData = 0;
	$.ajax({
		type : "POST",
		url : "/EXPIS/"+bizCode+"/ietm/dupChkTokey.do",
		async: false,
		dataType : "json",
		data : "toKey=" + encodeURIComponent(toKey),
		success : function(result) {
			resultData = result.result;
		}
	});
	return resultData;
}

//TO 추가
function addMyToco() {
    if (selectedFolderSeq === null) {
        alert('목차를 추가할 폴더를 선택해주세요.');
        return;
    }

    const mytoSeq = selectedFolderSeq;
    // 2. 중복 체크 -> 이미 등록되어 있으면 건너 띄고 insert
    let dupResult = dupChkTokey();
    console.log("addMyToco dupResult", dupResult);

    tocoChkInsert();
}

//insert
function tocoChkInsert() {
	let paramTocoId = new Array();
	let paramTocoName = new Array();
	let paramPTocoId = new Array();
	let paramType = new Array();
	let paramOrd = new Array();
	let tocoId = "";
	let tocoName = "";
	let pTocoId = "";
	let type = "";
	let ord = 0;
    let toKey = $('.toco_list.tree ul li:first').attr('itemid'); // toKeyName(1A-50A-2-00FR-00-1)
    let toKeyId = $('.toco_list.tree ul li:first').attr('id');  // toKeyId(6B407E5627914f92AAE377DD6AAFC667)
    let mytoSeq = selectedFolderSeq;
	let param = "";

	const checkedItems = document.querySelectorAll('.toco_list .toco-checkbox:checked');
    checkedItems.forEach((item, index) => {
		if($(item).prop("checked") == true) {
			const li = item.closest('li.nav-item');
			tocoId = li.getAttribute('id');
			tocoName = li.getAttribute('name');
			// 부모 <li> 요소 찾기 (단, 부모가 체크된 폴더인 경우만 유효)
            let pTocoId = null;
            let parentLi = li.closest('ul').closest('li.nav-item'); // 가장 가까운 부모 <li>

            if (parentLi) {
                const parentCheckbox = parentLi.querySelector('.toco-checkbox'); // 부모 체크박스
//                if (parentCheckbox && parentCheckbox.checked) { // 부모 폴더가 체크되어 있으면
                    // 체크 안 되어 있어도 그냥 부모꺼 저장
                    pTocoId = parentLi.getAttribute('id') || toKeyId; // 부모 폴더의 ID를 pTocoId로 저장, 최상위 폴더이면 toKey 값 저장
//                }
            } else {
                pTocoId = toKeyId;
            }

			type = li.getAttribute('type');
			ord = li.getAttribute('ord');
			// 중복체크
			if(!$("#mycheckbox_" + tocoId).length) {
				paramTocoId.push(tocoId);
				paramTocoName.push(tocoName);
				paramPTocoId.push(pTocoId);
				paramType.push(type);
				paramOrd.push(ord);
			}
		}
	});

	if(paramTocoId.length == 0) {
		return;
	}

    if (mytoSeq == "") {
        alert("목차를 추가할 폴더를 선택해주세요.");
        return;
    }

	param = {"toKey" : toKey, "mytoSeq" : mytoSeq , "paramTocoId" : paramTocoId, "paramTocoName" : paramTocoName, "paramPTocoId" : paramPTocoId, "paramType" : paramType
			,"paramOrd" : paramOrd};
    console.log('tocoChkInsert param ', param);

	$.ajax({
		type : "POST",
		url : "/EXPIS/"+bizCode+"/ietm/insertMyToco.do",
		dataType : "json",
		data : param,
		contentType: "application/x-www-form-urlencoded; charset=UTF-8",
		success : function(data) {
		    alert("MYTO 목차 등록 완료");
		},
		error:function(request,status,error) {
			 alert("Error tocoChkInsert \n" + "code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
		},
		complete : function() {
			if($("#selected_seq").val() != "") {
				mytoSeq = $("#selected_seq").val();
				console.log("tocoChkInsert mytoSeq(선택된 폴더번호): ", mytoSeq);
				selectTo(toKey,'MYTOCO', mytoSeq);
			} else {
				$("#mytoco_list").html("");
			}
		 }
	});
}