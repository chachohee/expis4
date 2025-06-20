$.when( $.ready ).then(function(){
    //Document is ready

    //Accordion Menu
    bindMenuCollapse();

    //Menu Event
    //bindMenuEvent();

    //Update active BreadCrumb & Menu
    updateItem();

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
    data.menu2.addClass("active");
    data.menu2.find("i").removeClass("fa-minus").addClass("fa-plus");
}

