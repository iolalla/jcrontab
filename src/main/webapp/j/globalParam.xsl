<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:param name="ageParam">60</xsl:param>

  <xsl:template match="people">
    <h1>List of people in the XML document</h1>
    <table border="1">
      <th>Name</th><th>Age</th>
      <xsl:apply-templates />
    </table>
  </xsl:template>

  <xsl:template match="person">
    <xsl:call-template name="processPerson">
      <xsl:with-param name="ageParam">
        51
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="processPerson">
    <!-- <xsl:param name="ageParam" /> -->

    <tr>
      <td>
        <xsl:value-of select="name/text()" />
      </td>
      <td>
        <xsl:value-of select="age/text()"  />
        <xsl:if test="age/text() > $ageParam">
            (old!)
          </xsl:if>
      </td>
    </tr>    
  </xsl:template>
</xsl:transform>