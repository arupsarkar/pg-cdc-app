package com.kv.sfdcasync.Util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kv.sfdcasync.KafkaEngine.KafkaMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(AccountProcessor.class);
    private static Gson gson = new Gson();

    public static void processIncomingMessage(KafkaMessage message) {
        LOG.debug("---> processing start -----");
        String json = message.toString();
        JsonObject body = gson.fromJson(json, JsonObject.class);
        JsonArray payload = body.get("payload").getAsJsonArray();
        // convert it to JsonObject
        JsonObject afterObj = payload.get(0).getAsJsonObject();
        JsonArray after = afterObj.get("after").getAsJsonArray();

        JsonObject result = after.get(0).getAsJsonObject();
        JsonElement name = result.get("name");
        JsonElement phone = result.get("phone");
        JsonElement sfid = result.get("sfid");
        LOG.debug("---> account name: " + name + ", phone: " + phone + ", sfid: " + sfid);
        LOG.debug("---> processing end -----");
    }
}
