<template>
  <main class="page-shell">
    <section class="card auth-card">
      <h2 class="section-title">用户登录</h2>
      <p class="muted">可使用后端种子用户手机号 + 密码登录。</p>
      <form @submit.prevent="submit">
        <input v-model.trim="username" placeholder="手机号" class="input" />
        <input v-model.trim="password" placeholder="密码" type="password" class="input" />
        <button class="btn btn-primary" :disabled="loading">{{ loading ? "登录中..." : "登录" }}</button>
      </form>
      <p v-if="error" class="error">{{ error }}</p>
    </section>
  </main>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/store/auth";
import { loginUser } from "@/services/modules/auth";
import { toErrorMessage } from "@/services/http";

const router = useRouter();
const auth = useAuthStore();
const username = ref("");
const password = ref("");
const loading = ref(false);
const error = ref("");

const submit = async () => {
  loading.value = true;
  error.value = "";
  try {
    const data = await loginUser(username.value, password.value);
    auth.setSession(data.token, { ...data.user, role: "USER" });
    await router.push("/home");
  } catch (e) {
    error.value = toErrorMessage(e);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.auth-card { max-width: 430px; margin: 44px auto; }
form { display: grid; gap: 10px; }
.error { color: var(--danger); }
</style>
