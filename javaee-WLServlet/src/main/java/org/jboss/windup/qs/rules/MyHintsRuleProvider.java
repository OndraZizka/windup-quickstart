package org.jboss.windup.qs.rules;

import java.util.Collections;
import java.util.List;

import javax.inject.Singleton;

import org.jboss.windup.config.WindupRuleProvider;
import org.jboss.windup.config.metadata.RuleMetadata;
import org.jboss.windup.config.operation.Iteration;
import org.jboss.windup.graph.GraphContext;
import org.jboss.windup.reporting.config.Classification;
import org.jboss.windup.reporting.config.Hint;
import org.jboss.windup.reporting.config.Link;
import org.jboss.windup.rules.apps.java.condition.JavaClass;
import org.jboss.windup.rules.apps.java.scan.ast.TypeReferenceLocation;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;


/**
 *
 *  @author Ondrej Zizka, ozizka at redhat.com
 */
@Singleton
public class MyHintsRuleProvider extends WindupRuleProvider {
    @Override
    public List<Class<? extends WindupRuleProvider>> getExecuteAfter()
    {
        return Collections.emptyList();
        // Returning a value here will cause the Rules from this provider to execute after the rules
        //  from the providers in the list. 
        //
        // The following example specifies that this provider's rules should execute after the rules 
        // in AnalyzeJavaFilesRuleProvider. This is technically unnecessary, as the rules in 
        // are set to execute in an earlier phase, but is here only to demonstrate the concept.
        //
        // return asClassList(AnalyzeJavaFilesRuleProvider.class);
    }


    // @formatter:off
    @Override
    public Configuration getConfiguration(GraphContext context)
    {
        return ConfigurationBuilder.begin()
            .addRule()
            .when(
                JavaClass.references("weblogic.servlet.annotation.WLServlet").at(TypeReferenceLocation.ANNOTATION).as("ann")
            )
            .perform(
                Iteration.over().perform(
                    Classification.of("ann").as("WebLogic @WLServlet")
                       .with(Link.to("Java EE 6 @WebServlet", "https://access.redhat.com/documentation/en-US/JBoss_Enterprise_Application_Platform/index.html"))
                       .withEffort(0)
                    .and(Hint.in("ann").withText("Migrate to Java EE 6 @WebServlet.").withEffort(8))
                )
                .endIteration()
            ).withMetadata(RuleMetadata.CATEGORY, "Java");
    }
    // @formatter:on

}
