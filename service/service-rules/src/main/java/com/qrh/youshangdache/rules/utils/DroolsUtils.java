package com.qrh.youshangdache.rules.utils;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

import java.util.HashMap;

/**
 * @author QRH
 * @date 2024/8/22 17:06
 * @description TODO
 */
public class DroolsUtils {

    /**
     * 加载规则文件
     *
     * @return KieContainer
     */
    public static KieSession loadForRule(String ruleFilePath) {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem()
                .write(ResourceFactory.newClassPathResource(ruleFilePath));
        KieModule kieModule = kieServices.newKieBuilder(kieFileSystem)
                .buildAll()
                .getKieModule();
        KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());
        return kieContainer.newKieSession();
    }


}
