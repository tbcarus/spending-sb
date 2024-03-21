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

function activeCheckbox(id) {
    let status = document.getElementById("enable-" + id).checked;

    let request = new XMLHttpRequest();
    let csrfToken = document.getElementsByName("_csrf")[0].value;
    request.open("POST", "http://localhost:8080/spending/rest/admin/users/" + id + "/enable");
    request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    request.send("param=" + status + "&_csrf=" + csrfToken);
    changeRecordColor(status, false, id);
}

function banCheckbox(id) {
    let status = document.getElementById("ban-" + id).checked;

    let request = new XMLHttpRequest();
    let csrfToken = document.getElementsByName("_csrf")[0].value;
    request.open("POST", "http://localhost:8080/spending/rest/admin/users/" + id + "/ban");
    request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    request.send("param=" + status + "&_csrf=" + csrfToken);
    eCheckbox = document.getElementById("enable-" + id);
    if (status) {
        eCheckbox.checked = false;
        eCheckbox.disabled = true;

    } else {
        eCheckbox.disabled = false;
    }
    changeRecordColor(status, true, id);
}