<template>
  <div class="auth-shell">
    <div class="auth-pattern" aria-hidden="true" />
    <main class="auth-center">
      <div class="auth-split-card">
        <aside class="auth-aside">
          <div class="aside-glow" aria-hidden="true" />
          <div class="brand-row">
            <span class="brand-badge" aria-hidden="true">🐶</span>
            <span class="brand-name">宠物之家</span>
          </div>
          <h2 class="aside-title">
            <slot name="aside-title">欢迎回来</slot>
          </h2>
          <p class="aside-desc">
            <slot name="aside-desc">温暖、可信赖的宠物服务平台</slot>
          </p>
          <ul class="feature-list">
            <slot name="features" />
          </ul>
          <div class="aside-paw" aria-hidden="true">🐱</div>
        </aside>

        <div class="auth-panel">
          <slot />
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped lang="scss">
.auth-shell {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px 16px;
  position: relative;
  overflow-x: hidden;
  background: var(--bg);
}

.auth-pattern {
  position: fixed;
  inset: 0;
  pointer-events: none;
  background-color: var(--bg);
  background-image:
    radial-gradient(circle at 1px 1px, rgba(255, 157, 122, 0.14) 1px, transparent 0),
    radial-gradient(ellipse 120% 80% at 10% 20%, rgba(255, 217, 168, 0.35), transparent 45%),
    radial-gradient(ellipse 100% 60% at 90% 80%, rgba(255, 236, 210, 0.5), transparent 40%);
  background-size: 20px 20px, 100% 100%, 100% 100%;
}

.auth-center {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 960px;
}

.auth-split-card {
  display: grid;
  grid-template-columns: minmax(280px, 1fr) minmax(320px, 1.05fr);
  border-radius: var(--radius-xl);
  overflow: hidden;
  background: var(--surface);
  box-shadow:
    0 4px 6px rgba(102, 72, 48, 0.04),
    0 24px 48px rgba(102, 72, 48, 0.12),
    0 0 0 1px rgba(240, 223, 210, 0.8);
}

.auth-aside {
  position: relative;
  padding: 44px 36px 52px;
  /* 底层渐变 + 略偏参考图的上下暖色层次 */
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.12) 0%, transparent 42%),
    var(--hero-gradient);
  color: var(--hero-text);
  display: flex;
  flex-direction: column;
  gap: 0;
  min-height: 100%;
  text-align: left;
}

.aside-glow {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(circle at 18% 22%, rgba(255, 255, 255, 0.32), transparent 52%),
    radial-gradient(circle at 88% 78%, rgba(255, 255, 255, 0.14), transparent 48%);
  pointer-events: none;
}

.brand-row {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 4px;
}

.brand-badge {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.28);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  border: 1px solid rgba(255, 255, 255, 0.42);
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.06);
}

.brand-name {
  font-size: 20px;
  font-weight: 800;
  letter-spacing: 0.06em;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.aside-title {
  position: relative;
  z-index: 1;
  margin: 20px 0 0;
  font-size: clamp(26px, 4vw, 34px);
  font-weight: 800;
  line-height: 1.28;
  letter-spacing: 0.02em;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.aside-desc {
  position: relative;
  z-index: 1;
  margin: 14px 0 0;
  font-size: 16px;
  line-height: 1.75;
  font-weight: 500;
  color: rgba(255, 255, 255, 0.92);
  max-width: 34em;
  letter-spacing: 0.02em;
}

.feature-list {
  position: relative;
  z-index: 1;
  list-style: none;
  margin: 26px 0 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.feature-list :deep(li) {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.22);
  border: 1px solid rgba(255, 255, 255, 0.35);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  font-size: 15px;
  font-weight: 600;
  line-height: 1.45;
  color: rgba(255, 255, 255, 0.98);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
}

.feature-list :deep(li > span:first-child) {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.32);
  border: 1px solid rgba(255, 255, 255, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.aside-paw {
  position: absolute;
  bottom: 22px;
  right: 22px;
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.24);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  border: 1px solid rgba(255, 255, 255, 0.4);
  z-index: 1;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
}

.auth-panel {
  padding: 44px 40px 40px;
  background: var(--surface);
}

@media (max-width: 820px) {
  .auth-split-card {
    grid-template-columns: 1fr;
    max-width: 440px;
    margin: 0 auto;
  }

  .auth-aside {
    padding: 32px 22px 36px;
    min-height: auto;
  }

  .aside-title {
    margin-top: 16px;
    font-size: clamp(24px, 6vw, 30px);
  }

  .aside-desc {
    font-size: 15px;
    max-width: 100%;
  }

  .feature-list {
    margin-top: 20px;
    gap: 12px;
  }

  .feature-list :deep(li) {
    font-size: 14px;
    padding: 12px 14px;
  }

  .aside-paw {
    display: none;
  }

  .auth-panel {
    padding: 28px 22px 32px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .auth-shell * {
    animation: none !important;
  }
}
</style>
