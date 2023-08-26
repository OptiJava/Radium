<template>
  <div id="app">
    <div class="top-bar">
      <div class="top-bar-text">Radium 文件分享站</div>
    </div>

    <div style="z-index: 2;position: absolute;right: 4%;top: 10px; opacity: 1.0">
      <el-dropdown>
        <el-button type="default">设置</el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="display_backend_setting_dialog = true">设置储存节点</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <el-dialog
          v-model="display_backend_setting_dialog"
          :before-close="handle_backend_setting_dialog_close"
          title="设置储存节点"
          width="30%"
      >
        <span>
          <el-input v-model="backend_setting"
                    placeholder="请输入后端储存节点服务器地址，应以http(s)://开头，末端不加/"></el-input>
        </span>
        <template #footer>
            <span class="dialog-footer">
              <el-button @click="backend_setting='https://radium--optijava.repl.co'">使用默认后端储存节点</el-button>
              <el-button
                  @click="display_backend_setting_dialog = false;backend_setting='https://radium--optijava.repl.co'">取消</el-button>
              <el-button type="primary"
                         @click="handle_backend_setting_dialog_close(() => {display_backend_setting_dialog = false})">确认</el-button>
            </span>
        </template>
      </el-dialog>
    </div>

    <div style="text-align: center">
      <h1 :style="title_text_color" class="animate__animated animate__backInUp">
        方便、快捷地与他人共享你的文件</h1>
      <br>

      <el-button
          v-if="display_upload===false"
          size="large"
          type="primary"
          @click="display_upload=true;upload_success_msg = ''"
      >上传文件
      </el-button>

      <br>
      <el-text v-if="upload_success_msg !== '' && display_upload === false" size="large" type="success">
        {{ upload_success_msg }}
      </el-text>

      <el-upload
          v-if="display_upload"
          :before-upload="() => { uploading=true }"
          :on-success="() => { uploading=false }"
          :on-error="() => { uploading=false }"
          :auto-upload="true"
          :drag="true"
          :http-request="uploadFile"
          :show-file-list="false"
          :multiple="false"
          action='/api/upload/'
          style="margin-left: 30%;margin-right: 30%;margin-top: 25px;opacity: 0.76;"
      >
        <el-button style="opacity: 0.9" type="primary">点击上传</el-button>
        <div slot="tip" class="el-upload__tip">只能上传一个文件</div>
      </el-upload>
      <br>
      <div v-if="uploading">
        <el-text>正在上传中...</el-text>
        <el-progress style="margin-right: 32%;margin-left: 32%;" :stroke-width='10' :striped="true" :percentage="78"
                     :show-text="false" :indeterminate="true" duration="2.24"/>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {ElMessage} from 'element-plus'
import {ref} from "vue";
import {isServerAddressValid} from "./utils"

export default {
  setup() {
    // Settings //
    const display_backend_setting_dialog = ref(false)
    const backend_setting = ref('https://radium--optijava.repl.co')

    function handle_backend_setting_dialog_close(done: () => void) {
      if (!backend_setting.value.startsWith('http')) {
        ElMessage.error("存储节点服务器地址应以http(s)://开头")
      } else if (backend_setting.value.endsWith('/')) {
        ElMessage.error("存储节点服务器地址末端不能加/")
      } else if (!isServerAddressValid(backend_setting.value, ElMessage)) {
        ElMessage.error("存储节点服务器地址无效！")
      } else {
        ElMessage.success("存储节点服务器设置成功")
        done()
      }
    }

    // Upload //
    const display_upload = ref(false)
    const upload_success_msg = ref("")
    const uploading = ref(false)

    function uploadFile(file) {
      ElMessage.info(`正在上传文件${file.file.name}`)
      const url = `${backend_setting.value}/api/upload/${file.file.name}`
      const config = {
        method: 'PUT',
        headers: {'Content-Type': 'application/octet-stream'},
        body: file.file
      }
      return fetch(url, config)
          .then(response => {
            if (response.ok) {
              display_upload.value = false
              response.text().then(text => {
                upload_success_msg.value = `文件 ${file.file.name} 已成功上传，文件id：${text}`
                ElMessage.success(`文件上传成功`)
                console.log(`File uploaded to ${url}`)
              })
            } else {
              response.text().then(text => {
                    ElMessage.error(`文件上传失败，可能是后端储存节点正在维护 返回码：` + response.statusText + '，错误信息：' + text)
                  }
              )
            }
          })
    }

    // Title //
    const title_text_color = ref(`margin-top: 85px;color: ${["blue", "aqua", "wheat", "black", "gray", "white", '#9773ff'][Math.floor(Math.random() * (7))]}; opacity: 1.0;`)

    return {
      uploadFile,
      display_upload,
      title_text_color,
      upload_success_msg,
      display_backend_setting_dialog,
      backend_setting,
      handle_backend_setting_dialog_close,
      uploading
    }
  },
}
</script>

<style scoped>
.top-bar {
  height: 50px;
  width: 100%;
  position: fixed;
  top: 0;
  z-index: 1;
  right: 0;
  left: 0;
  background-color: white;
  border: gray 1px solid;
  box-shadow: rgba(0, 0, 0, 0.65) 0 1px 7px 0;
  opacity: 0.78;
}

.top-bar-text {
  font-size: 20px;
  color: #253341;
  line-height: 50px;
  z-index: 2;
  margin-left: 20px;
  opacity: 0.78;
}
</style>