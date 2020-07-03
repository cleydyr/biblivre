<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:include href="character_replacement.xsl"/>
<xsl:param name="hitno">1</xsl:param>
<xsl:param name="bp">1</xsl:param>
<xsl:param name="debug">no</xsl:param>
<xsl:param name="source">no</xsl:param>
<xsl:output method="html"/>

<xsl:template match="/">
    <xsl:apply-templates select="grs" />
</xsl:template>

<xsl:template match="grs">
<tr>
    <td valign="top" class="documentnames">
        <a href="processing_summary.jsp?hitno={$hitno}&amp;bp={$bp}">
		<xsl:apply-templates select="./GRSTag[@type=2 and @value='1']"/>
        </a>
    </td>
    <td valign="top" class="documentnames">
        <xsl:choose>
            <xsl:when test="$source='ang'">
                <img src="images/source_a2a.gif" border="0" alt="Source: A2A" name="A2A"/>
                <!--A2A-->
            </xsl:when>
            <xsl:when test="$source='archhub'">
                <img src="images/source_hub.gif" border="0" alt="Source: Hub" name="Hub"/>
            </xsl:when>
            <xsl:otherwise>
		    <!--Hub-->
		    Unknown
	    </xsl:otherwise>
        </xsl:choose>
    </td>
    <td valign="top" nowrap="true">
    </td>
  <td valign="top">
      <a href="processing_summary.jsp?hitno={$hitno}&amp;bp={$bp}"><img src="images/sr_recordsummary.gif" border="0" alt="Document Summary" onMouseOver="document['sum1'].src='images/sr_recordsummary_on.gif'" onMouseOut="document['sum1'].src='images/sr_recordsummary.gif'" name="sum1"/></a>
      <a href="processing_complete.jsp?hitno={$hitno}&amp;bp={$bp}" target="new"> <img src="images/sr_recordfull.gif" border="0" alt="Complete Document"  onMouseOver="document['ful1'].src='images/sr_recordfull_on.gif'" onMouseOut="document['ful1'].src='images/sr_recordfull.gif'" name="ful1"/></a>&#160;
  </td>
</tr>
</xsl:template>

</xsl:stylesheet>
