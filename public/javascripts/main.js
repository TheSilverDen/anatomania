let availableQuestions;
let availableQuestionsByTag;
let currentQuestion = {};
let questionCount;
let questionIndex = 0;
let answerSubmitted = false;
let answerMap;
let level;
let organ;
let correctAnswerCount;
let totalScore;
let failCounter = [];
let answeredQuestions;
let currentQuestionId;
let oldOrgan;
let questionTracker;
let joker;
let tagTracker;
let timer;

/**
 * Startet das Spiel, ruft die Methode getSessionData()
 * auf und setzt das Level in der Session Storage.
 */
function startGame(){
    getSessionData();
    sessionStorage.setItem("level", level);

}

/**
 * Holt Session Daten über den Server,
 * ruft initializeFields(), startCountdown()
 * (bei Aufruf auf der Game Seite) und getQuestions() auf.
 */
function getSessionData() {
    fetch("getSessionData", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
    })
        .then(
            async result => {
                let res = await result.json()
                let dataMap = new Map(Object.entries(res));
                initializeFields(dataMap);
                if (document.title === "Game"){
                    startCountdown();
                }
                getQuestions();
            });
}

/**
 * Initialisiert Felder mit den Session-Daten, die als Map übergeben werden.
 *
 * @param {Map} dataMap - Map mit den Session-Daten.
 */
function initializeFields(dataMap) {
    level = parseInt(dataMap.get("level"));
    totalScore = parseInt(dataMap.get("totalScore"));
    questionCount = parseInt(dataMap.get("questionCount"));
    correctAnswerCount = parseInt(dataMap.get("correctAnswerCount"));
    organ = dataMap.get("organ");
    failCounterString = dataMap.get("failCounter");
    failCounter = failCounterString.split(",")
    username = dataMap.get("username");
    currentQuestionId = dataMap.get("question_ID")
    answeredQuestions = getAnsweredQuestions(dataMap);
    questionTracker = dataMap.get("questionTracker")
    tagTracker = dataMap.get("tagTracker")
    joker = dataMap.get("joker")
    timer = dataMap.get("timer")
}

/**
 * Liefert eine Liste mit den bereits beantworteten Fragen.
 *
 * @param {Map} dataMap - Map mit den Session-Daten.
 * @returns {Array} - Liste der bereits beantworteten Fragen.
 */
function getAnsweredQuestions(dataMap) {
    let answeredQuestionsString = dataMap.get("answeredQuestions")
    if (answeredQuestionsString === "") {
        return []
    } else {
        return answeredQuestionsString.split(",")
    }
}

/**
 * Holt Fragen vom Server, ruft receiveQuestions() auf.
 */
