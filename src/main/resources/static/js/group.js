function changeColor(id) {
    let color = document.getElementById("f-color-" + id).value;

    let request = new XMLHttpRequest();
    let csrfToken = document.getElementsByName("_csrf")[0].value;
    request.open("POST", getUrl("/spending/rest/friends/" + id + "/change-color"));
    request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    request.send("color=" + color + "&_csrf=" + csrfToken);
}

async function addFriend() {
    let csrfToken = document.getElementsByName("_csrf")[0].value;
    let email = document.getElementById("email").value;

    let res = await fetch(getUrl("/spending/rest/group/addfriend"), {
        method: 'post',
        headers: {
            "Content-type": "application/x-www-form-urlencoded; charset=UTF-8"
        },
        body: "email=" + email + "&_csrf=" + csrfToken
    });
    if (res.status === 200) {
        // let note = await res.json();
        // console.log(note);
    } else {
        alert(await res.text())
    }

}