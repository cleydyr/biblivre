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
package biblivre.z3950;

import biblivre.cataloging.bibliographic.BiblioRecordDTO;
import biblivre.core.AbstractBO;
import biblivre.core.AbstractDTO;
import biblivre.core.DTOCollection;
import biblivre.core.configurations.Configurations;
import biblivre.core.utils.Constants;
import biblivre.z3950.client.Z3950Client;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.marc4j.marc.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Z3950BO extends AbstractBO {
    @Autowired Z3950Client z3950Client;

    private Z3950DAO z3950DAO;

    public List<Z3950RecordDTO> search(List<Z3950AddressDTO> servers, Pair<String, String> search) {
        List<Z3950RecordDTO> dtoList = new ArrayList<>();

        int limit = Configurations.getInt(Constants.CONFIG_Z3950_RESULT_LIMIT, 100);

        for (Z3950AddressDTO searchServer : servers) {
            try {
                List<Record> recordList = z3950Client.search(searchServer, search, limit);

                if (CollectionUtils.isNotEmpty(recordList)) {
                    for (Record record : recordList) {
                        Z3950RecordDTO recordDto = new Z3950RecordDTO();
                        recordDto.setServer(searchServer);
                        BiblioRecordDTO dto = new BiblioRecordDTO();
                        dto.setRecord(record);
                        recordDto.setRecord(dto);
                        dtoList.add(recordDto);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return dtoList;
    }

    public DTOCollection<Z3950AddressDTO> search(String value, int limit, int offset) {
        return this.z3950DAO.search(value, limit, offset);
    }

    public List<Z3950AddressDTO> listAll() {
        return this.z3950DAO.listAll();
    }

    public DTOCollection<Z3950AddressDTO> listServers() {
        DTOCollection<Z3950AddressDTO> servers = new DTOCollection<>();

        servers.addAll(this.z3950DAO.listAll());
        return servers;
    }

    public boolean save(Z3950AddressDTO dto) {
        if (dto == null) {
            return false;
        }

        if (dto.getId() == 0) {
            return this.z3950DAO.insert(dto);
        } else {
            return this.z3950DAO.update(dto);
        }
    }

    public boolean delete(Z3950AddressDTO dto) {
        return this.z3950DAO.delete(dto);
    }

    public Z3950AddressDTO findById(int id) {
        List<Integer> ids = List.of(id);

        List<Z3950AddressDTO> list = this.z3950DAO.list(ids);

        return (list.size() > 0) ? list.get(0) : null;
    }

    public List<Z3950AddressDTO> list(List<Integer> ids) {
        return this.z3950DAO.list(ids);
    }

    public boolean saveFromBiblivre3(List<? extends AbstractDTO> dtoList) {
        return this.z3950DAO.saveFromBiblivre3(dtoList);
    }

    protected static final Logger logger = LoggerFactory.getLogger(Z3950BO.class);

    @Autowired
    public void setZ3950DAO(Z3950DAO z3950dao) {
        z3950DAO = z3950dao;
    }
}
