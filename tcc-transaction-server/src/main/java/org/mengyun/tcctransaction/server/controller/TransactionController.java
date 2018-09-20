package org.mengyun.tcctransaction.server.controller;

import org.apache.commons.lang3.StringUtils;
import org.mengyun.tcctransaction.server.dao.DaoRepository;
import org.mengyun.tcctransaction.server.dao.RedisTransactionDao;
import org.mengyun.tcctransaction.server.dto.PageDto;
import org.mengyun.tcctransaction.server.vo.CommonResponse;
import org.mengyun.tcctransaction.server.vo.TransactionVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;


/**
 * Created by changming.xie on 8/26/16.
 */
@Controller
@RequestMapping("/management")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private DaoRepository daoRepository;

    public static final Integer DEFAULT_PAGE_NUM = 1;

    public static final int DEFAULT_PAGE_SIZE = 10;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView manager(@RequestParam(value = "domain", required = false) String domain,
                                @RequestParam(value = "pagenum", required = false) Integer pageNum,
                                @RequestParam(value = "isdelete", required = false, defaultValue = "0") Integer isDelete) {

        logger.info("query with domain:{},pageNum:{}", domain, pageNum);

        if (StringUtils.isEmpty(domain)) {
            return manager();
        }

        if (pageNum == null) {
            return manager(domain, DEFAULT_PAGE_NUM, isDelete);
        }

        ModelAndView modelAndView = new ModelAndView("manager");


        PageDto<TransactionVo> pageDto;
        if (isDelete.intValue() == 0) {
            pageDto = daoRepository.getDao(domain).findTransactions(pageNum, DEFAULT_PAGE_SIZE);
        } else {
            pageDto = daoRepository.getDao(domain).findDeletedTransactions(pageNum, DEFAULT_PAGE_SIZE);
        }

        List<TransactionVo> transactionVos = pageDto.getData();
        Integer totalCount = pageDto.getTotalCount();

        Integer pages = totalCount / DEFAULT_PAGE_SIZE;
        if (totalCount % DEFAULT_PAGE_SIZE > 0) {
            pages++;
        }
        modelAndView.addObject("pages", pages);

        modelAndView.addObject("transactionVos", transactionVos);
        modelAndView.addObject("pageNum", pageNum);
        modelAndView.addObject("pageSize", DEFAULT_PAGE_SIZE);
        modelAndView.addObject("domains", daoRepository.getDomains());
        modelAndView.addObject("currentDomain", domain);
        modelAndView.addObject("isdelete", isDelete);
        modelAndView.addObject("urlWithoutPaging", "management?domain=" + domain);
        return modelAndView;
    }

    @RequestMapping(value = "/redis/add", method = RequestMethod.POST)
    public ModelAndView addRedisTransactionDao(
            @RequestParam(value = "host") String host,
            @RequestParam(value = "port") int port,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "database") int database,
            @RequestParam(value = "key") String key,
            @RequestParam(value = "domain") String domain) {

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(20);
        config.setMaxTotal(50);
        config.setMinIdle(2);
        config.setMaxWaitMillis(3000);

        JedisPool pool = new JedisPool(config, host, port, 1000, password, database);

        RedisTransactionDao dao = new RedisTransactionDao();
        dao.setDomain(domain);
        dao.setJedisPool(pool);
        dao.setKeySuffix(key);

        daoRepository.addDao(dao);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:../../management");
        return modelAndView;
    }

    @RequestMapping(value = "/retry/reset", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResponse<Void> reset(String domain, String globalTxId, String branchQualifier) {

        logger.info("request /retry/reset with domain: {} globalTxId: {} branchQualifier: {} ",
                new Object[]{domain, globalTxId, branchQualifier});

        daoRepository.getDao(domain).resetRetryCount(
                globalTxId,
                branchQualifier);

        return new CommonResponse<Void>();
    }

    @RequestMapping(value = "/retry/delete", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResponse<Void> delete(String domain, String globalTxId, String branchQualifier) {

        logger.info("request /retry /delete with domain: {} globalTxId: {} branchQualifier: {} ",
                new Object[]{domain, globalTxId, branchQualifier});

        daoRepository.getDao(domain).delete(
                globalTxId,
                branchQualifier);

        return new CommonResponse<Void>();
    }

    @RequestMapping(value = "/retry/restore", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResponse<Void> restore(String domain, String globalTxId, String branchQualifier) {

        logger.info("request /retry /restore with domain: {} globalTxId: {} branchQualifier: {} ",
                new Object[]{domain, globalTxId, branchQualifier});

        daoRepository.getDao(domain).restore(
                globalTxId,
                branchQualifier);

        return new CommonResponse<Void>();
    }

    @RequestMapping(value = "/retry/confirm", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResponse<Void> confirm(String domain, String globalTxId, String branchQualifier) {

        logger.info("request /retry/confirm with domain: {} globalTxId: {} branchQualifier: {} ",
                new Object[]{domain, globalTxId, branchQualifier});

        daoRepository.getDao(domain).confirm(
                globalTxId,
                branchQualifier);

        return new CommonResponse<Void>();
    }

    @RequestMapping(value = "/retry/cancel", method = RequestMethod.PUT)
    @ResponseBody
    public CommonResponse<Void> cancel(String domain, String globalTxId, String branchQualifier) {

        logger.info("request /retry/cancel with domain: {} globalTxId: {} branchQualifier: {} ",
                new Object[]{domain, globalTxId, branchQualifier});

        daoRepository.getDao(domain).cancel(
                globalTxId,
                branchQualifier);

        return new CommonResponse<Void>();
    }

    public ModelAndView manager() {

        logger.info("query without any parameter");

        ModelAndView modelAndView = new ModelAndView("manager");
        modelAndView.addObject("domains", daoRepository.getDomains());
        return modelAndView;
    }

    public ModelAndView manager(String domain) {

        logger.info("query with domain:{}", domain);
        return manager(domain, DEFAULT_PAGE_NUM, 0);
    }


}
