package br.com.ilhasoft.push.java_wrapper.validations;

import java.util.Map;

import br.com.ilhasoft.push.java_wrapper.models.FlowDefinition;
import br.com.ilhasoft.push.java_wrapper.models.FlowRuleTest;
import br.com.ilhasoft.push.java_wrapper.models.RulesetResponse;

/**
 * Created by johncordeiro on 15/10/15.
 */
public class FieldStartsValidation implements FlowRuleValidation {

    @Override
    public boolean validate(FlowDefinition flowDefinition, RulesetResponse response) {
        FlowRuleTest flowRuleTest = response.getRule().getTest();
        Map<String, String> object = (Map<String, String>) flowRuleTest.getTest();
        return response.getResponse().startsWith(object.get(flowDefinition.getBaseLanguage()));
    }

}
