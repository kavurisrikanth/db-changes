package gqltosql.schema;

public class DClazzMethod {
    private String name;
    private DParam[] params;
    private int index;
    private int returnType = -1;
    private boolean returnColl;
    
    public DClazzMethod(String name, int index, DParam[] params) {
        this.name = name;
        this.index = index;
        this.params = params;
    }

    public DClazzMethod(String name, int index, int numParams) {
        this.name = name;
        this.index = index;
        this.params = new DParam[numParams];
    }
    
    public DClazzMethod(String name, int index, int returnType, boolean returnColl, DParam[] params) {
    	this(name, index, params);
    	this.returnType = returnType;
    	this.returnColl = returnColl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DParam[] getParams() {
        return params;
    }

    public void setParams(DParam[] params) {
        this.params = params;
    }

    public void addParam(int index, DParam param) {
        this.params[index] = param;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    
    public boolean hasReturnType() {
    	return this.returnType != -1;
    }

    public int getReturnType() {
      return returnType;
    }

    public void setReturnType(int returnType, boolean returnColl) {
      this.returnType = returnType;
      this.returnColl = returnColl;
    }
    
    public boolean isReturnColl() {
      return returnColl;
    }
}
