package be.pxl.services.client;

import be.pxl.services.domain.Post;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "post-service", path = "/api/posts")
public interface PostClient {

    @PutMapping("/{id}/approve")
    void approvePost(@PathVariable("id") Long id);

    @PutMapping("/{id}/disapprove")
    void disapprovePost(@PathVariable("id") Long id);

    @GetMapping("/reviewable")
    List<Post> getReviewablePosts(@RequestHeader("username") String username);
}
