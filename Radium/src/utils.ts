export function isServerAddressValid(serverAddress: string): Promise<Response> {
    return fetch(`${serverAddress}/api/files/list`)
}