<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>

<xsl:template match="/">
<table>
	<tr><td><pre><xsl:value-of select="sutrs"/></pre></td></tr>
</table>
</xsl:template>

</xsl:stylesheet>
