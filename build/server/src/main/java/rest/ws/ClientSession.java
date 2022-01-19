package rest.ws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import d3e.core.MapExt;

public class ClientSession {

	private WebSocketSession session;
	ByteArrayOutputStream stream = new ByteArrayOutputStream();
	Template template;
	long userId;
	String userType;
	private ReentrantLock lock = new ReentrantLock();
	Map<String, AbstractClientProxy> proxies = MapExt.Map();
	List<BinaryMessage> queue = new ArrayList<>();
	private long timeout;

	public ClientSession(WebSocketSession session) {
		this.session = session;
	}

	public String getId() {
		return session.getId();
	}

	public void lock() {
		lock.lock();
	}

	public boolean isLocked() {
		return lock.isLocked() && lock.isHeldByCurrentThread();
	}

	public void unlock() {
		lock.unlock();
	}

	public void sendMessage(BinaryMessage msg, int msgId) throws IOException {
		if (msgId == 0) {
			lock.lock();
		}
		try {
			if (session == null) {
				queue.add(msg);
			} else {
				session.sendMessage(msg);
			}
		} finally {
			if (msg.isLast()) {
				lock.unlock();
			}
		}
	}

	public WebSocketSession getSession() {
		return session;
	}

	public void setSession(WebSocketSession session) throws IOException {
		this.session = session;
		if (session != null) {
			for (BinaryMessage msg : queue) {
				session.sendMessage(msg);
			}
			queue.clear();
		}
	}

	public void setTimeOut(long timeout) {
		this.timeout = timeout;
	}
	
	public long getTimeout() {
		return timeout;
	}

	public void logout() {
		this.userId = 0;
		this.userType = null;
	}
}
