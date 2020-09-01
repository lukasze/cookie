package workshop.sc.cookieservice.decision;

import org.springframework.stereotype.Component;
import workshop.sc.Response;

@Component
public class DecisionFallback implements DecisionClient{
    @Override
    public Response getResponse() {
        return new Response(" ","You must");
    }
}