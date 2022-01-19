package rest.ws;

public abstract class AbstractClientProxy {
  private static final int CHANNEL_MESSAGE = -2;
  
  protected void send(RocketMessage out) {
    out.flush();
  }

  protected RocketMessage createMessage(int chIdx, int msgIdx) {
    ClientSession session = getSession();
    RocketMessage out = new RocketMessage(session);
    out.writeInt(CHANNEL_MESSAGE);
    Template template = session.template;
    int channelIndex = template.getClientChannelIndex(chIdx);
    out.writeInt(channelIndex);
    TemplateClazz channel = template.getChannel(channelIndex);
    int msgIndex = channel.getClientMethodIndex(msgIdx);
    out.writeInt(msgIndex);
    return out;
  }
  
  public abstract ClientSession getSession();
}
