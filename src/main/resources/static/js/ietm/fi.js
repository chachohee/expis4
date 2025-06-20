const fiFontSize = 12;

function fiRender(fiTocoType, contents, fiType){
    if(fiTocoType === "") return;
    $("#fi").hide();
    $("#fipopup").hide();
    var fiTypeChk = true;

    //일반 fi
    if(fiTypeChk == true && fiTocoType == "FI") {
        //$(".main-content").hide();
        $("#fipopup").show();
        //fi시 검색 및 유효목록 사이즈 수정
        $("#fi").show();
        fiMake(contents, fiTocoType, fiType);
    }

    //교범내용 아래 fi
    if(fiTocoType == "DI_DESC"){
        fiMake(contents, fiTocoType, fiType);
    }

    //테이블 공란 <br/> 추가
    $('.fi-input-item').find('li').each(function() {
        if($(this).text() == "") {
            $(this).find('p').append("<br/>");
        }
    });
}

function canvasCreate(contents, fiTocoType, layerIdNameStr) {
    console.log("CALL canvasCreate fiTocoType : "+fiTocoType+", layerIdNameStr : "+layerIdNameStr);
    var height = 0;
    var width = 0;

    $(contents).find("node").each(function() {
        if(height < $(this).attr("y2")*1) {
            height = $(this).attr("y2");
            width = $(this).attr("x2");
        }
    });
    $(contents).find("entry").each(function() {
        if(height < $(this).attr("y2")*1) {
            height = $(this).attr("y2");
            width = $(this).attr("x2");
        }
    });

    height = height*1 + 200;
    width = width*1 + 100;

    if(fiTocoType == "DI_DESC") {
        $("#fi-link").append('<canvas id="canvas" width="1020px;" height="'+height+'"></canvas>');}
    else {
        $("#main_fi_contents").append('<canvas id="canvas" width="1350px;" height="'+height+'"></canvas>');
    }
    return "canvas";
}

function basicDraw(canvas) {
    var extcode = ",,,Basic";
    var triangle1 = new fabric.Triangle({
        left : 42, top : 38,
        width : 12, height : 12, angle : 180,
        fill : "black", hoverCursor : "default",
        ntype : "basic",
        extcode : extcode
    });
    var triangle2 = new fabric.Triangle({
        left : 220, top : 38,
        width : 12, height : 12, angle : 180,
        fill : "black",	ntype : "basic",
        hoverCursor : "default",
        extcode : extcode
    });
    var triangle3 = new fabric.Triangle({
        left : 675,	top : 38,
        width : 12,	height : 12, angle : 180,
        fill : "black", ntype : "basic",
        hoverCursor : "default",
        extcode : extcode
    });
    canvas.add(triangle1, triangle2, triangle3);

    try{
        var tempIdx = 0;
        var lang = $("#lang").val();
        if(lang == "en"){
            var text1 = new fabric.Text("FAULT CODE", {
                left : 15, top : 10,
                fontSize : fiFontSize,
                ntype : "basic",
                hoverCursor : "default",
                extcode : extcode
            });
            var text2 = new fabric.Text("FAULT ISOLATION", {
                left : 165,	top : 10,
                fontSize : fiFontSize,
                ntype : "basic",
                hoverCursor : "default",
                extcode : extcode
            });
            var text3 = new fabric.Text("CORRECTIVE ACTION", {
                left : 630,	top : 10,
                fontSize : fiFontSize,
                ntype : "basic",
                hoverCursor : "default",
                extcode : extcode
            });
            canvas.add(text1, text2, text3);
        }else{
            var text1 = new fabric.Text("결함코드", {
                left : 15, top : 10,
                fontSize : fiFontSize,
                ntype : "basic",
                hoverCursor : "default",
                extcode : extcode
            });
            var text2 = new fabric.Text("고장탐구 점검 절차", {
                left : 165,	top : 10,
                fontSize : fiFontSize,
                ntype : "basic",
                hoverCursor : "default",
                extcode : extcode
            });
            var text3 = new fabric.Text("결함 수정 조치", {
                left : 630,	top : 10,
                fontSize : fiFontSize,
                ntype : "basic",
                hoverCursor : "default",
                extcode : extcode
            });
            canvas.add(text1, text2, text3);
        }
    }catch (e) {
        var text1 = new fabric.Text("결함코드", {
            left : 15, top : 10,
            fontSize : fiFontSize,
            ntype : "basic",
            hoverCursor : "default",
            extcode : extcode
        });
        var text2 = new fabric.Text("고장탐구 점검 절차", {
            left : 165,	top : 10,
            fontSize : fiFontSize,
            ntype : "basic",
            hoverCursor : "default",
            extcode : extcode
        });
        var text3 = new fabric.Text("결함 수정 조치", {
            left : 630,	top : 10,
            fontSize : fiFontSize,
            ntype : "basic",
            hoverCursor : "default",
            extcode : extcode
        });
        canvas.add(text1, text2, text3);
    }
}

var allPartFiTocoType;

