<template>
  <header class="yx-header" :class="{ 'yx-header--scrolled': isScrolled }">
    <div class="container yx-header__container">
      <div class="yx-header__brand">
        <span class="yx-header__title brand-title">悦鑫乐怡民宿</span>
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
        <slot name="actions">
          <router-link to="/login" class="login-link"> 登录 </router-link>
          <router-link to="/register" class="register-link"> 注册 </router-link>
        </slot>
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
    max-width: 1600px;
    margin: 0 auto;
    padding: 0 24px;
  }

  &__brand {
    display: inline-flex;
    align-items: center;
    gap: var(--space-3);
  }

  &__title {
    color: var(--brand-primary, #3b82f6);
    font-weight: 600;
    font-size: clamp(24px, 2.2vw, 32px);
    line-height: 1.1;
    letter-spacing: 2px;
  }

  &__nav {
    display: none; gap: 32px;
    @media (min-width: 768px) { display: inline-flex; }
  }
  &__link {
    color: #2c3e50;
    padding: 10px 18px; 
    border-radius: 8px;
    font-size: 17px;
    font-weight: 600;
    transition: color var(--duration-150) var(--ease-out), background var(--duration-150) var(--ease-out);
  }
  &__link:hover { 
    color: #409eff; 
    background: rgba(64, 158, 255, 0.1); 
  }

  &__actions { display: inline-flex; gap: var(--space-4); align-items: center; }
}

:global([data-theme="dark"]) .yx-header {
  background: rgba(15, 23, 42, 0.5);
  &--scrolled { background: rgba(15, 23, 42, 0.65); }
}
</style>


:root .login-link {
  color: #2468f2; text-decoration: none; font-weight: 600; padding: 8px 12px; border: 1px solid #2468f2; border-radius: 8px; transition: all var(--duration-150) var(--ease-out);
}
:root .login-link:hover { background: #2468f2; color: #fff; }

:root .register-link {
  color: #fff; background: #2468f2; text-decoration: none; font-weight: 600; padding: 8px 12px; border-radius: 8px; transition: background var(--duration-150) var(--ease-out);
}
:root .register-link:hover { background: #1d54c5; }
