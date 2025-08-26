package be.pxl.services.client;

import be.pxl.services.domain.Post;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "post-service", path = "/api/posts")
public interface PostClient {

    @GetMapping("/{id}")
    Post getPostById(@PathVariable("id") Long id);

    @GetMapping("/by-ids")
    List<Post> getPostsByIds(@RequestParam("ids") List<Long> ids);
}
