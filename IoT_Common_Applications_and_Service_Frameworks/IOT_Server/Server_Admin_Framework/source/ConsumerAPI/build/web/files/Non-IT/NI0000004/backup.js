/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
 var x1=0;
  var y1=0;
 var width=360;
var height=270;
var subids=[];
var xdist=20;
var ydist=20;
var ytopheight=80;
 var divID= "#ActivityinnerSearch";
$(window).load(function(){
 divID= "#ActivityinnerSearch";
    x1=x1+$(divID).position().left;
    y1= y1+$(divID).position().top+ytopheight;
  
    
});






function createWindowWithRemotingUrl() {
	$.window({
		title: "SA-Desk DashBoard",
		url: "http://192.168.1.46:8084/MasterDashBoard/SLADashBoard.jsp",
                x: x1,
               y: y1,
             width: width,           // window width
              height: height,
                checkBoundary:true,
                withinBrowserWindow:true,
                bookmarkable:false,
                showFooter:false,
                 containerClass: "my_frameshadow",
                 showRoundCorner: true,
                 
                onOpen: function(wnd) {
     setOnOpenOptions(wnd);

     },
        onClose: function(wnd) {
           checkRedirectMsg = null;
          setOnCloseOptions(wnd);
         },

onMinimize:function(wnd){
  onMinimized(wnd);
},
afterCascade: function(wnd) { // a callback function after window cascaded
      afterCascadeSet(wnd);
   }
                
	});
}

function createWindowWithRemotingUrljava() {
	$.window({
		title: "JAVA",
		url: "http://java.com",
                checkBoundary:true,
                withinBrowserWindow:true,
                bookmarkable:false,
                showFooter:false

	});
}

function createWindowWithRemotingUrlSADesk() {
    $.window({
		title: "SA-Desk",
		url: "http://192.168.1.46:8085/SA-Desk",
                 x: x1,
                 y: y1,
                 width: width,           // window width
              height: height,
              containerClass: "my_frameshadow",
                checkBoundary:true,
                withinBrowserWindow:true,
                bookmarkable:false,
                showFooter:false,
                showRoundCorner: true,
                scrollable:false,
                 headerClass: "my_header",
                onOpen: function(wnd) {
     setOnOpenOptions(wnd);

     },
        onClose: function(wnd) {
           checkRedirectMsg = null;
          setOnCloseOptions(wnd);
         },

onMinimize:function(wnd){
  onMinimized(wnd);
},
afterCascade: function(wnd) { // a callback function after window cascaded
      afterCascadeSet(wnd);
   }
                
	});
}

function createWindowWithRemotingUrlproChara() {

   var checkRedirectMsg = null;
	window.onbeforeunload = function() {
		if(checkRedirectMsg != null) {
			return checkRedirectMsg;
		}
	}




   $.window({
		title: "proChara",
		url: "http://192.168.1.46:8085/proChara/jsp/GIFT_GAAdminLogin.jsp",
                 x: x1,
               y: y1,
               width: width,           // window width
              height: height,
                checkBoundary:true,
                withinBrowserWindow:true,
                bookmarkable:false,
                showFooter:false,
                onIframeStart: function(wnd, url) {
			checkRedirectMsg = "the window is going to redirect to URL:\r\n"+url;
		},
		onIframeEnd: function(wnd, url) {
			checkRedirectMsg = null;
		},
		
                onOpen: function(wnd) {
     setOnOpenOptions(wnd);

     },
        onClose: function(wnd) {
           checkRedirectMsg = null;
          setOnCloseOptions(wnd);
         },

onMinimize:function(wnd){
  onMinimized(wnd);
},
afterCascade: function(wnd) { // a callback function after window cascaded
      afterCascadeSet(wnd);
   }
                
                
	});
}



function createWindowWithRemotingUrlTotalService() {
    
    $.window({
		title: "PieChart",
		url: "TotalCountService.jsp",
                 x: x1,
                 y: y1,
                 width: width,           // window width
                height: height,
                checkBoundary:true,
                withinBrowserWindow:true,
                bookmarkable:false,
                showFooter:false,
                 containerClass: "my_frameshadow",
                 showRoundCorner: true,
                 
                 onOpen: function(wnd) {
     setOnOpenOptions(wnd);

     },
        onClose: function(wnd) {

          setOnCloseOptions(wnd);
         },
         
onMinimize:function(wnd){
  onMinimized(wnd);
},
afterCascade: function(wnd) { // a callback function after window cascaded
      afterCascadeSet(wnd);
   }
         
	});
}


