package biblivre.view.taglibs;

import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.translations.TranslationsMap;
import biblivre.core.utils.Constants;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import lombok.Setter;

@Setter
public class CultureScriptTag extends SimpleTagSupport {
    @Override
    public void doTag() {
        final PageContext pageContext = (PageContext) getJspContext();

        final JspWriter out = pageContext.getOut();

        ServletRequest request = pageContext.getRequest();

        TranslationsMap map = (TranslationsMap) request.getAttribute("translationsMap");

        String languageCode = map.getText("language_code");

        ServletContext servletContext = pageContext.getServletContext();

        ConfigurationBO configurationBO = (ConfigurationBO) request.getAttribute("configurations");

        try {
            out.print(
                    "<script type=\"text/javascript\" src=\""
                            + servletContext.getContextPath()
                            + "/static/scripts/cultures/globalize.culture."
                            + languageCode
                            + ".js"
                            + "\"></script>");

            out.print(
                    "<script type=\"text/javascript\" >Globalize.culture('"
                            + languageCode
                            + "');</script>");

            out.print(
                    "<script type=\"text/javascript\" >Globalize.culture().numberFormat.currency.symbol = '"
                            + configurationBO.getString(Constants.CONFIG_CURRENCY)
                            + "';</script>");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
