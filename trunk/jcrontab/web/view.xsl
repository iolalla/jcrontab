<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:variable name="p" />

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
    <TD>
        <xsl:value-of select="hours"/> 
    </TD>
    <TD>
        <xsl:value-of select="daysofweek"/> 
    </TD>
    <TD>
        <xsl:value-of select="daysofmonth"/> 
    </TD>
    <TD>
        <xsl:value-of select="priority"/> 
    </TD>
    <TD>
    <xsl:for-each select="extrainfo">
        <xsl:variable name="pi" select="extrainfo"/>
        <xsl:variable name="p" select="concat($p , ' ', $pi)"/>
    </xsl:for-each>
        <xsl:value-of select="$p"/>
    </TD>
    </TR>
  </xsl:template>

  <xsl:template match="extrainfo">
    <TD>
        <xsl:variable name="pi" select="parameter"/>
        <xsl:variable name="p" select="concat($p , ' ', $pi)"/>
        <xsl:value-of select="$p"/>
    </TD>
  </xsl:template>
</xsl:stylesheet>