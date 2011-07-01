<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:variable name="p" />
	<xsl:variable name="counter" />
	<xsl:template match="page">
		<HTML>
			<HEAD>
				<META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE" />
				<META http-equiv="Refresh" content="30" />
				<TITLE>Welcome to jcrontabEntry Editor</TITLE>
			</HEAD>
			<BODY>
				<H2>Welcome to jcrontabEntry Editor</H2>
				<P>
					<TABLE border="0" width="100%" cellspacing="0" cellspading="0">
						<xsl:for-each select="error">
							<xsl:apply-templates />
						</xsl:for-each>
					</TABLE>
				</P>
				<TABLE border="2" width="100%" cellspacing="0" cellspading="0">
					<TR>
						<TD>
							<a href="help.html#remove">Remove</a>
						</TD>
						<TD>
							<a href="help.html#minutes">Minutes</a>
						</TD>
						<TD>
							<a href="help.html#hours">Hours</a>
						</TD>
						<TD>
							<a href="help.html#daysOfMonth">Days of month</a>
						</TD>
						<TD>
							<a href="help.html#month">Month</a>
						</TD>
						<TD>
							<a href="help.html#daysOfWeek">Days of week</a>
						</TD>
						<TD>timestamp</TD>
						<TD>retval</TD>
						<TD>count</TD>
						<TD>
							<a href="help.html#class">Classname#Methodname</a>
						</TD>
						<TD>
							<a href="help.html#extraInfo">Extrainfo</a>
						</TD>
					</TR>
					<form action="jcrontabxml" method="post" name="delete">
						<input type="hidden" name="event" value="1" />
						<xsl:for-each select="crontabentries">
							<xsl:apply-templates />
						</xsl:for-each>
						<TR>
							<TD>
								<input type="submit" name="delete" value="delete"></input>
							</TD>
						</TR>
					</form>
					<form action="jcrontabxml" method="post" name="add">
						<input type="hidden" name="event" value="0" />
						<TR>
							<TD width="3">
								<input type="checkbox" name=""></input>
							</TD>
							<TD width="6">
								<input type="text" name="Minutes" value="*"></input>
							</TD>
							<TD width="6">
								<input type="text" name="Hours" value="*"></input>
							</TD>
							<TD width="6">
								<input type="text" name="Daysofmonth" value="*"></input>
							</TD>
							<TD width="6">
								<input type="text" name="Month" value="*"></input>
							</TD>
							<TD width="6">
								<input type="text" name="Daysofweek" value="*"></input>
							</TD>
							<TD width="6">
								<input type="text" name="Classname"></input>
							</TD>
							<TD>
								<input type="text" name="Extrainfo"></input>
							</TD>
						</TR>
						<TR>
							<TD>
								<input type="submit" name="save" value="save"></input>
							</TD>
						</TR>
					</form>
				</TABLE>
			</BODY>
		</HTML>
	</xsl:template>
	<xsl:template match="crontabentry">
		<TR>
			<TD width="3">
				<input name="remove" type="checkbox" value="{@id}" />
				<input name="remove1" type="hidden" value="{../@id}" />
				<input name="remove3" type="hidden" value="${id}" />
				<input name="remove4" type="hidden" value="${@id}" />
				<xsl:value-of select="id" />
			</TD>
			<TD width="3">
				<xsl:value-of select="minutes" />
			</TD>

			<TD width="3">
				<xsl:value-of select="hours" />
			</TD>
			<TD width="3">
				<xsl:value-of select="daysofmonth" />
			</TD>
			<TD width="3">
				<xsl:value-of select="months" />
			</TD>
			<TD width="3">
				<xsl:value-of select="daysofweek" />
			</TD>
			<TD width="3">
				<xsl:value-of select="beanLastStarted" />
			</TD>
			<TD width="3">
				<xsl:value-of select="beanLastResult" />
			</TD>
			<TD width="3">
				<xsl:value-of select="beanExecCount" />
			</TD>
			<TD width="3">
				<xsl:value-of select="class" />
				<xsl:if test="method!=''">
					#
					<xsl:value-of select="method" />
				</xsl:if>
			</TD>
			<TD>
				<xsl:for-each select="parameters">
					<xsl:value-of select="." />
				</xsl:for-each>
			</TD>
		</TR>
	</xsl:template>
	<xsl:template match="error">
		<TR>
			<TD>
				<font color="#A0522D">Error:</font>
			</TD>
			<TD>
				<font color="#A0522D">
					<xsl:value-of select="text" />
				</font>
			</TD>
		</TR>

	</xsl:template>

</xsl:stylesheet>

