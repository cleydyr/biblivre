package biblivre.update.v6_0_0$9_0_5$alpha;

import biblivre.update.exception.UpdateException;
import biblivre.update.translations.TranslationCreatorUpdate;
import biblivre.update.translations.TranslationModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.Set;
import org.springframework.stereotype.Component;

/** Adds address_neighborhood user field (after address_zip) and CEP lookup UI translations. */
@Component
public class Update extends TranslationCreatorUpdate {

    @Override
    public void doUpdateScopedBySchema(Connection connection) {
        addAddressNeighborhoodField(connection);
    }

    @Override
    public Collection<TranslationModel> getAdditionalTranslations() {
        return ADDITIONAL_TRANSLATIONS;
    }

    private void addAddressNeighborhoodField(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            boolean alreadyExists;
            try (ResultSet resultSet =
                    statement.executeQuery(
                            """
							SELECT 1 FROM users_fields WHERE key = 'address_neighborhood'
							""")) {
                alreadyExists = resultSet.next();
            }

            if (alreadyExists) {
                return;
            }

            statement.execute(
                    """
					UPDATE users_fields
					   SET sort_order = sort_order + 1
					 WHERE sort_order >= 13
					""");

            statement.execute(
                    """
					INSERT INTO users_fields (key, type, required, max_length, sort_order)
					VALUES ('address_neighborhood', 'string', false, 100, 13)
					""");
        } catch (Exception e) {
            throw new UpdateException("Error adding address_neighborhood user field", e);
        }
    }

    private static final Collection<TranslationModel> ADDITIONAL_TRANSLATIONS =
            Set.of(
                    new TranslationModel(
                            "circulation.custom.user_field.address_neighborhood",
                            "pt-BR",
                            "Bairro"),
                    new TranslationModel(
                            "circulation.custom.user_field.address_neighborhood",
                            "en-US",
                            "Neighborhood"),
                    new TranslationModel(
                            "circulation.custom.user_field.address_neighborhood", "es", "Barrio"),
                    new TranslationModel(
                            "circulation.user.address_lookup.search", "pt-BR", "Buscar"),
                    new TranslationModel(
                            "circulation.user.address_lookup.search", "en-US", "Lookup"),
                    new TranslationModel("circulation.user.address_lookup.search", "es", "Buscar"),
                    new TranslationModel(
                            "circulation.user.address_lookup.confirm_overwrite_title",
                            "pt-BR",
                            "Substituir endereço?"),
                    new TranslationModel(
                            "circulation.user.address_lookup.confirm_overwrite_title",
                            "en-US",
                            "Replace address?"),
                    new TranslationModel(
                            "circulation.user.address_lookup.confirm_overwrite_title",
                            "es",
                            "¿Reemplazar dirección?"),
                    new TranslationModel(
                            "circulation.user.address_lookup.confirm_overwrite_message",
                            "pt-BR",
                            "Já existem dados de endereço preenchidos. Substituir pelos dados do CEP?"),
                    new TranslationModel(
                            "circulation.user.address_lookup.confirm_overwrite_message",
                            "en-US",
                            "Some address fields already have values. Replace them with the ZIP lookup results?"),
                    new TranslationModel(
                            "circulation.user.address_lookup.confirm_overwrite_message",
                            "es",
                            "Ya hay datos de dirección. ¿Reemplazarlos con los del código postal?"),
                    new TranslationModel(
                            "circulation.user.address_lookup.incomplete",
                            "pt-BR",
                            "Endereço incompleto; preencha a rua manualmente."),
                    new TranslationModel(
                            "circulation.user.address_lookup.incomplete",
                            "en-US",
                            "Incomplete address; please fill in the street manually."),
                    new TranslationModel(
                            "circulation.user.address_lookup.incomplete",
                            "es",
                            "Dirección incompleta; complete la calle manualmente."),
                    new TranslationModel(
                            "circulation.user.address_lookup.not_found",
                            "pt-BR",
                            "CEP não encontrado."),
                    new TranslationModel(
                            "circulation.user.address_lookup.not_found",
                            "en-US",
                            "ZIP code not found."),
                    new TranslationModel(
                            "circulation.user.address_lookup.not_found",
                            "es",
                            "Código postal no encontrado."),
                    new TranslationModel(
                            "circulation.user.address_lookup.invalid_cep",
                            "pt-BR",
                            "Informe um CEP válido com 8 dígitos."),
                    new TranslationModel(
                            "circulation.user.address_lookup.invalid_cep",
                            "en-US",
                            "Enter a valid 8-digit ZIP code."),
                    new TranslationModel(
                            "circulation.user.address_lookup.invalid_cep",
                            "es",
                            "Ingrese un código postal válido de 8 dígitos."),
                    new TranslationModel(
                            "circulation.user.address_lookup.error",
                            "pt-BR",
                            "Não foi possível consultar o CEP. Tente novamente ou preencha o endereço manualmente."),
                    new TranslationModel(
                            "circulation.user.address_lookup.error",
                            "en-US",
                            "Could not look up the ZIP code. Try again or fill the address manually."),
                    new TranslationModel(
                            "circulation.user.address_lookup.error",
                            "es",
                            "No fue posible consultar el código postal. Intente de nuevo o complete la dirección"
                                    + " manualmente."));
}
