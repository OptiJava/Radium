export function isServerAddressValid(serverAddress, elm) {
    elm.info("正在检查服务器地址...")
    const http = new XMLHttpRequest();
    http.open('GET', serverAddress + '/api/files/list', false)
    http.send()
    console.log(serverAddress + " checking finished.")
    return http.status <= 299 && http.status >= 200
}