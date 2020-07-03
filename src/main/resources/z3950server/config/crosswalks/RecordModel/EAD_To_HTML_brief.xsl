<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:param name="hitno">1</xsl:param>
<xsl:param name="bp">1</xsl:param>
<xsl:param name="debug">no</xsl:param>
<xsl:param name="source">no</xsl:param>
<xsl:output method="html"/>

<xsl:template match="/">
    <xsl:apply-templates select="EAD" />
</xsl:template>

<xsl:template match="EAD">
<tr>
    <!-- <td width="20" align="center" valign="top" class="documenthitz"><b>1</b></td> -->
    <td valign="top" class="documentnames">
        <a href="processing_summary.jsp?hitno={$hitno}&amp;bp={$bp}">
            <xsl:choose>
                <xsl:when test="/EAD/index-id"><xsl:apply-templates select="/EAD/index-id"/> - </xsl:when>
                <xsl:otherwise>[No ref.] -</xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="/EAD/index-title"> <xsl:apply-templates select="/EAD/index-title"/> </xsl:when>
                <xsl:otherwise>
                    <xsl:choose>

                        <xsl:when test="/EAD/index-scope"> <xsl:value-of select="substring(./EAD/index-scope,1,30)"/></xsl:when>
                        <xsl:otherwise> [No scope or title]</xsl:otherwise>
                    </xsl:choose>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="./ARCHDESC/DID/UNITTITLE"> ( Part of <xsl:apply-templates select="./ARCHDESC/DID/UNITTITLE" /> )</xsl:if>
        </a>
    </td>
    <td valign="top" nowrap="true">
        <xsl:choose>
            <xsl:when test="$source='ang'">
                <img src="images/source_a2a.gif" border="0" alt="Source: A2A" name="A2A"/>
                <!--A2A -->
            </xsl:when>
            <xsl:when test="$source='archhub'">
                <img src="images/source_hub.gif" border="0" alt="Source: Hub" name="Hub"/>
            </xsl:when>
	    <xsl:otherwise>
		 Unknown
            </xsl:otherwise>
        </xsl:choose>
    </td>
    <td valign="top" nowrap="true"><xsl:apply-templates select="./ARCHDESC/DID/UNITDATE"/> </td>
    <td valign="top">
      <a href="processing_summary.jsp?hitno={$hitno}&amp;bp={$bp}">
          <img src="images/sr_recordsummary.gif"
              border="0"
              alt="Document Summary"
              onMouseOver="document['sum1'].src='images/sr_recordsummary_on.gif'"
              onMouseOut="document['sum1'].src='images/sr_recordsummary.gif'"
              name="sum1"/>
      </a>
      <xsl:if test="/EAD/full-display"><a target="new"><xsl:attribute name="href"><xsl:value-of select='substring-before(normalize-space(/EAD/full-display),".xml")'/>.htm</xsl:attribute><img src="images/sr_recordfull.gif"
             border="0"
             alt="Complete Document"
             onMouseOver="document['ful1'].src='images/sr_recordfull_on.gif'"
             onMouseOut="document['ful1'].src='images/sr_recordfull.gif'"
             name="ful1"/></a>
      </xsl:if>
      <xsl:if test="$debug='yes'">
          <a href="dumprec.jsp?hitno={$hitno}">dump</a>
      </xsl:if>
  </td>
</tr>
</xsl:template>


</xsl:stylesheet>
