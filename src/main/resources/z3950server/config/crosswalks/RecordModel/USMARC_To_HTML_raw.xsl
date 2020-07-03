<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>

<xsl:template match="/">
	<xsl:apply-templates select="iso2709"/>
</xsl:template>

<xsl:template match="iso2709">
	<table>
        <xsl:apply-templates select="field"/>
	</table>
</xsl:template>

<xsl:template match="field">
        <tr><td align="right">
                <xsl:value-of select="@tag"/>
        </td><td>
                <xsl:value-of select="@Indicator1"/>
                <xsl:value-of select="@Indicator2"/>
        </td><td>
                <xsl:apply-templates select="subfield|text()"/>
        </td></tr>
</xsl:template>

<xsl:template match="subfield">
        <b> <xsl:value-of select="@code"/> </b> &#160;
              <xsl:value-of select="."/>
        &#160;
</xsl:template>

</xsl:stylesheet>