function getQuestions(){
    fetch("requestQuestions", {
        method: 'POST',
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
        .then(async result => await receiveQuestions(result.json()));
}

/**
 * Verarbeitet die erhaltenen Fragen, setzt die globale Variable availableQuestions.
 *
 * @param {Promise} questions - Promise, das die Fragen enthält.
 */
function receiveQuestions(questions){
    questions.then(async (res) => {
        availableQuestions = res;
        if (currentQuestionId === null || currentQuestionId === undefined) {
            await getNewQuestion();
            fetch("getInitialQuestion", {
                method: 'POST',
                body: JSON.stringify({ //ist eig unnötig, da ich nichts mitgeben muss
                    questionID: currentQuestionId,
                    organ: organ,
                }),
                headers: {
                    'X-Requested-With': 'XMLHttpRequest',
                    'Accept': 'text/javascript, text/html, application/xml, text/xml',
                    'Content-Type': 'application/json'
                },
            })
                .then(async result => {
                    let ok = result.ok;
                });

        }else{
            for (let i = 0; i < availableQuestions.length; i++) {
                if (availableQuestions[i]["question_ID"] == currentQuestionId) {
                    currentQuestion = availableQuestions[i]
                    currentQuestionId = currentQuestion["question_ID"]
                    answerMap = currentQuestion["answers"];
                    break;
                }
            }
        }



    });
    if(joker==="true"){
        document.getElementById("joker").disabled = true;
        document.getElementById("joker").style.background = "rgba(162,161,161,0.7)"
    }

    //setzen des Question Obejekts auf Question mit ID, die aus Session geholt wurde

}

/**
 * Diese Funktion prüft, ob in der Liste der
 * verfügbaren Fragen unbeantwortete Fragen vorhanden sind,
 * und entfernt gegebenenfalls beantwortete Fragen.
 */
function checkIfListHasUnansweredQuestions(){
    let cleanAvailableQuestionsByTag = []
    for(let i=0; i<availableQuestionsByTag.length; i++){
        let questionID = availableQuestionsByTag[i]["question_ID"]
        if(!answeredQuestions.includes(questionID.toString())){
            cleanAvailableQuestionsByTag.push(availableQuestionsByTag[i])
        }
    }
    availableQuestionsByTag = cleanAvailableQuestionsByTag;
}

/**
 * Diese Funktion lädt eine neue Frage, indem eine zufällige Frage aus der Liste
 * der verfügbaren Fragen ausgewählt wird.
 */
async function loadNewQuestion() {
    //check if question already used
    await getNewQuestion()
    reloadUpdateSessionData("loadQuestion")
}

/**
 * Prüft, ob eine neue Frage verwendet wird.
 *
 * @returns {Promise<void>}
*/
async function getNewQuestion(){
    while (true) {
        availableQuestionsByTag = await getAllQuestionsByTag(organ, level, availableQuestions)
        checkIfListHasUnansweredQuestions()
        if (availableQuestionsByTag.length > 0) {
            questionIndex = randomize(availableQuestionsByTag.length - 1);
            currentQuestion = availableQuestionsByTag[questionIndex];
            currentQuestionId = currentQuestion["question_ID"];
            answerMap = currentQuestion["answers"];
            break;
        }
    }
}

/**
 * Diese Funktion generiert eine zufällige Zahl innerhalb eines bestimmten Bereichs.
 *
 * @param {number} index - die obere Grenze des Bereichs
 * @returns {number} - die generierte zufällige Zahl
 */
function randomize(index){
    return Math.floor(Math.random()*(index+1));
}

/**
 * Diese Funktion gibt ein zufälliges Organ zurück.
 *
 * @returns {string} - das zufällig gewählte Organ
 */
function randomizeOrgan(){
    let possibleOrgans = ["Herz", "Gehirn", "Magen", "Lunge", "Leber"];
    return possibleOrgans[randomize(4)];
}

/**
 * Diese Funktion wird aufgerufen, wenn eine Antwort eingereicht wird, und prüft, ob die Antwort richtig oder falsch ist.
 *
 * @param {HTMLElement} self - der Button, der angeklickt wurde, um die Antwort einzureichen
 */
function answerSubmitEvent(self){
    if(!answerSubmitted){
        let submittedAnswerData = self.innerText;
        try{
            if(answerMap===undefined || answerMap ===null){
                answerMap = currentQuestion["answers"];

            }
            questionTracker = questionTracker + ";" + currentQuestionId.toString() + "," + answerMap[submittedAnswerData]
        }catch (TypeError){
            return; //klick before all question data loaded: no answer submit possible
        }
        disableJokerButton()
        pauseTimeBar();
        document.getElementById("answer1").style.pointerEvents = "none"
        document.getElementById("answer2").style.pointerEvents = "none"
        document.getElementById("answer3").style.pointerEvents = "none"
        document.getElementById("answer4").style.pointerEvents = "none"
        answerSubmitted = true;
        document.getElementById("buttonElements").style.visibility = "visible"

        if(answerMap[submittedAnswerData]){
            self.style.background='#B1FF9A';
            correctAnswerCount++;
            totalScore++;
        }else{
            self.style.background='#FF9E81';
            answerMap = new Map(Object.entries(answerMap))
            let correctAnswer
            for(const [key, value] of answerMap.entries()){
                if(value){
                    correctAnswer = key
                }
            }
            for(let i=1; i<=4; i++){
                let id = "answer" + i
                let answer = document.getElementById(id)
                if(answer.innerText === correctAnswer){
                    answer.style.background = '#B1FF9A';
                }
            }
        }
    }
}

/**
 * Aktualisiert den Tag-Tracker mit der ID des übergebenen Tag-Buttons.
 *
 * @param {string} tagButtonID - Die ID des Tag-Buttons, der ausgewählt wurde.
 * "smile" für ein lächelndes Gesicht,
 * "meh" für ein neutralen Gesicht und
 * "frown" für ein trauriges Gesicht.
 */
function updateTagTracker(tagButtonID){
    if(tagButtonID==="smile"){
        tagTracker = tagTracker + ",1"
    }else if(tagButtonID==="meh"){
        tagTracker = tagTracker + ",2"
    }else if(tagButtonID==="frown"){
        tagTracker = tagTracker + ",3"
    }
}

/**
 * Deaktiviert den übergebenen Button-Typ und aktualisiert den Tag-Tracker mit der ID des Buttons.
 * Sendet eine POST-Anfrage an "setQuestionTag" mit der ID der aktuellen Frage und dem Tag.
 * Wenn eine Antwort eingereicht wurde und weniger als 4 Fragen beantwortet wurden,
 * wird eine neue Frage geladen. Andernfalls wird nichts ausgeführt.
 *
 * @param {HTMLElement} buttonType - Der Button-Typ, der deaktiviert werden soll.
 */
function weiterAction(buttonType){
    buttonType.disabled = true;
    updateTagTracker(buttonType.id)
    let tag = buttonType.id;
    fetch("setQuestionTag", {
        method: 'POST',
        body: JSON.stringify({
            tag: tag,
            questionID: currentQuestionId
        }),
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(result => {
            let tagIsSet = result.text();
            if(answerSubmitted && questionCount<4){
                if(!answeredQuestions.includes(currentQuestionId.toString())) {
                    answeredQuestions.push(currentQuestionId.toString())
                }
                questionCount++;
                if (checkLevelUp()){
                    return;
                }
                loadNewQuestion();
            }else{
                //document.getElementById("quizWeiter").style.background='#FF9E81';
            }

        });
}

/**
 * Überprüft, ob der Benutzer genügend Fragen in der aktuellen Runde richtig beantwortet hat, um aufzusteigen.
 * Wenn der Benutzer 4 Fragen in einer Runde beantwortet hat, wird geprüft,
 * ob der Benutzer genügend Fragen in der aktuellen Runde richtig beantwortet hat.
 * Wenn ja, wird der Schwierigkeitsgrad erhöht und die Session-Daten aktualisiert. Wenn nein, wird die Session-Daten aktualisiert.
 *
 * @returns {boolean} true, wenn der Benutzer aufgestiegen ist, ansonsten false.
 */
function checkLevelUp(){

    if(questionCount===4) {
        answeredQuestions = []

        if (level===0) {
            if (correctAnswerCount >= 3) {
                oldOrgan = organ
                organ = randomizeOrgan()
                level++;
                //getQuestionsAfterLevelUp()
                reloadUpdateSessionData("success");
                return true;
            } else {
                failCounter[0] = parseInt(failCounter[0])+1;
                //getQuestionsAfterLevelUp()
                reloadUpdateSessionData("failure");
                return true;
            }
        } else if (level === 1) {
            if (correctAnswerCount >= 4) {
                oldOrgan = organ
                organ = randomizeOrgan()
                level++;
                //getQuestionsAfterLevelUp()
                reloadUpdateSessionData("success");
                return true;
            } else {
                failCounter[1] = parseInt(failCounter[1])+1;
               // getQuestionsAfterLevelUp()
                reloadUpdateSessionData("failure");
                return true;

            }
        } else  if(level===2) {
                if (correctAnswerCount >= 5) {
                    oldOrgan = organ
                    organ = randomizeOrgan()
                    level++;
                    // getQuestionsAfterLevelUp()
                    reloadUpdateSessionData("success");
                    return true;
                } else {
                    failCounter[2] = parseInt(failCounter[2])+1;
                    // getQuestionsAfterLevelUp()
                    reloadUpdateSessionData("failure");
                    return true;
                }
        }
    }
    return false;
}

/**
 * Stoppt das Spiel und gibt eine Bestätigungsnachricht aus.
 *
 * @returns {void}
 */
function stopRound(){
    if(window.confirm("Willst du das Spiel wirklich beenden?")){
        window.location.href = "index"
    }
}

/**
 * Lädt die Spielinformationen neu und aktualisiert die aktuelle URL.
 *
 * @returns {void}
 */
function reloadUpdateSessionData(url){
    //answeredQuestions = []
    window.location.href = url+"?level="+level+"&totalScore="+totalScore+"&questionCount="+questionCount+"&correctAnswerCount="
        +correctAnswerCount+"&organ="+organ+"&failCounter="+failCounter+"&question_ID="+currentQuestionId+"&oldOrgan="
        +oldOrgan+"&answeredQuestions="+answeredQuestions+"&questionTracker="+questionTracker+"&tagTracker="+tagTracker + "&timer=" + timer
    ;
}

/**
 * Holt sich die nächsten Fragen nachdem das Level erhöht wurde.
 *
 * @returns {void}
 */
function getQuestionsAfterLevelUp(){
    fetch("requestQuestions", {
        method: 'POST',
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
        .then(async result => await receiveQuestionsAfterLevelUp(result.json()));
}

/**
 * Wird aufgerufen, wenn neue Fragen nach Level-Up empfangen wurden.
 *
 * @param {array} questions - Die neuen Fragen.
 * @returns {void}
 */
function receiveQuestionsAfterLevelUp(questions){
    if(level===3){
        reloadUpdateSessionData("complete")
    }

    questions.then(async (res)=>{
        availableQuestions = await res;
        while (true){
            availableQuestionsByTag = await getAllQuestionsByTag(organ, level, availableQuestions)
            checkIfListHasUnansweredQuestions()
            if(availableQuestionsByTag.length>0){
                let questionIndex = randomize(availableQuestionsByTag.length-1)
                currentQuestion = availableQuestionsByTag[questionIndex]
                currentQuestionId = currentQuestion["question_ID"]
                answeredQuestions.push(currentQuestionId)
                reloadUpdateSessionData("game")
                break;
            }
        }
    });
}

/**
 * Holt sich die nächsten Fragen nachdem eine Runde gefailt wurde.
 *
 * @returns {void}
 */
function getQuestionsAfterFail(){
    fetch("requestQuestions", {
        method: 'POST',
        body: JSON.stringify({
            level: level,
            organ: organ
        }),
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(async result => await receiveQuestionsAfterFail(result.json()));
}

/**
 * Diese Funktion empfängt Fragen nach einem Fehlversuch und führt
 * verschiedene Aktionen aus, je nachdem auf welcher Level-Stufe man sich befindet.
 *
 * @param {Promise} questions - Ein Promise, das die Fragen enthält, die nach einem Fehlversuch empfangen wurden.
 */
function receiveQuestionsAfterFail(questions){
    if(level===3){
        reloadUpdateSessionData("complete")
    }
    questions.then(async (res)=>{

        availableQuestions = await res;
        while (true){
            availableQuestionsByTag = await getAllQuestionsByTag(organ, level, availableQuestions)
            checkIfListHasUnansweredQuestions()
            if(availableQuestionsByTag.length>0){
                await getNewQuestion()
                reloadUpdateSessionData("game")
                break;
            }
        }
    });
}

/**
 * Diese Funktion konvertiert ein Map-Objekt in ein JSON-Objekt.
 *
 * @param {Map} map - Das Map-Objekt, das konvertiert werden soll.
 * @returns {string} - Das JSON-Objekt, das aus der Map konvertiert wurde.
 */
function mapToJson(map){
    return JSON.stringify(Object.fromEntries(map))
}

/**
 * Diese Funktion lädt ein Bild des Organs, das als Parameter übergeben wird, und zeigt es auf der Seite an.
 *
 * @param {string} organ - Der Name des Organs, dessen Bild geladen werden soll.
 */
function loadImage(organ){
    var organImg = '';
    if (organ === 'Leber') {
        organImg = '/assets/images/organImg/Leber.png';
    } else if (organ === 'Herz') {
        organImg = '/assets/images/organImg/Herz.png';
    } else if (organ === 'Gehirn') {
        organImg = '/assets/images/organImg/Gehirn.png';
    } else if (organ === 'Lunge') {
        organImg = '/assets/images/organImg/Lunge.png';
    } else if (organ === 'Magen') {
        organImg = '/assets/images/organImg/Magen.png';
    }

    document.getElementById('organ-img').src = organImg;
}

/**
 * Diese Funktion berechnet die optimale Schriftgröße für die Antwortbuttons,
 * abhängig von der Breite des Buttons und der Länge des Antworttextes.
 * @param {void}
 */
function calcAnswerFontSize(){
    const buttons = document.querySelectorAll('.answer');
    for(let i=0; i<4; i++){
        const maxChars = parseInt(window.getComputedStyle(buttons[i]).width) / 3.8;
        const textLength = buttons[i].textContent.trim().length;
        const fontSize = Math.min(24, Math.max(13, (maxChars / textLength) * 13));
        buttons[i].style.fontSize = fontSize + 'px';
    }
}
/**
 * Diese Funktion ruft für jeden Antwort-Option-Button die Funktion auf, die zu lange
 * Wörter herausfiltert und die Font-Size bei Fragen mit langen Wörtern verringert
 * @returns {void}
 */
function checkAnswerButtonSize(){
    for(let i=1; i<5;i++){
        let answerButton = "answer"+i;
        checkIfAnswerWordTooLong(document.getElementById(answerButton));
    }
}
/**
 * Diese Funktion analysiert den Antwort-Text eines Antwort-Buttons.
 * Sie prüft, ob Wörter mit einer Länge >20 enthalten sind und verringert die Font-Size
 * bei diesen Fragen auf 18px.
 * @param {HTMLElement} answerButton - Der Name des Organs, dessen Bild geladen werden soll.
 * @returns {void}
 */
function checkIfAnswerWordTooLong(answerButton){
    let answerWords = answerButton.innerText.split(" ");
    for(let i=0; i<answerWords.length; i++){
        if(answerWords[i].length > 20){
            answerButton.style.fontSize = "16px";
            if(answerWords.length>2){
                calcAnswerFontSize();
            }
        }else if(answerWords[i].length > 17) {
            answerButton.style.fontSize = "18px";
            if(answerWords.length>2){
                calcAnswerFontSize();
            }

        }else if(answerWords[i].length > 15) {
            answerButton.style.fontSize = "20px";
            if(answerWords.length>2){
                calcAnswerFontSize();
            }

        }
    }
}