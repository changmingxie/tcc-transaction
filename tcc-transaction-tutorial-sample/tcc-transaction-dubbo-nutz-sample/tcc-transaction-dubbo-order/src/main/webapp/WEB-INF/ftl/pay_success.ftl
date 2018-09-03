[#ftl ]

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>支付结果</title>
</head>
<body>
<div class="page">
    <p>支付状态:<b>${obj.payResult} </b> </p>

    <p>剩余可用账户余额: ${obj.capitalAmount?string("0.00")}元</p>
    <p>剩余可用红包余额: ${obj.redPacketAmount?string("0.00")}元</p>
</div>
</body>
</html>