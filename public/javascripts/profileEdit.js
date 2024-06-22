
    var savebutton = document.getElementById('saveButton');
    var readonly = true;
    var inputs = document.querySelectorAll('input[type="text"]');
    document.addEventListener('DOMContentLoaded', function() {
        const savebutton = document.querySelector('#save-button');
        if (savebutton) {
            savebutton.addEventListener('click', function() {
                window.location.href = "profileEdit";
            });
        }
    });
    /**
     * Lädt das ausgewählte Bild in ein HTML-Image-Element.
     *
     * @param {Event} event - Das Event, das den Aufruf der Funktion ausgelöst hat.
     */
    var loadFile = function (event) {
    var image =
    document.getElementById("output");
    image.src =
    URL.createObjectURL(event.target.files[0]);
};
    /**
     * Aktualisiert die Nutzerdaten des angemeldeten Benutzers.
     */
    function updateUserData(){
    fetch("updateUserData", {
    method: 'POST',
    body: JSON.stringify({
    username: document.getElementById("username").value,
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
     * Zeigt eine Erfolgsmeldung an, wenn das Aktualisieren des Nutzerkontos erfolgreich war.
     *
     * @param {Promise} result - Die Promise-Instanz, die den HTTP-Antworttext enthält.
     */
    function successfulAccountCreation(result){
    result.then(res => {
        if(res === 200){
            window.alert("Account erfolgreich aktualisiert ")
            window.location.href = "profileView"
        }else if(res === 300){
            window.alert("Benutzername bereits vergeben ")
        }else{
            window.alert("Fehler beim Aktualisieren Ihres Accounts")
        }
    })
}
    /**
     * Überprüft, ob die eingegebenen Passwörter übereinstimmen.
     */
    function checkPassword(self){
    let pw1 = document.getElementById("password").value
    let pw2 = document.getElementById("passwordWdh").value
    if(pw1===pw2){
        document.getElementById("password").style.borderColor = "green"
        document.getElementById("passwordWdh").style.borderColor = "green"
        document.getElementById("saveButton").disabled = false
        checkIfEmpty(self);
    }else{
        document.getElementById("password").style.borderColor = "red"
        document.getElementById("passwordWdh").style.borderColor = "red"
        document.getElementById("saveButton").disabled = true
    }
}
    /**
     * Überprüft, ob alle benötigten Felder ausgefüllt sind.
     * Ändert den Text und das Verhalten des Buttons "Speichern" auf der Profilbearbeitungsseite.
     *
     */
    function checkIfEmpty(self){
        let text = self.value
        if(text===null || text==="" || (self.id==="username" && text.length<3)){
            self.style.borderColor = "red";
        }else{
            self.style.borderColor = "black";
        }
        let first = document.getElementById("first_name").value
        let sur = document.getElementById("surname").value
        let username = document.getElementById("username").value
        let pw = document.getElementById("password").value
        let pw2 = document.getElementById("passwordWdh").value
        if(first===null || first==="" || sur===null || sur==="" || username===null || username==="" || pw===null || pw==="" || pw2===null || pw2===""|| username.length<3){
            document.getElementById("saveButton").disabled = true
        }else{
            document.getElementById("saveButton").disabled = false
        }
    }
