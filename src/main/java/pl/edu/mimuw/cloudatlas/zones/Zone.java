package pl.edu.mimuw.cloudatlas.zones;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Zone {

	private final String globalName;
	private ZMI zmi;
	
	private final Zone parent;
	private final Map<String, Zone> children = new HashMap<String, Zone>();
	
	private Zone(Zone parent, String globalName, ZMI zmi) {
		this.globalName = globalName;
		this.parent = parent;
		this.zmi = zmi;
	}
	
	public ZMI getZMI() {
		return zmi;
	}
	
	public String getGlobalName() {
		return globalName;
	}
	
	public String getLocalName() {
		return ZoneNames.getLocalName(globalName);
	}
	
	public Zone getParent() {
		return parent;
	}
	
	public Zone getRoot() {
		Zone candidate = this;
		while (candidate.parent != null) {
			candidate = candidate.parent;
		}
		return candidate;
	}
	
	public Zone findZone(String path) {
		Zone zone = this;
		
		if (path.length() > 0 && path.charAt(0) == '/') {
			zone = getRoot();
			path = path.substring(1);
		}
		
		if (path.length() > 0) {
			for (String name : path.split("/")) {
				zone = zone.getChild(name);
				if (zone == null) {
					return null;
				}
			}
		}
		
		return zone;
	}
	
	public Zone getChild(String name) {
		return children.get(name);
	}
	
	public Collection<Zone> getChildren() {
		return children.values();
	}
	
	public Collection<ZMI> getChildZMIs() {
		List<ZMI> result = new ArrayList<ZMI>();
		for (Zone child : children.values()) {
			result.add(child.getZMI());
		}
		return result;
	}
	
	public Zone addChild(String localName, ZMI zmi) {
		assert ZoneNames.isLocalName(localName);
		
		String globalName = parent == null ? "/" + localName : this.globalName + "/" + localName;
		Zone child = new Zone(this, globalName, zmi);
		
		children.put(localName, child);
		return child;
	}
	
	public static Zone createRoot(ZMI zmi) {
		return new Zone(null, "/", zmi);
	}
}
