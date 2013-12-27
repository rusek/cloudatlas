package pl.edu.mimuw.cloudatlas.agent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pl.edu.mimuw.cloudatlas.attributes.ContactValue;
import pl.edu.mimuw.cloudatlas.attributes.IntegerValue;
import pl.edu.mimuw.cloudatlas.attributes.SetType;
import pl.edu.mimuw.cloudatlas.attributes.SetValue;
import pl.edu.mimuw.cloudatlas.attributes.SimpleType;
import pl.edu.mimuw.cloudatlas.attributes.StringValue;
import pl.edu.mimuw.cloudatlas.attributes.TimeValue;
import pl.edu.mimuw.cloudatlas.attributes.Value;
import pl.edu.mimuw.cloudatlas.query.Env;
import pl.edu.mimuw.cloudatlas.query.EvaluationException;
import pl.edu.mimuw.cloudatlas.query.ParseException;
import pl.edu.mimuw.cloudatlas.query.Parsers;
import pl.edu.mimuw.cloudatlas.query.SelectionResult;
import pl.edu.mimuw.cloudatlas.query.Stmt;
import pl.edu.mimuw.cloudatlas.zones.Attribute;
import pl.edu.mimuw.cloudatlas.zones.AttributeNames;
import pl.edu.mimuw.cloudatlas.zones.ZMI;
import pl.edu.mimuw.cloudatlas.zones.Zone;

public class ZoneAncestorsRefresher {
	
	private static final int MAX_CONTACTS = 10;
	
	private static Logger log = LogManager.getFormatterLogger(ZoneAncestorsRefresher.class);
	
	private final String myZoneName;
	
	public ZoneAncestorsRefresher(String myZoneName) {
		this.myZoneName = myZoneName;
	}
	
	public void refreshAncestors(Zone rootZone) {
		Zone ancestorZone = rootZone.findZone(myZoneName).getParent();
		while (ancestorZone != null) {
			refreshAncestor(ancestorZone);
			ancestorZone = ancestorZone.getParent();
		}
	}

	private void removeOutdatedAttributes(ZMI zmi) {
		List<String> attributeNames = new ArrayList<String>();
		attributeNames.addAll(zmi.getAttributeNames());
		
		for (String attributeName : attributeNames) {
			if (!AttributeNames.isBuiltinName(attributeName) && !AttributeNames.isSpecialName(attributeName)) {
				zmi.removeAttribute(attributeName);
			}
		}
	}
	
	private void updateBuiltinAttributes(Zone zone) {
		ZMI zmi = zone.getZMI();
		zmi.setAttribute("owner", new StringValue(myZoneName));
		zmi.setAttribute("timestamp", TimeValue.now());
		
		updateContacts(zone);
		updateCardinality(zone);
	}
	
	private void updateCardinality(Zone zone) {
		long cardinality = 0;
		for (Zone childZone : zone.getChildren()) {
			Value value = childZone.getZMI().getAttributeValue("cardinality");
			if (value == null || !(value instanceof IntegerValue)) {
				zone.getZMI().setAttribute("cardinality", new IntegerValue(0));
				return;
			}
			cardinality += ((IntegerValue) value).getInteger();
		}
		zone.getZMI().setAttribute("cardinality", new IntegerValue(cardinality));
	}
	
	@SuppressWarnings("unchecked")
	private void updateContacts(Zone zone) {
		SetValue<ContactValue> contacts = SetValue.of(SimpleType.CONTACT);
		for (Zone childZone : zone.getChildren()) {
			Value value = childZone.getZMI().getAttributeValue("contacts");
			if (value == null || !value.getType().equals(SetType.of(SimpleType.CONTACT))) {
				continue;
			}
			contacts.getItems().addAll(((SetValue<ContactValue>) value).getItems());
		}
		
		while (contacts.size() > MAX_CONTACTS) {
			contacts.getItems().remove(contacts.getItems().iterator().next());
		}
		
		zone.getZMI().setAttribute("contacts", contacts);
	}
	
	private void executeQueries(Zone zone) {
		List<Attribute> attributes = new ArrayList<Attribute>();
		attributes.addAll(zone.getZMI().getAttributes());
		
		for (Attribute attribute : attributes) {
			if (AttributeNames.isSpecialName(attribute.getName())) {
				if (attribute.getValue() == null || !(attribute.getValue() instanceof StringValue)) {
					log.warn("Invalid special attribute %s when looking up for queries: %s",
							attribute.getName(), attribute.getValue());
					continue;
				}
				String query = ((StringValue) attribute.getValue()).getString();
				executeQuery(zone, query);
			}
		}
	}

	private void executeQuery(Zone zone, String query) {
		try {
			List<Stmt> stmts = Parsers.parseQuery(query);
			List<SelectionResult> result = new ArrayList<SelectionResult>();
			Env env;
			try {
				env = Env.createFromZMIs(zone.getChildZMIs());
			} catch (IllegalArgumentException e) {
				log.warn("Could not construct env for query (error: %s)", e.getMessage());
				return;
			}
			for (Stmt stmt : stmts) {
				result.addAll(stmt.executeSelection(env));
			}
			
			Set<String> seenNames = new HashSet<String>();
			for (SelectionResult selResult : result) {
				if (AttributeNames.isBuiltinName(selResult.getName())) {
					log.warn("Builtin attribute name %s in query: %s", selResult.getName(), query);
					return;
				}
				if (seenNames.contains(selResult.getName())) {
					log.warn("Duplicate attribute name %s in query: %s", selResult.getName(), query);
					return;
				}
				seenNames.add(selResult.getName());
			}
			
			ZMI zmi = zone.getZMI();
			for (SelectionResult selResult : result) {
				zmi.addAttribute(selResult.getName(), selResult.getType(), selResult.getValue());
			}
		} catch (ParseException e) {
			log.warn("Could not parse query: %s (error: %s)", query, e.getMessage());
		} catch (EvaluationException e) {
			log.warn("Could not evaluate query: %s (error: %s)", query, e.getMessage());
		}
	}

	private void refreshAncestor(Zone ancestorZone) {
		removeOutdatedAttributes(ancestorZone.getZMI());
		updateBuiltinAttributes(ancestorZone);
		executeQueries(ancestorZone);
	}
}
