<template>
  <div class="about-page">
    <section class="hero">
      <h1>关于悦鑫乐怡民宿管理系统</h1>
      <p>我们致力于以数字化能力提升雷州民宿行业的运营效率与用户体验。</p>
    </section>

    <section class="content">
      <div class="card">
        <h2>平台定位</h2>
        <p>
          悦鑫乐怡民宿管理系统采用前后端分离架构，围绕“预订、管理、服务”三大场景构建。普通用户可以在平台上完成搜索、预订、评价；民宿管理员则可以在后台集中处理房源、订单、财务以及客服沟通。
        </p>
      </div>

      <div class="features">
        <div class="feature">
          <h3>智能运营</h3>
          <p>通过可视化仪表盘、订单分析等功能，帮助民宿掌握经营动态，快速做出决策。</p>
        </div>
        <div class="feature">
          <h3>多端体验</h3>
          <p>PC 与移动端一致的交互体验，支持游客、住客、民宿主等多种角色无缝切换。</p>
        </div>
        <div class="feature">
          <h3>安全可靠</h3>
          <p>采用 JWT + 权限校验机制，保障用户数据安全；配合 MinIO、Redis 等基础设施，确保平台稳定运行。</p>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.about-page {
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

.card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
  margin-bottom: 28px;
  line-height: 1.7;
  color: #4a4a4a;
}

.features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
}

.feature {
  background: #f5f9ff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #e0edff;
}

.feature h3 {
  margin: 0 0 8px;
  color: #2468f2;
}

.feature p {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}
</style>
