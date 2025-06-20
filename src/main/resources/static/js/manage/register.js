$.when( $.ready ).then(function(){
    //Document is ready

    //Button Event
    bindButtonEvent();

});

//버튼 이벤트
function bindButtonEvent() {

    // systree 업로드
    let _btnUpload = $("#btn-upload");
    if ( _btnUpload.length > 0 ) {
        _btnUpload.on("click", function() {
            insertSystree();
        });
    }

    // Package T.O 등록
    let _btnPackage = $("#btn-package");
    if (_btnPackage.length > 0) {
        _btnPackage.on("click", function() {
            insertPackage();
        });
    }

    // Project T.O 등록
    let _btnProject = $("#btn-project");
    if (_btnProject.length > 0) {
        _btnProject.on("click", function() {
            insertProject();
        });
    }

    // T.O 등록
    let _btnSingle = $("#btn-single");
    if (_btnSingle.length > 0) {
        _btnSingle.on("click", function() {
            insertSingleTo();
        });
    }

    // T.O 삭제
    let _btnDeleteTo = $("#btn-delete-to");
    if (_btnDeleteTo.length > 0) {
        _btnDeleteTo.on("click", function() {
            deleteSingleTo();
        });
    }

    // 전체 프로젝트 삭제
    let _btnDeleteAll = $("#btn-delete-all");
    if (_btnDeleteAll.length > 0) {
        _btnDeleteAll.on("click", function() {
            deleteProject();
        });
    }

    // 전체 계통 삭제
    let _btnDeleteRelated = $("#btn-delete-related");
    if (_btnDeleteRelated.length > 0) {
        _btnDeleteRelated.on("click", function() {
            deleteSystree();
        });
    }
}

// 계통트리 SysTree 파일 등록
function insertSystree() {
    let _file = $("#sys_file_upload");
	let filePath = _file.val() || "";
	filePath = filePath.replace(/\\/g, "/");
	let fileName = filePath.substring(filePath.lastIndexOf("/")+1, filePath.length);

	if (fileName.toLowerCase() != "systree.xml") {
		//alert("파일이 적합하지 않습니다. 정확한 계통트리 파일을 업로드해주세요.");
		let msgInvalid = $("input[name='msg-invalid-upload']").val() || "파일이 적합하지 않습니다. 정확한 계통트리 파일을 업로드해주세요.";
		alert(msgInvalid);
		return false;
	} else {
		//계통트리(System Tree)를 등록하시겠습니까?
        let msgRegister = $("input[name='msg-systree-register']").val() || "계통트리(System Tree)를 등록하시겠습니까?";
		if ( !confirm(msgRegister)) {
		    return false;
		}

		let bizCode = $("input[name='bizCode']").val() || "";
        if (bizCode === "") return false;

        let url = "/EXPIS/"+ bizCode +"/manage/systreeInsert.do";
        let formData = new FormData();
        let file = _file[0].files[0];
        formData.append("file", file);

        $.ajax({
            url: url,
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: function(response){
                alert('File upload Successfully');
            },
            error: function(xth, status, error){
                alert("Failure upload file");
            }
        });
	}
}

// 프로젝트 TO 등록
function insertProject() {
	var filePath = $("#to_dir_path").val();
	filePath = filePath.replace(/\\/g, "\\");

	var param = $("#frmRegister").serialize();
	param = "to_dir_path=" + filePath;

	if(filePath == "") {
		//alert("파일이 적합하지 않습니다. 정확한 저작 폴더를 선택해주세요.");
		alert($("#admin_file_type_msg").val());
		return;
	} else {
		//Project를 등록하시겠습니까?
		if(!confirm($("#admin_project_insert_msg").val())) {
			return false;
		}
	}

    //TODO:
	//$("#loading_bar").show();

	$.ajax({
		type : "POST",
		url : "/EXPIS/"+ bizCode +"/manage/projectList.do",
		dataType : "json",
		data : param,
		success : function(data) {
			if (data.returnResult >= 0) {
				// 등록 실패한 경우 있는지 확인
				if (data.errorListCount && data.errorListCount >= 0) {
					var tempstr = "Data Insert Error["+data.errorListCount+"]"
					console.log("data.errorListCount : "+data.errorListCount);
					for(var i=0; i < data.errorListCount;i++){
						console.log(data.errorList[i].tocoId+"["+data.errorList[i].tocoId+"] : "+data.errorList[i].errMsg);
						tempstr += "\n TOCO_ID = "+data.errorList[i].tocoId+" : "+data.errorList[i].errMsg;
					}
					alert(tempstr);
				}else{
					alert($("#admin_project_insert_complete").val());
				}
			} else {
				alert("Insert Project Error!!");
				return false;
			}
		}
		, error : function() {
			alert("Insert Project Error!!");
		}
		, complete : function() {
			//$("#loading_bar").hide();
		}
	});
}