function createWindowWithRemotingUrlTotalSLAService() {
    $.window({
		title: "BarChart",
		url: "TotalSLAVoilation.jsp",
                x: x1,
                 y: y1,
                 width: width,           // window width
                height: height,
                checkBoundary:true,
                withinBrowserWindow:true,
                bookmarkable:false,
                showFooter:false,
                 containerClass: "my_frameshadow",
                 showRoundCorner: true,
                
                  onOpen: function(wnd) {
     setOnOpenOptions(wnd);

     },
        onClose: function(wnd) {

          setOnCloseOptions(wnd);
         },

onMinimize:function(wnd){
  onMinimized(wnd);
},
afterCascade: function(wnd) { // a callback function after window cascaded
      afterCascadeSet(wnd);
   }
          
	});
}

function createWindowWithRemotingUrlTotalSLAService2() {
    $.window({
		title: "BarChart",
		url: "TotalSLAVoilation2.jsp",
                x: x1,
                 y: y1,
                 width: width,           // window width
                height: height,
                checkBoundary:true,
                withinBrowserWindow:true,
                bookmarkable:false,
                showFooter:false,
                 containerClass: "my_frameshadow",
                 showRoundCorner: true,
                 
                   onOpen: function(wnd) {
     setOnOpenOptions(wnd);

     },
        onClose: function(wnd) {

          setOnCloseOptions(wnd);
         },

onMinimize:function(wnd){
  onMinimized(wnd);
},
afterCascade: function(wnd) { // a callback function after window cascaded
      afterCascadeSet(wnd);
   }
          
	});
}


function createWindowresTest() {
    $.window({
		title: "BarChart",
		url: "Testres.jsp",
                x: x1,
                 y: y1,
                 width: width,           // window width
                height: height,
                checkBoundary:true,
                withinBrowserWindow:true,
                bookmarkable:false,
                showFooter:false,
                 containerClass: "my_frameshadow",
                 showRoundCorner: true,

                   onShow: function(wnd) {
     setOnOpenOptions(wnd);

     },
        onClose: function(wnd) {

          setOnCloseOptions(wnd);
         },

onMinimize:function(wnd){
  onMinimized(wnd);
},
afterCascade: function(wnd) { // a callback function after window cascaded
      afterCascadeSet(wnd);
   }

	});
}



function  createWindowWithTotalAlertCount(){
$.window({
		title: "AlertCount",
		url: "TotalAlertCount.jsp",
                x: x1,
                 y: y1,
                 width: width,           // window width
                height: height,
                checkBoundary:true,
                withinBrowserWindow:true,
                bookmarkable:false,
                showFooter:false,
                 containerClass: "my_frameshadow",
                 showRoundCorner: true,

                   onOpen: function(wnd) {
     setOnOpenOptions(wnd);

     },
        onClose: function(wnd) {

          setOnCloseOptions(wnd);
         },

onMinimize:function(wnd){
  onMinimized(wnd);
},
afterCascade: function(wnd) { // a callback function after window cascaded
      afterCascadeSet(wnd);
   }
});
}


function createWindowWithSalesByPeriod() {
    $.window({
		title: "Sales By Period",
		url: "SalesByPeriod.jsp",
                 x: x1,
                 y: y1,
                 width: width,           // window width
              height: height,
              containerClass: "my_frameshadow",
                checkBoundary:true,
                withinBrowserWindow:true,
                bookmarkable:false,
                showFooter:false,
                showRoundCorner: true,
                scrollable:false,
                 headerClass: "my_header",
                onShow: function(wnd) {
     setOnOpenOptions(wnd);

     },
        onClose: function(wnd) {
           checkRedirectMsg = null;
          setOnCloseOptions(wnd);
         },

onMinimize:function(wnd){
  onMinimized(wnd);
},
afterCascade: function(wnd) { // a callback function after window cascaded
      afterCascadeSet(wnd);
   }

	});
}