function fiMake(contents, fiTocoType, fiType, layerIdNameStr) {
    console.log("CALL fiMake contents : "+contents+", fiTocoType : "+fiTocoType+", layerIdNameStr : "+layerIdNameStr);
    allPartFiTocoType = "";

    if ( fiTocoType == "DI_DESC" && layerIdNameStr == "" ) {
        $("#fi-link" , "#"+layerIdNameStr).empty();
    } else {
        if(fiTocoType == "DI_DESC"){
            console.log("fiMake fi-link length : "+$(".fi-link").length);
            if($("#fi-link").length < 2){
                $("#fi-link").empty();
            }else{
                $("#fi-link:last").empty();
            }
        } else if(fiTocoType == "T"){
            $("#"+layerIdNameStr).empty();
        } else {
            $("#main_fi_contents").empty();
        }
    }
    allPartFiTocoType = fiTocoType;
    var canvasIdStr = canvasCreate(contents, fiTocoType, layerIdNameStr);
    console.log("fiMake canvasIdStr : "+canvasIdStr);
    var fiMode = $("#fi_mode").val(); // 단계별 : 01, 전체 : 02
    var fiMode = "02"; // 단계별 : 01, 전체 : 02
    var canvas = new fabric.Canvas(canvasIdStr);
    var rectText = "";

    try{
        contents = $.parseXML(contents);
        if(fiType === "23" || fiType === "29"){
            basicDraw(canvas);
        }

    }catch (e) {
        console.log("Xml Parser Error ReTry");
        if ( contents.indexOf("<?xml") > 0 ) {
            var tempFiXML = contents.substring(contents.indexOf("<?xml"),contents.indexOf("</faultinf>")+11);
            contents = $.parseXML(tempFiXML);
        }
        if ( typeof(contents) == "string" && contents.indexOf("&amp;") == -1  && contents.indexOf("&") > 0 ) {
            var tempFiXML = contents.replace(/&/gi, "&amp;");
            contents = $.parseXML(tempFiXML);
        }
    }

    var fiCheckType = false;
    $(contents).find("faultinf").each(function() {
        var ntype = $(this).attr("type");
        if(ntype =="field"){
            console.log("FI Create 야전급 ");
            fiCheckType = true;
        }
    });
    console.log("FI Create fiTocoType : "+fiTocoType+", fiCheckType : "+fiCheckType);
    $(contents).find("step").each(function() {

        var isModHeight = (fiCheckType && $(this).find('table').length > 0 ) ? true : false;
        var latestY2 = 0;

        $(this).children().each(function() {
            var ntype = $(this).attr("ntype");
            var extcode = $(this).attr("extcode");
            var exttype = $(this).attr("exttype");
            var nodekind = $(this).attr("nodekind");
            var beginnum = $(this).attr("beginnum");
            var multiLineX2 = 0;
            var shadowed = "";
            var lineshow = "black";

            fiTempContent = "";
            rectText = "";

            if(beginnum != undefined) {// 결함및식별 ②(특문숫자) 생성
                createBeginnum(canvas, $(this));
            }
            console.log("Create Text ntype : "+ntype+", extcode : "+extcode+", exttype : "+exttype+", nodekind : "+nodekind+", beginnum : "+beginnum);
            if(ntype != "FITable" && beginnum == undefined) {		//FITable 아닐 경우.
                stepId = $(this).parent().attr("id");
                x1 = Number($(this).attr("x1"));
                x2 = Number($(this).attr("x2"));
                y1 = Number($(this).attr("y1")) + 50;
                y2 = Number($(this).attr("y2")) + 50;

                if ( isModHeight ) {
                    if ( y1 < (latestY2) ) {
                        var meHeight = y2 - y1;
                        y1 = latestY2;
                        y2 = latestY2 + meHeight;
                    }
                    latestY2 = y2;
                }

                var tempIdStr = $(this).attr("id");
                if(!$(this).attr("x1")){
                    console.log("Create Text Location Find False continue");
                }
                var param = x1 + "," + x2 + "," + y1 + "," + y2 + "," + $(this).attr("id");
                console.log("Create Text param : "+param);

                var tooltip = $(this).attr("tooltip");

                //그림자 효과 체크
                if($(this).parent().attr("shadowed") == "true") {
                    shadowed = "blue 5px 5px 5px";
                }
                //결함코드 선 유/무 체크
                if(nodekind == "FaultCode" && ($(this).attr("lineshow") == undefined || $(this).attr("lineshow") == "false" || $(this).attr("lineshow") == "" )) {
                    lineshow = "rgba(255,0,0,0)";
                }
                // 네모 박스 그리기
                console.log("Make Box stepId : "+stepId+", ntype : "+ntype);
                //if(ntype == "diamond"){//마름모
                if(ntype == "diamond"){//마름모
                    var pointX1 = x1+((x2-x1)/2);
                    var pointX2 = x2;
                    var pointX3 = x1+((x2-x1)/2);
                    var pointX4 = x1;
                    var pointY1 = y1;
                    var pointY2 = y1+((y2-y1)/2);
                    var pointY3 = y2;
                    var pointY4 = y1+((y2-y1)/2);
                    console.log("Make Box diamond : "+pointX1+", "+pointX2+", "+pointX3+", "+pointX4);
                    console.log("Make Box diamond : "+pointY1+", "+pointY2+", "+pointY3+", "+pointY4);
                    var polygon = new fabric.Polygon([
                        {x:pointX1,y:pointY1},{x:pointX2,y:pointY2},{x:pointX3,y:pointY3},{x:pointX4,y:pointY4}
                    ],{
                        stroke : lineshow, fill : "white", hoverCursor : "default",
                        extcode : extcode, exttype : exttype, nodekind : nodekind,
                        id : param,	stepId : stepId, ntype : ntype, shadow : shadowed
                    })
                    canvas.add(polygon);
                }else if(ntype == "documentmove"){
                    var pointX1 = x1;
                    var pointX2 = x2;
                    var pointX3 = x2;
                    var pointX4 = x1+((x2-x1)/2);
                    var pointX5 = x1;
                    var pointY1 = y1;
                    var pointY2 = y1;
                    var pointY3 = y1+((y2-y1)/2);
                    var pointY4 = y2;
                    var pointY5 = y1+((y2-y1)/2);
                    var polygon = new fabric.Polygon([
                        {x:pointX1,y:pointY1},{x:pointX2,y:pointY2},{x:pointX3,y:pointY3},{x:pointX4,y:pointY4},{x:pointX5,y:pointY5},{x:pointX1,y:pointY1}
                    ],{
                        stroke : lineshow, fill : "white", hoverCursor : "default",
                        extcode : extcode, exttype : exttype, nodekind : nodekind,
                        id : param,	stepId : stepId, ntype : ntype, shadow : shadowed
                    })
                    canvas.add(polygon);
                }else if(ntype == "document"){
                    var circleLise = new fabric.Path("M"+x1+","+y2+" L"+x1+","+y1+" L"+x2+","+y1+" L"+x2+","+y2+
                        " Q"+(x2-((x2-x1)/4))+", "+(y2-((y2-y1)/7))+", "+(x2-((x2-x1)/2))+" ,"+y2+
                        " Q"+(x1+((x2-x1)/4))+", "+(y2+((y2-y1)/7))+", "+x1+" ,"+y2
                        ,{
                            objectCaching : false,
                            stroke : lineshow, fill : "white", hoverCursor : "default",
                            extcode : extcode, exttype : exttype, nodekind : nodekind,
                            id : param,	stepId : stepId, ntype : ntype, shadow : shadowed
                        }
                    );
                    canvas.add(circleLise);
                }else{
                    var rect;
                    if(ntype == "round"){//라운드 사각형
                        rect = new fabric.Rect({
                            left : x1, top : y1,
                            objectCaching : false,
                            rx : 10, ry:10,
                            stroke : lineshow, fill : "white", hoverCursor : "default",
                            width : x2-x1, height : y2-y1,
                            extcode : extcode, exttype : exttype, nodekind : nodekind,
                            id : param,	stepId : stepId, ntype : ntype, shadow : shadowed
                        });
                        rect.on('scaling',function(){
                            this.set({
                                width : this.width * this.scaleX,
                                height : this.height * this.scaleY,
                                scaleX : 1,
                                scaleY : 1
                            })
                        });
                    }else{
                        rect = new fabric.Rect({
                            left : x1, top : y1,
                            stroke : lineshow, fill : "white", hoverCursor : "default",
                            width : x2-x1, height : y2-y1,
                            extcode : extcode, exttype : exttype, nodekind : nodekind,
                            id : param,	stepId : stepId, ntype : ntype, shadow : shadowed
                        });
                    }
                    //툴팁 체크
                    if(tooltip != undefined && tooltip != "") {

                        var tooltip = "";
                        tooltip += '<span class="fi_tooltip" style="width: 500px;">';
                        tooltip += '	<div onmouseover="hideTooltip();" id="tooltip_'+stepId+'" class="tooltip" style="display:none;top:'+(y1-32)+'px;">'+$(this).attr("tooltip")+'</div>';
                        tooltip += '</span>';
                        $("#"+canvasIdStr).parent().append(tooltip);
                        rect.on("mouseover", function(e) {
                            showTooltip("", x1, y1, e);
                        });
                        rect.on("mouseout", function(e) {
                            hideTooltip();
                        });
                    }
                    canvas.add(rect).renderAll();
                }
                textLocationX = x1+5;
                textLocationY = y1+2;
                multiLineX2 = x2+5;

                // 주기박스생성
                if(nodekind == "FaultAlertNoteNode" || nodekind == "FaultAlertWarningNode"
                    || nodekind == "FaultStepAlertWarningNode" || nodekind == "FaultStepAlertNoteNode"
                    || nodekind == "FaultAlertAttentionNode" || nodekind == "FaultStepAlertAttentionNode") {

                    var line =  new fabric.Line([x1, y1+16, x2, y1+16], {
                        fill : "black",	stroke : "black",
                        extcode : extcode, exttype : exttype, nodekind : nodekind,
                        id : param,	stepId : stepId, ntype : ntype
                    });
                    canvas.add(line);
                    // 주기 있을시 다음에오는 텍스트 줄바꿈
                    textLocationY = textLocationY + 2.1 * 8;

                    var alertText = "";
                    var alertColor = "";
                    var createAlert = "";
                    // var alertTop = y1+26;
                    var alertTop = y1 - 5;
                    //
                    if(nodekind == "FaultAlertWarningNode" || nodekind == "FaultStepAlertWarningNode") {
                        alertText = "경고";
                        alertColor = "red";
                    } else if(nodekind == "FaultAlertNoteNode" || nodekind == "FaultStepAlertNoteNode") {
                        alertText = "주기";
                        alertColor = "yellow";
                    } else {
                        alertText = "주의";
                        alertColor = "dodgerBlue";
                    }

                    if(fiTocoType == "DI_DESC") {
                        alertTop = y1 - 5;
                    }else if(fiTocoType == "T") {
                        alertTop = y1 - 5;
                    }

                    var isNoPaddingAlert = false;
                    if( $("#bizCode").length > 0 && $("#bizCode").val() == "KTA" ) {	isNoPaddingAlert = true; 	}

                    if ( isNoPaddingAlert ) {
                        if(fiTocoType == "DI_DESC" || fiTocoType == "T") {
                            createAlert += '<p class="fi_unity_box '+ stepId +'" style="font-size:11px; text-align:center; top:'+ y1 +'px; left:'+ (x1+5) +'px; width:'+ (x2-x1-5) +'px; height:18px; padding:unset;">';
                        } else {
                            createAlert += '<p class="fi_unity_box '+ stepId +'" style="text-align:center; top:'+ y1 +'px; left:'+ (x1+10) +'px; width:'+ (x2-x1-5) +'px; height:18px; padding:unset;">';
                        }
                        createAlert += '	<span style="background-color:'+alertColor+'; padding:1px 3px; line-height:18px;">'+alertText+'</span>';
                        createAlert += '</p>';
                    } else {
                        if(fiTocoType == "DI_DESC") {
                            createAlert += '<p class="fi_unity_box '+ stepId +'" style="font-size:11px; text-align:center; top:'+ alertTop +'px; left:'+ (x1+20) +'px; width:'+ (x2-x1-20) +'px; height:18px;">';
                        } else if(fiTocoType == "T") {
                            createAlert += '<p class="fi_unity_box '+ stepId +'" style="font-size:11px; text-align:center; top:'+ alertTop +'px; left:'+ (x1+20) +'px; width:'+ (x2-x1-20) +'px; height:18px;">';
                        } else {
                            createAlert += '<p class="fi_unity_box '+ stepId +'" style="text-align:center; top:'+ alertTop +'px; left:'+ (x1+30) +'px; width:'+ (x2-x1-20) +'px; height:18px;">';
                        }
                        createAlert += '	<span style="background-color:'+alertColor+';">'+alertText+'</span>';
                        createAlert += '</p>';
                    }

                    if(fiTocoType == "DI_DESC") {
                        $("#"+canvasIdStr).parent().append(createAlert);
                        //$(".canvas-container:last").append(createAlert);
                    } else if(fiTocoType == "T"){
                        $("#"+layerIdNameStr).append(createAlert);
                    } else {
                        $("#main_fi_contents").append(createAlert);
                    }
                }

                var hipenCheck = 0;
                // var textTop = y1 + 30 + "px";
                // var textLeft = x1 + 30 + "px";
                var textTop = y1 + "px";
                var textLeft = x1 + "px";
                if(fiTocoType == "T") {
                    textTop = y1 + "px";
                    textLeft = x1 + "px";
                }

                if($("#bizCode") && $("#bizCode").val() == "KTA"){
                    textTop = y1 + "px";
                    textLeft = x1 + "px";
                }

                if($("#bizCode").length > 0 && $("#bizCode").val() == "MUAV"){
                    textTop = y1 + "px";
                    textLeft = x1 + "px";
                }
                var textWidth = x2 - x1 - 20 + "px";
                var textHeight = y2 - y1 - 10 + "px";
                var createText = "";
                var tagStepId = "";
                var tagToolTip = "";
                var type = "";
                console.log("Create Text textWidth : "+textWidth+", textHeight : "+textHeight+", textTop : "+textTop+", textLeft : "+textLeft);
                var textParentType = "";
                //텍스트 생성
                $(this).find("text").each(function() {
                    var linkid = "";
                    textParentType = $(this).parent().attr("type");
                    var text = $(this).text();

                    $(contents).find("icon").each(function() {
                        console.log("Create Text $(this).text() : "+$(this).text());
                        console.log("Create Text $(this).filename : "+$(this).attr("filename"));
                        console.log("Create Text $(this).iconid : "+$(this).attr("iconid"));
                        text = text.replace("#24;#"+$(this).attr("iconid")+";","<img src=\""+$(this).attr("filename")+"\" width=\"15px\" height=\"15px\" />").replace("/Icon/","/icon/");;
                    });
                    type = $(this).parent().attr("type");
                    tagStepId = $(this).parent().parent().attr("id");
                    if($(this).parent().parent().attr("id")){
                        console.log("Create Text $(this).parent().parent() : "+tagStepId);
                    }else{
                        console.log("Create Text Find Other : "+$(this).parent().parent().attr("id")+", "+$(this).parent().attr("id")+", "+$(this).attr("id"));
                    }
                    tagToolTip = $(this).parent().attr("tooltip");
                    console.log("Create Text type : "+type+", tagStepId : "+tagStepId+", tagToolTip : "+tagToolTip+", text : "+text);
                    if($(this).attr("linkid") != undefined && $(this).attr("linkid") != "") {
                        linkid = $(this).attr("linkid");
                    } else if($(this).parent().attr("linkid") != undefined && $(this).parent().attr("linkid") != "") {
                        linkid = $(this).parent().attr("linkid");
                    } else {
                        linkid = "";
                    }
                    if($(this).parent().attr("ntype") == "package") {
                        if(fiTocoType == "DI_DESC") {
                            textTop = y1 + 18 + "px";
                        } else if(fiTocoType == "T") {
                            textTop = y1 + 18 + "px";
                        } else {
                            textTop = y1 + 15 + "px";
                        }
                    }else if($(this).parent().parent() && $(this).parent().parent().attr("ntype") == "FITable") {
                        if(fiTocoType == "DI_DESC") {
                            textTop = y1 + 18 + "px";
                        } else if(fiTocoType == "T") {
                            textTop = y1 + 18 + "px";
                        } else {
                            textTop = y1 + 30 + 15 + "px";
                        }
                    }
                    console.log("Create Text $(this) : "+JSON.stringify($(this))+", textTop : "+textTop+", id : "+$(this).attr("id")+", nType : "+$(this).parent().parent().attr("ntype"));
                    console.log("Create Text nodekind : "+nodekind+", linkid : "+linkid+", text.length : "+text.length+", linktype : "+$(this).attr("linktype")+", fiTocoType : "+fiTocoType);

                    if(nodekind == "Faultcode") {
                        rectText += text;
                    } else {
                        if($(this).attr("linktype") == "link" || linkid != "") {
                            var multiViewType = "";
                            var multiListName = "";
                            var multiLinkId = "";
                            var multiToKey = "";
                            text = text.replace(/;/gi,"");
                            if($(this).attr("viewtype")){
                                multiViewType = $(this).attr("viewtype").split(",");
                            }else{
                                if($(this).parent().attr("viewtype")){
                                    multiViewType = $(this).parent().attr("viewtype").split(",");
                                }
                            }
                            if($(this).attr("listname")){
                                multiListName = $(this).attr("listname").split(",");
                            }else{
                                if($(this).parent().attr("listname")){
                                    multiListName = $(this).parent().attr("listname").split(",");
                                }
                            }
                            if($(this).attr("linkid") == null && $(this).attr("listname")) {
                                multiLinkId = $(this).attr("listname").split(",");
                            } else {
                                if($(this).attr("linkid")){
                                    multiLinkId = $(this).attr("linkid").split(",");
                                }else{
                                    if($(this).parent().attr("linkid")){
                                        multiLinkId = $(this).parent().attr("linkid").split(",");
                                    }
                                }
                            }
                            if($(this).attr("tmname")){
                                multiToKey = $(this).attr("tmname").split(",");
                            }else{
                                if($(this).parent().attr("tmname")){
                                    multiToKey = $(this).parent().attr("tmname").split(",");
                                }
                            }
                            console.log("Make Link Check multiViewType : "+multiViewType);
                            console.log("Make Link Check multiListName : "+multiListName);
                            console.log("Make Link Check multiLinkId   : "+multiLinkId);
                            console.log("Make Link Check multiToKey    : "+multiToKey);
                            var multiChecker = false;
                            console.log("Make Link indexOf : "+(multiListName+"").indexOf(","));
                            if((multiListName+"").indexOf(",") > 0) {
                                multiChecker = true;
                            }
                            if(!multiChecker) {
                                console.log("Single Link");
                                if(multiViewType == "" || multiViewType == "window" || multiViewType == "undefined" || multiViewType == undefined) {
                                    console.log("Single Link window");
                                    var tempRevelationtype = "01";
                                    if("테이블" == $(this).attr("revelationtype")){
                                        tempRevelationtype = "04";
                                    }else if("테이블" == $(this).parent().attr("revelationtype")){
                                        tempRevelationtype = "04";
                                    }
                                    if($(this).attr("revelationtype") == "RDN" || $(this).parent().attr("revelationtype") == "RDN") {
                                        rectText += '<a style="display:inline;letter-spacing:0px; cursor: pointer; color: blue;" onclick=\"onclickColorChange(this);rdnOpenWin(\''+$(this).attr("tmname")+'\', \''+$(this).attr("linkid")+'\', \'\', \''+tempRevelationtype+'\', \''+$(this).attr("listname")+'\', \''+$(this).attr("listname")+'\', \''+$(this).attr("listname")+'\');\">'+ text +'</a>';
                                    } else {
                                        if($(this).attr("sectionid") != $(this).attr("linkid") && $(this).attr("sectionid")){
                                            if($(this).parent().attr("linkid") && $(this).parent().attr("linkid") != ''){
                                                console.log("fiMake sectionid != linkid : "+$(this).parent().attr("linkid")+", "+($(this).parent().attr("sectionid")));
                                                rectText += '<a style="display:inline;letter-spacing:0px; cursor: pointer; color: blue;" onclick=\"onclickColorChange(this);viewExOpenWin(\''+$(this).parent().attr("tmname")+'\', \''+$(this).parent().attr("sectionid")+'\', \'\', \''+tempRevelationtype+'\', \''+$(this).parent().attr("sectionid")+'\');\">'+ text +'</a>';
                                            }else{
                                                console.log("fiMake sectionid != linkid : "+$(this).attr("linkid")+", "+($(this).attr("sectionid")));
                                                rectText += '<a style="display:inline;letter-spacing:0px; cursor: pointer; color: blue;" onclick=\"onclickColorChange(this);viewExOpenWin(\''+$(this).attr("tmname")+'\', \''+$(this).attr("sectionid")+'\', \'\', \''+tempRevelationtype+'\', \''+$(this).attr("sectionid")+'\');\">'+ text +'</a>';
                                            }
                                        }else{
                                            if($(this).parent().attr("linkid") && $(this).parent().attr("linkid") != ''){
                                                console.log("fiMake sectionid != linkid else parent");
                                                rectText += '<a style="display:inline;letter-spacing:0px; cursor: pointer; color: blue;" onclick=\"onclickColorChange(this);viewExOpenWin(\''+$(this).parent().attr("tmname")+'\', \''+$(this).parent().attr("linkid")+'\', \'\', \''+tempRevelationtype+'\', \''+$(this).parent().attr("linkid")+'\');\">'+ text +'</a>';
                                            }else{
                                                console.log("fiMake sectionid != linkid else ");
                                                rectText += '<a style="display:inline;letter-spacing:0px; cursor: pointer; color: blue;" onclick=\"onclickColorChange(this);viewExOpenWin(\''+$(this).attr("tmname")+'\', \''+$(this).attr("linkid")+'\', \'\', \''+tempRevelationtype+'\', \''+$(this).attr("linkid")+'\');\">'+ text +'</a>';
                                            }
                                        }
                                    }
                                }else{
                                    console.log("Single Link self");
                                    if(linkid != "") {
                                        console.log("Make Link Else Type1");
                                        if($(this).parent().attr("linkid") && $(this).parent().attr("linkid") != ''){
                                            if($(this).parent().attr("viewtype")){
                                                console.log("Make Link Check A Parent : "+$(this).parent().attr("viewtype"));
                                                rectText += '<a style="text-decoration:none; cursor: pointer; color: blue;" onclick=\"onclickColorChange(this);viewExContents(\''+$(this).parent().attr("tmname")+'\',\''+$(this).parent().attr("linkid")+'\', \'\',\'01\',\'\');\">'+ text +'</a>';
                                            }else{
                                                console.log("Make Link Check A this : "+$(this).attr("viewtype"));
                                                rectText += '<a style="text-decoration:none; cursor: pointer; color: blue;" onclick=\"onclickColorChange(this);viewExContents(\''+$(this).attr("tmname")+'\',\''+$(this).attr("linkid")+'\', \'\',\'01\',\'\');\">'+ text +'</a>';
                                            }
                                        }else{
                                            let isSectionMove = false;
                                            if( $("#bizCode") && $("#bizCode").val() == "KTA" ) { 	isSectionMove = true; 	}

                                            if ( isSectionMove ) {
                                                let _tmname = !!$(this).parent().attr("tmname") ? $(this).parent().attr("tmname") : $(this).attr("tmname");
                                                let _sectionid = !!$(this).parent().attr("sectionid") ? $(this).parent().attr("sectionid") : $(this).attr("sectionid");
                                                rectText += '<a style="text-decoration:none; cursor: pointer; color: blue;" onclick=\"viewExContents(\''+_tmname+'\', \''+_sectionid+'\', \'\', \'01\', \'\');\">'+ text +'</a>';
                                            } else {
                                                if($(this).parent().attr("tmname")){
                                                    console.log("Make Link Check A Else parent : "+$(this).parent().attr("tmname"));
                                                    rectText += '<a style="text-decoration:none; cursor: pointer; color: blue;" onclick=\"selectTo(\''+$(this).parent().attr("tmname")+'\', \'to\');\">'+ text +'</a>';
                                                }else{
                                                    console.log("Make Link Check A Else this : "+$(this).parent().attr("tmname"));
                                                    rectText += '<a style="text-decoration:none; cursor: pointer; color: blue;" onclick=\"selectTo(\''+$(this).attr("tmname")+'\', \'to\');\">'+ text +'</a>';
                                                }
                                            }
                                        }
                                    } else {
                                        if($(this).attr("revelationtype") == "RDN") {
                                            console.log("Make Link Else Type2");
                                            rectText += '<a style="display:inline;letter-spacing:0px; cursor: pointer; color: blue;" onclick=\"onclickColorChange(this);rdnOpenWin(\''+$(this).attr("tmname")+'\', \''+$(this).attr("linkid")+'\', \'\', \'01\', \''+$(this).attr("listname")+'\', \''+$(this).attr("listname")+'\', \''+$(this).attr("listname")+'\');\">'+ text +'</a>';
                                        } else {
                                            if(fiTocoType == "DI_DESC") {
                                                console.log("Make Link Else Type3");
                                                if($(this).parent().attr("tmname")){
                                                    if($(this).parent().attr("linkid") && $(this).parent().attr("linkid") != ''){
                                                        if($(this).parent().attr("viewtype") && $(this).parent().attr("viewtype") == "window"){
                                                            rectText += '<a style="display:inline; text-decoration:none; cursor: pointer; color: blue;" onclick=\"onclickColorChange(this);viewExOpenWin(\''+$(this).parent().attr("tmname")+'\', \''+$(this).parent().attr("linkid")+'\', \'\', \'01\', \'\');\">'+ text +'</a>';
                                                        }else{
                                                            rectText += '<a style="display:inline; text-decoration:none; cursor: pointer; color: blue;" onclick=\"onclickColorChange(this);viewExContents(\''+$(this).parent().attr("tmname")+'\', \''+$(this).parent().attr("linkid")+'\', \'\', \'01\', \'\');\">'+ text +'</a>';
                                                        }
                                                    }else{
                                                        rectText += '<a style="display:inline; text-decoration:none; cursor: pointer; color: blue;" onclick="selectTo(\''+$(this).parent().attr("tmname")+'\', \'to\');">'+ text +'</a>';
                                                    }
                                                }else{
                                                    if($(this).attr("linkid") && $(this).attr("linkid")!=''){
                                                        console.log("Make Link Check B : "+$(this).attr("viewtype")+", "+$(this).attr("linkid")+", "+$(this).attr("tmname"));
                                                        if($(this).attr("viewtype") && $(this).attr("viewtype") == "window"){
                                                            rectText += '<a style="display:inline; text-decoration:none; cursor: pointer; color: blue;" onclick="onclickColorChange(this);viewExOpenWin(\''+$(this).attr("tmname")+'\',\''+$(this).attr("linkid")+'\',\'\',\'01\',\'\');">'+text+'</a>';
                                                        }else{
                                                            rectText += '<a style="display:inline; text-decoration:none; cursor: pointer; color: blue;" onclick="onclickColorChange(this);viewExContents(\''+$(this).attr("tmname")+'\', \''+$(this).attr("linkid")+'\', \'\', \'01\', \'\');">'+ text +'</a>';
                                                        }
                                                    }else{
                                                        rectText += '<a style="display:inline; text-decoration:none; cursor: pointer; color: blue;" onclick=\"selectTo(\''+$(this).attr("tmname")+'\', \'to\');\">'+ text +'</a>';
                                                    }
                                                }
                                            } else if(fiTocoType == "T") {
                                                console.log("Make Link Else Type4");
                                                if($(this).parent().attr("linkid") && $(this).parent().attr("linkid") != ""){
                                                    console.log("Make Link Check C : "+$(this).parent().attr("viewtype"));
                                                    if($(this).parent().attr("viewtype") && $(this).parent().attr("viewtype") == "window"){
                                                        rectText += '<a style="display:inline; text-decoration:none; cursor: pointer; color: blue;" onclick="onclickColorChange(this);viewExOpenWin(\''+$(this).parent().attr("tmname")+'\',\''+$(this).parent().attr("linkid")+'\',\'\',\'01\',\'\');">'+text+'</a>';
                                                    }else{
                                                        rectText += '<a style="display:inline; text-decoration:none; cursor: pointer; color: blue;" onclick="onclickColorChange(this);viewExContents(\''+$(this).parent().attr("tmname")+'\', \''+$(this).parent().attr("linkid")+'\', \'\', \'01\', \'\');">'+ text +'</a>';
                                                    }
                                                }else{
                                                    rectText += '<a style="display:inline; text-decoration:none; cursor: pointer; color: blue;" onclick=\"selectTo(\''+$(this).parent().attr("tmname")+'\', \'to\');\">'+ text +'</a>';
                                                }
                                            } else {
                                                console.log("Make Link Else Type5");
                                                if($(this).attr("linkid") && $(this).attr("linkid") != ""){
                                                    console.log("Make Link Check D : "+$(this).attr("viewtype"));
                                                    if($(this).attr("viewtype") && $(this).attr("viewtype") == "window"){
                                                        rectText += '<a style="display:inline;letter-spacing:0px; cursor: pointer; color: blue;" onclick="onclickColorChange(this);viewExOpenWin(\''+$(this).attr("tmname")+'\',\''+$(this).attr("linkid")+'\',\'\',\'01\',\'\');">'+text+'</a>';
                                                    }else{
                                                        rectText += '<a style="display:inline;letter-spacing:0px; cursor: pointer; color: blue;" onclick="onclickColorChange(this);viewExContents(\''+$(this).attr("tmname")+'\', \''+$(this).attr("linkid")+'\', \'\', \'01\', \'\');">'+ text +'</a>';
                                                    }
                                                }else{
                                                    console.log("Make Link : "+$(this).attr("tmname"));
                                                    console.log("Make Link : "+$(this).parent().attr("tmname"));
                                                    rectText += '<a style="display:inline;letter-spacing:0px; cursor: pointer; color: blue;" onclick=\"selectTo(\''+$(this).attr("tmname")+'\', \'to\');\">'+ text +'</a>';
                                                }
                                            }
                                        }
                                    }
                                }
                            }else{
                                console.log("Multi Linker");
                                console.log("Make Link Check else multiLinkId : "+multiLinkId+", multiViewType  : "+multiViewType );
                                if(multiLinkId){
                                    rectText += '<a style="display:inline;letter-spacing:0px; cursor: pointer; color: blue;" onclick=\"showFiMultiLink(\''+multiLinkId[0]+'\', event)\">'+ text +'</a>';
                                }else{
                                    console.log("fiMake multiLinkId 없는경우 viewExOpenWin 사용하게 수정 else linkid : "+linkid);
                                    rectText += '<a style="display:inline;letter-spacing:0px; cursor: pointer; color: blue;" onclick=\"onclickColorChange(this);viewExOpenWin(\''+$(this).attr("tmname")+'\', \''+linkid+'\', \'\', \'01\', \'\');\">'+ text +'</a>';
                                }

                                var multiLink = "";

                                multiLink += '<span class="link-multi">';
                                multiLink += '	<div id="link_' + multiLinkId[0] + '" class="link-list" style="z-index:13;">';

                                var multiRevelationType = [];
                                if ( !!$(this).attr("revelationtype") ) {
                                    multiRevelationType = $(this).attr("revelationtype").split(",");
                                }

                                for(var cnt = 0; cnt < multiToKey.length; cnt++) {
                                    console.log("Make Link Check E : "+multiViewType[cnt]);
                                    if(multiViewType[cnt] == "self") {
                                        if(multiLinkId[cnt] && multiLinkId[cnt] != ""){
                                            multiLink += '<a class="multiLink" style="cursor:pointer;" onclick="onclickColorChange(this);viewExContents(\''+multiToKey[cnt]+'\',\''+multiLinkId[cnt]+'\',\'\',\'01\',\'\');">'+multiListName[cnt]+'</a>';
                                        }else{
                                            multiLink += '<a class="multiLink" style="cursor:pointer;" onclick="selectTo(\''+multiToKey[cnt]+'\',\'to\');">'+multiListName[cnt]+'</a>';
                                        }
                                    } else {
                                        if(multiRevelationType[cnt] == "RDN") {
                                            var tempRevelationtype = "01";
                                            if("테이블" == multiRevelationType[cnt]){
                                                tempRevelationtype = "04";
                                            }else if("테이블" == $(this).parent().attr("revelationtype")){
                                                tempRevelationtype = "04";
                                            }

                                            multiLink +=' <a class="multiLink" style="cursor:pointer;" onclick=\"onclickColorChange(this);rdnOpenWin(\''+multiToKey[cnt]+'\', \''+multiLinkId[cnt]+'\', \'\', \''+tempRevelationtype+'\', \''+multiListName[cnt]+'\', \''+multiListName[cnt]+'\', \''+multiListName[cnt]+'\');\">'+ text +'</a>'
                                        } else {
                                            multiLink += '<a class="multiLink" style="cursor:pointer;" onclick="onclickColorChange(this);viewExOpenWin(\''+multiToKey[cnt]+'\',\''+multiLinkId[cnt]+'\',\'\',\'01\',\'\');">'+multiListName[cnt]+'</a>';
                                        }
                                    }
                                }
                                multiLink += '</div>';
                                multiLink += '</span>';

                                if(fiTocoType == "DI_DESC") {
                                    if($("#opener_link").val() == "link"){
                                        $(".fi_canvas_box:last").append(multiLink);
                                    }else{
                                        $(".wrap_content:last").append(multiLink);
                                    }
                                } else {
                                    if($("#opener_link").val() == "link"){
                                        $(".fi_canvas_box:last").append(multiLink);
                                    }else{
                                        $(".wrap_content:last").append(multiLink);
                                    }
                                }

                            }
                        } else {
                            rectText += text;
                        }
                    }
                });

                //교범검색 단어 하이라이트
                if($("#search_word").val() != "") {
                    var Rep = "";
                    if($("#search_word").val().split(",").length == 1) {
                        Rep = $("#search_word").val();
                        rectText = rectText.replace(new RegExp(Rep,"gi"),"<text style='color:red; font-weight:bold;'>"+Rep+"</text>");
                    } else {
                        for(var i=0; i < $("#search_word").val().split(",").length; i++) {
                            Rep = $("#search_word").val().split(",")[i];
                            rectText = rectText.replace(new RegExp(Rep,"gi"),"<text style='color:red; font-weight:bold;'>"+Rep+"</text>");
                        }
                    }
                }

                //전각문자 처리추가
                if(rectText.indexOf("#14;") >= 0){
                    console.log("Text Make EM TEXT : "+textParentType+", "+rectText.indexOf("#14;")+", "+rectText);
                    if(textParentType != "" && textParentType != undefined && textParentType != "undefined"){
                        var tempStrArry = Array.from(textParentType);
                        console.log("Text Make EM  tempStrArry["+tempStrArry.length+"] : "+tempStrArry);
                        for(var i=0;i<tempStrArry.length;i++){
                            console.log("Text Make EM  tempStrArry["+i+"] : "+tempStrArry[i]);
                            rectText = rectText.replace("#14;","<span class='fi_em_text"+tempStrArry[i]+"'>"
                                +"<img id='' src='"+$("#proectName").val()+"web/image/ietm/fi/Type_"+tempStrArry[i]+".jpg' class='icon' alt=''>"
                                +"</span>");
                        }
                    }
                }
                console.log("rectText PASS : "+rectText);
                //////////////////////////////////////////////////////////////////////////////////////////////////////////
                //////////////////////////////////////////////////////////////////////////////////////////////////////////
                rectText = rectText.replace(/#130;#700;#136;#50;/gi,"<span class='fi_subscrip_str'>");
                rectText = rectText.replace(/#130;#1000;#136;/gi,"</span>");
                rectText = rectText.replace(/&;/gi,"<br>");
                rectText = rectText.replace(/&#13;/gi,"<br>");
                rectText = rectText.replace(/#13;/gi,"<br>");
                rectText = rectText.replace(/lt;/gi,"<");
                rectText = rectText.replace(/gt;/gi,">");
                rectText = rectText.replace(/\&#255/gi,"");
                rectText = rectText.replace(/\&#254/gi,"");
                rectText = rectText.replace(/\&#200/gi,"");
                rectText = rectText.replace(/\&#1/gi,"");

                //추가 25-06-19
                rectText = rectText.replace(/&#0/gi,"");

                rectText = rectText.replace(/3,000/gi,"3000");
                console.log("rectText PASS1 : "+rectText);
                rectText = rectText.replace(/3000/gi,"3,000");
                rectText = rectText.replace(/#[0-9999]{4};/gi,"");
                rectText = rectText.replace(/#[0-999]{3};/gi,"");
                rectText = rectText.replace(/#[0-99]{2};/gi,"");
                rectText = rectText.replace(/#[0-9];/gi,"");
                rectText = rectText.replace(/#[0-9999]{4};/gi,"");
                rectText = rectText.replace(/#[0-999]{3};/gi,"");
                rectText = rectText.replace(/#[0-99]{2};/gi,"");
                rectText = rectText.replace(/#[0-9];/gi,"");
                console.log("rectText PASS2 : "+rectText);
                rectText = rectText.replace(/#-[0-99999]{5};/gi,"");
                rectText = rectText.replace(/#-[0-9999]{4};/gi,"");
                rectText = rectText.replace(/#-[0-999]{3};/gi,"");
                rectText = rectText.replace(/#-[0-99]{2};/gi,"");
                rectText = rectText.replace(/#-[0-9];/gi,"");
                //앞에 # 없는 경우 처리 추가(';'마크는 일반적으로 사용하지 않는다가 전제임)
                //; 사용시에 해당 코드로 인해 특정 글자 사라질수 있음 해당 코드는 다음 경우의 수가 나타나서 추가됨 ex)#2(8;#-25075;
                rectText = rectText.replace(/[0-9];/gi,"");

                //추가 25-06-19
                rectText = rectText.replace(/;5;;;6&;/gi,"");
                rectText = rectText.replace(/&/gi,"");
                rectText = rectText.replace(/amp#254amp#200amp#1amp#/gi,"");
                rectText = rectText.replace(/;;;;/,"");


                console.log("rectText : "+rectText);
                if ( $("#bizCode").length > 0 && $("#bizCode").val() == "BLOCK2" ) {
                } else {
                    if(type == "C") {
                        rectText = "　 " + rectText;
                    }
                }

                for(var i = 0; i < rectText.split("<br>").length; i++) {
                    if(rectText.split("<br>")[i].substring(0,1).indexOf("-") != -1) {
                        hipenCheck = 1;
                    }
                }

                if(nodekind == "FaultCode") {
                    if(tagToolTip == "") {
                        if(fiTocoType == "T") {
                            createText += '<p class="txt_lead faultcode" style="font-size: .9em;text-align: center; margin-top: 2px; top:'+ textTop +'; left:'+ textLeft +'; width:'+ (x2-x1) +'px; height:'+ (y2-y1) +'px;">'+ rectText +'</p>';
                        }else{
                            createText += '<p class="txt_lead faultcode" style="text-align: center; margin-top: 2px; top:'+ textTop +'; left:'+ textLeft +'; width:'+ (x2-x1) +'px; height:'+ (y2-y1) +'px;">'+ rectText +'</p>';
                        }
                    } else {
                        if(fiTocoType == "T") {
                            console.log("tagShowTooltip Case 1");
                            //Rack 만드는 부분에서 좌표 까지 처리해서 만들기 때문에 중복정인 마우스 오버 이벤트 제거
                            createText += '<p class="txt_lead faultcode" onmouseover=\"tagShowTooltip(\''+tagStepId+'\');\" onmouseout=\"hideTooltip();\" style="font-size: .9em;text-align: center; margin-top: 2px; top:'+ textTop +'; left:'+ textLeft +'; width:'+ (x2-x1) +'px; height:'+ (y2-y1) +'px;">'+ rectText +'</p>';
                        }else{
                            console.log("tagShowTooltip Case 2");
                            //Rack 만드는 부분에서 좌표 까지 처리해서 만들기 때문에 중복정인 마우스 오버 이벤트 제거
                            createText += '<p class="txt_lead faultcode" onmouseover=\"tagShowTooltip(\''+tagStepId+'\');\" onmouseout=\"hideTooltip();\" style="text-align: center; margin-top: 2px; top:'+ textTop +'; left:'+ textLeft +'; width:'+ (x2-x1) +'px; height:'+ (y2-y1) +'px;">'+ rectText +'</p>';
                        }
                    }
                } else {
                    console.log("Make TEXT Else hipenCheck : "+hipenCheck+", "+rectText.substring(0,8).match(/[0-9]+\. /gi)+", "+rectText.substring(0,4).match(/[가-라]+\. /gi)+", "+rectText.indexOf("3.")+", "+rectText.indexOf("5.")+", "+rectText.indexOf("●")+", "+rectText.indexOf("•"));
                    let bIndent = false;
                    if ( hipenCheck != 1 && rectText.substring(0,8).match(/[0-9]+\. /gi) == null && rectText.substring(0,4).match(/[가-라]+\. /gi) == null && (!dotPointChecker(rectText, "3.") || rectText.indexOf("3.") == -1) && (!dotPointChecker(rectText, "5.") || rectText.indexOf("5.") == -1) && rectText.indexOf("●") == -1 && rectText.indexOf("•") == -1 ) {
                        bIndent = true;
                    }
                    if ( $("#bizCode").length > 0 && $("#bizCode").val() == "BLOCK2" ) {
                        if (nodekind == "FaultTextNode" || nodekind == "FaultAlertWarningNode" || nodekind == "FaultStepAlertWarningNode" || nodekind == "FaultAlertNoteNode" || nodekind == "FaultStepAlertNoteNode") {
                            bIndent = true;
                        }
                    }
                    if ( $("#bizCode").length > 0 && $("#bizCode").val() == "MUAV" ) {
                        if (nodekind == "FaultTitleNode") {
                            bIndent = true;
                        }
                    }
                    if(bIndent) {
                        console.log("Make TEXT Else If in : "+type);
                        if(type == "ACD") {
                            createText += '<p class="fi_unity_box '+ tagStepId +'" style="top:'+ textTop +'; left:'+ textLeft +'; width:'+ textWidth +'; height:'+ textHeight +'">';
                            createText += '	<span style="background-color:red; font-weight:bold; padding-top:2px; padding-left:9px; padding-right:9px;">ACD</span>';
                            createText += ''+ rectText +'</p>';
                        } else {
                            // if($("#to_key").val().indexOf("94FI") == -1) {
                            if($("#to_key").val() == -1) {
                                textWidth = x2 - x1 - 16 + "px";
                            } else {
                                textWidth = x2 - x1 - 14 + "px";
                            }
                            if(fiTocoType == "DI_DESC") {
                                console.log("Make textTop fiTocoType");
                                textTop = y1 + "px";
                                textLeft = x1 + "px";
                                textWidth = x2 - x1 - 5 + "px";
                                textHeight = y2 - y1 - 10 + "px";

                                if(ntype == "package") {
                                    console.log("Make textTop package");
                                    textTop = y1 + 14 + "px";
                                }

                                if(tagToolTip == "") {
                                    console.log("fiMake P Maker Type A");
                                    createText += '<p class="fi_unity_box '+ tagStepId +'" style="font-size:11.5px; padding-left:5px; top:'+ textTop +'; left:'+ textLeft +'; width:'+ textWidth +'; height:'+ textHeight +'">'+ rectText +'</p>';
                                } else {
                                    console.log("fiMake P Maker Type B");
                                    //createText += '<p class="fi_unity_box '+ tagStepId +'" onmouseover=\"tagShowTooltip(\''+tagStepId+'\');\"  onmouseout=\"hideTooltip();\" style="font-size:11.5px; padding-left:5px; top:'+ textTop +'; left:'+ textLeft +'; width:'+ textWidth +'; height:'+ textHeight +'">'+ rectText +'</p>';
                                    if(ntype == "diamond"){//마름모
                                        createText += '<div class="fi_unity_box '+ tagStepId +'" onmouseout=\"hideTooltip();\" style="padding:unset; font-size:11.5px; top:'+ getFixSizeNum(textTop,5) +'; left:'+ getFixSizeNum(textLeft,5) +'; height:'+ textHeight +'; width:'+ textWidth +';">';
                                        createText += '<p style="width:'+ textWidth +'; height:'+ textHeight +';text-align: center;vertical-align: middle;display: table-cell;">'+ rectText +'</p>'+'</div>';
                                    }else if(ntype == "documentmove"){//오각형
                                        createText += '<div class="fi_unity_box '+ tagStepId +'" onmouseout=\"hideTooltip();\" style="padding:unset; font-size:11.5px; top:'+ getFixSizeNum(textTop,5) +'; left:'+ getFixSizeNum(textLeft,5) +'; height:'+ textHeight +'; width:'+ textWidth +';">';
                                        createText += '<p style="width:'+ textWidth +'; height:'+ textHeight +';text-align: center;vertical-align: middle;display: table-cell;">'+ rectText +'</p>'+'</div>';
                                    }else if(ntype == "document"){
                                        createText += '<div class="fi_unity_box '+ tagStepId +'" onmouseout=\"hideTooltip();\" style="padding:unset; font-size:11.5px; top:'+ getFixSizeNum(textTop,5) +'; left:'+ getFixSizeNum(textLeft,5) +'; height:'+ textHeight +'; width:'+ textWidth +';">';
                                        createText += '<p style="width:'+ textWidth +'; height:'+ textHeight +';text-align: center;vertical-align: middle;display: table-cell;">'+ rectText +'</p>'+'</div>';
                                    }else if(ntype == "round"){//라운드 사각형
                                        createText += '<div class="fi_unity_box '+ tagStepId +'" onmouseout=\"hideTooltip();\" style="padding:unset; font-size:11.5px; top:'+ getFixSizeNum(textTop,5) +'; left:'+ getFixSizeNum(textLeft,5) +'; height:'+ textHeight +'; width:'+ textWidth +';">';
                                        createText += '<p style="width:'+ textWidth +'; height:'+ textHeight +';text-align: center;vertical-align: middle;display: table-cell;">'+ rectText +'</p>'+'</div>';
                                    }else{
                                        let nodeTextAlign = $(this).attr("textalign") || "";
                                        let cssTextAlign = nodeTextAlign == "2" ? "text-align:center;" : "";
                                        createText += '<div class="fi_unity_box '+ tagStepId +'" onmouseout=\"hideTooltip();\" style="padding:unset; font-size:11.5px; top:'+ getFixSizeNum(textTop,5) +'; left:'+ getFixSizeNum(textLeft,5) +'; height:'+ textHeight +'; width:'+ textWidth +';">';
                                        createText += '<p style="width:'+ textWidth +'; height:'+ textHeight +';' + cssTextAlign + 'vertical-align: middle;display: table-cell;">'+ rectText +'</p>'+'</div>';
                                    }
                                    //console.log("Make createText : "+createText);
                                }
                            } else if(fiTocoType == "T") {
                                textTop = y1 + "px";
                                textLeft = x1 + "px";
                                textWidth = x2 - x1 - 5 + "px";
                                textHeight = y2 - y1 - 10 + "px";
                                console.log("fiMake P Maker Type T["+nodekind+"] : "+rectText);

                                if(nodekind == "FaultAlertNoteNode" || nodekind == "FaultAlertWarningNode"
                                    || nodekind == "FaultStepAlertWarningNode" || nodekind == "FaultStepAlertNoteNode"
                                    || nodekind == "FaultAlertAttentionNode" || nodekind == "FaultStepAlertAttentionNode") {
                                    createText += '<p class="fi_unity_box '+ tagStepId +'" onmouseout=\"hideTooltip();\" style="padding:unset; font-size:11px; top:'+ getFixSizeNum(textTop,20) +'; left:'+ getFixSizeNum(textLeft,5) +'; height:'+ textHeight +'; width:'+ textWidth +';">'+ rectText +'</p>';
                                }else{
                                    createText += '<p class="fi_unity_box '+ tagStepId +'" onmouseout=\"hideTooltip();\" style="padding:unset; font-size:11px; top:'+ getFixSizeNum(textTop,5) +'; left:'+ getFixSizeNum(textLeft,5) +'; height:'+ textHeight +'; width:'+ textWidth +';">'+ rectText +'</p>';
                                }
                            } else {
                                console.log("fiMake P Maker Type C");

                                if(ntype == "diamond"){//마름모
                                    createText += '<div class="fi_unity_box '+ tagStepId +'" style="top:'+ textTop +'; left:'+ textLeft +'; width:'+ textWidth +'; height:'+ textHeight +'; letter-spacing:0px;">';
                                    createText += '<p style="width:'+ textWidth +'; height:'+ textHeight +';text-align: center;vertical-align: middle;display: table-cell;">'+ rectText +'</p>'+'</div>';
                                }else if(ntype == "documentmove"){//오각형
                                    createText += '<div class="fi_unity_box '+ tagStepId +'" style="top:'+ textTop +'; left:'+ textLeft +'; width:'+ textWidth +'; height:'+ textHeight +'; letter-spacing:0px;">';
                                    createText += '<p style="width:'+ textWidth +'; height:'+ textHeight +';text-align: center;vertical-align: middle;display: table-cell;">'+ rectText +'</p>'+'</div>';
                                }else if(ntype == "document"){
                                    createText += '<div class="fi_unity_box '+ tagStepId +'" style="top:'+ textTop +'; left:'+ textLeft +'; width:'+ textWidth +'; height:'+ textHeight +'; letter-spacing:0px;">';
                                    createText += '<p style="width:'+ textWidth +'; height:'+ textHeight +';text-align: center;vertical-align: middle;display: table-cell;">'+ rectText +'</p>'+'</div>';
                                }else if(ntype == "round"){
                                    createText += '<div class="fi_unity_box '+ tagStepId +'" style="top:'+ textTop +'; left:'+ textLeft +'; width:'+ textWidth +'; height:'+ textHeight +'; letter-spacing:0px;">';
                                    createText += '<p style="width:'+ textWidth +'; height:'+ textHeight +';text-align: center;vertical-align: middle;display: table-cell;">'+ rectText +'</p>'+'</div>';
                                }else{
                                    let nodeTextAlign = $(this).attr("textalign") || "";
                                    let cssTextAlign = nodeTextAlign == "2" ? "text-align:center;" : "";
                                    createText += '<div class="fi_unity_box '+ tagStepId +'" style="top:'+ textTop +'; left:'+ textLeft +'; width:'+ textWidth +'; height:'+ textHeight +'; letter-spacing:0px;">';
                                    createText += '<p style="width:'+ textWidth +'; height:'+ textHeight +';' + cssTextAlign + 'vertical-align: middle;display: table-cell;">'+ rectText +'</p>'+'</div>';
                                }
                            }
                        }
                    } else {
                        var splitText = rectText.split("<br>");

                        textWidth = x2 - x1 - 5 + "px";


                        if ( $("#bizCode").length > 0 && $("#bizCode").val() == "BLOCK2" ) {
                            textWidth = (x2 - x1 - 15) + "px";
                        }

                        if(fiTocoType == "T") {
                            console.log("fiMake Ul Maker Type A");
                            createText += '<ul class="fi_txt_box '+ tagStepId +'" style="top:'+ textTop +'; left:'+ textLeft +'; width:'+ textWidth +'; height:'+ textHeight +'">';
                        }else{
                            console.log("fiMake Ul Maker Type B : "+fiCheckType+", textTop : "+textTop+", textLeft : "+textLeft+", x1 : "+x1+", y1 : "+y1+", nodekind : "+nodekind);
                            if(fiCheckType){
                                textTop = y1 + "px";
                                textLeft = x1 + "px";
                            }

                            if($("#to_key").val() > 0){

                                if(nodekind == "FaultAlertNoteNode" || nodekind == "FaultAlertWarningNode"
                                    || nodekind == "FaultStepAlertWarningNode" || nodekind == "FaultStepAlertNoteNode"
                                    || nodekind == "FaultAlertAttentionNode" || nodekind == "FaultStepAlertAttentionNode") {
                                    createText += '<ul class="fi_txt_box '+ tagStepId +'" style="top:'+ (y1+20)+"px" +'; left:'+  (x1)+"px" +'; width:'+ textWidth +'; height:'+ textHeight +'">';
                                }else{
                                    createText += '<ul class="fi_txt_box '+ tagStepId +'" style="top:'+ (y1)+"px" +'; left:'+  (x1)+"px" +'; width:'+ textWidth +'; height:'+ textHeight +'">';
                                }
                            }else{
                                createText += '<ul class="fi_txt_box '+ tagStepId +'" style="top:'+ textTop +'; left:'+ textLeft +'; width:'+ textWidth +'; height:'+ textHeight +'">';
                            }
                        }


                        for(var i=0; i < splitText.length; i++) {
                            var wordBreak = "keep-all";
                            if(hipenCheck == 0) {
                                if( (rectText.indexOf("●") != -1 && splitText[i].indexOf("●") == -1) || (rectText.indexOf("•") != -1 && splitText[i].indexOf("•") == -1) ||
                                    ( (rectText.indexOf("●") == -1 && rectText.indexOf("•") == -1 ) && rectText.match(/[0-9]+\./gi) != 0 && splitText[i].match(/[0-9]+\./gi) == null ) ) {

                                    if(splitText[i].indexOf("●") == -1 && splitText[i].match(/[0-9]+\./gi) == null && splitText[i].substring(0,4).match(/[가-사]+\./gi) == null) {
                                        if(fiTocoType == "T") {
                                            createText += '<li style="font-size:11px;word-break:'+wordBreak+'; padding-left:2px; text-indent:0px;">'+ splitText[i] +'</li>';
                                        }else{
                                            createText += '<li style="word-break:'+wordBreak+'; padding-left:2px; text-indent:0px;">'+ splitText[i] +'</li>';
                                        }
                                    } else if(rectText.indexOf("●") != -1 && rectText.match(/[0-9]+\./gi) != null && splitText[i].substring(0,4).match(/[가-사]+\./gi) != null) {
                                        if(fiTocoType == "T") {
                                            createText += '<li style="font-size:11px;word-break:'+wordBreak+'; padding-left:35px;">'+ splitText[i] +'</li>';
                                        }else{
                                            createText += '<li style="word-break:'+wordBreak+'; padding-left:35px;">'+ splitText[i] +'</li>';
                                        }
                                    } else if(splitText[i].indexOf("•") == -1 && splitText[i].match(/[0-9]+\./gi) == null && splitText[i].substring(0,4).match(/[가-사]+\./gi) == null) {
                                        if(fiTocoType == "T") {
                                            createText += '<li style="font-size:11px;word-break:'+wordBreak+'; padding-left:2px; text-indent:0px;">'+ splitText[i] +'</li>';
                                        }else{
                                            createText += '<li style="word-break:'+wordBreak+'; padding-left:2px; text-indent:0px;">'+ splitText[i] +'</li>';
                                        }
                                    } else if(rectText.indexOf("•") != -1 && rectText.match(/[0-9]+\./gi) != null && splitText[i].substring(0,4).match(/[가-사]+\./gi) != null) {
                                        if(fiTocoType == "T") {
                                            createText += '<li style="font-size:11px;word-break:'+wordBreak+'; padding-left:35px;">'+ splitText[i] +'</li>';
                                        }else{
                                            createText += '<li style="word-break:'+wordBreak+'; padding-left:35px;">'+ splitText[i] +'</li>';
                                        }
                                    } else {
                                        if(fiTocoType == "T") {
                                            createText += '<li style="font-size:11px;word-break:'+wordBreak+'; padding-left:25px;">'+ splitText[i] +'</li>';
                                        }else{
                                            createText += '<li style="word-break:'+wordBreak+'; padding-left:25px;">'+ splitText[i] +'</li>';
                                        }
                                    }
                                }
                                else {
                                    if(fiTocoType == "T") {
                                        createText += '<li style="font-size:11px;word-break:'+wordBreak+';">'+ splitText[i] +'</li>';
                                    }else{

                                        if(rectText.indexOf("•") != -1 ) {
                                            //createText += '<li style="list-style-position:inside;text-indent:-11px;padding-left:12px;list-style-type:none;">'+ splitText[i] +'</li>';
                                            createText += '<li style="list-style-position:inside;text-indent:-14px;padding-left:12px;list-style-type:none;">'+ splitText[i] +'</li>';
                                        }
                                        else {
                                            createText += '<li style>'+ splitText[i] +'</li>';
                                        }
                                    }
                                }
                            } else {
                                if(splitText[i].substring(0,4).indexOf("-") != -1) {
                                    if(fiTocoType == "T") {
                                        createText += '<li style="font-size:11px;word-break:'+wordBreak+';">'+ splitText[i] +'</li>';
                                    }else{
                                        createText += '<li style="word-break:'+wordBreak+';">'+ splitText[i] +'</li>';
                                    }
                                } else {
                                    if(fiTocoType == "T") {
                                        createText += '<li style="font-size:11px;word-break:'+wordBreak+'; padding-left:2px; text-indent:0px;">'+ splitText[i] +'</li>';
                                    }else{
                                        createText += '<li style="word-break:'+wordBreak+'; padding-left:2px; text-indent:0px;">'+ splitText[i] +'</li>';
                                    }
                                }
                            }
                        }
                        createText += '</ul>';
                    }
                }

                if(fiTocoType == "DI_DESC") {
                    $("#"+canvasIdStr).parent().append(createText);
                    //$(".canvas-container:last").append(createText);
                } else if(fiTocoType == "T"){
                    $("#"+layerIdNameStr).append(createText);
                } else {
                    console.log("fiMake createText : "+createText);
                    $("#main_fi_contents").append(createText);
                }

                createTableText += '</div>';

            } else {		// FITable 인 경우.
                var createChk = 0;
                var createTableText = "";
                // 테이블 생성
                var tempIdx = 0;

                var tableObject = $(this);
                var trIdx = 0;
                //Table 최상단에 colspan 있을경우 width 안먹는 현상 수정하기 위해 최초에 보이지 않는 tr 행 하나 만드는 로직 추가
                var tempTrCheck			= true;		//현재 TR이 정상 적으로 1개씩만 존재 했는지
                var tempHiddenTD		= "";		//보이지 않는 tr 행 String
                var tempHiddenAddFlag	= false;	//보이지 않는 tr 행 String이 추가됬는지
                var tempTRTD			= "";		//실제 TABLE에 추가될 내용
                var tempTableCloseFlag	= false;	//TABLE 정상 종료 여부 확인
                //Table td width 설정 처리하기 위해 추가
                var tempColGroup		= "";		//ColGroup 처리용


                //v2 저작으로 작성된 KTA의 FI 형태의 테이블 보정
                var isV2FITable = false;
                if( $("#bizCode") && $("#bizCode").val() == "KTA" ) { 	isV2FITable = true; 	}

                //테이블의 테두리 한번만 그리도록 변경
                x1 = Number($(this).attr("x1"));
                y1 = Number($(this).attr("y1")) +50;
                x2 = Number($(this).attr("x2"));
                y2 = Number($(this).attr("y2")) +50;

                //결함코드 야전급 일때, <step>내에 table, node가 혼재한 경우 높이 보정
                if ( isModHeight ) {
                    if ( y1 < (latestY2) ) {
                        var meHeight = y2 - y1;
                        y1 = latestY2;
                        y2 = latestY2 + meHeight;
                    }
                    latestY2 = y2;

                    //테이블 높이 재조정
                    var meRows = $(this).attr("rowcnt") || "1";
                    if ( meRows > 1 ) {
                        y2 = y1 + Number(meRows * 18);		//18 == lineheight 17px + border 1px
                        $(this).attr("y1", y1-50);
                        $(this).attr("y2", y2-50);
                        latestY2 = y2;
                    }
                }

                var param = x1 + "," + x2 + "," + y1 + "," + y2 + "," + $(this).attr("id");
                var stepId = $(this).parent().attr("id");
                var extcode = $(this).attr("extcode");

                // KTA인 경우에만 테이블 테두리로만 그리도록 변경
                if( isV2FITable ) {
                    //테이블 일 경우에만 테이블 테두리를 그리도록
                    if ( $(this).find("entry").length > 0 ) {
                        var rect = new fabric.Rect({
                            left : x1, top : y1,
                            stroke : "black", fill : "white",
                            width : x2-x1, height : y2-y1,
                            extcode : extcode, id : param,
                            tempStepId : stepId,
                            ntype: "table",
                            entryId : $(this).attr("id")
                        });
                        canvas.add(rect).renderAll();
                    }
                }

                $(this).find("entry").each(function() {
                    tempIdx++;
                    console.log("---------------------------Table each entry In---------------------------");
                    var stepId = $(this).parent().parent().attr("id");
                    var extcode = $(this).parent().attr("extcode");
                    var entryText = "";
                    $(this).find("text").each(function() {
                        entryText += fi_makeLinkStr($(this),contents,fiTocoType);
                    });
                    console.log("Table entryText : "+entryText);
                    //T#130;#10;#136;2.5#130;#12;#136;
                    var colcnt = $(this).parent().attr("colcnt") - 1;
                    var coladdr = $(this).attr("coladdr");
                    var colspan = $(this).attr("colspan");
                    if(!$(this).attr("colspan")){
                        colspan = 1;
                    }
                    ////////////////////////////////////////////////////////////////////////////////////////////
                    //colspan 여부 확인
                    ////////////////////////////////////////////////////////////////////////////////////////////
                    if(colspan == 1 || colspan == "1"){
                        console.log("Table Hidden Logic tempTrCheck : "+tempTrCheck+", colspan : "+colspan+", tempHiddenTD : "+tempHiddenTD);
                    }else{
                        tempTrCheck			= false;
                        tempHiddenTD		= "";
                        console.log("Table Hidden Logic tempTrCheck : "+tempTrCheck+", colspan : "+colspan+", tempHiddenTD : "+tempHiddenTD);
                        tempColGroup		= "";
                    }
                    var rowcnt = $(this).parent().attr("rowcnt");
                    var strokeColor = "black";
                    var rowaddr = 0;
                    var text;
                    var x1 = 0;
                    var y1 = 0;
                    var x2 = 0;
                    var y2 = 0;
                    console.log("Table stepId : "+stepId+", extcode : "+extcode+", entryText : "+entryText+", colcnt : "+colcnt+", coladdr : "+coladdr+", rowcnt : "+rowcnt);
                    console.log("Table rect x1 : "+x1+", y1 : "+y1+", x2-x1 : "+(x2-x1)+", y2-y1 : "+(y2-y1)+", extcode : "+extcode+", stepId : "+stepId);
                    var cols;
                    var rows;
                    var column;
                    var row;
                    if($(this).attr("x1")){
                        x1 = Number($(this).attr("x1"));
                        x2 = Number($(this).attr("x2"));
                        y1 = Number($(this).attr("y1")) + 50;
                        column = Number($(this).attr("coladdr"));
                        row = Number($(this).attr("rowaddr"));
                        if ( isV2FITable ) {
                            column = $(this).attr("col") ? Number($(this).attr("col")) : Number($(this).attr("coladdr"));
                            row = $(this).attr("row") ? Number($(this).attr("row")) : Number($(this).attr("rowaddr"));
                        }
                    }else{
                        cols = Number(tableObject.attr("cols"));
                        rows = Number(tableObject.attr("rows"));
                        column = Number($(this).attr("column"));
                        row = Number($(this).attr("row"));
                        if(tableObject.attr("cols")){
                            console.log("Table USE tableObject cols");
                        }else{
                            console.log("Table USE tableObject Data colcnt");
                            cols = isNaN(cols) ? Number($(this).parent().attr("colcnt")) : cols;
                            rows = isNaN(rows) ? Number($(this).parent().attr("rowcnt")) : rows;
                            column = isNaN(column) ? Number($(this).attr("coladdr")) : column;
                            row = isNaN(row) ? Number($(this).attr("rowaddr")) : row;
                        }
                        if(!$(this).attr("row")){
                            row = Number($(this).attr("rowaddr"));
                        }
                        console.log("Table USE Other cols : "+cols+", rows : "+rows+", column : "+column+", row : "+row);
                        x1 = Number(tableObject.attr("x1"));
                        x2 = Number(tableObject.attr("x2"));
                        y1 = Number(tableObject.attr("y1")) + 50;
                        var tempXpoint = x1+((x2-x1)/cols*(column+1));
                        console.log("Table USE Other x1 : "+x1+", x2 : "+x2+", y1 : "+y1+", tempXpoint : "+tempXpoint);
                        console.log("Table USE Other (x2-x1) : "+(x2-x1)+", (x2-x1)/cols : "+(x2-x1)/cols+", (column+1) : "+(column+1)+", tempXpoint : "+tempXpoint);
                        x2 = tempXpoint;
                    }
                    if($(this).attr("id") == "3B0552CE-C09E-41A3-BE14-412B5A39BA4B" || $(this).attr("id") == "937A4C50-3844-43C9-87AE-603AD4B39CBE") {
                        y2 = Number($(this).attr("y2")) + 50 + 14;
                    } else {
                        if($(this).attr("y2")){
                            y2 = Number($(this).attr("y2")) + 50;
                        }else{
                            y2 = Number(tableObject.attr("y2")) + 50;
                        }
                    }

                    var param = x1 + "," + x2 + "," + y1 + "," + y2 + "," + $(this).attr("id");

                    console.log("Table rect x1 : "+x1+", y1 : "+y1+", x2-x1 : "+(x2-x1)+", y2-y1 : "+(y2-y1)+", extcode : "+extcode+", stepId : "+stepId+", row : "+row);

                    //KTA가 아닌 경우 기존 로직으로 그리도록 변경
                    if( !isV2FITable ) {
                        var rect = new fabric.Rect({
                            left : x1, top : y1,
                            stroke : strokeColor, fill : "white",
                            width : x2-x1, height : y2-y1,
                            extcode : extcode, id : param,
                            tempStepId : stepId,
                            entryId : $(this).attr("id")
                        });
                        console.log("Table group param : "+param);
                        var group = new fabric.Group([rect/* , text */], {
                            hoverCursor : "default",
                            id : param,	stepId : stepId,
                            tempStepId : stepId,
                            ntype : "table", extcode : extcode
                        });
                        canvas.add(group);
                    }

                    var textLeft = (tableObject.attr("x1")*1 + 30) + "px";
                    var textTop = (tableObject.attr("y1")*1 + 50 + 30) + "px";
                    var textHeight = tableObject.attr("y2") - tableObject.attr("y1") + "px";
                    var textWidth = tableObject.attr("x2") - tableObject.attr("x1") + "px";
                    var spanWidth = textWidth;
                    var spanHeight = textHeight;
                    // 2024.02.21 - [T50]교범 검색 단어 하이라이트 - JSH
                    var searchHighlight = "color:red; font-weight:bold; ";
                    if($(this).attr("x1")){
                        console.log("Table DIV USER SELF LOACTIOM : "+fiTocoType);
                        if(fiTocoType == "DI_DESC") {
                            textLeft = ($(this).attr("x1")*1) + "px";
                            textTop = ($(this).attr("y1")*1+48) + "px";
                            spanWidth = $(this).attr("x2") - $(this).attr("x1");
                            spanHeight = $(this).attr("y2") - $(this).attr("y1");
                        }else if(fiTocoType == "T") {
                            textLeft = ($(this).attr("x1")*1) + "px";
                            textTop = ($(this).attr("y1")*1+48) + "px";
                            spanWidth = $(this).attr("x2") - $(this).attr("x1");
                            spanHeight = $(this).attr("y2") - $(this).attr("y1");
                        }else{

                            textLeft = ($(this).attr("x1")*1 + 30) + "px";
                            textTop = ($(this).attr("y1")*1 + 48 + 30) + "px";
                            textTop = ($(this).attr("y1")*1 + 48) + "px";
                            spanWidth = $(this).attr("x2") - $(this).attr("x1");
                            spanHeight = $(this).attr("y2") - $(this).attr("y1");
                        }
                    }else if(fiTocoType == "DI_DESC") {
                        console.log("Table DIV USER Parent Location DI_DESC");
                        textLeft = (tableObject.attr("x1")*1) + "px";
                        textTop = (tableObject.attr("y1")*1 + 48) + "px";
                        spanWidth = ( tableObject.attr("x2") - tableObject.attr("x1"))/cols;
                        spanHeight = (tableObject.attr("y2") - tableObject.attr("y1"))/rows;
                    }else{
                        console.log("Table DIV USER Parent Location");
                    }
                    console.log("Table DIV  textLeft : "+textLeft+", textTop : "+textTop+", textHeight : "+textHeight+", textWidth : "+textWidth+", spanWidth : "+spanWidth);
                    // if($("#to_key").val().indexOf("94FI") != -1 || $("#toco_id").val() == "b401dd06639f41f28c4c7aa2b067fd1b" || $("#toco_id").val() == "E382CDEF-747E-4089-9747-4107181BCEAF") {
                    //     textWidth = $(this).parent().attr("x2") - $(this).parent().attr("x1") + 1 + "px";
                    // }


                    textWidth = $(this).parent().attr("x2") - $(this).parent().attr("x1") + 1 + "px";


                    if(createChk == 0) {
                        if(fiTocoType == "DI_DESC") {
                            createTableText += '<div class="fi_bottom_box '+ stepId +'" style="letter-spacing:0.5px; top:'+ textTop +'; left:'+ textLeft +'; height:'+ textHeight +'; width:'+ textWidth +'; overflow: hidden;">';
                        }else{
                            createTableText += '<div class="fi_bottom_box '+ stepId +'" style="letter-spacing:0.5px; top:'+ textTop +'; left:'+ textLeft +'; height:'+ textHeight +';">';
                        }
                        if ( isV2FITable ) {
                            createTableText += '<table class="fi_bottom_table '+ stepId +'"><tr height="20px">';
                        } else {
                            createTableText += '<table class="fi_bottom_table ';
                            createTableText += fiCheckType ? 'field_table ' : '';
                            createTableText += stepId +'"><tr>';
                        }
//						createTableText += '<table class="fi_bottom_table '+ stepId +'"><colgroup/><tr>';
                        createChk++;
                    }
                    console.log("Table DIV  trIdx : "+trIdx+", row : "+row);

                    if(trIdx!=row){
                        trIdx = row;
                        if(fiTocoType == "DI_DESC") {
                            createTableText += "</tr><tr>";
                        }else{
                            ////////////////////////////////////////////////////////////////////////////////////////////
                            //라인 바꾸기 전에 전체가 1개 짜리로 TD 가 만들어 졌을경우 해당 tempHiddenTD을 createTableText에 추가하고 더이상 추가되지 않게 tempHiddenAddFlag = true;
                            ////////////////////////////////////////////////////////////////////////////////////////////
                            console.log("Table Hidden Logic Make TR tempTrCheck : "+tempTrCheck+", tempHiddenTD : "+tempHiddenTD);
                            if(tempTrCheck && !tempHiddenAddFlag){
                                createTableText += tempHiddenTD+"</tr><tr>"
                                tempHiddenAddFlag = true;
                            }else{
                                tempHiddenTD = "";
                            }
                            tempTrCheck = true;
                            tempTRTD += "</tr><tr>";
                            tempColGroup = "";
                            console.log("Table Hidden Logic tempTrCheck Reset : "+tempTrCheck);
                        }
                    }

                    if(fiTocoType == "DI_DESC") {
                        var cnvHeight = "";
                        if ( isV2FITable ) {
                            if ( entryText.split("<br/>").length > 1 ) {
                                let cHeight = entryText.split("<br/>").length * 16 -1;
                                cHeight = ((row*1) == 0 || row == '0') ? cHeight + 5 : cHeight;
                                cnvHeight = " height:" + ( cHeight ) + "px;";
                            }
                        }
                        if(((row*1) == 0 || row == '0')){
                            console.log("Table Check row In : "+row+", "+((row*1) == 0));
                            if(!fiCheckType){
                                if(tableObject.attr("top_row_align")){
                                    createTableText += '<td colspan="'+colspan+'" style="text-align:center;vertical-align:'+tableObject.attr("top_row_align")+'; border:1px solid; width:'+ spanWidth +'px;">'+ entryText +'</td>';
                                }else{
                                    createTableText += '<td colspan="'+colspan+'" style="text-align:center;vertical-align:top; border:1px solid; width:'+ spanWidth +'px;' +cnvHeight+ '">'+ entryText +'</td>';
                                }
                            }else{
                                if(tableObject.attr("top_row_align")){
                                    createTableText += '<td colspan="'+colspan+'" style="text-align:'+tableObject.attr("top_row_align")+';vertical-align:top; width:'+ spanWidth +'px;">'+ entryText +'</td>';
                                }else{
                                    createTableText += '<td colspan="'+colspan+'" style="text-align:center;vertical-align:top; width:'+ spanWidth +'px;' +cnvHeight+ '">'+ entryText +'</td>';
                                }
                            }
                        }else{
                            console.log("Table Check row else : "+row+", "+((row*1) == 0));
                            if(!fiCheckType){
                                createTableText += '<td colspan="'+colspan+'" style="vertical-align:top; padding:0px 0px 0px 3px; border:1px solid; width:'+ spanWidth +'px;' +cnvHeight+ '">'+ entryText +'</td>';
                            }else{
                                createTableText += '<td colspan="'+colspan+'" style="vertical-align:top; width:'+ spanWidth +'px;">'+ entryText +'</td>';
                            }
                        }

                    }else{
                        tempHiddenTD += '<td width="'+spanWidth+'px;" > </td>';
                        tempColGroup += '<col width="'+spanWidth+'px;"/>';
                        if(((row*1) == 0 || row == '0')){
                            console.log("Table Check row In : "+row+", "+((row*1) == 0));
                            if(tableObject.attr("top_row_align")){
                                tempTRTD += '<td width="'+spanWidth+'px;" colspan="'+colspan+'" style="text-align:'+tableObject.attr("top_row_align")+';vertical-align:top; padding:0px 0px 0px 5px; width:'+ spanWidth +'px; height:'+spanHeight+'px;">'+ entryText +'</td>';
                            }else{
                                tempTRTD += '<td width="'+spanWidth+'px;" colspan="'+colspan+'" style="text-align:center;vertical-align:top; padding:0px 0px 0px 5px; width:'+ spanWidth +'px; height:'+spanHeight+'px;">'+ entryText +'</td>';
                            }
                        }else{
                            console.log("Table Check row else : "+row+", "+((row*1) == 0));
                            // 2024.02.21 - T50, 검색어와 entryText 같으면 교범 검색단어 하이라이트  - JSH
                            if ($("#search_word").val() == entryText) {
                                tempTRTD += '<td width="'+spanWidth+'px;" colspan="'+colspan+'" style="vertical-align:top; padding:0px 0px 0px 5px; '+ searchHighlight + 'width:'+ spanWidth +'px; height:'+spanHeight+'px;">'+ entryText +'</td>';
                            }else {
                                tempTRTD += '<td width="'+spanWidth+'px;" colspan="'+colspan+'" style="vertical-align:top; padding:0px 0px 0px 5px; width:'+ spanWidth +'px; height:'+spanHeight+'px;">'+ entryText +'</td>';
                            }
                        }
                    }

                    rowaddr = $(this).attr("rowaddr");

                    console.log("Table Make Final Check : "+colcnt+", "+$(this).attr("colcnt")+", "+$(this).attr("coladdr"));
                    if(colcnt == $(this).attr("colcnt") || colcnt == $(this).attr("coladdr")){
                    }
                    if(colcnt == $(this).attr("colcnt")){
                        console.log("Table Make Final : "+fiTocoType);
                        if(fiTocoType == "DI_DESC") {
                            createTableText += '</tr></table></div>';
                        }else{
                            console.log("Table Make Final tempTRTD : "+tempTRTD);
                            ////////////////////////////////////////////////////////////////////////////////////////////
                            //그동안 만들어진  tempTRTD추가
                            ////////////////////////////////////////////////////////////////////////////////////////////
                            createTableText += tempTRTD;
                            createTableText += '</tr></table></div>';
                            tempTableCloseFlag = true;
                        }
                    }
                    console.log("Table Hidden Logic tempTRTD : "+tempTRTD);
                    console.log("Table Hidden Logic DIV colcnt : "+colcnt+", $(this).attr('colcnt') : "+$(this).attr("colcnt"));

                });
                ////////////////////////////////////////////////////////////////////////////////////////////
                //TABLE 정상 적으로 닫히지 앟았을경우 처리
                ////////////////////////////////////////////////////////////////////////////////////////////
                if(fiTocoType != "DI_DESC" && !tempTableCloseFlag){
                    createTableText += tempTRTD;
                    createTableText += '</tr></table></div>';
                    tempTableCloseFlag = true;
                }

                //Table 자식 노드를 못찾을 경우
                if(tempIdx == 0){
                    console.log("----------------------------------Create Table FI["+$(this).attr("id")+"]-------------------------------------");
                    var tableId = $(this).parent().attr("id");
                    var tableObj = $(this);
                    $(contents).find("entry").each(function(idx,element) {
                        console.log("idx : "+idx+", element : "+element);
                        var parentId = $(this).parent().attr("id");
                        console.log("---------------------------Table contents entry In "+tableId+","+parentId+"---------------------------");
                        if(tableId == parentId){
                            var stepId = tableObj.parent().attr("id");
                            var extcode = tableObj.attr("extcode");
                            var entryText = $(this).find("text").text().replace(/#13;/gi,"\n");
                            var colcnt = tableObj.attr("colcnt") - 1;
                            var coladdr = $(this).attr("coladdr");
                            var rowcnt = tableObj.attr("rowcnt");
                            var strokeColor = "black";
                            var rowaddr = 0;
                            var text;
                            console.log("Table stepId : "+stepId+", extcode : "+extcode+", entryText : "+entryText+", colcnt : "+colcnt+", coladdr : "+coladdr+", rowcnt : "+rowcnt);
                            if($(this).attr("x1")){
                                console.log("Table USE THIS");
                                x1 = Number($(this).attr("x1"));
                                x2 = Number($(this).attr("x2"));
                                y1 = Number($(this).attr("y1")) + 50;
                            }else{
                                console.log("Table USE Other : "+JSON.stringify($(this)));
                                var cols = Number(tableObj.attr("cols"));
                                var rows = Number(tableObj.attr("rows"));
                                var column = Number($(this).attr("column"));
                                var row = Number($(this).attr("row"));
                                console.log("Table USE Other cols : "+cols+", rows : "+rows+", column : "+column+", row : "+row);
                                x1 = Number(tableObj.attr("x1"));
                                x2 = Number(tableObj.attr("x2"));
                                y1 = Number(tableObj.attr("y1")) + 50;
                                //var tempXpoint = (((Number(tableObj.attr("x2")) - Number(tableObj.attr("x1")))/(cols*1))*($(this).attr("column")*1));
                                console.log("Table USE Other (x2-x1) : "+(x2-x1)+", (x2-x1)/cols : "+(x2-x1)/cols+", (column+1) : "+(column+1)+", tempXpoint : "+tempXpoint);
                                var tempXpoint = x1+((x2-x1)/cols*(column+1));
                                console.log("Table USE Other x1 : "+x1+", x2 : "+x2+", y1 : "+y1+", tempXpoint : "+tempXpoint);
                                x2 = tempXpoint;
                            }
                            if($(this).attr("id") == "3B0552CE-C09E-41A3-BE14-412B5A39BA4B" || $(this).attr("id") == "937A4C50-3844-43C9-87AE-603AD4B39CBE") {
                                y2 = Number($(this).attr("y2")) + 50 + 14;
                            } else {
                                if($(this).attr("y2")){
                                    y2 = Number($(this).attr("y2")) + 50;
                                }else{
                                    y2 = Number(tableObj.attr("y2")) + 50;
                                }
                            }

                            var param = x1 + "," + x2 + "," + y1 + "," + y2 + "," + $(this).attr("id");

                            console.log("Table rect x1 : "+x1+", y1 : "+y1+", x2-x1 : "+(x2-x1)+", y2-y1 : "+(y2-y1)+", extcode : "+extcode+", stepId : "+stepId);
                            console.log("Table strokeColor : "+strokeColor+", x1 : "+x1+", y1 : "+y1+", width : "+(x2-x1)+", height : "+(y2-y1));
                            var rect = new fabric.Rect({
                                left : x1, top : y1,
                                stroke : strokeColor, fill : "white",
                                width : x2-x1, height : y2-y1,
                                extcode : extcode, id : param,
                                entryId : $(this).attr("id")
                            });
                            var group = new fabric.Group([rect/* , text */], {
                                hoverCursor : "default",
                                id : param,	stepId : stepId,
                                ntype : "table", extcode : extcode
                            });
                            canvas.add(group);
                        }
                    });
                }

                if(fiTocoType == "DI_DESC") {
                    $("#"+canvasIdStr).parent().append(createTableText);
                    //$(".canvas-container:last").append(createTableText);
                } else if(fiTocoType == "T"){
                    $("#"+layerIdNameStr).append(createTableText);
                } else {
                    $("#main_fi_contents").append(createTableText);
                }
            }
        });
    });

    var extcodeTemp1 = "";
    var extcodeTemp6 = "";
    var extcodeCnt = 0;
    var tempLineIdx = 0;
    $(contents).find("node").each(function() {
        var arrowChk = 0;
        var leftArrowChk = 0;
        var ntype = $(this).attr("ntype");
        var extcode = $(this).attr("extcode");
        var lineExtArray = $(this).attr("extcode").split(",");
        var lineText = "";
        console.log("Line Make : "+$(this).attr("id")+" ==> "+$(this).attr("extcode"));
        if(lineExtArray.length == 18) {
            lineText = lineExtArray[0];
        } else {
            lineText = lineExtArray[0] + ", " + lineExtArray[1];
        }
        if($(this).attr("label") && $(this).attr("label") != ""){
        }
        if(lineText && lineText !="" && $(this).attr("label") && lineText != $(this).attr("label")){
            lineText = $(this).attr("label");
        }
        console.log("Line lineText : "+lineText);
        // 선그리기
        if(ntype == "line") {
            tempLineIdx++;
            if(lineExtArray.length == 18) {
                if(extcodeTemp1 == lineExtArray[5] || extcodeTemp6 == lineExtArray[1]) {

                } else {
                    extcodeCnt++;
                }

                extcodeTemp1 = lineExtArray[1];
                extcodeTemp5 = lineExtArray[5];
                extcodeTemp6 = lineExtArray[6];

            } else {
                if(extcodeTemp1 == lineExtArray[6] || extcodeTemp6 == lineExtArray[2]) {

                } else {
                    extcodeCnt++;
                }

                extcodeTemp1 = lineExtArray[2];
                extcodeTemp5 = lineExtArray[6];
                extcodeTemp6 = lineExtArray[7];
            }

            id = $(this).attr("id");
            x1 = Number($(this).attr("x1"));
            x2 = Number($(this).attr("x2"));
            y1 = Number($(this).attr("y1")) + 50;
            y2 = Number($(this).attr("y2")) + 50;

            var className = "lineClass" + extcodeCnt;
            var param = x1 + "," + x2 + "," + y1 + "," + y2 + "," + id;

            canvas.getObjects().forEach(function(event) {
                console.log("Line arrow lineExtArray.length : "+lineExtArray.length+", event.get(ntype)"+event.get("ntype")+", extcode.split(,)[6] : "+extcode.split(",")[6]+",  extcode.split(,)[7] : "+ extcode.split(",")[7]);
                if(lineExtArray.length == 18) {
                    if((event.get("ntype") == "documentmove" || event.get("ntype") == "document" || event.get("ntype") == "diamond" || event.get("ntype") == "round" || event.get("ntype") == "note" || event.get("ntype") == "table" || event.get("ntype") == "package") && extcode.split(",")[6] == event.get("extcode").split(",")[0]) {
                        arrowChk++;
                    }
                } else {
                    if((event.get("ntype") == "documentmove" || event.get("ntype") == "document" || event.get("ntype") == "diamond" || event.get("ntype") == "round" || event.get("ntype") == "note" || event.get("ntype") == "table" || event.get("ntype") == "package") && extcode.split(",")[7] == event.get("extcode").split(",")[0]) {
                        arrowChk++;
                    }
                }
            });
            //extcode 마지막요소 1; 인경우 왼쪽 화살표체크
            console.log("Line arrow x1 : "+x1+", x2 : "+x2+", fiTocoType : "+fiTocoType);
            if(x1 > x2 && fiTocoType != "DI_DESC") {
                $(contents).find("node").each(function() {
                    if($(this).attr("ntype") == "line") {
                        if($(this).attr("extcode").split(",").length == 18) {
                            if($(this).attr("extcode").split(",")[17] == "1;") {
                                arrowChk++;
                            }
                        } else {
                            if($(this).attr("extcode").split(",")[18] == "1;") {
                                arrowChk++;
                            }
                        }
                    }
                });
            }else if(x1 > x2 ) {
                $(contents).find("node").each(function() {
                    if($(this).attr("ntype") == "line") {
                        if($(this).attr("extcode").split(",").length == 18) {
                            if($(this).attr("extcode").split(",")[17] == "1;") {
                                arrowChk++;
                            }
                        } else {
                            if($(this).attr("extcode").split(",")[18] == "1;") {
                                arrowChk++;
                            }
                        }
                    }
                });
            }else if(x1 == x2  && y1 < y2) {
                if(lineExtArray[17] == "1;") {
                    console.log("Line arrow 2022 09 DI_DESC ADD triangle 17 : "+$(this).attr("id")+" ==> "+$(this).attr("extcode").split(",")[17]);
                    arrowChk++;
                }else if(lineExtArray[18] == "1;") {
                    console.log("Line arrow 2022 09 DI_DESC ADD triangle 18 : "+$(this).attr("id")+" ==> "+$(this).attr("extcode").split(",")[18]);
                    arrowChk++;
                }
            }
            console.log("Line arrow arrowChk : "+arrowChk);
            if(arrowChk != 0) {
                var arrowX = 0;
                var arrowY = 0;
                var angle = 0;

                if(x1 == x2 && x1 <= x2 && fiTocoType != "DI_DESC") {
                    arrowX = x2 + 6;
                    arrowY = y2 + 1;
                    if(y1 > y2) {
                        arrowX = x2 - 5;
                        angle = 0;
                    } else {
                        angle = 180;
                    }
                } else if(y1 == y2 && x1 <= x2) {
                    arrowX = x2 + 1;
                    arrowY = y2 - 5;
                    angle = 90;
                } else if(x1 > x2 && fiTocoType != "DI_DESC") {
                    arrowX = x2 - 1;
                    arrowY = y2 + 6;
                    angle = 270;
                } else {
                    if(x1 == x2 && x1 <= x2) {
                        arrowX = x2 + 6;
                        arrowY = y2 + 1;
                        if(y1 > y2) {
                            arrowX = x2 - 5;
                            angle = 0;
                        } else {
                            angle = 180;
                        }
                    } else if(y1 == y2 && x1 <= x2) {
                        arrowX = x2 + 1;
                        arrowY = y2 - 5;
                        angle = 90;
                    } else if(x1 > x2) {
                        arrowX = x2 - 1;
                        arrowY = y2 + 6;
                        angle = 270;
                    }else{
                    }
                }

                var procArrow = "";
                if ( $("#bizCode").length > 0 && $("#bizCode").val() == "BLOCK2" ) {
                    procArrow = "extra";
                }

                if ( procArrow == "extra" ) {
                    //모든 라인 끝부분에 화살표 추가
                    if ( lineExtArray[15] != '' && lineExtArray[15] != '0' ) {
                        var triangle = new fabric.Triangle({
                            width : 10,	height : 6,
                            left : arrowX, top : arrowY,
                            angle : angle,
                            id : param, ntype : "lineArrow",
                            extcode : extcode, class : className
                        });
                        canvas.add(triangle);
                    }
                    //라인 중간 합류하는 라인에 화살표 추가
                    if ( lineExtArray[17] == '1;' ) {
                        var triangle = new fabric.Triangle({
                            width : 10,	height : 6,
                            left : arrowX, top : arrowY,
                            angle : angle,
                            id : param, ntype : "lineArrow",
                            extcode : extcode, class : className
                        });
                        canvas.add(triangle);
                    }

                } else {
                    // 선 끝 화살표 모양 추가
                    if(arrowX != 0 && arrowY != 0) {
                        var triangle = new fabric.Triangle({
                            width : 10,	height : 6,
                            left : arrowX, top : arrowY,
                            angle : angle,
                            id : param, ntype : "lineArrow",
                            extcode : extcode, class : className
                        });
                        canvas.add(triangle);
                    }
                }


            }

            var line = makeLine([x1, y1, x2, y2], param, extcode, className);
            // 선 이벤트
            line.on("mouseover", function(e) {
                canvas.getObjects().forEach(function(event) {
                    $(".tooltip").hide();
                    if(event.get("class") == e.target.get("class") && event.get("ntype") != "lineText" && event.get("ntype") != "lineArrow") {
                        event.set("stroke", "blue");
                        event.set("strokeWidth", 3);
                    }
                });

                canvas.renderAll();
            });
            line.on("mouseout", function(e) {
                if (e.target && e.target.get("stroke") != "red") {
                    canvas.getObjects().forEach(function(event) {
                        if (
                            event.get("class") == e.target.get("class") &&
                            event.get("ntype") != "lineText" &&
                            event.get("ntype") != "lineArrow"
                        ) {
                            event.set("stroke", "black");
                            event.set("strokeWidth", 1);
                        }
                    });
                }
                canvas.renderAll();
            });
            line.on("mousedown", function(e) {
                canvas.getObjects().forEach(function(event) {
                    if(event.get("class") == e.target.get("class") && event.get("ntype") != "lineText" && event.get("ntype") != "lineArrow") {
                        event.set("stroke", "red");
                        event.set("strokeWidth", 3);
                    } else if(event.get("ntype") == "line") {
                        event.set("stroke", "black");
                        event.set("strokeWidth", 1);
                    }
                });
                if(fiMode == "02") {
                    canvasShowHide(canvas, e.target.get("id"), e.target.get("extcode"), e.target.get("class"));
                }
            });
            canvas.add(line);

            var locationX = 0;
            var locationY = 0;
            var boxWidth = 0;
            var textalign = "";

            lineText = lineText.replace(/\^/gi,"\n");
            // 텍스트 위치 조절
            if(x1 == x2) {
                locationX = (x1+x2)/2 - 105;
                locationY = ((y1+y2)/2) - 5;
                boxWidth = 100;
                textalign = "right";
            }
            if(y1 == y2) {
                if(x1 > x2) {
                    locationX = x2;
                    boxWidth = x1 - x2;
                } else {
                    locationX = x1;
                    boxWidth = x2 - x1;
                }
                locationY = ((y1+y2)/2) - 15;

                textalign = "center";
            }

            // 비정상, 정상 텍스트
            if(lineText != undefined && lineText != "") {
                var fiFontfamaily = "Gulimche, '맑은 고딕', 'Malgun Gothic', Dotum, sans-serif";
                var text = new fabric.Textbox(lineText, {
                    left : locationX, top : locationY,
                    width : boxWidth, height : 50,
                    fontSize : fiFontSize, hoverCursor : "default",
                    fontFamily: fiFontfamaily,
                    id : param, textAlign : textalign,
                    ntype : "lineText", extcode : extcode, class : className
                });
                canvas.add(text);
            }
        }
    });

    if(fiTocoType == "T"){
        createVersionInfo(contents, canvas, fiTocoType, canvasIdStr, layerIdNameStr);//변경바 추가
    }else{
        createVersionInfo(contents, canvas, fiTocoType, canvasIdStr);//변경바 추가
    }
    if ( $("#bizCode").length > 0 && $("#bizCode").val() == "BLOCK2" ) {
        // BLOCK2 일 경우 type C 아이콘이 중복으로 표시되는 문제로 호출 안함
    } else {
        createTypeInfo(contents, canvas);//아이콘 추가
    }

    canvas.getObjects().forEach(function(e) {// 클릭, 크기조절(fabric.js자체기능) 안되도록
        e.selectable = false;
    });
    console.log("fiMake  fiMode : "+fiMode+", fiTocoType : "+fiTocoType+", canvas : "+canvas);
    if(fiMode == "02" && fiTocoType == "FI") {// FI 단계별 보기일경우 첫번째 요소제외 hide
        if(tempLineIdx < 2){
            console.log("Line Number is : "+tempLineIdx);
        }else{
            fiDisplayType(canvas, fiMode);
        }
    }else if(fiMode == "02" && fiTocoType == "DI_DESC" && $("#bizCode") && $("#bizCode").val() == "KTA"){
        if(tempLineIdx < 2){
            console.log("Line Number is : "+tempLineIdx);
        }else{
            fiDisplayType(canvas, fiMode);
        }
    }
    canvas.renderAll();//마지막에 renderAll 안해주면 렌더링 시점 못잡아서 늦게뜨는경우있음
    $(".multiLink").css("color","#fff");
}

function makeLine(coords, param, extcode, className) {
    var fiMode = $("#fi_mode").val(); // 단계별 : 01, 전체 : 02
    var cursorType = "pointer";

    return new fabric.Line(coords, {
        fill : "black", stroke : "black", id : "lineId",
        hoverCursor : cursorType,
        id : param,	ntype : "line",
        extcode : extcode, class : className,
        padding : 5
    });
}

var showExtCode = 0;
function canvasShowHide(canvas, param, extcode, className) {
    console.log("CALL canvasShowHide canvas : "+canvas+", param : "+param+", extcode : "+extcode+", className : "+className+", allPartFiTocoType : "+allPartFiTocoType);
    var extcode6 = 0;
    var extcode0 = 0;
    var stepId = "";
    var count = 0;
    var x1 = 0;
    var x2 = 0;
    var y1 = 0;
    var y2 = 0;
    $(".loading").show();

    // show
    canvas.getObjects().forEach(function(e) {
        var ntype = e.get("ntype");
        if(ntype == "line" && e.get("class") == className) {
            var extcode = e.get("extcode").split(",");
            if(extcode.length == 18) {
                extcode6 = extcode[6];
            } else {
                extcode6 = extcode[7];
            }
        }
    });

    canvas.getObjects().forEach(function(e) {
        var ntype = e.get("ntype");
        var tagStepId = e.get("stepId");

        if(ntype != "line" && ntype != "basic" && e.get("extcode") != null && e.get("extcode").split(",")[0] == extcode6) {
            var extcode = e.get("extcode").split(",");
            e.set("opacity", 1);
            $("."+tagStepId).show();
            canvas.renderAll();
            x1 = e.get("id").split(",")[0] * 1;
            x2 = e.get("id").split(",")[1] * 1;
            y1 = e.get("id").split(",")[2] * 1;
            y2 = e.get("id").split(",")[3] * 1;
            extcode0 = extcode[0];
            stepId = e.get("stepId");
            count++;
        }
    });

    showRender(canvas, x1, x2, y1, y2, extcode0, stepId);

    // hide
    stepId = "";
    canvas.getObjects().forEach(function(event) {
        if(event.get("ntype") == "line" && event.get("class") == className) {
            var extcode5 = "";

            if(event.get("extcode").split(",").length == 18) {
                extcode5 = event.get("extcode").split(",")[5];
            } else {
                extcode5 = event.get("extcode").split(",")[6];
            }

            canvas.getObjects().forEach(function(e) {
                if(e.get("ntype") != "line" && e.get("ntype") != "basic" && e.get("extcode") != null && e.get("extcode").split(",")[0] == extcode5) {
                    if(e.get("stepId") != undefined) {
                        stepId = e.get("stepId");
                    }
                }
            });
        }
    });

    hideRender(canvas, stepId, className, showExtCode);

    $(".loading").hide();
}

function fiDisplayType(canvas, fiMode) {
    console.log("CALL fiDisplayType : "+fiMode+", canvas : "+canvas);
    var count = 0;
    var extCnt = 0;
    var extCheck = 0;
    var packageChk = 0;
    var codeYLocation = 0;
    var stepId = "";
    var tempTableStepId = "";
    var lineCode = new Array();
    var x1 = 0;
    var x2 = 0;
    var y1 = 0;
    var y2 = 0;

    $(".txt_lead").hide();
    $(".fi_txt_box").hide();
    $(".fi_bottom_box").hide();
    $(".fi_unity_box").hide();
    $(".version_info_box").hide();
    $(".faultcode").show();
    canvas.getObjects().forEach(function(e) {
        var ntype = e.get("ntype");
        var nodekind = e.get("nodekind");
        var tagStepId = e.get("stepId");
        var tempStepId = e.get("tempStepId");
        console.log("fiDisplayType ntype : "+ntype+", nodekind : "+nodekind+", tagStepId : "+tagStepId+", tempStepId : "+tempStepId+", e : "+e);
        if(tagStepId == undefined || tagStepId == "undefined" || !tagStepId){
            console.log("Temp e : "+e);
        }
        if(e.get("extcode") != null && e.get("extcode") != "") {
            var extcode = e.get("extcode").split(",");

            if((ntype == "package" || nodekind == "FaultCode") && extcode[0] == 1) {
                e.set("opacity", 1);
                console.log("Check ntype : "+ntype+", nodekind : "+nodekind+", extcode[0] : "+extcode[0]);
                if(nodekind != "FaultCode") {
                    $("."+tagStepId).show();
                }

                x1 = e.get("id").split(",")[0] * 1;
                x2 = e.get("id").split(",")[1] * 1;
                y1 = e.get("id").split(",")[2] * 1;
                y2 = e.get("id").split(",")[3] * 1;
                lineCode[count] = extcode[0];
                extCheck++;
                count++;

                if(nodekind == "FaultCode") {
                    codeYLocation = y1;
                }

                if(ntype == "package") {
                    packageChk = 1;
                }
            } else if(ntype != "basic" && nodekind != "Faultcode" && extcode[0] != 1) {
                e.set("opacity", 0);
                canvas.renderAll();
            }
        }
    });

    console.log("packageChk : "+packageChk);
    packageChk = 1;
    if(packageChk == 1) {
        canvas.getObjects().forEach(function(e) {
            var ntype = e.get("ntype");
            var nodekind = e.get("nodekind");
            var tagStepId = e.get("stepId");

            if(nodekind == "FaultCode") {
                console.log("packageChk  FaultCode : "+tagStepId);
                var extcode = e.get("extcode").split(",");

                e.set("opacity", 1);
                $("."+tagStepId).show();
                x1 = e.get("id").split(",")[0] * 1;
                x2 = e.get("id").split(",")[1] * 1;
                y1 = e.get("id").split(",")[2] * 1;
                y2 = e.get("id").split(",")[3] * 1;
                lineCode[count] = extcode[0];
                count++;
            } else if(ntype != "basic" && nodekind != "FaultCode"  && nodekind != "FaultAlertNoteNode" && nodekind != "FaultAlertWarningNode" && nodekind != "FaultStepAlertNoteNode" && nodekind != "FaultAlertAttentionNode") {
                //테두리 제거
                console.log("packageChk  else if : "+tagStepId);
                e.set("opacity", 0);
                canvas.renderAll();
            }
        });
    }

    console.log("extCheck : "+extCheck);
    if(extCheck == 0) {
        canvas.getObjects().forEach(function(e) {
            var ntype = e.get("ntype");
            var nodekind = e.get("nodekind");
            var tagStepId = e.get("stepId");

            if(nodekind == "FaultCode" || nodekind == "FaultTitleNode") {
                var extcode = e.get("extcode").split(",");

                e.set("opacity", 1);
                $("."+tagStepId).show();
                x1 = e.get("id").split(",")[0] * 1;
                x2 = e.get("id").split(",")[1] * 1;
                y1 = e.get("id").split(",")[2] * 1;
                y2 = e.get("id").split(",")[3] * 1;

                if(nodekind != "FaultCode") {
                    lineCode[count] = extcode[0];
                    count++;
                }

                if(TOKEY.indexOf("94FI") != -1 && nodekind == "FaultCode") {
                    lineCode[count] = extcode[0];
                    count++;
                }

                stepId = e.get("stepId");
                extCheck++;

                if(nodekind == "FaultTitleNode") {
                    codeYLocation = y1;
                }

            } else if(ntype != "basic" && nodekind != "FaultCode"  && nodekind != "FaultAlertNoteNode" && nodekind != "FaultAlertWarningNode" && nodekind != "FaultStepAlertNoteNode" && nodekind != "FaultAlertAttentionNode") {
                e.set("opacity", 0);
                canvas.renderAll();
            }
        });
    }

    canvas.getObjects().forEach(function(e) {
        var ntype = e.get("ntype");
        var nodekind = e.get("nodekind");
        var tagStepId = e.get("stepId");
        var tempStepId = "";

        if(((nodekind == "FaultStepAlertNoteNode" || nodekind == "FaultAlertNoteNode" || nodekind == "FaultAlertWarningNode" || nodekind == "FaultAlertAttentionNode")
                && (ntype == "text" || ntype == "package" || ntype == "version"))
            && codeYLocation > e.get("id").split(",")[3] * 1) {
            var tempX1 = e.get("id").split(",")[0];
            var tempY2 = e.get("id").split(",")[3];

            tempStepId = e.get("stepId");
            e.set("opacity", 1);
            $("."+tagStepId).show();

            if(TOKEY.indexOf("27FI") != -1) {
                canvas.getObjects().forEach(function(e) {
                    if(e.get("ntype") == "table" && e.get("id").split(",")[0] == tempX1 && e.get("id").split(",")[2] == tempY2) {
                        tempTableStepId = e.get("stepId");
                    }
                });
            }
        }

        if(e.get("extcode") != null && e.get("extcode") != undefined && e.get("extcode") != "") {
            if((ntype == "table" || ntype == "note" || ntype == "text" || ntype == "version" || e.get("stepId") == tempStepId || e.get("stepId") == tempTableStepId) && e.get("extcode").split(",")[0] != 1) {
                var extcode = e.get("extcode").split(",");

                currentX1 = e.get("id").split(",")[0] * 1;
                currentX2 = e.get("id").split(",")[1] * 1;
                currentY1 = e.get("id").split(",")[2] * 1;
                currentY2 = e.get("id").split(",")[3] * 1;

                if(e.get("nodekind") == "FaultTitleNode" || e.get("stepId") == stepId || e.get("stepId") == tempStepId || e.get("stepId") == tempTableStepId) {
                    e.set("opacity", 1);
                    $("."+tagStepId).show();
                    stepId = e.get("stepId");
                    lineCode[count] = extcode[0];
                    count++;
                }

                if((x1 <= currentX1 && currentX1 <= x2) || (x1 <= currentX2 && currentX2 <= x2)) {
                    if((y1 <= currentY1 && currentY1 <= y2) || (y1 <= currentY2 && currentY2 <= y2)) {
                        tempStepId = e.get("stepId");
                        x1 = e.get("id").split(",")[0] * 1;
                        x2 = e.get("id").split(",")[1] * 1;
                        y1 = e.get("id").split(",")[2] * 1;
                        y2 = e.get("id").split(",")[3] * 1;

                        lineCode[count] = extcode[0];
                        count++;

                        e.set("opacity", 1);
                        $("."+tagStepId).show();
                        canvas.renderAll();

                        canvas.getObjects().forEach(function(e) {
                            if((e.get("nodekind") == "FaultTextNode" || e.get("ntype") == "table") && e.get("stepId") == tempStepId) {
                                lineCode[count] = e.get("extcode").split(",")[0];
                                count++;
                                e.set("opacity", 1);
                            }

                            if(e.get("ntype") == "package" && e.get("stepId") == tempStepId) {
                                e.set("opacity", 1);
                            }

                            if(e.get("extcode") != null && e.get("extcode") != "") {
                                var extTemp = e.get("extcode").split(",");
                                if(extcode[0] == extTemp[5]) {
                                    extCnt++;
                                }
                            }
                        });
                    }
                }
            }
        }
    });
    console.log("extCnt : "+extCnt);
    if(extCnt == 0) {
        canvas.getObjects().forEach(function(e) {
            var ntype = e.get("ntype");
            var tagStepId = e.get("stepId");

            if(e.get("extcode") != null && e.get("extcode") != "") {
                var extcode = e.get("extcode").split(",");

                if(e.get("nodekind") == "FaultTitleNode" || e.get("stepId") == stepId) {
                    e.set("opacity", 1);
                    $("."+tagStepId).show();
                    lineCode[count] = extcode[0];
                    count++;
                }

                if((ntype == "table" || ntype == "package" || ntype == "note" || ntype == "text" || ntype == "version") && extcode[0] != 1) {
                    currentX1 = e.get("id").split(",")[0] * 1;
                    currentX2 = e.get("id").split(",")[1] * 1;
                    currentY1 = e.get("id").split(",")[2] * 1;
                    currentY2 = e.get("id").split(",")[3] * 1;

                    if((x1 <= currentX1 && currentX1 <= x2) || (x1 <= currentX2 && currentX2 <= x2)) {
                        if((y1 <= currentY1 && currentY1 <= y2) || (y1 <= currentY2 && currentY2 <= y2)) {
                            x1 = e.get("id").split(",")[0] * 1;
                            x2 = e.get("id").split(",")[1] * 1;
                            y1 = e.get("id").split(",")[2] * 1;
                            y2 = e.get("id").split(",")[3] * 1;
                            lineCode[count] = extcode[0];
                            count++;
                            e.set("opacity", 1);
                            $("."+tagStepId).show();
                            canvas.renderAll();
                        }
                    }
                }
            }
        });
    }

    for(var i = 0; i < lineCode.length; i++) {
        canvas.getObjects().forEach(function(e) {
            if(e.get("extcode") != null && e.get("extcode") != "") {
                var extcode = e.get("extcode").split(",");
                var tagStepId = e.get("stepId");

                if(lineCode[i] == extcode[5]) {
                    var className = e.get("class");

                    canvas.getObjects().forEach(function(e) {
                        if(e.get("class") == className) {
                            e.set("opacity", 1);
                            $("."+tagStepId).show();
                            canvas.renderAll();
                        }
                    });
                }
            }
        });
    }

}

function showRender(canvas, px1, px2, py1, py2, extcode0, stepId) {
    console.log("showRender px1 : "+px1+", px2 : "+px2+", py1 : "+py1+", py2 : "+py2+", extcode0 : "+extcode0+", stepId : "+stepId);
    var count = 1;
    var lineCode = new Array();
    lineCode[0] = extcode0;
    var x1 = px1;
    var x2 = px2;
    var y1 = py1;
    var y2 = py2;

    canvas.getObjects().forEach(function(e) {
        var ntype = e.get("ntype");
        var tagStepId = e.get("stepId");
        console.log("showRender canvas.getObjects() e["+e.get("stepId")+"] : "+e);
        if(ntype != "line" && ntype != "basic" && e.get("stepId") == stepId) {
            console.log("showRender e.get(extcode) : "+e.get("extcode"));
            if(e.get("extcode")){
                var extcode = e.get("extcode").split(",");
                e.set("opacity", 1);
                $("."+tagStepId).show();
                canvas.renderAll();
                x1 = e.get("id").split(",")[0] * 1;
                x2 = e.get("id").split(",")[1] * 1;
                y1 = e.get("id").split(",")[2] * 1;
                y2 = e.get("id").split(",")[3] * 1;
                lineCode[count] = extcode[0];
                showExtCode = extcode[0];
                count++;
            }else{
                console.log("showRender "+$("."+tagStepId));
                $("."+tagStepId).show();
            }
        }
    });

    canvas.getObjects().forEach(function(e) {
        var ntype = e.get("ntype");
        var tagStepId = e.get("stepId") || e.get("tempStepId");
        var prevNtype = "";
        console.log("showRender ntype : "+ntype+", tagStepId : "+tagStepId+", e.get(extcode) : "+e.get("extcode")+" ==> "+extcode0);
        if((ntype == "table" || ntype == "note" || ntype == "text") && e.get("extcode") != null && e.get("extcode").split(",")[0] != extcode0) {

            var extcode = e.get("extcode").split(",");

            currentX1 = e.get("id").split(",")[0] * 1;
            currentX2 = e.get("id").split(",")[1] * 1;
            currentY1 = e.get("id").split(",")[2] * 1;
            currentY2 = e.get("id").split(",")[3] * 1;

            if((x1 <= currentX1 && currentX1 <= x2) || (x1 <= currentX2 && currentX2 <= x2)) {
                if((y1 <= currentY1 && currentY1 <= y2) || (y1 <= currentY2 && currentY2 <= y2)) {
                    if(prevNtype != "table" && ntype != "note") {
                        x1 = e.get("id").split(",")[0] * 1;
                        x2 = e.get("id").split(",")[1] * 1;
                        y1 = e.get("id").split(",")[2] * 1;
                        y2 = e.get("id").split(",")[3] * 1;
                        lineCode[count] = extcode[0];
                        showExtCode = extcode[0];
                        count++;
                        e.set("opacity", 1);
                        $("."+tagStepId).show();
                        canvas.renderAll();
                    }
                }
            }
        }
        prevNtype = e.get("ntype");
    });

    for(var i = 0; i < lineCode.length; i++) {
        canvas.getObjects().forEach(function(e) {
            if(e.get("extcode") != null) {
                var extcode = e.get("extcode").split(",");
                var extcode5 = "";

                if(extcode.length == 18) {
                    extcode5 = extcode[5];
                } else {
                    extcode5 = extcode[6];
                }

                if(lineCode[i] == extcode5) {
                    var className = e.get("class");

                    canvas.getObjects().forEach(function(e) {
                        if(e.get("class") == className) {
                            e.set("opacity", 1);
                            $("."+e.get("stepId")).show();
                            canvas.renderAll();
                        }
                    });
                }
            }
        });
    }
}

function hideRender(canvas, stepId, className, showExtCode) {
    var extcode6 = new Array();
    var extcode6Cnt = 0;
    console.log("CALL hideRender stepId : "+stepId+", className : "+className+", showExtCode : "+showExtCode);
    canvas.getObjects().forEach(function(e) {
        var ntype = e.get("ntype");
        var ext6 = "";

        if((ntype == "table" || ntype == "note") && e.get("stepId") == stepId) {
            var extcode = e.get("extcode").split(",");

            canvas.getObjects().forEach(function(e) {
                var extcode5 = "";

                if(e.get("ntype") == "line" && e.get("extcode").split(",").length == 18) {
                    extcode5 = e.get("extcode").split(",")[5];
                } else if(e.get("ntype") == "line" && e.get("extcode").split(",").length == 19) {
                    extcode5 = e.get("extcode").split(",")[6];
                }

                if(e.get("ntype") == "line" && extcode[0] == extcode5) {
                    if(e.get("class") != className) {
                        var lineClass = e.get("class");

                        canvas.getObjects().forEach(function(e) {
                            if(e.get("ntype") == "line" && e.get("class") == lineClass) {
                                if(e.get("extcode").split(",").length == 18) {
                                    ext6 = e.get("extcode").split(",")[6];
                                } else {
                                    ext6 = e.get("extcode").split(",")[7];
                                }
                            }
                        });
                        extcode6[extcode6Cnt] = ext6;
                        extcode6Cnt++;
                    }
                }
            });
        }
    });

    var hideLineParam = new Array();
    var paramCnt = 0;

    canvas.getObjects().forEach(function(e) {
        var ntype = e.get("ntype");
        var stepId = "";

        for(var i=0; i < extcode6.length; i++) {
            if((ntype == "table" || ntype == "note" || ntype == "package") && e.get("extcode") != null && extcode6[i] == e.get("extcode").split(",")[0]) {
                var extcode = e.get("extcode").split(",");
                stepId = e.get("stepId");

                canvas.getObjects().forEach(function(e) {
                    if((e.get("ntype") == "text" || e.get("ntype") == "version") && e.get("stepId") == stepId && e.get("nodekind") != "FaultCode" && e.get("extcode").split(",")[0] != showExtCode) {
                        $("."+e.get("stepId")).hide();
                        e.set("opacity", 0);
                    }
                    if((e.get("ntype") == "table" || e.get("ntype") == "note" || e.get("ntype") == "package") && e.get("stepId") == stepId && e.get("nodekind") != "FaultCode" && e.get("extcode").split(",")[0] != showExtCode) {
                        $("."+e.get("stepId")).hide();
                        e.set("opacity", 0);

                        if(hideLineParam.length != 0) {
                            var extCnt = 0;

                            for(var i = 0; i < hideLineParam.length; i++) {
                                if(hideLineParam[i] == e.get("extcode").split(",")[0]) {
                                    extCnt++;
                                }
                            }
                            if(extCnt == 0) {
                                hideLineParam[paramCnt] = e.get("extcode").split(",")[0];
                                paramCnt++;
                            }
                        } else {
                            hideLineParam[paramCnt] = e.get("extcode").split(",")[0];
                            paramCnt++;
                        }
                    }
                });
            }
        }
    });

    hideLineLoop(canvas, hideLineParam, showExtCode);
}

function hideLineLoop(canvas, hideLineParam, showExtCode) {
    var hideRectParam = new Array();
    var paramCnt = 0;
    var extcode6 = 0;

    for(var i = 0; i < hideLineParam.length; i++) {
        canvas.getObjects().forEach(function(e) {
            var ntype = e.get("ntype");
            var className = "";
            var extcode5 = "";

            if(ntype == "line" && e.get("extcode").split(",").length == 18) {
                extcode5 = e.get("extcode").split(",")[5];
            } else if(ntype == "line" && e.get("extcode").split(",").length == 19) {
                extcode5 = e.get("extcode").split(",")[6];
            }

            if(ntype == "line" && extcode5 == hideLineParam[i]) {
                var extcode = e.get("extcode").split(",");
                className = e.get("class");

                canvas.getObjects().forEach(function(e) {
                    if(className == e.get("class")) {
                        $("."+e.get("stepId")).hide();
                        e.set("opacity", 0);

                        if(e.get("extcode").split(",").length == 18) {
                            extcode6 = e.get("extcode").split(",")[6];
                        } else if(e.get("extcode").split(",").length == 19) {
                            extcode6 = e.get("extcode").split(",")[7];
                        }

                        if(paramCnt == 0) {
                            hideRectParam[paramCnt] = extcode6;
                            paramCnt++;
                        } else {
                            if(hideRectParam[paramCnt - 1] != extcode6) {
                                hideRectParam[paramCnt] = extcode6;
                                paramCnt++;
                            }
                        }

                    }
                });
            }
        });
    }

    if(hideRectParam.length != 0) {
        hideRectLoop(canvas, hideRectParam, showExtCode);
    }
}

function hideRectLoop(canvas, hideRectParam, showExtCode) {
    var hideLineParam = new Array();
    var paramCnt = 0;

    for(var i = 0; i < hideRectParam.length; i++) {
        var stepId = "";

        canvas.getObjects().forEach(function(e) {
            var ntype = e.get("ntype");

            if((ntype == "table" || ntype == "note" || ntype == "package") && e.get("extcode") != null && e.get("extcode").split(",")[0] == hideRectParam[i]) {
                if(e.get("extcode").split(",")[0] != showExtCode) {
                    stepId = e.get("stepId");
                }
            }
        });

        canvas.getObjects().forEach(function(e) {
            var ntype = e.get("ntype");

            if((ntype == "text" || ntype == "version") && e.get("stepId") == stepId && e.get("nodekind") != "FaultCode") {
                e.set("opacity", 0);
            }

            if((ntype == "note" || ntype == "table" || ntype == "package") && e.get("stepId") == stepId && e.get("nodekind") != "FaultCode") {
                $("."+e.get("stepId")).hide();
                e.set("opacity", 0);

                if(hideLineParam.length != 0) {
                    var extCnt = 0;
                    for(var i = 0; i < hideLineParam.length; i++) {
                        if(hideLineParam[i] == e.get("extcode").split(",")[0]) {
                            extCnt++;
                        }
                    }
                    if(extCnt == 0) {
                        hideLineParam[paramCnt] = e.get("extcode").split(",")[0];
                        paramCnt++;
                    }
                } else {
                    hideLineParam[paramCnt] = e.get("extcode").split(",")[0];
                    paramCnt++;
                }
            }
        });
    }

    if(hideLineParam.length != 0) {
        hideLineLoop(canvas, hideLineParam, showExtCode);
    }
}

function showFiMultiLink(multiLinkId, event) {
    var baseObj = $("#main_fi_contents").length > 0 ? $("#main_fi_contents") : $("#canvas");
    var rect = baseObj[0].getBoundingClientRect();

    var clientHeight = $("#link_"+multiLinkId).children().length * 28;
    var adjTop = (event.clientY + clientHeight) > rect.bottom ? clientHeight : 0;
    adjTop = (event.clientY - adjTop) < rect.top ? event.clientY - rect.top : adjTop;			//최소값
    $("#link_" + multiLinkId).css("top", event.clientY - adjTop);

    var clientWidth = $("#link_"+multiLinkId).width();
    var adjLeft = (event.clientX + clientWidth) > rect.right ? clientWidth : 0;
    adjLeft = (event.clientX - adjLeft) < rect.left ? event.clientX - rect.left : adjLeft;		//최소값
    $("#link_" + multiLinkId).css("left", event.clientX - adjLeft - (rect.width/30));			//clientX 위치 보정 추가

    popupCheck(multiLinkId, event);

    $("#link_" + multiLinkId).show();

    $(document).mouseup(function(event) {
        if (!$(".link-list").is(event.target)){
            $("#link_" + multiLinkId).hide();
        }
    });
}

function popupCheck(multiLinkId, event) {
    // RDN 링크 텍스트가 다르게 시현되는 오류를 해결하기 위함.
    var text = event.target.text;
    var aChild = $("#link_" + multiLinkId).children();

    for (var i = 0; i < aChild.length; i++) {
        var aTxt = aChild[i].text || "";
        var clickStr = aChild[i].getAttribute("onclick");

        if(clickStr.includes("rdnOpenWin") && text != aTxt){
            aChild[i].text = text;

            var startIdx = clickStr.indexOf("rdnOpenWin(");
            var subStr = clickStr.substr(startIdx + "rdnOpenWin(".length).split(",");

            aChild[i].setAttribute("onclick", "onclickColorChange(this);rdnOpenWin("+subStr[0]+", "+subStr[1]+", '', '01', '"+text+"', '"+text+"', '"+text+"');");
        }
    }
}

function tagShowTooltip(id) {
    console.log("id : "+id);
    console.log("tooltip : "+$("#tooltip_" + id));
    $("#tooltip_" + id).css("cursor", "default");
    $("#tooltip_" + id).css("display", "inline-block");
}

function tagBlindTooltip(id) {
    hideTooltip();
}

function showTooltip(id, x, y, event) {
    var locationX = event.target.get("left");
    var locationY = event.target.get("top");
    var id = event.target.get("stepId");
    var height = $("#tooltip_" + id).height()/20;
    console.log("locationX : "+locationX+", locationY : "+locationY+", height : "+height);

    if(height == 1) {
        height = 0;
    }

    $("#tooltip_" + id).css("top", (locationY - 32) - (height * 10));
    $("#tooltip_" + id).css("left", locationX);
    $("#tooltip_" + id).css("display", "inline-block");
    $("#tooltip_" + id).css("width", "80%");
}

function hideTooltip() {
    $(".tooltip").hide();
}

function createBeginnum(canvas, cont) {
    var x1 = Number(cont.attr("x1"));
    var x2 = Number(cont.attr("x2"));
    var y1 = Number(cont.attr("y1")) + 50;
    var y2 = Number(cont.attr("y2")) + 50;
    var num = cont.attr("beginnum");
    var extcode = cont.attr("extcode");
    var param = x1 + "," + x2 + "," + y1 + "," + y2 + "," + cont.attr("id");

    var circle = new fabric.Circle({
        left : x1-2, top : y1,
        hoverCursor : "default",
        radius : (x2-x1)/2,
        stroke : "black",
        strokeWidth : 2,
        fill : "white",
        extcode : extcode,
        id : param
    });

    canvas.add(circle);

    var beginnum = new fabric.Text(num, {
        left : (x2+x1)/2 - 4, top : (y2+y1)/2 - 5,
        fontSize : fiFontSize,
        hoverCursor : "default",
        extcode : extcode,
        fontWeight: 'bold',
        id : param
    });

    canvas.add(beginnum);
}

function createVersionInfo(contents, canvas, fiTocoType, canvasIdStr, layerIdNameStr) {
    $(contents).find("node,table").each(function() {
        if(($(this).attr("version") != undefined && $(this).attr("version") != "")
            || ($(this).parent().attr("version") != undefined && $(this).parent().attr("version") != "")) {
            var x1				= Number($(this).attr("x1"));
            var x2				= Number($(this).attr("x2"));
            var y1				= Number($(this).attr("y1")) + 50;
            var y2				= Number($(this).attr("y2")) + 50;
            var version			= "";
            var contId			= "";
            var status			= "";
            var stepId			= "";
            var changebasis		= "";
            var strokeColor = "";
            let toKey = TOKEY;
            var param			= x1 + "," + x2 + "," + y1 + "," + y2 + "," + $(this).attr("id");

            if($(this).parent().attr("version") != undefined && $(this).parent().attr("version") != "") {
                version		= $(this).parent().attr("version");
                status		= $(this).parent().attr("status");
                contId		= $(this).parent().attr("id");
                if($(this).parent().attr("changebasis")){changebasis	= $(this).parent().attr("changebasis")};
            } else {
                version		= $(this).attr("version");
                status		= $(this).attr("status");
                contId		= $(this).attr("id");
                if($(this).attr("changebasis")){changebasis	= $(this).attr("changebasis")};
            }

            $.ajax({
                type : "POST",
                url : "versionInfo.do",
                dataType : "json",
                async : false,
                data : {
                    toKey : encodeURIComponent(toKey),
                    tocoId : "",
                    contId : contId,
                    verId : version,
                    verStatus : status,
                    changebasis : ""
                },
                success : function(data) {
                    if(data.verChgNo == data.lastVerNo) {
                        strokeColor = "blue";
                    }
                },
                error : function(data) {
                    //alert("popupVersionInfo error");
                    console.log("createVersionInfo error toKey : "+toKey+", contId : "+contId+", verId : "+version);
                }
            });
            if(fiTocoType == "DI_DESC") {
                var versionInfoPTag = '<p class="version_info_box '+$(this).parent().attr("id")+'" style="font-size:11.5px; text-align:center; top:'+ y1 +'px; left:'+ (x1) +'px; width:5px; height:'+(y2-y1)+'px;background-color:'+strokeColor+';position:absolute;cursor:pointer;z-index:99999;">';
                versionInfoPTag +='<a style="display: inline-block;width: 5px;height: '+(y2-y1)+'px;" xid="' + contId + '">&nbsp;</a>';
                versionInfoPTag +='</p>'
                $("#"+canvasIdStr).parent().append(versionInfoPTag);
            } else if(fiTocoType == "T"){
                var versionInfoPTag = '<p class="version_info_box '+$(this).parent().attr("id")+'" style="font-size:11.5px; text-align:center; top:'+ (y1) +'px; left:'+ (x1) +'px; width:5px; height:'+(y2-y1)+'px;background-color:'+strokeColor+';position:absolute;cursor:pointer;z-index:99999;">';
                versionInfoPTag +='<a style="display: inline-block;width: 5px;height: '+(y2-y1)+'px;" xid="' + contId + '">&nbsp;</a>';
                versionInfoPTag +='</p>'
                $("#"+layerIdNameStr).append(versionInfoPTag);
            } else {
                var versionInfoPTag = '<p class="tt'+$(this).parent().attr("id")+'" style="font-size:11.5px; text-align:center; top:'+ (y1) +'px; left:'+ (x1) +'px; width:5px; height:'+(y2-y1)+'px;background-color:'+strokeColor+';position:absolute;cursor:pointer;z-index:99999;">';
                versionInfoPTag +='<a style="display: inline-block;width: 5px; height: '+(y2-y1)+'px;" xid="' + contId + '">&nbsp;</a>';
                versionInfoPTag +='</p>'
                $("#main_fi_contents").append(versionInfoPTag);
            }
        }
    });
}

function createTypeInfo(contents, canvas) {
    var imgTypeC = document.getElementById("fi_img_type_c");
    $("#fi_img_type_c").show();

    $(contents).find("node").each(function() {
        var x1 = Number($(this).attr("x1"));
        var x2 = Number($(this).attr("x2"));
        var y1 = Number($(this).attr("y1")) + 50;
        var y2 = Number($(this).attr("y2")) + 50;
        var type = $(this).attr("type");
        var extcode = $(this).attr("extcode");
        var stepId = $(this).parent().attr("id");
        var nodekind = $(this).attr("nodekind");
        var param = x1 + "," + x2 + "," + y1 + "," + y2 + "," + $(this).attr("id");

        if(type == "C") {
            var img = new fabric.Image(imgTypeC, {
                left : x1 + 2,
                top : y1 + 2,
                stepId : stepId,
                nodekind : nodekind,
                extcode : extcode,
                ntype : $(this).attr("ntype"),
                id : param,
                hoverCursor : "default"
            });

            if($(this).attr("tooltip") != undefined) {
                img.on("mouseover", function(e) {
                    $("#tooltip_" + stepId).css("display", "inline-block");
                });
                img.on("mouseout", function(e) {
                    hideTooltip();
                });
            }
            canvas.add(img);
        }
    });
}

/**
 * 사이즈 전달 받은 숫자 만큼 수정
 */
function getFixSizeNum(obj,addFix) {
    var tempNum = obj.slice(0, obj.indexOf("px"));
    console.log("getFixSizeNum tempNum : "+tempNum+", addFix : "+addFix);
    console.log("getFixSizeNum : "+(tempNum*1+addFix)+"px");
    return (tempNum*1+addFix)+"px";
}

function dotPointChecker(text, str){
    console.log("CALL dotPointChecker : "+text+", "+str);
    if(text.indexOf(str) > -1){
        if(text.substring(text.indexOf(str)+str.length,text.indexOf("3.")+(str.length+1)) == " "){
            console.log("CALL dotPointChecker Point Start");
            return true;
        }else{
            console.log("CALL dotPointChecker not Point Start");
            return false;
        }
    }else{
        return false;
    }
}


function fi_makeLinkStr(obj,contents,fiTocoType){
    console.log("Call fi_makeLinkStr : "+ $(obj).text()+", ($(obj).attr('linkid') : "+$(obj).attr("linkid")+", fiTocoType : "+fiTocoType);
    if(($(obj).attr("linkid") != undefined && $(obj).attr("linkid") != "") || ($(obj).parent().attr("linkid") != undefined && $(obj).parent().attr("linkid") != "")) {
        console.log("fi_makeLinkUrl Make Link Start ");
        var returnStr = "";
        var entryText = $(obj).text();
        var linkid;				//링크가 걸리는 TOCOID
        var viewType;			//팝업으로 보는지 화면 이동으로 하는지 : window 인 경우 팝업
        var multiListName;		//멀티일 경우 리스트에 표시하는 글
        var multiToKey = "";	//멀티일 경우 TOKEY
        ////////////////////////////////////////////////////////////////////////////////////////
        //Step1. Icon 처리 및 아래 첨자 처리
        ////////////////////////////////////////////////////////////////////////////////////////
        $(contents).find("icon").each(function() {
            console.log("fi_makeLinkStr Create Text $(this).text() : "+$(this).text());
            console.log("fi_makeLinkStr Create Text $(this).filename : "+$(this).attr("filename"));
            console.log("fi_makeLinkStr Create Text $(this).iconid : "+$(this).attr("iconid"));
            entryText = entryText.replace("#24;#"+$(this).attr("iconid")+";","<img src=\""+$(this).attr("filename")+"\" width=\"15px\" height=\"15px\" />").replace("/Icon/","/icon/");
        });
        entryText = $(obj).text().replace(/#13;/gi,"<br/>");
        //아래첨자 처리 추가 TODO 윗첨자 처리 추가 필요
        entryText = entryText.replace(/#130;#700;#136;#50;/gi,"<span class='fi_subscrip_str'>");
        entryText = entryText.replace(/#130;#1000;#136;/gi,"</span>");
        entryText = entryText.replace(/#130;#10;#136;/gi,"<span class='fi_subscrip_str'>");
        entryText = entryText.replace(/#130;#12;#136;/gi,"</span>");
        ////////////////////////////////////////////////////////////////////////////////////////
        //Step2. linkid 처리 : linkid 없이 listname 만 존재 할경우 listname을 linkid로 사용
        ////////////////////////////////////////////////////////////////////////////////////////
        if($(obj).attr("linkid") == null && $(obj).attr("listname")) {
            linkid = $(obj).attr("listname").split(",");
        } else {
            if($(obj).attr("linkid")){
                linkid = $(obj).attr("linkid").split(",");
            }else{
                if($(obj).parent().attr("linkid")){
                    linkid = $(obj).parent().attr("linkid").split(",");
                }
            }
        }
        ////////////////////////////////////////////////////////////////////////////////////////
        //Step3. viewType 처리
        ////////////////////////////////////////////////////////////////////////////////////////
        if($(obj).attr("viewtype")){
            viewType = $(obj).attr("viewtype").split(",");
        }else{
            if($(obj).parent().attr("viewtype")){
                viewType = $(obj).parent().attr("viewtype").split(",");
            }
        }
        ////////////////////////////////////////////////////////////////////////////////////////
        //Step4. multiListName 처리
        ////////////////////////////////////////////////////////////////////////////////////////
        if($(obj).attr("listname")){
            multiListName = $(obj).attr("listname").split(",");
        }else{
            if($(obj).parent().attr("listname")){
                multiListName = $(obj).parent().attr("listname").split(",");
            }
        }
        ////////////////////////////////////////////////////////////////////////////////////////
        //Step5. multiToKey 처리
        ////////////////////////////////////////////////////////////////////////////////////////
        if($(obj).attr("tmname")){
            multiToKey = $(obj).attr("tmname").split(",");
        }else{
            if($(obj).parent().attr("tmname")){
                multiToKey = $(obj).parent().attr("tmname").split(",");
            }
        }
        console.log("fi_makeLinkStr linkid : "+linkid);
        console.log("fi_makeLinkStr viewType : "+viewType);
        console.log("fi_makeLinkStr multiListName : "+multiListName);
        console.log("fi_makeLinkStr multiToKey : "+multiToKey);
        ////////////////////////////////////////////////////////////////////////////////////////
        //Step6. 멀티 링크 여부 판단 해서 각각에 맞는 링크 작성
        ////////////////////////////////////////////////////////////////////////////////////////
        if((multiListName+"").indexOf(",") > 0) {
            console.log("fi_makeLinkStr Make Multi Link Start");
            //linkid에, 없는 경우 일반 링크로 판단
            if((linkid+"").indexOf(",") > 0) {
                returnStr += '<a style="display:inline;letter-spacing:0px; cursor: pointer; color: blue;" onclick=\"showFiMultiLink(\''+linkid[0]+'\', event)\">'+ entryText +'</a>';
                var multiLink = "";
                multiLink += '<span class="link-multi">';
                multiLink += '	<div id="link_' + linkid[0] + '" class="link-list" style="z-index:13;">';
                console.log("fi_makeLinkStr multiToKey.length : "+multiToKey.length);
                var isSectionMove = false;
                var _sectionKey;
                if( $("#bizCode") && $("#bizCode").val() == "KTA" ) { 	isSectionMove = true; 	}
                if ( isSectionMove ) {
                    _sectionKey = !!$(obj).parent().attr("sectionid") ? $(obj).parent().attr("sectionid") : $(obj).attr("sectionid");
                    if ( !!_sectionKey && _sectionKey != "" ) {
                        _sectionKey = _sectionKey.split(",");
                    } else {
                        isSectionMove = false;
                    }
                }

                for(var cnt = 0; cnt < multiToKey.length; cnt++) {
                    console.log("Make Link Check E : "+viewType[cnt]);
                    if(viewType[cnt] == "self") {
                        if(linkid[cnt] && linkid[cnt] != ""){
                            multiLink += '<a class="multiLink" style="cursor:pointer;" onclick="onclickColorChange(this);viewExContents(\''+multiToKey[cnt]+'\',\''+linkid[cnt]+'\',\'\',\'01\',\'\');">'+multiListName[cnt]+'</a>';
                        }else{
                            if ( isSectionMove ) {
                                // multiLink += '<a class="multiLink" style="cursor:pointer;" onclick="selectTo(\''+multiToKey[cnt]+'\',\'section\');">'+multiListName[cnt]+'</a>';
                                multiLink += '<a class="multiLink" style="cursor:pointer;" onclick=\"viewExContents(\''+multiToKey[cnt]+'\', \''+_sectionKey[cnt]+'\', \'\', \'01\', \'\');\">'+ multiListName[cnt] +'</a>';
                            } else {
                                multiLink += '<a class="multiLink" style="cursor:pointer;" onclick="selectTo(\''+multiToKey[cnt]+'\',\'to\');">'+multiListName[cnt]+'</a>';
                            }
                        }
                    } else {
                        var multiRevelationType = $(obj).attr("revelationtype").split(",");
                        if(multiRevelationType[cnt] == "RDN") {
                            var tempRevelationtype = "01";
                            if("테이블" == multiRevelationType[cnt]){
                                tempRevelationtype = "04";
                            }else if("테이블" == $(this).parent().attr("revelationtype")){
                                tempRevelationtype = "04";
                            }

                            multiLink +=' <a class="multiLink" style="cursor:pointer;" onclick=\"onclickColorChange(this);rdnOpenWin(\''+multiToKey[cnt]+'\', \''+linkid[cnt]+'\', \'\', \''+tempRevelationtype+'\', \''+multiListName[cnt]+'\', \''+multiListName[cnt]+'\', \''+multiListName[cnt]+'\');\">'+ multiListName[cnt] +'</a>'
                        } else {
                            multiLink += '<a class="multiLink" style="cursor:pointer;" onclick="onclickColorChange(this);viewExOpenWin(\''+multiToKey[cnt]+'\',\''+linkid[cnt]+'\',\'\',\'01\',\'\');">'+multiListName[cnt]+'</a>';
                        }
                    }
                }
                multiLink += '</div>';
                multiLink += '</span>';
                if ( $(".wrap_content:last").length == 0 ) {
                    $(".fi_canvas_box:last").append(multiLink);
                } else {
                    $(".wrap_content:last").append(multiLink);
                }
            }else{
                returnStr += '<a style="display:inline;letter-spacing:0px; cursor: pointer; color: blue;" onclick=\"onclickColorChange(this);viewExOpenWin(\''+$(this).attr("tmname")+'\', \''+linkid+'\', \'\', \'01\', \'\');\">'+ entryText +'</a>';
            }
        }else{
            console.log("Make Nomal Link : "+linkid);
            if(viewType == "" || viewType == "window" || viewType == "undefined" || viewType == undefined) {
                console.log("Single Link window");
                var tempRevelationtype = "01";
                if("테이블" == $(obj).attr("revelationtype")){
                    tempRevelationtype = "04";
                }else if("테이블" == $(obj).parent().attr("revelationtype")){
                    tempRevelationtype = "04";
                }
                if($(obj).attr("revelationtype") == "RDN" || $(obj).parent().attr("revelationtype") == "RDN") {
                    returnStr += '<a style="display:inline;letter-spacing:0px; cursor: pointer; color: blue;" onclick=\"onclickColorChange(this);rdnOpenWin(\''+$(obj).attr("tmname")+'\', \''+$(obj).attr("linkid")+'\', \'\', \''+tempRevelationtype+'\', \''+$(this).attr("listname")+'\', \''+$(obj).attr("listname")+'\', \''+$(obj).attr("listname")+'\');\">'+ entryText +'</a>';
                } else {
                    if($(obj).attr("sectionid") && $(obj).attr("sectionid") != $(obj).attr("linkid")){
                        if($(obj).parent().attr("linkid") && $(obj).parent().attr("linkid") != ''){
                            console.log("fiMake sectionid != linkid : "+$(obj).parent().attr("linkid")+", "+($(obj).parent().attr("sectionid")));
                            returnStr += '<a style="display:inline;letter-spacing:0px; cursor: pointer; color: blue;" onclick=\"onclickColorChange(this);viewExOpenWin(\''+$(obj).parent().attr("tmname")+'\', \''+$(obj).parent().attr("sectionid")+'\', \'\', \''+tempRevelationtype+'\', \''+$(obj).parent().attr("sectionid")+'\');\">'+ entryText +'</a>';
                        }else{
                            console.log("fiMake sectionid != linkid : "+$(obj).attr("linkid")+", "+($(obj).attr("sectionid")));
                            returnStr += '<a style="display:inline;letter-spacing:0px; cursor: pointer; color: blue;" onclick=\"onclickColorChange(this);viewExOpenWin(\''+$(obj).attr("tmname")+'\', \''+$(obj).attr("sectionid")+'\', \'\', \''+tempRevelationtype+'\', \''+$(obj).attr("sectionid")+'\');\">'+ entryText +'</a>';
                        }
                    }else{
                        if($(obj).parent().attr("linkid") && $(obj).parent().attr("linkid") != ''){
                            console.log("fiMake sectionid != linkid else parent");
                            returnStr += '<a style="display:inline;letter-spacing:0px; cursor: pointer; color: blue;" onclick=\"onclickColorChange(this);viewExOpenWin(\''+$(obj).parent().attr("tmname")+'\', \''+$(obj).parent().attr("linkid")+'\', \'\', \''+tempRevelationtype+'\', \''+$(obj).parent().attr("linkid")+'\');\">'+ entryText +'</a>';
                        }else{
                            console.log("fiMake sectionid != linkid else ");
                            returnStr += '<a style="display:inline;letter-spacing:0px; cursor: pointer; color: blue;" onclick=\"onclickColorChange(this);viewExOpenWin(\''+$(obj).attr("tmname")+'\', \''+$(obj).attr("linkid")+'\', \'\', \''+tempRevelationtype+'\', \''+$(obj).attr("linkid")+'\');\">'+ entryText +'</a>';
                        }
                    }
                }
            }else{
                console.log("Single Link self");
                if(linkid != "") {
                    console.log("Make Link Else Type1");
                    if($(obj).parent().attr("linkid") && $(obj).parent().attr("linkid") != ''){
                        if($(obj).parent().attr("viewtype")){
                            console.log("Make Link Check A Parent : "+$(obj).parent().attr("viewtype"));
                            returnStr += '<a style="text-decoration:none; cursor: pointer; color: blue;" onclick=\"onclickColorChange(this);viewExContents(\''+$(obj).parent().attr("tmname")+'\',\''+$(obj).parent().attr("linkid")+'\', \'\',\'01\',\'\');\">'+ entryText +'</a>';
                        }else{
                            console.log("Make Link Check A this : "+$(obj).attr("viewtype"));
                            returnStr += '<a style="text-decoration:none; cursor: pointer; color: blue;" onclick=\"onclickColorChange(this);viewExContents(\''+$(obj).attr("tmname")+'\',\''+$(obj).attr("linkid")+'\', \'\',\'01\',\'\');\">'+ entryText +'</a>';
                        }
                    }else{
                        if($(obj).parent().attr("tmname")){
                            console.log("Make Link Check A Else parent : "+$(obj).parent().attr("tmname"));
                            returnStr += '<a style="text-decoration:none; cursor: pointer; color: blue;" onclick=\"selectTo(\''+$(obj).parent().attr("tmname")+'\', \'to\');\">'+ entryText +'</a>';
                        }else{
                            console.log("Make Link Check A Else this : "+$(obj).parent().attr("tmname"));
                            returnStr += '<a style="text-decoration:none; cursor: pointer; color: blue;" onclick=\"selectTo(\''+$(obj).attr("tmname")+'\', \'to\');\">'+ entryText +'</a>';
                        }
                    }
                } else {
                    if($(obj).attr("revelationtype") == "RDN") {
                        console.log("Make Link Else Type2");
                        returnStr += '<a style="display:inline;letter-spacing:0px; cursor: pointer; color: blue;" onclick=\"onclickColorChange(this);rdnOpenWin(\''+$(obj).attr("tmname")+'\', \''+$(obj).attr("linkid")+'\', \'\', \'01\', \''+$(obj).attr("listname")+'\', \''+$(obj).attr("listname")+'\', \''+$(obj).attr("listname")+'\');\">'+ entryText +'</a>';
                    } else {
                        if(fiTocoType == "DI_DESC") {
                            console.log("Make Link Else Type3");
                            if($(obj).parent().attr("tmname")){
                                if($(obj).parent().attr("linkid") && $(obj).parent().attr("linkid") != ''){
                                    if($(obj).parent().attr("viewtype") && $(obj).parent().attr("viewtype") == "window"){
                                        returnStr += '<a style="display:inline; text-decoration:none; cursor: pointer; color: blue;" onclick=\"onclickColorChange(this);viewExOpenWin(\''+$(obj).parent().attr("tmname")+'\', \''+$(obj).parent().attr("linkid")+'\', \'\', \'01\', \'\');\">'+ entryText +'</a>';
                                    }else{
                                        returnStr += '<a style="display:inline; text-decoration:none; cursor: pointer; color: blue;" onclick=\"onclickColorChange(this);viewExContents(\''+$(obj).parent().attr("tmname")+'\', \''+$(obj).parent().attr("linkid")+'\', \'\', \'01\', \'\');\">'+ entryText +'</a>';
                                    }
                                }else{
                                    returnStr += '<a style="display:inline; text-decoration:none; cursor: pointer; color: blue;" onclick="selectTo(\''+$(obj).parent().attr("tmname")+'\', \'to\');">'+ entryText +'</a>';
                                }
                            }else{
                                if($(obj).attr("linkid") && $(obj).attr("linkid")!=''){
                                    console.log("Make Link Check B : "+$(obj).attr("viewtype")+", "+$(obj).attr("linkid")+", "+$(obj).attr("tmname"));
                                    if($(obj).attr("viewtype") && $(obj).attr("viewtype") == "window"){
                                        returnStr += '<a style="display:inline; text-decoration:none; cursor: pointer; color: blue;" onclick="onclickColorChange(this);viewExOpenWin(\''+$(obj).attr("tmname")+'\',\''+$(obj).attr("linkid")+'\',\'\',\'01\',\'\');">'+entryText+'</a>';
                                    }else{
                                        returnStr += '<a style="display:inline; text-decoration:none; cursor: pointer; color: blue;" onclick="onclickColorChange(this);viewExContents(\''+$(obj).attr("tmname")+'\', \''+$(obj).attr("linkid")+'\', \'\', \'01\', \'\');">'+ entryText +'</a>';
                                    }
                                }else{
                                    returnStr += '<a style="display:inline; text-decoration:none; cursor: pointer; color: blue;" onclick=\"selectTo(\''+$(obj).attr("tmname")+'\', \'to\');\">'+ entryText +'</a>';
                                }
                            }
                        } else if(fiTocoType == "T") {
                            console.log("Make Link Else Type4");
                            if($(obj).parent().attr("linkid") && $(obj).parent().attr("linkid") != ""){
                                console.log("Make Link Check C : "+$(obj).parent().attr("viewtype"));
                                if($(obj).parent().attr("viewtype") && $(obj).parent().attr("viewtype") == "window"){
                                    returnStr += '<a style="display:inline; text-decoration:none; cursor: pointer; color: blue;" onclick="onclickColorChange(this);viewExOpenWin(\''+$(obj).parent().attr("tmname")+'\',\''+$(obj).parent().attr("linkid")+'\',\'\',\'01\',\'\');">'+entryText+'</a>';
                                }else{
                                    returnStr += '<a style="display:inline; text-decoration:none; cursor: pointer; color: blue;" onclick="onclickColorChange(this);viewExContents(\''+$(obj).parent().attr("tmname")+'\', \''+$(obj).parent().attr("linkid")+'\', \'\', \'01\', \'\');">'+ entryText +'</a>';
                                }
                            }else{
                                returnStr += '<a style="display:inline; text-decoration:none; cursor: pointer; color: blue;" onclick=\"selectTo(\''+$(obj).parent().attr("tmname")+'\', \'to\');\">'+ entryText +'</a>';
                            }
                        } else {
                            console.log("Make Link Else Type5");
                            if($(obj).attr("linkid") && $(obj).attr("linkid") != ""){
                                console.log("Make Link Check D : "+$(obj).attr("viewtype"));
                                if($(obj).attr("viewtype") && $(obj).attr("viewtype") == "window"){
                                    returnStr += '<a style="display:inline;letter-spacing:0px; cursor: pointer; color: blue;" onclick="onclickColorChange(this);viewExOpenWin(\''+$(obj).attr("tmname")+'\',\''+$(obj).attr("linkid")+'\',\'\',\'01\',\'\');">'+entryText+'</a>';
                                }else{
                                    returnStr += '<a style="display:inline;letter-spacing:0px; cursor: pointer; color: blue;" onclick="onclickColorChange(this);viewExContents(\''+$(obj).attr("tmname")+'\', \''+$(obj).attr("linkid")+'\', \'\', \'01\', \'\');">'+ entryText +'</a>';
                                }
                            }else{
                                console.log("Make Link : "+$(obj).attr("tmname"));
                                returnStr += '<a style="display:inline;letter-spacing:0px; cursor: pointer; color: blue;" onclick=\"selectTo(\''+$(obj).attr("tmname")+'\', \'to\');\">'+ entryText +'</a>';
                            }
                        }
                    }
                }
            }
        }
        return returnStr;
    }else{
        var entryText = $(obj).text().replace(/#13;/gi,"<br/>");
        entryText = entryText.replace(/#130;#700;#136;#50;/gi,"<span class='fi_subscrip_str'>");
        entryText = entryText.replace(/#130;#1000;#136;/gi,"</span>");
        entryText = entryText.replace(/#130;#10;#136;/gi,"<span class='fi_subscrip_str'>");
        entryText = entryText.replace(/#130;#12;#136;/gi,"</span>");
        console.log("fi_makeLinkUrl No Link return text : "+entryText);
        return entryText;
    }
}