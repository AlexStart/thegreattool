package com.sam.jcc.cloud.utils.project;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by gladivs on 15.06.2017.
 */
public class ProjectPackageHelperTest {
    /*
    * The method checks eligible patterns for java package names.
    */
    @Test
    public void fullPackageNameValidationTest() {
        // Valid patterns for java package names
        assertThat(ProjectPackageHelper.isFullPackageNameValid("a_bc")).isTrue();
        assertThat(ProjectPackageHelper.isFullPackageNameValid("cc")).isTrue();
        assertThat(ProjectPackageHelper.isFullPackageNameValid("a.b.c_c")).isTrue();
        assertThat(ProjectPackageHelper.isFullPackageNameValid("test.app")).isTrue();
        // Invalid patterns for java package names
        assertThat(ProjectPackageHelper.isFullPackageNameValid("C")).isFalse();
        assertThat(ProjectPackageHelper.isFullPackageNameValid("Cc")).isFalse();
        assertThat(ProjectPackageHelper.isFullPackageNameValid("b.Cc")).isFalse();
        assertThat(ProjectPackageHelper.isFullPackageNameValid("aAa.b.Cc")).isFalse();
        assertThat(ProjectPackageHelper.isFullPackageNameValid("a.b.Cc")).isFalse();
        assertThat(ProjectPackageHelper.isFullPackageNameValid("b.C")).isFalse();
        assertThat(ProjectPackageHelper.isFullPackageNameValid("a.b.C_c")).isFalse();
        assertThat(ProjectPackageHelper.isFullPackageNameValid(".C")).isFalse();
        assertThat(ProjectPackageHelper.isFullPackageNameValid("b..C")).isFalse();
        assertThat(ProjectPackageHelper.isFullPackageNameValid("b.9C")).isFalse();
        assertThat(ProjectPackageHelper.isFullPackageNameValid("c.a-bc")).isFalse();
        assertThat(ProjectPackageHelper.isFullPackageNameValid("a.b.C$c")).isFalse();
        assertThat(ProjectPackageHelper.isFullPackageNameValid("a.b.C9")).isFalse();
        assertThat(ProjectPackageHelper.isFullPackageNameValid(".test.app")).isFalse();
        assertThat(ProjectPackageHelper.isFullPackageNameValid("test.app.")).isFalse();
        assertThat(ProjectPackageHelper.isFullPackageNameValid("test.app..")).isFalse();
        assertThat(ProjectPackageHelper.isFullPackageNameValid("..test.app")).isFalse();
        assertThat(ProjectPackageHelper.isFullPackageNameValid("test..app")).isFalse();
        // Invalid patterns for java package names with java keywords
        assertThat(ProjectPackageHelper.isFullPackageNameValid("default")).isFalse();
        assertThat(ProjectPackageHelper.isFullPackageNameValid("return.abstract")).isFalse();
        assertThat(ProjectPackageHelper.isFullPackageNameValid("app.class")).isFalse();
    }


    /*
    * The method checks the results of getValidProjectPackage method.
    */
    @Test
    public void packageNameCreationTest() {
        assertThat(ProjectPackageHelper.getValidProjectPackage("C")).isEqualTo("c");
        assertThat(ProjectPackageHelper.getValidProjectPackage("Cc")).isEqualTo("cc");
        assertThat(ProjectPackageHelper.getValidProjectPackage("b.Cc")).isEqualTo("b.cc");
        assertThat(ProjectPackageHelper.getValidProjectPackage("aAa.b.Cc")).isEqualTo("aaa.b.cc");
        assertThat(ProjectPackageHelper.getValidProjectPackage("a.b.Cc")).isEqualTo("a.b.cc");
        assertThat(ProjectPackageHelper.getValidProjectPackage("b.C")).isEqualTo("b.c");
        assertThat(ProjectPackageHelper.getValidProjectPackage("a.b.C_c")).isEqualTo("a.b.c_c");
        assertThat(ProjectPackageHelper.getValidProjectPackage("aaa.b.cc")).isEqualTo("aaa.b.cc");
        assertThat(ProjectPackageHelper.getValidProjectPackage("a.b.c_c")).isEqualTo("a.b.c_c");
        assertThat(ProjectPackageHelper.getValidProjectPackage("test.app")).isEqualTo("test.app");
        assertThat(ProjectPackageHelper.getValidProjectPackage("test..app")).isEqualTo("test.app");
        assertThat(ProjectPackageHelper.getValidProjectPackage(".test.app")).isEqualTo("test.app");
        assertThat(ProjectPackageHelper.getValidProjectPackage("test.app.")).isEqualTo("test.app");
        assertThat(ProjectPackageHelper.getValidProjectPackage("test.app..")).isEqualTo("test.app");
        assertThat(ProjectPackageHelper.getValidProjectPackage("..test.app")).isEqualTo("test.app");
        assertThat(ProjectPackageHelper.getValidProjectPackage("default")).isEqualTo("");
        assertThat(ProjectPackageHelper.getValidProjectPackage("return.abstract")).isEqualTo("");
        assertThat(ProjectPackageHelper.getValidProjectPackage("app.class")).isEqualTo("app");
    }

