import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/ontology',
    name: 'Ontology',
    component: () => import('../views/Ontology.vue')
  },
  {
    path: '/class',
    name: 'Class',
    component: () => import('../views/Class.vue')
  },
  {
    path: '/property',
    name: 'Property',
    component: () => import('../views/Property.vue')
  },
  {
    path: '/instance',
    name: 'Instance',
    component: () => import('../views/Instance.vue')
  },
  {
    path: '/editor',
    name: 'Editor',
    component: () => import('../views/Editor.vue')
  },
  {
    path: '/hierarchy',
    name: 'Hierarchy',
    component: () => import('../views/Hierarchy.vue')
  },
  {
    path: '/reasoning',
    name: 'Reasoning',
    component: () => import('../views/Reasoning.vue')
  },
  {
    path: '/collaboration',
    name: 'Collaboration',
    component: () => import('../views/Collaboration.vue')
  },
  {
    path: '/version',
    name: 'Version',
    component: () => import('../views/Version.vue')
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('../views/Search.vue')
  },
  {
    path: '/import-export',
    name: 'ImportExport',
    component: () => import('../views/ImportExport.vue')
  },
  {
    path: '/admin/translation',
    name: 'TranslationAdmin',
    component: () => import('../views/TranslationAdmin.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router