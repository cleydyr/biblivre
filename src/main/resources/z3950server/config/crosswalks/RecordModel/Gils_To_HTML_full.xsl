<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>

<xsl:template match="/">
	<xsl:apply-templates select="grs" />
</xsl:template>

<xsl:template match="grs">
        <table>
		<xsl:apply-templates select="GRSTag[@type=2 and @value=1]"/> <!-- Title -->
		<xsl:apply-templates select="GRSTag[@type=1 and @value=12]"/> <!-- URL -->
		<xsl:apply-templates select="GRSTag[@type=1 and @value=14]"/> <!-- Control number -->
		<xsl:apply-templates select="GRSTag[@type=1 and @value=16]"/> <!-- Date of last modification -->
		<xsl:apply-templates select="GRSTag[@type=2 and @value=2]"/> <!-- Contributor -->
		<xsl:apply-templates select="GRSTag[@type=2 and @value=3]"/> <!-- Place of Publication -->
		<xsl:apply-templates select="GRSTag[@type=2 and @value=4]"/> <!-- Date of Publication -->
		<xsl:apply-templates select="GRSTag[@type=2 and @value=6]"/> <!-- Abstract-->
		<xsl:apply-templates select="GRSTag[@type=4 and @value=19]"/> <!-- Record Source -->
		<xsl:apply-templates select="GRSTag[@type=4 and @value=24]"/> <!-- Record Review Date -->
		<xsl:apply-templates select="GRSTag[@type=4 and @value=31]"/> <!-- Schedule number -->
		<xsl:apply-templates select="GRSTag[@type=4 and @value=34]"/> <!-- Language of Record -->
		<xsl:apply-templates select="GRSTag[@type=4 and @value=51]"/> <!-- Purpose -->
		<xsl:apply-templates select="GRSTag[@type=4 and @value=52]"/> <!-- Originator -->
		<xsl:apply-templates select="GRSTag[@type=4 and @value=53]"/> <!-- Access Constraints -->
		<xsl:apply-templates select="GRSTag[@type=4 and @value=54]"/> <!-- Use Constraints -->
		<xsl:apply-templates select="GRSTag[@type=4 and @value=56]"/> <!-- Agency Program -->
		<xsl:apply-templates select="GRSTag[@type=4 and @value=57]"/> <!-- Sources of data -->
		<xsl:apply-templates select="GRSTag[@type=4 and @value=58]"/> <!-- Methodology -->
		<xsl:apply-templates select="GRSTag[@type=4 and @value=59]"/> <!-- Additional info -->
		<xsl:apply-templates select="GRSTag[@type=4 and @value=70]"/> <!-- Availability-->
		<xsl:apply-templates select="GRSTag[@type=4 and @value=71]"/> <!-- Spatial Domain -->
		<xsl:apply-templates select="GRSTag[@type=4 and @value=93]"/> <!-- Time Period -->
		<xsl:apply-templates select="GRSTag[@type=4 and @value=94]"/> <!-- Point Of Contact -->
		<xsl:apply-templates select="GRSTag[@type=4 and @value=95]"/> <!-- Supplemental Information -->
		<xsl:apply-templates select="GRSTag[@type=4 and @value=95]"/> <!-- Controlled Subject Index -->
		<xsl:apply-templates select="GRSTag[@type=4 and @value=97]"/> <!-- Uncontrolled Subject Terms -->
		<xsl:apply-templates select="GRSTag[@type=4 and @value=98]"/> <!-- Cross Reference -->
	</table>
</xsl:template>

<xsl:template match="GRSTag[@type=1 and @value=12]">
  <tr><td align="right" nowrap="true" valign="top">URL : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="GRSTag[@type=2 and @value=1]">
  <tr><td align="right" nowrap="true" valign="top">Title : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="GRSTag[@type=1 and @value=14]">
	<tr><td align="right" nowrap="true" valign="top">Control Number : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="GRSTag[@type=1 and @value=16]">
	<tr><td align="right" nowrap="true" valign="top">Last Modified : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="GRSTag[@type=4 and @value=70]">
	<xsl:apply-templates select="GRSTag[@type=4 and @value=7]"/> <!-- 4,7 = availability -->
</xsl:template>

<xsl:template match="GRSTag[@type=4 and @value=7]">
	<tr><td align="right" nowrap="true" valign="top">Resource Description : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="GRSTag[@type=4 and @value=59]">
	<tr><td align="right" nowrap="true" valign="top">Supplemental Information : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="GRSTag[@type=4 and @value=94]">
	<xsl:apply-templates select="GRSTag[@type=2 and @value=7]"/> <!-- Name -->
	<xsl:apply-templates select="GRSTag[@type=2 and @value=10]"/> <!-- Organisation -->
	<xsl:apply-templates select="GRSTag[@type=2 and @value=14]"/> <!-- Phone/Fax -->
	<xsl:apply-templates select="GRSTag[@type=4 and @value=2]"/> <!-- Street Address -->
</xsl:template>

<xsl:template match="GRSTag[@type=2 and @value=7]">
	<tr><td align="right" nowrap="true" valign="top">Name : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="GRSTag[@type=2 and @value=10]">
	<tr><td align="right" nowrap="true" valign="top">Organisation : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="GRSTag[@type=2 and @value=14]">
	<tr><td align="right" nowrap="true" valign="top">Phone/Fax : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="GRSTag[@type=4 and @value=2]">
	<tr><td align="right" nowrap="true" valign="top">Street Address : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="GRSTag[@type=4 and @value=71]">
	<tr><td align="right" nowrap="true" valign="top">Place : </td> <td><xsl:apply-templates select="GRSTag[@type=4 and @value=92]"/></td></tr>
</xsl:template>

<xsl:template match="GRSTag[@type=4 and @value=92]">
	<xsl:apply-templates select="GRSTag[@type=4 and @value=13]"/> <!-- Place Keyword -->
</xsl:template>

<xsl:template match="GRSTag[@type=4 and @value=13]">
	<xsl:value-of select="."/><xsl:text disable-output-escaping='yes'>&amp;nbsp;</xsl:text>
</xsl:template>

</xsl:stylesheet>
