package d3e.core;

public class BlockString {

	private String content;

	private Object attachment;

	public BlockString(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

	public Object getAttachment() {
		return attachment;
	}
	
	@Override
	public String toString() {
		return this.content;
	}
}
