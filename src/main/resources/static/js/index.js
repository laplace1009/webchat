class Message {
    constructor(name, message, changedName, userList = []) {
        this.name = name
        this.message = message
        this.changedName = changedName
        this.userList = userList
    }
}

class Client {
    constructor(addr, name, userList = []) {
        this.name = name
        this.webSocket = new WebSocket(addr)
        this.chatBasis = document.getElementById("messages")
        this.userBasis = document.getElementById("user-list").querySelector('ul')
        this.webSocket.onmessage = (event) => {
            const msg = JSON.parse(event.data)
            if (msg.changedName) {
                this.name = msg.changedName
            } else if (msg.userList.length) {
                this.userBasis.innerHTML = msg.userList.reduce((ul, name) => ul.concat(`<li>${name}</li>`), ``)
            } else {
                this.chatBasis.insertAdjacentHTML('beforeend', `<span>${msg.name}: ${msg.message}</span>`)
            }
        }
        this.webSocket.onclose = (e) => {
            if (e.wasClean) alert(`Clean closed connection ${e.code}, ${e.reason}`)
            else alert('Connection was forcibly terminated.')
        }
        this.webSocket.onerror = (error) => console.error(`WebSocket Error: ${error}`)
    }
}


const ws = new Client("ws://125.130.106.78:8080/ws", '')
const buttonElem = document.querySelector('button');
const textElem = document.getElementById('message-input')

function sendMessage() {
    if (ws.webSocket.readyState === WebSocket.OPEN) {
        const text = textElem.value.trim()
        const changeCmd = '/n '
        if (text.startsWith(changeCmd)) {
            const changedName = text.slice(changeCmd.length)
            const chatMsg = new Message(ws.name, '', changedName)
            ws.webSocket.send(JSON.stringify(chatMsg))
        } else {
            const chatMsg = new Message(ws.name, text, '')
            ws.webSocket.send(JSON.stringify(chatMsg))
        }
        textElem.value = ''
    } else if (ws.webSocket.readyState === WebSocket.CLOSED) {
        console.log("WebSocket connection is closed.");
    }
}
buttonElem.addEventListener('click', (e) => {
    e.preventDefault();
    sendMessage();
});

textElem.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
        e.preventDefault();
        sendMessage();
    }
})
