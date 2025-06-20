//$(document).ready(function() {
//	$(document).ajaxStart(function() {
//		$(".loading").show();
//	});
//	$(document).ajaxComplete(function() {
//		$(".loading").hide();
//	});
//});


	/**
	 * 내용 시현시 목차 및 컨텐츠 종류 분류(view contents kind)
	 * 01	: 내용 - 일반목차
	 * 02	: 경고 - 프레임보기에서 경고창
	 * 03	: 그래픽 - 그림목차, 그림팝업
	 * 04	: 표(테이블) - 표목차, 표팝업
	 * 05	: IPB(IPB목차)
	 */
	var constVcontKind	= new Array("01", "02", "03", "04", "05");

	/**
	 * 목차 타입 (목차 트리에서 그림/표목차 믈릭시)
	 * 01	: 일반목차
	 * 02	: 그림목차
	 * 03	: 표목차
	 * 04	: 음성목차
	 * 05	: 동영상목차
	 */
	var constTocoListKind	= new Array("01", "02", "03", "04", "05");

	/**
	 * 검색시 결과 형태 분류
	 * 01	: 목차
	 * 02	: 내용
	 * 03	: 그래픽
	 * 04	: 표(테이블)
	 * 05	: 경고
	 * 06	: IPB
	 * 07	: FI
	 * 08	: WC
	 * 09	: WUC
	 * 10	: 용어
	 */
	var constScRtKind	= new Array("01", "02", "03", "04", "05", "06", "07", "08", "09", "10");


	/**
	 * T.O 목록 클릭시 jQuery 
	 * @AuthorDate				: PARK S. B. / 2013. 08. 05.
	 * @ModificationHistory	: 
	 */
	function toListLoad() {
	
		// Tree Navigation
		var tNav = $('.tNav');
		var tNavPlus = '\<button type=\"button\" class=\"tNavToggle plus\"\>+\<\/button\>';
		var tNavMinus = '\<button type=\"button\" class=\"tNavToggle minus\"\>-\<\/button\>';
		tNav.find('li>ul').css('display','none');
		tNav.find('ul>li:last-child').addClass('last');
		tNav.find('li>ul:hidden').parent('li').prepend(tNavPlus);
		tNav.find('li>ul:visible').parent('li').prepend(tNavMinus);
		tNav.find('li.active').addClass('open').parents('li').addClass('open');
		tNav.find('li.open').parents('li').addClass('open');
		tNav.find('li.open>.tNavToggle').text('-').removeClass('plus').addClass('minus');
		tNav.find('li.open>ul').slideDown(100);

		$('.tNav .tNavToggle').click(function(){
			t = $(this);
			t.parent('li').toggleClass('open');
			if(t.parent('li').hasClass('open')){
				t.text('-').removeClass('plus').addClass('minus');
				t.parent('li').find('>ul').slideDown(100);
			} else {
				t.text('+').removeClass('minus').addClass('plus');
				t.parent('li').find('>ul').slideUp(100);
			}
			return false;
		});

		$('.tNav a[href=#]').click(function(){
			//카테고리 아이디 값 저장
			var id = $(this).attr('id');

			//전체 변경
			var tNav = $('.tNav');
			tNav.find('a').css('backgroundColor','');
			tNav.find('a').css('fontWeight','normal');
			
			//선택된 교범목차만 변경
			$(this).css('backgroundColor', 'gold');
			$(this).css('fontWeight', 'bold');
			
			t = $(this);
			t.parent('li').toggleClass('open');
			if(t.parent('li').hasClass('open')){
				t.prev('button.tNavToggle').text('-').removeClass('plus').addClass('minus');
				t.parent('li').find('>ul').slideDown(100);
			} else {
				t.prev('button.tNavToggle').text('+').removeClass('minus').addClass('plus');
				t.parent('li').find('>ul').slideUp(100);
			}
			return false;
		});
		
	}

	
	/**
	 * T.O 목록 클릭시 jQuery 
	 * @AuthorDate				: PARK S. B. / 2013. 09. 10.
	 * @ModificationHistory	: 
	 */
	function toDeListLoad(tocoId, isTitle) {
		// Tree Navigation
		var tNav2 = $('.tNav2');
		var tNav2Plus = '\<button type=\"button\" class=\"tNav2Toggle plus\"\>+\<\/button\>';
		var tNav2Minus = '\<button type=\"button\" class=\"tNav2Toggle minus\"\>-\<\/button\>';
		tNav2.find('li>ul').css('display','none');
		tNav2.find('ul>li:last-child').addClass('last');
		tNav2.find('li>ul:hidden').parent('li').prepend(tNav2Plus);
		tNav2.find('li>ul:visible').parent('li').prepend(tNav2Minus);

		tNav2.find('li.active').addClass('open').parents('li').addClass('open');
		tNav2.find('li.open').parents('li').addClass('open');
		tNav2.find('li.open>.tNav2Toggle').text('-').removeClass('plus').addClass('minus');
		tNav2.find('li.open>ul').slideDown(100);
		
		$('.tNav2 .tNav2Toggle').click(function(){
			t = $(this);
			t.parent('li').toggleClass('open');
			if(t.parent('li').hasClass('open')){
				t.text('-').removeClass('plus').addClass('minus');
				t.parent('li').find('>ul').slideDown(100);
			} else {
				t.text('+').removeClass('minus').addClass('plus');
				t.parent('li').find('>ul').slideUp(100);
			}
			return false;
		});

		$('.tNav2 a[href=#]').click(function(){
			//카테고리 아이디 값 저장
			var id = $(this).attr('id');
			var tNav = $('.tNav2');
			tNav.find('a').css('backgroundColor','');
			tNav.find('a').css('fontWeight','normal');
			$(this).css('backgroundColor', 'skyBlue');
			$(this).css('fontWeight', 'bold');

			return false;
		});
		
		if(isTitle == "표지") {
			$("#tNav2>ul>li>a").css("backgroundColor", "skyBlue");
			$("#tNav2>ul>li>a").css("fontWeight", "bold");	
		} else {
			$("#" + tocoId).css("backgroundColor", "skyBlue");
			$("#" + tocoId).css("fontWeight", "bold");
		}
	}

	
	/**
	 * T.O 목록 가져오기
	 * @FunctionName			: getToList
	 * @AuthorDate				: JS Kim // 2014-06-24
	 * @ModificationHistory	: 
	 */
	function getToList() {
	
		$.ajax({
			 	type:"GET",
			 	data:"to_kind=01",
			 	url:$("#proectName").val()+"ietm/toTreeAjax.do",
			 	dataType:"json",
			 	cache:false,
			 	success:function(data){
			 		
					var SystreeData = data.returnData;
					var lang = $("#lang").val();
					console.log("getToList lang : "+lang);
					//xml가져온 data에서 system을 찾고 길이가 0보다 큰지를 확인
					if ( $(SystreeData).find('system').length > 0 ) {
						
						//변수 선언 및 초기화
						var divHtml ="";
						//xml가져온 data에서 system을 찾고 그 수만큼 루프를 돔
						$(SystreeData).find('system').each(function() {
							//data에서  속성 값 id, name, type을 찾아 각각의 변수에 저장
							var $sysNode = $(this);
							var sysId = $sysNode.attr('id');
							var sysName = $sysNode.attr('name');
							//2022 06 13 Park.J.S. Add
							try{
								if(lang == "en" && sysName =="그림목차"){
									sysName = "List of Figures";
								}else if(lang == "en" && sysName =="표목차"){
									sysName = "List of Table";
								}
							}catch (e) {}
							var sysType = $sysNode.attr('type');
							var parentId = $(this).parent().attr('id');
							
							if((sysType == "System" || sysType == "SubSystem" || sysType == "Component" || sysType == "SubAssembly") && $(this).children().length > 0){
								
								//현재 노드의 부모노드가 System, SubSystem, Component, SubAssembly일때만 아래의 로직 처리
								if($(this).parent().attr('type') == "System" || $(this).parent().attr('type') == "SubSystem" || 
										$(this).parent().attr('type') == "Component" || $(this).parent().attr('type') == "SubAssembly"){
									
									divHtml ="<li id='li_"+sysId+"'><a href='#' id='"+sysId+"' title='"+sysName+"'>"+sysName+"</a><ul id='ul_"+sysId+"'></ul></li>";
									$("#tNav ul#ul_"+parentId+"").append(divHtml);
									
								}else{
									divHtml ="<li id='li_"+sysId+"'><a href='#' id='"+sysId+"' title='"+sysName+"'>"+sysName+"</a><ul id='ul_"+sysId+"'></ul></li>";
									$("#tNav ul#sys").append(divHtml);
								}
								
								//현재 노드의 자식 노드의 수만큼 루프를 돔
								$(this).children().each(function() {
									var $child = $(this);
									var chdId = $child.attr('id');
									var chdName = $child.attr('name');
									var chdType = $child.attr('type');
									var saveyn;
									
									if($child.attr('saveyn') == "Y") {
										saveyn = "book";
									} else {
										saveyn = "none";
									}
									
									if(chdType == "TO"){
										divHtml ="<li id='li_"+chdId+"'><span id='"+saveyn+"'><a href='#' onclick=\"goToDetailList('"+chdName+"', '"+chdId+"', '표지', false, '', '', '');\" id='"+chdId+"' title='"+chdName+"'>"+chdName+"</a></span></li>";
										$("#tNav ul#ul_"+$child.parent().attr('id')+"").append(divHtml);
									}
								});

							}else{
								if($(this).parent().attr('type') == null){
									divHtml ="<li id='li_"+sysId+"'><span id='noChild'><a href='#' id='"+sysId+"' title='"+sysName+"'>"+sysName+"</a></span></li>";
									$("#tNav ul#sys").append(divHtml);	
								}
							}
						});
					}
			 	},
			 	error:function(e){
					alert( "XML PARSING ERROR!!\n"+e);
				},
				complete:function() {
					$("#oriToNav ul#sys").html($("#tNav ul#sys").html());
					toListLoad();
				}
		 });
	}


	/**
	 * T.O 목차 가져오기
	 * @FunctionName			: goToDetailList
	 * @AuthorDate				: JS Kim / 2014.07.03.
	 * @ModificationHistory	: 
	 */
	function goToDetailList(toKey, tocoId, isTitle, searchWord, vcontKind, contId) {
		
		if (vcontKind == null || vcontKind == "undefined") {
			vcontKind = "";
		}
		if (contId == null || contId == "undefined") {
			contId = "";
		}

		$.ajax({
			type: "POST",
		 	data:"to_key=" + encodeURIComponent(toKey) + "&to_kind=02",
			url:$("#proectName").val()+"ietm/toTreeAjax.do",
			dataType:"json",
			cache: false,
			success: function(data){
				
				var SystreeData = data.returnData;

				//var fontSize = data.returnData;
				//$(".ac-content *").css("font-size",data.fontSize+"px");

				//xml가져온 data에서 system을 찾고 길이가 0보다 큰지를 확인
				if ( $(SystreeData).find('system').length > 0 ) {
					
					$("#toName").val(toKey);
					
					//변수 선언 및 초기화
					var divHtml ="";
					var cnt = 0;

					//ajax 동작 전 수행하고 싶은 내용을 입력 합니다. (로딩 이미지 출력 등)
					//alert( 'ajax 동작 수행 전 입니다.' );
	 				$("#tNav2 ul#sys").remove();
	 				$("#tNav2").append("<ul id='sys'></ul>");
	 				
					divHtml ="";
					
					//xml가져온 data에서 system을 찾고 그 수만큼 루프를 돔
					$(SystreeData).find('system').each(function() {
						
						//data에서  속성 값 id, name, type을 찾아 각각의 변수에 저장
						var $sysNode = $(this);
						var sysId = $sysNode.attr('id');
						var sysName = $sysNode.attr('name');
						var sysType = $sysNode.attr('type');
						var parentId = $(this).parent().attr('id');
						
						if(cnt == 0 && (tocoId == null || tocoId == "" || isTitle == "표지")){
							toContView(toKey, sysId, "표지", false, searchWord, vcontKind, contId);

						} else if(tocoId != null && tocoId != "" && tocoId == sysId) {
							toContView(toKey, sysId, sysName, false, searchWord, vcontKind, contId);
						}
						cnt++;
						
						if (sysType=="TO") {
							if(tocoId != null && tocoId != ""){
								divHtml ="<li id='li_"+sysId+"' class='open'><a href='#' onclick=\"javascript:toContView('"+toKey+"', '"+sysId+"', '"+sysName+"', false, '', '"+constVcontKind[0]+"', '');\" id='"+sysId+"' title='"+sysName+"'>"+sysName+"</a><ul id='ul_"+sysId+"'></ul></li>";
							}else{
								divHtml ="<li id='li_"+sysId+"' class='open'><a href='#' onclick=\"javascript:toContView('"+toKey+"', '"+sysId+"', '"+sysName+"', false, '', '"+constVcontKind[0]+"', '');\" id='"+sysId+"' title='"+sysName+"' style='font-weight: bold;'>"+sysName+"</a><ul id='ul_"+sysId+"'></ul></li>";
							}
							$("#tNav2 ul#sys").append(divHtml);

						} else if (sysType=="GrphToco") {
							divHtml ="<li id='li_"+sysId+"'><a href='#' onclick=\"javascript:toSubTocoList('"+toKey+"', '"+constTocoListKind[1]+"');\" id='"+sysId+"' title='"+sysName+"'>"+sysName+"</a><ul id='ul_"+sysId+"'></ul></li>";
							$("#tNav2 ul#sys").append(divHtml);
						
						} else if (sysType=="TableToco") {
							divHtml ="<li id='li_"+sysId+"'><a href='#' onclick=\"javascript:toSubTocoList('"+toKey+"', '"+constTocoListKind[2]+"');\" id='"+sysId+"' title='"+sysName+"'>"+sysName+"</a><ul id='ul_"+sysId+"'></ul></li>";
							$("#tNav2 ul#sys").append(divHtml);

						} else if (sysType=="Grph") {
							var listId = $sysNode.attr('listuuid');
							divHtml ="<li id='li_"+sysId+"'><span id='paper'><a href='#' onclick=\"javascript:toContView('"+toKey+"', '"+listId+"', '"+sysName+"', false, '', '"+constVcontKind[2]+"', '"+sysId+"');\" id='"+sysId+"' title='"+sysName+"'>"+sysName+"</a></span></li>";
							$("#tNav2 ul#ul_"+parentId+"").append(divHtml);

						} else if (sysType=="Table") {
							var listId = $sysNode.attr('listuuid');
							divHtml ="<li id='li_"+sysId+"'><span id='paper'><a href='#' onclick=\"javascript:toContView('"+toKey+"', '"+listId+"', '"+sysName+"', false, '', '"+constVcontKind[3]+"', '"+sysId+"');\" id='"+sysId+"' title='"+sysName+"'>"+sysName+"</a></span></li>";
							$("#tNav2 ul#ul_"+parentId+"").append(divHtml);
						
						} else if (sysType=="ipb") {
							if($(this).children().length > 0){
								divHtml ="<li id='li_"+sysId+"'><a href='#' onclick=\"javascript:toContView('"+toKey+"', '"+sysId+"', '"+sysName+"', false, '', '"+constVcontKind[4]+"', '');\" id='"+sysId+"' title='"+sysName+"'>"+sysName+"</a><ul id='ul_"+sysId+"'></ul></li>";
								$("#tNav2 ul#ul_"+parentId+"").append(divHtml);
							}else{
								divHtml ="<li id='li_"+sysId+"'><span id='paper'><a href='#' onclick=\"javascript:toContView('"+toKey+"', '"+sysId+"', '"+sysName+"', false, '', '"+constVcontKind[4]+"', '');\" id='"+sysId+"' title='"+sysName+"'>"+sysName+"</a></span></li>";
								$("#tNav2 ul#ul_"+parentId+"").append(divHtml);
							}
						
						} else {
							//자식이 있는 경우
							if($(this).children().length > 0){
								divHtml ="<li id='li_"+sysId+"'><a href='#' onclick=\"javascript:toContView('"+toKey+"', '"+sysId+"', '"+sysName+"', false, '', '"+constVcontKind[0]+"', '');\" id='"+sysId+"' title='"+sysName+"'>"+sysName+"</a><ul id='ul_"+sysId+"'></ul></li>";
								$("#tNav2 ul#ul_"+parentId+"").append(divHtml);

							}else{
							//자식이 없는 경우
								divHtml ="<li id='li_"+sysId+"'><span id='paper'><a href='#' onclick=\"javascript:toContView('"+toKey+"', '"+sysId+"', '"+sysName+"', false, '', '"+constVcontKind[0]+"', '');\" id='"+sysId+"' title='"+sysName+"'>"+sysName+"</a></span></li>";
								$("#tNav2 ul#ul_"+parentId+"").append(divHtml);
							}
						}
					});
					
					if(tocoId != null && tocoId != ""){
						$('.tNav2').find('#li_'+tocoId).parents("li").attr('class','open');
					}

					$("#lhs_toList").show();
					$("#lhs_toList_li").hide();
					$("#lhs_toList_de").show();
					$("#lhs_myToList").hide();
					$("#toView").show();
					
					//T.O 리스트 파싱 완료후 tree jQuery 적용
					toDeListLoad(tocoId, isTitle);

				} else {
					toListNotMsg();
				}
				
			},
		});
	}


	/**
	 * T.O 그림/표목차 리스트 jQuery 
	 * @AuthorDate				: LIM Y.M. / 2014.09.22
	 * @ModificationHistory	: 
	 */
