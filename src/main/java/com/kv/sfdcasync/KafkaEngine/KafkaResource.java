package com.kv.sfdcasync.KafkaEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class KafkaResource {

    // private final KafkaListener consumer;

    // public KafkaResource(KafkaListener consumer) {
    // this.consumer = consumer;
    // }

    // @GET
    // @Path("messages")
    // @Produces(MediaType.APPLICATION_JSON)
    // @Timed
    // public List<KafkaMessage> getMessages() {
    // return Lists.reverse(consumer.getMessages());
    // }

    @GetMapping("/messages")
    String messages(Map<String, Object> model) {
        ArrayList<String> output = new ArrayList<String>();
        KafkaConfig config = new KafkaConfig();
        output.add("topic - " + config.getTopic());
        // output.add("consumer group - " + config.getConsumerGroup());

        KafkaListener listener = new KafkaListener(config);
        output.add("listener - " + listener.toString());
        try {
            listener.start();
            List<KafkaMessage> messages = listener.getMessages();
            for (KafkaMessage message : messages) {
                output.add(message.getMessage());
            }
            model.put("messages", output);
            return "kafka/messages";
        } catch (Exception e) {
            model.put("message", e.getMessage());
            return "error";
        }

    }

}
