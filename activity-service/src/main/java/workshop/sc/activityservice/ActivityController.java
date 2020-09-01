package workshop.sc.activityservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import workshop.sc.Response;

import java.util.Random;

@RestController
public class ActivityController {

    @Value("${activities}")
    private String [] activities;
    @Value("${spring.application.name}")
    private String serviceName;

    @GetMapping("/activity")
    public Response decision() {
        return getResponseWithRandomDecision();
    }


    private Response getResponseWithRandomDecision() {
        String msg = activities[new Random().nextInt(activities.length)];
        Response response = new Response(serviceName.toUpperCase(), msg);
        return response;
    }
}
