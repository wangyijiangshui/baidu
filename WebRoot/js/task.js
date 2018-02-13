/**
 * 任务管理
 * @date	2014-04-19
 */
 $(function(){
	 $("#listDiv").css("height", $(document).height()-170);
	 
	//新增对话框   
	$("#addOrEditTaskDialog").dialog({
		autoOpen: false,
		height: 550,
		width: 750,
		title:'Add Task',
		modal:true,
		buttons: [
			{text: "Ok",
				click: function() {
					submitTask();
				}
			},{
				text: "Cancel",
				click: function() {
					$( this ).dialog( "close" );
				}
			}
		]
	});
	//状态修改对话框  
	$("#statusChangeDialog").dialog({
		autoOpen: false,
		height: 450,
		width: 750,
		title:'Status Change',
		modal:true,
		buttons: [
			{text: "Ok",
				click: function() {
					submitStatusChange();
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
	//开始时间日期控件初始化
	$("#addOrEditForm input[name='startTime'],#addOrEditForm input[name='endTime']").datepicker( {
		showButtonPanel : true,
		changeYear:true,
		changeMonth:true,
		dateFormat:'yy-mm-dd'
	});
	//初始化页面中所有“任务来源”select
	$.post("task.do?method=queryComes",{},function(data){
	    if(data) {
	    	var html = "";
	    	$.each(data, function(i, v){
	    		if (!html) {
	    			html = '<option value="'+v.id+'">'+v.taskCome+'</option>';
	    		} else {
	    			html = html + '<option value="'+v.id+'">'+v.taskCome+'</option>';
	    		}
	    	});
	    	$("#addOrEditForm select[name='taskCome']").html(html);
	    }
	},"json");
	
	//任务新增
	$("#add_task_button").bind('click', function(){
		openAddTaskDialog();
	});
	//任务状态+备注更改
	$(".taskStatus").bind('click', function(){
		openStatusChangeDialog($(this).attr("taskId"));
	});
	//双击序号删除
	$(".sequence").bind('dblclick', function(){
		deleteTask($(this).attr("taskId"));
	});
	
	//“任务时间安排”话框   
	$("#taskTimeDialog").dialog({
		autoOpen: false,
		height: 650,
		width: 1100,
		title:'Task Time',
		modal:true
	});
	
	//打开“任务时间安排”对话框
	$("#task_time_button").bind('click', function(){
		$( "#taskTimeDialog").dialog( "open" );
	});
	
	//“人生目标”话框   
	$("#taskGoalDialog").dialog({
		autoOpen: false,
		height: 650,
		width: 1100,
		title:'Task Goal',
		modal:true
	});
	
	//打开“人生目标”对话框
	$("#task_goal_button").bind('click', function(){
		$( "#taskGoalDialog").dialog( "open" );
	});
})

/**
 * 打开新增窗口
 */
function openAddTaskDialog() {
	resetContent("addOrEditForm");
	$("#addOrEditTaskDialog").dialog( "open" );
} 
/**
 * 提交新增任务数据
 */
function submitTask() {
	if(!window.confirm('Are you sure？')){
		return false;
	}
	//获取表单数据
	var task = $("#addOrEditForm").serializeToJson();
	$.post("task.do?method=addTask",task,function(data){
	    if(data && data.result) {
	    	$( "#addOrEditTaskDialog").dialog( "close" );
	    } else {
	    	alert("新增失败！");
	    }
	},"json");
}

/**
 * 打开任务状态+备注修改窗口
 */
var operatorId = "";
function openStatusChangeDialog(id) {
	operatorId = id;
	$("#statusChangeDialog").dialog( "open" );
}
/**
 * 提交任务状态+备注修改数据
 */
function submitStatusChange() {
	if(!window.confirm('Are you sure？')){
		return false;
	}
	//获取新状态
	var status = $("#statusChangeDialog select[name='taskStatus']").val();
	//var statusText = $("#statusChangeDialog select[name='taskStatus']").find("option:selected").text();
	var remark = $.trim($("#statusChangeDialog textarea[name='remark']").val());
	$.post("task.do?method=statusChange",{"id":operatorId,"status":status,"remark":remark},function(data){
	    if(data && data.result) {
	    	$( "#statusChangeDialog").dialog( "close" );
	    	$("#tr"+operatorId).remove();
	    } else {
	    	alert("状态变更失败！");
	    }
	},"json");
}

/**
 * 删除
 */
function deleteTask(id) {
	if(!window.confirm('Are you sure to delete this note ？')){
		return false;
	}
	$.post("task.do?method=deleteTask",{"id":id},function(data){
	    if(data && data.result) {
	    	$("#tr"+id).remove();
	    } else {
	    	alert("删除失败！");
	    }
	},"json");
}

/**
 * 打开备注详细查看对话框
 */
function openRemarkViewDialog(id) {
	openProcessDialog();
	$.post("task.do?method=loadHistoryRemark",{"id":id},function(data){
	    closeProcessDialog();
	    if(data && data.length > 0) {
	    	var remarkHtml = '';
	    	var taskHtml = '';
	    	$.each(data, function(i, v){
	    		if (i+1 == data.length) {
	    			taskHtml = '<tr><td>'+
					    			'<div style="text-align: center;color:green;">'+v.createTime+'</div>'+
					    			'<div style="text-align: left;margin-top: 10px;"><p>'+v.remark+'</p></div>'+
				    			'</td></tr>';
	    		}else {
		    		if(!remarkHtml) {
		    			remarkHtml = '<tr><td>'+
					    			'<div style="text-align: left;margin-top: 10px;"><p>'+v.remark+'</p></div>'+
					    			'<div style="text-align: right;color:green;">'+v.createTime+'</div>'+
				    			'</td></tr>';
		    		} else {
		    			remarkHtml = remarkHtml + '<tr><td>'+
							    			'<div style="text-align: left;margin-top: 10px;"><p>'+v.remark+'</p></div>'+
							    			'<div style="text-align: right;color:green;">'+v.createTime+'</div>'+
						    			'</td></tr>';
		    		}
	    		}
	    	});
	    	$("#taskViewTable").html(taskHtml);
	    	$("#remarkViewTable").html(remarkHtml);
	    	$("#remarkViewDialog").dialog( "open" );
	    }
	},"json");
}