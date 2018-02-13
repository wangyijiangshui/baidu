$(function(){
	$("#listDiv").css("height", $(document).height()-170);
	
	$('#uploadLogoForm').form({
        url:'upload.do',
        onSubmit: function(){
	        openProcessDialog();
        },
        success:function(data){
        	data = eval('('+data+')');
        	closeProcessDialog();
       		if (data.result) {
       			$('#uploadLogo').attr('src','download.do?fileName='+data.msg);
       			$("#addOrEditForm input[name='logo']").val(data.msg);
       			resetContent("uploadLogoForm");
       		} else {
       			alert(data.msg);
       		}
        }
	});
	
	$("#uploadLogo").bind('click', function(){
		$("#fileName").click();
	});
	
	$("#fileName").bind('change', function(){
		$('#uploadLogoForm').submit();
	});
	
	
	//新增或修改对话框   
	$("#addOrEditContactsDialog").dialog({
		autoOpen: false,
		height: 850,
		width: 1000,
		title:'Add Contactor',
		modal:true,
		buttons: [
			{text: "Ok",
				click: function() {
					submitContacts();
				}
			},{
				text: "Cancel",
				click: function() {
					$( this ).dialog( "close" );
				}
			}
		]
	});
	
	//详细查看对话框
	$("#detailViewDialog").dialog({
		autoOpen: false,
		height: 850,
		width: 1000,
		title:'Detail View',
		modal:true
	});
	
	//任务新增
	$("#add_contacts_button").bind('click', function(){
		openAddContactsDialog();
	});
	
	//初始化页面中所有“分类 ”select
	$.post("contacts.do?method=queryCatType",{},function(data){
	    if(data) {
	    	var html = "";
	    	$.each(data, function(i, v){
	    		if (!html) {
	    			html = '<option value="'+v.id+'">'+v.typeName+'</option>';
	    		} else {
	    			html = html + '<option value="'+v.id+'">'+v.typeName+'</option>';
	    		}
	    	});
	    	$("#addOrEditForm select[name='catType']").html(html);
	    }
	},"json");
	//开始时间日期控件初始化
	$("#addOrEditForm input[name='birthday'],#addOrEditForm input[name='knowTime']").datepicker( {
		showButtonPanel : true,
		changeYear:true,
		changeMonth:true,
		dateFormat:'yy-mm-dd'
	});
})


/**
 * 打开新增窗口
 */
function openAddContactsDialog() {
	resetContent("addOrEditForm");
	$("#addOrEditForm input[name='id']").val("");
	editHistoryData = null;
	$("#changes").html("&nbsp;");
	$("#uploadLogo").attr("src", "image/head_icon.png");
	$("#addOrEditForm input[name='logo']").val("");
	
	$("#addOrEditContactsDialog").dialog( "open" );
} 
/**
 * 提交新增任务数据
 */
function submitContacts() {
	if(!window.confirm('Are you sure？')){
		return false;
	}
	//获取表单数据
	var contactor = $("#addOrEditForm").serializeToJson();
	
	//如果有id，则新增，否则修改
	if (!contactor.id) {
		$.post("contacts.do?method=addContactor",contactor,function(data){
		    if(data && data.result) {
		    	$("#addOrEditContactsDialog").dialog( "close" );
		    } else {
		    	alert("新增失败！");
		    }
		},"json");
	} else {
		contactor['changes'] = checkChange(contactor);
		$.post("contacts.do?method=editContactor",contactor,function(data){
		    if(data && data.result) {
		    	$("#addOrEditContactsDialog").dialog( "close" );
		    } else {
		    	alert("修改失败！");
		    }
		},"json");
	}
}

/**
 * 打开修改窗口
 */
 var editHistoryData = null;
function openEditContactsDialog(id) {
	openProcessDialog();
	resetContent("addOrEditForm");
	$("#addOrEditForm input[name='id']").val("");
	editHistoryData = null;
	$("#changes").html("&nbsp;");
	
	
	$.post("contacts.do?method=queryById",{"id":id},function(data){
		closeProcessDialog();
	    if(data) {
	    	editHistoryData = data;
	    	$("#addOrEditForm").serializeToForm(data, 'value', 'name');
	    	if(data.logo) {
	    		$("#uploadLogo").attr("src", "download.do?fileName="+id+"/"+data.logo);
	    	} else {
	    		$("#uploadLogo").attr("src", "image/head_icon.png");
	    	}
	    	
	    	$("#addOrEditContactsDialog").dialog( "open" );
	    } else {
	    	alert("数据加载失败！");
	    }
	},"json");
}

