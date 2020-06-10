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
package biblivre.acquisition.supplier;

import java.sql.ResultSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import biblivre.core.AbstractDAO;
import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import biblivre.core.PreparedStatementUtil;

public class SupplierDAO extends AbstractDAO {

	private static final String _SEARCH_ALL_SQL =
		"SELECT * FROM suppliers ORDER BY id ASC LIMIT ? OFFSET ?";

	private static final String _SEARCH_KEYWORD_SQL =
		"SELECT * FROM suppliers " +
		"WHERE trademark ilike ? OR supplier_name ilike ? " +
			"OR supplier_number = ? " +
		"ORDER BY id ASC LIMIT ? OFFSET ?";

	private static final String _GET_SQL =
		"SELECT * FROM suppliers WHERE id = ?";

	private static final String _DELETE_SQL =
		"DELETE FROM suppliers WHERE id = ?";

	private static final String _UPDATE_SQL =
		"UPDATE suppliers " +
		"SET trademark = ?, supplier_name = ?, supplier_number = ?, " +
			"vat_registration_number = ?, address = ?, address_number = ?, " +
			"address_complement = ?, area = ?, city = ?, state = ?, " +
			"country = ?, zip_code = ?, telephone_1 = ?, telephone_2 = ?, " +
			"telephone_3 = ?, telephone_4 = ?, contact_1 = ?, contact_2 = ?, " +
			"contact_3 = ?, contact_4 = ?, info = ?, url = ?, email = ?, " +
			"modified = now(), modified_by = ? " +
		"WHERE id = ?";

	private static final String _SAVE_SQL =
		"INSERT INTO suppliers" +
			"(trademark, supplier_name, supplier_number, " +
			"vat_registration_number, address, address_number, " +
			"address_complement, area, city, state, country, zip_code, " +
			"telephone_1, telephone_2, telephone_3, telephone_4, contact_1, " +
			"contact_2, contact_3, contact_4, info, url, email, created_by) " +
		"VALUES (" + StringUtils.repeat("?", ", ", 24) + ")";

	private static final String _SAVE_FROM_V3_SQL =
		"INSERT INTO suppliers" +
			"(trademark, supplier_name, supplier_number, " +
			"vat_registration_number, address, address_number, " +
			"address_complement, area, city, state, country, zip_code, " +
			"telephone_1, telephone_2, telephone_3, telephone_4, contact_1, " +
			"contact_2, contact_3, contact_4, info, url, email, created_by, " +
			"id) " +
		"VALUES (" + StringUtils.repeat("?", ", ", 25) + ")";

	public static SupplierDAO getInstance(String schema) {
		return (SupplierDAO) AbstractDAO.getInstance(SupplierDAO.class, schema);
	}

	public boolean save(SupplierDTO dto) {
		return executeUpdate(
			_SAVE_SQL, dto.getTrademark(), dto.getName(),
			dto.getSupplierNumber(), dto.getVatRegistrationNumber(),
			dto.getAddress(), dto.getAddressNumber(), dto.getComplement(),
			dto.getArea(), dto.getCity(), dto.getState(), dto.getCountry(),
			dto.getZipCode(), dto.getTelephone1(), dto.getTelephone2(),
			dto.getTelephone3(), dto.getTelephone4(), dto.getContact1(),
			dto.getContact2(), dto.getContact3(), dto.getContact4(),
			dto.getInfo(), dto.getUrl(), dto.getEmail(), dto.getCreatedBy());
	}

	public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
		return executeBatchUpdate((pst, abstractDto) -> {
			SupplierDTO dto = (SupplierDTO) abstractDto;

			PreparedStatementUtil.setAllParameters(
				pst, dto.getTrademark(), dto.getName(),
				dto.getSupplierNumber(), dto.getVatRegistrationNumber(),
				dto.getAddress(), dto.getAddressNumber(),
				dto.getComplement(), dto.getArea(), dto.getCity(),
				dto.getState(), dto.getCountry(), dto.getZipCode(),
				dto.getTelephone1(), dto.getTelephone2(),
				dto.getTelephone3(), dto.getTelephone4(),
				dto.getContact1(), dto.getContact2(), dto.getContact3(),
				dto.getContact4(), dto.getInfo(), dto.getUrl(),
				dto.getEmail(), dto.getCreatedBy(), dto.getId());

		}, dtoList, _SAVE_FROM_V3_SQL);
	}

	public boolean update(SupplierDTO dto) {
		return executeUpdate(
			_UPDATE_SQL, dto.getTrademark(), dto.getName(),
			dto.getSupplierNumber(), dto.getVatRegistrationNumber(),
			dto.getAddress(), dto.getAddressNumber(), dto.getComplement(),
			dto.getArea(), dto.getCity(), dto.getState(), dto.getCountry(),
			dto.getZipCode(), dto.getTelephone1(), dto.getTelephone2(),
			dto.getTelephone3(), dto.getTelephone4(), dto.getContact1(),
			dto.getContact2(), dto.getContact3(), dto.getContact4(),
			dto.getInfo(), dto.getUrl(), dto.getEmail(), dto.getCreatedBy(),
			dto.getId());
	}

	public boolean delete(SupplierDTO dto) {
		return executeUpdate(_DELETE_SQL, dto.getId());
	}

	public SupplierDTO get(int id) {
		return fetchOne(this::populateDto, _GET_SQL, id);
	}

	public DTOCollection<SupplierDTO> search(
		String value, int limit, int offset) {

		if (StringUtils.isNotBlank(value)) {
			String likeValue = "%" + value + "%";

			return pagedListWith(
				this::populateDto, _SEARCH_KEYWORD_SQL, limit, offset,
				likeValue, likeValue, value);
		}
		else {
			return pagedListWith(
				this::populateDto, _SEARCH_ALL_SQL, limit, offset);
		}
	}

	private SupplierDTO populateDto(ResultSet rs) throws Exception {
		SupplierDTO dto = new SupplierDTO();
		dto.setId(rs.getInt("id"));
		dto.setTrademark(rs.getString("trademark"));
		dto.setName(rs.getString("supplier_name"));
		dto.setSupplierNumber(rs.getString("supplier_number"));
		dto.setVatRegistrationNumber(rs.getString("vat_registration_number"));
		dto.setAddress(rs.getString("address"));
		dto.setAddressNumber(rs.getString("address_number"));
		dto.setComplement(rs.getString("address_complement"));
		dto.setArea(rs.getString("area"));
		dto.setCity(rs.getString("city"));
		dto.setState(rs.getString("state"));
		dto.setCountry(rs.getString("country"));
		dto.setZipCode(rs.getString("zip_code"));
		dto.setTelephone1(rs.getString("telephone_1"));
		dto.setTelephone2(rs.getString("telephone_2"));
		dto.setTelephone3(rs.getString("telephone_3"));
		dto.setTelephone4(rs.getString("telephone_4"));
		dto.setContact1(rs.getString("contact_1"));
		dto.setContact2(rs.getString("contact_2"));
		dto.setContact3(rs.getString("contact_3"));
		dto.setContact4(rs.getString("contact_4"));
		dto.setInfo(rs.getString("info"));
		dto.setUrl(rs.getString("url"));
		dto.setEmail(rs.getString("email"));
		dto.setCreated(rs.getTimestamp("created"));
		dto.setCreatedBy(rs.getInt("created_by"));
		dto.setModified(rs.getTimestamp("modified"));
		dto.setModifiedBy(rs.getInt("modified_by"));
		return dto;
	}

}
