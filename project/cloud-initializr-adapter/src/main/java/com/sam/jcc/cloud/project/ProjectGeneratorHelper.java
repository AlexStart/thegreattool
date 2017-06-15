package com.sam.jcc.cloud.project;

import java.util.regex.Pattern;

/**
 * Created by gladivs on 15.06.2017.
 */
public final class ProjectGeneratorHelper {

    private static final String ID_PATTERN = "\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
    private static final Pattern FQCN = Pattern.compile(ID_PATTERN + "(\\." + ID_PATTERN + ")*");

    private ProjectGeneratorHelper (){

    }

    public static String getProjectPackage(String identifier) {
        return getValidJavaIdentifier(identifier);
    }

    public static boolean validateJavaIdentifier(String identifier) {
        return FQCN.matcher(identifier).matches();
    }

    private static String getValidJavaIdentifier(String identifier) {
        String trimmedIdentifier = identifier != null ? identifier.trim() : "";
        StringBuilder strBuilder = new StringBuilder();
        if(trimmedIdentifier.length() == 0){
            return "";
        }
        if(validateJavaIdentifier(trimmedIdentifier)) {
            return trimmedIdentifier;
        }
        String[]parts = trimmedIdentifier.split("\\.", -1) ;
        for(String part : parts){
            if(!validateJavaIdentifier(part)){
                part = fixJavaIdentifier(part);
            }
            if(!isEmpty(part)){
                strBuilder.append(part).append(".");
            }
        }
        return strBuilder.toString().replaceFirst(".$", "");
    }

    private static String fixJavaIdentifier(String identifier){
        StringBuilder strBuilder = new StringBuilder();
        for(int i=0; i<identifier.length(); i++){
            char c = identifier.charAt(i);
            if(Character.isJavaIdentifierPart(c)){
                strBuilder.append(c);
            }
        }
        return strBuilder.toString();
    }

    private static boolean isEmpty(String param){
        return (param == null || param.length() == 0);
    }

}
