/**
 * Die Funktion "goToCreateUser" sendet eine HTTP-Anfrage an den Server, um einen neuen Benutzer mit den eingegebenen Daten zu erstellen.
 *
 * @returns {void}
 */
function goToCreateUser(){
    fetch("createUser", {
        method: 'POST',
        body: JSON.stringify({
            username: document.getElementById("username").value,
            email: document.getElementById("email").value,
            password: document.getElementById("password").value,
            surname: document.getElementById("surname").value,
            first_name: document.getElementById("first_name").value
        }),
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(
            result => {
                successfulAccountCreation(result.json())
            });
}
/**
 * Die Funktion "successfulAccountCreation" verarbeitet das Ergebnis der HTTP-Anfrage und gibt eine Meldung aus, ob der Account erfolgreich erstellt wurde oder nicht.
 *
 * @param {Promise<Response>} result - Ein Promise-Objekt, das das Ergebnis der HTTP-Anfrage enthält.
 * @returns {void}
 */
function successfulAccountCreation(result){
    result.then(res => {
        if(res === 200){
            window.alert("Account erfolgreich erstellt")
            window.location.href = "index"
        }else if(res === 300){
            window.alert("Benutzername bereits vergeben")
        }else{
            window.alert("Fehler beim Erstellen Ihres Accounts")
        }
    })
}
/**
 * Die Funktion "checkPassword" überprüft, ob die eingegebenen Passwörter übereinstimmen, und deaktiviert den Submit-Button, wenn sie nicht übereinstimmen.
 *
 * @returns {void}
 */
function checkPassword(){
    let pw1 = document.getElementById("password").value
    let pw2 = document.getElementById("passwordWdh").value
    if(pw1===pw2){
        document.getElementById("password").style.borderColor = "green"
        document.getElementById("passwordWdh").style.borderColor = "green"
        document.getElementById("submitSignUp").disabled = false

    }else{
        document.getElementById("password").style.borderColor = "red"
        document.getElementById("passwordWdh").style.borderColor = "red"
        document.getElementById("submitSignUp").disabled = true
    }
}

/**
 * Die Funktion "checkUsernameLength" überprüft, ob der Benutzername mindestens drei Zeichen lang ist,
 * und deaktiviert den Submit-Button, wenn er zu kurz ist.
 *
 * @returns {void}
 */
function checkUsernameLength(){
    let username = document.getElementById("username").value;
    if(username.length <= 2){
        document.getElementById("username").style.borderColor = "red"
        document.getElementById("submitSignUp").disabled = true

    }else{
        document.getElementById("username").style.borderColor = "green"
        document.getElementById("submitSignUp").disabled = false

    }
}