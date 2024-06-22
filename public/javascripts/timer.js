var timerCalc;

/**
 * Startet den Countdown und aktualisiert die verbleibende Zeit alle Sekunde.
 * Wenn der Timer auf 0 fällt, wird die Funktion 'checkIfTimerZero()' aufgerufen.
 */
function startCountdown(){
    fillTimeBar();
    timerCalc = setInterval(()=>{
        timer--;
        fillTimeBar();
        checkIfTimerZero();
    }, 1000) // each 1 second
}

/**
 * Füllt die Zeitleiste entsprechend des verbleibenden Countdowns aus.
 * Die Farbe der Leiste ändert sich je nachdem, wie viel Zeit noch übrig ist.
 */
function fillTimeBar(){
    let totalTime = 300;
    let fillPercent = (timer/totalTime)*100;
    document.getElementById("timeBarFill").style.width = fillPercent + "%";
    if(fillPercent>60){
        document.getElementById("timeBarFill").style.backgroundColor = "#0080007F";
    }else if (fillPercent<=60 && fillPercent>=20){
        document.getElementById("timeBarFill").style.backgroundColor = "#FFB4007F";

    }else{
        document.getElementById("timeBarFill").style.backgroundColor = "#A400007F";
    }
}
/**
 * Prüft, ob der Timer auf 0 gesunken ist und stoppt den Countdown.
 * Zeigt das Popup-Fenster 'timeFailPopup' an, wenn der Timer abgelaufen ist.
 */
function checkIfTimerZero(){
    if(timer===0){
        clearInterval(timerCalc)
        var popup = document.getElementById("timeFailPopup");
        popup.style.display = "flex";
    }
}

/**
 * Leitet den Nutzer zur Startseite ('index') weiter.
 */
function backToIndex(){
    window.location.href = "index";
}
/**
 * Stoppt den Countdown und färbt die Zeitleiste grau.
 */
function pauseTimeBar(){
    clearInterval(timerCalc);
    document.getElementById("timeBarFill").style.backgroundColor = "#CECECE7F"
}