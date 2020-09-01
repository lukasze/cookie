package workshop.sc.cookieservice.activity;

import org.springframework.stereotype.Component;
import workshop.sc.Response;

@Component
public class ActivityFallback implements ActivityClient {

    @Override
    public Response getResponse() {
        return new Response("","take a rest!");
    }
}