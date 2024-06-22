/**
 * Diese Funktion ruft die Server-API auf, um alle freigeschalteten Achievements für einen Benutzer abzurufen.
 *
 * @return Eine Promise, die ein JSON-Objekt mit den Informationen über die freigeschalteten Achievements enthält.
 */
function getUnlockedAchievementsPerUser(){
    return fetch("getAllAchievementsPerUser", {
        method: 'POST',
        body: JSON.stringify({
            user: "me"
        }),
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(
            result => {
                return getAllAchievements(result.json())
            });
}

/**
 * Holt alle Achievements und prüft, welche bereits freigeschaltet wurden.
 *
 * @param unlockedAchievements JSON-Objekt mit den bereits freigeschalteten Achievements
 * @return Promise-Objekt, welches die Liste aller Achievements enthält (freigeschaltet und nicht freigeschaltet)
 */
function getAllAchievements(unlockedAchievements){
    return fetch("getAllAchievements", {
        method: 'POST',
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(
            async result => {
                let unlocked = await unlockedAchievements;
                return getAllAchievementsLockedUnlocked(result.json(), unlocked)
            });
}
/**
 * Diese Funktion erhält zwei Promises mit den Daten aller Achievements und den vom Benutzer freigeschalteten Achievements.
 * Ausgehend davon erstellt die Funktion eine Map, die jedem Achievements den Wert true oder false zuordnet,
 * je nachdem ob sie vom Benutzer freigeschaltet wurden oder nicht.
 * Anschließend wird basierend auf dem Titel der aktuellen HTML-Seite entweder die vollständige Liste aller
 * Achievements oder die besten drei vom Benutzer angezeigt.
 *
 * @param {Promise} allAchievements - Ein Promise mit den Daten aller Achievements
 * @param {Promise} unlockedAchievements - Ein Promise mit den vom Benutzer freigeschalteten Achievements
 * @returns {Promise} - Ein Promise, das bei erfolgreicher Ausführung nichts zurückgibt.
 */
function getAllAchievementsLockedUnlocked(allAchievements, unlockedAchievements){
    return allAchievements.then(allAchievements => {
        let achievementsMap = new Map
        allAchievements = new Map(Object.entries(allAchievements))
        unlockedAchievements = new Map(Object.entries(unlockedAchievements))
        for (const [key, value] of allAchievements.entries()) {
            if(unlockedAchievements.has(key)){
                achievementsMap.set(value, true)
            }else{
                achievementsMap.set(value, false)
            }
        }
        let title = document.title;
        if(title==="Achievements"){
            displayAllAchievementListFromPromise(achievementsMap)
        }else if(title==="Profile"){
            displayBest3AchievementListFromPromise(unlockedAchievements)
        }
    })
}

/**
 * Diese Funktion nimmt eine Map aller Achievements als Parameter und sortiert sie in verschiedene Kategorien.
 * Anschließend werden die Achievements in der entsprechenden Kategorie in einer HTML-Liste dargestellt.
 *
 * @param {Map} allAchievements - Eine Map, die alle Achievements enthält.
 * @returns {Map} Eine Map, die alle Achievements enthält.
 */
function displayAllAchievementListFromPromise(allAchievements) {
    //liste splitten
    let achievementsMap = allAchievements
    let allNovize = new Map;
    let allProfi = new Map;
    let allExperte = new Map;
    let allMeister = new Map;
    let fehlerfrei = new Map;
    for (const [key, value] of achievementsMap.entries()) {
        if(key["achievement_name"]==="Novize"){
            allNovize.set(key, value)
        }else if (key["achievement_name"]==="Profi"){
            allProfi.set(key, value)
        }else if (key["achievement_name"]==="Experte"){
            allExperte.set(key, value)
        }else if (key["achievement_name"]==="Meister"){
            allMeister.set(key, value)
        }else if (key["achievement_name"]==="Fehlerfrei"){
            fehlerfrei.set(key, value)
        }
    }
    let allAchievementsInMaps = [allNovize, allProfi, allExperte, allMeister, fehlerfrei]
    let achievementNames = ["Novize", "Profi", "Experte", "Meister", "Fehlerfrei"]
    for(let i=0; i<allAchievementsInMaps.length; i++){
        let achievementsList = document.getElementById("achievementsList"+ achievementNames[i]);
        for (const [key, value] of allAchievementsInMaps[i].entries()) {
            buildAchievementsList(key, value, achievementsList, "achievements");
        }
    }

    return achievementsMap;
    //})
}

/**
 * Diese Funktion zeigt die besten 3 erreichten Achievements eines Benutzers an.
 *
 * @param {Map} unlockedAchievements - Eine Map, die die erreichten Achievements des Benutzers anzeigt.
 * @returns {Map} Eine Map mit den erreichten Achievements des Benutzers.
 */
function displayBest3AchievementListFromPromise(unlockedAchievements) {
    if(checkIfNoAchievements(unlockedAchievements)){
        return
    }
    //return result.then(res => {
    let achievementsMap = unlockedAchievements
    let achievementsList = document.getElementById("achievementsBest3List");
    let bestAchievements = get3BestAchievements(achievementsMap);
    for (const [key, value] of bestAchievements.entries()) {
        buildAchievementsList(value, true, achievementsList, "profile");
    }
    return achievementsMap;
    //})
}

/**
 * Überprüft, ob es freigeschaltete Achievements gibt.
 * Wenn keine freigeschalteten Achievements vorhanden sind, wird ein entsprechender Text angezeigt.
 *
 * @param {Map} unlockedAchievements - Eine Map mit freigeschaltete Achievements.
 * @returns {boolean} Gibt true zurück, wenn keine freigeschalteten Achievements vorhanden sind. Gibt false zurück, wenn es freigeschaltete Achievements gibt.
 */
function checkIfNoAchievements(unlockedAchievements){
    if(unlockedAchievements.size <=0){
        document.getElementById("noAchievementsText").style.display = "flex"
        return true;
    } else{
        return false;
    }
}

/**
 * Erstellt ein Listen-Element für ein freigeschaltetes oder noch nicht freigeschaltetes Achievement.
 *
 * @param {Object} key - Das Schlüsselobjekt für das Achievement, das Informationen wie den Namen, die Beschreibung und die Pfade zu den Bildern enthält.
 * @param {Boolean} value - Ein boolescher Wert, der angibt, ob das Achievement freigeschaltet wurde oder nicht.
 * @param {HTMLElement} achievementsList - Das HTML-Listenelement, in das das neue Listen-Element eingefügt werden soll.
 * @returns {void}
 */
function buildAchievementsList(key, value, achievementsList, view){
    let achievement = document.createElement("li");
    //achievement.innerHTML = key + " " + value["achievement_name"] + " " + value["unlockedImgPath"];
    achievement.setAttribute("class", "achievementsListElement")
    achievement.setAttribute("title", key["description"])
    let achievementsText = document.createElement("div")
    achievementsText.setAttribute("class", "achievementsText")
    let achievementsImage = document.createElement("img")
    achievementsImage.setAttribute("class", "achievementImage")
    if(view==="profile"){
        achievementsText.innerText = key["achievement_name"]
    }else{
        achievementsText.innerText = key["organ"]

    }
    if(value){
        achievementsImage.src = key["unlockedImgPath"]
    }else{
        achievementsImage.src = key["lockedImgPath"]

    }
    achievement.appendChild(achievementsText)
    achievement.appendChild(achievementsImage)
    achievementsList.appendChild(achievement);
}

/**
 * Gibt die drei besten Leistungen einer gegebenen Map von Achievements zurück.
 *
 * @param {Map} achievementsMap - Map von Achievements, aus denen die drei besten ausgewählt werden.
 * @returns {Map} - Eine Map mit den drei besten Achievements.
 */
function get3BestAchievements(achievementsMap){
    let best3Achievements = new Map;
    for (const [keyAll, valueAll] of achievementsMap.entries()) {
        if(best3Achievements.size < 3){
            best3Achievements.set(keyAll, valueAll);
            continue;
        }
        let points = valueAll["points"];
        let min = Number.MAX_SAFE_INTEGER;
        let minKey = 0;
        for (const [keyBest, valueBest] of best3Achievements.entries()) {
            if(valueBest["points"]<min){
                min = valueBest["points"];
                minKey = keyBest;
            }
        }
        if(points > min){
            best3Achievements.delete(minKey);
            best3Achievements.set(keyAll, valueAll);
        }
    }
    return best3Achievements;
}