function insertProjectTo(filePath, toKey) {
	param = "to_key=" + toKey + "&to_dir_path=" + filePath;

	$.ajax({
		type : "POST",
		url : $("#proectName").val()+"register/singletoInsert.do",
		dataType : "json",
		data : param,
		success : function(data) {
			if (data.returnResult >= 0) {
			} else {
				alert("Error!!-1");
				return false;
			}
		}
		, error : function(e) {
			alert("Error!!-2");
		}
	});
}

// * 패키지 TO 등록
function insertPackage() {
	var filePath = $("#to_dir_path").val();
	filePath = filePath.replace(/\\/g, "\\");

	var param = $("#frmRegister").serialize();
	param = "to_dir_path=" + filePath;

	if(filePath == "" || filePath.split(".")[1] !="zip") {
		//alert("파일이 적합하지 않습니다. 정확한 패키지파일을 선택해주세요.");
		alert($("#admin_package_upload_msg").val());
		return;
	} else {
		//Package를 등록하시겠습니까?
		if(!confirm($("#admin_package_insert_msg").val())) {
			return false;
		}
	}

    //TODO:
	// $("#loading_bar").show();

	$.ajax({
		type : "POST",
		url : "/EXPIS/"+ bizCode +"/manage/packageInsert.do",
		dataType : "json",
		data : param,
		success : function(data) {
			if (data.returnResult >= 0) {
				//alert("Package 등록이 완료되었습니다.");
				alert($("#admin_package_insert_complete").val());
			} else {
				alert("Insert Package Error!!");
				return false;
			}
		}
		, error : function() {
			alert("Insert Package Error!!");
		}
		, complete : function() {
			// $("#loading_bar").hide();
		}
	});
}

// * 단일 TO 등록
function insertSingleTo() {

//	var toKey = $("#to_key").val();
	var toKey = "";
	var filePath = $("#to_dir_path").val() || "";

	filePath = filePath.replace(/\\/g, "/");
	var lastWord = filePath.substring(filePath.length-1, filePath.length);
	if (lastWord == "/") {
		filePath = filePath.substring(0, filePath.length-1);
	}
	console.log("filePath : "+filePath);

	var fileMdName = filePath.substring(filePath.lastIndexOf("/")+1, filePath.length);
	toKey = fileMdName;
	console.log("fileMdName : "+fileMdName);

	if (filePath == "") {
		//alert("파일이 적합하지 않습니다. 정확한 저작 폴더를 선택해주세요.");
		let msgInvalid = $("#msg-invalid-filepath").val() || "파일이 적합하지 않습니다. 정확한 저작 폴더를 선택해주세요.";
		alert(msgInvalid);
		return false;
	} else {
		//alert(gvType+"를 등록하시겠습니까?");
		let msgConfirm = $("#msg-insert-to").val() || "등록하시겠습니까?";
		if ( !confirm(msgConfirm)) {
			return false;
		}

        let bizCode = $("input[name='bizCode']").val() || "";
        if (bizCode === "") return false;

        let url = "/EXPIS/"+ bizCode +"/manage/singleToInsert.do";
        let pdata = {"to_key":encodeURIComponent(toKey), "to_dir_path":encodeURIComponent(filePath)};

        //TODO: Loading
        //$("#loading_bar").show();

        $.ajax({
            url: url,
            type: "POST",
            data: pdata,
            dataType: "json",
            success: function(data){
                console.log("returnResult : "+data);
                let status = data.status || "";
                let errorListCount = data.errorListCount || -1;

                if ( status != "success" || errorListCount >= 0 ) {
                    var tempstr = "Data Insert Error["+data.errorListCount+"]"

                    for(var i=0; i < data.errorListCount;i++){
                        console.log(data.errorList[i].tocoId+"["+data.errorList[i].tocoId+"] : "+data.errorList[i].errMsg);
                        tempstr += "\n TOCO_ID = "+data.errorList[i].tocoId+" : "+data.errorList[i].errMsg;
                    }
                    alert(tempstr);
                }else{
                    let msgComplete = $("#msg-complete-to").val() || "등록이 완료되었습니다.";
//                    alert($("#admin_"+gvTypeMsg+"_insert_complete").val());
                    alert(msgComplete);
                }
                location.reload();
            },
            error: function(xth, status, error){
                alert("Failure upload file");
            },
            complete : function() {
                //TODO: Loading
                //$("#loading_bar").hide();
            }
        });
	}
}

