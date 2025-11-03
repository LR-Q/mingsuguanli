<template>
  <div class="yx-search glass-card hover-lift">
    <el-form :inline="true" class="yx-search__form" @submit.prevent>
      <el-form-item class="yx-search__item yx-search__item--grow">
        <el-input v-model="keyword" placeholder="搜索目的地/民宿名" clearable />
      </el-form-item>
      <el-form-item class="yx-search__item">
        <el-date-picker
          v-model="dates"
          type="daterange"
          start-placeholder="入住"
          end-placeholder="离店"
          range-separator="至"
          :editable="false"
          :unlink-panels="true"
        />
      </el-form-item>
      <el-form-item class="yx-search__item">
        <el-select v-model="guest" placeholder="人数">
          <el-option :key="i" :label="i + ' 人'" :value="i" v-for="i in 10" />
        </el-select>
      </el-form-item>
      <el-form-item class="yx-search__item">
        <el-button type="primary" size="large" v-ripple @click="onSearch">
          搜索
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const keyword = ref('')
const dates = ref(null)
const guest = ref(null)
const emit = defineEmits(['search'])

const onSearch = () => {
  emit('search', { keyword: keyword.value, dates: dates.value, guest: guest.value })
}
</script>

<style lang="scss" scoped>
@use "@/assets/styles/tokens" as *;

.yx-search {
  padding: var(--space-4);
  border-radius: var(--radius-12);

  &__form {
    display: grid;
    grid-template-columns: 1fr;
    gap: var(--space-3);

    @media (min-width: 768px) {
      grid-template-columns: 1.2fr 1fr .6fr auto;
      align-items: center;
    }
  }

  &__item { margin: 0; }
  &__item--grow { min-width: 180px; }
}
</style>


