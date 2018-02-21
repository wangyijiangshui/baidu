<%@ page language="java" import="com.duapp.util.*,java.sql.*" pageEncoding="utf-8"%>
<!DOCTYPE html>
<html>
  <head>
    <title>index</title>
    <!-- 导入框架css和js库 -->
	<jsp:include page="head-ui-1.10.4.jsp"></jsp:include>
	<style type="text/css">
		.input{
			font-size: 36px;
			cursor: pointer;
		}
		
		.ui-button-text{
			cursor: pointer;
		}
		
		.ui-button{
			width: 120px;
			height: 70px;
			margin-top: 10px;
		}
		
		#mainDiv td{
			text-align: center;
			height: 70px;
		}
		
		#uiThemeChangeDialog td{
			color: white;
			height: 40px;
			cursor: pointer;
		}
	</style>
	
	<script type="text/javascript">
		//点击次数
		var clickNum = 0;
		//密码，记录四次点击的数字密码
		var pass = "";
		//暂时在密码框中的内容
		var passHtml = "";
		
		$(function(){
			//如果登陆了
			if(userName) {
				$("#headDiv").fadeIn("slow");
				$("#indexContent").fadeIn("slow");
				$("#loginTable").fadeOut("slow");
			} else {
				$("#headDiv").fadeOut("slow");
				$("#indexContent").fadeOut("slow");
				$("#loginTable").fadeIn("slow");
			}
		
			//数字输入框按钮点击事件绑定
			$(".input").bind('click', function(){
				passHtml = "";
				clickNum = clickNum + 1;
				var val = $(this).text();
				pass = pass + "" + val;
				
				for (var i = 1; i <= clickNum; i++) {
					passHtml = passHtml + "*&nbsp;&nbsp;";
				}
				$("#pass").html(passHtml);
				if (4 == clickNum){
					$.post("login.do?method=login",{"userName":pass},function(data){
					    if(data && data.result) {
					    	$("#headDiv").fadeIn("slow");
					    	$("#indexContent").fadeIn("slow");
					    	$("#loginTable").fadeOut("slow");
					    }
					    clickNum = 0;
						pass = "";
						$("#pass").html("&nbsp;");
					},"json");
				}
			});
			
			//主题修改对话框   
			$("#uiThemeChangeDialog").dialog({
				autoOpen: false,
				height: 800,
				width: 1000,
				title:'UI Theme Change',
				modal:true
			});
			
			//打开主题修改对话框
			$("#ui_theme_button").bind('click', function(){
				$( "#uiThemeChangeDialog").dialog( "open" );
			});
			
			//打开主题修改对话框
			$("#uiThemeChangeDialog img").bind('dblclick', function(){
				var src = $(this).attr("src");
				var theme = src.substring(src.lastIndexOf("/")+1,src.lastIndexOf("."))
				submitUiThemeChange(theme);
			});
			
			//退出系统
			$("#exit_button").bind('click', function(){
				$.post("login.do?method=exit",{},function(data){
				    if(data && data.result) {
				    	$("#headDiv").fadeOut("slow");
				    	$("#indexContent").fadeOut("slow");
				    	$("#loginTable").fadeIn("slow");
				    } else {
				    	alert("fuck！Exit Failure！！");
				    }
				},"json");
			});
			
			//启动我的生命倒计时
			myLiveCountdown();
			
			$("#mainDiv").css("height", $(document).height()-130);
		});
		
		/**
		 * 修改系统主题
		 */
		function submitUiThemeChange(theme) {
			$.post("index.do?method=changeUiTheme",{"theme":theme},function(data){
			    if(data && data.result) {
			    	$( "#uiThemeChangeDialog").dialog( "close" );
			    	window.location = 'index.jsp';
			    } else {
			    	alert("主题修改失败！");
			    }
			},"json");
		}
		
		//我的生命倒计时
		var time_end = new Date("2034/12/31 23:59:59");
		//time_end = new Date("2014/06/02 17:56:59");
		time_end = time_end.getTime();
		function myLiveCountdown(){
			var time_now = new Date();  // 获取当前时间
			time_now = time_now.getTime();
			var time_distance = time_end - time_now;  // 时间差：活动结束时间减去当前时间  
			var int_day=0, int_hour=0, int_minute=0, int_second=0;  
			if(time_distance >= 0){  
				// 相减的差数换算成天数  
				int_day = Math.floor(time_distance/86400000);
				time_distance -= int_day * 86400000;
				// 相减的差数换算成小时
				int_hour = Math.floor(time_distance/3600000);
				time_distance -= int_hour * 3600000; 
				// 相减的差数换算成分钟  
				int_minute = Math.floor(time_distance/60000);  
				time_distance -= int_minute * 60000;
				// 相减的差数换算成秒数 
				int_second = Math.floor(time_distance/1000);  
				// 判断小时小于10时，前面加0进行占位
				if(int_hour < 10)
				int_hour = "0" + int_hour; 
				// 判断分钟小于10时，前面加0进行占位     
				if(int_minute < 10)   
				int_minute = "0" + int_minute; 
				// 判断秒数小于10时，前面加0进行占位
				if(int_second < 10)
				int_second = "0" + int_second;      
				setTimeout("myLiveCountdown()",1000);
				// 显示倒计时效果 
				$("#myLiveCountdown").html(int_day+"天"+int_hour+"时"+int_minute+"分"+int_second+"秒&nbsp;&nbsp;&nbsp;&nbsp;<s style='color:grey'>"+(7517-int_day)+"</s>");
			} else {
				// 显示倒计时效果 
				$("#myLiveCountdown").html("20年走完，水，你现在幸福吗？");
			}
			
		}
	</script>
  </head>
  
  <body>
  	<div id="headDiv" style="display: none;">
	  	<div class="ui-widget" style="font-size: 14px;">
			<div class="ui-state-highlight ui-corner-all" style="height: 75px;">
				<div class="menuDiv" style="margin-top: 1px;">
					<table border="0" width="100%">
						<tr>
							<td width="33%">
								<jsp:include page="menu.jsp"></jsp:include>
							</td>
							<td style="text-aligin:center;">
								你以为我在和你开玩笑呢！！
							</td>
							<td align="right" width="33%">
								<button id="ui_theme_button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" style="border-radius: 10px;height:45px;width:220px;">
									<span class="ui-button-text">UI Theme</span>
								</button>
								<button id="exit_button" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" style="border-radius: 10px;height:45px;width:220px;">
									<span class="ui-button-text">Exit</span>
								</button>
							</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
	</div>
		
	
  	<div id="mainDiv" style="overflow: auto;width: 100%;height: 500px;">
 		<div id="indexContent" style="margin-top: 5px;display: none;">
	  		<table width="100%">
	  			<tr>
	  				<th width="1%"></th>
	  				<th align="left">
	  					<div class="ui-widget" style="margin-top: 1px;font-size: 20px;margin-bottom: 10px;text-align: center;">
							服从命令、快速执行、一切从简、一次成功————>敢想、敢拼、敢做、敢梦
							
						</div>
						
						<div class="ui-widget" style="margin-top: 10px;">
							<div class="ui-state-error ui-corner-all" style="padding: 0 .7em;">
								<p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
								<strong>呵呵：</strong>拒绝木讷，学会主动，学会体贴，最主要的是学会自我做主，领导生活。要把该照顾的人，照顾的妥妥帖帖的！</p>
							</div>
						</div>
						
						<div class="ui-widget" style="margin-top: 10px;">
							<div class="ui-state-error ui-corner-all" style="padding: 0 .7em;">
								<p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
								<strong>呵呵：</strong>万不得已不撒谎，记住朋友姓名，记住亲人手机生日。</p>
							</div>
						</div>
						
						<div class="ui-widget" style="margin-top: 10px;">
							<div class="ui-state-error ui-corner-all" style="padding: 0 .7em;">
								<p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
								<strong>双重身份：</strong>“执行我” 听命于 “计划我”！每周“计划我”上班一次，制定或取消短长期各种任务。</p>
							</div>
						</div>
						<div class="ui-widget" style="margin-top: 10px;">
							<div class="ui-state-error ui-corner-all" style="padding: 0 .7em;">
								<p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
								<strong>呵呵：</strong>学会做减法，在精而不在多</p>
							</div>
						</div>
		  				<div class="ui-widget" style="margin-top: 10px;">
							<div class="ui-state-error ui-corner-all" style="padding: 0 .7em;">
								<p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
								<strong>呵呵：</strong>大度人生==>>行到水穷处，坐看云起时</p>
							</div>
						</div>
						<div class="ui-widget" style="margin-top: 10px;">
							<div class="ui-state-error ui-corner-all" style="padding: 0 .7em;">
								<p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
								<strong>呵呵：</strong>看得起任何人，看不起任何人</p>
							</div>
						</div>
						<div class="ui-widget" style="margin-top: 10px;">
							<div class="ui-state-error ui-corner-all" style="padding: 0 .7em;">
								<p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
								<strong>呵呵：</strong>人生最大的悲哀不是失去太多，而是计较太多；君子上善若水，水善利万物而不争</p>
							</div>
						</div>
						<div class="ui-widget" style="margin-top: 10px;">
							<div class="ui-state-error ui-corner-all" style="padding: 0 .7em;">
								<p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
								<strong>呵呵：</strong>宁愿为自己做过的后悔，也不愿为自己没做的遗憾；很多事情现在不做，可能永远都不会做</p>
							</div>
						</div>
						<div class="ui-widget" style="margin-top: 10px;">
							<div class="ui-state-error ui-corner-all" style="padding: 0 .7em;">
								<p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
								<strong>呵呵：</strong>就算不快乐也不要皱眉，因为你永远也不知道谁会爱上你的笑容</p>
							</div>
						</div>
						<div class="ui-widget" style="margin-top: 10px;">
							<div class="ui-state-error ui-corner-all" style="padding: 0 .7em;">
								<p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
								<strong>呵呵：</strong>天下之事，只该难得，不该易得，易得之事易失去，难得之事难失去</p>
							</div>
						</div>
						<div class="ui-widget" style="margin-top: 10px;">
							<div class="ui-state-error ui-corner-all" style="padding: 0 .7em;">
								<p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
								<strong>呵呵：</strong>争论中让对方赢，不放过任何一个称赞别人的机会。</p>
							</div>
						</div>
						<div class="ui-widget" style="margin-top: 10px;">
							<div class="ui-state-error ui-corner-all" style="padding: 0 .7em;">
								<p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
								<strong>呵呵：</strong>快乐人生的十个要点==>>1、脾气小一点。2、心情好一点。3、嘴巴甜一点。4、脑袋活一点。5、行动快一点。6、效率高一点。7、胆子大一点。8、理由少一点。9、运动多一点。10、身体好一点</p>
							</div>
						</div>
						<div id="myLiveCountdown" class="ui-widget" style="margin-top: 20px;text-align: center;font-size: 60px;color: red;">
							
						</div>
	  				</th>
	  				<th width="1%"></th>
	  			</tr>
	  		</table>
	  	</div>
	  	
	  	<table id="loginTable" border="0" width="100%" style="margin-top: 100px;display: none;" cellpadding="0" cellspacing="0">
 				<tr>
 					<td width="35%">&nbsp;</td>
 					<td style="border:#333 1px dashed;">
 						<table border="0" width="100%" cellpadding="0" cellspacing="0">
 							<thead>
  							<tr>
  								<td colspan="3">
  									<button class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
										<span id="pass" class="ui-button-text" style="width: 80px;">
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										</span>
									</button>
  								</td>
  							</tr>
 							</thead>
 							<tbody>
 								<tr>
  								<td colspan="3" height="20px;">
  									&nbsp;
  								</td>
  							</tr>
  							<tr>
  								<td>
  									<button class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
										<span class="input ui-button-text">7</span>
									</button>
  								</td>
  								<td>
  									<button class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
										<span class="input ui-button-text">8</span>
									</button>
  								</td>
  								<td>
  									<button class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
										<span class="input ui-button-text">9</span>
									</button>
  								</td>
  							</tr>
  							<tr>
  								<td>
  									<button class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
										<span class="input ui-button-text">4</span>
									</button>
  								</td>
  								<td>
  									<button class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
										<span class="input ui-button-text">5</span>
									</button>
  								</td>
  								<td>
  									<button class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
										<span class="input ui-button-text">6</span>
									</button>
  								</td>
  							</tr>
  							<tr>
  								<td>
  									<button class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
										<span class="input ui-button-text">1</span>
									</button>
  								</td>
  								<td>
  									<button class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
										<span class="input ui-button-text">2</span>
									</button>
  								</td>
  								<td>
  									<button class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
										<span class="input ui-button-text">3</span>
									</button>
  								</td>
  							</tr>
  							<tr>
  								<td>
  									&nbsp;
  								</td>
  								<td>
  									<button class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only">
										<span class="input ui-button-text">0</span>
									</button>
  								</td>
  								<td>
  									&nbsp;
  								</td>
  							</tr>
 							</tbody>
 						</table>
 					</td>
 					<td width="35%">&nbsp;</td>
 				</tr>
 			</table>
  	</div>
  	
  	<!-- 系统主题修改对话框   -->
  	<div id="uiThemeChangeDialog">
  		<table width="100%" border="0" cellpadding="0" cellspacing="0" class="ui-widget-content">
  			<tr>
  				<td align="center"><img src="image/ui-lightness.png" border="0"/></td>
  				<td align="center"><img src="image/ui-darkness.png" border="0"/></td>
  				<td align="center"><img src="image/smoothness.png" border="0"/></td>
  				<td align="center"><img src="image/start.png" border="0"/></td>
  			</tr>
  			<tr>
  				<td align="center">UI lightness</td>
  				<td align="center">UI darkness</td>
  				<td align="center">Smoothness</td>
  				<td align="center">Start</td>
  			</tr>
  			
  			<tr>
  				<td align="center"><img src="image/redmond.png" border="0"/></td>
  				<td align="center"><img src="image/sunny.png" border="0"/></td>
  				<td align="center"><img src="image/overcast.png" border="0"/></td>
  				<td align="center"><img src="image/le-frog.png" border="0"/></td>
  			</tr>
  			<tr>
  				<td align="center">Redmond</td>
  				<td align="center">Sunny</td>
  				<td align="center">Overcast</td>
  				<td align="center">Le Frog</td>
  			</tr>
  			
  			<tr>
  				<td align="center"><img src="image/flick.png" border="0"/></td>
  				<td align="center"><img src="image/pepper-grinder.png" border="0"/></td>
  				<td align="center"><img src="image/eggplant.png" border="0"/></td>
  				<td align="center"><img src="image/dark-hive.png" border="0"/></td>
  			</tr>
  			<tr>
  				<td align="center">Flick</td>
  				<td align="center">Pepper Grinder</td>
  				<td align="center">Eggplant</td>
  				<td align="center">Dark Hive</td>
  			</tr>
  			
  			<tr>
  				<td align="center"><img src="image/cupertino.png" border="0"/></td>
  				<td align="center"><img src="image/smoothness.png" border="0"/></td>
  				<td align="center"><img src="image/blitzer.png" border="0"/></td>
  				<td align="center"><img src="image/humanity.png" border="0"/></td>
  			</tr>
  			<tr>
  				<td align="center">Cupertino</td>
  				<td align="center">South Street</td>
  				<td align="center">Blitzer</td>
  				<td align="center">Humanity</td>
  			</tr>
  			
  			<tr>
  				<td align="center"><img src="image/hot-sneaks.png" border="0"/></td>
  				<td align="center"><img src="image/excite-bike.png" border="0"/></td>
  				<td align="center"><img src="image/vader.png" border="0"/></td>
  				<td align="center"><img src="image/dot-luv.png" border="0"/></td>
  			</tr>
  			<tr>
  				<td align="center">Hot Sneaks</td>
  				<td align="center">Excite Bike</td>
  				<td align="center">Vader</td>
  				<td align="center">Dot Luv</td>
  			</tr>
  			
  			<tr>
  				<td align="center"><img src="image/mint-choc.png" border="0"/></td>
  				<td align="center"><img src="image/black-tie.png" border="0"/></td>
  				<td align="center"><img src="image/trontastic.png" border="0"/></td>
  				<td align="center"><img src="image/swanky-purse.png" border="0"/></td>
  			</tr>
  			<tr>
  				<td align="center">Mint Choc</td>
  				<td align="center">Black Tie</td>
  				<td align="center">Trontastic</td>
  				<td align="center">Swanky Purse</td>
  			</tr>
  		</table>
  	</div>
  	
  </body>
</html>
