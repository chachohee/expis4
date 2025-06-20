console.log("call contPopup.js");

/* 팝업 오픈 */
function openPopup(params) {
    let {toKey, grphId, tocoId, system, tblId, contId, verId, verStatus, changebasis} = params || {};
    if (!toKey) toKey = TOKEY;

    const urlParams = new URLSearchParams();
    const paramMap = {toKey, grphId, tocoId, system, tblId, contId, verId, verStatus, changebasis};
    for (const [key, value] of Object.entries(paramMap)) {
        if (value) urlParams.append(key, value);
    }

    const features = "width=600px, height=600px, left=500, top=100, toolbar=no, menubar=no resizable=yes";
    const popup = window.open(`/EXPIS/${bizCode}/ietm/contPopup.do?${urlParams}`, "View To", features);

    if (!popup) {
        alert("팝업 허용 후 다시 시도해주세요.");
        return;
    }

    popup.focus();
}
/* --- */

/* 팝업 오픈 시 실행되는 함수 */
window.addEventListener("DOMContentLoaded", function () {
    console.log("toKey:", TOKEY, "verId:", verId, "contId:", contId, "verStatus:", verStatus, "tocoId:", tocoId);
    if (grphId) popupData({grphId: grphId});               // 이미지
    if (tocoId) tocoContView(tocoId);                             // 해당목차 이동용
    if (system) popupData({toKey: TOKEY, system: system}); // WC 테이블
    if (tblId) popupData({tblId: tblId});                  // 테이블
    if (contId && verId && verStatus) popupData({toKey: TOKEY, contId, verId, verStatus, changebasis}); // 버전바

    // 렌더링 이후 팝업 사이즈 조절
    setTimeout(adjustPopupSize, 100);
});
/* --- */

/* 팝업 사이즈 조절 */
function adjustPopupSize() {
    const content = document.getElementById("popup_div") || document.getElementById("popup_content");
    const width = content.scrollWidth + 50;
    const height = content.scrollHeight + 100;
    const maxWidth = screen.availWidth - 50;
    const maxHeight = screen.availHeight - 50;

    window.resizeTo(
        Math.min(width, maxWidth),
        Math.min(height, maxHeight)
    );
}
/* --- */

/* 팝업 데이터 */
function popupData(params) {
    const {toKey, grphId, tblId, system, contId, verId, verStatus, changebasis} = params || {};
    const data = {
        toKey: toKey,
        grphId: grphId,
        tblId: tblId,
        system: system,
        contId: contId,
        verId: verId,
        verStatus: verStatus,
        changebasis: changebasis,
    };

    $.ajax({
        type: "GET",
        url: `/EXPIS/${bizCode}/ietm/contOpener.do`,
        data: data,

        success: function (data) {
            if (params.system) { // WC 테이블
                renderWCTable(data.returnData)

            } else if (params.grphId) { // 이미지
                let xmlData = processXmlData(data);
                $(".main-content").html(xmlData);

                if (data.tocoLink) { // 해당목차 이동용
                    $(".main-content").append(data.tocoLink);
                }

            } else if (params.contId && params.verId && params.verStatus) { // 버전바
                // 각 태그 존재 여부 판단
                const xcontList = Array.isArray(data.returnData)
                    ? data.returnData.map(item => item.xcont || "")
                    : [];

                const hasPartinfo = xcontList.some(xcont => xcont.includes('<partinfo'));
                const hasWstep = xcontList.some(xcont => xcont.includes('<wstep'));

                // 렌더링 함수 분기 처리
                if (hasPartinfo) {
                    renderIPBVerCont(data);
                } else if (hasWstep) {
                    renderWCVerCont(data);
                } else {
                    renderVerCont(data);
                }
            }

            imageEventBind();
        },
        error: function (status, error) {
            console.error("error:", status, error);
        }
    });
}
/* --- */

/**
 * 멀티 링크 클릭시 링크 리스트 레이어팝업 보기
 * 기존 showLinkMulti 주석 후 함수 새로 생성 - osm
 */
/* 멀티 링크 선택 시 실행 */
function showLinkMulti(linkId, event) {
    const winWidth = $(window).width();
    const $link = $('[xid="' + linkId + '"]');
    const $popup = $link.siblings(".layer-popup");

    if (!$popup.length) {
        console.warn("레이어 팝업 요소가 없습니다: #" + linkId);
        return;
    }

    $popup.empty();
    populatePopupLinks($popup);

    positionPopupNearCursor($popup, event, winWidth);

    // 외부 클릭 시 팝업 닫기
    $(document).off("mousedown.linkClose").on("mousedown.linkClose", function (e) {
        if (!$popup.is(e.target) && $popup.has(e.target).length === 0) {
            $popup.hide();
            $(document).off("mousedown.linkClose");
        }
    });
}
/* --- */

