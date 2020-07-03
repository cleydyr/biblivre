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
        <xsl:variable name="children" select="child::node()"/>
        <xsl:call-template name="generic_heirarchy_walk">
            <xsl:with-param name="children" select="$children"/>
        </xsl:call-template>
      </xsl:element>
    </td>
  </tr>
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='EADHEADER']">
    <xsl:apply-templates select="./GRSTag[@type=3 and @value='FILEDESC']" />
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='FILEDESC']">
    <xsl:apply-templates select="./GRSTag[@type=3 and @value='TITLESTMT']" />
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='TITLESTMT']">
    <xsl:apply-templates select="GRSTag[@type=3 and @value='TITLEPROPER']"/>
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='TITLEPROPER']">
    <tr>
        <td width="100%" align="center">
            <xsl:call-template name="generic_heirarchy_walk">
                <xsl:with-param name="children" select="./node()" />
            </xsl:call-template>
            <hr />
        </td>
    </tr>
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='ARCHDESC']">
        <tr>
            <td width="100%">
                <span class="filetext">
                    <xsl:apply-templates select="./GRSTag[@type=3 and @value='DID']/GRSTag[@type=3 and @value='UNITTITLE']"/>
                </span>
                <span class="refnumdata">
                    <xsl:apply-templates select="./GRSTag[@type=3 and @value='DID']/GRSTag[@type=3 and @value='UNITID']"/>
                </span>
                <span class="filetext">
                    <xsl:apply-templates select="./GRSTag[@type=3 and @value='DID']/GRSTag[@type=3 and @value='UNITDATE']"/>
                </span>
                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <xsl:if test="starts-with(name(),'C')">
                            <td background="images/nodeI.gif" valign="top">&#160;</td>
                        </xsl:if>
                        <td width="100%"><p/>
                            <ul>
                                <xsl:apply-templates select="./GRSTag[@type=3 and @value='BIOGHIST']"/>
                                <xsl:apply-templates select="./GRSTag[@type=3 and @value='SCOPECONTENT']"/>
                                <xsl:apply-templates select="./GRSTag[@type=3 and @value='ARRANGEMENT']"/>
                                <xsl:apply-templates select="./GRSTag[@type=3 and @value='ADMININFO']"/>
                                <xsl:apply-templates select="./GRSTag[@type=3 and @value='ADD']"/>
                                <xsl:apply-templates select="./GRSTag[@type=3 and @value='ODD']"/>
                                <xsl:apply-templates select="./GRSTag[@type=3 and @value='DID']/GRSTag[@type=3 and @value='PHYSDESC']"/>
                                <xsl:apply-templates select="./GRSTag[@type=3 and @value='NOTE']"/>
                                <xsl:apply-templates select="./GRSTag[@type=3 and @value='CONTROLACCESS']"/>
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
                    </span>
                    <span class="filetext">
                        <xsl:apply-templates select="./GRSTag[@type=3 and @value='OTHERLEVEL']"/>
                    </span>
                </b> -
                <span class="filetext">
                    <xsl:apply-templates select="./GRSTag[@type=3 and @value='DID']/GRSTag[@type=3 and @value='UNITTITLE']"/>
                </span>
                <span class="refnumdata">
                    <xsl:apply-templates select="./GRSTag[@type=3 and @value='DID']/GRSTag[@type=3 and @value='UNITID']"/>
                </span>
                <span class="filetext">
                    <xsl:apply-templates select="./GRSTag[@type=3 and @value='DID']/GRSTag[@type=3 and @value='UNITDATE']"/> &#160;
                </span>

                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <xsl:if test="starts-with(name(),'C')">
                            <td background="images/nodeI.gif" valign="top">&#160;</td>
                        </xsl:if>
                        <td width="100%"><p/>
                            <ul>
                                <xsl:apply-templates select="./GRSTag[@type=3 and @value='BIOGHIST']"/>
                                <xsl:apply-templates select="./GRSTag[@type=3 and @value='SCOPECONTENT']"/>
                                <xsl:apply-templates select="./GRSTag[@type=3 and @value='ARRANGEMENT']"/>
                                <xsl:apply-templates select="./GRSTag[@type=3 and @value='ADMININFO']"/>
                                <xsl:apply-templates select="./GRSTag[@type=3 and @value='ADD']"/>
                                <xsl:apply-templates select="./GRSTag[@type=3 and @value='ODD']"/>
                                <xsl:apply-templates select="./GRSTag[@type=3 and @value='DID']/GRSTag[@type=3 and @value='PHYSDESC']"/>
                                <xsl:apply-templates select="./GRSTag[@type=3 and @value='NOTE']"/>
                                <xsl:apply-templates select="./GRSTag[@type=3 and @value='CONTROLACCESS']"/>
                            </ul>
                        </td>
                    </tr>
                </table>
                <xsl:apply-templates select="GRSTag[@type=3 and starts-with(@value,'C') and @value!='CONTROLACCESS']" />
            </td>
        </tr>
    </table>
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='UNITTITLE']">
    <xsl:call-template name="generic_heirarchy_walk">
        <xsl:with-param name="children" select="./node()" />
    </xsl:call-template>
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='UNITID']">
    <xsl:variable name="text_counter" select="count(descendant::text()[normalize-space(.)!=''])" />
    <xsl:if test="$text_counter!='0'">
        <xsl:if test="count(preceding-sibling::GRSTag[@type=3 and @value='UNITID'])=0">
            &#160;  <b>ref.</b> &#160;
        </xsl:if>

        <xsl:call-template name="generic_heirarchy_walk">
            <xsl:with-param name="children" select="./node()" />
        </xsl:call-template>
    </xsl:if>
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='UNITDATE']">
    <xsl:variable name="text_counter" select="count(descendant::text()[normalize-space(.)!=''])" />
    <xsl:if test="$text_counter!='0'">
        &#160;  <b>date</b> &#160;

        <xsl:call-template name="generic_heirarchy_walk">
            <xsl:with-param name="children" select="./node()" />
        </xsl:call-template>
    </xsl:if>
