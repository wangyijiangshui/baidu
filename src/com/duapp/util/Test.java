package com.duapp.util;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Test {

	public static void main(String[]sraga) throws IOException {
		String xmlDoc = "({\"Data\":[[[],]]});"; //({"Data":[[[1542]]]});
		xmlDoc = xmlDoc.replace("({\"Data\":[[[", "").replace("]]]});", "");
		System.out.println(xmlDoc);
	}
}
