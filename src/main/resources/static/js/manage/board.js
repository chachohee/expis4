$.when( $.ready ).then(function(){
    //Document is ready

    //Accordion Menu
    bindMenuCollapse();

    //Menu Event
    //bindMenuEvent();

    //Update active BreadCrumb & Menu
    updateItem();

    //Button Event
    bindButtonEvent();

});

//좌측메뉴 접기 펼치기
//TODO: 서브메뉴 배경효과 추가
function bindMenuCollapse() {
    var _allMenu = $("#menulist a.nav-link[data-bs-target]");
    if (_allMenu.length == 0) return;
    _allMenu.on("click", function(event){
        //reset
        _allMenu.removeClass("active");
        $(this).addClass("active");
        _allMenu.next().collapse("hide");

        let mySelector = $(this).attr("data-bs-target") || "";
        if ( mySelector === "" ) return true;
        var _btn = $(mySelector);
        if ( _btn.length == 0 ) return true;
        _btn.collapse("toggle");
    });
}

//좌측메뉴 이벤트 처리 - 사용안함
function bindMenuEvent() {
    var _allItem = $("#menulist a.nav-link[data-role]");
    if (_allItem.length == 0) return;
    _allItem.on("click", function(event){
        //reset
        _allItem.removeClass("sel");
        $(this).addClass("sel");
        _allItem.find("i").removeClass("fa-plus").addClass("fa-minus");

        let icon = $(this).find("i");
        if ( icon.length > 0 ) {
            icon.removeClass("fa-minus").addClass("fa-plus");
        }
    });
}

//버튼 이벤트
function bindButtonEvent() {

    //TO관리
    let _btnTomngUpload = $("#btn-tomng-upload");
    if ( _btnTomngUpload.length > 0 ) {
        _btnTomngUpload.on("click", function() {
            insertSystree();
        });
    }

}

//메뉴 업데이트
function updateItem() {
    let firstId = $("input[name='firstId']").val() || "";
    let secondId = $("input[name='secondId']").val() || "";

    let _firstMenu;
    if ( firstId === "" ) {
        _firstMenu = $("div.subButton", "#menulist").first();
    } else {
        _firstMenu = $("#"+firstId, "#menulist");
    }
    let _parentMenu = $("a[data-bs-target='#"+_firstMenu.attr("id")+"']");

    let _secondMenu;
    if ( secondId === "" ) {
        _secondMenu = $("a.nav-link[data-role]", _firstMenu).first();
    } else {
        _secondMenu = $("a.nav-link[data-role='"+secondId+"']", _firstMenu);
    }

    let data = {label1:_parentMenu, menu1:_firstMenu, menu2:_secondMenu};
    activeBreadCrumb(data);
    activeMenu(data);

}

//상단 우측 빵 부스러기 업데이트
//TODO: 링크도 추가
function activeBreadCrumb( data ) {
    var _headerLabel = $("#headerLabel");
    if (_headerLabel.length == 0) return;
    var _headerBreadcrumb = $("#headerBreadcrumb");
    if (_headerBreadcrumb.length == 0) return;

    if ( data.label1.length == 0 ) return;
    if ( data.menu1.length == 0 ) return;
    if ( data.menu2.length == 0 ) return;

    let parentLabel = data.label1.find("label").text() || "";
    let selLabel = data.menu2.find("label").text() || "";
    _headerLabel.text(selLabel);

    let breadLi = _headerBreadcrumb.find("li");
    breadLi.eq(1).find("label").text(parentLabel);
    breadLi.eq(2).find("label").text(selLabel);
}

//선택된 메뉴 Style 업데이트
function activeMenu( data ) {
    if ( data.label1.length == 0 ) return;
    if ( data.menu1.length == 0 ) return;
    if ( data.menu2.length == 0 ) return;

    data.label1.addClass("active");
    data.menu1.addClass("show");
    data.menu2.find("i").removeClass("fa-minus").addClass("fa-plus");
}


