package pl.edu.mimuw.cloudatlas.zones;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.edu.mimuw.cloudatlas.attributes.ContactValue;
import pl.edu.mimuw.cloudatlas.attributes.IntegerValue;
import pl.edu.mimuw.cloudatlas.attributes.SetValue;
import pl.edu.mimuw.cloudatlas.attributes.SimpleType;
import pl.edu.mimuw.cloudatlas.attributes.StringValue;
import pl.edu.mimuw.cloudatlas.attributes.TimeValue;


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
	
	public void setZMI(ZMI zmi) {
		this.zmi = zmi;
	}
	
	public boolean isRoot() {
		return globalName.equals("/");
	}
	
	public String getGlobalName() {
		return globalName;
	}
	
	public String getLocalName() {
		return ZoneNames.getLocalName(globalName);
	}
	
	public int getLevel() {
		return ZoneNames.getLevel(globalName);
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
	
	public Collection<String> getChildNames() {
		return children.keySet();
	}
	
	public void removeChild(String name) {
		children.remove(name);
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
	
	public Zone addChildWithOwner(String localName, String owner, ContactValue ownerContact) {
		assert ZoneNames.isGlobalName(owner);
		assert ownerContact != null;
		
		return addChild(localName, new ZMI()).setupZoneZMI(owner, ownerContact);
	}
	
	public static Zone createRoot(ZMI zmi) {
		return new Zone(null, "/", zmi);
	}
	
	public static Zone createRootWithOwner(String owner, ContactValue ownerContact) {
		assert ZoneNames.isGlobalName(owner);
		
		return createRoot(new ZMI()).setupZoneZMI(owner, ownerContact);
	}
	
	private Zone setupZoneZMI(String owner, ContactValue ownerContact) {
		zmi.addAttribute("level", new IntegerValue(getLevel()));
		zmi.addAttribute("name", SimpleType.STRING, isRoot() ? null : new StringValue(getLocalName()));
		zmi.addAttribute("owner", new StringValue(owner));
		zmi.addAttribute("timestamp", TimeValue.now());
		SetValue<ContactValue> contacts = SetValue.of(SimpleType.CONTACT);
		contacts.addItem(ownerContact);
		zmi.addAttribute("contacts", contacts);
		zmi.addAttribute("cardinality", new IntegerValue(0));
		
		return this;
	}
}
