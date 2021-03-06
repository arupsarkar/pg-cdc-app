package com.kv.sfdcasync.KafkaEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.github.jkutner.EnvKeyStore;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

// import org.hibernate.validator.constraints.NotEmpty;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.lang.System.getenv;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class KafkaConfig {

    // @NotEmpty
    private String topic = "dynamic_connector_33041.salesforce.account";

    // @NotEmpty
    // private String consumerGroup;

    public Properties getProperties() {
        return buildDefaults();
    }

    private Properties buildDefaults() {
        Properties properties = new Properties();
        // Map<String, Object> properties = new HashMap<>();
        List<String> hostPorts = Lists.newArrayList();

        for (String url : Splitter.on(",").split(checkNotNull(getenv("KAFKA_URL")))) {
            try {
                URI uri = new URI(url);
                hostPorts.add(format("%s:%d", uri.getHost(), uri.getPort()));

                switch (uri.getScheme()) {
                    case "kafka":
                        properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "PLAINTEXT");
                        break;
                    case "kafka+ssl":
                        properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
                        properties.put("ssl.endpoint.identification.algorithm", "");

                        try {
                            EnvKeyStore envTrustStore = EnvKeyStore.createWithRandomPassword("KAFKA_TRUSTED_CERT");
                            EnvKeyStore envKeyStore = EnvKeyStore.createWithRandomPassword("KAFKA_CLIENT_CERT_KEY",
                                    "KAFKA_CLIENT_CERT");

                            File trustStore = envTrustStore.storeTemp();
                            File keyStore = envKeyStore.storeTemp();

                            properties.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, envTrustStore.type());
                            properties.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, trustStore.getAbsolutePath());
                            properties.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, envTrustStore.password());
                            properties.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, envKeyStore.type());
                            properties.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keyStore.getAbsolutePath());
                            properties.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, envKeyStore.password());
                        } catch (Exception e) {
                            throw new RuntimeException("There was a problem creating the Kafka key stores", e);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException(format("unknown scheme; %s", uri.getScheme()));
                }
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, Joiner.on(",").join(hostPorts));
        return properties;
    }

    public ArrayList<String> getTopic() {
        List<String> topics = new ArrayList<>();
        topics.add(topic);
        return (ArrayList<String>) topics;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Properties config = buildDefaults();

        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        Map<String, Object> elements = new HashMap<String, Object>();
        for (String key : config.stringPropertyNames()) {
            elements.put(key, config.getProperty(key));
        }
        return new DefaultKafkaProducerFactory<>(elements);
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Properties config = buildDefaults();
        // config.put(ConsumerConfig.GROUP_ID_CONFIG, "xyz");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        Map<String, Object> elements = new HashMap<String, Object>();
        for (String key : config.stringPropertyNames()) {
            elements.put(key, config.getProperty(key));
        }
        return new DefaultKafkaConsumerFactory<>(elements);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
