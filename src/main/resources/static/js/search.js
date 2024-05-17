let webSocketRootUrl = "ws://localhost:8989/ws/search"
let webSocket = null;

$(function () {
    console.log("Remote Log Viewer Designed and Implementer By Hackyle Shawe. Open Source: https://github.com/HackyleShawe/RemoteLogViewer")
});

//点击了页面上的Search按钮
$("#search").click(function () {
    $("#status").text("Started...");
    //当前是那个日志目标
    let targetCode = $('#targetCode').text();
    //输入的关键字
    let keywords = $('#keywords').val();
    keywords =keywords.replaceAll(" ", "-"); //替换空格为-

    obtainLogBySocket(targetCode, keywords);
});

//点击了页面上的停止按钮
$("#stop").click(closeWebSocket());

//点击了页面上的清除按钮
$("#clean").click(function () {
    closeWebSocket();

    let msgBody = $("#msgBody");
    msgBody.empty();
    msgBody.html("<p id=\"msgHead\">Message Body</p>");
});

function closeWebSocket() {
    //关闭WebSocket连接
    if(webSocket !== null && webSocket.readyState === WebSocket.OPEN) {
        let sessionId = window.sessionStorage.getItem("sessionId")
        if(sessionId === null || sessionId === '') {
            return
        }

        //发送Ajax请求，告诉Server端我要关闭了，你也关闭吧
        $.get("/log/stop?sid="+sessionId, function (data) {
            console.log("The '/log/stop' Response Close Status: ", data)
        });

        //如果直接在Client端直接关闭，在Server端会抛异常（Caused by: java.io.IOException: 你的主机中的软件中止了一个已建立的连接。）
        webSocket.close()
        console.log("WebSocketClient Connection was Sent close Command.");

        $("#status").text("Stopped.");
    }
}

/**
 * 发起WebSocket请求，获取数据
 */
function obtainLogBySocket(targetCode, keywords) {
    webSocket = new WebSocket(webSocketRootUrl + "?targetCode=" +targetCode+ "&keywords=" + keywords)
    webSocket.onopen = function(evt) { //连接成功后的回调函数
        console.log("WebSocketClient Connection Opened.");
        // webSocket.send("Hello, I am Client."); //发送
    };

    webSocket.onmessage = function(evt) { //接收到消息的回调函数
        // console.log( "接收Server端发来的消息: " + evt.data); //接收
        if(evt.data.startsWith("sessionId:")) {
            //接收后端发来的本个连接Id
            let sidArr =  evt.data.split(":")
            let sid = sidArr[1]
            //存储本地，sessionStorage只在本个页面有效
            window.sessionStorage.setItem("sessionId", sid)
            // window.sessionStorage.getItem("sessionId")

        } else {
            writeLog2DOM(evt.data);
        }
    };

    webSocket.onclose = function(evt) { //连接断开的回调函数
        console.log("WebSocketClient Connection closed.");
    };
}

/**
 * 写日志信息到DOM中
 */
function writeLog2DOM(data) {
    $("#msgHead").append("<p>" +data+ "</p>");
}

/**
 * 监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常
 */
window.onbeforeunload = function () {
    if(webSocket != null && webSocket.readyState===1) { //WebSocket.OPEN为1
        let sessionId = window.sessionStorage.getItem("sessionId")
        if(sessionId === null || sessionId === '') {
            return
        }
        //发送Ajax请求，告诉Server端我要关闭了，你也关闭吧
        $.get("/log/stop?sid="+sessionId, function (data) {
            console.log("The '/log/stop' Response Close Status: ", data)
        });

        webSocket.close();
    }
}

/**
 * 复制某个HTML元素中的值
 * @param element 元素
 */
function copyText(element) {
    let data = element.val()
    if('' !== data) {
        element.select();
        document.execCommand('copy');
    }
}

/**
 * 单击输入框，复制其中的内容
 * 双击输入框，清除里面的内容
 */
let keywords = $("#keywords");
keywords.click(function () {
    copyText(keywords);
})
keywords.dblclick(function () {
    keywords.val("")
})

let searchText01 = $("#searchText01");
searchText01.click(function () {
    copyText(searchText01);
})
searchText01.dblclick(function () {
    searchText01.val("")
})

let searchText02 = $("#searchText02");
searchText02.click(function () {
    copyText(searchText02);
})
searchText02.dblclick(function () {
    searchText02.val("")
})


/**
 * 页面搜索
 */
$("#searchBtn01").click(function () {
    toFind(searchText01.val())
})
$("#searchBtn02").click(function () {
    toFind(searchText02.val())
})
function toFind(keyword) {
    if(keyword === null || keyword === '' || keyword === undefined) {
        return
    }
    window.find(keyword, false, false, true)
}

/**
 * 当有实时数据过来时，将页面始终放在最下面
 */
$("#msgBody").bind('DOMNodeInserted', function(e) {
    // console.log("onload")
    window.scrollTo(0, document.body.scrollHeight)
});

/**
 * 回到顶部
 */
function topFunction() {
    document.body.scrollTop = 0;
    document.documentElement.scrollTop = 0;
}
