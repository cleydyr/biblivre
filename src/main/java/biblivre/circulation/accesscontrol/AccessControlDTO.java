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

import biblivre.administration.accesscards.AccessCardDTO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractDTO;
import java.io.Serial;
import java.util.Date;

public class AccessControlDTO extends AbstractDTO {
    @Serial private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer accessCardId;
    private Integer userId;
    private Date arrivalTime;
    private Date departureTime;

    private transient UserDTO user;
    private transient AccessCardDTO accessCard;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAccessCardId() {
        return this.accessCardId;
    }

    public void setAccessCardId(Integer accessCardId) {
        this.accessCardId = accessCardId;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getArrivalTime() {
        return this.arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Date getDepartureTime() {
        return this.departureTime;
    }

    public void setDepartureTime(Date departureTime) {
        this.departureTime = departureTime;
    }

    public UserDTO getUser() {
        return this.user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public AccessCardDTO getAccessCard() {
        return this.accessCard;
    }

    public void setAccessCard(AccessCardDTO accessCard) {
        this.accessCard = accessCard;
    }
}
