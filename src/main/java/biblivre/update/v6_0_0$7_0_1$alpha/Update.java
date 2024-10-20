package biblivre.update.v6_0_0$7_0_1$alpha;

import biblivre.core.translations.TranslationBO;
import biblivre.update.UpdateService;
import biblivre.update.exception.UpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Update implements UpdateService {

    @Override
    public void doUpdate(Connection connection) {
        addConfigurations(connection);

        addTranslations(connection);
    }

    private void addConfigurations(Connection connection) {
        try (PreparedStatement statement =
                connection.prepareStatement(INSERT_CONFIGURATION_TEMPLATE)) {
            for (ConfigurationModel configuration : configurations) {
                statement.setString(1, configuration.key());
                statement.setString(2, configuration.value());
                statement.setString(3, configuration.type());
                statement.setBoolean(4, configuration.required());
                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException sqlException) {
            throw new UpdateException("can't add configurations", sqlException);
        }
    }

    private void addTranslations(Connection connection) throws UpdateException {
        for (Map.Entry<String, Map<String, String>> entry : ADDITIONAL_TRANSLATIONS.entrySet()) {
            for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
                String key = entry.getKey();

                String language = entry2.getKey();

                String translation = entry2.getValue();

                translationBO.addSingleTranslation(language, key, translation);
            }
        }
    }

    private final Map<String, Map<String, String>> ADDITIONAL_TRANSLATIONS =
            Map.of(
                    "cataloging.reservation.error.limit_exceeded",
                    Map.of(
                            "en-US",
                            "The selected reader surpassed the limit of authorized loans",
                            "es",
                            "El lector seleccionado excedió el límite de reservas permitidas",
                            "pt-BR",
                            "O leitor selecionado ultrapassou o limite de reservas permitidas"),
                    "cataloging.import.error.file_upload_error",
                    Map.of(
                            "en-US",
                            "Couldn't upload file. Please contact the administrator to analyze this problem.",
                            "es",
                            "No ha sido posible subir el archivo. Por favor, contacta el administrador del sistema para analizar este problema.",
                            "pt-BR",
                            "Não foi possível fazer upload do arquivo. Por favor, contacte o administrador do sistema para anlizar este problema."),
                    "administration.configuration.title.holding.label_print_paragraph_alignment",
                    Map.of(
                            "pt-BR",
                            "Alinhamento de parágrafo",
                            "es",
                            "Alineación de párrafo",
                            "en-US",
                            "Paragraph alignment"),
                    "administration.configuration.description.holding.label_print_paragraph_alignment",
                    Map.of(
                            "pt-BR",
                            "Alinhamento de parágrafo que será utilizado em cada etiqueta impressa",
                            "es",
                            "Alineación de párrafo que va a ser usado en cada etiqueta impresa",
                            "en-US",
                            "Paragraph alignment which will be used in each printed label"),
                    "administration.configuration.label_print.ALIGN_CENTER",
                    Map.of("pt-BR", "Centralizado", "es", "Centrado", "en-US", "Center"),
                    "administration.configuration.label_print.ALIGN_JUSTIFIED_ALL",
                    Map.of(
                            "pt-BR", "Justificado (tudo)",
                            "es", "Justificado (todo)",
                            "en-US", "Justified (all)"),
                    "administration.configuration.label_print.ALIGN_JUSTIFIED",
                    Map.of(
                            "pt-BR", "Justificado",
                            "es", "Justificado",
                            "en-US", "Justified"),
                    "administration.configuration.label_print.ALIGN_LEFT",
                    Map.of(
                            "pt-BR", "À esquerda",
                            "es", "A la izquierda",
                            "en-US", "Left"),
                    "administration.configuration.label_print.ALIGN_RIGHT",
                    Map.of("pt-BR", "À direita", "es", "A la derecha", "en-US", "Right"),
                    "circulation.error.invalid_user_name",
                    Map.of(
                            "pt-BR",
                            "Este usuário possui nome com caracteres inválidos (:)",
                            "es",
                            "Este usuario posee un nombre con caracteres inválidos (:)",
                            "en-US",
                            "This user has a name with invalid characters (:)"));

    private static final String INSERT_CONFIGURATION_TEMPLATE =
            """
            INSERT INTO configurations
              ("key", value, "type", required)
              VALUES(?, ?, ?, ?)
              ON CONFLICT DO NOTHING;
            """;

    Collection<ConfigurationModel> configurations =
            Set.of(
                    new ConfigurationModel(
                            "holding.label_print_paragraph_alignment",
                            "ALIGN_CENTER",
                            "string",
                            true),
                    new ConfigurationModel(
                            "circulation.lending_receipt.printer.type",
                            "printer_24_columns",
                            "string",
                            false));

    record ConfigurationModel(String key, String value, String type, boolean required) {}

    private TranslationBO translationBO;

    @Autowired
    public void setTranslationsBO(TranslationBO translationBO) {
        this.translationBO = translationBO;
    }
}
