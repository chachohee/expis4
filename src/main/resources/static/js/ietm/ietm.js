let toKey = "";

$(document).ready(function () {
    if (typeof (showTo) === 'function') showTo();
    if (typeof (loadTree) === 'function') loadTree();
    if (typeof (menuBtnEvent) === 'function') menuBtnEvent(toKey);
    if (typeof (optionMain) === 'function') optionMain(false); // 페이지 로드 시에는 옵션 레이어 팝업 뜨지 않도록
    if (typeof (openSubSidebar) === 'function') openSubSidebar(); // 페이지 로드 시 서브 사이드바 초기 상태 설정 - 열린 상태
    if (typeof (closeSubVersion) === 'function') closeSubVersion(); // 페이지 로드 시 유효목록 상태 설정 - 닫힘
});

// 서버에서 받은 CL 교범 목록을 기준으로 체크 상태 반영
function chkCL(clList) {
    if (!Array.isArray(clList)) return;

    clList.forEach(entry => {
        const [, tocoId] = entry.split("_");
        const $checkbox = $("#" + tocoId);
        if ($checkbox.length) {
            if (!$checkbox.prop("checked")) {
                $checkbox.prop("checked", true);
            }
        }
    });

    const totalChecked = $('input[type="checkbox"]:checked:visible').length

    sessionStorage.setItem("chkCount", totalChecked);
}

// CL 교범 체크 상태를 서버에 저장
function countCL(checkbox){
    toKey = TOKEY;
    tocoId = checkbox.value;
    let chkVal = $("#" + tocoId).prop("checked");

    const totalChecked = $('input[type="checkbox"]:checked:visible').length

    sessionStorage.setItem("chkCount", totalChecked);

    $.ajax({
        type: "POST",
        data: {
            toKey: toKey,
            tocoId: tocoId,
            chkVal: chkVal,
        },
        dataType: "json",
        url: "/EXPIS/" + bizCode + "/ietm/checkCL.do",
        success: function (data){
            console.log("성공");
        },
        error: function (){
            console.warn("실패");
        }
    });
}

//교범 검색 기능
function toListSearch() {
    //MyTo 검색보류
    let searchWord = $("#to_search_word").val().trim();
    let foundCount = 0;
    let firstMatch = null;

    if (searchWord === "") {
        alert("교범명을 입력해주세요.");
        $("#to_search_word").focus();
        return;
    }

    $("#to_list a").removeClass("bg-warning fw-bold");

    // TO 트리 열기
    $("#to_list a").each(function () {
        const $link = $(this);
        const text = $link.text();

        if (text.indexOf(searchWord) > -1) {
            $link.addClass("bg-warning fw-bold");

            // 첫 번째 검색 요소 저장
            if(!firstMatch){
                firstMatch = $link;
            }

            foundCount++;

            // 상위 트리 열기
            $link.parents("ul.collapse").each(function () {
                const $childUl = $(this);
                const $li = $childUl.closest("li");

                if (!$childUl.hasClass("show")) {
                    toggleTree($li, $childUl);
                }
            });
        } else {
            $link.removeClass("bg-warning fw-bold");
        }
    });

    if (foundCount === 0) {
        alert("검색 결과가 없습니다.");
    }else if (firstMatch){
        //해당 검색 교범에 위치
        firstMatch.get(0).scrollIntoView({behavior: "smooth", block: "center"});
    }
}

//관련교범목록 기능
function siblingTO(toKey) {
    let siblingHtml = "";
    $("#sibling-to").empty();

    $.ajax({
        type: "POST",
        data: "toKey=" + encodeURIComponent(toKey),
        url: "/EXPIS/" + bizCode + "/ietm/selectSibling.do",
        dataType: "json",
        cache: false,
        success: function (data) {
            if (!data?.siblingList || data.siblingList?.length === 0) {
                alert('관련교범목록이 없습니다.');
            }

            siblingHtml += ``;
            data.siblingList.forEach(item => {
                const {statusKind, refUserId, refToKey} = item;

                siblingHtml += `
                            <li class="d-flex align-items-center px-2">
                                <i class="fa fa-book text-primary"></i>
                                <a class="dropdown-item flex-grow-1" onclick="selectTo('${refToKey}', 'toco')">
                                    ${refToKey}
                                </a>
                            </li>
            `;
            });

            $("#sibling-to").append(siblingHtml);
        }
    });
}

function subMenuEvent(menu) {
    //사이드 메뉴 이벤트
    // sideMenuClick();
    if (menu == "version") {
        openSubVersion()
    } else if (menu == "search") {
        openSubSearch();
    }
}

function menuBtnEvent(toKey) {
    let menuToKey = toKey;

    if (menuToKey == "") {
        $("#side_sub_version, #side_sub_search")
            .addClass("disabled")
            .attr("title", "교범을 등록해주세요.");

    } else {
        $("#side_sub_version, #side_sub_search")
            .removeClass("disabled")
            .attr("title", "");
    }
}

/* 좌측 메뉴 활성화 */
function sideMenuClick() {
    if (!event) return;

    // menu active
    let _btnAll = $("#sidebar ul li a");
    if (_btnAll.length > 0) {
        _btnAll.removeClass('active');
    }


    if (!event.currentTarget) return;
    let _me = $(event.currentTarget);
    if (_me.length == 0) return;
    _me.addClass('active');
}