/**
 * 检查并获取当前的数据差异
 */
function checkChange(contactor) {
	if (!editHistoryData) {
		return "";
	}
	if(!contactor) {
		contactor = $("#addOrEditForm").serializeToJson();
	}
	var changes = "";
	var logoChange = "";
	for(var v in contactor){
		if (contactor[v] == editHistoryData[v]) {
			continue;
		}
		if ("logo" == v) {
			if(editHistoryData.logo) {
				logoChange = "<div>logo：" + 
							"<img style='cursor: pointer;' src='download.do?fileName="+contactor.id+"/"+editHistoryData.logo+"' width='105px;' height='120px;' border='0'/>"
							+ " ==>> "
							+ "<img style='cursor: pointer;' src='download.do?fileName="+contactor.id+"/"+contactor.logo+"' width='105px;' height='120px;' border='0'/>"
						 + "</div>";
			} else {
				logoChange = "<div>logo：" + 
							"<img style='cursor: pointer;' src='image/head_icon.png' width='105px;' height='120px;' border='0'/>"
							+ " ==>> "
							+ "<img style='cursor: pointer;' src='download.do?fileName="+contactor.id+"/"+contactor.logo+"' width='105px;' height='120px;' border='0'/>"
						 + "</div>";
			}
		} else if ("catType" == v){
			changes = changes + "<div>" + $("#addOrEditForm [name='"+v+"']").attr("title") + "：" 
									+ $("#addOrEditForm [name='"+v+"']").find("option[value='"+editHistoryData[v]+"']").text()
									+ " ==>> " 
									+ $("#addOrEditForm [name='"+v+"']").find("option[value='"+contactor[v]+"']").text()
								+"</div>";
		} else if ("remark" == v) {
			changes = changes + "<div>" + $("#addOrEditForm [name='"+v+"']").attr("title") + "：" 
									+ contactor[v]
								+"</div>";
		} else {
			changes = changes + "<div>" + $("#addOrEditForm [name='"+v+"']").attr("title") + "：" 
									+ editHistoryData[v]
									+ " ==>> " 
									+ contactor[v]
								+"</div>";
		}
	}
	$("#changes").html(changes + logoChange);
	return changes + logoChange;
}

/**
 * 打开详细查看窗口
 */
var viewId = "";
function openDetailViewDialog(id) {
	viewId = id;
	openProcessDialog();
	$("#remarkViewTable").html("");
	$("#remarkViewTable").hide();
	$("#photoViewTable").html("");
	$("#photoViewTable").hide();
	$("#photoUploadTable").hide();
	
	$.post("contacts.do?method=queryById",{"id":id},function(data){
		closeProcessDialog();
	    if(data) {
		    	$("#detailViewDialog").serializeToForm(data, 'html', 'name');
	    	$("#detailViewDialog td[name='remark']").html(data.remark);
	    	if(data.logo) {
	    		$("#detailViewDialog img[name='logo']").attr("src", "download.do?fileName="+id+"/"+data.logo);
	    	} else {
	    		$("#detailViewDialog img[name='logo']").attr("src", "image/head_icon.png");
	    	}
	    	$("#detailViewDialog").dialog( "open" );
	    } else {
	    	alert("数据加载失败！");
	    }
	},"json");
}

/**
 *	加减权重
 */
function addOrMinusWeight(id, weight) {
	var oldWeight = parseInt($("#weightDiv"+id).text());
	if('NaN' == oldWeight) {
		return false;
	}
	var newWeight = oldWeight + weight;
	//不允许低于0
	if (newWeight >= 0) {
		$.post("contacts.do?method=addOrMinusWeight",{"weight":newWeight,"id":id},function(data){
		    if(data && data.result) {
		    	$("#weightDiv"+id).text(newWeight);
		    }
		},"json");
	}
}


/**
 * 加载联系人基本信息修改历史
 */
