package mchorse.metamorph.api.events;

import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.HashMap;
import java.util.Map;

/**
 * Register ramep event
 *
 * This event is fired when Metamorph is signaled to reload morph ID mappings.
 * During this event you can register names of morphs which you want
 * to be remapped to another names.
 */
public class RegisterRemapEvent extends Event
{
	/**
	 * Map of which morph IDs should be remapped
	 */
	public Map<String, String> map = new HashMap<String, String>();
}
