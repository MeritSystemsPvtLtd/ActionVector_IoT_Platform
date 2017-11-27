function popup(message,divid,popdiv,mesdiv,width,height) {

    // get the screen height and width

    var maskleft = $("#"+divid).position().left;
    var masktop = $("#"+divid).position().top;
    var divponter=maskleft+(($("#"+divid).width())/2);

    // calculate the values for center alignment
    var dialogTop =  masktop+$("#"+divid).height()-8;
    var dialogLeft = maskleft-(width) ;

    // assign values to the overlay and dialog box
    //$('#dialog-overlay').css({height:maskHeight, width:maskWidth}).show();
    $('#'+popdiv).css({
        "width":width,
        "height":height
    }).show();
    $('#'+popdiv).css({
        top:dialogTop,
        left:dialogLeft
    }).show();
    $("#triangle").css({
        "margin-left": 155
    }).show();
    $("#"+mesdiv).css({
        "height": height
    }).show();
    // display the message
    $('#'+mesdiv).html(message);
}


$(document).ready(function () {

    $("#WelcomeUser").click(function(){
        if($('#menupop').is(':hidden')){

            var ele=$('#innerbuttons').html();
           // popup(ele,"userpic","menupop","tooltip_menu",250,150);
        }else{
            $('#menupop').hide();
        }

    });


    $("#userpic").click(function(){
        if($('#menupop').is(':hidden')){
            var ele1=$('#innerbuttons').html();
           // popup(ele1,"userpic","menupop","tooltip_menu",250,150);
        }else{
            $('#menupop').hide();
        }

    });


    $("#menupop").mouseleave(function(){
        $('#menupop').hide();
    });
    $(window).resize(function () {
        $('#menupop').hide();
        //only do it if the dialog box is not hidden
        if (!$('#menupop').is(':hidden')) popup();
    });


});

function  SignOut(){

    $('#menupop').hide();
    var sessionid=$("#sessionid").val();
    var UserID=$("#UserID").val();
    // document.UserID.submit();
    window.location.href="User_Signout.action?UserID="+UserID+"&sessionid="+sessionid;


}
function Cancel(){
    $('#menupop').hide();
    return false;
}

function UserProfile(Userid,sessionid)
{
    //alert($("#window").data("kendoWindow"))

    $.window({
        title: "User Profile",
        content: $("#userprofile").html(),
        //url:"/home/manoj/JavaPaginationEx/web/UserProfile.html",
        x: 150,               // the x-axis value on screen, if -1 means put on screen center
        y: 70,               // the y-axis value on screen, if -1 means put on screen center
        width: 600,           // window width
        height: 500,
        showModal: true,
        modalOpacity: 0.5,
        showFooter:false,
        showRoundCorner: true,
        minimizable:false,
        maximizable:false,
        //headerClass: "my_header",
        bookmarkable:false,
        scrollable:true,
        draggable:false,
        resizable:false,

        onShow: function(wnd){

            $.ajax(
            {
                type: "GET",
                url: "User_Profile.action",
                data: {
                    szsessionid:sessionid,
                    userid:Userid
                },

                success: function(data)
                {

                    var SessionidCheck=$(data).find("Description");
                    if(SessionidCheck[0].firstChild.nodeValue!="Invalid Session"){
                        var selectedgroup=wnd.getContainer().find("#myparagraph");
                        selectedgroup.html(SessionidCheck[0].firstChild.nodeValue)
                    }else{
                        $.window.closeAll();
                        SessionTimeOutWindow("Session TimedOut , Please Relogin to perform an action");
                    }
                },

                error: function(err) {
                    alert("error message::"+err.toString());
                    if (err.status == 200) {
                        ParseResult(err);
                    }
                    else {
                        alert('Error:' + err.responseText + '  Status: ' + err.status);
                    }
                }

            });
        },
        onClose:function(wnd){

        }

    });
}
function SessionTimeOutWindow(SessionMes){
    $.window({
        title:"Session TimedOut",
        content:$("#SessionTimeOut"),
        width: 400,           // window width
        height: 250,
        showModal: true,
        modalOpacity: 0.5,
        showFooter:false,
        showRoundCorner: true,
        minimizable:false,
        maximizable:false,
        closable:false,
        headerClass: "my_header",
        bookmarkable:false,
        scrollable:false,
        draggable:false,
        resizable:false,

        onShow: function(wnd) {
            //FilesLinks
            wnd.getContainer().find("#SessionTimeOutMes").html(SessionMes);


        },
        onClose: function(wnd) {



        }



    });

}
