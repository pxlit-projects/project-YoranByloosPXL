package be.pxl.services.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "post-service-client", url = "http://localhost:8080/api/posts")
public interface PostClient {

    @PutMapping("/{id}/approve")
    void approvePost(@PathVariable("id") Long id);

    @PutMapping("/{id}/disapprove")
    void disapprovePost(@PathVariable("id") Long id);
}
