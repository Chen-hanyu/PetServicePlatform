<template>
  <AuthSplitShell>
    <template #aside-title>欢迎回来，管理员</template>
    <template #aside-desc>
      快速完成审核、配置运营与处理订单，让平台保持健康稳定运行。
    </template>
    <template #features>
      <li><span>🧾</span> 内容审核与运营配置</li>
      <li><span>🐾</span> 领养/宠物服务管理</li>
      <li><span>🧩</span> 商城商品与订单处理</li>
    </template>

    <div class="panel-inner">
      <h1 class="panel-title">管理员登录</h1>
      <p class="panel-sub">使用管理员手机号与密码登录后台。</p>

      <p v-if="error" class="error-banner">{{ error }}</p>

      <form @submit.prevent="submit" class="auth-form">
        <div class="field">
          <label for="admin-phone">管理员手机号</label>
          <div class="input-shell input-shell--phone">
            <span class="field-ico" aria-hidden="true">📱</span>
            <span class="phone-prefix">+86</span>
            <input
              id="admin-phone"
              v-model.trim="username"
              type="tel"
              autocomplete="tel"
              placeholder="请输入手机号"
              class="field-input"
            />
          </div>
        </div>

        <div class="field">
          <label for="admin-pwd">密码</label>
          <div class="input-shell">
            <span class="field-ico" aria-hidden="true">🔒</span>
            <input
              id="admin-pwd"
              v-model.trim="password"
              type="password"
              autocomplete="current-password"
              placeholder="请输入密码"
              class="field-input field-input--pad"
            />
          </div>
        </div>

        <div class="row-between">
          <label class="check">
            <input v-model="rememberMe" type="checkbox" />
            <span>记住我</span>
          </label>
          <a href="#" class="text-link" @click.prevent>忘记密码？</a>
        </div>

        <button type="submit" class="submit-btn" :disabled="loading">
          <span v-if="loading" class="spinner" />
          {{ loading ? "登录中…" : "进入后台" }}
        </button>
      </form>

      <p class="switch-line">
        去用户端？
        <RouterLink to="/login" class="text-link">立即登录</RouterLink>
      </p>

      <div class="demo-tip">
        <span class="demo-dot" />
        联调账号：管理员 13900000000 / admin123
      </div>
    </div>
  </AuthSplitShell>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/store/auth";
import { loginAdmin } from "@/api/modules/admin-auth";
import { toErrorMessage } from "@/api/http";
import AuthSplitShell from "@/components/auth/AuthSplitShell.vue";

const router = useRouter();
const auth = useAuthStore();

const username = ref("");
const password = ref("");
const rememberMe = ref(true);

const loading = ref(false);
const error = ref("");

const submit = async () => {
  if (!username.value || !password.value) {
    error.value = "请输入管理员手机号和密码";
    return;
  }

  loading.value = true;
  error.value = "";
  try {
    const data = await loginAdmin(username.value, password.value);
    // 管理端固定角色 ADMIN
    auth.setSession(data.token, { ...data.user, role: "ADMIN" });

    // “记住我”在当前实现里等价于写入 localStorage（由 store 决定），这里只保留 UI 开关
    void rememberMe.value;

    await router.push("/admin/dashboard");
  } catch (e) {
    error.value = toErrorMessage(e);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped lang="scss">
.panel-inner {
  max-width: 420px;
  margin: 0 auto;
}

.panel-title {
  margin: 0 0 8px;
  font-size: 26px;
  font-weight: 800;
  color: var(--text-heading);
  letter-spacing: 0.02em;
}

.panel-sub {
  margin: 0 0 28px;
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
  gap: 20px;
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

.input-shell--phone .field-input {
  padding-left: 0;
}

.row-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
}

.check {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--muted);
  cursor: pointer;
  user-select: none;
}

.check input {
  width: 16px;
  height: 16px;
  accent-color: var(--primary-strong);
}

.text-link {
  color: var(--primary-strong);
  font-weight: 700;
  text-decoration: none;
}

.text-link:hover {
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
  opacity: 0.65;
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

.demo-tip {
  margin-top: 20px;
  padding: 12px 14px;
  border-radius: var(--radius-md);
  background: var(--surface-tint);
  border: 1px solid var(--chip-border);
  font-size: 12px;
  line-height: 1.5;
  color: var(--text-subheading);
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.demo-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--primary);
  margin-top: 5px;
  flex-shrink: 0;
}
</style>
