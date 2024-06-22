let unlockedAchievements = []

/**
 * Fügt den Gesamtscore des Spielers zum Highscore hinzu
 *
 * @param {number} totalScore - der Gesamtscore des Spielers
 * @returns {Promise} - ein Promise-Objekt, das den aktualisierten Highscore zurückgibt
*/
function addScoreToHighscore(totalScore){
    fetch("getHighscore", {
        method: "POST",
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(async result => {
            let scoreTimerMap = await result.json()
            let oldHighscore = scoreTimerMap["highscore"];
            let timerVal = scoreTimerMap["timer"];
            updateHighscore(oldHighscore+totalScore)
        });
}

/**
 * Aktualisiert den Highscore des Spielers
 *
 * @param {number} newHighscore - der neue Highscore des Spielers
 * @returns {Promise} - ein Promise-Objekt, das den aktualisierten Highscore zurückgibt
 */
function updateHighscore(newHighscore){
    fetch("updateHighscore", {
        method: "POST",
        body: JSON.stringify({ //ist eig unnötig, da ich nichts mitgeben muss
            highscore: newHighscore
        }),
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(async result => {
            console.log(await result)
            //return await result.json()
        });
}

/**
 * Holt die Ergebnisse der beantworteten Fragen des Spielers
 *
 * @returns {Promise} - ein Promise-Objekt, das eine Map mit den beantworteten Fragen zurückgibt
 */
function getQuestionResults(){
    fetch("getQuestionResults", {
        method: "POST",
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(async result => {
            let questionTracker = await result.text()
            questionTracker = questionTracker.substring(1, questionTracker.length)
            let questionPairs = questionTracker.split(";")
            let questionsMap = new Map;
            for(let i=0; i<questionPairs.length; i++){
                let keyVal = questionPairs[i].split(",")
                questionsMap.set(keyVal[0], keyVal[1])
            }
            updateUserHasQuestion(questionsMap)
            return questionsMap; //List with questionID/answered boolean
        });
}

/**
 * Aktualisiert die Beantwortungsergebnisse des Benutzers für jede Frage, indem eine Anfrage an den Server gesendet wird.
 *
 * @param questionsMap eine Map, die die ID jeder Frage und einen Boolean-Wert für die Beantwortung enthält.
 */
function updateUserHasQuestion(questionsMap){
    let questionsObject = Object.fromEntries(questionsMap)
    fetch("updateUserHasQuestion", {
        method: "POST",
        body: JSON.stringify({
            questionsMap: questionsObject
        }),
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(async result => {
            checkAchievements();
            return result;
        });
}

/**
 * Überprüft, ob der Benutzer bestimmte Leistungsziele erreicht hat und schaltet entsprechende Achievements frei.
 */
async function checkAchievements(){
    let organs = getOrgans()
    for(let i=1; i<=3; i++){
        unlockedAchievements.push(await unlockAchievement("Novize", i, organs[i-1], 5))
    }
    for(let i=1; i<=3; i++){
        if(checkProfi(i)){
            unlockedAchievements.push(await unlockAchievement("Profi", i, organs[i-1], 20))
        }
    }
    for(let i=1; i<=3; i++){
        if(await getCorrectlyAnsweredQuestions("Experte", i, organs[i - 1])){
             unlockedAchievements.push(await unlockAchievement("Experte", i, organs[i-1], 50))
        }
    }
    for(let i=1; i<=3; i++){
        if(await getCorrectlyAnsweredQuestions("Meister", 0, organs[i - 1])){
            unlockedAchievements.push(await unlockAchievement("Meister", 0, organs[i-1], 100))
        }
    }
    if(checkFehlerfrei()){
        unlockedAchievements.push(await unlockAchievement("Fehlerfrei", 0, null, 50))
    }
    unlockedAchievements = removeNullElementsFromList(unlockedAchievements)
    await displayUnlockedAchievements()

}

/**
 * Überprüft, ob der Benutzer ein "Profi" ist, indem seine Erfolge und seine Fehlerzahl überprüft werden.
 *
 * @param level das Level, das geprüft werden soll.
 * @returns {boolean} true, wenn der Benutzer ein "Profi" ist, false, wenn nicht.
 */
function checkProfi(level){
    let failsID = "failsLevel" + level
    let correctID = "Level" + level
    let statisticsContent = document.getElementById(correctID).innerText.split(" ")
    let failCountPerLevel = document.getElementById(failsID).innerText
    let correctAnswersCount = statisticsContent[3];
    if((parseInt(failCountPerLevel) - 1) > 0 || correctAnswersCount < 5){
        return false;
    }else{
        return true;
    }
}

/**
 * Überprüft, ob der Benutzer ein "Experte" ist, indem die Anzahl der korrekt beantworteten Fragen gezählt wird.
 *
 * @param questionsCorrectlyAnswered eine Liste von IDs für die Fragen, die der Benutzer korrekt beantwortet hat.
 * @returns {boolean} true, wenn der Benutzer ein "Experte" ist, false, wenn nicht.
 */
function checkExperte(questionsCorrectlyAnswered){
    return questionsCorrectlyAnswered.length >= 10; //hier 10!
}

/**
 * Überprüft, ob der Benutzer ein "Meister" ist, indem die Anzahl der korrekt beantworteten Fragen gezählt wird.
 *
 * @param questionsCorrectlyAnswered eine Liste von IDs für die Fragen, die der Benutzer korrekt beantwortet hat.
 * @returns {boolean} true, wenn der Benutzer ein "Meister" ist, false, wenn nicht.
 */
function checkMeister(questionsCorrectlyAnswered){
    return questionsCorrectlyAnswered.length >= 30; //hier 30!
}

/**
 * Holt die Fragen, die der Benutzer in einem bestimmten Level richtig beantwortet hat,
 * und überprüft, ob der Benutzer genügend Fragen richtig beantwortet hat, um das Experte- oder
 * Meister-Abzeichen freizuschalten.
 *
 * @param {String} name Der Name des Abzeichens, das freigeschaltet werden soll.
 * @param {Number} level Der Level, für den die Fragen abgerufen werden sollen.
 * @param {String} organ Das Organ, für das die Fragen abgerufen werden sollen.
 * @returns {Promise} Eine Promise, die true zurückgibt, wenn der Benutzer genügend Fragen richtig beantwortet hat, um das Abzeichen freizuschalten, false sonst.
 */
function getCorrectlyAnsweredQuestions(name, level, organ){
    return fetch("getCorrectlyAnsweredQuestions", {
        method: "POST",
        body: JSON.stringify({ //ist eig unnötig, da ich nichts mitgeben muss
            level: level,
            organ: organ
        }),
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(async result => {
            let questions = await result.json()
            if(name==="Experte"){
                return checkExperte(questions)
            }else if(name==="Meister"){
                return checkMeister(questions)
            }
        });
}

/**
 * Schaltet ein bestimmtes Achievement frei.
 *
 * @param {String} name Der Name des Achievement, das freigeschaltet werden soll.
 * @param {Number} level Der Level, für den das Achievement freigeschaltet werden soll.
 * @param {String} organ Das Organ, für das das Achievement freigeschaltet werden soll.
 * @param {Number} score Der Score, der dem Benutzer für das Freischalten des Achievements gutgeschrieben werden soll.
 * @returns {Promise} Eine Promise, die ein Objekt mit den Daten des freigeschalteten Achievements zurückgibt, wenn das Achievement
 *                      erfolgreich freigeschaltet wurde, oder null, wenn das Achievement nicht freigeschaltet wurde.
 */
function unlockAchievement(name, level, organ, score){
    return fetch("unlockAchievement", {
        method: "POST",
        body: JSON.stringify({ //ist eig unnötig, da ich nichts mitgeben muss
            name: name,
            level: level,
            organ: organ
        }),
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(async result => {
            let unlocked = await result.json()
            if(unlocked){
                let unlockedAchievement = {
                    "name": name,
                    "level": level,
                    "organ": organ
                }
                addScoreToHighscore(score)
                return unlockedAchievement
            }
            return null
        });

}

/**
 * Überprüft, ob alle Level fehlerfrei beantwortet wurden und ob der gesamte Punktestand 15 ist.
 *
 * @returns {boolean} - True, wenn alle Level fehlerfrei beantwortet wurden und der Punktestand 15 ist, sonst False.
 */
function checkFehlerfrei(){
    let fails1 = parseInt(document.getElementById("failsLevel1").innerText)
    let fails2 = parseInt(document.getElementById("failsLevel2").innerText)
    let fails3 = parseInt(document.getElementById("failsLevel3").innerText)
    let totalFails = fails1+fails2+fails3
    let totalScore = parseInt(document.getElementById("totalScore").innerText)
    return totalFails-3 ===0 && totalScore===15
}

/**
 * Gibt die Organe für jedes Level zurück.
 *
 * @returns {string[]} - Ein Array mit den Namen der Organe für jedes Level.
 */
function getOrgans(){
    let organ1 = document.getElementById("Level1").innerText
    let innerText1 = organ1.split(" ")
    let organ2 = document.getElementById("Level2").innerText
    let innerText2 = organ2.split(" ")
    let organ3 = document.getElementById("Level3").innerText
    let innerText3 = organ3.split(" ")
    return [innerText1[2], innerText2[2], innerText3[2]]
}

/**
 * Holt das Achievement-Objekt aus der Datenbank für das angegebene Achievement.
 *
 * @param {string} name - Der Name des Achievements.
 * @param {number} level - Die Schwierigkeitsstufe des Achievements.
 * @param {string} organ - Das Organ, für das das Achievement freigeschaltet wurde.
 * @returns {Promise<Object>} - Ein Promise, das das Achievement-Objekt für das angegebene Achievement enthält.
 */
function getAchievementObject(name, level, organ){
    return fetch("getAchievementObject", {
        method: "POST",
        body: JSON.stringify({ //ist eig unnötig, da ich nichts mitgeben muss
            level: level,
            organ: organ,
            name: name
        }),
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(async result => {
            return  await result.json()
        });
}

/**
 * Entfernt alle Null-Elemente aus einer Liste.
 *
 * @param {Array} list - Die Liste, aus der die Null-Elemente entfernt werden sollen.
 * @returns {Array} - Eine neue Liste ohne Null-Elemente.
 */
function removeNullElementsFromList(list){
    let newList = []
    for(let i=0; i<list.length; i++){
        if(list.at(i)!==null){
            newList.push(list[i])
        }
    }
    return newList
}

/**
 * Zeigt alle freigeschalteten Achievements in einer Liste an.
 */
async function displayUnlockedAchievements() {
    if(unlockedAchievements.length>0){
        document.getElementById("achievements").style.display = "flex";
    }
    let achievementsList = document.getElementById("achievementsList");
    for (let i=0; i<unlockedAchievements.length; i++) {
        let achievementObj = await getAchievementObject(unlockedAchievements[i]["name"], unlockedAchievements[i]["level"], unlockedAchievements[i]["organ"])
        //let achievementObj = await getAchievementObject("Novize", 1, "Herz")
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
