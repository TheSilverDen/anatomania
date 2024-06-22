let questionID;
let jokerType;
let answers

/**
 * Die Funktion `getJoker` ruft eine Jokerfunktion auf, basierend auf dem geklickten Button.
 * Wenn der Joker kaufbar ist, wird die Frage-ID aktualisiert, die Antworten werden in einer Map gespeichert und der Highscore wird aktualisiert.
 * Andernfalls wird das Popup "Lacking Coins" angezeigt.
 *
 * @param jokerButton Der geklickte Button, der den Typ des Jokers angibt.
 * @param questionIDParam Die ID der Frage, die der Spieler beantworten möchte.
 */
function getJoker(jokerButton, questionIDParam){
    jokerType = jokerButton.id;
    if(checkIfPurchasable(jokerType)){
        questionID = questionIDParam
        answers = new Map(Object.entries(answerMap))
        updateHighscore()
    }else{
        showLackingCoinsPopup();
    }
}

/**
 * Die Funktion `updateHighscore` aktualisiert den aktuellen Highscore
 * des Spielers und zieht den Preis des gekauften Jokers ab.
 */
function updateHighscore(){
    let price;
    let currentScore = document.getElementById("currentScore").innerText
    if(jokerType==="joker50"){
        price = 20;
    }else if(jokerType==="jokerAudience"){
        price = 10;
    }else{
        price = 15;
    }
    fetch("updateHighscore", {
        method: 'POST',
        body: JSON.stringify({ //ist eig unnötig, da ich nichts mitgeben muss
            highscore: currentScore-price
        }),
        headers: {
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(async result => {
            let succeeded = await result.text()
            hideShopPopup();
            //await showPurchaseSucceededPopup()
            showPurchaseSucceededPopup();
        });
}

/**
 * Die Funktion `showPurchaseSucceededPopup` zeigt das Popup "Purchase Succeeded" an, das den erfolgreichen Kauf des Jokers bestätigt.
 */
function showPurchaseSucceededPopup(){
    var popup = document.getElementById("purchaseSucceeded");
    popup.style.display = "flex";
    //return fade(popup);
}

/**
 * Die Funktion `hidePurchaseSucceededPopup` blendet das Popup "Purchase Succeeded" aus und deaktiviert den gekauften Joker-Button.
 * Je nach gekauftem Jokertyp wird die entsprechende Jokerfunktion aufgerufen.
 */
function hidePurchaseSucceededPopup(){
    var popup = document.getElementById("purchaseSucceeded");
    popup.style.display = "none";
    disableJokerButton();
    if(jokerType==="joker50"){
        apply50Joker(answers);
    }else if(jokerType==="jokerAudience"){
        applyAudienceJoker();
    }else{
        applySkipJoker();
    }
}

/**
 * Die Funktion `showLackingCoinsPopup` zeigt das Popup "Lacking Coins" an, wenn der Spieler nicht genug Münzen hat, um einen Joker zu kaufen.
 */
function showLackingCoinsPopup(){
    var popup = document.getElementById("lackingCoinsPopup");
    popup.style.display = "flex";
    fade(popup);
}

/**
 * Die Funktion `fade` ändert die Opazität eines Elements, bis es vollständig ausgeblendet ist.
 *
 * @param element Das auszublendende Element.
 * @return Eine Promise, die aufgelöst wird, wenn das Element vollständig ausgeblendet ist.
 */
function fade(element){
    return new Promise(function(resolve) {
        var op = 1;  // initial opacity
        var timer = setInterval(function () {
            if (op <= 0.6){
                clearInterval(timer);
                element.style.display = 'none';
                resolve();
            }
            element.style.opacity = op;
            element.style.filter = 'alpha(opacity=' + op * 100 + ")";
            op -= op * 0.005;

        }, 20);
    });
}

/**
 * Die Funktion `checkIfPurchasable` prüft, ob der geklickte Joker vom Spieler gekauft werden kann.
 *
 * @return `true`, wenn der Joker kaufbar ist, `false` sonst.
 */
function checkIfPurchasable(){
    let currentScore = document.getElementById("currentScore").innerText;
    if(jokerType==="joker50"){
        return currentScore >= 20;
    }else if(jokerType==="jokerAudience"){
        return currentScore>=10;
    }else{
        return currentScore>=15;
    }
}

/**
 * Diese Funktion ruft die Methode "getSessionData" mit einer POST-Anfrage auf und erhält
 * eine Antwort als JSON-Objekt zurück. Die Antwort wird in eine Map umgewandelt. Anschließend wird
 * der Wert des Schlüssels "skipJokerCount" aus der Map extrahiert und um eins erhöht. Falls dieser
 * Wert kleiner als 3 ist, wird die Methode "updateJokerCounter" mit einer POST-Anfrage aufgerufen
 * und der erhöhte Zählerwert als JSON-Objekt im Body mitgesendet. Nachdem der Server die Anfrage
 * bearbeitet hat, wird die Methode "loadNewQuestion" aufgerufen.
 */
function applySkipJoker() {
    fetch("getSessionData", {
        method: 'POST',
        body: JSON.stringify({ //ist eig unnötig, da ich nichts mitgeben muss
            data: "i want questions"
        }),
        headers: {
            'Content-Type': 'application/json'
        },
    })
        .then(
            async result => {
                let res = await result.json()
                let dataMap = new Map(Object.entries(res));
                let counter = parseInt(dataMap.get("skipJokerCount"));
                if (counter < 3) {
                    counter += 1;

                    fetch("updateJokerCounter", {
                        method: 'POST',
                        body: JSON.stringify({ //ist eig unnötig, da ich nichts mitgeben muss
                            counter: counter
                        }),
                        headers: {
                            'X-Requested-With': 'XMLHttpRequest',
                            'Accept': 'text/javascript, text/html, application/xml, text/xml',
                            'Content-Type': 'application/json'
                        },
                    })
                        .then(async result => {
                            let counter = await result.text()
                            answeredQuestions.push(questionID)
                            loadNewQuestion();
                        });
                }
            })
}

/**
 * Diese Funktion ruft die Methode "updateJokerCounter" mit einer POST-Anfrage auf
 * und übergibt den Wert "counter" als JSON-Objekt im Body. Nachdem der Server die
 * Anfrage bearbeitet hat, wird die Methode "receiveQuestionsAfterLevelUp" aufgerufen
 * und danach die Methode "loadNewQuestion".
 *
 * @param {number} counter - Der Zählerwert, der an den Server übermittelt werden soll.
 */
function updateCounter(counter){
    fetch("updateJokerCounter", {
        method: 'POST',
        body: JSON.stringify({ //ist eig unnötig, da ich nichts mitgeben muss
            counter: counter
        }),
        headers: {
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(async result => await receiveQuestionsAfterLevelUp(result.json()));
    loadNewQuestion();

}

/**
 * Diese Funktion zeigt ein Popup-Fenster an und ruft die Methode "showAudienceJokerPopup" auf.
 */
function applyAudienceJoker(){
    //zeit anhalten!
    showAudienceJokerPopup()

}

/**
 * Diese Funktion erhält ein Map-Objekt "answers" als Eingabe und entfernt
 * zwei falsche Antworten aus der Antwortliste. Eine zufällige richtige Antwort
 * wird beibehalten. Die deaktivierten Antworten werden durch Änderung des Stils ausgeblendet.
 *
 * @param {Map} answers - Die Antwortliste, die bearbeitet werden soll.
 */
function apply50Joker(answers){
    let falseAnswers = []
    for(const [key, value] of answers.entries()) {
        if (!value) {
            falseAnswers.push(key)
        }
    }
    let randomAnswerToKeep = randomize(2)
    falseAnswers = removeAnswerFromArray(falseAnswers, randomAnswerToKeep)
    //falseAnswers = falseAnswers.splice(randomAnswerToKeep)
    for(let i=0; i<2; i++){
        if(document.getElementById("answer1").innerText === falseAnswers[i]){
            document.getElementById("answer1").disabled = true;
            document.getElementById("answer1").style.color = "rgba(236,236,236,0)"
            document.getElementById("answer1").style.borderStyle = "hidden"
        } else if(document.getElementById("answer2").innerText === falseAnswers[i]){
            document.getElementById("answer2").disabled = true;
            document.getElementById("answer2").style.color = "rgba(236,236,236,0.2)"
            document.getElementById("answer2").style.borderStyle = "hidden"


        } else if(document.getElementById("answer3").innerText === falseAnswers[i]){
            document.getElementById("answer3").disabled = true;
            document.getElementById("answer3").style.color = "rgba(236,236,236,0)"
            document.getElementById("answer3").style.borderStyle = "hidden"


        } else {
            document.getElementById("answer4").disabled = true;
            document.getElementById("answer4").style.color = "rgba(236,236,236,0)"
            document.getElementById("answer4").style.borderStyle = "hidden"



        }
    }

}

/**
 * Entfernt ein Element aus einem Array.
 *
 * @param {Array} array - Das Array, aus dem ein Element entfernt werden soll.
 * @param {number} index - Der Index des Elements, das entfernt werden soll.
 * @returns {Array} Ein neues Array mit dem Element entfernt.
 */
function removeAnswerFromArray(array, index){
    let newArray = []
    for(let i=0; i<array.length; i++){
        if(i!==index){
            newArray.push(array[i])
        }
    }
    return newArray
}

/**
 * Zeigt das Publikums-Joker-Popup an.
 */
function showAudienceJokerPopup() {
    var popup = document.getElementById("audiencePopup");
    popup.style.display = "flex";
    getRandomPreset()
    //startProgress(getRandomPreset());
}


/**
 * Ruft eine Zufallsfrage vom Server ab und gibt ein zufälliges Antwortmuster zurück, als Publikums Joker
 */
function getRandomPreset(){
    let correctAnswer;
    let preset;
    fetch("getCorrectAnswerByQuestion", {
        method: 'POST',
        body: JSON.stringify({
            question_id: questionID
        }),
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(async result => {
            correctAnswer = await result.text();
            let correctAnswerLetter;
            if(document.getElementById("answer1").innerText===correctAnswer){
                correctAnswerLetter = "A";
            }else if(document.getElementById("answer2").innerText===correctAnswer){
                correctAnswerLetter = "B";
            }else if(document.getElementById("answer3").innerText===correctAnswer){
                correctAnswerLetter = "C";
            }else{
                correctAnswerLetter = "D";
            }
            let randomNum = randomize(2);
            if(randomNum===0){
                preset = getPreset(correctAnswerLetter, 70, 11, 9, 10)
            }else if (randomNum===1){
                preset = getPreset(correctAnswerLetter, 40, 20, 22, 18)
            }else{
                preset = getPreset(correctAnswerLetter, 20, 30, 35, 15)
            }
            startProgress(preset)
        });
}
/**
 * Gibt ein Antwortmuster zurück, das auf der Position der richtigen Antwort basiert.
 *
 * @param {string} correctAnswerLetter - Der Buchstabe der richtigen Antwort (A, B, C oder D).
 * @param {number} a - Der Prozentsatz der Publikumsstimmen für Antwort A.
 * @param {number} b - Der Prozentsatz der Publikumsstimmen für Antwort B.
 * @param {number} c - Der Prozentsatz der Publikumsstimmen für Antwort C.
 * @param {number} d - Der Prozentsatz der Publikumsstimmen für Antwort D.
 * @returns {Array} Ein Antwortmuster-Array, das auf der Position der richtigen Antwort basiert.
 */
function getPreset(correctAnswerLetter, a, b, c, d){
    // a = 70, b = 11, c = 9, d = 10
    let preset = []
    if(correctAnswerLetter==="A"){
        preset = [a, b, c, d]
    }else if (correctAnswerLetter==="B"){
        preset = [b, a, c, d]
    }else if (correctAnswerLetter==="C"){
        preset = [b, c, a, d]
    }else{
        preset = [b, c, d, a]
    }
    return preset;
}

/**
 * Versteckt das Pop-up-Fenster für den Publikumsjoker.
 */
function hideAudienceJokerPopup() {
    var popup = document.getElementById("audiencePopup");
    popup.style.display = "none";
    stopProgress();
}

/**
 * Startet die Fortschrittsanzeige, die eine Balkenanzeige ist.
 *
 * @param {Array} preset - Ein Array, das die Fortschrittsprozente enthält, die jeder Balken darstellt.
 */
function startProgress(preset) {
    var bars = document.getElementsByClassName("bar");
    //var progress = [10, 30, 50, 100];
    var progress = preset;
    var i = 0;
    var intervalId = setInterval(function() {
        if (i >= progress.length) {
            clearInterval(intervalId);
            return;
        }
        bars[i].style.width = progress[i] + "%";
        i++;
    }, 500);
}

/**
 * Stoppt die Fortschrittsanzeige und setzt alle Balken auf 0%.
 */
function stopProgress() {
    var bars = document.getElementsByClassName("bar");
    for (var i = 0; i < bars.length; i++) {
        bars[i].style.width = "0%";
    }
}

/**
 * Öffnet das Pop-up-Fenster für den Shop.
 */
function openShopPopup(){
    fetch("getSessionData", {
        method: 'POST',
        body: JSON.stringify({ //ist eig unnötig, da ich nichts mitgeben muss
            data: "i want questions"
        }),
        headers: {
            'Content-Type': 'application/json'
        },
    })
        .then(
            async result => {
                let res = await result.json()
                var popup = document.getElementById("shopPopup");
                popup.style.display = "flex";
                let dataMap = new Map(Object.entries(res));
                let counter = parseInt(dataMap.get("skipJokerCount"));
                if (counter >= 3){
                    document.getElementById("jokerSkip").style.backgroundColor = "gray";
                    document.getElementById("jokerSkip").disabled = true;
                }
            });


}

/**
 * Versteckt das Pop-up-Fenster für den Shop.
 */
function hideShopPopup(){
    var popup = document.getElementById("shopPopup");
    popup.style.display = "none";
}

/**
 * Deaktiviert den Joker-Button im Shop.
 */
function disableJokerButton(){
    document.getElementById("jokerShopButton").style.background = "rgb(203,236,247)"
    document.getElementById("jokerShopButton").disabled = true;
}