function createWindowWithOrderCountByStatus(NoOfDays,ContentItem){


 $.window({
		title: "Order Count By Status"+"<br>(Last "+NoOfDays+" Days)",
		content: ContentItem,
                 x: x1,
                 y: y1,
                 width: width,           // window width
              height: "auto",
              containerClass: "my_frameshadow",
                checkBoundary:true,
                withinBrowserWindow:true,
                bookmarkable:false,
                showFooter:false,
                showRoundCorner: true,
                scrollable:false,
                 headerClass: "Order_header",
                onShow: function(wnd) {
     setOnOpenOptions(wnd);

     },
        onClose: function(wnd) {

          setOnCloseOptions(wnd);
         },

onMinimize:function(wnd){
  onMinimized(wnd);
},
afterCascade: function(wnd) { // a callback function after window cascaded
      afterCascadeSet(wnd);
   }

	});
}



function OrderListForStatus(status){


 $.window({
		title: "OrderListForStatus"+"-"+status+"<br>(Last 90 Days)",
		url: "OrderList.jsp?Status="+status,
                 x: x1,
                 y: y1,
                 width: $(divID).width(),           // window width
              height: 400,
              containerClass: "my_frameshadow",
                checkBoundary:true,
                withinBrowserWindow:true,
                bookmarkable:false,
                showFooter:false,
                showRoundCorner: true,
                scrollable:false,
                 headerClass: "Order_header",
                onShow: function(wnd) {
   setOnOpenOptions(wnd);

     },
        onClose: function(wnd) {

        setOnCloseOptions(wnd);
         },

onMinimize:function(wnd){
  onMinimized(wnd);
},
afterCascade: function(wnd) { // a callback function after window cascaded
      afterCascadeSet(wnd);
   }

	});
}





function OrderCountByStatus(NoOfDays){
var inputXMLRequest="<Request><VendorId>12345</VendorId><Period>90</Period></Request>";
var contentItem="";
                     $.ajax(
             {
                 type: "POST",
                 url: "OrderCountByStatus.do",
                 data:"XMLRequest="+inputXMLRequest,

                 success: function(submsg)
                 {
                
                   var Status=$(submsg).find("Status");
                   var Amount=$(submsg).find("Amount");
                   var Orders=$(submsg).find("Orders");
                    var FeedbackReceived=$(submsg).find("FeedbackReceived");

                     var arrlength=Status.length;

                      if(Status.length==arrlength && Orders.length==arrlength &&FeedbackReceived.length==arrlength ){
                          for(var i=0;i<Status.length;i++){
                              contentItem=contentItem+"<div style='border-bottom: 1px solid #d0ccc9;border-width: 1px 1px 1px 1px;padding-bottom:5px'><a style='color:blue;' href='javascript:void(0)' onclick=OrderListForStatus('"+$(Status[i]).text()+"')><b>"+$(Status[i]).text()+"</b></a><div style='float:right;color:black;'><div style='float:left'>Rs. "+$(Amount[i]).text()+"</div><div style='float:right;padding-left:40px'><b>"+$(Orders[i]).text()+ "</b></div></div><br><div style='padding-top:5px'>FeedBackRecieved  <div style='float:right;color:black'>"+$(FeedbackReceived[i]).text()+"</div></div></div><br>"
                          }

                          }
                      var SendContent="<div id='OrderCountByStatus' style='font-size:13px;color:blue;padding:15px;'>"+contentItem+"</div>"

                      createWindowWithOrderCountByStatus(NoOfDays,SendContent);


                 }
             });

  
}



function GetProductCategories(){
  $.window({
		title: "Product Category",
		url: "GetCategories.jsp",
                 x: x1,
                 y: y1,
                 width: $(divID).width(),           // window width
              height: 400,
              containerClass: "my_frameshadow",
                checkBoundary:true,
                withinBrowserWindow:true,
                bookmarkable:false,
                showFooter:false,
                showRoundCorner: true,
                scrollable:false,
                 headerClass: "Categories_header",
                onShow: function(wnd) {
   setOnOpenOptions(wnd);

     },
        onClose: function(wnd) {

        setOnCloseOptions(wnd);
         },

onMinimize:function(wnd){
  onMinimized(wnd);
},
afterCascade: function(wnd) { // a callback function after window cascaded
      afterCascadeSet(wnd);
   }

	});
}




