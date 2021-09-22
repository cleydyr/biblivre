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
package biblivre.digitalmedia;

import biblivre.core.AbstractBO;
import biblivre.core.file.BiblivreFile;
import biblivre.core.file.MemoryFile;

public class DigitalMediaBO extends AbstractBO {
    protected DigitalMediaDAO digitalMediaDAO;

    public static DigitalMediaBO getInstance() {
        DigitalMediaBO bo = AbstractBO.getInstance(DigitalMediaBO.class);

        if (bo.digitalMediaDAO == null) {
            bo.digitalMediaDAO = DigitalMediaDAO.getInstance();
        }

        return bo;
    }

    public Integer save(MemoryFile file) {
        return this.digitalMediaDAO.save(file);
    }

    public BiblivreFile load(int id, String name) {
        return this.digitalMediaDAO.load(id, name);
    }

    public boolean delete(Integer fileId, String fileName) {
        return this.digitalMediaDAO.delete(fileId);
    }
}
