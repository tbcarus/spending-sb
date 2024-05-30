function changeColor(id) {
    let color = document.getElementById("f-color-" + id).value;

    let request = new XMLHttpRequest();
    let csrfToken = document.getElementsByName("_csrf")[0].value;
    request.open("POST", getUrl("/spending/rest/friends/" + id + "/change-color"));
    request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    request.send("color=" + color + "&_csrf=" + csrfToken);
}

function addFriend() {
    let csrfToken = document.getElementsByName("_csrf")[0].value;
    let email = document.getElementById("email").value;
    alert(email);
    let request = new XMLHttpRequest();
    request.open("POST", getUrl("/spending/rest/group/addfriend"));
    request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    request.send("email=" + email + "&_csrf=" + csrfToken);
}