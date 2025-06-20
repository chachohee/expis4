/**
 * 2025.05.16 - osm
 * 사용자 접속 통계 클릭 시 데이터 렌더링
 */
window.onload = function () {
    otherTab('statistics');
};

/**
 * 2025.05.16 - osm
 * 통계, 내역 flag 별 분기
 */
function otherTab(flag) {
    $(".status_tap").hide();
    $("#status_user_info").hide();
    $("#status_user_detail_info").hide();

    const actions = {
        statistics: () => {
            $("#tab_report").removeClass("active");
            $("#tab_statistics").addClass("active");
            $("#status_user_info").show();
            $("#select_id").val("total");
            $(".select_sub").hide();
            userContactStatus();
            userIdStatusList();
        },
        // 내역
        report: () => {
            $("#tab_statistics").removeClass("active");
            $("#tab_report").addClass("active");
            $("#status_user_detail_info").show();
            userIdStatusDetailList();
        }
    };
    actions[flag]?.();
}

/**
 * 2025.05.16 - osm
 * 사용자 접속 통계
 * - 전체, 연, 월, 일 선택 시 발생되는 select box 이벤트
 */
function selectBoxEvent() {
    const selectValue = $("#select_id").val();

    $(".select_sub").hide();

    const actions = {
        total: () => {
            userContactStatus();
            userIdStatusList();
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
 * 2025.05.16 - osm
 * 사용자 접속 통계
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

    userContactStatus(type);
    userIdStatusList(type);
    $("#select_check").val(type);
}

/**
 * 2025.05.16 - osm
 * 사용자 접속 통계
 * - type에 따라 Ajax 요청을 보내고 차트 렌더링
 * - 차트 제목, 요청 URL, 요청 파라미터, 라벨 함수는 flag 객체에서 관리
 * - 반복되는 로직을 통합하여 하나의 함수로 구성
 * - type이 전달되지 않으면 기본값으로 'total' 처리
 */
function userContactStatus(type = "total") {
    const bizCode = $("input[name='bizCode']").val();

    /*
        title   : 막대 차트 상단 제목
        data    : Ajax 요청 data
        labelFn : 차트 하단 라벨을 구성하는 함수
    */
    const flag = {
        total: {
            title: getMessages("ietm.all") + " " + getMessages("admin.user.access.static"),
            data: {
                type: type
            },
            labelFn: item => `${item.year}${getMessages("admin.years")}`
        },
        year: {
            title: getMessages("admin.yearly") + " " + getMessages("admin.user.access.static"),
            data: {
                type: type,
                year: $("#select_year").val()
            },
            labelFn: item => `${item.month}${getMessages("admin.month")}`
        },
        month: {
            title: getMessages("admin.monthly") + " " + getMessages("admin.user.access.static"),
            data: {
                type: type,
                yearMonth: `${$("#select_month_year").val()}-${$("#select_month_month").val()}`
            },
            labelFn: item => `${item.day}${getMessages("admin.days")}`
        },
        day: {
            title: getMessages("admin.glance") + " " + getMessages("admin.user.access.static"),
            data: {
                type: type,
                date: $("#search_value").val()
            },
            labelFn: item => `${item.timeVal}시`
        }
    };

    const {title, data, labelFn} = flag[type];

    if (typeof data !== 'object'
        || Object.values(data).some(value => value === null || value === undefined || value === "")) return;

    $.ajax({
        type: "POST",
        url: `/EXPIS/${bizCode}/manage/userContactStatus.do`,
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (data) {
            // 차트용 데이터 구성 [라벨, 값]
            const barData = {
                categories: data.userStatusList.map(item => labelFn(item)),
                values: data.userStatusList.map(item => item.userCount)
            };

            // 연도 선택 옵션 구성 (type이 'total'일 경우에만)
            let content = `<option value=""> ${getMessages("admin.year.selection")} </option>`;
            data.userStatusList.forEach(item => {
                content += `<option value="${item.year}"> ${item.year}${getMessages("admin.years")} </option>`;
            });
            // 연도 선택 박스를 초기화 후 옵션 재구성
            if (type === "total") $("#select_year, #select_month_year").empty().append(content);

            // 막대 차트 렌더링
            barChart(barData, title);
        },
        error: function (error) {
            console.warn(error);
            alert("error");
        }
    });
}

/**
 * 2025.05.16 - osm
 * 사용자 접속 통계 - 월별
 * - 연도 선택 시 발생하는 이벤트
 */
function selectYearEvent() {
    $("#select_month_month").prop("disabled", false);
}

/**
 * 2025.05.16 - osm
 * 사용자 접속 통계 - 일별
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
 * 2025.05.16 - osm
 * 사용자 접속 통계 - 막대 차트
 */
function barChart(barData, title) {
    const accessCount = getMessages("admin.count.access");
    // 차트 렌더링할 DOM 요소
    const chartDom = document.getElementById('user_bar_chart');
    const myChart = echarts.getInstanceByDom(chartDom) || echarts.init(chartDom);

    // 유효성 검사
    if (!barData
        || !Array.isArray(barData.categories)
        || barData.categories.length === 0
        || barData.values.every(val => val === 0)
    ) {
        echarts.dispose(chartDom);
        chartDom.innerHTML = `<div style="text-align:center; padding:20px; color:#999; font-size:16px;"> ${getMessages("admin.no.data")} </div>`;
        return;
    }

    // ECharts 구성
    const option = {
        // 차트 제목
        title: {
            text: title,
            textStyle: {color: '#fff'},
            left: 'center'
        },
        // 툴팁 설정
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        // X축 라벨
        xAxis: {
            type: 'category',
            data: barData.categories,
            axisLabel: {color: '#fff'}
        },
        // Y축 라벨
        yAxis: {
            type: 'value',
            name: accessCount,
            nameTextStyle: {
                color: '#fff',
                fontWeight: 'bold'
            },
            axisLabel: {color: '#fff'}
        },
        // 범례
        legend: {
            show: false
        },
        // 데이터 설정
        series: [{
            type: 'bar',
            data: barData.values,
            label: {
                show: true,
                color: '#fff',
                fontWeight: 'bold',
                position: 'top'
            },
            itemStyle: {
                color: function (params) {
                    const colors = ['#2894ec', '#386288', '#a46af2', '#4fdad3', '#f4f632'];
                    return colors[params.dataIndex % colors.length];
                }
            }
        }]
    };
    myChart.setOption(option);
}

/**
 * 2025.05.16 - osm
 * 사용자 아이디(ID) 별 리스트
 * - 파이 차트 구성
 * - 사용자 테이블 구성
 * - default type = total
 */
function userIdStatusList(type = "total") {
    const bizCode = $("input[name='bizCode']").val();

    const titleMap = {
        total: getMessages("ietm.all") + " " + getMessages("admin.user.connect.status"),
        year: getMessages("admin.yearly") + " " + getMessages("admin.user.connect.status"),
        month: getMessages("admin.monthly") + " " + getMessages("admin.user.connect.status"),
        day: getMessages("admin.glance") + " " + getMessages("admin.user.connect.status")
    };
    const title = titleMap[type] || getMessages("admin.user.connect.status");

    const typeToDate = {
        year: () => $("#select_year").val(),
        month: () => `${$("#select_month_year").val()}-${$("#select_month_month").val()}`,
        day: () => $("#search_value").val()
    };
    const date = typeToDate[type]?.() || "";

    const data = {
        type: type,
        date: date,
    }

    $.ajax({
        type: "POST",
        url: `/EXPIS/${bizCode}/manage/userIdStatusList.do`,
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (data) {
            const userStatusList = data.userStatusList || [];

            // 파이 차트 구성
            const top3 = userStatusList.slice(0, 3); // 상위 3개 항목 추출 (가장 많이 접속한 사용자)
            const etcList = userStatusList.slice(3); // 4번째 이후 항목들은 '기타'로 합산
            const etcTotal = etcList.reduce((sum, item) => sum + item.userCount, 0); // '기타' 항목들의 카운트 합산

            // pieData 배열에 상위 3개 항목을 [이름, 카운트] 형태로 매핑
            const pieData = top3.map(item => ({name: item.userName, value: item.userCount}));
            // 4개 이상일 경우 '기타' 데이터를 pieData에 추가
            if (etcList.length > 0) pieData.push({name: getMessages("admin.etc"), value: etcTotal});

            // 파이 차트 호출
            pieChart(pieData, title);

            // 테이블 렌더링
            renderTable({
                selector: "#user_status_list", // 렌더링 할 tbodyId
                data: userStatusList,
                rowMapper: (item, index) => [ // 테이블에 보여줄 데이터
                    index + 1,
                    item.userId,
                    item.userName,
                    item.userCount
                ],
                onClickRow: item => `userIdStatusDetailList(1, "id", "${item.userId}")`,
                colspan: 4,
            });

        },
        error: function (error) {
            console.warn(error);
            alert("error");
        }
    });
}

/**
 * 2025.05.16 - osm
 * 사용자 접속통계 상세내역
 * - 내역 테이블
 */
function userIdStatusDetailList(page = 1, type = "none", inputValue = "none") {
    const bizCode = $("input[name='bizCode']").val();
    const baseUrl = "/EXPIS/" + bizCode + "/manage";

    const data = {
        nowPage: page,
        searchType: type,
        searchValue: inputValue,
    }

    $.ajax({
        type: "POST",
        url: baseUrl + "/userIdStatusDetailList.do",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (data) {
            // 테이블 렌더링
            renderTable({
                    selector: "#user_detail_list",
                    data: data.userStatusList,
                    rowMapper: (item, index) => {
                        const tNum = data.totalCount - ((data.nowPage - 1) * data.recordCnt + index);
                        const disconnDate = (item.disconnDate == null || item.disconnDate === "null") ? "-" : item.disconnDate;
                        return [tNum, item.userId, item.userName, item.connDate, disconnDate];
                    },
                    colspan: 5,
                }
            );

            $(".status_tap").hide();
            $("#status_user_info").hide();
            $("#tab_statistics").removeClass("active");
            $("#tab_report").addClass("active");
            $("#status_user_detail_info").show();
            $("#status_user_detail_info").data("searchType", type);
            $("#status_user_detail_info").data("searchValue", inputValue);

            // 페이징 처리
            $("#user_detail_page").html(data.page);
        },
        error: function (error) {
            console.warn(error);
            alert("error");
        }
    });
}

/**
 * 2025.05.16 - osm
 * 사용자 아이디(ID) 별 리스트
 * - 테이블 렌더링 함수
 * - 기존의 각 테이블 생성 마다 생성되어있던 함수 통합
 */
function renderTable({selector, data, rowMapper, onClickRow, colspan}) {
    let contents = "";

    !data || data.length === 0
        ? contents += `<tr><td colspan=${colspan}> ${getMessages("admin.no.data")} </td></tr>`
        // 데이터 배열 수 만큼 순회
        : data.forEach(
            (item, index) => {
                // onClickRow 제공 시 onclick 이벤트 추가
                contents += `<tr${onClickRow ? ` onclick='${onClickRow(item)}'` : ""}>`;

                // rowMapper로 받아온 각 셀 데이터를 <td>로 출력
                rowMapper(item, index).forEach(cell => {
                    contents += `<td title="${cell}">${cell}</td>`;
                });

                contents += "</tr>";
            }
        );

    // contents 지정된 tbody에 삽입
    $(selector).html(contents);
}

/**
 * 내역 페이징 동작
 */
function statDetailPage(pNum) {
    const type = $("#status_user_detail_info").data("searchType");
    const value = $("#status_user_detail_info").data("searchValue");
    userIdStatusDetailList(pNum, type, value);
}

/**
 * 검색창 열기
 */
function layShowHide(id) {
    $(".search_pup_form").hide();

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
    userIdStatusDetailList(1, type, $(`#search_${type}`).val());

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
        userIdStatusDetailList(1, type, $(`#search_${type}`).val());

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
 * 2025.05.16 - osm
 * 사용자 아이디(ID) 별 리스트 - 파이 차트
 */
function pieChart(pieData, title) {
    // 차트를 렌더링할 DOM 요소
    const chartDom = document.getElementById('user_pie_chart');
    const myChart = echarts.getInstanceByDom(chartDom) || echarts.init(chartDom);

    // 유효성 검사: 데이터가 없거나 전부 0인 경우
    if (!pieData || !Array.isArray(pieData) || pieData.length === 0 || pieData.every(item => !item.value || item.value === 0)) {
        echarts.dispose(chartDom);
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
        // 시리즈 설정
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
