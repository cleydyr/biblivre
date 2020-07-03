<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:param name="source">no</xsl:param>
<xsl:param name="image_html">no</xsl:param>
<xsl:param name="full_details_link">no</xsl:param>

<xsl:output method="html"/>

<xsl:template match="/">
	<xsl:apply-templates select="iso2709"/>
</xsl:template>

<xsl:template match="iso2709">
	<table border="0" width="100%">
               <tr>
                        <td align="left" width="80" valign="top">
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
					<xsl:apply-templates select="field[@tag=245]"/>
                                </a>
                        </td>
                </tr>
<!--
	<xsl:apply-templates select="field[@tag=35]"/>
	<xsl:apply-templates select="field[@tag=37]"/>
	<xsl:apply-templates select="field[@tag=40]"/>
	<xsl:apply-templates select="field[@tag=246]"/>
	<xsl:apply-templates select="field[@tag=300]"/>
	<xsl:apply-templates select="field[@tag=321]"/>
	<xsl:apply-templates select="field[@tag=362]"/>
	<xsl:apply-templates select="field[@tag=506]"/>
	<xsl:apply-templates select="field[@tag=513]"/>
	<xsl:apply-templates select="field[@tag=520]"/>
	<xsl:apply-templates select="field[@tag=555]"/>
	<xsl:apply-templates select="field[@tag=650]"/>
	<xsl:apply-templates select="field[@tag=785]"/>
-->
	</table>
</xsl:template>

<xsl:template match="field[@tag=35]">
	<tr><td align="right" nowrap="true" valign="top">System control number : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="field[@tag=37]">
	<tr><td align="right" nowrap="true" valign="top">Source of Acquisition : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="field[@tag=40]">
	<tr><td align="right" nowrap="true" valign="top">Cataloging Source : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="field[@tag=245]">
	<xsl:value-of select="."/>
</xsl:template>

<xsl:template match="field[@tag=246]">
	<tr><td align="right" nowrap="true" valign="top">Subtitle : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="field[@tag=300]">
	<tr>
		<td align="right" nowrap="true" valign="top">Pub info : </td>
		<td>
			<xsl:value-of select="."/>
		</td>
	</tr>
</xsl:template>

<xsl:template match="field[@tag=321]">
	<tr><td align="right" nowrap="true" valign="top">321 : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="field[@tag=362]">
	<tr><td align="right" nowrap="true" valign="top">362 : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="field[@tag=506]">
	<tr><td align="right" nowrap="true" valign="top">Restrictions on Access : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="field[@tag=513]">
	<tr><td align="right" nowrap="true" valign="top">Type of report and Period Covered : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="field[@tag=520]">
	<tr><td align="right" nowrap="true" valign="top">Summary : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="field[@tag=555]">
	<tr><td align="right" nowrap="true" valign="top">Summary : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="field[@tag=650]">
	<tr><td align="right" nowrap="true" valign="top">Subject : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>

<xsl:template match="field[@tag=785]">
	<tr><td align="right" nowrap="true" valign="top">Lookup 785 : </td> <td><xsl:value-of select="."/></td></tr>
</xsl:template>
</xsl:stylesheet>
