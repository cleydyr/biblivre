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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.json.JSONArray;

public class JavascriptCacheableList<T extends IFJson>
        implements Collection<T>, IFCacheableJavascript {
    private final String variable;
    private final String prefix;
    private final String suffix;
    private JavascriptCache cache;

    private final Collection<T> list = new ArrayList<>();

    public JavascriptCacheableList(String variable, String prefix, String suffix) {
        this.variable = variable;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public String getCacheFileNamePrefix() {
        return this.prefix;
    }

    @Override
    public String getCacheFileNameSuffix() {
        return this.suffix;
    }

    @Override
    public String toJavascriptString() {
        JSONArray array = new JSONArray();

        for (T el : this) {
            array.put(el.toJSONObject());
        }

        return this.variable + " = " + array + ";";
    }

    @Override
    public File getCacheFile() {
        if (this.cache == null) {
            this.cache = new JavascriptCache(this);
        }

        return this.cache.getCacheFile();
    }

    @Override
    public String getCacheFileName() {
        if (this.cache == null) {
            this.cache = new JavascriptCache(this);
        }

        return this.cache.getFileName();
    }

    @Override
    public void invalidateCache() {
        this.cache = null;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <U> U[] toArray(U[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(T e) {
        return list.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return list.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }
}
