<template>
  <aside class="ai-chat" :class="{ open: isOpen }" aria-label="AI宠医助手">
    <div class="chat-head">
      <div class="chat-title">
        <span class="chat-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M12 2a10 10 0 0 1 10 10c0 5.52-4.48 10-10 10S2 17.52 2 12 6.48 2 12 2z"/>
            <path d="M8 10s1.5 2 4 2 4-2 4-2"/>
            <path d="M9 17h6"/>
            <path d="M12 17v-2"/>
          </svg>
        </span>
        <div class="title-text">
          <span class="title-main">AI 宠医助手</span>
          <span class="title-sub">DeepSeek AI 驱动</span>
        </div>
      </div>
      <button type="button" class="chat-close" aria-label="关闭" @click="open = false">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M18 6L6 18M6 6l12 12"/>
        </svg>
      </button>
    </div>

    <div class="chat-messages" ref="messagesRef">
      <div v-if="messages.length === 0" class="chat-welcome">
        <div class="welcome-avatar">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M4.5 12.5c0-4.5 3.5-8 8-8s8 3.5 8 8-3.5 8-8 8-8-3.5-8-8z"/>
            <path d="M8 10s1.5 2 4 2 4-2 4-2"/>
            <path d="M9 15h6"/>
            <path d="M12 15v-1"/>
          </svg>
        </div>
        <h3>你好，我是 AI 宠医助手</h3>
        <p>我可以帮你解答宠物健康、习性、饮食等方面的问题</p>

        <div class="quick-questions">
          <p class="quick-title">快捷问题</p>
          <button
            v-for="q in quickQuestions"
            :key="q"
            class="quick-btn"
            @click="sendQuickQuestion(q)"
          >
            {{ q }}
          </button>
        </div>
      </div>

      <div
        v-for="(msg, index) in messages"
        :key="index"
        :class="['message', msg.role]"
      >
        <div v-if="msg.role === 'assistant'" class="message-avatar">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M4.5 12.5c0-4.5 3.5-8 8-8s8 3.5 8 8-3.5 8-8 8-8-3.5-8-8z"/>
            <path d="M8 10s1.5 2 4 2 4-2 4-2"/>
            <path d="M9 15h6"/>
          </svg>
        </div>
        <div class="message-content">{{ msg.content }}</div>
      </div>

      <div v-if="isLoading" class="message assistant">
        <div class="message-avatar">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M4.5 12.5c0-4.5 3.5-8 8-8s8 3.5 8 8-3.5 8-8 8-8-3.5-8-8z"/>
            <path d="M8 10s1.5 2 4 2 4-2 4-2"/>
            <path d="M9 15h6"/>
          </svg>
        </div>
        <div class="message-content loading">
          <span class="loading-dot"></span>
          <span class="loading-dot"></span>
          <span class="loading-dot"></span>
        </div>
      </div>
    </div>

    <div class="chat-input-area">
      <textarea
        v-model="inputText"
        placeholder="输入你的问题..."
        class="chat-input"
        rows="1"
        @keydown.enter.exact.prevent="sendMessage"
        @input="autoResize"
      />
      <button
        type="button"
        class="chat-send"
        @click="sendMessage"
        :disabled="!inputText.trim() || isLoading"
      >
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z"/>
        </svg>
      </button>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { ref, nextTick, watch, computed } from "vue";
import type { ChatMessage } from "@/api/modules/ai";
import { sendChatMessage } from "@/api/modules/ai";
import { toErrorMessage } from "@/api/http";

const props = defineProps<{
  open?: boolean;
}>();

const emit = defineEmits<{
  (e: "update:open", value: boolean): void;
}>();

const isOpen = computed({
  get: () => props.open ?? false,
  set: (value) => emit("update:open", value)
});

const inputText = ref("");
const messagesRef = ref<HTMLElement | null>(null);
const isLoading = ref(false);

const messages = ref<ChatMessage[]>([]);

const quickQuestions = [
  "我家猫咪一直打喷嚏是怎么回事？",
  "狗狗可以吃巧克力吗？",
  "猫咪多久洗一次澡比较好？",
  "如何给狗狗选择合适的狗粮？"
];

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight;
    }
  });
};

const autoResize = (e: Event) => {
  const textarea = e.target as HTMLTextAreaElement;
  textarea.style.height = "auto";
  textarea.style.height = Math.min(textarea.scrollHeight, 120) + "px";
};