/* 다중링크의 revalationType:'테이블' onclick 함수가 동작하지 않아 수정 --- cch  */
function populatePopupLinks($popup) {
    const listNames = $popup.attr("listname")?.split(/\s*,\s*/);
    const revelationTypes = $popup.attr("revelationtype")?.split(/\s*,\s*/);
    const linkIds = $popup.attr("linkid")?.split(/\s*,\s*/);
    const tmNames = $popup.attr("tmname")?.split(/\s*,\s*/);

    if (!listNames || !revelationTypes) return;

    listNames.forEach((text, i) => {
        const revelationType = revelationTypes[i]?.trim() || '';
        const linkId = linkIds?.[i]?.trim() || '';
        const tmName = tmNames?.[i]?.trim() || '';
        const listName = text?.trim() || '';

        let onClick = '';

        // 부분 문자열로 포함 여부 판단
        if (/도해/.test(revelationType)) {
            onClick = `openPopup({ toKey: '${tmName}', grphId: '${linkId}' })`;
        } else if (/목차/.test(revelationType)) {
            onClick = `activateTocLink('${linkId}'); tocoContView('${linkId}');`;
        } else if (/TO/.test(revelationType)) {
            onClick = `selectTo('${listName}')`;
        } else if (/RDN/.test(revelationType)) {
            onClick = `openPopup({ toKey: '${tmName}', tocoId: '${linkId}' })`;
        } else if (/테이블/.test(revelationType)) {
            onClick = `openPopup({ toKey: '${tmName}', tblId: '${linkId}' })`;
        }

        console.log(`Link[${i}]`, { revelationType, listName, onClick });

        const $link = $("<a/>", {
            href: "javascript:void(0)",
            text: listName,
            style: "font-size: 16px;"
        });

        if (onClick) {
            $link.attr("onclick", onClick);
        }

        $popup.append($link);
    });

    // 생성된 a 태그 확인
    $(".layer-popup a").each(function(i) {
        console.log(`A[${i}]:`, $(this).text(), "->", $(this).attr("onclick"));
    });
}
/* --- */
///* 내용을 콤마(,) 단위로 분류하여 각각 a태그로 생성 */
//function populatePopupLinks($popup) {
//    // 공백 포함 콤마 분리
//    const listNames = $popup.attr("listname")?.split(/\s*,\s*/);
//    const revelationTypes = $popup.attr("revelationtype")?.split(/\s*,\s*/);
//    const linkIds = $popup.attr("linkid")?.split(/\s*,\s*/);
//    const tmNames = $popup.attr("tmname")?.split(/\s*,\s*/);
//
//    if (!listNames || !revelationTypes) return;
//
//    // 각 요소마다 a태그 생성
//    listNames?.forEach((text, i) => {
//        // 공백, 콤마 분리 한 값을 하나씩 추출
//        const revelationType = revelationTypes?.[i]?.trim();
//        const linkId = linkIds?.[i]?.trim();
//        const tmName = tmNames?.[i]?.trim();
//        const listName = text?.trim();
//
//        // revelationType 도해, 목차 링크에 따른 js 적용
//        const onClickHandler = {
//            '도해': `openPopup({ toKey: '${tmName}', grphId: '${linkId}' })`,
//            '목차': `activateTocLink('${linkId}'); tocoContView('${linkId}');`,
//            'TO': `selectTo('${listName}')`,
//            'RDN': `openPopup({ toKey: '${tmName}', tocoId: '${linkId}' })`
//        }[revelationType] || '';
//
//        // a태그 구성
//        $("<a/>", {
//            href: "javascript:void(0)",
//            onclick: onClickHandler,
//            text: listName
//        }).appendTo($popup);
//    });
//}
///* --- */

