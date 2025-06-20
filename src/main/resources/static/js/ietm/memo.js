let isDescending = true; // 기본값: 내림차순

function openMemoLayer() {
  document.getElementById('memoLayer').style.display = 'block';
  getMemoList();
}

function closeMemoLayer() {
  document.getElementById('memoLayer').style.display = 'none';
}


// 메모 제목 정렬
function memoSort() {
    const sortButton = document.getElementById("memoSort");
    const sortIcon = sortButton.querySelector("i");
    const memoListContainer = document.getElementById("memoListContainer");
    const memoDivs = Array.from(memoListContainer.getElementsByClassName("border"));

    // 내림차순 -> 오름차순 / 오름차순 -> 내림차순 토글
    isDescending = !isDescending;

    // 아이콘 변경
    if (isDescending) {
        // 내림차순
        sortIcon.classList.remove("fa-sort-amount-up");
        sortIcon.classList.add("fa-sort-amount-down");
        sortButton.innerHTML = '<i class="fas fa-sort-amount-down"></i> 내림차순';
    } else {
        // 오름차순
        sortIcon.classList.remove("fa-sort-amount-down");
        sortIcon.classList.add("fa-sort-amount-up");
        sortButton.innerHTML = '<i class="fas fa-sort-amount-up"></i> 오름차순';
    }
    
    getMemoList();
}





// 메모 목록 조회
function getMemoList() {
    console.log("call getMemoList()...");
    //사이드 메뉴 이벤트
    sideMenuClick();

    const searchValue = document.getElementById("searchValue").value.trim();
    let sortCode = isDescending ? '1' : '2'; // 내림차순 '1', 오름차순 '2'

    // 기본 URL 생성
    let url = `/EXPIS/`+ bizCode + `/ietm/memoList.do?sortCode=${sortCode}`;
    // 검색어가 있으면 쿼리 파라미터 추가
    if (searchValue !== "") {
        url += `&searchValue=${encodeURIComponent(searchValue)}`;
    }

    fetch(url)
        .then(response => response.json())
        .then(data => {
            const memoList = data.memoList;
            const memoListContainer = document.getElementById("memoListContainer");

            memoListContainer.innerHTML = '';

            if (memoList && memoList.length > 0) {
                // 메모 목록이 있을 경우
                memoList.forEach(memo => {
                    const memoDiv = document.createElement("div");
                    memoDiv.classList.add("border", "rounded", "p-2", "mb-2");
                    memoDiv.setAttribute("data-memo-seq", memo.memoSeq);

                    // 제목에 링크 추가
                    const memoHeader = document.createElement("div");
                    memoHeader.classList.add("d-flex", "justify-content-between", "align-items-center");

                    const memoTitle = document.createElement("a");
                    memoTitle.classList.add("text-decoration-none", "text-dark", "fw-bold");
                    memoTitle.href = "#";
                    memoTitle.textContent = memo.subject || "제목 없음";
                    memoTitle.onclick = (e) => {
                        e.preventDefault();
                        detailMemo(memo.memoSeq);
                        toggleMemoContent(memo.memoSeq);
                    };

                    // 펼쳐지는 내용
                    const memoContent = document.createElement("div");
                    memoContent.id = `memoContent-${memo.memoSeq}`;
                    memoContent.classList.add("mt-2", "text-muted", "d-none");
                    memoContent.innerHTML = `
                        <textarea id="contentText-${memo.memoSeq}" style="width: 100%; height: 150px; resize: none; font-size: 1rem;" disabled>${memo.cont || "내용 없음"}</textarea>
                        <div class="memo-dates" style="font-size: 0.8rem; opacity: 0.7; margin-top: 10px; text-align: right;">
                            <p class="small text-muted">
                                생성일: ${memo.createDate || "알 수 없음"}<br>
                                수정일: ${memo.modifyDate || "알 수 없음"}
                            </p>
                        </div>
                        `;

                    // 삭제 버튼
                    const deleteButton = document.createElement("button");
                    deleteButton.classList.add("btn", "btn-sm", "text-muted", "border-0", "p-0", "ms-2");
                    deleteButton.id = `deleteButton-${memo.memoSeq}`; // 삭제 버튼 id 추가
                    deleteButton.innerHTML = '<i class="fas fa-trash"></i>';
                    deleteButton.onclick = () => deleteMemo(memo.memoSeq); // 삭제 함수 호출

                    // 수정 버튼
                    const editButton = document.createElement("button");
                    editButton.classList.add("btn", "btn-sm", "text-muted", "border-0", "p-0", "ms-2");
                    editButton.id = `editButton-${memo.memoSeq}`; // 수정 버튼 id 추가
                    editButton.innerHTML = '<i class="fas fa-pen"></i>';
                    editButton.onclick = () => {
                        const openMemoContent = memoContent.classList.contains('d-none'); // display가 none인지

                        if (openMemoContent) {
                            // memoContent가 닫혀 있으면
                            toggleMemoContent(memo.memoSeq); // 내용 펼치고
                            enableMemoEdit(memo.memoSeq); // 수정창 보여주기
                        } else {
                            // memoContent가 열려 있는 경우
                            const hasSaveButton = memoContent.querySelector("#editSaveButton");

                            if (hasSaveButton) {
                                // 저장 버튼이 있으면
                                detailMemo(memo.memoSeq); // 상세포기폼 보여주기
                            } else {
                                // 저장 버튼이 없으면
                                enableMemoEdit(memo.memoSeq); // 수정폼 보여주기
                            }
                        }
                    };

                    // 버튼 컨테이너
                    const buttonContainer = document.createElement("div");
                    buttonContainer.classList.add("d-flex");
                    buttonContainer.appendChild(editButton);
                    buttonContainer.appendChild(deleteButton);

                    memoHeader.appendChild(memoTitle);
                    memoHeader.appendChild(buttonContainer);

                    memoDiv.appendChild(memoHeader);
                    memoDiv.appendChild(memoContent);

                    memoListContainer.appendChild(memoDiv);
                }); // end forEach

                // 메모 개수가 5개 이상일 경우 스크롤바가 보이도록
                if (memoList.length >= 5) {
                    memoListContainer.classList.add('scrollable');
                }

            } else {
                const emptyDiv = document.createElement("div");
                emptyDiv.classList.add("border", "rounded", "p-2");

                const emptyContent = document.createElement("p");
                emptyContent.classList.add("text-muted", "mb-0");
                emptyContent.textContent = "내용없음";

                emptyDiv.appendChild(emptyContent);
                memoListContainer.appendChild(emptyDiv);
            }
        })
        .catch(error => {
            console.error('Error fetching memo list:', error);
        });
}


