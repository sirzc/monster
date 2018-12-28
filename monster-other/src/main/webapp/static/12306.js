//记录等待时间
var waittime=0;


//统计等待时间，防止刷新间隔时间短时导致卡住不再刷票
function waitforRestart(){
    if($(".ad-gt").length>0){
        $(".ad-gt").remove();
    }
    if($(".audiojs").length>0){
        $(".audiojs").remove();
    }
    if(!$("#autoSubmit").prop("checked"))return ;
    if($("#query_ticket").text()=="停止查询"){
        waittime++;
    }
    if(waittime>10){
        $("#query_ticket").click();
        setTimeout(function(){
            waittime=0;
            if($("#query_ticket").text()=="查询") {
                $("#query_ticket").click();
            }
        },1000);
    }
}

//防自动退出-1分钟刷新一次“个人信息”页面，保证登录状态
function preventLogout(){
    setInterval(function(){
        if($(".footer").length>0){
            $("#footer-my").remove();
            var htmlStr="<iframe id='footer-my' width='0' height='0' src='https://kyfw.12306.cn/otn/index/initMy12306?t="+(new Date().getTime())+"'></iframe>";
            $(".footer").append(htmlStr);
        }
    },1000*30);
}

//添加桌面通知
function showNotice() {
    var timer_desktopNotice = setInterval(function(){
        if($("#tryPlayer").length>0 && $("#tryPlayer").text=="停止提示音乐"){
            Notification.requestPermission(function (perm) {
                if (perm == "granted") {
                    var notification = new Notification("【恭喜恭喜】", {
                        dir: "auto",
                        lang: "hi",
                        tag: "12306刷票提示",
                        icon: "https://kyfw.12306.cn/otn/resources/images/ots/favicon.ico",
                        body: "恭喜你，终于抢到票了！！！"
                    });
                    clearInterval(timer_desktopNotice);
                }
            });
        }
    },500);
}

//页面关闭或者跳转时，自动提示（当提供提交票时，自动提示）
$(window).bind('beforeunload',function(){
    Notification.requestPermission(function (perm) {
        if (perm == "granted") {
            var notification = new Notification("【恭喜恭喜】", {
                dir: "auto",
                lang: "hi",
                tag: "12306刷票提示",
                icon: "https://kyfw.12306.cn/otn/resources/images/ots/favicon.ico",
                body: "恭喜你，终于抢到票了！！！"
            });
        }
    });
    $(window).unbind('beforeunload');
});

//注册回调方法
function callback(fun1,fun2){
    fun1();
    fun2();
}


//添加乘车人
function addBuyer(name){
    callback($.showSelectBuyer,function(){
        $("#buyer-list li[p_value^="+name+"]").click();
        $.closeSelectBuyer();
    });
}

//突破限制，添加车次
function addTrain(trainCode){
    $("#prior_train").append('<span name="prior_train-span" class="sel-box w80">'+trainCode+'<a class="close" href="javascript:" onclick="$.removeSel(this,\''+trainCode+'\',4)"></a></span>');
}

//添加优先席别
function addSeat(seat){
    callback($.showSelectSeat, function(){
        $("#seat-list li[name^="+seat+"]").click();
        $.closeSelectSeat();
    });
}

//添加备选日期
function addDate(date){
    callback($.showSelectDate, function(){
        $("#date-list li[name^="+date+"]").click();
        $.closeSelectDate();
    });
}

//自定义查询区间
function addTimes(startTime,endTime){
    $("#cc_start_time").append('<option value="'+startTime.replace(":","")+endTime.replace(":","")+'">'+startTime+'--'+endTime+'</option>');
    $("#cc_start_time option:last").selected();
}

//根据时间段、车次类型查询所有满足的车次。
function queryByTimeAndTrainType(){
    callback($.showYxTrain, function(){
        var yxTrainTimer = setInterval(function(){
            if($("#filterTic").length>0){
                $("#filterTic").prop("checked",true);
                $("#yxtrain_close").click();
                clearInterval(yxTrainTimer);
            }
        },10);
    });
}

//勾选某些类型的车
function selectTrainType(types){
    $("#_ul_station_train_code input").each(function(i,e){
        $(e).prop("checked", false);
    });
    var typelist = types.split("/");
    for(var i=0;i<typelist.length;i++){
        $("input[value='"+typelist[i]+"']").prop("checked",true);
    }
}

//选择发站站点
function selectFrom(station){
    $("#fromStationText").trigger('keydown');
    $("#fromStationText").val(station);
    $("#fromStationText").trigger('keyup');
    $("#panel_cities div").each(function(i,s){
        var t = $(s).find("span").first();
        if(t.text()==station){
            $(s).trigger('mouseover').trigger('click');
        }
    });
}
//选择到站站点
function selectTo(station){
    $("#toStationText").trigger('keydown');
    $("#toStationText").val(station);
    $("#toStationText").trigger('keyup');
    $("#panel_cities div").each(function(i,s){
        var t = $(s).find("span").first();
        if(t.text()==station){
            $(s).trigger('mouseover').trigger('click');
        }
    });
}
//选择发站-到站站点
function selectFromTo(s1,s2){
    selectFrom(s1);
    selectTo(s2);
}

//选择乘车时间
function selectDate(date){
    $("#train_date").val(date);
}

//==========================================
//  以下则根据需求自行配置：
//      乘车人、车次、席别
//      仅显示要刷的车次、查询区间、刷新时间
//==========================================

//开启桌面通知
showNotice();

//选择北京西-石家庄的车
selectFromTo("深圳","蕲春");

//勾选车次类型：G-高铁/城际，D-动车，Z-直达，T-特快，K-快速，QT-其他
selectTrainType("K");

//选择乘车日期
selectDate('2019-01-27');

//选择乘车人
addBuyer("张松");

//手动添加车次，按优先顺序添加（轻松突破5车次限制）
addTrain("K1282");
addTrain("K824");

//添加备选日期
addDate('2019-01-26');
addDate('2019-01-25');

//添加优先席别，按优先顺序添加
addSeat("软卧");
addSeat("硬卧");
addSeat("硬坐");

//自定义时间区间
addTimes('15:00','20:00');

//自动刷新时间0.5s
autoSearchTime=500;

//收起订票助手
$(".up").click();

//启动检测防挂
var timer = setInterval(waitforRestart,autoSearchTime);

//开启防退出功能
preventLogout();

//开始刷票
if($("#query_ticket").text()=="查询"){
    //仅查看勾选的车次
    $("#filterTic").prop("checked",true);
    var timer2 = setInterval(function(){
        if($("#auto_query").prop("checked") && $("#autoSubmit").prop("checked")&& ($("#filterTic").length==0 || $("#filterTic").prop("checked"))){
            console.log("----开始刷票-----at:"+new Date().toLocaleString());
            $("#query_ticket").click();
            if($("#filterTic").length>0){
                $("#filterTic").prop("checked",true);
            }
            clearInterval(timer2);
        }
    },10);
}
