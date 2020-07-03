<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:include href="character_replacement.xsl"/>
<xsl:param name="hitno"/>
<xsl:param name="keyword_phrase"/>
<xsl:output method="html"/>

<xsl:template match="/">
    <xsl:apply-templates select="grs/GRSTag[@type=3 and @value='EAD']" />
</xsl:template>



<xsl:template match="grs/GRSTag[@type=3 and @value='EAD']">
    <center>
    <table width="80%">
        <xsl:apply-templates select="./GRSTag[@type=3 and @value='ARCHDESC']/GRSTag[@type=3 and @value='DID']/GRSTag[@type=3 and @value='REPOSITORY']" />
        <xsl:apply-templates select="./GRSTag[@type=3 and @value='EADHEADER']" />
        <xsl:apply-templates select="./GRSTag[@type=3 and @value='ARCHDESC']" />
    </table>
    </center>
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='EADHEADER']">
    <xsl:apply-templates select="./GRSTag[@type=3 and @value='FILEDESC']" />
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='FILEDESC']">
    <xsl:apply-templates select="./GRSTag[@type=3 and @value='TITLESTMT']" />
    <xsl:apply-templates select="./GRSTag[@type=3 and @value='NOTESTMT']" />
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='ARCHDESC']">
        <tr>
            <td width="100%">

                <xsl:variable name="the_text" select="./GRSTag[@type=3 and @value='DID']/GRSTag[@type=3 and @value='UNITTITLE']/GRSTag[@type=2 and @value='19']"/>
                <xsl:if test="normalize-space($the_text)!=''">
                    <span class="filetext">
                        <xsl:call-template name="highlight">
                            <xsl:with-param name="phrase" select="$keyword_phrase"/>
                            <xsl:with-param name="originaltext">
                                <xsl:call-template name="replace_entity_refs">
                                    <xsl:with-param name="the_text" select="$the_text"/>
                                </xsl:call-template>
                            </xsl:with-param>
                        </xsl:call-template>
                    </span>
                </xsl:if>

                <xsl:apply-templates select="./GRSTag[@type=3 and @value='DID']/GRSTag[@type=3 and @value='UNITID']/GRSTag[@type=2 and @value='19']"/>
                <xsl:apply-templates select="./GRSTag[@type=3 and @value='DID']/GRSTag[@type=3 and @value='UNITDATE']/GRSTag[@type=2 and @value='19']"/>

                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <xsl:if test="starts-with(name(),'C')">
                            <td background="images/nodeI.gif" valign="top">&#160;</td>
                        </xsl:if>
                        <td width="100%"><p/>
                            <ul>
                                <xsl:apply-templates select="./GRSTag[@type=3 and @value='BIOGHIST' ]" mode="long"/>
                                <xsl:apply-templates select="./GRSTag[@type=3 and @value='SCOPECONTENT']" mode="long"/>
                            </ul>
                        </td>
                    </tr>
                </table>
                <xsl:apply-templates select="./GRSTag[@type=3 and @value='DSC']" />
            </td>
        </tr>
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='DSC']">
    <xsl:apply-templates select="./GRSTag[@type=3 and starts-with(@value,'C')]" />
</xsl:template>



<xsl:template match="GRSTag[@type=3 and starts-with(@value,'C')]">
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td valign="top">
                <img src="images/nodeL.gif"/>
            </td>
            <td width="100%" valign="top">
                <b>
                    <span class="filetext">
                        <xsl:apply-templates select="./GRSTag[@type=3 and @value='LEVEL']"/> &#160;
                        <xsl:apply-templates select="./GRSTag[@type=3 and @value='OTHERLEVEL']"/>
                    </span>
                </b> -

                <xsl:variable name="the_text" select="./GRSTag[@type=3 and @value='DID']/GRSTag[@type=3 and @value='UNITTITLE']/GRSTag[@type=2 and @value='19']"/>
                <xsl:if test="normalize-space($the_text)!=''">
                    <span class="filetext">
                        <xsl:call-template name="highlight">
                            <xsl:with-param name="phrase" select="$keyword_phrase"/>
                            <xsl:with-param name="originaltext">
                                <xsl:call-template name="replace_entity_refs">
                                    <xsl:with-param name="the_text" select="$the_text"/>
                                </xsl:call-template>
                            </xsl:with-param>
                        </xsl:call-template>
                    </span>
                </xsl:if>

                <xsl:apply-templates select="./GRSTag[@type=3 and @value='DID']/GRSTag[@type=3 and @value='UNITID']"/>

                <xsl:apply-templates select="./GRSTag[@type=3 and @value='DID']/GRSTag[@type=3 and @value='UNITDATE']/GRSTag[@type=2 and @value='19']"/> &#160;

                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td width="100%"><p/>
                            <ul>
                                <xsl:apply-templates select="./GRSTag[@type=3 and @value='BIOGHIST' ] " mode="short"/>
                                <xsl:apply-templates select="./GRSTag[@type=3 and @value='SCOPECONTENT' ]" mode="short"/>
                            </ul>
                        </td>
                    </tr>
                </table>
                <xsl:apply-templates select="GRSTag[@type=3 and starts-with(@value,'C') and @value!='CONTROLACCESS']" />
            </td>
        </tr>
    </table>
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='BIOGHIST' ]" mode="long">
    <xsl:variable name="the_text" select="./GRSTag[@type=3 and @value='P']" />
    <xsl:if test="normalize-space($the_text)!=''">
       <li>
            <b>From Biographical History : </b>
            <xsl:call-template name="truncated_paragraph">
                <xsl:with-param name="the_text" select="$the_text" />
                <xsl:with-param name="length">600</xsl:with-param>
            </xsl:call-template>
        </li>
    </xsl:if>
