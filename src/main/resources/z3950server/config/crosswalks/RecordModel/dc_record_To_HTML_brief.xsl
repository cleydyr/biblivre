<?xml version="1.0"?>
 
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
<xsl:param name="source">no</xsl:param>
<xsl:param name="image_html">no</xsl:param>
<xsl:param name="full_details_link">no</xsl:param>

<xsl:output method="html"/>

<xsl:template match="/">
	<xsl:apply-templates select="dc-record" />
</xsl:template>

<xsl:template match="dc-record">
	<table border="0" width="100%">
		<tr>
			<td valign="top" align="left" width="80">
				<img>
                                	<xsl:attribute name="src">
						<xsl:value-of select="$image_html"/>
					</xsl:attribute>
				</img>
			</td>
			<td align="left" valign="top">
				<a>
                                	<xsl:attribute name="href">
						<xsl:value-of select="$full_details_link"/>
					</xsl:attribute>
					<xsl:apply-templates select="title"/>
				</a>
			</td>
	  	</tr>
	  	<tr>
			<td colspan="2">
				<xsl:apply-templates select="description"/>
			</td>
	  	</tr>
	</table>
</xsl:template>


</xsl:stylesheet>
