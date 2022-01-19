package d3e.core;

import javax.persistence.Entity;
import javax.persistence.Id;

import store.Database;

@Entity
public class DFile {

	private String name;
	@Id
	private String id;
	private long size;
	private transient boolean proxy;

	private String mimeType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getSize() {
		_checkProxy();
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getName() {
		_checkProxy();
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMimeType() {
		_checkProxy();
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public void _markProxy() {
		this.proxy = true;
	}

	public void _checkProxy() {
		if (this.proxy) {
			Database.get().unproxyDFile(this);
			this.proxy = false;
		}
	}
}