// 메모 작성
function writeMemo() {
    console.log("call memoWrite()...");

    const memoSubject = document.getElementById("memoSubject").value;
    const memoContent = document.getElementById("memoContent").value;
    let toKey = "blank";
    let tocoId = "blank";

    let shareYN = "N";
    const shareButton = document.getElementById("shareButton");
    if (shareButton && shareButton.classList.contains("active")) {
        shareYN = "Y";
    } else {
        shareYN = "N";
    }

    if(memoSubject == ""){
        alert("제목을 입력하십시오.");
        return;
    }

    let toViewElement = parent.document.getElementById("toView");

    if (toViewElement) {
        // 'toView' 요소가 존재하면 toKey와 tocoId 값을 설정
        toKey = toViewElement.getAttribute("toKey") || "blank";
        tocoId = toViewElement.getAttribute("tocoId") || "blank";
    } else {
        // 'toView' 요소가 없으면 빈 문자열로 설정
        toKey = "blank";
        tocoId = "blank";
    }

    fetch("/EXPIS/" + bizCode + "/ietm/memoInsert.do", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        toKey: toKey,
        tocoId: tocoId,
        subject: memoSubject,
        cont: memoContent,
        shareYn: shareYN
      })
    })
    .then(response => response.json())
    .then(data => {
      if (data.success) {
        console.log("메모 저장 성공", data);
        alert("메모가 저장되었습니다.");

        getMemoList();      // 메모 리스트 다시 불러오기
        shareToggleActive();     // 공유하기 버튼 active 해제

        document.getElementById("memoSubject").value = "";
        document.getElementById("memoContent").value = "";

      } else {
        console.error("메모 저장 실패 - 서버 응답 실패", data);
        alert("메모 저장에 실패했습니다: " + (data.message || "서버 오류"));
      }
    })
    .catch(error => {
      console.error("메모 저장 실패", error);
      alert("메모 저장에 실패했습니다.");
    });
}