//	function toSubTocoList-Old(toKey, tocoListKind) {
//		
//		var param = "to_key="+toKey+"&toco_list_kind="+tocoListKind;
//
//		$.ajax({
//			type: "POST",
//			url:  $("#proectName").val()+"ietm/toSubTocoList.do",
//			data: param,
//			dataType: 'json',
//			success: function(data){
//
//				if(data.message == "success"){
//					$("#toView").html("");
//					
//					$("#subTocoCaption").html(data.subTocoCaption);
//					$("#subTocoTitle").html(data.subTocoTitle);
//					$("#toSubTocoListTbody").html(data.returnData);
//					$("#toView").html($("#toSubTocoListView").html());
//				}
//			}
//		});
//	}

	
	/**
	 * T.O 내용 보기 jQuery 
	 * @AuthorDate				: JS Kim // 2014-07-08
	 * @ModificationHistory	: 
	 */
	function toContView(toKey, tocoID, tocoName, isMyto, searchWord, vcontKind, contId) {
		var param = "to_key=" + encodeURIComponent(toKey) + "&toco_id=" + tocoID  + "&to_name=" + tocoName + "&search_word=" + searchWord;
		param += "&vcont_kind=" + vcontKind + "&cont_id=" + contId;
		if(isMyto != true) {
//			initMenu();
			$('.leftMenu li a.leftList').find('img').attr('src','"+$("#proectName").val()+"web/image/ietm/menu/btn_left_menu01_over.png');
			$("#lhs_toList_de").show();
			$("#toName").val(tocoName);
			leftList = true;
		}
		
		//TOP 정보 적용
		$.ajax({
			type: "POST",
			url:  $("#proectName").val()+"ietm/topInfo.do",
			data: param,
			dataType: 'json',
			success: function(data){
				$("#topToModel").html("T50");
				$("#topToKey").html(toKey);
				$("#topTocoSssnNo").html(data.tocoSssnNo);
				$("#topToVerDate").html(data.toVerDate);
			}
		});

		//북마크 위해 사전 정보 저장
		$("#sub_body").css("backgroundColor", "#FFFFFF");
		$("#toView").attr("subject", toKey + " > " + tocoName);
		$("#toView").attr("toKey", toKey);
		$("#toView").attr("tocoId", tocoID);
		$("#toView").attr("toName", tocoName);
		
		//관련교범목록 숨기기
		$(".to_sibling_list").each(function() {
    		$(this).attr("status", "close");
    		$(this).hide();
    	});

		//표지 및 내용 화면 호출
		if(tocoName == "표지" || toKey == tocoName) {
			$("#toView").attr("isMain", "true");
			$("#checkvalue").val("0");	
			selectToCover(param);
		} else {
			$("#toView").attr("isMain", "false");
			selectToContents(param, searchWord);
		}
		

		if($("#opener_link").val() == "link") {
			viewOpenerContents($('#to_key').val(), $('#toco_id').val(), "05");
		} else {
			viewExContents($('#to_key').val(), $('#toco_id').val(), '',"05");
		}
		
	}

	
	/**
	 * T.O 표지 jQuery 
	 * @AuthorDate				: JS Kim // 2014-07-08
	 * @ModificationHistory	: 
	 */
	function selectToCover(param) {

		$.ajax({
			type: "POST",
			url:  $("#proectName").val()+"ietm/toCover.do",
			data: param,
			dataType: 'json',
			success: function(data){
				$(".toKey").text(data.toData.toType + " " + data.toData.toKey);
				$(".toMainName").text(data.toData.toName);
				$(".toSubname").text(data.toData.toSubname);
				$(".toPart").text(data.toData.toPart);
				$(".toOrginal").text(data.toOrgDate);
				$(".toVersion").text(data.toVerNo + " " + data.toVerDate);
				$("#toView").html($("#toCoverView").html());
				latestToInsert(param);
			}
		});
	}
	

	/**
	 * T.O 내용 중 폰트 사이즈 조정 jQuery 
	 * @AuthorDate				: Hong S.H. / 2014.08.
	 * @ModificationHistory	: 
	 */
	function selectToContents(param, searchWord) {
		var bWidth	= window.outerWidth;
		var bHeight	= window.outerHeight;
		param += "&browser_width=" + bWidth + "&browser_height=" + bHeight;

		$.ajax({
			type: "POST",
			url:  $("#proectName").val()+"ietm/toCont.do",
			data: param,
			dataType: 'json',
			success: function(data){

				if(data.message == "success"){
					$("#toView").html("");
					$("#toView").html(data.returnData);

					if(data.fontSize == 14){	//, parent.document		
						$('#toView div').each(function() {
							var fontEx = $(this).css("font-size").replace(/[^-\d\.]/g, '');
							$(this).css('font-size', Number(fontEx)+2);
							$("#fontvalue").val(Number(fontEx)+2);
						});
						$('#toView div p').each(function() {
							var fontEx = $(this).css("font-size").replace(/[^-\d\.]/g, '');
							$(this).css('font-size', Number(fontEx)+2);									
							$("#fontvalue").val(Number(fontEx)+2);
						});
						$('.in_table td').each(function() {
							var fontEx = $(this).css("font-size").replace(/[^-\d\.]/g, '');
							$(this).css('font-size', Number(fontEx)+2);
							$("#fontvalue").val(Number(fontEx)+2);
						});
						
					}else if(data.fontSize == 12){
						$('#toView div').each(function() {
							var fontEx = $(this).css("font-size").replace(/[^-\d\.]/g, '');
							$(this).css('font-size', Number(fontEx));
							$("#fontvalue").val(Number(fontEx));
						});
						$('#toView div p').each(function() {
							var fontEx = $(this, parent.document).css("font-size").replace(/[^-\d\.]/g, '');
							$(this).css('font-size', Number(fontEx));									
							$("#fontvalue").val(Number(fontEx)+2);
						});
						$('.in_table td', parent.document).each(function() {
							var fontEx = $(this).css("font-size").replace(/[^-\d\.]/g, '');
							$(this).css('font-size', Number(fontEx));
							$("#fontvalue").val(Number(fontEx));
						});
			
					}else if(data.fontSize == 10){
						$('#toView div').each(function() {
							var fontEx = $(this).css("font-size").replace(/[^-\d\.]/g, '');
							$(this).css('font-size', Number(fontEx)-2);
							$("#fontvalue").val(Number(fontEx));
						});
						$('#toView div p').each(function() {
							var fontEx = $(this).css("font-size").replace(/[^-\d\.]/g, '');
							$(this).css('font-size', Number(fontEx)-2);									
							$("#fontvalue").val(Number(fontEx)+2);
						});
						$('.in_table td').each(function() {
							var fontEx = $(this).css("font-size").replace(/[^-\d\.]/g, '');
							$(this).css('font-size', Number(fontEx)-2);
							$("#fontvalue").val(Number(fontEx));
						});
				
					}
						
					latestToInsert(param);
				}
			}
		});
	}
	
	
	/**
	 * 교범 안내 문구
	 * @FunctionName			: toListNotMsg
	 * @AuthorDate				: PARK S. B. / 2013. 8. 29.
	 * @ModificationHistory	: 
	 */		
	function toListNotMsg(){
		//2023.07.20 - language 처리 추가 - jingi.kim
		var msg_notExist = $("#ietm_manual_not_exist").val() || "교범이 존재하지 않습니다.";
		alert(msg_notExist);
	}
	

	/**
	 * 본문 아코디언 메뉴 전체 오픈
	 * @FunctionName	: divAllOpen
	 * @AuthorDate		: PARK S. B. / 2013. 8. 22.
	 * @ModificationHistory	: 
	 */
	function divAllOpen(){
		$('.ac-arrow').attr('class','ac-arrow-open');
		$('.ac-div').css('display', 'block');		 
	}
	
		
	/**
	 * 본문 아코디언 메뉴 전체 오픈
	 * @FunctionName	: divAllClose
	 * @AuthorDate		: PARK S. B. / 2013. 8. 22.
	 * @ModificationHistory	: 
	 */		
	function divAllClose(){
		$('.ac-arrow-open').attr('class','ac-arrow');
		$('.ac-div').css('display', 'none');		 
	}
	
