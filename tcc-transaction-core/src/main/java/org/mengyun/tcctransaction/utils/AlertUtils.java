package org.mengyun.tcctransaction.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.mengyun.tcctransaction.dashboard.enums.ResponseCodeEnum;
import org.mengyun.tcctransaction.dashboard.exception.TransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @Author huabao.fang
 * @Date 2022/6/15 00:21
 **/
public class AlertUtils {

    private static Logger logger = LoggerFactory.getLogger(AlertUtils.class);

    private static ObjectMapper jackson = new ObjectMapper();

    public static boolean dingAlert(String dingRobotUrl, String phoneNumbers, String content) {
        try {
            doDingAlert(dingRobotUrl, phoneNumbers, content);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void doDingAlert(String dingRobotUrl, String phoneNumbers, String content) {
        JsonNode params = buildDingRequestParams(phoneNumbers, content);
        logger.info("ding alert request:{}", params.toString());
        JsonNode repsonse = doPost(dingRobotUrl, params);
        logger.info("ding alert response:{}", repsonse.toString());
        int errcode = repsonse.get("errcode").asInt(0);
        String errmsg = repsonse.get("errmsg").asText();
        if (errcode != 0) {
            logger.error("errcode:{} errmsg:{}", errcode, errmsg);
            throw new TransactionException(ResponseCodeEnum.ALERT_DING_ERROR.getCode(), errmsg);
        }
    }

    /**
     * 请求参数：
     * {
     * "msgtype": "text",
     * "text": {
     * "content": "错误码测试"
     * },
     * "at": {
     * "atMobiles": [
     * "18221876404"
     * ],
     * "isAtAll": false
     * }
     * }
     * 响应参数：
     * {
     * "errcode": 0,
     * "errmsg": "ok"
     * }
     *
     * @return
     */
    private static JsonNode buildDingRequestParams(String phoneNumbers, String content) {
        ObjectNode params = jackson.createObjectNode();

        params.put("msgtype", "text");

        ObjectNode textField = jackson.createObjectNode();
        textField.put("content", content);
        params.set("text", textField);

        ObjectNode atField = jackson.createObjectNode();
        ArrayNode atMobilesField = jackson.createArrayNode();
        Arrays.stream(phoneNumbers.split(",")).forEach(atMobilesField::add);
        atField.set("atMobiles", atMobilesField);
        atField.put("isAtAll", false);
        params.set("at", atField);

        return params;

    }

    private static JsonNode doPost(String url, JsonNode params) {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URLConnection conn = new URL(url).openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(params.toString());
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            logger.error("doPost url:{} error", url);
            throw new TransactionException(ResponseCodeEnum.ALERT_DING_ERROR);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                logger.error("doPost url:{} connect close error", url);
            }
        }
        try {
            return jackson.readTree(result.toString());
        } catch (JsonProcessingException e) {
            throw new TransactionException(ResponseCodeEnum.ALERT_DING_ERROR.getCode(), "failed to deserialize response");
        }

    }
}
