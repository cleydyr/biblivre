package biblivre.update.v6_0_0$9_0_9$alpha;

import biblivre.update.translations.TranslationCreatorUpdate;
import biblivre.update.translations.TranslationModel;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

/** Translations for filling the bibliographic cataloging form from an ISBN. */
@Component
public class Update extends TranslationCreatorUpdate {

    private static final String PREFIX = "cataloging.bibliographic.isbn_lookup.";

    @Override
    public Collection<TranslationModel> getAdditionalTranslations() {
        return ADDITIONAL_TRANSLATIONS;
    }

    private static List<TranslationModel> translations(
            String key, String portuguese, String english, String spanish) {
        return List.of(
                new TranslationModel(PREFIX + key, "pt-BR", portuguese),
                new TranslationModel(PREFIX + key, "en-US", english),
                new TranslationModel(PREFIX + key, "es", spanish));
    }

    static final Collection<TranslationModel> ADDITIONAL_TRANSLATIONS =
            Stream.of(
                            translations(
                                    "label",
                                    "Preencher por ISBN",
                                    "Fill from ISBN",
                                    "Completar por ISBN"),
                            translations("search", "Buscar", "Look up", "Buscar"),
                            translations(
                                    "invalid_isbn",
                                    "Informe um ISBN válido com 10 ou 13 dígitos.",
                                    "Enter a valid 10- or 13-digit ISBN.",
                                    "Ingrese un ISBN válido de 10 o 13 dígitos."),
                            translations(
                                    "not_found",
                                    "ISBN não encontrado no provedor de metadados.",
                                    "ISBN not found by the metadata provider.",
                                    "ISBN no encontrado en el proveedor de metadatos."),
                            translations(
                                    "error",
                                    "Não foi possível consultar o ISBN. Tente novamente ou"
                                            + " preencha os campos manualmente.",
                                    "Could not look up the ISBN. Try again or fill in the fields"
                                            + " manually.",
                                    "No fue posible consultar el ISBN. Intente nuevamente o"
                                            + " complete los campos manualmente."),
                            translations(
                                    "success",
                                    "Campos preenchidos a partir do ISBN. Revise-os antes de"
                                            + " salvar.",
                                    "Fields filled from the ISBN. Review them before saving.",
                                    "Campos completados a partir del ISBN. Revíselos antes de"
                                            + " guardar."),
                            translations(
                                    "confirm_overwrite_title",
                                    "Substituir campos?",
                                    "Replace fields?",
                                    "¿Reemplazar campos?"),
                            translations(
                                    "confirm_overwrite_message",
                                    "Alguns campos encontrados para este ISBN já estão"
                                            + " preenchidos e serão substituídos.",
                                    "Some fields found for this ISBN already have values and will"
                                            + " be replaced.",
                                    "Algunos campos encontrados para este ISBN ya tienen valores y"
                                            + " serán reemplazados."))
                    .flatMap(List::stream)
                    .toList();
}