</xsl:template>



<!-- if there are other nodes of this type only display the heading once -->
<!-- if there is a child (3,HEAD) tag use this as the heading -->
<!-- these comments apply to all the displayable 'sections' -->
<xsl:template match="GRSTag[@type=3 and @value='BIOGHIST']">
    <xsl:variable name="section_content">
        <xsl:call-template name="bioghist_walk">
            <xsl:with-param name="nodes_to_walk" select="child::node()" />
        </xsl:call-template>
    </xsl:variable>

    <xsl:if test="normalize-space($section_content)!=''">
    <li>
        <xsl:variable name="heading" select="./GRSTag[@type=3 and @value='HEAD']/text()" />
            <xsl:if test="normalize-space($heading)=''">
                <xsl:if test="count(preceding-sibling::GRSTag[@type=3 and @value='BIOGHIST'])=0">
                    <b> Biographical History : </b>
                </xsl:if>
        </xsl:if>
        <xsl:copy-of select="$section_content" />
    </li>
    <p />
    </xsl:if>
</xsl:template>



<!-- this tyep of template deals with the non standard elements which can be children of each node type-->
<!-- it then calls the generic heirarchy walk where appropraite-->
<!-- there are templates of this type for each section -->
<xsl:template name="bioghist_walk">
    <xsl:param name="nodes_to_walk"/>
        <xsl:for-each select="$nodes_to_walk">
            <xsl:choose>
                <xsl:when test="self::GRSTag[@type=3 and @value='BIOGHIST']" >
                    <xsl:call-template name="bioghist_walk">
                        <xsl:with-param name="nodes_to_walk" select="child::node()" />
                    </xsl:call-template>
                    <p />
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="generic_heirarchy_walk">
                        <xsl:with-param name="children" select="." />
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='SCOPECONTENT']">
    <xsl:variable name="section_content">
        <xsl:call-template name="scopecontent_walk">
            <xsl:with-param name="nodes_to_walk" select="child::node()" />
        </xsl:call-template>
    </xsl:variable>

    <xsl:if test="normalize-space($section_content)!=''">
    <li>
        <xsl:variable name="heading" select="./GRSTag[@type=3 and @value='HEAD']/text()" />
            <xsl:if test="normalize-space($heading)=''">
                <xsl:if test="count(preceding-sibling::GRSTag[@type=3 and @value='SCOPECONTENT'])=0">
                    <b> Scope and Content : </b>
                </xsl:if>
        </xsl:if>
        <xsl:copy-of select="$section_content" />
    </li>
    <p />
    </xsl:if>
</xsl:template>



<xsl:template name="scopecontent_walk">
    <xsl:param name="nodes_to_walk"/>
        <xsl:for-each select="$nodes_to_walk">
            <xsl:choose>
                <xsl:when test="self::GRSTag[@type=3 and @value='SCOPECONTENT']" >
                    <xsl:call-template name="scopecontent_walk">
                        <xsl:with-param name="nodes_to_walk" select="child::node()" />
                    </xsl:call-template>
                    <p />
                </xsl:when>
                <xsl:when test="self::GRSTag[@type=3 and @value='ARRANGEMENT']" >
                    <xsl:call-template name="generic_heirarchy_walk">
                        <xsl:with-param name="children" select="child::node()" />
                    </xsl:call-template>
                    <p />
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="generic_heirarchy_walk">
                        <xsl:with-param name="children" select="." />
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='ARRANGEMENT']">
    <xsl:variable name="section_content">
        <xsl:call-template name="arrangement_walk">
            <xsl:with-param name="nodes_to_walk" select="child::node()" />
        </xsl:call-template>
    </xsl:variable>

    <xsl:if test="normalize-space($section_content)!=''">
    <li>
        <xsl:variable name="heading" select="./GRSTag[@type=3 and @value='HEAD']/text()" />
            <xsl:if test="normalize-space($heading)=''">
                <xsl:if test="count(preceding-sibling::GRSTag[@type=3 and @value='ARRANGEMENT'])=0">
                    <b> Arrangement : </b>
                </xsl:if>
        </xsl:if>
        <xsl:copy-of select="$section_content" />
    </li>
    <p />
    </xsl:if>
</xsl:template>



<xsl:template name="arrangement_walk">
    <xsl:param name="nodes_to_walk"/>
        <xsl:for-each select="$nodes_to_walk">
            <xsl:choose>
                <xsl:when test="self::GRSTag[@type=3 and @value='ARRANGEMENT']" >
                    <xsl:call-template name="arrangement_walk">
                        <xsl:with-param name="nodes_to_walk" select="child::node()" />
                    </xsl:call-template>
                    <p />
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="generic_heirarchy_walk">
                        <xsl:with-param name="children" select="." />
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
</xsl:template>



