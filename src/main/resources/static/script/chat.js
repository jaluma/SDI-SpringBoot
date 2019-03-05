function connectWebSocket() {
    var socket = new SockJS('/chatWS');
    var stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        stompClient.subscribe('/chats/messages', function (response) {
            var data = JSON.parse(response.body);
            draw("left", data.message);
        });
    });
}

function draw(side, text) {
    console.log("drawing...");
    var $message;
    $message = $($('.message_template').clone().html());
    $message.addClass(side).find('.text').html(text);
    $('.messages').append($message);
    return setTimeout(function () {
        return $message.addClass('appeared');
    }, 0);

}

connectWebSocket();