<?xml version="1.0"?>

<!-- THIS CURRENTLY DOES NOT HAVE PARAGRAPH TRUNCATION IMPLEMENTED SEE Generice_GRS_To_HTML_full.xsl -->


<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:param name="hitno"/>
<xsl:param name="keyword_phrase"/>
<xsl:param name="terms"/>
<xsl:output method="html"/>

<xsl:variable name="uc" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
<xsl:variable name="lc" select="'abcdefghijklmnopqrstuvwxyz'"/>

<xsl:template match="/">
    <xsl:apply-templates select="EAD" />
</xsl:template>

<xsl:template match="EAD">
    <center>
    <table width="80%">
        <xsl:apply-templates select="./ARCHDESC/DID/REPOSITORY" />
        <xsl:apply-templates select="./EADHEADER" />
        <xsl:apply-templates select="./ARCHDESC" />
    </table>
    </center>
</xsl:template>

<xsl:template match="EADHEADER">
    <xsl:apply-templates select="./FILEDESC" />
</xsl:template>



<xsl:template match="FILEDESC">
    <xsl:apply-templates select="./TITLESTMT" />
</xsl:template>



<xsl:template match="ARCHDESC">
        <tr>
            <td width="100%">
                <span class="filetext">
                    <xsl:call-template name="highlight">
                        <xsl:with-param name="phrase" select="$keyword_phrase"/>
                        <xsl:with-param name="originaltext" select="./DID/UNITTITLE"/>
                    </xsl:call-template>
                </span>

                <xsl:apply-templates select="./DID/UNITID"/>
                <xsl:apply-templates select="./DID/UNITDATE"/>

                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <xsl:if test="./C">
                            <td background="images/nodeI.gif" valign="top">&#160;</td>
                        </xsl:if>
                        <td width="100%"><p/>
                            <ul>
                                <xsl:apply-templates select="./BIOGHIST"/>
                                <xsl:apply-templates select="./SCOPECONTENT"/>
                            </ul>
                        </td>
                    </tr>
                </table>
                <xsl:apply-templates select="DSC" />
            </td>
        </tr>
</xsl:template>



<xsl:template match="DSC">
    <xsl:apply-templates select="C" />
</xsl:template>



<xsl:template match="C">
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td valign="top">
                <img src="images/nodeL.gif"/>
            </td>
            <td width="100%" valign="top">

                <span class="filetext">
                    <xsl:call-template name="highlight">
                        <xsl:with-param name="phrase" select="$keyword_phrase"/>
                        <xsl:with-param name="originaltext" select="./DID/UNITTITLE"/>
                    </xsl:call-template>
                </span>

                <xsl:apply-templates select="./DID/UNITID"/>
                <xsl:apply-templates select="./DID/UNITDATE"/>

                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <xsl:if test="./C">
                            <td background="images/nodeI.gif" valign="top">&#160;</td>
                        </xsl:if>
                        <td width="100%"><p/>
                            <ul>
                                <xsl:apply-templates select="./BIOGHIST"/>
                                <xsl:apply-templates select="./SCOPECONTENT"/>

                            </ul>
                        </td>
                    </tr>
                </table>
                <xsl:apply-templates select="C" />
            </td>
        </tr>
    </table>
</xsl:template>


<xsl:template match="DID/UNITID">
    <xsl:if test="normalize-space(.)!=''">
        <b>ref.</b>
        <span class="refnumdata">
            <xsl:value-of select="."/>
        </span>
    </xsl:if>
</xsl:template>


<xsl:template match="DID/UNITDATE">
    <xsl:if test="normalize-space(.)!=''">
        <span class="filetext">
            <b>date</b>
            <xsl:value-of select="."/>
        </span>
    </xsl:if>
</xsl:template>


<xsl:template match="BIOGHIST">
    <xsl:variable name="original_text" select="." />
    <xsl:if test="normalize-space($original_text)!=''">
        <li><b>From Biographical History : </b>
            <xsl:call-template name="highlight">
                <xsl:with-param name="phrase" select="$keyword_phrase"/>
                <xsl:with-param name="originaltext" select="$original_text"/>
            </xsl:call-template>
            <p/>
        </li>
    </xsl:if>
