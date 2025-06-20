function convertAirplaneType() {
    // 선택된 라디오 버튼 값
    var selectedAirplane = document.querySelector('input[name="airplane"]:checked');

    if (selectedAirplane) {
      // 선택된 값
      var airplaneValue = selectedAirplane.value;
      var airplaneLabel = selectedAirplane.nextElementSibling.innerText; // 라벨 텍스트

      // 상단 네비게이션 항공기 정보 업데이트
      var airplaneTextElement = document.querySelector('.navbar-nav .dropdown-menu .dropdown-item:first-child');
        airplaneTextElement.innerHTML = "항공기: " + airplaneLabel;

      // 모달 닫기
      var modal = bootstrap.Modal.getInstance(document.getElementById('airplane'));
        modal.hide();
    } else {
        alert('항공기 타입을 선택해주세요.');
    }
}