package rest.ws;

public class ServerChannel<C> {

	private ThreadLocal<C> client = new ThreadLocal<>();

	public void setClient(C client) {
		this.client.set(client);
	}

	public void removeClient() {
		client.remove();
	}

	public C getClient() {
		return client.get();
	}

	public boolean onConnect(C client) {
		return true;
	}

	public void onDisconnect(C client) {
	}
}
