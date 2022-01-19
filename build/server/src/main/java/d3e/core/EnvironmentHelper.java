package d3e.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.env.Environment;

public class EnvironmentHelper {
  private static final Pattern variables = Pattern.compile("\\{(.*?)\\}");

  public static String getEnvString(Environment env, String str) {
    StringBuilder sb = new StringBuilder();
    Matcher matcher = variables.matcher(str);
    while (matcher.find()) {
      String group = matcher.group();
      group = group.substring(1, group.length() - 1);
      String val = env.getProperty(group, group);
      matcher.appendReplacement(sb, val);
    }
    matcher.appendTail(sb);
    return sb.toString();
  }
}