function EditData(){
 $.window({
		title: "Edit Products",
		url: "EditableData.jsp",
                 x: x1,
                 y: y1,
                 width: $(divID).width(),           // window width
              height: 400,
              containerClass: "my_frameshadow",
                checkBoundary:true,
                withinBrowserWindow:true,
                bookmarkable:false,
                showFooter:false,
                showRoundCorner: true,
                scrollable:false,
                 headerClass: "Edit_header",
                onShow: function(wnd) {
   setOnOpenOptions(wnd);

     },
        onClose: function(wnd) {

        setOnCloseOptions(wnd);
         },

onMinimize:function(wnd){
  onMinimized(wnd);
},
afterCascade: function(wnd) { // a callback function after window cascaded
      afterCascadeSet(wnd);
   }

	});
}


/*function OrderListForStatus(status){
    var inputXMLRequest="<Request><VendorId>12345</VendorId><Period>90</Period><Status>"+status+"</Status></Request>";
var contentItem="";
                     $.ajax(
             {
                 type: "POST",
                 url: "OrderListForStatus.do",
                 data:"XMLRequest="+inputXMLRequest,

                 success: function(submsg)
                 {
                 
                   var OrderId=$(submsg).find("OrderId");
                   var TransactionId=$(submsg).find("TransactionId");
                   var ProductId=$(submsg).find("ProductId");
                    var ProductDesc=$(submsg).find("ProductDesc");
                    var OrderDate=$(submsg).find("OrderDate");
                     var ShipmentDate=$(submsg).find("ShipmentDate");
                     var DeliveryDate=$(submsg).find("DeliveryDate");
                     var PaymentDate=$(submsg).find("PaymentDate");
                      var Amount=$(submsg).find("Amount");
                       var LogisticsBy=$(submsg).find("LogisticsBy");
                        var LogisticsOrderRef=$(submsg).find("LogisticsOrderRef");
                         var CustomerName=$(submsg).find("CustomerName");
                         var CustomerPhone=$(submsg).find("CustomerPhone");
                         var ShipmentCity=$(submsg).find("ShipmentCity");
                         var ShipmentPincode=$(submsg).find("ShipmentPincode");


                      if(OrderId.length!=0){
                          for(var i=0;i<OrderId.length;i++){
                              contentItem=contentItem+"";
                          }

                          }
                     

                     

                 }
             });


}*/

function setOnOpenOptions(wnd){
  
   subids.push(wnd.getWindowId());



if(subids.length<3){

if($("#"+wnd.getWindowId()).width()==$(divID).width() || ($("#"+wnd.getWindowId()).width()>$(divID).width())){
x1=$(divID).position().left;
 y1=y1+getY()+ydist;
$("#"+wnd.getWindowId()).animate({"left": x1,"top":y1},"slow");
}else{

if(subids.length%2==0){
        x1=$(divID).position().left;
        y1=y1+ $("#"+wnd.getWindowId()).height() +ydist;

    }else{
    
       x1=x1+$("#"+wnd.getWindowId()).width()+xdist;

    }
}
    document.getElementById(wnd.getWindowId()).scrollIntoView();

}else{
    var leftlineposition=$(divID).position().left;
    var leftheight=0;
    var rightheight=0;
    var leftlastwindow="";
    var rightlastwindow="";
    if(subids.length>2){
for(var i=0;i<subids.length-1;i++){
  if(leftlineposition==$("#"+subids[i]).position().left || (leftlineposition+10)>=$("#"+subids[i]).position().left){
     leftheight=leftheight+$("#"+subids[i]).height();
      leftlastwindow=subids[i];
  }else{
      rightheight=rightheight+$("#"+subids[i]).height();
      rightlastwindow=subids[i];
  }
  
}

if($("#"+wnd.getWindowId()).width()==$(divID).width() || ($("#"+wnd.getWindowId()).width()>$(divID).width())){

}else{
if(leftheight>rightheight ){
    x1=$("#"+rightlastwindow).position().left;
    y1=$("#"+rightlastwindow).position().top+$("#"+rightlastwindow).height()+ydist;
    $("#"+wnd.getWindowId()).animate({"left": x1,"top":y1},"slow");
}else if(leftheight<rightheight ){
    x1=$(divID).position().left;
    y1=$("#"+leftlastwindow).position().top+$("#"+leftlastwindow).height()+ydist;
$("#"+wnd.getWindowId()).animate({"left": x1,"top":y1},"slow");

}

}

    }

 document.getElementById(wnd.getWindowId()).scrollIntoView();
}
    
 }


