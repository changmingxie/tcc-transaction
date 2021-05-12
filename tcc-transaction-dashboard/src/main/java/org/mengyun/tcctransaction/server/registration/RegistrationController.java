package org.mengyun.tcctransaction.server.registration;


import org.mengyun.tcctransaction.server.dao.Daos;
import org.mengyun.tcctransaction.server.dao.TransactionDao;
import org.mengyun.tcctransaction.server.model.Page;
import org.mengyun.tcctransaction.server.model.Result;
import org.mengyun.tcctransaction.server.vo.TransactionVo;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;

/**
 * Created by Lee on 2020/4/8 18:28.
 */
@RestController
@RequestMapping("/api")
public class RegistrationController {


    private static final int DEFAULT_PAGE_SIZE = 10;
    private final Daos daos;
    private final RegistrationContainer container;

    public RegistrationController(RegistrationContainer container, Daos daos) {
        this.daos = daos;
        this.container = container;
    }

    /**
     * 获得所有domain
     *
     * @return
     */
    @GetMapping("/domains")
    public Object domains() {
        return daos.domains();
    }

    /**
     * 移除一个domain
     *
     * @param domain
     * @return
     */
    @DeleteMapping("/domain")
    public Object remove(@RequestParam String domain) throws Exception {
        container.remove(domain);
        return Result.ok();
    }


    @GetMapping("/manage")
    public Object list(@RequestParam String domain,
                       @RequestParam String row,
                       @RequestParam(required = false, defaultValue = "false") boolean isDeleted,
                       @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                       @RequestParam(required = false, defaultValue = "" + DEFAULT_PAGE_SIZE) Integer pageSize) {

        return daos.get(domain, row)
                .map(new Function<TransactionDao, Page<TransactionVo>>() {
                    @Override
                    public Page<TransactionVo> apply(TransactionDao dao) {
                        if (isDeleted) {
                            return dao.findDeletedTransactions(pageNum, pageSize);
                        } else {
                            return dao.findTransactions(pageNum, pageSize);
                        }
                    }
                }).orElse(Page.empty());


    }


    /**
     * 重置
     *
     * @return
     */
    @PutMapping("/reset")
    public Object reset(@RequestParam String domain,
                        @RequestParam String row,
                        @RequestParam String globalTxId,
                        @RequestParam String branchQualifier) {

        return daos.get(domain, row)
                .map(dao -> {
                    dao.resetRetryCount(globalTxId, branchQualifier);
                    return Result.ok();
                }).orElse(Result.ok());
    }

    /**
     * 软删除
     *
     * @return
     */
    @DeleteMapping("/delete")
    public Object delete(@RequestParam String domain,
                         @RequestParam String row,
                         @RequestParam String globalTxId,
                         @RequestParam String branchQualifier) {

        return daos.get(domain, row)
                .map(dao -> {
                    dao.delete(globalTxId, branchQualifier);
                    return Result.ok();
                }).orElse(Result.ok());
    }


    /**
     * restore
     *
     * @return
     */
    @PutMapping("/restore")
    public Object restore(@RequestParam String domain,
                          @RequestParam String row,
                          @RequestParam String globalTxId,
                          @RequestParam String branchQualifier) {

        return daos.get(domain, row)
                .map(dao -> {
                    dao.restore(globalTxId, branchQualifier);
                    return Result.ok();
                }).orElse(Result.ok());
    }

    /**
     * confirm
     *
     * @return
     */
    @PutMapping("/confirm")
    public Object confirm(@RequestParam String domain,
                          @RequestParam String row,
                          @RequestParam String globalTxId,
                          @RequestParam String branchQualifier) {

        return daos.get(domain, row)
                .map(dao -> {
                    dao.confirm(globalTxId, branchQualifier);
                    return Result.ok();
                }).orElse(Result.ok());
    }

    /**
     * cancel
     *
     * @return
     */
    @PutMapping("/cancel")
    public Object cancel(@RequestParam String domain,
                         @RequestParam String row,
                         @RequestParam String globalTxId,
                         @RequestParam String branchQualifier) {

        return daos.get(domain, row)
                .map(dao -> {
                    dao.cancel(globalTxId, branchQualifier);
                    return Result.ok();
                }).orElse(Result.ok());
    }
}
