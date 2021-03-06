package bkampfbot.modes;

/*
 Copyright (C) 2011  georf@georf.de

 This file is part of BKampfBot.

 BKampfBot is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 2 of the License, or
 any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import json.JSONException;
import json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import bkampfbot.Control;
import bkampfbot.Utils;
import bkampfbot.output.Output;
import bkampfbot.plan.PlanObject;
import bkampfbot.state.Config;

public final class Tagesspiel extends PlanObject {

	boolean zwerg = false;

	public static Tagesspiel getInstance() {
		return new Tagesspiel(new JSONObject());
	}
	
	public static Tagesspiel getInstance(boolean zwerg) {
		Tagesspiel t = getInstance();
		t.zwerg = zwerg;
		return t;
	}

	public Tagesspiel(JSONObject setup) {
		super("Tagesspiel");

		try {
			zwerg = setup.getBoolean("Zwergeneinsatz");
		} catch (JSONException e) {
		}
	}

	public void run() {
		printJump();

		String page = Utils.getString("city/index");

		// Komische Figur schon abgeschossen?
		if (page.indexOf("win: \"1\"") != -1) {
			Output.printTabLn("Versuche den Gegner in der Stadt abzuschießen.",
					1);

			Control.sleep(30);

			List<NameValuePair> nvps2 = new ArrayList<NameValuePair>();
			nvps2.add(new BasicNameValuePair("hallo", "1"));

			String resulte = Utils.getString("city/winning", nvps2);
			if (resulte.equals("{\"success\":1}")) {
				Output.printTabLn("10 Fleispunkte bekommen", Output.INFO);
			}
		}

		try {

			page = Utils.getString("games");

			if (!zwerg
					&& page.indexOf("<img src=\"" + Config.getHost()
							+ "layout/game.png\"") == -1) {
				Output.printTabLn("Tagesspiel schon erledigt.", Output.INFO);
				return;
			}

			if (zwerg
					&& page.indexOf("<img src=\"" + Config.getHost()
							+ "layout/game2.png\"") == -1 && page.indexOf("<img src=\"" + Config.getHost()
									+ "layout/game.png\"") == -1) {
				Output.printTabLn(
						"Maximale Anzahl der Spiele erreicht für heute!", Output.INFO);
				return;
			}

			if (page.indexOf("<img src=\"" + Config.getHost()
					+ "layout/game.png\"") == -1) {
				Output.printTabLn("Tagesspiel für 1 Zwerg.", Output.INFO);
				page = page.substring(page.indexOf("<img src=\""
						+ Config.getHost() + "layout/game2.png\"") - 50);

			} else {

				page = page.substring(page.indexOf("<img src=\""
						+ Config.getHost() + "layout/game.png\"") - 50);
			}
			page = page.substring(0, 50);
			page = page.substring(page.indexOf("<a href=\""));
			page = page.replaceAll("[^0-9]", "");

			Utils.visit("games/play/" + page);
			Utils.visit("games/getGame");

			int wait = new Random().nextInt(30) + 30;

			Control.sleep(wait * 10, 2);

			final int fleis = 15;

			JSONObject result = Utils.getJSON("games/punkte/" + fleis + "/"
					+ ((1177 + fleis) * 177 + 77) * fleis);
			if (result.getInt("result") == 1) {
				Output.printTabLn("Tagesspiel gewonnen", Output.INFO);
			} else {
				Output.printTabLn("Tagesspiel verloren", Output.INFO);
			}
		} catch (JSONException e) {
			Output.error(e);
		}
	}
}