/* 사이드바 목록 버튼 눌렀을 때 */
function showTo() {
    //사이드 메뉴 이벤트
    sideMenuClick();

    document.getElementById('to-buttons').classList.remove('d-none');
    document.getElementById('myto-buttons').classList.add('d-none');
    document.getElementById('to_list').classList.remove('d-none');
    document.getElementById('toco_list').classList.add('d-none');
    document.getElementById('myto_list').classList.add('d-none');
    document.getElementById('mytoco_list').classList.add('d-none');
    document.getElementById('select-sibling').classList.add('d-none');
}

/* 사이드바 MY T.O 버튼 눌렀을 때 */
function showMyTo() {
    //사이드 메뉴 이벤트
    sideMenuClick();

    document.getElementById('myto-buttons').classList.remove('d-none');
    document.getElementById('to-buttons').classList.add('d-none');
    document.getElementById('myto_list').classList.remove('d-none');
    document.getElementById('mytoco_list').classList.add('d-none');
    document.getElementById('toco_list').classList.add('d-none');
    document.getElementById('to_list').classList.add('d-none');
    document.getElementById('select-sibling').classList.add('d-none');
}

// TO 목록 클릭 이벤트
function toggleToActive(type) {
    const btnTo = document.getElementById('btn-to');
    const btnToco = document.getElementById('btn-toco');
    const toList = document.getElementById('to_list');
    const tocoList = document.getElementById('toco_list');
    const toSearch = document.getElementById('to-search');
    const selectSibling = document.getElementById('select-sibling');

    if (type === 'to') {
        // TO 버튼 활성화 스타일 적용
        btnTo.classList.add('active');

        // TOCO 버튼 비활성화 스타일 적용
        btnToco.classList.remove('active');

        // 목록 보이기
        toList.classList.remove('d-none');
        tocoList.classList.add('d-none');

        // TO 검색창 보이기
        toSearch.classList.remove('d-none');

        // 관련교범목록 숨기기
        selectSibling.classList.add('d-none');

    } else if (type === 'toco') {
        // TOCO 버튼 활성화 스타일 적용
        btnToco.classList.add('active');

        // TO 버튼 비활성화 스타일 적용
        btnTo.classList.remove('active');

        // 목차 보이기
        tocoList.classList.remove('d-none');
        toList.classList.add('d-none');

        // TO 검색창 숨기기
        toSearch.classList.add('d-none');

        // 관련교범목록 보이기
        selectSibling.classList.remove('d-none');
        // siblingTO();
    }
}

// My TO 클릭 이벤트
function toggleMyToActive(type) {
    const btnMyTo = document.getElementById('btn-myto');
    const btnMyToco = document.getElementById('btn-mytoco');
    const btnToManage = document.getElementById('btn-to-manage');
    const mytoList = document.getElementById('myto_list');
    const mytocoList = document.getElementById('mytoco_list');

    if (type === 'myto') {
        // MYTO 버튼 활성화 스타일 적용
        btnMyTo.classList.add('active');

        // 나머지 버튼 비활성화 스타일 적용
        btnMyToco.classList.remove('active');

        // 목록 보이기
        mytoList.classList.remove('d-none');
        mytocoList.classList.add('d-none');

    } else if (type === 'mytoco') {
        // MYTOCO 버튼 활성화 스타일 적용
        btnMyToco.classList.add('active');

        // 나머지 버튼 비활성화 스타일 적용
        btnMyTo.classList.remove('active');

        // 목차 보이기
        mytocoList.classList.remove('d-none');
        mytoList.classList.add('d-none');

    } else if (type === 'tomanage') {
        // TO 관리 버튼 활성화 스타일 적용


        // 나머지 버튼 비활성화 스타일 적용
        btnMyTo.classList.remove('active');
        btnMyToco.classList.remove('active');
    }
}


// MYTO 관리 팝업창
function openToManage() {
    let url = `/EXPIS/` + bizCode + `/ietm/myToPopup.do`;
    let popup = window.open(
        url,
        "MYTO 관리",
        "width=1000,height=600,scrollbars=yes,resizable=yes,minWidth=470,minHeight=600"
    );
    if (popup) {
        popup.focus();
        // 크기 제한을 적용하기 위해 resize 이벤트 추가
        popup.addEventListener('resize', function () {
            if (popup.outerWidth < 470) {
                popup.resizeTo(470, popup.outerHeight);
            }
            if (popup.outerHeight < 600) {
                popup.resizeTo(popup.outerWidth, 600);
            }
        });
    } else {
        alert("팝업창이 차단되었습니다. 팝업 허용 후 다시 시도해주세요.");
    }
}

