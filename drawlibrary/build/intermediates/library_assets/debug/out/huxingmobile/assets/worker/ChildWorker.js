/**
 * 子线程
 * @author xuwenyi
 */

var VERSION = 1;

importScripts('ByteArray.js?v='+VERSION, 'ParserItem3D.js?v='+VERSION);


 // 监听事件
onmessage = function (e) 
{
    var message = e.data;
    var method = message["method"];
    var sequence = message["sequence"];
    var data = message["data"];

    switch(method)
    {
        // 解析3D文件
        case "parse_hljm":
            parse_hljm(method, sequence, data);
            break;
    }
}




/**
 * 解析hljm文件
 */
function parse_hljm(method, sequence, data)
{
    var bytearray = new ByteArray(data);
    var parser = new ParserItem3D(bytearray);

    sendMessage(method, sequence, parser);
}




/**
 * 发送消息给主线程
 * @param {*} method 
 * @param {*} message 
 */
function sendMessage(method, sequence, data)
{
    var message = {};
    message["method"] = method;
    message["sequence"] = sequence;
    message["data"] = data;

    postMessage(message);
}