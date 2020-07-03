<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>

<xsl:template match="/">
	<xsl:apply-templates select="grs" />
</xsl:template>

<xsl:template match="grs">
	<table border="0" width="100%">
		<tr>
			<td>
				<xsl:apply-templates select="GRSTag[@type=2 and @value=1]"/> <!-- Title -->
			</td>
			<td align="right">
				<font size="-1">
				<xsl:apply-templates select="GRSTag[@type=1 and @value=16]"/> <!-- Date of last modification -->
				</font>
			</td>
	  	</tr>
	</table>
</xsl:template>


</xsl:stylesheet>
