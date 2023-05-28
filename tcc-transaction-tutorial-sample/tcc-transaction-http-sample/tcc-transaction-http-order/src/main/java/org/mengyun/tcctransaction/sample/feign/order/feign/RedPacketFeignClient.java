package org.mengyun.tcctransaction.sample.feign.order.feign;

import org.mengyun.tcctransaction.api.EnableTcc;
import org.mengyun.tcctransaction.sample.feign.redpacket.api.dto.RedPacketTradeOrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

/**
 * @author Nervose.Wu
 * @date 2022/6/9 11:40
 */
@FeignClient(name = "redPacket", url = "http://localhost:8083/tcc-transaction-http-redpacket/")
public interface RedPacketFeignClient {

    @RequestMapping(value = "/account/query", method = RequestMethod.GET)
    @ResponseBody
    BigDecimal getRedPacketAccountByUserId(@RequestParam long userId);

    @EnableTcc
    @RequestMapping(value = "/tradeOrder/record", method = RequestMethod.POST)
    @ResponseBody
    String record(@RequestBody RedPacketTradeOrderDto tradeOrderDto);
}
