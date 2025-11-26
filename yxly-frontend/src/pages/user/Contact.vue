<template>
  <div class="contact-page">
    <section class="hero">
      <h1>联系我们</h1>
      <p>如果您在预订或入住过程中遇到任何问题，欢迎随时与我们联系。</p>
    </section>

    <div class="contact-grid">
      <div class="contact-card">
        <h3>客服热线</h3>
        <p class="value">400-888-0000</p>
        <p class="desc">每日 08:00 - 22:00 提供人工服务</p>
      </div>
      <div class="contact-card">
        <h3>服务邮箱</h3>
        <p class="value">support@yxly.com</p>
        <p class="desc">欢迎反馈产品建议或合作需求</p>
      </div>
      <div class="contact-card">
        <h3>总部地址</h3>
        <p class="value">广东 · 梅州 · 民宿产业园</p>
        <p class="desc">周一至周五 09:00 - 18:00 对外接待</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.contact-page {
  max-width: 960px;
  margin: 0 auto;
  padding: 40px 20px 80px;
}

.hero {
  text-align: center;
  margin-bottom: 32px;
}

.hero h1 {
  margin: 0 0 12px;
  font-size: 30px;
  color: #303133;
}

.hero p {
  margin: 0;
  color: #606266;
}

.contact-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

.contact-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
  text-align: center;
}

.contact-card h3 {
  margin: 0;
  color: #2468f2;
}

.contact-card .value {
  margin: 12px 0 6px;
  font-size: 20px;
  font-weight: 600;
}

.contact-card .desc {
  margin: 0;
  color: #606266;
}
</style>
