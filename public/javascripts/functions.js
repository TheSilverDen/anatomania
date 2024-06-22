/**
 * Die Funktion dateTime() aktualisiert das Element mit der
 * ID "demo" mit dem aktuellen Datum und Uhrzeit.
 */
function dateTime() {
  document.getElementById('demo').innerHTML = Date()
}
/**
 * Die Funktion login() wird aufgerufen,
 * wenn der Benutzer versucht, sich anzumelden.
 * Sie erfasst die Benutzername- und Passwort-Eingaben des Benutzers
 * und prüft, ob diese den Admin-Benutzerdaten entsprechen.
 * Wenn ja, wird der Benutzer zur Hauptseite weitergeleitet.
 */
function login(){
  let user = document.getElementById("username").value;
  let pw = document.getElementById("pw").value;
  if(user==="admin" && pw==="admin"){
    toMain();
  }
}

/**
 * Die Funktion toMain() leitet den Benutzer zur Hauptseite weiter.
 */
function toMain(){
  window.location.href = "main";
}

/**
 * Die Funktion edit() erfasst die Eingaben des Benutzers für Name,
 * Benutzername und Passwort und speichert sie als JSON-Objekt in
 * einer Datei mit dem Namen "thing.json".
 */
function edit(){
  let name = document.getElementById("name").value;
  let user = document.getElementById("userName").value;
  let pw = document.getElementById("password").value;

  let profileData = '{ "Name":name , "User Name":user, "Password": pw}';
  let jsonObject = JSON.stringify(profileData);
  var fs = require('fs');
  fs.writeFile("thing.json", jsonObject);
}

/**
 * Die Funktion goTo() wird aufgerufen, wenn der Benutzer auf einen Button
 * klickt, der den Wert "data-name" hat. Abhängig vom Wert dieses Attributs
 * wird der Benutzer zur entsprechenden Seite weitergeleitet.
 */
function goTo(){
  let page = document.getElementById("messagesButton").getAttribute("data-name");
  switch (page){
    case "Main": window.location.href = "main"; break;
    case "ProfileView": window.location.href = "profileView"; break;
    case "ProfileEdit": window.location.href = "profileEdit"; break;
    case "Friends": window.location.href = "friends"; break;
    case "Chat": window.location.href = "chat"; break;
    case "Achievements": window.location.href = "achievements"; break;
    case "Highscores": window.location.href = "highscores"; break;

  }
}

/**
 * Die Funktion goBack() leitet den Benutzer zur vorherigen Seite weiter.
 */
function goBack(){
    window.location.href = document.referrer;
}

/**
 * Die Funktion goBackToFriendsList() leitet den Benutzer zur Freundesliste-Seite zurück.
 */
function goBackToFriendsList(){
  window.location.href = "friends";
}

/**
 * Die Funktion logOutConfirmation() zeigt eine Bestätigungsnachricht an,
 * die den Benutzer fragt ober er sich wirklich abmelden will. Wenn der Benutzer die Bestätigung
 * akzeptiert, wird er zur Abmelde-Seite weitergeleitet.
 */
function logOutConfirmation() {
  var r = window.confirm("Bist du sicher, dass du dich ausloggen möchtest?");
  if (r === true) {
    window.location.href = "logout"
  }
}
function openUserManual(){
  var popup = document.getElementById("infoPopup");
  popup.style.display = "flex";
}

/**
 * Versteckt das Pop-up-Fenster für das User Manual.
 */
function hideUserManual(){
  var popup = document.getElementById("infoPopup");
  popup.style.display = "none";
}