/* 유효목록 열기 */
function openSubVersion() {
  console.log(TOKEY);
  let toKey = TOKEY;

  $("#avail_version_list").empty();
  $(".version_select_list").empty();

  $.ajax({
    type: "POST",
    url: "versionMain.do",
    data: "toKey=" + encodeURIComponent(toKey),
    dataType: "json",
    success: function (data) {
      var versionList = data.versionList;
      console.log("CALL availOpen versionList : " + versionList);
      var versionLength = versionList.length;
      var allBook = data.allBook;
      var toKey = data.toKey;
      var orgBook = data.orgBook;
      var contents = "";
      var selectbox_content = "";
      var ietm_view_all = "전체보기";
      var ietm_change_history = "변경이력";
      var ietm_count = "건";
      var ietm_original = "원판";
      var ietm_modified_version = "변경판";
      var ietm_basic_version = "기본판";
      if (versionLength == 0) {
        contents += "<li class='no_data' title='" + ietm_no_change_history + "'><div class='in_div'><br />" + ietm_no_change_history + "</div></li>";
      } else {
        for (var i = 0; i < versionLength; i++) {
          var list = versionList[i];
          var splitChgDate = [];
          console.log("list.chgNo : " + list.chgNo);
          if (list.chgNo == "0") {
            contents += "<li>";
            contents += "<a href='javascript:void(0);' title='" + ietm_view_all + "' onclick=\"subVersionDetail('" + allBook + "','" + list.verId + "','" + toKey + "','')\" class='allBook'>" + ietm_view_all + "</a>";
            contents += "</li>";
            contents += "<li>";
            contents += "<a href='javascript:void(0);' title='" + ietm_change_history + "' onclick=\"subVersionDetail('total','all','" + toKey + "','x')\" class='originalBook'>" + ietm_change_history + "</a>";
            contents += "</li>";
            contents += "<li>";
            contents += "<a href='javascript:void(0);' title='" + ietm_count + " - " + list.chgDate + "' class='originalBook'>";
            contents += "<span>" + ietm_basic_version + "</span>";
            contents += "<span>- " + list.chgNo + " -</span>";
            contents += "<span>" + list.chgDate + "</span>";
            contents += "</a>";
            contents += "</li>";
            selectbox_content += "<option id='version_select_list_' value='subVersionDetail(\"" + allBook + "\",\"" + list.verId + "\",\"" + toKey + "\",\"\")' onclick=\"subVersionDetail('" + allBook + "','" + list.verId + "','" + toKey + "','')\">";
            selectbox_content += "" + ietm_view_all + "";
            selectbox_content += "</option>";
            selectbox_content += "<option id='version_select_list_x' value='subVersionDetail(\"total\",\"all\",\"" + toKey + "\",\"x\")' onclick=\"subVersionDetail('total','all','" + toKey + "','x')\">";
            selectbox_content += "" + ietm_change_history + "";
            selectbox_content += "</option>";

          } else if (list.chgNo != "0") {
            contents += "<li>";
            contents += "<a href='javascript:void(0);' title='" + ietm_modified_version + " - " + list.chgDate + "' onclick=\"subVersionDetail('chg','" + list.verId + "','" + toKey + "','" + list.chgNo + "')\" class='changeBook'>";
            contents += "<span>" + ietm_modified_version + "</span>";
            contents += "<span>- " + list.chgNo + " -</span>";
            contents += "<span>" + list.chgDate + "</span>";
            contents += "</a>";
            contents += "</li>";
            selectbox_content += "<option id='version_select_list_" + list.chgNo + "' value='subVersionDetail(\"chg\",\"" + list.verId + "\",\"" + toKey + "\",\"" + list.chgNo + "\")' onclick=\"subVersionDetail('chg','" + list.verId + "','" + toKey + "','" + list.chgNo + "')\">";
            selectbox_content += "" + ietm_modified_version + list.chgNo + "";
            selectbox_content += "</option>";
          }
        }
      }

      $(".version_select_list").append(selectbox_content);
      $("#avail_version_list").append(contents);
      $("#avail_version_list").removeClass('d-none');
      $("#sub_search").addClass('d-none');
      $("#version_con_list").addClass('d-none');
      $("#sub_version").removeClass('d-none');
    }
  });
}

/* 유효목록 닫기 */
function closeSubVersion() {
  $("#sub_version").addClass('d-none');
  $("#version_list").addClass('d-none');
}

function subVersionDetail(book, verId, toKey, chgNo) {

  var param = "toKey=" + encodeURIComponent(toKey) + "&verId=" + verId + "&book=" + book;
  var ietmAll = "전체";
  var ietmCount = "건";
  $("#avail_detail_list").empty();
  $("#pageNum").empty();

  $.ajax({
    type: "POST",
    url: "versionDetailList.do",
    data: param,
    dataType: "json",
    success: function (data) {
      console.log(data);
      var detailList = data.detailList;
      var detailLength = detailList.length;
      var ntocoName = data.ntocoName;
      var totCnt = data.totCnt;
      var contents = "";
      var ietm_no_change_info = "변경판 정보가 없습니다.";
      $("#version_select_list_" + chgNo).attr("selected", "selected");

      if (totCnt > "0") {
        $("#pageNum").text("" + ietmAll + " : " + totCnt + "" + ietmCount + "");
        $("#pageNum").attr("title", "" + ietmAll + " :" + totCnt + "" + ietmCount + "");

      } else {
        $("#pageNum").text("" + ietmAll + " : 0" + ietmCount + "");
        $("#pageNum").attr("title", "" + ietmAll + " : 0" + ietmCount + "");
      }

      if (ntocoName == "0") {
        contents += "<tr>";
        contents += "<td align='center' title='" + ietm_no_change_info + "' colspan='2'>" + ietm_no_change_info + "</td>";
        contents += "</tr>";
      } else {
        for (var i = 0; i < detailLength; i++) {
          var list = detailList[i];
          contents += "<tr>";
          contents += "<td align='left' title='" + list.tocoName + "'>";
          contents += "<a href='javascript:void(0);' title='" + list.tocoName + "' onclick=\"tocoContView('" + list.tocoId + "');\">" + list.tocoName + "</a>";
          contents += "</td>";
          contents += "<td title=" + list.tocoChgNo + ">" + list.tocoChgNo + "</td>";
          contents += "</tr>";
        }
      }
      $("#avail_version_list").addClass('d-none');
      $("#version_con_list").removeClass('d-none');
      $("#avail_detail_list").append(contents);
      $(".version_con_list").scrollTop(0);
    }
  });
}