// * 전체 계통 및 TO 삭제
function deleteSystree() {

	//전체 계통 및 "+gvType+"을 삭제하시겠습니까?
	if (!confirm($("#admin_"+gvTypeMsg+"_delete_msg").val())) {
		return false;
	}

	//$("#loading_bar").show();

	//Form 형식의 Ajax Parameter
	var param = $("#frmRegister").serialize();
	param = "";

	$.ajax({
		type : "POST",
		url : "/EXPIS/"+ bizCode +"/manage/systreeDelete.do",
		dataType : "json",
		data : param,
		success : function(data) {
			if (data.returnResult > 0) {
				//alert("전체 계통 및 "+gvType+" 삭제가 완료되었습니다.");
				alert($("#admin_"+gvTypeMsg+"_delete_complete").val());
			} else {
				alert("Error!!-1");
			}
			location.reload();
		}
		, error : function(e) {
			alert("Error!!-2");
		}
		, complete : function() {
			//$("#loading_bar").hide();
		}
	});

}

// * 전체 TO 삭제
function deleteProject() {

	//'전체 T.M.(T.O.)를 삭제하시겠습니까?'
	if (!confirm($("#admin_"+gvTypeMsg+"_all_delete").val())) {
		return false;
	}

	//$("#loading_bar").show();

	//Form 형식의 Ajax Parameter
	var param = $("#frmRegister").serialize();
	param = "";
	//2023.04.10 jysi ADD : 파일 처리 포함 체크되어있으면 파라미터 생성
	if ($("#file_chk").is(":checked")){
		param += "file_processing=chk";
	}

	$.ajax({
		type : "POST",
		url : "/EXPIS/"+ bizCode +"/manage/projectDelete.do",
		dataType : "json",
		data : param,
		success : function(data) {
			if (data.returnResult > 0) {
				//전체 삭제가 완료되었습니다
				// 전체 프로젝트 삭제 완료 후 알림창에 "undefined" 출력되어 수정
				//  => 해당 hidden input 요소 id변경(admin.all.delete.complete >> admin_all_delete_complete)
				//alert($("#admin.all.delete.complete").val());
				alert($("#admin_all_delete_complete").val());
			} else {
				// returnResult가 -1이면 프로젝트 삭제 실패, -2면 파일처리만 실패
				if (data.returnResult == -2) {
					alert("File processing failed!!")
				} else {
					alert("Error!!-1");
				}
			}
			location.reload();
		}
		, error : function(e) {
			alert("Error!!-2");
		}
		, complete : function() {
			//$("#loading_bar").hide();
		}
	});

}

// * 단일 TO 삭제
function deleteSingleTo() {

	//'해당 T.M.(T.O.)를 삭제하시겠습니까?'
	if (!confirm($("#admin_this_"+gvTypeMsg+"_delete").val())) {
		return false;
	}
	//$("#loading_bar").show();

	//Form 형식의 Ajax Parameter
	var param = $("#frmRegister").serialize();
	// 교범명에 "&" 문자가 존재할 경우 삭제완료되었다고 경고창 출력되나 실제 교범삭제가 안되어 수정
	param = "to_key=" + encodeURIComponent($("#to_key2").val());

	$.ajax({
		type : "POST",
		url : "/EXPIS/"+ bizCode +"/manage/singletoDelete.do",
		dataType : "json",
		data : param,
		success : function(data) {
			if (data.returnResult > 0) {
				//해당 삭제가 완료되었습니다.
				alert($("#admin_this_delete_complete").val());
			} else {
				alert("Error!!-1");
			}
			location.reload();
		}
		, error : function(e) {
			alert("Error!!-2");
		}
		, complete : function() {
			//$("#loading_bar").hide();
		}
	});

}

function projectCoverInsert() {
	var filePath = $("#to_dir_path").val();
	filePath = filePath.replace(/\\/g, "\\");
	//2022 12 05 jysi EDIT : LSAM 교범명에 & 들어가는 경우 있어 인코딩 처리
	filePath = encodeURIComponent(filePath);

	var param = $("#frmRegister").serialize();
	param = "to_dir_path=" + filePath;

	if(filePath == "") {
		alert($("#admin_file_type_msg").val());
		return;
	}

	$("#loading_bar").show();

	$.ajax({
		type : "POST",
		url : $("#proectName").val()+"register/projectCoverInsert.do",
		dataType : "json",
		data : param,
		success : function(data) {
			console.log("data : "+data.returnResult);
			if(data.returnResult != "-1"){
				alert($("#admin_cover_insert_complete").val());
				location.reload();
			}else{
				alert("Project Cover Insert Error!!");
			}
		}, error : function() {
			alert("Project Cover Insert Error!!");
		}, complete : function() {
			$("#loading_bar").hide();
		}
	});
}
