package br.com.ilhasoft.push.java_wrapper.validations;

import br.com.ilhasoft.push.java_wrapper.models.FlowDefinition;
import br.com.ilhasoft.push.java_wrapper.models.RulesetResponse;

/**
 * Created by johncordeiro on 15/10/15.
 */
public interface FlowRuleValidation {

    boolean validate(FlowDefinition flowDefinition, RulesetResponse response);

}
