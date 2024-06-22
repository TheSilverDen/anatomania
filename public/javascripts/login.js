/**
 * Geht zur Authentifizierungsseite und validiert die Benutzerdaten.
 *
 * @returns {void}
 */
function goToAuthenticate(){
    fetch("validateLogin", {
        method: 'POST',
        body: JSON.stringify({
            username: document.getElementById("username").value,
            password: document.getElementById("password").value
        }),
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(result => {
            successfullyLoggedIn(result.json())
        });
}
/**
 * Verarbeitet das Ergebnis der Authentifizierung und leitet den Benutzer auf die Indexseite weiter.
 *
 * @param {Promise} result - Das Ergebnis der Authentifizierung
 * @returns {void}
 */
function successfullyLoggedIn(result){
    result.then(res => {
        if(res === 200){
            window.location.href = "index"
        }else{
            window.alert("Benutzername oder Passwort falsch eingegeben. Bitte versuchen Sie es erneut.")
        }
    })
}