package org.mengyun.tcctransaction.sample.feign.order.feign;

import org.mengyun.tcctransaction.api.EnableTcc;
import org.mengyun.tcctransaction.sample.feign.capital.api.dto.CapitalTradeOrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

/**
 * @author Nervose.Wu
 * @date 2022/6/9 11:40
 */
@FeignClient(name = "capital", url = "http://localhost:8082/tcc-transaction-http-capital/")
public interface CapitalFeignClient {

    @RequestMapping(value = "/account/query", method = RequestMethod.GET)
    @ResponseBody
    BigDecimal getCapitalAccountByUserId(@RequestParam long userId);

    @EnableTcc
    @RequestMapping(value = "/tradeOrder/record", method = RequestMethod.POST)
    @ResponseBody
    String record(@RequestBody CapitalTradeOrderDto tradeOrderDto);
}
