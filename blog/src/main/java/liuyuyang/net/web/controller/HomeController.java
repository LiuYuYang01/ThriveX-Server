package liuyuyang.net.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "首页")
@RestController
@RequestMapping("/")
public class HomeController {
    @GetMapping
    public String Home() {
        return "<h1>Hello ThriveX</h1>";
    }
}