// 메모 삭제
function deleteMemo(memoSeq) {
    console.log(`삭제하려는 메모의 memoSeq: ${memoSeq}`);

    // 삭제 확인 메시지
    const userConfirmed = confirm("정말 삭제하시겠습니까?");
    if (!userConfirmed) {
        console.log("사용자가 삭제를 취소했습니다.");
        return;
    }

    fetch("/EXPIS/" + bizCode + "/ietm/memoDelete.do", {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ memoSeq: memoSeq })
    })
    .then(response => response.json())
    .then(data => {
        if (data.result === "success") {
            console.log("메모가 삭제되었습니다.");
            // 삭제 성공 시, 해당 메모를 UI에서 제거 --> 이렇게 할지 아니면 getMemoList() 다시 부를지 고민 -> getMemoList 다시 부르면 data-memo-seq 속성 없어도 됨
            const memoElement = document.querySelector(`[data-memo-seq="${memoSeq}"]`);
            if (memoElement) {
                memoElement.remove();
            }
        } else {
            console.error("Failed to delete memo.");
            alert("메모 삭제에 실패했습니다. 다시 시도해주세요.");
        }
    })
    .catch(error => {
        console.error("Error while deleting memo:", error);
        alert("오류가 발생했습니다. 다시 시도해주세요.");
    });
}

// 메모 수정(저장 버튼)
function saveMemoEdit(memoSeq) {
    const memoContent = document.getElementById(`memoContent-${memoSeq}`);
    const editContent = document.getElementById(`contentText-${memoSeq}`).value;

    fetch("/EXPIS/" + bizCode + "/ietm/memoUpdate.do", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            memoSeq: memoSeq,
            cont: editContent
        })
    })
    .then(response => response.json())
    .then(data => {
        console.log(data); // 응답 데이터 확인
        if (data.result === "success") {
            detailMemo(data.memo.memoSeq);
        } else {
            alert("수정 실패: " + (data.message || "알 수 없는 오류"));
        }
    })
    .catch(error => {
        console.error('Error updating memo:', error);
    });
}


// 메모 조회(비동기)
async function getMemoAsync(memoSeq) {
    console.log("call getMemoAsync()...");

    try {
        const response = await fetch("/EXPIS/" + bizCode + "/ietm/memoDetail.do", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ memoSeq: memoSeq })
        });

        const data = await response.json();
        console.log(data.memo);
        return data.memo;

    } catch (error) {
        console.error('Error fetching memo detail:', error);
    }
}

// 메모 내용 토글 함수
function toggleMemoContent(memoSeq) {
    const contentDiv = document.getElementById(`memoContent-${memoSeq}`);
    if (contentDiv) {
        contentDiv.classList.toggle("d-none");
    }
}

// 상세보기폼
async function detailMemo(memoSeq) {
    const memo = await getMemoAsync(memoSeq);
    console.log("memo", memo);
    const memoContent = document.getElementById(`memoContent-${memoSeq}`);
    memoContent.innerHTML = `
        <textarea id="contentText-${memoSeq}" style="width: 100%; height: 150px; resize: none; font-size: 1rem;" disabled>${memo.cont || "내용 없음"}</textarea>
        <div class="memo-dates" style="font-size: 0.8rem; opacity: 0.7; margin-top: 10px; text-align: right;">
            <p class="small text-muted">
                생성일: ${memo.createDate || "알 수 없음"}<br>
                수정일: ${memo.modifyDate || "알 수 없음"}
            </p>
        </div>
    `;
}

// 수정폼
async function enableMemoEdit(memoSeq) {
    const memo = await getMemoAsync(memoSeq);
    const memoContent = document.getElementById(`memoContent-${memoSeq}`);
    memoContent.innerHTML = `
        <textarea id="contentText-${memoSeq}" style="width: 100%; height: 150px; resize: none; font-size: 1rem;">${memo.cont || "내용 없음"}</textarea>
        <button id="editSaveButton" class="btn btn-sm btn-primary" onclick="saveMemoEdit(${memoSeq})">저장</button>
    `;
}

// 버튼 Active 함수
function shareToggleActive() {
    const shareButton = document.getElementById("shareButton");
    shareButton.classList.toggle("active");
}