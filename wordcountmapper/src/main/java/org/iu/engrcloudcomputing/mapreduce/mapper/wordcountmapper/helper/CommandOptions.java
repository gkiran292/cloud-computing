package org.iu.engrcloudcomputing.mapreduce.mapper.wordcountmapper.helper;

import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings("unchecked")
public class CommandOptions {
    private ArrayList arguments;

    public CommandOptions(String[] args) {
        parse(args);
    }

    private void parse(String[] args) {
        arguments = new ArrayList();
        Collections.addAll(arguments, args);
    }

    public int size() {
        return arguments.size();
    }

    public boolean hasOption(String option) {
        boolean hasValue = false;
        String str;
        for (Object argument : arguments) {
            str = (String) argument;
            if (str.equalsIgnoreCase(option)) {
                hasValue = true;
                break;
            }
        }
        return hasValue;
    }

    public String valueOf(String option) {
        String value = null;
        String str;
        for (int i = 0; i < arguments.size(); i++) {
            str = (String) arguments.get(i);
            if (str.equalsIgnoreCase(option)) {
                value = (String) arguments.get(i + 1);
                break;
            }
        }
        return value;
    }
}