function openSubSearch() {
  $("#sub_version").addClass('d-none');
  $("#sub_search").removeClass('d-none');
  hoverEvent();
}

//교범있을때 메뉴 버튼 활성화 이벤트
function hoverEvent() {
    $("#top_search_selectbox").attr("onclick", "selectBoxEvent('top_search')");
  $("#search_selectbox").attr("onclick", "selectBoxEvent('search')");
}

function selectBoxEvent(param) {
  $('.select_box_form').slideUp("fast");
  if ($("#choice_" + param).css("display") == "none") {
    $("#choice_" + param).show();
  } else {
    $("#choice_" + param).slideUp("fast");
  }
  window.event.cancelBubble = true;
}

function changeSelVal(param, scCond, scSubCond, scTitle) {
  var changeHtml = "<a href='javascript:void(0);' onclick='selectBoxEvent(\"" + param + "\");' title='" + scTitle + "'>" + scTitle + "</a>";
  $("#" + param + "_selectbox").html(changeHtml);
  $("#search_cond").val(scCond);
  $("#search_sub_cond").val(scSubCond);
  $("#choice_" + param + "").slideUp("fast");
}

// 검색 클릭
function searchClickExe(param) {
  var searchWord = $("#" + param + "search_word").val();

  // // reset
  $(".result_comm_html").slideUp();
  $(".list_comm").removeClass("list_close");
  $(".list_comm").addClass("search_list_open");

  // 검증 잠깐 보류
  if (!searchValidation(param)) return false;

  let repWord = replaceXSS(searchWord);
  if (repWord != searchWord) {
    searchWord = repWord;
    $("#" + param + "search_word").val(searchWord);
  }

  if (param == "top_") {
    openSubSearch();
    searchExe(searchWord, true);
  } else {
    searchExe(searchWord, false);
  }

  syncSearchCriteria(param);
}

function syncSearchCriteria(param) {
  var isTop = param == "top_" ? true : false;

  var _toKey = $("#to_key");
  if (!!_toKey && _toKey.val() == "" && isTop) {
    console.log("Top Search : " + searchWord + ", isTop : " + isTop);
    var topTitle = $("#top_search_selectbox a").attr("title") || "";
    var fstTitle = $("#top_search_selectbox").parent().find("ul li a").eq(0).attr("title") || "";
    if (topTitle != "" && $.trim(topTitle) == $.trim(fstTitle)) {
      changeSelVal('search', '03', '', '부품정보');
      changeSelVal('top_search', '03', '', '부품정보');
    }
  }

  if (isTop == true) {
    if ($("#pup_search").css("display") == "none") {
      $("#pup_search").show();
    }
  }

  var _baseSel;
  var _baseInp;
  var baseTitle = "";
  var searchWord = "";
  var _targetSel;
  var _targetInp;
  var targetPara = "";
  if (isTop) {
    _baseSel = $("#top_search_selectbox a");
    searchWord = $("#top_search_word").val() || "";
    _targetSel = $("#search_selectbox");
    _targetInp = $("#search_word");
    targetPara = "search";
  } else {
    _baseSel = $("#search_selectbox a");
    searchWord = $("#search_word").val() || "";
    _targetSel = $("#top_search_selectbox");
    _targetInp = $("#top_search_word");
    targetPara = "top_search";
  }

  baseTitle = !!_baseSel.attr("title") ? _baseSel.attr("title") : baseTitle;
  if (baseTitle == "") return false;
  if (!_targetSel) return false;
  if (!!_targetInp && searchWord != "") {
    _targetInp.val(searchWord);
  }

  //ietm 검색에서 교범 등록안할시, 검색카테고리 볼수없도록 함
  var changeHtml = "<a href='javascript:void(0);' onclick='alertSearch()'>" + baseTitle + "</a>";
  _targetSel.html(changeHtml);

}

function alertSearch() {
  console.log(TOKEY);
  let toKey = TOKEY;
  // var toKey = $("#to_key").val() || "";
  if (toKey != "") return true;

  var ietm_select_manual_please = ietmSelectManualPlease;
  alert(ietm_select_manual_please);
}

