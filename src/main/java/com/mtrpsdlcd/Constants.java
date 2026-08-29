package com.mtrpsdlcd;

import org.mtr.mapping.holder.Identifier;

public class Constants {
	public static final String MOD_ID = "mtr_psd_lcd";
	public static final String MOD_NAME = "屏蔽门";

	public static Identifier id(String id) {
		return new Identifier(MOD_ID, id);
	}
}
