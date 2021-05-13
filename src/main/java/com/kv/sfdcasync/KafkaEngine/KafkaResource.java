package com.kv.sfdcasync.KafkaEngine;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Lists;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/")
public class KafkaResource {

    private final KafkaListener consumer;

    public KafkaResource(KafkaListener consumer) {
        this.consumer = consumer;
    }

    @GET
    @Path("messages")
    @Produces(MediaType.APPLICATION_JSON)
    @Timed
    public List<KafkaMessage> getMessages() {
        return Lists.reverse(consumer.getMessages());
    }

}
