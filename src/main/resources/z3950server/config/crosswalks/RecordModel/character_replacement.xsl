<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html"/>

<xsl:variable name="uc" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
<xsl:variable name="lc" select="'abcdefghijklmnopqrstuvwxyz'"/>

<xsl:template name="replace_entity_refs"> 
    <xsl:param name="the_text" />

    <xsl:param name="no_apos">    
        <xsl:call-template name="character_replace">
            <xsl:with-param name="string" select="$the_text"/>
            <xsl:with-param name="character_to_replace">&amp;apos;</xsl:with-param>
            <xsl:with-param name="replacement_character">'</xsl:with-param> 
        </xsl:call-template>
    </xsl:param>
    
    <xsl:param name="no_quot">    
        <xsl:call-template name="character_replace">
            <xsl:with-param name="string" select="$no_apos"/>
            <xsl:with-param name="character_to_replace">&amp;quot;</xsl:with-param>
            <xsl:with-param name="replacement_character">"</xsl:with-param> 
        </xsl:call-template>
    </xsl:param>
    

     <xsl:param name="no_lt">    
        <xsl:call-template name="character_replace">
            <xsl:with-param name="string" select="$no_quot"/>
            <xsl:with-param name="character_to_replace">&amp;lt;</xsl:with-param>
            <xsl:with-param name="replacement_character"><![CDATA[<]]></xsl:with-param> 
        </xsl:call-template>
    </xsl:param>
 
 
    <xsl:param name="no_gt">    
        <xsl:call-template name="character_replace">
            <xsl:with-param name="string" select="$no_lt"/>
            <xsl:with-param name="character_to_replace">&amp;gt;</xsl:with-param>
            <xsl:with-param name="replacement_character"><![CDATA[>]]></xsl:with-param> 
        </xsl:call-template>
    </xsl:param>
    
    
    <xsl:param name="no_amp">    
        <xsl:call-template name="character_replace">
            <xsl:with-param name="string" select="$no_gt"/>
            <xsl:with-param name="character_to_replace">&amp;amp;</xsl:with-param>
            <xsl:with-param name="replacement_character"><![CDATA[&]]></xsl:with-param> 
        </xsl:call-template>
    </xsl:param>
    
    <xsl:param name="no_iac">    
        <xsl:call-template name="character_replace">
            <xsl:with-param name="string" select="$no_amp"/>
            <xsl:with-param name="character_to_replace">&amp;iacute;</xsl:with-param>
            <xsl:with-param name="replacement_character"><![CDATA[í]]></xsl:with-param> 
        </xsl:call-template>
    </xsl:param>
    
    <xsl:param name="no_eac">    
        <xsl:call-template name="character_replace">
            <xsl:with-param name="string" select="$no_iac"/>
            <xsl:with-param name="character_to_replace">&amp;eacute;</xsl:with-param>
            <xsl:with-param name="replacement_character"><![CDATA[é]]></xsl:with-param> 
        </xsl:call-template>
    </xsl:param>
    
     <xsl:param name="no_auml">    
         <xsl:call-template name="character_replace">
             <xsl:with-param name="string" select="$no_eac"/>
             <xsl:with-param name="character_to_replace">&amp;auml;</xsl:with-param>
             <xsl:with-param name="replacement_character"><![CDATA[ä]]></xsl:with-param> 
         </xsl:call-template>
     </xsl:param>


     <xsl:param name="no_ouml">    
         <xsl:call-template name="character_replace">
             <xsl:with-param name="string" select="$no_auml"/>
             <xsl:with-param name="character_to_replace">&amp;ouml;</xsl:with-param>
             <xsl:with-param name="replacement_character"><![CDATA[ö]]></xsl:with-param> 
         </xsl:call-template>
     </xsl:param>

     <xsl:param name="no_euml">    
         <xsl:call-template name="character_replace">
             <xsl:with-param name="string" select="$no_ouml"/>
             <xsl:with-param name="character_to_replace">&amp;euml</xsl:with-param>
             <xsl:with-param name="replacement_character"><![CDATA[ë]]></xsl:with-param> 
         </xsl:call-template>
     </xsl:param>


     <xsl:param name="no_uuml">    
         <xsl:call-template name="character_replace">
             <xsl:with-param name="string" select="$no_euml"/>
             <xsl:with-param name="character_to_replace">&amp;uuml;</xsl:with-param>
             <xsl:with-param name="replacement_character"><![CDATA[ü]]></xsl:with-param> 
         </xsl:call-template>
     </xsl:param>
     
     
     <xsl:param name="no_lb">    
         <xsl:call-template name="character_replace">
             <xsl:with-param name="string" select="$no_uuml"/>
             <xsl:with-param name="character_to_replace">&amp;lsqb;</xsl:with-param>
             <xsl:with-param name="replacement_character">[</xsl:with-param> 
         </xsl:call-template>
     </xsl:param>
 
 
      <xsl:param name="no_rb">    
          <xsl:call-template name="character_replace">
              <xsl:with-param name="string" select="$no_lb"/>
              <xsl:with-param name="character_to_replace">&amp;rsqb;</xsl:with-param>
              <xsl:with-param name="replacement_character">]</xsl:with-param> 
          </xsl:call-template>
      </xsl:param>
 
 
      <xsl:param name="no_at">    
          <xsl:call-template name="character_replace">
              <xsl:with-param name="string" select="$no_rb"/>
              <xsl:with-param name="character_to_replace">&amp;commat;</xsl:with-param>
              <xsl:with-param name="replacement_character">@</xsl:with-param> 
          </xsl:call-template>
      </xsl:param>
      
     <xsl:param name="no_slash">    
                <xsl:call-template name="character_replace">
                    <xsl:with-param name="string" select="$no_at"/>
                    <xsl:with-param name="character_to_replace">&amp;sol;</xsl:with-param>
                    <xsl:with-param name="replacement_character">/</xsl:with-param> 
                </xsl:call-template>
      </xsl:param>


     <xsl:param name="no_ecirc">    
                <xsl:call-template name="character_replace">
                    <xsl:with-param name="string" select="$no_slash"/>
                    <xsl:with-param name="character_to_replace">&amp;ecirc;</xsl:with-param>
                    <xsl:with-param name="replacement_character"><![CDATA[ê]]></xsl:with-param> 
                </xsl:call-template>
      </xsl:param>
      
    
      <xsl:param name="no_ccedil">    
                <xsl:call-template name="character_replace">
                    <xsl:with-param name="string" select="$no_ecirc"/>
                    <xsl:with-param name="character_to_replace">&amp;ccedil;</xsl:with-param>
                    <xsl:with-param name="replacement_character"><![CDATA[ç]]></xsl:with-param> 
                </xsl:call-template>
      </xsl:param>
     
      
      <xsl:param name="no_egrave">    
                <xsl:call-template name="character_replace">
                    <xsl:with-param name="string" select="$no_ccedil"/>
                    <xsl:with-param name="character_to_replace">&amp;egrave;</xsl:with-param>
                    <xsl:with-param name="replacement_character"><![CDATA[è]]></xsl:with-param> 
                </xsl:call-template>
      </xsl:param>
 
 
       <xsl:param name="no_pound">    
                <xsl:call-template name="character_replace">
                    <xsl:with-param name="string" select="$no_egrave"/>
                    <xsl:with-param name="character_to_replace">&amp;pound;</xsl:with-param>
                    <xsl:with-param name="replacement_character"><![CDATA[£]]></xsl:with-param> 
                </xsl:call-template>
      </xsl:param>
     
 
    <xsl:param name="no_italic">    
        <xsl:call-template name="remove_italic">
            <xsl:with-param name="string" select="$no_pound"/>
        </xsl:call-template>
    </xsl:param>  
    
    <xsl:param name="no_bold">    
        <xsl:call-template name="remove_bold">
            <xsl:with-param name="string" select="$no_italic"/>
        </xsl:call-template>
    </xsl:param>    
                 
    <xsl:value-of select="$no_bold" />  
