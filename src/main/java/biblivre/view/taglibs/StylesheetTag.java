package biblivre.view.taglibs;

import jakarta.servlet.ServletContext;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import lombok.Setter;

@Setter
public class StylesheetTag extends SimpleTagSupport {
    private String fileName = "";
    private String media = "all";

    @Override
    public void doTag() {
        final JspWriter out = getJspContext().getOut();

        ServletContext servletContext = ((PageContext) getJspContext()).getServletContext();

        try {
            out.print(
                    "<link rel=\"stylesheet\" type=\"text/css\" media=\""
                            + this.media
                            + "\" href=\""
                            + servletContext.getContextPath()
                            + "/static/styles/"
                            + this.fileName
                            + "\" />");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
