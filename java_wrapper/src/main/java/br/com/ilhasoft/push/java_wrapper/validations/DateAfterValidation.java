package br.com.ilhasoft.push.java_wrapper.validations;

import java.text.ParseException;
import java.util.Date;

import br.com.ilhasoft.push.java_wrapper.managers.FlowRunnerManager;
import br.com.ilhasoft.push.java_wrapper.models.FlowDefinition;
import br.com.ilhasoft.push.java_wrapper.models.RulesetResponse;

/**
 * Created by johncordeiro on 15/10/15.
 */
public class DateAfterValidation extends DateValidation implements FlowRuleValidation {

    @Override
    public boolean validate(FlowDefinition flowDefinition, RulesetResponse response) {
        try {
            Integer timeDelta = getTimeDeltaValue(response);
            Date deltaTime = getDeltaTime(timeDelta);

            Date date = FlowRunnerManager.getDefaultDateFormat().parse(response.getResponse());
            return date.after(deltaTime);
        } catch(ParseException | NumberFormatException exception) {
            return false;
        }
    }

}
