<template>
  <AuthSplitShell>
    <template #aside-title>加入宠物之家</template>
    <template #aside-desc>
      开启萌宠之旅，记录爱宠成长。社区、领养、服务与商城，一站陪伴你和毛孩子。
    </template>
    <template #features>
      <li><span>❤️</span> 专业的宠物护理与服务</li>
      <li><span>📋</span> 个人宠物档案与预约管理</li>
      <li><span>🛍️</span> 优质宠物用品商城</li>
    </template>

    <div class="panel-inner">
      <h1 class="panel-title">创建新账户</h1>
      <p class="panel-sub">填写以下信息完成注册</p>

      <p v-if="error" class="error-banner">{{ error }}</p>

      <form @submit.prevent="submit" class="auth-form">
        <div class="field">
          <label for="reg-phone">手机号</label>
          <div class="input-shell input-shell--phone">
            <span class="field-ico" aria-hidden="true">📱</span>
            <span class="phone-prefix">+86</span>
            <input
              id="reg-phone"
              v-model.trim="phone"
              type="tel"
              autocomplete="tel"
              placeholder="请输入手机号"
              class="field-input"
            />
          </div>
        </div>

        <div class="field">
          <label for="reg-code">验证码</label>
          <div class="input-shell input-shell--code">
            <span class="field-ico" aria-hidden="true">✉️</span>
            <input
              id="reg-code"
              v-model.trim="code"
              type="text"
              inputmode="numeric"
              placeholder="请输入验证码"
              class="field-input field-input--code"
            />
            <button type="button" class="code-btn" :disabled="countdown > 0" @click="sendCode">
              {{ countdown > 0 ? `${countdown}s` : "获取验证码" }}
            </button>
          </div>
        </div>

        <div class="grid-2">
          <div class="field">
            <label for="reg-pwd">设置密码</label>
            <div class="input-shell">
              <span class="field-ico" aria-hidden="true">🔒</span>
              <input
                id="reg-pwd"
                v-model.trim="password"
                type="password"
                autocomplete="new-password"
                placeholder="6–20 位"
                class="field-input field-input--pad"
              />
            </div>
          </div>
          <div class="field">
            <label for="reg-pwd2">确认密码</label>
            <div class="input-shell">
              <span class="field-ico" aria-hidden="true">✅</span>
              <input
                id="reg-pwd2"
                v-model.trim="confirmPassword"
                type="password"
                autocomplete="new-password"
                placeholder="再次输入"
                class="field-input field-input--pad"
              />
            </div>
          </div>
        </div>

        <label class="agree">
          <input v-model="agreeTerms" type="checkbox" />
          <span>
            我已阅读并同意
            <a href="#" class="inline-link" @click.prevent>《用户协议》</a>
            和
            <a href="#" class="inline-link" @click.prevent>《隐私政策》</a>
          </span>
        </label>

        <button type="submit" class="submit-btn" :disabled="loading || !agreeTerms">
          <span v-if="loading" class="spinner" />
          {{ loading ? "注册中…" : "立即注册" }}
        </button>
      </form>

      <p class="switch-line">
        已有账号？
        <RouterLink to="/login" class="text-link">立即登录</RouterLink>
      </p>
    </div>
  </AuthSplitShell>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/store/auth";
import { mockUser } from "@/mocks/auth";
import AuthSplitShell from "@/components/auth/AuthSplitShell.vue";

const router = useRouter();
const auth = useAuthStore();
const phone = ref("");
const code = ref("");
const password = ref("");
const confirmPassword = ref("");
const agreeTerms = ref(false);
const loading = ref(false);
const countdown = ref(0);
const error = ref("");

const sendCode = () => {
  if (!phone.value) {
    error.value = "请先填写手机号";
    return;
  }
  error.value = "";
  countdown.value = 60;
  const timer = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) clearInterval(timer);
  }, 1000);
};

const submit = () => {
  if (!phone.value || !code.value || !password.value) {
    error.value = "请填写所有必填项";
    return;
  }
  if (password.value !== confirmPassword.value) {
    error.value = "两次输入的密码不一致";
    return;
  }
  if (!agreeTerms.value) {
    error.value = "请阅读并同意用户协议";
    return;
  }

  loading.value = true;
  error.value = "";
  setTimeout(() => {
    auth.setSession(mockUser.token, mockUser.user);
    router.push("/home");
    loading.value = false;
  }, 600);
};
</script>

