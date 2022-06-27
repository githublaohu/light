package com.lamp.light.util;

import java.util.ArrayList;
import java.util.List;

//import com.lamp.component.essential.util.Tuple.Triplet;

public class StringContent {

	private static final char LEFTPARENTHESIS = '{';
	
	private static final char RIGHTPARENTHESIS = '}';
	
//	private List<Triplet<String, String, Boolean>>  content = new ArrayList<>();
	
	
	
	public StringContent(String content) {
		String[] stringArray =  content.split("{");
		if(stringArray.length == 1) {
			
		}
		for(String string : stringArray) {
			
		}
	}
	
}
