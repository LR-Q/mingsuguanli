<template>
  <section class="yx-hero hero">
    <div class="container yx-hero__container">
      <div class="yx-hero__content">
        <h1 class="yx-hero__title">发现下一个让你心动的民宿</h1>
        <p class="yx-hero__desc">精选高评分，位置优越，风格多元，让旅途更美好。</p>
        <div class="yx-hero__cta">
          <button class="btn btn--primary" v-ripple @click="$emit('goExplore')">
            开始探索
          </button>
          <button class="btn btn--secondary" @click="$emit('openMap')">
            查看地图
          </button>
        </div>
      </div>
      <div class="yx-hero__illustration">
        <slot name="illustration" />
      </div>
    </div>
  </section>
</template>

<script setup>
defineEmits(['goExplore', 'openMap'])
</script>

<style lang="scss" scoped>
@use "@/assets/styles/tokens" as *;

.yx-hero {
  position: relative;
  padding: 72px 0;

  &__container {
    display: grid;
    grid-template-columns: 1fr;
    gap: var(--space-8);
    align-items: center;
    @media (min-width: 1024px) {
      grid-template-columns: 1.1fr .9fr;
    }
  }
  &__content { z-index: 1; }
  &__title {
    font-size: 34px; line-height: 1.2; margin: 0 0 var(--space-3);
    background: var(--color-primary-gradient);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }
  &__desc {
    color: var(--text-2); margin-bottom: var(--space-6);
    max-width: 52ch;
  }
  &__cta { display: inline-flex; gap: var(--space-3); flex-wrap: wrap; }
  &__illustration { min-height: 220px; }
}

:global([data-theme="dark"]) .yx-hero__title {
  opacity: .95;
}
</style>


