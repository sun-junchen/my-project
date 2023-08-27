import { createRouter, createWebHashHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import WelcomeView from '../views/WelcomeView.vue'
import LoginPage from '../views/welcome/LoginPage.vue'
import RegisterPage from '../views/welcome/RegisterPage.vue'
import IndexView from '../views/IndexView.vue'
import { unauthorized } from '@/plugins/myAxios'

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'welcome',
    component: WelcomeView,
    children: [
      {
        path: '',
        name: 'welcome-login',
        component: () => LoginPage
      },
      {
        path: '/register',
        name: 'welcome-register',
        component: () => RegisterPage
      }
    ]
  },
  {
    path: '/index',
    name: 'index',
    component: () => IndexView
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const isUnauthorized = unauthorized()
  if (to.name?.toString().startsWith('welcome-') && !isUnauthorized) {
    next('/index')
  } else if (to.fullPath.startsWith('/index') && isUnauthorized) {
    next('/')
  } else {
    next()
  }
})

// 注意：刷新页面会导致页面路由重置
export const setRoutes = (menus: string) => {
  if (!menus || !menus.length) {
    const manager = localStorage.getItem('manager')
    if (!manager) {
      return
    }
    menus = JSON.parse(manager).managerInfo.menus
  }
}
export default router
