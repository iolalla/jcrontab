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
        <TABLE border="2">
    <TR>
    <TD>
        Classname
    </TD>
    <TD>
        Methodname 
    </TD>
    <TD>
        Hours
    </TD>
    <TD>
        Daysofweek
    </TD>
    <TD>
        Daysofmonth
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
    <form action="" method="get">
    <TR>

            <TD>
                <input type="text" name= "Classname"></input>
            </TD>
            <TD>
                <input type="text" name= "Methodname"></input>
            </TD>
            <TD>
                <input type="text" name= "Hours" size="6"></input>
            </TD>
            <TD>
                <input type="text" name= "Daysofweek" size="6"></input>
            </TD>
            <TD>
                <input type="text" name= "Daysofmonth" size="6"></input>
            </TD>
            <TD>
                <input type="text" name= "Priority" size="2"></input>
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
        <xsl:value-of select="."/>
    </xsl:for-each>
    </TD>
    </TR>
  </xsl:template>

</xsl:stylesheet>