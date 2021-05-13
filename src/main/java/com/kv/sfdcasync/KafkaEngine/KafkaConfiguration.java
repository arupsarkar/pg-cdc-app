package com.kv.sfdcasync.KafkaEngine;

import com.loginbox.heroku.config.HerokuConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.Valid;

public class KafkaConfiguration extends HerokuConfiguration {
    @Valid
    @JsonProperty("kafka")
    private final KafkaConfig kafkaConfig = new KafkaConfig();

    public KafkaConfig getKafkaConfig() {
        return kafkaConfig;
    }
}
