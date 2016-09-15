[#macro paging pageNum pageSize pages ctxPath]
    [#if (pageNum>1)]
    <li><a aria-label="Previous" href="${ctxPath}/pagenum/${pageNum-1}"><span aria-hidden="true">«</span></a></li>
    [/#if]


    [#assign begin=pageNum-3]

    [#if (pages-pageNum) lt 3]
        [#assign begin=pages-6]
    [/#if]

    [#if pageNum lt 4]
        [#if pages gt 7]
            [#assign end=7]
        [#else]
            [#assign end=pages]
        [/#if]
    [#else]
        [#assign end=pageNum+3]
    [/#if]

    [#list begin..end as index]
        [#if pages gt 1]
            [#if (index >= 1)&&(index <= pages)]
            <li [#if index == pageNum]
                    class="active"[/#if]><a href="${ctxPath}/pagenum/${index}">${index}</a></li>
            [/#if]
        [/#if]
    [/#list]

[#--[#if (end < pages - 2)]--]
[#--&hellip;--]
[#--[/#if]--]
[#--[#if (end < pages - 1)]--]
[#--<li><a href="${ctxPath}/pagenum/${pages-1}">${pages-1}</a></li>--]
[#--[/#if]--]
[#--[#if (end < pages)]--]
[#--<li><a href="${ctxPath}/pagenum/${pages}">${pages}</a></li>--]
[#--[/#if]--]

    [#if (pages>pageNum)]
    <li><a aria-label="Next" href="${ctxPath}/pagenum/${pageNum+1}"><span aria-hidden="true">»</span></a></li>
    [/#if]
[/#macro]