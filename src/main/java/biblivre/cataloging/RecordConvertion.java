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
package biblivre.cataloging;

import biblivre.marc.HumanReadableMarcReader;
import biblivre.marc.JsonMarcReader;
import biblivre.marc.MaterialType;
import biblivre.marc.RecordStatus;
import java.lang.reflect.Constructor;
import org.marc4j.MarcReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum RecordConvertion {
    MARC(HumanReadableMarcReader.class),
    RECORD(HumanReadableMarcReader.class),
    HOLDING_MARC(HumanReadableMarcReader.class),

    FORM(JsonMarcReader.class),
    HOLDING_FORM(JsonMarcReader.class);

    private final Class<? extends MarcReader> readerClass;

    private static final Logger logger = LoggerFactory.getLogger(RecordConvertion.class);

    RecordConvertion(Class<? extends MarcReader> readerClass) {
        this.readerClass = readerClass;
    }

    public MarcReader getReader(String data, MaterialType materialType, RecordStatus recordStatus) {
        Constructor<? extends MarcReader> constructor;

        try {
            constructor =
                    this.readerClass.getConstructor(
                            String.class, MaterialType.class, RecordStatus.class);

            return constructor.newInstance(data, materialType, recordStatus);
        } catch (Exception e) {
            logger.error("error while creating instance of reader", e);
        }

        return null;
    }
}
