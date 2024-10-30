import { createApp } from 'vue'
import { createPinia } from 'pinia'
import '@/assets/main.scss'
import App from './App.vue'
import router from './router'
import persist from 'pinia-plugin-persistedstate'

import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css'; // 引入 Element Plus 样式
import zhCn from 'element-plus/es/locale/lang/zh-cn'; // 引入中文语言包
import permissionDirective from './directives/permission'; // 导入自定义指令


const app = createApp(App)
app.use(ElementPlus, { locale: zhCn });
app.directive('permission', permissionDirective); // 注册自定义指令

app.use(createPinia().use(persist))
app.use(router)
app.mount('#app')
