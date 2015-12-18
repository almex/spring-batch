/*
 * Copyright 2006-2007 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.springframework.util.PropertiesPersister;
import org.springframework.util.StringUtils;

/**
 * Utility to convert a Properties object to a String and back. Ideally this
 * utility should have been used to convert to string in order to convert that
 * string back to a Properties Object. Attempting to convert a string obtained
 * by calling Properties.toString() will return an invalid Properties object.
 * The format of Properties is that used by {@link PropertiesPersister} from the
 * Spring Core, so a String in the correct format for a Spring property editor
 * is fine (key=value pairs separated by new lines).
 * 
 * @author Lucas Ward
 * @author Dave Syer
 * @author Alexis Soumagne
 */
public final class PropertiesConverter {

    private static final String LINE_SEPARATOR = "\n";

    private static final String KEY_VALUE_SEPARATOR = "=";
    
    private static final String COMMA = ",";

    // prevents the class from being instantiated
    private PropertiesConverter() {
    };

    /**
     * Parse a String to a Properties object. If string is null, an empty
     * Properties object will be returned. The input String is a set of
     * name=value pairs, delimited by either newline or comma (for brevity). If
     * the input String contains a newline it is assumed that the separator is
     * newline, otherwise comma.
     * 
     * @param stringToParse String to parse.
     * @return Properties parsed from each string.
     * @see PropertiesPersister
     */
    public static Properties stringToProperties(String stringToParse) {
        Properties result = new Properties();

        if (StringUtils.hasText(stringToParse)) {
            String[] keyValues = new String[]{stringToParse};

            if (contains(stringToParse, LINE_SEPARATOR)) {
                keyValues = StringUtils.delimitedListToStringArray(stringToParse, LINE_SEPARATOR);
            } else if (contains(stringToParse, COMMA)) {
                keyValues = StringUtils.commaDelimitedListToStringArray(stringToParse);
            }

            for (String keyValue : keyValues) {
                // It's important to trim after finding separator because method 
                // below also remove '\n' character.
                String trimedString = StringUtils.trimLeadingWhitespace(keyValue);
                String[] keyValuePair = StringUtils.delimitedListToStringArray(trimedString, KEY_VALUE_SEPARATOR);

                // We silently do not take into account any unparsable keyValue
                if (keyValuePair.length == 2) {
                    String key = keyValuePair[0];
                    String value = keyValuePair[1];
                    
                    result.setProperty(key, value);
                }
            }
        }

        return result;
    }

    /**
     * Convert Properties object to String. This is only necessary for
     * compatibility with converting the String back to a properties object. If
     * an empty properties object is passed in, a blank string is returned,
     * otherwise it's string representation is returned.
     * 
     * @param propertiesToParse
     * @return String representation of properties object
     */
    public static String propertiesToString(Properties propertiesToParse) {
        String result = "";

        // If properties is empty, return a blank string.
        if (propertiesToParse != null && propertiesToParse.size() != 0) {

            List<String> list = new ArrayList<String>();
            for (Entry<Object, Object> entry : propertiesToParse.entrySet()) {
                StringBuilder builder = new StringBuilder();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();

                builder.append(key);
                builder.append(KEY_VALUE_SEPARATOR);
                builder.append(value);

                list.add(builder.toString());
            }

            String tempValue = StringUtils.collectionToCommaDelimitedString(list);
            int count = StringUtils.countOccurrencesOf(tempValue, COMMA);
            if (count == list.size() - 1) {
                result = tempValue;
            }
            if (result.endsWith(COMMA)) {
                result = result.substring(0, result.length() - 1);
            }
        }

        return result;
    }

    private static boolean contains(String str, String searchStr) {
        return str.indexOf(searchStr) != -1;
    }
}
