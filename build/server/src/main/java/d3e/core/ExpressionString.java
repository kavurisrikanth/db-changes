package d3e.core;

public class ExpressionString {

	private String content;

	private Object attachment;

	public ExpressionString() {
	}

	public ExpressionString(String content) {
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
