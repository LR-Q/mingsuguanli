<template>
  <header class="yx-header" :class="{ 'yx-header--scrolled': isScrolled }">
    <div class="container yx-header__container">
      <div class="yx-header__brand">
        <img class="yx-header__logo" src="/logo.svg" alt="Logo" />
        <span class="yx-header__title">悦鑫乐怡 · 民宿</span>
      </div>
      <nav class="yx-header__nav">
        <slot name="nav">
          <a href="#home" class="yx-header__link">首页</a>
          <a href="#explore" class="yx-header__link">探索</a>
          <a href="#map" class="yx-header__link">地图</a>
          <a href="#contact" class="yx-header__link">联系</a>
        </slot>
      </nav>
      <div class="yx-header__actions">
        <slot name="actions" />
      </div>
    </div>
  </header>
  
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue'

const isScrolled = ref(false)
const onScroll = () => {
  isScrolled.value = window.scrollY > 8
}
onMounted(() => window.addEventListener('scroll', onScroll, { passive: true }))
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<style lang="scss" scoped>
@use "@/assets/styles/tokens" as *;

.yx-header {
  position: sticky;
  top: 0;
  z-index: 50;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.65);
  border-bottom: 1px solid var(--border-color);
  transition: background var(--duration-200) var(--ease-out), box-shadow var(--duration-200) var(--ease-out);

  &--scrolled {
    background: rgba(255, 255, 255, 0.78);
    box-shadow: var(--shadow-sm);
  }

  &__container {
    display: flex; align-items: center; justify-content: space-between;
    height: 64px;
  }

  &__brand {
    display: inline-flex; align-items: center; gap: var(--space-3);
  }
  &__logo { width: 28px; height: 28px; }
  &__title { font-weight: 700; color: var(--text-1); letter-spacing: .2px; }

  &__nav {
    display: none; gap: var(--space-4);
    @media (min-width: 768px) { display: inline-flex; }
  }
  &__link {
    color: var(--text-2);
    padding: 8px 10px; border-radius: 8px;
    transition: color var(--duration-150) var(--ease-out), background var(--duration-150) var(--ease-out);
  }
  &__link:hover { color: var(--text-1); background: rgba(2,6,23,0.04); }

  &__actions { display: inline-flex; gap: var(--space-3); }
}

:global([data-theme="dark"]) .yx-header {
  background: rgba(15, 23, 42, 0.5);
  &--scrolled { background: rgba(15, 23, 42, 0.65); }
}
</style>


