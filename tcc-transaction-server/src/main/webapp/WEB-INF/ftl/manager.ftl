[#ftl ]
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>TCC-TRANSACTION管理后台</title>
    <link rel="stylesheet" href="static/css/bootstrap.css"/>
    <link rel="stylesheet" href="static/css/admin-base.css"/>
    <link rel="stylesheet" href="static/css/base.css"/>
    <link rel="stylesheet" href="static/css/style.css"/>
</head>

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
                <select name="domain">
                [#list domains as domainValue]

                    <option value="${domainValue}" [#if currentDomain==domainValue ]
                            selected="true" [/#if]>${domainValue}</option>
                [/#list]
                </select>
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
                    <th>Domain</th>
                    <th>Global Tx Id</th>
                    <th>Branch Qualifier</th>
                    <th>Status</th>
                    <th>Transaction Type</th>
                    <th>Retried Count</th>
                    <th>Content</th>
                    <th>Create Time</th>
                    <th>Last Update Time</th>
                    <th>Operation</th>
                </tr>
                </thead>
                <tbody>
                [#list transactionVos as transactionVo]
                <tr>
                    <td>${transactionVo.domain}</td>
                    <td>${transactionVo.globalTxId}</td>
                    <td>${transactionVo.branchQualifier}</td>
                    <td>[#if transactionVo.status==1]Trying[#elseif  transactionVo.status==2]
                        Confirming[#elseif transactionVo.status==3]Cancelling[#else ]Unknown[/#if]</td>
                    <td>[#if transactionVo.transactionType==1]Root[#else ]Branch[/#if]</td>
                    <td>${transactionVo.retriedCount}</td>
                    <td>
                        <div style="width: 600px; height: 120px; overflow: scroll;">${transactionVo.contentView}</div>
                    </td>
                    <td>${transactionVo.createTime?datetime}</td>
                    <td>${transactionVo.lastUpdateTime?datetime}</td>
                    <td>
                        <button class="btn btn-info btn-xs j-edit" data-url="" data-echo="">重置</button>
                        <button class="btn btn-info btn-xs j-delete" data-url="" data-echo="">删除</button>
                        <button class="btn btn-info btn-xs j-cancel" data-url="" data-echo="">取消</button>
                        <button class="btn btn-info btn-xs j-confirm" data-url="" data-echo="">确认</button>
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
<script src="static/js/jquery.js"></script>
<script src="static/js/base.js"></script>
</body>
</html>