package org.mengyun.tcctransaction.server.controller;

import org.mengyun.tcctransaction.server.dao.DaoRepository;
import org.mengyun.tcctransaction.server.vo.CommonResponse;
import org.mengyun.tcctransaction.server.vo.TransactionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.xml.bind.DatatypeConverter;
import java.util.List;


/**
 * Created by changming.xie on 8/26/16.
 */
@Controller
public class TransactionController {

    @Autowired
    private DaoRepository daoRepository;

    public static final Integer DEFAULT_PAGE_NUM = 1;

    public static final int DEFAULT_PAGE_SIZE = 10;

    @RequestMapping(value = "/management", method = RequestMethod.GET)
    public ModelAndView manager() {
        ModelAndView modelAndView = new ModelAndView("manager");
        return modelAndView;
    }

    @RequestMapping(value = "/management/domain/{domain}", method = RequestMethod.GET)
    public ModelAndView manager(@PathVariable String domain) {
        return manager(domain, DEFAULT_PAGE_NUM);
    }

    @RequestMapping(value = "/management/domain/{domain}/pagenum/{pageNum}", method = RequestMethod.GET)
    public ModelAndView manager(@PathVariable String domain, @PathVariable Integer pageNum) {
        ModelAndView modelAndView = new ModelAndView("manager");

        List<TransactionVo> transactionVos = daoRepository.getDao(domain).findTransactions(pageNum, DEFAULT_PAGE_SIZE);
        Integer totalCount = daoRepository.getDao(domain).countOfFindTransactions();
        Integer pages = totalCount / DEFAULT_PAGE_SIZE;
        if (totalCount % DEFAULT_PAGE_SIZE > 0) {
            pages++;
        }
        modelAndView.addObject("transactionVos", transactionVos);
        modelAndView.addObject("pageNum", pageNum);
        modelAndView.addObject("pageSize", DEFAULT_PAGE_SIZE);
        modelAndView.addObject("pages", pages);
        modelAndView.addObject("domain", domain);
        modelAndView.addObject("urlWithoutPaging", "/management/domain/" + domain);
        return modelAndView;
    }

    @RequestMapping(value = "/domain/{domain}/retry/reset", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResponse<Void> reset(@PathVariable String domain, String globalTxId, String branchQualifier) {

        daoRepository.getDao(domain).resetRetryCount(
                DatatypeConverter.parseHexBinary(globalTxId),
                DatatypeConverter.parseHexBinary(branchQualifier));

        return new CommonResponse<Void>();
    }


}
