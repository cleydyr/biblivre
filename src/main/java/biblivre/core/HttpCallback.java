package biblivre.core;

import java.io.IOException;

/**
 * Interface funcional implementada como callback de requisições que necessitam de ações adicionais
 * após completadas.
 *
 * @author Cleydyr B. de Albuquerque
 */
@FunctionalInterface
public interface HttpCallback {
    void success() throws IOException;
}
