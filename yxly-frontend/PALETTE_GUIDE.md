# 色板替换建议与实施步骤

- 主色由单色 `#2563eb` 升级为渐变 `#2563eb -> #60a5fa`；纯色备用 `#3b82f6`。
- 中性色：文本 `--text-1/#0f172a`、次文本 `--text-2/#475569`、底色 `--color-neutral-50/#f8fafc`。
- 状态色：成功 `#22c55e`、警告 `#f59e0b`、危险 `#ef4444`、信息 `#60a5fa`。

## 实施
1. 全局样式已引入：`tokens.scss`、`base.scss`、`utilities.scss`、`theme-element.scss`、`dark.scss`。
2. 覆盖 Element Plus 变量通过 `theme-element.scss` 完成；无需更改组件代码即可生效。
3. 将业务样式中硬编码主色（如 `#2563eb`、`#3b82f6`）替换为：
   - 纯色：`var(--color-primary-solid)`
   - 渐变背景：`var(--color-primary-gradient)`
4. 边框与分割线统一为：`var(--border-color)`。
5. 卡片类统一添加：`glass-card hover-lift`，获得玻璃拟态与悬停提升。
6. 暗色模式：在 `html` 或 `body` 添加 `data-theme="dark"` 开关。

> 若需要在脚本字符串中设置颜色（如地图标注 label），无法直接使用 CSS 变量，可替换为接近主色的 `#3b82f6`。


