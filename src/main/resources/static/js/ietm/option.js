// 사이드메뉴에서 옵션 클릭했을 때 실행되는 함수
function optionMain(showPopup = false) {
    //사이드 메뉴 이벤트
    sideMenuClick();

//    const layer = document.getElementById('optionLayer');
//    layer.style.display = layer.style.display === 'block' ? 'none' : 'block';
    $.ajax({
        url: '/EXPIS/' + bizCode + '/ietm/optionMain.do',
        type: 'GET',
        dataType: 'json',
        success: function(response) {
            console.log(response);

            // 각 옵션 값 모달창에 세팅
            setOptionModal(response);

            const mainContent = document.querySelector('.main-content');
            // font size 조정
            adjustFontSize(mainContent, response.fontSize);
            // font family 조정
            adjustFontFamily(mainContent, response.fontFamily);
            // 동적으로 추가되는 요소들에도 적용되도록 설정
            observeChanges(mainContent, response.fontSize, response.fontFamily);

        },
        error: function(xhr, status, error) {
            console.error("Error fetching option data:", error);
        }
    });

    if (showPopup) {
        document.getElementById('optionLayer').style.display = 'block';
    }
}

function closeOptionLayer() {
    document.getElementById('optionLayer').style.display = 'none';
}

function setOptionModal(optionData) {
    if (optionData.exploreMode == '01') {
        $('#exploreMode button[name="01"]').addClass('active');
        $('#exploreMode button[name="02"]').removeClass('active');
    } else if (optionData.exploreMode == '02') {
        $('#exploreMode button[name="02"]').addClass('active');
        $('#exploreMode button[name="01"]').removeClass('active');
    }

    if (optionData.outputMode == '01') {
        $('#outputMode button[name="01"]').addClass('active');
        $('#outputMode button[name="02"]').removeClass('active');
    } else if (optionData.outputMode == '02') {
        $('#outputMode button[name="02"]').addClass('active');
        $('#outputMode button[name="01"]').removeClass('active');
    }

    if (optionData.fiMode == '01') {
        $('#fiMode button[name="01"]').addClass('active');
        $('#fiMode button[name="02"]').removeClass('active');
    } else if (optionData.fiMode == '02') {
        $('#fiMode button[name="02"]').addClass('active');
        $('#fiMode button[name="01"]').removeClass('active');
    }

    if (optionData.viewMode == '01') {
        $('#viewMode button[name="01"]').addClass('active');
        $('#viewMode button[name="02"]').removeClass('active');
    } else if (optionData.viewMode == '02') {
        $('#viewMode button[name="02"]').addClass('active');
        $('#viewMode button[name="01"]').removeClass('active');
    }

    // 글꼴 크기와 글꼴 스타일 설정
    $('#fontSize').val(optionData.fontSize);
    $('#fontFamily').val(optionData.fontFamily);
}



// 각 버튼 그룹에 대해 active 클래스를 토글하는 함수
function setActiveButton(buttons) {
  buttons.forEach(button => {
    button.addEventListener('click', () => {
      // 모든 버튼에서 active 클래스를 제거
      buttons.forEach(b => b.classList.remove('active'));

      // 클릭된 버튼에만 active 클래스를 추가
      button.classList.add('active');
    });
  });
}

// 각 모드별로 버튼을 처리
document.addEventListener('DOMContentLoaded', () => {
  const exploreModeButtons = document.querySelectorAll('#exploreMode button');
  const outputModeButtons = document.querySelectorAll('#outputMode button');
  const fiModeButtons = document.querySelectorAll('#fiMode button');
  const viewModeButtons = document.querySelectorAll('#viewMode button');

  // 각 모드별로 버튼 클릭 시 active 클래스를 추가/제거하는 함수 호출
  setActiveButton(exploreModeButtons);
  setActiveButton(outputModeButtons);
  setActiveButton(fiModeButtons);
  setActiveButton(viewModeButtons);
});



// 적용 버튼 클릭 시 값을 변수에 저장하고 처리
function optionApply() {
  const exploreMode = getSelectedButtonValue(document.querySelectorAll('#exploreMode button'));
  const outputMode = getSelectedButtonValue(document.querySelectorAll('#outputMode button'));
  const fiMode = getSelectedButtonValue(document.querySelectorAll('#fiMode button'));
  const viewMode = getSelectedButtonValue(document.querySelectorAll('#viewMode button'));
  const fontSize = document.getElementById('fontSize').value;
  const fontFamily = document.getElementById('fontFamily').value;

  closeOptionLayer();

  fetch('/EXPIS/' + bizCode + '/ietm/optionUpdate.do', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ exploreMode, outputMode, fiMode, viewMode, fontSize, fontFamily }),
  })
    .then(response => {
      if (!response.ok) throw new Error('서버 오류');
      return response.json();
    })
    .then(data => {
      console.log('설정이 적용되었습니다:', data);

      const mainContent = document.querySelector('.main-content');

      // font size 조정
      adjustFontSize(mainContent, fontSize);
      // font family 조정
      adjustFontFamily(mainContent, fontFamily);
      // 동적으로 추가되는 요소들에도 적용되도록 설정
      observeChanges(mainContent, fontSize, fontFamily);

    })
    .catch(error => console.error('오류 발생:', error));
}

// 선택된 버튼의 name 값을 반환하는 함수
function getSelectedButtonValue(buttons) {
  const activeButton = Array.from(buttons).find(button => button.classList.contains('active'));
  return activeButton ? activeButton.getAttribute('name') : "01";
}



// font size 조정 함수
function adjustFontSize(mainContent, fontSize) {
  const currentFontSize = parseFloat(window.getComputedStyle(mainContent).fontSize); // 현재 font size
  let newFontSize;

  if (fontSize === 'small') {
    newFontSize = currentFontSize * 0.8;
  } else if (fontSize === 'medium') {
    newFontSize = currentFontSize * 1;
  } else if (fontSize === 'large') {
    newFontSize = currentFontSize * 1.5;
  }

  const elements = mainContent.querySelectorAll('*');
  elements.forEach(element => {
    element.style.fontSize = `${newFontSize}px`;
  });
}


// fontFamily 조정 함수
function adjustFontFamily(mainContent, fontFamily) {
  if (fontFamily === '나눔고딕') {
    mainContent.style.fontFamily = "'나눔고딕', sans-serif";
  } else if (fontFamily === '굴림체') {
    mainContent.style.fontFamily = "'굴림체', sans-serif";
  } else if (fontFamily === '본고딕') {
    mainContent.style.fontFamily = "'본고딕', sans-serif";
  }
}


// 새로운 요소가 동적으로 추가될 때마다 font size 및 font family를 적용하도록 설정
function observeChanges(mainContent, fontSize, fontFamily) {
  const observer = new MutationObserver(() => {
    adjustFontSize(mainContent, fontSize);
    adjustFontFamily(mainContent, fontFamily);
  });

  // mainContent 내에서 요소의 추가, 삭제, 속성 변화를 감지
  observer.observe(mainContent, {
    childList: true,   // 자식 노드의 추가 및 제거 감지
    subtree: true,     // 하위 노드까지 포함
  });
}