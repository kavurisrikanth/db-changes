package rest.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import d3e.core.D3ELogger;
import d3e.core.ListExt;
import d3e.core.MD5Util;
import gqltosql.schema.DClazz;
import gqltosql.schema.DField;
import gqltosql.schema.DClazzMethod;
import gqltosql.schema.DModel;
import gqltosql.schema.DModelType;
import gqltosql.schema.DParam;
import gqltosql.schema.IModelSchema;

@Service
public class MasterTemplate {

	@Autowired
	private IModelSchema schema;

	private Map<String, TemplateType> typesByHash = new HashMap<>();
	private Map<String, TemplateUsage> usageByHash = new HashMap<>();
	private Map<String, TemplateClazz> channelsByHash = new HashMap<>();
	private Map<String, TemplateClazz> rpcsByHash = new HashMap<>();

	@PostConstruct
	public void init() {
		List<DModel<?>> allTypes = schema.getAllTypes();
		Map<String, TemplateType> hashByType = new HashMap<>();
		allTypes.forEach(t -> addTemplateType(t, hashByType));
		hashByType.clear();

		List<DClazz> allChannels = schema.getAllChannels();
		allChannels.forEach(c -> addTemplateChannel(c));

		List<DClazz> allRPCs = schema.getAllRPCs();
		allRPCs.forEach(c -> addTemplateRPC(c));

		D3ELogger.info("Master Template Ready");
	}

	public String getByType(String type) {
		Set<Entry<String, TemplateType>> entrySet = typesByHash.entrySet();
		for (Entry<String, TemplateType> e : entrySet) {
			if (e.getValue().getModel().getType().equals(type)) {
				return e.getKey();
			}
		}
		return null;
	}

	private void addTemplateRPC(DClazz c) {
		addClazzToTemplate(c, true);
	}

	private void addTemplateChannel(DClazz c) {
		addClazzToTemplate(c, false);
	}

	private void addClazzToTemplate(DClazz c, boolean isRPC) {
		List<String> md5 = new ArrayList<>();
		md5.add(c.getName());
		List<DClazzMethod> allFields = ListExt.from(c.getMethods(), false);
		TemplateClazz tt = new TemplateClazz(c, allFields.size());
		int i = 0;
		for (DClazzMethod f : allFields) {
			tt.addMethod(i++, f);
			md5.add(f.getName());
			for (DParam p : f.getParams()) {
				// TODO: Collection?
				DModel<?> dm = schema.getType(p.getType());
				md5.add(dm.getType());
			}
			// Remote Procedure Call could have return type
			if (isRPC && f.hasReturnType()) {
				DModel<?> dm = schema.getType(f.getReturnType());
				md5.add(dm.getType());
			}
		}
		String hash = MD5Util.md5(md5);
		tt.setHash(hash);
		if (isRPC) {
			rpcsByHash.put(hash, tt);
		} else {
			channelsByHash.put(hash, tt);
		}
	}

	private TemplateType addTemplateType(DModel<?> md, Map<String, TemplateType> allTypes) {
		if (allTypes.containsKey(md.getType())) {
			return allTypes.get(md.getType());
		}
		TemplateType tt = new TemplateType(md, md.getFields().length);
		allTypes.put(md.getType(), tt);

		List<DField<?, ?>> allFields = ListExt.from(md.getFields(), false);
		if (md.getModelType() != DModelType.ENUM) {
			allFields.sort((a, b) -> a.getName().compareTo(b.getName()));
		}
		int i = 0;
		for (DField<?, ?> f : allFields) {
			tt.addField(i++, f);
		}
		if (md.getParent() != null) {
			TemplateType parent = addTemplateType(md.getParent(), allTypes);
			tt.setParent(parent);
		}
		tt.computeHash();
		typesByHash.put(tt.getHash(), tt);
		return tt;
	}

	public TemplateType getTemplateType(String typeHash) {
		return typesByHash.get(typeHash);
	}

	public TemplateUsage getUsageTemplate(String usageHash) {
		return usageByHash.get(usageHash);
	}

	public TemplateClazz getChannelTemplate(String channelHash) {
		return channelsByHash.get(channelHash);
	}

	public TemplateClazz getRPCTemplate(String channelHash) {
		return rpcsByHash.get(channelHash);
	}

	public void addTypeTemplate(TemplateType tt) {
		typesByHash.put(tt.getHash(), tt);
	}

	public void addUsageTemplate(TemplateUsage tu, Template tml) {
		List<String> md5 = new ArrayList<>();
		UsageType[] uts = tu.getTypes();
		for (UsageType ut : uts) {
			addUsageMD5Strings(md5, ut, tml);
		}
		String hash = MD5Util.md5(md5);
		tu.setHash(hash);
		// usageByHash.put(hash, tu);
	}

	public void addChannelTemplate(TemplateClazz tc, Template tml) {
		/*
		 * channel name methods name param type
		 */
		addClazzMethod(tc, tml, false);
	}

	private void addClazzMethod(TemplateClazz tc, Template tml, boolean isRPC) {
		List<String> md5 = new ArrayList<>();
		DClazz dc = tc.getClazz();
		md5.add(dc.getName());
		for (DClazzMethod one : tc.getMethods()) {
			md5.add(one.getName());
			for (DParam type : one.getParams()) {
				// TODO: Collection?
				DModel<?> dm = tml.getType(type.getType()).getModel();
				md5.add(dm.getType());
			}
			if (isRPC && one.hasReturnType()) {
				DModel<?> dm = tml.getType(one.getReturnType()).getModel();
				md5.add(dm.getType());
			}
		}
		String hash = MD5Util.md5(md5);
		tc.setHash(hash);
		if (isRPC) {
			rpcsByHash.put(hash, tc);
		} else {
			channelsByHash.put(hash, tc);
		}
	}

	private void addUsageMD5Strings(List<String> md5, UsageType ut, Template tml) {
		TemplateType type = tml.getType(ut.getType());
		if(type.getModel() == null) {
			return;
		}
		md5.add(type.getModel().getType());
		for (UsageField f : ut.getFields()) {
			DField<?, ?> df = type.getField(f.getField());
			md5.add(df.getName());
			UsageType[] types = f.getTypes();
			for (UsageType utt : types) {
				addUsageMD5Strings(md5, utt, tml);
			}
		}
	}

	public void addRPCTemplate(TemplateClazz tc, Template template) {
		addClazzMethod(tc, template, true);
	}
}
