<template>
  <div class="home-reco">
    <div class="page-header">
      <h2>首页推荐管理</h2>
      <div class="actions">
        <el-button type="primary" :loading="saving" @click="save">保存推荐</el-button>
        <el-button @click="reload">刷新列表</el-button>
      </div>
    </div>

    <el-alert type="info" :closable="false" show-icon class="tip">
      首页推荐用于用户端首页展示精选房源。当前实现为前端存储（localStorage），后续可改为后端接口。
    </el-alert>

    <div class="list-toolbar">
      <el-input v-model="keyword" placeholder="按房型/位置名称筛选" clearable class="w-260" />
      <el-select v-model="locationId" placeholder="按民宿位置筛选" clearable class="w-220">
        <el-option v-for="l in locations" :key="l.id" :label="l.name" :value="l.id" />
      </el-select>
    </div>

    <el-table :data="filteredRooms" style="width: 100%" v-loading="loading" height="520">
      <el-table-column label="封面" width="120">
        <template #default="{ row }">
          <el-image :src="row.currentImage" fit="cover" style="width:100px;height:80px">
            <template #error>
              <div class="img-ph">无图</div>
            </template>
          </el-image>
        </template>
      </el-table-column>
      <el-table-column prop="roomTypeName" label="房型" min-width="160" />
      <el-table-column prop="locationName" label="民宿位置" min-width="160" />
      <el-table-column prop="price" label="价格/晚" width="120">
        <template #default="{ row }">￥{{ row.price }}</template>
      </el-table-column>
      <el-table-column label="推荐" width="120">
        <template #default="{ row }">
          <el-switch v-model="recoSet[row.id]" :active-value="true" :inactive-value="false" />
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAvailableRooms, getUserLocations } from '@/api/modules/userRoom'

const loading = ref(false)
const saving = ref(false)
const rooms = ref([])
const locations = ref([])
const keyword = ref('')
const locationId = ref(null)
const recoSet = ref({})

const LS_KEY = 'superAdmin:homeRecommendations'

const reload = async () => {
  try {
    loading.value = true
    const resLoc = await getUserLocations()
    locations.value = resLoc.data || []
    const res = await getAvailableRooms({ current: 1, size: 50 })
    const data = res.data || { records: [] }
    rooms.value = (data.records || []).map(r => {
      // 规范字段
      if (r.images && typeof r.images === 'string') {
        try { const arr = JSON.parse(r.images); r.currentImage = arr?.[0] || null } catch { r.currentImage = null }
      } else {
        r.currentImage = Array.isArray(r.images) ? r.images?.[0] || null : null
      }
      r.price = r.currentPrice || r.basePrice || 0
      return r
    })
  } catch (e) {
    ElMessage.error('加载房源失败')
  } finally {
    loading.value = false
  }
}

const filteredRooms = computed(() => {
  let list = rooms.value
  if (keyword.value) {
    const kw = keyword.value.toLowerCase()
    list = list.filter(r => (r.roomTypeName || '').toLowerCase().includes(kw) || (r.locationName || '').toLowerCase().includes(kw))
  }
  if (locationId.value) list = list.filter(r => r.locationId === locationId.value)
  return list
})

const loadReco = () => {
  try {
    const raw = localStorage.getItem(LS_KEY)
    if (!raw) return
    const ids = JSON.parse(raw)
    const set = {}
    ids.forEach(id => set[id] = true)
    recoSet.value = set
  } catch {}
}

const save = () => {
  try {
    saving.value = true
    const ids = Object.keys(recoSet.value).filter(id => recoSet.value[id]).map(id => Number(id))
    localStorage.setItem(LS_KEY, JSON.stringify(ids))
    ElMessage.success('首页推荐已保存')
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  loadReco()
  await reload()
})
</script>

<style scoped>
.home-reco { background: #fff; padding: 16px; border-radius: 12px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.page-header h2 { margin: 0; }
.tip { margin-bottom: 12px; }
.list-toolbar { display: flex; gap: 12px; margin-bottom: 12px; }
.w-260 { width: 260px; }
.w-220 { width: 220px; }
.img-ph { width:100px; height:80px; background:#f5f7fa; display:flex; align-items:center; justify-content:center; color:#909399; border-radius:6px; }
</style>
