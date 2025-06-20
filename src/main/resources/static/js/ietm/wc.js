$(document).on("click", ".wc-toggle", function (event){
    wcToggle.call(this, event);
});

function wcToggle(){
    const icon = $(".wc-content i");
    const task = $(".wc-task");

    if(icon.hasClass("fa-arrow-down")){
        icon.removeClass("fa-arrow-down").addClass("fa-arrow-left");
        task.hide();
    }else {
        icon.removeClass("fa-arrow-left").addClass("fa-arrow-down");
        task.show();
    }
}

function onclickColorChange(obj){
    if($(obj).css("display")){
        $(obj).attr("style" , "cursor: pointer; color:purple; display: "+$(obj).css("display")+"; letter-spacing:" + $(obj).css("letter-spacing") +";");
    }else{
        $(obj).attr("style" , "letter-spacing:0px; cursor: pointer; color:purple;");
    }
}