package workshop.sc.cookieservice.activity;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import workshop.sc.Response;

@FeignClient(name = "ACTIVITY-SERVICE")
@RibbonClient(name = "ACTIVITY-SERVICE")
public interface ActivityClient {
    @GetMapping("/activity")
    Response getResponse();
}
