<template>
  <button
    v-show="showButton"
    type="button"
    class="ai-dock-btn"
    aria-label="打开AI宠医助手"
    @click="openAIChat"
  >
    <span class="ai-icon">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
        <path d="M4.5 12.5c0-4.5 3.5-8 8-8s8 3.5 8 8-3.5 8-8 8-8-3.5-8-8z"/>
        <path d="M8 10s1.5 2 4 2 4-2 4-2"/>
        <path d="M9 15h6"/>
        <path d="M12 15v-1"/>
      </svg>
    </span>
    <span class="ai-label">AI助手</span>
    <span class="ai-badge">NEW</span>
  </button>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from "vue";

const showButton = ref(false);

const openAIChat = () => {
  document.dispatchEvent(new CustomEvent("open-ai-chat"));
};

const checkScroll = () => {
  showButton.value = window.scrollY > 200;
};

let scrollListener: (() => void) | undefined;

onMounted(() => {
  checkScroll();
  scrollListener = () => checkScroll();
  window.addEventListener("scroll", scrollListener, { passive: true });
});

onBeforeUnmount(() => {
  if (scrollListener) {
    window.removeEventListener("scroll", scrollListener);
  }
});
</script>

<style scoped lang="scss">
.ai-dock-btn {
  position: fixed;
  right: 20px;
  bottom: 24px;
  z-index: 44;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 12px 16px;
  border: none;
  border-radius: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  cursor: pointer;
  box-shadow: 0 6px 24px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
  animation: float-in 0.5s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 10px 32px rgba(102, 126, 234, 0.5);
  }
}

.ai-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;

  svg {
    width: 32px;
    height: 32px;
  }
}

.ai-label {
  font-size: 12px;
  font-weight: 700;
  white-space: nowrap;
}

.ai-badge {
  position: absolute;
  top: -6px;
  right: -6px;
  padding: 2px 6px;
  background: #ff6b6b;
  border-radius: 8px;
  font-size: 10px;
  font-weight: 900;
  animation: pulse-badge 2s infinite;
}

@keyframes float-in {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse-badge {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

@media (max-width: 900px) {
  .ai-dock-btn {
    right: 16px;
    bottom: 16px;
    padding: 10px 14px;
  }

  .ai-icon {
    width: 36px;
    height: 36px;

    svg {
      width: 28px;
      height: 28px;
    }
  }
}
</style>
