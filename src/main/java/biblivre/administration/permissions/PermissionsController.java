package biblivre.administration.permissions;

import java.util.Collection;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.circulation.user.UserSearchDTO;
import biblivre.core.DTOCollection;
import biblivre.core.configurations.Configurations;
import biblivre.core.utils.Constants;
import biblivre.login.LoginBO;
import biblivre.login.LoginDTO;

@RestController
@RequestMapping("/administration.permissions")
public class PermissionsController {
	private PermissionBOFactory _permissionBOFactory;

	@Autowired
	public void setPermissionBOFactory(PermissionBOFactory permissionBOFactory) {
		_permissionBOFactory = permissionBOFactory;
	}

	@PostMapping(
			path = "database/{database}/item_count",
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	public String search(@RequestParam String schema,
			@RequestParam(name = "search_parameters") String searchParameters,
			@RequestParam(required = false, defaultValue = "0") int limit,
			@RequestParam(required = false, defaultValue = "0") int offset,
			@RequestParam(required = false, defaultValue = "1") int page) {
		UserSearchDTO searchDto = new UserSearchDTO(searchParameters);

		limit = limit != 0 ? limit :
			Configurations.getInt(schema, Constants.CONFIG_SEARCH_RESULTS_PER_PAGE);

		if (page > 1) {
			offset = limit * (page - 1);
		}
		
		UserBO bo = UserBO.getInstance(schema);

		DTOCollection<UserDTO> users = bo.search(searchDto, limit, offset);

		if (users.size() == 0) {
//			handler.setMessage(ActionResult.WARNING, "circulation.error.no_users_found");
		}
		
		if (users == null || users.size() == 0) {
//			this.setMessage(ActionResult.WARNING, "circulation.error.no_users_found");
//			return;
		}

		DTOCollection<PermissionDTO> permissions = new DTOCollection<PermissionDTO>();

		permissions.setPaging(users.getPaging());

		for (UserDTO user : users) {
			permissions.add(this.populatePermission(schema, user));
		}
		
//		try {
//			this.json.put("search", permissions.toJSONObject());
//		} catch (JSONException e) {
//			this.setMessage(ActionResult.WARNING, "error.invalid_json");
//		}

		JSONObject result = new JSONObject();

		result.put("search", permissions.toJSONObject());

		return result.toString();
	}
	
	
//	public void open(ExtendedRequest request, ExtendedResponse response) {
//		String schema = request.getSchema();
//		int userId = request.getInteger("user_id");
//		if (userId == 0) {
//			this.setMessage(ActionResult.WARNING, "error.invalid_user");
//			return;
//		}
//
//		UserBO ubo = UserBO.getInstance(schema);
//		LoginBO lbo = LoginBO.getInstance(schema);
//		
//		UserDTO udto = ubo.get(userId);
//		if (udto == null) {
//			this.setMessage(ActionResult.WARNING, "error.invalid_user");
//			return;
//		}
//
//		int loginId = udto.getLoginId();
//
//		PermissionDTO dto = new PermissionDTO();
//		dto.setUser(udto);
//
//		if (loginId > 0) {
//			dto.setLogin(lbo.get(loginId));
//			PermissionBO bo = permissionBOFactory.getInstance(schema);
//			Collection<String> list = bo.getPermissionsByLoginId(loginId);
//			if (list != null) {
//				dto.setPermissions(list);
//			}
//		}
//		try {
//			this.json.put("permission", dto.toJSONObject());
//		} catch (JSONException e) {
//			this.setMessage(ActionResult.WARNING, "error.invalid_json");
//			return;
//		}
//	}
//	
//	public void save(ExtendedRequest request, ExtendedResponse response) {
//		String schema = request.getSchema();
//		int userId = request.getInteger("user_id");
//		if (userId == 0) {
//			this.setMessage(ActionResult.WARNING, "error.invalid_user");
//			return;
//		}
//
//		UserBO ubo = UserBO.getInstance(schema);
//		UserDTO udto = ubo.get(userId);
//
//		if (udto == null) {
//			this.setMessage(ActionResult.WARNING, "error.invalid_user");
//			return;
//		}
//
//		String login = request.getString("new_login");
//		String password = request.getString("new_password");
//		boolean employee = request.getBoolean("employee", false);
//
//		Integer loginId = udto.getLoginId();
//		boolean newLogin = (loginId == null || loginId == 0);
//
//		PermissionBO pbo = permissionBOFactory.getInstance(schema);
//		LoginBO lbo = LoginBO.getInstance(schema);
//		
//		LoginDTO ldto = new LoginDTO();
//		ldto.setLogin(login);
//		ldto.setEmployee(employee);
//		
//		if (StringUtils.isNotBlank(password)) {
//			ldto.setEncPassword(TextUtils.encodePassword(password));
//		}
//		
//		boolean result = true;
//		
//		if (newLogin) {
//			ldto.setCreatedBy(request.getLoggedUserId());
//			result = lbo.save(ldto, udto);
//		} else {
//			ldto.setId(udto.getLoginId());
//			ldto.setModifiedBy(request.getLoggedUserId());
//			result = lbo.update(ldto);
//		}
//
//		String[] permissions = request.getParameterValues("permissions[]");
//		
//		if (permissions != null) {
//			result &= pbo.saveAll(udto.getLoginId(), Arrays.asList(permissions));
//		}
//		 
//		if (result) {
//			if (newLogin) {
//				this.setMessage(ActionResult.SUCCESS, "administration.permission.success.create_login");
//			} else if (!password.isEmpty()) {
//				this.setMessage(ActionResult.SUCCESS, "administration.permission.success.password_saved");
//			} else {
//				this.setMessage(ActionResult.SUCCESS, "administration.permission.success.permissions_saved");
//			}
//		} else {
//			this.setMessage(ActionResult.WARNING, "administration.permission.error.create_login");
//		}
//		
//		
//		PermissionDTO dto = this.populatePermission(schema, udto);
//		
//		try {
//			this.json.put("data", dto.toJSONObject());
//			this.json.put("full_data", true);
//		} catch (JSONException e) {
//			this.setMessage(ActionResult.WARNING, "error.invalid_json");
//			return;
//		}
//	}
//	
//	public void delete(ExtendedRequest request, ExtendedResponse response) {
//		String schema = request.getSchema();
//		int userId = request.getInteger("user_id");
//		if (userId == 0) {
//			this.setMessage(ActionResult.WARNING, "error.invalid_user");
//			return;
//		}
//
//		UserBO ubo = UserBO.getInstance(schema);
//		UserDTO udto = ubo.get(userId);
//
//		if (udto == null) {
//			this.setMessage(ActionResult.WARNING, "error.invalid_user");
//			return;
//		}
//
//		//WE DELETE THE LOGIN RECORD, AND THE PERMISSIONS WILL ALSO BE DELETED
//		//BY THE LOGIN_BO
//		LoginBO lbo = LoginBO.getInstance(schema);
//
//		if (lbo.delete(udto)) {
//			this.setMessage(ActionResult.SUCCESS, "administration.permission.success.delete");
//		} else {
//			this.setMessage(ActionResult.WARNING, "administration.permission.error.delete");
//		}
//	}
//	
	private PermissionDTO populatePermission(String schema, UserDTO user) {
		PermissionBO pbo = _permissionBOFactory.getInstance(schema);

		LoginBO lbo = LoginBO.getInstance(schema);
		
		PermissionDTO dto = new PermissionDTO();
		dto.setUser(user);
		
		if (user.getLoginId() == null || user.getLoginId() == 0) {
			return dto;
		}
		
		LoginDTO ldto = lbo.get(user.getLoginId());
		
		if (ldto == null) {
			return dto;
		}
		
		dto.setLogin(ldto);
		
		Collection<String> permissions = pbo.getPermissionsByLoginId(user.getLoginId());
		
		if (permissions == null) {
			return dto;
		}
		
		dto.setPermissions(permissions);
		
		return dto;
	}
}