<!-- This template deals with the possible repetition of the top level tag with different
     descendants. We only ever require one overall heading even if the descendants are different in
     subsequent siblings. -->
<xsl:template match="GRSTag[@type=3 and @value='ADMININFO']">
    <xsl:if test="count(preceding-sibling::GRSTag[@type=3 and @value='ADMININFO'])=0">
        <xsl:variable name="this_node" select="." />
        <xsl:variable name="following_nodes" select="following-sibling::GRSTag[@type=3 and @value='ADMININFO']" />

        <xsl:variable name="section_content_this">
            <xsl:call-template name="admininfo_walk">
                <xsl:with-param name="nodes_to_walk" select="$this_node/node()" />
            </xsl:call-template>
        </xsl:variable>

        <xsl:variable name="section_content_following">
            <xsl:call-template name="admininfo_walk">
                <xsl:with-param name="nodes_to_walk" select="$following_nodes/node()" />
            </xsl:call-template>
        </xsl:variable>
        <!-- need a choose probably -->
        <xsl:if test="normalize-space($section_content_this)!=''">
            <li>
                <xsl:variable name="heading" select="./GRSTag[@type=3 and @value='HEAD']/text()" />
                    <xsl:if test="normalize-space($heading)=''">
                        <b> Administrative Information : </b>
                    </xsl:if>
                <xsl:copy-of select="$section_content_this" />
                <xsl:copy-of select="$section_content_following" />
            </li>
        </xsl:if>
        <p />
    </xsl:if>
</xsl:template>



<xsl:template name="admininfo_walk"><!-- have discovered that CUSTODHIST can contain nested acq info -->
    <xsl:param name="nodes_to_walk"/><!-- need to fix this as the template doesn't cater for it -->
        <xsl:for-each select="$nodes_to_walk">
            <xsl:choose>
                <xsl:when test="self::GRSTag[@type=3 and @value='ADMININFO']" >
                    <xsl:variable name="nested_admin">
                        <xsl:call-template name="admininfo_walk">
                            <xsl:with-param name="nodes_to_walk" select="child::node()" />
                        </xsl:call-template>
                    </xsl:variable>

                    <xsl:if test="normalize-space($nested_admin)!=''">
                        <ul>
                            <li>
                                <xsl:copy-of select="$nested_admin" />
                            </li>
                        </ul>
                        <p />
                    </xsl:if>
                </xsl:when>
                <xsl:when test="self::GRSTag[@type=3 and @value='ACQINFO']" >
                    <xsl:call-template name="new_list">
                        <xsl:with-param name="node" select="."/>
                        <xsl:with-param name="heading" >Acquisition Information</xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="self::GRSTag[@type=3 and @value='APPRAISAL']" >
                    <xsl:call-template name="new_list">
                        <xsl:with-param name="node" select="."/>
                        <xsl:with-param name="heading" >Appraisal</xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="self::GRSTag[@type=3 and @value='ACCRUALS']" >
                    <xsl:call-template name="new_list">
                        <xsl:with-param name="node" select="."/>
                        <xsl:with-param name="heading" >Accruals</xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="self::GRSTag[@type=3 and @value='CUSTODHIST']" >
                    <xsl:call-template name="new_admininfo_list_nested">
                        <xsl:with-param name="node" select="."/>
                        <xsl:with-param name="heading" >Custodial History</xsl:with-param>
                        <xsl:with-param name="nested_node_type" >ACQINFO</xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="self::GRSTag[@type=3 and @value='USERESTRICT']" >
                    <xsl:call-template name="new_list">
                        <xsl:with-param name="node" select="."/>
                        <xsl:with-param name="heading" >Restrictions on Use</xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="self::GRSTag[@type=3 and @value='ACCESSRESTRICT']" >
                    <xsl:call-template name="new_list">
                        <xsl:with-param name="node" select="."/>
                        <xsl:with-param name="heading" >Restrictions on Access</xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="self::GRSTag[@type=3 and @value='ALTFORMAVAIL']" >
                    <xsl:call-template name="new_list">
                        <xsl:with-param name="node" select="."/>
                        <xsl:with-param name="heading" >Alternative Form Available</xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="self::GRSTag[@type=3 and @value='PROCESSINFO']" >
                    <xsl:call-template name="new_list">
                        <xsl:with-param name="node" select="."/>
                        <xsl:with-param name="heading" >Processing Information</xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="self::GRSTag[@type=3 and @value='PREFERCITE']" >
                    <xsl:call-template name="new_list">
                        <xsl:with-param name="node" select="."/>
                        <xsl:with-param name="heading" >Preferred Citation</xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="generic_heirarchy_walk">
                        <xsl:with-param name="children" select="." />
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
</xsl:template>



<xsl:template name="new_list">
    <xsl:param name="node" />
    <xsl:param name="heading" />
    <ul>
    <xsl:variable name="section_content">
        <xsl:call-template name="generic_heirarchy_walk">
            <xsl:with-param name="children" select="$node/node()" />
        </xsl:call-template>
    </xsl:variable>
    <xsl:if test="normalize-space($section_content)!=''">
    <p />
    <li>
        <xsl:variable name="grs_heading" select="./GRSTag[@type=3 and @value='HEAD']/text()" />
            <xsl:if test="normalize-space($grs_heading)=''">
                <b> <xsl:value-of select="$heading" /> : </b>
            </xsl:if>
        <xsl:copy-of select="$section_content" />
    </li>
    </xsl:if>
    </ul>
