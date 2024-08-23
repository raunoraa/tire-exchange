import { createRouter, createWebHistory } from 'vue-router';
import HomePage from '../views/HomePage.vue';  // Import the Home component

const routes = [
  {
    path: '/',          // The root path of the application
    name: 'HomePage',
    component: HomePage   // The component to render for the root path
  }
];

const router = createRouter({
  history: createWebHistory(),  // Use HTML5 history mode
  routes
});

export default router;
