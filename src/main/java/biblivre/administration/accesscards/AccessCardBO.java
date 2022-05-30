/*******************************************************************************
 * Este arquivo é parte do Biblivre5.
 *
 * Biblivre5 é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da Licença Pública Geral GNU como
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da
 * Licença, ou (caso queira) qualquer versão posterior.
 *
 * Este programa é distribuído na esperança de que possa ser  útil,
 * mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 * MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 * Licença Pública Geral GNU para maiores detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 * com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 *
 * @author Alberto Wagner <alberto@biblivre.org.br>
 * @author Danniel Willian <danniel@biblivre.org.br>
 ******************************************************************************/
package biblivre.administration.accesscards;

import biblivre.core.AbstractDTO;
import biblivre.core.exceptions.ValidationException;
import biblivre.legacy.entity.AccessCard;
import biblivre.legacy.repository.AccessCardRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class AccessCardBO {
    @Autowired private AccessCardRepository accessCardRepository;

    public boolean save(AccessCard accessCard) {
        if (accessCard != null) {
            Optional<AccessCard> existingCard = accessCardRepository.getByCode(accessCard.getCode());

            if (existingCard.isPresent()) {
            	throw new ValidationException("administration.accesscards.error.existing_card");
            }

            accessCardRepository.save(accessCard);

            return true;
        }

        return false;
    }

    public Page<AccessCard> search(String code, AccessCardStatus status, int limit, int offset) {
        int page = offset / limit;

        PageRequest pageable = PageRequest.of(page, limit);

		if (code == null) {
            if (status == null) {
                return accessCardRepository.findAll(pageable);
            } else {
            	return accessCardRepository.findByAccessCardStatus(status, pageable);
            }
        }
        else {
        	if (status == null) {
        		return accessCardRepository.findByCodeContaining(code, pageable);
            } else {
            	return accessCardRepository.findByCodeContainingAndAccessCardStatus(code, status, pageable);
            }
        }
    }

    public Optional<AccessCard> get(int id) {
        return accessCardRepository.findById(id);
    }

    public Optional<AccessCard> get(String code) {
        return accessCardRepository.getByCode(code);
    }

    public Collection<AccessCard> saveCardList(
            String prefix,
            String suffix,
            String startString,
            String endString,
            Integer loggedUserId,
            AccessCardStatus status) {

        int start = Integer.parseInt(startString);

        int end = Integer.parseInt(endString);

        Collection<String> codeList = new ArrayList<>();

        int pad = startString.length();

        for (int i = start; i <= end; i++) {
            String number = StringUtils.leftPad(String.valueOf(i), pad, "0");

            codeList.add(prefix + number + suffix);
        }

        // Validate existing cards
        Collection<AccessCard> existingCards =
                accessCardRepository.findByCodesInAndStatusIn(codeList, null);

        Collection<String> existingCodes = existingCards.stream().map(AccessCard::getCode).toList();

        if (existingCodes.size() > 0) {
            ValidationException ve =
                    new ValidationException("administration.accesscards.error.existing_cards");

            ve.addError("existing_cards", StringUtils.join(existingCodes, ", "));

            throw ve;
        }

        Collection<AccessCard> cardList =
                codeList.stream()
                        .map(
                                code -> {
                                    AccessCard accessCard = new AccessCard();

                                    accessCard.setCode(code);
                                    accessCard.setAcessCardStatus(status);
                                    accessCard.setCreatedBy(loggedUserId);

                                    return accessCard;
                                })
                        .toList();

        accessCardRepository.saveAll(cardList);

        return cardList;
    }

    public boolean removeCard(int id) {
        Optional<AccessCard> oCard = accessCardRepository.findById(id);

        if (!oCard.isPresent()) {
            throw new ValidationException("administration.accesscards.error.card_not_found");
        }

        AccessCard accessCard = oCard.get();

        if (accessCard.getAccessCardStatus() == AccessCardStatus.CANCELLED) {
            accessCardRepository.delete(accessCard);

            return true;
        }

        accessCard.setAcessCardStatus(AccessCardStatus.CANCELLED);

        accessCardRepository.save(accessCard);

        return true;
    }

    public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
        return false;
    }

    public boolean update(AccessCard accessCard) {
        if (accessCard != null) {
            accessCardRepository.save(accessCard);

            return true;
        }

        return false;
    }
}