</xsl:template>


<xsl:template name="new_admininfo_list_nested">
    <xsl:param name="node" />
    <xsl:param name="heading" />
    <xsl:param name="nested_node_type" />
    <ul>
    <xsl:variable name="section_content">
        <xsl:call-template name="nested_admininfo_walk">
            <xsl:with-param name="children" select="$node/node()" />
            <xsl:with-param name="nested_node_type" select="$nested_node_type" />
        </xsl:call-template>
    </xsl:variable>

    <xsl:if test="normalize-space($section_content)!=''">
    <p />
    <li>
        <xsl:variable name="grs_heading" select="./GRSTag[@type=3 and @value='HEAD']/text()" />
            <xsl:if test="normalize-space($grs_heading)=''">
                <b> <xsl:value-of select="$heading" /> : </b>
            </xsl:if>
        <xsl:copy-of select="$section_content" />
    </li>
    </xsl:if>
    </ul>
</xsl:template>



<xsl:template name="nested_admininfo_walk">
    <xsl:param name="children"/>
    <xsl:param name="nested_node_type" />
    <xsl:for-each select="$children">
        <xsl:choose>
            <xsl:when test="self::GRSTag[@type=3 and @value=$nested_node_type]" >
                <xsl:call-template name="admininfo_walk">
                    <xsl:with-param name="nodes_to_walk" select="."/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="." />
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:for-each>
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='PHYSDESC']">

    <xsl:variable name="section_content">
        <xsl:call-template name="physdesc_walk">
            <xsl:with-param name="nodes_to_walk" select="child::node()" />
        </xsl:call-template>
    </xsl:variable>

    <xsl:if test="normalize-space($section_content)!=''">
    <li>
        <xsl:variable name="heading" select="./GRSTag[@type=3 and @value='HEAD']/text()" />
        <xsl:variable name="label" select="./GRSTag[@type=3 and @value='LABEL']/text()" />

        <xsl:if test="normalize-space($heading)=''">
            <xsl:choose>
                <xsl:when test="normalize-space($label)=''">
                    <xsl:if test="count(preceding-sibling::GRSTag[@type=3 and @value='PHYSDESC'])=0">
                        <b> Physical Description : </b>
                    </xsl:if>
                </xsl:when>
                <xsl:otherwise>
                    <b> <xsl:value-of select="$label" /> : </b>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>

        <xsl:copy-of select="$section_content" />
    </li>
    <p />
    </xsl:if>
</xsl:template>



<xsl:template name="physdesc_walk">
    <xsl:param name="nodes_to_walk"/>
        <xsl:for-each select="$nodes_to_walk">
            <xsl:choose>
                <xsl:when test="self::GRSTag[@type=3 and @value='PHYSFACET']" >
                    <xsl:call-template name="generic_heirarchy_walk">
                        <xsl:with-param name="children" select="child::node()" />
                    </xsl:call-template>
                    <xsl:if test="count(following-sibling::GRSTag[@type=3 and @value='GENREFORM'])=0">
                        <p />
                    </xsl:if>
                </xsl:when>
                <xsl:when test="self::GRSTag[@type=3 and @value='EXTENT']" >
                    <xsl:variable name="units">
                        <xsl:value-of select="./GRSTag[@type=3 and @value='UNIT']"/>
                    </xsl:variable>
                    <xsl:call-template name="generic_heirarchy_walk">
                        <xsl:with-param name="children" select="child::node()" />
                    </xsl:call-template>
                    <xsl:if test="normalize-space($units)!=''">
                        &#160;
                        <xsl:value-of select="$units" />
                    </xsl:if>
                    <xsl:if test="count(following-sibling::GRSTag[@type=3 and @value='GENREFORM'])=0">
                        <p />
                    </xsl:if>
                </xsl:when>
                 <xsl:when test="self::GRSTag[@type=3 and @value='DIMENSIONS']" >
                    <xsl:call-template name="physdesc_walk">
                        <xsl:with-param name="nodes_to_walk" select="child::node()" />
                    </xsl:call-template>
                    <xsl:if test="count(following-sibling::GRSTag[@type=3 and @value='GENREFORM'])=0">
                        <p />
                    </xsl:if>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:variable name="the_rest">
                        <xsl:call-template name="generic_heirarchy_walk">
                            <xsl:with-param name="children" select="." />
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:if test="normalize-space($the_rest)!=''">
                        <xsl:copy-of select="$the_rest" />
                        <p />
                    </xsl:if>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='ODD']">
    <xsl:variable name="section_content">
        <xsl:call-template name="odd_walk">
            <xsl:with-param name="nodes_to_walk" select="child::node()" />
        </xsl:call-template>
    </xsl:variable>

    <xsl:if test="normalize-space($section_content)!=''">
    <li>
        <xsl:variable name="heading" select="./GRSTag[@type=3 and @value='HEAD']/text()" />
            <xsl:if test="normalize-space($heading)=''">
                <xsl:if test="count(preceding-sibling::GRSTag[@type=3 and @value='ODD'])=0">
                    <b> Other Descriptive Data : </b>
                </xsl:if>
            </xsl:if>
        <xsl:copy-of select="$section_content" />
    </li>
    <p />
    </xsl:if>
