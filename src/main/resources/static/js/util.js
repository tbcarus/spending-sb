function getUrl(str) {
    let link = (document.location.protocol + "//"
        + document.location.host
        + str);
    return link;
}