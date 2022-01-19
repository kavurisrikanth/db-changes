package rest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.SubProtocolCapable;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import d3e.core.D3ELogger;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import security.AppSessionProvider;

@Configuration
@EnableWebSocket
public class GraphQLWebSocketConfig implements WebSocketConfigurer {

	@Value("${api.native.subscription:/api/native/subscriptions}")
	private String subscriptionPath;

	@Autowired
	private NativeSubscription subscription;

	@Autowired
	private AppSessionProvider appProvider;

	private Map<String, GraphQLSession> subscriptions = new HashMap<>();

	private Timer timer = new Timer();

	@Bean
	public ThreadPoolTaskScheduler taskScheduler(TaskSchedulerBuilder builder) {
		return builder.build();
	}

	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(new TextWebSocketHandlerImpl(), subscriptionPath).setAllowedOrigins("*");
	}

	private class TextWebSocketHandlerImpl extends TextWebSocketHandler implements SubProtocolCapable {

		private List<String> subProtocals = Arrays.asList("graphql-ws");

		private StringBuilder partialPayload = new StringBuilder();

		@Override
		public boolean supportsPartialMessages() {
			return true;
		}

		@Override
		public void afterConnectionEstablished(WebSocketSession session) throws Exception {
			StandardWebSocketSession sss = (StandardWebSocketSession) session;
			Map<String, List<String>> requestParameterMap = sss.getNativeSession().getRequestParameterMap();
			List<String> values = requestParameterMap.get("token");
			String token = null;
			if (values != null && !values.isEmpty()) {
				token = values.get(0);
			}
			subscriptions.put(session.getId(), new GraphQLSession(session, token));
			super.afterConnectionEstablished(session);
		}

		@Override
		public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
			super.afterConnectionClosed(session, status);
			GraphQLSession gql = subscriptions.get(session.getId());
			if (gql != null) {
				gql.close();
			}
		}

		@Override
		public void handleTextMessage(WebSocketSession session, TextMessage message)
				throws InterruptedException, Exception {
			partialPayload.append(message.getPayload());
			if (!message.isLast()) {
				return;
			}
			GraphQLSession gql = subscriptions.get(session.getId());
			String msg = partialPayload.toString();
			partialPayload = new StringBuilder();
			try {
				JSONObject json = new JSONObject(msg);
				if (json.has("id")) {
					String id = json.getString("id");
					try {
						if (json.has("type")) {
							String type = json.getString("type");
							if ("start".equals(type)) {
								D3ELogger.info("Subscription Start: " + id);
								if (json.has("payload")) {
									if (gql.containsKey(id)) {
										gql.sendError(id, "Duplicate id found: " + id);
									} else {
										gql.subscribe(id, json.getJSONObject("payload"));
									}
								} else {
									gql.sendError(id, "Payload not present");
								}
							} else if ("stop".equals(type)) {
								D3ELogger.info("Subscription Stop: " + id);
								gql.dispose(id);
							} else {
								gql.sendError(id, "Type not supported: " + type);
							}
						} else {
							gql.sendError(id, "Type not present");
						}
					} catch (Exception e) {
						D3ELogger.printStackTrace(e);
						gql.sendError(id, e.getMessage());
					}
				} else {
					if (json.has("type")) {
						String type = json.getString("type");
						if (type.equals("connection_init")) {
							session.sendMessage(new TextMessage("{\"type\":\"connection_ack\"}"));
						}
					} else {
						gql.sendError(null, "Id not present");
					}
				}
			} catch (Exception e) {
				gql.sendError(null, "Got Exception " + e.getMessage());
				D3ELogger.printStackTrace(e);
			}
		}

		@Override
		public List<String> getSubProtocols() {
			return subProtocals;
		}
	}

	private class GraphQLSession {

		private WebSocketSession session;

		private Map<String, Disposable> subscriptions = new HashMap<>();
		private TimerTask ka;

		private String token;

		public GraphQLSession(WebSocketSession session, String token) {
			this.session = session;
			this.token = token;
			rescheduleHeartBeat();
		}

		public void subscribe(String id, JSONObject payload) throws Exception {
			appProvider.setToken(token);
			Flowable<JSONObject> flowable = subscription.subscribe(payload);
			if(flowable == null) {
				sendError(id, "Subscription not found");
				return;
			}
			Disposable disposable = flowable.subscribe(m -> send(id, m), t -> {
				D3ELogger.info("SubscriptionException: " + id);
				D3ELogger.printStackTrace(t);
				sendError(id, t.getMessage());
			});
			subscriptions.put(id, disposable);
		}

		private void sendError(String id, String error) throws Exception {
			JSONObject ret = new JSONObject();
			if (id != null) {
				ret.put("id", id);
			}
			ret.put("type", "error");
			ret.put("error", error);
			session.sendMessage(new TextMessage(ret.toString()));
		}

		private void send(String id, JSONObject data) throws Exception {
			JSONObject ret = new JSONObject();
			ret.put("id", id);
			ret.put("type", "data");
			JSONObject payload = new JSONObject();
			payload.put("data", data);
			ret.put("payload", payload);
			String total = ret.toString();
			int limit = session.getTextMessageSizeLimit();
			while (true) {
				if (total.length() < limit) {
					session.sendMessage(new TextMessage(total));
					break;
				}
				String first = total.substring(0, limit);
				session.sendMessage(new TextMessage(first, false));
				total = total.substring(limit);
			}
		}

		private void rescheduleHeartBeat() {
			if (ka != null) {
				ka.cancel();
			}
			TimerTask ka = new TimerTask() {

				@Override
				public void run() {
					try {
						if (session.isOpen()) {
							session.sendMessage(new TextMessage("{\"type\":\"ka\"}"));
						}
					} catch (Exception e) {
					}
				}
			};
			timer.schedule(ka, 15_000, 15_000);
			this.ka = ka;
		}

		public void dispose(String id) {
			Disposable disposable = subscriptions.remove(id);
			if (disposable != null) {
				disposable.dispose();
			}
		}

		public boolean containsKey(String id) {
			return subscriptions.containsKey(id);
		}

		public void close() {
			subscriptions.values().forEach(d -> d.dispose());
			if (ka != null) {
				ka.cancel();
			}
		}
	}
}