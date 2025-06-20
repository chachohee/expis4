$.when( $.ready ).then(function(){
    // 전체 메시지 캐쉬
    loadAllMessages();
});

// 메시지 load
function loadAllMessages() {
    $.ajax({
        url: '/EXPIS/i18n',
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            storeMessages(data);
        }
    });
}

// 메시지 캐쉬
var msg_storage;
function storeMessages(data) {
    if ( typeof(window.sessionStorage) === 'undefined' ) {
        msg_storage = JSON.stringify(data);
    } else {
        sessionStorage.setItem('LANG', JSON.stringify(data));
    }
}

// 메시지 조회
function getMessages(key) {
    let msgData = sessionStorage ? sessionStorage.getItem('LANG') : msg_storage;
    if ( !msgData ) return '';
    let msgList = JSON.parse(msgData);
    if ( !msgList ) return '';
    return msgList[key];
}

// 메시지 적용
function applyMessages(area) {
    let parent = !!area ? area : ".main-content";
    let _spans = $("span[lang-key]", parent);
    if (_spans.length == 0) return;

    _spans.each(function(idx){
        let lKey = $(this).attr("lang-key");
        if (lKey === "") return;
        let msg = getMessages(lKey);
        $(this).text(msg);
        $(this).removeAttr("lang-key");
    });
}
