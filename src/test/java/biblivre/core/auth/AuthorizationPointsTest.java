/*
 * ******************************************************************************
 *  * Este arquivo é parte do Biblivre5.
 *  *
 *  * Biblivre5 é um software livre; você pode redistribuí-lo e/ou
 *  * modificá-lo dentro dos termos da Licença Pública Geral GNU como
 *  * publicada pela Fundação do Software Livre (FSF); na versão 3 da
 *  * Licença, ou (caso queira) qualquer versão posterior.
 *  *
 *  * Este programa é distribuído na esperança de que possa ser  útil,
 *  * mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 *  * MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 *  * Licença Pública Geral GNU para maiores detalhes.
 *  *
 *  * Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 *  * com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 *  *
 *  * @author Cleydyr de Albuquerque <cleydyr@biblivre.cloud>
 *  *****************************************************************************
 */

package biblivre.core.auth;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AuthorizationPointsTest {

    @Test
    void getNotLoggedInstance() {
        AuthorizationPoints authorizationPoints = AuthorizationPoints.getNotLoggedInstance();

        assertNotNull(authorizationPoints);
    }
}
