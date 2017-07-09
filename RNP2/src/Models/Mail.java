package Models;

import java.util.List;
import java.util.Map;

public class Mail {

	private Map<String, List<String>> header;
	private String body, UID = " ";
	private long size;
	private boolean destroyFlag, setUIDFlag = false;

	public Mail(Map<String, List<String>> header, String body, long size) {
		this.header = header;
		this.body = body;
		this.size = size;
		this.destroyFlag = false;
	}

	/*
	 * ////////////////// GETTER & SETTER
	 *//////////////////

	public Map<String, List<String>> getHeader() {
		return this.header;
	}

	public String getBody() {
		return this.body;
	}

	public long getSize() {
		return this.size;
	}

	public boolean getDestroyFlag() {
		return this.destroyFlag;
	}

	public String getUID() {
		return this.UID;
	}

	public void setDestroyFlag(boolean flag) {
		if (!this.setUIDFlag) {
			this.destroyFlag = flag;
			this.setUIDFlag = true;
		}
	}

	public void setUID() {
		this.UID = UniqueId.generateId();
	}
}
