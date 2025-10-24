<template>
  <div class="room-form">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <h3>{{ isEdit ? '编辑房间' : '添加房间' }}</h3>
          <el-button @click="handleBack">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
        </div>
      </template>
      
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        v-loading="loading"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属民宿" prop="locationId">
              <el-select 
                v-model="formData.locationId" 
                placeholder="请选择民宿位置"
                style="width: 100%"
                :disabled="isEdit"
              >
                <el-option 
                  v-for="location in locationList" 
                  :key="location.id" 
                  :label="location.name" 
                  :value="location.id"
                >
                  <div style="display: flex; justify-content: space-between;">
                    <span>{{ location.name }}</span>
                    <span style="color: #8492a6; font-size: 12px;">{{ location.address }}</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="房间号" prop="roomNumber">
              <el-input 
                v-model="formData.roomNumber" 
                placeholder="请输入房间号"
                :disabled="isEdit"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="房型" prop="roomTypeId">
              <div style="display: flex; gap: 8px; align-items: center;">
                <el-select 
                  v-model="formData.roomTypeId" 
                  placeholder="请选择房型"
                  style="min-width: 200px; flex: 1;"
                  @change="handleRoomTypeChange"
                  popper-class="room-type-dropdown"
                  :teleported="false"
                  placement="bottom-start"
                  clearable
                >
                  <el-option
                    v-for="type in roomTypes"
                    :key="type.id"
                    :label="type.typeName"
                    :value="type.id"
                  />
                </el-select>
                <el-button type="warning" size="small" @click="initRoomTypes" :loading="initLoading">
                  初始化房型
                </el-button>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="楼层" prop="floorNumber">
              <el-input-number 
                v-model="formData.floorNumber" 
                :min="1" 
                :max="50"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="面积(㎡)" prop="area">
              <el-input-number 
                v-model="formData.area" 
                :min="10" 
                :max="200"
                :precision="2"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="床型" prop="bedType">
              <el-select 
                v-model="formData.bedType" 
                placeholder="请选择床型"
                style="width: 100%; min-width: 120px;"
                popper-class="bed-type-dropdown"
                :teleported="false"
                placement="bottom-start"
                clearable
              >
                <el-option label="单人床" value="单人床" />
                <el-option label="双人床" value="双人床" />
                <el-option label="大床" value="大床" />
                <el-option label="双床" value="双床" />
                <el-option label="上下铺" value="上下铺" />

              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="8">
            <el-form-item label="最大入住人数" prop="maxGuests">
              <el-input-number 
                v-model="formData.maxGuests" 
                :min="1" 
                :max="10"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="8">
            <el-form-item label="房间状态" prop="status">
              <el-select 
                v-model="formData.status" 
                placeholder="请选择房间状态"
                style="width: 100%; min-width: 120px;"
                popper-class="room-status-dropdown"
                :teleported="false"
                placement="bottom-start"
                clearable
              >
                <el-option label="可用" :value="1" />
                <el-option label="占用" :value="2" />
                <el-option label="维修" :value="3" />
                <el-option label="清洁" :value="4" />
                <el-option label="停用" :value="5" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="基础价格" prop="basePrice">
              <el-input-number 
                v-model="formData.basePrice" 
                :min="0" 
                :precision="2"
                style="width: 100%"
              />
              <span style="margin-left: 8px;">元/晚</span>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="当前价格" prop="currentPrice">
              <el-input-number 
                v-model="formData.currentPrice" 
                :min="0" 
                :precision="2"
                style="width: 100%"
              />
              <span style="margin-left: 8px;">元/晚</span>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="WiFi密码" prop="wifiPassword">
              <el-input 
                v-model="formData.wifiPassword" 
                placeholder="请输入WiFi密码"
                show-password
                style="max-width: 400px;"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="房间设施" prop="facilities">
          <el-checkbox-group v-model="formData.facilities">
            <el-checkbox label="空调">空调</el-checkbox>
            <el-checkbox label="电视">电视</el-checkbox>
            <el-checkbox label="冰箱">冰箱</el-checkbox>
            <el-checkbox label="热水器">热水器</el-checkbox>
            <el-checkbox label="洗衣机">洗衣机</el-checkbox>
            <el-checkbox label="微波炉">微波炉</el-checkbox>
            <el-checkbox label="吹风机">吹风机</el-checkbox>
            <el-checkbox label="WiFi">WiFi</el-checkbox>
            <el-checkbox label="阳台">阳台</el-checkbox>
            <el-checkbox label="独立卫浴">独立卫浴</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        
        <el-form-item label="房间描述" prop="description">
          <el-input 
            v-model="formData.description" 
            type="textarea" 
            :rows="4"
            placeholder="请输入房间描述"
          />
        </el-form-item>
        
        <el-form-item label="房间图片" prop="images">
          <el-upload
            class="room-images-upload"
            :auto-upload="true"
            :http-request="customUpload"
            list-type="picture-card"
            :file-list="imageList"
            :on-remove="handleImageRemove"
            :before-upload="beforeImageUpload"
            multiple
            :limit="6"
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">
                只能上传图片文件，且不超过10MB，最多6张图片
              </div>
            </template>
          </el-upload>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            {{ isEdit ? '更新' : '保存' }}
          </el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button @click="handleBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Plus } from '@element-plus/icons-vue'
