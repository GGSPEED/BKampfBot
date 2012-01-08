package bkampfbot.bundesklatsche.field;

import json.JSONException;
import json.JSONObject;
import bkampfbot.output.Output;

public class EreignisField extends Field {

	public EreignisField(JSONObject result) {
		super(result);
	}

	@Override
	public boolean action() throws JSONException {
/*"action": {
        "cont": "",
        "text": "Auch wenn es Bogdan nicht gerne sieht: Du verkaufst Rubbellose zu Gunsten der Aktion Morgenwind. Deine Einnahmen erreichen bereits am ersten Tag:",
        "bonus": "1",
        "gold": 1702,
        "klatschen": 5
    },*/
		JSONObject action = result.getJSONObject("action");
		
		
		Output.printClock("Ereignis - ", Output.INFO);
		
		if (action.getString("bonus").equals("1")) {
			Output.println("gewonnen ("+action.getInt("gold")+" DM, "+action.getInt("klatschen")+" Klatschen)", Output.INFO);
			return true;
		} else {
			
			// TODO Nicht implementiert

			Output.printTabLn("Nicht implementiert: "
					+ this.getClass().getSimpleName(), Output.ERROR);
			return false;
			
		}
	}
}