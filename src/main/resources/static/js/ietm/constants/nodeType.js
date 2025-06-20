/**
 * 임시 생성 -> 통합 로직에 불필요시 제거, 아직 상수에 대한 정확한 분류가 되어있지 않음
 * nodeType 에 대한 상수 정의, 이후 추가 시 대문자로 통일
 * - osm
 **/
const nodeType = Object.freeze({
    IPB: "IPB",
    FI: "FI",
    TOPIC: "TOPIC",
});
