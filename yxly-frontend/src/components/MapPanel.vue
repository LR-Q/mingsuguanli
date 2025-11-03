<template>
  <aside class="yx-map-panel map-panel glass-card">
    <header class="yx-map-panel__head">
      <h3 class="yx-map-panel__title">
        附近推荐
      </h3>
      <slot name="actions" />
    </header>
    <div class="yx-map-panel__body">
      <slot />
    </div>
  </aside>
</template>

<script setup>
/* 纯展示容器，不改动业务逻辑 */
</script>

<style lang="scss" scoped>
@use "@/assets/styles/tokens" as *;

.yx-map-panel {
  padding: var(--space-4);

  &__head {
    display: flex; align-items: center; justify-content: space-between;
    margin-bottom: var(--space-3);
  }
  &__title {
    font-size: var(--font-size-title);
    margin: 0;
  }
  &__body { display: grid; gap: var(--space-3); }
}
</style>


