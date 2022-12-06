let webSocketRootUrl = "ws://localhost:8989/log/ws"
let webSocket = null;

$(function () {
    console.log("Remote Log Viewer Designed and Implementer By Hackyle Shawe. Open Source: https://github.com/HackyleShawe/RemoteLogViewer")
});

//点击了页面上的开始按钮
$("#start").click(function () {
    $("#status").text("Started...");
    let count = $('input:radio[name=count]:checked').val();
    obtainLogBySocket(count);
});

//点击了页面上的停止按钮
$("#stop").click(function () {
    //关闭WebSocket连接
    if(webSocket !== null && webSocket.readyState === WebSocket.OPEN) {
        let sessionId = window.sessionStorage.getItem("sessionId")
        if(sessionId === null || sessionId === '') {
            return
        }

        //发送Ajax请求，告诉Server端我要关闭了，你也关闭吧
        $.get("/log/stopWebSocket?sid="+sessionId, function (data) {
            console.log("The '/log/stopWebSocket' Response Close Status: ", data)
        });

        //如果直接在Client端直接关闭，在Server端会抛异常（Caused by: java.io.IOException: 你的主机中的软件中止了一个已建立的连接。）
        webSocket.close()
        console.log("WebSocketClient Connection was Sent close Command.");

        // while(WebSocket.CLOSED === webSocket.readyState) { //关闭是一个过程，需要耗费时间
        //     console.log("WebSocketClient Connection has been Closed.");
        // }
        // if(WebSocket.CLOSED === webSocket.readyState) {
        //     console.log("WebSocketClient Connection has been Closed.");
        // } else {
        //     console.log("WebSocketClient Connection didn't Closed. State: " + webSocket.readyState);
        // }
        $("#status").text("Stopped.");
    }
});

//点击了页面上的清除按钮
$("#clean").click(function () {
    let msgBody = $("#msgBody");
    msgBody.empty();
    msgBody.html("<p id=\"msgHead\">Message Body</p>");
});


/**
 * 发起WebSocket请求，获取数据
 */
function obtainLogBySocket(count) {
    count = count < 1 ? 1 : count;
    webSocket = new WebSocket(webSocketRootUrl + "?count=" + count)
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
        $.get("/log/stopWebSocket?sid="+sessionId, function (data) {
            console.log("The '/log/stopWebSocket' Response Close Status: ", data)
        });

        webSocket.close();
    }
}

/**
 * 复制内容到剪贴板
 * Notice：需要导入clipboard.min.js
 * @param content 要复制的内容
 */
function copyHandle(content){
    let copy = (e)=>{
        e.preventDefault()
        e.clipboardData.setData('text/plain',content)
        // alert('复制成功')
        document.removeEventListener('copy',copy)
    }
    document.addEventListener('copy',copy)
    document.execCommand("Copy");
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

let searchText03 = $("#searchText03");
searchText03.click(function () {
    copyText(searchText03);
})
searchText03.dblclick(function () {
    searchText03.val("")
})

let searchText04 = $("#searchText04");
searchText04.click(function () {
    copyText(searchText04);
})
searchText04.dblclick(function () {
    searchText04.val("")
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
$("#searchBtn03").click(function () {
    toFind(searchText03.val())
})
$("#searchBtn04").click(function () {
    toFind(searchText04.val())
})
function toFind(keyword) {
    if(keyword === null || keyword === '' || keyword === undefined) {
        return
    }

    //模拟调用浏览器的Ctrl+F查找功能
    //https://developer.mozilla.org/zh-CN/docs/Web/API/Window/find
    // window.find(aString, aCaseSensitive, aBackwards, aWrapAround,
    //     aWholeWord, aSearchInFrames, aShowDialog);
    // aString：将要搜索的字符串
    // aCaseSensitive：布尔值，如果为true,表示搜索是区分大小写的。
    // aBackwards：布尔值。如果为true, 表示搜索方向为向上搜索。
    // aWrapAround：布尔值。如果为true, 表示为循环搜索。
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