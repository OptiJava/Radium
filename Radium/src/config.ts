import {getCookie, setCookie} from "typescript-cookie";
import {ref} from "vue";

export const default_backend_setting = 'https://replit.radium.optijava.top'

export const backend_setting = ref(default_backend_setting)

export function loadConfigNow() {
    if (getCookie('backend_setting') === undefined) {
        setCookie('backend_setting', backend_setting.value, { expires: 999999 })
    } else {
        backend_setting.value = <string>getCookie('backend_setting')
    }
}
