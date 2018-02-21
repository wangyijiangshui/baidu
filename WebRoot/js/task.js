/**
 * 任务管理
 * @date	2014-04-19
 */
$(function(){
	 $("#listDiv").css("height", $(document).height()-170);
	
	//“任务时间安排”对话框初始化   
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
	
	//“人生目标”话框初始化
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

/*======================= Task add =======================*/
$(function(){
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
	//任务新增
	$("#add_task_button").bind('click', function(){
		openAddTaskDialog();
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
	if(!window.confirm('Are you sure to add task？')){
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

/*======================= Task update =======================*/
$(function(){
	//状态修改对话框  
	$("#updateTaskDialog").dialog({
		autoOpen: false,
		height: 450,
		width: 750,
		title:'Status Change',
		modal:true,
		buttons: [
			{text: "Ok",
				click: function() {
					updateTask();
				}
			},{
				text: "Cancel",
				click: function() {
					$( this ).dialog( "close" );
				}
			}
		]
	});
	//任务状态+备注更改
	$(".taskStatus").bind('click', function(){
		openUpdateTaskDialog($(this).attr("taskId"));
	});
})
/**
 * 打开任务状态+备注修改窗口
 */
var operatorTaskId = "";
function openUpdateTaskDialog(taskId) {
	operatorTaskId = taskId;
	$.post("task.do?method=loadTaskById",{"taskId":operatorTaskId},function(data){
	    if(data) {
	    	$("#updateTaskDialog").find("select[name='taskStatus']").val(data.taskStatus);
	    	$("#updateTaskDialog").find("textarea[name='task']").val(data.task)
	    	$("#updateTaskDialog").dialog( "open" );
	    } else {
	    	alert("History data load fail！");
	    }
	},"json");
}
/**
 * 提交任务状态+备注修改数据
 */
function updateTask() {
	if(!window.confirm('Are you sure to update task？')){
		return false;
	}
	//获取新状态
	var taskStatus = $("#updateTaskDialog select[name='taskStatus']").val();
	var task = $.trim($("#updateTaskDialog textarea[name='task']").val());
	$.post("task.do?method=updateTask",{"taskId":operatorTaskId,"taskStatus":taskStatus,"task":task},function(data){
	    if(data && data.result) {
	    	$( "#updateTaskDialog").dialog( "close" );
	    	$("#tr"+operatorTaskId).remove();
	    } else {
	    	alert("Task update fail.");
	    }
	},"json");
}

/*======================= Task delete =======================*/
$(function(){
	//双击序号删除
	$(".sequence").bind('dblclick', function(){
		deleteTask($(this).attr("taskId"));
	});
})
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

/*======================= Remark show + add + update =======================*/
$(function(){
	//历史备注详细查看对话框   
	$("#remarkViewDialog").dialog({
		autoOpen: false,
		height: 800,
		width: 1000,
		title:'View Remark',
		modal:true
	});
})
/**
 * Open task remark view dialog
 */
var remarkViewDialogTaskId = "";
function openRemarkViewDialog(taskId) {
	remarkViewDialogTaskId = taskId;
	$("#remarkViewDialog").dialog("open");
	loadHistoryRemark(taskId);
}

/**
 * Load history remark to show
 */
function loadHistoryRemark(taskId) {
	openProcessDialog();
	$("#taskViewTable").empty();
	$("#remarkViewTable").empty();
	$.post("task.do?method=loadHistoryRemark",{"taskId":taskId},function(data){
	    closeProcessDialog();
	    if (data) {
	    	var taskHtml = '<tr><td>'+
							'<div style="text-align: center;color:green;">'+data.createTime+'</div>'+
							'<div style="text-align: left;margin-top: 10px;"><p>'+data.task+'</p></div>'+
						'</td></tr>';
	    	$("#taskViewTable").html(taskHtml);
	    }
	    if(data && data.taskRemarks && data.taskRemarks.length > 0) {
	    	var remarkHtml = '';
	    	$.each(data.taskRemarks, function(i, v){
    			remarkHtml = remarkHtml + '<tr><td>'+
    								'<input type="hidden" name="remarkId" value="'+v.id+'"></span>' +
    								'<input type="hidden" name="isLatestRemark" value="'+(0 == i ? '1':'0')+'"></span>' +
					    			'<div style="text-align: left;margin-top: 10px;cursor:pointer;" ondblclick="showRemarkUpdateForm(this)"><p>'+v.remark+'</p></div>'+
					    			'<div style="text-align: right;color:green;">'+v.createTime+'</div>'+
				    			'</td></tr>';
	    	});
	    	$("#remarkViewTable").html(remarkHtml);
	    }
	},"json");
}

/**
 * Save a new remark
 */
function addRemark() {
	var remark = $.trim($("#remarkViewDialog").find("textarea[name='addRemark']").val());
	if (!remark) {
		alert("remark shoud not be empty!");
		return false;
	}
	$.post("task.do?method=addOrUpdateTaskRemark",{"taskId":remarkViewDialogTaskId,"remark":remark},function(data){
	    if(data && data.result) {
	    	$("#remarkViewDialog").find("textarea[name='addRemark']").val("");
	    	$('#remarkAddTable').hide(remarkViewDialogTaskId);
	    	loadHistoryRemark(remarkViewDialogTaskId);
	    } else {
	    	alert("add new remark fail.");
	    }
	},"json");
}

/**
 * Show remark update form to edit
 */
function showRemarkUpdateForm(obj) {
	var remark = $(obj).find("p").text();
	var html = '<table id="remarkUpdateTable" width="100%">'+
					'<tr>'+
						'<td>'+
							'<textarea name="updateRemark" rows="3" style="width: 97%">'+remark+'</textarea>'+
						'</td>'+
					'</tr>'+
					'<tr>'+
						'<td width="10%" style="text-align: right;">'+
							'<button class="ui-button ui-widget ui-state-default ui-button-text-only" onclick="updateRemark()"><span class="ui-button-text">update</span></button>'+
							'<button class="ui-button ui-widget ui-state-default ui-button-text-only" onclick="cancelUpdateRemark()"><span class="ui-button-text">Cancel</span></button>'+
						'</td>'+
					'</tr>'+
				'</table>';
	var trObj = $(obj).parent().parent();
	$(trObj).find("td").hide();
	$(trObj).append(html);
}

/**
 * Cancel update and hide update form
 */
function cancelUpdateRemark() {
	$("#remarkUpdateTable").parent().find("td").show();
	$("#remarkUpdateTable").remove();
}

/**
 * Submit update form data to save
 */
function updateRemark() {
	var remarkId = $("#remarkUpdateTable").parent().find("input[name='remarkId']").val();
	var isLatestRemark = $("#remarkUpdateTable").parent().find("input[name='isLatestRemark']").val();
	var remark = $.trim($("#remarkViewDialog").find("textarea[name='updateRemark']").val());
	if (!remark) {
		alert("remark shoud not be empty!");
		return false;
	}
	$.post("task.do?method=addOrUpdateTaskRemark",{"taskId":remarkViewDialogTaskId,"remarkId":remarkId,"remark":remark,"isLatestRemark":isLatestRemark},function(data){
	    if(data && data.result) {
	    	loadHistoryRemark(remarkViewDialogTaskId);
	    } else {
	    	alert("update remark fail.");
	    }
	},"json");
}