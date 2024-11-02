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
package biblivre.core;

import biblivre.core.utils.Constants;
import java.io.BufferedWriter;
import java.io.File;
import org.apache.commons.io.output.FileWriterWithEncoding;

public class JavascriptCache {

    private File cacheFile;
    private final IFCacheableJavascript parent;

    public JavascriptCache(IFCacheableJavascript parent) {
        this.parent = parent;
    }

    private void createCacheFile() {
        try {
            this.cacheFile =
                    File.createTempFile(
                            this.parent.getCacheFileNamePrefix() + ".",
                            this.parent.getCacheFileNameSuffix());
            this.cacheFile.deleteOnExit();

            try (BufferedWriter out =
                    new BufferedWriter(
                            FileWriterWithEncoding.builder()
                                    .setCharset(Constants.DEFAULT_CHARSET)
                                    .setFile(this.cacheFile)
                                    .get())) {
                out.write(this.parent.toJavascriptString());
            }

        } catch (Exception e) {
            this.cacheFile = null;
        }
    }

    private boolean validateCacheFile() {
        if (this.cacheFile == null || !this.cacheFile.exists() || this.cacheFile.length() == 0) {
            this.createCacheFile();
        }

        return this.cacheFile != null;
    }

    public String getFileName() {
        if (this.validateCacheFile()) {
            return this.cacheFile.getName();
        } else {
            return this.parent.getCacheFileNamePrefix() + this.parent.getCacheFileNameSuffix();
        }
    }

    public File getCacheFile() {
        this.validateCacheFile();
        return this.cacheFile;
    }
}
