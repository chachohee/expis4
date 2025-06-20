/**
 * IPB 색인 이벤트 처리
 */
function searchIPBClick() {
    const searchOption = $("#ipbSearchOpt1").val();
    let searchValue = $("#keyword").val();

    if (searchValue === "") {
        alert(getMessages("ietm.enter.search.term"));
        return false;
    }

    if (searchValue === "*") {
        searchValue = "";
    }

    // 2023.04.11 - KTA인 경우 해당 교범 내의 데이터만 검색 - jingi.kim
    let data = "searchCond=" + searchOption + "&searchWord=" + searchValue;
    data += bizCode === "KT1" ? "&toKey=" + TOKEY : "";

    $.ajax({
        type: "POST",
        url: `/EXPIS/${bizCode}/ietm/searchIPBList.do`,
        datatype: "json",
        data: data,
        success: function (data) {
            if (data.searchList != null) {
                $("#to_sub_toco_body").empty();
                if (data.searchList.length > 0) {
                    var rlHtml = "";
                    rlHtml += "<table class='all_list'>";
                    if (searchOption === "1" || bizCode === 'KT1') {
                        rlHtml += "<colgroup><col width='*'><col width='300'><col width='300'><col width='300'><col width='300'><col width='*'></colgroup><tbody>";
                    } else if (searchOption === "2") {
                        rlHtml += "<colgroup><col width='*'><col width='300'><col width='350'><col width='*'></colgroup><tbody>";
                    } else if (searchOption === "3") {
                        rlHtml += "<colgroup><col width='*'><col width='300'><col width='300'><col width='300'><col width='*'></colgroup>";
                    } else if (searchOption === "4") {
                        rlHtml += "<colgroup><col width='*'><col width='300'><col width='300'><col width='300'><col width='*'></colgroup>";
                    } else if (searchOption === "5") {
                        rlHtml += "<colgroup><col width='*'><col width='300'><col width='300'><col width='300'><col width='300'><col width='*'></colgroup>";
                    }
                    for (let i = 0; i < data.searchList.length; i++) {
                        const toKey = data.searchList[i].toKey;		            	//교범_KEY
                        const tocoId = data.searchList[i].tocoId;	            	//목차_ID
                        const vcKind = "01";
                        const cage = data.searchList[i].cage;		            	//생산자부호
                        const grphNo = "/" + data.searchList[i].grphNo;     	//그래픽_번호 -> ?
                        const indexNo = "/" + data.searchList[i].indexNo;	//색인_번호
                        const nsn = data.searchList[i].nsn;			                //국가재고번호
                        const partName = data.searchList[i].partName;	        	//부품_명
                        const partNo = data.searchList[i].partNo;	            	//부품_번호
                        const smr = data.searchList[i].smr;			                //근원정비복구성부호
                        const upa = data.searchList[i].upa;		                	//단위당구성수량(UPA)
                        const rdn = data.searchList[i].rdn;		                	//참조지시번호
                        const stdMngt = data.searchList[i].stdMngt;		            //규격_관리도면
                        if (searchOption === "1" || bizCode === 'KT1') {
                            rlHtml += "<tr><td></td><td style='text-align:center;'><a href='javascript:void(0)' title=" + partNo + " onclick=\"viewExContents('" + toKey + "', '" + tocoId + "', '" + searchValue + "', '" + vcKind + "')\">" + partNo + "</a> </td>";
                            rlHtml += "<td style='text-align:center;'><a href='javascript:void(0)' title=" + grphNo + indexNo + " onclick=\"viewExContents('" + toKey + "', '" + tocoId + "', '" + searchValue + "', '" + vcKind + "')\">" + grphNo + indexNo + "</a> </td>";
                            rlHtml += "<td style='text-align:center;'><a href='javascript:void(0)' title=" + upa + " onclick=\"viewExContents('" + toKey + "', '" + tocoId + "', '" + searchValue + "', '" + vcKind + "')\">" + upa + "</a> </td>";
                            rlHtml += "<td style='text-align:center;'><a href='javascript:void(0)' title=" + smr + " onclick=\"viewExContents('" + toKey + "', '" + tocoId + "', '" + searchValue + "', '" + vcKind + "')\">" + smr + "</a> </td>";
                            rlHtml += "<td></td></tr>";
                        } else if (searchOption === "2") {
                            rlHtml += "<tr><td></td>";
                            rlHtml += "<td style='text-align:center;'><a href='javascript:void(0)' title=" + rdn + " onclick=\"viewExContents('" + toKey + "', '" + tocoId + "', '" + searchValue + "', '" + vcKind + "')\">" + rdn + "</a> </td>";
                            rlHtml += "<td style='text-align:center;'><a href='javascript:void(0)' title=" + grphNo + indexNo + " onclick=\"viewExContents('" + toKey + "', '" + tocoId + "', '" + searchValue + "', '" + vcKind + "')\">" + grphNo + indexNo + "</a> </td>";
                            rlHtml += "<td></td></tr>";
                        } else if (searchOption === "3") {
                            rlHtml += "<tr><td></td>";
                            rlHtml += "<td style='text-align:center;'>" + nsn + "</td>";
                            rlHtml += "<td style='text-align:center;'>" + data.searchList[i].grphNo + "</td>";
                            rlHtml += "<td style='text-align:center;'>" + data.searchList[i].indexNo + "</td>";
                            rlHtml += "<td></td></tr>";
                        } else if (searchOption === "4") {
                            rlHtml += "<tr><td></td>";
                            rlHtml += "<td style='text-align:center;'>" + partNo + "</td>";
                            rlHtml += "<td style='text-align:center;'>" + data.searchList[i].grphNo + "</td>";
                            rlHtml += "<td style='text-align:center;'>" + data.searchList[i].indexNo + "</td>";
                            rlHtml += "<td></td></tr>";
                        } else if (searchOption === "5") {
                            rlHtml += "<tr><td></td>";
                            rlHtml += "<td style='text-align:center;'>" + data.searchList[i].ipbCode + "</td>";
                            rlHtml += "<td style='text-align:center;'>" + data.searchList[i].grphNo + "</td>";
                            rlHtml += "<td style='text-align:center;'>" + data.searchList[i].indexNo + "</td>";
                            rlHtml += "<td style='text-align:center;'>" + data.searchList[i].partNo + "</td>";
                            rlHtml += "<td></td></tr>";
                        } else {
                            rlHtml += "<tr><td></td>";
                            rlHtml += "<td style='text-align:center;'><a href='javascript:void(0)' title=" + rdn + " onclick=\"viewExContents('" + toKey + "', '" + tocoId + "', '" + searchValue + "', '" + vcKind + "')\">" + rdn + "</a> </td>";
                            rlHtml += "<td style='text-align:center;'><a href='javascript:void(0)' title=" + grphNo + indexNo + " onclick=\"viewExContents('" + toKey + "', '" + tocoId + "', '" + searchValue + "', '" + vcKind + "')\">" + grphNo + indexNo + "</a> </td>";
                            rlHtml += "<td></td></tr>";
                        }
                    }
                    rlHtml += "</tbody></table>";
                    $("#to_sub_toco_body").append(rlHtml);
                }
            }
        },
        error: function (e) {
            alert("Search Failed\n" + e);
            console.warn(e);
        }
    });
}