/* 클릭한 버튼(링크) 기준 아래에 팝업 표시되도록 수정 --- cch */
function positionPopupNearCursor($popup, event, winWidth) {
    if (!event || typeof event.pageX === 'undefined') {
        console.warn("마우스 이벤트가 유효하지 않습니다.");
        return;
    }

    const $target = $(event.currentTarget);
    const $wrapper = $target.closest(".popup-wrapper");

    if (!$wrapper.length) {
        console.warn("popup-wrapper 요소를 찾을 수 없습니다.");
        return;
    }

    const offset = $target.position(); // wrapper 기준 상대 위치
    const height = $target.outerHeight();

    // 팝업 잠깐 보여주고 사이즈 측정
    $popup.css({ visibility: 'hidden', display: 'block' });
    const popupWidth = $popup.outerWidth();
    const popupHeight = $popup.outerHeight();
    $popup.css({ display: 'none', visibility: '' });

    // 중앙 정렬
    let leftPos = offset.left + ($target.outerWidth() / 2) - (popupWidth / 2);
    let topPos = offset.top + height;

    // wrapper 기준 너비와 위치 보정
    const wrapperWidth = $wrapper.width();

    // 우측 넘침 방지
    if (leftPos + popupWidth > wrapperWidth) {
        leftPos = wrapperWidth - popupWidth - 10;
    }
    if (leftPos < 0) {
        leftPos = 10;
    }

    // 팝업 표시 (항상 아래로)
    $popup.css({
        position: "absolute",
        left: `${leftPos}px`,
        top: `${topPos}px`,
        display: "block"
    });
}
/* --- */
///* 기준 요소를 window로, 마우스 기준으로 직접 위치 지정 */
//function positionPopupNearCursor($popup, event, winWidth) {
//    const popupWidth = $popup.outerWidth();
//    const popupHeight = $popup.outerHeight();
//    let leftPos = event.pageX - popupWidth / 2;
//    let topPos = event.pageY - popupHeight - 10; // 커서 아래 10px
//
//    // 화면 오른쪽 벗어나는 경우 조정
//    if (leftPos + popupWidth > winWidth) {
//        leftPos = winWidth - popupWidth - 10;
//    }
//    if (leftPos < 0) leftPos = 10;
//
//    // 링크 선택 활성화 시 적용되는 css
//    $popup.css({
//        left: leftPos + "px",
//        top: topPos + "px",
//        display: "block",
//    });
//}
///* --- */

/**
 * 2025.06.02 - osm
 * - WC 교범 계통 링크
 * - 테이블 팝업 템플릿
 */
function renderWCTable(dataList) {
    let html = `
        <table class="in_table_wc">
            <thead>
                <tr>
                    <th colspan="6">계통별 WC 점검절차 리스트</th>
                </tr>
                <tr>
                    <th>구분</th><th>인시분</th><th>작업구역</th><th>계통</th><th>하부계통</th><th>검사내용</th>
                </tr>
            </thead>`;

    !dataList || dataList.length === 0
        ? html += `<tbody><tr><td colspan="6"> ${getMessages("admin.no.data")} </td></tr></tbody>`
        : dataList.forEach(item => {
            html += `
        <tbody>
            <tr>
                <td class="view_wc_link_td_center">${item.wcName || ""}</td>
                <td class="view_wc_link_td_center">${item.wcSteptime || ""}</td>
                <td class="view_wc_link_td_center">${item.wcSteparea || ""}</td>
                <td class="view_wc_link_td_center">${item.wcSystem || ""}</td>
                <td class="view_wc_link_td_center">${item.wcSubsystem || ""}</td>
                <td class="view_wc_link_td_center">${item.wcContent || ""}</td>
            </tr>
        </tbody>`;
        });

    html += `</table>`;
    $(".main-content").html(html);
}

/**
 * 2025.06.13 - osm
 * - 변경바 링크 템플릿
 */
function renderVerCont(data) {
    const title = (verStatus === "u") ? getMessages("ietm.before.change") : getMessages("ietm.added");
    const chgNo = data.versionData?.chgNo || "";
    const chgDate = data.versionData?.chgDate || "";

    let xcontHtml = "";
    const list = Array.isArray(data.returnData) ? data.returnData : [];
    if (list.length > 0) {
        list.forEach(item => {
            xcontHtml += `<div class="ac-content">${item.xcont}</div>`;
        });
    } else {
        xcontHtml += `<div class="ac-content">${data.returnData}</div>`;
    }

    const html = `
    <div class="pup_ver_wrap" id="ver_link_layer">
        <div class="pup_form" id="popup_div">
            <!-- pup_header -->
            <div class="pup_header">
                <span id="version_title_input" title="title">${title}</span>
            </div>
            <div class="ver_cont">
                <table cellspacing="0" cellpadding="0" style="table-layout: fixed;">
                    <colgroup>
                        <col width="200" />
                        <col />
                    </colgroup>
                    <thead>
                        <tr>
                            <th>${getMessages("ietm.change.number")} : <span id="chg_number">${chgNo}</span></th>
                            <th>${getMessages("ietm.change.date")} : <span id="chg_date_input">${chgDate}</span></th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr> 
                            <td colspan="2">
                                <div class="table_scroll">
                                    ${xcontHtml}
                                    ${changebasis ? `<p id="link_text_create">${changebasis}</p>` : ""}
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    `;

    $(".main-content").html(html);
}

