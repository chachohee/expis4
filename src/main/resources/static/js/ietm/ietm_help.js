function helpOpen(kind, list) {
	$("#login_help_list").hide();
	$("#main_help_list").hide();
	$("#top_help_list").hide();
	$("#footer_help_list").hide();
	$("#nav_help_list").hide();
	$("#menual_help_list").hide();
	$("#login_help").removeClass("active");
	$("#main_help").removeClass("active");
	$("#top_help").removeClass("active");
	$("#footer_help").removeClass("active");
	$("#nav_help").removeClass("active");
	$("#menual_help").removeClass("active");
	helpContClose();
	
	$("#" + kind + "_help").addClass("active");
	$("#" + kind + "_help_list").show();
	$("#" + list + "_cont").addClass("active");
	$("#" + list + "_help_cont").show();
}

function openHelpCont(kind) {
	
	helpContClose();
	
	$("#" + kind + "_cont").addClass("active");
	$("#" + kind + "_help_cont").show();
}

function helpContClose() {
	$("#login_cont").removeClass("active");
	$("#main_cont").removeClass("active");
	$("#top_cont").removeClass("active");
	$("#list_cont").removeClass("active");
	$("#myto_cont").removeClass("active");
	$("#verlist_cont").removeClass("active");
	$("#navsearch_cont").removeClass("active");
	$("#memo_cont").removeClass("active");
	$("#option_cont").removeClass("active");
	$("#footer_cont").removeClass("active");
	$("#pen_cont").removeClass("active");
	$("#glossary_cont").removeClass("active");
	$("#version_cont").removeClass("active");
	$("#img_cont").removeClass("active");
	
	$("#login_help_cont").hide();
	$("#main_help_cont").hide();
	$("#top_help_cont").hide();
	$("#list_help_cont").hide();
	$("#myto_help_cont").hide();
	$("#verlist_help_cont").hide();
	$("#navsearch_help_cont").hide();
	$("#memo_help_cont").hide();
	$("#option_help_cont").hide();
	$("#footer_help_cont").hide();
	$("#pen_help_cont").hide();
	$("#glossary_help_cont").hide();
	$("#version_help_cont").hide();
	$("#img_help_cont").hide();
}