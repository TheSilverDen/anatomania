/**
 * Löscht den Benutzeraccount
 *
 * @returns {Promise<Response>} Ein Promise, das eine Antwort des Servers zurückgibt
 */
function deleteAccount(){
    fetch("deleteAccount", {
        method: 'POST',
        headers:{
            'X-Requested-With': 'XMLHttpRequest',
            'Accept': 'text/javascript, text/html, application/xml, text/xml',
            'Content-Type': 'application/json'
        },
    })
        .then(result => {
            window.alert("Account erfolgreich gelöscht");
            window.location.href = "login"
            result.json();
        });
}

/**
 * Zeigt ein Popup an, das den Benutzer nach der Bestätigung der Accountlöschung fragt.
 * Wenn der Benutzer die Accountlöschung bestätigt, wird der Account gelöscht.
 */
function showDeletionConfirmPopup(){
    let deletionChoice = window.confirm("Willst Du Deinen Account wirklich löschen?")
    if(deletionChoice){
        deleteAccount();
    }
}