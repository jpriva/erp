package com.jpriva.erpsp;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class ArchitectureTest {

    private final JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.jpriva.erpsp");

    @Test
    void domainOnlyDependsOnJavaLibrariesOrSharedDomain() {
        System.out.println("=== DOMAIN ARCHUNIT START ===");
        System.out.println("Total classes found: " + importedClasses.size());

        var domainClasses = importedClasses.stream()
                .filter(clazz -> clazz.getPackageName().contains(".domain"))
                .toList();

        System.out.println("domain classes found: " + domainClasses.size());

        if (domainClasses.isEmpty()) {
            throw new RuntimeException("¡ERROR! No classes found in .domain package. Check 'importPackages' string");
        }
        AtomicBoolean validArch = new AtomicBoolean(true);
        domainClasses.forEach(clazz -> {
            String module = clazz.getPackageName().split("\\.")[3];
            clazz.getDirectDependenciesFromSelf()
                    .forEach(dep -> {
                        String dependency = dep.getTargetClass().getPackageName();
                        if (!(
                                dependency.startsWith("java.") ||
                                        dependency.startsWith("com.jpriva.erpsp." + module + ".domain") ||
                                        dependency.startsWith("com.jpriva.erpsp.shared.domain")
                        )) {
                            System.out.println("\n*** Module:" + module.toUpperCase() + " *** CLASS: " + clazz.getName());
                            System.out.println("   -> Depends on: " + dep.getTargetClass().getName());
                            System.out.println("      (Type: " + dep.getDescription() + ")");
                            validArch.set(false);
                        }
                    });
        });
        if (!validArch.get()) {
            throw new RuntimeException("Architecture validation failed, Domain should not depend on other " +
                    "modules or libraries, just shared domain and java libraries. Check logs for details.");
        }
        System.out.println("=== DOMAIN ARCHUNIT END ===");
    }

    @Test
    void applicationOnlyDependsOnJavaLibrariesOrSharedDomain() {
        System.out.println("=== APPLICATION ARCHUNIT START ===");
        System.out.println("Total classes found: " + importedClasses.size());

        var applicationClasses = importedClasses.stream()
                .filter(clazz -> clazz.getPackageName().contains(".application"))
                .toList();

        System.out.println("Application classes found: " + applicationClasses.size());

        if (applicationClasses.isEmpty()) {
            throw new RuntimeException("¡ERROR! No classes found in .application package. Check 'importPackages' string");
        }
        AtomicBoolean validArch = new AtomicBoolean(true);
        applicationClasses.forEach(clazz -> {
            String module = clazz.getPackageName().split("\\.")[3];
            clazz.getDirectDependenciesFromSelf()
                    .forEach(dep -> {
                        String dependency = dep.getTargetClass().getPackageName();
                        if (!(
                                dependency.startsWith("java.") ||
                                        dependency.startsWith("com.jpriva.erpsp." + module + ".domain") ||
                                        dependency.startsWith("com.jpriva.erpsp.shared.domain") ||
                                        dependency.startsWith("com.jpriva.erpsp." + module + ".application") ||
                                        dependency.startsWith("com.jpriva.erpsp.shared.application")
                        )) {
                            System.out.println("\\n*** Module: \" + module.toUpperCase() *** CLASS: " + clazz.getName());
                            System.out.println("   -> Depends on: " + dep.getTargetClass().getName());
                            System.out.println("      (Type: " + dep.getDescription() + ")");
                            validArch.set(false);
                        }
                    });
        });
        if (!validArch.get()) {
            throw new RuntimeException("Architecture validation failed, Application should not depend on other " +
                    "modules or libraries, just shared domain and application and java libraries. Check logs for details.");
        }
        System.out.println("=== APPLICATION ARCHUNIT END ===");
    }
}
