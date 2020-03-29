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
package biblivre.circulation.accesscontrol;

import java.util.Date;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import biblivre.administration.accesscards.AccessCardBO;
import biblivre.administration.accesscards.AccessCardDTO;
import biblivre.administration.accesscards.SearchParameters;
import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractHandler;
import biblivre.core.DTOCollection;
import biblivre.core.ExtendedRequest;
import biblivre.core.ExtendedResponse;
import biblivre.core.enums.ActionResult;

@Component
public class Handler extends AbstractHandler {
	private AccessControlBO _accessControlBO;

	private AccessCardBO _accessCardBO;

	@Autowired
	public Handler(AccessControlBO accessControlBO, AccessCardBO accessCardBO) {
		super();
		_accessControlBO = accessControlBO;
		_accessCardBO = accessCardBO;
	}

	public void userSearch(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();

		_accessCardBO.setSchema(schema);
		_accessControlBO.setSchema(schema);

		biblivre.circulation.user.Handler userHandler = new biblivre.circulation.user.Handler();
		DTOCollection<UserDTO> userList = userHandler.searchHelper(request, response, this);
		
		if (userList == null) {
			return;
		}
		
		DTOCollection<AccessControlDTO> list = new DTOCollection<AccessControlDTO>();
		list.setPaging(userList.getPaging());
		
		for (UserDTO user : userList) {
			AccessControlDTO dto = _accessControlBO.getByUserId(user.getId());
			if (dto == null) {
				dto = new AccessControlDTO();
				dto.setUserId(user.getId());
			}

			dto.setId(user.getId());
			dto.setUser(user);
			
			if (dto.getAccessCardId() != null) {
				dto.setAccessCard(_accessCardBO.get(dto.getAccessCardId()));
			}
			
			list.add(dto);
		}
		
		if (list.size() == 0) {
			this.setMessage(ActionResult.WARNING, "circulation.error.no_users_found");
			return;
		}
		
		try {
			this.json.put("search", list.toJSONObject());
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
		}
	}
	
	public void cardSearch(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();

		_accessCardBO.setSchema(schema);

		_accessControlBO.setSchema(schema);

		SearchParameters searchParameters = SearchParameters.extractSearchParameters(request);

		DTOCollection<AccessCardDTO> cardList = _accessCardBO.search(searchParameters);

		if (cardList.size() == 0) {
			this.setMessage(ActionResult.WARNING, "administration.accesscards.error.no_card_found");
		}
		
		DTOCollection<AccessControlDTO> list = new DTOCollection<AccessControlDTO>();
		list.setPaging(cardList.getPaging());
		
		UserBO ubo = UserBO.getInstance(schema);
		
		for (AccessCardDTO card : cardList) {
			AccessControlDTO dto = _accessControlBO.getByCardId(card.getId());
			if (dto == null) {
				dto = new AccessControlDTO();
				dto.setAccessCardId(card.getId());
			}

			dto.setId(card.getId());
			dto.setAccessCard(card);
			
			if (dto.getUserId() != null) {
				dto.setUser(ubo.get(dto.getUserId()));
			}
			
			list.add(dto);
		}


		if (list.size() == 0) {
			this.setMessage(ActionResult.WARNING, "administration.accesscards.error.no_card_found");
			return;
		}
		
		try {
			this.json.put("search", list.toJSONObject());
		} catch (JSONException e) {
			this.setMessage(ActionResult.WARNING, "error.invalid_json");
		}
	}

	public void bind(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();

		_accessControlBO.setSchema(schema);

		Integer cardId = request.getInteger("card_id");
		Integer userId = request.getInteger("user_id");

		AccessControlDTO dto = new AccessControlDTO();
		dto.setAccessCardId(cardId);
		dto.setUserId(userId);
		dto.setCreatedBy(request.getLoggedUserId());
		dto.setArrivalTime(new Date());
		
		if (_accessControlBO.lendCard(dto)) {
			this.setMessage(ActionResult.SUCCESS, "circulation.accesscards.lend.success");
			try {
				dto = _accessControlBO.getByCardId(cardId);
				_accessControlBO.populateDetails(dto);
				dto.setId(cardId);
				this.json.put("data", dto.toJSONObject());
				this.json.put("full_data", true);
			} catch (JSONException e) {
				this.setMessage(ActionResult.WARNING, "error.invalid_json");
				return;
			}
		} else {
			this.setMessage(ActionResult.WARNING, "circulation.accesscards.lend.error");
		}
	}
	
	public void unbind(ExtendedRequest request, ExtendedResponse response) {
		String schema = request.getSchema();

		_accessControlBO.setSchema(schema);

		Integer cardId = request.getInteger("card_id");
		Integer userId = request.getInteger("user_id");

		AccessControlDTO dto = new AccessControlDTO();
		dto.setAccessCardId(cardId);
		dto.setUserId(userId);
		dto.setModifiedBy(request.getLoggedUserId());
		dto.setDepartureTime(new Date());
		
		if (_accessControlBO.returnCard(dto)) {
			this.setMessage(ActionResult.SUCCESS, "circulation.accesscards.return.success");
			try {
				_accessControlBO.populateDetails(dto);
				dto.setId(cardId);
				this.json.put("data", dto.toJSONObject());
				this.json.put("full_data", true);
			} catch (JSONException e) {
				this.setMessage(ActionResult.WARNING, "error.invalid_json");
				return;
			}
		} else {
			this.setMessage(ActionResult.WARNING, "circulation.accesscards.return.error");
		}
	}	
}
