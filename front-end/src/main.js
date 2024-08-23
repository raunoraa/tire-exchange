import { createApp } from 'vue';
import App from './App.vue';
import router from './router';

const app = createApp(App);

// Use the router in the Vue app
app.use(router);

app.mount('#app');