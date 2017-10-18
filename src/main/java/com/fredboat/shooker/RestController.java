package com.fredboat.shooker;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RestController {

    private static final Logger log = LoggerFactory.getLogger(RestController.class);
    private final Config config;

    public RestController(Config config) {
        this.config = config;
    }

    @PostMapping("/sentry")
    @ResponseBody
    public String sentry(@RequestBody String body) {
        JSONObject json = new JSONObject(body);
        log.info("test");
        return config.getProjects().toString();
    }

}
