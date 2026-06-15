<template>
  <aside class="service-chat" :class="{ open }" aria-label="在线客服">
    <div class="chat-head">
      <div class="chat-title">
        <span class="chat-icon">💬</span>
        <span>在线客服</span>
      </div>
      <button type="button" class="chat-close" aria-label="关闭" @click="open = false">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M18 6L6 18M6 6l12 12"/>
        </svg>
      </button>
    </div>
    <div class="chat-status">
      <span class="status-dot"></span>
      <span>在线 · 随时响应</span>
    </div>
    <div class="chat-messages" ref="messagesRef">
      <div
        v-for="(msg, index) in messages"
        :key="index"
        :class="['message', msg.type]"
      >
        <div class="message-content">{{ msg.text }}</div>
      </div>
      <div v-if="messages.length === 0" class="chat-empty">
        <p>👋 你好！有什么可以帮到你的？</p>
        <p>可以询问关于宠物护理、服务预约、商品推荐等问题</p>
      </div>
    </div>
    <div class="chat-input-area">
      <input
        v-model="inputText"
        type="text"
        placeholder="输入消息..."
        class="chat-input"
        @keydown.enter.prevent="sendMessage"
      />
      <button type="button" class="chat-send" @click="sendMessage" :disabled="!inputText.trim() || sending">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z"/>
        </svg>
      </button>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { ref, nextTick, watch } from "vue";
import { submitSupportMessage } from "@/api/modules/messages";
import { toErrorMessage } from "@/api/http";

const open = ref(false);
const inputText = ref("");
const messagesRef = ref<HTMLElement | null>(null);
const source = ref("在线客服");
const sending = ref(false);

interface Message {
  type: "user" | "system";
  text: string;
}

const messages = ref<Message[]>([]);

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight;
    }
  });
};

const sendMessage = async () => {
  const text = inputText.value.trim();
  if (!text || sending.value) return;

  messages.value.push({ type: "user", text });
  inputText.value = "";
  scrollToBottom();
  sending.value = true;

  try {
    await submitSupportMessage({ content: text, source: source.value });
    messages.value.push({
      type: "system",
      text: "已提交给后台客服，处理结果会同步到「我的-消息通知」。"
    });
  } catch (error) {
    messages.value.push({
      type: "system",
      text: `提交失败：${toErrorMessage(error)}`
    });
  } finally {
    sending.value = false;
    scrollToBottom();
  }
};

watch(open, (v) => {
  if (v) {
    messages.value = [];
  }
});

const toggle = (nextSource?: string) => {
  if (nextSource) source.value = nextSource;
  open.value = !open.value;
};

const openWithSource = (nextSource = "在线客服") => {
  source.value = nextSource;
  open.value = true;
};

defineExpose({ toggle, open: openWithSource });
</script>

<style scoped lang="scss">
.service-chat {
  position: fixed;
  right: 0;
  top: 0;
  bottom: 0;
  width: 380px;
  max-width: 100vw;
  background: var(--surface);
  border-left: 1px solid var(--border-warm);
  display: flex;
  flex-direction: column;
  z-index: 60;
  box-shadow: -4px 0 24px rgba(0, 0, 0, 0.15);
  transform: translateX(100%);
  transition: transform 0.3s ease;
  
  &.open {
    transform: translateX(0);
  }
}

.chat-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-warm);
  background: var(--hero-gradient);
  color: var(--hero-text);
}

.chat-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
}

.chat-icon {
  font-size: 20px;
}

.chat-close {
  background: none;
  border: none;
  padding: 4px;
  cursor: pointer;
  color: var(--hero-text);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;

  svg {
    width: 20px;
    height: 20px;
  }

  &:hover {
    background: rgba(255, 255, 255, 0.2);
  }
}

.chat-status {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  font-size: 12px;
  color: var(--muted);
  background: var(--surface-muted);
  border-bottom: 1px solid var(--border-warm);
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #52c41a;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.chat-empty {
  text-align: center;
  padding: 40px 20px;
  color: var(--muted);
  font-size: 14px;
  line-height: 1.6;

  p {
    margin: 4px 0;
  }
}

.message {
  display: flex;
  flex-direction: column;
  max-width: 80%;

  &.user {
    align-self: flex-end;

    .message-content {
      background: var(--primary);
      color: #fff;
      border-radius: 16px 16px 4px 16px;
    }
  }

  &.system {
    align-self: flex-start;

    .message-content {
      background: var(--surface-muted);
      color: var(--text-heading);
      border-radius: 16px 16px 16px 4px;
    }
  }
}

.message-content {
  padding: 10px 14px;
  font-size: 14px;
  line-height: 1.5;
}

.chat-input-area {
  display: flex;
  gap: 8px;
  padding: 16px;
  border-top: 1px solid var(--border-warm);
  background: var(--surface);
}

.chat-input {
  flex: 1;
  padding: 10px 14px;
  border: 1px solid var(--border-input);
  border-radius: 20px;
  font-size: 14px;
  background: var(--bg);
  color: var(--text);
  outline: none;
  transition: border-color 0.2s;

  &:focus {
    border-color: var(--primary);
  }

  &::placeholder {
    color: var(--muted-soft);
  }
}

.chat-send {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  background: var(--primary);
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  flex-shrink: 0;

  svg {
    width: 18px;
    height: 18px;
  }

  &:hover:not(:disabled) {
    background: var(--primary-strong);
    transform: scale(1.05);
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

@media (max-width: 480px) {
  .service-chat {
    width: 100vw;
  }
}
</style>
