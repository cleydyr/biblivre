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

package biblivre;

import static biblivre.AbstractContainerDatabaseTest.postgreSQLContainer;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestDatasourceConfiguration {
    @Bean
    @Primary
    public DataSource datasource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(postgreSQLContainer.getJdbcUrl());
        config.setUsername(postgreSQLContainer.getUsername());
        config.setPassword(postgreSQLContainer.getPassword());
        config.setDriverClassName(postgreSQLContainer.getDriverClassName());
        return new SchemaAwareDatasourceDecorator(new HikariDataSource(config));
    }
}