function loadEditHistory() {
	if(!$("#remarkViewTable").html()) {
		openProcessDialog();
		$.post("contacts.do?method=loadEditHistory",{"id":viewId},function(data){
		    closeProcessDialog();
		    if(data && data.length > 0) {
		    	var remarkHtml = '';
		    	$.each(data, function(i, v){
		    		remarkHtml = remarkHtml+'<tr><td style="border-bottom:#333 1px dashed;">'+
					    			'<div style="text-align: left;margin-top: 10px;"><p>'+v.remark+'</p></div>'+
					    			'<div style="text-align: right;color:green;">'+v.createTime+'</div>'+
				    			'</td></tr>';
		    	});
		    	$("#remarkViewTable").html(remarkHtml);
		    	$("#remarkViewTable").toggle("slow");
		    }
		},"json");
	} else {
		$("#remarkViewTable").toggle("slow");
	}
}

/*==============图片上传编辑================*/
$(function(){
	$('#uploadPhotoForm').form({
        url:'upload.do',
        onSubmit: function(){
	        openProcessDialog();
        },
        success:function(data){
        	data = eval('('+data+')');
        	closeProcessDialog();
       		if (data.result) {
       			$('#photoUploadTable div[name="photoDiv"]').html('<img src="download.do?fileName='+data.msg+'" border="0" width="499px" height="299px"/>');
       			$("#photoUploadTable input[name='photo']").val(data.msg);
       			resetContent("uploadPhotoForm");
       		} else {
       			alert(data.msg);
       		}
        }
	});
	
	$("#photoUploadTable div[name='photoDiv']").bind('click', function(){
		$("#photoName").click();
	});
	
	$("#photoName").bind('change', function(){
		$('#uploadPhotoForm').submit();
	});
	
	//发布按钮单击事件(Publish)
	$("#photo_publish_button").bind('click', function(){
		publishPhoto();
	});
})

/**
 * 发布新添加的照片
 */
function publishPhoto() {
	//获取表单数据
	var photo = $("#photoUploadTable input[name='photo']").val();
	var photoRemark = $("#photoUploadTable textarea[name='photoRemark']").val();
	if(!photo) {
		alert("Fuck，please upload photo！！");
		return false;
	}
	$.post("contacts.do?method=publishPhoto",{"catId":viewId,"photo":photo,"remark":photoRemark},function(data){
		    if(data && data.result) {
		    	$('#photoUploadTable div[name="photoDiv"]').html("&nbsp;");
		    	$("#photoUploadTable input[name='photo']").val("");
		    	$("#photoUploadTable textarea[name='photoRemark']").val("");
		    	//实时更新照片展示列表
		    	var photoHtml = '<tr>'+
						  				'<td>'+
						  					'<div style="width: 100%;height: 400px;border:#333 1px dashed;cursor: pointer;text-align: center;overflow: auto;">'+
								  				'<img src="download.do?fileName='+viewId+'/'+photo+'" border="0"/>'+
								  			'</div>'+
								  			'<div style="border-bottom:#333 1px dashed;margin-top: 15px;">'+
						  						photoRemark+
						  					'</div>'+
						  					'<div style="text-align: right;margin-top: 15px;margin-bottom: 50px;">'+
						  						'<span style="border-bottom:#333 1px dashed;">'+((new Date()).dateFormat("yyyy-MM-dd hh:mm"))+'</span>'+
						  					'</div>'+
						  				'</td>'+
						  			'</tr>';
				$("#photoViewTable").prepend(photoHtml);
		    	$("#photoViewTable").show("slow");		  			
		    } else {
		    	alert("照片发布失败！");
		    }
		},"json");
}

/**
 * 加载照片信息
 */
function loadPhotos() {
	openProcessDialog();
	$.post("contacts.do?method=loadPhotos",{"id":viewId},function(data){
	    closeProcessDialog();
	    if(data && data.length > 0) {
	    	var photoHtml = '';
	    	$.each(data, function(i, v){
	    		photoHtml = photoHtml+'<tr>'+
					  				'<td>'+
					  					'<div style="width: 100%;height: 400px;border:#333 1px dashed;cursor: pointer;text-align: center;overflow: auto;">'+
							  				'<img src="download.do?fileName='+viewId+'/'+v.photo+'" border="0"/>'+
							  			'</div>'+
							  			'<div style="border-bottom:#333 1px dashed;margin-top: 15px;">'+
					  						v.remark+
					  					'</div>'+
					  					'<div style="text-align: right;margin-top: 15px;margin-bottom: 50px;">'+
					  						'<span style="border-bottom:#333 1px dashed;">'+v.createTime+'</span>'+
					  					'</div>'+
					  				'</td>'+
					  			'</tr>';
	    	});
	    	$("#photoViewTable").html(photoHtml);
	    	$("#photoViewTable").toggle("slow");
	    }
	},"json");
}