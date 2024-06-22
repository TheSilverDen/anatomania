var users;
var friends;

/**
 * Diese Funktion ruft alle Benutzerdaten vom Server ab und gibt diese als Map-Objekt zurück.
 *
 * @return {Promise<Map>} Eine Promise, die die Benutzerdaten als Map-Objekt enthält.
 */
function getUsers(){
    return fetch("getAllUsers", {
        method: 'POST',
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(
            result => {
                return receiveUserListFromPromise(result.json())
            });
}

/**
 * Diese Funktion nimmt das Ergebnis der Serveranfrage an, extrahiert die Benutzerliste und gibt sie als Map-Objekt zurück.
 *
 * @param {Promise} result Die Serverantwort in Form eines Promises.
 * @return {Promise<Map>} Eine Promise, die die Benutzerliste als Map-Objekt enthält.
 */
function receiveUserListFromPromise(result) {
    result.then(res => {
        users = new Map(Object.entries(res))
        return users;
    })
}

/**
 * Diese Funktion ruft alle Freunde vom Server ab und gibt diese als Map-Objekt zurück.
 *
 * @return {Promise<Map>} Eine Promise, die die Freunde als Map-Objekt enthält.
 */
function getFriends(){
    return fetch("getAllFriends", {
        method: 'POST',
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(
            result => {
                return receiveFriendsListFromPromise(result.json())
            });
}

/**
 * Diese Funktion nimmt das Ergebnis der Serveranfrage an, extrahiert die Freundesliste und gibt sie als Map-Objekt zurück.
 *
 * @param {Promise} result Die Serverantwort in Form eines Promises.
 * @return {Promise<Map>} Eine Promise, die die Freundesliste als Map-Objekt enthält.
 */
function receiveFriendsListFromPromise(result) {
    result.then(res => {
        let friendsMap = new Map(Object.entries(res))
        let friendsList = document.getElementById("friendList");
        let friendsMapSorted =sortUsersMap(friendsMap)
        for (const [username, friendObj] of friendsMapSorted.entries()) {
            let friend = document.createElement("li");
            let friendsText = document.createElement("div");
            let friendsImg = document.createElement("img")
            let friendsMsg = document.createElement("button")
            let friendsMsgIcon = document.createElement("i")
            friendsText.setAttribute("class", "friendsListText")
            friendsText.innerText = username
            friendsImg.setAttribute("class", "friendsListImg")
            friendsImg.src = friendObj["profile_picture"]
            friend.setAttribute("class", "friendListElement")
            friend.setAttribute("onmouseup", "selectOption('" + username + "')");
            friend.setAttribute("id", username);
            friendsMsg.setAttribute("onclick", "goToChat('"+username+"')")
            friendsMsg.setAttribute("class", "friendsMsgButton")
            friendsMsg.setAttribute("onmouseover", "disableFriendsListElement('"+username+"')");
            friendsMsg.setAttribute("onmouseleave", "enableFriendsListElement('"+username+"')");
            friendsMsg.appendChild(friendsMsgIcon)
            friendsMsgIcon.setAttribute("data-feather", "send")
            friendsMsgIcon.setAttribute("id", "messageIcon")
            friend.appendChild(friendsImg)
            friend.appendChild(friendsText)
            friend.appendChild(friendsMsg)
            friendsList.appendChild(friend);
            feather.replace()
        }
        return friendsMap;
    })
}

/**
 * Navigiert den Benutzer zu einem Chat mit dem angegebenen Chat-Partner.
 *
 * @param {string} chatPartner - der Benutzername des Chat-Partners
 */
function goToChat(chatPartner) {
    chatPartner = chatPartner.trim()
    window.location.href = "goToChat?usernameFriend=" + chatPartner
}

/**
 * Sortiert eine Map von Benutzerinformationen alphabetisch nach Benutzernamen.
 *
 * @param {Map} map - die Map von Benutzerinformationen, die sortiert werden soll
 * @return die sortierte Map von Benutzerinformationen
 */
function sortUsersMap(map) {
    let keys = Array.from(map.keys());
    for (let i = 0; i < keys.length - 1; i++) {
        for (let j = 0; j < keys.length - i - 1; j++) {
            if (keys[j] > keys[j + 1]) {
                let temp = keys[j];
                keys[j] = keys[j + 1];
                keys[j + 1] = temp;
            }
        }
    }
    return new Map(keys.map(k => [k, map.get(k)]));
}

/**
 * Sortiert eine Liste von Benutzernamen alphabetisch.
 *
 * @param {Array} list - die Liste von Benutzernamen, die sortiert werden soll
 * @return die sortierte Liste von Benutzernamen
 */
function sortUsersList(list) {
    for (let i = 0; i < list.length - 1; i++) {
        for (let j = 0; j < list.length - i - 1; j++) {
            if (list[j] > list[j + 1]) {
                let temp = list[j];
                list[j] = list[j + 1];
                list[j + 1] = temp;
            }
        }
    }
    return list
}

/**
 * Deaktiviert das HTML-Element für den angegebenen Benutzernamen in der Freundesliste.
 *
 * @param {string} username - der Benutzername des Freundes
 */
function disableFriendsListElement(username){
    document.getElementById(username).disabled = true;
}

/**
 * Aktiviert das HTML-Element für den angegebenen Benutzernamen in der Freundesliste.
 *
 * @param {string} username - der Benutzername des Freundes
 */
function enableFriendsListElement(username){
    document.getElementById(username).disabled = false;
}

/**
 * Zeigt die Dropdown-Liste der Suchergebnisse basierend auf der eingegebenen Suchanfrage an.
 *
 * Ruft auch die Funktionen `findUsersBySearchRequest` und `sortUsersList` auf, um die
 * Suchanfrage auszuführen und die Ergebnisse zu sortieren. Erstellt dann HTML-Elemente für
 * jede Option in der Dropdown-Liste und fügt sie in die HTML-Seite ein.
 */
function showDropdown() {
    clearList()
    let searchRequest = document.getElementById("searchBar").value
    let requestedUsers = findUsersBySearchRequest(searchRequest)
    let optionsList = document.getElementById("searchOptions");
    requestedUsers = sortUsersList(requestedUsers)
    for (let i = 0; i < requestedUsers.length; i++) {
        if("@username"===requestedUsers[i]){
            continue;
        }
        let option = document.createElement("li");
        option.innerHTML = requestedUsers[i];
        option.setAttribute("class", "searchResult")
        option.setAttribute("onmousedown", "selectOption('" + requestedUsers[i] + "')");
        optionsList.appendChild(option);
    }
    document.getElementById("dropdown").style.display = "block";
    if(requestedUsers.length>0) {
        hideFriendsList();
    }else {
        showFriendsList();
    }
}

/**
 * Diese Funktion wird aufgerufen, wenn ein Benutzer auf eine der Optionen in der Dropdown-Liste klickt.
 * Die Funktion blendet die Dropdown-Liste aus und leitet den Benutzer auf das Profil des ausgewählten Benutzers weiter.
 *
 * @param requestedUser Der ausgewählte Benutzer, auf den der Benutzer weitergeleitet werden soll.
 */
function selectOption(requestedUser) {
    document.getElementById("dropdown").style.display = "none";
    window.location.href = "profileFriend?username="+requestedUser;
}

/**
 * Diese Funktion durchsucht die Liste der Benutzer und gibt eine Liste der Benutzernamen zurück, die den Suchkriterien entsprechen.
 *
 * @param request Der Suchbegriff, nach dem in der Liste der Benutzernamen gesucht wird.
 * @return Eine Liste der Benutzernamen, die den Suchkriterien entsprechen.
 */
function findUsersBySearchRequest(request){
    let requestedUsers = []
    if(request==="" || request===null){
        return requestedUsers
    }
    for (const [key, value] of users.entries()) {
        //userSubString = key.substring(0, request.length)
        if(key.includes(request)){
            requestedUsers.push(key)
        }
    }
    return requestedUsers
}

/**
 * Diese Funktion blendet die Dropdown-Liste aus und löscht alle darin enthaltenen Suchergebnisse.
 */
function hideDropdown(){
    document.getElementById("dropdown").style.display = "none";
    clearList()
}

/**
 * Diese Funktion löscht alle Suchergebnisse aus der Dropdown-Liste.
 */
function clearList() {
    let results = document.getElementsByClassName("searchResult")
    while (results.length > 0) {
        results[0].parentNode.removeChild(results[0])
    }
}

/**
 * Diese Funktion entfernt den Platzhaltertext aus der Suchleiste.
 */
function removePlaceholder(){
    document.getElementById("searchBar").removeAttribute("placeholder")
}

/**
 * Diese Funktion setzt den Platzhaltertext in der Suchleiste zurück.
 */
function showPlaceholder(){
    document.getElementById("searchBar").setAttribute("placeholder", "Nach Benutzer*innen suchen")
}

/**
 * Diese Funktion blendet die Liste der Freunde aus und versteckt den Button zum Öffnen des Chatfensters.
 */
function hideFriendsList(){
    document.getElementById("friendsListWrapper").style.display = "none";
    // document.getElementById("messagesButton").style.display = "none";
}

/**
 * Diese Funktion blendet die Liste der Freunde wieder ein und zeigt den Button zum Öffnen des Chatfensters an.
 */
function showFriendsList(){
    document.getElementById("friendsListWrapper").style.display = "inline";
    // document.getElementById("messagesButton").style.display = "inline";

}