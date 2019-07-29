package biblivre.core;

/**
 * Interface funcional implementada como callback de requisições que necessitam de ações adicionais
 * após completadas.
 * 
 * @author Cleydyr B. de Albuquerque
 *
 */
@FunctionalInterface
public interface HttpCallback {
	public void success();
}