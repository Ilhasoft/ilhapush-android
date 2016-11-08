package br.com.ilhasoft.push.java_wrapper.validations;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import br.com.ilhasoft.push.java_wrapper.managers.FlowRunnerManager;
import br.com.ilhasoft.push.java_wrapper.models.FlowDefinition;
import br.com.ilhasoft.push.java_wrapper.models.RulesetResponse;

/**
 * Created by johncordeiro on 15/10/15.
 */
public class DateEqualValidation extends DateValidation implements FlowRuleValidation {

    @Override
    public boolean validate(FlowDefinition flowDefinition, RulesetResponse response) {
        try {
            Integer timeDelta = getTimeDeltaValue(response);
            Date deltaTime = getDeltaTime(timeDelta);

            Date date = FlowRunnerManager.getDefaultDateFormat().parse(response.getResponse());
            return isDateEquals(date, deltaTime);
        } catch(ParseException | NumberFormatException exception) {
            return false;
        }
    }

    private boolean isDateEquals(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(date1);
        calendar2.setTime(date2);
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR);
    }
}
