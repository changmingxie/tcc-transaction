/*!
 * jQuery Json Plugin (with Transition Definitions)
 * Examples and documentation at: http://json.cn/
 * Copyright (c) 2012-2013  China.Ren.
 * Version: 1.0.2 (19-OCT-2013)
 * Dual licensed under the MIT and GPL licenses.
 * http://jquery.malsup.com/license.html
 * Requires: jQuery v1.3.1 or later
 */
var JSONFormat = (function(){
    var _toString = Object.prototype.toString;
    var _bigNums = [];
    function format(object, indent_count){
        var html_fragment = '';
        switch(_typeof(object)){
            case 'Null' :0
                html_fragment = _format_null(object);
                break;
            case 'Boolean' :
                html_fragment = _format_boolean(object);
                break;
            case 'Number' :
                html_fragment = _format_number(object);
                break;
            case 'String' :
                html_fragment = _format_string(object);
                break;
            case 'Array' :
                html_fragment = _format_array(object, indent_count);
                break;
            case 'Object' :
                html_fragment = _format_object(object, indent_count);
                break;
        }
        return html_fragment;
    };

    function _format_null(object){
        return '<span class="json_null">null</span>';
    }

    function _format_boolean(object){
        return '<span class="json_boolean">' + object + '</span>';
    }

    function _format_number(object){
        return '<span class="json_number">' + object + '</span>';
    }

    function _format_string(object){
        if(!isNaN(object) && object.length>=15 && $.inArray(object, _bigNums)>-1){
            return _format_number(object);
        }
        object = object.replace(/\</g,"&lt;");
        object = object.replace(/\>/g,"&gt;");
        if(0 <= object.search(/^http/)){
            object = '<a href="' + object + '" target="_blank" class="json_link">' + object + '</a>'
        }
        return '<span class="json_string">"' + object + '"</span>';
    }

    function _format_array(object, indent_count){
        var tmp_array = [];
        for(var i = 0, size = object.length; i < size; ++i){
            tmp_array.push(indent_tab(indent_count) + format(object[i], indent_count + 1));
        }
        return '<span data-type="array" data-size="' + tmp_array.length + '"><i  style="cursor:pointer;" class="fa fa-minus-square-o" onclick="hide(this)"></i>[<br/>'
            + tmp_array.join(',<br/>')
            + '<br/>' + indent_tab(indent_count - 1) + ']</span>';
    }

    function _format_object(object, indent_count){
        var tmp_array = [];
        for(var key in object){
            tmp_array.push( indent_tab(indent_count) + '<span class="json_key">"' + key + '"</span>:' +  format(object[key], indent_count + 1));
        }
        return '<span  data-type="object"><i  style="cursor:pointer;" class="fa fa-minus-square-o" onclick="hide(this)"></i>{<br/>'
            + tmp_array.join(',<br/>')
            + '<br/>' + indent_tab(indent_count - 1) + '}</span>';
    }

    function indent_tab(indent_count){
        return (new Array(indent_count + 1)).join('&nbsp;&nbsp;&nbsp;&nbsp;');
    }

    function _typeof(object){
        var tf = typeof object,
            ts = _toString.call(object);
        return null === object ? 'Null' :
            'undefined' == tf ? 'Undefined'   :
                'boolean' == tf ? 'Boolean'   :
                    'number' == tf ? 'Number'   :
                        'string' == tf ? 'String'   :
                            '[object Function]' == ts ? 'Function' :
                                '[object Array]' == ts ? 'Array' :
                                    '[object Date]' == ts ? 'Date' : 'Object';
    };

    function loadCssString(){
        var style = document.createElement('style');
        style.type = 'text/css';
        var code = Array.prototype.slice.apply(arguments).join('');
        try{
            style.appendChild(document.createTextNode(code));
        }catch(ex){
            style.styleSheet.cssText = code;
        }
        document.getElementsByTagName('head')[0].appendChild(style);
    }

    loadCssString(
        '.json_key{ color: #92278f;font-weight:bold;}',
        '.json_null{color: #f1592a;font-weight:bold;}',
        '.json_string{ color: #3ab54a;font-weight:bold;}',
        '.json_number{ color: #25aae2;font-weight:bold;}',
        '.json_boolean{ color: #f98280;font-weight:bold;}',
        '.json_link{ color: #61D2D6;font-weight:bold;}',
        '.json_array_brackets{}');

    var _JSONFormat = function(origin_data){
        //this.data = origin_data ? origin_data :
        //JSON && JSON.parse ? JSON.parse(origin_data) : eval('(' + origin_data + ')');
        _bigNums = [];
        var check_data = origin_data.replace(/\s/g,'');
        var bigNum_regex = /[^\\][\"]([\[:]){1}(\d{16,})([,\}\]])/g;
        //var tmp_bigNums = check_data.match(bigNum_regex);
        var m;
        do {
            m = bigNum_regex.exec(check_data);
            if (m) {
                _bigNums.push(m[2]);
                origin_data=origin_data.replace(/([\[:])?(\d{16,})\s*([,\}\]])/, "$1\"$2\"$3");
            }
        } while (m);
        this.data = JSON.parse(origin_data);
    };

    _JSONFormat.prototype = {
        constructor : JSONFormat,
        toString : function(){
            return format(this.data, 1);
        }
    }

    return _JSONFormat;

})();
var last_html = '';
function hide(obj){
    var data_type = obj.parentNode.getAttribute('data-type');
    var data_size = obj.parentNode.getAttribute('data-size');
    obj.parentNode.setAttribute('data-inner',obj.parentNode.innerHTML);
    if (data_type === 'array') {
        obj.parentNode.innerHTML = '<i  style="cursor:pointer;" class="fa fa-plus-square-o" onclick="show(this)"></i>Array[<span class="json_number">' + data_size + '</span>]';
    }else{
        obj.parentNode.innerHTML = '<i  style="cursor:pointer;" class="fa fa-plus-square-o" onclick="show(this)"></i>Object{...}';
    }

}

function show(obj){
    var innerHtml = obj.parentNode.getAttribute('data-inner');
    obj.parentNode.innerHTML = innerHtml;
}