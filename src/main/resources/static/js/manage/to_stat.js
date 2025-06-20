/**
 * 2025.05.20 - osm
 * 교범 접속 통계 클릭 시 데이터 렌더링
 */
window.onload = function () {
    otherTab('statistics');
};

/**
 * 2025.05.20 - osm
 * 통계, 내역 flag 별 분기
 */
function otherTab(flag) {
    $(".status_tap").hide();
    $("#to_info").hide();
    $("#to_detail_info").hide();

    const actions = {
        statistics: () => {
            $("#tab_report").removeClass("active");
            $("#tab_statistics").addClass("active");
            $("#to_info").show();
            $("#select_id").val("total");
            $(".select_sub").hide();
            toPie();
            tocoPie();
            createYear();
        },
        report: () => {
            $("#tab_statistics").removeClass("active");
            $("#tab_report").addClass("active");
            $("#to_detail_info").show();
            toDetailList();
        }
    };
    actions[flag]?.();
}

/**
 * 2025.05.20 - osm
 * 교범 접속 통계
 * - 전체, 연, 월, 일 선택 시 발생되는 select box 이벤트
 */
function selectBoxEvent() {
    const selectValue = $("#select_id").val();

    $(".select_sub").hide();

    const actions = {
        total: () => {
            toPie();
            tocoPie();
        },
        year: () => {
            $("#select_year").show()
            $("#select_year").prop("selectedIndex", 0)
        },
        month: () => {
            $("#select_month_year").show();
            $("#select_month_year").prop("selectedIndex", 0)
            $("#select_month_month").show();
            $("#select_month_month").prop("selectedIndex", 0).prop("disabled", true);

        },
        day: () => {
            $("#search_value").val("");
            $("#select_calendar").show()
        }
    };
    actions[selectValue]?.();
}

/**
 * 2025.05.20 - osm
 * 교범 접속 통계
 * - 연, 월, 일 분기
 * - selectBoxEvent 처리 후 선택 시 발생하는 이벤트
 */
function selectSubEvent(selectValue) {
    if ($("#" + selectValue).val() === "") return;

    const typeMap = {
        select_year: "year",
        select_month: "month",
        select_day: "day"
    };
    const type = typeMap[selectValue];
    if (!type) return;

    toPie(type);
    tocoPie(type);
    $("#select_check").val(type);
}

/**
 * 2025.05.20 - osm
 * 교범 접속 통계 - 연도 생성
 */
function createYear() {
    const bizCode = $("input[name='bizCode']").val();

    $.ajax({
        type: "POST",
        dataType: "json",
        url: `/EXPIS/${bizCode}/manage/toCreateDate.do`,
        success: function (data) {
            let content = `<option value="">${getMessages("admin.year.selection")}</option>`;

            data.result.forEach(item => {
                content += `<option value="${item.connDate}">${item.connDate}${getMessages("admin.years")}</option>`;
            });

            $("#select_year, #select_month_year").html(content);
        },
        error: function (error) {
            console.warn(error);
            alert("error");
        }
    });
}

/**
 * 2025.05.20 - osm
 * 교범별 접속 TOP5 - 파이 차트, 테이블
 */
function toPie(type = "total") {
    const bizCode = $("input[name='bizCode']").val();

    /*
        title   : 막대 차트 상단 제목
        data    : Ajax 요청 data
        labelFn : 차트 하단 라벨을 구성하는 함수
    */
    const flag = {
        total: {
            title: getMessages("ietm.all") + " " + getMessages("admin.static.access.manual"),
            data: {
                type: type
            }
        },
        year: {
            title: getMessages("admin.yearly") + " " + getMessages("admin.static.access.manual"),
            data: {
                date: `${$("#select_year").val()}`,
                type: type
            }
        },
        month: {
            title: getMessages("admin.monthly") + " " + getMessages("admin.static.access.manual"),
            data: {
                date: `${$("#select_month_year").val()}-${$("#select_month_month").val()}`,
                type: type
            }
        },
        day: {
            title: getMessages("admin.glance") + " " + getMessages("admin.static.access.manual"),
            data: {
                date: `${$("#search_value").val()}`,
                type: type
            }
        }
    };

    const {title, data} = flag[type];

    if (Object.values(data).some(v => !v)) return;

    $.ajax({
        type: "POST",
        url: `/EXPIS/${bizCode}/manage/toPieChart.do`,
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (data) {
            renderTable({
                selector: "#to_pie_list",
                data: data.toPie,
                colspan: 3,
                onClickRow: (item) => `toDetailList(1, "to", "${item.toName}")`,
                rowMapper: (item, index) => [
                    index + 1,
                    item.toName,
                    item.connCount
                ]
            });

            const toPieData = data.toPie.map(item => ({
                name: item.toName,
                value: item.connCount
            }));

            pieChart("to_pie", toPieData, title);
        },
        error: function (error) {
            console.warn(error);
            alert("error");
        }
    });
}


