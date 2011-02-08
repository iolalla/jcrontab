<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Shorty - An Open Source URL Shortner in Struts2/Hibernate | ViralPatel.net</title>
<link href="css/style.css" rel="stylesheet"/>
</head>
<body>
<h1 class="title">Shorty</h1>
<br>
<p>A Simple URL Shortner in Struts2/Hibernate/MySQL</p>
<br><br>
<div id="link-container">
 
    <form action="add" method="post">
        <actionerror/>
        <textfield name="url" cssClass="link"/>
        <submit value="Shorten"/>
    </form>
    <if test="link.shortCode != null">
          <h3><text name="shorty.base.url"/><property value="link.shortCode"/></h3>
    </if>
 
</div>
</body>
</html>