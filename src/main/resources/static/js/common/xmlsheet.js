
// 텍스트를 xml로
function loadXML(data) {
    if ( data === 0 ) return "";
    return $.parseXML(data);
}

//XSLT 로드
function getXmlSheet(jData) {
    let rtn = "";
    if (jData.type) {
        jData.type = jData.type.toUpperCase();
    }
    let url = "/xslt/ContTo.xml";

    url = jData.type === "TO" ? "/xslt/ToTree.xml" : url;
    url = jData.type === "TOCO" ? "/xslt/ToCoTree.xml" : url;
    url = jData.type === "MYTOCO" ? "/xslt/MyTocoTree.xml" : url;
    url = jData.type === "DI_DESC" ? "/xslt/ContDiDesc.xml" : url;
    url = jData.type === "LR_DESC" ? "/xslt/ContLrDesc.xml" : url;
    url = jData.type === "WORKCARD" ? "/xslt/ContWorkCard.xml" : url;
    url = jData.type === "KTAWORKCARD" ? "/xslt/ContWorkCardKTA.xml" : url;
    url = jData.type === "IPB" ? "/xslt/ContIpb.xml" : url;
    url = jData.type === "STEPSEQ" ? "/xslt/ContJgMnt.xml" : url;
    url = jData.type === "REQCOND" ? "/xslt/ContReqCond.xml" : url;
    url = jData.type === "FIELDSTEP" ? "/xslt/ContFieldStep.xml" : url;

    $.ajax({
        type : "GET",
        async: false,
        url : url,
        dataType : "text",
        success : function(data) {
            rtn = data;
        },
        error:function(e){
            rtn = "";
        }
    });
    return rtn;
}

/**
 * 호출 함수
 * - osm
 * xml과 xslt 변환 처리 전역함수로 기능 분리 (해당 함수 호출하여 사용)
 */
function processXmlData(data) {
    console.log(data);
    let jData = {
        type:"contents",
        fiType: data.fiType || ""
    };
    jData.type = data.nodeType !== "" ? data.nodeType : jData.type;

    let xslData = getXmlSheet(jData); // XSLT 데이터 로드
    let xslt = loadXML(xslData); // XSLT 문서 로드
    let xml = loadXML(data.returnData); // XML 데이터 로드

    // XSLTProcessor로 변환 처리
    let xsltProcessor = new XSLTProcessor();
    xsltProcessor.importStylesheet(xslt);
    return xsltProcessor.transformToFragment(xml, document);
}
/* --- */
