<template>
  <div id="app">
    <header>
      <h1>Tire Exchange Application</h1>
      <h2>Time Slot Booking</h2>
    </header>
    <main>
      <HomePage />
    </main>
    <footer>
      <p>© 2024 Tire Exchange</p>
    </footer>
  </div>
</template>

<script>
import HomePage from './views/HomePage.vue';

export default {
  name: 'App',
  components: {
    HomePage
  }
};
</script>

<style>
/* Add any global styles here */
header {
  background-color: #333;
  color: white;
  padding: 10px;
  text-align: center;
}

footer {
  background-color: #333;
  color: white;
  padding: 10px;
  text-align: center;
  position: fixed;
  bottom: 0;
  width: 100%;
}

main {
  padding: 20px;
}
</style>
