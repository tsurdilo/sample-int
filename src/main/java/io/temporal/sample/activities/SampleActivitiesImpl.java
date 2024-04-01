package io.temporal.sample.activities;

import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
import io.temporal.client.ActivityCompletionException;
import io.temporal.failure.ApplicationFailure;
import io.temporal.spring.boot.ActivityImpl;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Component
@ActivityImpl(taskQueues = "samplequeue")
public class SampleActivitiesImpl implements SampleActivities {
    @Override
    public String one() {
        sleep(1);
        return "done...";
    }

    @Override
    public String two() {
        sleep(1);
        return "done...";
    }

    @Override
    public String three() {
        sleep(1);
//        return "done...";
        throw ApplicationFailure.newNonRetryableFailure("simulated activity failure from three",
                "some error", null);
    }

    @Override
    public String four() {
        ActivityExecutionContext context = Activity.getExecutionContext();
        for (int i = 0; i < 6; i++) {
            sleep(1);
            try {
                // Perform the heartbeat. Used to notify the workflow that activity execution is alive
                context.heartbeat(i);
            } catch (ActivityCompletionException e) {
                System.out.println("******** ACTIVITY FOUR CANCELING");
                throw e;
            }
        }
        return "done...";
    }

    @Override
    public String five() {
        sleep(1);
        return "done...";
    }


    private void sleep(int seconds) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
        } catch (InterruptedException ee) {
            // Empty
        }
    }
}
