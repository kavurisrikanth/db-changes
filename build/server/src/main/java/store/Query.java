package store;

import java.util.List;

public interface Query extends javax.persistence.Query {

	<T> List<T> getObjectResultList(int type);

	<T> T getObjectFirstResult(int type);
}