/**
 * 2025.06.16 - osm
 * 변경바 링크 WC 템플릿
 */
function renderWCVerCont(data) {
    const title = (verStatus === "u") ? getMessages("ietm.before.change") : getMessages("ietm.added");
    const chgNo = data.versionData?.chgNo || "";
    const chgDate = data.versionData?.chgDate || "";

    let xcontHtml = "";
    const list = Array.isArray(data.returnData) ? data.returnData : [];

    if (list.length > 0) {
        list.forEach(item => {
            const xmlStr = item.xcont || "";
            const parser = new DOMParser();
            const xmlDoc = parser.parseFromString(xmlStr, "application/xml");

            const wsteps = xmlDoc.querySelectorAll("wstep");

            wsteps.forEach(wstep => {
                const steptime = wstep.getAttribute("steptime") || "";
                const steparea = wstep.getAttribute("steparea") || "";
                const system = wstep.getAttribute("system") || "";
                const subsystem = wstep.getAttribute("subsystem") || "";
                const text = wstep.querySelector("text")?.textContent || "";

                xcontHtml += `
                    <tr class="in_table_wc">
                        <td>${steptime}</td>
                        <td>${steparea}</td>
                        <td>${system}</td>
                        <td>${subsystem}</td>
                        <td>${text}</td>
                    </tr>
                `;
            });
        });
    } else {
        xcontHtml = `<tr><td colspan="5" class="ac-content">${data.returnData}</td></tr>`;
    }

    const html = `
    <div class="pup_ver_wrap" id="ver_link_layer">
        <div class="pup_form" id="popup_div">
            <div class="pup_header">
                <span id="version_title_input" title="${title}">${title}</span>
            </div>
            <div class="ver_cont">
                <table cellspacing="0" cellpadding="0" style="table-layout: fixed; width: 100%;">
                    <colgroup>
                        <col width="12%" />
                        <col width="12%"/>
                        <col width="12%"/>
                        <col width="15%"/>
                        <col width="30%"/>
                    </colgroup>
                    <thead>
                        <tr>
                            <th colspan="2">${getMessages("ietm.change.number")} : <span id="chg_number">${chgNo}</span></th>
                            <th colspan="3">${getMessages("ietm.change.date")} : <span id="chg_date_input">${chgDate}</span></th>
                        </tr>
                    </thead>
                    <tbody>
                        ${xcontHtml}
                    </tbody>
                </table>
                ${changebasis ? `<p id="link_text_create">${changebasis}</p>` : ""}
            </div>
        </div>
    </div>
    `;

    $(".main-content").html(html);
}



/**
 * 2025.06.16 - osm
 * - 변경바 링크 IPB 템플릿
 */
