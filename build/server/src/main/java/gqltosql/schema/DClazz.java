package gqltosql.schema;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import d3e.core.MapExt;

public class DClazz {
  private String name;
  private int index;
  private DClazzMethod[] methods;
  private Map<String, DClazzMethod> methodsByName;
  
  public DClazz(String name, int index, int msgCount) {
    this.name = name;
    this.index = index;
    this.methods = new DClazzMethod[msgCount];
    this.methodsByName = new HashMap<>();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public DClazzMethod[] getMethods() {
    return methods;
  }
  
  public DClazzMethod getMethod(int idx) {
    return methods[idx];
  }
  
  public DClazzMethod getMethod(String name) {
    return methodsByName.get(name);
  }

  public void setMethods(DClazzMethod[] messages) {
    this.methods = messages;
    this.methodsByName = MapExt.fromIterable(Arrays.asList(messages), x -> x.getName(), (x) -> x);
  }
  
  public void addMethod(int index, DClazzMethod msg) {
    this.methods[index] = msg;
    this.methodsByName.put(msg.getName(), msg);
  }
}