import { getRoomTypes, createRoom, updateRoom, getRoomById, uploadRoomImage, getAdminLocations } from '@/api/modules/room'

const route = useRoute()
const router = useRouter()

// 响应式数据
const formRef = ref()
const loading = ref(false)
const submitting = ref(false)
const initLoading = ref(false)
const roomTypes = ref([])
const locationList = ref([])
const imageList = ref([])

// 判断是否为编辑模式
const isEdit = computed(() => !!route.params.id)

// 表单数据
const formData = reactive({
  locationId: null,
  roomNumber: '',
  roomTypeId: null,
  floorNumber: 1,
  area: null,
  bedType: '',
  maxGuests: 2,
  basePrice: null,
  currentPrice: null,
  status: 1,
  facilities: [],
  description: '',
  wifiPassword: '',
  images: []
})

// 表单验证规则
const formRules = {
  locationId: [
    { required: true, message: '请选择所属民宿', trigger: 'change' }
  ],
  roomNumber: [
    { required: true, message: '请输入房间号', trigger: 'blur' },
    { min: 1, max: 20, message: '房间号长度在1到20个字符', trigger: 'blur' }
  ],
  roomTypeId: [
    { required: true, message: '请选择房型', trigger: 'change' }
  ],
  floorNumber: [
    { required: true, message: '请输入楼层', trigger: 'blur' }
  ],
  area: [
    { required: true, message: '请输入面积', trigger: 'blur' }
  ],
  bedType: [
    { required: true, message: '请选择床型', trigger: 'change' }
  ],
  maxGuests: [
    { required: true, message: '请输入最大入住人数', trigger: 'blur' }
  ],
  basePrice: [
    { required: true, message: '请输入基础价格', trigger: 'blur' }
  ],
  currentPrice: [
    { required: true, message: '请输入当前价格', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择房间状态', trigger: 'change' }
  ]
}

// 方法
const loadRoomTypes = async () => {
  try {
    const response = await getRoomTypes()
    // 响应拦截器已经处理了成功响应，直接使用data
    roomTypes.value = response.data
  } catch (error) {
    console.error('获取房型列表失败:', error)
    ElMessage.error('获取房型列表失败')
  }
}

const loadLocationList = async () => {
  try {
    const response = await getAdminLocations()
    locationList.value = response.data || []
    console.log('民宿位置列表:', locationList.value)
  } catch (error) {
    console.error('获取民宿位置列表失败:', error)
    ElMessage.error('获取民宿位置列表失败，请刷新重试')
  }
}

const handleRoomTypeChange = (roomTypeId) => {
  const selectedType = roomTypes.value.find(type => type.id === roomTypeId)
  if (selectedType) {
    formData.basePrice = selectedType.basePrice
    formData.currentPrice = selectedType.basePrice
  }
}

// 初始化房型数据
const initRoomTypes = async () => {
  try {
    initLoading.value = true
    
    const response = await fetch('/api/v1/dev/init-room-types', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      }
    })
    
    const result = await response.json()
    
    if (result.code === 200) {
      ElMessage.success(result.message || '房型数据初始化成功')
      // 重新加载房型列表
      await loadRoomTypes()
    } else {
      ElMessage.error(result.message || '房型数据初始化失败')
    }
  } catch (error) {
    console.error('初始化房型数据失败:', error)
    ElMessage.error('初始化房型数据失败')
  } finally {
    initLoading.value = false
  }
}

