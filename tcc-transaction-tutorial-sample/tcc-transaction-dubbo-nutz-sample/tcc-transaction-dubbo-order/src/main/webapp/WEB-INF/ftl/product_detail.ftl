[#ftl ]

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>订单详情</title>
</head>
<body>
<div class="page">
    <p>名称: ${obj.product.productName}</p>
    <p>价格: ${obj.product.price?string("0.00")}元</p>

    <p>可用账户余额: ${obj.capitalAmount?string("0.00")}元</p>
    <p>可用红包余额: ${obj.redPacketAmount?string("0.00")}元</p>

    <form action="/placeorder" method="post">
        红包金额:&nbsp;&nbsp;&nbsp;<input type="text" style="width: 220px" name="redPacketPayAmount" value="" placeholder="请输入期望使用的红包金额"/>
        <input type="hidden" name="shopId" value="${obj.shopId}" />
        <input type="hidden" name="productId" value="${obj.product.productId}"/>
        <input type="hidden" name="payerUserId" value="${obj.userId}"/>

        <input type="submit" value="支付"/>
    </form>
</div>
</body>
</html>