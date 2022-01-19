package classes;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import d3e.core.IntegerExt;
import d3e.core.ListExt;
import d3e.core.StringExt;

public class AutoGenerateUtil {
  private static String REPLACE_REGEX = "\\W+";

  public AutoGenerateUtil() {}

  public static String generateIdentity(String src, String sanitizeWith, String prefix, String suffix) {
    return generateIdentity(src, sanitizeWith, prefix, suffix, null);
  }
  
  public static String generateIdentityLowerCase(String src, String sanitizeWith, String prefix, String suffix) {
    return generateIdentity(src, sanitizeWith, prefix, suffix, str -> str.toLowerCase());
  }
  
  public static String generateIdentityUpperCase(String src, String sanitizeWith, String prefix, String suffix) {
    return generateIdentity(src, sanitizeWith, prefix, suffix, str -> str.toUpperCase());
  }
  
  public static String generateIdentityCamelCaseStartLower(String src, String sanitizeWith, String prefix, String suffix) {
    return StringUtils.uncapitalize(generateIdentityCamelCaseStartUpper(src, sanitizeWith, prefix, suffix));
  }
  
  public static String generateIdentityCamelCaseStartUpper(String src, String sanitizeWith, String prefix, String suffix) {
    if (src == null || sanitizeWith == null) {
      return null;
    }
    
    String joined = join(prefix, suffix, src);
    
    return Arrays.stream(joined.split(REPLACE_REGEX)).map(s -> StringUtils.capitalize(s)).collect(Collectors.joining(sanitizeWith));
  }
  
  /***
   * Helper methods
   */
  
  
  private static String generateIdentity(String src, String sanitizeWith, String prefix, String suffix, Function<String, String> modResult) {
    if (src == null || sanitizeWith == null) {
      return null;
    }
    
    String sanitized = sanitize(src, sanitizeWith);
    
    String sanitizedPrefix = null;
    if (prefix != null) {
      sanitizedPrefix = sanitize(prefix, sanitizeWith);
    }
    
    String sanitizedSuffix = null;
    if (suffix != null) {
      sanitizedSuffix = sanitize(suffix, sanitizeWith);
    }
    
    String joined = join(sanitizedPrefix, sanitizedSuffix, sanitized);

    String modified = (modResult == null) ? joined : modResult.apply(joined);
    
    return modified;
  }
  
  private static String sanitize(String src, String sanitizeWith) {
    return src.replaceAll(REPLACE_REGEX, sanitizeWith);
  }

  private static String join(String prefix, String suffix, String payload) {
    StringBuilder sb = new StringBuilder();
    if (prefix != null) {
      sb.append(prefix);
    }
    if (payload.matches("[0-9]+") && prefix.isEmpty()) {
      sb.append("n");
    }
    sb.append(payload);
    if (suffix != null) {
      sb.append(suffix);
    }
    return sb.toString();
  }
  
  public static String generateNextSequenceString(
      long startsFrom, long step, String prefix, String suffix, String old) {
    if (prefix == null) {
      prefix = "";
    }
    if (suffix == null) {
      suffix = "";
    }
    if (old == null) {
      return IntegerExt.toString(startsFrom);
    }
    String stripped = StringExt.substring(old, StringExt.length(prefix), 0l);
    stripped =
        StringExt.substring(stripped, 0l, StringExt.length(stripped) - StringExt.length(suffix));
    long oldInt = IntegerExt.tryParse(old, 0l);
    long newInt = AutoGenerateUtil.generateNextSequence(startsFrom, step, oldInt, false);
    return new StringBuilder(prefix).append(newInt).append(suffix).toString();
  }

  public static long generateNextSequence(long startsFrom, long step, long old, boolean hasOld) {
    if (!hasOld) {
      return startsFrom;
    }
    return old + step;
  }

  public static String toName(String str) {
    String[] pieces = splitCamelCaseString(str);
    if (pieces.length > 0) {
      pieces[0] = StringUtils.capitalize(pieces[0]);
      for (int i = 1; i < pieces.length; i++) {
        pieces[i] = StringUtils.lowerCase(pieces[i]);
      }
    }
    return String.join(" ", pieces);
  }
  
  private static String[] splitCamelCaseString(String str) {
    int i = 0, len = str.length();
    int prevType = -1;
    StringBuilder sb = new StringBuilder();
    List<String> pieces = ListExt.asList();
    while (i < len) {
      char currentChar = str.charAt(i);
      int type = Character.getType(currentChar);
      boolean upperNextToLower = prevType == Character.LOWERCASE_LETTER && type == Character.UPPERCASE_LETTER;
      if (prevType == -1 || type == prevType || !upperNextToLower) {
        sb.append(currentChar);
      } else {
        if (upperNextToLower) {
          pieces.add(sb.toString());
          sb = new StringBuilder();
        }
      }
      i++;
    }
    if (sb.length() != 0) {
      pieces.add(sb.toString());
    }
    return pieces.stream().toArray(String[]::new);
  }
}
