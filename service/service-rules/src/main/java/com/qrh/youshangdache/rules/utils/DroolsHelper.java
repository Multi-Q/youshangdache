package com.qrh.youshangdache.rules.utils;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

/**
 * @author QRH
 * @date 2024/8/22 17:06
 * @description TODO
 */
public class DroolsHelper {
    private static final String RULES_CUSTOMER_RULES_DRL = "rules/FeeRule.drl";

    public static KieSession loadForRule(String drlStr) {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource(drlStr));
        KieBuilder kb = kieServices.newKieBuilder(kieFileSystem);
        kb.buildAll();
        KieModule kieModule = kb.getKieModule();
        KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());
        return kieContainer.newKieSession();
    }
}