const customUpload = async (options) => {
  console.log('开始自定义上传，文件:', options.file.name)
  
  try {
    const formDataObj = new FormData()
    formDataObj.append('file', options.file)
    
    // 如果是编辑模式，使用现有房间ID；如果是新建，使用临时ID
    const roomId = isEdit.value ? route.params.id : Date.now()
    
    console.log('上传参数 - 房间ID:', roomId, '文件大小:', options.file.size)
    
    const response = await uploadRoomImage(formDataObj, roomId)
    const imageUrl = response.data
    
    console.log('上传成功，图片URL:', imageUrl)
    
    // 添加到图片数据
    formData.images.push(imageUrl)
    
    // 调用成功回调
    options.onSuccess(response, options.file)
    
    ElMessage.success('图片上传成功')
  } catch (error) {
    console.error('图片上传失败:', error)
    ElMessage.error('图片上传失败: ' + (error.response?.data?.message || error.message))
    
    // 调用失败回调
    options.onError(error, options.file)
  }
}

const handleImageRemove = (file) => {
  const index = formData.images.findIndex(url => url === file.url)
  if (index > -1) {
    formData.images.splice(index, 1)
  }
  
  // 从显示列表中移除
  const listIndex = imageList.value.findIndex(item => item.url === file.url)
  if (listIndex > -1) {
    imageList.value.splice(listIndex, 1)
  }
}

const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!')
    return false
  }
  return true
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    submitting.value = true
    
    const submitData = {
      ...formData,
      facilities: JSON.stringify(formData.facilities),
      images: JSON.stringify(formData.images)
    }
    
    if (isEdit.value) {
      await updateRoom(route.params.id, submitData)
      ElMessage.success('房间更新成功')
      setTimeout(() => {
        router.push('/admin/rooms')
      }, 1500)
    } else {
      await createRoom(submitData)
      ElMessage.success('房间创建成功')
      setTimeout(() => {
        router.push('/admin/rooms')
      }, 1500)
    }
    
  } catch (error) {
    console.error('操作失败:', error)
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error(isEdit.value ? '房间更新失败' : '房间创建失败')
    }
  } finally {
    submitting.value = false
  }
}

const handleReset = () => {
  formRef.value.resetFields()
  imageList.value = []
  formData.images = []
  formData.facilities = []
}

const handleBack = () => {
  router.push('/admin/rooms')
}

const loadRoomData = async (id) => {
  if (!id) return
  
  loading.value = true
  try {
    const response = await getRoomById(id)
    // 响应拦截器已经处理了成功响应，直接使用data
    const roomData = response.data
    
    // 解析JSON字段
    if (roomData.facilities) {
      try {
        roomData.facilities = JSON.parse(roomData.facilities)
      } catch (e) {
        roomData.facilities = []
      }
    }
    
    if (roomData.images) {
      try {
        roomData.images = JSON.parse(roomData.images)
        // 设置图片列表用于显示
        imageList.value = roomData.images.map((url, index) => ({
          name: `image-${index}`,
          url: url
        }))
      } catch (e) {
        roomData.images = []
      }
    }
    
    // 将加载的数据赋值给表单
    Object.assign(formData, roomData)
    
    console.log('房间数据加载成功:', roomData)
  } catch (error) {
    console.error('获取房间信息失败:', error)
    ElMessage.error('获取房间信息失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadLocationList()
  loadRoomTypes()
  if (isEdit.value) {
    loadRoomData(route.params.id)
  }
})
</script>

<style lang="scss" scoped>
.room-form {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    h3 {
      margin: 0;
      color: #303133;
      font-size: 18px;
      font-weight: 600;
    }
  }
  
  .room-images-upload {
    :deep(.el-upload--picture-card) {
      width: 120px;
      height: 120px;
    }
    
    :deep(.el-upload-list--picture-card .el-upload-list__item) {
      width: 120px;
      height: 120px;
    }
  }
  
  .el-form {
    max-width: 800px;
  }
  
  .el-checkbox-group {
    .el-checkbox {
      margin-right: 20px;
      margin-bottom: 10px;
    }
  }
}

// 下拉框样式优化
:deep(.room-type-dropdown),
:deep(.bed-type-dropdown),
:deep(.room-status-dropdown) {
  z-index: 9999 !important;
  max-height: 300px;
  overflow-y: auto;
}

:deep(.el-select-dropdown) {
  z-index: 9999 !important;
}

:deep(.el-popper) {
  z-index: 9999 !important;
}

// 确保下拉框在表单容器内正确显示
.room-form {
  position: relative;
  z-index: 1;
  
  :deep(.el-select) {
    .el-select__wrapper {
      position: relative;
      min-height: 32px;
    }
    
    .el-select__selected-item {
      max-width: 100%;
      overflow: visible;
      text-overflow: clip;
      white-space: nowrap;
    }
    
    .el-select__input {
      height: auto;
      line-height: normal;
    }
  }
}
</style>