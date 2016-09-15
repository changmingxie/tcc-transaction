[#ftl ]
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>TCC-TRANSACTION管理后台</title>
    <link rel="stylesheet" href="${tcc_domain}/static/css/bootstrap.css"/>
    <link rel="stylesheet" href="${tcc_domain}/static/css/admin-base.css"/>
    <link rel="stylesheet" href="${tcc_domain}/static/css/base.css"/>
    <link rel="stylesheet" href="${tcc_domain}/static/css/style.css"/>
</head>

<script type="application/javascript">
    var config={
        tcc_domain:"${tcc_domain}"
    }
</script>
<body>

[#import "paging.ftl" as p]
<div class="container-fluid">
    <div class="page-header">
        <h3>TCC-TRANSACTION管理后台</h3>
    </div>
    <div class="s-wrapper liveDetail">
        <div class="form-inline">
            <div class="form-group is-loading">
                <label>DOMAIN</label>
                <input type="text" class="form-control" name="domain" value="${domain}"/>
            </div>
            &emsp;&emsp;
            <div class="form-group">
                <button class="btn btn-info j-add">查询</button>
            </div>
        </div>
        <br/>
        <div class="table-responsive">
            <p>查询结果</p>
            <table class="table table-hover table-striped table-bordered">
                <thead>
                <tr>
                    <th>DOMAIN</th>
                    <th>GLOBAL_TX_ID</th>
                    <th>BRANCH_QUALIFIER</th>
                    <th>STATUS</th>
                    <th>TRANSACTION_TYPE</th>
                    <th>RETRIED_COUNT</th>
                    <th>CREATE_TIME</th>
                    <th>LAST_UPDATE_TIME</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody>
                [#list transactionVos as transactionVo]
                <tr>
                    <td>${transactionVo.domain}</td>
                    <td>${transactionVo.globalTxId}</td>
                    <td>${transactionVo.branchQualifier}</td>
                    <td>${transactionVo.status}</td>
                    <td>${transactionVo.transactionType}</td>
                    <td>${transactionVo.retriedCount}</td>
                    <td>${transactionVo.createTime?datetime}</td>
                    <td>${transactionVo.lastUpdateTime?datetime}</td>
                    <td>
                        <button class="btn btn-info btn-xs j-edit" data-url="" data-echo="">重置</button>
                    </td>
                </tr>
                [/#list]

                </tbody>
            </table>
        </div>
        <!--:分页-->
        [#if pages??]
        <div class="pager-panel">
            <div class="pull-right">
                <nav>
                    <ul class="pagination pagination-sm">
                        [@p.paging pageNum pageSize pages urlWithoutPaging/]
                    </ul>
                </nav>
            </div>
        </div>
        [/#if]
    </div>
</div>
<script src="${tcc_domain}/static/js/jquery.js"></script>
<script src="${tcc_domain}/static/js/base.js"></script>
</body>
</html>