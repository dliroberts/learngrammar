package simplenlg.lexicon.verbnet;

import java.util.ArrayList;
import java.util.List;

public class VerbnetFrame {

	private List<VerbnetSlot> slots;
	private String mainDescription;
	private String subDescription;

	public VerbnetFrame() {
		this.slots = new ArrayList<VerbnetSlot>();
	}

	public int getSlotCount() {
		return this.slots.size();
	}

	public void addSlot(VerbnetSlotType type, boolean opt, String... contents) {
		VerbnetSlot slot = new VerbnetSlot(type);

		for (String s : contents) {
			slot.addContent(s);
		}

		this.slots.add(slot);
	}

	public void addSlot(VerbnetSlotType type, String... contents) {
		addSlot(type, false, contents);
	}

	public void addSlots(VerbnetSlot... vfslots) {
		for (VerbnetSlot slot : vfslots) {
			this.slots.add(slot);
		}
	}

	public String getMainDescription() {
		return this.mainDescription;
	}

	public void setMainDescription(String mainDescription) {
		this.mainDescription = mainDescription;
	}

	public String getSubDescription() {
		return this.subDescription;
	}

	public void setSubDescription(String subDescription) {
		this.subDescription = subDescription;
	}

}
