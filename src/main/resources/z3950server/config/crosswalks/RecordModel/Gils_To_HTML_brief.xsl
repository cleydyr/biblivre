<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>

<xsl:template match="/">
	<xsl:apply-templates select="grs" />
</xsl:template>

<xsl:template match="grs">
<tr>
  <td width="20" align="center" valign="top" class="documenthitz"><b>1</b></td>
  <td valign="top" class="documentnames"><a href="#"> <xsl:apply-templates select="GRSTag[@type=2 and @value=1]"/><!-- Title --></a></td>
  <td valign="top" nowrap="true"><xsl:apply-templates select="GRSTag[@type=1 and @value=14]"/></td>
  <td valign="top">
	  <a href="#"><img src="images/sr_recordsummary.gif" border="0" alt="Document Summary" onMouseOver="document['sum1'].src='images/sr_recordsummary_on.gif'" onMouseOut="document['sum1'].src='images/sr_recordsummary.gif'" name="sum1"/></a>&#160;<img src="images/sr_recordfull.gif" border="0" alt="Complete Document"  onMouseOver="document['ful1'].src='images/sr_recordfull_on.gif'" onMouseOut="document['ful1'].src='images/sr_recordfull.gif'" name="ful1"/>&#160;
  </td>
</tr>
</xsl:template>

</xsl:stylesheet>
