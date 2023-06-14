package biblivre.update.v6_0_0$1_0_0$alpha;

import biblivre.core.translations.TranslationBO;
import biblivre.update.UpdateService;
import biblivre.update.exception.UpdateException;
import java.sql.Connection;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Update implements UpdateService {

    private TranslationBO translationBO;

    public void doUpdate(Connection connection) throws UpdateException {
        _addTranslations(connection);
    }

    private void _addTranslations(Connection connection) throws UpdateException {
        for (Map.Entry<String, Map<String, String>> entry : _TRANSLATIONS.entrySet()) {
            for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
                String key = entry.getKey();

                String language = entry2.getKey();

                String translation = entry2.getValue();

                translationBO.addSingleTranslation(language, key, translation);
            }
        }
    }

    @Autowired
    public void setTranslationsBO(TranslationBO translationBO) {
        this.translationBO = translationBO;
    }

    private final Map<String, Map<String, String>> _TRANSLATIONS =
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
                            "Não foi possível fazer upload do arquivo. Por favor, contacte o administrador do sistema para anlizar este problema."));
}