</xsl:template>



<xsl:template name="odd_walk">
    <xsl:param name="nodes_to_walk"/>
        <xsl:for-each select="$nodes_to_walk">
            <xsl:choose>
                <xsl:when test="self::GRSTag[@type=3 and @value='ODD']" >
                    <xsl:variable name="nested_odd">
                        <xsl:call-template name="odd_walk">
                            <xsl:with-param name="nodes_to_walk" select="child::node()" />
                        </xsl:call-template>
                    </xsl:variable>

                    <xsl:if test="normalize-space($nested_odd)!=''">
                        <p />
                        <ul>
                            <li>
                                <xsl:variable name="heading" select="./GRSTag[@type=3 and @value='HEAD']/text()" />
                                <xsl:if test="normalize-space($heading)=''">
                                    <xsl:if test="count(preceding-sibling::GRSTag[@type=3 and @value='ODD'])=0">
                                        <b> Other Descriptive Data : </b>
                                    </xsl:if>
                                </xsl:if>
                                <xsl:copy-of select="$nested_odd" />
                            </li>
                        </ul>
                        <p />
                    </xsl:if>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="generic_heirarchy_walk">
                        <xsl:with-param name="children" select="." />
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='CONTROLACCESS']">
    <xsl:variable name="section_content">
        <xsl:call-template name="controlaccess_walk">
            <xsl:with-param name="nodes_to_walk" select="child::node()" />
        </xsl:call-template>
    </xsl:variable>

    <xsl:if test="normalize-space($section_content)!=''">
    <li>
        <xsl:variable name="heading" select="./GRSTag[@type=3 and @value='HEAD']/text()" />
            <xsl:if test="normalize-space($heading)=''">
                <xsl:if test="count(preceding-sibling::GRSTag[@type=3 and @value='CONTROLACCESS'])=0">
                    <b> Controlled Access Headings : </b>
                </xsl:if>
        </xsl:if>
        <p />
        <xsl:copy-of select="$section_content" />
    </li>
    <p />
    </xsl:if>
</xsl:template>




<xsl:template name="controlaccess_walk">
    <xsl:param name="nodes_to_walk"/>
        <xsl:for-each select="$nodes_to_walk">
            <xsl:choose>
                <xsl:when test="self::GRSTag[@type=3 and @value='CONTROLACCESS']" >
                    <xsl:variable name="nested_controlaccess">
                        <xsl:call-template name="controlaccess_walk">
                            <xsl:with-param name="nodes_to_walk" select="child::node()" />
                        </xsl:call-template>
                    </xsl:variable>

                    <xsl:if test="normalize-space($nested_controlaccess)!=''">
                        <ul>
                            <li>
                                <xsl:copy-of select="$nested_controlaccess" />
                            </li>
                        </ul>
                        <p />
                    </xsl:if>
                </xsl:when>
                <xsl:when test="self::GRSTag[@type=3 and @value='FAMNAME']" >
                    <xsl:call-template name="controlaccess_special">
                        <xsl:with-param name="nodes" select="child::node()" />
                    </xsl:call-template>
                    <p />
                </xsl:when>
                <xsl:when test="self::GRSTag[@type=3 and @value='PERSNAME']" >
                    <xsl:call-template name="controlaccess_special">
                        <xsl:with-param name="nodes" select="child::node()" />
                    </xsl:call-template>
                    <p />
                </xsl:when>
                <xsl:when test="self::GRSTag[@type=3 and @value='GEOGNAME']" >
                    <xsl:call-template name="controlaccess_special">
                        <xsl:with-param name="nodes" select="child::node()" />
                    </xsl:call-template>
                    <p />
                </xsl:when>
                <xsl:when test="self::GRSTag[@type=3 and @value='SUBJECT']" >
                    <xsl:call-template name="controlaccess_special">
                        <xsl:with-param name="nodes" select="child::node()" />
                    </xsl:call-template>
                    <p />
                </xsl:when>
                <xsl:when test="self::GRSTag[@type=3 and @value='CORPNAME']" >
                    <xsl:call-template name="controlaccess_special">
                        <xsl:with-param name="nodes" select="child::node()" />
                    </xsl:call-template>
                    <p />
                </xsl:when>
                <xsl:when test="self::GRSTag[@type=3 and @value='OCCUPATION']" >
                    <xsl:call-template name="controlaccess_special">
                        <xsl:with-param name="nodes" select="child::node()" />
                    </xsl:call-template>
                    <p />
                </xsl:when>
                <xsl:when test="self::GRSTag[@type=3 and @value='GENREFORM']" >
                    <xsl:call-template name="controlaccess_special">
                        <xsl:with-param name="nodes" select="child::node()" />
                    </xsl:call-template>
                    <p />
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="generic_heirarchy_walk">
                        <xsl:with-param name="children" select="." />
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
</xsl:template>


