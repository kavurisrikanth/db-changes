package rest.ws;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import d3e.core.D3ELogger;
import gqltosql.schema.DField;
import lists.TypeAndId;
import store.DBObject;

public class ObjectsToSend {

	Map<ClientSession, Map<DBObject, Map<DField, BitSet>>> addEmbeddedMap = new HashMap<>();
	Map<ClientSession, Map<DBObject, BitSet>> addMap = new HashMap<>();
	Map<ClientSession, List<TypeAndId>> delMap = new HashMap<>();

	public void addEmbedded(ClientSession session, DBObject parent, DField parentField, BitSet field) {
		if (field.isEmpty()) {
			return;
		}
		//D3ELogger.info("Sending embedded " + parent + " and field " + field);
		Map<DBObject, Map<DField, BitSet>> map2 = addEmbeddedMap.get(session);
		if (map2 == null) {
			map2 = new HashMap<>();
			addEmbeddedMap.put(session, map2);
		}
		Map<DField, BitSet> byField = map2.get(parent);
		if (byField == null) {
			byField = new HashMap<>();
			map2.put(parent, byField);
		}
		BitSet set = byField.get(parentField);
		if (set == null) {
			set = new BitSet();
			byField.put(parentField, set);
		}
		set.or(field);
	}

	public void add(ClientSession session, DBObject object, BitSet field) {
		if (field.isEmpty()) {
			return;
		}
		//D3ELogger.info("Sending " + object + " and field " + field);
		Map<DBObject, BitSet> map2 = addMap.get(session);
		if (map2 == null) {
			map2 = new HashMap<>();
			addMap.put(session, map2);
		}
		BitSet set = map2.get(object);
		if (set == null) {
			set = new BitSet();
			map2.put(object, set);
		}
		set.or(field);
	}

	public void delete(ClientSession session, TypeAndId typeId) {
		//D3ELogger.info("Sending Delete type: " + typeId.type + " id: " + typeId.id);
		List<TypeAndId> list = delMap.get(session);
		if (list == null) {
			list = new ArrayList<TypeAndId>();
			delMap.put(session, list);
		}
		list.add(typeId);
	}

	public void send(D3EWebsocket socket) {
		for (ClientSession session : addMap.keySet()) {
			Map<DBObject, BitSet> map2 = addMap.get(session);
			if (!map2.isEmpty()) {
				socket.sendChanges(session, map2);
			}
		}
		for (ClientSession session : addEmbeddedMap.keySet()) {
			Map<DBObject, Map<DField, BitSet>> map2 = addEmbeddedMap.get(session);
			socket.sendEmbeddedChanges(session, map2);
		}
		for (ClientSession session : delMap.keySet()) {
			List<TypeAndId> map2 = delMap.get(session);
			socket.sendDelete(session, map2);
		}
	}

}
