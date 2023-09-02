import {getCookie, setCookie} from "typescript-cookie";
import {ref} from "vue";

export const backend_setting = ref('https://radium--optijava.repl.co')

export function loadConfigNow() {
    if (getCookie('backend_setting') === undefined) {
        setCookie('backend_setting', backend_setting.value, { expires: 99999999999999 })
    } else {
        backend_setting.value = <string>getCookie('backend_setting')
    }
}
