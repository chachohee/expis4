$(document).ready(function () {
    introList();
});

function introList() {
    $.ajax({
        type : "GET",
        url : "introList.do",
        datatype : "json",
        success : function(data) {
            var map = data.introList;
            var list = map.detailList; //게시물
            var introBoard = map.introBoardName; //게시판종류
            var boardSize = introBoard.length;
            var boardName = "";
            var boardClass = "";
            var content = "";

            for(var i=0; i<boardSize; i++) {
                boardName = introBoard[i].boardName;
                boardClass = introBoard[i].className;
                content += mkUl(boardName, boardClass, list[i]);
            }

            $("#cmntMain").html(content);

        }, error : function() {
            alert( "Error : CMNT introList()");
        }
    });
}

function mkUl(boardName, boardClass, list) {
    var contents = "";
    var listSize = list.length;
    var dTitle = "";
    var dSeq = "";
    var mSeq = "";
    var dDate = "";
    var viewUserId = "";

    contents += "<div class='part " + boardClass + "_sample_1'>";
    contents += "	<strong title='" + boardName + "'>" + boardName + "</strong>";
    contents += "	<ul>";

    if(listSize == 0) {
        contents += "<li>"+$("#cmnt_board_not_exist").val()+"</li>";
    } else {

        for(var i=0; i<listSize; i++) {
            dTitle = list[i].boardTitle;
            dSeq = list[i].boardDSeq;
            mSeq = list[i].boardMSeq;
            dDate = list[i].createDate;
            viewUserId = list[i].viewUserId;

            contents += "		<li>";
            contents += `         <a href='/EXPIS/${bizCode}/cmnt/boardDetail.do?boardDSeq=${dSeq}&boardMSeq=${mSeq}&nPage=1' title='${dTitle}'>${dTitle}</a>`;;
            contents += `            <br>`;
            contents += "			<span title='" + dDate +"' class='m_date'>" + dDate + " </span>";
            contents += "			<span title='" + viewUserId + "' class='m_userId'>" + viewUserId + " </span>";
            contents += "		</li>";
        }
    }

    contents += "	</ul>";
    contents += "</div>";

    return contents;
}

function deleteFile(){
    $("#fileNameDisplay").hide();
    $("#file_upload").after("<small class='form-text text-muted'>첨부파일이 존재하지 않으면 비워두세요.</small>");
}

//관련사이트 삭제
function relatedSitesDelete() {
    const fileSeq = document.getElementById('fileSeqDelete').value;
    const relSeq = document.getElementById('relSeqDelete').value;
    const url = `/EXPIS/${bizCode}/cmnt/relatedDelete.do?relSeq=${relSeq}&fileSeq=${fileSeq}`;

    fetch(url, {
        method: 'GET',
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
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
                location.href = `/EXPIS/${bizCode}/cmnt/relatedList.do?nPage=${data.nPage}`;
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