function getY(){
    var leftlineposition=$(divID).position().left;
    var leftheight=0;
    var rightheight=0;
    var leftlastwindow="";
    var rightlastwindow="";

for(var i=0;i<subids.length-1;i++){
  if(leftlineposition==$("#"+subids[i]).position().left || (leftlineposition+10)>=$("#"+subids[i]).position().left){
     leftheight=leftheight+$("#"+subids[i]).height();
      alert(leftheight)
  }

}
return leftheight;
}


function setOnCloseOptions(wnd){

           subids.splice(subids.indexOf(wnd.getWindowId()),1);
           
           
           x1=0;
           y1=0;
            
           x1=$(divID).position().left;
           y1=$(divID).position().top+ytopheight;
     
           
      
         if(subids.length!=0){


          var minimizefilterclose=arrfilter();
         

         
    for(var i=0;i<minimizefilterclose.length;i++){
       
        

if(i<2){
     $("#"+minimizefilterclose[i]).animate({"left": x1,"top":y1},"slow");
         if(i%2==0){

         x1=x1+$("#"+minimizefilterclose[i]).width()+xdist;
    }else{
    x1=$(divID).position().left;
        y1=y1+$("#"+minimizefilterclose[i]).height()+ydist;
    }
}else if(i>=2){

     var leftlineposition=$(divID).position().left;
    var leftheight=0;
    var rightheight=0;
    var leftlastwindow="";
    var rightlastwindow="";
    

             for(var m=0;m<i;m++){
                  if(leftlineposition==$("#"+minimizefilterclose[m]).position().left){

      leftheight=leftheight+$("#"+minimizefilterclose[m]).height();
      leftlastwindow=minimizefilterclose[m];
  }else{
      rightheight=rightheight+$("#"+minimizefilterclose[m]).height();
      rightlastwindow=minimizefilterclose[m];
  }
             }

             
//alert(leftheight+" "+rightheight)
             if(leftheight>rightheight && rightlastwindow!=""){
    x1=$("#"+rightlastwindow).position().left;
    y1=$("#"+rightlastwindow).position().top+$("#"+rightlastwindow).height()+ydist;
   $("#"+minimizefilterclose[i]).animate({"left": x1,"top":y1},"slow");
}else if(leftheight<rightheight && leftlastwindow!=""){
    x1=$(divID).position().left;
    y1=$("#"+leftlastwindow).position().top+$("#"+leftlastwindow).height()+ydist;
$("#"+minimizefilterclose[i]).animate({"left": x1,"top":y1},"slow");

}


       
}else{
    if(i%2==0){

         x1=x1+$("#"+minimizefilterclose[i]).width()+xdist;
    }else{
    x1=$(divID).position().left;
        y1=y1+$("#"+minimizefilterclose[i]).height()+ydist;
    }
}

}





}else{


           x1=$(divID).position().left;
           y1=$(divID).position().top+ytopheight;
}

}


function onMinimized(wnd){
   
          x1=0;
           y1=0;
           x1=$(divID).position().left;
           y1=$(divID).position().top+ytopheight;



         if(subids.length!=0){
             var minimizefilter=arrfilter();
         
         
     for(var i=0;i<minimizefilter.length;i++){

         $("#"+minimizefilter[i]).animate({"left": x1,"top":y1},"slow");

if(i<2){
         if(i%2==0){

         x1=x1+$("#"+minimizefilter[i]).width()+xdist;
    }else{
    x1=$(divID).position().left;
        y1=y1+$("#"+minimizefilter[i]).height()+ydist;
    }
}else if(i>=2){

     var leftlineposition=$(divID).position().left;
    var leftheight=0;
    var rightheight=0;
    var leftlastwindow="";
    var rightlastwindow="";

       if(i>1){
             for(var m=0;m<i;m++){
                  if(leftlineposition==$("#"+minimizefilter[m]).position().left){
      leftheight=leftheight+$("#"+minimizefilter[m]).height();
      leftlastwindow=minimizefilter[m];
  }else{
      rightheight=rightheight+$("#"+minimizefilter[m]).height();
      rightlastwindow=minimizefilter[m];
  }
             }


             if(leftheight>rightheight &&  rightlastwindow!=""){
    x1=$("#"+rightlastwindow).position().left;
    y1=$("#"+rightlastwindow).position().top+$("#"+rightlastwindow).height()+ydist;
   $("#"+minimizefilter[i]).animate({"left": x1,"top":y1},"slow");
}else if(leftheight<rightheight && leftlastwindow!=""){
    x1=$(divID).position().left;
    y1=$("#"+leftlastwindow).position().top+$("#"+leftlastwindow).height()+ydist;
$("#"+minimizefilter[i]).animate({"left": x1,"top":y1},"slow");

}


       }
}else{
    if(i%2==0){

         x1=x1+$("#"+minimizefilter[i]).width()+xdist;
    }else{
    x1=$(divID).position().left;
        y1=y1+$("#"+minimizefilter[i]).height()+ydist;
    }
}

}

}else{


           x1=$(divID).position().left;
           y1=$(divID).position().top+ytopheight;
}
}

