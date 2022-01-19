package rest.ws;

import java.util.Arrays;

import gqltosql.schema.DClazz;
import gqltosql.schema.DClazzMethod;
import gqltosql.schema.DModel;

public class TemplateClazz {
    private String hash;
    
    private DClazz clazz;
    private DClazzMethod[] methods;
    private int[] mapping;

    public TemplateClazz(DClazz ch, int size) {
        this.setClazz(ch);
        this.methods = new DClazzMethod[size];
        if (ch != null) {
            this.mapping = new int[ch.getMethods().length];
            Arrays.fill(mapping, -1);
        }
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getHash() {
		return hash;
	}
    
    public DClazz getClazz() {
        return clazz;
    }

    public void setClazz(DClazz ch) {
        this.clazz = ch;
    }

    public DClazzMethod[] getMethods() {
        return methods;
    }

    public void setMethods(DClazzMethod[] messages) {
        this.methods = messages;
    }
    
    public void addMethod(int idx, DClazzMethod f) {
        this.methods[idx] = f;
        if (f != null) {
            this.mapping[f.getIndex()] = idx;
        }
    }
    
    public int getClientMethodIndex(int serverIdx) {
        return mapping[serverIdx];
    }
}
