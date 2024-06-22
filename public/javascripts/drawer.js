/**
 * Diese Funktion prüft, ob die aktuelle Seite aktiv ist und setzt das entsprechende Menüelement auf "active".
 * @return {void}
 */
function isActive(){
  let title = document.getElementsByTagName("title")[0].innerHTML;
  if(title==="Main"){
    document.getElementById("liMain").className = "active";
  }else if(title==="Profile"){
    document.getElementById("liProfile").className = "active";
  }else if(title==="Friends" || title ==="Profile Friend"){
    document.getElementById("liFriends").className = "active";
  }else if(title==="Highscores"){
    document.getElementById("liHighscores").className = "active";
  } else if (title === "Achievements") {
    document.getElementById("liAchievements").className = "active";
  }else if (title === "Settings") {
    document.getElementById("liSettings").className = "active";
  }else if (title === "Logout") {
    document.getElementById("liLogout").className = "active";
  }
}