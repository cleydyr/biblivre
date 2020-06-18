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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractBO;
import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import biblivre.core.exceptions.ValidationException;

public class AccessCardBO extends AbstractBO {
	private IAccessCardDAO dao;

	public AccessCardBO(IAccessCardDAO dao) {
		this.dao = dao;
	}

	public DTOCollection<AccessCardDTO> search(String code, AccessCardStatus status, int limit, int offset) {
		return this.dao.search(code, status, limit, offset);
	}

	public AccessCardDTO get(int id) {
		return this.dao.get(id);
	}

	public LinkedList<AccessCardDTO> saveCardList(String prefix, String suffix, int start, int end, Integer loggedUserId, AccessCardStatus status) {
		LinkedList<String> codeList = new LinkedList<String>();

		int padding = Integer.toString(start).length();

		for (int i = start; i <= end; i++) {
			String number = StringUtils.leftPad(
				String.valueOf(i), padding, "0");
			codeList.add(prefix + number + suffix);
		}

		//Validate existing cards
		Collection<AccessCardDTO> existingCards = this.dao.get(codeList);

		if (existingCards.size() > 0) {
			ValidationException ve = new ValidationException("administration.accesscards.error.existing_cards");

			Iterator<String> iterator = existingCards.stream()
				.map(card -> card.getCode())
				.iterator();

			ve.addError("existing_cards", StringUtils.join(iterator, ", "));

			throw ve;
		}

		LinkedList<AccessCardDTO> cardList = new LinkedList<AccessCardDTO>();

		for (String code : codeList) {
			AccessCardDTO dto = dao.create();
			dto.setCode(code);
			dto.setStatus(status);
			dto.setCreatedBy(loggedUserId);
			cardList.add(dto);
		}

		if (this.dao.save(cardList)) {
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
		return this.dao.update(dto);
	}

	public boolean delete(int id) {
		return this.dao.delete(id);
	}

	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		return this.dao.saveFromBiblivre3(dtoList);
	}

	public AccessCardDTO createCard(
		int loggedUserId, String code, AccessCardStatus status) {

		AccessCardDTO dto = dao.create();

		dto.setCode(code);
		dto.setStatus(status);
		dto.setCreatedBy(loggedUserId);

		return dto;
	}
}
