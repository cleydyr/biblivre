<?xml version="1.0"?>
 
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://purl.org/dc/elements/1.1/ http://www.openarchives.org/OAI/1.1/dc.xsd" xmlns:idzebra="http://www.indexdata.dk/zebra/">
<xsl:param name="source">no</xsl:param>
<xsl:param name="image_html">no</xsl:param>
<xsl:param name="full_details_link">no</xsl:param>

<xsl:output method="html"/>

<xsl:template match="/">
	<xsl:apply-templates select="dc:dc" />
</xsl:template>

<xsl:template match="dc:dc">
	<table border="0" width="100%">
		<tr>
			<td align="left" valign="top" width="80">
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
					<xsl:apply-templates select="dc:title"/>
				</a>
			</td>
			<td align="right"> 
				<xsl:apply-templates select="dc:date"/>
			</td>
	  	</tr>
	</table>
</xsl:template>


</xsl:stylesheet>
