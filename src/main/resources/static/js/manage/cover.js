/**
 * 표지 출력
 * 2025.05.09 - osm
 */
function viewCover(toKey) {
    const baseUrl = "/EXPIS/" + bizCode + "/manage";
    const data = {
        "toKey": toKey
    }

    $.ajax({
        type: "POST",
        url: baseUrl + "/coverInfo.do",
        data: data,
        success: function (data) {
            renderCoverToPage(data);
        },
        error: function (error) {
            console.error("요청 실패: ", error);
        },
    });
}

/**
 * 표지 렌더링
 * 2025.05.09 - osm
 * - ietmHome.html 하단 hidden input태그를 통해 messages.properties 내용을 messages로 지정하여
 *   generateCoverHTML 템플릿 함수로 전달
 * - 확장 시 messages 처리방식 고민
 */
function renderCoverToPage(data) {
    // 메세지
    const messages = {
        books: $("#books").val(),
        distribute: $("#distribute").val(),
        distributeValue: $("#distributeValue").val(),
        warning: $("#warning").val(),
        warningValue: $("#warningValue").val(),
        notice: $("#notice").val(),
        noticeValue: $("#noticeValue").val(),
        business: $("#business").val(),
        verLabel: $("#verLabel").val(),
        revision: document.querySelector(".revision").getAttribute("data-revision")
        /* 필요한 경우, 다른 message 추가 */
    };

    // css
    const css = {
        cover01: "book_cover01",
        cover02: "book_cover02",
        cover03: "book_cover03",
    };

    let html;

    // bizCode 별 템플릿 구성
    switch (bizCode) {
        case "T50":
            html = generateCoverHTML(data, messages, css.cover01);
            break;

        /* 다른 bizCode에 대한 케이스 추가 */

        default:
            html = generateCoverHTML(data, messages, css.cover01);
            break;
    }

    $(".main-content").html(html);
}

/**
 * 표지 템플릿
 * 2025.05.09 - osm
 * - 동적 css 기준 head, cont, bottom으로 구성
 */
function generateCoverHTML(data, messages, css) {
    // data 변수
    const toKey = data.coverManageDto.toKey;
    const coverDate = data.coverManageDto.coverDate;
    const coverVerDate = data.coverManageDto.coverVerDate;
    const chgNo = data.coverManageDto.coverChgNo;

    // messages 변수
    const books = messages.books;
    const distribute = messages.distribute;
    const distributeValue = messages.distributeValue;
    const warning = messages.warning;
    const warningValue = messages.warningValue;
    const notice = messages.notice;
    const noticeValue = messages.noticeValue;
    const business = messages.business;
    const verLabel = messages.verLabel;
    const revision = messages.revision;

    return `
        <div class="${css}">
            <div class="book_bg">	
                <div class="cover_m cover_head">
                    <div>
                        <span>${books}</span>
                        <span>${toKey}</span>
                        <br/>
                        <span></span>
                        <span>${revision}</span>
                        <div style="height:30px;"></div>
                    </div>

                    <h1>${toKey}</h1>
                </div>

                <div class="cover_m cover_cont">
                    <strong>${distribute}</strong>
                    <p>${distributeValue}</p>

                    <strong>${warning}</strong>
                    <p>${warningValue}</p>

                    <strong>${notice}</strong>
                    <p>${noticeValue}</p>
                </div>

                <div class="cover_m cover_bottom">
                    <p>${business}</p>
                    <span>${coverDate}</span>
                    <span>${verLabel}&nbsp;${chgNo}&nbsp;&nbsp;${coverVerDate}</span>
                </div>
            </div>
        </div>
    `;
}
