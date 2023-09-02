import {backend_setting} from "@/config";

export async function isServerAddressValid(serverAddress: string) {
    return fetch(`${serverAddress}/api/files/list`)
}

export async function getFileList(): Promise<string> {
    return (await fetch(`${backend_setting.value}/api/files/list`)).text()
}
