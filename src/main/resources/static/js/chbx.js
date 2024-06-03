function changeRecordColor(status, ban, id) {
    if (status && !ban) {
        document.getElementById("name-" + id).style.color = '#000000';
        document.getElementById("email-" + id).style.color = '#000000';
        document.getElementById("roles-" + id).style.color = '#000000';
        document.getElementById("del-" + id).style.color = '#000000';
    }
    if (!status && !ban) {
        document.getElementById("name-" + id).style.color = '#cccccc';
        document.getElementById("email-" + id).style.color = '#cccccc';
        document.getElementById("roles-" + id).style.color = '#cccccc';
        document.getElementById("del-" + id).style.color = '#cccccc';
    }
    if (status && ban) {
        document.getElementById("name-" + id).style.color = '#e75b5b';
        document.getElementById("email-" + id).style.color = '#e75b5b';
        document.getElementById("roles-" + id).style.color = '#e75b5b';
        document.getElementById("del-" + id).style.color = '#e75b5b';
    }
    if (!status && ban) {
        document.getElementById("name-" + id).style.color = '#cccccc';
        document.getElementById("email-" + id).style.color = '#cccccc';
        document.getElementById("roles-" + id).style.color = '#cccccc';
        document.getElementById("del-" + id).style.color = '#cccccc';
    }
}

async function activeCheckbox(id) {
    let status = document.getElementById("enable-" + id).checked;
    let csrfToken = document.getElementsByName("_csrf")[0].value;
    let res = await fetch(getUrl("/spending/rest/admin/users/" + id + "/enable"), {
        method: 'post',
        headers: {
            "Content-type": "application/x-www-form-urlencoded; charset=UTF-8"
        },
        body: "param=" + status + "&_csrf=" + csrfToken
    });
    changeRecordColor(status, false, id);
    let user = await res.json();
    console.log(user);
}

function banCheckbox(id) {
    let status = document.getElementById("ban-" + id).checked;
    let csrfToken = document.getElementsByName("_csrf")[0].value;
    fetch(getUrl("/spending/rest/admin/users/" + id + "/ban"), {
        method: 'post',
        headers: {
            "Content-type": "application/x-www-form-urlencoded; charset=UTF-8"
        },
        body: "param=" + status + "&_csrf=" + csrfToken
    });

    eCheckbox = document.getElementById("enable-" + id);
    if (status) {
        eCheckbox.checked = false;
        eCheckbox.disabled = true;

    } else {
        eCheckbox.disabled = false;
    }
    changeRecordColor(status, true, id);
}