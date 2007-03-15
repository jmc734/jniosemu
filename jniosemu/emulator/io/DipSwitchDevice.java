package jniosemu.emulator.io;

import java.util.Vector;
import jniosemu.emulator.memory.MemoryManager;
import jniosemu.events.Events;
import jniosemu.events.EventManager;
import jniosemu.events.EventObserver;
/**
 * Handle the dipswitches
 */
public class DipSwitchDevice extends IODevice implements EventObserver
{
	/**
	 * Address to memory where this is placed
	 */
	public static int MEMORYADDR = 0x850;
	/**
	 * Length of memory that is used
	 */
	public static int MEMORYLENGTH = 16;
	/**
	 * Number of dipswitches
	 */
	public static int COUNT = 4;
	/**
	 * Containing the states of each dipswitch
	 */
	private Vector<Boolean> state;
	/**
	 * Used MemoryManager
	 */
	private MemoryManager memory;
	/**
	 * Used EventManager
	 */
	private EventManager eventManager;

	/**
	 * Init DipSwitchDevice
	 *
	 * @post Add events. Init states.
	 * @calledby IOManager.reset()
	 *
	 * @param memory  current MemoryManager
	 * @param eventManager current EventManager
	 */
	public DipSwitchDevice(MemoryManager memory, EventManager eventManager) {
		this.reset(memory, eventManager);
	}

	/**
	 * Reset
	 *
	 * @calledby  DipSwitchDevice()
	 *
	 * @param memory current MemoryManager
	 * @param eventManager current EventManager
	 */
	public void reset(MemoryManager memory, EventManager eventManager) {
		this.memory = memory;
		this.eventManager = eventManager;

		this.eventManager.addEventObserver(Events.EVENTID_GUI_DIPSWITCHES, this);
		this.memory.register("DipSwitches", MEMORYADDR, MEMORYLENGTH, this);

		this.state = new Vector<Boolean>(COUNT);
		for (int i = 0; i < COUNT; i++)
			this.state.add(i, false);

		this.sendEvent();
	}

	/**
	 * When memory change in in this region this method is called. And then we
	 * want to restore the memory.
	 *
	 * @calledby MemoryManager.memoryChange()
	 */
	public void memoryChange() {
		this.memory.writeInt(MEMORYADDR     , this.vectorToInt(this.state), false);
		this.memory.writeInt(MEMORYADDR +  4, 0, false);
		this.memory.writeInt(MEMORYADDR +  8, 0, false);
		this.memory.writeInt(MEMORYADDR + 12, 0, false);
	}

	/**
	 * Send states to eventManager
	 *
	 * @calledby reset(), update()
	 */
	private void sendEvent() {
		this.eventManager.sendEvent(Events.EVENTID_UPDATE_DIPSWITCHES, this.state);
	}

	public void update(String eventIdentifier, Object obj) {
		if (eventIdentifier.equals(Events.EVENTID_GUI_DIPSWITCHES)) {
			System.out.println("Event mottaget!");
			int index = ((Integer)obj).intValue();
			this.state.set(index, !this.state.get(index));
			this.memory.writeInt(MEMORYADDR, this.vectorToInt(this.state), false);

			this.sendEvent();
		}
	}
}