/**
 * IPB 색인 기능 SelectBox 동적 이벤트 처리
 */
function ipbSelectChange() {
    var searchOption = $("#ipbSearchOpt1").val();
    var ipbSearchHtmlStr = "";
    if (searchOption === 2) {
        $("#ipbSearchResultTable").empty();
        $("#to_sub_toco_body").empty();
        ipbSearchHtmlStr += "<colgroup><col width='*'><col width='300'><col width='350'><col width='*'></colgroup>";
        ipbSearchHtmlStr += "<thead>";
        ipbSearchHtmlStr += "<tr><th></th><th>RDN</th><th>계통/그림/시트 및 색인번호</th><th></th></tr>";
        ipbSearchHtmlStr += "</thead>";
        ipbSearchHtmlStr += "</table>";
        $("#ipbSearchResultTable").append(ipbSearchHtmlStr);
    } else if (searchOption === 1) {
        $("#ipbSearchResultTable").empty();
        $("#to_sub_toco_body").empty();
        ipbSearchHtmlStr += "<colgroup><col width='*'><col width='300'><col width='300'><col width='300'><col width='300'><col width='*'></colgroup>";
        ipbSearchHtmlStr += "<thead>";
        ipbSearchHtmlStr += "<tr><th></th><th>부품 번호</th><th>계통/그림/시트 및 색인번호</th><th>단위당 구성수량</th><th>근원정비복구성부호</th><th></th></tr>";
        ipbSearchHtmlStr += "</thead>";
        ipbSearchHtmlStr += "</table>";
        $("#ipbSearchResultTable").append(ipbSearchHtmlStr);
    }
}
