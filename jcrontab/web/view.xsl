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
        <TABLE border="2" width="100%" 
            cellspacing="0" cellspading="0">
    <TR>
    <TD>
        Minutes
    </TD>
    <TD>
        Hours
    </TD>
    <TD>
        Days of month
    </TD>
    <TD>
        Month
    </TD>
    <TD>
        Days of week
    </TD>
    <TD>
        Classname#Methodname
    </TD>
    <TD>
        Priority
    </TD>
    <TD>
        Extrainfo
    </TD>
    </TR>
        <xsl:for-each select="page">           
                 <xsl:apply-templates/>           
        </xsl:for-each>
    <form action="jcrontabxml" method="get">
    <TR>
            <TD width="6">
                <input type="text" name= "Minutes"
                    value="*"></input>
            </TD>

            <TD width="6">
                <input type="text" name= "Hours"
                    value="*"></input>
            </TD>
            <TD width="6">
                <input type="text" name= "Daysofmonth"
                    value="*"></input>
            </TD>
            <TD width="6">
                <input type="text" name= "Month"
                    value="*"></input>
            </TD>
            <TD width="6">
                <input type="text" name= "Daysofweek"
                    value="*"></input>
            </TD>
            <TD width="6">
                <input type="text" name= "Classname"></input>
            </TD>
            <TD width="6">
                <input type="text" name= "Priority" size="1"
                    value="1"></input>
            </TD>
            <TD>
                <input type="text" name= "Extrainfo"></input>
            </TD>
    </TR>
    <TR>
            <TD>
                <input type="submit" name="apply" value="apply"></input>
            </TD>
    </TR>
    </form>
    </TABLE>
      </BODY>
    </HTML>
  </xsl:template>


  <xsl:template match="crontabentry">
    <TR>
    <TD width="6">
        <xsl:value-of select="minutes"/> 
    </TD>
    <TD width="6">
        <xsl:value-of select="hours"/> 
    </TD>
    <TD width="6">
        <xsl:value-of select="daysofmonth"/> 
    </TD>
    <TD width="6">
        <xsl:value-of select="month"/> 
    </TD>
    <TD width="6">
        <xsl:value-of select="daysofweek"/> 
    </TD>
    <TD width="6">
        <xsl:value-of select="classname"/>
        <xsl:if test="methodname!='NULL'">
            #<xsl:value-of select="methodname"/>
        </xsl:if>
    </TD>
    <TD>
        <xsl:value-of select="priority"/> 
    </TD>
    <TD>
    <xsl:for-each select="extrainfo">
        <xsl:value-of select="."/>
    </xsl:for-each>
    </TD>
    </TR>
  </xsl:template>

</xsl:stylesheet>