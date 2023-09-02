import {createApp} from 'vue'

import Root from "./Root.vue";
import App from "./App.vue"

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import {createRouter, createWebHistory} from "vue-router";
import FilesList from "@/files/FilesList.vue";


const router = createRouter({
    history: createWebHistory(),
    routes: [
        {path: '/', component: App},
        {path: '/list', component: FilesList}
    ]
})

const app = createApp(Root)

app.use(ElementPlus)
app.use(router)

app.mount("#root")
