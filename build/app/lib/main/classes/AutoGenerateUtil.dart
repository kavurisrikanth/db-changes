import 'core.dart';

class AutoGenerateUtil {
  static RegExp replaceRegex = new RegExp(r'\W+');

  static String generateIdentity(String src, String sanitizeWith,
      {String prefix = '', String suffix = ''}) {
    return _generateIdentity(src, sanitizeWith, prefix, suffix);
  }

  static String generateIdentityLowerCase(String src, String sanitizeWith,
      {String prefix = '', String suffix = ''}) {
    return _generateIdentity(src, sanitizeWith, prefix, suffix,
        modResult: (str) => str.toLowerCase());
  }

  static String generateIdentityUpperCase(String src, String sanitizeWith,
      {String prefix = '', String suffix = ''}) {
    return _generateIdentity(src, sanitizeWith, prefix, suffix,
        modResult: (str) => str.toUpperCase());
  }

  static String generateIdentityCamelCaseStartLower(
      String src, String sanitizeWith,
      {String prefix = '', String suffix = ''}) {
    return _caseFirstChar(
        generateIdentityCamelCaseStartUpper(src, sanitizeWith,
            prefix: prefix, suffix: suffix),
        lower: true);
  }

  static String generateIdentityCamelCaseStartUpper(
      String src, String sanitizeWith,
      {String prefix = '', String suffix = ''}) {
    if (src == null || sanitizeWith == null) {
      return null;
    }

    String joined = join(prefix, suffix, src);

    return joined
        .split(replaceRegex)
        .map((s) => _caseFirstChar(s))
        .join(sanitizeWith);
  }

  /*
   * Helper methods
  */

  static String _generateIdentity(
      String src, String sanitizeWith, String prefix, String suffix,
      {OneFunction<String, String> modResult}) {
    if (src == null || sanitizeWith == null) {
      return null;
    }

    String sanitized = sanitize(src, sanitizeWith);

    String sanitizedPrefix;
    if (prefix != null) {
      sanitizedPrefix = sanitize(prefix, sanitizeWith);
    }

    String sanitizedSuffix;
    if (suffix != null) {
      sanitizedSuffix = sanitize(suffix, sanitizeWith);
    }

    String joined = join(sanitizedPrefix, sanitizedSuffix, sanitized);

    String modified = (modResult == null) ? joined : modResult(joined);

    return modified;
  }

  static String sanitize(String src, String sanitizeWith) {
    return src.replaceAll(replaceRegex, sanitizeWith);
  }

  static String join(String prefix, String suffix, String payload) {
    List<String> pieces = [if (prefix != null) prefix];
    if (num.tryParse(payload) != null && prefix.isEmpty) {
      pieces.add('n');
    }
    pieces.add(payload);
    if (suffix != null) {
      pieces.add(suffix);
    }
    return pieces.join('');
  }

  static String generateNextSequenceString(
      int startsFrom, int step, String prefix, String suffix, String old) {
    if (prefix == null) {
      prefix = '';
    }
    if (suffix == null) {
      suffix = '';
    }
    if (old == null) {
      return generateNextSequence(startsFrom, step, -1, hasOld: false)
          .toString();
    }
    String stripped = old.substring(prefix.length);
    stripped = stripped.substring(0, stripped.length - suffix.length);
    int oldInt = int.tryParse(stripped);
    int newInt = generateNextSequence(startsFrom, step, oldInt);
    return prefix + newInt.toString() + suffix;
  }

  static int generateNextSequence(int startsFrom, int step, int old,
      {bool hasOld = true}) {
    if (!hasOld) {
      return startsFrom;
    }
    return old + step;
  }

  static String _caseFirstChar(String str, {bool lower = false}) {
    if (str == null || str.isEmpty) {
      return str;
    }
    String first = str.substring(0, 1);
    return (lower ? first.toLowerCase() : first.toUpperCase()) +
        str.substring(1);
  }
}
