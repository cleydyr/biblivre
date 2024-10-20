package biblivre.update.v6_0_0$7_0_1$alpha;

import biblivre.core.translations.TranslationBO;
import biblivre.update.UpdateService;
import biblivre.update.exception.UpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
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
        for (TranslationModel entry : ADDITIONAL_TRANSLATIONS) {
            translationBO.addSingleTranslation(entry.language(), entry.key(), entry.text());
        }
    }

    record TranslationModel(String key, String language, String text) {}

    Collection<TranslationModel> ADDITIONAL_TRANSLATIONS =
            Set.of(
                    new TranslationModel(
                            "cataloging.reservation.error.limit_exceeded",
                            "en-US",
                            "The selected reader surpassed the limit of authorized loans"),
                    new TranslationModel(
                            "cataloging.reservation.error.limit_exceeded",
                            "es",
                            "El lector seleccionado excedió el límite de reservas permitidas"),
                    new TranslationModel(
                            "cataloging.reservation.error.limit_exceeded",
                            "pt-BR",
                            "O leitor selecionado ultrapassou o limite de reservas permitidas"),
                    new TranslationModel(
                            "cataloging.import.error.file_upload_error",
                            "en-US",
                            "Couldn't upload file. Please contact the administrator to analyze this problem."),
                    new TranslationModel(
                            "cataloging.import.error.file_upload_error",
                            "es",
                            "No ha sido posible subir el archivo. Por favor, contacta el administrador del sistema para analizar este problema."),
                    new TranslationModel(
                            "cataloging.import.error.file_upload_error",
                            "pt-BR",
                            "Não foi possível fazer upload do arquivo. Por favor, contacte o administrador do sistema para anlizar este problema."),
                    new TranslationModel(
                            "administration.configuration.title.holding.label_print_paragraph_alignment",
                            "pt-BR",
                            "Alinhamento de parágrafo"),
                    new TranslationModel(
                            "administration.configuration.title.holding.label_print_paragraph_alignment",
                            "es",
                            "Alineación de párrafo"),
                    new TranslationModel(
                            "administration.configuration.title.holding.label_print_paragraph_alignment",
                            "en-US",
                            "Paragraph alignment"),
                    new TranslationModel(
                            "administration.configuration.description.holding.label_print_paragraph_alignment",
                            "pt-BR",
                            "Alinhamento de parágrafo que será utilizado em cada etiqueta impressa"),
                    new TranslationModel(
                            "administration.configuration.description.holding.label_print_paragraph_alignment",
                            "es",
                            "Alineación de párrafo que va a ser usado en cada etiqueta impresa"),
                    new TranslationModel(
                            "administration.configuration.description.holding.label_print_paragraph_alignment",
                            "en-US",
                            "Paragraph alignment which will be used in each printed label"),
                    new TranslationModel(
                            "administration.configuration.label_print.ALIGN_CENTER",
                            "pt-BR",
                            "Centralizado"),
                    new TranslationModel(
                            "administration.configuration.label_print.ALIGN_CENTER",
                            "es",
                            "Centrado"),
                    new TranslationModel(
                            "administration.configuration.label_print.ALIGN_CENTER",
                            "en-US",
                            "Center"),
                    new TranslationModel(
                            "administration.configuration.label_print.ALIGN_JUSTIFIED_ALL",
                            "pt-BR",
                            "Justificado (tudo)"),
                    new TranslationModel(
                            "administration.configuration.label_print.ALIGN_JUSTIFIED_ALL",
                            "es",
                            "Justificado (todo)"),
                    new TranslationModel(
                            "administration.configuration.label_print.ALIGN_JUSTIFIED_ALL",
                            "en-US",
                            "Justified (all)"),
                    new TranslationModel(
                            "administration.configuration.label_print.ALIGN_JUSTIFIED",
                            "pt-BR",
                            "Justificado"),
                    new TranslationModel(
                            "administration.configuration.label_print.ALIGN_JUSTIFIED",
                            "es",
                            "Justificado"),
                    new TranslationModel(
                            "administration.configuration.label_print.ALIGN_JUSTIFIED",
                            "en-US",
                            "Justified"),
                    new TranslationModel(
                            "administration.configuration.label_print.ALIGN_LEFT",
                            "pt-BR",
                            "À esquerda"),
                    new TranslationModel(
                            "administration.configuration.label_print.ALIGN_LEFT",
                            "es",
                            "A la izquierda"),
                    new TranslationModel(
                            "administration.configuration.label_print.ALIGN_LEFT", "en-US", "Left"),
                    new TranslationModel(
                            "administration.configuration.label_print.ALIGN_RIGHT",
                            "pt-BR",
                            "À direita"),
                    new TranslationModel(
                            "administration.configuration.label_print.ALIGN_RIGHT",
                            "es",
                            "A la derecha"),
                    new TranslationModel(
                            "administration.configuration.label_print.ALIGN_RIGHT",
                            "en-US",
                            "Right"),
                    new TranslationModel(
                            "circulation.error.invalid_user_name",
                            "pt-BR",
                            "Este usuário possui nome com caracteres inválidos (:)"),
                    new TranslationModel(
                            "circulation.error.invalid_user_name",
                            "es",
                            "Este usuario posee un nombre con caracteres inválidos (:)"),
                    new TranslationModel(
                            "circulation.error.invalid_user_name",
                            "en-US",
                            "This user has a name with invalid characters (:)"),
                    new TranslationModel(
                            "circulation.users.success.saved",
                            "pt-BR",
                            "Usuário salvo com sucesso"),
                    new TranslationModel(
                            "circulation.users.success.saved", "es", "Usuario guardado con éxito"),
                    new TranslationModel(
                            "circulation.users.success.saved", "en-US", "User saved successfully"));

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
