<?xml version="1.0"?>
 
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:param name="hitno">1</xsl:param>
<xsl:param name="bp">1</xsl:param>
<xsl:param name="debug">no</xsl:param>
<xsl:output method="html"/>

<xsl:template match="/">
	<xsl:apply-templates select="ead" />
</xsl:template>

<xsl:template match="ead">
<tr>
	<!-- <td width="20" align="center" valign="top" class="documenthitz"><b>1</b></td> -->
  <td valign="top" class="documentnames">
	  <a href="summary.jsp?hitno={$hitno}&amp;bp={$bp}"> 
		    <xsl:if test="/ead/index-title">
			    <xsl:apply-templates select="/ead/index-title"/> /
		    </xsl:if>
		    <xsl:apply-templates select="./archdesc/did/unittitle"/>
	    </a>
  </td>
  <td valign="top" nowrap="true"><xsl:apply-templates select="./archdesc/did/unitdate"/> </td>
  <td valign="top"> 
	  <a href="summary.jsp?hitno={$hitno}&amp;bp={$bp}">
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
