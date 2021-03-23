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
package biblivre.marc.material;

public enum MaterialType {
    BOOK('a', "m ", true),
    PAMPHLET('a', "m ", true),
    LEGAL_STANCE('a', "m ", true),
    MANUSCRIPT('t', "m ", true),
    THESIS('a', "m ", true),
    PERIODIC('a', "s ", true),
    ARTICLES('a', "b ", true),
    COMPUTER_LEGIBLE('m', "m ", true),
    MAP('e', "m ", true),
    PHOTO('k', "m ", true),
    MOVIE('p', "m ", true),
    SCORE('c', "m ", true),
    MUSIC('j', "m ", true),
    NONMUSICAL_SOUND('i', "m ", true),
    OBJECT_3D('r', "m ", true),
    AUTHORITIES('z', "  ", false),
    VOCABULARY('w', "  ", false),
    HOLDINGS('u', "  ", false);

    private char typeOfRecord;
    private String implDefined1;
    private boolean searchable;

    private MaterialType(char typeOfRecord, String implDef1, boolean searchable) {
        this.typeOfRecord = typeOfRecord;
        this.implDefined1 = implDef1;
        this.searchable = searchable;
    }

    public char getTypeOfRecord() {
        return this.typeOfRecord;
    }

    public String getImplDefined1() {
        return this.implDefined1;
    }

    public boolean isSearchable() {
        return this.searchable;
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    public String getString() {
        return this.toString();
    }
}
