<?xml version="1.0"?>
 
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                              xmlns:dc="http://purl.org/dc/elements/1.1/" 
                              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
                              xmlns:idzebra="http://www.indexdata.dk/zebra/">

<xsl:param name="source">no</xsl:param>
<xsl:param name="image_html">no</xsl:param>

<xsl:output method="html"/>

<xsl:template match="/">
	<xsl:apply-templates select="dc:dc" />
</xsl:template>

<xsl:template match="dc:dc">
	<table border="0" width="100%">
		<tr>
			<td>
				<xsl:apply-templates select="dc:title"/>
			</td>
			<td>
                                <img>
                                        <xsl:attribute name="src">
                                                <xsl:value-of select="$image_html"/>
                                        </xsl:attribute>
                                </img>
			</td>
			<td align="right"> 
				<xsl:apply-templates select="dc:pubdate"/>
			</td>
	  	</tr>
	</table>
</xsl:template>


</xsl:stylesheet>
