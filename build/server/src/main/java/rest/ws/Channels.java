package rest.ws;

import org.springframework.stereotype.Service;
import store.EntityHelperService;

@Service
public class Channels extends AbstractChannels {
  protected AbstractClientProxy getChannelClientProxy(
      int idx, ClientSession ses, EntityHelperService helperService, Template template) {
    AbstractClientProxy proxy = null;
    switch (idx) {
    }
    return proxy;
  }

  protected void handleChannelMessage(
      int idx, int msgSrvIdx, RocketInputContext ctx, ServerChannel channel) {
    switch (idx) {
    }
  }
}
