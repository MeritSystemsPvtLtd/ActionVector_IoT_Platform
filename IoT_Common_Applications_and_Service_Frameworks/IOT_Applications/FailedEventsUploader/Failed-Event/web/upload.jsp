<%-- 
    Document   : index
    Created on : Jul 27, 2015, 1:59:09 PM
    Author     : ananddw
--%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>File Upload</title>
</head>
<br><br><br><br><br><br><br>
<body bgcolor="#1B141A">
    <center>
        <form action="upload" method="post" enctype="multipart/form-data" autocomplete="off">
            <label for="myFile" class="text">Upload your file</label>
            <input type="file"  name="myFile" class="text1" required  />
      <br> <br> <br>
      <input type="submit" value="Upload" onClick="clearform();"/>
   </form>
    </center>
<div id="footerbottom">
        <div class="footerwrap">
            <div id="copyright">2015 - Designed By -Prokosha System Pvt Ltd.<a href="http://www.prokosha.com/" target="_blank"><img src="prokosha.png" width="150" height="28" border="0"  /></a></div>

        </div>
    </div>

</body>
</html>
<style>
    .footerwrap{
        background-color: #7a7a7a;
        position:absolute;
        bottom:0;
        width:100%;
        left:  -0.5px;
        height:50px;  
        text-align: center;
        
    }
    .text{
        color: #fff;
        text-align: center;
    }
    .text1{
        color: #000;
    }
    

</style>

