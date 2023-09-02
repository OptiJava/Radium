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

// file list loading //
const files = ref()

getFileList().then(data => {
  files.value = JSON.parse(data)
})

// file download //
function downloadFile(id: string, name: string) {
  window.open(`${backend_setting.value}/api/files/${id}/${name}`)
}

</script>

<style scoped lang="css">
.file-share {
  margin: 20px;
}
</style>