<!-- need to deal with the fact that this rubbish format requires us to separate the
lower level text ONLY IN THIS CASE FOR A LIST OF CONTROL ACCESS rubbish!!!!!! -->
<xsl:template name="controlaccess_special">
    <xsl:param name="nodes"/>
    <xsl:for-each select="$nodes" >
        <xsl:variable name="content">
            <xsl:call-template name="generic_heirarchy_walk">
                <xsl:with-param name="children" select="." />
            </xsl:call-template>
        </xsl:variable>

        <xsl:if test="normalize-space($content)!=''">
            <xsl:copy-of select="$content" />
             &#160;&#160;
        </xsl:if>
    </xsl:for-each>
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='NOTE']">
    <xsl:variable name="section_content">
        <xsl:call-template name="generic_heirarchy_walk">
            <xsl:with-param name="children" select="child::node()" />
        </xsl:call-template>
    </xsl:variable>

    <xsl:if test="normalize-space($section_content)!=''">
    <li>
        <xsl:variable name="heading" select="./GRSTag[@type=3 and @value='HEAD']/text()" />
        <xsl:variable name="label" select="./GRSTag[@type=3 and @value='LABEL']/text()" />

        <xsl:if test="normalize-space($heading)=''">
            <xsl:choose>
                <xsl:when test="normalize-space($label)=''">
                    <xsl:if test="count(preceding-sibling::GRSTag[@type=3 and @value='PHYSDESC'])=0">
                        <b> Note : </b>
                    </xsl:if>
                </xsl:when>
                <xsl:otherwise>
                    <b> <xsl:value-of select="$label" /> : </b>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>

        <xsl:copy-of select="$section_content" />
    </li>
    <p />
    </xsl:if>
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='ADD']">
    <xsl:if test="count(preceding-sibling::GRSTag[@type=3 and @value='ADD'])=0">
        <xsl:variable name="this_node" select="." />
        <xsl:variable name="following_nodes" select="following-sibling::GRSTag[@type=3 and @value='ADD']" />

        <xsl:variable name="section_content_this">
            <xsl:call-template name="add_walk">
                <xsl:with-param name="nodes_to_walk" select="$this_node/node()" />
            </xsl:call-template>
        </xsl:variable>

        <xsl:variable name="section_content_following">
            <xsl:call-template name="add_walk">
                <xsl:with-param name="nodes_to_walk" select="$following_nodes/node()" />
            </xsl:call-template>
        </xsl:variable>
        <!-- need a choose probably -->
        <xsl:if test="normalize-space($section_content_this)!=''">
            <li>
                <xsl:variable name="heading" select="./GRSTag[@type=3 and @value='HEAD']/text()" />
                    <xsl:if test="normalize-space($heading)=''">
                        <b> Adjunct Descriptive Data : </b>
                    </xsl:if>
                <xsl:copy-of select="$section_content_this" />
                <xsl:copy-of select="$section_content_following" />
            </li>
        </xsl:if>
        <p />
    </xsl:if>
</xsl:template>



<xsl:template name="add_walk">
    <xsl:param name="nodes_to_walk"/>
        <xsl:for-each select="$nodes_to_walk">
            <xsl:choose>
                <xsl:when test="self::GRSTag[@type=3 and @value='ADD']" >
                    <xsl:variable name="nested_add">
                        <xsl:call-template name="add_walk">
                            <xsl:with-param name="nodes_to_walk" select="child::node()" />
                        </xsl:call-template>
                    </xsl:variable>

                    <xsl:if test="normalize-space($nested_add)!=''">
                        <ul>
                            <li>
                                <xsl:copy-of select="$nested_add" />
                            </li>
                        </ul>
                        <p />
                    </xsl:if>
                </xsl:when>
                <xsl:when test="self::GRSTag[@type=3 and @value='OTHERFINDAID']" >
                    <xsl:call-template name="new_add_list_nested">
                        <xsl:with-param name="node" select="."/>
                        <xsl:with-param name="heading" >Other Finding Aid</xsl:with-param>
                        <xsl:with-param name="nested_node_type" >OTHERFINDAID</xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="self::GRSTag[@type=3 and @value='RELATEDMATERIAL']" >
                    <xsl:call-template name="new_add_list_nested">
                        <xsl:with-param name="node" select="."/>
                        <xsl:with-param name="heading" >Related Material</xsl:with-param>
                        <xsl:with-param name="nested_node_type" >RELATEDMATERIAL</xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="self::GRSTag[@type=3 and @value='SEPARATEDMATERIAL']" >
                    <xsl:call-template name="new_add_list_nested">
                        <xsl:with-param name="node" select="."/>
                        <xsl:with-param name="heading" >Separated Material</xsl:with-param>
                        <xsl:with-param name="nested_node_type" >SEPARATEDMATERIAL</xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="self::GRSTag[@type=3 and @value='BIBLIOGRAPHY']" >
                    <xsl:call-template name="new_add_list_nested">
                        <xsl:with-param name="node" select="."/>
                        <xsl:with-param name="heading" >Bibliography</xsl:with-param>
                        <xsl:with-param name="nested_node_type" >BIBLIOGRAPHY</xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="self::GRSTag[@type=3 and @value='FILEPLAN']" >
                    <xsl:call-template name="new_add_list_nested">
                        <xsl:with-param name="node" select="."/>
                        <xsl:with-param name="heading" >File Plan</xsl:with-param>
                        <xsl:with-param name="nested_node_type" >FILEPLAN</xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="generic_heirarchy_walk">
                        <xsl:with-param name="children" select="." />
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
</xsl:template>


