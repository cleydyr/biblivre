package biblivre.administration.reports.v2.model;

import java.util.Collection;
import net.sf.jasperreports.engine.JRParameter;

/**
 * This class is a model for the report module. It is responsible for handling the data for the
 * report module. A report has a name, a description, a set of parameters.
 */
public interface JasperReport {
    String getName();

    String getDescription();

    Collection<JRParameter> getParameters();
}
