package br.com.ilhasoft.push.java_wrapper.validations;

import br.com.ilhasoft.push.java_wrapper.models.FlowDefinition;
import br.com.ilhasoft.push.java_wrapper.models.RulesetResponse;

/**
 * Created by johncordeiro on 15/10/15.
 */
public class NumberLessThanValidation implements FlowRuleValidation {

    @Override
    public boolean validate(FlowDefinition flowDefinition, RulesetResponse response) {
        try {
            String testValue = response.getRule().getTest().getTest().values().iterator().next();

            Integer value = Integer.valueOf(response.getResponse());
            return value < Integer.valueOf(testValue);
        } catch(NumberFormatException exception) {
            return false;
        }
    }
}
