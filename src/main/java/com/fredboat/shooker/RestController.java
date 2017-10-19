package com.fredboat.shooker;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.*;
import java.time.Instant;
import java.util.concurrent.ExecutionException;

@Controller
public class RestController {

    private static final Logger log = LoggerFactory.getLogger(RestController.class);
    private final Config config;
    private static final int HORIZONTAL_CHAR_LIMIT = 105;

    public RestController(Config config) {
        this.config = config;
    }

    @PostMapping("/sentry")
    @ResponseBody
    public String sentry(@RequestBody String body) throws ExecutionException, InterruptedException {
        log.info(body);
        JSONObject json = new JSONObject(body);
        JSONObject event = json.getJSONObject("event");
        WebhookClient client = getWebhookFromConfig(json.getString("project"));

        MessageEmbed embed = new EmbedBuilder()
                .setDescription(compileStacktraces(json))
                .setTitle(json.getString("project_name") + ": " + json.get("message"), json.getString("url"))
                .setTimestamp(Instant.ofEpochSecond(event.getLong("received")))
                .setColor(getLevelColor(json.getString("level")))
                .build();

        assert client != null;
        client.send(embed).get();
        client.close();

        return "OK";
    }

    private WebhookClient getWebhookFromConfig(String project) {
        for (Config.Project proj : config.getSentry()) {
            if (proj.getProjectName().equals(project)) {
                return new WebhookClientBuilder(proj.getWebhook())
                        .build();
            }
        }
        return null;
    }

    private String compileStacktraces(JSONObject json) {
        JSONArray array = json.getJSONObject("event")
                .getJSONObject("sentry.interfaces.Exception")
                .getJSONArray("values");

        StringBuilder str = new StringBuilder();

        str.append("```");

        // This hack ensures the width of the embed
        str.append("=========================================================================================================\n");

        for (int i = 0; i < array.length(); i++) {
            JSONObject trace = array.getJSONObject(i);
            str.append(String.format("%s.%s: %s\n",
                    trace.getString("module"),
                    trace.getString("type"),
                    trace.getString("value")));

            JSONObject traceInner = trace.getJSONObject("stacktrace");
            JSONArray frames = traceInner.getJSONArray("frames");
            for (int j = 0; j < frames.length(); j++) {
                JSONObject frame = frames.getJSONObject(frames.length() - j - 1);
                String frameMsg = String.format("    %s.%s(%s:%s)\n",
                        frame.getString("module"),
                        frame.get("function"),
                        frame.get("filename"),
                        frame.get("lineno"));

                //Try to shorten it
                if (frameMsg.length() > HORIZONTAL_CHAR_LIMIT) {
                    frameMsg = String.format("    %s.%s(%s:%s)\n",
                            abbreviatePackage(frame.getString("module")),
                            frame.get("function"),
                            frame.get("filename"),
                            frame.get("lineno"));
                }

                str.append(frameMsg);


                if (j > 5) break;
            }
        }

        str.append("```");

        return str.toString();
    }

    @SuppressWarnings("StringConcatenationInLoop")
    private String abbreviatePackage(String pack) {
        String str = "";
        String[] split = pack.split("\\.");
        for (int i = 0; i < split.length; i++) {
            if (i == split.length - 1) {
                // Preserve the classname
                str += "." + split[i];
            } else {
                //Grab the first char
                str += "." + split[i].charAt(0);
            }
        }

        //Remove the first dot
        str = str.substring(1);

        return str;
    }

    private Color getLevelColor(String level) {
        switch (level) {
            case "warning":
                return new Color(249, 166, 109);
            case "error":
                return new Color(236, 94, 68);
            case "fatal":
                return new Color(224, 69, 46);
            default:
                log.warn("Unknown level: " + level);
                return new Color(255, 255, 255);
        }
    }

}
