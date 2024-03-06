package org.mengyun.tcctransaction.dashboard.service.impl.tccserver;

import com.netflix.loadbalancer.Server;
import org.mengyun.tcctransaction.dashboard.constants.DashboardConstant;
import org.mengyun.tcctransaction.dashboard.dto.*;
import org.mengyun.tcctransaction.dashboard.enums.ResponseCodeEnum;
import org.mengyun.tcctransaction.dashboard.service.TransactionService;
import org.mengyun.tcctransaction.dashboard.service.condition.TccServerStorageCondition;
import org.mengyun.tcctransaction.stats.StatsDto;
import org.mengyun.tcctransaction.stats.StatsSupplier;
import org.mengyun.tcctransaction.storage.TransactionStorage;
import org.mengyun.tcctransaction.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author huabao.fang
 * @Date 2022/5/30 14:19
 **/
@Conditional(TccServerStorageCondition.class)
@Service
public class TccServerTransactionServiceImpl implements TransactionService, StatsSupplier {

    private Logger logger = LoggerFactory.getLogger(TccServerTransactionServiceImpl.class);

    private static final String REQUEST_METHOD_TRANSACTION_DETAIL = "transaction/detail";

    private static final String REQUEST_METHOD_STATS = "server/stats";

    @Autowired
    private TccServerFeignClient tccServerFeignClient;

    @Autowired
    private SpringClientFactory springClientFactory;

    @Autowired
    private RestTemplate restTemplate;

    private final ExecutorService executorService = new ThreadPoolExecutor(5,
            5,
            0,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(100),
            new ThreadFactory() {
                private final AtomicInteger threadIndex = new AtomicInteger(0);

                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, String.format("StatsDtoObtainThread_%d", threadIndex.getAndIncrement()));
                }
            });

    @Override
    public ResponseDto<TransactionPageDto> list(TransactionPageRequestDto requestDto) {
        return tccServerFeignClient.transactionList(requestDto);
    }

    @Override
    public ResponseDto<TransactionStoreDto> detail(TransactionDetailRequestDto requestDto) {

        List<Server> servers = getServers();
        if (CollectionUtils.isEmpty(servers)) {
            return ResponseDto.returnFail(ResponseCodeEnum.TRANSACTION_DETAIL_NO_INSTANCES);
        }

        String errorMessage = "";
        String errorCode = "";
        for (Server server : servers) {
            String detailRequestUrl = "http://"
                    .concat(server.getHostPort())
                    .concat("/")
                    .concat(DashboardConstant.TCC_SERVER_GROUP)
                    .concat("/")
                    .concat(REQUEST_METHOD_TRANSACTION_DETAIL);
            try {
                ResponseDto<TransactionStoreDto> responseDto = restTemplate.postForObject(detailRequestUrl, requestDto, ResponseDto.class);
                if (responseDto.isSuccess()) {
                    return responseDto;
                }
                errorMessage = responseDto.getMessage();
                errorCode = responseDto.getCode();
            } catch (Exception e) {
                logger.warn("request detailRequestUrl:{} failed!", detailRequestUrl, e);
            }

        }
        if (StringUtils.isEmpty(errorMessage)) {
            return ResponseDto.returnFail(ResponseCodeEnum.TRANSACTION_CONTENT_VISUALIZE_ERROR);
        } else {
            return ResponseDto.returnFail(errorCode, errorMessage);
        }
    }

    @Override
    public ResponseDto<Void> confirm(TransactionOperateRequestDto requestDto) {
        return tccServerFeignClient.transactionConfirm(requestDto);
    }

    @Override
    public ResponseDto<Void> cancel(TransactionOperateRequestDto requestDto) {
        return tccServerFeignClient.transactionCancel(requestDto);
    }

    @Override
    public ResponseDto<Void> reset(TransactionOperateRequestDto requestDto) {
        return tccServerFeignClient.transactionReset(requestDto);
    }

    @Override
    public ResponseDto<Void> markDeleted(TransactionOperateRequestDto requestDto) {
        return tccServerFeignClient.transactionMarkDeleted(requestDto);
    }

    @Override
    public ResponseDto<Void> restore(TransactionOperateRequestDto requestDto) {
        return tccServerFeignClient.transactionRestore(requestDto);
    }

    @Override
    public ResponseDto<Void> delete(TransactionOperateRequestDto requestDto) {
        return tccServerFeignClient.transactionDelete(requestDto);
    }

    @Override
    public TransactionStorage getTransactionStorage() {
        return null;
    }

    @Override
    public List<StatsDto> getStatsDtoList() {
        CompletionService<StatsDto> completionService = new ExecutorCompletionService<>(executorService);
        int added = 0;
        for (Server server : getServers()) {
            try {
                completionService.submit(() -> getStatsDto(server));
                added++;
            } catch (RejectedExecutionException e) {
                logger.warn("system thread pool busy", e);
            }
        }
        List<StatsDto> res = new ArrayList<>();
        for (int i = 0; i < added; i++) {
            try {
                StatsDto statsDto = completionService.take().get();
                if (statsDto != null) {
                    res.add(statsDto);
                }
            } catch (Exception e) {
                logger.warn("request server stats failed!", e);
            }
        }
        return res;
    }

    public StatsDto getStatsDto(Server server) {
        String statsRequestUrl = "http://"
                .concat(server.getHostPort())
                .concat("/")
                .concat(DashboardConstant.TCC_SERVER_GROUP)
                .concat("/")
                .concat(REQUEST_METHOD_STATS);
        return restTemplate.getForObject(statsRequestUrl, StatsDto.class);
    }

    private List<Server> getServers(){
        return springClientFactory.getLoadBalancer(DashboardConstant.TCC_SERVER_GROUP).getReachableServers();
    }
}
