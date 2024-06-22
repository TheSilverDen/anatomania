function checkDbElements(){
    fetch("checkDB", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
    })
        .then(
            async result => {
                let res = await result.text();
                if(res==="false"){
                    window.alert("Essentielle Elemente sind nicht mehr in der Datenbank vorhanden. Bitte wende dich an deinen Administrator.")
                    window.location.href = "login"
                }
            });
}