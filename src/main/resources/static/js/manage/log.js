/**
 * 2025.05.26 - osm
 * 로그 보기, 로그 코드에 따라 클릭 시 데이터 렌더링
 */
window.onload = function () {
    const secondId = $("input[name='secondId']").val();
    if (secondId === "view") {
        logInfo();
    } else if (secondId === "code") {
        logCode();
    }
};

/**
 * 2025.05.26 - osm
 * 로그 보기
 * - 로그 목록 검색 및 데이터 (logInfo)
 */
function logInfo(page = 1) {
    const bizCode = $("input[name='bizCode']").val()

    const data = {
        nowPage: page || 1,
        startDate: $("#start_date").val() || "none",
        endDate: $("#end_date").val() || "none",
    }

    $.ajax({
        url: `/EXPIS/${bizCode}/manage/tools/logInfo.do`,
        method: "GET",
        data: data,
        cache: false,
        success: function(data) {
            // 총 게시물 수
            document.getElementById("totalCount").innerText = data.totCnt;
            // 테이블 렌더링
            renderTable({
                selector: "#logTableContainer",
                data: data.aList,
                rowMapper: (item) => [
                    item.rnum,
                    item.createUserId,
                    item.createDate,
                    `[${item.codeType}] ${item.codeName}`,
                    item.codeInfo
                ],
                colspan: 5
            });
            // 페이징 처리
            $("#paginationContainer").html(data.pageTag);
        },
        error: function(error) {
            alert("데이터를 불러오지 못했습니다.");
            console.warn(error);
        }
    });
}

/**
 * 2025.05.26 - osm
 * - 로그 목록 검색 및 데이터 (logCode)
 */
function logCode(page = 1) {
    const bizCode = $("input[name='bizCode']").val()
    console.log(page);

    const data = {
        nowPage: page || 1,
        startDate: $("#start_date").val() || "none",
        endDate: $("#end_date").val() || "none",
    }

    $.ajax({
        url: `/EXPIS/${bizCode}/manage/tools/logCode.do`,
        method: "GET",
        data: data,
        cache: false,
        success: function(data) {
            // 총 게시물 수
            document.getElementById("totalCount").innerText = data.totCnt;
            // 테이블 렌더링
            renderTable({
                selector: "#logTableContainer",
                data: data.aList,
                rowMapper: (item) => [
                    item.rnum,
                    item.codeType,
                    item.codeName,
                    item.codeInfo,
                    item.createDate
                ],
                colspan: 5
            });
            // 페이징 처리
            $("#paginationContainer").html(data.pageTag);
        },
        error: function(error) {
            alert("데이터를 불러오지 못했습니다.");
            console.warn(error);
        }
    });
}

/**
 * 2025.05.26 - osm
 * 테이블 구성
 * logInfo, logCode 공통
 */
function renderTable({ selector, data, rowMapper, colspan, startIndex }) {
    let contents = "";

    if (!data || data.length === 0) {
        contents = `<tr><td colspan="${colspan}" class="text-center">${getMessages("admin.no.data")}</td></tr>`;
    } else {
        data.forEach((item, index) => {
            contents += `<tr>`;
            rowMapper(item, startIndex + index).forEach(cell => {
                contents += `<td title="${cell}">${cell}</td>`;
            });
            contents += "</tr>";
        });
    }

    $(selector).html(contents);
}

/**
 * 2025.05.26 - osm
 * 날짜 선택 설정
 * - logInfo, logCode 공통
 * - start_date, end_date 공통
 */
function pickerEvent(event) {
    const target = $(event.target);
    target.datepicker({
        dateFormat: "yy-mm-dd",
        maxDate: 0,
        changeMonth: true,
        changeYear: true,
        yearRange: "c-30:c+0"
    }).datepicker("show");
}

/**
 * 2025.05.26 - osm
 * 로그 목록 페이징 이동
 * - logInfo, logCode 공통
 */
function logPageEvent(nowPage) {
    const secondId = $("input[name='secondId']").val();
    if (secondId === "view") {
        logInfo(nowPage);
    } else if (secondId === "code") {
        logCode(nowPage);
    }
}
