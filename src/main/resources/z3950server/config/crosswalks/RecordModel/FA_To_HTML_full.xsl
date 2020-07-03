<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>

<xsl:template match="/">
	<xsl:apply-templates select="sdfa" />
</xsl:template>

<xsl:template match="sdfa">
	<table width="90%" cellpadding="0" cellspacing="0">
		<tr>
			<td align="center">
				<b><xsl:apply-templates select="./document"/></b>
			</td>
		</tr>
		<tr>
			<td>
			<b><span class="filetext"><xsl:apply-templates select="./level"/></span></b> -
			<span class="filetext"><xsl:apply-templates select="./title"/></span>
			<b>ref.</b><span class="refnumdata"><xsl:apply-templates select="./documentId"/></span>
			<span class="filetext"><b>date</b><xsl:apply-templates select="./unitdate"/></span>
			</td>
		</tr>

		<xsl:if test="./scope">
		<tr>
			<td class="contenttext">
				<font size="2">
					<span class="info">[from <i>Scope and Content</i>] </span>
				&#160;</font>
				<xsl:apply-templates select="./scope"/>
			</td>
		</tr>
		</xsl:if>
	</table>
</xsl:template>

</xsl:stylesheet>
