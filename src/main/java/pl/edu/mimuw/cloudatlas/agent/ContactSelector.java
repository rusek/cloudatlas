package pl.edu.mimuw.cloudatlas.agent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import pl.edu.mimuw.cloudatlas.attributes.ContactValue;
import pl.edu.mimuw.cloudatlas.attributes.SetType;
import pl.edu.mimuw.cloudatlas.attributes.SetValue;
import pl.edu.mimuw.cloudatlas.attributes.SimpleType;
import pl.edu.mimuw.cloudatlas.attributes.Value;
import pl.edu.mimuw.cloudatlas.zones.Zone;

public class ContactSelector {
	
	private static int NUM_TRIES = 10;
	private static double noContactChance = 0.2;
	
	private LevelSelectionStrategy levelSelectionStrategy;
	private Random random = new Random();

	public ContactSelector(String zoneName, Properties properties) {
		levelSelectionStrategy = LevelSelectionStrategy.createStrategy(zoneName, properties);
	}
	
	public ContactValue nextContact(Zone rootZone) {
		if (random.nextDouble() < noContactChance) {
			// One of fallback contacts should be used instead. Without this hack it's really hard
			// to achieve system consistency - when new agent receives ZMIs with contacts it will probably
			// never again use fallback contacts and they might be the only way to communicate with further
			// zones
			return null;
		}
		
		ContactValue result = null;
		for (int i = 0; i < NUM_TRIES && result == null; i++) {
			result = nextContactOnce(rootZone);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private ContactValue nextContactOnce(Zone rootZone) {
		String levelName = levelSelectionStrategy.nextLevel();
		Zone levelZone = rootZone.findZone(levelName);
		Set<ContactValue> contacts = new HashSet<ContactValue>();
		for (Zone siblingZone : levelZone.getParent().getChildren()) {
			if (siblingZone != levelZone) {
				Value value = siblingZone.getZMI().getAttributeValue("contacts");
				if (value != null && value.getType().equals(SetType.of(SimpleType.CONTACT))) {
					contacts.addAll(((SetValue<ContactValue>) value).getItems());
				}
			}
		}
		return randomItem(contacts);
	}
	
	private <T> T randomItem(Collection<T> collection) {
		if (collection.isEmpty()) {
			return null;
		} else {
			int index = random.nextInt(collection.size());
			for (T item : collection) {
				if (index == 0) {
					return item;
				} else {
					index--;
				}
			}
			throw new RuntimeException("Impossible happened");
		}
	}
}
