<template>
  <div id="app">
    <div class="top-bar">
      <div class="top-bar-text">Radium 文件分享站</div>
    </div>

    <div style="text-align: center">
      <h1 :style="title_text_color" class="animate__animated animate__backInUp">
        方便、快捷地与他人共享你的文件</h1>
      <br>

      <el-button
          @click="display_upload=true"
          type="primary"
          size="large"
          v-if="display_upload===false"
      >上传文件
      </el-button>

      <el-upload
          v-if="display_upload"
          style="margin-left: 30%;margin-right: 30%;margin-top: 40px;opacity: 0.63;"
          action='/api/upload/'
          :auto-upload="true"
          :drag="true"
          :http-request="uploadFile"
          :show-file-list="false"
      >
        <el-button type="primary">点击上传</el-button>
        <div slot="tip" class="el-upload__tip">只能上传一个文件</div>
      </el-upload>

    </div>
  </div>
</template>

<script>
import {ElMessage} from 'element-plus'
import {ref} from "vue";
import axios from 'axios'

export default {
  setup() {
    const display_upload = ref(false)

    const colors = ["blue", "aqua", "wheat", "black", "gray", "white", '#9773ff']

    const title_text_color = ref(`margin-top: 85px;color: ${colors[Math.floor(Math.random() * (7))]}; opacity: 1.0;`)

    function uploadFile(file) {
      const url = `/api/upload/${file.file.name}`
      const config = {
        headers: {'Content-Type': 'application/octet-stream'},
      }
      return axios.put(url, file.file, config).then(() => {
        ElMessage.success(`文件上传成功`)
        console.log(`File uploaded to ${url}`)
      })
    }

    return {uploadFile, display_upload, title_text_color}
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