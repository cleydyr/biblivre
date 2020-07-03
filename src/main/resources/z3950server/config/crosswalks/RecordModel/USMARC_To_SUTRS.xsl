<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>

<xsl:template match="/">
	<xsl:apply-templates select="iso2709"/>
</xsl:template>

<xsl:template match="iso2709">
<xsl:apply-templates select="field[@tag=35]"/>
<xsl:apply-templates select="field[@tag=37]"/>
<xsl:apply-templates select="field[@tag=40]"/>
<xsl:apply-templates select="field[@tag=245]"/>
<xsl:apply-templates select="field[@tag=246]"/>
<xsl:apply-templates select="field[@tag=300]"/>
<xsl:apply-templates select="field[@tag=506]"/>
<xsl:apply-templates select="field[@tag=513]"/>
<xsl:apply-templates select="field[@tag=520]"/>
<xsl:apply-templates select="field[@tag=555]"/>
<xsl:apply-templates select="field[@tag=650]"/>
<xsl:apply-templates select="field[@tag=785]"/>
</xsl:template>

<xsl:template match="field[@tag=35]">
System control number : <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="field[@tag=37]">
Source of Acquisition : <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="field[@tag=40]">
Cataloging Source : <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="field[@tag=245]">
Title : <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="field[@tag=246]">
Subtitle : <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="field[@tag=300]">
Pub info : <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="field[@tag=506]">
	Restrictions on Access : <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="field[@tag=513]">
	Type of report and Period Covered : <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="field[@tag=520]">
	Summary : <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="field[@tag=555]">
	Summary : <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="field[@tag=650]">
	Subject : <xsl:value-of select="."/>
</xsl:template>

<xsl:template match="field[@tag=785]">
	Lookup 785 : <xsl:value-of select="."/>
</xsl:template>

</xsl:stylesheet>
