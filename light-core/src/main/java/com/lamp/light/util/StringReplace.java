package com.lamp.light.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StringReplace {

    private List<Paragraph> paragraphList = new ArrayList<>();


	public StringReplace(String data) {
		String[] stringArray  = data.split("\\{");
		for(int i = 0 ; i< stringArray.length ; i++) {
			String splitString = stringArray[i];
			if(Objects.equals(splitString, "")) {
				continue;
			}else {
				if(splitString.indexOf('}') == -1) {
					Paragraph paragraph = new Paragraph();
					paragraph.splitString = splitString;
					paragraphList.add(paragraph);
					continue;
				}
			}
			String[] tmp = splitString.split("}");
			Paragraph paragraph = new Paragraph();
			paragraph.key = tmp[0];
			paragraphList.add(paragraph);

			if(tmp.length == 2) {
				paragraph = new Paragraph();
				paragraph.splitString = tmp[1];
				paragraphList.add(paragraph);
			}
		}
	}

    public String replace(Map<String, String> values) {
        StringBuffer sb = new StringBuffer();
        for (Paragraph paragraph : paragraphList) {
            if (Objects.nonNull(paragraph.splitString)) {
                sb.append(paragraph.splitString);
            } else {
                sb.append(values.get(paragraph.key));
            }
        }
        return sb.toString();
    }

    public String replace(List<String> values) {
        StringBuffer sb = new StringBuffer();
        int i = 0;
        for (Paragraph paragraph : paragraphList) {
            if (Objects.nonNull(paragraph.splitString)) {
                sb.append(paragraph.splitString);
            } else {
                sb.append(values.get(i++));
            }
        }
        return sb.toString();
    }

    public String replaceObject(Object values) {
        return null;
    }

    private static class Paragraph {

        private String splitString;

        private String key;
    }
}
