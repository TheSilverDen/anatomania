/**
 * Diese Funktion füllt die Batterie für das Level des Benutzers auf der Webseite basierend auf der
 * Anzahl der bereits beantworteten Fragen.
 *
 * @param {number} level - Das Level des Benutzers (1-3).
 * @param {number} questionCount - Die Anzahl der beantworteten Fragen des Benutzers.
 */
function fillLevelBattery(level, questionCount){
    if (level === 1){
        document.getElementById("level1").style.backgroundColor="rgb(246,193,63)";
        document.getElementById("level1").style.width = ((questionCount-1)/5)*100 + "%";
        document.getElementById("levelText1").style.color="black"
    } else if (level === 2){
        document.getElementById("level1").style.backgroundColor="rgb(246,193,63)";
        document.getElementById("level1").style.width = 100 + "%";
        document.getElementById("levelText1").style.color="black"
        document.getElementById("level2").style.backgroundColor="rgb(251,176,78)";
        document.getElementById("level2").style.width = ((questionCount-1)/5)*100 + "%";

        document.getElementById("levelText2").style.color="black"
    } else if (level === 3){
        document.getElementById("level1").style.backgroundColor="rgb(246,193,63)";
        document.getElementById("level1").style.width = 100 + "%";
        document.getElementById("levelText1").style.color="black"
        document.getElementById("level2").style.backgroundColor="rgb(251,176,78)";
        document.getElementById("level2").style.width = 100 + "%";
        document.getElementById("levelText2").style.color="black"
        document.getElementById("level3").style.backgroundColor="rgb(248,138,5)";
        document.getElementById("level3").style.width = ((questionCount-1)/5)*100 + "%";
        document.getElementById("levelText3").style.color="black"
    }
}