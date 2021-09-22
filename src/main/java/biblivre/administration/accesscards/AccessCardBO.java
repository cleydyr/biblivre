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
import biblivre.core.DTOCollection;
import biblivre.core.exceptions.ValidationException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class AccessCardBO {
    private AccessCardDAO accessCardDAO;

    public boolean save(AccessCardDTO dto) {
        if (dto != null) {
            AccessCardDTO existingCard = this.accessCardDAO.get(dto.getCode());
            if (existingCard != null) {
                throw new ValidationException("administration.accesscards.error.existing_card");
            }
            return this.accessCardDAO.save(dto);
        }

        return false;
    }

    public DTOCollection<AccessCardDTO> search(
            String code, AccessCardStatus status, int limit, int offset) {
        return this.accessCardDAO.search(code, status, limit, offset);
    }

    public AccessCardDTO get(int id) {
        return this.accessCardDAO.get(id);
    }

    public AccessCardDTO get(String code) {
        return this.accessCardDAO.get(code);
    }

    public List<AccessCardDTO> saveCardList(
            String prefix,
            String suffix,
            String startString,
            String endString,
            Integer loggedUserId,
            AccessCardStatus status) {
        int start = Integer.parseInt(startString);
        int end = Integer.parseInt(endString);

        ArrayList<String> codeList = new ArrayList<>();
        int pad = startString.length();
        for (int i = start; i <= end; i++) {
            String number = StringUtils.leftPad(String.valueOf(i), pad, "0");
            codeList.add(prefix + number + suffix);
        }

        // Validate existing cards
        List<AccessCardDTO> existingCards = this.accessCardDAO.get(codeList, null);
        List<String> existingCodes = new ArrayList<>();
        for (AccessCardDTO card : existingCards) {
            existingCodes.add(card.getCode());
        }
        if (existingCodes.size() > 0) {
            ValidationException ve =
                    new ValidationException("administration.accesscards.error.existing_cards");
            ve.addError("existing_cards", StringUtils.join(existingCodes, ", "));
            throw ve;
        }

        ArrayList<AccessCardDTO> cardList = new ArrayList<>();
        for (String code : codeList) {
            AccessCardDTO dto = new AccessCardDTO();
            dto.setCode(code);
            dto.setStatus(status);
            dto.setCreatedBy(loggedUserId);
            cardList.add(dto);
        }

        if (this.accessCardDAO.save(cardList)) {
            return cardList;
        } else {
            return null;
        }
    }

    public boolean removeCard(AccessCardDTO dto) {
        if (dto != null) {
            AccessCardDTO card = this.get(dto.getId());
            if (card == null) {
                throw new ValidationException("administration.accesscards.error.card_not_found");
            }

            if (card.getStatus() == AccessCardStatus.CANCELLED) {
                return this.delete(card.getId());
            }

            dto.setStatus(AccessCardStatus.CANCELLED);
            return this.update(dto);
        }
        return false;
    }

    public boolean update(AccessCardDTO dto) {
        return this.accessCardDAO.update(dto);
    }

    public boolean delete(int id) {
        return this.accessCardDAO.delete(id);
    }

    public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
        return this.accessCardDAO.saveFromBiblivre3(dtoList);
    }

    public void setAccessCardDAO(AccessCardDAO accessCardDAO) {
        this.accessCardDAO = accessCardDAO;
    }
}
