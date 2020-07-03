<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:param name="hitno"/>
<xsl:param name="debug"/>
<xsl:output method="html"/>

<xsl:template match="/">
	<xsl:apply-templates select="sdfa" />
</xsl:template>

<xsl:template match="sdfa">
<tr>
  <td width="20" align="center" valign="top" class="documenthitz"><b>1</b></td>
  <td valign="top" class="documentnames"><a href="summary.jsp?hitno={$hitno}">
		<xsl:choose>
		  <xsl:when test="./title">
			  <xsl:apply-templates select="./title"/>
		  </xsl:when>
		  <xsl:otherwise> [from scope] :
			  <xsl:apply-templates select="./scope"/><!-- Scope as title -->
		  </xsl:otherwise>
	        </xsl:choose>
  </a></td>
  <td valign="top" nowrap="true"><xsl:apply-templates select="./unitdate"/></td>
  <td valign="top" nowrap="true">
	  <a href="summary.jsp?hitno={$hitno}">
		  <img src="images/sr_recordsummary.gif"
			  border="0"
			  alt="Document Summary"
			  onMouseOver="document['sum1'].src='images/sr_recordsummary_on.gif'"
			  onMouseOut="document['sum1'].src='images/sr_recordsummary.gif'"
			  name="sum1"/>
	  </a>&#160;
	  <img src="images/sr_recordfull.gif"
		  border="0"
		  alt="Complete Document"
		  onMouseOver="document['ful1'].src='images/sr_recordfull_on.gif'"
		  onMouseOut="document['ful1'].src='images/sr_recordfull.gif'"
		  name="ful1"/>&#160;

          <xsl:if test="$debug='yes'">
            <a href="dumprec.jsp?hitno={$hitno}">dump</a>
          </xsl:if>
  </td>
</tr>
</xsl:template>

</xsl:stylesheet>
