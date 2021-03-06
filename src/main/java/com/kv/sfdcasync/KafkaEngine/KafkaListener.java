package com.kv.sfdcasync.KafkaEngine;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.collect.Lists;
import com.kv.sfdcasync.Util.AccountProcessor;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.dropwizard.lifecycle.Managed;

public class KafkaListener implements Managed {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaListener.class);
    private static final int CAPACITY = 10;
    private final KafkaConfig config;
    private ExecutorService executor;
    private final AtomicBoolean running = new AtomicBoolean();
    private CountDownLatch stopLatch;
    private KafkaConsumer<String, String> consumer;
    private List<String> topic = new ArrayList<String>();
    private final Queue<KafkaMessage> queue = new ArrayBlockingQueue<>(CAPACITY);

    public KafkaListener(KafkaConfig config) {
        this.config = config;
    }

    @Override
    public void start() throws Exception {
        executor = Executors.newSingleThreadExecutor();
        executor.submit(this::loop);
        running.set(true);
        stopLatch = new CountDownLatch(1);
    }

    private void loop() {
        printSystemProperties();
        LOG.info("---> starting");
        Properties properties = config.getProperties();
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "xyz");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        printMessages(properties);
        try {
            consumer = new KafkaConsumer<>(properties);
            // consumer.subscribe(singletonList(config.getTopic()));
            topic = config.getTopic();
            LOG.debug("---> kafka topic name : " + topic);
            consumer.subscribe(Collections.singletonList(topic.get(0)));
            LOG.info("---> consumer started ");
        } catch (Exception ex) {
            LOG.error("---> Error ", ex.getMessage());
        }

        do {
            LOG.debug("---> Starting to poll");
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(20000));
            if (records.isEmpty()) {
                LOG.debug("---> records empty");
            } else {
                for (ConsumerRecord<String, String> record : records) {
                    LOG.debug("---> queue size and capacity : " + queue.size() + "-" + CAPACITY);
                    while (queue.size() >= CAPACITY) {
                        queue.poll();
                    }

                    KafkaMessage message = new KafkaMessage(record.value(), topic.get(0), record.partition(),
                            record.offset());
                    // LOG.debug("---> kafka message " + message.getMessage());
                    AccountProcessor.processIncomingMessage(message);
                    if (queue.offer(message)) {
                        consumer.commitSync();
                    } else {
                        LOG.error("Failed to track message: {}", message);
                    }
                }
            }

        } while (running.get());

        LOG.info("---> closing consumer");
        consumer.close();
        stopLatch.countDown();
    }

    @Override
    public void stop() throws Exception {
        LOG.info("stopping");
        running.set(false);
        stopLatch.await();
        executor.shutdown();
        LOG.info("stopped");

    }

    public List<KafkaMessage> getMessages() {
        List<KafkaMessage> messages = Lists.newArrayList();
        queue.iterator().forEachRemaining(messages::add);
        return messages;
    }

    private void printMessages(Properties messages) {
        Set<String> keys = messages.stringPropertyNames();
        for (String key : keys) {
            LOG.debug(key + " : " + messages.getProperty(key));
        }
    }

    private void printSystemProperties() {
        LOG.debug("-------------- print system environments : start ---------------");
        Map<String, String> map = System.getenv();
        for (String key : map.keySet()) {
            LOG.debug(key + " : " + map.get(key));
        }
        LOG.debug("-------------- print system environments : end ---------------");
    }

}
