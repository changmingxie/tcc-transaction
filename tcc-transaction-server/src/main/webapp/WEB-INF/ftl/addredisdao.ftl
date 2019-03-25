<!-- 模态框（Modal） -->
<div class="modal fade" id="addRedisDao" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                <h4 class="modal-title" id="myModalLabel">添加新TCC或AGG的Redis源</h4>
            </div>
            <form role="form" class="form-horizontal" method="POST" action="management/redis/add">
            <div class="modal-body">
                    <div class="">
                        <label class="col-md-2 control-label">Redis IP</label>
                        <div class="col-md-10">
                            <input class="form-control" required name="host" id="host" type="text" value="192.168.3.220">
                        </div>
                    </div>
                    <div class="">
                        <label class="col-md-2 control-label">端口</label>
                        <div class="col-md-10">
                            <input class="form-control" required name="port" id="port" type="text" value="6379">
                        </div>
                    </div>
                    <div class="">
                        <label class="col-md-2 control-label">密码</label>
                        <div class="col-md-10">
                            <input class="form-control" required name="password" id="password" type="text" value="jredis123456">
                        </div>
                    </div>
                    <div class="">
                        <label class="col-md-2 control-label">数据库ID</label>
                        <div class="col-md-10">
                            <input class="form-control" required name="database" id="database" type="text" value="">
                        </div>
                    </div>
                    <div class="">
                        <label class="col-md-2 control-label">前缀</label>
                        <div class="col-md-10">
                            <input class="form-control" required name="key" id="key" type="text" placeholder="AGG: 或者 TCC:">
                        </div>
                    </div>
                    <div class="">
                        <label class="col-md-2 control-label">域</label>
                        <div class="col-md-10">
                            <input class="form-control" required name="domain" id="domain" type="text" value="">
                        </div>
                    </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="submit" class="btn btn-primary">提交更改</button>
            </div>
            </form>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div>
<!-- /.modal -->