/**
 * 2025.05.20 - osm
 * 목차별 접속 TOP5 - 파이 차트, 테이블
 */
function tocoPie(type = "total") {
    const bizCode = $("input[name='bizCode']").val();

    /*
        title   : 막대 차트 상단 제목
        data    : Ajax 요청 data
    */
    const flag = {
        total: {
            title: getMessages("ietm.all") + " " + getMessages("admin.toc.access.static"),
            data: {
                type: type
            }
        },
        year: {
            title: getMessages("admin.yearly") + " " + getMessages("admin.toc.access.static"),
            data: {
                date: `${$("#select_year").val()}`, type: type
            }
        },
        month: {
            title: getMessages("admin.monthly") + " " + getMessages("admin.toc.access.static"),
            data: {
                date: `${$("#select_month_year").val()}-${$("#select_month_month").val()}`,
                type: type
            }
        },
        day: {
            title: getMessages("admin.glance") + " " + getMessages("admin.toc.access.static"),
            data: {
                date: `${$("#search_value").val()}`,
                type: type
            }
        }
    };
    // type에 따라 필요한 설정 추출
    const {title, data} = flag[type];

    $.ajax({
        type: "POST",
        url: `/EXPIS/${bizCode}/manage/tocoPieChart.do`,
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (data) {
            const list = data.tocoPie;

            renderTable({
                selector: "#toco_pie_list",
                data: list,
                colspan: 4,
                onClickRow: (item) => `toDetailList(1, "toco", "${item.tocoName}")`,
                rowMapper: (item, index) => [
                    index + 1,
                    item.tocoName,
                    item.toKey,
                    item.connCount
                ]
            });

            const tocoPieData = list.map(item => ({
                name: item.tocoName,
                value: item.connCount
            }));

            pieChart("toco_pie", tocoPieData, title);
        },
        error: function (error) {
            console.warn(error);
            alert("error");
        }
    });
}

/**
 * 2025.05.20 - osm
 * 내역 - 테이블 데이터
 */
function toDetailList(page = 1, type = "none", inputValue = "none") {
    const bizCode = $("input[name='bizCode']").val();

    if (!inputValue || inputValue === "none") {
        switch (type) {
            case "id":
                inputValue = $("#search_id").val();
                break;
            case "name":
                inputValue = $("#search_name").val();
                break;
            case "to":
                inputValue = $("#search_to").val();
                break;
            case "toco":
                inputValue = $("#search_toco").val();
                break;
            default:
                inputValue = "none";
        }
    }

    const data = {
        nowPage: page,
        searchType: type,
        searchValue: inputValue,
    }

    $.ajax({
        type: "POST",
        url: `/EXPIS/${bizCode}/manage/toDetailList.do`,
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (data) {
            renderTable({
                selector: "#to_history_list",
                data: data.result,
                rowMapper: (item, index) => {
                    const tNum = data.totalCnt - ((data.nowPage - 1) * data.recordCnt + index);
                    return [
                        tNum,
                        item.userId,
                        item.userName,
                        item.toKey,
                        item.tocoName,
                        item.connDate
                    ];
                },
                colspan: 6
            });

            $("#tab_statistics").removeClass("active");
            $("#tab_report").addClass("active");
            $("#to_info").hide();
            $("#to_detail_info").show();
            $("#to_detail_info").data("searchType", type);
            $("#to_detail_info").data("searchValue", inputValue);

            $("#to_history_paging").html(data.page);
        },
        error: function (error) {
            console.warn(error);
            alert("error");
        }
    });
}

/**
 * 2025.05.20 - osm
 * 교범 접속 통계
 * - 테이블 렌더링 함수
 */
function renderTable({selector, data, rowMapper, onClickRow, colspan}) {
    const hasData = Array.isArray(data) && data.length > 0;
    let contents = "";

    if (!hasData) {
        contents += `<tr><td colspan="${colspan}"> ${getMessages("admin.no.data")} </td></tr>`;
    } else {
        contents = data.map((item, index) => {
            const rowClick = onClickRow ? ` onclick='${onClickRow(item)}'` : "";
            const cells = rowMapper(item, index)
                .map(cell => `<td title="${cell}">${cell}</td>`)
                .join("");
            return `<tr${rowClick}>${cells}</tr>`;
        }).join("");
    }

    $(selector).html(contents);
}