</xsl:template>


<xsl:template match="SCOPECONTENT">
    <xsl:variable name="original_text" select="." />
    <xsl:if test="normalize-space($original_text)!=''">
        <li><b>From Scope/Content : </b>
            <xsl:call-template name="highlight">
                <xsl:with-param name="phrase" select="$keyword_phrase"/>
                <xsl:with-param name="originaltext" select="$original_text"/>
            </xsl:call-template>
            <p/>
        </li>
    </xsl:if>
</xsl:template>


<xsl:template match="TITLESTMT">
    <xsl:apply-templates select="TITLEPROPER"/>
</xsl:template>


<xsl:template match="REPOSITORY">
    <tr>
        <td width="100%" align="center">
            <xsl:element name="a">
                <xsl:attribute name="href">http://www.hmc.gov.uk/archon/searches/locresult.asp?LR=<xsl:value-of select="/EAD/EADHEADER/FILEDESC/TITLESTMT/AUTHOR/@altrender"/></xsl:attribute>
                <xsl:attribute name="target">
                    new
                </xsl:attribute>
                <xsl:apply-templates select="CORPNAME" />
            </xsl:element>
        </td>
    </tr>
</xsl:template>


<xsl:template match="TITLEPROPER">
    <tr>
        <td width="100%" align="center">
            <xsl:value-of select="."/><hr/>
        </td>
    </tr>
</xsl:template>


<xsl:template match="CORPNAME">
    <xsl:value-of select="."/>
</xsl:template>



<xsl:template name="truncated_paragraph">
    <xsl:param name="the_text" />
    <xsl:param name="length" />

    <xsl:if test="normalize-space($the_text)!=''">
       <xsl:variable name="truncated_1" > <xsl:value-of select="substring($the_text, 1, $length)" /></xsl:variable>
       <xsl:variable name="the_rest" > <xsl:value-of select="substring($the_text, 1+$length)" /> </xsl:variable>
       <!--<xsl:variable name="to_first_space" select="substring-before($truncated_2, ' ')"/>-->
       <xsl:variable name="truncated"><xsl:value-of select="concat($truncated_1, substring-before($the_rest, ' '))" /> ...</xsl:variable>

        <xsl:call-template name="highlight">
            <xsl:with-param name="phrase" select="$keyword_phrase"/>
            <xsl:with-param name="originaltext">
                <!--xsl:call-template name="replace_entity_refs">
                    <xsl:with-param name="the_text" select="$truncated"/>
                </xsl:call-template-->
            </xsl:with-param>
        </xsl:call-template>
        <p />
    </xsl:if>
</xsl:template>



<xsl:template name="highlight" mode="search">
    <xsl:param name="phrase"/>
    <xsl:param name="originaltext" select="."/>

    <xsl:param name="lctext" select="translate($originaltext,$lc,$uc)"/>
    <xsl:param name="lcphrase" select="translate($phrase,$lc,$uc)"/>

    <xsl:choose>
        <xsl:when test="contains($lctext, $lcphrase)">
            <xsl:value-of select="substring($originaltext,1,string-length(substring-before($lctext,$lcphrase)))"/>
            <em style='background:gold; font-weight: bold'>
                <xsl:value-of select="substring($originaltext,string-length(substring-before($lctext,$lcphrase))+1,string-length($phrase))"/>
            </em>
            <xsl:if test="$originaltext">
                <xsl:call-template name="highlight">
                    <xsl:with-param name="phrase" select="$phrase"/>
                    <xsl:with-param name="originaltext" select="substring($originaltext,string-length(substring-before($lctext,$lcphrase))+1+string-length($phrase))"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:when>
        <xsl:otherwise>
            <xsl:value-of select="$originaltext"/>
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

</xsl:stylesheet>
