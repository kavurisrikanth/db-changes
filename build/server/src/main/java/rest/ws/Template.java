package rest.ws;

import d3e.core.ChannelConstants;
import d3e.core.RPCConstants;
import d3e.core.SchemaConstants;
import gqltosql.schema.DClazz;

public class Template {

	private String hash;
	private TemplateType[] types;
	private int[] mapping;
	private int[] channelMapping;
	private int[] rpcMapping;

	private TemplateUsage[] usages;
	private TemplateClazz[] channelInfo;
	private TemplateClazz[] rpcInfo;

	public Template(int types, int usages, int channels, int rpcs) {
		this.usages = new TemplateUsage[usages];
		this.types = new TemplateType[types];
		this.mapping = new int[SchemaConstants._TOTAL_COUNT];
		this.channelInfo = new TemplateClazz[channels];
		this.channelMapping = new int[ChannelConstants._CHANNEL_COUNT];
		this.rpcInfo = new TemplateClazz[rpcs];
		this.rpcMapping = new int[RPCConstants._CLASS_COUNT];
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getHash() {
		return hash;
	}

	public TemplateType[] getTypes() {
		return types;
	}

	public TemplateUsage[] getUsages() {
		return usages;
	}

	public TemplateClazz[] getChannels() {
		return channelInfo;
	}

	public TemplateType getType(int idx) {
		return types[idx];
	}

	public TemplateUsage getUsageType(int idx) {
		return usages[idx];
	}

	public void setTypeTemplate(int idx, TemplateType tt) {
		types[idx] = tt;
		if (tt.getModel() != null) {
			mapping[tt.getModel().getIndex()] = idx;
		}
	}

	public void setUsageTemplate(int idx, TemplateUsage ut) {
		usages[idx] = ut;
	}

	public int toClientTypeIdx(int serverIdx) {
		return mapping[serverIdx];
	}

	public void setChannelTemplate(int i, TemplateClazz tc) {
		channelInfo[i] = tc;
		DClazz ch = tc.getClazz();
		if (ch != null) {
			channelMapping[ch.getIndex()] = i;
		}
	}

	public int getClientChannelIndex(int serverIdx) {
		return channelMapping[serverIdx];
	}

	public TemplateClazz getChannel(int chIdx) {
		TemplateClazz channel = channelInfo[chIdx];
		return channel;
	}

	public void setRPCTemplate(int i, TemplateClazz tc) {
		rpcInfo[i] = tc;
		DClazz ch = tc.getClazz();
		if (ch != null) {
			rpcMapping[ch.getIndex()] = i;
		}
	}

	public int getClientRPCIndex(int serverIdx) {
		return rpcMapping[serverIdx];
	}

	public TemplateClazz getRPCMethod(int chIdx) {
		TemplateClazz clazz = rpcInfo[chIdx];
		return clazz;
	}
}
