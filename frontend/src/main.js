import { createApp } from 'vue'
import { createPinia } from 'pinia'
import '@/assets/main.scss'
import App from './App.vue'
import router from './router'
import persist from 'pinia-plugin-persistedstate'

import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css'; // 引入 Element Plus 样式
import zhCn from 'element-plus/es/locale/lang/zh-cn'; // 引入中文语言包


const app = createApp(App)
app.use(ElementPlus, { locale: zhCn });

app.use(createPinia().use(persist))
app.use(router)
app.mount('#app')
