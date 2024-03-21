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
package biblivre.core.utils;

import biblivre.core.utils.exception.PasswordEncodingException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.text.Normalizer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.mozilla.universalchardet.UniversalDetector;

public class TextUtils {
    private static final int PBE_KEY_LENGTH = 256 * 8;
    private static final int PBE_KEY_ITERATION_COUNT = 4096;

    public static byte[] encodeSaltedPassword(String password, byte[] salt) {
        if (StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("Password is null");
        }

        try {
            KeySpec spec =
                    new PBEKeySpec(
                            password.toCharArray(), salt, PBE_KEY_ITERATION_COUNT, PBE_KEY_LENGTH);

            SecretKeyFactory secretKeyFactory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            return secretKeyFactory.generateSecret(spec).getEncoded();

        } catch (Exception e) {
            throw new PasswordEncodingException("can't encode password", e);
        }
    }

    public static byte[] generatePasswordSalt() {
        SecureRandom secureRandom = new SecureRandom();

        byte[] salt = new byte[32];

        secureRandom.nextBytes(salt);

        return salt;
    }

    public static String encodePasswordSHA(String password) {
        if (StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("Password is null");
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(password.getBytes(Constants.DEFAULT_CHARSET));
            byte[] pass = Base64.getEncoder().encode(md.digest());

            return new String(pass);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA algorithm not found", e);
        }
    }

    public static boolean endsInValidCharacter(String str) {
        if (str == null) {
            return true;
        }

        String lastChar = String.valueOf(str.charAt(str.length() - 1));

        return TextUtils.removeDiacriticals(lastChar).matches("[0-9a-zA-Z]");
    }

    public static String camelCase(String str) {
        if (StringUtils.isBlank(str)) {
            return "";
        }

        String[] terms = StringUtils.split(str.toLowerCase(), "_");

        StringBuilder result = new StringBuilder(terms[0]);

        for (int i = 1; i < terms.length; i++) {
            result.append(StringUtils.capitalize(terms[i]));
        }

        return result.toString();
    }

    public static String biblivreEncode(String input) {
        if (input == null) {
            return "";
        }

        return StringUtils.reverse(Base64.getEncoder().encodeToString(input.getBytes()));
    }

    public static String biblivreDecode(String input) {
        if (input == null) {
            return "";
        }

        return new String(Base64.getDecoder().decode(StringUtils.reverse(input)));
    }

    public static String preparePhrase(String input) {
        return TextUtils.removeDiacriticals(TextUtils.removeDoubleSpaces(input)).toLowerCase();
    }

    public static String[] prepareWords(String phrase) {
        return StringUtils.split(TextUtils.removeNonLettersOrDigits(phrase, " "));
    }

    public static String[] prepareAutocomplete(String phrase) {
        return StringUtils.split(TextUtils.preparePhrase(phrase));
    }

    public static String prepareWord(String word) {
        return TextUtils.removeNonLettersOrDigits(word, "");
    }

    public static String[] prepareExactTerms(String phrase) {
        String[] terms = TextUtils.prepareWords(phrase);

        Arrays.sort(
                terms,
                (o1, o2) -> {
                    if (o1.length() < o2.length()) {
                        return 1;
                    } else if (o1.length() > o2.length()) {
                        return -1;
                    }
                    return o1.compareTo(o2);
                });

        List<String> newList = new ArrayList<>();
        for (String term : terms) {
            if (term.length() > 2) {
                newList.add(term);
            }

            if (newList.size() > 2) {
                break;
            }
        }

        return newList.toArray(new String[] {});
    }

    public static String removeDiacriticals(String input) {
        if (input == null) {
            return "";
        }

        String decomposed = Normalizer.normalize(input, Normalizer.Form.NFD);
        return decomposed.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public static String removeDoubleSpaces(String input) {
        if (input == null) {
            return "";
        }

        String trimmed = StringUtils.trimToEmpty(input);
        return trimmed.replaceAll("\\s{2,}", " ");
    }

    public static String removeNonLettersOrDigits(String input, String replace) {
        if (input == null) {
            return "";
        }

        return input.replaceAll("[^\\p{L}\\p{N}*]", replace).replaceAll("\\*([^\\s])", "$1");
    }

    public static Date parseDate(String date) throws ParseException {
        if (StringUtils.isBlank(date)) {
            return null;
        }
        return DateUtils.parseDate(
                date, DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.getPattern());
    }

    public static int defaultInt(String value) {
        return TextUtils.defaultInt(value, 0);
    }

    public static int defaultInt(String value, int defValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defValue;
        }
    }

    public static String detectCharset(InputStream input) throws IOException {
        UniversalDetector detector = new UniversalDetector();

        byte[] buf = new byte[4096];

        int read;

        while ((read = input.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, read);
        }

        detector.dataEnd();

        return detector.getDetectedCharset();
    }

    public static String incrementLastChar(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }

        return text + "zzzzzzzzzzzzzzzzz";
    }
}
