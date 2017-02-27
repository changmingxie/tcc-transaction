[#ftl ]

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>tcc transacton dubbo sample order</title>
</head>
<body>
<div class="page">
    <b>sample说明：</b>
    <br/>
    打开下面商品列表链接，选择一个商品购买，输入红包支付金额，进行支付，系统将使用红包＋资金账户转账支付。
    <br/>
    支付成功后，各个project会打印如下日志：
    <br/>
    <br>
    &nbsp;&nbsp; <b>sample-dubbo-order:</b>
    <br/>
    &nbsp;&nbsp;&nbsp;&nbsp; order try make payment called
    <br/>
    &nbsp;&nbsp;&nbsp;&nbsp; order confirm make payment called
    <br/>
    <br/>
    &nbsp;&nbsp; <b>sample-dubbo-capital:</b>
    <br/>
    &nbsp;&nbsp;&nbsp;&nbsp; capital try record called
    <br/>
    &nbsp;&nbsp;&nbsp;&nbsp; capital confirm record called
    <br/>
    <br/>
    &nbsp;&nbsp; <b>sample-dubbo-redpacket:</b>
    <br/>
    &nbsp;&nbsp;&nbsp;&nbsp; red packet try record called
    <br/>
    &nbsp;&nbsp;&nbsp;&nbsp; red packet confirm record called
    <p/>
    <a href="/user/2000/shop/1">
        商品列表链接
    </a>
</div>
</body>
</html>