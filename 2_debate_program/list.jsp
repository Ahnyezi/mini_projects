<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import="java.util.*, java.io.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>Debate</title>
<style>
	
.cssZoom{
	border:1px solid #ccc;
	padding:1px;
	margin-top:1px;
	width:640px;
	height:480px;
	overflow:hidden;
}
.cssZoom iframe{
	-ms-zoom: 0.9;
	
	-moz-transform: scale(0.9);
	-moz-transform-origin: 0 0;
	
	-o-transform: scale(0.9);
	-o-transform-origin: 0 0;
	
	-webkit-transform: scale(0.9);
	-webkit-transform-origin: 0 0;
	
	transform: scale(1);
	transform-origin: 0 0;	
	
	border:1px solid #ccc;
	border:1px solid #ccc;
	top:0px;
	left:0px;
	
	width:100%;
	height:100%;
}
.cssZoom2{
	border:1px solid #ccc;
	padding:1px;
	margin-top:1px;
	width:203px;
	height:144px;
	overflow:hidden;
}
.cssZoom2 iframe{
	-ms-zoom: 0.5;
	
	-moz-transform: scale(0.5);
	-moz-transform-origin: 0 0;
	
	-o-transform: scale(0.5);
	-o-transform-origin: 0 0;
	
	-webkit-transform: scale(0.5);
	-webkit-transform-origin: 0 0;
	
	transform: scale(0.3);
	transform-origin: 5 0;	
	
	border:0px solid #ccc;
	border:0px solid #ccc;
	top:0px;
	left:0px;
	width:320%;
	height:336%;
}
</style>
</head>
<script type="text/javascript">
setTimeout("history.go(0);",10000);
</script>
<body onload="">
<%
ArrayList<String> iplist = new ArrayList<String>();
BufferedReader fb;
BufferedReader fb2;
try {
	fb = new BufferedReader(new FileReader("C:\\Users\\Playdata\\Desktop\\workspace\\Mini Project\\WebContent\\iplist.txt"));
	fb2 = new BufferedReader(new FileReader("C:\\Users\\Playdata\\Desktop\\workspace\\Mini Project\\WebContent\\speakerip.txt"));
	String ips = null;
	while (fb.read()!=-1){
		ips = fb.readLine();
		System.out.println("iplist:"+ips);
	}
	String speakerIp = fb2.readLine();
	System.out.println("speakerip:"+speakerIp);
	fb.close();
	fb2.close();
	String[] tmp = {""};
	if(ips != null)
		tmp = ips.split("/");
	
	%>
	<table border="1" width="100" height="150">
	<tr><th>발언자</th><th>참여자들</th></tr>
	<tr><td rowspan="3">
	<div class="cssZoom">
	<iframe frameborder="0" src="http://<%=speakerIp%>:8091/javascript_simple.html" scrolling="no"></iframe>
	</div>
	</td>

<%
//테이블2: 나머지 클라이언트
System.out.println("check1");

	for(int i=0;i<tmp.length;i++){
		System.out.println(i+":"+tmp[i]);
	if(i!=0){ %>
	<tr>
	<%} %>
	<td><div class="cssZoom2">
	<iframe src="http://<%=tmp[i]%>:8091/javascript_simple.html" scrolling="no"></iframe></div></td>
	</tr>
	<%
	System.out.println("check2");
}
} catch (FileNotFoundException e1) {
	// TODO Auto-generated catch block
	e1.printStackTrace();
} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
%>
</table>
</body>
</html>