function afterCascadeSet(wnd){
          x1=0;
           y1=0;

           x1=$(divID).position().left;
           y1=$(divID).position().top+ytopheight;



         if(subids.length!=0){
         /*   $.each($.browser, function(key,value){
	alert("Key: "+key+". Value: "+value);
});*/
      var cascadefilter=[];
        if ( ($.browser.msie)){
    cascadefilter=arrfilter();
}else{
      cascadefilter=arrcascadefilter(wnd);
}
             
    for(var i=0;i<cascadefilter.length;i++){

    $("#"+cascadefilter[i]).animate({"left": x1,"top":y1},"slow");

if(i<2){
         if(i%2==0){

         x1=x1+$("#"+cascadefilter[i]).width()+xdist;
    }else{
    x1=$(divID).position().left;
        y1=y1+$("#"+cascadefilter[i]).height()+ydist;
    }
}else if(i>=2){

     var leftlineposition=$(divID).position().left;
    var leftheight=0;
    var rightheight=0;
    var leftlastwindow="";
    var rightlastwindow="";

       if(i>1){
             for(var m=0;m<i;m++){
                  if(leftlineposition==$("#"+cascadefilter[m]).position().left){
      leftheight=leftheight+$("#"+cascadefilter[m]).height();
      leftlastwindow=cascadefilter[m];
  }else{
      rightheight=rightheight+$("#"+cascadefilter[m]).height();
      rightlastwindow=cascadefilter[m];
  }
             }


             if(leftheight>rightheight && rightlastwindow ){
    x1=$("#"+rightlastwindow).position().left;
    y1=$("#"+rightlastwindow).position().top+$("#"+rightlastwindow).height()+ydist;
   $("#"+cascadefilter[i]).animate({"left": x1,"top":y1},"slow");
}else if(leftheight<rightheight && leftlastwindow ){
    x1=$(divID).position().left;
    y1=$("#"+leftlastwindow).position().top+$("#"+leftlastwindow).height()+ydist;
$("#"+cascadefilter[i]).animate({"left": x1,"top":y1},"slow");

}


       }
}else{
    if(i%2==0){

         x1=x1+$("#"+cascadefilter[i]).width()+xdist;
    }else{
    x1=$(divID).position().left;
        y1=y1+$("#"+cascadefilter[i]).height()+ydist;
    }
}

}

}else{


           x1=$(divID).position().left;
           y1=$(divID).position().top+ytopheight;
}
}
function arrfilter(){
     var minimizefilterclose=[];
         for(var j=0;j<subids.length;j++){
             if($.window.getWindow(subids[j]).isMinimized()!=true  && $.window.getWindow(subids[j]).isMaximized()!=true ){
                 minimizefilterclose.push(subids[j]);
             }

         }

         return minimizefilterclose;

}

function arrcascadefilter(wnd){
    subids.splice(subids.indexOf(wnd.getWindowId()),1);
     subids.unshift(wnd.getWindowId());
      var cascadefilterclose=[];
         for(var j=0;j<subids.length;j++){
             if($.window.getWindow(subids[j]).isMinimized()!=true && $.window.getWindow(subids[j]).isMaximized()!=true){
                 cascadefilterclose.push(subids[j]);
             }

         }

         return cascadefilterclose;

}

function hideAllWindow() {
   $.window.hideAll(); // hide all windows
}

function showAllWindow() {
   $.window.showAll(); // show all windows
}
