[#ftl ]

<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>商品列表</title>
</head>
<body>
<div class="page">
    <div class="bg-f">
        <ul class="list" >
        [#if products?size > 0]
            [#list products as product]
                <li class="list-item">
                    <p>${product.productName}(${product.price?string("0.00")})&nbsp;&nbsp;&nbsp;&nbsp;<span><a href="/user/${userId}/shop/${shopId}/product/${product.productId}/confirm">购买</a></span></p>
                </li>
            [/#list]
        [/#if]
        </ul>
    </div>
</div>
</body>
</html>