<xsl:template name="new_add_list_nested">
    <xsl:param name="node" />
    <xsl:param name="heading" />
    <xsl:param name="nested_node_type" />
    <ul>
    <xsl:variable name="section_content">
        <xsl:call-template name="nested_add_walk">
            <xsl:with-param name="children" select="$node/node()" />
            <xsl:with-param name="nested_node_type" select="$nested_node_type" />
        </xsl:call-template>
    </xsl:variable>

    <xsl:if test="normalize-space($section_content)!=''">
    <p />
    <li>
        <xsl:variable name="grs_heading" select="./GRSTag[@type=3 and @value='HEAD']/text()" />
            <xsl:if test="normalize-space($grs_heading)=''">
                <b> <xsl:value-of select="$heading" /> : </b>
            </xsl:if>
        <xsl:copy-of select="$section_content" />
    </li>
    </xsl:if>
    </ul>
</xsl:template>



<xsl:template name="nested_add_walk">
    <xsl:param name="children"/>
    <xsl:param name="nested_node_type" />
    <xsl:for-each select="$children">
        <xsl:choose>
            <xsl:when test="self::GRSTag[@type=3 and @value=$nested_node_type]" >
                <xsl:call-template name="add_walk">
                    <xsl:with-param name="nodes_to_walk" select="."/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="." />
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:for-each>
</xsl:template>



<!-- the first thing we need to do is see if this nodelist contains any rendering info -->
<!-- if it does split the node list into 2 halves - rendered and not rendered-->
<!-- apply the rendering to the nodes that are following siblings -->
<!-- if not continue unchanged -->
<xsl:template name="generic_heirarchy_walk">
    <xsl:param name="children" />

        <xsl:for-each select="$children">
            <xsl:choose>
                <xsl:when test="self::GRSTag[@type=3 and @value='RENDER']">
                    <xsl:variable name="text_to_render">
                        <xsl:call-template name="generic_term_match">
                            <xsl:with-param name="node_list" select="following-sibling::node()"/>
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:variable name="render_instruction" select="translate(normalize-space(./text()), $lc, $uc)"/>
                    <xsl:choose>
                        <xsl:when test="$render_instruction='ITALIC' or $render_instruction='BOLD'">
                            <i>
                                <xsl:value-of select="$text_to_render" />
                            </i>
                        </xsl:when>
                        <xsl:when test="$render_instruction='QUOTED'">
                            "
                               <xsl:value-of select="$text_to_render" />
                            "
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$text_to_render" />
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:when test="count(preceding-sibling::GRSTag[@type=3 and @value='RENDER'])=0">
                    <xsl:call-template name="generic_term_match">
                        <xsl:with-param name="node_list" select="."/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise />
            </xsl:choose>
        </xsl:for-each>
</xsl:template>