//TO관리 - SysTree 파일 등록
function insertSystree() {
    let filePath = $("#sys_file_upload").val() || "";
    filePath = filePath.replace(/\\/g, "/");
    let fileName = filePath.substring(filePath.lastIndexOf("/")+1, filePath.length);

    if (fileName.toLowerCase() != "systree.xml") {
        //alert("파일이 적합하지 않습니다. 정확한 계통트리 파일을 업로드해주세요.");
        let msgInvalid = $("input[name='msg-invalid-upload']").val() || "";
        alert(msgInvalid);
        return false;
    } else {
        //계통트리(System Tree)를 등록하시겠습니까?
        let msgRegister = $("input[name='msg-systree-register']").val() || "";
        if (confirm(msgRegister)) {
            postSysTree($("#sys_file_upload"));
        }
    }
}

//post systree.xml
function postSysTree(_file) {
    let bizCode = $("input[name='bizCode']").val() || "";
    if (bizCode === "") return false;

    let url = "/manage/"+ bizCode +"/systreeInsert.do";
    let formData = new FormData();
    let file = _file[0].files[0];
    formData.append("file", file);

    $.ajax({
        url: url,
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,
        success: function(response){
            alert('File upload Successfully');
        },
        error: function(xth, status, error){
            alert("Failure upload file");
        }
    });
}


//게시판 상세
function selectBoardList() {
    let boardMSeq = $("#aciveBoardMSeq").val();
    let statusKind = $("#hidden_status_kind").val();
    let bizCode = $("input[name='bizCode']").val();

    if(boardMSeq === "") {
        return;
    }

    location.href = `/EXPIS/${bizCode}/manage/activateBoard.do?boardMSeq=${boardMSeq}&statusKind=${statusKind}`;
}

//관련사이트 삭제
function relatedSitesDelete() {
    let fileSeq = document.getElementById('fileSeqDelete').value;
    let relSeq = document.getElementById('relSeqDelete').value;
    let bizCode = $("input[name='bizCode']").val();

    // URL에 데이터 추가
    let url = `/EXPIS/${bizCode}/manage/relatedDelete.do?relSeq=${relSeq}&fileSeq=${fileSeq}`;

    fetch(url, {
        method: 'GET', // GET 요청
        headers: {
            'X-Requested-With': 'XMLHttpRequest' // AJAX 요청임을 명시
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            if (data.status === "success") {
                alert(data.message);
                // 필요 시 UI 갱신 또는 페이지 이동
                location.href = `/EXPIS/${bizCode}/manage/relatedSites.do?`;
            } else {
                alert(data.message);
            }
        })
        .catch(error => {
            // 오류 처리
            alert("삭제 중 오류가 발생했습니다.");
            console.error("Error:", error);
        });
}

function indexUpdate(button) {
    const direction = button.classList.contains('up') ? 'up' : 'down';
    const currentRow = button.closest('tr');
    const currentSeq = button.dataset.seq;
    const currentOrder = button.dataset.order;
    let bizCode = $("input[name='bizCode']").val();

    let targetRow, targetSeq, targetOrder;

    if (direction === 'up') {
        // 위로 이동
        targetRow = currentRow.previousElementSibling;
        if (!targetRow) {
            alert('더 이상 위로 이동할 수 없습니다.');
            return;
        }
    } else {
        // 아래로 이동
        targetRow = currentRow.nextElementSibling;
        if (!targetRow) {
            alert('더 이상 아래로 이동할 수 없습니다.');
            return;
        }
    }

    // 이동 대상 행의 데이터
    targetSeq = targetRow.querySelector('button.up').dataset.seq; // 위/아래 행의 mSeq
    targetOrder = targetRow.querySelector('button.up').dataset.order; // 위/아래 행의 nNum

    // 서버에서 기대하는 데이터 구조 맞춤
    const params = new URLSearchParams({
        nNum: currentOrder,
        updateNum: targetOrder,

        mSeq: currentSeq,
        updateMSeq: targetSeq,

    }).toString();

    fetch(`/EXPIS/${bizCode}/manage/indexUpdate.do?${params}`, {
        method: 'GET'
    })
        .then(() => {
            location.reload();
        })
        .catch(err => {
            console.error('Error:', err);
            alert('순서 변경 중 오류가 발생했습니다.');
        });
}
