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

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import biblivre.core.AbstractBO;
import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import biblivre.core.exceptions.ValidationException;

@Service
public class AccessCardBO extends AbstractBO {
	private AccessCardPersistence _accessCardPersistence;

	@Autowired
	public AccessCardBO(AccessCardPersistence accessCardPersistence) {
		super();
		_accessCardPersistence = accessCardPersistence;
	}

	public boolean save(AccessCardDTO dto) {
		if (dto != null) {
			AccessCardDTO existingCard =
				this._accessCardPersistence.get(dto.getCode());

			if (existingCard != null) {
				throw new ValidationException(
					"administration.accesscards.error.existing_card");
			}

			return this._accessCardPersistence.save(dto);
		}
		
		return false;
	}

	public DTOCollection<AccessCardDTO> search(
			AccessCardSearchParameters parameterObject) {

		return this._accessCardPersistence.search(
			parameterObject.code, parameterObject.status, parameterObject.limit,
			parameterObject.offset);
	}

	public AccessCardDTO get(int id) {
		return this._accessCardPersistence.get(id);
	}

	public AccessCardDTO get(String code) {
		return this._accessCardPersistence.get(code);
	}
	
	public LinkedList<AccessCardDTO> saveCardList(
			String prefix, String suffix, String startString, String endString,
			Integer loggedUserId, AccessCardStatus status) {
		
		LinkedList<String> codeList = new LinkedList<String>();

		int pad = startString.length();

		int start = Integer.parseInt(startString);

		int end = Integer.parseInt(endString);

		for (int i = start; i <= end; i++) {
			String number = StringUtils.leftPad(String.valueOf(i), pad, "0");

			codeList.add(prefix + number + suffix);
		}
		
		_validateExistingCards(codeList);
		
		LinkedList<AccessCardDTO> cardList = new LinkedList<AccessCardDTO>();

		for (String code : codeList) {
			AccessCardDTO dto = new AccessCardDTO();

			dto.setCode(code);

			dto.setStatus(status);

			dto.setCreatedBy(loggedUserId);

			cardList.add(dto);
		}
		
		if (this._accessCardPersistence.save(cardList)) {
			return cardList;
		} else {
			return null;
		}
	}

	private void _validateExistingCards(LinkedList<String> codeList) {
		List<AccessCardDTO> existingCards =
			this._accessCardPersistence.get(codeList, null);

		List<String> existingCodes = new LinkedList<String>();

		for (AccessCardDTO card : existingCards) {
			existingCodes.add(card.getCode());
		}

		if (existingCodes.size() > 0) {
			ValidationException ve =
				new ValidationException(
					"administration.accesscards.error.existing_cards");

			ve.addError(
				"existing_cards", StringUtils.join(existingCodes, ", "));

			throw ve;
		}
	}

	public boolean removeCard(AccessCardDTO dto) {
		if (dto != null) {
			AccessCardDTO card = this.get(dto.getId());

			if (card == null) {
				throw new ValidationException(
					"administration.accesscards.error.card_not_found");
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
		return this._accessCardPersistence.update(dto);
	}
	
	public boolean delete(int id) {
		return this._accessCardPersistence.delete(id);
	}
	
	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		return this._accessCardPersistence.saveFromBiblivre3(dtoList);
	}

	@Override
	public void setSchema(String schema) {
		_accessCardPersistence.setSchema(schema);
	}
}
