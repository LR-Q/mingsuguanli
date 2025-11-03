<template>
  <section class="yx-cta">
    <div class="container yx-cta__container">
      <div class="yx-cta__content">
        <h2 class="yx-cta__title">注册会员，享专属优惠</h2>
        <p class="yx-cta__desc">限时福利、会员折扣、优先预订权等你来拿。</p>
      </div>
      <div class="yx-cta__actions">
        <button class="btn btn--primary" v-ripple @click="$emit('signup')">立即加入</button>
        <button class="btn btn--secondary" @click="$emit('learnMore')">了解更多</button>
      </div>
    </div>
  </section>
</template>

<script setup>
defineEmits(['signup', 'learnMore'])
</script>

<style lang="scss" scoped>
@use "@/assets/styles/tokens" as *;

.yx-cta {
  padding: var(--space-8) 0;
  background-image: var(--color-primary-gradient);
  color: #fff;

  &__container {
    display: grid; gap: var(--space-4);
    grid-template-columns: 1fr;
    align-items: center;
    @media (min-width: 768px) { grid-template-columns: 1.3fr .7fr; }
  }
  &__title { margin: 0 0 var(--space-2); }
  &__desc { margin: 0; opacity: .9; }
  &__actions { display: inline-flex; gap: var(--space-3); flex-wrap: wrap; }
}

:global([data-theme="dark"]) .yx-cta {
  filter: saturate(0.95);
}
</style>