<!-- deal generically with all freely occurring nodes we are interested in - in the order that they occur -->
<!-- specific selections  e.g. ADD/OTHERFINDAIDS are dealt with individually at a higher level  -->
<!-- all selections eventually use this template for display -->
<!-- rendering is dealt with before this is reached RENDERING NODES ARE IGNORED IN THIS TEMPLATE-->
 <xsl:template name="generic_term_match">
    <xsl:param name="node_list" />
    <xsl:for-each select="$node_list">

        <xsl:choose>
            <!-- the lowest level display the text -->
            <xsl:when test="self::text()">

                <xsl:if test="normalize-space(.)!=''">
                    <xsl:call-template name="highlight_and_replace">
                        <xsl:with-param name="the_text" select="." />
                        <xsl:with-param name="keyword_phrase" select="$keyword_phrase" />
                    </xsl:call-template>
                </xsl:if>
            </xsl:when>

            <!-- (3,P) new paragraph when finished-->
            <xsl:when test="self::GRSTag[@type=3 and @value='P']">

                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
                <p />
            </xsl:when>

            <xsl:when test="self::GRSTag[@type=2 and @value=19]">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>

            <xsl:when test="self::GRSTag[@type=3 and @value='EMPH']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>

            <xsl:when test="self::GRSTag[@type=3 and @value='LIST']">
                <ul>
                    <xsl:call-template name="generic_heirarchy_walk">
                        <xsl:with-param name="children" select="child::node()"/>
                    </xsl:call-template>
                </ul>
            </xsl:when>

            <xsl:when test="self::GRSTag[@type=3 and @value='ITEM']">
                <li>
                    <xsl:call-template name="generic_heirarchy_walk">
                        <xsl:with-param name="children" select="child::node()"/>
                    </xsl:call-template>
                </li>
            </xsl:when>

            <xsl:when test="self::GRSTag[@type=3 and @value='DATE']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>

            <xsl:when test="self::GRSTag[@type=3 and @value='HEAD']">
                <xsl:if test="normalize-space(./text())!=''">
                    <b>
                        <xsl:value-of select="./text()" /> :
                    </b>
                </xsl:if>
            </xsl:when>

            <xsl:when test="self::GRSTag[@type=3 and @value='TITLE']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>

            <xsl:when test="self::GRSTag[@type=3 and @value='EXTREF']">
                <xsl:call-template name="href">
                    <xsl:with-param name="extref" select="."/>
                </xsl:call-template>
            </xsl:when>

            <xsl:when test="self::GRSTag[@type=3 and @value='ARCHREF']">
                <xsl:call-template name="href">
                    <xsl:with-param name="extref" select="."/>
                </xsl:call-template>
            </xsl:when>

            <xsl:when test="self::GRSTag[@type=3 and @value='NOTE']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>

            <xsl:when test="self::GRSTag[@type=3 and @value='BIBREF']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>

            <xsl:when test="self::GRSTag[@type=3 and @value='CORPNAME']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>

            <xsl:when test="self::GRSTag[@type=3 and @value='FAMNAME']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>

             <xsl:when test="self::GRSTag[@type=3 and @value='SUBJECT']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>

             <xsl:when test="self::GRSTag[@type=3 and @value='PERSNAME']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>

             <xsl:when test="self::GRSTag[@type=3 and @value='GEOGNAME']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>

             <xsl:when test="self::GRSTag[@type=3 and @value='NAME']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>

             <xsl:when test="self::GRSTag[@type=3 and @value='GENREFORM']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>

             <xsl:when test="self::GRSTag[@type=3 and @value='FUNCTION']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>

             <xsl:when test="self::GRSTag[@type=3 and @value='OCCUPATION']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>

             <xsl:when test="self::GRSTag[@type=3 and @value='ADDRESS']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>

             <xsl:when test="self::GRSTag[@type=3 and @value='ADDRESSLINE']">
                 <xsl:call-template name="generic_heirarchy_walk">
                     <xsl:with-param name="children" select="child::node()"/>
                 </xsl:call-template>
                 <br />
            </xsl:when>

             <xsl:when test="self::GRSTag[@type=3 and @value='CHRONLIST']">
                <ul>
                    <xsl:call-template name="generic_heirarchy_walk">
                        <xsl:with-param name="children" select="child::node()"/>
                    </xsl:call-template>
                </ul>
            </xsl:when>

             <xsl:when test="self::GRSTag[@type=3 and @value='CHRONITEM']">
                <li>
                    <xsl:call-template name="generic_heirarchy_walk">
                        <xsl:with-param name="children" select="child::node()"/>
                    </xsl:call-template>
                </li>
            </xsl:when>
             <xsl:when test="self::GRSTag[@type=3 and @value='IMPRINT']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>
             <xsl:when test="self::GRSTag[@type=3 and @value='PUBLISHER']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>
             <xsl:when test="self::GRSTag[@type=3 and @value='SUBAREA']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>
             <xsl:when test="self::GRSTag[@type=3 and @value='AUTHOR']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>
             <xsl:when test="self::GRSTag[@type=3 and @value='BIBSERIES']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>
             <xsl:when test="self::GRSTag[@type=3 and @value='BLOCKQUOTE']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>
             <xsl:when test="self::GRSTag[@type=3 and @value='EDITION']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>
             <xsl:when test="self::GRSTag[@type=3 and @value='EVENT']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>
             <xsl:when test="self::GRSTag[@type=3 and @value='EVENTGRP']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>
             <xsl:when test="self::GRSTag[@type=3 and @value='NUM']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>
             <xsl:when test="self::GRSTag[@type=3 and @value='ORGANIZATION']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>
             <xsl:when test="self::GRSTag[@type=3 and @value='ORIGINATION']">
                <xsl:call-template name="generic_heirarchy_walk">
                    <xsl:with-param name="children" select="child::node()"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>  <!-- all the nodes we are not interested in -->
                 <!--otherwise (tag not required <xsl:value-of select="." /> )-->
            </xsl:otherwise>
        </xsl:choose>
    </xsl:for-each>
</xsl:template>



<xsl:template name="href">
    <xsl:param name="extref"/>
    <xsl:variable name="link" select="$extref/GRSTag[@type=3 and @value='HREF']/text()"/>
    <xsl:variable name="display" select="$extref/GRSTag[@type=2 and @value=19]/text()"/>

    <xsl:if test="normalize-space($link)!=''">
        <xsl:variable name="link_text">
            <xsl:call-template name="replace_entity_refs">
                <xsl:with-param name="the_text" select="normalize-space($link)"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:variable name="display_text">
            <xsl:call-template name="highlight_and_replace">
                <xsl:with-param name="the_text" select="normalize-space($display)"/>
                <xsl:with-param name="keyword_phrase" select="$keyword_phrase"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:element name="a">
            <xsl:attribute name="href">
                <xsl:value-of select="$link_text" />
            </xsl:attribute>
            <xsl:attribute name="target">
                new
            </xsl:attribute>
            <xsl:value-of select="$display_text" />
        </xsl:element>
    </xsl:if>
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='LEVEL']">
    <xsl:variable name="level" select="."/>
    <xsl:variable name="level_lc" select="translate($level, $uc, $lc)"/>

    <xsl:if test="normalize-space($level_lc)!='otherlevel'">
        <xsl:value-of select="$level" />
    </xsl:if>
</xsl:template>



<xsl:template match="GRSTag[@type=3 and @value='OTHERLEVEL']">
    <xsl:variable name="the_text" select="."/>
    <xsl:if test="normalize-space($the_text)!=''">
        <xsl:call-template name="replace_entity_refs">
            <xsl:with-param name="the_text" select="$the_text"/>
        </xsl:call-template>
    </xsl:if>
</xsl:template>
</xsl:stylesheet>