<style scoped lang="scss">
.panel-inner {
  width: 100%;
  max-width: 100%;
  margin: 0 auto;
  container-type: inline-size;
  container-name: reg-panel;
}

.panel-title {
  margin: 0 0 8px;
  font-size: 26px;
  font-weight: 800;
  color: var(--text-heading);
  letter-spacing: 0.02em;
}

.panel-sub {
  margin: 0 0 24px;
  font-size: 14px;
  color: var(--muted-soft);
  line-height: 1.5;
}

.error-banner {
  margin: 0 0 16px;
  padding: 10px 14px;
  border-radius: var(--radius-md);
  background: rgba(223, 122, 122, 0.12);
  border: 1px solid rgba(223, 122, 122, 0.35);
  color: var(--danger);
  font-size: 13px;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

/* 默认单列；右栏较窄时双列易溢出 */
.grid-2 {
  display: grid;
  grid-template-columns: 1fr;
  gap: 18px;
  width: 100%;
  min-width: 0;
}

/* 表单区略宽即可双列；minmax(0,1fr) + 子项 min-width:0 防止撑破白底 */
@container reg-panel (min-width: 400px) {
  .grid-2 {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 8px;
  }
}

.grid-2 .field {
  min-width: 0;
}

.grid-2 .input-shell {
  min-width: 0;
  max-width: 100%;
  gap: 6px;
  padding: 0 8px;
  box-sizing: border-box;
}

.grid-2 .field-ico {
  font-size: 16px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.field label {
  font-size: 14px;
  font-weight: 700;
  color: var(--text-heading);
}

.input-shell {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 14px;
  min-height: 48px;
  border-radius: var(--radius-md);
  border: 1px solid var(--border-input);
  background: var(--surface);
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.input-shell:focus-within {
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(255, 157, 122, 0.2);
}

.field-ico {
  font-size: 18px;
  opacity: 0.75;
  flex-shrink: 0;
}

.phone-prefix {
  font-size: 14px;
  font-weight: 700;
  color: var(--muted);
  padding-right: 10px;
  border-right: 1px solid var(--border-warm-mid);
  flex-shrink: 0;
}

.field-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 15px;
  color: var(--text);
  min-width: 0;
  height: 46px;
}

.field-input::placeholder {
  color: var(--muted-soft);
}

.field-input--pad {
  padding-left: 0;
}

.input-shell--code {
  padding-right: 8px;
}

.field-input--code {
  padding-right: 8px;
}

.code-btn {
  flex-shrink: 0;
  padding: 8px 12px;
  border: none;
  border-left: 1px solid var(--border-warm-mid);
  background: transparent;
  color: var(--primary-strong);
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  margin-left: 4px;
}

.code-btn:disabled {
  color: var(--muted-soft);
  cursor: not-allowed;
}

.agree {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  font-size: 13px;
  line-height: 1.5;
  color: var(--muted);
  cursor: pointer;
  user-select: none;
}

.agree input {
  width: 16px;
  height: 16px;
  margin-top: 2px;
  flex-shrink: 0;
  accent-color: var(--primary-strong);
}

.inline-link {
  color: var(--primary-strong);
  font-weight: 700;
  text-decoration: none;
}

.inline-link:hover {
  text-decoration: underline;
}

.submit-btn {
  width: 100%;
  margin-top: 4px;
  min-height: 50px;
  border: none;
  border-radius: var(--radius-md);
  font-size: 16px;
  font-weight: 800;
  color: #fff;
  cursor: pointer;
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-strong) 100%);
  box-shadow:
    0 8px 20px rgba(241, 124, 83, 0.35),
    0 0 0 1px rgba(255, 255, 255, 0.2) inset;
  transition: transform 0.15s ease, filter 0.15s ease;
}

.submit-btn:hover:not(:disabled) {
  filter: brightness(1.03);
  transform: translateY(-1px);
}

.submit-btn:disabled {
  opacity: 0.55;
  cursor: not-allowed;
  transform: none;
}

.spinner {
  display: inline-block;
  width: 18px;
  height: 18px;
  margin-right: 8px;
  border: 2px solid rgba(255, 255, 255, 0.35);
  border-top-color: #fff;
  border-radius: 50%;
  vertical-align: middle;
  animation: spin 0.75s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.switch-line {
  margin: 28px 0 0;
  text-align: center;
  font-size: 14px;
  color: var(--muted);
}

.text-link {
  color: var(--primary-strong);
  font-weight: 700;
  text-decoration: none;
}

.text-link:hover {
  text-decoration: underline;
}
</style>
