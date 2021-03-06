/**
 * 股票管理
 * @date 2014-04-01
 */
 $(function(){
	$("#listDiv").css("height", $(document).height()-163);
 
	//权重修改对话框   
	$("#weightChangeDialog").dialog({
		autoOpen: false,
		height: 500,
		width: 750,
		title:'Weight Change',
		modal:true,
		buttons: [
			{text: "Ok",
				click: function() {
					submitWeightChange();
				}
			},{
				text: "Cancel",
				click: function() {
					$( this ).dialog( "close" );
				}
			}
		]
	});
	
	//备注填写对话框   
	$("#remarkWriteDialog").dialog({
		autoOpen: false,
		height: 400,
		width: 700,
		title:'Write Remark',
		modal:true,
		buttons: [
			{text: "Ok",
				click: function() {
					submitRemark();
				}
			},{
				text: "Cancel",
				click: function() {
					$( this ).dialog( "close" );
				}
			}
		]
	});
	
	//历史备注详细查看对话框   
	$("#remarkViewDialog").dialog({
		autoOpen: false,
		height: 800,
		width: 1000,
		title:'View Remark',
		modal:true
	});
	
	//投资策略对话框   
	$("#wayToStockDialog").dialog({
		autoOpen: false,
		height: 800,
		width: 1000,
		title:'Way to stock',
		modal:true
	});
	
	//刷新界面
	$("#refresh_page_button").bind('click', function(){
		refreshPageButton();
	});
	//刷新数据库
	$("#refresh_db_button").bind('click', function(){
		if(window.confirm('Are you sure？')){
			refreshDbButton();
		}
	});
	//刷新基础信息
	$("#refresh_base_button").bind('click', function(){
		if(window.confirm('Are you sure？')){
			refreshBaseButton();
		}
	});
	//清空新提示消息
	$(".read_news_td").bind('click', function(){
		readNews(this);
	});
	//打开投资策略对话框 
	$("#way_to_stock_button").bind('click', function(){
		$( "#wayToStockDialog").dialog( "open" );
	});
})

/**
 * 打开权重修改窗口
 */
var operatorId = "";
function openQzChangeDialog(id,gpjzqz,star,orderNum) {
	operatorId = id;
	$("#gpjzqz").val(gpjzqz);
	$("#star").val(star);
	$("#orderNum").val(orderNum);
	$( "#weightChangeDialog").dialog( "open" );
} 

/**
 * 修改权重
 */
function submitWeightChange() {
	var gpjzqz = $("#gpjzqz").val();
	var remark = $.trim($("#remark1").val());
	var star = $("#star").val();
	var orderNum = $("#orderNum").val();
	$.post("stock.do?method=changeWeight",{"gpjzqz":gpjzqz,"star":star,"remark":remark,"id":operatorId,"orderNum":orderNum},function(data){
	    if(data && data.result) {
	    	alert("操作成功！");
	    	$( "#weightChangeDialog").dialog( "close" );
	    	$( "#tr"+operatorId).remove();
	    } else {
	    	alert("操作失败！");
	    }
	},"json");
}

/**
 * 刷新数据库
 */
function refreshDbButton() {
	openProcessDialog();
	$.post("stock.do?method=refreshDbButton",{},function(data){
		closeProcessDialog();
	    if(data && data.result) {
	    	alert("操作成功！");
	    	$( "#weightChangeDialog").dialog( "close" );
	    	window.location.href=location.href;
	    } else {
	    	alert("操作失败！");
	    }
	},"json");
}

/**
 * 刷新数据库
 */
function refreshBaseButton() {
	openProcessDialog();
	$.post("stock.do?method=refreshBaseButton",{},function(data){
		closeProcessDialog();
	    if(data && data.result) {
	    	alert("操作成功！");
	    	$( "#weightChangeDialog").dialog( "close" );
	    	window.location.href=location.href;
	    } else {
	    	alert("操作失败！");
	    }
	},"json");
}

/**
 * 刷新页面
 *
 */
