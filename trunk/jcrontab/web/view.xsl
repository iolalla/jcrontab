<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<!-- <xsl:variable name="title" select="concat(todo/@project, ' ', todo/@major-version)"/> -->
  <xsl:template match="/">
    <HTML>
      <HEAD>
         <TITLE>Welcome to jcrontabEntry Editor</TITLE>
      </HEAD>
      <BODY>

        <H2>Welcome to jcrontabEntry Editor</H2>
        <TABLE border="3">
        <xsl:for-each select="page">           
                 <xsl:apply-templates/>           
        </xsl:for-each>          
        </TABLE>
      </BODY>
    </HTML>
  </xsl:template>


  <xsl:template match="crontabentry">
    <TR>
    <TD>
        <xsl:value-of select="classname"/>
    </TD>
    <TD>
        <xsl:value-of select="methodname"/>  
    </TD>
    </TR>
  </xsl:template>

</xsl:stylesheet>