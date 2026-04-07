import { createRouter, createWebHistory } from 'vue-router'
import Home from '@/views/Home.vue'
import LoveApp from '@/views/LoveApp.vue'
import CasyManus from '@/views/CasyManus.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { title: '首页' }
  },
  {
    path: '/love-app',
    name: 'LoveApp',
    component: LoveApp,
    meta: { title: 'AI 恋爱大师' }
  },
  {
    path: '/casy-manus',
    name: 'CasyManus',
    component: CasyManus,
    meta: { title: 'AI 超级智能体' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - CASY AI` : 'CASY AI Agent'
  next()
})

export default router