function refreshPageButton() {
	$.each($(".hqxx"), function(i, v){
		$(this).find("a").html("&nbsp;");
	});
	$.post("stock.do?method=refreshPageButton",{},function(data){
	    if(data && data.result) {
			$.each(eval('('+data.msg+')'), function(i, v){
				if (v.zdbl > 0) {
					$("#gpjg"+v.gpdm).find("a").html(v.gpjg);
					$("#zdbl"+v.gpdm).find("a").html('<font color="red">↑+'+v.zdbl+'%</font>');
				} else if (v.zdbl < 0) {
					$("#gpjg"+v.gpdm).find("a").html(v.gpjg);
					$("#zdbl"+v.gpdm).find("a").html('<font color="green">↓'+v.zdbl+'%</font>');
				} else {
					$("#gpjg"+v.gpdm).find("a").html(v.gpjg);
					$("#zdbl"+v.gpdm).find("a").html(v.zdbl+'%');
				}
			});		    	
	    }
	},"json");
}

/**
 * 清空新提示消息(最近更新内容提示)
 */
function readNews(obj) {
	id = $(obj).attr("nodeId");
	msg = $.trim($(obj).text());
	if("" != msg) {
		openProcessDialog();
		$.post("stock.do?method=readNews",{"id":id},function(data){
			closeProcessDialog();
		    if(data && data.result) {
		    	$("#weightChangeDialog").dialog( "close" );
		    	$(obj).html("&nbsp;");
		    } else {
		    	alert("操作失败！");
		    }
		},"json");
	}
}

/**
 * 打开备注填写对话框
 */
var operatorGpdm = "";
function openRemarkWriteDialog(gpdm) {
	operatorGpdm = gpdm;
	$( "#remarkWriteDialog").dialog( "open" );
}

/**
 * 提交保存备注信息
 */
function submitRemark() {
	var remark = $.trim($("#remark").val());
	if(!remark) {
		alert("搞毛呀！！！！！！！！！！！");
		return false;
	}
	openProcessDialog();
	$.post("stock.do?method=writeRemark",{"remark":remark,"gpdm":operatorGpdm},function(data){
	    closeProcessDialog();
	    if(data && data.result) {
	    	alert("操作成功！");
	    	$( "#remarkWriteDialog").dialog( "close" );
	    	if (remark.length > 12) {
	    		remark = remark.substring(0, 12)+"...";
	    	}
	    	$( "#remark"+operatorGpdm).html(remark);
	    	$( "#remarkTime"+operatorGpdm).html("0");
	    } else {
	    	alert("操作失败！");
	    }
	},"json");
}

/**
 * 打开备注详细查看对话框
 */
function openRemarkViewDialog(gpdm) {
	openProcessDialog();
	$.post("stock.do?method=loadHistoryRemark",{"gpdm":gpdm},function(data){
	    closeProcessDialog();
	    if(data && data.length > 0) {
	    	var html = '';
	    	$.each(data, function(i, v){
	    		if(!html) {
	    			html = '<tr><td>'+
				    			'<div style="text-align: left;margin-top: 10px;"><p>'+v.remark+'</p></div>'+
				    			'<div style="text-align: right;color:green;">'+v.createTime+'</div>'+
			    			'</td></tr>';
	    		} else {
	    			html = html + '<tr><td>'+
						    			'<div style="text-align: left;margin-top: 10px;"><p>'+v.remark+'</p></div>'+
						    			'<div style="text-align: right;color:green;">'+v.createTime+'</div>'+
					    			'</td></tr>';
	    		}
	    	});
	    	$( "#remarkViewTable").html(html);
	    	$("#remarkViewDialog").dialog( "open" );
	    }
	},"json");
}

/**
 * 更新股票最近查看时间
 */
function submitRemarkTime(operatorGpdm) {
	openProcessDialog();
	$.post("stock.do?method=writeRemarkTime",{"gpdm":operatorGpdm},function(data){
	    closeProcessDialog();
	    if(data && data.result) {
	    	$( "#remarkTime"+operatorGpdm).html("0");
	    } else {
	    	alert("操作失败！");
	    }
	},"json");
}