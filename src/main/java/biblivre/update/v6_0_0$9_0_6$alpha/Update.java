package biblivre.update.v6_0_0$9_0_6$alpha;

import biblivre.update.translations.TranslationCreatorUpdate;
import biblivre.update.translations.TranslationModel;
import java.util.Collection;
import java.util.Set;
import org.springframework.stereotype.Component;

/** Translations for bibliographic Cutter-Sanborn author code generation (090 $b). */
@Component
public class Update extends TranslationCreatorUpdate {

    @Override
    public Collection<TranslationModel> getAdditionalTranslations() {
        return ADDITIONAL_TRANSLATIONS;
    }

    private static final Collection<TranslationModel> ADDITIONAL_TRANSLATIONS =
            Set.of(
                    new TranslationModel(
                            "cataloging.bibliographic.cutter.generate", "pt-BR", "Gerar"),
                    new TranslationModel(
                            "cataloging.bibliographic.cutter.generate", "en-US", "Generate"),
                    new TranslationModel(
                            "cataloging.bibliographic.cutter.generate", "es", "Generar"),
                    new TranslationModel(
                            "cataloging.bibliographic.cutter.missing_author",
                            "pt-BR",
                            "Informe o autor (campo 100, 110 ou 111) antes de gerar o código Cutter."),
                    new TranslationModel(
                            "cataloging.bibliographic.cutter.missing_author",
                            "en-US",
                            "Enter the author (field 100, 110, or 111) before generating the Cutter"
                                    + " code."),
                    new TranslationModel(
                            "cataloging.bibliographic.cutter.missing_author",
                            "es",
                            "Indique el autor (campo 100, 110 o 111) antes de generar el código"
                                    + " Cutter."),
                    new TranslationModel(
                            "cataloging.bibliographic.cutter.missing_title",
                            "pt-BR",
                            "Informe o título (campo 245) antes de gerar o código Cutter."),
                    new TranslationModel(
                            "cataloging.bibliographic.cutter.missing_title",
                            "en-US",
                            "Enter the title (field 245) before generating the Cutter code."),
                    new TranslationModel(
                            "cataloging.bibliographic.cutter.missing_title",
                            "es",
                            "Indique el título (campo 245) antes de generar el código Cutter."),
                    new TranslationModel(
                            "cataloging.bibliographic.cutter.not_found",
                            "pt-BR",
                            "Não foi possível calcular o código Cutter para este autor."),
                    new TranslationModel(
                            "cataloging.bibliographic.cutter.not_found",
                            "en-US",
                            "Could not calculate the Cutter code for this author."),
                    new TranslationModel(
                            "cataloging.bibliographic.cutter.not_found",
                            "es",
                            "No fue posible calcular el código Cutter para este autor."),
                    new TranslationModel(
                            "cataloging.bibliographic.cutter.confirm_overwrite_title",
                            "pt-BR",
                            "Substituir código do autor?"),
                    new TranslationModel(
                            "cataloging.bibliographic.cutter.confirm_overwrite_title",
                            "en-US",
                            "Replace author code?"),
                    new TranslationModel(
                            "cataloging.bibliographic.cutter.confirm_overwrite_title",
                            "es",
                            "¿Reemplazar código del autor?"),
                    new TranslationModel(
                            "cataloging.bibliographic.cutter.confirm_overwrite_message",
                            "pt-BR",
                            "O campo código do autor (090 $b) já está preenchido. Substituir pelo"
                                    + " código Cutter calculado?"),
                    new TranslationModel(
                            "cataloging.bibliographic.cutter.confirm_overwrite_message",
                            "en-US",
                            "The author code field (090 $b) already has a value. Replace it with the"
                                    + " calculated Cutter code?"),
                    new TranslationModel(
                            "cataloging.bibliographic.cutter.confirm_overwrite_message",
                            "es",
                            "El campo código del autor (090 $b) ya tiene un valor. ¿Reemplazarlo"
                                    + " con el código Cutter calculado?"));
}
