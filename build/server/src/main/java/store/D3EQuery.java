package store;

import java.util.List;

import d3e.core.ListExt;

public class D3EQuery {

	String query;
	List<Object> args = ListExt.List();
	D3EQuery pre;
	D3EQuery next;
	private DatabaseObject obj;

	public D3EQuery() {
	}

	public DatabaseObject getObj() {
		return obj;
	}

	public void setObj(DatabaseObject obj) {
		this.obj = obj;
	}

	public void addPreQuery(D3EQuery q) {
		if (this.pre != null) {
			this.pre.addPreQuery(q);
			return;
		}
		this.pre = q;
	}
	
	public void addNextQuery(D3EQuery q) {
		if (this.next != null) {
			this.next.addNextQuery(q);
			return;
		}
		this.next = q;
	}

	public void setQuery(String q) {
		this.query = q;
	}

	public void setArgs(List<Object> args) {
		this.args = args;
	}
}
