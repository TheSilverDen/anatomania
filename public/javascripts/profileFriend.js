/**
 * Fügt den aktuellen Benutzer als Freund hinzu, schaut vorher ob sie bereits freunde sind.
 *
 * @returns {Promise<Response>} Eine Promise, die die Antwort auf die Anfrage zurückgibt.
 */
function friendAdded(){
    return fetch("editFriendship", {
        method: 'POST',
        body: JSON.stringify({
            username: document.getElementById("username").innerText
        }),
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(
            result => {
                checkAlreadyFriends()
                return result
            });

}
/**
 * Zeigt eine Bestätigungsmeldung an, bevor der Benutzer
 * als Freund entfernt wird. Wenn der aktuelle Button "Freund*in entfernen"
 * ist und der Benutzer auf OK klickt, wird der Freund entfernt.
 */
function removeFriendConfirmation(){
    let currentButton = document.getElementById("addFriend").innerText
    let usernameFriend = document.getElementById("username").innerText;
    if(currentButton === "Freund*in entfernen"){
        if(window.confirm("Willst du " + usernameFriend + " wirklich als Freund*in entfernen?")){
            friendAdded()
        }
    } else {
        friendAdded()
    }
}

/**
 * Überprüft, ob der aktuelle Benutzer bereits ein Freund ist und ändert den Text des Buttons entsprechend.
 *
 * @returns {Promise<boolean>} Eine Promise, die einen booleschen Wert zurückgibt,
 * der angibt, ob der Benutzer bereits ein Freund ist oder nicht.
 */
function checkAlreadyFriends(){
    return fetch("checkFriendship", {
        method: 'POST',
        body: JSON.stringify({
            username: document.getElementById("username").innerText
        }),
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(
            async result => {
                let added =  await result.json()
                if(added){
                    document.getElementById("addFriend").innerText = "Freund*in entfernen"
                }else{
                    document.getElementById("addFriend").innerText = "Freund*in hinzufügen"
                }
                return added
            });
}

/**
 * Ändert die Hintergrundfarbe des "addFriend"-Buttons, wenn der Benutzer mit der Maus darüber fährt.
 */
function hoverColor(){
    if (document.getElementById("addFriend").innerText === "Freund*in hinzufügen"){
        document.getElementById("addFriend").style.backgroundColor = "lightblue"
    } else {
        document.getElementById("addFriend").style.backgroundColor = "#de9999"
    }
}

/**
 * Setzt die Hintergrundfarbe des "addFriend"-Buttons auf seine Standardfarbe zurück.
 */
function regularColor(){
    document.getElementById("addFriend").style.backgroundColor = "#8A8B8E33"
}

/**
 * Leitet den Benutzer auf die Chat-Seite mit dem ausgewählten Freund weiter.
 */
function goToChat() {
    let chatPartner = document.getElementById("username").innerText
    chatPartner = chatPartner.trim()
    window.location.href = "goToChat?usernameFriend=" + chatPartner
}

/**
 * Ruft alle freigeschalteten Achievements des ausgewählten Freundes ab und zeigt sie an.
 *
 * @returns {Promise<void>} Eine Promise, die nichts zurückgibt.
 */
function getUnlockedAchievementsOfFriend(){
    let user = document.getElementById("username").innerText;
    return fetch("getAllAchievementsPerUser", {
        method: 'POST',
        body: JSON.stringify({ //ist eig unnötig, da ich nichts mitgeben muss
            user: user
        }),
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(
            async result => {
                return await displayUnlockedAchievements(result.json())
            });
}

/**
 * Zeigt die freigeschalteten Achievements des ausgewählten Freundes an.
 *
 * @param {Promise<Object>} unlockedAchievements - Eine Promise, die ein Objekt mit den freigeschalteten Achievements zurückgibt.
 * @returns {Promise<void>} Eine Promise, die nichts zurückgibt.
 */
async function displayUnlockedAchievements(unlockedAchievements) {
    unlockedAchievements = new Map(Object.entries(await unlockedAchievements))
    if(checkIfNoAchievements(unlockedAchievements)){
        return;
    }
    let achievementsList = document.getElementById("achievementsList");
    for (const [key, value] of unlockedAchievements.entries()) {
        let achievementObj = value;
        let achievement = document.createElement("li");
        achievement.setAttribute("class", "achievementsListElement")
        achievement.setAttribute("title", achievementObj["description"])
        let achievementsText = document.createElement("div")
        let achievementsImage = document.createElement("img")
        achievementsImage.setAttribute("class", "unlockedIcon")
        achievementsText.innerText = achievementObj["achievement_name"]
        achievementsText.setAttribute("class", "achievementsText")
        achievementsImage.src = achievementObj["unlockedImgPath"]
        achievementsImage.setAttribute("class", "achievementsImage")
        achievement.appendChild(achievementsText)
        achievement.appendChild(achievementsImage)
        achievementsList.appendChild(achievement);
    }
}

/**
 * Überprüft, ob der ausgewählte Freund keine Achievements hat und zeigt in diesem Fall eine entsprechende Meldung an.
 *
 * @param {Promise<Object>} unlockedAchievements - Eine Promise, die ein Objekt mit den freigeschalteten Achievements zurückgibt.
 * @returns {boolean} Ein boolescher Wert, der angibt, ob der Freund keine Achievements hat oder nicht.
 */
function checkIfNoAchievements(unlockedAchievements){
    if(unlockedAchievements.size <=0){
        document.getElementById("noAchievementsText").style.display = "flex"

        return true;
    } else{
        return false;
    }
}