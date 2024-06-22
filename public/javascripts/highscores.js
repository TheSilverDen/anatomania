/**
 * Füllt die Anzeige der Bestenliste aus den zurückgegebenen Benutzerdaten.
 * Ruft die Methode getList() auf, um die Benutzerliste abzurufen.
 */
function fillLeaderboard(){
  getList(); //hier muss ein promise resolved werden
}

/**
 * Sendet einen fetch Request an den Server, um eine Liste
 * von Benutzern und ihren Highscores zu erhalten.
 *
 * @returns {Promise} Ein Promise, das die Liste von Benutzern und ihren Highscores enthält.
 */
function getList(){
  return fetch("getAllUsers", {
    method: 'POST',
    headers:{
      'X-Requested-With': 'XMLHttpRequest',
      'Accept': 'text/javascript, text/html, application/xml, text/xml',
      'Content-Type': 'application/json'
    },
  })
      .then(
          result => {
            return receiveUserListFromPromise(result.json())
          });
}

/**
 * Nimmt das Ergebnis von getList() entgegen und sortiert
 * die Benutzerliste absteigend nach ihrem Highscore.
 * Ruft die Methoden fillPodium() und fillTable() auf,
 * um die Anzeige der Bestenliste zu füllen.
 *
 * @param {Promise} result - Ein Promise, das die Liste von Benutzern und ihren Highscores enthält.
 * @returns {Map} Eine sortierte Map von Benutzern und ihren Highscores.
 */
function receiveUserListFromPromise(result) {
  result.then(res => {
    let dataMap = new Map(Object.entries(res))
    dataMap = sortList(dataMap);
    fillPodium(dataMap);
    fillTable(dataMap);
    return dataMap;
  })
}

/**
 * Füllt die Anzeige des Podiums in der Bestenliste aus den Benutzerdaten der ersten
 * drei Benutzer in der sortierten Benutzerliste.
 *
 * @param {Map} data - Eine sortierte Map von Benutzern und ihren Highscores.
 */
function fillPodium(data){
  let name = null;
  let score = null;
  let count = 0;
  let imgFirst;
  let imgSecond;
  let imgThird;
  for (const [key, value] of data.entries()) {
    if(count===0){
      name = "firstName";
      score = "firstScore";
      imgFirst = value["profile_picture"]
    }else if (count===1){
      name = "secondName";
      score = "secondScore";
      imgSecond = value["profile_picture"]
    }else if (count===2){
      name = "thirdName";
      score = "thirdScore";
      imgThird = value["profile_picture"]
    }
    document.getElementById(name).innerHTML = key;
    document.getElementById(score).innerHTML = value["highscore"];
    count++;
    if(count===3){
      document.getElementById("firstPlaceIMG").setAttribute("src", imgFirst)
      document.getElementById("secondPlaceIMG").setAttribute("src", imgSecond)
      document.getElementById("thirdPlaceIMG").setAttribute("src", imgThird)
      return;
    }
  }

}

/**
 * Füllt die Anzeige der Tabelle in der Bestenliste
 * aus den Benutzerdaten aller Benutzer in der sortierten Benutzerliste,
 * außer der ersten drei.
 *
 * @param {Map} data - Eine sortierte Map von Benutzern und ihren Highscores.
 */
function fillTable(data){
  var table = "" ;
  var pos = 0;
  for(const [key, value] of data.entries()){
    pos++;

    if(pos<4){
      continue;
    }
    table += "<tr>";
    table += "<td id='position'>"
      + pos +"</td>"
      + "<td class='tableColumn'>" + key +"</td>"
      + "<td class='tableColumn'>" + value["highscore"] +"</td>";
    table += "</tr>";


  }
  document.getElementById("result").innerHTML = table;

}

/**
 * Sortiert die Benutzerliste absteigend nach ihrem Highscore.
 *
 * @param {Map} data - Eine Map von Benutzern und ihren Highscores.
 * @returns {Map} Eine sortierte Map von Benutzern und ihren Highscores.
 */
function sortList(data){
  return new Map([...data.entries()].sort((a, b) => b[1]["highscore"] - a[1]["highscore"]));
}