</xsl:template>




<xsl:template match="GRSTag[@type=3 and @value='BIOGHIST' ]" mode="short">
    <xsl:variable name="the_text" select="./GRSTag[@type=3 and @value='P']" />
    <xsl:if test="normalize-space($the_text)!=''">
       <li>
            <b>From Biographical History : </b>
            <xsl:call-template name="truncated_paragraph">
                <xsl:with-param name="the_text" select="$the_text" />
                <xsl:with-param name="length">70</xsl:with-param>
            </xsl:call-template>
        </li>
    </xsl:if>
</xsl:template>




<xsl:template match="GRSTag[@type=3 and @value='SCOPECONTENT']" mode="long">
    <xsl:variable name="the_text" select="./GRSTag[@type=3 and @value='P']" />
    <xsl:if test="normalize-space($the_text)!=''">
        <li>
            <b>From Scope/Content : </b>
            <xsl:call-template name="truncated_paragraph">
                <xsl:with-param name="the_text" select="$the_text" />
                <xsl:with-param name="length">600</xsl:with-param>
            </xsl:call-template>
        </li>
    </xsl:if>
</xsl:template>




<xsl:template match="GRSTag[@type=3 and @value='SCOPECONTENT']" mode="short">
    <xsl:variable name="the_text" select="./GRSTag[@type=3 and @value='P']" />
    <xsl:if test="normalize-space($the_text)!=''">
        <li>
            <b>From Scope/Content : </b>
            <xsl:call-template name="truncated_paragraph">
                <xsl:with-param name="the_text" select="$the_text" />
                <xsl:with-param name="length">70</xsl:with-param>
            </xsl:call-template>
        </li>
    </xsl:if>
</xsl:template>

<xsl:template match="GRSTag[@type=3 and @value='TITLESTMT']">
    <xsl:apply-templates select="GRSTag[@type=3 and @value='TITLEPROPER']"/>
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='NOTESTMT']">
    <xsl:apply-templates select="NOTE"/>
</xsl:template>




<xsl:template name="truncated_paragraph">
    <xsl:param name="the_text" />
    <xsl:param name="length" />

    <xsl:if test="normalize-space($the_text)!=''">
       <xsl:variable name="truncated_1" > <xsl:value-of select="substring($the_text, 1, $length)" /></xsl:variable>
       <xsl:variable name="the_rest" > <xsl:value-of select="substring($the_text, 1+$length)" /> </xsl:variable>
       <xsl:variable name="truncated">
            <xsl:choose>
            <!--
                <xsl:when test="contains(translate($the_text, $lc, $uc), translate($keyword_phrase, $lc, $uc))">
                    <xsl:value-of select="$the_text" />
                </xsl:when>-->
                <xsl:when test="normalize-space($the_text) = normalize-space($truncated_1)">
                    <xsl:value-of select="$the_text" />
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat($truncated_1, substring-before($the_rest, ' '))" /> ...
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

       <!--<xsl:value-of select="concat($truncated_1, substring-before($the_rest, ' '))" /> ...-->

        <xsl:call-template name="highlight">
            <xsl:with-param name="phrase" select="$keyword_phrase"/>
            <xsl:with-param name="originaltext">
                <xsl:call-template name="replace_entity_refs">
                    <xsl:with-param name="the_text" select="$truncated"/>
                </xsl:call-template>
            </xsl:with-param>
        </xsl:call-template>
        <p />
    </xsl:if>
</xsl:template>