    /*
    * The method checks if a java project name meets :
    * @ the pattern like '^[a-z][a-z0-9_\-.]+[a-z0-9]$';
    * @ not to start with 'cloud' and 'sam-checkstyle';
    * @ not a java keyword.
    */
    @Test
    public void projectNameValidationTest() {
        //Valid patterns for java project names
        assertThat(ProjectPackageHelper.isProjectNameValid("t")).isTrue();
        assertThat(ProjectPackageHelper.isProjectNameValid("my-project")).isTrue();
        assertThat(ProjectPackageHelper.isProjectNameValid("com.samsolutions.my-project")).isTrue();
        assertThat(ProjectPackageHelper.isProjectNameValid("test9.app")).isTrue();
        assertThat(ProjectPackageHelper.isProjectNameValid("test.9app")).isTrue();
        assertThat(ProjectPackageHelper.isProjectNameValid("test.app9")).isTrue();
        assertThat(ProjectPackageHelper.isProjectNameValid("app.cloud")).isTrue();
        assertThat(ProjectPackageHelper.isProjectNameValid("app_cloud")).isTrue();
        assertThat(ProjectPackageHelper.isProjectNameValid("appcloud")).isTrue();
        assertThat(ProjectPackageHelper.isProjectNameValid("app_sam-checkstyle")).isTrue();
        //Invalid patterns for java project names
        assertThat(ProjectPackageHelper.isProjectNameValid("9")).isFalse();
        assertThat(ProjectPackageHelper.isProjectNameValid("-")).isFalse();
        assertThat(ProjectPackageHelper.isProjectNameValid("_")).isFalse();
        assertThat(ProjectPackageHelper.isProjectNameValid(".")).isFalse();
        assertThat(ProjectPackageHelper.isProjectNameValid("test.app.")).isFalse();
        assertThat(ProjectPackageHelper.isProjectNameValid("test.app-")).isFalse();
        assertThat(ProjectPackageHelper.isProjectNameValid("my%project")).isFalse();
        assertThat(ProjectPackageHelper.isProjectNameValid("my$project")).isFalse();
        assertThat(ProjectPackageHelper.isProjectNameValid("my*project")).isFalse();
        assertThat(ProjectPackageHelper.isProjectNameValid(".test.app")).isFalse();
        assertThat(ProjectPackageHelper.isProjectNameValid("-test.app")).isFalse();
        assertThat(ProjectPackageHelper.isProjectNameValid("_test.app")).isFalse();
        assertThat(ProjectPackageHelper.isProjectNameValid("9test.app")).isFalse();
        assertThat(ProjectPackageHelper.isProjectNameValid("cloud")).isFalse();
        assertThat(ProjectPackageHelper.isProjectNameValid("sam-checkstyle")).isFalse();
        assertThat(ProjectPackageHelper.isProjectNameValid("cloud.app")).isFalse();
        assertThat(ProjectPackageHelper.isProjectNameValid("cloudapp")).isFalse();
        assertThat(ProjectPackageHelper.isProjectNameValid("sam-checkstyle_app")).isFalse();
        assertThat(ProjectPackageHelper.isProjectNameValid("Test.app")).isFalse();
        assertThat(ProjectPackageHelper.isProjectNameValid("test.App")).isFalse();
        assertThat(ProjectPackageHelper.isProjectNameValid("test.apP")).isFalse();
        // Invalid usage of the java keywords in project name
        assertThat(ProjectPackageHelper.isProjectNameValid("return")).isFalse();
        assertThat(ProjectPackageHelper.isProjectNameValid("class")).isFalse();
        assertThat(ProjectPackageHelper.isProjectNameValid("const")).isFalse();
        // Valid usage of the java keywords in project name
        assertThat(ProjectPackageHelper.isProjectNameValid("return.random")).isTrue();
        assertThat(ProjectPackageHelper.isProjectNameValid("my-class")).isTrue();
        assertThat(ProjectPackageHelper.isProjectNameValid("const_generator")).isTrue();
    }

}
