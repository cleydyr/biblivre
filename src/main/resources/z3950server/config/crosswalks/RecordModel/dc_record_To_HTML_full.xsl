<?xml version="1.0"?>
 
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
                              xmlns:idzebra="http://www.indexdata.dk/zebra/">

<xsl:param name="source">no</xsl:param>
<xsl:param name="image_html">no</xsl:param>

<xsl:output method="html"/>

<xsl:template match="/">
	<xsl:apply-templates select="dc-record" />
</xsl:template>

<xsl:template match="dc-record">
	<table border="0" width="100%">
		<tr>
			<td align="right">Title</td>
			<td>
				<xsl:apply-templates select="title"/>
			</td>
	  	</tr>
		<tr>
			<td align="right">Source</td>
			<td>
                                <img>
                                        <xsl:attribute name="src">
                                                <xsl:value-of select="$image_html"/>
                                        </xsl:attribute>
                                </img>
			</td>
	  	</tr>
		<tr>
			<td align="right">Publication date</td>
			<td> 
				<xsl:apply-templates select="pubdate"/>
			</td>
	  	</tr>
		<tr>
			<td align="right">Description</td>
			<td> 
				<xsl:apply-templates select="descriptions"/>
			</td>
	  	</tr>
	</table>
</xsl:template>


</xsl:stylesheet>
