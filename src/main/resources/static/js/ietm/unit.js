function unitConvert() {
    console.log("call unitConvert ...");

    // 입력된 값과 선택된 단위 가져오기
    const value = parseFloat(document.getElementById('conversionValue').value);
    const currentUnit = document.getElementById('currentUnit').value;
    const convertUnit = document.getElementById('convertUnit').value;

    // 입력값, 단위 유효한지 확인
    if (isNaN(value)) {
      alert("유효한 숫자를 입력해주세요.");
      return;
    }
    if (currentUnit === "- 단위를 선택하세요 -" || convertUnit === "- 단위를 선택하세요 -") {
      alert("현재 단위와 변환 단위를 선택해주세요.");
      return;
    }

    // 단위 변환 함수
    function convertUnits(value, fromUnit, toUnit) {
      const unitsInMeters = {
        mm: 0.001,
        cm: 0.01,
        m: 1,
        km: 1000,
        inch: 0.0254,
        ft: 0.3048,
        yd: 0.9144,
        mile: 1609.34
      };

      // 미터로 변환
      const valueInMeters = value * unitsInMeters[fromUnit];

      // 결과값 계산
      return valueInMeters / unitsInMeters[toUnit];
    }

    // 변환 계산
    const result = convertUnits(value, currentUnit, convertUnit);

    // 결과값 표시
    document.getElementById('result').value = result.toFixed(6);  // 소수점 6자리까지 표시
}