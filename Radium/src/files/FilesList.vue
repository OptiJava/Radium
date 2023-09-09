<template>
  <div class="file-share">
    <el-row>
      <el-col :span="24">
        <el-text>以下文件为本储存节点中全部公开文件，私有文件不在此列表</el-text>
      </el-col>
    </el-row>
    <el-divider></el-divider>
    <el-row>
      <el-col :span="24">
        <el-table :data="files" border stripe>
          <el-table-column prop="fileName" label="文件名" width="400px"></el-table-column>
          <el-table-column prop="uploadTime" label="上传日期" width="150px"></el-table-column>
          <el-table-column prop="size" label="大小" width="100px"></el-table-column>
          <el-table-column prop="expire" label="过期时间" width="150px"></el-table-column>
          <el-table-column prop="download" label="下载" width="70px">
            <template #default="scope">
              <el-button @click="downloadFile(scope.row.id, scope.row.fileName)"></el-button>
            </template>
          </el-table-column>
          <el-table-column prop="delete" label="删除" width="70px">
            <template #default="scope">
              <el-button @click="removeFile(scope.row.id, scope.row.fileName)"></el-button>
            </template>
          </el-table-column>
          <el-table-column prop="id" label="ID"></el-table-column>
        </el-table>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import {ref} from "vue";
import {getFileList} from "@/utils";
import {backend_setting} from "@/config";
import {ElMessage} from "element-plus"

// file list loading //
const files = ref()

function loadList() {
  ElMessage.info("正在加载文件列表...")
  getFileList().then(data => {
    files.value = JSON.parse(data)
  }).catch(err => {
    ElMessage.error(`获取文件列表失败：${err}`)
  })
}

loadList()

// file download //
function downloadFile(id: string, name: string) {
  window.open(`${backend_setting.value}/api/files/${id}/${name}`)
}

// file remove //
async function removeFile(id: string, name: string) {
  ElMessage.info(`正在删除文件 ${name} ...`)
  await fetch(`${backend_setting.value}/api/files/${id}/`, {method: 'DELETE'}).then(resp => {
    if (resp.ok) {
      loadList()
      ElMessage.success(`删除文件 ${name} 成功`)
    } else {
      resp.text().then(text => {
        ElMessage.error(`删除文件 ${name} 失败：${resp.status} ${text}`)
      })
    }
  }).catch(err => {
    ElMessage.error(`删除文件 ${name} 失败：${err.text}`)
  })
}

</script>

<style scoped lang="css">
.file-share {
  margin: 20px;
}
</style>
