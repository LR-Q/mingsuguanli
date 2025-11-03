<template>
  <article class="yx-card glass-card hover-lift">
    <header class="yx-card__header" v-if="$slots.header">
      <slot name="header" />
    </header>
    <section class="yx-card__body">
      <slot />
    </section>
    <footer class="yx-card__footer" v-if="$slots.footer">
      <slot name="footer" />
    </footer>
  </article>
</template>

<script setup>
/* 纯UI容器组件 */
</script>

<style lang="scss" scoped>
@use "@/assets/styles/tokens" as *;

.yx-card {
  border-radius: var(--radius-12);
  overflow: hidden;

  &__header, &__footer {
    padding: var(--space-4);
  }
  &__body {
    padding: var(--space-4);
  }
}
</style>