<xsl:template match="GRSTag[@type=3 and @value='P']">
    <xsl:variable name="the_text" select="./text()"/>
    <xsl:if test="normalize-space($the_text)!=''">
        <p>
        <xsl:call-template name="highlight">
            <xsl:with-param name="phrase" select="$keyword_phrase"/>
            <xsl:with-param name="originaltext">
                <xsl:call-template name="replace_entity_refs">
                    <xsl:with-param name="the_text" select="$the_text"/>
                </xsl:call-template>
            </xsl:with-param>
        </xsl:call-template>
        </p>
    </xsl:if>

</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='P']" mode="no_paragraph_not_text">
    <xsl:variable name="the_text" select="."/>
    <xsl:if test="normalize-space($the_text)!=''">
        <xsl:call-template name="highlight">
            <xsl:with-param name="phrase" select="$keyword_phrase"/>
            <xsl:with-param name="originaltext">
                <xsl:call-template name="replace_entity_refs">
                    <xsl:with-param name="the_text" select="$the_text"/>
                </xsl:call-template>
            </xsl:with-param>
        </xsl:call-template>
        <p />
    </xsl:if>
</xsl:template>


<xsl:template match="GRSTag[@type=3 and @value='REPOSITORY']">
  <tr>
    <td width="100%" align="center">
      <xsl:element name="a">
          <xsl:choose>
              <xsl:when test="contains(../GRSTag[@type=3 and @value='UNITID']/GRSTag[@type=2 and @value='19'], '/')">
                  <xsl:attribute name="href">http://www.hmc.gov.uk/archon/searches/locresult.asp?LR=<xsl:value-of select="number(substring-after(substring-before(normalize-space(../GRSTag[@type=3 and @value='UNITID']/GRSTag[@type=2 and @value='19']),'/'),' '))"/></xsl:attribute>
              </xsl:when>
              <xsl:otherwise>
                  <xsl:attribute name="href">http://www.hmc.gov.uk/archon/searches/locresult.asp?LR=<xsl:value-of select="number(substring-before(substring-after(normalize-space(../GRSTag[@type=3 and @value='UNITID']/GRSTag[@type=2 and @value='19']),' '),' '))"/></xsl:attribute>
              </xsl:otherwise>
          </xsl:choose>
        <xsl:attribute name="target"> new </xsl:attribute>
        <xsl:apply-templates select="GRSTag[@type=3 and @value='CORPNAME']" />
        <xsl:apply-templates select="./text()" />
      </xsl:element>
    </td>
  </tr>
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='TITLEPROPER']">
    <xsl:variable name="title_proper" select="./GRSTag[@type=2 and @value='19']" />

        <tr>
            <td width="100%" align="center">
                <xsl:if test="normalize-space($title_proper)!=''">
                    <xsl:call-template name="replace_entity_refs">
                        <xsl:with-param name="the_text" select="$title_proper"/>
                    </xsl:call-template>
                </xsl:if>
                <hr />
            </td>
        </tr>
</xsl:template>


<xsl:template match="GRSTag[@type=3 and @value='NOTE']">
    <tr>
        <td width="100%" align="center">
            <xsl:value-of select="./P"/><hr/>
        </td>
    </tr>
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='CORPNAME']">
    <xsl:value-of select="."/>
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='DID']/GRSTag[@type=3 and @value='UNITID']/GRSTag[@type=2 and @value='19']">
    <xsl:if test="normalize-space(.)!=''">
      &#160;  <b>ref.</b> &#160;
        <span class="refnumdata">
            <xsl:value-of select="."/>
        </span>
    </xsl:if>
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='DID']/GRSTag[@type=3 and @value='UNITID']">
    <xsl:if test="normalize-space(.)!=''">
      &#160;  <b>ref.</b> &#160;
        <span class="refnumdata">
            <xsl:value-of select="."/>
        </span>
    </xsl:if>
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='DID']/GRSTag[@type=3 and @value='UNITDATE']/GRSTag[@type=2 and @value='19']">
    <xsl:if test="normalize-space(.)!=''">
        <span class="filetext">
            &#160; <b>date</b> &#160;
            <xsl:value-of select="."/>
        </span>
    </xsl:if>
</xsl:template>




<xsl:template match="GRSTag[@type=3 and @value='LEVEL']">
    <xsl:variable name="level" select="."/>
    <xsl:variable name="level_lc" select="translate($level, $uc, $lc)"/>

    <xsl:if test="normalize-space($level_lc)!='otherlevel'">
        <xsl:value-of select="$level" />
    </xsl:if>
</xsl:template>


</xsl:stylesheet>
