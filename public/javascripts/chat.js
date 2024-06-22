/**
 * Entfernt den Placeholder-Text aus dem Suchfeld.
 */
function removePlaceholder(){
    document.getElementById("searchBar").removeAttribute("placeholder")
}

/**
 * Zeigt den Placeholder-Text im Suchfeld an.
 */
function showPlaceholder(){
    document.getElementById("searchBar").setAttribute("placeholder", "Nach Chats suchen")
}

/**
 * Sendet eine Nachricht an den aktuellen Chat-Partner.
 *
 * @return {Promise} Ein Promise-Objekt, das den Erfolg der Nachrichtenübermittlung anzeigt.
 */
function sendMessage(){
    let pattern = /^\S.*\S$/;
    if(document.getElementById("usermsg").value === "" || !pattern.test(document.getElementById("usermsg").value)){
        return;
    }
    let userMsg = document.getElementById("usermsg").value;
    let chatPartner = document.getElementById("currentChatPartner").innerText;
    return fetch("sendMessage", {
        method: 'POST',
        body: JSON.stringify({
            message: userMsg,
            chatPartner: chatPartner}),
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(
            async result => {
                console.log(result.json())
                getChatHistory();
                document.getElementById("usermsg").value = "";
            })
}

/**
 * Holt die Chat-Historie mit dem aktuellen Chat-Partner vom Server und zeigt sie an.
 *
 * @return {Promise} Ein Promise-Objekt, das die Chat-Historie mit dem aktuellen Chat-Partner enthält.
 */
function getChatHistory(){
    let chatPartner = document.getElementById("currentChatPartner").innerText;
    return fetch("getChatHistory", {
        method: 'POST',
        body: JSON.stringify({
            chatPartner: chatPartner}),
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(
            async result => {
                try{
                    let chatHistoryString = await result.json();
                    let chatHistoryMap = new Map(Object.entries(chatHistoryString));
                    showChatHistory(chatHistoryMap);
                }catch (SyntaxError){
                }

            })
}

/**
 * Zeigt die Chatverlaufsliste im HTML-Element "chatHistoryList" an.
 *
 * @param {Map} chatHistory - Ein Map-Objekt, das den Chatverlauf enthält
 * @return {HTMLElement} - Das HTML-Element "chatHistoryList"
 */
function showChatHistory(chatHistory){
    clearChatHistory()
    let chatPartner = document.getElementById("currentChatPartner").innerText
    let chatHistoryList = document.getElementById("chatHistoryList");
    for (const [key, value] of chatHistory.entries()) {
        let massage = document.createElement("li");
        massage.innerHTML = value["message"];
        massage.setAttribute("class", "messageListElement")
        massage.setAttribute("onmousedown", "selectOption('" + value["message"] + "')");
        if(value["sender"]["username"] ===chatPartner){
            massage.setAttribute("id", "messageFriend")
        }else{
            massage.setAttribute("id", "messageMe")
        }
        chatHistoryList.appendChild(massage);
    }
    const items = chatHistoryList.getElementsByTagName("li");
    let offset = 0;
    for (let i = 0; i < items.length; i++) {
        const item = items[i];
        item.style.top = offset + "px";
        offset += 5;
        offset += item.offsetHeight;
    }
    chatHistoryList.scrollTop = chatHistoryList.scrollHeight - chatHistoryList.clientHeight;
    return chatHistoryList;
}

/**
 * Leert das HTML-Element "chatHistoryList"
 */
function clearChatHistory(){
    let chatHistoryList = document.getElementById("chatHistoryList");
    chatHistoryList.innerHTML = ""

}

/**
 * Holt eine Liste aller Chats und zeigt diese im HTML-Element "chatList" an.
 *
 * @return {Promise<HTMLElement>} - Das HTML-Element "chatList"
 */
function getChats(){
    return fetch("getAllChats", {
        method: 'POST',
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(
            async result => {
                let chatList = await result.json();
                //chatList = sortChatList()
                let chatListElement = document.getElementById("chatList");
                for (let i=0; i<chatList.length; i++) {
                    let chat = document.createElement("li");
                    chat.innerHTML = chatList[i]["friend"]["username"];
                    chat.setAttribute("class", "chatElement")
                    //chat.setAttribute("onmousedown", "selectOption('" + key + "')");
                    chatListElement.appendChild(chat);
                }
                return chatListElement;
            });
}

/**
 * Sortiert die Liste aller Chats nach dem zuletzt gesendeten Nachrichten.
 *
 * @param {Array} chats - Eine Liste von Chat-Objekten
 * @return {Array} - Die sortierte Liste von Chat-Objekten
 */
function sortChatList(chats) {
    //hier nach most recent sortieren
    return chats
}

/**
 * Navigiert zu dem Profil eines bestimmten Freundes.
 *
 * @param {string} requestedUser - Der Benutzername des Freundes, dessen Profil angezeigt werden soll
 */
function goToFriendProfile(requestedUser) {
    window.location.href = "profileFriend?username="+requestedUser;
}