function renderIPBVerCont(data) {
    const title = (verStatus === "u") ? getMessages("ietm.before.change") : getMessages("ietm.added");
    const chgNo = data.versionData?.chgNo || "";
    const chgDate = data.versionData?.chgDate || "";

    // 버전 상세 테이블 렌더링 HTML 생성
    const verHtml = (() => {
        if (!Array.isArray(data.returnData)) return "";

        // returnData.xcont 문자열 추출 및 xml 파싱
        const xmlStr = data.returnData.find(item => item.xcont)?.xcont || "";
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(xmlStr, "application/xml");

        // xml 내 partinfo, partbase 태그 각각 추출
        const partinfos = Array.from(xmlDoc.querySelectorAll("partinfo"));
        const partbases = Array.from(xmlDoc.querySelectorAll("partbase"));
        const rowCount = Math.max(partinfos.length, partbases.length);

        // 테이블에서 데이터 필드와 data-role 매핑
        const fieldMapping = {
            figureno: "graphicnum",
            indexnum: "indexnum",
            smr: "smr",
            nsn: "nsn",
            partnum: "partnum",
            cage: "cage",
            name: "name",
            usablon: "usablon",
            unitsper: "unitsper"
        };

        // 부모 창의 기존 데이터에서 비교 기준이 되는 tr 찾기
        const $targetTr = $(window.opener.document).find(`[xid='${contId}']`).closest("tr");

        // 비교 결과에 따라 강조 표시 클래스 부여
        const getClassIfDifferent = (key, newVal) => {
            const role = fieldMapping[key];
            const oldVal = $targetTr.find(`td[data-role='${role}']`).text().trim();
            return (oldVal !== newVal) ? "ipb_data_diff_td" : "";
        };

        // 각 row 렌더링
        return Array.from({ length: rowCount }).map((_, i) => {
            const info = partinfos[i];
            const base = partbases[i];

            return `
                <tr class="in_table_ipb">
                    <td></td>
                    <td class="${getClassIfDifferent('figureno', info?.getAttribute('figureno') || '')}"> ${info?.getAttribute("figureno") || ""} </td>
                    <td class="${getClassIfDifferent('indexnum', info?.getAttribute('indexnum') || '')}"> ${info?.getAttribute("indexnum") || ""} </td>
                    <td class="${getClassIfDifferent('smr', base?.getAttribute('smr') || '')}"> ${base?.getAttribute("smr") || ""} </td>
                    <td class="${getClassIfDifferent('nsn', base?.getAttribute('nsn') || '')}"> ${base?.getAttribute("nsn") || ""} </td>
                    <td class="${getClassIfDifferent('partnum', base?.getAttribute('partnum') || '')}"> ${base?.getAttribute("partnum") || ""} </td>
                    <td class="${getClassIfDifferent('cage', base?.getAttribute('cage') || '')}"> ${base?.getAttribute("cage") || ""} </td>
                    <td class="ipb_nom ${getClassIfDifferent('name', info?.getAttribute('name') || '')}"> ${info?.getAttribute("name") || ""} </td>
                    <td class="${getClassIfDifferent('usablon', info?.getAttribute('usablon') || '')}"> ${info?.getAttribute("usablon") || ""} </td>
                    <td class="${getClassIfDifferent('unitsper', info?.getAttribute('unitsper') || '')}"> ${info?.getAttribute("unitsper") || ""} </td>
                </tr>
            `;
        }).join("");
    })();

    // 최종 팝업 HTML 생성
    const html = `
    <div class="pup_ver_wrap" id="ver_link_layer">
        <div class="pup_form" id="popup_div" style="width: 1500px; margin-left: -750px;">
            <div class="pup_header">
                <span id="version_title_input" title="${title}">${title}</span>
            </div>

            <div class="ver_cont">
                <table cellspacing="0" cellpadding="0" style="table-layout: fixed; width: 100%;">
                    <colgroup>
                        <col width="10px" />
                        <col width="15%"/>
                        <col width="8%"/>
                        <col width="10%"/>
                        <col width="10%"/>
                        <col width="25%"/>
                        <col width="10%"/>
                        <col width="30%"/>
                        <col width="10%"/>
                        <col width="10%"/>
                    </colgroup>
                    <thead>
                        <tr>
                            <th colspan="2">${getMessages("ietm.change.number")} : <span id="chg_number">${chgNo}</span></th>
                            <th colspan="8">${getMessages("ietm.change.date")} : <span id="chg_date_input">${chgDate}</span></th>
                        </tr>
                        <tr>
                            <th></th>
                            <th>${getMessages("ietm.partinfo.picture") + getMessages("ietm.partinfo.number")}</th>
                            <th>${getMessages("ietm.partinfo.item") + getMessages("ietm.partinfo.number")}</th>
                            <th>${getMessages("ietm.partinfo.overhaul")}<br/>${getMessages("ietm.partinfo.recoverability") + getMessages("ietm.partinfo.code")}</th>
                            <th>${getMessages("ietm.partinfo.national.inventory")}<br/>${getMessages("ietm.partinfo.number")}</th>
                            <th>${getMessages("ietm.partinfo.subpart") + getMessages("ietm.partinfo.number")}</th>
                            <th>${getMessages("ietm.partinfo.manufacturer") + getMessages("ietm.partinfo.code")}</th>
                            <th>${getMessages("ietm.partinfo.description")}<br/>${getMessages("ietm.partinfo.usability") + getMessages("ietm.partinfo.code")}</th>
                            <th>${getMessages("ietm.issue.unit")}</th>
                            <th>${getMessages("ietm.partinfo.perunit") + " " + getMessages("ietm.partinfo.component") + " " + getMessages("ietm.partinfo.count")}</th>
                        </tr>                    
                    </thead>
                    <tbody id="link_text_create">
                        ${verHtml}
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    `;

    // 팝업에 렌더링
    $(".main-content").html(html);
}

