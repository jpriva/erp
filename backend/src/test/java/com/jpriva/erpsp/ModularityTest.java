package com.jpriva.erpsp;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

public class ModularityTest {
    @Test
    void verifyModularity() {
        ApplicationModules.of(ErpSpringbootApplication.class).verify();
    }

    @Test
    void writeDocumentationSnippets() {
        new Documenter(ApplicationModules.of(ErpSpringbootApplication.class))
                .writeDocumentation()
                .writeIndividualModulesAsPlantUml();
    }
}