function replaceXSS(searchWord) {
  var repWord = searchWord;
  repWord = repWord.replace(/\</gi, "&lt;");
  repWord = repWord.replace(/\>/gi, "&gt;");
  repWord = repWord.replace(/\(/gi, "&#40;");
  repWord = repWord.replace(/\)/gi, "&#41;");
  repWord = repWord.replace(/eval(.*)/gi, "");
  //repWord = repWord.replace(/[\"\'][\s]*javascript:(.*)[\"\']/gi, "\'\'");
  repWord = repWord.replace(/[\s]*javascript:(.*)/gi, "");
  repWord = repWord.replace(/script/gi, "");
  return repWord;
}


function searchValidation(param) {
  var searchCond = $("#search_cond").val();
  var ietm_enter_more_letters = ietmEnterMoreLetters || "";
  var ietm_select_manual_toc = ietmSelectManualToc || "";
  var ietm_comma_not_used = ietmCommaNotUsed || "";
  var ietm_enter_more_limit = ietmEnterMoreLimit || "";

  var _searchWord = $("#" + param + "search_word");
  if (!_searchWord || $.trim(_searchWord.val()) == "") {
    var ietm_enter_search_term = ietmEnterSearchTerm || "";
    if (ietm_enter_search_term != "") alert(ietm_enter_search_term);
    _searchWord.val("");
    return false;
  }

  if ($.trim(_searchWord.val()).length < 2) {
    if (ietm_enter_more_letters != "") alert(ietm_enter_more_letters);
    return false;
  }

  var _toKey = $("#to_key");
  if (!_toKey || _toKey.val() == "" && param == "") {
    if (ietm_select_manual_toc != "") alert(ietm_select_manual_toc);
    return false;
  }


  var chkVal = $("input[type=radio][name=search_radio]:checked").val() || "";
  var strArray = _searchWord.val().split(',');
  if (chkVal != "none" && strArray.length > 2) {
    if (ietm_enter_more_limit != "") alert(ietm_enter_more_limit);
    return false;
  }
  if (chkVal != "none" && strArray.length < 2) {
    if (ietm_enter_more_letters != "") alert(ietm_enter_more_letters);
    return false;
  }
  if (chkVal == "none") {
    if (_searchWord.val().indexOf(",") != -1) {
      if (ietm_comma_not_used != "") alert(ietm_comma_not_used);
      return false;
    }

  }

  return true;
}

function showResult(param, size) {
  var spanId = "#result_" + param + "_span";
  var htmlId = "#result_" + param + "_html";

  $(".result_comm_html").slideUp();
  $(".list_comm").removeClass("list_close");
  $(".list_comm").addClass("search_list_open");
//	$(htmlId).css("max-height", "315px");
  if ($(htmlId).css("display") == "none") {
//		var ulSize = $(htmlId).css("max-height");
//		ulSize = ulSize.slice(0, -2);
//		$(htmlId).css("max-height",Number(ulSize) - size);
    $(spanId).removeClass("search_list_open");
    $(spanId).addClass("list_close");
    $(htmlId).slideDown();
  }
}

function searchExe(searchWord, isTop) {
  var toKey = TOKEY;
  var searchCond = $("#search_cond").val();
  var searchSubCond = "";
  var searchWordArray = [];
  var strArray = [];
  var searchArray = [];
  var searchFirst = "";
  var searchSecond = "";
  strArray = searchWord.split(',');

  if (toKey.length == 0 && isTop) {
    toKey = "top";
    searchCond = "03";
  }

  var chkVal = $("input[type=radio][name=search_radio]:checked").val();
  for (var i = 0; i < strArray.length; i++) {

    if (strArray[i] == "") {
      break;
    } else {
      searchArray.push(strArray[i]);
    }
  }
  if (bizCode == "KICC") {
    if (chkVal != "none") {
      searchArray.push(searchWord);
    }
  } else {
    if (chkVal == "none") {
      searchArray.push(searchWord);
    }
  }

  if (searchCond != "01" && searchCond != "02") {
    toKey = "";
  }

  var tempVehicleType = "";
  if ($("#vehicle_type")) {
    tempVehicleType = $("#vehicle_type").val();
  }
  var tempVehicleType = "B"; //임시 하드코딩
  console.log("searchCond : " + searchCond + ", searchSubCond : " + searchSubCond + ", tempVehicleType : " + tempVehicleType);
  // $(".loading").show();

  $.ajax({
    type: "POST",
    data: "toKey=" + encodeURIComponent(toKey) + "&searchCond=" + searchCond + "&searchSubCond=" + searchSubCond + "&searchArray=" + searchArray + "&chkVal=" + chkVal + "&vehicleType=" + tempVehicleType,
    url: "searchList.do",
    datatype: "json",
    success: function (data) {
      $(".result_comm_html").empty();
      if (data.searchList != null) {
        searchResult(searchCond, searchWord, data.searchList);
      }
    },
    error: function (e) {
      alert("Search Failed\n" + e);
      $(".loading").hide();
    },
    complete: function (e) {
      $(".opacity_search").stop();
      $(".opacity_search").hide();
      $(".search_list_ul").show();
    }
  });

}

