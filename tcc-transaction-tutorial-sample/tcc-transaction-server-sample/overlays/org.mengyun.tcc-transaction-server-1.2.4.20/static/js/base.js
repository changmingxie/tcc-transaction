jQuery(document).ready(function($) {
	var btnSearch = $('.j-add'),
		iptSearch = $('[name=domain]');
	//manage/domain/{domain}/pagenum/{pageNum}
	btnSearch.click(function(event) {
		var $this = $(this),
			searchValue,
			form;

		searchValue = iptSearch.val();
		if($.trim(searchValue) === '') {
			return;
		}

		location.href = 'management?domain='+searchValue+'&pagenum=1';
	});

		iptSearch.keyup(function(event) {
			if(event.keyCode === 13) {
				btnSearch.click();
			}
	});

	$('.table > tbody').on('click', '.j-edit', function () {
		var $this = $(this),
			globalTxId,
			branchQualifier,
			domain,
			url;

		globalTxId = $this.parent().siblings().eq(1).text();
		branchQualifier = $this.parent().siblings().eq(2).text();
		domain = (location.href.match(/domain=([^&]+)/) || [,''])[1];
		if(!domain) {
			return;
		}

		url = 'management/retry/reset';
		$.ajax({
			url: url,
			type: 'PUT',
			dataType: 'json',
			data: {
				domain:domain,
				globalTxId: globalTxId,
				branchQualifier: branchQualifier
			}
		})
		.done(function(result) {
			if(result.code === 200) {
				location.reload();
			}
			console.log(result.msg);
		})
		.fail(function() {
			console.log(arguments);
		});
		
	});

    $('.table > tbody').on('click', '.j-delete', function () {
        var $this = $(this),
            globalTxId,
            branchQualifier,
            domain,
            url;

        globalTxId = $this.parent().siblings().eq(1).text();
        branchQualifier = $this.parent().siblings().eq(2).text();
        domain = (location.href.match(/domain=([^&]+)/) || [,''])[1];
        if(!domain) {
            return;
        }

        url = 'management/retry/delete';
        $.ajax({
            url: url,
            type: 'PUT',
            dataType: 'json',
            data: {
                domain:domain,
                globalTxId: globalTxId,
                branchQualifier: branchQualifier
            }
        })
            .done(function(result) {
                if(result.code === 200) {
                    location.reload();
                }
                console.log(result.msg);
            })
            .fail(function() {
                console.log(arguments);
            });

    });

    $('.table > tbody').on('click', '.j-cancel', function () {
        var $this = $(this),
            globalTxId,
            branchQualifier,
            domain,
            url;

        globalTxId = $this.parent().siblings().eq(1).text();
        branchQualifier = $this.parent().siblings().eq(2).text();
        domain = (location.href.match(/domain=([^&]+)/) || [,''])[1];
        if(!domain) {
            return;
        }

        url = 'management/retry/cancel';
        $.ajax({
            url: url,
            type: 'PUT',
            dataType: 'json',
            data: {
                domain:domain,
                globalTxId: globalTxId,
                branchQualifier: branchQualifier
            }
        })
            .done(function(result) {
                if(result.code === 200) {
                    location.reload();
                }
                console.log(result.msg);
            })
            .fail(function() {
                console.log(arguments);
            });

    });

    $('.table > tbody').on('click', '.j-confirm', function () {
        var $this = $(this),
            globalTxId,
            branchQualifier,
            domain,
            url;

        globalTxId = $this.parent().siblings().eq(1).text();
        branchQualifier = $this.parent().siblings().eq(2).text();
        domain = (location.href.match(/domain=([^&]+)/) || [,''])[1];
        if(!domain) {
            return;
        }

        url = 'management/retry/confirm';
        $.ajax({
            url: url,
            type: 'PUT',
            dataType: 'json',
            data: {
                domain:domain,
                globalTxId: globalTxId,
                branchQualifier: branchQualifier
            }
        })
            .done(function(result) {
                if(result.code === 200) {
                    location.reload();
                }
                console.log(result.msg);
            })
            .fail(function() {
                console.log(arguments);
            });

    });

});