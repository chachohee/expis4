$.when( $.ready ).then(function(){
    //Document is ready

    //public Key
    getPublicKey();

    //submit
    bindSubmit();

});

var publicKey;

// 페이지가 로드될 때 공개 키를 가져온다.
function getPublicKey() {
    $.ajax({
        url: "/EXPIS/rsa/publicKey",
        type: "GET",
        success: function(data){
            publicKey = data;
        },
        error: function(xth, status, error){
            return "";
        }
    });
}

function bindSubmit() {
    let _btnSubmit = $("button[id='submit']");
    if (_btnSubmit.length == 0) return;

    _btnSubmit.on("click", function(e){
        e.preventDefault;

        // 공개 키가 준비되었는지 확인
        if (!publicKey) {
            alert("Public key is not loaded. Please try again later.");
            return;
        }

        const encrypt = new JSEncrypt();
        encrypt.setPublicKey(publicKey);

        // 사용자 입력 값 가져오기
        const loginId = ($('#loginId').val() || "").trim();
        const password = ($('#password').val() || "").trim();

        // 암호화된 데이터 생성
        let data = {loginId: loginId, password: password};
        const encryptedData = encrypt.encrypt(JSON.stringify(data));
        if (!encryptedData) {
            alert("Encryption failed. Please try again.");
            return;
        }

        console.log("Encrypted data:", encryptedData);

        let bizCode = $("input[name='bizCode']").val() || "";
        let url = `/EXPIS/${bizCode}/login`;

        fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: encryptedData
        }).then(response => {
            if (response.ok) {
                return response.json(); // JSON 형태로 응답을 파싱
            } else {
                throw new Error('Failed to log in.');
            }
        }).then(data => {
            if (data.status === 'success') {
                console.log("Login success data:", data);
                // alert("Login success!");
                // bizCode, loginId, name을 사용하여 처리
                const bizCode = data.BIZ_CODE;
                const loginId = data.loginId;
                const name = data.name;
                console.log("BizCode:", bizCode, "Login ID:", loginId, "Name:", name);

               window.location.href = `/EXPIS/` + bizCode + `/ietm/toMain.do`;
//                 window.location.href = `/`;
            } else {
                // 로그인 실패 시 처리
                console.log("Login failed:", data.message);
                alert(data.message);

                $("#login_user_id").val('');
                $("#login_user_pwd").val('');
                $("#login_user_id").focus();
            }
        }).catch(error => {
            console.error('Error', error);
            alert("An error occurred during login. Please try again.");
        });

    });

}