const sendMessage = async () => {
  const text = inputText.value.trim();
  if (!text || isLoading.value) return;

  messages.value.push({ role: "user", content: text });
  inputText.value = "";
  scrollToBottom();

  isLoading.value = true;

  try {
    const response = await sendChatMessage(messages.value);
    messages.value.push({ role: "assistant", content: response.reply });
    scrollToBottom();
  } catch (error) {
    messages.value.push({
      role: "assistant",
      content: `抱歉，遇到了一个问题：${toErrorMessage(error)}。请稍后再试。`
    });
    scrollToBottom();
  } finally {
    isLoading.value = false;
  }
};

const sendQuickQuestion = async (question: string) => {
  inputText.value = question;
  await sendMessage();
};

watch(isOpen, (v) => {
  if (v) {
    messages.value = [];
  }
});

const toggle = () => {
  isOpen.value = !isOpen.value;
};

defineExpose({ toggle, open: isOpen });
</script>

<style scoped lang="scss">
.ai-chat {
  position: fixed;
  right: 0;
  top: 0;
  bottom: 0;
  width: 400px;
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.chat-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.chat-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;

  svg {
    width: 22px;
    height: 22px;
  }
}

.title-text {
  display: flex;
  flex-direction: column;
}

.title-main {
  font-size: 16px;
  font-weight: 700;
}

.title-sub {
  font-size: 11px;
  opacity: 0.8;
}

.chat-close {
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;

  svg {
    width: 18px;
    height: 18px;
  }

  &:hover {
    background: rgba(255, 255, 255, 0.3);
  }
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: var(--bg);
}

.chat-welcome {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 24px 16px;

  .welcome-avatar {
    width: 72px;
    height: 72px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 16px;

    svg {
      width: 40px;
      height: 40px;
      color: #fff;
    }
  }

  h3 {
    margin: 0 0 8px;
    font-size: 18px;
    color: var(--text-heading);
  }

  p {
    margin: 0;
    font-size: 14px;
    color: var(--muted);
    line-height: 1.5;
  }
}

.quick-questions {
  margin-top: 24px;
  width: 100%;
}

.quick-title {
  font-size: 12px;
  color: var(--muted-soft);
  margin-bottom: 10px;
  font-weight: 600;
}

.quick-btn {
  display: block;
  width: 100%;
  padding: 10px 14px;
  margin-bottom: 8px;
  background: var(--surface);
  border: 1px solid var(--border-warm);
  border-radius: 10px;
  font-size: 13px;
  color: var(--text);
  text-align: left;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    background: var(--chip-bg);
    border-color: var(--primary);
    color: var(--primary);
  }
}

.message {
  display: flex;
  gap: 10px;
  max-width: 85%;

  &.user {
    align-self: flex-end;
    flex-direction: row-reverse;

    .message-content {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: #fff;
      border-radius: 18px 18px 4px 18px;
    }
  }

  &.assistant {
    align-self: flex-start;

    .message-content {
      background: var(--surface);
      color: var(--text);
      border-radius: 18px 18px 18px 4px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    }
  }
}

.message-avatar {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  svg {
    width: 18px;
    height: 18px;
    color: #fff;
  }
}

.message-content {
  padding: 12px 16px;
  font-size: 14px;
  line-height: 1.5;

  &.loading {
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 14px 18px;
  }
}

.loading-dot {
  width: 8px;
  height: 8px;
  background: var(--muted-soft);
  border-radius: 50%;
  animation: bounce 1.4s infinite ease-in-out both;

  &:nth-child(1) {
    animation-delay: -0.32s;
  }

  &:nth-child(2) {
    animation-delay: -0.16s;
  }
}

@keyframes bounce {
  0%,
  80%,
  100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

.chat-input-area {
  display: flex;
  gap: 10px;
  padding: 14px 16px;
  border-top: 1px solid var(--border-warm);
  background: var(--surface);
  align-items: flex-end;
}

.chat-input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid var(--border-input);
  border-radius: 20px;
  font-size: 14px;
  background: var(--bg);
  color: var(--text);
  outline: none;
  resize: none;
  transition: border-color 0.2s;
  max-height: 120px;
  font-family: inherit;
  line-height: 1.4;

  &:focus {
    border-color: #667eea;
  }

  &::placeholder {
    color: var(--muted-soft);
  }
}

.chat-send {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: none;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
    transform: scale(1.05);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

@media (max-width: 480px) {
  .ai-chat {
    width: 100vw;
  }
}
</style>
