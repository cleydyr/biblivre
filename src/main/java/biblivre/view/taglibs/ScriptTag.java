package biblivre.view.taglibs;

import jakarta.servlet.ServletContext;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import lombok.Setter;

@Setter
public class ScriptTag extends SimpleTagSupport {
    private String fileName = "";

    @Override
    public void doTag() {
        final JspWriter out = getJspContext().getOut();

        ServletContext servletContext = ((PageContext) getJspContext()).getServletContext();

        try {
            out.print(
                    "<script type=\"text/javascript\" src=\""
                            + servletContext.getContextPath()
                            + "/static/scripts/"
                            + this.fileName
                            + "\"></script>");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
