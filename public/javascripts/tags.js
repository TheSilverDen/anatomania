let smileQuestions;
let mehQuestions;
let frownQuestions;

/**
 * Funktion zur zufälligen Auswahl der Tag - Fragen häufigkeit.
 *
 * @returns {number} Eine zufällig ausgewählte Zahl zwischen 1 und 3, die einen der möglichen Tags repräsentiert.
 */
function randomizeTag(){
    let random = [1,1,1,1,1,2,2,2,2,2,2,2,2,2,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3];
    let randomIndex = randomize(29);
    return  random[randomIndex];
}
/**
 * Funktion zum Abrufen von Fragen anhand eines bestimmten Tags.
 *
 * @param {string} organ - Das Organ, zu dem die Fragen gehören sollen.
 * @param {number} level - Das Level, für das die Fragen abgerufen werden sollen.
 * @param {Array} availableQuestions - Eine Liste von Fragen, die für das Spiel zur Verfügung stehen.
 **/
function getAllQuestionsByTag(organ, level, availableQuestions){
    let tagID = randomizeTag();
    let actualAvailableQuestions = [];
    //fetch questions by tag
    //befülle acutalAvailableQ
    return fetch("getQuestionsByTag", {
        method: 'POST',
        body: JSON.stringify({ //ist eig unnötig, da ich nichts mitgeben muss
            level: level,
            organ: organ,
        }),
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(async result => {
            let questionMap = await result.json()
            smileQuestions = questionMap[1]
            mehQuestions = questionMap[2]
            frownQuestions = questionMap[3]
            let noTagQuestions = []
            let allTaggedQuestions = smileQuestions.concat(mehQuestions, frownQuestions)
            if(tagID===1){
                actualAvailableQuestions = smileQuestions;
            }else if(tagID===2){
                for(let i=0; i<availableQuestions.length; i++){
                    if(!allTaggedQuestions.includes(availableQuestions[i])){
                        noTagQuestions.push(availableQuestions[i])
                    }
                }
                actualAvailableQuestions = mehQuestions.concat(noTagQuestions)
            }else{
                actualAvailableQuestions = frownQuestions;
            }
            return actualAvailableQuestions;
        });
}

/**
 * Funktion zur Initialisierung der Fragen für das Spiel.
 */
async function getInitialQuestion(){
    answeredQuestions = []
    organ = randomizeOrgan();
    level = 0;
    await getQuestions();
}
/**
 * Funktion zur Füllung der Tag-Leiste.
 */
async function fillTagBar(){
    let totalQuestionCount = await getTotalQuestionCount();
    let questionCountPerTag = await getQuestionCountPerTag();
    let smileWidth = questionCountPerTag[1] / totalQuestionCount;
    let mehWidth = questionCountPerTag[2] / totalQuestionCount;
    let frownWidth = questionCountPerTag[3] / totalQuestionCount;
    let noWidth = 1-smileWidth-mehWidth-frownWidth
    document.getElementById("tagBarSmile").style.width = (smileWidth*100) + "%";
    document.getElementById("tagBarMeh").style.width = (mehWidth*100) + "%";
    document.getElementById("tagBarFrown").style.width = (frownWidth*100) + "%";
    document.getElementById("tagBarNo").style.width = (noWidth*100) + "%";

}

/**
 * Funktion zum Abrufen der Gesamtzahl der Fragen.
 *
 * @returns {number} Die Gesamtzahl der verfügbaren Fragen.
 */
async function getTotalQuestionCount(){
    let title = document.title;
    if(title==="Profile"){
        return await getTotalQuestionCountProfile()
    }else{
        return 1
    }
}

/**
 * Funktion zum Abrufen der Anzahl von Fragen für jeden verfügbaren Tag.
 *
 * @returns {Object} Ein Objekt mit der Anzahl von Fragen für jeden verfügbaren Tag.
 */
async function getQuestionCountPerTag(){
    let title = document.title;
    if(title==="Profile"){
        return await getQuestionCountPerTagProfile();
    }else{
        return await getQuestionCountPerTagGame();
    }
}
/**
 * Funktion zum Abrufen der Anzahl von Fragen für jeden verfügbaren Tag für das Spiel.
 *
 * @returns {Promise<Object>} Ein Promise-Objekt, das ein Objekt mit der Anzahl von Fragen für jeden verfügbaren Tag zurückgibt.
 */
function getQuestionCountPerTagGame(){
    return fetch("getQuestionCountPerTag", {
        method: 'POST',
        body: JSON.stringify({ //ist eig unnötig, da ich nichts mitgeben muss
            view: "Game"
        }),
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(async result =>{
            return await result.json();
        });}