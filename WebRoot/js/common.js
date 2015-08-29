$(function(){
	$( "#menu" ).menu();
	
	//操作进度提示框
	$("#processDialog").dialog({
		autoOpen: false,
		height: 150,
		width: 250,
		title:'处理中....',
		modal:true
	});
});

/**
 * 字符串替换
 *
 * 调用方式：如想替换字符串"aa bb cc"中的所有空格,可以执行"aa bb cc".replaceAll(" ", "");
 */
String.prototype.replaceAll = function(s1,s2) { 
    return this.replace(new RegExp(s1,"gm"),s2); 
}

/**
 *	日期格式化函数，支持模式：YYYY/yyyy年MM月dd日hh小时mm分ss秒SSS毫秒
 */
Date.prototype.dateFormat = function(format){ 
	var o = { 
		"M+" : this.getMonth()+1, //month 
		"d+" : this.getDate(), //day 
		"h+" : this.getHours(), //hour 
		"m+" : this.getMinutes(), //minute 
		"s+" : this.getSeconds(), //second 
		"q+" : Math.floor((this.getMonth()+3)/3), //quarter 
		"S" : this.getMilliseconds() //millisecond 
	} 
	if(/(y+)/.test(format)) { 
		format = format.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
	} 
	for(var k in o) { 
		if(new RegExp("("+ k +")").test(format)) { 
			format = format.replace(RegExp.$1, RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length)); 
		} 
	} 
	return format; 
} 

/**
 *	清空指定表单中的内容,参数为目标form的id
 *
 *	@param 	formId	将要清空内容的表单的id
 */
function resetContent(formId) {
	var clearForm = document.getElementById(formId);
	if (null != clearForm) {
		clearForm.reset();
   		$("#"+formId+" :input[type='hidden']").val("");
	}
}

/**
 * 格式化表单内容为json字符串
 */
$.fn.serializeToJson = function(notEmptyField){
    var obj = {};
    $.each( this.serializeArray(), function(i,o){
        var n = o.name, v = $.trim(o.value);
        if (!(notEmptyField && "" == v)) {
        	obj[n] = (obj[n] === undefined) ? v : $.isArray(obj[n]) ? obj[n].concat(v) : [obj[n], v];
        }
    });
    return obj;
};

/**
 * 将json中的值填充到页面中，按field域进行填充
 *
 * @param	jsonObject	用于填充的json数据对象
 * @param	type	填充数据的方式，text：将数据填充到text域中，否则填充到value域中
 */
$.fn.serializeToForm = function(jsonObject, type, tagAttr){
	if(!tagAttr)tagAttr = "field";
	if(!jsonObject)return false;
     for(var key in jsonObject) {
     	var inputObj = this.find("["+tagAttr+"='"+key+"']");
     	if (inputObj && inputObj.size() > 0) {
     		if(null != jsonObject[key] && "null" != jsonObject[key]) {
	     		if (type && 'text'== type) {
	     			inputObj.text(jsonObject[key]);
	     		} else {
	     			inputObj.val(jsonObject[key]);
	     		}
     		} else {
     			if (type && 'text'== type) {
	     			inputObj.text("&nbsp;");
	     		} else {
	     			inputObj.val("");
	     		}
     		}
     	}
     }
};

/**
 * 操作进度条开启
 */
function openProcessDialog() {
	$( "#processDialog").dialog( "open" );
}

/**
 * 操作进度条关闭
 */
function closeProcessDialog() {
	$( "#processDialog").dialog( "close" );
}

/**
 *	全局监控ajax请求，处理session无效、无权限操作时，自动跳转到登录页面
 */
$(function(){
	$.ajaxSetup({
       cache : false,
       global : true,
       complete: function(req, status) {
       try{
           var reqObj = eval('('+req.responseText+")");
           //如果数据请求验证时，当前登录状态已经失效   
           if(reqObj && reqObj.noLogin){
           	   alert("登录状态已失效，请重新登录！");
               window.location.href = basePath + '/index.jsp';
           //如果数据请求验证时，对应的请求资源(路径)没有权限
           } else if (reqObj && reqObj.noRight) {
           	   alert("你无权进行该项操作！");
               window.location.href = basePath + '/index.jsp';
           }
         }catch(e){}
       }
   });
});