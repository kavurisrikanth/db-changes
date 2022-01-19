package rest.ws;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import d3e.core.D3ELogger;
import gqltosql.schema.DClazz;
import store.EntityHelperService;

public abstract class AbstractChannels {
  @Autowired(required =  false)
  private Map<String, ServerChannel<?>> allChannels;
  
  public boolean connect(DClazz dm, ClientSession ses, EntityHelperService helperService, Template template) {
	  D3ELogger.info("Channel connected: " + dm.getName());
    ServerChannel channel = allChannels.get(dm.getName());
    AbstractClientProxy proxy = getChannelClientProxy(dm.getIndex(), ses, helperService, template);
    boolean val = channel.onConnect(proxy);
    if (val) {
      ses.proxies.put(dm.getName(), proxy);
    }
    return val;
  }
  
  public void disconnect(DClazz dm, ClientSession ses) {
	  D3ELogger.info("Channel disconnected: " + dm.getName());
    ServerChannel channel = allChannels.get(dm.getName());
    AbstractClientProxy proxy = (AbstractClientProxy) ses.proxies.remove(dm.getName());
    if (proxy != null) {
      channel.onDisconnect(proxy);
    }
  }

  public void disconnect(ClientSession ses) {
	  ses.proxies.forEach((k, p) -> {
		  ServerChannel channel = allChannels.get(k);
		  channel.onDisconnect(p);
	  });
  }
  
  public void onMessage(DClazz dm, int msgSrvIdx, ClientSession sesssion, RocketInputContext ctx) {
    ServerChannel channel = allChannels.get(dm.getName());
    channel.setClient(sesssion.proxies.get(dm.getName()));
    try {
      handleChannelMessage(dm.getIndex(), msgSrvIdx, ctx, channel);
    } finally {
      channel.removeClient();
    }
  }

  protected abstract void handleChannelMessage(int idx, int msgSrvIdx, RocketInputContext ctx,
      ServerChannel channel);

  protected abstract AbstractClientProxy getChannelClientProxy(int idx, ClientSession ses, EntityHelperService helperService, Template template);
}