</xsl:template>



<xsl:template name="character_replace">
    <xsl:param name="string" />
    <xsl:param name="character_to_replace" />
    <xsl:param name="replacement_character" />
    
    <xsl:choose>    
        <xsl:when test="contains($string, $character_to_replace)">
            <xsl:value-of select="substring-before($string, $character_to_replace)" />
            <xsl:value-of select="$replacement_character" />
            <xsl:call-template name="character_replace">
                <xsl:with-param name="string" select="substring-after($string, $character_to_replace)"/>
                <xsl:with-param name="character_to_replace" select="$character_to_replace"/>
                <xsl:with-param name="replacement_character" select="$replacement_character"/>  
            </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>             
            <xsl:value-of select="$string" /> 
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>


<xsl:template name="remove_italic">
    <xsl:param name="string" />
    
    <xsl:choose>    
        <xsl:when test="contains($string, 'italic')"> 
            <xsl:value-of select="substring-before($string, 'italic')" />             
            <xsl:if test="starts-with(substring-after($string, 'italic'), ' ' ) or starts-with(substring-after($string, 'italic'), 'ised' )">
                italic             
            </xsl:if> 
            <xsl:call-template name="remove_italic">            
                <xsl:with-param name="string" select="substring-after($string, 'italic')"/>             
            </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>             
            <xsl:value-of select="$string" /> 
        </xsl:otherwise>
    </xsl:choose>       
</xsl:template>


<xsl:template name="remove_bold">
    <xsl:param name="string" />
    
    <xsl:choose>    
        <xsl:when test="contains($string, 'bold')"> 
            <xsl:value-of select="substring-before($string, 'bold')" />             
            <xsl:if test="starts-with(substring-after($string, 'bold'), ' ' ) or starts-with(substring-after($string, 'bold'), 'er ') or starts-with(substring-after($string, 'bold'), 'en ') or starts-with(substring-after($string, 'bold'), 'ly ') or starts-with(substring-after($string, 'bold'), 'ness ') or starts-with(substring-after($string, 'bold'), '-')">
                bold             
            </xsl:if> 
            <xsl:call-template name="remove_bold">            
                <xsl:with-param name="string" select="substring-after($string, 'bold')"/>             
            </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>             
            <xsl:value-of select="$string" /> 
        </xsl:otherwise>
    </xsl:choose>       
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



<xsl:template name="highlight_and_replace" >
    <xsl:param name="the_text"/>
    <xsl:param name="keyword_phrase"/>
    <xsl:call-template name="highlight">
        <xsl:with-param name="phrase" select="$keyword_phrase"/>
        <xsl:with-param name="originaltext">
            <xsl:call-template name="replace_entity_refs">
                <xsl:with-param name="the_text" select="$the_text"/>
            </xsl:call-template>
        </xsl:with-param>
    </xsl:call-template>    
</xsl:template>


</xsl:stylesheet>