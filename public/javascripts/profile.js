/**
 * Gibt die Anzahl aller Fragen zurück, die in der Datenbank gespeichert sind.
 *
 * @returns {Promise} - Eine Promise, die die Gesamtzahl der Fragen als Text enthält.
 */
function getTotalQuestionCountProfile(){
    return fetch("getTotalQuestionCount", {
        method: 'POST',
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(result => {
            return result.text();
        });
}

/**
 * Gibt die Anzahl der Fragen pro Tag für den Nutzer zurück, die in der Datenbank gespeichert sind.
 *
 * @returns {Promise} - Eine Promise, die die Anzahl der Fragen pro Tag als JSON-Objekt enthält.
 */
function getQuestionCountPerTagProfile(){
    return fetch("getQuestionCountPerTag", {
        method: 'POST',
        body: JSON.stringify({ //ist eig unnötig, da ich nichts mitgeben muss
            view: "Profile"
        }),
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(result =>{
            return result.json();
        });
}


var savebutton = document.getElementById('editButton');
var readonly = true;
var inputs = document.querySelectorAll('input[type="text"]');

/**
 * Event-Listener für den Klick auf den "Editieren"-Button in der Profilansicht.
 * Navigiert zur Profilbearbeitungsseite.
 */
document.addEventListener('DOMContentLoaded', function() {
    const savebutton = document.querySelector('#save-button');
    if (savebutton) {
        savebutton.addEventListener('click', function() {
            window.location.href = "profileEdit";
        });
    }
});

/**
 * Lädt eine Bilddatei aus dem Dateisystem und zeigt sie in einem HTML-Element an.
 *
 * @param {Event} event - Das Event-Objekt, das die Datei enthält.
 */
var loadFile = function (event) {
    var image =
    document.getElementById("output");
    image.src =
    URL.createObjectURL(event.target.files[0]);
};

