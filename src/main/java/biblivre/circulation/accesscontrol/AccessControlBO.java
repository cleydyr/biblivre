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

import biblivre.administration.accesscards.AccessCardBO;
import biblivre.administration.accesscards.AccessCardDTO;
import biblivre.administration.accesscards.AccessCardStatus;
import biblivre.circulation.user.UserBO;
import biblivre.circulation.user.UserDTO;
import biblivre.core.AbstractBO;
import biblivre.core.exceptions.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccessControlBO extends AbstractBO {
    private AccessControlDAO accessControlDAO;
    private AccessCardBO accessCardBO;
    private UserBO userBO;

    public void populateDetails(AccessControlDTO dto) {
        if (dto == null) {
            return;
        }

        if (dto.getAccessCardId() != null) {
            dto.setAccessCard(accessCardBO.get(dto.getAccessCardId()));
        }

        if (dto.getUserId() != null) {
            dto.setUser(userBO.get(dto.getUserId()));
        }
    }

    public boolean lendCard(AccessControlDTO dto) {

        UserDTO udto = null;
        try {
            udto = userBO.get(dto.getUserId());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if (udto == null) {
            throw new ValidationException("circulation.error.user_not_found");
        }

        AccessCardDTO cardDto = accessCardBO.get(dto.getAccessCardId());
        if (cardDto == null) {
            throw new ValidationException("circulation.access_control.card_not_found");
        } else if (!cardDto.getStatus().equals(AccessCardStatus.AVAILABLE)) {
            throw new ValidationException("circulation.access_control.card_unavailable");
        }

        AccessControlDTO existingAccess = this.getByCardId(dto.getAccessCardId());
        if (existingAccess != null) {
            throw new ValidationException("circulation.access_control.card_in_use");
        }
        existingAccess = this.getByUserId(dto.getUserId());
        if (existingAccess != null) {
            throw new ValidationException("circulation.access_control.user_has_card");
        }

        try {
            cardDto.setStatus(AccessCardStatus.IN_USE);
            accessCardBO.update(cardDto);
            return this.accessControlDAO.save(dto);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    public boolean returnCard(AccessControlDTO dto) {

        UserDTO udto = null;
        try {
            udto = userBO.get(dto.getUserId());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if (udto == null) {
            throw new ValidationException("circulation.error.user_not_found");
        }

        AccessControlDTO existingAccess = null;

        if (dto.getAccessCardId() != 0) {
            AccessCardDTO cardDto = accessCardBO.get(dto.getAccessCardId());
            if (cardDto == null) {
                throw new ValidationException("circulation.access_control.card_not_found");
            } else if (cardDto.getStatus().equals(AccessCardStatus.AVAILABLE)) {
                throw new ValidationException("circulation.access_control.card_available");
            }

            existingAccess = this.getByCardId(dto.getAccessCardId());
            if (existingAccess == null) {
                existingAccess = this.getByUserId(dto.getUserId());
                if (existingAccess == null) {
                    throw new ValidationException("circulation.access_control.user_has_no_card");
                }
            }
        }

        try {
            if (existingAccess != null) {
                AccessCardDTO cardDto = accessCardBO.get(existingAccess.getAccessCardId());
                // If the cardId was sent in the parameters, it means that the user has returned it.
                // Else, it means that the user left the library without returning the card, so we
                // have to block it.
                cardDto.setStatus(
                        dto.getAccessCardId() != 0
                                ? AccessCardStatus.AVAILABLE
                                : AccessCardStatus.IN_USE_AND_BLOCKED);
                accessCardBO.update(cardDto);
                return this.accessControlDAO.update(existingAccess);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    public boolean update(AccessControlDTO dto) {
        return this.accessControlDAO.update(dto);
    }

    public AccessControlDTO getByCardId(Integer cardId) {
        return this.accessControlDAO.getByCardId(cardId);
    }

    public AccessControlDTO getByUserId(Integer userId) {
        return this.accessControlDAO.getByUserId(userId);
    }

    protected static final Logger logger = LoggerFactory.getLogger(AccessControlBO.class);

    @Autowired
    public void setAccessControlDAO(AccessControlDAO accessControlDAO) {
        this.accessControlDAO = accessControlDAO;
    }

    @Autowired
    public void setAccessCardBO(AccessCardBO accessCardBO) {
        this.accessCardBO = accessCardBO;
    }

    @Autowired
    public void setUserBO(UserBO userBO) {
        this.userBO = userBO;
    }
}