function searchResult(searchCond, searchWord, resultList) {
  var dataSize = resultList.length;
  var resultKind = ["toco", "cont", "graph", "table", "alert", "ipb", "fi", "wc", "wuc", "dic"];
  var resultKindSize = resultKind.length;
  var resultCnt = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
  var beforeResultType = "";
  var beforeResultToKey = "";
  var resultHtml = "";
  var textVal = "";
  var searchSecond = $("#search_sub_cond").val();
  var nameCnt = 0;
  var noCnt = 0;
  var nsnCnt = 0;
  var rdnCnt = 0;
  var cageCnt = 0;
  var stdCnt = 0;
  var ipbwucCnt = 0;
  var cardNoCnt = 0;
  var cardContCnt = 0;
  var wucCodeCnt = 0;
  var wucNameCnt = 0;
  var ietm_card_num = $("#ietm_card_num").val();
  var ietm_card_cont = $("#ietm_card_cont").val();
  var ietm_work_cards = $("#ietm_work.cards").val();

  $(".result_li").hide();

  if (searchCond == "01" || searchCond == "02") {
    $(".comm_class").show();

  } else if (searchCond == "03") {
    $("#result_ipb_text").text(textVal);
    $("#result_part_name_li").show();
    $("#result_part_no_li").show();
    $("#result_nsn_li").show();
    $("#result_rdn_li").show();
    $("#result_cage_li").show();

    if ($("#bizCode").val() == "KTA") {
      $("#result_rdn_li").hide();
      $("#result_cage_li").hide();
    }
    if ($("#bizCode").val() == "BLOCK2") {
      $("#result_std_li").show();
      $("#result_ipbwuc_li").show();
    }

  } else if (searchCond == "04") {
    $("#result_fi_li").show();

  } else if (searchCond == "05") {
    if (searchSecond == "01") {
      textVal = ietm_card_num;
    } else if (searchSecond == "02") {
      textVal = ietm_card_cont;
    } else {
      textVal = ietm_work_cards;
    }
    $("#result_wc_text").text(textVal);
    $("#result_card_no_li").show();
    $("#result_wc_xcont_li").show();

  } else if (searchCond == "06") {
    $("#result_wuc_text").text(textVal);
    $("#result_wuc_code_li").show();
    $("#result_wuc_name_li").show();

  } else if (searchCond == "07") {
    $("#result_dic_li").show();
  }

  for (var i = 0; i < resultKindSize; i++) {
    $("#result_" + resultKind[i] + "_html>li").remove();
    $("#result_" + resultKind[i] + "_html").css("display", "none");
    $("#result_" + resultKind[i] + "_span").removeClass("list_close");
    $("#result_" + resultKind[i] + "_span").addClass("search_list_open");
  }
//	result_ipb_li
  if (dataSize != 0) {
    var tempCheckToKey1 = "";
    var tempCheckToKey2 = "";
    var tempCheckToKey3 = "";
    var tempCheckToKey4 = "";
    var tempCheckToKey5 = "";
    var tempCheckToKey6 = "";
    var tempCheckToKey7 = "";
    var tempToKeyWucName = "";
    var tempToKeyWucCode = "";
    for (var i = 0; i < dataSize; i++) {
      // 용어검색의 경우 다르게 그려줘야 함
      if (searchCond != 07) {
        var toKey = resultList[i].toKey;
        var tocoId = resultList[i].tocoId;
        var vcKind = "01";
        var resultType = resultList[i].resultKind;
        var cont = resultList[i].cont;
        var pTocoId = resultList[i].pTocoId;
        var hasFirst = resultList[i].hasFirst;
        var hasSecond = resultList[i].hasSecond;
        var hasThird = resultList[i].hasThird;
        var hasFourth = resultList[i].hasFourth;
        var hasFifth = resultList[i].hasFifth;
        var hasSixth = resultList[i].hasSixth;
        var hasSeventh = resultList[i].hasSeventh;
        var rlHtml = "";

        if (resultType != beforeResultType || toKey != beforeResultToKey) {
          resultHtml = "<li class='list_2nd_dep'><a href='javascript:void(0);' title='TO Title'>" + toKey + "</a></li>";
          beforeResultType = resultType;
          beforeResultToKey = toKey;
        }

        if (cont != "" && $("#" + tocoId).attr("onclick") != "") {//Type 체크해서 안보이게 할경우
          //if(cont != "" ) {//Type 상관 없이 전체 검색
          // 검색결과 종류 : 01-목차, 02-본문, 03-그림, 04-표, 05-경고, 06-부품, 07-결함, 08-작업카드, 09-작업단위부호, 10-용어
          if (resultType == "01") {
            resultHtml += "<li><a href='javascript:void(0)' title=" + cont + " onclick=\"tocoContView('" + tocoId + "')\">" + cont + "</a></li>";
            $("#result_" + resultKind[0] + "_html").append(resultHtml);
            resultCnt[0] += 1;

          } else if (resultType == "02") {
            resultHtml += "<li><a href='javascript:void(0)' title=" + cont + " onclick=\"tocoContView('" + tocoId + "')\">" + cont + "</a></li>";
            $("#result_" + resultKind[1] + "_html").append(resultHtml);
            resultCnt[1] += 1;

          } else if (resultType == "03") {
            resultHtml += "<li><a class='icon_graph' title=" + cont + " href='javascript:void(0)' onclick=\"tocoContView('" + tocoId + "')\">" + cont + "</a></li>";
            $("#result_" + resultKind[2] + "_html").append(resultHtml);
            resultCnt[2] += 1;

          } else if (resultType == "04") {
            resultHtml += "<li><a class='icon_table' title=" + cont + " href='javascript:void(0)' onclick=\"tocoContView('" + tocoId + "')\">" + cont + "</a></li>";
            $("#result_" + resultKind[3] + "_html").append(resultHtml);
            resultCnt[3] += 1;

          } else if (resultType == "05") {
            resultHtml += "<li><a href='javascript:void(0)' title=" + cont + " onclick=\"tocoContView('" + tocoId + "')\">" + cont + "</a></li>";
            $("#result_" + resultKind[4] + "_html").append(resultHtml);
            resultCnt[4] += 1;
          } else if (resultType == "06") {
            if (hasFirst == "part_name") {
              if (tempCheckToKey1 == "") {
                console.log("First : " + toKey);
                rlHtml += "<li>" + toKey + "</li>";
                tempCheckToKey1 = toKey;
              } else if (tempCheckToKey1 != toKey) {
                console.log("Set tempCheckToKey : " + tempCheckToKey1 + ", toKey : " + toKey);
                rlHtml += "<li>" + toKey + "</li>";
                tempCheckToKey1 = toKey;
              }
            } else if (hasSecond == "part_no") {
              if (tempCheckToKey2 == "") {
                console.log("Second : " + toKey);
                rlHtml += "<li>" + toKey + "</li>";
                tempCheckToKey2 = toKey;
              } else if (tempCheckToKey2 != toKey) {
                console.log("Set tempCheckToKey : " + tempCheckToKey2 + ", toKey : " + toKey);
                rlHtml += "<li>" + toKey + "</li>";
                tempCheckToKey2 = toKey;
              }
            } else if (hasThird == "nsn") {
              if (tempCheckToKey3 == "") {
                console.log("Third : " + toKey);
                rlHtml += "<li>" + toKey + "</li>";
                tempCheckToKey3 = toKey;
              } else if (tempCheckToKey3 != toKey) {
                console.log("Set tempCheckToKey : " + tempCheckToKey3 + ", toKey : " + toKey);
                rlHtml += "<li>" + toKey + "</li>";
                tempCheckToKey3 = toKey;
              }
            } else if (hasFourth == "rdn") {
              if (tempCheckToKey4 == "") {
                console.log("Fourth : " + toKey);
                rlHtml += "<li>" + toKey + "</li>";
                tempCheckToKey4 = toKey;
              } else if (tempCheckToKey4 != toKey) {
                console.log("Set tempCheckToKey : " + tempCheckToKey4 + ", toKey : " + toKey);
                rlHtml += "<li>" + toKey + "</li>";
                tempCheckToKey4 = toKey;
              }
            } else if (hasFifth == "cage") {
              if (tempCheckToKey5 == "") {
                console.log("Fifth : " + toKey);
                rlHtml += "<li>" + toKey + "</li>";
                tempCheckToKey5 = toKey;
              } else if (tempCheckToKey5 != toKey) {
                console.log("Set tempCheckToKey : " + tempCheckToKey5 + ", toKey : " + toKey);
                rlHtml += "<li>" + toKey + "</li>";
                tempCheckToKey5 = toKey;
              }
            } else if (hasSixth == "std_mngt") {
              if (tempCheckToKey6 == "") {
                console.log("Sixth : " + toKey);
                rlHtml += "<li>" + toKey + "</li>";
                tempCheckToKey6 = toKey;
              } else if (tempCheckToKey6 != toKey) {
                console.log("Set tempCheckToKey : " + tempCheckToKey6 + ", toKey : " + toKey);
                rlHtml += "<li>" + toKey + "</li>";
                tempCheckToKey6 = toKey;
              }
            } else if (hasSeventh == "wuc") {
              if (tempCheckToKey7 == "" || tempCheckToKey7 != toKey) {
                console.log("Set tempCheckToKey : " + tempCheckToKey7 + ", toKey : " + toKey);
                rlHtml += "<li>" + toKey + "</li>";
                tempCheckToKey7 = toKey;
              }
            }
            rlHtml += "<li><a href='javascript:void(0)' title=" + cont
                + " onclick=\"viewToContents('" + toKey + "', '" + tocoId + "', '" + searchWord + "', '05', '', 'N')\">" + cont + "</a></li>";
            if (hasFirst == "part_name") {
              nameCnt++;
              $("#result_" + hasFirst + "_html").append(rlHtml);
            } else if (hasSecond == "part_no") {
              noCnt++;
              //$("#result_" + hasSecond + "_html").append("</ul>");
              $("#result_" + hasSecond + "_html").append(rlHtml);
            } else if (hasThird == "nsn") {
              nsnCnt++;
              //$("#result_" + hasThird + "_html").append("</ul>");
              $("#result_" + hasThird + "_html").append(rlHtml);
            } else if (hasFourth == "rdn") {
              rdnCnt++;
              //$("#result_" + hasFourth + "_html").append("</ul>");
              $("#result_" + hasFourth + "_html").append(rlHtml);
            } else if (hasFifth == "cage") {
              cageCnt++
              $("#result_" + hasFifth + "_html").append(rlHtml);
            } else if (hasSixth == "std_mngt") {
              stdCnt++;
              $("#result_std_html").append(rlHtml);
            } else if (hasSeventh == "wuc") {
              ipbwucCnt++;
              $("#result_ipbwuc_html").append(rlHtml);
            }
          } else if (resultType == "07") {
            resultHtml += "<li><a href='javascript:void(0)' title=" + cont + " onclick=\"tocoContView('" + tocoId + "')\">" + cont + "</a></li>";
            $("#result_" + resultKind[6] + "_html").append(resultHtml);
            resultCnt[6] += 1;

          } else if (resultType == "08") {
            rlHtml += "<li><a href='javascript:void(0)' title=" + cont + " " +
                "onclick=\"tocoContView('" + toKey + "', '" + tocoId + "', '" + searchWord + "', '06')\">" + cont + "</a></li>";

            if (hasFirst == "card_no") {
              if (tempCheckToKey1 != toKey) {
                $("#result_" + hasFirst + "_html").append("<li>" + toKey + "</li>");
                tempCheckToKey1 = toKey;
              }
              cardNoCnt++;
              $("#result_" + hasFirst + "_html").append(rlHtml);
            } else if (hasSecond == "wc_xcont") {
              if (tempCheckToKey2 != toKey) {
                $("#result_" + hasSecond + "_html").append("<li>" + toKey + "</li>");
                tempCheckToKey2 = toKey;
              }
              cardContCnt++;
              $("#result_" + hasSecond + "_html").append(rlHtml);
            }

          } else if (resultType == "09") {

            rlHtml += "<li><a href='javascript:void(0)' title=" + cont + "" +
                " onclick=\"tocoContView('" + tocoId + "')\">" + cont + "</a></li>";

            if (hasFirst == "wuc_code") {
              if (tempToKeyWucCode != toKey) {
                tempToKeyWucCode = toKey;
                $("#result_" + hasFirst + "_html").append("<li>" + toKey + "</li>");
              }
              wucCodeCnt++;
              $("#result_" + hasFirst + "_html").append(rlHtml);
            } else if (hasSecond == "wuc_name") {
              if (tempToKeyWucName != toKey) {
                tempToKeyWucName = toKey;
                $("#result_" + hasSecond + "_html").append("<li>" + toKey + "</li>");
              }
              wucNameCnt++;
              $("#result_" + hasSecond + "_html").append(rlHtml);
            }
          }
        }
      } else {
        resultHtml += "<li>";

        if (resultList[i].glsAbbr != null && resultList[i].glsAbbr != "") {
          resultHtml += "<span class='bl_key_tit'>" + resultList[i].glsAbbr + "</span>";
          resultHtml += "<p>" + resultList[i].glsDesc + "</p>";

        } else {
          resultHtml += "<p class='bl_key_tit'>" + resultList[i].glsDesc + "</p>";
        }

        resultHtml += "</li>";

        $("#result_" + resultKind[9] + "_html").append(resultHtml);
        resultCnt[9] += 1;
      }
      resultHtml = "";
    }
  }

  $("#result_part_name_cnt").text(nameCnt);
  $("#result_part_no_cnt").text(noCnt);
  $("#result_nsn_cnt").text(nsnCnt);
  $("#result_rdn_cnt").text(rdnCnt);
  $("#result_cage_cnt").text(cageCnt);
  $("#result_std_cnt").text(stdCnt);
  $("#result_ipbwuc_cnt").text(ipbwucCnt);
  $("#result_card_no_cnt").text(cardNoCnt);
  $("#result_wc_xcont_cnt").text(cardContCnt);
  $("#result_wuc_code_cnt").text(wucCodeCnt);
  $("#result_wuc_name_cnt").text(wucNameCnt);

  for (var i = 0; i < resultKindSize; i++) {
    $("#result_" + resultKind[i] + "_cnt").text(resultCnt[i]);
  }
}

function closeSubSearch() {
  $("#sub_search").addClass('d-none');
}

// 검색창 엔터
function searchEnter(param) {
  if (event.keyCode == 13) {
    searchClickExe(param);
  }
}

function onceFocusing(param) {
  if ($("#" + param + "search_word").focus()) {
    if ($("#pup_bookmark").css("display") != "none") {
      $("#pup_bookmark").hide();
    }
  }
}


/* 서브 사이드바 열기 */
function openSubSidebar() {
  $(".subSidebar").css({
    width: "100%",
  });

  $("#sub_search").addClass('d-none'); //좌측 검색창 닫기
  $("#sub_version").addClass('d-none'); //유효목록 닫기
  $("#rightButton").hide();
  $("#leftButton").show();
}


/* 서브 사이드바 닫기 */
function closeSubSidebar() {
  $(".subSidebar").css({
    width: "0%",
  });

  $("#leftButton").hide();
  $("#rightButton").show();
}
