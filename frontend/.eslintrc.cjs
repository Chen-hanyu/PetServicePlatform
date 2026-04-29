module.exports = {
  root: true,
  env: {
    browser: true,
    node: true,
    es2021: true,
  },
  extends: [
    'eslint:recommended',
    'plugin:vue/vue3-essential',      // 使用 essential 而非 recommended，减少警告
    'plugin:@typescript-eslint/recommended',
  ],
  parser: 'vue-eslint-parser',
  parserOptions: {
    parser: '@typescript-eslint/parser',
    ecmaVersion: 'latest',
    sourceType: 'module',
  },
  rules: {
    // 关闭所有格式相关的规则
    'vue/max-attributes-per-line': 'off',
    'vue/html-self-closing': 'off',
    'vue/singleline-html-element-content-newline': 'off',
    'vue/multiline-html-element-content-newline': 'off',
    'vue/html-closing-bracket-spacing': 'off',
    'vue/attributes-order': 'off',
    'vue/html-indent': 'off',
    'vue/no-v-html': 'off',
    
    // 关闭 any 类型警告
    '@typescript-eslint/no-explicit-any': 'off',
    
    // 关闭未使用变量检查（或让它只警告，但不要阻止 CI）
    '@typescript-eslint/no-unused-vars': 'off',
    
    // 关闭 no-undef（TypeScript 会处理类型检查）
    'no-undef': 'off',
  },
};