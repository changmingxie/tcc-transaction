[#ftl ]
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>TCC-TRANSACTION管理后台 1.0</title>
    <link rel="stylesheet" href="static/css/bootstrap.css"/>
    <link rel="stylesheet" href="static/css/admin-base.css"/>
    <link rel="stylesheet" href="static/css/base.css"/>
    <link rel="stylesheet" href="static/css/style.css"/>
    <link rel="stylesheet" href="static/css/font-awesome/css/font-awesome.css"/>
    <script src="static/js/jquery.js"></script>
    <script src="static/js/bootstrap.min.js"></script>
    <script src="static/js/jquery.json.js"></script>
</head>

<body>

[#import "paging.ftl" as p]
<div class="container-fluid">
    <div class="page-header">
        <h3>TCC-TRANSACTION管理后台 1.0</h3>
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
                <button class="btn btn-info" data-toggle="modal" data-target="#addRedisDao">添加</button>
                [#include "addredisdao.ftl"]
            </div>
        </div>
        <br/>

        <p>标签式的导航菜单</p>
        <ul class="nav nav-tabs">
            [#if isdelete==0]
                <li class="active"><a href="#">Normal</a></li>
                <li><a href="management?domain=${currentDomain}&isdelete=1">Deleted Key</a></li>
            [#else]
                <li><a href="management?domain=${currentDomain}&isdelete=0">Normal</a></li>
                <li class="active"><a href="#">Deleted Key</a></li>
            [/#if]
        </ul>

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
                    <td>${transactionVo.domain!"default-domain"}</td>
                    <td>${transactionVo.globalTxId!"default-globalTxId"}</td>
                    <td>${transactionVo.branchQualifier!"default-branchQualifier"}</td>
                    <td>
                    [#if transactionVo.status??]
                        [#if transactionVo.status==1]Trying
                        [#elseif  transactionVo.status==2]Confirming
                        [#elseif transactionVo.status==3]Cancelling
                        [#else ]Unknown[/#if]
                    [/#if]
                    </td>
                    <td>[#if transactionVo.transactionType??]
                            [#if transactionVo.transactionType==1]Root
                            [#else ]Branch[/#if]
                        [/#if]
                    </td>
                    <td>${transactionVo.retriedCount!"default-retriedCount"}</td>
                    <td>
                        <div style="width: 600px;">
                            [#if transactionVo.aggInvocation??]
                            package: <span>${transactionVo.aggInvocation.packageName}</span><br>
                            EventHandler:  <span class="label label-primary">${transactionVo.aggInvocation.className}</span>.<span class="label label-warning">${transactionVo.aggInvocation.methodName}</span><br>
                            [/#if]
                            [#if transactionVo.tccConfimInvocation??]
                            package: <span>${transactionVo.tccConfimInvocation.packageName}</span><br>
                            Confim:  <span class="label label-primary">${transactionVo.tccConfimInvocation.className}</span>.<span class="label label-warning">${transactionVo.tccConfimInvocation.methodName}</span><br>
                            [/#if]
                            [#if transactionVo.tccCancelInvocation??]
                            Cancel:   <span class="label label-primary">${transactionVo.tccCancelInvocation.className}</span>.<span class="label label-warning">${transactionVo.tccCancelInvocation.methodName}</span><br>
                            [/#if]
                        </div>

                        <button type="button" class="btn btn-success" data-toggle="collapse"
                                data-target="#detail-${transactionVo.globalTxId}-${transactionVo.branchQualifier}">
                            ~~显示详情~~
                        </button>

                        <div id="detail-${transactionVo.globalTxId}-${transactionVo.branchQualifier}" class="collapse json-box">
                            <script>
                                document.getElementById('detail-${transactionVo.globalTxId}-${transactionVo.branchQualifier}').innerHTML =
                                        new JSONFormat(JSON.stringify(${transactionVo.contentView})).toString();
                            </script>
                        </div>
                    </td>
                    <td>
                        [#if transactionVo.createTime??]
                            ${transactionVo.createTime?datetime}
                        [/#if]
                    </td>
                    <td>
                        [#if transactionVo.lastUpdateTime??]
                            ${transactionVo.lastUpdateTime?datetime}
                        [/#if]
                    </td>
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


<script src="static/js/base.js"></script>

</body>
</html>