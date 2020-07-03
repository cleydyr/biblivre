<?xml version="1.0"?>
 
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:param name="hitno"/>
<xsl:output method="html"/>

<xsl:template match="/">
	<center>
	<table width="80%">
	<xsl:apply-templates select="ead" />
        </table>
	</center>
</xsl:template>

<xsl:template match="ead">
	<table width="80%">
		<xsl:apply-templates select="./archdesc/did/repository" />
		<xsl:apply-templates select="./eadheader" />
		<xsl:apply-templates select="./archdesc" />
 	</table>
</xsl:template>

<xsl:template match="eadheader">
	<xsl:apply-templates select="./filedesc" />
</xsl:template>

<xsl:template match="filedesc">
	<xsl:apply-templates select="./titlestmt" />
</xsl:template>

<xsl:template match="archdesc">
		<tr>
			<td width="100%">
				<b><span class="filetext"><xsl:apply-templates select="@level"/></span></b> -
				<span class="filetext"><xsl:apply-templates select="./did/unittitle"/></span>
				<b>ref.</b>
				<span class="refnumdata"><xsl:apply-templates select="./did/unitid"/></span>
				<span class="filetext">
					<b>date</b>
					<xsl:apply-templates select="./did/unitdate"/>
				</span>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<xsl:if test="./C">
							<td background="images/nodeI.gif" valign="top">&#160;</td>
						</xsl:if>
						<td width="100%"><p/>
							<ul>
								<xsl:apply-templates select="./bioghist"/>
								<xsl:apply-templates select="./scopecontent"/>
							</ul>
						</td>
					</tr>
				</table>
                          	<xsl:apply-templates select="dsc" />
			</td>
		</tr>
</xsl:template>

<xsl:template match="dsc">
	<xsl:apply-templates select="c" />
</xsl:template>

<xsl:template match="c">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td valign="top">
				<img src="images/nodeL.gif"/>
			</td>
			<td width="100%" valign="top">
				<b>
					<span class="filetext">
						<xsl:apply-templates select="@level"/>
						<xsl:apply-templates select="@otherlevel"/>
					</span>
				</b> -
				<span class="filetext"><xsl:apply-templates select="./did/unittitle"/></span>
				<b>ref.</b>
				<span class="refnumdata"><xsl:apply-templates select="./did/unitid"/></span>
				<span class="filetext">
					<b>date</b>
					<xsl:apply-templates select="./did/unitdate"/>
				</span>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<xsl:if test="./C">
							<td background="images/nodeI.gif" valign="top">&#160;</td>
						</xsl:if>
						<td width="100%"><p/>
							<ul>
								<xsl:apply-templates select="./bioghist"/>
								<xsl:apply-templates select="./scopecontent"/>

							</ul>
						</td>
					</tr>
				</table>
                          	<xsl:apply-templates select="c" />
			</td>
		</tr>
 	</table>
</xsl:template>

<xsl:template match="bioghist">
	<li><b>From Biographical History : </b><xsl:value-of select="."/><p/></li>
</xsl:template>

<xsl:template match="scopecontent">
	<li><b>From Scope/Content : </b><xsl:value-of select="."/><p/></li>
</xsl:template>

<xsl:template match="titlestmt">
	<xsl:apply-templates select="titleproper"/>
</xsl:template>

<xsl:template match="repository">
	<tr>
		<td width="100%" align="center">
			<xsl:apply-templates select="corpname" />
		</td>
	</tr>
</xsl:template>

<xsl:template match="titleproper">
	<tr>
		<td width="100%" align="center">
			<xsl:value-of select="."/><hr/>
		</td>
	</tr>
</xsl:template>

<xsl:template match="corpname">
	<xsl:value-of select="."/>
</xsl:template>

</xsl:stylesheet>
