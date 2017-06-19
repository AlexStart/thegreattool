package com.sam.jcc.cloud.utils.project;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;

/**
 * Created by gladivs on 15.06.2017.
 */
/*
* The class checks if project or package name is eligible and meets specific requirements.
*
 */
public final class ProjectPackageHelper {

    private static final String ARTIFACT_REGEX = "^[a-z][a-z0-9_\\-.]+[a-z0-9]$|^[a-z][a-z0-9_]*[a-z0-9]*$";
    private static final String BASE_PACKAGE_PATTERNT = ARTIFACT_REGEX.replace("\\-", "");
    private static final String PACKAGE_PART_PATTERNT = BASE_PACKAGE_PATTERNT.replace(".", "");
    private static final Pattern FULL_PACKAGE_PATTERNT = Pattern.compile(BASE_PACKAGE_PATTERNT + "(\\." + BASE_PACKAGE_PATTERNT + ")*");

    private static Set<String> keywords;

    // The list of java keywords that cannot be used as identifiers
    // according to 3.9 Keywords chapter of Oracle doc.
    static {
        keywords = new HashSet<>();
        keywords.add("abstract");
        keywords.add("continue");
        keywords.add("for");
        keywords.add("new");
        keywords.add("switch");
        keywords.add("assert");
        keywords.add("default");
        keywords.add("if");
        keywords.add("package");
        keywords.add("synchronized");
        keywords.add("boolean");
        keywords.add("do");
        keywords.add("goto");
        keywords.add("private");
        keywords.add("this");
        keywords.add("break");
        keywords.add("double");
        keywords.add("implements");
        keywords.add("protected");
        keywords.add("throw");
        keywords.add("byte");
        keywords.add("else");
        keywords.add("import");
        keywords.add("public");
        keywords.add("throws");
        keywords.add("case");
        keywords.add("enum");
        keywords.add("instanceof");
        keywords.add("return");
        keywords.add("transient");
        keywords.add("catch");
        keywords.add("extends");
        keywords.add("int");
        keywords.add("short");
        keywords.add("try");
        keywords.add("char");
        keywords.add("final");
        keywords.add("interface");
        keywords.add("static");
        keywords.add("void");
        keywords.add("class");
        keywords.add("finally");
        keywords.add("long");
        keywords.add("strictfp");
        keywords.add("volatile");
        keywords.add("const");
        keywords.add("float");
        keywords.add("native");
        keywords.add("super");
        keywords.add("while");
    }


    private ProjectPackageHelper() {

    }

    public static boolean isProjectNameValid(String projectName) {
        if (isNull(projectName)) {
            return false;
        }
        if (!projectName.trim().toLowerCase().equals(projectName)) {
            return false;
        }
        if(projectName.startsWith("cloud")||projectName.startsWith("sam-checkstyle")) {
            return false;
        }
        if (!projectName.matches(ARTIFACT_REGEX)) {
            return false;
        }
        if(keywords.contains(projectName)) {
            return false;
        }
        return true;
    }

    /*
    * The method returns a java package with eligible\valid name.
     */
    public static String getValidProjectPackage(String identifier) {
        StringBuilder strBuilder = new StringBuilder();
        String trimmedIdentifier = !isNull(identifier) ? identifier.trim().toLowerCase() : null;
        if(isFullPackageNameValid(identifier)) {
            return trimmedIdentifier;
        }
        String[] parts = trimmedIdentifier.split("\\.", -1) ;
        for(String part : parts){
            if(!isNameOfPackagePartValid(part)){
                part = fixPackagePart(part);
            }
            if(!isEmpty(part) && !hasPackageNameKeyword(part)) {
                strBuilder.append(part).append(".");
            }
        }
        return strBuilder.toString().replaceFirst(".$", "");
    }

    public static boolean isFullPackageNameValid(String identifier) {
        if (isNull(identifier)) {
            return false;
        }
        return !hasPackageNameKeyword(identifier) && FULL_PACKAGE_PATTERNT.matcher(identifier).matches() && !identifier.contains("..");
    }

    public static boolean isNameOfPackagePartValid(String part) {
        if (isNull(part)) {
            return false;
        }
        return part.matches(PACKAGE_PART_PATTERNT);
    }

    /*
    * The method tries to fix the name of a package part to make it eligible\valid.
     */
    private static String fixPackagePart(String identifier){
        StringBuilder strBuilder = new StringBuilder();
        for(int i=0; i<identifier.length(); i++){
            String s = String.valueOf(identifier.charAt(i)).toLowerCase();
            if(s.matches(PACKAGE_PART_PATTERNT)){
                strBuilder.append(s);
            }
        }
        return strBuilder.toString();
    }

    private static boolean hasPackageNameKeyword(String name){
        String[] parts = name.split("\\.", -1);
        for(String part : parts) {
            if(keywords.contains(part)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isEmpty(String param){
        return (param == null || param.length() == 0);
    }

}
