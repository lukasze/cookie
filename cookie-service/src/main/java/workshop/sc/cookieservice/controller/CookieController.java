package workshop.sc.cookieservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import workshop.sc.Response;
import workshop.sc.cookieservice.activity.ActivityClient;
import workshop.sc.cookieservice.decision.DecisionClient;

@RestController
public class CookieController {
    private final ActivityClient activity;
    private final DecisionClient decision;

    @Value("${spring.application.name}")
    private String serviceName;

    public CookieController(ActivityClient activity, DecisionClient decision) {
        this.activity = activity;
        this.decision = decision;
    }

    @GetMapping("/fortune")
    public Response fortune() {
        return getResponse();
    }

    private Response getResponse() {

        return getFortune();
    }

    private Response getFortune() {
        return new Response(serviceName.toUpperCase(),
                decision.getResponse().getMsg() + " " +
                        activity.getResponse().getMsg());
    }
}