/**
 * 2025.05.20 - osm
 * 교범 접속 통계 - 월별
 * - 연도 선택 시 발생하는 이벤트
 */
function selectYearEvent() {
    $("#select_month_month").prop("disabled", false);
}

/**
 * 2025.05.20 - osm
 * 교범 접속 통계 - 일별
 * - 캘린더 이벤트 구성
 */
function pickerEvent() {
    $("#search_value").datepicker({
        dateFormat: "yy-mm-dd",
        maxDate: 0,
        changeMonth: true,
        changeYear: true,
        yearRange: "c-30:c+0"
    }).datepicker("show");
}

/**
 * 내역 페이징 동작
 */
function statDetailPage(pNum) {
    const type = $("#to_detail_info").data("searchType");
    const value = $("#to_detail_info").data("searchValue");
    toDetailList(pNum, type, value);
}

/**
 * 검색창 열기
 */
function layShowHide(id) {
    $(".search_pup_form").hide(); // 기존 팝업 모두 닫기

    const popup = $("#" + id);
    const button = event.currentTarget;

    // 검색창 위치 설정
    const rect = button.getBoundingClientRect();
    const top = rect.bottom + window.scrollY;
    const left = rect.left + window.scrollX;
    popup.css({
        top: `${top}px`,
        left: `${left}px`,
        display: "block"
    });

    // 검색창 열리면 박스 안으로 포커스
    popup.find("input[type='text']").first().focus();

    // 클릭한 요소가 popup이나 그 내부가 아니면 닫기
    $(document).off("mousedown.popupClose").on("mousedown.popupClose", function (e) {
        if (!popup.is(e.target) && popup.has(e.target).length === 0 && !button.contains(e.target)) {
            popup.hide();
            $(document).off("mousedown.popupClose");
        }
    });
}

/**
 * 검색 동작 (클릭)
 */
function searchEvent(type) {
    toDetailList(1, type, $(`#search_${type}`).val());

    // 값 넘긴 후 검색 값 초기화
    setTimeout(() => {
        $("#search_id").val("");
        $("#search_name").val("");
        $("#search_toco").val("");
    }, 0);

    $(".search_pup_form").hide();
}

/**
 * 검색 동작 (엔터)
 */
function searchEnter(type) {
    if (event.key === "Enter") {
        toDetailList(1, type, $(`#search_${type}`).val());

        // 값 넘긴 후 검색 값 초기화
        setTimeout(() => {
            $("#search_id").val("");
            $("#search_name").val("");
            $("#search_toco").val("");
        }, 0);

        $(".search_pup_form").hide();
    }
}

/**
 * 검색창 닫기
 */
function closeLay() {
    $(".search_pup_form").hide();
}

/**
 * 2025.05.20 - osm
 * 교범 접속 통계 - 파이 차트
 */
function pieChart(chartId, pieData, title) {
    // 차트를 렌더링할 DOM 요소
    const chartDom = document.getElementById(chartId);
    const myChart = echarts.getInstanceByDom(chartDom) || echarts.init(chartDom);

    // 유효성 검사: 데이터가 없거나 전부 0인 경우
    if (!pieData || !Array.isArray(pieData) || pieData.length === 0 || pieData.every(item => !item.value || item.value === 0)) {
        echarts.dispose(chartDom);  // 기존 차트가 있으면 제거
        chartDom.innerHTML = `<div style="text-align:center; padding:20px; color:#999; font-size:16px;">${getMessages("admin.no.data")}</div>`;
        return;
    }

    // 차트 옵션 설정
    const option = {
        // 차트 제목
        title: {
            text: title,
            textStyle: {color: '#fff'},
            left: 'center'
        },
        // 툴팁: 마우스 호버 시 값 표시
        tooltip: {
            trigger: 'item',
            formatter: '{b}: <b>{d}%</b>'
        },
        // 범례 설정
        legend: {
            orient: 'horizontal',
            bottom: 0,
            left: 'center',
            textStyle: {color: '#fff'}
        },
        color: ['#2894ec', '#386288', '#a46af2', '#4fdad3', '#f4f632'],
        // 데이터 설정
        series: [{
            type: 'pie',
            radius: '60%',
            data: pieData,
            label: {
                show: true,
                color: '#fff',
                fontWeight: 'bold',
                formatter: '{b}: {d}%'
            },
            // 강조 효과
            emphasis: {
                itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        }]
    };
    myChart.setOption(option);
}
